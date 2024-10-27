package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import xyz.cucumber.base.Client;
import xyz.cucumber.base.module.feat.visuals.BetterItemsModule;

public class RenderEntityItem extends Render<EntityItem> {
   private final RenderItem itemRenderer;
   private Random field_177079_e = new Random();

   public RenderEntityItem(RenderManager renderManagerIn, RenderItem p_i46167_2_) {
      super(renderManagerIn);
      this.itemRenderer = p_i46167_2_;
      this.shadowSize = 0.15F;
      this.shadowOpaque = 0.75F;
   }

   private int func_177077_a(EntityItem itemIn, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_) {
      BetterItemsModule mod = (BetterItemsModule)Client.INSTANCE.getModuleManager().getModule(BetterItemsModule.class);
      ItemStack itemstack = itemIn.getEntityItem();
      Item item = itemstack.getItem();
      if (item == null) {
         return 0;
      } else {
         boolean flag = p_177077_9_.isGui3d();
         int i = this.func_177078_a(itemstack);
         float f = 0.25F;
         float f1 = MathHelper.sin(((float)itemIn.getAge() + p_177077_8_) / 10.0F + itemIn.hoverStart) * 0.1F + 0.1F;
         if (mod.isEnabled()) {
            f1 = 0.0F;
         }

         float f2 = p_177077_9_.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
         if (mod.isEnabled()) {
            GlStateManager.translate((float)p_177077_2_, (float)p_177077_4_ + f1 + 0.05F * f2, (float)p_177077_6_);
         } else {
            GlStateManager.translate((float)p_177077_2_, (float)p_177077_4_ + f1 + 0.25F * f2, (float)p_177077_6_);
         }

         float f3;
         if (flag || this.renderManager.options != null) {
            f3 = (((float)itemIn.getAge() + p_177077_8_) / 20.0F + itemIn.hoverStart) * 57.295776F;
            if (mod.isEnabled()) {
               if (itemIn.onGround) {
                  GL11.glRotatef(itemIn.rotationYaw, 0.0F, 1.0F, 0.0F);
                  GL11.glRotatef(itemIn.rotationPitch + 90.0F, 1.0F, 0.0F, 0.0F);
               } else {
                  for(int a = 0; a < 10; ++a) {
                     GL11.glRotatef(f3, 0.7F, 0.7F, 0.0F);
                  }
               }
            } else {
               GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
            }
         }

         if (!flag) {
            f3 = -0.0F * (float)(i - 1) * 0.5F;
            float f4 = -0.0F * (float)(i - 1) * 0.5F;
            float f5 = -0.046875F * (float)(i - 1) * 0.5F;
            GlStateManager.translate(f3, f4, f5);
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         return i;
      }
   }

   private int func_177078_a(ItemStack stack) {
      int i = 1;
      if (stack.stackSize > 48) {
         i = 5;
      } else if (stack.stackSize > 32) {
         i = 4;
      } else if (stack.stackSize > 16) {
         i = 3;
      } else if (stack.stackSize > 1) {
         i = 2;
      }

      return i;
   }

   public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
      ItemStack itemstack = entity.getEntityItem();
      this.field_177079_e.setSeed(187L);
      boolean flag = false;
      if (this.bindEntityTexture(entity)) {
         this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
         flag = true;
      }

      GlStateManager.enableRescaleNormal();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.pushMatrix();
      IBakedModel ibakedmodel = this.itemRenderer.getItemModelMesher().getItemModel(itemstack);
      int i = this.func_177077_a(entity, x, y, z, partialTicks, ibakedmodel);

      for(int j = 0; j < i; ++j) {
         float f;
         float f1;
         float f2;
         if (ibakedmodel.isGui3d()) {
            GlStateManager.pushMatrix();
            if (j > 0) {
               f = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
               f1 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
               f2 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
               GlStateManager.translate(f, f1, f2);
            }

            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            ibakedmodel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
            this.itemRenderer.renderItem(itemstack, ibakedmodel);
            GlStateManager.popMatrix();
         } else {
            GlStateManager.pushMatrix();
            ibakedmodel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
            this.itemRenderer.renderItem(itemstack, ibakedmodel);
            GlStateManager.popMatrix();
            f = ibakedmodel.getItemCameraTransforms().ground.scale.x;
            f1 = ibakedmodel.getItemCameraTransforms().ground.scale.y;
            f2 = ibakedmodel.getItemCameraTransforms().ground.scale.z;
            GlStateManager.translate(0.0F * f, 0.0F * f1, 0.046875F * f2);
         }
      }

      GlStateManager.popMatrix();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
      this.bindEntityTexture(entity);
      if (flag) {
         this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
      }

      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   protected ResourceLocation getEntityTexture(EntityItem entity) {
      return TextureMap.locationBlocksTexture;
   }
}
