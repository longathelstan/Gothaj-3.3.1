package com.viaversion.viarewind.protocol.protocol1_8to1_9.entityreplacement;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;

public class ShulkerBulletModel extends EntityModel1_8To1_9 {
   private final int entityId;
   private final List<Metadata> datawatcher = new ArrayList();
   private double locX;
   private double locY;
   private double locZ;
   private float yaw;
   private float pitch;
   private float headYaw;

   public ShulkerBulletModel(UserConnection user, Protocol1_8To1_9 protocol, int entityId) {
      super(user, protocol);
      this.entityId = entityId;
      this.sendSpawnPacket();
   }

   public void updateReplacementPosition(double x, double y, double z) {
      if (x != this.locX || y != this.locY || z != this.locZ) {
         this.locX = x;
         this.locY = y;
         this.locZ = z;
         this.updateLocation();
      }

   }

   public void handleOriginalMovementPacket(double x, double y, double z) {
      if (x != 0.0D || y != 0.0D || z != 0.0D) {
         this.locX += x;
         this.locY += y;
         this.locZ += z;
         this.updateLocation();
      }
   }

   public void setYawPitch(float yaw, float pitch) {
      if (this.yaw != yaw && this.pitch != pitch) {
         this.yaw = yaw;
         this.pitch = pitch;
         this.updateLocation();
      }

   }

   public void setHeadYaw(float yaw) {
      this.headYaw = yaw;
   }

   public void updateMetadata(List<Metadata> metadataList) {
   }

   public void updateLocation() {
      this.sendTeleportWithHead(this.entityId, this.locX, this.locY, this.locZ, this.yaw, this.pitch, this.headYaw);
   }

   public void sendSpawnPacket() {
      this.sendSpawnEntity(this.entityId, 66);
   }

   public void deleteEntity() {
      PacketWrapper despawn = PacketWrapper.create(ClientboundPackets1_8.DESTROY_ENTITIES, (ByteBuf)null, this.user);
      despawn.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[]{this.entityId});
      PacketUtil.sendPacket(despawn, Protocol1_8To1_9.class, true, true);
   }

   public int getEntityId() {
      return this.entityId;
   }
}
