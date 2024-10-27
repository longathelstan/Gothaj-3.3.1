package xyz.cucumber.base.interf.DropdownClickGui.ext.Settings;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import xyz.cucumber.base.module.settings.ColorSettings;
import xyz.cucumber.base.module.settings.ModuleSettings;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.Fonts;

public class DropdownClickGuiColor extends DropdownClickGuiSettings {
   private ColorSettings setting;
   private PositionUtils color1 = new PositionUtils(0.0D, 0.0D, 50.0D, 50.0D, 1.0F);
   private PositionUtils hue1 = new PositionUtils(0.0D, 0.0D, 50.0D, 4.0D, 1.0F);
   double h1 = 0.0D;
   double h2 = 0.0D;
   private float[] pointer1 = new float[]{0.0F, 0.0F};
   private float[] pointer2 = new float[]{0.0F, 0.0F};
   private boolean open;
   private boolean dragging;
   private boolean draggingHue2;
   private boolean draggingAlpha;
   private boolean draggingColor1;
   private boolean draggingColor2;
   private double defaultheight = 12.0D;
   private PositionUtils mode = new PositionUtils(0.0D, 0.0D, 100.0D, 10.0D, 0.0F);
   private PositionUtils color2 = new PositionUtils(0.0D, 0.0D, 50.0D, 50.0D, 1.0F);
   private PositionUtils hue2 = new PositionUtils(0.0D, 0.0D, 50.0D, 4.0D, 1.0F);
   private PositionUtils alpha = new PositionUtils(0.0D, 0.0D, 50.0D, 4.0D, 1.0F);
   private PositionUtils color1Point = new PositionUtils(0.0D, 0.0D, 4.0D, 4.0D, 1.0F);
   private PositionUtils color2Point = new PositionUtils(0.0D, 0.0D, 4.0D, 4.0D, 1.0F);
   private PositionUtils hue1Point = new PositionUtils(0.0D, 0.0D, 4.0D, 4.0D, 1.0F);
   private PositionUtils hue2Point = new PositionUtils(0.0D, 0.0D, 4.0D, 4.0D, 1.0F);
   private PositionUtils alphaPoint = new PositionUtils(0.0D, 0.0D, 4.0D, 4.0D, 1.0F);

   public DropdownClickGuiColor(ModuleSettings setting, PositionUtils positionUtils) {
      this.setting = (ColorSettings)setting;
      this.position = positionUtils;
      this.mainSetting = setting;
      Color c = new Color(this.setting.getMainColor());
      this.h1 = (double)Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), (float[])null)[0];
      Color c1 = new Color(this.setting.getSecondaryColor());
      this.h2 = (double)Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), (float[])null)[0];
      this.pointer1 = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), (float[])null);
      this.pointer2 = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), (float[])null);
   }

   public void draw(int mouseX, int mouseY) {
      Fonts.getFont("rb-m-13").drawString(this.setting.getName(), this.position.getX() + 3.0D, this.position.getY() + 3.0D, -1);
      RenderUtils.drawMixedRoundedRect(this.position.getX2() - 20.0D, this.position.getY() + 2.0D, this.position.getX2() - 5.0D, this.position.getY() + this.defaultheight - 2.0D, this.setting.getMainColor(), this.setting.getSecondaryColor(), 2.0D);
      if (this.open) {
         this.getPosition().setHeight(150.0D);
         this.color1.setX(this.getPosition().getX() + 25.0D);
         this.color1.setY(this.getPosition().getY() + this.defaultheight);
         this.hue1.setX(this.getPosition().getX() + 25.0D);
         this.hue1.setY(this.getPosition().getY() + this.defaultheight + 50.0D);
         this.color2.setX(this.getPosition().getX() + 25.0D);
         this.color2.setY(this.getPosition().getY() + this.defaultheight + 60.0D);
         this.hue2.setX(this.getPosition().getX() + 25.0D);
         this.hue2.setY(this.getPosition().getY() + this.defaultheight + 110.0D);
         this.alpha.setX(this.getPosition().getX() + 25.0D);
         this.alpha.setY(this.getPosition().getY() + this.defaultheight + 110.0D + 10.0D);
         RenderUtils.drawColorPicker(this.color1.getX(), this.color1.getY(), this.color1.getWidth(), this.color1.getHeight(), (float)this.h1);
         RenderUtils.drawColorSlider(this.hue1.getX(), this.hue1.getY(), this.hue1.getWidth(), this.hue1.getHeight());
         RenderUtils.drawColorPicker(this.color2.getX(), this.color2.getY(), this.color2.getWidth(), this.color2.getHeight(), (float)this.h2);
         RenderUtils.drawColorSlider(this.hue2.getX(), this.hue2.getY(), this.hue2.getWidth(), this.hue2.getHeight());
         RenderUtils.drawImage(this.alpha.getX(), this.alpha.getY(), this.alpha.getWidth(), this.alpha.getHeight(), new ResourceLocation("client/images/alpha.png"), -1);
         GlStateManager.pushMatrix();
         RenderUtils.start2D();
         GL11.glShadeModel(7425);
         GL11.glBegin(7);
         RenderUtils.color(-65536);
         GL11.glVertex2d(this.alpha.getX(), this.alpha.getY());
         RenderUtils.color(16711680);
         GL11.glVertex2d(this.alpha.getX2(), this.alpha.getY());
         GL11.glVertex2d(this.alpha.getX2(), this.alpha.getY2());
         RenderUtils.color(-65536);
         GL11.glVertex2d(this.alpha.getX(), this.alpha.getY2());
         GL11.glEnd();
         RenderUtils.stop2D();
         GlStateManager.popMatrix();
         Color c = new Color(this.setting.getMainColor());
         float[] a = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), (float[])null);
         this.color1Point.setX(this.color1.getX() + this.color1.getWidth() * (double)a[1] - this.color1Point.getWidth() / 2.0D);
         this.color1Point.setY(this.color1.getY() + this.color1.getHeight() * (double)(1.0F - a[2]) - this.color1Point.getHeight() / 2.0D);
         RenderUtils.drawRoundedRect(this.color1Point.getX() - 0.5D, this.color1Point.getY() - 0.5D, this.color1Point.getX2() + 0.5D, this.color1Point.getY2() + 0.5D, -1, 1.0F);
         RenderUtils.drawRoundedRect(this.color1Point.getX(), this.color1Point.getY(), this.color1Point.getX2(), this.color1Point.getY2(), c.getRGB(), 1.0F);
         this.hue1Point.setX(this.hue1.getX() + this.hue1.getWidth() * this.h1 - this.hue1Point.getHeight() / 2.0D);
         this.hue1Point.setY(this.hue1.getY());
         RenderUtils.drawRoundedRect(this.hue1Point.getX() - 1.0D, this.hue1Point.getY() - 1.0D, this.hue1Point.getX2() + 1.0D, this.hue1Point.getY2() + 1.0D, -1, 1.0F);
         RenderUtils.drawRoundedRect(this.hue1Point.getX() - 0.5D, this.hue1Point.getY() - 0.5D, this.hue1Point.getX2() + 0.5D, this.hue1Point.getY2() + 0.5D, Color.HSBtoRGB((float)this.h2, 1.0F, 1.0F), 1.0F);
         c = new Color(this.setting.getSecondaryColor());
         a = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), (float[])null);
         this.color2Point.setX(this.color2.getX() + this.color2.getWidth() * (double)a[1] - this.color2Point.getWidth() / 2.0D);
         this.color2Point.setY(this.color2.getY() + this.color2.getHeight() * (double)(1.0F - a[2]) - this.color2Point.getHeight() / 2.0D);
         RenderUtils.drawRoundedRect(this.color2Point.getX() - 0.5D, this.color2Point.getY() - 0.5D, this.color2Point.getX2() + 0.5D, this.color2Point.getY2() + 0.5D, -1, 1.0F);
         RenderUtils.drawRoundedRect(this.color2Point.getX(), this.color2Point.getY(), this.color2Point.getX2(), this.color2Point.getY2(), c.getRGB(), 1.0F);
         this.hue2Point.setX(this.hue2.getX() + this.hue2.getWidth() * this.h2 - this.hue2Point.getHeight() / 2.0D);
         this.hue2Point.setY(this.hue2.getY());
         RenderUtils.drawRoundedRect(this.hue2Point.getX() - 1.0D, this.hue2Point.getY() - 1.0D, this.hue2Point.getX2() + 1.0D, this.hue2Point.getY2() + 1.0D, -1, 1.0F);
         RenderUtils.drawRoundedRect(this.hue2Point.getX() - 0.5D, this.hue2Point.getY() - 0.5D, this.hue2Point.getX2() + 0.5D, this.hue2Point.getY2() + 0.5D, Color.HSBtoRGB((float)this.h2, 1.0F, 1.0F), 1.0F);
         double diffX;
         double diffY;
         if (this.dragging) {
            diffX = Math.min(this.hue1.getWidth(), Math.max(0.0D, (double)mouseX - this.hue1.getX()));
            diffY = this.roundToPlace(diffX / this.hue1.getWidth() * 1.0D, (int)this.hue1.getWidth());
            this.h1 = (double)((float)diffY);
         }

         if (this.draggingHue2) {
            diffX = Math.min(this.hue2.getWidth(), Math.max(0.0D, (double)mouseX - this.hue2.getX()));
            diffY = this.roundToPlace(diffX / this.hue2.getWidth() * 1.0D, (int)this.hue2.getWidth());
            this.h2 = (double)((float)diffY);
         }

         if (this.draggingAlpha) {
            diffX = Math.min(this.alpha.getWidth(), Math.max(0.0D, (double)mouseX - this.alpha.getX()));
            diffY = this.roundToPlace(diffX / this.alpha.getWidth() * 100.0D, (int)this.alpha.getWidth());
            this.setting.setAlpha(100 - (int)Math.round(diffY));
         }

         double newValueX;
         double newValueY;
         if (this.draggingColor1) {
            diffX = Math.min(this.color1.getWidth(), Math.max(0.0D, (double)mouseX - this.color1.getX()));
            diffY = Math.min(this.color1.getHeight(), Math.max(0.0D, (double)mouseY - this.color1.getY()));
            newValueX = this.roundToPlace(diffX / this.color1.getWidth() * 1.0D, (int)this.color1.getWidth());
            newValueY = this.roundToPlace(diffY / this.color1.getHeight() * 1.0D, (int)this.color1.getHeight());
            this.setting.setMainColor(Color.HSBtoRGB((float)this.h1, (float)newValueX, 1.0F - (float)newValueY));
            this.pointer1[0] = (float)newValueX;
            this.pointer1[1] = 1.0F - (float)newValueY;
         }

         if (this.draggingColor2) {
            diffX = Math.min(this.color2.getWidth(), Math.max(0.0D, (double)mouseX - this.color2.getX()));
            diffY = Math.min(this.color2.getHeight(), Math.max(0.0D, (double)mouseY - this.color2.getY()));
            newValueX = this.roundToPlace(diffX / this.color2.getWidth() * 1.0D, (int)this.color2.getWidth());
            newValueY = this.roundToPlace(diffY / this.color2.getHeight() * 1.0D, (int)this.color2.getHeight());
            this.setting.setSecondaryColor(Color.HSBtoRGB((float)this.h2, (float)newValueX, 1.0F - (float)newValueY));
            this.pointer2[0] = (float)newValueX;
            this.pointer2[1] = 1.0F - (float)newValueY;
         }

         RenderUtils.drawRoundedRect(this.alpha.getX() - 3.0D + -((double)this.setting.getAlpha() * this.alpha.getWidth()) / 100.0D + this.alpha.getWidth(), this.alpha.getY() - 1.0D, this.alpha.getX() + 3.0D + -((double)this.setting.getAlpha() * this.alpha.getWidth()) / 100.0D + this.alpha.getWidth(), this.alpha.getY2() + 1.0D, -5592406, 2.0F);
         RenderUtils.drawRoundedRect(this.alpha.getX() - 2.5D + -((double)this.setting.getAlpha() * this.alpha.getWidth()) / 100.0D + this.alpha.getWidth(), this.alpha.getY() - 0.5D, this.alpha.getX() + 2.5D + -((double)this.setting.getAlpha() * this.alpha.getWidth()) / 100.0D + this.alpha.getWidth(), this.alpha.getY2() + 0.5D, -1, 2.0F);
         this.mode.setX(this.getPosition().getX());
         this.mode.setY(this.getPosition().getY() + 140.0D);
         Fonts.getFont("rb-m-13").drawString("Mode", this.mode.getX() + 7.0D, this.mode.getY() + 3.0D, -1);
         RenderUtils.drawRoundedRectWithCorners(this.mode.getX() + Fonts.getFont("rb-m-13").getWidth("Mode") + 5.0D - 1.0D + 4.0D, this.mode.getY() + this.mode.getHeight() / 2.0D - 4.0D, this.mode.getX() + 4.0D + Fonts.getFont("rb-m-13").getWidth("Mode") + 6.0D + Fonts.getFont("rb-m-13").getWidth(this.setting.getMode()), this.mode.getY() + this.mode.getHeight() / 2.0D + 4.0D, -13354432, 2.0D, true, true, true, true);
         Fonts.getFont("rb-m-13").drawString(this.setting.getMode(), this.mode.getX() + 4.0D + Fonts.getFont("rb-m-13").getWidth("Mode") + 5.5D, this.mode.getY() + 3.0D, -1);
      } else {
         this.getPosition().setHeight(this.defaultheight);
      }

   }

   public void onClick(int mouseX, int mouseY, int button) {
      if (button == 1) {
         this.open = !this.open;
      }

      if (button == 0 && this.mode.isInside(mouseX, mouseY)) {
         this.setting.cycleModes();
      }

      if (this.hue1.isInside(mouseX, mouseY) && button == 0) {
         this.dragging = true;
      }

      if (this.hue2.isInside(mouseX, mouseY) && button == 0) {
         this.draggingHue2 = true;
      }

      if (this.alpha.isInside(mouseX, mouseY) && button == 0) {
         this.draggingAlpha = true;
      }

      if (this.color1.isInside(mouseX, mouseY) && button == 0) {
         this.draggingColor1 = true;
      }

      if (this.color2.isInside(mouseX, mouseY) && button == 0) {
         this.draggingColor2 = true;
      }

   }

   public void onRelease(int mouseX, int mouseY, int button) {
      this.dragging = false;
      this.draggingHue2 = false;
      this.draggingAlpha = false;
      this.draggingColor1 = false;
      this.draggingColor2 = false;
   }

   private double roundToPlace(double value, int places) {
      if (places < 0) {
         throw new IllegalArgumentException();
      } else {
         BigDecimal bd = new BigDecimal(value);
         bd = bd.setScale(places, RoundingMode.HALF_UP);
         return bd.doubleValue();
      }
   }
}
