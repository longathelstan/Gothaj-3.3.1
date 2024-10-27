package com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets;

import com.google.common.collect.Sets;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.DoubleTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.storage.EntityTracker1_14;
import com.viaversion.viaversion.rewriter.ComponentRewriter;
import com.viaversion.viaversion.rewriter.ItemRewriter;
import com.viaversion.viaversion.rewriter.RecipeRewriter;
import com.viaversion.viaversion.util.ComponentUtil;
import com.viaversion.viaversion.util.Key;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class InventoryPackets extends ItemRewriter<ClientboundPackets1_13, ServerboundPackets1_14, Protocol1_14To1_13_2> {
   private static final String NBT_TAG_NAME = "ViaVersion|" + Protocol1_14To1_13_2.class.getSimpleName();
   private static final Set<String> REMOVED_RECIPE_TYPES = Sets.newHashSet(new String[]{"crafting_special_banneraddpattern", "crafting_special_repairitem"});
   private static final ComponentRewriter<ClientboundPackets1_13> COMPONENT_REWRITER;

   public InventoryPackets(Protocol1_14To1_13_2 protocol) {
      super(protocol, Type.ITEM1_13_2, Type.ITEM1_13_2_ARRAY);
   }

   public void registerPackets() {
      this.registerSetCooldown(ClientboundPackets1_13.COOLDOWN);
      this.registerAdvancements(ClientboundPackets1_13.ADVANCEMENTS, Type.ITEM1_13_2);
      ((Protocol1_14To1_13_2)this.protocol).registerClientbound(ClientboundPackets1_13.OPEN_WINDOW, (ClientboundPacketType)null, (wrapper) -> {
         Short windowId = (Short)wrapper.read(Type.UNSIGNED_BYTE);
         String type = (String)wrapper.read(Type.STRING);
         JsonElement title = (JsonElement)wrapper.read(Type.COMPONENT);
         COMPONENT_REWRITER.processText(title);
         Short slots = (Short)wrapper.read(Type.UNSIGNED_BYTE);
         int typeId;
         if (type.equals("EntityHorse")) {
            wrapper.setPacketType(ClientboundPackets1_14.OPEN_HORSE_WINDOW);
            typeId = (Integer)wrapper.read(Type.INT);
            wrapper.write(Type.UNSIGNED_BYTE, windowId);
            wrapper.write(Type.VAR_INT, slots.intValue());
            wrapper.write(Type.INT, typeId);
         } else {
            wrapper.setPacketType(ClientboundPackets1_14.OPEN_WINDOW);
            wrapper.write(Type.VAR_INT, windowId.intValue());
            typeId = -1;
            byte var7 = -1;
            switch(type.hashCode()) {
            case -1879003021:
               if (type.equals("minecraft:villager")) {
                  var7 = 6;
               }
               break;
            case -1719356277:
               if (type.equals("minecraft:furnace")) {
                  var7 = 1;
               }
               break;
            case -1293651279:
               if (type.equals("minecraft:beacon")) {
                  var7 = 7;
               }
               break;
            case -1150744385:
               if (type.equals("minecraft:anvil")) {
                  var7 = 8;
               }
               break;
            case -1149092108:
               if (type.equals("minecraft:chest")) {
                  var7 = 12;
               }
               break;
            case -1124126594:
               if (type.equals("minecraft:crafting_table")) {
                  var7 = 0;
               }
               break;
            case -1112182111:
               if (type.equals("minecraft:hopper")) {
                  var7 = 9;
               }
               break;
            case 319164197:
               if (type.equals("minecraft:enchanting_table")) {
                  var7 = 4;
               }
               break;
            case 712019713:
               if (type.equals("minecraft:dropper")) {
                  var7 = 2;
               }
               break;
            case 1374330859:
               if (type.equals("minecraft:shulker_box")) {
                  var7 = 10;
               }
               break;
            case 1438413556:
               if (type.equals("minecraft:container")) {
                  var7 = 11;
               }
               break;
            case 1649065834:
               if (type.equals("minecraft:brewing_stand")) {
                  var7 = 5;
               }
               break;
            case 2090881320:
               if (type.equals("minecraft:dispenser")) {
                  var7 = 3;
               }
            }

            switch(var7) {
            case 0:
               typeId = 11;
               break;
            case 1:
               typeId = 13;
               break;
            case 2:
            case 3:
               typeId = 6;
               break;
            case 4:
               typeId = 12;
               break;
            case 5:
               typeId = 10;
               break;
            case 6:
               typeId = 18;
               break;
            case 7:
               typeId = 8;
               break;
            case 8:
               typeId = 7;
               break;
            case 9:
               typeId = 15;
               break;
            case 10:
               typeId = 19;
               break;
            case 11:
            case 12:
            default:
               if (slots > 0 && slots <= 54) {
                  typeId = slots / 9 - 1;
               }
            }

            if (typeId == -1) {
               Via.getPlatform().getLogger().warning("Can't open inventory for 1.14 player! Type: " + type + " Size: " + slots);
            }

            wrapper.write(Type.VAR_INT, typeId);
            wrapper.write(Type.COMPONENT, title);
         }

      });
      this.registerWindowItems(ClientboundPackets1_13.WINDOW_ITEMS, Type.ITEM1_13_2_SHORT_ARRAY);
      this.registerSetSlot(ClientboundPackets1_13.SET_SLOT, Type.ITEM1_13_2);
      ((Protocol1_14To1_13_2)this.protocol).registerClientbound(ClientboundPackets1_13.PLUGIN_MESSAGE, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.handler((wrapper) -> {
               String channel = Key.namespaced((String)wrapper.get(Type.STRING, 0));
               int hand;
               if (channel.equals("minecraft:trader_list")) {
                  wrapper.setPacketType(ClientboundPackets1_14.TRADE_LIST);
                  wrapper.resetReader();
                  wrapper.read(Type.STRING);
                  hand = (Integer)wrapper.read(Type.INT);
                  EntityTracker1_14 tracker = (EntityTracker1_14)wrapper.user().getEntityTracker(Protocol1_14To1_13_2.class);
                  tracker.setLatestTradeWindowId(hand);
                  wrapper.write(Type.VAR_INT, hand);
                  int size = (Short)wrapper.passthrough(Type.UNSIGNED_BYTE);

                  for(int i = 0; i < size; ++i) {
                     InventoryPackets.this.handleItemToClient((Item)wrapper.passthrough(Type.ITEM1_13_2));
                     InventoryPackets.this.handleItemToClient((Item)wrapper.passthrough(Type.ITEM1_13_2));
                     boolean secondItem = (Boolean)wrapper.passthrough(Type.BOOLEAN);
                     if (secondItem) {
                        InventoryPackets.this.handleItemToClient((Item)wrapper.passthrough(Type.ITEM1_13_2));
                     }

                     wrapper.passthrough(Type.BOOLEAN);
                     wrapper.passthrough(Type.INT);
                     wrapper.passthrough(Type.INT);
                     wrapper.write(Type.INT, 0);
                     wrapper.write(Type.INT, 0);
                     wrapper.write(Type.FLOAT, 0.0F);
                  }

                  wrapper.write(Type.VAR_INT, 0);
                  wrapper.write(Type.VAR_INT, 0);
                  wrapper.write(Type.BOOLEAN, false);
                  wrapper.clearInputBuffer();
               } else if (channel.equals("minecraft:book_open")) {
                  hand = (Integer)wrapper.read(Type.VAR_INT);
                  wrapper.clearPacket();
                  wrapper.setPacketType(ClientboundPackets1_14.OPEN_BOOK);
                  wrapper.write(Type.VAR_INT, hand);
               }

            });
         }
      });
      this.registerEntityEquipment(ClientboundPackets1_13.ENTITY_EQUIPMENT, Type.ITEM1_13_2);
      RecipeRewriter<ClientboundPackets1_13> recipeRewriter = new RecipeRewriter(this.protocol);
      ((Protocol1_14To1_13_2)this.protocol).registerClientbound(ClientboundPackets1_13.DECLARE_RECIPES, (wrapper) -> {
         int size = (Integer)wrapper.passthrough(Type.VAR_INT);
         int deleted = 0;

         for(int i = 0; i < size; ++i) {
            String id = (String)wrapper.read(Type.STRING);
            String type = (String)wrapper.read(Type.STRING);
            if (REMOVED_RECIPE_TYPES.contains(type)) {
               ++deleted;
            } else {
               wrapper.write(Type.STRING, type);
               wrapper.write(Type.STRING, id);
               recipeRewriter.handleRecipeType(wrapper, type);
            }
         }

         wrapper.set(Type.VAR_INT, 0, size - deleted);
      });
      this.registerClickWindow(ServerboundPackets1_14.CLICK_WINDOW, Type.ITEM1_13_2);
      ((Protocol1_14To1_13_2)this.protocol).registerServerbound(ServerboundPackets1_14.SELECT_TRADE, (wrapper) -> {
         PacketWrapper resyncPacket = wrapper.create(ServerboundPackets1_13.CLICK_WINDOW);
         EntityTracker1_14 tracker = (EntityTracker1_14)wrapper.user().getEntityTracker(Protocol1_14To1_13_2.class);
         resyncPacket.write(Type.UNSIGNED_BYTE, (short)tracker.getLatestTradeWindowId());
         resyncPacket.write(Type.SHORT, -999);
         resyncPacket.write(Type.BYTE, (byte)2);
         resyncPacket.write(Type.SHORT, (short)ThreadLocalRandom.current().nextInt());
         resyncPacket.write(Type.VAR_INT, 5);
         CompoundTag tag = new CompoundTag();
         tag.put("force_resync", new DoubleTag(Double.NaN));
         resyncPacket.write(Type.ITEM1_13_2, new DataItem(1, (byte)1, (short)0, tag));
         resyncPacket.scheduleSendToServer(Protocol1_14To1_13_2.class);
      });
      this.registerCreativeInvAction(ServerboundPackets1_14.CREATIVE_INVENTORY_ACTION, Type.ITEM1_13_2);
      this.registerSpawnParticle(ClientboundPackets1_13.SPAWN_PARTICLE, Type.ITEM1_13_2, Type.FLOAT);
   }

   public Item handleItemToClient(Item item) {
      if (item == null) {
         return null;
      } else {
         item.setIdentifier(Protocol1_14To1_13_2.MAPPINGS.getNewItemId(item.identifier()));
         if (item.tag() == null) {
            return item;
         } else {
            Tag displayTag = item.tag().get("display");
            if (displayTag instanceof CompoundTag) {
               CompoundTag display = (CompoundTag)displayTag;
               Tag loreTag = display.get("Lore");
               if (loreTag instanceof ListTag) {
                  ListTag lore = (ListTag)loreTag;
                  display.put(NBT_TAG_NAME + "|Lore", new ListTag(lore.copy().getValue()));
                  Iterator var6 = lore.iterator();

                  while(var6.hasNext()) {
                     Tag loreEntry = (Tag)var6.next();
                     if (loreEntry instanceof StringTag) {
                        String jsonText = ComponentUtil.legacyToJsonString(((StringTag)loreEntry).getValue(), true);
                        ((StringTag)loreEntry).setValue(jsonText);
                     }
                  }
               }
            }

            return item;
         }
      }
   }

   public Item handleItemToServer(Item item) {
      if (item == null) {
         return null;
      } else {
         item.setIdentifier(Protocol1_14To1_13_2.MAPPINGS.getOldItemId(item.identifier()));
         if (item.tag() == null) {
            return item;
         } else {
            Tag displayTag = item.tag().get("display");
            if (displayTag instanceof CompoundTag) {
               CompoundTag display = (CompoundTag)displayTag;
               Tag loreTag = display.get("Lore");
               if (loreTag instanceof ListTag) {
                  ListTag lore = (ListTag)loreTag;
                  ListTag savedLore = (ListTag)display.remove(NBT_TAG_NAME + "|Lore");
                  if (savedLore != null) {
                     display.put("Lore", new ListTag(savedLore.getValue()));
                  } else {
                     Iterator var7 = lore.iterator();

                     while(var7.hasNext()) {
                        Tag loreEntry = (Tag)var7.next();
                        if (loreEntry instanceof StringTag) {
                           ((StringTag)loreEntry).setValue(ComponentUtil.jsonToLegacy(((StringTag)loreEntry).getValue()));
                        }
                     }
                  }
               }
            }

            return item;
         }
      }
   }

   static {
      COMPONENT_REWRITER = new ComponentRewriter<ClientboundPackets1_13>((Protocol)null, ComponentRewriter.ReadType.JSON) {
         protected void handleTranslate(JsonObject object, String translate) {
            super.handleTranslate(object, translate);
            if (translate.startsWith("block.") && translate.endsWith(".name")) {
               object.addProperty("translate", translate.substring(0, translate.length() - 5));
            }

         }
      };
   }
}
