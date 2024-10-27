package net.optifine.shaders.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.config.EnumShaderOption;

public class GuiButtonEnumShaderOption extends GuiButton {
   private EnumShaderOption enumShaderOption = null;
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$net$optifine$shaders$config$EnumShaderOption;

   public GuiButtonEnumShaderOption(EnumShaderOption enumShaderOption, int x, int y, int widthIn, int heightIn) {
      super(enumShaderOption.ordinal(), x, y, widthIn, heightIn, getButtonText(enumShaderOption));
      this.enumShaderOption = enumShaderOption;
   }

   public EnumShaderOption getEnumShaderOption() {
      return this.enumShaderOption;
   }

   private static String getButtonText(EnumShaderOption eso) {
      String s = I18n.format(eso.getResourceKey()) + ": ";
      switch($SWITCH_TABLE$net$optifine$shaders$config$EnumShaderOption()[eso.ordinal()]) {
      case 1:
         return s + GuiShaders.toStringAa(Shaders.configAntialiasingLevel);
      case 2:
         return s + GuiShaders.toStringOnOff(Shaders.configNormalMap);
      case 3:
         return s + GuiShaders.toStringOnOff(Shaders.configSpecularMap);
      case 4:
         return s + GuiShaders.toStringQuality(Shaders.configRenderResMul);
      case 5:
         return s + GuiShaders.toStringQuality(Shaders.configShadowResMul);
      case 6:
         return s + GuiShaders.toStringHandDepth(Shaders.configHandDepthMul);
      case 7:
         return s + GuiShaders.toStringOnOff(Shaders.configCloudShadow);
      case 8:
         return s + Shaders.configOldHandLight.getUserValue();
      case 9:
         return s + Shaders.configOldLighting.getUserValue();
      case 10:
      default:
         return s + Shaders.getEnumShaderOption(eso);
      case 11:
         return s + GuiShaders.toStringOnOff(Shaders.configTweakBlockDamage);
      case 12:
         return s + GuiShaders.toStringOnOff(Shaders.configShadowClipFrustrum);
      }
   }

   public void updateButtonText() {
      this.displayString = getButtonText(this.enumShaderOption);
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$optifine$shaders$config$EnumShaderOption() {
      int[] var10000 = $SWITCH_TABLE$net$optifine$shaders$config$EnumShaderOption;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[EnumShaderOption.values().length];

         try {
            var0[EnumShaderOption.ANTIALIASING.ordinal()] = 1;
         } catch (NoSuchFieldError var18) {
         }

         try {
            var0[EnumShaderOption.CLOUD_SHADOW.ordinal()] = 7;
         } catch (NoSuchFieldError var17) {
         }

         try {
            var0[EnumShaderOption.HAND_DEPTH_MUL.ordinal()] = 6;
         } catch (NoSuchFieldError var16) {
         }

         try {
            var0[EnumShaderOption.NORMAL_MAP.ordinal()] = 2;
         } catch (NoSuchFieldError var15) {
         }

         try {
            var0[EnumShaderOption.OLD_HAND_LIGHT.ordinal()] = 8;
         } catch (NoSuchFieldError var14) {
         }

         try {
            var0[EnumShaderOption.OLD_LIGHTING.ordinal()] = 9;
         } catch (NoSuchFieldError var13) {
         }

         try {
            var0[EnumShaderOption.RENDER_RES_MUL.ordinal()] = 4;
         } catch (NoSuchFieldError var12) {
         }

         try {
            var0[EnumShaderOption.SHADER_PACK.ordinal()] = 10;
         } catch (NoSuchFieldError var11) {
         }

         try {
            var0[EnumShaderOption.SHADOW_CLIP_FRUSTRUM.ordinal()] = 12;
         } catch (NoSuchFieldError var10) {
         }

         try {
            var0[EnumShaderOption.SHADOW_RES_MUL.ordinal()] = 5;
         } catch (NoSuchFieldError var9) {
         }

         try {
            var0[EnumShaderOption.SPECULAR_MAP.ordinal()] = 3;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var0[EnumShaderOption.TEX_MAG_FIL_B.ordinal()] = 16;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[EnumShaderOption.TEX_MAG_FIL_N.ordinal()] = 17;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[EnumShaderOption.TEX_MAG_FIL_S.ordinal()] = 18;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[EnumShaderOption.TEX_MIN_FIL_B.ordinal()] = 13;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[EnumShaderOption.TEX_MIN_FIL_N.ordinal()] = 14;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[EnumShaderOption.TEX_MIN_FIL_S.ordinal()] = 15;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[EnumShaderOption.TWEAK_BLOCK_DAMAGE.ordinal()] = 11;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$net$optifine$shaders$config$EnumShaderOption = var0;
         return var0;
      }
   }
}
