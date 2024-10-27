package xyz.cucumber.base.interf.newclickgui.impl;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import xyz.cucumber.base.module.settings.ModeSettings;
import xyz.cucumber.base.module.settings.ModuleSettings;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.Fonts;

public class NewModeSettings extends NewSetting {
   private double defheight = 12.0D;
   private LinkedHashMap<String, PositionUtils> modes = new LinkedHashMap();

   public NewModeSettings(ModuleSettings s) {
      this.setting = s;
      this.modes.clear();
      Iterator var3 = ((ModeSettings)this.setting).getModes().iterator();

      while(var3.hasNext()) {
         String mode = (String)var3.next();
         this.modes.put(mode, new PositionUtils(0.0D, 0.0D, 1.0D, 9.0D, 1.0F));
      }

      this.position.setWidth(294.0D);
      this.position.setHeight(this.defheight);
   }

   public void draw(int mouseX, int mouseY) {
      Fonts.getFont("rb-m").drawString(((ModeSettings)this.setting).getName(), this.position.getX() + 12.0D, this.position.getY() + 6.0D, -1);
      double x = Fonts.getFont("rb-m").getWidth(((ModeSettings)this.setting).getName()) + 12.0D + 3.0D;
      int y = 0;
      Iterator var7 = this.modes.entrySet().iterator();

      while(var7.hasNext()) {
         Entry<String, PositionUtils> pos = (Entry)var7.next();
         ((PositionUtils)pos.getValue()).setWidth(Fonts.getFont("rb-m-13").getWidth((String)pos.getKey()) + 2.0D);
         if (x + Fonts.getFont("rb-m-13").getWidth((String)pos.getKey()) + 3.0D <= this.position.getWidth() - 12.0D) {
            ((PositionUtils)pos.getValue()).setX(this.position.getX() + x);
            ((PositionUtils)pos.getValue()).setY(this.position.getY() + 3.0D + (double)(y * 10));
            x += Fonts.getFont("rb-m-13").getWidth((String)pos.getKey()) + 3.0D;
         } else {
            x = Fonts.getFont("rb-m-13").getWidth(((ModeSettings)this.setting).getName()) + 12.0D + 3.0D;
            ++y;
            ((PositionUtils)pos.getValue()).setX(this.position.getX() + x);
            ((PositionUtils)pos.getValue()).setY(this.position.getY() + 3.0D + (double)(y * 10));
            x += Fonts.getFont("rb-m-13").getWidth((String)pos.getKey()) + 3.0D;
         }

         int color = -13354432;
         if (((ModeSettings)this.setting).getMode().equals(pos.getKey())) {
            color = -881934;
         }

         RenderUtils.drawRoundedRect(((PositionUtils)pos.getValue()).getX(), ((PositionUtils)pos.getValue()).getY(), ((PositionUtils)pos.getValue()).getX2(), ((PositionUtils)pos.getValue()).getY2(), color, 1.0F);
         Fonts.getFont("rb-m-13").drawString((String)pos.getKey(), ((PositionUtils)pos.getValue()).getX() + 2.0D, ((PositionUtils)pos.getValue()).getY() + 3.0D, -1);
      }

      this.position.setHeight(this.defheight + (double)(10 * y) + 4.0D);
   }

   public void onClick(int mouseX, int mouseY, int b) {
      Iterator var5 = this.modes.entrySet().iterator();

      while(var5.hasNext()) {
         Entry<String, PositionUtils> pos = (Entry)var5.next();
         if (((PositionUtils)pos.getValue()).isInside(mouseX, mouseY) && b == 0) {
            ((ModeSettings)this.setting).setMode((String)pos.getKey());
         }
      }

   }
}
