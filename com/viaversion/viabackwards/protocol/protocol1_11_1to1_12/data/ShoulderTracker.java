package com.viaversion.viabackwards.protocol.protocol1_11_1to1_12.data;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.protocol.protocol1_11_1to1_12.Protocol1_11_1To1_12;
import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.ClientboundPackets1_12;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.util.Key;
import io.netty.buffer.ByteBuf;

public class ShoulderTracker extends StoredObject {
   private int entityId;
   private String leftShoulder;
   private String rightShoulder;

   public ShoulderTracker(UserConnection user) {
      super(user);
   }

   public void update() {
      PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_12.CHAT_MESSAGE, (ByteBuf)null, this.getUser());
      wrapper.write(Type.COMPONENT, Protocol1_9To1_8.fixJson(this.generateString()));
      wrapper.write(Type.BYTE, (byte)2);

      try {
         wrapper.scheduleSend(Protocol1_11_1To1_12.class);
      } catch (Exception var3) {
         ViaBackwards.getPlatform().getLogger().severe("Failed to send the shoulder indication");
         var3.printStackTrace();
      }

   }

   private String generateString() {
      StringBuilder builder = new StringBuilder();
      builder.append("  ");
      if (this.leftShoulder == null) {
         builder.append("§4§lNothing");
      } else {
         builder.append("§2§l").append(this.getName(this.leftShoulder));
      }

      builder.append("§8§l <- §7§lShoulders§8§l -> ");
      if (this.rightShoulder == null) {
         builder.append("§4§lNothing");
      } else {
         builder.append("§2§l").append(this.getName(this.rightShoulder));
      }

      return builder.toString();
   }

   private String getName(String current) {
      current = Key.stripMinecraftNamespace(current);
      String[] array = current.split("_");
      StringBuilder builder = new StringBuilder();
      String[] var4 = array;
      int var5 = array.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String s = var4[var6];
         builder.append(s.substring(0, 1).toUpperCase()).append(s.substring(1)).append(" ");
      }

      return builder.toString();
   }

   public int getEntityId() {
      return this.entityId;
   }

   public void setEntityId(int entityId) {
      this.entityId = entityId;
   }

   public String getLeftShoulder() {
      return this.leftShoulder;
   }

   public void setLeftShoulder(String leftShoulder) {
      this.leftShoulder = leftShoulder;
   }

   public String getRightShoulder() {
      return this.rightShoulder;
   }

   public void setRightShoulder(String rightShoulder) {
      this.rightShoulder = rightShoulder;
   }

   public String toString() {
      return "ShoulderTracker{entityId=" + this.entityId + ", leftShoulder='" + this.leftShoulder + '\'' + ", rightShoulder='" + this.rightShoulder + '\'' + '}';
   }
}
