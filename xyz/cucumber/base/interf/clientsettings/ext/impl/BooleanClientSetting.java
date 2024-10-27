package xyz.cucumber.base.interf.clientsettings.ext.impl;

import xyz.cucumber.base.module.settings.BooleanSettings;
import xyz.cucumber.base.module.settings.ModuleSettings;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.Fonts;

public class BooleanClientSetting extends ClientSetting {
   private BooleanSettings setting;
   private PositionUtils button = new PositionUtils(0.0D, 0.0D, 20.0D, 10.0D, 1.0F);
   private double animation;

   public BooleanClientSetting(ModuleSettings setting) {
      this.setting = (BooleanSettings)setting;
      this.position.setHeight(15.0D);
   }

   public void draw(int mouseX, int mouseY) {
      this.button.setX(this.position.getX2() - this.button.getWidth() - 7.0D);
      this.button.setY(this.position.getY() + this.position.getHeight() / 2.0D - this.button.getHeight() / 2.0D);
      Fonts.getFont("rb-m").drawString(this.setting.getName(), this.position.getX() + 8.0D, this.position.getY() + this.position.getHeight() / 2.0D - (double)(Fonts.getFont("rb-m").getHeight("H") / 2.0F), -1);
      RenderUtils.drawRoundedRect(this.button.getX(), this.button.getY(), this.button.getX2(), this.button.getY2(), -1867863382, 4.0F);
      if (this.setting.isEnabled()) {
         this.animation = (this.animation * 9.0D + 10.0D) / 10.0D;
      } else {
         this.animation = this.animation * 9.0D / 10.0D;
      }

      RenderUtils.drawCircle(this.button.getX() + 5.0D + this.animation, this.button.getY() + 5.0D, 4.5D, 637534207, 2.0D);
      RenderUtils.drawCircle(this.button.getX() + 5.0D + this.animation, this.button.getY() + 5.0D, 5.5D, 637534207, 2.0D);
      RenderUtils.drawCircle(this.button.getX() + 5.0D + this.animation, this.button.getY() + 5.0D, 6.5D, 637534207, 2.0D);
      RenderUtils.drawCircle(this.button.getX() + 5.0D + this.animation, this.button.getY() + 5.0D, 4.0D, -1, 2.0D);
   }

   public void onClick(int mouseX, int mouseY, int button) {
      if (this.button.isInside(mouseX, mouseY) && button == 0) {
         this.setting.setEnabled(!this.setting.isEnabled());
      }

   }

   public void onRelease(int mouseX, int mouseY, int button) {
   }
}
