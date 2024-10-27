package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.ClientWorld;
import com.viaversion.viaversion.api.minecraft.Particle;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_13;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_9_3;
import com.viaversion.viaversion.libs.fastutil.ints.IntOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.NamedSoundRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.ParticleRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.BlockEntityProvider;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.PaintingProvider;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.BlockStorage;
import com.viaversion.viaversion.util.Key;
import java.util.Iterator;
import java.util.Optional;

public class WorldPackets {
   private static final IntSet VALID_BIOMES = new IntOpenHashSet(70, 0.99F);

   public static void register(Protocol1_13To1_12_2 protocol) {
      protocol.registerClientbound(ClientboundPackets1_12_1.SPAWN_PAINTING, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.UUID);
            this.handler((wrapper) -> {
               PaintingProvider provider = (PaintingProvider)Via.getManager().getProviders().get(PaintingProvider.class);
               String motive = (String)wrapper.read(Type.STRING);
               Optional<Integer> id = provider.getIntByIdentifier(motive);
               if (!id.isPresent() && (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug())) {
                  Via.getPlatform().getLogger().warning("Could not find painting motive: " + motive + " falling back to default (0)");
               }

               wrapper.write(Type.VAR_INT, id.orElse(0));
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_12_1.BLOCK_ENTITY_DATA, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_8);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.NAMED_COMPOUND_TAG);
            this.handler((wrapper) -> {
               Position position = (Position)wrapper.get(Type.POSITION1_8, 0);
               short action = (Short)wrapper.get(Type.UNSIGNED_BYTE, 0);
               CompoundTag tag = (CompoundTag)wrapper.get(Type.NAMED_COMPOUND_TAG, 0);
               BlockEntityProvider provider = (BlockEntityProvider)Via.getManager().getProviders().get(BlockEntityProvider.class);
               int newId = provider.transform(wrapper.user(), position, tag, true);
               if (newId != -1) {
                  BlockStorage storage = (BlockStorage)wrapper.user().get(BlockStorage.class);
                  BlockStorage.ReplacementData replacementData = storage.get(position);
                  if (replacementData != null) {
                     replacementData.setReplacement(newId);
                  }
               }

               if (action == 5) {
                  wrapper.cancel();
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_12_1.BLOCK_ACTION, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_8);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               Position pos = (Position)wrapper.get(Type.POSITION1_8, 0);
               short action = (Short)wrapper.get(Type.UNSIGNED_BYTE, 0);
               short param = (Short)wrapper.get(Type.UNSIGNED_BYTE, 1);
               int blockId = (Integer)wrapper.get(Type.VAR_INT, 0);
               if (blockId == 25) {
                  blockId = 73;
               } else if (blockId == 33) {
                  blockId = 99;
               } else if (blockId == 29) {
                  blockId = 92;
               } else if (blockId == 54) {
                  blockId = 142;
               } else if (blockId == 146) {
                  blockId = 305;
               } else if (blockId == 130) {
                  blockId = 249;
               } else if (blockId == 138) {
                  blockId = 257;
               } else if (blockId == 52) {
                  blockId = 140;
               } else if (blockId == 209) {
                  blockId = 472;
               } else if (blockId >= 219 && blockId <= 234) {
                  blockId = blockId - 219 + 483;
               }

               if (blockId == 73) {
                  PacketWrapper blockChange = wrapper.create(ClientboundPackets1_13.BLOCK_CHANGE);
                  blockChange.write(Type.POSITION1_8, pos);
                  blockChange.write(Type.VAR_INT, 249 + action * 24 * 2 + param * 2);
                  blockChange.send(Protocol1_13To1_12_2.class);
               }

               wrapper.set(Type.VAR_INT, 0, blockId);
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_12_1.BLOCK_CHANGE, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_8);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               Position position = (Position)wrapper.get(Type.POSITION1_8, 0);
               int newId = WorldPackets.toNewId((Integer)wrapper.get(Type.VAR_INT, 0));
               UserConnection userConnection = wrapper.user();
               if (Via.getConfig().isServersideBlockConnections()) {
                  newId = ConnectionData.connect(userConnection, position, newId);
                  ConnectionData.updateBlockStorage(userConnection, position.x(), position.y(), position.z(), newId);
               }

               wrapper.set(Type.VAR_INT, 0, WorldPackets.checkStorage(wrapper.user(), position, newId));
               if (Via.getConfig().isServersideBlockConnections()) {
                  wrapper.send(Protocol1_13To1_12_2.class);
                  wrapper.cancel();
                  ConnectionData.update(userConnection, position);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_12_1.MULTI_BLOCK_CHANGE, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.INT);
            this.map(Type.BLOCK_CHANGE_RECORD_ARRAY);
            this.handler((wrapper) -> {
               int chunkX = (Integer)wrapper.get(Type.INT, 0);
               int chunkZ = (Integer)wrapper.get(Type.INT, 1);
               UserConnection userConnection = wrapper.user();
               BlockChangeRecord[] records = (BlockChangeRecord[])wrapper.get(Type.BLOCK_CHANGE_RECORD_ARRAY, 0);
               BlockChangeRecord[] var5 = records;
               int var6 = records.length;

               int var7;
               BlockChangeRecord record;
               int blockState;
               Position positionx;
               for(var7 = 0; var7 < var6; ++var7) {
                  record = var5[var7];
                  blockState = WorldPackets.toNewId(record.getBlockId());
                  positionx = new Position(record.getSectionX() + (chunkX << 4), record.getY(), record.getSectionZ() + (chunkZ << 4));
                  record.setBlockId(WorldPackets.checkStorage(wrapper.user(), positionx, blockState));
                  if (Via.getConfig().isServersideBlockConnections()) {
                     ConnectionData.updateBlockStorage(userConnection, positionx.x(), positionx.y(), positionx.z(), blockState);
                  }
               }

               if (Via.getConfig().isServersideBlockConnections()) {
                  var5 = records;
                  var6 = records.length;

                  for(var7 = 0; var7 < var6; ++var7) {
                     record = var5[var7];
                     blockState = record.getBlockId();
                     positionx = new Position(record.getSectionX() + chunkX * 16, record.getY(), record.getSectionZ() + chunkZ * 16);
                     ConnectionHandler handler = ConnectionData.getConnectionHandler(blockState);
                     if (handler != null) {
                        blockState = handler.connect(userConnection, positionx, blockState);
                        record.setBlockId(blockState);
                        ConnectionData.updateBlockStorage(userConnection, positionx.x(), positionx.y(), positionx.z(), blockState);
                     }
                  }

                  wrapper.send(Protocol1_13To1_12_2.class);
                  wrapper.cancel();
                  var5 = records;
                  var6 = records.length;

                  for(var7 = 0; var7 < var6; ++var7) {
                     record = var5[var7];
                     Position position = new Position(record.getSectionX() + chunkX * 16, record.getY(), record.getSectionZ() + chunkZ * 16);
                     ConnectionData.update(userConnection, position);
                  }
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_12_1.EXPLOSION, new PacketHandlers() {
         public void register() {
            if (Via.getConfig().isServersideBlockConnections()) {
               this.map(Type.FLOAT);
               this.map(Type.FLOAT);
               this.map(Type.FLOAT);
               this.map(Type.FLOAT);
               this.map(Type.INT);
               this.handler((wrapper) -> {
                  UserConnection userConnection = wrapper.user();
                  int x = (int)Math.floor((double)(Float)wrapper.get(Type.FLOAT, 0));
                  int y = (int)Math.floor((double)(Float)wrapper.get(Type.FLOAT, 1));
                  int z = (int)Math.floor((double)(Float)wrapper.get(Type.FLOAT, 2));
                  int recordCount = (Integer)wrapper.get(Type.INT, 0);
                  Position[] records = new Position[recordCount];

                  int i;
                  for(i = 0; i < recordCount; ++i) {
                     Position position = new Position(x + (Byte)wrapper.passthrough(Type.BYTE), (short)(y + (Byte)wrapper.passthrough(Type.BYTE)), z + (Byte)wrapper.passthrough(Type.BYTE));
                     records[i] = position;
                     ConnectionData.updateBlockStorage(userConnection, position.x(), position.y(), position.z(), 0);
                  }

                  wrapper.send(Protocol1_13To1_12_2.class);
                  wrapper.cancel();

                  for(i = 0; i < recordCount; ++i) {
                     ConnectionData.update(userConnection, records[i]);
                  }

               });
            }
         }
      });
      protocol.registerClientbound(ClientboundPackets1_12_1.UNLOAD_CHUNK, new PacketHandlers() {
         public void register() {
            if (Via.getConfig().isServersideBlockConnections()) {
               this.handler((wrapper) -> {
                  int x = (Integer)wrapper.passthrough(Type.INT);
                  int z = (Integer)wrapper.passthrough(Type.INT);
                  ConnectionData.blockConnectionProvider.unloadChunk(wrapper.user(), x, z);
               });
            }

         }
      });
      protocol.registerClientbound(ClientboundPackets1_12_1.NAMED_SOUND, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.handler((wrapper) -> {
               String sound = Key.stripMinecraftNamespace((String)wrapper.get(Type.STRING, 0));
               String newSoundId = NamedSoundRewriter.getNewId(sound);
               wrapper.set(Type.STRING, 0, newSoundId);
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_12_1.CHUNK_DATA, (wrapper) -> {
         ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
         BlockStorage storage = (BlockStorage)wrapper.user().get(BlockStorage.class);
         ChunkType1_9_3 type = ChunkType1_9_3.forEnvironment(clientWorld.getEnvironment());
         ChunkType1_13 type1_13 = ChunkType1_13.forEnvironment(clientWorld.getEnvironment());
         Chunk chunk = (Chunk)wrapper.read(type);
         wrapper.write(type1_13, chunk);

         int s;
         int i;
         int idx;
         int idxx;
         int globalX;
         for(s = 0; s < chunk.getSections().length; ++s) {
            ChunkSection sectionx = chunk.getSections()[s];
            if (sectionx != null) {
               DataPalette blocks = sectionx.palette(PaletteType.BLOCKS);

               for(i = 0; i < blocks.size(); ++i) {
                  idx = blocks.idByIndex(i);
                  idxx = toNewId(idx);
                  blocks.setIdByIndex(i, idxx);
               }

               boolean willSave;
               label180: {
                  if (chunk.isFullChunk()) {
                     willSave = false;

                     for(idx = 0; idx < blocks.size(); ++idx) {
                        if (storage.isWelcome(blocks.idByIndex(idx))) {
                           willSave = true;
                           break;
                        }
                     }

                     if (!willSave) {
                        break label180;
                     }
                  }

                  for(i = 0; i < 4096; ++i) {
                     idx = blocks.idAt(i);
                     Position position = new Position(ChunkSection.xFromIndex(i) + (chunk.getX() << 4), ChunkSection.yFromIndex(i) + (s << 4), ChunkSection.zFromIndex(i) + (chunk.getZ() << 4));
                     if (storage.isWelcome(idx)) {
                        storage.store(position, idx);
                     } else if (!chunk.isFullChunk()) {
                        storage.remove(position);
                     }
                  }
               }

               if (Via.getConfig().isServersideBlockConnections() && ConnectionData.needStoreBlocks()) {
                  if (!chunk.isFullChunk()) {
                     ConnectionData.blockConnectionProvider.unloadChunkSection(wrapper.user(), chunk.getX(), s, chunk.getZ());
                  }

                  willSave = false;

                  for(idx = 0; idx < blocks.size(); ++idx) {
                     if (ConnectionData.isWelcome(blocks.idByIndex(idx))) {
                        willSave = true;
                        break;
                     }
                  }

                  if (willSave) {
                     for(idx = 0; idx < 4096; ++idx) {
                        idxx = blocks.idAt(idx);
                        if (ConnectionData.isWelcome(idxx)) {
                           globalX = ChunkSection.xFromIndex(idx) + (chunk.getX() << 4);
                           int globalY = ChunkSection.yFromIndex(idx) + (s << 4);
                           int globalZ = ChunkSection.zFromIndex(idx) + (chunk.getZ() << 4);
                           ConnectionData.blockConnectionProvider.storeBlock(wrapper.user(), globalX, globalY, globalZ, idxx);
                        }
                     }
                  }
               }
            }
         }

         if (chunk.isBiomeData()) {
            s = Integer.MIN_VALUE;

            for(int ix = 0; ix < 256; ++ix) {
               int biome = chunk.getBiomeData()[ix];
               if (!VALID_BIOMES.contains(biome)) {
                  if (biome != 255 && s != biome) {
                     if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                        Via.getPlatform().getLogger().warning("Received invalid biome id " + biome);
                     }

                     s = biome;
                  }

                  chunk.getBiomeData()[ix] = 1;
               }
            }
         }

         BlockEntityProvider provider = (BlockEntityProvider)Via.getManager().getProviders().get(BlockEntityProvider.class);
         Iterator iterator = chunk.getBlockEntities().iterator();

         while(iterator.hasNext()) {
            CompoundTag tag = (CompoundTag)iterator.next();
            i = provider.transform(wrapper.user(), (Position)null, tag, false);
            if (i != -1) {
               idx = ((NumberTag)tag.get("x")).asInt();
               idxx = ((NumberTag)tag.get("y")).asInt();
               globalX = ((NumberTag)tag.get("z")).asInt();
               Position positionx = new Position(idx, (short)idxx, globalX);
               BlockStorage.ReplacementData replacementData = storage.get(positionx);
               if (replacementData != null) {
                  replacementData.setReplacement(i);
               }

               chunk.getSections()[idxx >> 4].palette(PaletteType.BLOCKS).setIdAt(idx & 15, idxx & 15, globalX & 15, i);
            }

            Tag idTag = tag.get("id");
            if (idTag instanceof StringTag) {
               String id = Key.namespaced(((StringTag)idTag).getValue());
               if (id.equals("minecraft:noteblock") || id.equals("minecraft:flower_pot")) {
                  iterator.remove();
               }
            }
         }

         if (Via.getConfig().isServersideBlockConnections()) {
            ConnectionData.connectBlocks(wrapper.user(), chunk);
            wrapper.send(Protocol1_13To1_12_2.class);
            wrapper.cancel();
            ConnectionData.NeighbourUpdater updater = new ConnectionData.NeighbourUpdater(wrapper.user());

            for(i = 0; i < chunk.getSections().length; ++i) {
               ChunkSection section = chunk.getSections()[i];
               if (section != null) {
                  updater.updateChunkSectionNeighbours(chunk.getX(), chunk.getZ(), i);
               }
            }
         }

      });
      protocol.registerClientbound(ClientboundPackets1_12_1.SPAWN_PARTICLE, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.BOOLEAN);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.INT);
            this.handler((wrapper) -> {
               int particleId = (Integer)wrapper.get(Type.INT, 0);
               int dataCount = 0;
               if (particleId != 37 && particleId != 38 && particleId != 46) {
                  if (particleId == 36) {
                     dataCount = 2;
                  }
               } else {
                  dataCount = 1;
               }

               Integer[] data = new Integer[dataCount];

               for(int i = 0; i < data.length; ++i) {
                  data[i] = (Integer)wrapper.read(Type.VAR_INT);
               }

               Particle particle = ParticleRewriter.rewriteParticle(particleId, data);
               if (particle != null && particle.getId() != -1) {
                  if (particle.getId() == 11) {
                     int count = (Integer)wrapper.get(Type.INT, 1);
                     float speed = (Float)wrapper.get(Type.FLOAT, 6);
                     if (count == 0) {
                        wrapper.set(Type.INT, 1, 1);
                        wrapper.set(Type.FLOAT, 6, 0.0F);

                        for(int ix = 0; ix < 3; ++ix) {
                           float colorValue = (Float)wrapper.get(Type.FLOAT, ix + 3) * speed;
                           if (colorValue == 0.0F && ix == 0) {
                              colorValue = 1.0F;
                           }

                           particle.getArgument(ix).setValue(colorValue);
                           wrapper.set(Type.FLOAT, ix + 3, 0.0F);
                        }
                     }
                  }

                  wrapper.set(Type.INT, 0, particle.getId());
                  Iterator var10 = particle.getArguments().iterator();

                  while(var10.hasNext()) {
                     Particle.ParticleData<?> particleData = (Particle.ParticleData)var10.next();
                     particleData.write(wrapper);
                  }

               } else {
                  wrapper.cancel();
               }
            });
         }
      });
      protocol.registerServerbound(ServerboundPackets1_13.PLAYER_BLOCK_PLACEMENT, (wrapper) -> {
         Position pos = (Position)wrapper.passthrough(Type.POSITION1_8);
         wrapper.passthrough(Type.VAR_INT);
         wrapper.passthrough(Type.VAR_INT);
         wrapper.passthrough(Type.FLOAT);
         wrapper.passthrough(Type.FLOAT);
         wrapper.passthrough(Type.FLOAT);
         if (Via.getConfig().isServersideBlockConnections() && ConnectionData.needStoreBlocks()) {
            ConnectionData.markModified(wrapper.user(), pos);
         }

      });
      protocol.registerServerbound(ServerboundPackets1_13.PLAYER_DIGGING, (wrapper) -> {
         int status = (Integer)wrapper.passthrough(Type.VAR_INT);
         Position pos = (Position)wrapper.passthrough(Type.POSITION1_8);
         wrapper.passthrough(Type.UNSIGNED_BYTE);
         if (status == 0 && Via.getConfig().isServersideBlockConnections() && ConnectionData.needStoreBlocks()) {
            ConnectionData.markModified(wrapper.user(), pos);
         }

      });
   }

   public static int toNewId(int oldId) {
      if (oldId < 0) {
         oldId = 0;
      }

      int newId = Protocol1_13To1_12_2.MAPPINGS.getBlockMappings().getNewId(oldId);
      if (newId != -1) {
         return newId;
      } else {
         newId = Protocol1_13To1_12_2.MAPPINGS.getBlockMappings().getNewId(oldId & -16);
         if (newId != -1) {
            if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
               Via.getPlatform().getLogger().warning("Missing block " + oldId);
            }

            return newId;
         } else {
            if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
               Via.getPlatform().getLogger().warning("Missing block completely " + oldId);
            }

            return 0;
         }
      }
   }

   private static int checkStorage(UserConnection user, Position position, int newId) {
      BlockStorage storage = (BlockStorage)user.get(BlockStorage.class);
      if (storage.contains(position)) {
         BlockStorage.ReplacementData data = storage.get(position);
         if (data.getOriginal() == newId) {
            if (data.getReplacement() != -1) {
               return data.getReplacement();
            }
         } else {
            storage.remove(position);
            if (storage.isWelcome(newId)) {
               storage.store(position, newId);
            }
         }
      } else if (storage.isWelcome(newId)) {
         storage.store(position, newId);
      }

      return newId;
   }

   static {
      int i;
      for(i = 0; i < 50; ++i) {
         VALID_BIOMES.add(i);
      }

      VALID_BIOMES.add(127);

      for(i = 129; i <= 134; ++i) {
         VALID_BIOMES.add(i);
      }

      VALID_BIOMES.add(140);
      VALID_BIOMES.add(149);
      VALID_BIOMES.add(151);

      for(i = 155; i <= 158; ++i) {
         VALID_BIOMES.add(i);
      }

      for(i = 160; i <= 167; ++i) {
         VALID_BIOMES.add(i);
      }

   }
}
