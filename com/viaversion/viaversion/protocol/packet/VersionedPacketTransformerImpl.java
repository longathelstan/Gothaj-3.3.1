package com.viaversion.viaversion.protocol.packet;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.ProtocolPathEntry;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.packet.VersionedPacketTransformer;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.Nullable;

public class VersionedPacketTransformerImpl<C extends ClientboundPacketType, S extends ServerboundPacketType> implements VersionedPacketTransformer<C, S> {
   private final int inputProtocolVersion;
   private final Class<C> clientboundPacketsClass;
   private final Class<S> serverboundPacketsClass;

   public VersionedPacketTransformerImpl(ProtocolVersion inputVersion, @Nullable Class<C> clientboundPacketsClass, @Nullable Class<S> serverboundPacketsClass) {
      Preconditions.checkNotNull(inputVersion);
      Preconditions.checkArgument(clientboundPacketsClass != null || serverboundPacketsClass != null, "Either the clientbound or serverbound packets class has to be non-null");
      this.inputProtocolVersion = inputVersion.getVersion();
      this.clientboundPacketsClass = clientboundPacketsClass;
      this.serverboundPacketsClass = serverboundPacketsClass;
   }

   public boolean send(PacketWrapper packet) throws Exception {
      this.validatePacket(packet);
      return this.transformAndSendPacket(packet, true);
   }

   public boolean send(UserConnection connection, C packetType, Consumer<PacketWrapper> packetWriter) throws Exception {
      return this.createAndSend(connection, packetType, packetWriter);
   }

   public boolean send(UserConnection connection, S packetType, Consumer<PacketWrapper> packetWriter) throws Exception {
      return this.createAndSend(connection, packetType, packetWriter);
   }

   public boolean scheduleSend(PacketWrapper packet) throws Exception {
      this.validatePacket(packet);
      return this.transformAndSendPacket(packet, false);
   }

   public boolean scheduleSend(UserConnection connection, C packetType, Consumer<PacketWrapper> packetWriter) throws Exception {
      return this.scheduleCreateAndSend(connection, packetType, packetWriter);
   }

   public boolean scheduleSend(UserConnection connection, S packetType, Consumer<PacketWrapper> packetWriter) throws Exception {
      return this.scheduleCreateAndSend(connection, packetType, packetWriter);
   }

   @Nullable
   public PacketWrapper transform(PacketWrapper packet) throws Exception {
      this.validatePacket(packet);
      this.transformPacket(packet);
      return packet.isCancelled() ? null : packet;
   }

   @Nullable
   public PacketWrapper transform(UserConnection connection, C packetType, Consumer<PacketWrapper> packetWriter) throws Exception {
      return this.createAndTransform(connection, packetType, packetWriter);
   }

   @Nullable
   public PacketWrapper transform(UserConnection connection, S packetType, Consumer<PacketWrapper> packetWriter) throws Exception {
      return this.createAndTransform(connection, packetType, packetWriter);
   }

   private void validatePacket(PacketWrapper packet) {
      if (packet.user() == null) {
         throw new IllegalArgumentException("PacketWrapper does not have a targetted UserConnection");
      } else if (packet.getPacketType() == null) {
         throw new IllegalArgumentException("PacketWrapper does not have a valid packet type");
      } else {
         Class<? extends PacketType> expectedPacketClass = packet.getPacketType().direction() == Direction.CLIENTBOUND ? this.clientboundPacketsClass : this.serverboundPacketsClass;
         if (packet.getPacketType().getClass() != expectedPacketClass) {
            throw new IllegalArgumentException("PacketWrapper packet type is of the wrong packet class");
         }
      }
   }

   private boolean transformAndSendPacket(PacketWrapper packet, boolean currentThread) throws Exception {
      this.transformPacket(packet);
      if (packet.isCancelled()) {
         return false;
      } else {
         if (currentThread) {
            if (packet.getPacketType().direction() == Direction.CLIENTBOUND) {
               packet.sendRaw();
            } else {
               packet.sendToServerRaw();
            }
         } else if (packet.getPacketType().direction() == Direction.CLIENTBOUND) {
            packet.scheduleSendRaw();
         } else {
            packet.scheduleSendToServerRaw();
         }

         return true;
      }
   }

   private void transformPacket(PacketWrapper packet) throws Exception {
      PacketType packetType = packet.getPacketType();
      UserConnection connection = packet.user();
      boolean clientbound = packetType.direction() == Direction.CLIENTBOUND;
      int serverProtocolVersion = clientbound ? this.inputProtocolVersion : connection.getProtocolInfo().getServerProtocolVersion();
      int clientProtocolVersion = clientbound ? connection.getProtocolInfo().getProtocolVersion() : this.inputProtocolVersion;
      List<ProtocolPathEntry> path = Via.getManager().getProtocolManager().getProtocolPath(clientProtocolVersion, serverProtocolVersion);
      List<Protocol> protocolList = null;
      if (path != null) {
         protocolList = new ArrayList(path.size());
         Iterator var9 = path.iterator();

         while(var9.hasNext()) {
            ProtocolPathEntry entry = (ProtocolPathEntry)var9.next();
            protocolList.add(entry.protocol());
         }
      } else if (serverProtocolVersion != clientProtocolVersion) {
         throw new RuntimeException("No protocol path between client version " + clientProtocolVersion + " and server version " + serverProtocolVersion);
      }

      if (protocolList != null) {
         packet.resetReader();

         try {
            packet.apply(packetType.direction(), State.PLAY, 0, protocolList, clientbound);
         } catch (Exception var11) {
            throw new Exception("Exception trying to transform packet between client version " + clientProtocolVersion + " and server version " + serverProtocolVersion + ". Are you sure you used the correct input version and packet write types?", var11);
         }
      }

   }

   private boolean createAndSend(UserConnection connection, PacketType packetType, Consumer<PacketWrapper> packetWriter) throws Exception {
      PacketWrapper packet = PacketWrapper.create(packetType, connection);
      packetWriter.accept(packet);
      return this.send(packet);
   }

   private boolean scheduleCreateAndSend(UserConnection connection, PacketType packetType, Consumer<PacketWrapper> packetWriter) throws Exception {
      PacketWrapper packet = PacketWrapper.create(packetType, connection);
      packetWriter.accept(packet);
      return this.scheduleSend(packet);
   }

   @Nullable
   private PacketWrapper createAndTransform(UserConnection connection, PacketType packetType, Consumer<PacketWrapper> packetWriter) throws Exception {
      PacketWrapper packet = PacketWrapper.create(packetType, connection);
      packetWriter.accept(packet);
      return this.transform(packet);
   }
}
