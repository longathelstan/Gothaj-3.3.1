package com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockFace;
import com.viaversion.viaversion.api.minecraft.ClientWorld;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSectionLight;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.NibbleArray;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_13;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_14;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongArrayTag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.storage.EntityTracker1_14;
import com.viaversion.viaversion.rewriter.BlockRewriter;
import com.viaversion.viaversion.util.CompactArrayUtil;
import java.util.Arrays;

public class WorldPackets {
   public static final int SERVERSIDE_VIEW_DISTANCE = 64;
   private static final byte[] FULL_LIGHT = new byte[2048];
   public static int air;
   public static int voidAir;
   public static int caveAir;

   public static void register(final Protocol1_14To1_13_2 protocol) {
      BlockRewriter<ClientboundPackets1_13> blockRewriter = BlockRewriter.for1_14(protocol);
      protocol.registerClientbound(ClientboundPackets1_13.BLOCK_BREAK_ANIMATION, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.POSITION1_8, Type.POSITION1_14);
            this.map(Type.BYTE);
         }
      });
      protocol.registerClientbound(ClientboundPackets1_13.BLOCK_ENTITY_DATA, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_8, Type.POSITION1_14);
         }
      });
      protocol.registerClientbound(ClientboundPackets1_13.BLOCK_ACTION, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_8, Type.POSITION1_14);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               wrapper.set(Type.VAR_INT, 0, protocol.getMappingData().getNewBlockId((Integer)wrapper.get(Type.VAR_INT, 0)));
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_13.BLOCK_CHANGE, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_8, Type.POSITION1_14);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               int id = (Integer)wrapper.get(Type.VAR_INT, 0);
               wrapper.set(Type.VAR_INT, 0, protocol.getMappingData().getNewBlockStateId(id));
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_13.SERVER_DIFFICULTY, new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.handler((wrapper) -> {
               wrapper.write(Type.BOOLEAN, false);
            });
         }
      });
      blockRewriter.registerMultiBlockChange(ClientboundPackets1_13.MULTI_BLOCK_CHANGE);
      protocol.registerClientbound(ClientboundPackets1_13.EXPLOSION, new PacketHandlers() {
         public void register() {
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.handler((wrapper) -> {
               for(int i = 0; i < 3; ++i) {
                  float coord = (Float)wrapper.get(Type.FLOAT, i);
                  if (coord < 0.0F) {
                     coord = (float)((int)coord);
                     wrapper.set(Type.FLOAT, i, coord);
                  }
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_13.CHUNK_DATA, (wrapper) -> {
         ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
         Chunk chunk = (Chunk)wrapper.read(ChunkType1_13.forEnvironment(clientWorld.getEnvironment()));
         wrapper.write(ChunkType1_14.TYPE, chunk);
         int[] motionBlocking = new int[256];
         int[] worldSurface = new int[256];

         int nonAirBlockCount;
         int diffX;
         int idx;
         int xz;
         int y;
         for(int s = 0; s < chunk.getSections().length; ++s) {
            ChunkSection sectionxx = chunk.getSections()[s];
            if (sectionxx != null) {
               DataPalette blocks = sectionxx.palette(PaletteType.BLOCKS);
               boolean hasBlock = false;

               for(nonAirBlockCount = 0; nonAirBlockCount < blocks.size(); ++nonAirBlockCount) {
                  diffX = blocks.idByIndex(nonAirBlockCount);
                  idx = protocol.getMappingData().getNewBlockStateId(diffX);
                  if (!hasBlock && idx != air && idx != voidAir && idx != caveAir) {
                     hasBlock = true;
                  }

                  blocks.setIdByIndex(nonAirBlockCount, idx);
               }

               if (!hasBlock) {
                  sectionxx.setNonAirBlocksCount(0);
               } else {
                  nonAirBlockCount = 0;
                  diffX = s << 4;

                  for(idx = 0; idx < 4096; ++idx) {
                     int id = blocks.idAt(idx);
                     if (id != air && id != voidAir && id != caveAir) {
                        ++nonAirBlockCount;
                        xz = idx & 255;
                        y = ChunkSection.yFromIndex(idx);
                        worldSurface[xz] = diffX + y + 1;
                        if (protocol.getMappingData().getMotionBlocking().contains(id)) {
                           motionBlocking[xz] = diffX + y + 1;
                        }

                        if (Via.getConfig().isNonFullBlockLightFix() && protocol.getMappingData().getNonFullBlocks().contains(id)) {
                           int x = ChunkSection.xFromIndex(idx);
                           int z = ChunkSection.zFromIndex(idx);
                           setNonFullLight(chunk, sectionxx, s, x, y, z);
                        }
                     }
                  }

                  sectionxx.setNonAirBlocksCount(nonAirBlockCount);
               }
            }
         }

         CompoundTag heightMap = new CompoundTag();
         heightMap.put("MOTION_BLOCKING", new LongArrayTag(encodeHeightMap(motionBlocking)));
         heightMap.put("WORLD_SURFACE", new LongArrayTag(encodeHeightMap(worldSurface)));
         chunk.setHeightMap(heightMap);
         PacketWrapper lightPacket = wrapper.create(ClientboundPackets1_14.UPDATE_LIGHT);
         lightPacket.write(Type.VAR_INT, chunk.getX());
         lightPacket.write(Type.VAR_INT, chunk.getZ());
         int skyLightMask = chunk.isFullChunk() ? 262143 : 0;
         int blockLightMask = 0;

         for(nonAirBlockCount = 0; nonAirBlockCount < chunk.getSections().length; ++nonAirBlockCount) {
            ChunkSection sec = chunk.getSections()[nonAirBlockCount];
            if (sec != null) {
               if (!chunk.isFullChunk() && sec.getLight().hasSkyLight()) {
                  skyLightMask |= 1 << nonAirBlockCount + 1;
               }

               blockLightMask |= 1 << nonAirBlockCount + 1;
            }
         }

         lightPacket.write(Type.VAR_INT, skyLightMask);
         lightPacket.write(Type.VAR_INT, blockLightMask);
         lightPacket.write(Type.VAR_INT, 0);
         lightPacket.write(Type.VAR_INT, 0);
         if (chunk.isFullChunk()) {
            lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, FULL_LIGHT);
         }

         ChunkSection[] var23 = chunk.getSections();
         diffX = var23.length;

         ChunkSection section;
         for(idx = 0; idx < diffX; ++idx) {
            section = var23[idx];
            if (section != null && section.getLight().hasSkyLight()) {
               lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, section.getLight().getSkyLight());
            } else if (chunk.isFullChunk()) {
               lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, FULL_LIGHT);
            }
         }

         if (chunk.isFullChunk()) {
            lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, FULL_LIGHT);
         }

         var23 = chunk.getSections();
         diffX = var23.length;

         for(idx = 0; idx < diffX; ++idx) {
            section = var23[idx];
            if (section != null) {
               lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, section.getLight().getBlockLight());
            }
         }

         EntityTracker1_14 entityTracker = (EntityTracker1_14)wrapper.user().getEntityTracker(Protocol1_14To1_13_2.class);
         diffX = Math.abs(entityTracker.getChunkCenterX() - chunk.getX());
         idx = Math.abs(entityTracker.getChunkCenterZ() - chunk.getZ());
         if (entityTracker.isForceSendCenterChunk() || diffX >= 64 || idx >= 64) {
            PacketWrapper fakePosLook = wrapper.create(ClientboundPackets1_14.UPDATE_VIEW_POSITION);
            fakePosLook.write(Type.VAR_INT, chunk.getX());
            fakePosLook.write(Type.VAR_INT, chunk.getZ());
            fakePosLook.send(Protocol1_14To1_13_2.class);
            entityTracker.setChunkCenterX(chunk.getX());
            entityTracker.setChunkCenterZ(chunk.getZ());
         }

         lightPacket.send(Protocol1_14To1_13_2.class);
         ChunkSection[] var27 = chunk.getSections();
         xz = var27.length;

         for(y = 0; y < xz; ++y) {
            ChunkSection sectionx = var27[y];
            if (sectionx != null) {
               sectionx.setLight((ChunkSectionLight)null);
            }
         }

      });
      protocol.registerClientbound(ClientboundPackets1_13.EFFECT, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.POSITION1_8, Type.POSITION1_14);
            this.map(Type.INT);
            this.handler((wrapper) -> {
               int id = (Integer)wrapper.get(Type.INT, 0);
               int data = (Integer)wrapper.get(Type.INT, 1);
               if (id == 1010) {
                  wrapper.set(Type.INT, 1, protocol.getMappingData().getNewItemId(data));
               } else if (id == 2001) {
                  wrapper.set(Type.INT, 1, protocol.getMappingData().getNewBlockStateId(data));
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_13.MAP_DATA, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.BYTE);
            this.map(Type.BOOLEAN);
            this.handler((wrapper) -> {
               wrapper.write(Type.BOOLEAN, false);
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_13.RESPAWN, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.handler((wrapper) -> {
               ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
               int dimensionId = (Integer)wrapper.get(Type.INT, 0);
               clientWorld.setEnvironment(dimensionId);
               EntityTracker1_14 entityTracker = (EntityTracker1_14)wrapper.user().getEntityTracker(Protocol1_14To1_13_2.class);
               entityTracker.setForceSendCenterChunk(true);
            });
            this.handler((wrapper) -> {
               short difficulty = (Short)wrapper.read(Type.UNSIGNED_BYTE);
               PacketWrapper difficultyPacket = wrapper.create(ClientboundPackets1_14.SERVER_DIFFICULTY);
               difficultyPacket.write(Type.UNSIGNED_BYTE, difficulty);
               difficultyPacket.write(Type.BOOLEAN, false);
               difficultyPacket.scheduleSend(protocol.getClass());
            });
            this.handler((wrapper) -> {
               wrapper.send(Protocol1_14To1_13_2.class);
               wrapper.cancel();
               WorldPackets.sendViewDistancePacket(wrapper.user());
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_13.SPAWN_POSITION, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_8, Type.POSITION1_14);
         }
      });
   }

   static void sendViewDistancePacket(UserConnection connection) throws Exception {
      PacketWrapper setViewDistance = PacketWrapper.create(ClientboundPackets1_14.UPDATE_VIEW_DISTANCE, (UserConnection)connection);
      setViewDistance.write(Type.VAR_INT, 64);
      setViewDistance.send(Protocol1_14To1_13_2.class);
   }

   private static long[] encodeHeightMap(int[] heightMap) {
      return CompactArrayUtil.createCompactArray(9, heightMap.length, (i) -> {
         return (long)heightMap[i];
      });
   }

   private static void setNonFullLight(Chunk chunk, ChunkSection section, int ySection, int x, int y, int z) {
      int skyLight = 0;
      int blockLight = 0;
      BlockFace[] var8 = BlockFace.values();
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         BlockFace blockFace = var8[var10];
         NibbleArray skyLightArray = section.getLight().getSkyLightNibbleArray();
         NibbleArray blockLightArray = section.getLight().getBlockLightNibbleArray();
         int neighbourX = x + blockFace.modX();
         int neighbourY = y + blockFace.modY();
         int neighbourZ = z + blockFace.modZ();
         if (blockFace.modX() != 0) {
            if (neighbourX == 16 || neighbourX == -1) {
               continue;
            }
         } else if (blockFace.modY() != 0) {
            if (neighbourY == 16 || neighbourY == -1) {
               if (neighbourY == 16) {
                  ++ySection;
                  neighbourY = 0;
               } else {
                  --ySection;
                  neighbourY = 15;
               }

               if (ySection == chunk.getSections().length || ySection == -1) {
                  continue;
               }

               ChunkSection newSection = chunk.getSections()[ySection];
               if (newSection == null) {
                  continue;
               }

               skyLightArray = newSection.getLight().getSkyLightNibbleArray();
               blockLightArray = newSection.getLight().getBlockLightNibbleArray();
            }
         } else if (blockFace.modZ() != 0 && (neighbourZ == 16 || neighbourZ == -1)) {
            continue;
         }

         byte neighbourSkyLight;
         if (blockLightArray != null && blockLight != 15) {
            neighbourSkyLight = blockLightArray.get(neighbourX, neighbourY, neighbourZ);
            if (neighbourSkyLight == 15) {
               blockLight = 14;
            } else if (neighbourSkyLight > blockLight) {
               blockLight = neighbourSkyLight - 1;
            }
         }

         if (skyLightArray != null && skyLight != 15) {
            neighbourSkyLight = skyLightArray.get(neighbourX, neighbourY, neighbourZ);
            if (neighbourSkyLight == 15) {
               if (blockFace.modY() == 1) {
                  skyLight = 15;
               } else {
                  skyLight = 14;
               }
            } else if (neighbourSkyLight > skyLight) {
               skyLight = neighbourSkyLight - 1;
            }
         }
      }

      if (skyLight != 0) {
         if (!section.getLight().hasSkyLight()) {
            byte[] newSkyLight = new byte[2028];
            section.getLight().setSkyLight(newSkyLight);
         }

         section.getLight().getSkyLightNibbleArray().set(x, y, z, skyLight);
      }

      if (blockLight != 0) {
         section.getLight().getBlockLightNibbleArray().set(x, y, z, blockLight);
      }

   }

   private static long getChunkIndex(int x, int z) {
      return ((long)x & 67108863L) << 38 | (long)z & 67108863L;
   }

   static {
      Arrays.fill(FULL_LIGHT, (byte)-1);
   }
}
