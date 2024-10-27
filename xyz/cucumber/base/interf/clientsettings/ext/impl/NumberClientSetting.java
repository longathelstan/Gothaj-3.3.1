package xyz.cucumber.base.interf.clientsettings.ext.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import xyz.cucumber.base.module.settings.ModuleSettings;
import xyz.cucumber.base.module.settings.NumberSettings;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.Fonts;

public class NumberClientSetting extends ClientSetting {
   private NumberSettings setting;
   private boolean dragging;
   private double animation;
   private double anim;
   private PositionUtils slider = new PositionUtils(0.0D, 0.0D, 136.0D, 3.0D, 1.0F);
   private PositionUtils point = new PositionUtils(0.0D, 0.0D, 6.0D, 6.0D, 1.0F);

   public NumberClientSetting(ModuleSettings setting) {
      this.setting = (NumberSettings)setting;
      this.position.setHeight(20.0D);
   }

   public void draw(int mouseX, int mouseY) {
      double diff = Math.min(this.slider.getWidth(), Math.max(0.0D, (double)mouseX - this.slider.getX()));
      this.slider.setX(this.position.getX() + 7.0D);
      this.slider.setY(this.position.getY2() - 8.0D);
      double x;
      if (this.dragging) {
         x = this.roundToPlace(diff / this.slider.getWidth() * (this.setting.getMax() - this.setting.getMin()) + this.setting.getMin(), (int)this.slider.getWidth());
         this.setting.setValue((double)((float)x));
      }

      Fonts.getFont("rb-m").drawString(this.setting.getName(), this.position.getX() + 8.0D, this.position.getY() + 4.0D, -1);
      Fonts.getFont("rb-r").drawString(String.valueOf(this.setting.getValue()), this.position.getX2() - 8.0D - Fonts.getFont("rb-r").getWidth(String.valueOf(this.setting.getValue())), this.position.getY() + 4.0D, -1);
      x = this.slider.getWidth() * (this.setting.getValue() - this.setting.getMin()) / (this.setting.getMax() - this.setting.getMin());
      this.anim = (this.anim * 9.0D + x) / 10.0D;
      RenderUtils.drawRoundedRect(this.slider.getX(), this.slider.getY(), this.slider.getX2(), this.slider.getY2(), -1, 1.0F);
      RenderUtils.drawRoundedRect(this.slider.getX(), this.slider.getY(), this.slider.getX() + this.anim, this.slider.getY2(), -7303024, 1.0F);
      if (this.dragging) {
         this.animation = (this.animation * 9.0D + 3.5D) / 10.0D;
      } else {
         this.animation = (this.animation * 9.0D + 0.2D) / 10.0D;
      }

      this.point.setX(this.position.getX() + 7.0D + (this.anim - 1.5D));
      this.point.setY(this.position.getY2() - 8.0D);
      RenderUtils.drawCircle(this.point.getX() + 1.5D, this.point.getY() + 1.5D, this.animation, 637534207, 2.0D);
      RenderUtils.drawCircle(this.point.getX() + 1.5D, this.point.getY() + 1.5D, this.animation + 1.0D, 637534207, 2.0D);
      RenderUtils.drawCircle(this.point.getX() + 1.5D, this.point.getY() + 1.5D, this.animation + 2.0D, 637534207, 2.0D);
      RenderUtils.drawCircle(this.point.getX() + 1.5D, this.point.getY() + 1.5D, 3.5D, -5592406, 2.0D);
      RenderUtils.drawCircle(this.point.getX() + 1.5D, this.point.getY() + 1.5D, 3.0D, -1, 2.0D);
   }

   public void onClick(int mouseX, int mouseY, int button) {
      if ((this.slider.isInside(mouseX, mouseY) || this.point.isInside(mouseX, mouseY)) && button == 0) {
         this.dragging = true;
      }

   }

   public void onRelease(int mouseX, int mouseY, int button) {
      this.dragging = false;
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
