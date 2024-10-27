package com.viaversion.viarewind.protocol.protocol1_8to1_9.packets;

import com.viaversion.viarewind.ViaRewind;
import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.protocol.protocol1_8to1_9.sound.Effect;
import com.viaversion.viarewind.protocol.protocol1_8to1_9.sound.SoundRemapper;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.ClientWorld;
import com.viaversion.viaversion.api.minecraft.Environment;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.BaseChunk;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSectionImpl;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_8;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_9_1;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;

public class WorldPackets {
   public static void register(final Protocol1_8To1_9 protocol) {
      protocol.registerClientbound(ClientboundPackets1_9.BLOCK_ENTITY_DATA, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_8);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.NAMED_COMPOUND_TAG);
            this.handler((packetWrapper) -> {
               CompoundTag tag = (CompoundTag)packetWrapper.get(Type.NAMED_COMPOUND_TAG, 0);
               if (tag != null && tag.contains("SpawnData")) {
                  CompoundTag spawnData = (CompoundTag)tag.get("SpawnData");
                  if (spawnData.contains("id")) {
                     String entity = (String)spawnData.get("id").getValue();
                     tag.remove("SpawnData");
                     tag.put("entityId", new StringTag(entity));
                  }
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.BLOCK_ACTION, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_8);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.VAR_INT);
            this.handler((packetWrapper) -> {
               int block = (Integer)packetWrapper.get(Type.VAR_INT, 0);
               if (block >= 219 && block <= 234) {
                  int blockx = true;
                  packetWrapper.set(Type.VAR_INT, 0, 130);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.BLOCK_CHANGE, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_8);
            this.map(Type.VAR_INT);
            this.handler((packetWrapper) -> {
               int combined = (Integer)packetWrapper.get(Type.VAR_INT, 0);
               int replacedCombined = protocol.getItemRewriter().replace(combined);
               packetWrapper.set(Type.VAR_INT, 0, replacedCombined);
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.MULTI_BLOCK_CHANGE, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.INT);
            this.map(Type.BLOCK_CHANGE_RECORD_ARRAY);
            this.handler((packetWrapper) -> {
               BlockChangeRecord[] var2 = (BlockChangeRecord[])packetWrapper.get(Type.BLOCK_CHANGE_RECORD_ARRAY, 0);
               int var3 = var2.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  BlockChangeRecord record = var2[var4];
                  int replacedCombined = protocol.getItemRewriter().replace(record.getBlockId());
                  record.setBlockId(replacedCombined);
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.NAMED_SOUND, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.handler((packetWrapper) -> {
               String name = (String)packetWrapper.get(Type.STRING, 0);
               name = SoundRemapper.getOldName(name);
               if (name == null) {
                  packetWrapper.cancel();
               } else {
                  packetWrapper.set(Type.STRING, 0, name);
               }

            });
            this.read(Type.VAR_INT);
            this.map(Type.INT);
            this.map(Type.INT);
            this.map(Type.INT);
            this.map(Type.FLOAT);
            this.map(Type.UNSIGNED_BYTE);
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.UNLOAD_CHUNK, ClientboundPackets1_8.CHUNK_DATA, new PacketHandlers() {
         public void register() {
            this.handler((packetWrapper) -> {
               Environment environment = ((ClientWorld)packetWrapper.user().get(ClientWorld.class)).getEnvironment();
               int chunkX = (Integer)packetWrapper.read(Type.INT);
               int chunkZ = (Integer)packetWrapper.read(Type.INT);
               packetWrapper.write(ChunkType1_8.forEnvironment(environment), new BaseChunk(chunkX, chunkZ, true, false, 0, new ChunkSection[16], (int[])null, new ArrayList()));
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.CHUNK_DATA, new PacketHandlers() {
         public void register() {
            this.handler((packetWrapper) -> {
               Environment environment = ((ClientWorld)packetWrapper.user().get(ClientWorld.class)).getEnvironment();
               Chunk chunk = (Chunk)packetWrapper.read(ChunkType1_9_1.forEnvironment(environment));
               ChunkSection[] var4 = ((Chunk)chunk).getSections();
               int var5 = var4.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  ChunkSection sectionx = var4[var6];
                  if (sectionx != null) {
                     DataPalette palette = sectionx.palette(PaletteType.BLOCKS);

                     for(int i = 0; i < palette.size(); ++i) {
                        int block = palette.idByIndex(i);
                        int replacedBlock = protocol.getItemRewriter().replace(block);
                        palette.setIdByIndex(i, replacedBlock);
                     }
                  }
               }

               if (((Chunk)chunk).isFullChunk() && ((Chunk)chunk).getBitmask() == 0) {
                  boolean skylight = environment == Environment.NORMAL;
                  ChunkSection[] sections = new ChunkSection[16];
                  ChunkSection section = new ChunkSectionImpl(true);
                  sections[0] = section;
                  section.palette(PaletteType.BLOCKS).addId(0);
                  if (skylight) {
                     section.getLight().setSkyLight(new byte[2048]);
                  }

                  chunk = new BaseChunk(((Chunk)chunk).getX(), ((Chunk)chunk).getZ(), true, false, 1, sections, ((Chunk)chunk).getBiomeData(), ((Chunk)chunk).getBlockEntities());
               }

               packetWrapper.write(ChunkType1_8.forEnvironment(environment), chunk);
               UserConnection user = packetWrapper.user();
               ((Chunk)chunk).getBlockEntities().forEach((nbt) -> {
                  if (nbt.contains("x") && nbt.contains("y") && nbt.contains("z") && nbt.contains("id")) {
                     Position position = new Position((Integer)nbt.get("x").getValue(), (Integer)nbt.get("y").getValue(), (Integer)nbt.get("z").getValue());
                     String id = (String)nbt.get("id").getValue();
                     byte var6 = -1;
                     switch(id.hashCode()) {
                     case -1883218338:
                        if (id.equals("minecraft:flower_pot")) {
                           var6 = 4;
                        }
                        break;
                     case -1296947815:
                        if (id.equals("minecraft:banner")) {
                           var6 = 5;
                        }
                        break;
                     case -1293651279:
                        if (id.equals("minecraft:beacon")) {
                           var6 = 2;
                        }
                        break;
                     case -1134211248:
                        if (id.equals("minecraft:skull")) {
                           var6 = 3;
                        }
                        break;
                     case -199249700:
                        if (id.equals("minecraft:mob_spawner")) {
                           var6 = 0;
                        }
                        break;
                     case 339138444:
                        if (id.equals("minecraft:command_block")) {
                           var6 = 1;
                        }
                     }

                     byte action;
                     switch(var6) {
                     case 0:
                        action = 1;
                        break;
                     case 1:
                        action = 2;
                        break;
                     case 2:
                        action = 3;
                        break;
                     case 3:
                        action = 4;
                        break;
                     case 4:
                        action = 5;
                        break;
                     case 5:
                        action = 6;
                        break;
                     default:
                        return;
                     }

                     PacketWrapper updateTileEntity = PacketWrapper.create(9, (ByteBuf)null, user);
                     updateTileEntity.write(Type.POSITION1_8, position);
                     updateTileEntity.write(Type.UNSIGNED_BYTE, Short.valueOf(action));
                     updateTileEntity.write(Type.NBT, nbt);
                     PacketUtil.sendPacket(updateTileEntity, Protocol1_8To1_9.class, false, false);
                  }
               });
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.EFFECT, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.POSITION1_8);
            this.map(Type.INT);
            this.map(Type.BOOLEAN);
            this.handler((packetWrapper) -> {
               int id = (Integer)packetWrapper.get(Type.INT, 0);
               id = Effect.getOldId(id);
               if (id == -1) {
                  packetWrapper.cancel();
               } else {
                  packetWrapper.set(Type.INT, 0, id);
                  if (id == 2001) {
                     int replacedBlock = protocol.getItemRewriter().replace((Integer)packetWrapper.get(Type.INT, 1));
                     packetWrapper.set(Type.INT, 1, replacedBlock);
                  }

               }
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.SPAWN_PARTICLE, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.handler((packetWrapper) -> {
               int type = (Integer)packetWrapper.get(Type.INT, 0);
               if (type > 41 && !ViaRewind.getConfig().isReplaceParticles()) {
                  packetWrapper.cancel();
               } else {
                  if (type == 42) {
                     packetWrapper.set(Type.INT, 0, 24);
                  } else if (type == 43) {
                     packetWrapper.set(Type.INT, 0, 3);
                  } else if (type == 44) {
                     packetWrapper.set(Type.INT, 0, 34);
                  } else if (type == 45) {
                     packetWrapper.set(Type.INT, 0, 1);
                  }

               }
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.MAP_DATA, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.BYTE);
            this.read(Type.BOOLEAN);
         }
      });
      protocol.registerClientbound(ClientboundPackets1_9.SOUND, ClientboundPackets1_8.NAMED_SOUND, new PacketHandlers() {
         public void register() {
            this.handler((packetWrapper) -> {
               int soundId = (Integer)packetWrapper.read(Type.VAR_INT);
               String sound = SoundRemapper.oldNameFromId(soundId);
               if (sound == null) {
                  packetWrapper.cancel();
               } else {
                  packetWrapper.write(Type.STRING, sound);
               }

            });
            this.handler((packetWrapper) -> {
               packetWrapper.read(Type.VAR_INT);
            });
            this.map(Type.INT);
            this.map(Type.INT);
            this.map(Type.INT);
            this.map(Type.FLOAT);
            this.map(Type.UNSIGNED_BYTE);
         }
      });
   }
}
