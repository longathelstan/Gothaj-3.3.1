package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.packets;

import com.google.common.base.Joiner;
import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.Protocol1_12_2To1_13;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data.ParticleMapping;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.storage.TabCompleteStorage;
import com.viaversion.viabackwards.utils.ChatUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ServerboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.packets.InventoryPackets;
import com.viaversion.viaversion.rewriter.CommandRewriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerPacket1_13 extends RewriterBase<Protocol1_12_2To1_13> {
   private final CommandRewriter<ClientboundPackets1_13> commandRewriter;

   public PlayerPacket1_13(Protocol1_12_2To1_13 protocol) {
      super(protocol);
      this.commandRewriter = new CommandRewriter(this.protocol);
   }

   protected void registerPackets() {
      ((Protocol1_12_2To1_13)this.protocol).registerClientbound(State.LOGIN, 4, -1, new PacketHandlers() {
         public void register() {
            this.handler((packetWrapper) -> {
               packetWrapper.cancel();
               packetWrapper.create(2, new PacketHandler() {
                  public void handle(PacketWrapper newWrapper) throws Exception {
                     newWrapper.write(Type.VAR_INT, (Integer)packetWrapper.read(Type.VAR_INT));
                     newWrapper.write(Type.BOOLEAN, false);
                  }
               }).sendToServer(Protocol1_12_2To1_13.class);
            });
         }
      });
      ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.PLUGIN_MESSAGE, (wrapper) -> {
         String channel = (String)wrapper.read(Type.STRING);
         if (channel.equals("minecraft:trader_list")) {
            wrapper.write(Type.STRING, "MC|TrList");
            wrapper.passthrough(Type.INT);
            int size = (Short)wrapper.passthrough(Type.UNSIGNED_BYTE);

            for(int i = 0; i < size; ++i) {
               Item input = (Item)wrapper.read(Type.ITEM1_13);
               wrapper.write(Type.ITEM1_8, ((Protocol1_12_2To1_13)this.protocol).getItemRewriter().handleItemToClient(input));
               Item output = (Item)wrapper.read(Type.ITEM1_13);
               wrapper.write(Type.ITEM1_8, ((Protocol1_12_2To1_13)this.protocol).getItemRewriter().handleItemToClient(output));
               boolean secondItem = (Boolean)wrapper.passthrough(Type.BOOLEAN);
               if (secondItem) {
                  Item second = (Item)wrapper.read(Type.ITEM1_13);
                  wrapper.write(Type.ITEM1_8, ((Protocol1_12_2To1_13)this.protocol).getItemRewriter().handleItemToClient(second));
               }

               wrapper.passthrough(Type.BOOLEAN);
               wrapper.passthrough(Type.INT);
               wrapper.passthrough(Type.INT);
            }
         } else {
            String oldChannel = InventoryPackets.getOldPluginChannelId(channel);
            if (oldChannel == null) {
               if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                  ViaBackwards.getPlatform().getLogger().warning("Ignoring outgoing plugin message with channel: " + channel);
               }

               wrapper.cancel();
               return;
            }

            wrapper.write(Type.STRING, oldChannel);
            if (oldChannel.equals("REGISTER") || oldChannel.equals("UNREGISTER")) {
               String[] channels = (new String((byte[])wrapper.read(Type.REMAINING_BYTES), StandardCharsets.UTF_8)).split("\u0000");
               List<String> rewrittenChannels = new ArrayList();
               String[] var14 = channels;
               int var15 = channels.length;

               for(int var16 = 0; var16 < var15; ++var16) {
                  String s = var14[var16];
                  String rewritten = InventoryPackets.getOldPluginChannelId(s);
                  if (rewritten != null) {
                     rewrittenChannels.add(rewritten);
                  } else if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                     ViaBackwards.getPlatform().getLogger().warning("Ignoring plugin channel in outgoing REGISTER: " + s);
                  }
               }

               wrapper.write(Type.REMAINING_BYTES, Joiner.on('\u0000').join(rewrittenChannels).getBytes(StandardCharsets.UTF_8));
            }
         }

      });
      ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.SPAWN_PARTICLE, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.BOOLEAN);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.INT);
            this.handler((wrapper) -> {
               ParticleMapping.ParticleData old = ParticleMapping.getMapping((Integer)wrapper.get(Type.INT, 0));
               wrapper.set(Type.INT, 0, old.getHistoryId());
               int[] data = old.rewriteData((Protocol1_12_2To1_13)PlayerPacket1_13.this.protocol, wrapper);
               if (data != null) {
                  if (old.getHandler().isBlockHandler() && data[0] == 0) {
                     wrapper.cancel();
                     return;
                  }

                  int[] var4 = data;
                  int var5 = data.length;

                  for(int var6 = 0; var6 < var5; ++var6) {
                     int i = var4[var6];
                     wrapper.write(Type.VAR_INT, i);
                  }
               }

            });
         }
      });
      ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.PLAYER_INFO, new PacketHandlers() {
         public void register() {
            this.handler((packetWrapper) -> {
               TabCompleteStorage storage = (TabCompleteStorage)packetWrapper.user().get(TabCompleteStorage.class);
               int action = (Integer)packetWrapper.passthrough(Type.VAR_INT);
               int nPlayers = (Integer)packetWrapper.passthrough(Type.VAR_INT);

               for(int i = 0; i < nPlayers; ++i) {
                  UUID uuid = (UUID)packetWrapper.passthrough(Type.UUID);
                  if (action != 0) {
                     if (action == 1) {
                        packetWrapper.passthrough(Type.VAR_INT);
                     } else if (action == 2) {
                        packetWrapper.passthrough(Type.VAR_INT);
                     } else if (action == 3) {
                        packetWrapper.passthrough(Type.OPTIONAL_COMPONENT);
                     } else if (action == 4) {
                        storage.usernames().remove(uuid);
                     }
                  } else {
                     String name = (String)packetWrapper.passthrough(Type.STRING);
                     storage.usernames().put(uuid, name);
                     int nProperties = (Integer)packetWrapper.passthrough(Type.VAR_INT);

                     for(int j = 0; j < nProperties; ++j) {
                        packetWrapper.passthrough(Type.STRING);
                        packetWrapper.passthrough(Type.STRING);
                        packetWrapper.passthrough(Type.OPTIONAL_STRING);
                     }

                     packetWrapper.passthrough(Type.VAR_INT);
                     packetWrapper.passthrough(Type.VAR_INT);
                     packetWrapper.passthrough(Type.OPTIONAL_COMPONENT);
                  }
               }

            });
         }
      });
      ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.SCOREBOARD_OBJECTIVE, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.map(Type.BYTE);
            this.handler((wrapper) -> {
               byte mode = (Byte)wrapper.get(Type.BYTE, 0);
               if (mode == 0 || mode == 2) {
                  JsonElement value = (JsonElement)wrapper.read(Type.COMPONENT);
                  String legacyValue = ((Protocol1_12_2To1_13)PlayerPacket1_13.this.protocol).jsonToLegacy(value);
                  wrapper.write(Type.STRING, ChatUtil.fromLegacy(legacyValue, 'f', 32));
                  int type = (Integer)wrapper.read(Type.VAR_INT);
                  wrapper.write(Type.STRING, type == 1 ? "hearts" : "integer");
               }

            });
         }
      });
      ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.TEAMS, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.map(Type.BYTE);
            this.handler((wrapper) -> {
               byte action = (Byte)wrapper.get(Type.BYTE, 0);
               if (action == 0 || action == 2) {
                  JsonElement displayName = (JsonElement)wrapper.read(Type.COMPONENT);
                  String legacyTextDisplayName = ((Protocol1_12_2To1_13)PlayerPacket1_13.this.protocol).jsonToLegacy(displayName);
                  wrapper.write(Type.STRING, ChatUtil.fromLegacy(legacyTextDisplayName, 'f', 32));
                  byte flags = (Byte)wrapper.read(Type.BYTE);
                  String nameTagVisibility = (String)wrapper.read(Type.STRING);
                  String collisionRule = (String)wrapper.read(Type.STRING);
                  int colour = (Integer)wrapper.read(Type.VAR_INT);
                  if (colour == 21) {
                     colour = -1;
                  }

                  JsonElement prefixComponent = (JsonElement)wrapper.read(Type.COMPONENT);
                  JsonElement suffixComponent = (JsonElement)wrapper.read(Type.COMPONENT);
                  String prefix = ((Protocol1_12_2To1_13)PlayerPacket1_13.this.protocol).jsonToLegacy(prefixComponent);
                  if (ViaBackwards.getConfig().addTeamColorTo1_13Prefix()) {
                     prefix = prefix + "§" + (colour > -1 && colour <= 15 ? Integer.toHexString(colour) : "r");
                  }

                  String suffix = ((Protocol1_12_2To1_13)PlayerPacket1_13.this.protocol).jsonToLegacy(suffixComponent);
                  wrapper.write(Type.STRING, ChatUtil.fromLegacyPrefix(prefix, 'f', 16));
                  wrapper.write(Type.STRING, ChatUtil.fromLegacy(suffix, '\u0000', 16));
                  wrapper.write(Type.BYTE, flags);
                  wrapper.write(Type.STRING, nameTagVisibility);
                  wrapper.write(Type.STRING, collisionRule);
                  wrapper.write(Type.BYTE, (byte)colour);
               }

               if (action == 0 || action == 3 || action == 4) {
                  wrapper.passthrough(Type.STRING_ARRAY);
               }

            });
         }
      });
      ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.DECLARE_COMMANDS, (ClientboundPacketType)null, (wrapper) -> {
         wrapper.cancel();
         TabCompleteStorage storage = (TabCompleteStorage)wrapper.user().get(TabCompleteStorage.class);
         if (!storage.commands().isEmpty()) {
            storage.commands().clear();
         }

         int size = (Integer)wrapper.read(Type.VAR_INT);
         boolean initialNodes = true;

         for(int i = 0; i < size; ++i) {
            byte flags = (Byte)wrapper.read(Type.BYTE);
            wrapper.read(Type.VAR_INT_ARRAY_PRIMITIVE);
            if ((flags & 8) != 0) {
               wrapper.read(Type.VAR_INT);
            }

            byte nodeType = (byte)(flags & 3);
            if (initialNodes && nodeType == 2) {
               initialNodes = false;
            }

            if (nodeType == 1 || nodeType == 2) {
               String name = (String)wrapper.read(Type.STRING);
               if (nodeType == 1 && initialNodes) {
                  storage.commands().add('/' + name);
               }
            }

            if (nodeType == 2) {
               this.commandRewriter.handleArgument(wrapper, (String)wrapper.read(Type.STRING));
            }

            if ((flags & 16) != 0) {
               wrapper.read(Type.STRING);
            }
         }

      });
      ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.TAB_COMPLETE, (wrapper) -> {
         TabCompleteStorage storage = (TabCompleteStorage)wrapper.user().get(TabCompleteStorage.class);
         if (storage.lastRequest() == null) {
            wrapper.cancel();
         } else {
            if (storage.lastId() != (Integer)wrapper.read(Type.VAR_INT)) {
               wrapper.cancel();
            }

            int start = (Integer)wrapper.read(Type.VAR_INT);
            int length = (Integer)wrapper.read(Type.VAR_INT);
            int lastRequestPartIndex = storage.lastRequest().lastIndexOf(32) + 1;
            if (lastRequestPartIndex != start) {
               wrapper.cancel();
            }

            if (length != storage.lastRequest().length() - lastRequestPartIndex) {
               wrapper.cancel();
            }

            int count = (Integer)wrapper.passthrough(Type.VAR_INT);

            for(int i = 0; i < count; ++i) {
               String match = (String)wrapper.read(Type.STRING);
               wrapper.write(Type.STRING, (start == 0 && !storage.isLastAssumeCommand() ? "/" : "") + match);
               wrapper.read(Type.OPTIONAL_COMPONENT);
            }

         }
      });
      ((Protocol1_12_2To1_13)this.protocol).registerServerbound(ServerboundPackets1_12_1.TAB_COMPLETE, (wrapper) -> {
         TabCompleteStorage storage = (TabCompleteStorage)wrapper.user().get(TabCompleteStorage.class);
         List<String> suggestions = new ArrayList();
         String command = (String)wrapper.read(Type.STRING);
         boolean assumeCommand = (Boolean)wrapper.read(Type.BOOLEAN);
         wrapper.read(Type.OPTIONAL_POSITION1_8);
         String valuex;
         Iterator var11;
         if (!assumeCommand && !command.startsWith("/")) {
            String buffer = command.substring(command.lastIndexOf(32) + 1);
            var11 = storage.usernames().values().iterator();

            while(var11.hasNext()) {
               valuex = (String)var11.next();
               if (startsWithIgnoreCase(valuex, buffer)) {
                  suggestions.add(valuex);
               }
            }
         } else if (!storage.commands().isEmpty() && !command.contains(" ")) {
            Iterator var5 = storage.commands().iterator();

            while(var5.hasNext()) {
               String value = (String)var5.next();
               if (startsWithIgnoreCase(value, command)) {
                  suggestions.add(value);
               }
            }
         }

         if (suggestions.isEmpty()) {
            if (!assumeCommand && command.startsWith("/")) {
               command = command.substring(1);
            }

            int id = ThreadLocalRandom.current().nextInt();
            wrapper.write(Type.VAR_INT, id);
            wrapper.write(Type.STRING, command);
            storage.setLastId(id);
            storage.setLastAssumeCommand(assumeCommand);
            storage.setLastRequest(command);
         } else {
            wrapper.cancel();
            PacketWrapper response = wrapper.create(ClientboundPackets1_12_1.TAB_COMPLETE);
            response.write(Type.VAR_INT, suggestions.size());
            var11 = suggestions.iterator();

            while(var11.hasNext()) {
               valuex = (String)var11.next();
               response.write(Type.STRING, valuex);
            }

            response.scheduleSend(Protocol1_12_2To1_13.class);
            storage.setLastRequest((String)null);
         }
      });
      ((Protocol1_12_2To1_13)this.protocol).registerServerbound(ServerboundPackets1_12_1.PLUGIN_MESSAGE, (wrapper) -> {
         String channel = (String)wrapper.read(Type.STRING);
         byte var4 = -1;
         switch(channel.hashCode()) {
         case -751882236:
            if (channel.equals("MC|ItemName")) {
               var4 = 2;
            }
            break;
         case -708575099:
            if (channel.equals("MC|AutoCmd")) {
               var4 = 4;
            }
            break;
         case -592727859:
            if (channel.equals("MC|AdvCmd")) {
               var4 = 3;
            }
            break;
         case -563769974:
            if (channel.equals("MC|Beacon")) {
               var4 = 6;
            }
            break;
         case -296231034:
            if (channel.equals("MC|BEdit")) {
               var4 = 1;
            }
            break;
         case -295809223:
            if (channel.equals("MC|BSign")) {
               var4 = 0;
            }
            break;
         case -278283530:
            if (channel.equals("MC|TrSel")) {
               var4 = 7;
            }
            break;
         case -62698213:
            if (channel.equals("MC|Struct")) {
               var4 = 5;
            }
            break;
         case 1626013338:
            if (channel.equals("MC|PickItem")) {
               var4 = 8;
            }
         }

         int x;
         int y;
         int z;
         int modeIdx;
         String rewritten;
         int modeId;
         switch(var4) {
         case 0:
         case 1:
            wrapper.setPacketType(ServerboundPackets1_13.EDIT_BOOK);
            Item book = (Item)wrapper.read(Type.ITEM1_8);
            wrapper.write(Type.ITEM1_13, ((Protocol1_12_2To1_13)this.protocol).getItemRewriter().handleItemToServer(book));
            boolean signing = channel.equals("MC|BSign");
            wrapper.write(Type.BOOLEAN, signing);
            break;
         case 2:
            wrapper.setPacketType(ServerboundPackets1_13.RENAME_ITEM);
            break;
         case 3:
            byte type = (Byte)wrapper.read(Type.BYTE);
            if (type == 0) {
               wrapper.setPacketType(ServerboundPackets1_13.UPDATE_COMMAND_BLOCK);
               wrapper.cancel();
               ViaBackwards.getPlatform().getLogger().warning("Client send MC|AdvCmd custom payload to update command block, weird!");
            } else if (type == 1) {
               wrapper.setPacketType(ServerboundPackets1_13.UPDATE_COMMAND_BLOCK_MINECART);
               wrapper.write(Type.VAR_INT, (Integer)wrapper.read(Type.INT));
               wrapper.passthrough(Type.STRING);
               wrapper.passthrough(Type.BOOLEAN);
            } else {
               wrapper.cancel();
            }
            break;
         case 4:
            wrapper.setPacketType(ServerboundPackets1_13.UPDATE_COMMAND_BLOCK);
            x = (Integer)wrapper.read(Type.INT);
            y = (Integer)wrapper.read(Type.INT);
            z = (Integer)wrapper.read(Type.INT);
            wrapper.write(Type.POSITION1_8, new Position(x, (short)y, z));
            wrapper.passthrough(Type.STRING);
            byte flagsx = 0;
            if ((Boolean)wrapper.read(Type.BOOLEAN)) {
               flagsx = (byte)(flagsx | 1);
            }

            String mode = (String)wrapper.read(Type.STRING);
            modeId = mode.equals("SEQUENCE") ? 0 : (mode.equals("AUTO") ? 1 : 2);
            wrapper.write(Type.VAR_INT, modeId);
            if ((Boolean)wrapper.read(Type.BOOLEAN)) {
               flagsx = (byte)(flagsx | 2);
            }

            if ((Boolean)wrapper.read(Type.BOOLEAN)) {
               flagsx = (byte)(flagsx | 4);
            }

            wrapper.write(Type.BYTE, flagsx);
            break;
         case 5:
            wrapper.setPacketType(ServerboundPackets1_13.UPDATE_STRUCTURE_BLOCK);
            x = (Integer)wrapper.read(Type.INT);
            y = (Integer)wrapper.read(Type.INT);
            z = (Integer)wrapper.read(Type.INT);
            wrapper.write(Type.POSITION1_8, new Position(x, (short)y, z));
            wrapper.write(Type.VAR_INT, (Byte)wrapper.read(Type.BYTE) - 1);
            String modex = (String)wrapper.read(Type.STRING);
            modeIdx = modex.equals("SAVE") ? 0 : (modex.equals("LOAD") ? 1 : (modex.equals("CORNER") ? 2 : 3));
            wrapper.write(Type.VAR_INT, modeIdx);
            wrapper.passthrough(Type.STRING);
            wrapper.write(Type.BYTE, ((Integer)wrapper.read(Type.INT)).byteValue());
            wrapper.write(Type.BYTE, ((Integer)wrapper.read(Type.INT)).byteValue());
            wrapper.write(Type.BYTE, ((Integer)wrapper.read(Type.INT)).byteValue());
            wrapper.write(Type.BYTE, ((Integer)wrapper.read(Type.INT)).byteValue());
            wrapper.write(Type.BYTE, ((Integer)wrapper.read(Type.INT)).byteValue());
            wrapper.write(Type.BYTE, ((Integer)wrapper.read(Type.INT)).byteValue());
            String mirror = (String)wrapper.read(Type.STRING);
            boolean var10000;
            if (modex.equals("NONE")) {
               var10000 = false;
            } else {
               var10000 = modex.equals("LEFT_RIGHT") ? true : true;
            }

            rewritten = (String)wrapper.read(Type.STRING);
            if (modex.equals("NONE")) {
               var10000 = false;
            } else if (modex.equals("CLOCKWISE_90")) {
               var10000 = true;
            } else {
               var10000 = modex.equals("CLOCKWISE_180") ? true : true;
            }

            wrapper.passthrough(Type.STRING);
            byte flags = 0;
            if ((Boolean)wrapper.read(Type.BOOLEAN)) {
               flags = (byte)(flags | 1);
            }

            if ((Boolean)wrapper.read(Type.BOOLEAN)) {
               flags = (byte)(flags | 2);
            }

            if ((Boolean)wrapper.read(Type.BOOLEAN)) {
               flags = (byte)(flags | 4);
            }

            wrapper.passthrough(Type.FLOAT);
            wrapper.passthrough(Type.VAR_LONG);
            wrapper.write(Type.BYTE, flags);
            break;
         case 6:
            wrapper.setPacketType(ServerboundPackets1_13.SET_BEACON_EFFECT);
            wrapper.write(Type.VAR_INT, (Integer)wrapper.read(Type.INT));
            wrapper.write(Type.VAR_INT, (Integer)wrapper.read(Type.INT));
            break;
         case 7:
            wrapper.setPacketType(ServerboundPackets1_13.SELECT_TRADE);
            wrapper.write(Type.VAR_INT, (Integer)wrapper.read(Type.INT));
            break;
         case 8:
            wrapper.setPacketType(ServerboundPackets1_13.PICK_ITEM);
            break;
         default:
            String newChannel = InventoryPackets.getNewPluginChannelId(channel);
            if (newChannel == null) {
               if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                  ViaBackwards.getPlatform().getLogger().warning("Ignoring incoming plugin message with channel: " + channel);
               }

               wrapper.cancel();
               return;
            }

            wrapper.write(Type.STRING, newChannel);
            if (newChannel.equals("minecraft:register") || newChannel.equals("minecraft:unregister")) {
               String[] channels = (new String((byte[])wrapper.read(Type.REMAINING_BYTES), StandardCharsets.UTF_8)).split("\u0000");
               List<String> rewrittenChannels = new ArrayList();
               String[] var24 = channels;
               modeIdx = channels.length;

               for(modeId = 0; modeId < modeIdx; ++modeId) {
                  String s = var24[modeId];
                  rewritten = InventoryPackets.getNewPluginChannelId(s);
                  if (rewritten != null) {
                     rewrittenChannels.add(rewritten);
                  } else if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                     ViaBackwards.getPlatform().getLogger().warning("Ignoring plugin channel in incoming REGISTER: " + s);
                  }
               }

               if (rewrittenChannels.isEmpty()) {
                  wrapper.cancel();
                  return;
               }

               wrapper.write(Type.REMAINING_BYTES, Joiner.on('\u0000').join(rewrittenChannels).getBytes(StandardCharsets.UTF_8));
            }
         }

      });
      ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.STATISTICS, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               int size = (Integer)wrapper.get(Type.VAR_INT, 0);
               int newSize = size;

               for(int i = 0; i < size; ++i) {
                  int categoryId = (Integer)wrapper.read(Type.VAR_INT);
                  int statisticId = (Integer)wrapper.read(Type.VAR_INT);
                  String name = "";
                  switch(categoryId) {
                  case 0:
                  case 1:
                  case 2:
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                  case 7:
                     wrapper.read(Type.VAR_INT);
                     --newSize;
                     break;
                  case 8:
                     name = (String)((Protocol1_12_2To1_13)PlayerPacket1_13.this.protocol).getMappingData().getStatisticMappings().get(statisticId);
                     if (name == null) {
                        wrapper.read(Type.VAR_INT);
                        --newSize;
                        break;
                     }
                  default:
                     wrapper.write(Type.STRING, name);
                     wrapper.passthrough(Type.VAR_INT);
                  }
               }

               if (newSize != size) {
                  wrapper.set(Type.VAR_INT, 0, newSize);
               }

            });
         }
      });
   }

   private static boolean startsWithIgnoreCase(String string, String prefix) {
      return string.length() < prefix.length() ? false : string.regionMatches(true, 0, prefix, 0, prefix.length());
   }
}
