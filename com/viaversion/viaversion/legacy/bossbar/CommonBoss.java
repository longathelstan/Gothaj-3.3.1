package com.viaversion.viaversion.legacy.bossbar;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.legacy.bossbar.BossBar;
import com.viaversion.viaversion.api.legacy.bossbar.BossColor;
import com.viaversion.viaversion.api.legacy.bossbar.BossFlag;
import com.viaversion.viaversion.api.legacy.bossbar.BossStyle;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CommonBoss implements BossBar {
   private final UUID uuid;
   private final Map<UUID, UserConnection> connections;
   private final Set<BossFlag> flags;
   private String title;
   private float health;
   private BossColor color;
   private BossStyle style;
   private boolean visible;

   public CommonBoss(String title, float health, BossColor color, BossStyle style) {
      Preconditions.checkNotNull(title, "Title cannot be null");
      Preconditions.checkArgument(health >= 0.0F && health <= 1.0F, "Health must be between 0 and 1. Input: " + health);
      this.uuid = UUID.randomUUID();
      this.title = title;
      this.health = health;
      this.color = color == null ? BossColor.PURPLE : color;
      this.style = style == null ? BossStyle.SOLID : style;
      this.connections = (new MapMaker()).weakValues().makeMap();
      this.flags = new HashSet();
      this.visible = true;
   }

   public BossBar setTitle(String title) {
      Preconditions.checkNotNull(title);
      this.title = title;
      this.sendPacket(CommonBoss.UpdateAction.UPDATE_TITLE);
      return this;
   }

   public BossBar setHealth(float health) {
      Preconditions.checkArgument(health >= 0.0F && health <= 1.0F, "Health must be between 0 and 1. Input: " + health);
      this.health = health;
      this.sendPacket(CommonBoss.UpdateAction.UPDATE_HEALTH);
      return this;
   }

   public BossColor getColor() {
      return this.color;
   }

   public BossBar setColor(BossColor color) {
      Preconditions.checkNotNull(color);
      this.color = color;
      this.sendPacket(CommonBoss.UpdateAction.UPDATE_STYLE);
      return this;
   }

   public BossBar setStyle(BossStyle style) {
      Preconditions.checkNotNull(style);
      this.style = style;
      this.sendPacket(CommonBoss.UpdateAction.UPDATE_STYLE);
      return this;
   }

   public BossBar addPlayer(UUID player) {
      UserConnection client = Via.getManager().getConnectionManager().getConnectedClient(player);
      if (client != null) {
         this.addConnection(client);
      }

      return this;
   }

   public BossBar addConnection(UserConnection conn) {
      if (this.connections.put(conn.getProtocolInfo().getUuid(), conn) == null && this.visible) {
         this.sendPacketConnection(conn, this.getPacket(CommonBoss.UpdateAction.ADD, conn));
      }

      return this;
   }

   public BossBar removePlayer(UUID uuid) {
      UserConnection client = (UserConnection)this.connections.remove(uuid);
      if (client != null) {
         this.sendPacketConnection(client, this.getPacket(CommonBoss.UpdateAction.REMOVE, client));
      }

      return this;
   }

   public BossBar removeConnection(UserConnection conn) {
      this.removePlayer(conn.getProtocolInfo().getUuid());
      return this;
   }

   public BossBar addFlag(BossFlag flag) {
      Preconditions.checkNotNull(flag);
      if (!this.hasFlag(flag)) {
         this.flags.add(flag);
      }

      this.sendPacket(CommonBoss.UpdateAction.UPDATE_FLAGS);
      return this;
   }

   public BossBar removeFlag(BossFlag flag) {
      Preconditions.checkNotNull(flag);
      if (this.hasFlag(flag)) {
         this.flags.remove(flag);
      }

      this.sendPacket(CommonBoss.UpdateAction.UPDATE_FLAGS);
      return this;
   }

   public boolean hasFlag(BossFlag flag) {
      Preconditions.checkNotNull(flag);
      return this.flags.contains(flag);
   }

   public Set<UUID> getPlayers() {
      return Collections.unmodifiableSet(this.connections.keySet());
   }

   public Set<UserConnection> getConnections() {
      return Collections.unmodifiableSet(new HashSet(this.connections.values()));
   }

   public BossBar show() {
      this.setVisible(true);
      return this;
   }

   public BossBar hide() {
      this.setVisible(false);
      return this;
   }

   public boolean isVisible() {
      return this.visible;
   }

   private void setVisible(boolean value) {
      if (this.visible != value) {
         this.visible = value;
         this.sendPacket(value ? CommonBoss.UpdateAction.ADD : CommonBoss.UpdateAction.REMOVE);
      }

   }

   public UUID getId() {
      return this.uuid;
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public String getTitle() {
      return this.title;
   }

   public float getHealth() {
      return this.health;
   }

   public BossStyle getStyle() {
      return this.style;
   }

   public Set<BossFlag> getFlags() {
      return this.flags;
   }

   private void sendPacket(CommonBoss.UpdateAction action) {
      Iterator var2 = (new ArrayList(this.connections.values())).iterator();

      while(var2.hasNext()) {
         UserConnection conn = (UserConnection)var2.next();
         PacketWrapper wrapper = this.getPacket(action, conn);
         this.sendPacketConnection(conn, wrapper);
      }

   }

   private void sendPacketConnection(UserConnection conn, PacketWrapper wrapper) {
      if (conn.getProtocolInfo() != null && conn.getProtocolInfo().getPipeline().contains(Protocol1_9To1_8.class)) {
         try {
            wrapper.scheduleSend(Protocol1_9To1_8.class);
         } catch (Exception var4) {
            var4.printStackTrace();
         }

      } else {
         this.connections.remove(conn.getProtocolInfo().getUuid());
      }
   }

   private PacketWrapper getPacket(CommonBoss.UpdateAction action, UserConnection connection) {
      try {
         PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_9.BOSSBAR, (ByteBuf)null, connection);
         wrapper.write(Type.UUID, this.uuid);
         wrapper.write(Type.VAR_INT, action.getId());
         switch(action) {
         case ADD:
            Protocol1_9To1_8.FIX_JSON.write(wrapper, this.title);
            wrapper.write(Type.FLOAT, this.health);
            wrapper.write(Type.VAR_INT, this.color.getId());
            wrapper.write(Type.VAR_INT, this.style.getId());
            wrapper.write(Type.BYTE, (byte)this.flagToBytes());
         case REMOVE:
         default:
            break;
         case UPDATE_HEALTH:
            wrapper.write(Type.FLOAT, this.health);
            break;
         case UPDATE_TITLE:
            Protocol1_9To1_8.FIX_JSON.write(wrapper, this.title);
            break;
         case UPDATE_STYLE:
            wrapper.write(Type.VAR_INT, this.color.getId());
            wrapper.write(Type.VAR_INT, this.style.getId());
            break;
         case UPDATE_FLAGS:
            wrapper.write(Type.BYTE, (byte)this.flagToBytes());
         }

         return wrapper;
      } catch (Exception var4) {
         var4.printStackTrace();
         return null;
      }
   }

   private int flagToBytes() {
      int bitmask = 0;

      BossFlag flag;
      for(Iterator var2 = this.flags.iterator(); var2.hasNext(); bitmask |= flag.getId()) {
         flag = (BossFlag)var2.next();
      }

      return bitmask;
   }

   private static enum UpdateAction {
      ADD(0),
      REMOVE(1),
      UPDATE_HEALTH(2),
      UPDATE_TITLE(3),
      UPDATE_STYLE(4),
      UPDATE_FLAGS(5);

      private final int id;

      private UpdateAction(int id) {
         this.id = id;
      }

      public int getId() {
         return this.id;
      }
   }
}
