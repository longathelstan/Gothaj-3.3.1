package xyz.cucumber.base.interf.DropdownClickGui.ext.Settings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import xyz.cucumber.base.module.settings.ModuleSettings;
import xyz.cucumber.base.module.settings.StringSettings;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.Fonts;

public class DropdownClickGuiString extends DropdownClickGuiSettings {
   private StringSettings setting;
   private GuiTextField field;

   public DropdownClickGuiString(ModuleSettings setting, PositionUtils position) {
      this.setting = (StringSettings)setting;
      this.position = position;
      this.field = new GuiTextField(0, Minecraft.getMinecraft().fontRendererObj, 0, 0, 60, 10);
      this.field.setText(this.setting.getString());
      this.mainSetting = setting;
   }

   public void draw(int mouseX, int mouseY) {
      this.field.xPosition = (int)this.position.getX2() - 60;
      this.field.yPosition = (int)this.position.getY() + 1;
      Fonts.getFont("rb-m-13").drawString(this.setting.getName(), this.position.getX() + 3.0D, (double)(this.field.yPosition + 3), -1);
      RenderUtils.drawRoundedRectWithCorners((double)this.field.xPosition, (double)this.field.yPosition, (double)(this.field.xPosition + this.field.width), (double)(this.field.yPosition + this.field.height), 1610612736, 2.0D, true, true, true, true);
      this.setting.setString(this.field.getText());
      Fonts.getFont("rb-r-13").drawString(this.setting.getString(), (double)this.field.xPosition, (double)(this.field.yPosition + 3), -1);
   }

   public void onClick(int mouseX, int mouseY, int button) {
      this.field.mouseClicked(mouseX, mouseY, button);
   }

   public void onKey(char chr, int key) {
      if (chr == '\t' && !this.field.isFocused()) {
         this.field.setFocused(true);
      }

      this.field.textboxKeyTyped(chr, key);
   }
}
