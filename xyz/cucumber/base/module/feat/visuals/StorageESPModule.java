package xyz.cucumber.base.module.feat.visuals;

import java.util.Iterator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import xyz.cucumber.base.events.EventListener;
import xyz.cucumber.base.events.ext.EventRender3D;
import xyz.cucumber.base.events.ext.EventRenderGui;
import xyz.cucumber.base.module.Category;
import xyz.cucumber.base.module.Mod;
import xyz.cucumber.base.module.ModuleInfo;
import xyz.cucumber.base.module.settings.ColorSettings;
import xyz.cucumber.base.module.settings.ModeSettings;
import xyz.cucumber.base.module.settings.ModuleSettings;
import xyz.cucumber.base.module.settings.NumberSettings;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.ColorUtils;
import xyz.cucumber.base.utils.render.GlowUtils;
import xyz.cucumber.base.utils.render.StencilUtils;

@ModuleInfo(
   category = Category.VISUALS,
   description = "Allows you see storages throw obsticles",
   name = "Storage ESP"
)
public class StorageESPModule extends Mod {
   public ModeSettings mode = new ModeSettings("Mode", new String[]{"Box", "Box2D", "Outline"});
   public ColorSettings fillColor = new ColorSettings("Fill Color", () -> {
      return !this.mode.getMode().toLowerCase().equals("outline");
   }, "Static", -1, -1, 100);
   public ModeSettings outline = new ModeSettings("Outline 2D", () -> {
      return !this.mode.getMode().toLowerCase().equals("outline");
   }, new String[]{"None", "Full", "Brackets", "Arrows"});
   public NumberSettings settings = new NumberSettings("Outline radius", () -> {
      return this.mode.getMode().toLowerCase().equals("outline");
   }, 7.0D, 0.0D, 15.0D, 1.0D);
   public NumberSettings sensitivity = new NumberSettings("Outline sensitivity", () -> {
      return this.mode.getMode().toLowerCase().equals("outline");
   }, 3.0D, 0.0D, 15.0D, 0.1D);
   public ModeSettings outlineMode = new ModeSettings("Outline Mode", () -> {
      return this.mode.getMode().toLowerCase().equals("outline");
   }, new String[]{"Glow", "Glitch", "Wave"});
   public ColorSettings outlineColor = new ColorSettings("Outline Color", "Static", -1, -1, 100);
   GlowUtils glow = new GlowUtils();
   private long time;

   public StorageESPModule() {
      this.addSettings(new ModuleSettings[]{this.mode, this.fillColor, this.outline, this.outlineColor, this.settings, this.sensitivity, this.outlineMode});
   }

   public void onEnable() {
      this.time = System.currentTimeMillis();
   }

   @EventListener
   public void onRender3D(EventRender3D e) {
      if (this.mode.getMode().toLowerCase().equals("outline")) {
         this.glow.pre();
      }

      Iterator var3 = this.mc.theWorld.loadedTileEntityList.iterator();

      while(true) {
         TileEntity tile;
         do {
            if (!var3.hasNext()) {
               if (this.mode.getMode().toLowerCase().equals("outline")) {
                  this.glow.after();
               }

               return;
            }

            tile = (TileEntity)var3.next();
         } while(!(tile instanceof TileEntityChest) && !(tile instanceof TileEntityEnderChest));

         double x = (double)tile.getPos().getX() - this.mc.getRenderManager().viewerPosX;
         double y = (double)tile.getPos().getY() - this.mc.getRenderManager().viewerPosY;
         double z = (double)tile.getPos().getZ() - this.mc.getRenderManager().viewerPosZ;
         AxisAlignedBB bb = new AxisAlignedBB(x, y + 1.0D, z, x + 1.0D, y, z + 1.0D);
         String var11;
         switch((var11 = this.mode.getMode().toLowerCase()).hashCode()) {
         case -1106245566:
            if (var11.equals("outline")) {
               TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, x, y, z, e.getPartialTicks());
            }
            break;
         case 97739:
            if (var11.equals("box")) {
               GlStateManager.pushMatrix();
               RenderUtils.start3D();
               RenderUtils.color(ColorUtils.getColor(this.fillColor, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D));
               RenderUtils.renderHitbox(bb, 7);
               RenderUtils.color(ColorUtils.getColor(this.outlineColor, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D));
               RenderUtils.renderHitbox(bb, 2);
               RenderUtils.stop3D();
               GlStateManager.popMatrix();
            }
            break;
         case 93928829:
            if (var11.equals("box2d")) {
               float f = 1.6F;
               float f1 = 0.016666668F * f;
               GlStateManager.pushMatrix();
               GlStateManager.translate((double)((float)x) + 0.5D, (double)((float)y) + 0.5D, (double)((float)z) + 0.5D);
               GL11.glNormal3f(0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(this.mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
               GlStateManager.scale(-f1, -f1, f1);
               int i = false;
               RenderUtils.drawRect(-25.0D, -25.0D, 25.0D, 25.0D, ColorUtils.getColor(this.fillColor, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D));
               this.renderOutline(new PositionUtils(-25.0D, -25.0D, 50.0D, 50.0D, 1.0F));
               GlStateManager.popMatrix();
            }
         }
      }
   }

   public void renderOutline(PositionUtils pos) {
      String var2;
      switch((var2 = this.outline.getMode().toLowerCase()).hashCode()) {
      case -1409151606:
         if (var2.equals("arrows")) {
            double aw = pos.getWidth() / 4.0D;
            this.renderBracket(pos.getX() + aw - 0.3D, pos.getY(), pos.getX(), pos.getY(), pos.getX(), pos.getY() + aw + 0.3D, -1879048192, 1.6D);
            this.renderBracket(pos.getX2() - aw - 0.3D, pos.getY(), pos.getX2(), pos.getY(), pos.getX2(), pos.getY() + aw + 0.3D, -1879048192, 1.6D);
            this.renderBracket(pos.getX(), pos.getY2() - aw - 0.3D, pos.getX(), pos.getY2(), pos.getX() + aw + 0.3D, pos.getY2(), -1879048192, 1.6D);
            this.renderBracket(pos.getX2(), pos.getY2() - aw - 0.3D, pos.getX2(), pos.getY2(), pos.getX2() - aw - 0.3D, pos.getY2(), -1879048192, 1.6D);
         }
         break;
      case -35329845:
         if (var2.equals("brackets")) {
            double bw = pos.getWidth() / 4.0D;
            this.renderBracket(new double[]{pos.getX(), pos.getY()}, new double[]{pos.getX(), pos.getY2()}, bw + 0.3D, -1879048192, 1.6D);
            this.renderBracket(new double[]{pos.getX2(), pos.getY()}, new double[]{pos.getX2(), pos.getY2()}, -bw - 0.3D, -1879048192, 1.6D);
         }
         break;
      case 3154575:
         if (var2.equals("full")) {
            RenderUtils.drawOutlinedRect(pos.getX(), pos.getY(), pos.getX2(), pos.getY2(), -1879048192, 1.6F);
         }
      }

      if (!this.outline.getMode().toLowerCase().equals("none")) {
         StencilUtils.initStencil();
         GL11.glEnable(2960);
         StencilUtils.bindWriteStencilBuffer();
         double z;
         String var8;
         switch((var8 = this.outline.getMode().toLowerCase()).hashCode()) {
         case -1409151606:
            if (var8.equals("arrows")) {
               double aw = pos.getWidth() / 4.0D;
               this.renderBracket(pos.getX() + aw, pos.getY(), pos.getX(), pos.getY(), pos.getX(), pos.getY() + aw, -1, 1.0D);
               this.renderBracket(pos.getX2() - aw, pos.getY(), pos.getX2(), pos.getY(), pos.getX2(), pos.getY() + aw, -1, 1.0D);
               this.renderBracket(pos.getX(), pos.getY2() - aw, pos.getX(), pos.getY2(), pos.getX() + aw, pos.getY2(), -1, 1.0D);
               this.renderBracket(pos.getX2(), pos.getY2() - aw, pos.getX2(), pos.getY2(), pos.getX2() - aw, pos.getY2(), -1, 1.0D);
            }
            break;
         case -35329845:
            if (var8.equals("brackets")) {
               z = pos.getWidth() / 4.0D;
               this.renderBracket(new double[]{pos.getX(), pos.getY()}, new double[]{pos.getX(), pos.getY2()}, z, -1, 1.0D);
               this.renderBracket(new double[]{pos.getX2(), pos.getY()}, new double[]{pos.getX2(), pos.getY2()}, -z, -1, 1.0D);
            }
            break;
         case 3154575:
            if (var8.equals("full")) {
               RenderUtils.drawOutlinedRect(pos.getX(), pos.getY(), pos.getX2(), pos.getY2(), -1, 1.0F);
            }
         }

         StencilUtils.bindReadStencilBuffer(1);
         GL11.glPushMatrix();
         RenderUtils.start2D();
         GL11.glShadeModel(7425);
         GL11.glBegin(6);
         GL11.glVertex2d(pos.getX() + pos.getWidth() / 2.0D, pos.getY() + pos.getHeight() / 2.0D);

         for(z = 0.0D; z <= 360.0D; z += 5.0D) {
            RenderUtils.color(ColorUtils.getColor(this.outlineColor, (double)(System.nanoTime() / 1000000L), z, 5.0D));
            GL11.glVertex2d(pos.getX() + pos.getWidth() / 2.0D + Math.sin(z * 3.141592653589793D / 180.0D) * Math.sqrt(pos.getWidth() * pos.getWidth() + pos.getHeight() * pos.getHeight()) / 2.0D, pos.getY() + pos.getHeight() / 2.0D - Math.cos(z * 3.141592653589793D / 180.0D) * Math.sqrt(pos.getWidth() * pos.getWidth() + pos.getHeight() * pos.getHeight()) / 2.0D);
         }

         GL11.glEnd();
         RenderUtils.stop2D();
         GlStateManager.resetColor();
         GL11.glPopMatrix();
         StencilUtils.uninitStencilBuffer();
      }

   }

   public void renderBracket(double[] pos1, double[] pos2, double width, int color, double size) {
      GlStateManager.pushMatrix();
      RenderUtils.start2D();
      RenderUtils.color(color);
      GL11.glLineWidth((float)size);
      GL11.glBegin(3);
      GL11.glVertex2d(pos1[0] + width, pos1[1]);
      GL11.glVertex2d(pos1[0], pos1[1]);
      GL11.glVertex2d(pos2[0], pos2[1]);
      GL11.glVertex2d(pos2[0] + width, pos2[1]);
      GL11.glEnd();
      RenderUtils.color(-1);
      RenderUtils.stop2D();
      GlStateManager.popMatrix();
   }

   public void renderBracket(double x, double y, double x1, double y1, double x2, double y2, int color, double size) {
      GlStateManager.pushMatrix();
      RenderUtils.start2D();
      RenderUtils.color(color);
      GL11.glLineWidth((float)size);
      GL11.glBegin(3);
      GL11.glVertex2d(x, y);
      GL11.glVertex2d(x1, y1);
      GL11.glVertex2d(x2, y2);
      GL11.glEnd();
      RenderUtils.color(-1);
      RenderUtils.stop2D();
      GlStateManager.popMatrix();
   }

   @EventListener
   public void onRenderGui(EventRenderGui e) {
      if (this.mode.getMode().toLowerCase().equals("outline")) {
         this.glow.post((float)this.sensitivity.getValue(), (float)this.settings.getValue(), 1.0F, this.time, this.outlineMode.getModes().indexOf(this.outlineMode.getMode()), this.outlineColor);
      }

   }
}
