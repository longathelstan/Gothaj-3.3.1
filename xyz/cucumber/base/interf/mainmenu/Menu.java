package xyz.cucumber.base.interf.mainmenu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.RandomUtils;
import xyz.cucumber.base.interf.altmanager.AltManager;
import xyz.cucumber.base.interf.mainmenu.buttons.MenuButton;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.BloomUtils;
import xyz.cucumber.base.utils.render.Particle;

public class Menu extends GuiScreen {
   private List<MenuButton> buttons = new ArrayList();
   private List<Particle> particles = new ArrayList();
   private float startTime;
   public AltManager altmanager;
   private BloomUtils bloom = new BloomUtils();

   public void initGui() {
      this.buttons.clear();
      this.particles.clear();
      this.startTime = (float)System.nanoTime();
      this.altmanager = new AltManager(this);
      this.buttons.add(new MenuButton("Changelogs", new PositionUtils(10.0D, 10.0D, 90.0D, 22.0D, 1.0F), -5636248, -30327, 0));
      this.buttons.add(new MenuButton("Single Player", new PositionUtils((double)(this.width / 2 - 80), (double)(this.height / 2 - 60), 160.0D, 22.0D, 1.0F), -2404361, -13929729, 1));
      this.buttons.add(new MenuButton("Multi Player", new PositionUtils((double)(this.width / 2 - 80), (double)(this.height / 2 - 60 + 24), 160.0D, 22.0D, 1.0F), -12346117, -948229, 2));
      this.buttons.add(new MenuButton("Alt Manager", new PositionUtils((double)(this.width / 2 - 80), (double)(this.height / 2 - 60 + 48), 79.0D, 22.0D, 1.0F), -748816, -6189618, 3));
      this.buttons.add(new MenuButton("Settings", new PositionUtils((double)(this.width / 2 + 1), (double)(this.height / 2 - 60 + 48), 79.0D, 22.0D, 1.0F), -10592050, -8323109, 4));
      this.buttons.add(new MenuButton("Exit", new PositionUtils((double)(this.width / 2 - 80), (double)(this.height / 2 - 60 + 72), 160.0D, 22.0D, 1.0F), -14384521, -7809877, 5));
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      if (this.particles.size() < 200) {
         int needed = 200 - this.particles.size();

         for(int i = 0; i < needed; ++i) {
            this.particles.add(new Particle((double)RandomUtils.nextInt(0, this.width), (double)RandomUtils.nextInt(0, this.height), (double)(RandomUtils.nextInt(2, 4) / 2), 2, -1, (float)RandomUtils.nextInt(0, 360), (float)RandomUtils.nextInt(1000, 3000)));
         }
      }

      Iterator iterator = this.particles.iterator();

      while(iterator.hasNext()) {
         boolean b = ((Particle)iterator.next()).draw();
         if (b) {
            iterator.remove();
         }
      }

      Iterator var6 = this.particles.iterator();

      while(var6.hasNext()) {
         Particle p = (Particle)var6.next();
         double diffX = p.getX() - (double)mouseX;
         double diffY = p.getY() - (double)mouseY;
         double dist = Math.sqrt(diffX * diffX + diffY * diffY);
         if (dist < 50.0D) {
            RenderUtils.drawLine(p.getX(), p.getY(), (double)mouseX, (double)mouseY, 553648127, 0.5F);
         }

         Iterator var14 = this.particles.iterator();

         while(var14.hasNext()) {
            Particle p2 = (Particle)var14.next();
            if (p2 != p) {
               double difX = p.getX() - p2.getX();
               double difY = p.getY() - p2.getY();
               double dit = Math.sqrt(difX * difX + difY * difY);
               if (dit < 30.0D) {
                  RenderUtils.drawLine(p.getX(), p.getY(), p2.getX(), p2.getY(), 100663295, 0.5F);
               }
            }
         }
      }

      var6 = this.buttons.iterator();

      while(var6.hasNext()) {
         MenuButton b = (MenuButton)var6.next();
         b.draw(mouseX, mouseY);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
      RenderUtils.drawImage((double)(this.width / 2 - 60), (double)(this.height / 2 - 60 - 60), 120.0D, 50.0D, new ResourceLocation("client/images/gothaj_logo.png"), -1);
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      Iterator var5 = this.buttons.iterator();

      while(var5.hasNext()) {
         MenuButton b = (MenuButton)var5.next();
         if (b.getPosition().isInside(mouseX, mouseY)) {
            switch(b.getId()) {
            case 0:
            default:
               break;
            case 1:
               this.mc.displayGuiScreen(new GuiSelectWorld(this));
               break;
            case 2:
               this.mc.displayGuiScreen(new GuiMultiplayer(this));
               break;
            case 3:
               this.mc.displayGuiScreen(this.altmanager);
               return;
            case 4:
               this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
               break;
            case 5:
               this.mc.shutdown();
               return;
            }
         }
      }

      super.mouseClicked(mouseX, mouseY, mouseButton);
   }
}
