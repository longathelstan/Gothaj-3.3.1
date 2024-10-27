package com.viaversion.viaversion.api.minecraft;

public class BlockChangeRecord1_8 implements BlockChangeRecord {
   private final byte sectionX;
   private final short y;
   private final byte sectionZ;
   private int blockId;

   public BlockChangeRecord1_8(byte sectionX, short y, byte sectionZ, int blockId) {
      this.sectionX = sectionX;
      this.y = y;
      this.sectionZ = sectionZ;
      this.blockId = blockId;
   }

   public BlockChangeRecord1_8(int sectionX, int y, int sectionZ, int blockId) {
      this((byte)sectionX, (short)y, (byte)sectionZ, blockId);
   }

   public byte getSectionX() {
      return this.sectionX;
   }

   public byte getSectionY() {
      return (byte)(this.y & 15);
   }

   public short getY(int chunkSectionY) {
      return this.y;
   }

   public byte getSectionZ() {
      return this.sectionZ;
   }

   public int getBlockId() {
      return this.blockId;
   }

   public void setBlockId(int blockId) {
      this.blockId = blockId;
   }
}
