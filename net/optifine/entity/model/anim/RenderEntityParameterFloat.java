package net.optifine.entity.model.anim;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.optifine.expr.ExpressionType;
import net.optifine.expr.IExpressionFloat;

public enum RenderEntityParameterFloat implements IExpressionFloat {
   LIMB_SWING("limb_swing"),
   LIMB_SWING_SPEED("limb_speed"),
   AGE("age"),
   HEAD_YAW("head_yaw"),
   HEAD_PITCH("head_pitch"),
   SCALE("scale"),
   HEALTH("health"),
   HURT_TIME("hurt_time"),
   IDLE_TIME("idle_time"),
   MAX_HEALTH("max_health"),
   MOVE_FORWARD("move_forward"),
   MOVE_STRAFING("move_strafing"),
   PARTIAL_TICKS("partial_ticks"),
   POS_X("pos_x"),
   POS_Y("pos_y"),
   POS_Z("pos_z"),
   REVENGE_TIME("revenge_time"),
   SWING_PROGRESS("swing_progress");

   private String name;
   private RenderManager renderManager;
   private static final RenderEntityParameterFloat[] VALUES = values();
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$net$optifine$entity$model$anim$RenderEntityParameterFloat;

   private RenderEntityParameterFloat(String name) {
      this.name = name;
      this.renderManager = Minecraft.getMinecraft().getRenderManager();
   }

   public String getName() {
      return this.name;
   }

   public ExpressionType getExpressionType() {
      return ExpressionType.FLOAT;
   }

   public float eval() {
      Render render = this.renderManager.renderRender;
      if (render == null) {
         return 0.0F;
      } else {
         if (render instanceof RendererLivingEntity) {
            RendererLivingEntity rendererlivingentity = (RendererLivingEntity)render;
            switch($SWITCH_TABLE$net$optifine$entity$model$anim$RenderEntityParameterFloat()[this.ordinal()]) {
            case 1:
               return rendererlivingentity.renderLimbSwing;
            case 2:
               return rendererlivingentity.renderLimbSwingAmount;
            case 3:
               return rendererlivingentity.renderAgeInTicks;
            case 4:
               return rendererlivingentity.renderHeadYaw;
            case 5:
               return rendererlivingentity.renderHeadPitch;
            case 6:
               return rendererlivingentity.renderScaleFactor;
            default:
               EntityLivingBase entitylivingbase = rendererlivingentity.renderEntity;
               if (entitylivingbase == null) {
                  return 0.0F;
               }

               switch($SWITCH_TABLE$net$optifine$entity$model$anim$RenderEntityParameterFloat()[this.ordinal()]) {
               case 7:
                  return entitylivingbase.getHealth();
               case 8:
                  return (float)entitylivingbase.hurtTime;
               case 9:
                  return (float)entitylivingbase.getAge();
               case 10:
                  return entitylivingbase.getMaxHealth();
               case 11:
                  return entitylivingbase.moveForward;
               case 12:
                  return entitylivingbase.moveStrafing;
               case 13:
               default:
                  break;
               case 14:
                  return (float)entitylivingbase.posX;
               case 15:
                  return (float)entitylivingbase.posY;
               case 16:
                  return (float)entitylivingbase.posZ;
               case 17:
                  return (float)entitylivingbase.getRevengeTimer();
               case 18:
                  return entitylivingbase.getSwingProgress(rendererlivingentity.renderPartialTicks);
               }
            }
         }

         return 0.0F;
      }
   }

   public static RenderEntityParameterFloat parse(String str) {
      if (str == null) {
         return null;
      } else {
         for(int i = 0; i < VALUES.length; ++i) {
            RenderEntityParameterFloat renderentityparameterfloat = VALUES[i];
            if (renderentityparameterfloat.getName().equals(str)) {
               return renderentityparameterfloat;
            }
         }

         return null;
      }
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$optifine$entity$model$anim$RenderEntityParameterFloat() {
      int[] var10000 = $SWITCH_TABLE$net$optifine$entity$model$anim$RenderEntityParameterFloat;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[values().length];

         try {
            var0[AGE.ordinal()] = 3;
         } catch (NoSuchFieldError var18) {
         }

         try {
            var0[HEAD_PITCH.ordinal()] = 5;
         } catch (NoSuchFieldError var17) {
         }

         try {
            var0[HEAD_YAW.ordinal()] = 4;
         } catch (NoSuchFieldError var16) {
         }

         try {
            var0[HEALTH.ordinal()] = 7;
         } catch (NoSuchFieldError var15) {
         }

         try {
            var0[HURT_TIME.ordinal()] = 8;
         } catch (NoSuchFieldError var14) {
         }

         try {
            var0[IDLE_TIME.ordinal()] = 9;
         } catch (NoSuchFieldError var13) {
         }

         try {
            var0[LIMB_SWING.ordinal()] = 1;
         } catch (NoSuchFieldError var12) {
         }

         try {
            var0[LIMB_SWING_SPEED.ordinal()] = 2;
         } catch (NoSuchFieldError var11) {
         }

         try {
            var0[MAX_HEALTH.ordinal()] = 10;
         } catch (NoSuchFieldError var10) {
         }

         try {
            var0[MOVE_FORWARD.ordinal()] = 11;
         } catch (NoSuchFieldError var9) {
         }

         try {
            var0[MOVE_STRAFING.ordinal()] = 12;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var0[PARTIAL_TICKS.ordinal()] = 13;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[POS_X.ordinal()] = 14;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[POS_Y.ordinal()] = 15;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[POS_Z.ordinal()] = 16;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[REVENGE_TIME.ordinal()] = 17;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[SCALE.ordinal()] = 6;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[SWING_PROGRESS.ordinal()] = 18;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$net$optifine$entity$model$anim$RenderEntityParameterFloat = var0;
         return var0;
      }
   }
}
