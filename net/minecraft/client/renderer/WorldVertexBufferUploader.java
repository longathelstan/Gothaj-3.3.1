package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.SVertexBuilder;
import org.lwjgl.opengl.GL11;

public class WorldVertexBufferUploader {
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$net$minecraft$client$renderer$vertex$VertexFormatElement$EnumUsage;

   public void draw(WorldRenderer p_181679_1_) {
      if (p_181679_1_.getVertexCount() > 0) {
         if (p_181679_1_.getDrawMode() == 7 && Config.isQuadsToTriangles()) {
            p_181679_1_.quadsToTriangles();
         }

         VertexFormat vertexformat = p_181679_1_.getVertexFormat();
         int i = vertexformat.getNextOffset();
         ByteBuffer bytebuffer = p_181679_1_.getByteBuffer();
         List<VertexFormatElement> list = vertexformat.getElements();
         boolean flag = Reflector.ForgeVertexFormatElementEnumUseage_preDraw.exists();
         boolean flag1 = Reflector.ForgeVertexFormatElementEnumUseage_postDraw.exists();

         int j1;
         int i1;
         for(j1 = 0; j1 < list.size(); ++j1) {
            VertexFormatElement vertexformatelement = (VertexFormatElement)list.get(j1);
            VertexFormatElement.EnumUsage vertexformatelement$enumusage = vertexformatelement.getUsage();
            if (flag) {
               Reflector.callVoid(vertexformatelement$enumusage, Reflector.ForgeVertexFormatElementEnumUseage_preDraw, vertexformat, j1, i, bytebuffer);
            } else {
               int k = vertexformatelement.getType().getGlConstant();
               i1 = vertexformatelement.getIndex();
               bytebuffer.position(vertexformat.getOffset(j1));
               switch($SWITCH_TABLE$net$minecraft$client$renderer$vertex$VertexFormatElement$EnumUsage()[vertexformatelement$enumusage.ordinal()]) {
               case 1:
                  GL11.glVertexPointer(vertexformatelement.getElementCount(), k, i, bytebuffer);
                  GL11.glEnableClientState(32884);
                  break;
               case 2:
                  GL11.glNormalPointer(k, i, bytebuffer);
                  GL11.glEnableClientState(32885);
                  break;
               case 3:
                  GL11.glColorPointer(vertexformatelement.getElementCount(), k, i, bytebuffer);
                  GL11.glEnableClientState(32886);
                  break;
               case 4:
                  OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + i1);
                  GL11.glTexCoordPointer(vertexformatelement.getElementCount(), k, i, bytebuffer);
                  GL11.glEnableClientState(32888);
                  OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
               }
            }
         }

         if (p_181679_1_.isMultiTexture()) {
            p_181679_1_.drawMultiTexture();
         } else if (Config.isShaders()) {
            SVertexBuilder.drawArrays(p_181679_1_.getDrawMode(), 0, p_181679_1_.getVertexCount(), p_181679_1_);
         } else {
            GL11.glDrawArrays(p_181679_1_.getDrawMode(), 0, p_181679_1_.getVertexCount());
         }

         j1 = 0;

         for(int k1 = list.size(); j1 < k1; ++j1) {
            VertexFormatElement vertexformatelement1 = (VertexFormatElement)list.get(j1);
            VertexFormatElement.EnumUsage vertexformatelement$enumusage1 = vertexformatelement1.getUsage();
            if (flag1) {
               Reflector.callVoid(vertexformatelement$enumusage1, Reflector.ForgeVertexFormatElementEnumUseage_postDraw, vertexformat, j1, i, bytebuffer);
            } else {
               i1 = vertexformatelement1.getIndex();
               switch($SWITCH_TABLE$net$minecraft$client$renderer$vertex$VertexFormatElement$EnumUsage()[vertexformatelement$enumusage1.ordinal()]) {
               case 1:
                  GL11.glDisableClientState(32884);
                  break;
               case 2:
                  GL11.glDisableClientState(32885);
                  break;
               case 3:
                  GL11.glDisableClientState(32886);
                  GlStateManager.resetColor();
                  break;
               case 4:
                  OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + i1);
                  GL11.glDisableClientState(32888);
                  OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
               }
            }
         }
      }

      p_181679_1_.reset();
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$minecraft$client$renderer$vertex$VertexFormatElement$EnumUsage() {
      int[] var10000 = $SWITCH_TABLE$net$minecraft$client$renderer$vertex$VertexFormatElement$EnumUsage;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[VertexFormatElement.EnumUsage.values().length];

         try {
            var0[VertexFormatElement.EnumUsage.BLEND_WEIGHT.ordinal()] = 6;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[VertexFormatElement.EnumUsage.COLOR.ordinal()] = 3;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[VertexFormatElement.EnumUsage.MATRIX.ordinal()] = 5;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[VertexFormatElement.EnumUsage.NORMAL.ordinal()] = 2;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[VertexFormatElement.EnumUsage.PADDING.ordinal()] = 7;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[VertexFormatElement.EnumUsage.POSITION.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[VertexFormatElement.EnumUsage.UV.ordinal()] = 4;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$net$minecraft$client$renderer$vertex$VertexFormatElement$EnumUsage = var0;
         return var0;
      }
   }
}
