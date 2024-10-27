package xyz.cucumber.base.module.feat.visuals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import xyz.cucumber.base.events.EventListener;
import xyz.cucumber.base.events.ext.EventRenderHotbar;
import xyz.cucumber.base.module.ArrayPriority;
import xyz.cucumber.base.module.Category;
import xyz.cucumber.base.module.Mod;
import xyz.cucumber.base.module.ModuleInfo;
import xyz.cucumber.base.module.settings.ColorSettings;
import xyz.cucumber.base.module.settings.ModeSettings;
import xyz.cucumber.base.module.settings.ModuleSettings;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.render.ColorUtils;
import xyz.cucumber.base.utils.render.Fonts;

@ModuleInfo(
   category = Category.VISUALS,
   description = "Displays hotbar",
   name = "Hotbar",
   priority = ArrayPriority.LOW
)
public class HotbarModule extends Mod {
   private ModeSettings mode = new ModeSettings("Mode", new String[]{"Style 1", "Style 2"});
   public ColorSettings textColor = new ColorSettings("Text Color", "Static", -1, -1, 100);
   public ColorSettings backgroundColor = new ColorSettings("Background Color", "Static", -1, -1, 100);
   private double animation;

   public HotbarModule() {
      this.addSettings(new ModuleSettings[]{this.mode, this.textColor, this.backgroundColor});
   }

   @EventListener
   public void onRenderHotbar(EventRenderHotbar e) {
      e.setCancelled(true);
      RenderUtils.drawRect(0.0D, e.getScaledResolution().getScaledHeight_double() - 23.0D, e.getScaledResolution().getScaledWidth_double(), e.getScaledResolution().getScaledWidth_double(), ColorUtils.getColor(this.backgroundColor, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D));
      double centerX = (double)(e.getScaledResolution().getScaledWidth() / 2 - 103);
      this.animation = (this.animation * 10.0D + (double)(23 * this.mc.thePlayer.inventory.currentItem)) / 11.0D;
      String var4;
      int j;
      double k;
      switch((var4 = this.mode.getMode().toLowerCase()).hashCode()) {
      case -1875215998:
         if (var4.equals("style 1")) {
            this.renderGradientCircle(centerX + 11.0D + this.animation, e.getScaledResolution().getScaledHeight_double() - 11.0D, 13.0D, 1996488704);

            for(j = 0; j < 9; ++j) {
               k = centerX + (double)(23 * j);
               Fonts.getFont("rb-b").drawString(String.valueOf(j + 1), k + 11.0D - Fonts.getFont("rb-b").getWidth(String.valueOf(j + 1)) / 2.0D + 0.5D, e.getScaledResolution().getScaledHeight_double() - 11.0D - 2.0D, -5592406);
            }
         }
         break;
      case -1875215997:
         if (var4.equals("style 2")) {
            RenderUtils.drawRect(centerX + this.animation, (double)(e.getScaledResolution().getScaledHeight() - 23), centerX + this.animation + 23.0D, (double)e.getScaledResolution().getScaledHeight(), 1996488704);
         }
      }

      GL11.glPushMatrix();
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      RenderHelper.enableGUIStandardItemLighting();

      for(j = 0; j < 9; ++j) {
         k = centerX + 3.0D + (double)(23 * j);
         double l = (double)(e.getScaledResolution().getScaledHeight() - 23 + 3);
         if (this.mc.thePlayer.inventory.currentItem != j) {
            e.getGuiIngame().renderHotbarItem(j, (int)k, (int)l, e.getPartialTicks(), this.mc.thePlayer);
         } else {
            GL11.glPushMatrix();
            GL11.glScaled(1.05D, 1.05D, 1.0D);
            e.getGuiIngame().renderHotbarItem(j, (int)(k - k * 0.05D) + 1, (int)(l - l * 0.05D) + 1, e.getPartialTicks(), this.mc.thePlayer);
            GL11.glScaled(1.0D, 1.0D, 1.0D);
            GL11.glPopMatrix();
         }
      }

      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
      GL11.glPopMatrix();
      NetworkPlayerInfo networkPlayerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.mc.thePlayer.getGameProfile().getId());
      int ping = 0;
      if (networkPlayerInfo != null) {
         ping = networkPlayerInfo.getResponseTime();
      }

      double size = 5.0D + Fonts.getFont("rb-b").getWidth("FPS: ") + Fonts.getFont("rb-m").getWidth(String.valueOf(Minecraft.debugFPS)) + 5.0D + Fonts.getFont("rb-b").getWidth("Ping: ") + Fonts.getFont("rb-m").getWidth(String.valueOf(ping)) + 5.0D;
      this.renderCustomText("X:", 5.0D, (double)(e.getScaledResolution().getScaledHeight() - 19));
      Fonts.getFont("rb-m").drawString(String.valueOf(Math.round(this.mc.thePlayer.posX)), 5.0D + Fonts.getFont("rb-b").getWidth("X: "), (double)(e.getScaledResolution().getScaledHeight() - 19), -5592406);
      this.renderCustomText("Y:", 5.0D + Fonts.getFont("rb-b").getWidth("X: ") + Fonts.getFont("rb-m").getWidth(String.valueOf(Math.round(this.mc.thePlayer.posX))) + 5.0D, (double)(e.getScaledResolution().getScaledHeight() - 19));
      Fonts.getFont("rb-m").drawString(String.valueOf(Math.round(this.mc.thePlayer.posY)), 5.0D + Fonts.getFont("rb-b").getWidth("X: ") + Fonts.getFont("rb-m").getWidth(String.valueOf(Math.round(this.mc.thePlayer.posX))) + 5.0D + Fonts.getFont("rb-b").getWidth("Y: "), (double)(e.getScaledResolution().getScaledHeight() - 19), -5592406);
      this.renderCustomText("Y:", 5.0D + Fonts.getFont("rb-b").getWidth("X: ") + Fonts.getFont("rb-m").getWidth(String.valueOf(Math.round(this.mc.thePlayer.posX))) + 5.0D, (double)(e.getScaledResolution().getScaledHeight() - 19));
      this.renderCustomText("Z:", 5.0D + Fonts.getFont("rb-b").getWidth("X: ") + Fonts.getFont("rb-m").getWidth(String.valueOf(Math.round(this.mc.thePlayer.posX))) + 5.0D + Fonts.getFont("rb-b").getWidth("Y: ") + Fonts.getFont("rb-m").getWidth(String.valueOf(Math.round(this.mc.thePlayer.posY))) + 5.0D, (double)(e.getScaledResolution().getScaledHeight() - 19));
      Fonts.getFont("rb-m").drawString(String.valueOf(Math.round(this.mc.thePlayer.posZ)), 5.0D + Fonts.getFont("rb-b").getWidth("X: ") + Fonts.getFont("rb-m").getWidth(String.valueOf(Math.round(this.mc.thePlayer.posX))) + 5.0D + Fonts.getFont("rb-b").getWidth("Y: ") + Fonts.getFont("rb-m").getWidth(String.valueOf(Math.round(this.mc.thePlayer.posY))) + 5.0D + Fonts.getFont("rb-b").getWidth("Z: "), (double)(e.getScaledResolution().getScaledHeight() - 19), -5592406);
      this.renderCustomText("FPS:", 5.0D, (double)(e.getScaledResolution().getScaledHeight() - 8));
      Fonts.getFont("rb-m").drawString(String.valueOf(Math.round((float)Minecraft.debugFPS)), 5.0D + Fonts.getFont("rb-b").getWidth("FPS: "), (double)(e.getScaledResolution().getScaledHeight() - 8), -5592406);
      this.renderCustomText("Ping:", 5.0D + Fonts.getFont("rb-b").getWidth("FPS: ") + Fonts.getFont("rb-m").getWidth(String.valueOf(Minecraft.debugFPS)) + 5.0D, (double)(e.getScaledResolution().getScaledHeight() - 8));
      Fonts.getFont("rb-m").drawString(String.valueOf(ping), 5.0D + Fonts.getFont("rb-b").getWidth("FPS: ") + Fonts.getFont("rb-m").getWidth(String.valueOf(Minecraft.debugFPS)) + 5.0D + Fonts.getFont("rb-b").getWidth("Ping: "), (double)(e.getScaledResolution().getScaledHeight() - 8), -5592406);
      double deltaX = this.mc.thePlayer.posX - this.mc.thePlayer.lastTickPosX;
      double deltaZ = this.mc.thePlayer.posZ - this.mc.thePlayer.lastTickPosZ;
      double bps = (double)Math.round(Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 100.0D * 20.0D) / 100.0D;
      this.renderCustomText("BPS:", 5.0D + Fonts.getFont("rb-b").getWidth("FPS: ") + Fonts.getFont("rb-m").getWidth(String.valueOf(Minecraft.debugFPS)) + 5.0D + Fonts.getFont("rb-b").getWidth("Ping: ") + Fonts.getFont("rb-m").getWidth(String.valueOf(ping)) + 5.0D, (double)(e.getScaledResolution().getScaledHeight() - 8));
      Fonts.getFont("rb-m").drawString(String.valueOf(bps), 5.0D + Fonts.getFont("rb-b").getWidth("FPS: ") + Fonts.getFont("rb-m").getWidth(String.valueOf(Minecraft.debugFPS)) + 5.0D + Fonts.getFont("rb-b").getWidth("Ping: ") + Fonts.getFont("rb-m").getWidth(String.valueOf(ping)) + 5.0D + Fonts.getFont("rb-b").getWidth("BPS: "), (double)(e.getScaledResolution().getScaledHeight() - 8), -5592406);
   }

   public void renderCustomText(String text, double x, double y) {
      String[] s = text.split("");
      double w = 0.0D;
      String[] var12 = s;
      int var11 = s.length;

      for(int var10 = 0; var10 < var11; ++var10) {
         String t = var12[var10];
         Fonts.getFont("rb-b").drawStringWithShadow(t, x + w, y, ColorUtils.getColor(this.textColor, (double)(System.nanoTime() / 1000000L), w, 5.0D), 1073741824);
         w += Fonts.getFont("rb-b").getWidth(t);
      }

   }

   public void renderGradientCircle(double x, double y, double r, int color) {
      GL11.glPushMatrix();
      RenderUtils.start2D();
      GL11.glShadeModel(7425);
      GL11.glBegin(6);
      RenderUtils.color(color);
      GL11.glVertex2d(x, y);

      for(int i = 0; i <= 360; i += 10) {
         RenderUtils.color(ColorUtils.getAlphaColor(color, 0));
         GL11.glVertex2d(x + Math.sin((double)i * 3.141592653589793D / 180.0D) * r, y - Math.cos((double)i * 3.141592653589793D / 180.0D) * r);
      }

      GL11.glEnd();
      RenderUtils.stop2D();
      GL11.glPopMatrix();
   }
}
