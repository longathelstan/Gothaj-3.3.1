package xyz.cucumber.base.interf.config.ext.panel;

import net.minecraft.util.ResourceLocation;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.cfgs.ConfigFileUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.ColorUtils;
import xyz.cucumber.base.utils.render.Fonts;

public abstract class Panel {
   String creator;
   String name;
   PositionUtils position = new PositionUtils(0.0D, 0.0D, 129.0D, 40.0D, 1.0F);
   PositionUtils informations = new PositionUtils(0.0D, 0.0D, Fonts.getFont("rb-r-13").getWidth("Hover to view information"), 10.0D, 1.0F);
   PositionUtils save = new PositionUtils(0.0D, 0.0D, 8.0D, 8.0D, 1.0F);
   PositionUtils load = new PositionUtils(0.0D, 0.0D, 8.0D, 8.0D, 1.0F);
   String time;
   String description = "";
   private int animation;

   public void draw(int mouseX, int mouseY) {
      if (this.position.isInside(mouseX, mouseY)) {
         this.animation = (this.animation * 14 + 80) / 15;
      } else {
         this.animation = (this.animation * 14 + 40) / 15;
      }

      RenderUtils.drawRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), ColorUtils.getAlphaColor(889916946, this.animation), 3.0F);
      Fonts.getFont("mitr").drawString(this.name.toUpperCase(), this.position.getX() + 5.0D, this.position.getY() + 4.0D, -3355444);
      Fonts.getFont("rb-m-13").drawString("@" + this.creator, this.position.getX() + 5.0D, this.position.getY() + 14.0D, -12877341);
      Fonts.getFont("rb-r-13").drawString(ConfigFileUtils.getTimeDifference(this.time), this.position.getX() + 5.0D + Fonts.getFont("rb-m-13").getWidth("@" + this.creator) + 5.0D, this.position.getY() + 14.0D, -5592406);
      this.informations.setX(this.position.getX() + 5.0D);
      this.informations.setY(this.position.getY2() - 10.0D);
      Fonts.getFont("rb-r-13").drawString("Hover to view information", this.position.getX() + 5.0D, this.position.getY2() - 8.0D, -8947849);
      this.save.setX(this.position.getX() + 10.0D + Fonts.getFont("rb-r-13").getWidth("Hover to view information") + 2.0D);
      this.save.setY(this.position.getY2() - 15.0D);
      this.load.setX(this.position.getX() + 10.0D + Fonts.getFont("rb-r-13").getWidth("Hover to view information") + 17.0D);
      this.load.setY(this.position.getY2() - 15.0D);
      RenderUtils.drawImage(this.load.getX(), this.load.getY(), this.load.getWidth(), this.load.getHeight(), new ResourceLocation("client/images/load.png"), -1);
      RenderUtils.drawImage(this.save.getX(), this.save.getY(), this.save.getWidth(), this.save.getHeight(), new ResourceLocation("client/images/save.png"), -1);
      if (this.informations.isInside(mouseX, mouseY) && this.description != "") {
         RenderUtils.drawRoundedRect(this.informations.getX() + this.informations.getWidth() / 2.0D - 50.0D, this.informations.getY() - 70.0D, this.informations.getX() + this.informations.getWidth() / 2.0D + 50.0D, this.informations.getY(), -15724528, 2.0F);
         RenderUtils.drawOutlinedRoundedRect(this.informations.getX() + this.informations.getWidth() / 2.0D - 50.0D, this.informations.getY() - 70.0D, this.informations.getX() + this.informations.getWidth() / 2.0D + 50.0D, this.informations.getY(), 544700279, 2.0D, 1.0D);
         String[] descSplitted = this.description.split(" ");
         String finalStr = "";
         int y = 0;

         for(int i = 0; i < descSplitted.length; ++i) {
            if (Fonts.getFont("rb-r-13").getWidth(finalStr) + Fonts.getFont("rb-r-13").getWidth(descSplitted[i]) < 94.0D) {
               finalStr = finalStr + (i == 0 ? "" : " ") + descSplitted[i];
            } else if (Fonts.getFont("rb-r-13").getWidth(descSplitted[i]) > 94.0D) {
               String[] sp = descSplitted[0].split("");
               String[] var11 = sp;
               int var10 = sp.length;

               for(int var9 = 0; var9 < var10; ++var9) {
                  String s = var11[var9];
                  if (Fonts.getFont("rb-r-13").getWidth(finalStr) + Fonts.getFont("rb-r-13").getWidth(s) < 94.0D) {
                     finalStr = finalStr + s;
                  } else {
                     Fonts.getFont("rb-r-13").drawString(finalStr, this.informations.getX() + this.informations.getWidth() / 2.0D - 47.0D, this.informations.getY() - 65.0D + (double)(10 * y), -8947849);
                     finalStr = s;
                     ++y;
                  }
               }
            } else {
               Fonts.getFont("rb-r-13").drawString(finalStr, this.informations.getX() + this.informations.getWidth() / 2.0D - 47.0D, this.informations.getY() - 65.0D + (double)(10 * y), -8947849);
               ++y;
               finalStr = descSplitted[i];
            }
         }

         if (!finalStr.equals("")) {
            Fonts.getFont("rb-r-13").drawString(finalStr, this.informations.getX() + this.informations.getWidth() / 2.0D - 47.0D, this.informations.getY() - 65.0D + (double)(10 * y), -8947849);
         }
      }

   }

   public void onClick(int mouseX, int mouseY, int button) {
      if (this.save.isInside(mouseX, mouseY) && button == 0) {
         this.save();
      }

      if (this.load.isInside(mouseX, mouseY) && button == 0) {
         this.load();
      }

   }

   public abstract void load();

   public abstract void save();

   public PositionUtils getPosition() {
      return this.position;
   }

   public void setPosition(PositionUtils position) {
      this.position = position;
   }

   public String getCreator() {
      return this.creator;
   }

   public String getName() {
      return this.name;
   }

   public PositionUtils getInformations() {
      return this.informations;
   }

   public PositionUtils getSave() {
      return this.save;
   }

   public PositionUtils getLoad() {
      return this.load;
   }

   public String getTime() {
      return this.time;
   }

   public String getDescription() {
      return this.description;
   }

   public int getAnimation() {
      return this.animation;
   }
}
