package com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.packets;

import com.google.common.collect.ImmutableSet;
import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.rewriters.EnchantmentRewriter;
import com.viaversion.viabackwards.api.rewriters.ItemRewriter;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.Protocol1_13_2To1_14;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.storage.ChunkLightStorage;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.ClientWorld;
import com.viaversion.viaversion.api.minecraft.Environment;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSectionLight;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSectionLightImpl;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_14;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_13;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_14;
import com.viaversion.viaversion.api.type.types.version.Types1_13;
import com.viaversion.viaversion.api.type.types.version.Types1_13_2;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import com.viaversion.viaversion.rewriter.BlockRewriter;
import com.viaversion.viaversion.rewriter.RecipeRewriter;
import com.viaversion.viaversion.util.ComponentUtil;
import com.viaversion.viaversion.util.Key;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BlockItemPackets1_14 extends ItemRewriter<ClientboundPackets1_14, ServerboundPackets1_13, Protocol1_13_2To1_14> {
   private EnchantmentRewriter enchantmentRewriter;

   public BlockItemPackets1_14(Protocol1_13_2To1_14 protocol) {
      super(protocol);
   }

   protected void registerPackets() {
      ((Protocol1_13_2To1_14)this.protocol).registerServerbound(ServerboundPackets1_13.EDIT_BOOK, (wrapper) -> {
         this.handleItemToServer((Item)wrapper.passthrough(Type.ITEM1_13_2));
      });
      ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.OPEN_WINDOW, (wrapper) -> {
         int windowId = (Integer)wrapper.read(Type.VAR_INT);
         wrapper.write(Type.UNSIGNED_BYTE, (short)windowId);
         int type = (Integer)wrapper.read(Type.VAR_INT);
         String stringType = null;
         String containerTitle = null;
         int slotSize = 0;
         if (type < 6) {
            if (type == 2) {
               containerTitle = "Barrel";
            }

            stringType = "minecraft:container";
            slotSize = (type + 1) * 9;
         } else {
            switch(type) {
            case 6:
               stringType = "minecraft:dropper";
               slotSize = 9;
               break;
            case 7:
            case 21:
               if (type == 21) {
                  containerTitle = "Cartography Table";
               }

               stringType = "minecraft:anvil";
               break;
            case 8:
               stringType = "minecraft:beacon";
               slotSize = 1;
               break;
            case 9:
            case 13:
            case 14:
            case 20:
               if (type == 9) {
                  containerTitle = "Blast Furnace";
               } else if (type == 20) {
                  containerTitle = "Smoker";
               } else if (type == 14) {
                  containerTitle = "Grindstone";
               }

               stringType = "minecraft:furnace";
               slotSize = 3;
               break;
            case 10:
               stringType = "minecraft:brewing_stand";
               slotSize = 5;
               break;
            case 11:
               stringType = "minecraft:crafting_table";
               break;
            case 12:
               stringType = "minecraft:enchanting_table";
               break;
            case 15:
               stringType = "minecraft:hopper";
               slotSize = 5;
            case 16:
            case 17:
            default:
               break;
            case 18:
               stringType = "minecraft:villager";
               break;
            case 19:
               stringType = "minecraft:shulker_box";
               slotSize = 27;
            }
         }

         if (stringType == null) {
            ViaBackwards.getPlatform().getLogger().warning("Can't open inventory for 1.13 player! Type: " + type);
            wrapper.cancel();
         } else {
            wrapper.write(Type.STRING, stringType);
            JsonElement title = (JsonElement)wrapper.read(Type.COMPONENT);
            JsonObject object;
            if (containerTitle != null && title.isJsonObject() && (object = title.getAsJsonObject()).has("translate") && (type != 2 || object.getAsJsonPrimitive("translate").getAsString().equals("container.barrel"))) {
               title = ComponentUtil.legacyToJson(containerTitle);
            }

            wrapper.write(Type.COMPONENT, title);
            wrapper.write(Type.UNSIGNED_BYTE, (short)slotSize);
         }
      });
      ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.OPEN_HORSE_WINDOW, ClientboundPackets1_13.OPEN_WINDOW, (wrapper) -> {
         wrapper.passthrough(Type.UNSIGNED_BYTE);
         wrapper.write(Type.STRING, "EntityHorse");
         JsonObject object = new JsonObject();
         object.addProperty("translate", "minecraft.horse");
         wrapper.write(Type.COMPONENT, object);
         wrapper.write(Type.UNSIGNED_BYTE, ((Integer)wrapper.read(Type.VAR_INT)).shortValue());
         wrapper.passthrough(Type.INT);
      });
      BlockRewriter<ClientboundPackets1_14> blockRewriter = BlockRewriter.legacy(this.protocol);
      this.registerSetCooldown(ClientboundPackets1_14.COOLDOWN);
      this.registerWindowItems(ClientboundPackets1_14.WINDOW_ITEMS, Type.ITEM1_13_2_SHORT_ARRAY);
      this.registerSetSlot(ClientboundPackets1_14.SET_SLOT, Type.ITEM1_13_2);
      this.registerAdvancements(ClientboundPackets1_14.ADVANCEMENTS, Type.ITEM1_13_2);
      ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.TRADE_LIST, ClientboundPackets1_13.PLUGIN_MESSAGE, (wrapper) -> {
         wrapper.write(Type.STRING, "minecraft:trader_list");
         int windowId = (Integer)wrapper.read(Type.VAR_INT);
         wrapper.write(Type.INT, windowId);
         int size = (Short)wrapper.passthrough(Type.UNSIGNED_BYTE);

         for(int i = 0; i < size; ++i) {
            Item input = (Item)wrapper.read(Type.ITEM1_13_2);
            input = this.handleItemToClient(input);
            wrapper.write(Type.ITEM1_13_2, input);
            Item output = (Item)wrapper.read(Type.ITEM1_13_2);
            output = this.handleItemToClient(output);
            wrapper.write(Type.ITEM1_13_2, output);
            boolean secondItem = (Boolean)wrapper.passthrough(Type.BOOLEAN);
            if (secondItem) {
               Item second = (Item)wrapper.read(Type.ITEM1_13_2);
               second = this.handleItemToClient(second);
               wrapper.write(Type.ITEM1_13_2, second);
            }

            wrapper.passthrough(Type.BOOLEAN);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.INT);
            wrapper.read(Type.INT);
            wrapper.read(Type.INT);
            wrapper.read(Type.FLOAT);
         }

         wrapper.read(Type.VAR_INT);
         wrapper.read(Type.VAR_INT);
         wrapper.read(Type.BOOLEAN);
      });
      ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.OPEN_BOOK, ClientboundPackets1_13.PLUGIN_MESSAGE, (wrapper) -> {
         wrapper.write(Type.STRING, "minecraft:book_open");
         wrapper.passthrough(Type.VAR_INT);
      });
      ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.ENTITY_EQUIPMENT, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.VAR_INT);
            this.map(Type.ITEM1_13_2);
            this.handler(BlockItemPackets1_14.this.itemToClientHandler(Type.ITEM1_13_2));
            this.handler((wrapper) -> {
               int entityId = (Integer)wrapper.get(Type.VAR_INT, 0);
               EntityType entityType = wrapper.user().getEntityTracker(Protocol1_13_2To1_14.class).entityType(entityId);
               if (entityType != null) {
                  if (entityType.isOrHasParent(EntityTypes1_14.ABSTRACT_HORSE)) {
                     wrapper.setPacketType(ClientboundPackets1_13.ENTITY_METADATA);
                     wrapper.resetReader();
                     wrapper.passthrough(Type.VAR_INT);
                     wrapper.read(Type.VAR_INT);
                     Item item = (Item)wrapper.read(Type.ITEM1_13_2);
                     int armorType = item != null && item.identifier() != 0 ? item.identifier() - 726 : 0;
                     if (armorType < 0 || armorType > 3) {
                        wrapper.cancel();
                        return;
                     }

                     List<Metadata> metadataList = new ArrayList();
                     metadataList.add(new Metadata(16, Types1_13_2.META_TYPES.varIntType, armorType));
                     wrapper.write(Types1_13.METADATA_LIST, metadataList);
                  }

               }
            });
         }
      });
      RecipeRewriter<ClientboundPackets1_14> recipeHandler = new RecipeRewriter(this.protocol);
      Set<String> removedTypes = ImmutableSet.of("crafting_special_suspiciousstew", "blasting", "smoking", "campfire_cooking", "stonecutting");
      ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.DECLARE_RECIPES, (wrapper) -> {
         int size = (Integer)wrapper.passthrough(Type.VAR_INT);
         int deleted = 0;

         for(int i = 0; i < size; ++i) {
            String type = (String)wrapper.read(Type.STRING);
            String id = (String)wrapper.read(Type.STRING);
            type = Key.stripMinecraftNamespace(type);
            if (removedTypes.contains(type)) {
               byte var9 = -1;
               switch(type.hashCode()) {
               case -2084878740:
                  if (type.equals("smoking")) {
                     var9 = 1;
                  }
                  break;
               case -1050336534:
                  if (type.equals("blasting")) {
                     var9 = 0;
                  }
                  break;
               case -858939349:
                  if (type.equals("stonecutting")) {
                     var9 = 3;
                  }
                  break;
               case -68678766:
                  if (type.equals("campfire_cooking")) {
                     var9 = 2;
                  }
               }

               switch(var9) {
               case 0:
               case 1:
               case 2:
                  wrapper.read(Type.STRING);
                  wrapper.read(Type.ITEM1_13_2_ARRAY);
                  wrapper.read(Type.ITEM1_13_2);
                  wrapper.read(Type.FLOAT);
                  wrapper.read(Type.VAR_INT);
                  break;
               case 3:
                  wrapper.read(Type.STRING);
                  wrapper.read(Type.ITEM1_13_2_ARRAY);
                  wrapper.read(Type.ITEM1_13_2);
               }

               ++deleted;
            } else {
               wrapper.write(Type.STRING, id);
               wrapper.write(Type.STRING, type);
               recipeHandler.handleRecipeType(wrapper, type);
            }
         }

         wrapper.set(Type.VAR_INT, 0, size - deleted);
      });
      this.registerClickWindow(ServerboundPackets1_13.CLICK_WINDOW, Type.ITEM1_13_2);
      this.registerCreativeInvAction(ServerboundPackets1_13.CREATIVE_INVENTORY_ACTION, Type.ITEM1_13_2);
      ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.BLOCK_BREAK_ANIMATION, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.POSITION1_14, Type.POSITION1_8);
            this.map(Type.BYTE);
         }
      });
      ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.BLOCK_ENTITY_DATA, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_14, Type.POSITION1_8);
         }
      });
      ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.BLOCK_ACTION, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_14, Type.POSITION1_8);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               int mappedId = ((Protocol1_13_2To1_14)BlockItemPackets1_14.this.protocol).getMappingData().getNewBlockId((Integer)wrapper.get(Type.VAR_INT, 0));
               if (mappedId == -1) {
                  wrapper.cancel();
               } else {
                  wrapper.set(Type.VAR_INT, 0, mappedId);
               }
            });
         }
      });
      ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.BLOCK_CHANGE, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_14, Type.POSITION1_8);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               int id = (Integer)wrapper.get(Type.VAR_INT, 0);
               wrapper.set(Type.VAR_INT, 0, ((Protocol1_13_2To1_14)BlockItemPackets1_14.this.protocol).getMappingData().getNewBlockStateId(id));
            });
         }
      });
      blockRewriter.registerMultiBlockChange(ClientboundPackets1_14.MULTI_BLOCK_CHANGE);
      ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.EXPLOSION, new PacketHandlers() {
         public void register() {
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.handler((wrapper) -> {
               for(int i = 0; i < 3; ++i) {
                  float coord = (Float)wrapper.get(Type.FLOAT, i);
                  if (coord < 0.0F) {
                     coord = (float)Math.floor((double)coord);
                     wrapper.set(Type.FLOAT, i, coord);
                  }
               }

            });
         }
      });
      ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.CHUNK_DATA, (wrapper) -> {
         ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
         Chunk chunk = (Chunk)wrapper.read(ChunkType1_14.TYPE);
         wrapper.write(ChunkType1_13.forEnvironment(clientWorld.getEnvironment()), chunk);
         ChunkLightStorage.ChunkLight chunkLight = ((ChunkLightStorage)wrapper.user().get(ChunkLightStorage.class)).getStoredLight(chunk.getX(), chunk.getZ());

         for(int i = 0; i < chunk.getSections().length; ++i) {
            ChunkSection section = chunk.getSections()[i];
            if (section != null) {
               ChunkSectionLight sectionLight = new ChunkSectionLightImpl();
               section.setLight(sectionLight);
               if (chunkLight == null) {
                  sectionLight.setBlockLight(ChunkLightStorage.FULL_LIGHT);
                  if (clientWorld.getEnvironment() == Environment.NORMAL) {
                     sectionLight.setSkyLight(ChunkLightStorage.FULL_LIGHT);
                  }
               } else {
                  byte[] blockLight = chunkLight.blockLight()[i];
                  sectionLight.setBlockLight(blockLight != null ? blockLight : ChunkLightStorage.FULL_LIGHT);
                  if (clientWorld.getEnvironment() == Environment.NORMAL) {
                     byte[] skyLight = chunkLight.skyLight()[i];
                     sectionLight.setSkyLight(skyLight != null ? skyLight : ChunkLightStorage.FULL_LIGHT);
                  }
               }

               DataPalette palette = section.palette(PaletteType.BLOCKS);
               int y;
               int x;
               if (Via.getConfig().isNonFullBlockLightFix() && section.getNonAirBlocksCount() != 0 && sectionLight.hasBlockLight()) {
                  for(x = 0; x < 16; ++x) {
                     for(y = 0; y < 16; ++y) {
                        for(int z = 0; z < 16; ++z) {
                           int id = palette.idAt(x, y, z);
                           if (Protocol1_14To1_13_2.MAPPINGS.getNonFullBlocks().contains(id)) {
                              sectionLight.getBlockLightNibbleArray().set(x, y, z, 0);
                           }
                        }
                     }
                  }
               }

               for(x = 0; x < palette.size(); ++x) {
                  y = ((Protocol1_13_2To1_14)this.protocol).getMappingData().getNewBlockStateId(palette.idByIndex(x));
                  palette.setIdByIndex(x, y);
               }
            }
         }

      });
      ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.UNLOAD_CHUNK, (wrapper) -> {
         int x = (Integer)wrapper.passthrough(Type.INT);
         int z = (Integer)wrapper.passthrough(Type.INT);
         ((ChunkLightStorage)wrapper.user().get(ChunkLightStorage.class)).unloadChunk(x, z);
      });
      ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.EFFECT, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.POSITION1_14, Type.POSITION1_8);
            this.map(Type.INT);
            this.handler((wrapper) -> {
               int id = (Integer)wrapper.get(Type.INT, 0);
               int data = (Integer)wrapper.get(Type.INT, 1);
               if (id == 1010) {
                  wrapper.set(Type.INT, 1, ((Protocol1_13_2To1_14)BlockItemPackets1_14.this.protocol).getMappingData().getNewItemId(data));
               } else if (id == 2001) {
                  wrapper.set(Type.INT, 1, ((Protocol1_13_2To1_14)BlockItemPackets1_14.this.protocol).getMappingData().getNewBlockStateId(data));
               }

            });
         }
      });
      this.registerSpawnParticle(ClientboundPackets1_14.SPAWN_PARTICLE, Type.ITEM1_13_2, Type.FLOAT);
      ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.MAP_DATA, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.BYTE);
            this.map(Type.BOOLEAN);
            this.read(Type.BOOLEAN);
         }
      });
      ((Protocol1_13_2To1_14)this.protocol).registerClientbound(ClientboundPackets1_14.SPAWN_POSITION, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_14, Type.POSITION1_8);
         }
      });
   }

   protected void registerRewrites() {
      this.enchantmentRewriter = new EnchantmentRewriter(this, false);
      this.enchantmentRewriter.registerEnchantment("minecraft:multishot", "§7Multishot");
      this.enchantmentRewriter.registerEnchantment("minecraft:quick_charge", "§7Quick Charge");
      this.enchantmentRewriter.registerEnchantment("minecraft:piercing", "§7Piercing");
   }

   public Item handleItemToClient(Item item) {
      if (item == null) {
         return null;
      } else {
         super.handleItemToClient(item);
         CompoundTag tag = item.tag();
         CompoundTag display;
         if (tag != null && (display = (CompoundTag)tag.get("display")) != null) {
            ListTag lore = (ListTag)display.get("Lore");
            if (lore != null) {
               this.saveListTag(display, lore, "Lore");
               Iterator var5 = lore.iterator();

               while(var5.hasNext()) {
                  Tag loreEntry = (Tag)var5.next();
                  if (loreEntry instanceof StringTag) {
                     StringTag loreEntryTag = (StringTag)loreEntry;
                     String value = loreEntryTag.getValue();
                     if (value != null && !value.isEmpty()) {
                        loreEntryTag.setValue(ComponentUtil.jsonToLegacy(value));
                     }
                  }
               }
            }
         }

         this.enchantmentRewriter.handleToClient(item);
         return item;
      }
   }

   public Item handleItemToServer(Item item) {
      if (item == null) {
         return null;
      } else {
         CompoundTag tag = item.tag();
         CompoundTag display;
         if (tag != null && (display = (CompoundTag)tag.get("display")) != null) {
            ListTag lore = (ListTag)display.get("Lore");
            if (lore != null && !this.hasBackupTag(display, "Lore")) {
               Iterator var5 = lore.iterator();

               while(var5.hasNext()) {
                  Tag loreEntry = (Tag)var5.next();
                  if (loreEntry instanceof StringTag) {
                     StringTag loreEntryTag = (StringTag)loreEntry;
                     loreEntryTag.setValue(ComponentUtil.legacyToJsonString(loreEntryTag.getValue()));
                  }
               }
            }
         }

         this.enchantmentRewriter.handleToServer(item);
         super.handleItemToServer(item);
         return item;
      }
   }
}
