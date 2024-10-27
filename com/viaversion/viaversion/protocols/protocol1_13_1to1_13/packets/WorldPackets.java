package com.viaversion.viaversion.protocols.protocol1_13_1to1_13.packets;

import com.viaversion.viaversion.api.minecraft.ClientWorld;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_13;
import com.viaversion.viaversion.protocols.protocol1_13_1to1_13.Protocol1_13_1To1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.rewriter.BlockRewriter;

public class WorldPackets {
   public static void register(final Protocol1_13_1To1_13 protocol) {
      BlockRewriter<ClientboundPackets1_13> blockRewriter = BlockRewriter.legacy(protocol);
      protocol.registerClientbound(ClientboundPackets1_13.CHUNK_DATA, (wrapper) -> {
         ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
         Chunk chunk = (Chunk)wrapper.passthrough(ChunkType1_13.forEnvironment(clientWorld.getEnvironment()));
         ChunkSection[] var4 = chunk.getSections();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            ChunkSection section = var4[var6];
            if (section != null) {
               DataPalette palette = section.palette(PaletteType.BLOCKS);

               for(int i = 0; i < palette.size(); ++i) {
                  int mappedBlockStateId = protocol.getMappingData().getNewBlockStateId(palette.idByIndex(i));
                  palette.setIdByIndex(i, mappedBlockStateId);
               }
            }
         }

      });
      blockRewriter.registerBlockAction(ClientboundPackets1_13.BLOCK_ACTION);
      blockRewriter.registerBlockChange(ClientboundPackets1_13.BLOCK_CHANGE);
      blockRewriter.registerMultiBlockChange(ClientboundPackets1_13.MULTI_BLOCK_CHANGE);
      protocol.registerClientbound(ClientboundPackets1_13.EFFECT, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.POSITION1_8);
            this.map(Type.INT);
            this.handler((wrapper) -> {
               int id = (Integer)wrapper.get(Type.INT, 0);
               if (id == 2000) {
                  int data = (Integer)wrapper.get(Type.INT, 1);
                  switch(data) {
                  case 0:
                  case 3:
                  case 6:
                     wrapper.set(Type.INT, 1, 4);
                     break;
                  case 1:
                     wrapper.set(Type.INT, 1, 2);
                     break;
                  case 2:
                  case 5:
                  case 8:
                     wrapper.set(Type.INT, 1, 5);
                     break;
                  case 4:
                  default:
                     wrapper.set(Type.INT, 1, 0);
                     break;
                  case 7:
                     wrapper.set(Type.INT, 1, 3);
                  }
               } else if (id == 1010) {
                  wrapper.set(Type.INT, 1, protocol.getMappingData().getNewItemId((Integer)wrapper.get(Type.INT, 1)));
               } else if (id == 2001) {
                  wrapper.set(Type.INT, 1, protocol.getMappingData().getNewBlockStateId((Integer)wrapper.get(Type.INT, 1)));
               }

            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_13.JOIN_GAME, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.INT);
            this.handler((wrapper) -> {
               ClientWorld clientChunks = (ClientWorld)wrapper.user().get(ClientWorld.class);
               int dimensionId = (Integer)wrapper.get(Type.INT, 1);
               clientChunks.setEnvironment(dimensionId);
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
            });
         }
      });
   }
}
