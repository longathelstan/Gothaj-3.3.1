package com.viaversion.viaversion.protocols.protocol1_20_2to1_20.rewriter;

import com.viaversion.viaversion.api.data.ParticleMappings;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.blockentity.BlockEntity;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.ChunkPosition;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_18;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_20_2;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.ClientboundPackets1_19_4;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.rewriter.RecipeRewriter1_19_4;
import com.viaversion.viaversion.protocols.protocol1_20_2to1_20.Protocol1_20_2To1_20;
import com.viaversion.viaversion.protocols.protocol1_20_2to1_20.packet.ServerboundPackets1_20_2;
import com.viaversion.viaversion.protocols.protocol1_20_2to1_20.util.PotionEffects;
import com.viaversion.viaversion.rewriter.BlockRewriter;
import com.viaversion.viaversion.rewriter.ItemRewriter;
import com.viaversion.viaversion.util.MathUtil;
import java.util.Iterator;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class BlockItemPacketRewriter1_20_2 extends ItemRewriter<ClientboundPackets1_19_4, ServerboundPackets1_20_2, Protocol1_20_2To1_20> {
   public BlockItemPacketRewriter1_20_2(Protocol1_20_2To1_20 protocol) {
      super(protocol, Type.ITEM1_13_2, Type.ITEM1_13_2_ARRAY);
   }

   public void registerPackets() {
      BlockRewriter<ClientboundPackets1_19_4> blockRewriter = BlockRewriter.for1_14(this.protocol);
      blockRewriter.registerBlockAction(ClientboundPackets1_19_4.BLOCK_ACTION);
      blockRewriter.registerBlockChange(ClientboundPackets1_19_4.BLOCK_CHANGE);
      blockRewriter.registerVarLongMultiBlockChange1_20(ClientboundPackets1_19_4.MULTI_BLOCK_CHANGE);
      blockRewriter.registerEffect(ClientboundPackets1_19_4.EFFECT, 1010, 2001);
      ((Protocol1_20_2To1_20)this.protocol).registerServerbound(ServerboundPackets1_20_2.SET_BEACON_EFFECT, (wrapper) -> {
         if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
            wrapper.write(Type.VAR_INT, (Integer)wrapper.read(Type.VAR_INT) + 1);
         }

         if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
            wrapper.write(Type.VAR_INT, (Integer)wrapper.read(Type.VAR_INT) + 1);
         }

      });
      ((Protocol1_20_2To1_20)this.protocol).registerClientbound(ClientboundPackets1_19_4.UNLOAD_CHUNK, (wrapper) -> {
         int x = (Integer)wrapper.read(Type.INT);
         int z = (Integer)wrapper.read(Type.INT);
         wrapper.write(Type.CHUNK_POSITION, new ChunkPosition(x, z));
      });
      ((Protocol1_20_2To1_20)this.protocol).registerClientbound(ClientboundPackets1_19_4.NBT_QUERY, (wrapper) -> {
         wrapper.passthrough(Type.VAR_INT);
         wrapper.write(Type.COMPOUND_TAG, wrapper.read(Type.NAMED_COMPOUND_TAG));
      });
      ((Protocol1_20_2To1_20)this.protocol).registerClientbound(ClientboundPackets1_19_4.BLOCK_ENTITY_DATA, (wrapper) -> {
         wrapper.passthrough(Type.POSITION1_14);
         wrapper.passthrough(Type.VAR_INT);
         wrapper.write(Type.COMPOUND_TAG, this.handleBlockEntity((CompoundTag)wrapper.read(Type.NAMED_COMPOUND_TAG)));
      });
      ((Protocol1_20_2To1_20)this.protocol).registerClientbound(ClientboundPackets1_19_4.CHUNK_DATA, (wrapper) -> {
         EntityTracker tracker = ((Protocol1_20_2To1_20)this.protocol).getEntityRewriter().tracker(wrapper.user());
         Type<Chunk> chunkType = new ChunkType1_18(tracker.currentWorldSectionHeight(), MathUtil.ceilLog2(((Protocol1_20_2To1_20)this.protocol).getMappingData().getBlockStateMappings().size()), MathUtil.ceilLog2(tracker.biomesSent()));
         Chunk chunk = (Chunk)wrapper.read(chunkType);
         Type<Chunk> newChunkType = new ChunkType1_20_2(tracker.currentWorldSectionHeight(), MathUtil.ceilLog2(((Protocol1_20_2To1_20)this.protocol).getMappingData().getBlockStateMappings().mappedSize()), MathUtil.ceilLog2(tracker.biomesSent()));
         wrapper.write(newChunkType, chunk);
         ChunkSection[] var6 = chunk.getSections();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            ChunkSection section = var6[var8];
            DataPalette blockPalette = section.palette(PaletteType.BLOCKS);

            for(int i = 0; i < blockPalette.size(); ++i) {
               int id = blockPalette.idByIndex(i);
               blockPalette.setIdByIndex(i, ((Protocol1_20_2To1_20)this.protocol).getMappingData().getNewBlockStateId(id));
            }
         }

         Iterator var13 = chunk.blockEntities().iterator();

         while(var13.hasNext()) {
            BlockEntity blockEntity = (BlockEntity)var13.next();
            this.handleBlockEntity(blockEntity.tag());
         }

      });
      ((Protocol1_20_2To1_20)this.protocol).registerClientbound(ClientboundPackets1_19_4.WINDOW_ITEMS, new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               Item[] items = (Item[])wrapper.read(Type.ITEM1_13_2_ARRAY);
               Item[] var3 = items;
               int var4 = items.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  Item item = var3[var5];
                  BlockItemPacketRewriter1_20_2.this.handleItemToClient(item);
               }

               wrapper.write(Type.ITEM1_20_2_ARRAY, items);
               wrapper.write(Type.ITEM1_20_2, BlockItemPacketRewriter1_20_2.this.handleItemToClient((Item)wrapper.read(Type.ITEM1_13_2)));
            });
         }
      });
      ((Protocol1_20_2To1_20)this.protocol).registerClientbound(ClientboundPackets1_19_4.SET_SLOT, new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.VAR_INT);
            this.map(Type.SHORT);
            this.handler((wrapper) -> {
               wrapper.write(Type.ITEM1_20_2, BlockItemPacketRewriter1_20_2.this.handleItemToClient((Item)wrapper.read(Type.ITEM1_13_2)));
            });
         }
      });
      ((Protocol1_20_2To1_20)this.protocol).registerClientbound(ClientboundPackets1_19_4.ADVANCEMENTS, (wrapper) -> {
         wrapper.passthrough(Type.BOOLEAN);
         int size = (Integer)wrapper.passthrough(Type.VAR_INT);

         for(int i = 0; i < size; ++i) {
            wrapper.passthrough(Type.STRING);
            if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
               wrapper.passthrough(Type.STRING);
            }

            int requirements;
            if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
               wrapper.passthrough(Type.COMPONENT);
               wrapper.passthrough(Type.COMPONENT);
               wrapper.write(Type.ITEM1_20_2, this.handleItemToClient((Item)wrapper.read(Type.ITEM1_13_2)));
               wrapper.passthrough(Type.VAR_INT);
               requirements = (Integer)wrapper.passthrough(Type.INT);
               if ((requirements & 1) != 0) {
                  wrapper.passthrough(Type.STRING);
               }

               wrapper.passthrough(Type.FLOAT);
               wrapper.passthrough(Type.FLOAT);
            }

            wrapper.read(Type.STRING_ARRAY);
            requirements = (Integer)wrapper.passthrough(Type.VAR_INT);

            for(int array = 0; array < requirements; ++array) {
               wrapper.passthrough(Type.STRING_ARRAY);
            }

            wrapper.passthrough(Type.BOOLEAN);
         }

      });
      ((Protocol1_20_2To1_20)this.protocol).registerClientbound(ClientboundPackets1_19_4.ENTITY_EQUIPMENT, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               byte slot;
               do {
                  slot = (Byte)wrapper.passthrough(Type.BYTE);
                  wrapper.write(Type.ITEM1_20_2, BlockItemPacketRewriter1_20_2.this.handleItemToClient((Item)wrapper.read(Type.ITEM1_13_2)));
               } while((slot & -128) != 0);

            });
         }
      });
      ((Protocol1_20_2To1_20)this.protocol).registerServerbound(ServerboundPackets1_20_2.CLICK_WINDOW, new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.VAR_INT);
            this.map(Type.SHORT);
            this.map(Type.BYTE);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               int length = (Integer)wrapper.passthrough(Type.VAR_INT);

               for(int i = 0; i < length; ++i) {
                  wrapper.passthrough(Type.SHORT);
                  wrapper.write(Type.ITEM1_13_2, BlockItemPacketRewriter1_20_2.this.handleItemToServer((Item)wrapper.read(Type.ITEM1_20_2)));
               }

               wrapper.write(Type.ITEM1_13_2, BlockItemPacketRewriter1_20_2.this.handleItemToServer((Item)wrapper.read(Type.ITEM1_20_2)));
            });
         }
      });
      ((Protocol1_20_2To1_20)this.protocol).registerClientbound(ClientboundPackets1_19_4.TRADE_LIST, (wrapper) -> {
         wrapper.passthrough(Type.VAR_INT);
         int size = (Integer)wrapper.passthrough(Type.VAR_INT);

         for(int i = 0; i < size; ++i) {
            wrapper.write(Type.ITEM1_20_2, this.handleItemToClient((Item)wrapper.read(Type.ITEM1_13_2)));
            wrapper.write(Type.ITEM1_20_2, this.handleItemToClient((Item)wrapper.read(Type.ITEM1_13_2)));
            wrapper.write(Type.ITEM1_20_2, this.handleItemToClient((Item)wrapper.read(Type.ITEM1_13_2)));
            wrapper.passthrough(Type.BOOLEAN);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.FLOAT);
            wrapper.passthrough(Type.INT);
         }

      });
      ((Protocol1_20_2To1_20)this.protocol).registerServerbound(ServerboundPackets1_20_2.CREATIVE_INVENTORY_ACTION, new PacketHandlers() {
         public void register() {
            this.map(Type.SHORT);
            this.handler((wrapper) -> {
               wrapper.write(Type.ITEM1_13_2, BlockItemPacketRewriter1_20_2.this.handleItemToServer((Item)wrapper.read(Type.ITEM1_20_2)));
            });
         }
      });
      ((Protocol1_20_2To1_20)this.protocol).registerClientbound(ClientboundPackets1_19_4.SPAWN_PARTICLE, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.BOOLEAN);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.INT);
            this.handler((wrapper) -> {
               int id = (Integer)wrapper.get(Type.VAR_INT, 0);
               ParticleMappings mappings = Protocol1_20_2To1_20.MAPPINGS.getParticleMappings();
               if (mappings.isBlockParticle(id)) {
                  int data = (Integer)wrapper.read(Type.VAR_INT);
                  wrapper.write(Type.VAR_INT, ((Protocol1_20_2To1_20)BlockItemPacketRewriter1_20_2.this.protocol).getMappingData().getNewBlockStateId(data));
               } else if (mappings.isItemParticle(id)) {
                  wrapper.write(Type.ITEM1_20_2, BlockItemPacketRewriter1_20_2.this.handleItemToClient((Item)wrapper.read(Type.ITEM1_13_2)));
               }

            });
         }
      });
      (new RecipeRewriter1_19_4<ClientboundPackets1_19_4>(this.protocol) {
         public void handleCraftingShapeless(PacketWrapper wrapper) throws Exception {
            wrapper.passthrough(Type.STRING);
            wrapper.passthrough(Type.VAR_INT);
            this.handleIngredients(wrapper);
            Item result = (Item)wrapper.read(this.itemType());
            this.rewrite(result);
            wrapper.write(Type.ITEM1_20_2, result);
         }

         public void handleSmelting(PacketWrapper wrapper) throws Exception {
            wrapper.passthrough(Type.STRING);
            wrapper.passthrough(Type.VAR_INT);
            this.handleIngredient(wrapper);
            Item result = (Item)wrapper.read(this.itemType());
            this.rewrite(result);
            wrapper.write(Type.ITEM1_20_2, result);
            wrapper.passthrough(Type.FLOAT);
            wrapper.passthrough(Type.VAR_INT);
         }

         public void handleCraftingShaped(PacketWrapper wrapper) throws Exception {
            int ingredients = (Integer)wrapper.passthrough(Type.VAR_INT) * (Integer)wrapper.passthrough(Type.VAR_INT);
            wrapper.passthrough(Type.STRING);
            wrapper.passthrough(Type.VAR_INT);

            for(int i = 0; i < ingredients; ++i) {
               this.handleIngredient(wrapper);
            }

            Item result = (Item)wrapper.read(this.itemType());
            this.rewrite(result);
            wrapper.write(Type.ITEM1_20_2, result);
            wrapper.passthrough(Type.BOOLEAN);
         }

         public void handleStonecutting(PacketWrapper wrapper) throws Exception {
            wrapper.passthrough(Type.STRING);
            this.handleIngredient(wrapper);
            Item result = (Item)wrapper.read(this.itemType());
            this.rewrite(result);
            wrapper.write(Type.ITEM1_20_2, result);
         }

         public void handleSmithing(PacketWrapper wrapper) throws Exception {
            this.handleIngredient(wrapper);
            this.handleIngredient(wrapper);
            Item result = (Item)wrapper.read(this.itemType());
            this.rewrite(result);
            wrapper.write(Type.ITEM1_20_2, result);
         }

         public void handleSmithingTransform(PacketWrapper wrapper) throws Exception {
            this.handleIngredient(wrapper);
            this.handleIngredient(wrapper);
            this.handleIngredient(wrapper);
            Item result = (Item)wrapper.read(this.itemType());
            this.rewrite(result);
            wrapper.write(Type.ITEM1_20_2, result);
         }

         protected void handleIngredient(PacketWrapper wrapper) throws Exception {
            Item[] items = (Item[])wrapper.read(this.itemArrayType());
            wrapper.write(Type.ITEM1_20_2_ARRAY, items);
            Item[] var3 = items;
            int var4 = items.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Item item = var3[var5];
               this.rewrite(item);
            }

         }
      }).register(ClientboundPackets1_19_4.DECLARE_RECIPES);
   }

   @Nullable
   public Item handleItemToClient(@Nullable Item item) {
      if (item == null) {
         return null;
      } else {
         if (item.tag() != null) {
            to1_20_2Effects(item);
         }

         return super.handleItemToClient(item);
      }
   }

   @Nullable
   public Item handleItemToServer(@Nullable Item item) {
      if (item == null) {
         return null;
      } else {
         if (item.tag() != null) {
            to1_20_1Effects(item);
         }

         return super.handleItemToServer(item);
      }
   }

   public static void to1_20_2Effects(Item item) {
      Tag customPotionEffectsTag = item.tag().remove("CustomPotionEffects");
      if (customPotionEffectsTag instanceof ListTag) {
         ListTag effectsTag = (ListTag)customPotionEffectsTag;
         item.tag().put("custom_potion_effects", customPotionEffectsTag);
         Iterator var3 = effectsTag.iterator();

         while(var3.hasNext()) {
            Tag tag = (Tag)var3.next();
            if (tag instanceof CompoundTag) {
               CompoundTag effectTag = (CompoundTag)tag;
               Tag idTag = effectTag.remove("Id");
               if (idTag instanceof NumberTag) {
                  String key = PotionEffects.idToKey(((NumberTag)idTag).asInt());
                  if (key != null) {
                     effectTag.put("id", new StringTag(key));
                  }
               }

               renameTag(effectTag, "Amplifier", "amplifier");
               renameTag(effectTag, "Duration", "duration");
               renameTag(effectTag, "Ambient", "ambient");
               renameTag(effectTag, "ShowParticles", "show_particles");
               renameTag(effectTag, "ShowIcon", "show_icon");
               renameTag(effectTag, "HiddenEffect", "hidden_effect");
               renameTag(effectTag, "FactorCalculationData", "factor_calculation_data");
            }
         }
      }

   }

   public static void to1_20_1Effects(Item item) {
      Tag customPotionEffectsTag = item.tag().remove("custom_potion_effects");
      if (customPotionEffectsTag instanceof ListTag) {
         ListTag effectsTag = (ListTag)customPotionEffectsTag;
         item.tag().put("CustomPotionEffects", effectsTag);
         Iterator var3 = effectsTag.iterator();

         while(var3.hasNext()) {
            Tag tag = (Tag)var3.next();
            if (tag instanceof CompoundTag) {
               CompoundTag effectTag = (CompoundTag)tag;
               Tag idTag = effectTag.remove("id");
               if (idTag instanceof StringTag) {
                  int id = PotionEffects.keyToId(((StringTag)idTag).getValue());
                  effectTag.put("Id", new IntTag(id));
               }

               renameTag(effectTag, "amplifier", "Amplifier");
               renameTag(effectTag, "duration", "Duration");
               renameTag(effectTag, "ambient", "Ambient");
               renameTag(effectTag, "show_particles", "ShowParticles");
               renameTag(effectTag, "show_icon", "ShowIcon");
               renameTag(effectTag, "hidden_effect", "HiddenEffect");
               renameTag(effectTag, "factor_calculation_data", "FactorCalculationData");
            }
         }
      }

   }

   private static void renameTag(CompoundTag tag, String entryName, String toEntryName) {
      Tag entry = tag.remove(entryName);
      if (entry != null) {
         tag.put(toEntryName, entry);
      }

   }

   @Nullable
   private CompoundTag handleBlockEntity(@Nullable CompoundTag tag) {
      if (tag == null) {
         return null;
      } else {
         IntTag primaryEffect = (IntTag)tag.remove("Primary");
         if (primaryEffect != null && primaryEffect.asInt() != 0) {
            tag.put("primary_effect", new StringTag(PotionEffects.idToKeyOrLuck(primaryEffect.asInt())));
         }

         IntTag secondaryEffect = (IntTag)tag.remove("Secondary");
         if (secondaryEffect != null && secondaryEffect.asInt() != 0) {
            tag.put("secondary_effect", new StringTag(PotionEffects.idToKeyOrLuck(secondaryEffect.asInt())));
         }

         return tag;
      }
   }
}
