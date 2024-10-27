package com.viaversion.viabackwards.protocol.protocol1_17_1to1_18.packets;

import com.viaversion.viabackwards.api.rewriters.ItemRewriter;
import com.viaversion.viabackwards.protocol.protocol1_17_1to1_18.Protocol1_17_1To1_18;
import com.viaversion.viabackwards.protocol.protocol1_17_1to1_18.data.BlockEntityIds;
import com.viaversion.viaversion.api.data.ParticleMappings;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.blockentity.BlockEntity;
import com.viaversion.viaversion.api.minecraft.chunks.BaseChunk;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_17;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_18;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.protocols.protocol1_17_1to1_17.ClientboundPackets1_17_1;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ServerboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.ClientboundPackets1_18;
import com.viaversion.viaversion.rewriter.RecipeRewriter;
import com.viaversion.viaversion.util.Key;
import com.viaversion.viaversion.util.MathUtil;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

public final class BlockItemPackets1_18 extends ItemRewriter<ClientboundPackets1_18, ServerboundPackets1_17, Protocol1_17_1To1_18> {
   public BlockItemPackets1_18(Protocol1_17_1To1_18 protocol) {
      super(protocol);
   }

   protected void registerPackets() {
      (new RecipeRewriter(this.protocol)).register(ClientboundPackets1_18.DECLARE_RECIPES);
      this.registerSetCooldown(ClientboundPackets1_18.COOLDOWN);
      this.registerWindowItems1_17_1(ClientboundPackets1_18.WINDOW_ITEMS);
      this.registerSetSlot1_17_1(ClientboundPackets1_18.SET_SLOT);
      this.registerEntityEquipmentArray(ClientboundPackets1_18.ENTITY_EQUIPMENT);
      this.registerTradeList(ClientboundPackets1_18.TRADE_LIST);
      this.registerAdvancements(ClientboundPackets1_18.ADVANCEMENTS, Type.ITEM1_13_2);
      this.registerClickWindow1_17_1(ServerboundPackets1_17.CLICK_WINDOW);
      ((Protocol1_17_1To1_18)this.protocol).registerClientbound(ClientboundPackets1_18.EFFECT, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.POSITION1_14);
            this.map(Type.INT);
            this.handler((wrapper) -> {
               int id = (Integer)wrapper.get(Type.INT, 0);
               int data = (Integer)wrapper.get(Type.INT, 1);
               if (id == 1010) {
                  wrapper.set(Type.INT, 1, ((Protocol1_17_1To1_18)BlockItemPackets1_18.this.protocol).getMappingData().getNewItemId(data));
               }

            });
         }
      });
      this.registerCreativeInvAction(ServerboundPackets1_17.CREATIVE_INVENTORY_ACTION, Type.ITEM1_13_2);
      ((Protocol1_17_1To1_18)this.protocol).registerClientbound(ClientboundPackets1_18.SPAWN_PARTICLE, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
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
               int id = (Integer)wrapper.get(Type.INT, 0);
               if (id == 3) {
                  int blockState = (Integer)wrapper.read(Type.VAR_INT);
                  if (blockState == 7786) {
                     wrapper.set(Type.INT, 0, 3);
                  } else {
                     wrapper.set(Type.INT, 0, 2);
                  }

               } else {
                  ParticleMappings mappings = ((Protocol1_17_1To1_18)BlockItemPackets1_18.this.protocol).getMappingData().getParticleMappings();
                  int newId;
                  if (mappings.isBlockParticle(id)) {
                     newId = (Integer)wrapper.passthrough(Type.VAR_INT);
                     wrapper.set(Type.VAR_INT, 0, ((Protocol1_17_1To1_18)BlockItemPackets1_18.this.protocol).getMappingData().getNewBlockStateId(newId));
                  } else if (mappings.isItemParticle(id)) {
                     BlockItemPackets1_18.this.handleItemToClient((Item)wrapper.passthrough(Type.ITEM1_13_2));
                  }

                  newId = ((Protocol1_17_1To1_18)BlockItemPackets1_18.this.protocol).getMappingData().getNewParticleId(id);
                  if (newId != id) {
                     wrapper.set(Type.INT, 0, newId);
                  }

               }
            });
         }
      });
      ((Protocol1_17_1To1_18)this.protocol).registerClientbound(ClientboundPackets1_18.BLOCK_ENTITY_DATA, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_14);
            this.handler((wrapper) -> {
               int id = (Integer)wrapper.read(Type.VAR_INT);
               CompoundTag tag = (CompoundTag)wrapper.read(Type.NAMED_COMPOUND_TAG);
               int mappedId = BlockEntityIds.mappedId(id);
               if (mappedId == -1) {
                  wrapper.cancel();
               } else {
                  String identifier = (String)((Protocol1_17_1To1_18)BlockItemPackets1_18.this.protocol).getMappingData().blockEntities().get(id);
                  if (identifier == null) {
                     wrapper.cancel();
                  } else {
                     CompoundTag newTag = tag == null ? new CompoundTag() : tag;
                     Position pos = (Position)wrapper.get(Type.POSITION1_14, 0);
                     newTag.put("id", new StringTag(Key.namespaced(identifier)));
                     newTag.put("x", new IntTag(pos.x()));
                     newTag.put("y", new IntTag(pos.y()));
                     newTag.put("z", new IntTag(pos.z()));
                     BlockItemPackets1_18.this.handleSpawner(id, newTag);
                     wrapper.write(Type.UNSIGNED_BYTE, (short)mappedId);
                     wrapper.write(Type.NAMED_COMPOUND_TAG, newTag);
                  }
               }
            });
         }
      });
      ((Protocol1_17_1To1_18)this.protocol).registerClientbound(ClientboundPackets1_18.CHUNK_DATA, (wrapper) -> {
         EntityTracker tracker = ((Protocol1_17_1To1_18)this.protocol).getEntityRewriter().tracker(wrapper.user());
         ChunkType1_18 chunkType = new ChunkType1_18(tracker.currentWorldSectionHeight(), MathUtil.ceilLog2(((Protocol1_17_1To1_18)this.protocol).getMappingData().getBlockStateMappings().mappedSize()), MathUtil.ceilLog2(tracker.biomesSent()));
         Chunk oldChunk = (Chunk)wrapper.read(chunkType);
         ChunkSection[] sections = oldChunk.getSections();
         BitSet mask = new BitSet(oldChunk.getSections().length);
         int[] biomeData = new int[sections.length * 64];
         int biomeIndex = 0;

         int skyLightLength;
         for(int j = 0; j < sections.length; ++j) {
            ChunkSection section = sections[j];
            DataPalette biomePalette = section.palette(PaletteType.BIOMES);

            for(skyLightLength = 0; skyLightLength < 64; ++skyLightLength) {
               biomeData[biomeIndex++] = biomePalette.idAt(skyLightLength);
            }

            if (section.getNonAirBlocksCount() == 0) {
               sections[j] = null;
            } else {
               mask.set(j);
            }
         }

         List<CompoundTag> blockEntityTags = new ArrayList(oldChunk.blockEntities().size());
         Iterator var16 = oldChunk.blockEntities().iterator();

         while(var16.hasNext()) {
            BlockEntity blockEntity = (BlockEntity)var16.next();
            String id = (String)((Protocol1_17_1To1_18)this.protocol).getMappingData().blockEntities().get(blockEntity.typeId());
            if (id != null) {
               CompoundTag tag;
               if (blockEntity.tag() != null) {
                  tag = blockEntity.tag();
                  this.handleSpawner(blockEntity.typeId(), tag);
               } else {
                  tag = new CompoundTag();
               }

               blockEntityTags.add(tag);
               tag.put("x", new IntTag((oldChunk.getX() << 4) + blockEntity.sectionX()));
               tag.put("y", new IntTag(blockEntity.y()));
               tag.put("z", new IntTag((oldChunk.getZ() << 4) + blockEntity.sectionZ()));
               tag.put("id", new StringTag(Key.namespaced(id)));
            }
         }

         Chunk chunk = new BaseChunk(oldChunk.getX(), oldChunk.getZ(), true, false, mask, oldChunk.getSections(), biomeData, oldChunk.getHeightMap(), blockEntityTags);
         wrapper.write(new ChunkType1_17(tracker.currentWorldSectionHeight()), chunk);
         PacketWrapper lightPacket = wrapper.create(ClientboundPackets1_17_1.UPDATE_LIGHT);
         lightPacket.write(Type.VAR_INT, chunk.getX());
         lightPacket.write(Type.VAR_INT, chunk.getZ());
         lightPacket.write(Type.BOOLEAN, (Boolean)wrapper.read(Type.BOOLEAN));
         lightPacket.write(Type.LONG_ARRAY_PRIMITIVE, (long[])wrapper.read(Type.LONG_ARRAY_PRIMITIVE));
         lightPacket.write(Type.LONG_ARRAY_PRIMITIVE, (long[])wrapper.read(Type.LONG_ARRAY_PRIMITIVE));
         lightPacket.write(Type.LONG_ARRAY_PRIMITIVE, (long[])wrapper.read(Type.LONG_ARRAY_PRIMITIVE));
         lightPacket.write(Type.LONG_ARRAY_PRIMITIVE, (long[])wrapper.read(Type.LONG_ARRAY_PRIMITIVE));
         skyLightLength = (Integer)wrapper.read(Type.VAR_INT);
         lightPacket.write(Type.VAR_INT, skyLightLength);

         int blockLightLength;
         for(blockLightLength = 0; blockLightLength < skyLightLength; ++blockLightLength) {
            lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, (byte[])wrapper.read(Type.BYTE_ARRAY_PRIMITIVE));
         }

         blockLightLength = (Integer)wrapper.read(Type.VAR_INT);
         lightPacket.write(Type.VAR_INT, blockLightLength);

         for(int i = 0; i < blockLightLength; ++i) {
            lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, (byte[])wrapper.read(Type.BYTE_ARRAY_PRIMITIVE));
         }

         lightPacket.send(Protocol1_17_1To1_18.class);
      });
      ((Protocol1_17_1To1_18)this.protocol).cancelClientbound(ClientboundPackets1_18.SET_SIMULATION_DISTANCE);
   }

   private void handleSpawner(int typeId, CompoundTag tag) {
      if (typeId == 8) {
         CompoundTag spawnData = (CompoundTag)tag.get("SpawnData");
         CompoundTag entity;
         if (spawnData != null && (entity = (CompoundTag)spawnData.get("entity")) != null) {
            tag.put("SpawnData", entity);
         }
      }

   }
}
