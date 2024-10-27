package net.optifine.entity.model.anim;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.src.Config;

public enum ModelVariableType {
   POS_X("tx"),
   POS_Y("ty"),
   POS_Z("tz"),
   ANGLE_X("rx"),
   ANGLE_Y("ry"),
   ANGLE_Z("rz"),
   OFFSET_X("ox"),
   OFFSET_Y("oy"),
   OFFSET_Z("oz"),
   SCALE_X("sx"),
   SCALE_Y("sy"),
   SCALE_Z("sz");

   private String name;
   public static ModelVariableType[] VALUES = values();
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$net$optifine$entity$model$anim$ModelVariableType;

   private ModelVariableType(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public float getFloat(ModelRenderer mr) {
      switch($SWITCH_TABLE$net$optifine$entity$model$anim$ModelVariableType()[this.ordinal()]) {
      case 1:
         return mr.rotationPointX;
      case 2:
         return mr.rotationPointY;
      case 3:
         return mr.rotationPointZ;
      case 4:
         return mr.rotateAngleX;
      case 5:
         return mr.rotateAngleY;
      case 6:
         return mr.rotateAngleZ;
      case 7:
         return mr.offsetX;
      case 8:
         return mr.offsetY;
      case 9:
         return mr.offsetZ;
      case 10:
         return mr.scaleX;
      case 11:
         return mr.scaleY;
      case 12:
         return mr.scaleZ;
      default:
         Config.warn("GetFloat not supported for: " + this);
         return 0.0F;
      }
   }

   public void setFloat(ModelRenderer mr, float val) {
      switch($SWITCH_TABLE$net$optifine$entity$model$anim$ModelVariableType()[this.ordinal()]) {
      case 1:
         mr.rotationPointX = val;
         return;
      case 2:
         mr.rotationPointY = val;
         return;
      case 3:
         mr.rotationPointZ = val;
         return;
      case 4:
         mr.rotateAngleX = val;
         return;
      case 5:
         mr.rotateAngleY = val;
         return;
      case 6:
         mr.rotateAngleZ = val;
         return;
      case 7:
         mr.offsetX = val;
         return;
      case 8:
         mr.offsetY = val;
         return;
      case 9:
         mr.offsetZ = val;
         return;
      case 10:
         mr.scaleX = val;
         return;
      case 11:
         mr.scaleY = val;
         return;
      case 12:
         mr.scaleZ = val;
         return;
      default:
         Config.warn("SetFloat not supported for: " + this);
      }
   }

   public static ModelVariableType parse(String str) {
      for(int i = 0; i < VALUES.length; ++i) {
         ModelVariableType modelvariabletype = VALUES[i];
         if (modelvariabletype.getName().equals(str)) {
            return modelvariabletype;
         }
      }

      return null;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$optifine$entity$model$anim$ModelVariableType() {
      int[] var10000 = $SWITCH_TABLE$net$optifine$entity$model$anim$ModelVariableType;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[values().length];

         try {
            var0[ANGLE_X.ordinal()] = 4;
         } catch (NoSuchFieldError var12) {
         }

         try {
            var0[ANGLE_Y.ordinal()] = 5;
         } catch (NoSuchFieldError var11) {
         }

         try {
            var0[ANGLE_Z.ordinal()] = 6;
         } catch (NoSuchFieldError var10) {
         }

         try {
            var0[OFFSET_X.ordinal()] = 7;
         } catch (NoSuchFieldError var9) {
         }

         try {
            var0[OFFSET_Y.ordinal()] = 8;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var0[OFFSET_Z.ordinal()] = 9;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[POS_X.ordinal()] = 1;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[POS_Y.ordinal()] = 2;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[POS_Z.ordinal()] = 3;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[SCALE_X.ordinal()] = 10;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[SCALE_Y.ordinal()] = 11;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[SCALE_Z.ordinal()] = 12;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$net$optifine$entity$model$anim$ModelVariableType = var0;
         return var0;
      }
   }
}
