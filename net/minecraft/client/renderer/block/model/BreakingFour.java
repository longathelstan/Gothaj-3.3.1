package net.minecraft.client.renderer.block.model;

import java.util.Arrays;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class BreakingFour extends BakedQuad {
   private final TextureAtlasSprite texture;
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$net$minecraft$util$EnumFacing;

   public BreakingFour(BakedQuad quad, TextureAtlasSprite textureIn) {
      super(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), quad.tintIndex, FaceBakery.getFacingFromVertexData(quad.getVertexData()));
      this.texture = textureIn;
      this.remapQuad();
      this.fixVertexData();
   }

   private void remapQuad() {
      for(int i = 0; i < 4; ++i) {
         this.remapVert(i);
      }

   }

   private void remapVert(int vertex) {
      int i = this.vertexData.length / 4;
      int j = i * vertex;
      float f = Float.intBitsToFloat(this.vertexData[j]);
      float f1 = Float.intBitsToFloat(this.vertexData[j + 1]);
      float f2 = Float.intBitsToFloat(this.vertexData[j + 2]);
      float f3 = 0.0F;
      float f4 = 0.0F;
      switch($SWITCH_TABLE$net$minecraft$util$EnumFacing()[this.face.ordinal()]) {
      case 1:
         f3 = f * 16.0F;
         f4 = (1.0F - f2) * 16.0F;
         break;
      case 2:
         f3 = f * 16.0F;
         f4 = f2 * 16.0F;
         break;
      case 3:
         f3 = (1.0F - f) * 16.0F;
         f4 = (1.0F - f1) * 16.0F;
         break;
      case 4:
         f3 = f * 16.0F;
         f4 = (1.0F - f1) * 16.0F;
         break;
      case 5:
         f3 = f2 * 16.0F;
         f4 = (1.0F - f1) * 16.0F;
         break;
      case 6:
         f3 = (1.0F - f2) * 16.0F;
         f4 = (1.0F - f1) * 16.0F;
      }

      this.vertexData[j + 4] = Float.floatToRawIntBits(this.texture.getInterpolatedU((double)f3));
      this.vertexData[j + 4 + 1] = Float.floatToRawIntBits(this.texture.getInterpolatedV((double)f4));
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
