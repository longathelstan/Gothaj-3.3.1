package xyz.cucumber.base.interf.newclickgui.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import xyz.cucumber.base.module.settings.ModuleSettings;
import xyz.cucumber.base.module.settings.NumberSettings;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.Fonts;

public class NewNumberSetting extends NewSetting {
   private boolean dragging;
   private double animation;
   private PositionUtils slider = new PositionUtils(0.0D, 0.0D, 270.0D, 3.0D, 1.0F);
   private PositionUtils collideCircle = new PositionUtils(0.0D, 0.0D, 8.0D, 8.0D, 1.0F);

   public NewNumberSetting(ModuleSettings setting) {
      this.setting = setting;
      this.position.setWidth(294.0D);
      this.position.setHeight(20.0D);
   }

   public void draw(int mouseX, int mouseY) {
      double diff = Math.min(this.slider.getWidth(), Math.max(0.0D, (double)mouseX - this.slider.getX()));
      double x;
      if (this.dragging) {
         x = this.roundToPlace(diff / this.slider.getWidth() * (((NumberSettings)this.setting).getMax() - ((NumberSettings)this.setting).getMin()) + ((NumberSettings)this.setting).getMin(), (int)this.slider.getWidth());
         ((NumberSettings)this.setting).setValue((double)((float)x));
      }

      Fonts.getFont("rb-m").drawString(((NumberSettings)this.setting).getName(), this.position.getX() + 12.0D, this.position.getY() + 6.0D, -1);
      Fonts.getFont("rb-r").drawString(String.valueOf(((NumberSettings)this.setting).getValue()), this.position.getX2() - 12.0D - Fonts.getFont("rb-r").getWidth(String.valueOf(((NumberSettings)this.setting).getValue())), this.position.getY() + 6.0D, -5592406);
      this.slider.setX(this.position.getX() + 12.0D);
      this.slider.setY(this.position.getY2() - 6.0D);
      x = this.slider.getWidth() * (((NumberSettings)this.setting).getValue() - ((NumberSettings)this.setting).getMin()) / (((NumberSettings)this.setting).getMax() - ((NumberSettings)this.setting).getMin());
      this.animation = (this.animation * 9.0D + x) / 10.0D;
      RenderUtils.drawRoundedRect(this.slider.getX(), this.slider.getY(), this.slider.getX2(), this.slider.getY2(), 805306368, 1.0F);
      RenderUtils.drawRoundedRect(this.slider.getX(), this.slider.getY(), this.slider.getX() + this.animation, this.slider.getY2(), -881934, 1.0F);
      this.collideCircle.setX(this.slider.getX() + this.animation - this.collideCircle.getWidth() / 2.0D);
      this.collideCircle.setY(this.slider.getY() + this.slider.getHeight() / 2.0D - this.collideCircle.getHeight() / 2.0D);
      RenderUtils.drawCircle(this.slider.getX() + this.animation, this.slider.getY() + this.slider.getHeight() / 2.0D, this.collideCircle.getHeight() / 2.0D, -881934, 1.0D);
      RenderUtils.drawCircle(this.slider.getX() + this.animation, this.slider.getY() + this.slider.getHeight() / 2.0D, this.collideCircle.getHeight() / 3.0D, -15329255, 1.0D);
   }

   public void onClick(int mouseX, int mouseY, int button) {
      if ((this.slider.isInside(mouseX, mouseY) || this.collideCircle.isInside(mouseX, mouseY)) && button == 0) {
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
