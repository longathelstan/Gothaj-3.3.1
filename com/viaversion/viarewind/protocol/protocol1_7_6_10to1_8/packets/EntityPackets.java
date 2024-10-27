package com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.packets;

import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.Protocol1_7_6_10To1_8;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.model.VirtualHologramEntity;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.storage.EntityTracker1_7_6_10;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.storage.GameProfileStorage;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.storage.PlayerSessionStorage;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.Types1_7_6_10;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_10;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import java.util.List;
import java.util.UUID;

public class EntityPackets {
   public static void register(final Protocol1_7_6_10To1_8 protocol) {
      protocol.registerClientbound(ClientboundPackets1_8.ENTITY_EQUIPMENT, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT, Type.INT);
            this.map(Type.SHORT);
            this.map(Type.ITEM1_8, Types1_7_6_10.COMPRESSED_NBT_ITEM);
            this.handler((wrapper) -> {
               Item item = (Item)wrapper.get(Types1_7_6_10.COMPRESSED_NBT_ITEM, 0);
               protocol.getItemRewriter().handleItemToClient(item);
               wrapper.set(Types1_7_6_10.COMPRESSED_NBT_ITEM, 0, item);
            });
            this.handler((wrapper) -> {
               short slot = (Short)wrapper.get(Type.SHORT, 0);
               UUID uuid = ((EntityTracker1_7_6_10)wrapper.user().get(EntityTracker1_7_6_10.class)).getPlayerUUID((Integer)wrapper.get(Type.INT, 0));
               if (uuid != null) {
                  Item item = (Item)wrapper.get(Types1_7_6_10.COMPRESSED_NBT_ITEM, 0);
                  ((PlayerSessionStorage)wrapper.user().get(PlayerSessionStorage.class)).setPlayerEquipment(uuid, item, slot);
                  GameProfileStorage storage = (GameProfileStorage)wrapper.user().get(GameProfileStorage.class);
                  GameProfileStorage.GameProfile profile = storage.get(uuid);
                  if (profile != null && profile.gamemode == 3) {
                     wrapper.cancel();
                  }

               }
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.USE_BED, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT, Type.INT);
            this.map(Type.POSITION1_8, Types1_7_6_10.U_BYTE_POSITION);
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.COLLECT_ITEM, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT, Type.INT);
            this.map(Type.VAR_INT, Type.INT);
            this.handler((wrapper) -> {
               ((EntityTracker1_7_6_10)wrapper.user().get(EntityTracker1_7_6_10.class)).removeEntity((Integer)wrapper.get(Type.INT, 0));
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.ENTITY_VELOCITY, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT, Type.INT);
            this.map(Type.SHORT);
            this.map(Type.SHORT);
            this.map(Type.SHORT);
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.DESTROY_ENTITIES, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT_ARRAY_PRIMITIVE, Types1_7_6_10.BYTE_INT_ARRAY);
            this.handler((wrapper) -> {
               EntityTracker1_7_6_10 tracker = (EntityTracker1_7_6_10)wrapper.user().get(EntityTracker1_7_6_10.class);
               int[] var2 = (int[])wrapper.get(Types1_7_6_10.BYTE_INT_ARRAY, 0);
               int var3 = var2.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  int entityId = var2[var4];
                  tracker.removeEntity(entityId);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.ENTITY_MOVEMENT, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT, Type.INT);
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.ENTITY_POSITION, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT, Type.INT);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.read(Type.BOOLEAN);
            this.handler((wrapper) -> {
               EntityTracker1_7_6_10 tracker = (EntityTracker1_7_6_10)wrapper.user().get(EntityTracker1_7_6_10.class);
               VirtualHologramEntity hologram = (VirtualHologramEntity)tracker.getVirtualHologramMap().get(wrapper.get(Type.INT, 0));
               if (hologram != null) {
                  wrapper.cancel();
                  int x = (Byte)wrapper.get(Type.BYTE, 0);
                  int y = (Byte)wrapper.get(Type.BYTE, 1);
                  int z = (Byte)wrapper.get(Type.BYTE, 2);
                  hologram.handleOriginalMovementPacket((double)x / 32.0D, (double)y / 32.0D, (double)z / 32.0D);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.ENTITY_ROTATION, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT, Type.INT);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.read(Type.BOOLEAN);
            this.handler((wrapper) -> {
               EntityTracker1_7_6_10 tracker = (EntityTracker1_7_6_10)wrapper.user().get(EntityTracker1_7_6_10.class);
               VirtualHologramEntity hologram = (VirtualHologramEntity)tracker.getVirtualHologramMap().get(wrapper.get(Type.INT, 0));
               if (hologram != null) {
                  wrapper.cancel();
                  int yaw = (Byte)wrapper.get(Type.BYTE, 0);
                  int pitch = (Byte)wrapper.get(Type.BYTE, 1);
                  hologram.setYawPitch((float)yaw * 360.0F / 256.0F, (float)pitch * 360.0F / 256.0F);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.ENTITY_POSITION_AND_ROTATION, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT, Type.INT);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.read(Type.BOOLEAN);
            this.handler((wrapper) -> {
               EntityTracker1_7_6_10 tracker = (EntityTracker1_7_6_10)wrapper.user().get(EntityTracker1_7_6_10.class);
               VirtualHologramEntity hologram = (VirtualHologramEntity)tracker.getVirtualHologramMap().get(wrapper.get(Type.INT, 0));
               if (hologram != null) {
                  wrapper.cancel();
                  int x = (Byte)wrapper.get(Type.BYTE, 0);
                  int y = (Byte)wrapper.get(Type.BYTE, 1);
                  int z = (Byte)wrapper.get(Type.BYTE, 2);
                  int yaw = (Byte)wrapper.get(Type.BYTE, 3);
                  int pitch = (Byte)wrapper.get(Type.BYTE, 4);
                  hologram.handleOriginalMovementPacket((double)x / 32.0D, (double)y / 32.0D, (double)z / 32.0D);
                  hologram.setYawPitch((float)yaw * 360.0F / 256.0F, (float)pitch * 360.0F / 256.0F);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.ENTITY_TELEPORT, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT, Type.INT);
            this.map(Type.INT);
            this.map(Type.INT);
            this.map(Type.INT);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.read(Type.BOOLEAN);
            this.handler((wrapper) -> {
               EntityTracker1_7_6_10 tracker = (EntityTracker1_7_6_10)wrapper.user().get(EntityTracker1_7_6_10.class);
               int entityId = (Integer)wrapper.get(Type.INT, 0);
               EntityTypes1_10.EntityType type = (EntityTypes1_10.EntityType)tracker.getEntityMap().get(entityId);
               if (type == EntityTypes1_10.EntityType.MINECART_ABSTRACT) {
                  int yx = (Integer)wrapper.get(Type.INT, 2);
                  yx += 12;
                  wrapper.set(Type.INT, 2, yx);
               }

               VirtualHologramEntity hologram = (VirtualHologramEntity)tracker.getVirtualHologramMap().get(entityId);
               if (hologram != null) {
                  wrapper.cancel();
                  int x = (Integer)wrapper.get(Type.INT, 1);
                  int y = (Integer)wrapper.get(Type.INT, 2);
                  int z = (Integer)wrapper.get(Type.INT, 3);
                  int yaw = (Byte)wrapper.get(Type.BYTE, 0);
                  int pitch = (Byte)wrapper.get(Type.BYTE, 1);
                  hologram.updateReplacementPosition((double)x / 32.0D, (double)y / 32.0D, (double)z / 32.0D);
                  hologram.setYawPitch((float)yaw * 360.0F / 256.0F, (float)pitch * 360.0F / 256.0F);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.ENTITY_HEAD_LOOK, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT, Type.INT);
            this.map(Type.BYTE);
            this.handler((wrapper) -> {
               EntityTracker1_7_6_10 tracker = (EntityTracker1_7_6_10)wrapper.user().get(EntityTracker1_7_6_10.class);
               VirtualHologramEntity hologram = (VirtualHologramEntity)tracker.getVirtualHologramMap().get(wrapper.get(Type.INT, 0));
               if (hologram != null) {
                  wrapper.cancel();
                  int yaw = (Byte)wrapper.get(Type.BYTE, 0);
                  hologram.setHeadYaw((float)yaw * 360.0F / 256.0F);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.ATTACH_ENTITY, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.INT);
            this.map(Type.BOOLEAN);
            this.handler((packetWrapper) -> {
               boolean leash = (Boolean)packetWrapper.get(Type.BOOLEAN, 0);
               if (!leash) {
                  EntityTracker1_7_6_10 tracker = (EntityTracker1_7_6_10)packetWrapper.user().get(EntityTracker1_7_6_10.class);
                  int passenger = (Integer)packetWrapper.get(Type.INT, 0);
                  int vehicle = (Integer)packetWrapper.get(Type.INT, 1);
                  tracker.setPassenger(vehicle, passenger);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.ENTITY_METADATA, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT, Type.INT);
            this.map(Types1_8.METADATA_LIST, Types1_7_6_10.METADATA_LIST);
            this.handler((wrapper) -> {
               int entityId = (Integer)wrapper.get(Type.INT, 0);
               List<Metadata> metadataList = (List)wrapper.get(Types1_7_6_10.METADATA_LIST, 0);
               EntityTracker1_7_6_10 tracker = (EntityTracker1_7_6_10)wrapper.user().get(EntityTracker1_7_6_10.class);
               if (tracker.getEntityReplacementMap().containsKey(entityId)) {
                  tracker.updateMetadata(entityId, metadataList);
                  wrapper.cancel();
               } else {
                  if (tracker.getEntityMap().containsKey(entityId)) {
                     protocol.getMetadataRewriter().transform((EntityTypes1_10.EntityType)tracker.getEntityMap().get(entityId), metadataList);
                     if (metadataList.isEmpty()) {
                        wrapper.cancel();
                     }
                  } else {
                     wrapper.cancel();
                  }

               }
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.ENTITY_EFFECT, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT, Type.INT);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.VAR_INT, Type.SHORT);
            this.read(Type.BYTE);
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.REMOVE_ENTITY_EFFECT, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT, Type.INT);
            this.map(Type.BYTE);
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.ENTITY_PROPERTIES, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT, Type.INT);
            this.handler((wrapper) -> {
               int entityId = (Integer)wrapper.get(Type.INT, 0);
               if (((EntityTracker1_7_6_10)wrapper.user().get(EntityTracker1_7_6_10.class)).getEntityReplacementMap().containsKey(entityId)) {
                  wrapper.cancel();
               } else {
                  int amount = (Integer)wrapper.passthrough(Type.INT);

                  for(int i = 0; i < amount; ++i) {
                     wrapper.passthrough(Type.STRING);
                     wrapper.passthrough(Type.DOUBLE);
                     int modifierLength = (Integer)wrapper.read(Type.VAR_INT);
                     wrapper.write(Type.SHORT, (short)modifierLength);

                     for(int j = 0; j < modifierLength; ++j) {
                        wrapper.passthrough(Type.UUID);
                        wrapper.passthrough(Type.DOUBLE);
                        wrapper.passthrough(Type.BYTE);
                     }
                  }

               }
            });
         }
      });
      protocol.cancelClientbound(ClientboundPackets1_8.UPDATE_ENTITY_NBT);
   }
}
