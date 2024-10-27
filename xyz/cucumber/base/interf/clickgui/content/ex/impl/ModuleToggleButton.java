package xyz.cucumber.base.interf.clickgui.content.ex.impl;

import xyz.cucumber.base.module.Mod;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.ColorUtils;
import xyz.cucumber.base.utils.render.Fonts;

public class ModuleToggleButton extends SettingsButton {
   private Mod module;
   private PositionUtils toggleButton;
   private double toggleAnimation;

   public ModuleToggleButton(Mod mod, PositionUtils position) {
      this.module = mod;
      this.position = position;
      this.toggleButton = new PositionUtils(0.0D, 0.0D, 20.0D, 10.0D, 1.0F);
   }

   public void draw(int mouseX, int mouseY) {
      int color = true;
      if (this.module.isEnabled()) {
         this.toggleAnimation = (this.toggleAnimation * 9.0D + this.toggleButton.getWidth() - 10.0D) / 10.0D;
      } else {
         this.toggleAnimation = this.toggleAnimation * 9.0D / 10.0D;
      }

      int color = ColorUtils.mix(-1, -12424715, this.toggleAnimation, this.toggleButton.getWidth() - 10.0D);
      RenderUtils.drawRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), -15066598, 1.0F);
      Fonts.getFont("rb-r").drawString(this.module.getName(), this.position.getX() + 5.0D, this.position.getY() + 5.0D, color);
      Fonts.getFont("rb-r").drawString(this.module.getDescription(), this.position.getX() + this.position.getWidth() / 2.0D - Fonts.getFont("rb-r").getWidth(this.module.getDescription()) / 2.0D, this.position.getY() + 4.0D, -5592406);
      this.toggleButton.setX(this.position.getX2() - this.toggleButton.getWidth() - 2.5D);
      this.toggleButton.setY(this.position.getY() + 2.5D);
      RenderUtils.drawRoundedRect(this.toggleButton.getX(), this.toggleButton.getY(), this.toggleButton.getX2(), this.toggleButton.getY2(), ColorUtils.mix(-13421773, -12424715, this.toggleAnimation, this.toggleButton.getWidth() - 10.0D), 4.0F);
      RenderUtils.drawCircle(this.toggleButton.getX() + 4.0D + 1.0D + this.toggleAnimation, this.toggleButton.getY() + this.toggleButton.getHeight() / 2.0D, 4.0D, -1, 1.0D);
   }

   public void click(int mouseX, int mouseY, int button) {
      if (this.toggleButton.isInside(mouseX, mouseY) && button == 0) {
         this.module.toggle();
      }

   }
}
