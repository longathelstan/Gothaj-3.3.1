package xyz.cucumber.base.interf.clickgui.navbar.ex;

import net.minecraft.util.ResourceLocation;
import xyz.cucumber.base.interf.clickgui.navbar.Navbar;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.Fonts;

public class ClientButtons extends NavbarButtons {
   private String name;
   private Navbar navbar;

   public ClientButtons(String name, Navbar navbar) {
      this.name = name;
      this.navcategory = NavbarButtons.NavCategory.Client;
      this.position = new PositionUtils(0.0D, 0.0D, 90.0D, 15.0D, 1.0F);
      this.navbar = navbar;
   }

   public void draw(int mouseX, int mouseY) {
      int color = -5592406;
      if (this.navbar.active == this) {
         color = -16777216;
      }

      RenderUtils.drawImage(this.position.getX() + 8.0D, this.position.getY() + 2.0D, this.position.getHeight() - 4.0D, this.position.getHeight() - 4.0D, new ResourceLocation("client/images/" + this.name.toLowerCase() + ".png"), color);
      Fonts.getFont("rb-r").drawString(this.name.substring(0, 1).toUpperCase() + this.name.substring(1).toLowerCase(), this.position.getX() + 30.0D, this.position.getY() + this.position.getHeight() / 2.0D - (double)(Fonts.getFont("rb-r").getHeight(this.name.substring(0, 1).toUpperCase() + this.name.substring(1).toLowerCase()) / 2.0F), color);
   }

   public void clicked(int mouseX, int mouseY, int button) {
      if (this.position.isInside(mouseX, mouseY) && button == 0) {
         this.navbar.active = this;
      }

   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Navbar getNavbar() {
      return this.navbar;
   }

   public void setNavbar(Navbar navbar) {
      this.navbar = navbar;
   }
}
