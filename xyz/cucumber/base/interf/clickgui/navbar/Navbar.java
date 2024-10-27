package xyz.cucumber.base.interf.clickgui.navbar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import xyz.cucumber.base.interf.clickgui.navbar.ex.CategoryButton;
import xyz.cucumber.base.interf.clickgui.navbar.ex.ClientButtons;
import xyz.cucumber.base.interf.clickgui.navbar.ex.NavbarButtons;
import xyz.cucumber.base.module.Category;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.BloomUtils;
import xyz.cucumber.base.utils.render.Fonts;
import xyz.cucumber.base.utils.render.StencilUtils;

public class Navbar {
   private PositionUtils position;
   public List<CategoryButton> categories = new ArrayList();
   public List<ClientButtons> client = new ArrayList();
   public NavbarButtons active;
   private double yanimation;
   BloomUtils bloom = new BloomUtils();

   public Navbar(PositionUtils position) {
      this.position = position;
      this.categories.clear();
      Category[] var5;
      int var4 = (var5 = Category.values()).length;

      for(int var3 = 0; var3 < var4; ++var3) {
         Category c = var5[var3];
         this.categories.add(new CategoryButton(c, this));
      }

      this.active = (NavbarButtons)this.categories.get(0);
      this.client.add(new ClientButtons("Configs", this));
   }

   public void draw(int mouseX, int mouseY) {
      StencilUtils.initStencil();
      GL11.glEnable(2960);
      StencilUtils.bindWriteStencilBuffer();
      RenderUtils.drawRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), -14342363, 1.0F);
      StencilUtils.bindReadStencilBuffer(1);
      RenderUtils.drawRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), -299555035);
      RenderUtils.drawRect(this.position.getX(), this.position.getY2() - 25.0D, this.position.getX2(), this.position.getY2(), -14540254);
      RenderUtils.drawRect(this.position.getY(), this.position.getY(), this.position.getX2(), this.position.getY() + 25.0D, -14540254);
      RenderUtils.drawCircle(this.position.getX() + 14.0D, this.position.getY() + 12.0D, 9.0D, -15132391, 5.0D);
      RenderUtils.drawImage(this.position.getX() + 14.0D - 9.0D, this.position.getY() + 12.0D - 8.0D, 18.0D, 18.0D, new ResourceLocation("client/images/gothaj.png"), -1);
      Fonts.getFont("rb-b").drawString("GOTHAJ", this.position.getX() + 14.0D + 18.0D + 5.0D, this.position.getY() + 25.0D - Fonts.getFont("rb-b").getWidth("GOTHAJ") / 2.0D, -12424715);
      double i = 15.0D;
      RenderUtils.drawRoundedRect(this.active.getPosition().getX(), this.yanimation, this.active.getPosition().getX2(), this.yanimation + this.active.getPosition().getHeight(), -12424715, 2.0F);
      i += this.drawCategory(mouseY, mouseY, this.position.getX(), this.position.getY() + 30.0D + i, this.position.getX2()) + 10.0D;
      this.drawClientButtons(mouseY, mouseY, this.position.getX(), this.position.getY() + 30.0D + i, this.position.getX2());
      StencilUtils.uninitStencilBuffer();
   }

   public void clicked(int mouseX, int mouseY, int button) {
      if (this.position.isInside(mouseX, mouseY)) {
         Iterator var5 = this.categories.iterator();

         while(var5.hasNext()) {
            CategoryButton c = (CategoryButton)var5.next();
            c.clicked(mouseX, mouseY, button);
         }

         var5 = this.client.iterator();

         while(var5.hasNext()) {
            NavbarButtons c = (NavbarButtons)var5.next();
            c.clicked(mouseX, mouseY, button);
         }
      }

   }

   public PositionUtils getPosition() {
      return this.position;
   }

   public void setPosition(PositionUtils position) {
      this.position = position;
   }

   private double drawCategory(int mouseX, int mouseY, double startX, double startY, double endX) {
      double s = 0.0D;
      double w = endX - startX - 10.0D;
      RenderUtils.drawLine(startX + 5.0D, startY, startX + 5.0D + w / 2.0D - Fonts.getFont("rb-r").getWidth("Category") / 2.0D - 2.0D, startY, -5592406, 1.0F);
      RenderUtils.drawLine(startX + 5.0D + w / 2.0D + Fonts.getFont("rb-r").getWidth("Category") / 2.0D + 2.0D, startY, endX - 5.0D, startY, -5592406, 1.0F);
      Fonts.getFont("rb-r").drawString("Category", startX + 5.0D + w / 2.0D - Fonts.getFont("rb-r").getWidth("Category") / 2.0D, startY - 2.5D, -5592406);
      s += 5.0D;
      this.yanimation = (this.yanimation * 9.0D + this.active.getPosition().getY()) / 10.0D;

      CategoryButton c;
      for(Iterator var14 = this.categories.iterator(); var14.hasNext(); s += c.getPosition().getHeight()) {
         c = (CategoryButton)var14.next();
         c.getPosition().setX(startX + 5.0D);
         c.getPosition().setY(startY + s);
         c.draw(mouseX, mouseY);
      }

      return s;
   }

   private double drawClientButtons(int mouseX, int mouseY, double startX, double startY, double endX) {
      double s = 0.0D;
      double w = endX - startX - 10.0D;
      RenderUtils.drawLine(startX + 5.0D, startY, startX + 5.0D + w / 2.0D - Fonts.getFont("rb-r").getWidth("Client") / 2.0D - 2.0D, startY, -5592406, 1.0F);
      RenderUtils.drawLine(startX + 5.0D + w / 2.0D + Fonts.getFont("rb-r").getWidth("Client") / 2.0D + 2.0D, startY, endX - 5.0D, startY, -5592406, 1.0F);
      Fonts.getFont("rb-r").drawString("Client", startX + 5.0D + w / 2.0D - Fonts.getFont("rb-r").getWidth("Client") / 2.0D, startY - 2.5D, -5592406);
      s += 5.0D;
      this.yanimation = (this.yanimation * 9.0D + this.active.getPosition().getY()) / 10.0D;

      ClientButtons c;
      for(Iterator var14 = this.client.iterator(); var14.hasNext(); s += c.getPosition().getHeight()) {
         c = (ClientButtons)var14.next();
         c.getPosition().setX(startX + 5.0D);
         c.getPosition().setY(startY + s);
         c.draw(mouseX, mouseY);
      }

      return s;
   }
}
