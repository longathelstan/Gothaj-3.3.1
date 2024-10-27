package xyz.cucumber.base.interf.DropdownClickGui.ext;

import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import xyz.cucumber.base.Client;
import xyz.cucumber.base.module.Category;
import xyz.cucumber.base.module.Mod;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.Fonts;

public class DropdownCategory implements DropdownButton {
   private Category category;
   public PositionUtils position = new PositionUtils(0.0D, 0.0D, 110.0D, 0.0D, 1.0F);
   private double dragX;
   private double dragY;
   private boolean dragging;
   private double scrollY;
   private double temp;
   private ArrayList<DropdownButton> buttons = new ArrayList();
   private boolean open = true;
   private double maxHeight = 200.0D;

   public DropdownCategory(Category category) {
      this.category = category;
      double h = 0.0D;
      Iterator var5 = Client.INSTANCE.getModuleManager().getModulesByCategory(category).iterator();

      while(var5.hasNext()) {
         Mod m = (Mod)var5.next();
         DropdownModule button = new DropdownModule(m);
         button.getPosition().setX(this.position.getX() + 5.0D);
         button.getPosition().setY(this.position.getY() + 20.0D + h - this.scrollY);
         h += button.getPosition().getHeight() + 2.0D;
         this.buttons.add(button);
      }

   }

   public void draw(int mouseX, int mouseY) {
      double h;
      if (this.position.isInside(mouseX, mouseY)) {
         h = this.position.getHeight() - 20.0D;
         if (h < this.getBigger()) {
            float g = (float)Mouse.getEventDWheel();
            double maxScrollY = this.getBigger() - h;
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
      }

      if (this.dragging) {
         this.position.setX((double)mouseX - this.dragX);
         this.position.setY((double)mouseY - this.dragY);
      }

      this.position.setHeight((this.position.getHeight() * 9.0D + this.getModHeight()) / 10.0D);
      RenderUtils.drawOutlinedRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), 805306368, 4.0D, 2.0D);
      RenderUtils.drawRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), -300410856, 4.0F);
      RenderUtils.drawImage(this.position.getX() + 3.0D, this.position.getY() + 3.0D, 15.0D, 15.0D, new ResourceLocation("client/images/" + this.category.name().toLowerCase() + ".png"), -1);
      Fonts.getFont("rb-b").drawString(this.category.name(), this.position.getX() + this.position.getWidth() / 2.0D - Fonts.getFont("rb-b").getWidth(this.category.name()) / 2.0D, this.position.getY() + 8.0D, -1);
      h = 0.0D;
      if (this.open || this.position.getHeight() >= 16.0D) {
         RenderUtils.enableScisor();
         RenderUtils.scissor(new ScaledResolution(Minecraft.getMinecraft()), this.position.getX() + 5.0D, this.position.getY() + 20.0D, 100.0D, this.position.getHeight() - 21.0D);
         Iterator var13 = this.buttons.iterator();

         while(var13.hasNext()) {
            DropdownButton b = (DropdownButton)var13.next();
            DropdownModule button = (DropdownModule)b;
            button.getPosition().setX(this.position.getX() + 5.0D);
            button.getPosition().setY(this.position.getY() + 20.0D + h - this.scrollY);
            h += button.getPosition().getHeight() + 1.0D;
            button.draw(mouseX, mouseY);
         }

         RenderUtils.disableScisor();
      }

   }

   public double getBigger() {
      double h = 0.0D;

      DropdownModule button;
      for(Iterator var4 = this.buttons.iterator(); var4.hasNext(); h += button.getPosition().getHeight() + 1.0D) {
         DropdownButton b = (DropdownButton)var4.next();
         button = (DropdownModule)b;
      }

      return h;
   }

   public double getModHeight() {
      if (!this.open) {
         return 20.0D;
      } else {
         double h = 0.0D;

         DropdownModule button;
         for(Iterator var4 = this.buttons.iterator(); var4.hasNext(); h += button.getPosition().getHeight() + 1.0D) {
            DropdownButton b = (DropdownButton)var4.next();
            button = (DropdownModule)b;
         }

         return h >= 178.0D ? this.maxHeight : h + 20.0D + 2.0D;
      }
   }

   public void onClick(int mouseX, int mouseY, int button) {
      if (this.position.isInside(mouseX, mouseY)) {
         if ((double)mouseX > this.position.getX() && this.position.getX2() > (double)mouseX && (double)mouseY > this.position.getY() + 15.0D && (double)mouseY < this.position.getY2()) {
            Iterator var5 = this.buttons.iterator();

            while(var5.hasNext()) {
               DropdownButton b = (DropdownButton)var5.next();
               DropdownModule but = (DropdownModule)b;
               if (but.getPosition().isInside(mouseX, mouseY)) {
                  but.onClick(mouseX, mouseY, button);
                  return;
               }
            }
         }

         if (button == 0) {
            this.dragging = true;
            this.dragX = (double)mouseX - this.position.getX();
            this.dragY = (double)mouseY - this.position.getY();
         }

         if (button == 1) {
            this.open = !this.open;
         }
      }

   }

   public void onRelease(int mouseX, int mouseY, int button) {
      this.dragging = false;
      Iterator var5 = this.buttons.iterator();

      while(var5.hasNext()) {
         DropdownButton b = (DropdownButton)var5.next();
         DropdownModule but = (DropdownModule)b;
         but.onRelease(mouseX, mouseY, button);
      }

   }

   public void onKey(char chr, int key) {
      Iterator var4 = this.buttons.iterator();

      while(var4.hasNext()) {
         DropdownButton b = (DropdownButton)var4.next();
         DropdownModule but = (DropdownModule)b;
         but.onKey(chr, key);
      }

   }

   public Category getCategory() {
      return this.category;
   }

   public void setCategory(Category category) {
      this.category = category;
   }

   public boolean isOpen() {
      return this.open;
   }

   public void setOpen(boolean open) {
      this.open = open;
   }

   public double getMaxHeight() {
      return this.maxHeight;
   }

   public void setMaxHeight(double maxHeight) {
      this.maxHeight = maxHeight;
   }

   public PositionUtils getPosition() {
      return this.position;
   }
}
