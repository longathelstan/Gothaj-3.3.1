package com.viaversion.viaversion.protocols.protocol1_18to1_17_1.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.blockentity.BlockEntity;
import com.viaversion.viaversion.api.minecraft.blockentity.BlockEntityImpl;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk1_18;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSectionImpl;
import com.viaversion.viaversion.api.minecraft.chunks.DataPaletteImpl;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_17;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_18;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_17_1to1_17.ClientboundPackets1_17_1;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.BlockEntityIds;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.Protocol1_18To1_17_1;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.storage.ChunkLightStorage;
import com.viaversion.viaversion.util.Key;
import com.viaversion.viaversion.util.MathUtil;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

public final class WorldPackets {
   public static void register(Protocol1_18To1_17_1 protocol) {
      protocol.registerClientbound(ClientboundPackets1_17_1.BLOCK_ENTITY_DATA, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_14);
            this.handler((wrapper) -> {
               short id = (Short)wrapper.read(Type.UNSIGNED_BYTE);
               int newId = BlockEntityIds.newId(id);
               wrapper.write(Type.VAR_INT, newId);
               WorldPackets.handleSpawners(newId, (CompoundTag)wrapper.passthrough(Type.NAMED_COMPOUND_TAG));
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_17_1.UPDATE_LIGHT, (wrapper) -> {
         int chunkX = (Integer)wrapper.passthrough(Type.VAR_INT);
         int chunkZ = (Integer)wrapper.passthrough(Type.VAR_INT);
         if (((ChunkLightStorage)wrapper.user().get(ChunkLightStorage.class)).isLoaded(chunkX, chunkZ)) {
            if (!Via.getConfig().cache1_17Light()) {
               return;
            }
         } else {
            wrapper.cancel();
         }

         boolean trustEdges = (Boolean)wrapper.passthrough(Type.BOOLEAN);
         long[] skyLightMask = (long[])wrapper.passthrough(Type.LONG_ARRAY_PRIMITIVE);
         long[] blockLightMask = (long[])wrapper.passthrough(Type.LONG_ARRAY_PRIMITIVE);
         long[] emptySkyLightMask = (long[])wrapper.passthrough(Type.LONG_ARRAY_PRIMITIVE);
         long[] emptyBlockLightMask = (long[])wrapper.passthrough(Type.LONG_ARRAY_PRIMITIVE);
         int skyLightLenght = (Integer)wrapper.passthrough(Type.VAR_INT);
         byte[][] skyLight = new byte[skyLightLenght][];

         int blockLightLength;
         for(blockLightLength = 0; blockLightLength < skyLightLenght; ++blockLightLength) {
            skyLight[blockLightLength] = (byte[])wrapper.passthrough(Type.BYTE_ARRAY_PRIMITIVE);
         }

         blockLightLength = (Integer)wrapper.passthrough(Type.VAR_INT);
         byte[][] blockLight = new byte[blockLightLength][];

         for(int i = 0; i < blockLightLength; ++i) {
            blockLight[i] = (byte[])wrapper.passthrough(Type.BYTE_ARRAY_PRIMITIVE);
         }

         ChunkLightStorage lightStorage = (ChunkLightStorage)wrapper.user().get(ChunkLightStorage.class);
         lightStorage.storeLight(chunkX, chunkZ, new ChunkLightStorage.ChunkLight(trustEdges, skyLightMask, blockLightMask, emptySkyLightMask, emptyBlockLightMask, skyLight, blockLight));
      });
      protocol.registerClientbound(ClientboundPackets1_17_1.CHUNK_DATA, (wrapper) -> {
         EntityTracker tracker = protocol.getEntityRewriter().tracker(wrapper.user());
         Chunk oldChunk = (Chunk)wrapper.read(new ChunkType1_17(tracker.currentWorldSectionHeight()));
         List<BlockEntity> blockEntities = new ArrayList(oldChunk.getBlockEntities().size());
         Iterator var5 = oldChunk.getBlockEntities().iterator();

         int biomeArrayIndex;
         while(var5.hasNext()) {
            CompoundTag tag = (CompoundTag)var5.next();
            NumberTag xTag = (NumberTag)tag.get("x");
            NumberTag yTag = (NumberTag)tag.get("y");
            NumberTag zTag = (NumberTag)tag.get("z");
            StringTag idTag = (StringTag)tag.get("id");
            if (xTag != null && yTag != null && zTag != null && idTag != null) {
               String id = idTag.getValue();
               biomeArrayIndex = protocol.getMappingData().blockEntityIds().getInt(Key.stripMinecraftNamespace(id));
               if (biomeArrayIndex == -1) {
                  Via.getPlatform().getLogger().warning("Unknown block entity: " + id);
               }

               handleSpawners(biomeArrayIndex, tag);
               byte packedXZ = (byte)((xTag.asInt() & 15) << 4 | zTag.asInt() & 15);
               blockEntities.add(new BlockEntityImpl(packedXZ, yTag.asShort(), biomeArrayIndex, tag));
            }
         }

         int[] biomeData = oldChunk.getBiomeData();
         ChunkSection[] sections = oldChunk.getSections();

         int biome;
         for(int i = 0; i < sections.length; ++i) {
            ChunkSection section = sections[i];
            DataPaletteImpl biomePalette;
            if (section == null) {
               section = new ChunkSectionImpl();
               sections[i] = (ChunkSection)section;
               ((ChunkSection)section).setNonAirBlocksCount(0);
               biomePalette = new DataPaletteImpl(4096);
               biomePalette.addId(0);
               ((ChunkSection)section).addPalette(PaletteType.BLOCKS, biomePalette);
            }

            biomePalette = new DataPaletteImpl(64);
            ((ChunkSection)section).addPalette(PaletteType.BIOMES, biomePalette);
            int offset = i * 64;
            int biomeIndex = 0;

            for(biomeArrayIndex = offset; biomeIndex < 64; ++biomeArrayIndex) {
               biome = biomeData[biomeArrayIndex];
               biomePalette.setIdAt(biomeIndex, biome != -1 ? biome : 0);
               ++biomeIndex;
            }
         }

         Chunk chunk = new Chunk1_18(oldChunk.getX(), oldChunk.getZ(), sections, oldChunk.getHeightMap(), blockEntities);
         wrapper.write(new ChunkType1_18(tracker.currentWorldSectionHeight(), MathUtil.ceilLog2(protocol.getMappingData().getBlockStateMappings().mappedSize()), MathUtil.ceilLog2(tracker.biomesSent())), chunk);
         ChunkLightStorage lightStorage = (ChunkLightStorage)wrapper.user().get(ChunkLightStorage.class);
         boolean alreadyLoaded = !lightStorage.addLoadedChunk(chunk.getX(), chunk.getZ());
         ChunkLightStorage.ChunkLight light = Via.getConfig().cache1_17Light() ? lightStorage.getLight(chunk.getX(), chunk.getZ()) : lightStorage.removeLight(chunk.getX(), chunk.getZ());
         if (light == null) {
            Via.getPlatform().getLogger().warning("No light data found for chunk at " + chunk.getX() + ", " + chunk.getZ() + ". Chunk was already loaded: " + alreadyLoaded);
            BitSet emptyLightMask = new BitSet();
            emptyLightMask.set(0, tracker.currentWorldSectionHeight() + 2);
            wrapper.write(Type.BOOLEAN, false);
            wrapper.write(Type.LONG_ARRAY_PRIMITIVE, new long[0]);
            wrapper.write(Type.LONG_ARRAY_PRIMITIVE, new long[0]);
            wrapper.write(Type.LONG_ARRAY_PRIMITIVE, emptyLightMask.toLongArray());
            wrapper.write(Type.LONG_ARRAY_PRIMITIVE, emptyLightMask.toLongArray());
            wrapper.write(Type.VAR_INT, 0);
            wrapper.write(Type.VAR_INT, 0);
         } else {
            wrapper.write(Type.BOOLEAN, light.trustEdges());
            wrapper.write(Type.LONG_ARRAY_PRIMITIVE, light.skyLightMask());
            wrapper.write(Type.LONG_ARRAY_PRIMITIVE, light.blockLightMask());
            wrapper.write(Type.LONG_ARRAY_PRIMITIVE, light.emptySkyLightMask());
            wrapper.write(Type.LONG_ARRAY_PRIMITIVE, light.emptyBlockLightMask());
            wrapper.write(Type.VAR_INT, light.skyLight().length);
            byte[][] var27 = light.skyLight();
            biomeArrayIndex = var27.length;

            byte[] blockLight;
            for(biome = 0; biome < biomeArrayIndex; ++biome) {
               blockLight = var27[biome];
               wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, blockLight);
            }

            wrapper.write(Type.VAR_INT, light.blockLight().length);
            var27 = light.blockLight();
            biomeArrayIndex = var27.length;

            for(biome = 0; biome < biomeArrayIndex; ++biome) {
               blockLight = var27[biome];
               wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, blockLight);
            }
         }

      });
      protocol.registerClientbound(ClientboundPackets1_17_1.UNLOAD_CHUNK, (wrapper) -> {
         int chunkX = (Integer)wrapper.passthrough(Type.INT);
         int chunkZ = (Integer)wrapper.passthrough(Type.INT);
         ((ChunkLightStorage)wrapper.user().get(ChunkLightStorage.class)).clear(chunkX, chunkZ);
      });
   }

   private static void handleSpawners(int typeId, CompoundTag tag) {
      if (typeId == 8) {
         Tag entity = tag.get("SpawnData");
         if (entity instanceof CompoundTag) {
            CompoundTag spawnData = new CompoundTag();
            tag.put("SpawnData", spawnData);
            spawnData.put("entity", entity);
         }
      }

   }
}
