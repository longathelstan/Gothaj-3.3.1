package xyz.cucumber.base.interf.clientsettings.ext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.lwjgl.opengl.GL11;
import xyz.cucumber.base.interf.clientsettings.ext.impl.ClientSetting;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.Fonts;

public abstract class Setting {
   private String name;
   private ArrayList<ClientSetting> settings = new ArrayList();
   private PositionUtils position = new PositionUtils(0.0D, 0.0D, 150.0D, 250.0D, 1.0F);
   private PositionUtils settingPosition = new PositionUtils(0.0D, 0.0D, 150.0D, 216.0D, 1.0F);

   public Setting(String name) {
      this.name = name;
   }

   public void draw(int mouseX, int mouseY) {
      RenderUtils.drawRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), -15395563, 3.0F);
      GL11.glPushMatrix();
      GL11.glTranslated(this.position.getX() + this.position.getWidth() / 2.0D - Fonts.getFont("rb-b").getWidth(this.name) * 1.1D / 2.0D, 5.0D, 0.0D);
      GL11.glScaled(1.1D, 1.1D, 1.0D);
      Fonts.getFont("rb-b").drawString(this.name, 0.0D, this.position.getY() - 12.5D, -1);
      GL11.glScaled(1.0D, 1.0D, 1.0D);
      GL11.glPopMatrix();
      RenderUtils.drawLine(this.position.getX() + 7.0D, this.position.getY() + 17.0D, this.position.getX2() - 7.0D, this.position.getY() + 17.0D, 1081571191, 2.0F);
      this.settingPosition.setX(this.position.getX());
      this.settingPosition.setY(this.position.getY() + 17.0D);
      double h = 0.0D;

      ClientSetting s;
      for(Iterator var6 = this.settings.iterator(); var6.hasNext(); h += s.getPosition().getHeight()) {
         s = (ClientSetting)var6.next();
         s.getPosition().setX(this.settingPosition.getX());
         s.getPosition().setY(this.settingPosition.getY() + h);
         s.draw(mouseX, mouseY);
      }

   }

   public void onClick(int mouseX, int mouseY, int button) {
      Iterator var5 = this.settings.iterator();

      while(var5.hasNext()) {
         ClientSetting s = (ClientSetting)var5.next();
         s.onClick(mouseX, mouseY, button);
      }

   }

   public void onRelease(int mouseX, int mouseY, int button) {
      Iterator var5 = this.settings.iterator();

      while(var5.hasNext()) {
         ClientSetting s = (ClientSetting)var5.next();
         s.onRelease(mouseX, mouseY, button);
      }

   }

   public PositionUtils getPosition() {
      return this.position;
   }

   public void setPosition(PositionUtils position) {
      this.position = position;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public ArrayList<ClientSetting> getSettings() {
      return this.settings;
   }

   public void setSettings(ArrayList<ClientSetting> settings) {
      this.settings = settings;
   }

   public void addSettings(ClientSetting... clientSettings) {
      this.settings.addAll(Arrays.asList(clientSettings));
   }
}
