package com.viaversion.viaversion.protocols.protocol1_16to1_15_2.packets;

import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_15;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_16;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.Protocol1_16To1_15_2;
import com.viaversion.viaversion.rewriter.BlockRewriter;
import com.viaversion.viaversion.util.CompactArrayUtil;
import com.viaversion.viaversion.util.UUIDUtil;
import java.util.Iterator;
import java.util.UUID;
import java.util.Map.Entry;

public class WorldPackets {
   public static void register(Protocol1_16To1_15_2 protocol) {
      BlockRewriter<ClientboundPackets1_15> blockRewriter = BlockRewriter.for1_14(protocol);
      blockRewriter.registerBlockAction(ClientboundPackets1_15.BLOCK_ACTION);
      blockRewriter.registerBlockChange(ClientboundPackets1_15.BLOCK_CHANGE);
      blockRewriter.registerMultiBlockChange(ClientboundPackets1_15.MULTI_BLOCK_CHANGE);
      blockRewriter.registerAcknowledgePlayerDigging(ClientboundPackets1_15.ACKNOWLEDGE_PLAYER_DIGGING);
      protocol.registerClientbound(ClientboundPackets1_15.UPDATE_LIGHT, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               wrapper.write(Type.BOOLEAN, true);
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_15.CHUNK_DATA, (wrapper) -> {
         Chunk chunk = (Chunk)wrapper.read(ChunkType1_15.TYPE);
         wrapper.write(ChunkType1_16.TYPE, chunk);
         chunk.setIgnoreOldLightData(chunk.isFullChunk());

         for(int s = 0; s < chunk.getSections().length; ++s) {
            ChunkSection section = chunk.getSections()[s];
            if (section != null) {
               DataPalette palette = section.palette(PaletteType.BLOCKS);

               for(int i = 0; i < palette.size(); ++i) {
                  int mappedBlockStateId = protocol.getMappingData().getNewBlockStateId(palette.idByIndex(i));
                  palette.setIdByIndex(i, mappedBlockStateId);
               }
            }
         }

         CompoundTag heightMaps = chunk.getHeightMap();
         Iterator var9 = heightMaps.values().iterator();

         while(var9.hasNext()) {
            Tag heightMapTag = (Tag)var9.next();
            LongArrayTag heightMap = (LongArrayTag)heightMapTag;
            int[] heightMapData = new int[256];
            CompactArrayUtil.iterateCompactArray(9, heightMapData.length, heightMap.getValue(), (ix, v) -> {
               heightMapData[ix] = v;
            });
            heightMap.setValue(CompactArrayUtil.createCompactArrayWithPadding(9, heightMapData.length, (ix) -> {
               return (long)heightMapData[ix];
            }));
         }

         if (chunk.getBlockEntities() != null) {
            var9 = chunk.getBlockEntities().iterator();

            while(var9.hasNext()) {
               CompoundTag blockEntity = (CompoundTag)var9.next();
               handleBlockEntity(protocol, blockEntity);
            }

         }
      });
      protocol.registerClientbound(ClientboundPackets1_15.BLOCK_ENTITY_DATA, (wrapper) -> {
         wrapper.passthrough(Type.POSITION1_14);
         wrapper.passthrough(Type.UNSIGNED_BYTE);
         CompoundTag tag = (CompoundTag)wrapper.passthrough(Type.NAMED_COMPOUND_TAG);
         handleBlockEntity(protocol, tag);
      });
      blockRewriter.registerEffect(ClientboundPackets1_15.EFFECT, 1010, 2001);
   }

   private static void handleBlockEntity(Protocol1_16To1_15_2 protocol, CompoundTag compoundTag) {
      StringTag idTag = (StringTag)compoundTag.get("id");
      if (idTag != null) {
         String id = idTag.getValue();
         Tag spawnDataTag;
         if (id.equals("minecraft:conduit")) {
            spawnDataTag = compoundTag.remove("target_uuid");
            if (!(spawnDataTag instanceof StringTag)) {
               return;
            }

            UUID targetUuid = UUID.fromString((String)spawnDataTag.getValue());
            compoundTag.put("Target", new IntArrayTag(UUIDUtil.toIntArray(targetUuid)));
         } else if (id.equals("minecraft:skull") && compoundTag.get("Owner") instanceof CompoundTag) {
            CompoundTag ownerTag = (CompoundTag)compoundTag.remove("Owner");
            StringTag ownerUuidTag = (StringTag)ownerTag.remove("Id");
            if (ownerUuidTag != null) {
               UUID ownerUuid = UUID.fromString(ownerUuidTag.getValue());
               ownerTag.put("Id", new IntArrayTag(UUIDUtil.toIntArray(ownerUuid)));
            }

            CompoundTag skullOwnerTag = new CompoundTag();
            Iterator var7 = ownerTag.entrySet().iterator();

            while(var7.hasNext()) {
               Entry<String, Tag> entry = (Entry)var7.next();
               skullOwnerTag.put((String)entry.getKey(), (Tag)entry.getValue());
            }

            compoundTag.put("SkullOwner", skullOwnerTag);
         } else {
            Tag spawnDataIdTag;
            if (id.equals("minecraft:sign")) {
               for(int i = 1; i <= 4; ++i) {
                  spawnDataIdTag = compoundTag.get("Text" + i);
                  if (spawnDataIdTag instanceof StringTag) {
                     JsonElement text = protocol.getComponentRewriter().processText(((StringTag)spawnDataIdTag).getValue());
                     compoundTag.put("Text" + i, new StringTag(text.toString()));
                  }
               }
            } else if (id.equals("minecraft:mob_spawner")) {
               spawnDataTag = compoundTag.get("SpawnData");
               if (spawnDataTag instanceof CompoundTag) {
                  spawnDataIdTag = ((CompoundTag)spawnDataTag).get("id");
                  if (spawnDataIdTag instanceof StringTag) {
                     StringTag spawnDataIdStringTag = (StringTag)spawnDataIdTag;
                     if (spawnDataIdStringTag.getValue().equals("minecraft:zombie_pigman")) {
                        spawnDataIdStringTag.setValue("minecraft:zombified_piglin");
                     }
                  }
               }
            }
         }

      }
   }
}
