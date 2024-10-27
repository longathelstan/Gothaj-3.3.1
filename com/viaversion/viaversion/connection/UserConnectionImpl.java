package com.viaversion.viaversion.connection;

import com.google.common.cache.CacheBuilder;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketTracker;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.exception.CancelException;
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl;
import com.viaversion.viaversion.util.ChatColorUtil;
import com.viaversion.viaversion.util.PipelineUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.Nullable;

public class UserConnectionImpl implements UserConnection {
   private static final AtomicLong IDS = new AtomicLong();
   private final long id;
   private final Map<Class<?>, StorableObject> storedObjects;
   private final Map<Class<? extends Protocol>, EntityTracker> entityTrackers;
   private final PacketTracker packetTracker;
   private final Set<UUID> passthroughTokens;
   private final ProtocolInfo protocolInfo;
   private final Channel channel;
   private final boolean clientSide;
   private boolean active;
   private boolean pendingDisconnect;
   private boolean packetLimiterEnabled;

   public UserConnectionImpl(@Nullable Channel channel, boolean clientSide) {
      this.id = IDS.incrementAndGet();
      this.storedObjects = new ConcurrentHashMap();
      this.entityTrackers = new HashMap();
      this.packetTracker = new PacketTracker(this);
      this.passthroughTokens = Collections.newSetFromMap(CacheBuilder.newBuilder().expireAfterWrite(10L, TimeUnit.SECONDS).build().asMap());
      this.protocolInfo = new ProtocolInfoImpl(this);
      this.active = true;
      this.packetLimiterEnabled = true;
      this.channel = channel;
      this.clientSide = clientSide;
   }

   public UserConnectionImpl(@Nullable Channel channel) {
      this(channel, false);
   }

   @Nullable
   public <T extends StorableObject> T get(Class<T> objectClass) {
      return (StorableObject)this.storedObjects.get(objectClass);
   }

   public boolean has(Class<? extends StorableObject> objectClass) {
      return this.storedObjects.containsKey(objectClass);
   }

   @Nullable
   public <T extends StorableObject> T remove(Class<T> objectClass) {
      StorableObject object = (StorableObject)this.storedObjects.remove(objectClass);
      if (object != null) {
         object.onRemove();
      }

      return object;
   }

   public void put(StorableObject object) {
      StorableObject previousObject = (StorableObject)this.storedObjects.put(object.getClass(), object);
      if (previousObject != null) {
         previousObject.onRemove();
      }

   }

   public Collection<EntityTracker> getEntityTrackers() {
      return this.entityTrackers.values();
   }

   @Nullable
   public <T extends EntityTracker> T getEntityTracker(Class<? extends Protocol> protocolClass) {
      return (EntityTracker)this.entityTrackers.get(protocolClass);
   }

   public void addEntityTracker(Class<? extends Protocol> protocolClass, EntityTracker tracker) {
      if (!this.entityTrackers.containsKey(protocolClass)) {
         this.entityTrackers.put(protocolClass, tracker);
      }

   }

   public void clearStoredObjects(boolean isServerSwitch) {
      Iterator var2;
      if (isServerSwitch) {
         this.storedObjects.values().removeIf((storableObject) -> {
            if (storableObject.clearOnServerSwitch()) {
               storableObject.onRemove();
               return true;
            } else {
               return false;
            }
         });
         var2 = this.entityTrackers.values().iterator();

         while(var2.hasNext()) {
            EntityTracker tracker = (EntityTracker)var2.next();
            tracker.clearEntities();
            tracker.trackClientEntity();
         }
      } else {
         var2 = this.storedObjects.values().iterator();

         while(var2.hasNext()) {
            StorableObject object = (StorableObject)var2.next();
            object.onRemove();
         }

         this.storedObjects.clear();
         this.entityTrackers.clear();
      }

   }

   public void sendRawPacket(ByteBuf packet) {
      this.sendRawPacket(packet, true);
   }

   public void scheduleSendRawPacket(ByteBuf packet) {
      this.sendRawPacket(packet, false);
   }

   private void sendRawPacket(ByteBuf packet, boolean currentThread) {
      Runnable act;
      if (this.clientSide) {
         act = () -> {
            this.getChannel().pipeline().context(Via.getManager().getInjector().getDecoderName()).fireChannelRead(packet);
         };
      } else {
         act = () -> {
            this.channel.pipeline().context(Via.getManager().getInjector().getEncoderName()).writeAndFlush(packet);
         };
      }

      if (currentThread) {
         act.run();
      } else {
         try {
            this.channel.eventLoop().submit(act);
         } catch (Throwable var5) {
            packet.release();
            var5.printStackTrace();
         }
      }

   }

   public ChannelFuture sendRawPacketFuture(ByteBuf packet) {
      if (this.clientSide) {
         this.getChannel().pipeline().context(Via.getManager().getInjector().getDecoderName()).fireChannelRead(packet);
         return this.getChannel().newSucceededFuture();
      } else {
         return this.channel.pipeline().context(Via.getManager().getInjector().getEncoderName()).writeAndFlush(packet);
      }
   }

   public PacketTracker getPacketTracker() {
      return this.packetTracker;
   }

   public void disconnect(String reason) {
      if (this.channel.isOpen() && !this.pendingDisconnect) {
         this.pendingDisconnect = true;
         Via.getPlatform().runSync(() -> {
            if (!Via.getPlatform().disconnect(this, ChatColorUtil.translateAlternateColorCodes(reason))) {
               this.channel.close();
            }

         });
      }
   }

   public void sendRawPacketToServer(ByteBuf packet) {
      if (this.clientSide) {
         this.sendRawPacketToServerClientSide(packet, true);
      } else {
         this.sendRawPacketToServerServerSide(packet, true);
      }

   }

   public void scheduleSendRawPacketToServer(ByteBuf packet) {
      if (this.clientSide) {
         this.sendRawPacketToServerClientSide(packet, false);
      } else {
         this.sendRawPacketToServerServerSide(packet, false);
      }

   }

   private void sendRawPacketToServerServerSide(ByteBuf packet, boolean currentThread) {
      ByteBuf buf = packet.alloc().buffer();

      try {
         ChannelHandlerContext context = PipelineUtil.getPreviousContext(Via.getManager().getInjector().getDecoderName(), this.channel.pipeline());
         if (this.shouldTransformPacket()) {
            try {
               Type.VAR_INT.writePrimitive(buf, 1000);
               Type.UUID.write(buf, this.generatePassthroughToken());
            } catch (Exception var12) {
               throw new RuntimeException(var12);
            }
         }

         buf.writeBytes(packet);
         Runnable act = () -> {
            if (context != null) {
               context.fireChannelRead(buf);
            } else {
               this.channel.pipeline().fireChannelRead(buf);
            }

         };
         if (currentThread) {
            act.run();
         } else {
            try {
               this.channel.eventLoop().submit(act);
            } catch (Throwable var11) {
               buf.release();
               throw var11;
            }
         }
      } finally {
         packet.release();
      }

   }

   private void sendRawPacketToServerClientSide(ByteBuf packet, boolean currentThread) {
      Runnable act = () -> {
         this.getChannel().pipeline().context(Via.getManager().getInjector().getEncoderName()).writeAndFlush(packet);
      };
      if (currentThread) {
         act.run();
      } else {
         try {
            this.getChannel().eventLoop().submit(act);
         } catch (Throwable var5) {
            var5.printStackTrace();
            packet.release();
         }
      }

   }

   public boolean checkServerboundPacket() {
      if (this.pendingDisconnect) {
         return false;
      } else {
         return !this.packetLimiterEnabled || !this.packetTracker.incrementReceived() || !this.packetTracker.exceedsMaxPPS();
      }
   }

   public boolean checkClientboundPacket() {
      this.packetTracker.incrementSent();
      return true;
   }

   public boolean shouldTransformPacket() {
      return this.active;
   }

   public void transformClientbound(ByteBuf buf, Function<Throwable, Exception> cancelSupplier) throws Exception {
      this.transform(buf, Direction.CLIENTBOUND, cancelSupplier);
   }

   public void transformServerbound(ByteBuf buf, Function<Throwable, Exception> cancelSupplier) throws Exception {
      this.transform(buf, Direction.SERVERBOUND, cancelSupplier);
   }

   private void transform(ByteBuf buf, Direction direction, Function<Throwable, Exception> cancelSupplier) throws Exception {
      if (buf.isReadable()) {
         int id = Type.VAR_INT.readPrimitive(buf);
         if (id == 1000) {
            if (!this.passthroughTokens.remove(Type.UUID.read(buf))) {
               throw new IllegalArgumentException("Invalid token");
            }
         } else {
            PacketWrapper wrapper = new PacketWrapperImpl(id, buf, this);
            State state = this.protocolInfo.getState(direction);

            try {
               this.protocolInfo.getPipeline().transform(direction, state, wrapper);
            } catch (CancelException var12) {
               throw (Exception)cancelSupplier.apply(var12);
            }

            ByteBuf transformed = buf.alloc().buffer();

            try {
               wrapper.writeToBuffer(transformed);
               buf.clear().writeBytes(transformed);
            } finally {
               transformed.release();
            }

         }
      }
   }

   public long getId() {
      return this.id;
   }

   @Nullable
   public Channel getChannel() {
      return this.channel;
   }

   public ProtocolInfo getProtocolInfo() {
      return this.protocolInfo;
   }

   public Map<Class<?>, StorableObject> getStoredObjects() {
      return this.storedObjects;
   }

   public boolean isActive() {
      return this.active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   public boolean isPendingDisconnect() {
      return this.pendingDisconnect;
   }

   public void setPendingDisconnect(boolean pendingDisconnect) {
      this.pendingDisconnect = pendingDisconnect;
   }

   public boolean isClientSide() {
      return this.clientSide;
   }

   public boolean shouldApplyBlockProtocol() {
      return !this.clientSide;
   }

   public boolean isPacketLimiterEnabled() {
      return this.packetLimiterEnabled;
   }

   public void setPacketLimiterEnabled(boolean packetLimiterEnabled) {
      this.packetLimiterEnabled = packetLimiterEnabled;
   }

   public UUID generatePassthroughToken() {
      UUID token = UUID.randomUUID();
      this.passthroughTokens.add(token);
      return token;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         UserConnectionImpl that = (UserConnectionImpl)o;
         return this.id == that.id;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }
}
