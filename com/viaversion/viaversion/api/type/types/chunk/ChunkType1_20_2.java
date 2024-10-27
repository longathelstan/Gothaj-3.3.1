package com.viaversion.viaversion.api.type.types.chunk;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.minecraft.blockentity.BlockEntity;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk1_18;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;

public final class ChunkType1_20_2 extends Type<Chunk> {
   private final ChunkSectionType1_18 sectionType;
   private final int ySectionCount;

   public ChunkType1_20_2(int ySectionCount, int globalPaletteBlockBits, int globalPaletteBiomeBits) {
      super(Chunk.class);
      Preconditions.checkArgument(ySectionCount > 0);
      this.sectionType = new ChunkSectionType1_18(globalPaletteBlockBits, globalPaletteBiomeBits);
      this.ySectionCount = ySectionCount;
   }

   public Chunk read(ByteBuf buffer) throws Exception {
      int chunkX = buffer.readInt();
      int chunkZ = buffer.readInt();
      CompoundTag heightMap = (CompoundTag)Type.COMPOUND_TAG.read(buffer);
      ByteBuf sectionsBuf = buffer.readBytes(Type.VAR_INT.readPrimitive(buffer));
      ChunkSection[] sections = new ChunkSection[this.ySectionCount];

      int blockEntitiesLength;
      try {
         for(blockEntitiesLength = 0; blockEntitiesLength < this.ySectionCount; ++blockEntitiesLength) {
            sections[blockEntitiesLength] = this.sectionType.read(sectionsBuf);
         }
      } finally {
         sectionsBuf.release();
      }

      blockEntitiesLength = Type.VAR_INT.readPrimitive(buffer);
      ArrayList blockEntities = new ArrayList(blockEntitiesLength);

      for(int i = 0; i < blockEntitiesLength; ++i) {
         blockEntities.add(Type.BLOCK_ENTITY1_20_2.read(buffer));
      }

      return new Chunk1_18(chunkX, chunkZ, sections, heightMap, blockEntities);
   }

   public void write(ByteBuf buffer, Chunk chunk) throws Exception {
      buffer.writeInt(chunk.getX());
      buffer.writeInt(chunk.getZ());
      Type.COMPOUND_TAG.write(buffer, chunk.getHeightMap());
      ByteBuf sectionBuffer = buffer.alloc().buffer();

      try {
         ChunkSection[] var4 = chunk.getSections();
         int var5 = var4.length;
         int var6 = 0;

         while(true) {
            if (var6 >= var5) {
               sectionBuffer.readerIndex(0);
               Type.VAR_INT.writePrimitive(buffer, sectionBuffer.readableBytes());
               buffer.writeBytes(sectionBuffer);
               break;
            }

            ChunkSection section = var4[var6];
            this.sectionType.write(sectionBuffer, section);
            ++var6;
         }
      } finally {
         sectionBuffer.release();
      }

      Type.VAR_INT.writePrimitive(buffer, chunk.blockEntities().size());
      Iterator var11 = chunk.blockEntities().iterator();

      while(var11.hasNext()) {
         BlockEntity blockEntity = (BlockEntity)var11.next();
         Type.BLOCK_ENTITY1_20_2.write(buffer, blockEntity);
      }

   }
}
