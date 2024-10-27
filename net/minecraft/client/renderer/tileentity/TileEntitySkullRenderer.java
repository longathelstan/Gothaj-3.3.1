package net.minecraft.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TileEntitySkullRenderer extends TileEntitySpecialRenderer<TileEntitySkull> {
   private static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/skeleton.png");
   private static final ResourceLocation WITHER_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
   private static final ResourceLocation ZOMBIE_TEXTURES = new ResourceLocation("textures/entity/zombie/zombie.png");
   private static final ResourceLocation CREEPER_TEXTURES = new ResourceLocation("textures/entity/creeper/creeper.png");
   public static TileEntitySkullRenderer instance;
   private final ModelSkeletonHead skeletonHead = new ModelSkeletonHead(0, 0, 64, 32);
   private final ModelSkeletonHead humanoidHead = new ModelHumanoidHead();
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$net$minecraft$util$EnumFacing;

   public void renderTileEntityAt(TileEntitySkull te, double x, double y, double z, float partialTicks, int destroyStage) {
      EnumFacing enumfacing = EnumFacing.getFront(te.getBlockMetadata() & 7);
      this.renderSkull((float)x, (float)y, (float)z, enumfacing, (float)(te.getSkullRotation() * 360) / 16.0F, te.getSkullType(), te.getPlayerProfile(), destroyStage);
   }

   public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcherIn) {
      super.setRendererDispatcher(rendererDispatcherIn);
      instance = this;
   }

   public void renderSkull(float p_180543_1_, float p_180543_2_, float p_180543_3_, EnumFacing p_180543_4_, float p_180543_5_, int p_180543_6_, GameProfile p_180543_7_, int p_180543_8_) {
      ModelBase modelbase = this.skeletonHead;
      if (p_180543_8_ >= 0) {
         this.bindTexture(DESTROY_STAGES[p_180543_8_]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scale(4.0F, 2.0F, 1.0F);
         GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         switch(p_180543_6_) {
         case 0:
         default:
            this.bindTexture(SKELETON_TEXTURES);
            break;
         case 1:
            this.bindTexture(WITHER_SKELETON_TEXTURES);
            break;
         case 2:
            this.bindTexture(ZOMBIE_TEXTURES);
            modelbase = this.humanoidHead;
            break;
         case 3:
            modelbase = this.humanoidHead;
            ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkinLegacy();
            if (p_180543_7_ != null) {
               Minecraft minecraft = Minecraft.getMinecraft();
               Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(p_180543_7_);
               if (map.containsKey(Type.SKIN)) {
                  resourcelocation = minecraft.getSkinManager().loadSkin((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
               } else {
                  UUID uuid = EntityPlayer.getUUID(p_180543_7_);
                  resourcelocation = DefaultPlayerSkin.getDefaultSkin(uuid);
               }
            }

            this.bindTexture(resourcelocation);
            break;
         case 4:
            this.bindTexture(CREEPER_TEXTURES);
         }
      }

      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      if (p_180543_4_ != EnumFacing.UP) {
         switch($SWITCH_TABLE$net$minecraft$util$EnumFacing()[p_180543_4_.ordinal()]) {
         case 3:
            GlStateManager.translate(p_180543_1_ + 0.5F, p_180543_2_ + 0.25F, p_180543_3_ + 0.74F);
            break;
         case 4:
            GlStateManager.translate(p_180543_1_ + 0.5F, p_180543_2_ + 0.25F, p_180543_3_ + 0.26F);
            p_180543_5_ = 180.0F;
            break;
         case 5:
            GlStateManager.translate(p_180543_1_ + 0.74F, p_180543_2_ + 0.25F, p_180543_3_ + 0.5F);
            p_180543_5_ = 270.0F;
            break;
         case 6:
         default:
            GlStateManager.translate(p_180543_1_ + 0.26F, p_180543_2_ + 0.25F, p_180543_3_ + 0.5F);
            p_180543_5_ = 90.0F;
         }
      } else {
         GlStateManager.translate(p_180543_1_ + 0.5F, p_180543_2_, p_180543_3_ + 0.5F);
      }

      float f = 0.0625F;
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(-1.0F, -1.0F, 1.0F);
      GlStateManager.enableAlpha();
      modelbase.render((Entity)null, 0.0F, 0.0F, 0.0F, p_180543_5_, 0.0F, f);
      GlStateManager.popMatrix();
      if (p_180543_8_ >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }

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
