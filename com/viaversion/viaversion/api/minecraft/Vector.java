package com.viaversion.viaversion.api.minecraft;

public class Vector {
   private int blockX;
   private int blockY;
   private int blockZ;

   public Vector(int blockX, int blockY, int blockZ) {
      this.blockX = blockX;
      this.blockY = blockY;
      this.blockZ = blockZ;
   }

   public int blockX() {
      return this.blockX;
   }

   public int blockY() {
      return this.blockY;
   }

   public int blockZ() {
      return this.blockZ;
   }

   /** @deprecated */
   @Deprecated
   public int getBlockX() {
      return this.blockX;
   }

   /** @deprecated */
   @Deprecated
   public int getBlockY() {
      return this.blockY;
   }

   /** @deprecated */
   @Deprecated
   public int getBlockZ() {
      return this.blockZ;
   }

   /** @deprecated */
   @Deprecated
   public void setBlockX(int blockX) {
      this.blockX = blockX;
   }

   /** @deprecated */
   @Deprecated
   public void setBlockY(int blockY) {
      this.blockY = blockY;
   }

   /** @deprecated */
   @Deprecated
   public void setBlockZ(int blockZ) {
      this.blockZ = blockZ;
   }
}
