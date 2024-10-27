package com.viaversion.viaversion.protocols.protocol1_9to1_8.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.ClientWorld;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_10;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ItemRewriter;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.PlayerMovementMapper;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.chat.ChatRewriter;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.chat.GameMode;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.CommandBlockProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.CompressionProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MainHandProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.ClientChunks;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;

public class PlayerPackets {
   public static void register(Protocol1_9To1_8 protocol) {
      protocol.registerClientbound(ClientboundPackets1_8.CHAT_MESSAGE, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            this.map(Type.BYTE);
            this.handler((wrapper) -> {
               try {
                  JsonObject obj = (JsonObject)wrapper.get(Type.COMPONENT, 0);
                  ChatRewriter.toClient(obj, wrapper.user());
               } catch (Exception var2) {
                  var2.printStackTrace();
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.TAB_LIST, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.DISCONNECT, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.TITLE, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               int action = (Integer)wrapper.get(Type.VAR_INT, 0);
               if (action == 0 || action == 1) {
                  Protocol1_9To1_8.FIX_JSON.write(wrapper, wrapper.read(Type.STRING));
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
            this.map(Type.BYTE);
            this.handler((wrapper) -> {
               wrapper.write(Type.VAR_INT, 0);
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.TEAMS, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.map(Type.BYTE);
            this.handler((wrapper) -> {
               byte mode = (Byte)wrapper.get(Type.BYTE, 0);
               if (mode == 0 || mode == 2) {
                  wrapper.passthrough(Type.STRING);
                  wrapper.passthrough(Type.STRING);
                  wrapper.passthrough(Type.STRING);
                  wrapper.passthrough(Type.BYTE);
                  wrapper.passthrough(Type.STRING);
                  wrapper.write(Type.STRING, Via.getConfig().isPreventCollision() ? "never" : "");
                  wrapper.passthrough(Type.BYTE);
               }

               if (mode == 0 || mode == 3 || mode == 4) {
                  String[] players = (String[])wrapper.passthrough(Type.STRING_ARRAY);
                  EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                  String myName = wrapper.user().getProtocolInfo().getUsername();
                  String teamNamex = (String)wrapper.get(Type.STRING, 0);
                  String[] var6 = players;
                  int var7 = players.length;

                  for(int var8 = 0; var8 < var7; ++var8) {
                     String player = var6[var8];
                     if (entityTracker.isAutoTeam() && player.equalsIgnoreCase(myName)) {
                        if (mode == 4) {
                           wrapper.send(Protocol1_9To1_8.class);
                           wrapper.cancel();
                           entityTracker.sendTeamPacket(true, true);
                           entityTracker.setCurrentTeam("viaversion");
                        } else {
                           entityTracker.sendTeamPacket(false, true);
                           entityTracker.setCurrentTeam(teamNamex);
                        }
                     }
                  }
               }

               if (mode == 1) {
                  EntityTracker1_9 entityTrackerx = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                  String teamName = (String)wrapper.get(Type.STRING, 0);
                  if (entityTrackerx.isAutoTeam() && teamName.equals(entityTrackerx.getCurrentTeam())) {
                     wrapper.send(Protocol1_9To1_8.class);
                     wrapper.cancel();
                     entityTrackerx.sendTeamPacket(true, true);
                     entityTrackerx.setCurrentTeam("viaversion");
                  }
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.JOIN_GAME, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.handler((wrapper) -> {
               int entityId = (Integer)wrapper.get(Type.INT, 0);
               EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
               tracker.addEntity(entityId, EntityTypes1_10.EntityType.PLAYER);
               tracker.setClientEntityId(entityId);
            });
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.BYTE);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.STRING);
            this.map(Type.BOOLEAN);
            this.handler((wrapper) -> {
               EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
               tracker.setGameMode(GameMode.getById((Short)wrapper.get(Type.UNSIGNED_BYTE, 0)));
            });
            this.handler((wrapper) -> {
               ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
               int dimensionId = (Byte)wrapper.get(Type.BYTE, 0);
               clientWorld.setEnvironment(dimensionId);
            });
            this.handler((wrapper) -> {
               CommandBlockProvider provider = (CommandBlockProvider)Via.getManager().getProviders().get(CommandBlockProvider.class);
               provider.sendPermission(wrapper.user());
            });
            this.handler((wrapper) -> {
               EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
               if (Via.getConfig().isAutoTeam()) {
                  entityTracker.setAutoTeam(true);
                  wrapper.send(Protocol1_9To1_8.class);
                  wrapper.cancel();
                  entityTracker.sendTeamPacket(true, true);
                  entityTracker.setCurrentTeam("viaversion");
               } else {
                  entityTracker.setAutoTeam(false);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.PLAYER_INFO, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               int action = (Integer)wrapper.get(Type.VAR_INT, 0);
               int count = (Integer)wrapper.get(Type.VAR_INT, 1);

               for(int i = 0; i < count; ++i) {
                  wrapper.passthrough(Type.UUID);
                  if (action != 0) {
                     if (action != 1 && action != 2) {
                        if (action == 3) {
                           String displayName = (String)wrapper.read(Type.OPTIONAL_STRING);
                           wrapper.write(Type.OPTIONAL_COMPONENT, displayName != null ? (JsonElement)Protocol1_9To1_8.FIX_JSON.transform(wrapper, displayName) : null);
                        } else if (action == 4) {
                        }
                     } else {
                        wrapper.passthrough(Type.VAR_INT);
                     }
                  } else {
                     wrapper.passthrough(Type.STRING);
                     int properties = (Integer)wrapper.passthrough(Type.VAR_INT);

                     for(int j = 0; j < properties; ++j) {
                        wrapper.passthrough(Type.STRING);
                        wrapper.passthrough(Type.STRING);
                        wrapper.passthrough(Type.OPTIONAL_STRING);
                     }

                     wrapper.passthrough(Type.VAR_INT);
                     wrapper.passthrough(Type.VAR_INT);
                     String displayNamex = (String)wrapper.read(Type.OPTIONAL_STRING);
                     wrapper.write(Type.OPTIONAL_COMPONENT, displayNamex != null ? (JsonElement)Protocol1_9To1_8.FIX_JSON.transform(wrapper, displayNamex) : null);
                  }
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.PLUGIN_MESSAGE, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.handler((wrapper) -> {
               String name = (String)wrapper.get(Type.STRING, 0);
               if (name.equalsIgnoreCase("MC|BOpen")) {
                  wrapper.write(Type.VAR_INT, 0);
               } else if (name.equalsIgnoreCase("MC|TrList")) {
                  wrapper.passthrough(Type.INT);
                  Short size = (Short)wrapper.passthrough(Type.UNSIGNED_BYTE);

                  for(int i = 0; i < size; ++i) {
                     Item item1 = (Item)wrapper.passthrough(Type.ITEM1_8);
                     ItemRewriter.toClient(item1);
                     Item item2 = (Item)wrapper.passthrough(Type.ITEM1_8);
                     ItemRewriter.toClient(item2);
                     boolean present = (Boolean)wrapper.passthrough(Type.BOOLEAN);
                     if (present) {
                        Item item3 = (Item)wrapper.passthrough(Type.ITEM1_8);
                        ItemRewriter.toClient(item3);
                     }

                     wrapper.passthrough(Type.BOOLEAN);
                     wrapper.passthrough(Type.INT);
                     wrapper.passthrough(Type.INT);
                  }
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.RESPAWN, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.STRING);
            this.handler((wrapper) -> {
               ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
               int dimensionId = (Integer)wrapper.get(Type.INT, 0);
               clientWorld.setEnvironment(dimensionId);
            });
            this.handler((wrapper) -> {
               ((ClientChunks)wrapper.user().get(ClientChunks.class)).getLoadedChunks().clear();
               int gamemode = (Short)wrapper.get(Type.UNSIGNED_BYTE, 0);
               EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
               tracker.setGameMode(GameMode.getById(gamemode));
            });
            this.handler((wrapper) -> {
               CommandBlockProvider provider = (CommandBlockProvider)Via.getManager().getProviders().get(CommandBlockProvider.class);
               provider.sendPermission(wrapper.user());
               provider.unloadChunks(wrapper.user());
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.GAME_EVENT, new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.FLOAT);
            this.handler((wrapper) -> {
               short reason = (Short)wrapper.get(Type.UNSIGNED_BYTE, 0);
               if (reason == 3) {
                  int gamemode = ((Float)wrapper.get(Type.FLOAT, 0)).intValue();
                  EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                  tracker.setGameMode(GameMode.getById(gamemode));
               } else if (reason == 4) {
                  wrapper.set(Type.FLOAT, 0, 1.0F);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.SET_COMPRESSION, (ClientboundPacketType)null, (wrapper) -> {
         wrapper.cancel();
         CompressionProvider provider = (CompressionProvider)Via.getManager().getProviders().get(CompressionProvider.class);
         provider.handlePlayCompression(wrapper.user(), (Integer)wrapper.read(Type.VAR_INT));
      });
      protocol.registerServerbound(ServerboundPackets1_9.TAB_COMPLETE, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.read(Type.BOOLEAN);
         }
      });
      protocol.registerServerbound(ServerboundPackets1_9.CLIENT_SETTINGS, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.map(Type.BYTE);
            this.map(Type.VAR_INT, Type.BYTE);
            this.map(Type.BOOLEAN);
            this.map(Type.UNSIGNED_BYTE);
            this.handler((wrapper) -> {
               int hand = (Integer)wrapper.read(Type.VAR_INT);
               if (Via.getConfig().isLeftHandedHandling() && hand == 0) {
                  wrapper.set(Type.UNSIGNED_BYTE, 0, (short)(((Short)wrapper.get(Type.UNSIGNED_BYTE, 0)).intValue() | 128));
               }

               wrapper.sendToServer(Protocol1_9To1_8.class);
               wrapper.cancel();
               ((MainHandProvider)Via.getManager().getProviders().get(MainHandProvider.class)).setMainHand(wrapper.user(), hand);
            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_9.ANIMATION, new PacketHandlers() {
         public void register() {
            this.read(Type.VAR_INT);
         }
      });
      protocol.cancelServerbound(ServerboundPackets1_9.TELEPORT_CONFIRM);
      protocol.cancelServerbound(ServerboundPackets1_9.VEHICLE_MOVE);
      protocol.cancelServerbound(ServerboundPackets1_9.STEER_BOAT);
      protocol.registerServerbound(ServerboundPackets1_9.PLUGIN_MESSAGE, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.handler((wrapper) -> {
               String name = (String)wrapper.get(Type.STRING, 0);
               if (name.equalsIgnoreCase("MC|BSign")) {
                  Item item = (Item)wrapper.passthrough(Type.ITEM1_8);
                  if (item != null) {
                     item.setIdentifier(387);
                     ItemRewriter.rewriteBookToServer(item);
                  }
               }

               if (name.equalsIgnoreCase("MC|AutoCmd")) {
                  wrapper.set(Type.STRING, 0, "MC|AdvCdm");
                  wrapper.write(Type.BYTE, (byte)0);
                  wrapper.passthrough(Type.INT);
                  wrapper.passthrough(Type.INT);
                  wrapper.passthrough(Type.INT);
                  wrapper.passthrough(Type.STRING);
                  wrapper.passthrough(Type.BOOLEAN);
                  wrapper.clearInputBuffer();
               }

               if (name.equalsIgnoreCase("MC|AdvCmd")) {
                  wrapper.set(Type.STRING, 0, "MC|AdvCdm");
               }

            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_9.CLIENT_STATUS, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               int action = (Integer)wrapper.get(Type.VAR_INT, 0);
               if (action == 2) {
                  EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                  if (tracker.isBlocking()) {
                     if (!Via.getConfig().isShowShieldWhenSwordInHand()) {
                        tracker.setSecondHand((Item)null);
                     }

                     tracker.setBlocking(false);
                  }
               }

            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_9.PLAYER_POSITION, new PacketHandlers() {
         public void register() {
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.BOOLEAN);
            this.handler(new PlayerMovementMapper());
         }
      });
      protocol.registerServerbound(ServerboundPackets1_9.PLAYER_POSITION_AND_ROTATION, new PacketHandlers() {
         public void register() {
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.BOOLEAN);
            this.handler(new PlayerMovementMapper());
         }
      });
      protocol.registerServerbound(ServerboundPackets1_9.PLAYER_ROTATION, new PacketHandlers() {
         public void register() {
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.BOOLEAN);
            this.handler(new PlayerMovementMapper());
         }
      });
      protocol.registerServerbound(ServerboundPackets1_9.PLAYER_MOVEMENT, new PacketHandlers() {
         public void register() {
            this.map(Type.BOOLEAN);
            this.handler(new PlayerMovementMapper());
         }
      });
   }
}
