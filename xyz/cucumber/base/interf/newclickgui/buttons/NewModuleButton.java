package xyz.cucumber.base.interf.newclickgui.buttons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import xyz.cucumber.base.interf.newclickgui.impl.NewBooleanSetting;
import xyz.cucumber.base.interf.newclickgui.impl.NewColorSetting;
import xyz.cucumber.base.interf.newclickgui.impl.NewModeSettings;
import xyz.cucumber.base.interf.newclickgui.impl.NewNumberSetting;
import xyz.cucumber.base.interf.newclickgui.impl.NewSetting;
import xyz.cucumber.base.interf.newclickgui.impl.NewStringSetting;
import xyz.cucumber.base.module.Mod;
import xyz.cucumber.base.module.settings.BooleanSettings;
import xyz.cucumber.base.module.settings.ColorSettings;
import xyz.cucumber.base.module.settings.ModeSettings;
import xyz.cucumber.base.module.settings.ModuleSettings;
import xyz.cucumber.base.module.settings.NumberSettings;
import xyz.cucumber.base.module.settings.StringSettings;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.button.Button;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.ColorUtils;
import xyz.cucumber.base.utils.render.Fonts;
import xyz.cucumber.base.utils.render.StencilUtils;

public class NewModuleButton extends Button {
   private Mod module;
   private boolean open = false;
   private boolean binding = false;
   private double animation;
   private double scrollAnimation;
   private double defaultHeight = 25.0D;
   private PositionUtils keybind = new PositionUtils(0.0D, 0.0D, 0.0D, 10.0D, 1.0F);
   public ResourceLocation categoryImage;
   private List<NewSetting> settings = new ArrayList();
   public int c = -5592406;
   public int activeColor = -881934;

   public NewModuleButton(Mod module, ResourceLocation categoryImage) {
      this.module = module;
      this.categoryImage = categoryImage;
      this.position.setWidth(294.0D);
      this.position.setHeight(25.0D);
      Iterator var4 = module.getSettings().iterator();

      while(var4.hasNext()) {
         ModuleSettings s = (ModuleSettings)var4.next();
         if (s instanceof BooleanSettings) {
            this.settings.add(new NewBooleanSetting(s));
         }

         if (s instanceof ModeSettings) {
            this.settings.add(new NewModeSettings(s));
         }

         if (s instanceof NumberSettings) {
            this.settings.add(new NewNumberSetting(s));
         }

         if (s instanceof ColorSettings) {
            this.settings.add(new NewColorSetting(s));
         }

         if (s instanceof StringSettings) {
            this.settings.add(new NewStringSetting(s));
         }
      }

   }

   public void draw(int mouseX, int mouseY) {
      if (this.module.isEnabled()) {
         this.animation = (this.animation * 9.0D + 1.0D) / 10.0D;
      } else {
         this.animation = (this.animation * 9.0D + 0.0D) / 10.0D;
      }

      int color = ColorUtils.mix(-1, this.activeColor, this.animation, 1.0D);
      this.keybind.setWidth((this.binding ? Fonts.getFont("rb-m").getWidth("...") : Fonts.getFont("rb-m").getWidth(Keyboard.getKeyName(this.module.getKey()))) + 6.0D);
      this.keybind.setX(this.position.getX2() - 10.0D - this.keybind.getWidth());
      this.keybind.setY(this.position.getY() + this.defaultHeight / 2.0D - this.keybind.getHeight() / 2.0D);
      RenderUtils.drawRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), -15329255, 3.0F);
      RenderUtils.drawImage(this.position.getX() + 7.5D, this.position.getY() + 5.0D, 15.0D, 15.0D, this.categoryImage, color);
      RenderUtils.drawRoundedRect(this.position.getX() + 2.0D, this.position.getY() + 3.0D, this.position.getX() + 4.0D, this.position.getY() + this.defaultHeight - 3.0D, this.activeColor, 0.5D);
      RenderUtils.drawRoundedRect(this.keybind.getX(), this.keybind.getY(), this.keybind.getX2(), this.keybind.getY2(), 548055722, 1.0F);
      Fonts.getFont("rb-m").drawString(this.binding ? "..." : Keyboard.getKeyName(this.module.getKey()), this.keybind.getX() + 3.0D, this.keybind.getY() + 3.5D, Keyboard.getKeyName(this.module.getKey()).equals("NONE") ? -8947849 : this.activeColor);
      Fonts.getFont("mitr").drawString(this.module.getName(), this.position.getX() + 30.0D, this.position.getY() + 4.0D, color);
      Fonts.getFont("rb-m").drawString(this.module.getDescription(), this.position.getX() + 30.0D, this.position.getY() + this.defaultHeight - 9.0D, -5592406);
      double h = 0.0D;
      Iterator var7 = this.settings.iterator();

      NewSetting s;
      while(var7.hasNext()) {
         s = (NewSetting)var7.next();
         if ((Boolean)s.getSetting().getVisibility().get()) {
            h += s.getPosition().getHeight();
         }
      }

      if (this.open && this.settings.size() > 0) {
         this.scrollAnimation = (this.scrollAnimation * 9.0D + h) / 10.0D;
      } else {
         this.scrollAnimation = this.scrollAnimation * 9.0D / 10.0D;
      }

      if (this.scrollAnimation > 1.0D && this.scrollAnimation < h - 1.0D) {
         StencilUtils.initStencil();
         GL11.glEnable(2960);
         StencilUtils.bindWriteStencilBuffer();
         RenderUtils.drawRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), color);
         StencilUtils.bindReadStencilBuffer(1);
      }

      h = 0.0D;
      if (this.scrollAnimation > 1.0D) {
         RenderUtils.drawLine(this.position.getX() + 10.0D, this.position.getY() + this.defaultHeight, this.position.getX2() - 10.0D, this.position.getY() + this.defaultHeight, 1084926634, 1.0F);
         var7 = this.settings.iterator();

         while(var7.hasNext()) {
            s = (NewSetting)var7.next();
            if ((Boolean)s.getSetting().getVisibility().get()) {
               s.getPosition().setX(this.position.getX());
               s.getPosition().setY(this.position.getY() + this.defaultHeight + h);
               s.draw(mouseX, mouseY);
               h += s.getPosition().getHeight();
            }
         }
      }

      if (this.scrollAnimation > 1.0D && this.scrollAnimation < h - 1.0D) {
         StencilUtils.uninitStencilBuffer();
      }

      this.position.setHeight(this.defaultHeight + this.scrollAnimation);
   }

   public void onClick(int mouseX, int mouseY, int b) {
      if (this.keybind.isInside(mouseX, mouseY)) {
         this.binding = !this.binding;
      } else if (this.position.isInside(mouseX, mouseY) && this.position.getY() + 25.0D > (double)mouseY) {
         if (b == 0) {
            this.module.toggle();
         } else if (b == 1) {
            this.open = !this.open;
         }
      } else if (this.open && this.settings.size() > 0) {
         Iterator var5 = this.settings.iterator();

         while(var5.hasNext()) {
            NewSetting s = (NewSetting)var5.next();
            if ((Boolean)s.getSetting().getVisibility().get()) {
               s.onClick(mouseX, mouseY, b);
            }
         }
      }

   }

   public void onRelease(int mouseX, int mouseY, int b) {
      if (this.open && this.settings.size() > 0) {
         Iterator var5 = this.settings.iterator();

         while(var5.hasNext()) {
            NewSetting s = (NewSetting)var5.next();
            if ((Boolean)s.getSetting().getVisibility().get()) {
               s.onRelease(mouseX, mouseY, b);
            }
         }
      }

   }

   public void onKey(char typedChar, int keyCode) {
      if (this.binding) {
         if (keyCode == 1) {
            this.binding = false;
            return;
         }

         this.module.setKey(keyCode);
         this.binding = false;
      } else if (this.open && this.settings.size() > 0) {
         Iterator var4 = this.settings.iterator();

         while(var4.hasNext()) {
            NewSetting s = (NewSetting)var4.next();
            if ((Boolean)s.getSetting().getVisibility().get()) {
               s.onKey(typedChar, keyCode);
            }
         }
      }

   }
}
