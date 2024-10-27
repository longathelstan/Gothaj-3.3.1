package com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.packets;

import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.Protocol1_7_6_10To1_8;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.model.ParticleIndex1_7_6_10;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.storage.WorldBorderEmulator;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.Types1_7_6_10;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.chunk.BulkChunkType1_7_6;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.chunk.ChunkType1_7_6;
import com.viaversion.viarewind.utils.ChatUtil;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.ClientWorld;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.FixedByteArrayType;
import com.viaversion.viaversion.api.type.types.chunk.BulkChunkType1_8;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_8;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.util.ChatColorUtil;

public class WorldPackets {
   private static void rewriteBlockIds(Protocol1_7_6_10To1_8 protocol, Chunk chunk) {
      ChunkSection[] var2 = chunk.getSections();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ChunkSection section = var2[var4];
         if (section != null) {
            DataPalette palette = section.palette(PaletteType.BLOCKS);

            for(int i = 0; i < palette.size(); ++i) {
               palette.setIdByIndex(i, protocol.getItemRewriter().replace(palette.idByIndex(i)));
            }
         }
      }

   }

   public static void register(final Protocol1_7_6_10To1_8 protocol) {
      protocol.registerClientbound(ClientboundPackets1_8.CHUNK_DATA, (wrapper) -> {
         ClientWorld world = (ClientWorld)wrapper.user().get(ClientWorld.class);
         Chunk chunk = (Chunk)wrapper.read(ChunkType1_8.forEnvironment(world.getEnvironment()));
         rewriteBlockIds(protocol, chunk);
         wrapper.write(ChunkType1_7_6.TYPE, chunk);
      });
      protocol.registerClientbound(ClientboundPackets1_8.MULTI_BLOCK_CHANGE, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.INT);
            this.handler((wrapper) -> {
               BlockChangeRecord[] records = (BlockChangeRecord[])wrapper.read(Type.BLOCK_CHANGE_RECORD_ARRAY);
               wrapper.write(Type.SHORT, (short)records.length);
               wrapper.write(Type.INT, records.length * 4);
               BlockChangeRecord[] var3 = records;
               int var4 = records.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  BlockChangeRecord record = var3[var5];
                  wrapper.write(Type.SHORT, (short)(record.getSectionX() << 12 | record.getSectionZ() << 8 | record.getY()));
                  wrapper.write(Type.SHORT, (short)protocol.getItemRewriter().replace(record.getBlockId()));
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.BLOCK_CHANGE, new PacketHandlers() {
         protected void register() {
            this.map(Type.POSITION1_8, Types1_7_6_10.U_BYTE_POSITION);
            this.handler((wrapper) -> {
               int data = (Integer)wrapper.read(Type.VAR_INT);
               data = protocol.getItemRewriter().replace(data);
               wrapper.write(Type.VAR_INT, data >> 4);
               wrapper.write(Type.UNSIGNED_BYTE, (short)(data & 15));
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.BLOCK_ACTION, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_8, Types1_7_6_10.SHORT_POSITION);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.VAR_INT);
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.BLOCK_BREAK_ANIMATION, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.POSITION1_8, Types1_7_6_10.INT_POSITION);
            this.map(Type.BYTE);
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.MAP_BULK_CHUNK, (wrapper) -> {
         Chunk[] chunks = (Chunk[])wrapper.read(BulkChunkType1_8.TYPE);
         Chunk[] var3 = chunks;
         int var4 = chunks.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Chunk chunk = var3[var5];
            rewriteBlockIds(protocol, chunk);
         }

         wrapper.write(BulkChunkType1_7_6.TYPE, chunks);
      });
      protocol.registerClientbound(ClientboundPackets1_8.EFFECT, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.POSITION1_8, Types1_7_6_10.BYTE_POSITION);
            this.map(Type.INT);
            this.map(Type.BOOLEAN);
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.SPAWN_PARTICLE, new PacketHandlers() {
         public void register() {
            this.handler((wrapper) -> {
               int particleId = (Integer)wrapper.read(Type.INT);
               ParticleIndex1_7_6_10 particle = ParticleIndex1_7_6_10.find(particleId);
               if (particle == null) {
                  particle = ParticleIndex1_7_6_10.CRIT;
               }

               wrapper.write(Type.STRING, particle.name);
            });
            this.read(Type.BOOLEAN);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.INT);
            this.handler((wrapper) -> {
               String name = (String)wrapper.get(Type.STRING, 0);
               ParticleIndex1_7_6_10 particle = ParticleIndex1_7_6_10.find(name);
               if (particle == ParticleIndex1_7_6_10.ICON_CRACK || particle == ParticleIndex1_7_6_10.BLOCK_CRACK || particle == ParticleIndex1_7_6_10.BLOCK_DUST) {
                  int id = (Integer)wrapper.read(Type.VAR_INT);
                  int data = particle == ParticleIndex1_7_6_10.ICON_CRACK ? (Integer)wrapper.read(Type.VAR_INT) : id / 4096;
                  id %= 4096;
                  if (id >= 256 && id <= 422 || id >= 2256 && id <= 2267) {
                     particle = ParticleIndex1_7_6_10.ICON_CRACK;
                  } else {
                     if ((id < 0 || id > 164) && (id < 170 || id > 175)) {
                        wrapper.cancel();
                        return;
                     }

                     if (particle == ParticleIndex1_7_6_10.ICON_CRACK) {
                        particle = ParticleIndex1_7_6_10.BLOCK_CRACK;
                     }
                  }

                  name = particle.name + "_" + id + "_" + data;
               }

               wrapper.set(Type.STRING, 0, name);
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.UPDATE_SIGN, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_8, Types1_7_6_10.SHORT_POSITION);
            this.handler((wrapper) -> {
               for(int i = 0; i < 4; ++i) {
                  String line = (String)wrapper.read(Type.STRING);
                  line = ChatUtil.jsonToLegacy(line);
                  line = ChatUtil.removeUnusedColor(line, '0');
                  if (line.length() > 15) {
                     line = ChatColorUtil.stripColor(line);
                     if (line.length() > 15) {
                        line = line.substring(0, 15);
                     }
                  }

                  wrapper.write(Type.STRING, line);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.MAP_DATA, new PacketHandlers() {
         public void register() {
            this.handler((wrapper) -> {
               wrapper.cancel();
               int id = (Integer)wrapper.read(Type.VAR_INT);
               byte scale = (Byte)wrapper.read(Type.BYTE);
               int iconCount = (Integer)wrapper.read(Type.VAR_INT);
               byte[] icons = new byte[iconCount * 4];

               for(int ix = 0; ix < iconCount; ++ix) {
                  int directionAndType = (Byte)wrapper.read(Type.BYTE);
                  icons[ix * 4] = (byte)(directionAndType >> 4 & 15);
                  icons[ix * 4 + 1] = (Byte)wrapper.read(Type.BYTE);
                  icons[ix * 4 + 2] = (Byte)wrapper.read(Type.BYTE);
                  icons[ix * 4 + 3] = (byte)(directionAndType & 15);
               }

               short columns = (Short)wrapper.read(Type.UNSIGNED_BYTE);
               if (columns > 0) {
                  short rows = (Short)wrapper.read(Type.UNSIGNED_BYTE);
                  short x = (Short)wrapper.read(Type.UNSIGNED_BYTE);
                  short z = (Short)wrapper.read(Type.UNSIGNED_BYTE);
                  byte[] data = (byte[])wrapper.read(Type.BYTE_ARRAY_PRIMITIVE);

                  for(int column = 0; column < columns; ++column) {
                     byte[] columnData = new byte[rows + 3];
                     columnData[0] = 0;
                     columnData[1] = (byte)(x + column);
                     columnData[2] = (byte)z;

                     for(int i = 0; i < rows; ++i) {
                        columnData[i + 3] = data[column + i * columns];
                     }

                     PacketWrapper mapDataxx = PacketWrapper.create(ClientboundPackets1_8.MAP_DATA, (UserConnection)wrapper.user());
                     mapDataxx.write(Type.VAR_INT, id);
                     mapDataxx.write(Type.SHORT, (short)columnData.length);
                     mapDataxx.write(new FixedByteArrayType(columnData.length), columnData);
                     mapDataxx.send(Protocol1_7_6_10To1_8.class, true);
                  }
               }

               if (iconCount > 0) {
                  byte[] iconData = new byte[iconCount * 3 + 1];
                  iconData[0] = 1;

                  for(int ixx = 0; ixx < iconCount; ++ixx) {
                     iconData[ixx * 3 + 1] = (byte)(icons[ixx * 4] << 4 | icons[ixx * 4 + 3] & 15);
                     iconData[ixx * 3 + 2] = icons[ixx * 4 + 1];
                     iconData[ixx * 3 + 3] = icons[ixx * 4 + 2];
                  }

                  PacketWrapper mapDatax = PacketWrapper.create(ClientboundPackets1_8.MAP_DATA, (UserConnection)wrapper.user());
                  mapDatax.write(Type.VAR_INT, id);
                  mapDatax.write(Type.SHORT, (short)iconData.length);
                  mapDatax.write(new FixedByteArrayType(iconData.length), iconData);
                  mapDatax.send(Protocol1_7_6_10To1_8.class, true);
               }

               PacketWrapper mapData = PacketWrapper.create(ClientboundPackets1_8.MAP_DATA, (UserConnection)wrapper.user());
               mapData.write(Type.VAR_INT, id);
               mapData.write(Type.SHORT, Short.valueOf((short)2));
               mapData.write(new FixedByteArrayType(2), new byte[]{2, scale});
               mapData.send(Protocol1_7_6_10To1_8.class, true);
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_8.BLOCK_ENTITY_DATA, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_8, Types1_7_6_10.SHORT_POSITION);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.NAMED_COMPOUND_TAG, Types1_7_6_10.COMPRESSED_NBT);
         }
      });
      protocol.cancelClientbound(ClientboundPackets1_8.SERVER_DIFFICULTY);
      protocol.cancelClientbound(ClientboundPackets1_8.COMBAT_EVENT);
      protocol.registerClientbound(ClientboundPackets1_8.WORLD_BORDER, (ClientboundPacketType)null, (wrapper) -> {
         WorldBorderEmulator emulator = (WorldBorderEmulator)wrapper.user().get(WorldBorderEmulator.class);
         wrapper.cancel();
         int action = (Integer)wrapper.read(Type.VAR_INT);
         if (action == 0) {
            emulator.setSize((Double)wrapper.read(Type.DOUBLE));
         } else if (action == 1) {
            emulator.lerpSize((Double)wrapper.read(Type.DOUBLE), (Double)wrapper.read(Type.DOUBLE), (Long)wrapper.read(Type.VAR_LONG));
         } else if (action == 2) {
            emulator.setCenter((Double)wrapper.read(Type.DOUBLE), (Double)wrapper.read(Type.DOUBLE));
         } else if (action == 3) {
            emulator.init((Double)wrapper.read(Type.DOUBLE), (Double)wrapper.read(Type.DOUBLE), (Double)wrapper.read(Type.DOUBLE), (Double)wrapper.read(Type.DOUBLE), (Long)wrapper.read(Type.VAR_LONG));
         }

      });
   }
}
