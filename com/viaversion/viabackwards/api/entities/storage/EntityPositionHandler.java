package com.viaversion.viabackwards.api.entities.storage;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.rewriters.EntityRewriterBase;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.StoredEntityData;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import java.util.function.Supplier;

public class EntityPositionHandler {
   public static final double RELATIVE_MOVE_FACTOR = 4096.0D;
   private final EntityRewriterBase<?, ?> entityRewriter;
   private final Class<? extends EntityPositionStorage> storageClass;
   private final Supplier<? extends EntityPositionStorage> storageSupplier;
   private boolean warnedForMissingEntity;

   public EntityPositionHandler(EntityRewriterBase<?, ?> entityRewriter, Class<? extends EntityPositionStorage> storageClass, Supplier<? extends EntityPositionStorage> storageSupplier) {
      this.entityRewriter = entityRewriter;
      this.storageClass = storageClass;
      this.storageSupplier = storageSupplier;
   }

   public void cacheEntityPosition(PacketWrapper wrapper, boolean create, boolean relative) throws Exception {
      this.cacheEntityPosition(wrapper, (Double)wrapper.get(Type.DOUBLE, 0), (Double)wrapper.get(Type.DOUBLE, 1), (Double)wrapper.get(Type.DOUBLE, 2), create, relative);
   }

   public void cacheEntityPosition(PacketWrapper wrapper, double x, double y, double z, boolean create, boolean relative) throws Exception {
      int entityId = (Integer)wrapper.get(Type.VAR_INT, 0);
      StoredEntityData storedEntity = this.entityRewriter.tracker(wrapper.user()).entityData(entityId);
      if (storedEntity != null) {
         EntityPositionStorage positionStorage;
         if (create) {
            positionStorage = (EntityPositionStorage)this.storageSupplier.get();
            storedEntity.put(positionStorage);
         } else {
            positionStorage = (EntityPositionStorage)storedEntity.get(this.storageClass);
            if (positionStorage == null) {
               ViaBackwards.getPlatform().getLogger().warning("Stored entity with id " + entityId + " missing " + this.storageClass.getSimpleName());
               return;
            }
         }

         positionStorage.setCoordinates(x, y, z, relative);
      } else {
         if (Via.getManager().isDebug()) {
            ViaBackwards.getPlatform().getLogger().warning("Stored entity with id " + entityId + " missing at position: " + x + " - " + y + " - " + z + " in " + this.storageClass.getSimpleName());
            if (entityId == -1 && x == 0.0D && y == 0.0D && z == 0.0D) {
               ViaBackwards.getPlatform().getLogger().warning("DO NOT REPORT THIS TO VIA, THIS IS A PLUGIN ISSUE");
            } else if (!this.warnedForMissingEntity) {
               this.warnedForMissingEntity = true;
               ViaBackwards.getPlatform().getLogger().warning("This is very likely caused by a plugin sending a teleport packet for an entity outside of the player's range.");
            }
         }

      }
   }

   public EntityPositionStorage getStorage(UserConnection user, int entityId) {
      StoredEntityData storedEntity = this.entityRewriter.tracker(user).entityData(entityId);
      EntityPositionStorage entityStorage;
      if (storedEntity != null && (entityStorage = (EntityPositionStorage)storedEntity.get(EntityPositionStorage.class)) != null) {
         return entityStorage;
      } else {
         ViaBackwards.getPlatform().getLogger().warning("Untracked entity with id " + entityId + " in " + this.storageClass.getSimpleName());
         return null;
      }
   }

   public static void writeFacingAngles(PacketWrapper wrapper, double x, double y, double z, double targetX, double targetY, double targetZ) {
      double dX = targetX - x;
      double dY = targetY - y;
      double dZ = targetZ - z;
      double r = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
      double yaw = -Math.atan2(dX, dZ) / 3.141592653589793D * 180.0D;
      if (yaw < 0.0D) {
         yaw += 360.0D;
      }

      double pitch = -Math.asin(dY / r) / 3.141592653589793D * 180.0D;
      wrapper.write(Type.BYTE, (byte)((int)(yaw * 256.0D / 360.0D)));
      wrapper.write(Type.BYTE, (byte)((int)(pitch * 256.0D / 360.0D)));
   }

   public static void writeFacingDegrees(PacketWrapper wrapper, double x, double y, double z, double targetX, double targetY, double targetZ) {
      double dX = targetX - x;
      double dY = targetY - y;
      double dZ = targetZ - z;
      double r = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
      double yaw = -Math.atan2(dX, dZ) / 3.141592653589793D * 180.0D;
      if (yaw < 0.0D) {
         yaw += 360.0D;
      }

      double pitch = -Math.asin(dY / r) / 3.141592653589793D * 180.0D;
      wrapper.write(Type.FLOAT, (float)yaw);
      wrapper.write(Type.FLOAT, (float)pitch);
   }
}
