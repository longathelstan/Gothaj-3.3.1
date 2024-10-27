package com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.packets;

import com.viaversion.viarewind.ViaRewind;
import com.viaversion.viarewind.protocol.protocol1_7_2_5to1_7_6_10.ClientboundPackets1_7_2_5;
import com.viaversion.viarewind.protocol.protocol1_7_2_5to1_7_6_10.ServerboundPackets1_7_2_5;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.Protocol1_7_6_10To1_8;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.model.VirtualHologramEntity;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.provider.TitleRenderProvider;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.storage.EntityTracker1_7_6_10;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.storage.GameProfileStorage;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.storage.InventoryTracker;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.storage.PlayerSessionStorage;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.storage.Scoreboard;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.Types1_7_6_10;
import com.viaversion.viarewind.utils.ChatUtil;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viarewind.utils.math.AABB;
import com.viaversion.viarewind.utils.math.Ray3d;
import com.viaversion.viarewind.utils.math.RayTracing;
import com.viaversion.viarewind.utils.math.Vector3d;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.ClientWorld;
import com.viaversion.viaversion.api.minecraft.Environment;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_10;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.util.ComponentUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PlayerPackets {
   public static void register(final Protocol1_7_6_10To1_8 protocol) {
      protocol.registerClientbound(ClientboundPackets1_8.JOIN_GAME, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.BYTE);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.STRING);
            this.read(Type.BOOLEAN);
            this.handler((wrapper) -> {
               if (ViaRewind.getConfig().isReplaceAdventureMode() && (Short)wrapper.get(Type.UNSIGNED_BYTE, 0) == 2) {
                  wrapper.set(Type.UNSIGNED_BYTE, 0, Short.valueOf((short)0));
               }

               EntityTracker1_7_6_10 tracker = (EntityTracker1_7_6_10)wrapper.user().get(EntityTracker1_7_6_10.class);
               tracker.setClientEntityId((Integer)wrapper.get(Type.INT, 0));
               tracker.addPlayer((Integer)wrapper.get(Type.INT, 0), wrapper.user().getProtocolInfo().getUuid());
               ((PlayerSessionStorage)wrapper.user().get(PlayerSessionStorage.class)).gameMode = (Short)wrapper.get(Type.UNSIGNED_BYTE, 0);
               ((ClientWorld)wrapper.user().get(ClientWorld.class)).setEnvironment((Byte)wrapper.get(Type.BYTE, 0));
               wrapper.user().put(new Scoreboard(wrapper.user()));
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.CHAT_MESSAGE, new PacketHandlers() {
         public void register() {
            this.map(Type.COMPONENT);
            this.handler((wrapper) -> {
               int position = (Byte)wrapper.read(Type.BYTE);
               if (position == 2) {
                  wrapper.cancel();
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.SPAWN_POSITION, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_8, Types1_7_6_10.INT_POSITION);
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.UPDATE_HEALTH, new PacketHandlers() {
         public void register() {
            this.map(Type.FLOAT);
            this.map(Type.VAR_INT, Type.SHORT);
            this.map(Type.FLOAT);
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.RESPAWN, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.STRING);
            this.handler((wrapper) -> {
               if (ViaRewind.getConfig().isReplaceAdventureMode() && (Short)wrapper.get(Type.UNSIGNED_BYTE, 1) == 2) {
                  wrapper.set(Type.UNSIGNED_BYTE, 1, Short.valueOf((short)0));
               }

               ((PlayerSessionStorage)wrapper.user().get(PlayerSessionStorage.class)).gameMode = (Short)wrapper.get(Type.UNSIGNED_BYTE, 1);
               ClientWorld world = (ClientWorld)wrapper.user().get(ClientWorld.class);
               Environment dimension = Environment.getEnvironmentById((Integer)wrapper.get(Type.INT, 0));
               EntityTracker1_7_6_10 tracker = (EntityTracker1_7_6_10)wrapper.user().get(EntityTracker1_7_6_10.class);
               if (world.getEnvironment() != dimension) {
                  world.setEnvironment(dimension.id());
                  tracker.clear();
                  tracker.getEntityMap().put(tracker.getPlayerId(), EntityTypes1_10.EntityType.ENTITY_HUMAN);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.PLAYER_POSITION, new PacketHandlers() {
         public void register() {
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.handler((wrapper) -> {
               double x = (Double)wrapper.get(Type.DOUBLE, 0);
               double y = (Double)wrapper.get(Type.DOUBLE, 1);
               double z = (Double)wrapper.get(Type.DOUBLE, 2);
               float yaw = (Float)wrapper.get(Type.FLOAT, 0);
               float pitch = (Float)wrapper.get(Type.FLOAT, 1);
               PlayerSessionStorage playerSession = (PlayerSessionStorage)wrapper.user().get(PlayerSessionStorage.class);
               int flags = (Byte)wrapper.read(Type.BYTE);
               if ((flags & 1) == 1) {
                  wrapper.set(Type.DOUBLE, 0, x + playerSession.getPosX());
               }

               if ((flags & 2) == 2) {
                  y += playerSession.getPosY();
               }

               playerSession.receivedPosY = y;
               wrapper.set(Type.DOUBLE, 1, y + 1.6200000047683716D);
               if ((flags & 4) == 4) {
                  wrapper.set(Type.DOUBLE, 2, z + playerSession.getPosZ());
               }

               if ((flags & 8) == 8) {
                  wrapper.set(Type.FLOAT, 0, yaw + playerSession.yaw);
               }

               if ((flags & 16) == 16) {
                  wrapper.set(Type.FLOAT, 1, pitch + playerSession.pitch);
               }

               wrapper.write(Type.BOOLEAN, playerSession.onGround);
               EntityTracker1_7_6_10 tracker = (EntityTracker1_7_6_10)wrapper.user().get(EntityTracker1_7_6_10.class);
               if (tracker.spectatingPlayerId != tracker.getPlayerId()) {
                  wrapper.cancel();
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.SET_EXPERIENCE, new PacketHandlers() {
         public void register() {
            this.map(Type.FLOAT);
            this.map(Type.VAR_INT, Type.SHORT);
            this.map(Type.VAR_INT, Type.SHORT);
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.GAME_EVENT, new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.FLOAT);
            this.handler((wrapper) -> {
               if ((Short)wrapper.get(Type.UNSIGNED_BYTE, 0) == 3) {
                  int gameMode = ((Float)wrapper.get(Type.FLOAT, 0)).intValue();
                  PlayerSessionStorage playerSession = (PlayerSessionStorage)wrapper.user().get(PlayerSessionStorage.class);
                  if (gameMode == 3 || playerSession.gameMode == 3) {
                     UUID myId = wrapper.user().getProtocolInfo().getUuid();
                     Item[] equipment = new Item[4];
                     int i;
                     if (gameMode == 3) {
                        GameProfileStorage.GameProfile profile = ((GameProfileStorage)wrapper.user().get(GameProfileStorage.class)).get(myId);
                        equipment[3] = profile == null ? null : profile.getSkull();
                     } else {
                        for(i = 0; i < equipment.length; ++i) {
                           equipment[i] = playerSession.getPlayerEquipment(myId, i);
                        }
                     }

                     for(i = 0; i < equipment.length; ++i) {
                        PacketWrapper setSlot = PacketWrapper.create(ClientboundPackets1_7_2_5.SET_SLOT, (UserConnection)wrapper.user());
                        setSlot.write(Type.BYTE, (byte)0);
                        setSlot.write(Type.SHORT, (short)(8 - i));
                        setSlot.write(Types1_7_6_10.COMPRESSED_NBT_ITEM, equipment[i]);
                        PacketUtil.sendPacket(setSlot, Protocol1_7_6_10To1_8.class);
                     }
                  }

                  if (gameMode == 2 && ViaRewind.getConfig().isReplaceAdventureMode()) {
                     gameMode = 0;
                     wrapper.set(Type.FLOAT, 0, 0.0F);
                  }

                  ((PlayerSessionStorage)wrapper.user().get(PlayerSessionStorage.class)).gameMode = gameMode;
               }
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.OPEN_SIGN_EDITOR, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_8, Types1_7_6_10.INT_POSITION);
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.PLAYER_INFO, new PacketHandlers() {
         public void register() {
            this.handler((packetWrapper) -> {
               packetWrapper.cancel();
               int action = (Integer)packetWrapper.read(Type.VAR_INT);
               int count = (Integer)packetWrapper.read(Type.VAR_INT);
               GameProfileStorage gameProfileStorage = (GameProfileStorage)packetWrapper.user().get(GameProfileStorage.class);

               for(int i = 0; i < count; ++i) {
                  UUID uuid = (UUID)packetWrapper.read(Type.UUID);
                  GameProfileStorage.GameProfile gameProfilexx;
                  int entityId;
                  if (action == 0) {
                     String name = (String)packetWrapper.read(Type.STRING);
                     gameProfilexx = gameProfileStorage.get(uuid);
                     if (gameProfilexx == null) {
                        gameProfilexx = gameProfileStorage.put(uuid, name);
                     }

                     int var21 = (Integer)packetWrapper.read(Type.VAR_INT);

                     while(var21-- > 0) {
                        String propertyName = (String)packetWrapper.read(Type.STRING);
                        String propertyValue = (String)packetWrapper.read(Type.STRING);
                        String propertySignature = (String)packetWrapper.read(Type.OPTIONAL_STRING);
                        gameProfilexx.properties.add(new GameProfileStorage.Property(propertyName, propertyValue, propertySignature));
                     }

                     entityId = (Integer)packetWrapper.read(Type.VAR_INT);
                     int ping = (Integer)packetWrapper.read(Type.VAR_INT);
                     gameProfilexx.ping = ping;
                     gameProfilexx.gamemode = entityId;
                     JsonElement displayName = (JsonElement)packetWrapper.read(Type.OPTIONAL_COMPONENT);
                     if (displayName != null) {
                        gameProfilexx.setDisplayName(ChatUtil.jsonToLegacy(displayName));
                     }

                     PacketWrapper packetx = PacketWrapper.create(ClientboundPackets1_7_2_5.PLAYER_INFO, (ByteBuf)null, packetWrapper.user());
                     packetx.write(Type.STRING, gameProfilexx.getDisplayName());
                     packetx.write(Type.BOOLEAN, true);
                     packetx.write(Type.SHORT, (short)ping);
                     PacketUtil.sendPacket(packetx, Protocol1_7_6_10To1_8.class);
                  } else {
                     int gamemode;
                     if (action == 1) {
                        gamemode = (Integer)packetWrapper.read(Type.VAR_INT);
                        gameProfilexx = gameProfileStorage.get(uuid);
                        if (gameProfilexx != null && gameProfilexx.gamemode != gamemode) {
                           if (gamemode == 3 || gameProfilexx.gamemode == 3) {
                              EntityTracker1_7_6_10 tracker = (EntityTracker1_7_6_10)packetWrapper.user().get(EntityTracker1_7_6_10.class);
                              entityId = tracker.getPlayerEntityId(uuid);
                              boolean isOwnPlayer = entityId == tracker.getPlayerId();
                              if (entityId != -1) {
                                 Item[] equipment = new Item[isOwnPlayer ? 4 : 5];
                                 if (gamemode == 3) {
                                    equipment[isOwnPlayer ? 3 : 4] = gameProfilexx.getSkull();
                                 } else {
                                    for(int j = 0; j < equipment.length; ++j) {
                                       equipment[j] = ((PlayerSessionStorage)packetWrapper.user().get(PlayerSessionStorage.class)).getPlayerEquipment(uuid, j);
                                    }
                                 }

                                 for(short slot = 0; slot < equipment.length; ++slot) {
                                    PacketWrapper equipmentPacket = PacketWrapper.create(ClientboundPackets1_7_2_5.ENTITY_EQUIPMENT, (UserConnection)packetWrapper.user());
                                    equipmentPacket.write(Type.INT, entityId);
                                    equipmentPacket.write(Type.SHORT, slot);
                                    equipmentPacket.write(Types1_7_6_10.COMPRESSED_NBT_ITEM, equipment[slot]);
                                    PacketUtil.sendPacket(equipmentPacket, Protocol1_7_6_10To1_8.class);
                                 }
                              }
                           }

                           gameProfilexx.gamemode = gamemode;
                        }
                     } else if (action == 2) {
                        gamemode = (Integer)packetWrapper.read(Type.VAR_INT);
                        gameProfilexx = gameProfileStorage.get(uuid);
                        if (gameProfilexx != null) {
                           PacketWrapper packetxxx = PacketWrapper.create(ClientboundPackets1_7_2_5.PLAYER_INFO, (ByteBuf)null, packetWrapper.user());
                           packetxxx.write(Type.STRING, gameProfilexx.getDisplayName());
                           packetxxx.write(Type.BOOLEAN, false);
                           packetxxx.write(Type.SHORT, (short)gameProfilexx.ping);
                           PacketUtil.sendPacket(packetxxx, Protocol1_7_6_10To1_8.class);
                           gameProfilexx.ping = gamemode;
                           packetxxx = PacketWrapper.create(ClientboundPackets1_7_2_5.PLAYER_INFO, (ByteBuf)null, packetWrapper.user());
                           packetxxx.write(Type.STRING, gameProfilexx.getDisplayName());
                           packetxxx.write(Type.BOOLEAN, true);
                           packetxxx.write(Type.SHORT, (short)gamemode);
                           PacketUtil.sendPacket(packetxxx, Protocol1_7_6_10To1_8.class);
                        }
                     } else if (action == 3) {
                        JsonElement displayNameComponent = (JsonElement)packetWrapper.read(Type.OPTIONAL_COMPONENT);
                        String displayNamex = displayNameComponent != null ? ChatUtil.jsonToLegacy(displayNameComponent) : null;
                        GameProfileStorage.GameProfile gameProfilex = gameProfileStorage.get(uuid);
                        if (gameProfilex != null && (gameProfilex.displayName != null || displayNamex != null)) {
                           PacketWrapper packetxx = PacketWrapper.create(ClientboundPackets1_7_2_5.PLAYER_INFO, (ByteBuf)null, packetWrapper.user());
                           packetxx.write(Type.STRING, gameProfilex.getDisplayName());
                           packetxx.write(Type.BOOLEAN, false);
                           packetxx.write(Type.SHORT, (short)gameProfilex.ping);
                           PacketUtil.sendPacket(packetxx, Protocol1_7_6_10To1_8.class);
                           if (gameProfilex.displayName == null && displayNamex != null || gameProfilex.displayName != null && displayNamex == null || !gameProfilex.displayName.equals(displayNamex)) {
                              gameProfilex.setDisplayName(displayNamex);
                           }

                           packetxx = PacketWrapper.create(ClientboundPackets1_7_2_5.PLAYER_INFO, (ByteBuf)null, packetWrapper.user());
                           packetxx.write(Type.STRING, gameProfilex.getDisplayName());
                           packetxx.write(Type.BOOLEAN, true);
                           packetxx.write(Type.SHORT, (short)gameProfilex.ping);
                           PacketUtil.sendPacket(packetxx, Protocol1_7_6_10To1_8.class);
                        }
                     } else if (action == 4) {
                        GameProfileStorage.GameProfile gameProfile = gameProfileStorage.remove(uuid);
                        if (gameProfile != null) {
                           PacketWrapper packet = PacketWrapper.create(ClientboundPackets1_7_2_5.PLAYER_INFO, (ByteBuf)null, packetWrapper.user());
                           packet.write(Type.STRING, gameProfile.getDisplayName());
                           packet.write(Type.BOOLEAN, false);
                           packet.write(Type.SHORT, (short)gameProfile.ping);
                           PacketUtil.sendPacket(packet, Protocol1_7_6_10To1_8.class);
                        }
                     }
                  }
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.PLAYER_ABILITIES, new PacketHandlers() {
         public void register() {
            this.map(Type.BYTE);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.handler((packetWrapper) -> {
               byte flags = (Byte)packetWrapper.get(Type.BYTE, 0);
               float flySpeed = (Float)packetWrapper.get(Type.FLOAT, 0);
               float walkSpeed = (Float)packetWrapper.get(Type.FLOAT, 1);
               PlayerSessionStorage abilities = (PlayerSessionStorage)packetWrapper.user().get(PlayerSessionStorage.class);
               abilities.invincible = (flags & 8) == 8;
               abilities.allowFly = (flags & 4) == 4;
               abilities.flying = (flags & 2) == 2;
               abilities.creative = (flags & 1) == 1;
               abilities.flySpeed = flySpeed;
               abilities.walkSpeed = walkSpeed;
               if (abilities.sprinting && abilities.flying) {
                  packetWrapper.set(Type.FLOAT, 0, abilities.flySpeed * 2.0F);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.PLUGIN_MESSAGE, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.handler((packetWrapper) -> {
               String channel = (String)packetWrapper.get(Type.STRING, 0);
               if (channel.equalsIgnoreCase("MC|TrList")) {
                  packetWrapper.passthrough(Type.INT);
                  short size;
                  if (packetWrapper.isReadable(Type.BYTE, 0)) {
                     size = (Byte)packetWrapper.passthrough(Type.BYTE);
                  } else {
                     size = (Short)packetWrapper.passthrough(Type.UNSIGNED_BYTE);
                  }

                  for(int i = 0; i < size; ++i) {
                     Item item = protocol.getItemRewriter().handleItemToClient((Item)packetWrapper.read(Type.ITEM1_8));
                     packetWrapper.write(Types1_7_6_10.COMPRESSED_NBT_ITEM, item);
                     item = protocol.getItemRewriter().handleItemToClient((Item)packetWrapper.read(Type.ITEM1_8));
                     packetWrapper.write(Types1_7_6_10.COMPRESSED_NBT_ITEM, item);
                     boolean has3Items = (Boolean)packetWrapper.passthrough(Type.BOOLEAN);
                     if (has3Items) {
                        item = protocol.getItemRewriter().handleItemToClient((Item)packetWrapper.read(Type.ITEM1_8));
                        packetWrapper.write(Types1_7_6_10.COMPRESSED_NBT_ITEM, item);
                     }

                     packetWrapper.passthrough(Type.BOOLEAN);
                     packetWrapper.read(Type.INT);
                     packetWrapper.read(Type.INT);
                  }
               } else if (channel.equalsIgnoreCase("MC|Brand")) {
                  packetWrapper.write(Type.REMAINING_BYTES, ((String)packetWrapper.read(Type.STRING)).getBytes(StandardCharsets.UTF_8));
               }

               packetWrapper.cancel();
               packetWrapper.setPacketType((PacketType)null);
               ByteBuf newPacketBuf = Unpooled.buffer();
               packetWrapper.writeToBuffer(newPacketBuf);
               PacketWrapper newWrapper = PacketWrapper.create(ClientboundPackets1_7_2_5.PLUGIN_MESSAGE, newPacketBuf, packetWrapper.user());
               newWrapper.passthrough(Type.STRING);
               if (newPacketBuf.readableBytes() <= 32767) {
                  newWrapper.write(Type.SHORT, (short)newPacketBuf.readableBytes());
                  newWrapper.send(Protocol1_7_6_10To1_8.class);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.CAMERA, (ClientboundPacketType)null, new PacketHandlers() {
         public void register() {
            this.handler((packetWrapper) -> {
               packetWrapper.cancel();
               EntityTracker1_7_6_10 tracker = (EntityTracker1_7_6_10)packetWrapper.user().get(EntityTracker1_7_6_10.class);
               int entityId = (Integer)packetWrapper.read(Type.VAR_INT);
               int spectating = tracker.spectatingPlayerId;
               if (spectating != entityId) {
                  tracker.setSpectating(entityId);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.TITLE, (ClientboundPacketType)null, new PacketHandlers() {
         public void register() {
            this.handler((packetWrapper) -> {
               packetWrapper.cancel();
               TitleRenderProvider titleRenderProvider = (TitleRenderProvider)Via.getManager().getProviders().get(TitleRenderProvider.class);
               if (titleRenderProvider != null) {
                  int action = (Integer)packetWrapper.read(Type.VAR_INT);
                  UUID uuid = packetWrapper.user().getProtocolInfo().getUuid();
                  switch(action) {
                  case 0:
                     titleRenderProvider.setTitle(uuid, (String)packetWrapper.read(Type.STRING));
                     break;
                  case 1:
                     titleRenderProvider.setSubTitle(uuid, (String)packetWrapper.read(Type.STRING));
                     break;
                  case 2:
                     titleRenderProvider.setTimings(uuid, (Integer)packetWrapper.read(Type.INT), (Integer)packetWrapper.read(Type.INT), (Integer)packetWrapper.read(Type.INT));
                     break;
                  case 3:
                     titleRenderProvider.clear(uuid);
                     break;
                  case 4:
                     titleRenderProvider.reset(uuid);
                  }

               }
            });
         }
      });
      protocol.cancelClientbound(ClientboundPackets1_8.TAB_LIST);
      protocol.registerClientbound(ClientboundPackets1_8.RESOURCE_PACK, ClientboundPackets1_7_2_5.PLUGIN_MESSAGE, new PacketHandlers() {
         public void register() {
            this.create(Type.STRING, "MC|RPack");
            this.handler((packetWrapper) -> {
               ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();

               try {
                  Type.STRING.write(buf, (String)packetWrapper.read(Type.STRING));
                  packetWrapper.write(Type.SHORT_BYTE_ARRAY, (byte[])Type.REMAINING_BYTES.read(buf));
               } finally {
                  buf.release();
               }

            });
            this.read(Type.STRING);
         }
      });
      protocol.registerServerbound(ServerboundPackets1_7_2_5.CHAT_MESSAGE, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.handler((packetWrapper) -> {
               String msg = (String)packetWrapper.get(Type.STRING, 0);
               int gamemode = ((PlayerSessionStorage)packetWrapper.user().get(PlayerSessionStorage.class)).gameMode;
               if (gamemode == 3 && msg.toLowerCase().startsWith("/stp ")) {
                  String username = msg.split(" ")[1];
                  GameProfileStorage storage = (GameProfileStorage)packetWrapper.user().get(GameProfileStorage.class);
                  GameProfileStorage.GameProfile profile = storage.get(username, true);
                  if (profile != null && profile.uuid != null) {
                     packetWrapper.cancel();
                     PacketWrapper teleportPacket = PacketWrapper.create(24, (ByteBuf)null, packetWrapper.user());
                     teleportPacket.write(Type.UUID, profile.uuid);
                     PacketUtil.sendToServer(teleportPacket, Protocol1_7_6_10To1_8.class, true, true);
                  }
               }

            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_7_2_5.INTERACT_ENTITY, new PacketHandlers() {
         public void register() {
            this.map(Type.INT, Type.VAR_INT);
            this.map(Type.BYTE, Type.VAR_INT);
            this.handler((wrapper) -> {
               int mode = (Integer)wrapper.get(Type.VAR_INT, 1);
               if (mode == 0) {
                  int entityId = (Integer)wrapper.get(Type.VAR_INT, 0);
                  EntityTracker1_7_6_10 tracker = (EntityTracker1_7_6_10)wrapper.user().get(EntityTracker1_7_6_10.class);
                  PlayerSessionStorage position = (PlayerSessionStorage)wrapper.user().get(PlayerSessionStorage.class);
                  if (tracker.getVirtualHologramMap().containsKey(entityId)) {
                     AABB boundingBox = ((VirtualHologramEntity)tracker.getVirtualHologramMap().get(entityId)).getBoundingBox();
                     Vector3d pos = new Vector3d(position.getPosX(), position.getPosY() + 1.8D, position.getPosZ());
                     double yaw = Math.toRadians((double)position.yaw);
                     double pitch = Math.toRadians((double)position.pitch);
                     Vector3d dir = new Vector3d(-Math.cos(pitch) * Math.sin(yaw), -Math.sin(pitch), Math.cos(pitch) * Math.cos(yaw));
                     Ray3d ray = new Ray3d(pos, dir);
                     Vector3d intersection = RayTracing.trace(ray, boundingBox, 5.0D);
                     if (intersection == null) {
                        return;
                     }

                     intersection.substract(boundingBox.getMin());
                     int modex = 2;
                     wrapper.set(Type.VAR_INT, 1, Integer.valueOf(modex));
                     wrapper.write(Type.FLOAT, (float)intersection.getX());
                     wrapper.write(Type.FLOAT, (float)intersection.getY());
                     wrapper.write(Type.FLOAT, (float)intersection.getZ());
                  }

               }
            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_7_2_5.PLAYER_MOVEMENT, new PacketHandlers() {
         public void register() {
            this.map(Type.BOOLEAN);
            this.handler((packetWrapper) -> {
               PlayerSessionStorage playerSession = (PlayerSessionStorage)packetWrapper.user().get(PlayerSessionStorage.class);
               playerSession.onGround = (Boolean)packetWrapper.get(Type.BOOLEAN, 0);
            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_7_2_5.PLAYER_POSITION, new PacketHandlers() {
         public void register() {
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.read(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.BOOLEAN);
            this.handler((packetWrapper) -> {
               double x = (Double)packetWrapper.get(Type.DOUBLE, 0);
               double feetY = (Double)packetWrapper.get(Type.DOUBLE, 1);
               double z = (Double)packetWrapper.get(Type.DOUBLE, 2);
               PlayerSessionStorage playerSession = (PlayerSessionStorage)packetWrapper.user().get(PlayerSessionStorage.class);
               playerSession.onGround = (Boolean)packetWrapper.get(Type.BOOLEAN, 0);
               playerSession.setPos(x, feetY, z);
            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_7_2_5.PLAYER_ROTATION, new PacketHandlers() {
         public void register() {
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.BOOLEAN);
            this.handler((packetWrapper) -> {
               PlayerSessionStorage playerSession = (PlayerSessionStorage)packetWrapper.user().get(PlayerSessionStorage.class);
               playerSession.yaw = (Float)packetWrapper.get(Type.FLOAT, 0);
               playerSession.pitch = (Float)packetWrapper.get(Type.FLOAT, 1);
               playerSession.onGround = (Boolean)packetWrapper.get(Type.BOOLEAN, 0);
            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_7_2_5.PLAYER_POSITION_AND_ROTATION, new PacketHandlers() {
         public void register() {
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.read(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.BOOLEAN);
            this.handler((packetWrapper) -> {
               double x = (Double)packetWrapper.get(Type.DOUBLE, 0);
               double feetY = (Double)packetWrapper.get(Type.DOUBLE, 1);
               double z = (Double)packetWrapper.get(Type.DOUBLE, 2);
               float yaw = (Float)packetWrapper.get(Type.FLOAT, 0);
               float pitch = (Float)packetWrapper.get(Type.FLOAT, 1);
               PlayerSessionStorage playerSession = (PlayerSessionStorage)packetWrapper.user().get(PlayerSessionStorage.class);
               playerSession.onGround = (Boolean)packetWrapper.get(Type.BOOLEAN, 0);
               playerSession.setPos(x, feetY, z);
               playerSession.yaw = yaw;
               playerSession.pitch = pitch;
            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_7_2_5.PLAYER_DIGGING, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.handler((packetWrapper) -> {
               int x = (Integer)packetWrapper.read(Type.INT);
               int y = (Short)packetWrapper.read(Type.UNSIGNED_BYTE);
               int z = (Integer)packetWrapper.read(Type.INT);
               packetWrapper.write(Type.POSITION1_8, new Position(x, y, z));
            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_7_2_5.PLAYER_BLOCK_PLACEMENT, new PacketHandlers() {
         public void register() {
            this.handler((packetWrapper) -> {
               int x = (Integer)packetWrapper.read(Type.INT);
               int y = (Short)packetWrapper.read(Type.UNSIGNED_BYTE);
               int z = (Integer)packetWrapper.read(Type.INT);
               packetWrapper.write(Type.POSITION1_8, new Position(x, y, z));
               packetWrapper.passthrough(Type.BYTE);
               Item item = (Item)packetWrapper.read(Types1_7_6_10.COMPRESSED_NBT_ITEM);
               item = protocol.getItemRewriter().handleItemToServer(item);
               packetWrapper.write(Type.ITEM1_8, item);

               for(int i = 0; i < 3; ++i) {
                  packetWrapper.passthrough(Type.BYTE);
               }

            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_7_2_5.ANIMATION, new PacketHandlers() {
         public void register() {
            this.handler((packetWrapper) -> {
               int entityId = (Integer)packetWrapper.read(Type.INT);
               int animationx = (Byte)packetWrapper.read(Type.BYTE);
               if (animationx != 1) {
                  packetWrapper.cancel();
                  byte animation;
                  switch(animationx) {
                  case 3:
                     animation = 2;
                     break;
                  case 104:
                     animation = 0;
                     break;
                  case 105:
                     animation = 1;
                     break;
                  default:
                     return;
                  }

                  PacketWrapper entityAction = PacketWrapper.create(11, (ByteBuf)null, packetWrapper.user());
                  entityAction.write(Type.VAR_INT, entityId);
                  entityAction.write(Type.VAR_INT, Integer.valueOf(animation));
                  entityAction.write(Type.VAR_INT, 0);
                  PacketUtil.sendPacket(entityAction, Protocol1_7_6_10To1_8.class, true, true);
               }
            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_7_2_5.ENTITY_ACTION, new PacketHandlers() {
         public void register() {
            this.map(Type.INT, Type.VAR_INT);
            this.handler((packetWrapper) -> {
               packetWrapper.write(Type.VAR_INT, (Byte)packetWrapper.read(Type.BYTE) - 1);
            });
            this.map(Type.INT, Type.VAR_INT);
            this.handler((packetWrapper) -> {
               int action = (Integer)packetWrapper.get(Type.VAR_INT, 1);
               if (action == 3 || action == 4) {
                  PlayerSessionStorage playerSession = (PlayerSessionStorage)packetWrapper.user().get(PlayerSessionStorage.class);
                  playerSession.sprinting = action == 3;
                  PacketWrapper abilitiesPacket = PacketWrapper.create(57, (ByteBuf)null, packetWrapper.user());
                  abilitiesPacket.write(Type.BYTE, playerSession.combineAbilities());
                  abilitiesPacket.write(Type.FLOAT, playerSession.sprinting ? playerSession.flySpeed * 2.0F : playerSession.flySpeed);
                  abilitiesPacket.write(Type.FLOAT, playerSession.walkSpeed);
                  PacketUtil.sendPacket(abilitiesPacket, Protocol1_7_6_10To1_8.class);
               }

            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_7_2_5.STEER_VEHICLE, new PacketHandlers() {
         public void register() {
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.handler((packetWrapper) -> {
               boolean jump = (Boolean)packetWrapper.read(Type.BOOLEAN);
               boolean unmount = (Boolean)packetWrapper.read(Type.BOOLEAN);
               short flags = 0;
               if (jump) {
                  ++flags;
               }

               if (unmount) {
                  flags = (short)(flags + 2);
               }

               packetWrapper.write(Type.UNSIGNED_BYTE, flags);
               if (unmount) {
                  EntityTracker1_7_6_10 tracker = (EntityTracker1_7_6_10)packetWrapper.user().get(EntityTracker1_7_6_10.class);
                  if (tracker.spectatingPlayerId != tracker.getPlayerId()) {
                     PacketWrapper sneakPacket = PacketWrapper.create(11, (ByteBuf)null, packetWrapper.user());
                     sneakPacket.write(Type.VAR_INT, tracker.getPlayerId());
                     sneakPacket.write(Type.VAR_INT, 0);
                     sneakPacket.write(Type.VAR_INT, 0);
                     PacketWrapper unsneakPacket = PacketWrapper.create(11, (ByteBuf)null, packetWrapper.user());
                     unsneakPacket.write(Type.VAR_INT, tracker.getPlayerId());
                     unsneakPacket.write(Type.VAR_INT, 1);
                     unsneakPacket.write(Type.VAR_INT, 0);
                     PacketUtil.sendToServer(sneakPacket, Protocol1_7_6_10To1_8.class);
                  }
               }

            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_7_2_5.UPDATE_SIGN, new PacketHandlers() {
         public void register() {
            this.handler((packetWrapper) -> {
               int x = (Integer)packetWrapper.read(Type.INT);
               int y = (Short)packetWrapper.read(Type.SHORT);
               int z = (Integer)packetWrapper.read(Type.INT);
               packetWrapper.write(Type.POSITION1_8, new Position(x, y, z));

               for(int i = 0; i < 4; ++i) {
                  String line = (String)packetWrapper.read(Type.STRING);
                  packetWrapper.write(Type.COMPONENT, ComponentUtil.legacyToJson(line));
               }

            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_7_2_5.PLAYER_ABILITIES, new PacketHandlers() {
         public void register() {
            this.map(Type.BYTE);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.handler((packetWrapper) -> {
               PlayerSessionStorage playerSession = (PlayerSessionStorage)packetWrapper.user().get(PlayerSessionStorage.class);
               if (playerSession.allowFly) {
                  byte flags = (Byte)packetWrapper.get(Type.BYTE, 0);
                  playerSession.flying = (flags & 2) == 2;
               }

               packetWrapper.set(Type.FLOAT, 0, playerSession.flySpeed);
            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_7_2_5.TAB_COMPLETE, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.create(Type.OPTIONAL_POSITION1_8, (Object)null);
            this.handler((packetWrapper) -> {
               String msg = (String)packetWrapper.get(Type.STRING, 0);
               if (msg.toLowerCase().startsWith("/stp ")) {
                  packetWrapper.cancel();
                  String[] args = msg.split(" ");
                  if (args.length <= 2) {
                     String prefix = args.length == 1 ? "" : args[1];
                     GameProfileStorage storage = (GameProfileStorage)packetWrapper.user().get(GameProfileStorage.class);
                     List<GameProfileStorage.GameProfile> profiles = storage.getAllWithPrefix(prefix, true);
                     PacketWrapper tabComplete = PacketWrapper.create(58, (ByteBuf)null, packetWrapper.user());
                     tabComplete.write(Type.VAR_INT, profiles.size());
                     Iterator var7 = profiles.iterator();

                     while(var7.hasNext()) {
                        GameProfileStorage.GameProfile profile = (GameProfileStorage.GameProfile)var7.next();
                        tabComplete.write(Type.STRING, profile.name);
                     }

                     PacketUtil.sendPacket(tabComplete, Protocol1_7_6_10To1_8.class);
                  }
               }

            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_7_2_5.CLIENT_SETTINGS, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.BOOLEAN);
            this.read(Type.BYTE);
            this.handler((packetWrapper) -> {
               boolean cape = (Boolean)packetWrapper.read(Type.BOOLEAN);
               packetWrapper.write(Type.UNSIGNED_BYTE, (short)(cape ? 127 : 126));
            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_7_2_5.PLUGIN_MESSAGE, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.read(Type.SHORT);
            this.handler((packetWrapper) -> {
               String channel = (String)packetWrapper.get(Type.STRING, 0);
               byte var3 = -1;
               switch(channel.hashCode()) {
               case -751882236:
                  if (channel.equals("MC|ItemName")) {
                     var3 = 1;
                  }
                  break;
               case -296231034:
                  if (channel.equals("MC|BEdit")) {
                     var3 = 2;
                  }
                  break;
               case -295809223:
                  if (channel.equals("MC|BSign")) {
                     var3 = 3;
                  }
                  break;
               case -294893183:
                  if (channel.equals("MC|Brand")) {
                     var3 = 4;
                  }
                  break;
               case -278283530:
                  if (channel.equals("MC|TrSel")) {
                     var3 = 0;
                  }
               }

               switch(var3) {
               case 0:
                  packetWrapper.passthrough(Type.INT);
                  packetWrapper.read(Type.REMAINING_BYTES);
                  break;
               case 1:
                  byte[] data = (byte[])packetWrapper.read(Type.REMAINING_BYTES);
                  String name = new String(data, StandardCharsets.UTF_8);
                  packetWrapper.write(Type.STRING, name);
                  InventoryTracker windowTracker = (InventoryTracker)packetWrapper.user().get(InventoryTracker.class);
                  PacketWrapper updateCost = PacketWrapper.create(49, (ByteBuf)null, packetWrapper.user());
                  updateCost.write(Type.UNSIGNED_BYTE, windowTracker.anvilId);
                  updateCost.write(Type.SHORT, Short.valueOf((short)0));
                  updateCost.write(Type.SHORT, windowTracker.levelCost);
                  PacketUtil.sendPacket(updateCost, Protocol1_7_6_10To1_8.class, true, true);
                  break;
               case 2:
               case 3:
                  Item book = (Item)packetWrapper.read(Types1_7_6_10.COMPRESSED_NBT_ITEM);
                  CompoundTag tag = book.tag();
                  if (tag != null && tag.contains("pages")) {
                     ListTag pages = (ListTag)tag.get("pages");

                     for(int i = 0; i < pages.size(); ++i) {
                        StringTag page = (StringTag)pages.get(i);
                        String value = page.getValue();
                        value = ComponentUtil.legacyToJsonString(value);
                        page.setValue(value);
                     }
                  }

                  packetWrapper.write(Type.ITEM1_8, book);
                  break;
               case 4:
                  packetWrapper.write(Type.STRING, new String((byte[])packetWrapper.read(Type.REMAINING_BYTES), StandardCharsets.UTF_8));
               }

            });
         }
      });
   }
}
