package xyz.cucumber.base.interf.DropdownClickGui.copy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import xyz.cucumber.base.Client;
import xyz.cucumber.base.interf.DropdownClickGui.ext.DropdownButton;
import xyz.cucumber.base.interf.DropdownClickGui.ext.DropdownCategory;
import xyz.cucumber.base.module.Category;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.BlurUtils;

public class DropdownClickGui extends GuiScreen {
   private ArrayList<DropdownButton> buttons = new ArrayList();
   private double initTimer = 0.0D;
   private PositionUtils position = new PositionUtils(0.0D, 0.0D, 25.0D, 25.0D, 1.0F);
   private double rotateAnimation;

   public DropdownClickGui() {
      double centerX = 20.0D;
      double centerY = 20.0D;
      this.buttons.clear();
      int i = 0;
      Category[] var9;
      int var8 = (var9 = Category.values()).length;

      for(int var7 = 0; var7 < var8; ++var7) {
         Category c = var9[var7];
         DropdownCategory category = new DropdownCategory(c);
         category.getPosition().setX(centerX + (double)(112 * i));
         category.getPosition().setY(centerY);
         category.getPosition().setHeight(20.0D);
         this.buttons.add(category);
         ++i;
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if (this.initTimer < 7.0D) {
         this.initTimer += 0.5D;
      }

      this.position.setX(10.0D);
      this.position.setY((double)(this.height - 35));
      BlurUtils.renderBlur((float)this.initTimer);
      Iterator var5 = this.buttons.iterator();

      while(var5.hasNext()) {
         DropdownButton b = (DropdownButton)var5.next();
         b.draw(mouseX, mouseY);
      }

      Client.INSTANCE.getConfigManager().draw(mouseX, mouseY);
      Client.INSTANCE.getClientSettings().draw(mouseX, mouseY);
      RenderUtils.drawCircle(this.position.getX() + this.position.getWidth() / 2.0D, this.position.getY() + this.position.getHeight() / 2.0D, this.position.getHeight() / 2.0D, -12877341, 10.0D);
      if (Client.INSTANCE.getClientSettings().open) {
         this.rotateAnimation = (this.rotateAnimation * 9.0D + 360.0D) / 10.0D;
      } else {
         this.rotateAnimation = this.rotateAnimation * 9.0D / 10.0D;
      }

      GL11.glPushMatrix();
      GL11.glTranslated(this.position.getX() + 12.5D, this.position.getY() + 12.5D, 0.0D);
      GL11.glRotated(this.rotateAnimation, 0.0D, 0.0D, 1.0D);
      GL11.glTranslated(-this.position.getX() - 12.5D, -this.position.getY() - 12.5D, 0.0D);
      RenderUtils.drawImage(this.position.getX() + 5.0D, this.position.getY() + 5.0D, 15.0D, 15.0D, new ResourceLocation("client/images/cs.png"), -1);
      GL11.glPopMatrix();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      Client.INSTANCE.getConfigManager().onKey(keyCode, typedChar);
      if (!Client.INSTANCE.getConfigManager().open) {
         Iterator var4 = this.buttons.iterator();

         while(var4.hasNext()) {
            DropdownButton b = (DropdownButton)var4.next();
            b.onKey(typedChar, keyCode);
         }

         super.keyTyped(typedChar, keyCode);
      }
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      Client.INSTANCE.getClientSettings().onClick(mouseX, mouseY, mouseButton);
      if (!Client.INSTANCE.getClientSettings().open) {
         Client.INSTANCE.getConfigManager().onClick(mouseX, mouseY, mouseButton);
         if (!Client.INSTANCE.getConfigManager().open) {
            if (this.position.isInside(mouseX, mouseY)) {
               Client.INSTANCE.getClientSettings().init();
               Client.INSTANCE.getClientSettings().open = true;
            }

            Iterator var5 = this.buttons.iterator();

            while(var5.hasNext()) {
               DropdownButton b = (DropdownButton)var5.next();
               b.onClick(mouseX, mouseY, mouseButton);
            }

            super.mouseClicked(mouseX, mouseY, mouseButton);
         }
      }
   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      Client.INSTANCE.getClientSettings().onRelease(mouseX, mouseY, state);
      if (!Client.INSTANCE.getClientSettings().open) {
         Client.INSTANCE.getConfigManager().onRelease(mouseX, mouseY, state);
         Iterator var5 = this.buttons.iterator();

         while(var5.hasNext()) {
            DropdownButton b = (DropdownButton)var5.next();
            b.onRelease(mouseX, mouseY, state);
         }

         super.mouseReleased(mouseX, mouseY, state);
      }
   }

   public void initGui() {
      this.initTimer = 1.0D;
      Client.INSTANCE.getConfigManager().initGui();
   }

   public boolean doesGuiPauseGame() {
      return false;
   }
}
