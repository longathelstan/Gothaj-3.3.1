package xyz.cucumber.base.interf.DropdownClickGui.ext.Settings;

import net.minecraft.util.ResourceLocation;
import xyz.cucumber.base.module.settings.BooleanSettings;
import xyz.cucumber.base.module.settings.ModuleSettings;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.Fonts;

public class DropdownClickGuiBoolean extends DropdownClickGuiSettings {
   private BooleanSettings settings;
   private PositionUtils checkMark = new PositionUtils(0.0D, 0.0D, 8.0D, 8.0D, 1.0F);
   private double animation;

   public DropdownClickGuiBoolean(ModuleSettings settings, PositionUtils position) {
      this.settings = (BooleanSettings)settings;
      this.position = position;
      this.mainSetting = settings;
   }

   public void draw(int mouseX, int mouseY) {
      this.checkMark.setX(this.position.getX2() - 12.0D);
      this.checkMark.setY(this.position.getY() + this.position.getHeight() / 2.0D - this.checkMark.getHeight() / 2.0D);
      Fonts.getFont("rb-m-13").drawString(this.settings.getName(), this.position.getX() + 3.0D, this.position.getY() + 3.0D, -1);
      RenderUtils.drawRoundedRectWithCorners(this.checkMark.getX(), this.checkMark.getY(), this.checkMark.getX2(), this.checkMark.getY2(), 805306368, 4.0D, true, true, true, true);
      RenderUtils.drawRoundedRectWithCorners(this.checkMark.getX() + this.checkMark.getWidth() / 2.0D - this.animation, this.checkMark.getY() + this.checkMark.getHeight() / 2.0D - this.animation, this.checkMark.getX() + this.checkMark.getWidth() / 2.0D + this.animation, this.checkMark.getY() + this.checkMark.getHeight() / 2.0D + this.animation, -13354432, 4.0D / (this.checkMark.getWidth() / 2.0D) * this.animation, true, true, true, true);
      if (this.settings.isEnabled()) {
         this.animation = (this.animation * 9.0D + this.checkMark.getWidth() / 2.0D) / 10.0D;
         RenderUtils.drawImage(this.checkMark.getX() + 1.0D, this.checkMark.getY() + 1.0D, 6.0D, 6.0D, new ResourceLocation("client/images/check.png"), -1);
      } else {
         this.animation = this.animation * 9.0D / 10.0D;
      }

   }

   public void onClick(int mouseX, int mouseY, int button) {
      if (this.checkMark.isInside(mouseX, mouseY) && button == 0) {
         this.settings.setEnabled(!this.settings.isEnabled());
      }

   }

   public void onRelease(int mouseX, int mouseY, int button) {
   }

   public void onKey(char chr, int key) {
   }
}
