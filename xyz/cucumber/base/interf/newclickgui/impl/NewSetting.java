package xyz.cucumber.base.interf.newclickgui.impl;

import xyz.cucumber.base.module.settings.ModuleSettings;
import xyz.cucumber.base.utils.button.Button;

public class NewSetting extends Button {
   ModuleSettings setting;

   public void draw(int mouseX, int mouseY) {
   }

   public void onClick(int mouseX, int mouseY, int b) {
   }

   public void onRelease(int mouseX, int mouseY, int b) {
   }

   public void onKey(char typedChar, int keyCode) {
   }

   public ModuleSettings getSetting() {
      return this.setting;
   }

   public void setSetting(ModuleSettings setting) {
      this.setting = setting;
   }
}
