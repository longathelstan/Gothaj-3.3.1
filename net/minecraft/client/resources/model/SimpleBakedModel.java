package net.minecraft.client.resources.model;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BreakingFour;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class SimpleBakedModel implements IBakedModel {
   protected final List<BakedQuad> generalQuads;
   protected final List<List<BakedQuad>> faceQuads;
   protected final boolean ambientOcclusion;
   protected final boolean gui3d;
   protected final TextureAtlasSprite texture;
   protected final ItemCameraTransforms cameraTransforms;

   public SimpleBakedModel(List<BakedQuad> generalQuadsIn, List<List<BakedQuad>> faceQuadsIn, boolean ambientOcclusionIn, boolean gui3dIn, TextureAtlasSprite textureIn, ItemCameraTransforms cameraTransformsIn) {
      this.generalQuads = generalQuadsIn;
      this.faceQuads = faceQuadsIn;
      this.ambientOcclusion = ambientOcclusionIn;
      this.gui3d = gui3dIn;
      this.texture = textureIn;
      this.cameraTransforms = cameraTransformsIn;
   }

   public List<BakedQuad> getFaceQuads(EnumFacing facing) {
      return (List)this.faceQuads.get(facing.ordinal());
   }

   public List<BakedQuad> getGeneralQuads() {
      return this.generalQuads;
   }

   public boolean isAmbientOcclusion() {
      return this.ambientOcclusion;
   }

   public boolean isGui3d() {
      return this.gui3d;
   }

   public boolean isBuiltInRenderer() {
      return false;
   }

   public TextureAtlasSprite getParticleTexture() {
      return this.texture;
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return this.cameraTransforms;
   }

   public static class Builder {
      private final List<BakedQuad> builderGeneralQuads;
      private final List<List<BakedQuad>> builderFaceQuads;
      private final boolean builderAmbientOcclusion;
      private TextureAtlasSprite builderTexture;
      private boolean builderGui3d;
      private ItemCameraTransforms builderCameraTransforms;

      public Builder(ModelBlock model) {
         this(model.isAmbientOcclusion(), model.isGui3d(), model.getAllTransforms());
      }

      public Builder(IBakedModel bakedModel, TextureAtlasSprite texture) {
         this(bakedModel.isAmbientOcclusion(), bakedModel.isGui3d(), bakedModel.getItemCameraTransforms());
         this.builderTexture = bakedModel.getParticleTexture();
         EnumFacing[] var6;
         int var5 = (var6 = EnumFacing.values()).length;

         for(int var4 = 0; var4 < var5; ++var4) {
            EnumFacing enumfacing = var6[var4];
            this.addFaceBreakingFours(bakedModel, texture, enumfacing);
         }

         this.addGeneralBreakingFours(bakedModel, texture);
      }

      private void addFaceBreakingFours(IBakedModel bakedModel, TextureAtlasSprite texture, EnumFacing facing) {
         Iterator var5 = bakedModel.getFaceQuads(facing).iterator();

         while(var5.hasNext()) {
            BakedQuad bakedquad = (BakedQuad)var5.next();
            this.addFaceQuad(facing, new BreakingFour(bakedquad, texture));
         }

      }

      private void addGeneralBreakingFours(IBakedModel p_177647_1_, TextureAtlasSprite texture) {
         Iterator var4 = p_177647_1_.getGeneralQuads().iterator();

         while(var4.hasNext()) {
            BakedQuad bakedquad = (BakedQuad)var4.next();
            this.addGeneralQuad(new BreakingFour(bakedquad, texture));
         }

      }

      private Builder(boolean ambientOcclusion, boolean gui3d, ItemCameraTransforms cameraTransforms) {
         this.builderGeneralQuads = Lists.newArrayList();
         this.builderFaceQuads = Lists.newArrayListWithCapacity(6);
         EnumFacing[] var7;
         int var6 = (var7 = EnumFacing.values()).length;

         for(int var5 = 0; var5 < var6; ++var5) {
            EnumFacing var10000 = var7[var5];
            this.builderFaceQuads.add(Lists.newArrayList());
         }

         this.builderAmbientOcclusion = ambientOcclusion;
         this.builderGui3d = gui3d;
         this.builderCameraTransforms = cameraTransforms;
      }

      public SimpleBakedModel.Builder addFaceQuad(EnumFacing facing, BakedQuad quad) {
         ((List)this.builderFaceQuads.get(facing.ordinal())).add(quad);
         return this;
      }

      public SimpleBakedModel.Builder addGeneralQuad(BakedQuad quad) {
         this.builderGeneralQuads.add(quad);
         return this;
      }

      public SimpleBakedModel.Builder setTexture(TextureAtlasSprite texture) {
         this.builderTexture = texture;
         return this;
      }

      public IBakedModel makeBakedModel() {
         if (this.builderTexture == null) {
            throw new RuntimeException("Missing particle!");
         } else {
            return new SimpleBakedModel(this.builderGeneralQuads, this.builderFaceQuads, this.builderAmbientOcclusion, this.builderGui3d, this.builderTexture, this.builderCameraTransforms);
         }
      }
   }
}
