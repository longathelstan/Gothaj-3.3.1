package com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.packets;

import com.viaversion.viabackwards.api.rewriters.ItemRewriter;
import com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.Protocol1_14_4To1_15;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_14;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_15;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import com.viaversion.viaversion.rewriter.BlockRewriter;
import com.viaversion.viaversion.rewriter.RecipeRewriter;

public class BlockItemPackets1_15 extends ItemRewriter<ClientboundPackets1_15, ServerboundPackets1_14, Protocol1_14_4To1_15> {
   public BlockItemPackets1_15(Protocol1_14_4To1_15 protocol) {
      super(protocol);
   }

   protected void registerPackets() {
      BlockRewriter<ClientboundPackets1_15> blockRewriter = BlockRewriter.for1_14(this.protocol);
      (new RecipeRewriter(this.protocol)).register(ClientboundPackets1_15.DECLARE_RECIPES);
      ((Protocol1_14_4To1_15)this.protocol).registerServerbound(ServerboundPackets1_14.EDIT_BOOK, (wrapper) -> {
         this.handleItemToServer((Item)wrapper.passthrough(Type.ITEM1_13_2));
      });
      this.registerSetCooldown(ClientboundPackets1_15.COOLDOWN);
      this.registerWindowItems(ClientboundPackets1_15.WINDOW_ITEMS, Type.ITEM1_13_2_SHORT_ARRAY);
      this.registerSetSlot(ClientboundPackets1_15.SET_SLOT, Type.ITEM1_13_2);
      this.registerTradeList(ClientboundPackets1_15.TRADE_LIST);
      this.registerEntityEquipment(ClientboundPackets1_15.ENTITY_EQUIPMENT, Type.ITEM1_13_2);
      this.registerAdvancements(ClientboundPackets1_15.ADVANCEMENTS, Type.ITEM1_13_2);
      this.registerClickWindow(ServerboundPackets1_14.CLICK_WINDOW, Type.ITEM1_13_2);
      this.registerCreativeInvAction(ServerboundPackets1_14.CREATIVE_INVENTORY_ACTION, Type.ITEM1_13_2);
      blockRewriter.registerAcknowledgePlayerDigging(ClientboundPackets1_15.ACKNOWLEDGE_PLAYER_DIGGING);
      blockRewriter.registerBlockAction(ClientboundPackets1_15.BLOCK_ACTION);
      blockRewriter.registerBlockChange(ClientboundPackets1_15.BLOCK_CHANGE);
      blockRewriter.registerMultiBlockChange(ClientboundPackets1_15.MULTI_BLOCK_CHANGE);
      ((Protocol1_14_4To1_15)this.protocol).registerClientbound(ClientboundPackets1_15.CHUNK_DATA, (wrapper) -> {
         Chunk chunk = (Chunk)wrapper.read(ChunkType1_15.TYPE);
         wrapper.write(ChunkType1_14.TYPE, chunk);
         int j;
         int mappedBlockStateId;
         if (chunk.isFullChunk()) {
            int[] biomeData = chunk.getBiomeData();
            int[] newBiomeData = new int[256];

            for(int ix = 0; ix < 4; ++ix) {
               for(j = 0; j < 4; ++j) {
                  mappedBlockStateId = j << 2;
                  int z = ix << 2;
                  int newIndex = z << 4 | mappedBlockStateId;
                  int oldIndex = ix << 2 | j;
                  int biome = biomeData[oldIndex];

                  for(int k = 0; k < 4; ++k) {
                     int offX = newIndex + (k << 4);

                     for(int l = 0; l < 4; ++l) {
                        newBiomeData[offX + l] = biome;
                     }
                  }
               }
            }

            chunk.setBiomeData(newBiomeData);
         }

         for(int i = 0; i < chunk.getSections().length; ++i) {
            ChunkSection section = chunk.getSections()[i];
            if (section != null) {
               DataPalette palette = section.palette(PaletteType.BLOCKS);

               for(j = 0; j < palette.size(); ++j) {
                  mappedBlockStateId = ((Protocol1_14_4To1_15)this.protocol).getMappingData().getNewBlockStateId(palette.idByIndex(j));
                  palette.setIdByIndex(j, mappedBlockStateId);
               }
            }
         }

      });
      blockRewriter.registerEffect(ClientboundPackets1_15.EFFECT, 1010, 2001);
      ((Protocol1_14_4To1_15)this.protocol).registerClientbound(ClientboundPackets1_15.SPAWN_PARTICLE, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.BOOLEAN);
            this.map(Type.DOUBLE, Type.FLOAT);
            this.map(Type.DOUBLE, Type.FLOAT);
            this.map(Type.DOUBLE, Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.INT);
            this.handler((wrapper) -> {
               int id = (Integer)wrapper.get(Type.INT, 0);
               int mappedId;
               if (id != 3 && id != 23) {
                  if (id == 32) {
                     Item item = BlockItemPackets1_15.this.handleItemToClient((Item)wrapper.read(Type.ITEM1_13_2));
                     wrapper.write(Type.ITEM1_13_2, item);
                  }
               } else {
                  mappedId = (Integer)wrapper.passthrough(Type.VAR_INT);
                  wrapper.set(Type.VAR_INT, 0, ((Protocol1_14_4To1_15)BlockItemPackets1_15.this.protocol).getMappingData().getNewBlockStateId(mappedId));
               }

               mappedId = ((Protocol1_14_4To1_15)BlockItemPackets1_15.this.protocol).getMappingData().getNewParticleId(id);
               if (id != mappedId) {
                  wrapper.set(Type.INT, 0, mappedId);
               }

            });
         }
      });
   }
}
