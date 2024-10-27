package xyz.cucumber.base.interf.DropdownClickGui.ext.Settings;

import java.math.BigDecimal;
import java.math.RoundingMode;
import xyz.cucumber.base.module.settings.ModuleSettings;
import xyz.cucumber.base.module.settings.NumberSettings;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.Fonts;

public class DropdownClickGuiNumber extends DropdownClickGuiSettings {
   private NumberSettings setting;
   private boolean dragging;
   private double animation;
   private PositionUtils slider = new PositionUtils(0.0D, 0.0D, 90.0D, 3.0D, 1.0F);
   private PositionUtils collideCircle = new PositionUtils(0.0D, 0.0D, 6.0D, 6.0D, 1.0F);

   public DropdownClickGuiNumber(ModuleSettings setting, PositionUtils modesPosition) {
      this.setting = (NumberSettings)setting;
      this.position = modesPosition;
      this.mainSetting = setting;
   }

   public void draw(int mouseX, int mouseY) {
      double diff = Math.min(this.slider.getWidth(), Math.max(0.0D, (double)mouseX - this.slider.getX()));
      double x;
      if (this.dragging) {
         x = this.roundToPlace(diff / this.slider.getWidth() * (this.setting.getMax() - this.setting.getMin()) + this.setting.getMin(), (int)this.slider.getWidth());
         this.setting.setValue((double)((float)x));
      }

      Fonts.getFont("rb-m-13").drawString(this.setting.getName(), this.position.getX() + 3.0D, this.position.getY() + 1.0D, -1);
      Fonts.getFont("rb-m-13").drawString(String.valueOf(this.setting.getValue()), this.position.getX2() - 5.0D - Fonts.getFont("rb-m-13").getWidth(String.valueOf(this.setting.getValue())), this.position.getY() + 1.0D, -5592406);
      this.slider.setX(this.position.getX() + 5.0D);
      this.slider.setY(this.position.getY() + 2.0D + 8.0D);
      x = this.slider.getWidth() * (this.setting.getValue() - this.setting.getMin()) / (this.setting.getMax() - this.setting.getMin());
      this.animation = (this.animation * 9.0D + x) / 10.0D;
      RenderUtils.drawRoundedRect(this.slider.getX(), this.slider.getY(), this.slider.getX2(), this.slider.getY2(), -16777216, 1.0F);
      RenderUtils.drawRoundedRect(this.slider.getX(), this.slider.getY(), this.slider.getX() + this.animation, this.slider.getY2(), -7706881, 1.0F);
      this.collideCircle.setX(this.slider.getX() + this.animation - this.collideCircle.getWidth() / 2.0D);
      this.collideCircle.setY(this.slider.getY() + this.slider.getHeight() / 2.0D - this.collideCircle.getHeight() / 2.0D);
      RenderUtils.drawCircle(this.slider.getX() + this.animation, this.slider.getY() + this.slider.getHeight() / 2.0D, this.collideCircle.getHeight() / 2.0D, -7706881, 1.0D);
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
