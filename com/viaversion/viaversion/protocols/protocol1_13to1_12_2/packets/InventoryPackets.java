package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.packets;

import com.google.common.base.Joiner;
import com.google.common.primitives.Ints;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.BlockIdData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.MappingData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.SoundSource;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.SpawnEggRewriter;
import com.viaversion.viaversion.rewriter.ItemRewriter;
import com.viaversion.viaversion.util.ComponentUtil;
import com.viaversion.viaversion.util.Key;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class InventoryPackets extends ItemRewriter<ClientboundPackets1_12_1, ServerboundPackets1_13, Protocol1_13To1_12_2> {
   private static final String NBT_TAG_NAME = "ViaVersion|" + Protocol1_13To1_12_2.class.getSimpleName();

   public InventoryPackets(Protocol1_13To1_12_2 protocol) {
      super(protocol, (Type)null, (Type)null);
   }

   public void registerPackets() {
      ((Protocol1_13To1_12_2)this.protocol).registerClientbound(ClientboundPackets1_12_1.SET_SLOT, new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.SHORT);
            this.map(Type.ITEM1_8, Type.ITEM1_13);
            this.handler(InventoryPackets.this.itemToClientHandler(Type.ITEM1_13));
         }
      });
      ((Protocol1_13To1_12_2)this.protocol).registerClientbound(ClientboundPackets1_12_1.WINDOW_ITEMS, new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.ITEM1_8_SHORT_ARRAY, Type.ITEM1_13_SHORT_ARRAY);
            this.handler(InventoryPackets.this.itemArrayToClientHandler(Type.ITEM1_13_SHORT_ARRAY));
         }
      });
      ((Protocol1_13To1_12_2)this.protocol).registerClientbound(ClientboundPackets1_12_1.WINDOW_PROPERTY, new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.SHORT);
            this.map(Type.SHORT);
            this.handler((wrapper) -> {
               short property = (Short)wrapper.get(Type.SHORT, 0);
               if (property >= 4 && property <= 6) {
                  wrapper.set(Type.SHORT, 1, (short)((Protocol1_13To1_12_2)InventoryPackets.this.protocol).getMappingData().getEnchantmentMappings().getNewId((Short)wrapper.get(Type.SHORT, 1)));
               }

            });
         }
      });
      ((Protocol1_13To1_12_2)this.protocol).registerClientbound(ClientboundPackets1_12_1.PLUGIN_MESSAGE, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.handler((wrapper) -> {
               String channel = (String)wrapper.get(Type.STRING, 0);
               String old;
               if (channel.equalsIgnoreCase("MC|StopSound")) {
                  old = (String)wrapper.read(Type.STRING);
                  String originalSound = (String)wrapper.read(Type.STRING);
                  wrapper.clearPacket();
                  wrapper.setPacketType(ClientboundPackets1_13.STOP_SOUND);
                  byte flags = 0;
                  wrapper.write(Type.BYTE, flags);
                  if (!old.isEmpty()) {
                     flags = (byte)(flags | 1);
                     Optional<SoundSource> finalSource = SoundSource.findBySource(old);
                     if (!finalSource.isPresent()) {
                        if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                           Via.getPlatform().getLogger().info("Could not handle unknown sound source " + old + " falling back to default: master");
                        }

                        finalSource = Optional.of(SoundSource.MASTER);
                     }

                     wrapper.write(Type.VAR_INT, ((SoundSource)finalSource.get()).getId());
                  }

                  if (!originalSound.isEmpty()) {
                     flags = (byte)(flags | 2);
                     wrapper.write(Type.STRING, originalSound);
                  }

                  wrapper.set(Type.BYTE, 0, flags);
               } else {
                  if (channel.equalsIgnoreCase("MC|TrList")) {
                     channel = "minecraft:trader_list";
                     wrapper.passthrough(Type.INT);
                     int size = (Short)wrapper.passthrough(Type.UNSIGNED_BYTE);

                     for(int i = 0; i < size; ++i) {
                        Item input = (Item)wrapper.read(Type.ITEM1_8);
                        InventoryPackets.this.handleItemToClient(input);
                        wrapper.write(Type.ITEM1_13, input);
                        Item output = (Item)wrapper.read(Type.ITEM1_8);
                        InventoryPackets.this.handleItemToClient(output);
                        wrapper.write(Type.ITEM1_13, output);
                        boolean secondItem = (Boolean)wrapper.passthrough(Type.BOOLEAN);
                        if (secondItem) {
                           Item second = (Item)wrapper.read(Type.ITEM1_8);
                           InventoryPackets.this.handleItemToClient(second);
                           wrapper.write(Type.ITEM1_13, second);
                        }

                        wrapper.passthrough(Type.BOOLEAN);
                        wrapper.passthrough(Type.INT);
                        wrapper.passthrough(Type.INT);
                     }
                  } else {
                     old = channel;
                     channel = InventoryPackets.getNewPluginChannelId(channel);
                     if (channel == null) {
                        if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                           Via.getPlatform().getLogger().warning("Ignoring outgoing plugin message with channel: " + old);
                        }

                        wrapper.cancel();
                        return;
                     }

                     if (channel.equals("minecraft:register") || channel.equals("minecraft:unregister")) {
                        String[] channels = (new String((byte[])wrapper.read(Type.REMAINING_BYTES), StandardCharsets.UTF_8)).split("\u0000");
                        List<String> rewrittenChannels = new ArrayList();
                        String[] var16 = channels;
                        int var18 = channels.length;

                        for(int var19 = 0; var19 < var18; ++var19) {
                           String s = var16[var19];
                           String rewritten = InventoryPackets.getNewPluginChannelId(s);
                           if (rewritten != null) {
                              rewrittenChannels.add(rewritten);
                           } else if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                              Via.getPlatform().getLogger().warning("Ignoring plugin channel in outgoing REGISTER: " + s);
                           }
                        }

                        if (rewrittenChannels.isEmpty()) {
                           wrapper.cancel();
                           return;
                        }

                        wrapper.write(Type.REMAINING_BYTES, Joiner.on('\u0000').join(rewrittenChannels).getBytes(StandardCharsets.UTF_8));
                     }
                  }

                  wrapper.set(Type.STRING, 0, channel);
               }
            });
         }
      });
      ((Protocol1_13To1_12_2)this.protocol).registerClientbound(ClientboundPackets1_12_1.ENTITY_EQUIPMENT, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.VAR_INT);
            this.map(Type.ITEM1_8, Type.ITEM1_13);
            this.handler(InventoryPackets.this.itemToClientHandler(Type.ITEM1_13));
         }
      });
      ((Protocol1_13To1_12_2)this.protocol).registerServerbound(ServerboundPackets1_13.CLICK_WINDOW, new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.SHORT);
            this.map(Type.BYTE);
            this.map(Type.SHORT);
            this.map(Type.VAR_INT);
            this.map(Type.ITEM1_13, Type.ITEM1_8);
            this.handler(InventoryPackets.this.itemToServerHandler(Type.ITEM1_8));
         }
      });
      ((Protocol1_13To1_12_2)this.protocol).registerServerbound(ServerboundPackets1_13.PLUGIN_MESSAGE, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.handler((wrapper) -> {
               String channel = (String)wrapper.get(Type.STRING, 0);
               String old = channel;
               channel = InventoryPackets.getOldPluginChannelId(channel);
               if (channel == null) {
                  if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                     Via.getPlatform().getLogger().warning("Ignoring incoming plugin message with channel: " + old);
                  }

                  wrapper.cancel();
               } else {
                  if (channel.equals("REGISTER") || channel.equals("UNREGISTER")) {
                     String[] channels = (new String((byte[])wrapper.read(Type.REMAINING_BYTES), StandardCharsets.UTF_8)).split("\u0000");
                     List<String> rewrittenChannels = new ArrayList();
                     String[] var5 = channels;
                     int var6 = channels.length;

                     for(int var7 = 0; var7 < var6; ++var7) {
                        String s = var5[var7];
                        String rewritten = InventoryPackets.getOldPluginChannelId(s);
                        if (rewritten != null) {
                           rewrittenChannels.add(rewritten);
                        } else if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                           Via.getPlatform().getLogger().warning("Ignoring plugin channel in incoming REGISTER: " + s);
                        }
                     }

                     wrapper.write(Type.REMAINING_BYTES, Joiner.on('\u0000').join(rewrittenChannels).getBytes(StandardCharsets.UTF_8));
                  }

                  wrapper.set(Type.STRING, 0, channel);
               }
            });
         }
      });
      ((Protocol1_13To1_12_2)this.protocol).registerServerbound(ServerboundPackets1_13.CREATIVE_INVENTORY_ACTION, new PacketHandlers() {
         public void register() {
            this.map(Type.SHORT);
            this.map(Type.ITEM1_13, Type.ITEM1_8);
            this.handler(InventoryPackets.this.itemToServerHandler(Type.ITEM1_8));
         }
      });
   }

   public Item handleItemToClient(Item item) {
      if (item == null) {
         return null;
      } else {
         CompoundTag tag = item.tag();
         int originalId = item.identifier() << 16 | item.data() & '\uffff';
         int rawId = item.identifier() << 4 | item.data() & 15;
         if (isDamageable(item.identifier())) {
            if (tag == null) {
               item.setTag(tag = new CompoundTag());
            }

            tag.put("Damage", new IntTag(item.data()));
         }

         if (item.identifier() == 358) {
            if (tag == null) {
               item.setTag(tag = new CompoundTag());
            }

            tag.put("map", new IntTag(item.data()));
         }

         if (tag != null) {
            boolean banner = item.identifier() == 425;
            CompoundTag entityTag;
            Tag oldTag;
            if ((banner || item.identifier() == 442) && tag.get("BlockEntityTag") instanceof CompoundTag) {
               entityTag = (CompoundTag)tag.get("BlockEntityTag");
               if (entityTag.get("Base") instanceof IntTag) {
                  IntTag base = (IntTag)entityTag.get("Base");
                  if (banner) {
                     rawId = 6800 + base.asInt();
                  }

                  base.setValue(15 - base.asInt());
               }

               if (entityTag.get("Patterns") instanceof ListTag) {
                  Iterator var19 = ((ListTag)entityTag.get("Patterns")).iterator();

                  while(var19.hasNext()) {
                     Tag pattern = (Tag)var19.next();
                     if (pattern instanceof CompoundTag) {
                        oldTag = ((CompoundTag)pattern).get("Color");
                        if (oldTag instanceof NumberTag) {
                           ((CompoundTag)pattern).put("Color", new IntTag(15 - ((NumberTag)oldTag).asInt()));
                        }
                     }
                  }
               }
            }

            StringTag identifier;
            if (tag.get("display") instanceof CompoundTag) {
               entityTag = (CompoundTag)tag.get("display");
               if (entityTag.get("Name") instanceof StringTag) {
                  identifier = (StringTag)entityTag.get("Name");
                  entityTag.put(NBT_TAG_NAME + "|Name", new StringTag(identifier.getValue()));
                  identifier.setValue(ComponentUtil.legacyToJsonString(identifier.getValue(), true));
               }
            }

            ListTag old;
            ListTag newCanDestroy;
            Iterator var22;
            if (tag.get("ench") instanceof ListTag) {
               old = (ListTag)tag.get("ench");
               newCanDestroy = new ListTag(CompoundTag.class);
               var22 = old.iterator();

               while(var22.hasNext()) {
                  oldTag = (Tag)var22.next();
                  NumberTag idTag;
                  if (oldTag instanceof CompoundTag && (idTag = (NumberTag)((CompoundTag)oldTag).get("id")) != null) {
                     CompoundTag enchantmentEntry = new CompoundTag();
                     short oldId = idTag.asShort();
                     String newId = (String)Protocol1_13To1_12_2.MAPPINGS.getOldEnchantmentsIds().get(oldId);
                     if (newId == null) {
                        newId = "viaversion:legacy/" + oldId;
                     }

                     enchantmentEntry.put("id", new StringTag(newId));
                     enchantmentEntry.put("lvl", new ShortTag(((NumberTag)((CompoundTag)oldTag).get("lvl")).asShort()));
                     newCanDestroy.add(enchantmentEntry);
                  }
               }

               tag.remove("ench");
               tag.put("Enchantments", newCanDestroy);
            }

            String numberConverted;
            if (tag.get("StoredEnchantments") instanceof ListTag) {
               old = (ListTag)tag.get("StoredEnchantments");
               newCanDestroy = new ListTag(CompoundTag.class);
               var22 = old.iterator();

               while(var22.hasNext()) {
                  oldTag = (Tag)var22.next();
                  if (oldTag instanceof CompoundTag) {
                     CompoundTag enchantmentEntry = new CompoundTag();
                     short oldId = ((NumberTag)((CompoundTag)oldTag).get("id")).asShort();
                     numberConverted = (String)Protocol1_13To1_12_2.MAPPINGS.getOldEnchantmentsIds().get(oldId);
                     if (numberConverted == null) {
                        numberConverted = "viaversion:legacy/" + oldId;
                     }

                     enchantmentEntry.put("id", new StringTag(numberConverted));
                     enchantmentEntry.put("lvl", new ShortTag(((NumberTag)((CompoundTag)oldTag).get("lvl")).asShort()));
                     newCanDestroy.add(enchantmentEntry);
                  }
               }

               tag.remove("StoredEnchantments");
               tag.put("StoredEnchantments", newCanDestroy);
            }

            String[] var14;
            int var15;
            int var16;
            String newValue;
            Object value;
            String oldId;
            String[] newValues;
            if (tag.get("CanPlaceOn") instanceof ListTag) {
               old = (ListTag)tag.get("CanPlaceOn");
               newCanDestroy = new ListTag(StringTag.class);
               tag.put(NBT_TAG_NAME + "|CanPlaceOn", old.copy());
               var22 = old.iterator();

               while(var22.hasNext()) {
                  oldTag = (Tag)var22.next();
                  value = oldTag.getValue();
                  oldId = Key.stripMinecraftNamespace(value.toString());
                  numberConverted = (String)BlockIdData.numberIdToString.get(Ints.tryParse(oldId));
                  if (numberConverted != null) {
                     oldId = numberConverted;
                  }

                  newValues = (String[])BlockIdData.blockIdMapping.get(oldId.toLowerCase(Locale.ROOT));
                  if (newValues != null) {
                     var14 = newValues;
                     var15 = newValues.length;

                     for(var16 = 0; var16 < var15; ++var16) {
                        newValue = var14[var16];
                        newCanDestroy.add(new StringTag(newValue));
                     }
                  } else {
                     newCanDestroy.add(new StringTag(oldId.toLowerCase(Locale.ROOT)));
                  }
               }

               tag.put("CanPlaceOn", newCanDestroy);
            }

            if (tag.get("CanDestroy") instanceof ListTag) {
               old = (ListTag)tag.get("CanDestroy");
               newCanDestroy = new ListTag(StringTag.class);
               tag.put(NBT_TAG_NAME + "|CanDestroy", old.copy());
               var22 = old.iterator();

               while(var22.hasNext()) {
                  oldTag = (Tag)var22.next();
                  value = oldTag.getValue();
                  oldId = Key.stripMinecraftNamespace(value.toString());
                  numberConverted = (String)BlockIdData.numberIdToString.get(Ints.tryParse(oldId));
                  if (numberConverted != null) {
                     oldId = numberConverted;
                  }

                  newValues = (String[])BlockIdData.blockIdMapping.get(oldId.toLowerCase(Locale.ROOT));
                  if (newValues != null) {
                     var14 = newValues;
                     var15 = newValues.length;

                     for(var16 = 0; var16 < var15; ++var16) {
                        newValue = var14[var16];
                        newCanDestroy.add(new StringTag(newValue));
                     }
                  } else {
                     newCanDestroy.add(new StringTag(oldId.toLowerCase(Locale.ROOT)));
                  }
               }

               tag.put("CanDestroy", newCanDestroy);
            }

            if (item.identifier() == 383) {
               if (tag.get("EntityTag") instanceof CompoundTag) {
                  entityTag = (CompoundTag)tag.get("EntityTag");
                  if (entityTag.get("id") instanceof StringTag) {
                     identifier = (StringTag)entityTag.get("id");
                     rawId = SpawnEggRewriter.getSpawnEggId(identifier.getValue());
                     if (rawId == -1) {
                        rawId = 25100288;
                     } else {
                        entityTag.remove("id");
                        if (entityTag.isEmpty()) {
                           tag.remove("EntityTag");
                        }
                     }
                  } else {
                     rawId = 25100288;
                  }
               } else {
                  rawId = 25100288;
               }
            }

            if (tag.isEmpty()) {
               tag = null;
               item.setTag((CompoundTag)null);
            }
         }

         if (Protocol1_13To1_12_2.MAPPINGS.getItemMappings().getNewId(rawId) == -1) {
            if (!isDamageable(item.identifier()) && item.identifier() != 358) {
               if (tag == null) {
                  item.setTag(tag = new CompoundTag());
               }

               tag.put(NBT_TAG_NAME, new IntTag(originalId));
            }

            if (item.identifier() == 31 && item.data() == 0) {
               rawId = 512;
            } else if (Protocol1_13To1_12_2.MAPPINGS.getItemMappings().getNewId(rawId & -16) != -1) {
               rawId &= -16;
            } else {
               if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                  Via.getPlatform().getLogger().warning("Failed to get 1.13 item for " + item.identifier());
               }

               rawId = 16;
            }
         }

         item.setIdentifier(Protocol1_13To1_12_2.MAPPINGS.getItemMappings().getNewId(rawId));
         item.setData((short)0);
         return item;
      }
   }

   public static String getNewPluginChannelId(String old) {
      byte var2 = -1;
      switch(old.hashCode()) {
      case -295921722:
         if (old.equals("MC|BOpen")) {
            var2 = 2;
         }
         break;
      case -294893183:
         if (old.equals("MC|Brand")) {
            var2 = 1;
         }
         break;
      case -234943831:
         if (old.equals("bungeecord:main")) {
            var2 = 8;
         }
         break;
      case -37059198:
         if (old.equals("MC|TrList")) {
            var2 = 0;
         }
         break;
      case 92413603:
         if (old.equals("REGISTER")) {
            var2 = 5;
         }
         break;
      case 125533714:
         if (old.equals("MC|DebugPath")) {
            var2 = 3;
         }
         break;
      case 1321107516:
         if (old.equals("UNREGISTER")) {
            var2 = 6;
         }
         break;
      case 1537336522:
         if (old.equals("BungeeCord")) {
            var2 = 7;
         }
         break;
      case 2076087261:
         if (old.equals("MC|DebugNeighborsUpdate")) {
            var2 = 4;
         }
      }

      switch(var2) {
      case 0:
         return "minecraft:trader_list";
      case 1:
         return "minecraft:brand";
      case 2:
         return "minecraft:book_open";
      case 3:
         return "minecraft:debug/paths";
      case 4:
         return "minecraft:debug/neighbors_update";
      case 5:
         return "minecraft:register";
      case 6:
         return "minecraft:unregister";
      case 7:
         return "bungeecord:main";
      case 8:
         return null;
      default:
         String mappedChannel = (String)Protocol1_13To1_12_2.MAPPINGS.getChannelMappings().get(old);
         return mappedChannel != null ? mappedChannel : MappingData.validateNewChannel(old);
      }
   }

   public Item handleItemToServer(Item item) {
      if (item == null) {
         return null;
      } else {
         Integer rawId = null;
         boolean gotRawIdFromTag = false;
         CompoundTag tag = item.tag();
         if (tag != null && tag.get(NBT_TAG_NAME) instanceof IntTag) {
            rawId = ((NumberTag)tag.get(NBT_TAG_NAME)).asInt();
            tag.remove(NBT_TAG_NAME);
            gotRawIdFromTag = true;
         }

         if (rawId == null) {
            int oldId = Protocol1_13To1_12_2.MAPPINGS.getItemMappings().inverse().getNewId(item.identifier());
            if (oldId != -1) {
               Optional<String> eggEntityId = SpawnEggRewriter.getEntityId(oldId);
               if (eggEntityId.isPresent()) {
                  rawId = 25100288;
                  if (tag == null) {
                     item.setTag(tag = new CompoundTag());
                  }

                  if (!tag.contains("EntityTag")) {
                     CompoundTag entityTag = new CompoundTag();
                     entityTag.put("id", new StringTag((String)eggEntityId.get()));
                     tag.put("EntityTag", entityTag);
                  }
               } else {
                  rawId = oldId >> 4 << 16 | oldId & 15;
               }
            }
         }

         if (rawId == null) {
            if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
               Via.getPlatform().getLogger().warning("Failed to get 1.12 item for " + item.identifier());
            }

            rawId = 65536;
         }

         item.setIdentifier((short)(rawId >> 16));
         item.setData((short)(rawId & '\uffff'));
         if (tag != null) {
            if (isDamageable(item.identifier()) && tag.get("Damage") instanceof IntTag) {
               if (!gotRawIdFromTag) {
                  item.setData((short)(Integer)tag.get("Damage").getValue());
               }

               tag.remove("Damage");
            }

            if (item.identifier() == 358 && tag.get("map") instanceof IntTag) {
               if (!gotRawIdFromTag) {
                  item.setData((short)(Integer)tag.get("map").getValue());
               }

               tag.remove("map");
            }

            CompoundTag display;
            if ((item.identifier() == 442 || item.identifier() == 425) && tag.get("BlockEntityTag") instanceof CompoundTag) {
               display = (CompoundTag)tag.get("BlockEntityTag");
               if (display.get("Base") instanceof IntTag) {
                  IntTag base = (IntTag)display.get("Base");
                  base.setValue(15 - base.asInt());
               }

               if (display.get("Patterns") instanceof ListTag) {
                  Iterator var18 = ((ListTag)display.get("Patterns")).iterator();

                  while(var18.hasNext()) {
                     Tag pattern = (Tag)var18.next();
                     if (pattern instanceof CompoundTag) {
                        IntTag c = (IntTag)((CompoundTag)pattern).get("Color");
                        c.setValue(15 - c.asInt());
                     }
                  }
               }
            }

            if (tag.get("display") instanceof CompoundTag) {
               display = (CompoundTag)tag.get("display");
               if (display.get("Name") instanceof StringTag) {
                  StringTag name = (StringTag)display.get("Name");
                  StringTag via = (StringTag)display.remove(NBT_TAG_NAME + "|Name");
                  name.setValue(via != null ? via.getValue() : ComponentUtil.jsonToLegacy(name.getValue()));
               }
            }

            CompoundTag enchEntry;
            String newId;
            Short oldId;
            ListTag old;
            ListTag newCanDestroy;
            Iterator var23;
            Tag oldTag;
            if (tag.get("Enchantments") instanceof ListTag) {
               old = (ListTag)tag.get("Enchantments");
               newCanDestroy = new ListTag(CompoundTag.class);
               var23 = old.iterator();

               while(var23.hasNext()) {
                  oldTag = (Tag)var23.next();
                  if (oldTag instanceof CompoundTag) {
                     enchEntry = new CompoundTag();
                     newId = (String)((CompoundTag)oldTag).get("id").getValue();
                     oldId = (Short)Protocol1_13To1_12_2.MAPPINGS.getOldEnchantmentsIds().inverse().get(newId);
                     if (oldId == null && newId.startsWith("viaversion:legacy/")) {
                        oldId = Short.valueOf(newId.substring(18));
                     }

                     if (oldId != null) {
                        enchEntry.put("id", new ShortTag(oldId));
                        enchEntry.put("lvl", new ShortTag(((NumberTag)((CompoundTag)oldTag).get("lvl")).asShort()));
                        newCanDestroy.add(enchEntry);
                     }
                  }
               }

               tag.remove("Enchantments");
               tag.put("ench", newCanDestroy);
            }

            if (tag.get("StoredEnchantments") instanceof ListTag) {
               old = (ListTag)tag.get("StoredEnchantments");
               newCanDestroy = new ListTag(CompoundTag.class);
               var23 = old.iterator();

               while(var23.hasNext()) {
                  oldTag = (Tag)var23.next();
                  if (oldTag instanceof CompoundTag) {
                     enchEntry = new CompoundTag();
                     newId = (String)((CompoundTag)oldTag).get("id").getValue();
                     oldId = (Short)Protocol1_13To1_12_2.MAPPINGS.getOldEnchantmentsIds().inverse().get(newId);
                     if (oldId == null && newId.startsWith("viaversion:legacy/")) {
                        oldId = Short.valueOf(newId.substring(18));
                     }

                     if (oldId != null) {
                        enchEntry.put("id", new ShortTag(oldId));
                        enchEntry.put("lvl", new ShortTag(((NumberTag)((CompoundTag)oldTag).get("lvl")).asShort()));
                        newCanDestroy.add(enchEntry);
                     }
                  }
               }

               tag.remove("StoredEnchantments");
               tag.put("StoredEnchantments", newCanDestroy);
            }

            int var12;
            int var13;
            String newValue;
            Object value;
            String[] newValues;
            String[] var27;
            if (tag.get(NBT_TAG_NAME + "|CanPlaceOn") instanceof ListTag) {
               tag.put("CanPlaceOn", tag.remove(NBT_TAG_NAME + "|CanPlaceOn"));
            } else if (tag.get("CanPlaceOn") instanceof ListTag) {
               old = (ListTag)tag.get("CanPlaceOn");
               newCanDestroy = new ListTag(StringTag.class);
               var23 = old.iterator();

               while(var23.hasNext()) {
                  oldTag = (Tag)var23.next();
                  value = oldTag.getValue();
                  newValues = (String[])BlockIdData.fallbackReverseMapping.get(value instanceof String ? Key.stripMinecraftNamespace((String)value) : null);
                  if (newValues != null) {
                     var27 = newValues;
                     var12 = newValues.length;

                     for(var13 = 0; var13 < var12; ++var13) {
                        newValue = var27[var13];
                        newCanDestroy.add(new StringTag(newValue));
                     }
                  } else {
                     newCanDestroy.add(oldTag);
                  }
               }

               tag.put("CanPlaceOn", newCanDestroy);
            }

            if (tag.get(NBT_TAG_NAME + "|CanDestroy") instanceof ListTag) {
               tag.put("CanDestroy", tag.remove(NBT_TAG_NAME + "|CanDestroy"));
            } else if (tag.get("CanDestroy") instanceof ListTag) {
               old = (ListTag)tag.get("CanDestroy");
               newCanDestroy = new ListTag(StringTag.class);
               var23 = old.iterator();

               while(true) {
                  while(var23.hasNext()) {
                     oldTag = (Tag)var23.next();
                     value = oldTag.getValue();
                     newValues = (String[])BlockIdData.fallbackReverseMapping.get(value instanceof String ? Key.stripMinecraftNamespace((String)value) : null);
                     if (newValues != null) {
                        var27 = newValues;
                        var12 = newValues.length;

                        for(var13 = 0; var13 < var12; ++var13) {
                           newValue = var27[var13];
                           newCanDestroy.add(new StringTag(newValue));
                        }
                     } else {
                        newCanDestroy.add(oldTag);
                     }
                  }

                  tag.put("CanDestroy", newCanDestroy);
                  break;
               }
            }
         }

         return item;
      }
   }

   public static String getOldPluginChannelId(String newId) {
      newId = MappingData.validateNewChannel(newId);
      if (newId == null) {
         return null;
      } else {
         byte var2 = -1;
         switch(newId.hashCode()) {
         case -1963049943:
            if (newId.equals("minecraft:unregister")) {
               var2 = 5;
            }
            break;
         case -1149721734:
            if (newId.equals("minecraft:brand")) {
               var2 = 6;
            }
            break;
         case -420924333:
            if (newId.equals("minecraft:book_open")) {
               var2 = 1;
            }
            break;
         case -234943831:
            if (newId.equals("bungeecord:main")) {
               var2 = 7;
            }
            break;
         case 339275216:
            if (newId.equals("minecraft:register")) {
               var2 = 4;
            }
            break;
         case 832866277:
            if (newId.equals("minecraft:debug/paths")) {
               var2 = 2;
            }
            break;
         case 1745645488:
            if (newId.equals("minecraft:debug/neighbors_update")) {
               var2 = 3;
            }
            break;
         case 1963953250:
            if (newId.equals("minecraft:trader_list")) {
               var2 = 0;
            }
         }

         switch(var2) {
         case 0:
            return "MC|TrList";
         case 1:
            return "MC|BOpen";
         case 2:
            return "MC|DebugPath";
         case 3:
            return "MC|DebugNeighborsUpdate";
         case 4:
            return "REGISTER";
         case 5:
            return "UNREGISTER";
         case 6:
            return "MC|Brand";
         case 7:
            return "BungeeCord";
         default:
            String mappedChannel = (String)Protocol1_13To1_12_2.MAPPINGS.getChannelMappings().inverse().get(newId);
            if (mappedChannel != null) {
               return mappedChannel;
            } else {
               return newId.length() > 20 ? newId.substring(0, 20) : newId;
            }
         }
      }
   }

   public static boolean isDamageable(int id) {
      return id >= 256 && id <= 259 || id == 261 || id >= 267 && id <= 279 || id >= 283 && id <= 286 || id >= 290 && id <= 294 || id >= 298 && id <= 317 || id == 346 || id == 359 || id == 398 || id == 442 || id == 443;
   }
}
