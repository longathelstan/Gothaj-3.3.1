package com.viaversion.viarewind.protocol.protocol1_8to1_9.packets;

import com.viaversion.viarewind.api.minecraft.EntityModel;
import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.protocol.protocol1_8to1_9.storage.Cooldown;
import com.viaversion.viarewind.protocol.protocol1_8to1_9.storage.EntityTracker;
import com.viaversion.viarewind.protocol.protocol1_8to1_9.storage.Levitation;
import com.viaversion.viarewind.protocol.protocol1_8to1_9.storage.PlayerPosition;
import com.viaversion.viarewind.protocol.protocol1_8to1_9.util.RelativeMoveUtil;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.minecraft.Vector;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_10;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import com.viaversion.viaversion.api.type.types.version.Types1_9;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.util.Pair;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class EntityPackets {
   public static void register(final Protocol1_8To1_9 protocol) {
      protocol.registerClientbound(ClientboundPackets1_9.ENTITY_STATUS, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.handler((packetWrapper) -> {
               byte status = (Byte)packetWrapper.read(Type.BYTE);
               if (status > 23) {
                  packetWrapper.cancel();
               } else {
                  packetWrapper.write(Type.BYTE, status);
               }
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.ENTITY_POSITION, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.handler((packetWrapper) -> {
               int entityId = (Integer)packetWrapper.get(Type.VAR_INT, 0);
               int relX = (Short)packetWrapper.read(Type.SHORT);
               int relY = (Short)packetWrapper.read(Type.SHORT);
               int relZ = (Short)packetWrapper.read(Type.SHORT);
               EntityTracker tracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
               EntityModel replacement = tracker.getEntityReplacement(entityId);
               if (replacement != null) {
                  packetWrapper.cancel();
                  replacement.handleOriginalMovementPacket((double)relX / 4096.0D, (double)relY / 4096.0D, (double)relZ / 4096.0D);
               } else {
                  Vector[] moves = RelativeMoveUtil.calculateRelativeMoves(packetWrapper.user(), entityId, relX, relY, relZ);
                  packetWrapper.write(Type.BYTE, (byte)moves[0].blockX());
                  packetWrapper.write(Type.BYTE, (byte)moves[0].blockY());
                  packetWrapper.write(Type.BYTE, (byte)moves[0].blockZ());
                  boolean onGround = (Boolean)packetWrapper.passthrough(Type.BOOLEAN);
                  if (moves.length > 1) {
                     PacketWrapper secondPacket = PacketWrapper.create(ClientboundPackets1_8.ENTITY_POSITION, (ByteBuf)null, packetWrapper.user());
                     secondPacket.write(Type.VAR_INT, (Integer)packetWrapper.get(Type.VAR_INT, 0));
                     secondPacket.write(Type.BYTE, (byte)moves[1].blockX());
                     secondPacket.write(Type.BYTE, (byte)moves[1].blockY());
                     secondPacket.write(Type.BYTE, (byte)moves[1].blockZ());
                     secondPacket.write(Type.BOOLEAN, onGround);
                     PacketUtil.sendPacket(secondPacket, Protocol1_8To1_9.class);
                  }

               }
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.ENTITY_POSITION_AND_ROTATION, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.handler((packetWrapper) -> {
               int entityId = (Integer)packetWrapper.get(Type.VAR_INT, 0);
               int relX = (Short)packetWrapper.read(Type.SHORT);
               int relY = (Short)packetWrapper.read(Type.SHORT);
               int relZ = (Short)packetWrapper.read(Type.SHORT);
               EntityTracker tracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
               EntityModel replacement = tracker.getEntityReplacement(entityId);
               if (replacement != null) {
                  packetWrapper.cancel();
                  replacement.handleOriginalMovementPacket((double)relX / 4096.0D, (double)relY / 4096.0D, (double)relZ / 4096.0D);
                  replacement.setYawPitch((float)(Byte)packetWrapper.read(Type.BYTE) * 360.0F / 256.0F, (float)(Byte)packetWrapper.read(Type.BYTE) * 360.0F / 256.0F);
               } else {
                  Vector[] moves = RelativeMoveUtil.calculateRelativeMoves(packetWrapper.user(), entityId, relX, relY, relZ);
                  packetWrapper.write(Type.BYTE, (byte)moves[0].blockX());
                  packetWrapper.write(Type.BYTE, (byte)moves[0].blockY());
                  packetWrapper.write(Type.BYTE, (byte)moves[0].blockZ());
                  byte yaw = (Byte)packetWrapper.passthrough(Type.BYTE);
                  byte pitch = (Byte)packetWrapper.passthrough(Type.BYTE);
                  boolean onGround = (Boolean)packetWrapper.passthrough(Type.BOOLEAN);
                  EntityTypes1_10.EntityType type = (EntityTypes1_10.EntityType)((EntityTracker)packetWrapper.user().get(EntityTracker.class)).getClientEntityTypes().get(entityId);
                  if (type == EntityTypes1_10.EntityType.BOAT) {
                     yaw = (byte)(yaw - 64);
                     packetWrapper.set(Type.BYTE, 3, yaw);
                  }

                  if (moves.length > 1) {
                     PacketWrapper secondPacket = PacketWrapper.create(ClientboundPackets1_8.ENTITY_POSITION_AND_ROTATION, (ByteBuf)null, packetWrapper.user());
                     secondPacket.write(Type.VAR_INT, (Integer)packetWrapper.get(Type.VAR_INT, 0));
                     secondPacket.write(Type.BYTE, (byte)moves[1].blockX());
                     secondPacket.write(Type.BYTE, (byte)moves[1].blockY());
                     secondPacket.write(Type.BYTE, (byte)moves[1].blockZ());
                     secondPacket.write(Type.BYTE, yaw);
                     secondPacket.write(Type.BYTE, pitch);
                     secondPacket.write(Type.BOOLEAN, onGround);
                     PacketUtil.sendPacket(secondPacket, Protocol1_8To1_9.class);
                  }

               }
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.ENTITY_ROTATION, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.BOOLEAN);
            this.handler((packetWrapper) -> {
               int entityId = (Integer)packetWrapper.get(Type.VAR_INT, 0);
               EntityTracker tracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
               EntityModel replacement = tracker.getEntityReplacement(entityId);
               if (replacement != null) {
                  packetWrapper.cancel();
                  int yaw = (Byte)packetWrapper.get(Type.BYTE, 0);
                  int pitch = (Byte)packetWrapper.get(Type.BYTE, 1);
                  replacement.setYawPitch((float)yaw * 360.0F / 256.0F, (float)pitch * 360.0F / 256.0F);
               }

            });
            this.handler((packetWrapper) -> {
               int entityId = (Integer)packetWrapper.get(Type.VAR_INT, 0);
               EntityTypes1_10.EntityType type = (EntityTypes1_10.EntityType)((EntityTracker)packetWrapper.user().get(EntityTracker.class)).getClientEntityTypes().get(entityId);
               if (type == EntityTypes1_10.EntityType.BOAT) {
                  byte yaw = (Byte)packetWrapper.get(Type.BYTE, 0);
                  yaw = (byte)(yaw - 64);
                  packetWrapper.set(Type.BYTE, 0, yaw);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.VEHICLE_MOVE, ClientboundPackets1_8.ENTITY_TELEPORT, new PacketHandlers() {
         public void register() {
            this.handler((packetWrapper) -> {
               EntityTracker tracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
               int vehicle = tracker.getVehicle(tracker.getPlayerId());
               if (vehicle == -1) {
                  packetWrapper.cancel();
               }

               packetWrapper.write(Type.VAR_INT, vehicle);
            });
            this.map(Type.DOUBLE, Protocol1_8To1_9.TO_OLD_INT);
            this.map(Type.DOUBLE, Protocol1_8To1_9.TO_OLD_INT);
            this.map(Type.DOUBLE, Protocol1_8To1_9.TO_OLD_INT);
            this.map(Type.FLOAT, Protocol1_8To1_9.DEGREES_TO_ANGLE);
            this.map(Type.FLOAT, Protocol1_8To1_9.DEGREES_TO_ANGLE);
            this.handler((packetWrapper) -> {
               if (!packetWrapper.isCancelled()) {
                  PlayerPosition position = (PlayerPosition)packetWrapper.user().get(PlayerPosition.class);
                  double x = (double)(Integer)packetWrapper.get(Type.INT, 0) / 32.0D;
                  double y = (double)(Integer)packetWrapper.get(Type.INT, 1) / 32.0D;
                  double z = (double)(Integer)packetWrapper.get(Type.INT, 2) / 32.0D;
                  position.setPos(x, y, z);
               }
            });
            this.create(Type.BOOLEAN, true);
            this.handler((packetWrapper) -> {
               int entityId = (Integer)packetWrapper.get(Type.VAR_INT, 0);
               EntityTypes1_10.EntityType type = (EntityTypes1_10.EntityType)((EntityTracker)packetWrapper.user().get(EntityTracker.class)).getClientEntityTypes().get(entityId);
               if (type == EntityTypes1_10.EntityType.BOAT) {
                  byte yaw = (Byte)packetWrapper.get(Type.BYTE, 1);
                  yaw = (byte)(yaw - 64);
                  packetWrapper.set(Type.BYTE, 0, yaw);
                  int y = (Integer)packetWrapper.get(Type.INT, 1);
                  y += 10;
                  packetWrapper.set(Type.INT, 1, y);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.DESTROY_ENTITIES, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT_ARRAY_PRIMITIVE);
            this.handler((packetWrapper) -> {
               EntityTracker tracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
               int[] var2 = (int[])packetWrapper.get(Type.VAR_INT_ARRAY_PRIMITIVE, 0);
               int var3 = var2.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  int entityId = var2[var4];
                  tracker.removeEntity(entityId);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.REMOVE_ENTITY_EFFECT, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.BYTE);
            this.handler((packetWrapper) -> {
               int id = (Byte)packetWrapper.get(Type.BYTE, 0);
               if (id > 23) {
                  packetWrapper.cancel();
               }

               if (id == 25) {
                  if ((Integer)packetWrapper.get(Type.VAR_INT, 0) != ((EntityTracker)packetWrapper.user().get(EntityTracker.class)).getPlayerId()) {
                     return;
                  }

                  Levitation levitation = (Levitation)packetWrapper.user().get(Levitation.class);
                  levitation.setActive(false);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.ENTITY_HEAD_LOOK, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.BYTE);
            this.handler((packetWrapper) -> {
               int entityId = (Integer)packetWrapper.get(Type.VAR_INT, 0);
               EntityTracker tracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
               EntityModel replacement = tracker.getEntityReplacement(entityId);
               if (replacement != null) {
                  packetWrapper.cancel();
                  int yaw = (Byte)packetWrapper.get(Type.BYTE, 0);
                  replacement.setHeadYaw((float)yaw * 360.0F / 256.0F);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.ENTITY_METADATA, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Types1_9.METADATA_LIST, Types1_8.METADATA_LIST);
            this.handler((wrapper) -> {
               List<Metadata> metadataList = (List)wrapper.get(Types1_8.METADATA_LIST, 0);
               int entityId = (Integer)wrapper.get(Type.VAR_INT, 0);
               EntityTracker tracker = (EntityTracker)wrapper.user().get(EntityTracker.class);
               if (tracker.getClientEntityTypes().containsKey(entityId)) {
                  protocol.getMetadataRewriter().transform(tracker, entityId, metadataList);
                  if (metadataList.isEmpty()) {
                     wrapper.cancel();
                  }
               } else {
                  tracker.addMetadataToBuffer(entityId, metadataList);
                  wrapper.cancel();
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.ATTACH_ENTITY, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.INT);
            this.create(Type.BOOLEAN, true);
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.ENTITY_EQUIPMENT, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.handler((packetWrapper) -> {
               int slot = (Integer)packetWrapper.read(Type.VAR_INT);
               if (slot == 1) {
                  packetWrapper.cancel();
               } else if (slot > 1) {
                  --slot;
               }

               packetWrapper.write(Type.SHORT, (short)slot);
            });
            this.map(Type.ITEM1_8);
            this.handler((packetWrapper) -> {
               packetWrapper.set(Type.ITEM1_8, 0, protocol.getItemRewriter().handleItemToClient((Item)packetWrapper.get(Type.ITEM1_8, 0)));
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.SET_PASSENGERS, (ClientboundPacketType)null, new PacketHandlers() {
         public void register() {
            this.handler((packetWrapper) -> {
               packetWrapper.cancel();
               EntityTracker entityTracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
               int vehicle = (Integer)packetWrapper.read(Type.VAR_INT);
               int count = (Integer)packetWrapper.read(Type.VAR_INT);
               ArrayList<Integer> passengers = new ArrayList();

               for(int ix = 0; ix < count; ++ix) {
                  passengers.add((Integer)packetWrapper.read(Type.VAR_INT));
               }

               List<Integer> oldPassengers = entityTracker.getPassengers(vehicle);
               entityTracker.setPassengers(vehicle, passengers);
               if (!oldPassengers.isEmpty()) {
                  Iterator var6 = oldPassengers.iterator();

                  while(var6.hasNext()) {
                     Integer passenger = (Integer)var6.next();
                     PacketWrapper detach = PacketWrapper.create(ClientboundPackets1_8.ATTACH_ENTITY, (ByteBuf)null, packetWrapper.user());
                     detach.write(Type.INT, passenger);
                     detach.write(Type.INT, -1);
                     detach.write(Type.BOOLEAN, false);
                     PacketUtil.sendPacket(detach, Protocol1_8To1_9.class);
                  }
               }

               for(int i = 0; i < count; ++i) {
                  int v = i == 0 ? vehicle : (Integer)passengers.get(i - 1);
                  int p = (Integer)passengers.get(i);
                  PacketWrapper attach = PacketWrapper.create(ClientboundPackets1_8.ATTACH_ENTITY, (ByteBuf)null, packetWrapper.user());
                  attach.write(Type.INT, p);
                  attach.write(Type.INT, v);
                  attach.write(Type.BOOLEAN, false);
                  PacketUtil.sendPacket(attach, Protocol1_8To1_9.class);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.ENTITY_TELEPORT, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.DOUBLE, Protocol1_8To1_9.TO_OLD_INT);
            this.map(Type.DOUBLE, Protocol1_8To1_9.TO_OLD_INT);
            this.map(Type.DOUBLE, Protocol1_8To1_9.TO_OLD_INT);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.BOOLEAN);
            this.handler((packetWrapper) -> {
               int entityId = (Integer)packetWrapper.get(Type.VAR_INT, 0);
               EntityTypes1_10.EntityType type = (EntityTypes1_10.EntityType)((EntityTracker)packetWrapper.user().get(EntityTracker.class)).getClientEntityTypes().get(entityId);
               if (type == EntityTypes1_10.EntityType.BOAT) {
                  byte yaw = (Byte)packetWrapper.get(Type.BYTE, 1);
                  yaw = (byte)(yaw - 64);
                  packetWrapper.set(Type.BYTE, 0, yaw);
                  int y = (Integer)packetWrapper.get(Type.INT, 1);
                  y += 10;
                  packetWrapper.set(Type.INT, 1, y);
               }

            });
            this.handler((packetWrapper) -> {
               int entityId = (Integer)packetWrapper.get(Type.VAR_INT, 0);
               ((EntityTracker)packetWrapper.user().get(EntityTracker.class)).resetEntityOffset(entityId);
            });
            this.handler((packetWrapper) -> {
               int entityId = (Integer)packetWrapper.get(Type.VAR_INT, 0);
               EntityTracker tracker = (EntityTracker)packetWrapper.user().get(EntityTracker.class);
               EntityModel replacement = tracker.getEntityReplacement(entityId);
               if (replacement != null) {
                  packetWrapper.cancel();
                  int x = (Integer)packetWrapper.get(Type.INT, 0);
                  int y = (Integer)packetWrapper.get(Type.INT, 1);
                  int z = (Integer)packetWrapper.get(Type.INT, 2);
                  int yaw = (Byte)packetWrapper.get(Type.BYTE, 0);
                  int pitch = (Byte)packetWrapper.get(Type.BYTE, 1);
                  replacement.updateReplacementPosition((double)x / 32.0D, (double)y / 32.0D, (double)z / 32.0D);
                  replacement.setYawPitch((float)yaw * 360.0F / 256.0F, (float)pitch * 360.0F / 256.0F);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.ENTITY_PROPERTIES, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.INT);
            this.handler((packetWrapper) -> {
               boolean player = (Integer)packetWrapper.get(Type.VAR_INT, 0) == ((EntityTracker)packetWrapper.user().get(EntityTracker.class)).getPlayerId();
               int size = (Integer)packetWrapper.get(Type.INT, 0);
               int removed = 0;

               for(int i = 0; i < size; ++i) {
                  String key = (String)packetWrapper.read(Type.STRING);
                  boolean skip = !Protocol1_8To1_9.VALID_ATTRIBUTES.contains(key);
                  double value = (Double)packetWrapper.read(Type.DOUBLE);
                  int modifierSize = (Integer)packetWrapper.read(Type.VAR_INT);
                  if (!skip) {
                     packetWrapper.write(Type.STRING, key);
                     packetWrapper.write(Type.DOUBLE, value);
                     packetWrapper.write(Type.VAR_INT, modifierSize);
                  } else {
                     ++removed;
                  }

                  ArrayList<Pair<Byte, Double>> modifiers = new ArrayList();

                  for(int j = 0; j < modifierSize; ++j) {
                     UUID uuid = (UUID)packetWrapper.read(Type.UUID);
                     double amount = (Double)packetWrapper.read(Type.DOUBLE);
                     byte operation = (Byte)packetWrapper.read(Type.BYTE);
                     modifiers.add(new Pair(operation, amount));
                     if (!skip) {
                        packetWrapper.write(Type.UUID, uuid);
                        packetWrapper.write(Type.DOUBLE, amount);
                        packetWrapper.write(Type.BYTE, operation);
                     }
                  }

                  if (player && key.equals("generic.attackSpeed")) {
                     ((Cooldown)packetWrapper.user().get(Cooldown.class)).setAttackSpeed(value, modifiers);
                  }
               }

               packetWrapper.set(Type.INT, 0, size - removed);
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.ENTITY_EFFECT, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.VAR_INT);
            this.map(Type.BYTE);
            this.handler((packetWrapper) -> {
               int id = (Byte)packetWrapper.get(Type.BYTE, 0);
               if (id > 23) {
                  packetWrapper.cancel();
               }

               if (id == 25) {
                  if ((Integer)packetWrapper.get(Type.VAR_INT, 0) != ((EntityTracker)packetWrapper.user().get(EntityTracker.class)).getPlayerId()) {
                     return;
                  }

                  Levitation levitation = (Levitation)packetWrapper.user().get(Levitation.class);
                  levitation.setActive(true);
                  levitation.setAmplifier((Byte)packetWrapper.get(Type.BYTE, 1));
               }

            });
         }
      });
   }
}
