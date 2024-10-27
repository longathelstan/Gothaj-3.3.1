package com.viaversion.viaversion.api.minecraft;

import com.google.common.base.Preconditions;

public class BlockChangeRecord1_16_2 implements BlockChangeRecord {
   private final byte sectionX;
   private final byte sectionY;
   private final byte sectionZ;
   private int blockId;

   public BlockChangeRecord1_16_2(byte sectionX, byte sectionY, byte sectionZ, int blockId) {
      this.sectionX = sectionX;
      this.sectionY = sectionY;
      this.sectionZ = sectionZ;
      this.blockId = blockId;
   }

   public BlockChangeRecord1_16_2(int sectionX, int sectionY, int sectionZ, int blockId) {
      this((byte)sectionX, (byte)sectionY, (byte)sectionZ, blockId);
   }

   public byte getSectionX() {
      return this.sectionX;
   }

   public byte getSectionY() {
      return this.sectionY;
   }

   public byte getSectionZ() {
      return this.sectionZ;
   }

   public short getY(int chunkSectionY) {
      Preconditions.checkArgument(chunkSectionY >= 0, "Invalid chunkSectionY: " + chunkSectionY);
      return (short)((chunkSectionY << 4) + this.sectionY);
   }

   public int getBlockId() {
      return this.blockId;
   }

   public void setBlockId(int blockId) {
      this.blockId = blockId;
   }
}
