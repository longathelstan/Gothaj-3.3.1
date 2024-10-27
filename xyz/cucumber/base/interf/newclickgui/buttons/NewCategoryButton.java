package xyz.cucumber.base.interf.newclickgui.buttons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import xyz.cucumber.base.Client;
import xyz.cucumber.base.module.Category;
import xyz.cucumber.base.module.Mod;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.button.Button;
import xyz.cucumber.base.utils.render.ColorUtils;
import xyz.cucumber.base.utils.render.Fonts;

public class NewCategoryButton extends Button {
   public List<NewModuleButton> modules = new ArrayList();
   public Category category;
   public ResourceLocation image;
   public boolean isActive;
   public double animation;
   public int c = -5592406;
   public int activeColor = -881934;

   public NewCategoryButton(Category category) {
      this.category = category;
      this.position.setWidth(96.0D);
      this.position.setHeight(20.0D);
      this.image = new ResourceLocation("client/images/" + category.name().toLowerCase() + ".png");
      Iterator var3 = Client.INSTANCE.getModuleManager().getModulesByCategory(category).iterator();

      while(var3.hasNext()) {
         Mod module = (Mod)var3.next();
         this.modules.add(new NewModuleButton(module, this.image));
      }

   }

   public void draw(int mouseX, int mouseY) {
      if (!this.isActive && !this.position.isInside(mouseX, mouseY)) {
         this.animation = this.animation * 9.0D / 10.0D;
      } else {
         this.animation = (this.animation * 9.0D + 1.0D) / 10.0D;
      }

      int color = ColorUtils.mix(this.c, this.activeColor, this.animation, 1.0D);
      RenderUtils.drawImage(this.position.getX() + 15.0D, this.position.getY() + 5.0D, 10.0D, 10.0D, this.image, color);
      Fonts.getFont("mitr").drawString(this.category.name().toLowerCase().substring(0, 1).toUpperCase() + this.category.name().toLowerCase().substring(1), this.position.getX() + 35.0D, this.position.getY() + 7.0D, color);
   }

   public void onClick(int mouseX, int mouseY, int b) {
   }

   public void onRelease(int mouseX, int mouseY, int b) {
   }

   public void onKey(char typedChar, int keyCode) {
   }
}
