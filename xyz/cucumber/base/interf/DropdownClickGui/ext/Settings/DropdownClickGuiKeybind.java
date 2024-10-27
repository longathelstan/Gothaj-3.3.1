package xyz.cucumber.base.interf.DropdownClickGui.ext.Settings;

import org.lwjgl.input.Keyboard;
import xyz.cucumber.base.module.Mod;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.Fonts;

public class DropdownClickGuiKeybind extends DropdownClickGuiSettings {
   private Mod module;
   private boolean binding;

   public DropdownClickGuiKeybind(Mod module, PositionUtils position) {
      this.module = module;
      this.position = position;
   }

   public void draw(int mouseX, int mouseY) {
      String s = Keyboard.getKeyName(this.module.getKey());
      if (this.binding) {
         s = "...";
      }

      Fonts.getFont("rb-m-13").drawString("Key: " + s, this.position.getX() + this.position.getWidth() / 2.0D - Fonts.getFont("rb-m-13").getWidth("Key: " + s) / 2.0D, this.position.getY() + 2.0D, -1);
   }

   public void onClick(int mouseX, int mouseY, int button) {
      this.binding = !this.binding;
   }

   public void onKey(char chr, int key) {
      if (this.binding) {
         this.binding = false;
         if (key == 1) {
            this.module.setKey(0);
            return;
         }

         this.module.setKey(key);
      }

   }
}
