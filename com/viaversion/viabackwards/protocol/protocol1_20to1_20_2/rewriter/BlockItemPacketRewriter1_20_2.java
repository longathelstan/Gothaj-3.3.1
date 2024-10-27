package com.viaversion.viabackwards.protocol.protocol1_20to1_20_2.rewriter;

import com.viaversion.viabackwards.api.rewriters.ItemRewriter;
import com.viaversion.viabackwards.protocol.protocol1_20to1_20_2.Protocol1_20To1_20_2;
import com.viaversion.viaversion.api.data.ParticleMappings;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.blockentity.BlockEntity;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.ChunkPosition;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_18;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_20_2;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.ServerboundPackets1_19_4;
import com.viaversion.viaversion.protocols.protocol1_20_2to1_20.packet.ClientboundPackets1_20_2;
import com.viaversion.viaversion.protocols.protocol1_20_2to1_20.packet.ServerboundPackets1_20_2;
import com.viaversion.viaversion.protocols.protocol1_20_2to1_20.rewriter.RecipeRewriter1_20_2;
import com.viaversion.viaversion.protocols.protocol1_20_2to1_20.util.PotionEffects;
import com.viaversion.viaversion.rewriter.BlockRewriter;
import com.viaversion.viaversion.util.Key;
import com.viaversion.viaversion.util.MathUtil;
import java.util.Iterator;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class BlockItemPacketRewriter1_20_2 extends ItemRewriter<ClientboundPackets1_20_2, ServerboundPackets1_19_4, Protocol1_20To1_20_2> {
   public BlockItemPacketRewriter1_20_2(Protocol1_20To1_20_2 protocol) {
      super(protocol, Type.ITEM1_20_2, Type.ITEM1_20_2_ARRAY);
   }

   public void registerPackets() {
      BlockRewriter<ClientboundPackets1_20_2> blockRewriter = BlockRewriter.for1_14(this.protocol);
      blockRewriter.registerBlockAction(ClientboundPackets1_20_2.BLOCK_ACTION);
      blockRewriter.registerBlockChange(ClientboundPackets1_20_2.BLOCK_CHANGE);
      blockRewriter.registerVarLongMultiBlockChange1_20(ClientboundPackets1_20_2.MULTI_BLOCK_CHANGE);
      blockRewriter.registerEffect(ClientboundPackets1_20_2.EFFECT, 1010, 2001);
      ((Protocol1_20To1_20_2)this.protocol).cancelClientbound(ClientboundPackets1_20_2.CHUNK_BATCH_START);
      ((Protocol1_20To1_20_2)this.protocol).registerClientbound(ClientboundPackets1_20_2.CHUNK_BATCH_FINISHED, (ClientboundPacketType)null, (wrapper) -> {
         wrapper.cancel();
         PacketWrapper receivedPacket = wrapper.create(ServerboundPackets1_20_2.CHUNK_BATCH_RECEIVED);
         receivedPacket.write(Type.FLOAT, 500.0F);
         receivedPacket.sendToServer(Protocol1_20To1_20_2.class);
      });
      ((Protocol1_20To1_20_2)this.protocol).registerClientbound(ClientboundPackets1_20_2.UNLOAD_CHUNK, (wrapper) -> {
         ChunkPosition chunkPosition = (ChunkPosition)wrapper.read(Type.CHUNK_POSITION);
         wrapper.write(Type.INT, chunkPosition.chunkX());
         wrapper.write(Type.INT, chunkPosition.chunkZ());
      });
      ((Protocol1_20To1_20_2)this.protocol).registerClientbound(ClientboundPackets1_20_2.MAP_DATA, (wrapper) -> {
         wrapper.passthrough(Type.VAR_INT);
         wrapper.passthrough(Type.BYTE);
         wrapper.passthrough(Type.BOOLEAN);
         if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
            int icons = (Integer)wrapper.passthrough(Type.VAR_INT);

            for(int i = 0; i < icons; ++i) {
               int markerType = (Integer)wrapper.read(Type.VAR_INT);
               wrapper.write(Type.VAR_INT, markerType < 27 ? markerType : 2);
               wrapper.passthrough(Type.BYTE);
               wrapper.passthrough(Type.BYTE);
               wrapper.passthrough(Type.BYTE);
               wrapper.passthrough(Type.OPTIONAL_COMPONENT);
            }
         }

      });
      ((Protocol1_20To1_20_2)this.protocol).registerClientbound(ClientboundPackets1_20_2.NBT_QUERY, (wrapper) -> {
         wrapper.passthrough(Type.VAR_INT);
         wrapper.write(Type.NAMED_COMPOUND_TAG, (CompoundTag)wrapper.read(Type.COMPOUND_TAG));
      });
      ((Protocol1_20To1_20_2)this.protocol).registerClientbound(ClientboundPackets1_20_2.BLOCK_ENTITY_DATA, (wrapper) -> {
         wrapper.passthrough(Type.POSITION1_14);
         wrapper.passthrough(Type.VAR_INT);
         wrapper.write(Type.NAMED_COMPOUND_TAG, this.handleBlockEntity((CompoundTag)wrapper.read(Type.COMPOUND_TAG)));
      });
      ((Protocol1_20To1_20_2)this.protocol).registerClientbound(ClientboundPackets1_20_2.CHUNK_DATA, (wrapper) -> {
         EntityTracker tracker = ((Protocol1_20To1_20_2)this.protocol).getEntityRewriter().tracker(wrapper.user());
         Type<Chunk> chunkType = new ChunkType1_20_2(tracker.currentWorldSectionHeight(), MathUtil.ceilLog2(((Protocol1_20To1_20_2)this.protocol).getMappingData().getBlockStateMappings().size()), MathUtil.ceilLog2(tracker.biomesSent()));
         Chunk chunk = (Chunk)wrapper.read(chunkType);
         Type<Chunk> newChunkType = new ChunkType1_18(tracker.currentWorldSectionHeight(), MathUtil.ceilLog2(((Protocol1_20To1_20_2)this.protocol).getMappingData().getBlockStateMappings().mappedSize()), MathUtil.ceilLog2(tracker.biomesSent()));
         wrapper.write(newChunkType, chunk);
         ChunkSection[] var6 = chunk.getSections();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            ChunkSection section = var6[var8];
            DataPalette blockPalette = section.palette(PaletteType.BLOCKS);

            for(int i = 0; i < blockPalette.size(); ++i) {
               int id = blockPalette.idByIndex(i);
               blockPalette.setIdByIndex(i, ((Protocol1_20To1_20_2)this.protocol).getMappingData().getNewBlockStateId(id));
            }
         }

         Iterator var13 = chunk.blockEntities().iterator();

         while(var13.hasNext()) {
            BlockEntity blockEntity = (BlockEntity)var13.next();
            this.handleBlockEntity(blockEntity.tag());
         }

      });
      ((Protocol1_20To1_20_2)this.protocol).registerServerbound(ServerboundPackets1_19_4.SET_BEACON_EFFECT, (wrapper) -> {
         if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
            wrapper.write(Type.VAR_INT, (Integer)wrapper.read(Type.VAR_INT) - 1);
         }

         if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
            wrapper.write(Type.VAR_INT, (Integer)wrapper.read(Type.VAR_INT) - 1);
         }

      });
      ((Protocol1_20To1_20_2)this.protocol).registerClientbound(ClientboundPackets1_20_2.WINDOW_ITEMS, new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               Item[] items = (Item[])wrapper.read(Type.ITEM1_20_2_ARRAY);
               Item[] var3 = items;
               int var4 = items.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  Item item = var3[var5];
                  BlockItemPacketRewriter1_20_2.this.handleItemToClient(item);
               }

               wrapper.write(Type.ITEM1_13_2_ARRAY, items);
               wrapper.write(Type.ITEM1_13_2, BlockItemPacketRewriter1_20_2.this.handleItemToClient((Item)wrapper.read(Type.ITEM1_20_2)));
            });
         }
      });
      ((Protocol1_20To1_20_2)this.protocol).registerClientbound(ClientboundPackets1_20_2.SET_SLOT, new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.VAR_INT);
            this.map(Type.SHORT);
            this.handler((wrapper) -> {
               wrapper.write(Type.ITEM1_13_2, BlockItemPacketRewriter1_20_2.this.handleItemToClient((Item)wrapper.read(Type.ITEM1_20_2)));
            });
         }
      });
      ((Protocol1_20To1_20_2)this.protocol).registerClientbound(ClientboundPackets1_20_2.ADVANCEMENTS, (wrapper) -> {
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
               wrapper.write(Type.ITEM1_13_2, this.handleItemToClient((Item)wrapper.read(Type.ITEM1_20_2)));
               wrapper.passthrough(Type.VAR_INT);
               requirements = (Integer)wrapper.passthrough(Type.INT);
               if ((requirements & 1) != 0) {
                  wrapper.passthrough(Type.STRING);
               }

               wrapper.passthrough(Type.FLOAT);
               wrapper.passthrough(Type.FLOAT);
            }

            wrapper.write(Type.STRING_ARRAY, new String[0]);
            requirements = (Integer)wrapper.passthrough(Type.VAR_INT);

            for(int array = 0; array < requirements; ++array) {
               wrapper.passthrough(Type.STRING_ARRAY);
            }

            wrapper.passthrough(Type.BOOLEAN);
         }

      });
      ((Protocol1_20To1_20_2)this.protocol).registerClientbound(ClientboundPackets1_20_2.ENTITY_EQUIPMENT, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               byte slot;
               do {
                  slot = (Byte)wrapper.passthrough(Type.BYTE);
                  wrapper.write(Type.ITEM1_13_2, BlockItemPacketRewriter1_20_2.this.handleItemToClient((Item)wrapper.read(Type.ITEM1_20_2)));
               } while((slot & -128) != 0);

            });
         }
      });
      ((Protocol1_20To1_20_2)this.protocol).registerServerbound(ServerboundPackets1_19_4.CLICK_WINDOW, new PacketHandlers() {
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
                  wrapper.write(Type.ITEM1_20_2, BlockItemPacketRewriter1_20_2.this.handleItemToServer((Item)wrapper.read(Type.ITEM1_13_2)));
               }

               wrapper.write(Type.ITEM1_20_2, BlockItemPacketRewriter1_20_2.this.handleItemToServer((Item)wrapper.read(Type.ITEM1_13_2)));
            });
         }
      });
      ((Protocol1_20To1_20_2)this.protocol).registerClientbound(ClientboundPackets1_20_2.TRADE_LIST, (wrapper) -> {
         wrapper.passthrough(Type.VAR_INT);
         int size = (Integer)wrapper.passthrough(Type.VAR_INT);

         for(int i = 0; i < size; ++i) {
            wrapper.write(Type.ITEM1_13_2, this.handleItemToClient((Item)wrapper.read(Type.ITEM1_20_2)));
            wrapper.write(Type.ITEM1_13_2, this.handleItemToClient((Item)wrapper.read(Type.ITEM1_20_2)));
            wrapper.write(Type.ITEM1_13_2, this.handleItemToClient((Item)wrapper.read(Type.ITEM1_20_2)));
            wrapper.passthrough(Type.BOOLEAN);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.FLOAT);
            wrapper.passthrough(Type.INT);
         }

      });
      ((Protocol1_20To1_20_2)this.protocol).registerServerbound(ServerboundPackets1_19_4.CREATIVE_INVENTORY_ACTION, new PacketHandlers() {
         public void register() {
            this.map(Type.SHORT);
            this.handler((wrapper) -> {
               wrapper.write(Type.ITEM1_20_2, BlockItemPacketRewriter1_20_2.this.handleItemToServer((Item)wrapper.read(Type.ITEM1_13_2)));
            });
         }
      });
      ((Protocol1_20To1_20_2)this.protocol).registerClientbound(ClientboundPackets1_20_2.SPAWN_PARTICLE, new PacketHandlers() {
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
               ParticleMappings mappings = Protocol1_20To1_20_2.MAPPINGS.getParticleMappings();
               if (mappings.isBlockParticle(id)) {
                  int data = (Integer)wrapper.read(Type.VAR_INT);
                  wrapper.write(Type.VAR_INT, ((Protocol1_20To1_20_2)BlockItemPacketRewriter1_20_2.this.protocol).getMappingData().getNewBlockStateId(data));
               } else if (mappings.isItemParticle(id)) {
                  wrapper.write(Type.ITEM1_13_2, BlockItemPacketRewriter1_20_2.this.handleItemToClient((Item)wrapper.read(Type.ITEM1_20_2)));
               }

            });
         }
      });
      (new RecipeRewriter1_20_2<ClientboundPackets1_20_2>(this.protocol) {
         public void handleCraftingShapeless(PacketWrapper wrapper) throws Exception {
            wrapper.passthrough(Type.STRING);
            wrapper.passthrough(Type.VAR_INT);
            this.handleIngredients(wrapper);
            Item result = (Item)wrapper.read(this.itemType());
            this.rewrite(result);
            wrapper.write(Type.ITEM1_13_2, result);
         }

         public void handleSmelting(PacketWrapper wrapper) throws Exception {
            wrapper.passthrough(Type.STRING);
            wrapper.passthrough(Type.VAR_INT);
            this.handleIngredient(wrapper);
            Item result = (Item)wrapper.read(this.itemType());
            this.rewrite(result);
            wrapper.write(Type.ITEM1_13_2, result);
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
            wrapper.write(Type.ITEM1_13_2, result);
            wrapper.passthrough(Type.BOOLEAN);
         }

         public void handleStonecutting(PacketWrapper wrapper) throws Exception {
            wrapper.passthrough(Type.STRING);
            this.handleIngredient(wrapper);
            Item result = (Item)wrapper.read(this.itemType());
            this.rewrite(result);
            wrapper.write(Type.ITEM1_13_2, result);
         }

         public void handleSmithing(PacketWrapper wrapper) throws Exception {
            this.handleIngredient(wrapper);
            this.handleIngredient(wrapper);
            Item result = (Item)wrapper.read(this.itemType());
            this.rewrite(result);
            wrapper.write(Type.ITEM1_13_2, result);
         }

         public void handleSmithingTransform(PacketWrapper wrapper) throws Exception {
            this.handleIngredient(wrapper);
            this.handleIngredient(wrapper);
            this.handleIngredient(wrapper);
            Item result = (Item)wrapper.read(this.itemType());
            this.rewrite(result);
            wrapper.write(Type.ITEM1_13_2, result);
         }

         protected void handleIngredient(PacketWrapper wrapper) throws Exception {
            Item[] items = (Item[])wrapper.read(this.itemArrayType());
            wrapper.write(Type.ITEM1_13_2_ARRAY, items);
            Item[] var3 = items;
            int var4 = items.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Item item = var3[var5];
               this.rewrite(item);
            }

         }
      }).register(ClientboundPackets1_20_2.DECLARE_RECIPES);
   }

   @Nullable
   public Item handleItemToClient(@Nullable Item item) {
      if (item == null) {
         return null;
      } else {
         if (item.tag() != null) {
            com.viaversion.viaversion.protocols.protocol1_20_2to1_20.rewriter.BlockItemPacketRewriter1_20_2.to1_20_1Effects(item);
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
            com.viaversion.viaversion.protocols.protocol1_20_2to1_20.rewriter.BlockItemPacketRewriter1_20_2.to1_20_2Effects(item);
         }

         return super.handleItemToServer(item);
      }
   }

   @Nullable
   private CompoundTag handleBlockEntity(@Nullable CompoundTag tag) {
      if (tag == null) {
         return null;
      } else {
         StringTag primaryEffect = (StringTag)tag.remove("primary_effect");
         if (primaryEffect != null) {
            String effectKey = Key.stripMinecraftNamespace(primaryEffect.getValue());
            tag.put("Primary", new IntTag(PotionEffects.keyToId(effectKey)));
         }

         StringTag secondaryEffect = (StringTag)tag.remove("secondary_effect");
         if (secondaryEffect != null) {
            String effectKey = Key.stripMinecraftNamespace(secondaryEffect.getValue());
            tag.put("Secondary", new IntTag(PotionEffects.keyToId(effectKey)));
         }

         return tag;
      }
   }
}
