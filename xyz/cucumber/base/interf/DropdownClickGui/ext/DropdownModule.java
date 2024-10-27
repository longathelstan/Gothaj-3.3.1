package xyz.cucumber.base.interf.DropdownClickGui.ext;

import java.util.ArrayList;
import java.util.Iterator;
import org.lwjgl.opengl.GL11;
import xyz.cucumber.base.interf.DropdownClickGui.ext.Settings.DropdownClickGuiBoolean;
import xyz.cucumber.base.interf.DropdownClickGui.ext.Settings.DropdownClickGuiColor;
import xyz.cucumber.base.interf.DropdownClickGui.ext.Settings.DropdownClickGuiKeybind;
import xyz.cucumber.base.interf.DropdownClickGui.ext.Settings.DropdownClickGuiMode;
import xyz.cucumber.base.interf.DropdownClickGui.ext.Settings.DropdownClickGuiNumber;
import xyz.cucumber.base.interf.DropdownClickGui.ext.Settings.DropdownClickGuiSettings;
import xyz.cucumber.base.interf.DropdownClickGui.ext.Settings.DropdownClickGuiString;
import xyz.cucumber.base.module.Mod;
import xyz.cucumber.base.module.settings.BooleanSettings;
import xyz.cucumber.base.module.settings.ColorSettings;
import xyz.cucumber.base.module.settings.ModeSettings;
import xyz.cucumber.base.module.settings.ModuleSettings;
import xyz.cucumber.base.module.settings.NumberSettings;
import xyz.cucumber.base.module.settings.StringSettings;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.ColorUtils;
import xyz.cucumber.base.utils.render.Fonts;
import xyz.cucumber.base.utils.render.StencilUtils;

public class DropdownModule implements DropdownButton {
   private Mod module;
   private double lastX;
   private double lastY;
   public PositionUtils position = new PositionUtils(0.0D, 0.0D, 100.0D, 15.0D, 1.0F);
   private boolean open;
   private double defaultHeight = 15.0D;
   private double Coloranimation;
   private double openAnimationArrow;
   private double openAnimation;
   private ArrayList<DropdownClickGuiSettings> settings = new ArrayList();

   public DropdownModule(Mod module) {
      this.module = module;
      this.settings.add(new DropdownClickGuiKeybind(module, new PositionUtils(0.0D, 0.0D, 100.0D, 12.0D, 1.0F)));
      Iterator var3 = module.getSettings().iterator();

      while(var3.hasNext()) {
         ModuleSettings s = (ModuleSettings)var3.next();
         if (s instanceof ModeSettings) {
            this.settings.add(new DropdownClickGuiMode(s, new PositionUtils(0.0D, 0.0D, 100.0D, 12.0D, 1.0F)));
         }

         if (s instanceof BooleanSettings) {
            this.settings.add(new DropdownClickGuiBoolean(s, new PositionUtils(0.0D, 0.0D, 100.0D, 12.0D, 1.0F)));
         }

         if (s instanceof StringSettings) {
            this.settings.add(new DropdownClickGuiString(s, new PositionUtils(0.0D, 0.0D, 100.0D, 12.0D, 1.0F)));
         }

         if (s instanceof ColorSettings) {
            this.settings.add(new DropdownClickGuiColor(s, new PositionUtils(0.0D, 0.0D, 100.0D, 12.0D, 1.0F)));
         }

         if (s instanceof NumberSettings) {
            this.settings.add(new DropdownClickGuiNumber(s, new PositionUtils(0.0D, 0.0D, 100.0D, 15.0D, 1.0F)));
         }
      }

   }

   public Mod getModule() {
      return this.module;
   }

   public void setModule(Mod module) {
      this.module = module;
   }

   public PositionUtils getPosition() {
      return this.position;
   }

   public void setPosition(PositionUtils position) {
      this.position = position;
   }

   public void draw(int mouseX, int mouseY) {
      this.position.setWidth(100.0D);
      int color = -297323283;
      if (this.module.isEnabled()) {
         this.Coloranimation = (this.Coloranimation * 10.0D + 100.0D) / 11.0D;
      } else {
         this.Coloranimation = this.Coloranimation < 10.0D ? 0.0D : this.Coloranimation - 10.0D;
      }

      RenderUtils.drawRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), -15527149, 3.0F);
      color = ColorUtils.getAlphaColor(-10402868, (int)this.Coloranimation);
      Fonts.getFont("rb-m").drawString(this.module.getName(), this.position.getX() + 3.0D, this.position.getY() + 6.0D, ColorUtils.mix(-5592406, color, this.Coloranimation, 100.0D));
      this.renderArrow(this.position.getX2() - 7.0D, this.position.getY() + 7.5D, 4.0D, -5592406, this.openAnimationArrow);
      this.position.setHeight((this.position.getHeight() * 8.0D + this.defaultHeight + (this.open ? this.getSettingsHeight() : 0.0D)) / 9.0D);
      if (!this.open && !(this.position.getHeight() >= this.defaultHeight + 1.0D)) {
         this.openAnimationArrow = this.openAnimationArrow * 10.0D / 11.0D;
      } else {
         this.openAnimationArrow = (this.openAnimationArrow * 10.0D + 180.0D) / 11.0D;
         double s = 0.0D;
         StencilUtils.initStencil();
         GL11.glEnable(2960);
         StencilUtils.bindWriteStencilBuffer();
         RenderUtils.drawRect(this.position.getX(), this.position.getY() + this.defaultHeight, this.position.getX2(), this.position.getY() + this.position.getHeight(), -1);
         StencilUtils.bindReadStencilBuffer(1);
         Iterator var7 = this.settings.iterator();

         while(true) {
            DropdownClickGuiSettings set;
            do {
               if (!var7.hasNext()) {
                  StencilUtils.uninitStencilBuffer();
                  return;
               }

               set = (DropdownClickGuiSettings)var7.next();
            } while(set.getMainSetting() != null && !(Boolean)set.getMainSetting().getVisibility().get());

            set.getPosition().setX(this.position.getX());
            set.getPosition().setY(this.position.getY() + this.defaultHeight + s);
            set.draw(mouseX, mouseY);
            s += set.getPosition().getHeight();
         }
      }
   }

   public double getSettingsHeight() {
      double s = 0.0D;
      Iterator var4 = this.settings.iterator();

      while(true) {
         DropdownClickGuiSettings set;
         do {
            if (!var4.hasNext()) {
               return s;
            }

            set = (DropdownClickGuiSettings)var4.next();
         } while(set.getMainSetting() != null && !(Boolean)set.getMainSetting().getVisibility().get());

         s += set.getPosition().getHeight();
      }
   }

   public void onClick(int mouseX, int mouseY, int button) {
      if (this.position.isInside(mouseX, mouseY)) {
         Iterator var5 = this.settings.iterator();

         DropdownClickGuiSettings set;
         do {
            do {
               if (!var5.hasNext()) {
                  if (button == 0) {
                     this.module.toggle();
                  }

                  if (button == 1) {
                     this.open = !this.open;
                  }

                  return;
               }

               set = (DropdownClickGuiSettings)var5.next();
            } while(set.getMainSetting() != null && !(Boolean)set.getMainSetting().getVisibility().get());
         } while(!set.getPosition().isInside(mouseX, mouseY));

         set.onClick(mouseX, mouseY, button);
      }
   }

   public void onRelease(int mouseX, int mouseY, int button) {
      Iterator var5 = this.settings.iterator();

      while(true) {
         DropdownClickGuiSettings set;
         do {
            if (!var5.hasNext()) {
               return;
            }

            set = (DropdownClickGuiSettings)var5.next();
         } while(set.getMainSetting() != null && !(Boolean)set.getMainSetting().getVisibility().get());

         set.onRelease(mouseX, mouseY, button);
      }
   }

   public void onKey(char chr, int key) {
      Iterator var4 = this.settings.iterator();

      while(true) {
         DropdownClickGuiSettings set;
         do {
            if (!var4.hasNext()) {
               return;
            }

            set = (DropdownClickGuiSettings)var4.next();
         } while(set.getMainSetting() != null && !(Boolean)set.getMainSetting().getVisibility().get());

         set.onKey(chr, key);
      }
   }

   public void renderArrow(double x, double y, double size, int color, double angle) {
      GL11.glPushMatrix();
      GL11.glTranslated(x, y, 0.0D);
      GL11.glRotated(angle, 0.0D, 0.0D, 1.0D);
      GL11.glTranslated(-x, -y, 0.0D);
      RenderUtils.start2D();
      RenderUtils.color(color);
      GL11.glBegin(7);
      GL11.glVertex2d(x - size / 3.0D, y - size / 3.0D);
      GL11.glVertex2d(x, y);
      GL11.glVertex2d(x + size / 3.0D, y - size / 3.0D);
      GL11.glVertex2d(x, y + size / 3.0D);
      GL11.glEnd();
      RenderUtils.stop2D();
      GL11.glRotated(0.0D, 0.0D, 0.0D, 1.0D);
      GL11.glPopMatrix();
   }
}
