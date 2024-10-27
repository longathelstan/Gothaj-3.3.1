package net.optifine.model;

import net.minecraft.util.EnumFacing;

public class QuadBounds {
   private float minX = Float.MAX_VALUE;
   private float minY = Float.MAX_VALUE;
   private float minZ = Float.MAX_VALUE;
   private float maxX = -3.4028235E38F;
   private float maxY = -3.4028235E38F;
   private float maxZ = -3.4028235E38F;
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$net$minecraft$util$EnumFacing;

   public QuadBounds(int[] vertexData) {
      int i = vertexData.length / 4;

      for(int j = 0; j < 4; ++j) {
         int k = j * i;
         float f = Float.intBitsToFloat(vertexData[k + 0]);
         float f1 = Float.intBitsToFloat(vertexData[k + 1]);
         float f2 = Float.intBitsToFloat(vertexData[k + 2]);
         if (this.minX > f) {
            this.minX = f;
         }

         if (this.minY > f1) {
            this.minY = f1;
         }

         if (this.minZ > f2) {
            this.minZ = f2;
         }

         if (this.maxX < f) {
            this.maxX = f;
         }

         if (this.maxY < f1) {
            this.maxY = f1;
         }

         if (this.maxZ < f2) {
            this.maxZ = f2;
         }
      }

   }

   public float getMinX() {
      return this.minX;
   }

   public float getMinY() {
      return this.minY;
   }

   public float getMinZ() {
      return this.minZ;
   }

   public float getMaxX() {
      return this.maxX;
   }

   public float getMaxY() {
      return this.maxY;
   }

   public float getMaxZ() {
      return this.maxZ;
   }

   public boolean isFaceQuad(EnumFacing face) {
      float f;
      float f1;
      float f2;
      switch($SWITCH_TABLE$net$minecraft$util$EnumFacing()[face.ordinal()]) {
      case 1:
         f = this.getMinY();
         f1 = this.getMaxY();
         f2 = 0.0F;
         break;
      case 2:
         f = this.getMinY();
         f1 = this.getMaxY();
         f2 = 1.0F;
         break;
      case 3:
         f = this.getMinZ();
         f1 = this.getMaxZ();
         f2 = 0.0F;
         break;
      case 4:
         f = this.getMinZ();
         f1 = this.getMaxZ();
         f2 = 1.0F;
         break;
      case 5:
         f = this.getMinX();
         f1 = this.getMaxX();
         f2 = 0.0F;
         break;
      case 6:
         f = this.getMinX();
         f1 = this.getMaxX();
         f2 = 1.0F;
         break;
      default:
         return false;
      }

      return f == f2 && f1 == f2;
   }

   public boolean isFullQuad(EnumFacing face) {
      float f;
      float f1;
      float f2;
      float f3;
      switch($SWITCH_TABLE$net$minecraft$util$EnumFacing()[face.ordinal()]) {
      case 1:
      case 2:
         f = this.getMinX();
         f1 = this.getMaxX();
         f2 = this.getMinZ();
         f3 = this.getMaxZ();
         break;
      case 3:
      case 4:
         f = this.getMinX();
         f1 = this.getMaxX();
         f2 = this.getMinY();
         f3 = this.getMaxY();
         break;
      case 5:
      case 6:
         f = this.getMinY();
         f1 = this.getMaxY();
         f2 = this.getMinZ();
         f3 = this.getMaxZ();
         break;
      default:
         return false;
      }

      return f == 0.0F && f1 == 1.0F && f2 == 0.0F && f3 == 1.0F;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$minecraft$util$EnumFacing() {
      int[] var10000 = $SWITCH_TABLE$net$minecraft$util$EnumFacing;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[EnumFacing.values().length];

         try {
            var0[EnumFacing.DOWN.ordinal()] = 1;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[EnumFacing.EAST.ordinal()] = 6;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[EnumFacing.NORTH.ordinal()] = 3;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[EnumFacing.SOUTH.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[EnumFacing.UP.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[EnumFacing.WEST.ordinal()] = 5;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$net$minecraft$util$EnumFacing = var0;
         return var0;
      }
   }
}
