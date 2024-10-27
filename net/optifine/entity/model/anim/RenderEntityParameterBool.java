package net.optifine.entity.model.anim;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.optifine.expr.ExpressionType;
import net.optifine.expr.IExpressionBool;

public enum RenderEntityParameterBool implements IExpressionBool {
   IS_ALIVE("is_alive"),
   IS_BURNING("is_burning"),
   IS_CHILD("is_child"),
   IS_GLOWING("is_glowing"),
   IS_HURT("is_hurt"),
   IS_IN_LAVA("is_in_lava"),
   IS_IN_WATER("is_in_water"),
   IS_INVISIBLE("is_invisible"),
   IS_ON_GROUND("is_on_ground"),
   IS_RIDDEN("is_ridden"),
   IS_RIDING("is_riding"),
   IS_SNEAKING("is_sneaking"),
   IS_SPRINTING("is_sprinting"),
   IS_WET("is_wet");

   private String name;
   private RenderManager renderManager;
   private static final RenderEntityParameterBool[] VALUES = values();
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$net$optifine$entity$model$anim$RenderEntityParameterBool;

   private RenderEntityParameterBool(String name) {
      this.name = name;
      this.renderManager = Minecraft.getMinecraft().getRenderManager();
   }

   public String getName() {
      return this.name;
   }

   public ExpressionType getExpressionType() {
      return ExpressionType.BOOL;
   }

   public boolean eval() {
      Render render = this.renderManager.renderRender;
      if (render == null) {
         return false;
      } else {
         if (render instanceof RendererLivingEntity) {
            RendererLivingEntity rendererlivingentity = (RendererLivingEntity)render;
            EntityLivingBase entitylivingbase = rendererlivingentity.renderEntity;
            if (entitylivingbase == null) {
               return false;
            }

            switch($SWITCH_TABLE$net$optifine$entity$model$anim$RenderEntityParameterBool()[this.ordinal()]) {
            case 1:
               return entitylivingbase.isEntityAlive();
            case 2:
               return entitylivingbase.isBurning();
            case 3:
               return entitylivingbase.isChild();
            case 4:
            default:
               break;
            case 5:
               if (entitylivingbase.hurtTime > 0) {
                  return true;
               }

               return false;
            case 6:
               return entitylivingbase.isInLava();
            case 7:
               return entitylivingbase.isInWater();
            case 8:
               return entitylivingbase.isInvisible();
            case 9:
               return entitylivingbase.onGround;
            case 10:
               if (entitylivingbase.riddenByEntity != null) {
                  return true;
               }

               return false;
            case 11:
               return entitylivingbase.isRiding();
            case 12:
               return entitylivingbase.isSneaking();
            case 13:
               return entitylivingbase.isSprinting();
            case 14:
               return entitylivingbase.isWet();
            }
         }

         return false;
      }
   }

   public static RenderEntityParameterBool parse(String str) {
      if (str == null) {
         return null;
      } else {
         for(int i = 0; i < VALUES.length; ++i) {
            RenderEntityParameterBool renderentityparameterbool = VALUES[i];
            if (renderentityparameterbool.getName().equals(str)) {
               return renderentityparameterbool;
            }
         }

         return null;
      }
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$optifine$entity$model$anim$RenderEntityParameterBool() {
      int[] var10000 = $SWITCH_TABLE$net$optifine$entity$model$anim$RenderEntityParameterBool;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[values().length];

         try {
            var0[IS_ALIVE.ordinal()] = 1;
         } catch (NoSuchFieldError var14) {
         }

         try {
            var0[IS_BURNING.ordinal()] = 2;
         } catch (NoSuchFieldError var13) {
         }

         try {
            var0[IS_CHILD.ordinal()] = 3;
         } catch (NoSuchFieldError var12) {
         }

         try {
            var0[IS_GLOWING.ordinal()] = 4;
         } catch (NoSuchFieldError var11) {
         }

         try {
            var0[IS_HURT.ordinal()] = 5;
         } catch (NoSuchFieldError var10) {
         }

         try {
            var0[IS_INVISIBLE.ordinal()] = 8;
         } catch (NoSuchFieldError var9) {
         }

         try {
            var0[IS_IN_LAVA.ordinal()] = 6;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var0[IS_IN_WATER.ordinal()] = 7;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[IS_ON_GROUND.ordinal()] = 9;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[IS_RIDDEN.ordinal()] = 10;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[IS_RIDING.ordinal()] = 11;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[IS_SNEAKING.ordinal()] = 12;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[IS_SPRINTING.ordinal()] = 13;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[IS_WET.ordinal()] = 14;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$net$optifine$entity$model$anim$RenderEntityParameterBool = var0;
         return var0;
      }
   }
}
