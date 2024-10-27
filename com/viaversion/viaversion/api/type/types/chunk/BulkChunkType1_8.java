package com.viaversion.viaversion.api.type.types.chunk;

import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class BulkChunkType1_8 extends Type<Chunk[]> {
   public static final Type<Chunk[]> TYPE = new BulkChunkType1_8();
   private static final int BLOCKS_PER_SECTION = 4096;
   private static final int BLOCKS_BYTES = 8192;
   private static final int LIGHT_BYTES = 2048;
   private static final int BIOME_BYTES = 256;

   public BulkChunkType1_8() {
      super(Chunk[].class);
   }

   public Chunk[] read(ByteBuf input) throws Exception {
      boolean skyLight = input.readBoolean();
      int count = Type.VAR_INT.readPrimitive(input);
      Chunk[] chunks = new Chunk[count];
      BulkChunkType1_8.ChunkBulkSection[] chunkInfo = new BulkChunkType1_8.ChunkBulkSection[count];

      int i;
      for(i = 0; i < chunkInfo.length; ++i) {
         chunkInfo[i] = new BulkChunkType1_8.ChunkBulkSection(input, skyLight);
      }

      for(i = 0; i < chunks.length; ++i) {
         BulkChunkType1_8.ChunkBulkSection chunkBulkSection = chunkInfo[i];
         chunkBulkSection.readData(input);
         chunks[i] = ChunkType1_8.deserialize(chunkBulkSection.chunkX, chunkBulkSection.chunkZ, true, skyLight, chunkBulkSection.bitmask, chunkBulkSection.data());
      }

      return chunks;
   }

   public void write(ByteBuf output, Chunk[] chunks) throws Exception {
      boolean skyLight = false;
      Chunk[] var4 = chunks;
      int var5 = chunks.length;

      int var6;
      Chunk chunk;
      label42:
      for(var6 = 0; var6 < var5; ++var6) {
         chunk = var4[var6];
         ChunkSection[] var8 = chunk.getSections();
         int var9 = var8.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            ChunkSection section = var8[var10];
            if (section != null && section.getLight().hasSkyLight()) {
               skyLight = true;
               break label42;
            }
         }
      }

      output.writeBoolean(skyLight);
      Type.VAR_INT.writePrimitive(output, chunks.length);
      var4 = chunks;
      var5 = chunks.length;

      for(var6 = 0; var6 < var5; ++var6) {
         chunk = var4[var6];
         output.writeInt(chunk.getX());
         output.writeInt(chunk.getZ());
         output.writeShort(chunk.getBitmask());
      }

      var4 = chunks;
      var5 = chunks.length;

      for(var6 = 0; var6 < var5; ++var6) {
         chunk = var4[var6];
         output.writeBytes(ChunkType1_8.serialize(chunk));
      }

   }

   public static final class ChunkBulkSection {
      private final int chunkX;
      private final int chunkZ;
      private final int bitmask;
      private final byte[] data;

      public ChunkBulkSection(ByteBuf input, boolean skyLight) {
         this.chunkX = input.readInt();
         this.chunkZ = input.readInt();
         this.bitmask = input.readUnsignedShort();
         int setSections = Integer.bitCount(this.bitmask);
         this.data = new byte[setSections * (8192 + (skyLight ? 4096 : 2048)) + 256];
      }

      public void readData(ByteBuf input) {
         input.readBytes(this.data);
      }

      public int chunkX() {
         return this.chunkX;
      }

      public int chunkZ() {
         return this.chunkZ;
      }

      public int bitmask() {
         return this.bitmask;
      }

      public byte[] data() {
         return this.data;
      }
   }
}
