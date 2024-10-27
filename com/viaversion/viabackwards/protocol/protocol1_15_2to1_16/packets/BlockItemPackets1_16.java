package com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.packets;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.rewriters.EnchantmentRewriter;
import com.viaversion.viabackwards.api.rewriters.ItemRewriter;
import com.viaversion.viabackwards.api.rewriters.MapColorRewriter;
import com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.Protocol1_15_2To1_16;
import com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.data.MapColorRewrites;
import com.viaversion.viabackwards.protocol.protocol1_16_1to1_16_2.storage.BiomeStorage;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_15;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_16;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.packets.InventoryPackets;
import com.viaversion.viaversion.rewriter.BlockRewriter;
import com.viaversion.viaversion.rewriter.RecipeRewriter;
import com.viaversion.viaversion.util.CompactArrayUtil;
import com.viaversion.viaversion.util.Key;
import com.viaversion.viaversion.util.UUIDUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.Map.Entry;

public class BlockItemPackets1_16 extends ItemRewriter<ClientboundPackets1_16, ServerboundPackets1_14, Protocol1_15_2To1_16> {
   private EnchantmentRewriter enchantmentRewriter;

   public BlockItemPackets1_16(Protocol1_15_2To1_16 protocol) {
      super(protocol);
   }

   protected void registerPackets() {
      BlockRewriter<ClientboundPackets1_16> blockRewriter = BlockRewriter.for1_14(this.protocol);
      RecipeRewriter<ClientboundPackets1_16> recipeRewriter = new RecipeRewriter(this.protocol);
      ((Protocol1_15_2To1_16)this.protocol).registerClientbound(ClientboundPackets1_16.DECLARE_RECIPES, (wrapper) -> {
         int size = (Integer)wrapper.passthrough(Type.VAR_INT);
         int newSize = size;

         for(int i = 0; i < size; ++i) {
            String originalType = (String)wrapper.read(Type.STRING);
            String type = Key.stripMinecraftNamespace(originalType);
            if (type.equals("smithing")) {
               --newSize;
               wrapper.read(Type.STRING);
               wrapper.read(Type.ITEM1_13_2_ARRAY);
               wrapper.read(Type.ITEM1_13_2_ARRAY);
               wrapper.read(Type.ITEM1_13_2);
            } else {
               wrapper.write(Type.STRING, originalType);
               wrapper.passthrough(Type.STRING);
               recipeRewriter.handleRecipeType(wrapper, type);
            }
         }

         wrapper.set(Type.VAR_INT, 0, newSize);
      });
      this.registerSetCooldown(ClientboundPackets1_16.COOLDOWN);
      this.registerWindowItems(ClientboundPackets1_16.WINDOW_ITEMS, Type.ITEM1_13_2_SHORT_ARRAY);
      this.registerSetSlot(ClientboundPackets1_16.SET_SLOT, Type.ITEM1_13_2);
      this.registerTradeList(ClientboundPackets1_16.TRADE_LIST);
      this.registerAdvancements(ClientboundPackets1_16.ADVANCEMENTS, Type.ITEM1_13_2);
      blockRewriter.registerAcknowledgePlayerDigging(ClientboundPackets1_16.ACKNOWLEDGE_PLAYER_DIGGING);
      blockRewriter.registerBlockAction(ClientboundPackets1_16.BLOCK_ACTION);
      blockRewriter.registerBlockChange(ClientboundPackets1_16.BLOCK_CHANGE);
      blockRewriter.registerMultiBlockChange(ClientboundPackets1_16.MULTI_BLOCK_CHANGE);
      ((Protocol1_15_2To1_16)this.protocol).registerClientbound(ClientboundPackets1_16.ENTITY_EQUIPMENT, (wrapper) -> {
         int entityId = (Integer)wrapper.passthrough(Type.VAR_INT);
         ArrayList equipmentData = new ArrayList();

         byte slot;
         int i;
         do {
            slot = (Byte)wrapper.read(Type.BYTE);
            Item item = this.handleItemToClient((Item)wrapper.read(Type.ITEM1_13_2));
            i = slot & 127;
            equipmentData.add(new BlockItemPackets1_16.EquipmentData(i, item));
         } while((slot & -128) != 0);

         BlockItemPackets1_16.EquipmentData firstData = (BlockItemPackets1_16.EquipmentData)equipmentData.get(0);
         wrapper.write(Type.VAR_INT, firstData.slot);
         wrapper.write(Type.ITEM1_13_2, firstData.item);

         for(i = 1; i < equipmentData.size(); ++i) {
            PacketWrapper equipmentPacket = wrapper.create(ClientboundPackets1_15.ENTITY_EQUIPMENT);
            BlockItemPackets1_16.EquipmentData data = (BlockItemPackets1_16.EquipmentData)equipmentData.get(i);
            equipmentPacket.write(Type.VAR_INT, entityId);
            equipmentPacket.write(Type.VAR_INT, data.slot);
            equipmentPacket.write(Type.ITEM1_13_2, data.item);
            equipmentPacket.send(Protocol1_15_2To1_16.class);
         }

      });
      ((Protocol1_15_2To1_16)this.protocol).registerClientbound(ClientboundPackets1_16.UPDATE_LIGHT, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.VAR_INT);
            this.read(Type.BOOLEAN);
         }
      });
      ((Protocol1_15_2To1_16)this.protocol).registerClientbound(ClientboundPackets1_16.CHUNK_DATA, (wrapper) -> {
         Chunk chunk = (Chunk)wrapper.read(ChunkType1_16.TYPE);
         wrapper.write(ChunkType1_15.TYPE, chunk);

         int biome;
         int legacyBiome;
         for(int ixx = 0; ixx < chunk.getSections().length; ++ixx) {
            ChunkSection section = chunk.getSections()[ixx];
            if (section != null) {
               DataPalette palette = section.palette(PaletteType.BLOCKS);

               for(biome = 0; biome < palette.size(); ++biome) {
                  legacyBiome = ((Protocol1_15_2To1_16)this.protocol).getMappingData().getNewBlockStateId(palette.idByIndex(biome));
                  palette.setIdByIndex(biome, legacyBiome);
               }
            }
         }

         CompoundTag heightMaps = chunk.getHeightMap();
         Iterator var9 = heightMaps.values().iterator();

         while(var9.hasNext()) {
            Tag heightMapTag = (Tag)var9.next();
            if (heightMapTag instanceof LongArrayTag) {
               LongArrayTag heightMap = (LongArrayTag)heightMapTag;
               int[] heightMapData = new int[256];
               CompactArrayUtil.iterateCompactArrayWithPadding(9, heightMapData.length, heightMap.getValue(), (ixxx, v) -> {
                  heightMapData[ixxx] = v;
               });
               heightMap.setValue(CompactArrayUtil.createCompactArray(9, heightMapData.length, (ixxx) -> {
                  return (long)heightMapData[ixxx];
               }));
            }
         }

         if (chunk.isBiomeData()) {
            int ix;
            if (wrapper.user().getProtocolInfo().getServerProtocolVersion() >= ProtocolVersion.v1_16_2.getVersion()) {
               BiomeStorage biomeStorage = (BiomeStorage)wrapper.user().get(BiomeStorage.class);

               for(ix = 0; ix < 1024; ++ix) {
                  biome = chunk.getBiomeData()[ix];
                  legacyBiome = biomeStorage.legacyBiome(biome);
                  if (legacyBiome == -1) {
                     ViaBackwards.getPlatform().getLogger().warning("Biome sent that does not exist in the biome registry: " + biome);
                     legacyBiome = 1;
                  }

                  chunk.getBiomeData()[ix] = legacyBiome;
               }
            } else {
               int i = 0;

               while(i < 1024) {
                  ix = chunk.getBiomeData()[i];
                  switch(ix) {
                  case 170:
                  case 171:
                  case 172:
                  case 173:
                     chunk.getBiomeData()[i] = 8;
                  default:
                     ++i;
                  }
               }
            }
         }

         if (chunk.getBlockEntities() != null) {
            var9 = chunk.getBlockEntities().iterator();

            while(var9.hasNext()) {
               CompoundTag blockEntity = (CompoundTag)var9.next();
               this.handleBlockEntity(blockEntity);
            }

         }
      });
      blockRewriter.registerEffect(ClientboundPackets1_16.EFFECT, 1010, 2001);
      this.registerSpawnParticle(ClientboundPackets1_16.SPAWN_PARTICLE, Type.ITEM1_13_2, Type.DOUBLE);
      ((Protocol1_15_2To1_16)this.protocol).registerClientbound(ClientboundPackets1_16.WINDOW_PROPERTY, new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.SHORT);
            this.map(Type.SHORT);
            this.handler((wrapper) -> {
               short property = (Short)wrapper.get(Type.SHORT, 0);
               if (property >= 4 && property <= 6) {
                  short enchantmentId = (Short)wrapper.get(Type.SHORT, 1);
                  if (enchantmentId > 11) {
                     --enchantmentId;
                     wrapper.set(Type.SHORT, 1, enchantmentId);
                  } else if (enchantmentId == 11) {
                     wrapper.set(Type.SHORT, 1, Short.valueOf((short)9));
                  }
               }

            });
         }
      });
      ((Protocol1_15_2To1_16)this.protocol).registerClientbound(ClientboundPackets1_16.MAP_DATA, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.BYTE);
            this.map(Type.BOOLEAN);
            this.map(Type.BOOLEAN);
            this.handler(MapColorRewriter.getRewriteHandler(MapColorRewrites::getMappedColor));
         }
      });
      ((Protocol1_15_2To1_16)this.protocol).registerClientbound(ClientboundPackets1_16.BLOCK_ENTITY_DATA, (wrapper) -> {
         wrapper.passthrough(Type.POSITION1_14);
         wrapper.passthrough(Type.UNSIGNED_BYTE);
         CompoundTag tag = (CompoundTag)wrapper.passthrough(Type.NAMED_COMPOUND_TAG);
         this.handleBlockEntity(tag);
      });
      this.registerClickWindow(ServerboundPackets1_14.CLICK_WINDOW, Type.ITEM1_13_2);
      this.registerCreativeInvAction(ServerboundPackets1_14.CREATIVE_INVENTORY_ACTION, Type.ITEM1_13_2);
      ((Protocol1_15_2To1_16)this.protocol).registerServerbound(ServerboundPackets1_14.EDIT_BOOK, (wrapper) -> {
         this.handleItemToServer((Item)wrapper.passthrough(Type.ITEM1_13_2));
      });
   }

   private void handleBlockEntity(CompoundTag tag) {
      StringTag idTag = (StringTag)tag.get("id");
      if (idTag != null) {
         String id = idTag.getValue();
         Tag skullOwnerTag;
         if (id.equals("minecraft:conduit")) {
            skullOwnerTag = tag.remove("Target");
            if (!(skullOwnerTag instanceof IntArrayTag)) {
               return;
            }

            UUID targetUuid = UUIDUtil.fromIntArray((int[])skullOwnerTag.getValue());
            tag.put("target_uuid", new StringTag(targetUuid.toString()));
         } else if (id.equals("minecraft:skull")) {
            skullOwnerTag = tag.remove("SkullOwner");
            if (!(skullOwnerTag instanceof CompoundTag)) {
               return;
            }

            CompoundTag skullOwnerCompoundTag = (CompoundTag)skullOwnerTag;
            Tag ownerUuidTag = skullOwnerCompoundTag.remove("Id");
            if (ownerUuidTag instanceof IntArrayTag) {
               UUID ownerUuid = UUIDUtil.fromIntArray((int[])ownerUuidTag.getValue());
               skullOwnerCompoundTag.put("Id", new StringTag(ownerUuid.toString()));
            }

            CompoundTag ownerTag = new CompoundTag();
            Iterator var8 = skullOwnerCompoundTag.iterator();

            while(var8.hasNext()) {
               Entry<String, Tag> entry = (Entry)var8.next();
               ownerTag.put((String)entry.getKey(), (Tag)entry.getValue());
            }

            tag.put("Owner", ownerTag);
         }

      }
   }

   protected void registerRewrites() {
      this.enchantmentRewriter = new EnchantmentRewriter(this);
      this.enchantmentRewriter.registerEnchantment("minecraft:soul_speed", "§7Soul Speed");
   }

   public Item handleItemToClient(Item item) {
      if (item == null) {
         return null;
      } else {
         super.handleItemToClient(item);
         CompoundTag tag = item.tag();
         Tag pagesTag;
         Tag page;
         if (item.identifier() == 771 && tag != null) {
            pagesTag = tag.get("SkullOwner");
            if (pagesTag instanceof CompoundTag) {
               CompoundTag ownerCompundTag = (CompoundTag)pagesTag;
               page = ownerCompundTag.get("Id");
               if (page instanceof IntArrayTag) {
                  UUID ownerUuid = UUIDUtil.fromIntArray((int[])page.getValue());
                  ownerCompundTag.put("Id", new StringTag(ownerUuid.toString()));
               }
            }
         }

         if ((item.identifier() == 758 || item.identifier() == 759) && tag != null) {
            pagesTag = tag.get("pages");
            if (pagesTag instanceof ListTag) {
               Iterator var8 = ((ListTag)pagesTag).iterator();

               while(var8.hasNext()) {
                  page = (Tag)var8.next();
                  if (page instanceof StringTag) {
                     StringTag pageTag = (StringTag)page;
                     JsonElement jsonElement = ((Protocol1_15_2To1_16)this.protocol).getTranslatableRewriter().processText(pageTag.getValue());
                     pageTag.setValue(jsonElement.toString());
                  }
               }
            }
         }

         InventoryPackets.newToOldAttributes(item);
         this.enchantmentRewriter.handleToClient(item);
         return item;
      }
   }

   public Item handleItemToServer(Item item) {
      if (item == null) {
         return null;
      } else {
         int identifier = item.identifier();
         super.handleItemToServer(item);
         CompoundTag tag = item.tag();
         if (identifier == 771 && tag != null) {
            Tag ownerTag = tag.get("SkullOwner");
            if (ownerTag instanceof CompoundTag) {
               CompoundTag ownerCompundTag = (CompoundTag)ownerTag;
               Tag idTag = ownerCompundTag.get("Id");
               if (idTag instanceof StringTag) {
                  UUID ownerUuid = UUID.fromString((String)idTag.getValue());
                  ownerCompundTag.put("Id", new IntArrayTag(UUIDUtil.toIntArray(ownerUuid)));
               }
            }
         }

         InventoryPackets.oldToNewAttributes(item);
         this.enchantmentRewriter.handleToServer(item);
         return item;
      }
   }

   private static final class EquipmentData {
      private final int slot;
      private final Item item;

      private EquipmentData(int slot, Item item) {
         this.slot = slot;
         this.item = item;
      }

      // $FF: synthetic method
      EquipmentData(int x0, Item x1, Object x2) {
         this(x0, x1);
      }
   }
}
