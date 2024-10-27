package xyz.cucumber.base.interf.newclickgui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import xyz.cucumber.base.Client;
import xyz.cucumber.base.interf.newclickgui.buttons.NewCategoryButton;
import xyz.cucumber.base.interf.newclickgui.buttons.NewModuleButton;
import xyz.cucumber.base.module.Category;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.BloomUtils;
import xyz.cucumber.base.utils.render.Fonts;

public class NewClickGui extends GuiScreen {
   private PositionUtils position = new PositionUtils(0.0D, 0.0D, 400.0D, 200.0D, 1.0F);
   private PositionUtils modulePosition = new PositionUtils(100.0D, 22.0D, 298.0D, 176.0D, 1.0F);
   public ResourceLocation logo = new ResourceLocation("client/images/gothaj.png");
   public int c = -5592406;
   public int activeColor = -881934;
   BloomUtils bloom = new BloomUtils();
   private double scrollY;
   private double temp;
   public NewCategoryButton active;
   private List<NewCategoryButton> buttons = new ArrayList();
   private double animation;
   private boolean dragging;
   private double dragX;
   private double dragY;
   private double rotateAnimation;
   private PositionUtils button = new PositionUtils(0.0D, 0.0D, 25.0D, 25.0D, 1.0F);

   public NewClickGui() {
      Category[] var4;
      int var3 = (var4 = Category.values()).length;

      for(int var2 = 0; var2 < var3; ++var2) {
         Category c = var4[var2];
         this.buttons.add(this.active == null ? (this.active = new NewCategoryButton(c)) : new NewCategoryButton(c));
      }

      this.position.setX(50.0D);
      this.position.setY(50.0D);
   }

   public void initGui() {
      this.dragging = false;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.button.setX(10.0D);
      this.button.setY((double)(this.height - 35));
      this.modulePosition.setX(this.position.getX() + 100.0D);
      this.modulePosition.setY(this.position.getY() + 22.0D);
      if (this.dragging) {
         this.position.setX((double)mouseX - this.dragX);
         this.position.setY((double)mouseY - this.dragY);
      }

      RenderUtils.drawRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), -15724013, 3.0F);
      RenderUtils.drawOutlinedRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), -1879048192, 3.0D, 1.0D);
      RenderUtils.drawRoundedRect(this.modulePosition.getX(), this.modulePosition.getY(), this.modulePosition.getX2(), this.modulePosition.getY2(), -15658219, 3.0F);
      Fonts.getFont("mitr").drawString("GOTHAJ", this.position.getX() + 30.0D, this.position.getY() + 9.0D, -1);
      Fonts.getFont("rb-m-13").drawString(Client.INSTANCE.version, this.position.getX() + 30.0D + Fonts.getFont("mitr").getWidth("GOTHAJ") + 2.0D, this.position.getY() + 9.0D, -5592406);
      RenderUtils.drawLine(this.position.getX() + 6.0D, this.position.getY() + 22.0D, this.position.getX() + 96.0D, this.position.getY() + 22.0D, 816491178, 2.0F);
      double i = 0.0D;

      for(Iterator var7 = this.buttons.iterator(); var7.hasNext(); ++i) {
         NewCategoryButton button = (NewCategoryButton)var7.next();
         button.position.setX(this.position.getX() + 2.0D);
         button.position.setY(this.position.getY() + 24.0D + 22.0D * i);
         button.isActive = button == this.active;
         button.draw(mouseX, mouseY);
      }

      this.animation = (this.animation * 9.0D + (double)(22 * this.buttons.indexOf(this.active))) / 10.0D;
      RenderUtils.drawRoundedRect(this.position.getX() + 10.0D, this.position.getY() + 24.0D + 3.0D + this.animation, this.position.getX() + 12.0D, this.position.getY() + 24.0D + 17.0D + this.animation, this.activeColor, 0.5D);
      double h = 2.0D;
      RenderUtils.enableScisor();
      RenderUtils.scissor(new ScaledResolution(this.mc), this.modulePosition.getX(), this.modulePosition.getY(), this.modulePosition.getWidth(), this.modulePosition.getHeight());

      NewModuleButton button;
      for(Iterator var9 = this.active.modules.iterator(); var9.hasNext(); h += button.getPosition().getHeight() + 2.0D) {
         button = (NewModuleButton)var9.next();
         button.position.setX(this.modulePosition.getX() + 2.0D);
         button.position.setY(this.modulePosition.getY() + h - this.scrollY);
         button.draw(mouseX, mouseY);
      }

      RenderUtils.disableScisor();
      super.drawScreen(mouseX, mouseY, partialTicks);
      RenderUtils.drawImage(this.position.getX() + 5.0D, this.position.getY() + 2.0D, 20.0D, 20.0D, this.logo, -1);
      if (Client.INSTANCE.getClientSettings().open) {
         this.rotateAnimation = (this.rotateAnimation * 9.0D + 360.0D) / 10.0D;
      } else {
         this.rotateAnimation = this.rotateAnimation * 9.0D / 10.0D;
      }

      double save = this.position.getHeight();
      if (save < h + 24.0D) {
         float g = (float)Mouse.getEventDWheel();
         double maxScrollY = h + 24.0D - save;
         double size = (double)(Mouse.getDWheel() / 60);
         if (size != 0.0D) {
            this.temp += size;
         }

         if (Math.round(this.temp) != 0L) {
            this.temp = this.temp * 9.0D / 10.0D;
            double l = this.scrollY;
            this.scrollY -= this.temp;
            if (this.scrollY < 0.0D) {
               this.scrollY = 0.0D;
            } else if (this.scrollY > maxScrollY) {
               this.scrollY = maxScrollY;
            }
         } else {
            this.temp = 0.0D;
         }
      } else {
         this.scrollY = 0.0D;
      }

      RenderUtils.drawCircle(this.button.getX() + this.button.getWidth() / 2.0D, this.button.getY() + this.button.getHeight() / 2.0D, this.button.getHeight() / 2.0D, -12877341, 10.0D);
      GL11.glPushMatrix();
      GL11.glTranslated(this.button.getX() + 12.5D, this.button.getY() + 12.5D, 0.0D);
      GL11.glRotated(this.rotateAnimation, 0.0D, 0.0D, 1.0D);
      GL11.glTranslated(-this.button.getX() - 12.5D, -this.button.getY() - 12.5D, 0.0D);
      RenderUtils.drawImage(this.button.getX() + 5.0D, this.button.getY() + 5.0D, 15.0D, 15.0D, new ResourceLocation("client/images/cs.png"), -1);
      GL11.glPopMatrix();
      Client.INSTANCE.getClientSettings().draw(mouseX, mouseY);
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      Iterator var4 = this.active.modules.iterator();

      while(var4.hasNext()) {
         NewModuleButton button = (NewModuleButton)var4.next();
         button.onKey(typedChar, keyCode);
      }

      super.keyTyped(typedChar, keyCode);
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      Client.INSTANCE.getClientSettings().onClick(mouseX, mouseY, mouseButton);
      if (!Client.INSTANCE.getClientSettings().open) {
         if (this.button.isInside(mouseX, mouseY)) {
            Client.INSTANCE.getClientSettings().init();
            Client.INSTANCE.getClientSettings().open = true;
         }

         Iterator var5 = this.buttons.iterator();

         while(var5.hasNext()) {
            NewCategoryButton button = (NewCategoryButton)var5.next();
            if (button.getPosition().isInside(mouseX, mouseY) && mouseButton == 0) {
               this.active = button;
               return;
            }
         }

         if (!this.modulePosition.isInside(mouseX, mouseY)) {
            if (this.position.isInside(mouseX, mouseY) && mouseButton == 0) {
               this.dragging = true;
               this.dragX = (double)mouseX - this.position.getX();
               this.dragY = (double)mouseY - this.position.getY();
            }

         } else {
            var5 = this.active.modules.iterator();

            while(var5.hasNext()) {
               NewModuleButton button = (NewModuleButton)var5.next();
               button.onClick(mouseX, mouseY, mouseButton);
            }

         }
      }
   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      Client.INSTANCE.getClientSettings().onRelease(mouseX, mouseY, state);
      if (!Client.INSTANCE.getClientSettings().open) {
         Iterator var5 = this.active.modules.iterator();

         while(var5.hasNext()) {
            NewModuleButton button = (NewModuleButton)var5.next();
            button.onRelease(mouseX, mouseY, state);
         }

         this.dragging = false;
      }
   }

   public boolean doesGuiPauseGame() {
      return false;
   }
}
