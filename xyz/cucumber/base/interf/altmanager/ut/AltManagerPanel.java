package xyz.cucumber.base.interf.altmanager.ut;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.litarvan.openauth.microsoft.model.response.MinecraftProfile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.Session;
import xyz.cucumber.base.interf.altmanager.AltManager;
import xyz.cucumber.base.interf.altmanager.impl.AltManagerClickable;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.button.Button;
import xyz.cucumber.base.utils.button.CloseButton;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.ColorUtils;
import xyz.cucumber.base.utils.render.Fonts;

public class AltManagerPanel {
   public GuiTextField username;
   public GuiTextField password;
   private PositionUtils position;
   private List<Button> buttons = new ArrayList();
   public boolean open;
   private double animation;
   private double fieldAnimation1;
   private double fieldAnimation2;
   AltManager altManager;

   public AltManagerPanel(AltManager altManager) {
      this.altManager = altManager;
      this.open = false;
      this.position = new PositionUtils((double)(altManager.width / 2 - 75), (double)altManager.height, 150.0D, 160.0D, 1.0F);
      this.username = new GuiTextField(0, Minecraft.getMinecraft().fontRendererObj, (int)(this.position.getX() + 2.0D), (int)(this.position.getY() + this.position.getHeight() / 2.0D - 5.0D - 25.0D), 146, 25);
      this.password = new GuiTextField(0, Minecraft.getMinecraft().fontRendererObj, (int)(this.position.getX() + 2.0D), (int)(this.position.getY() + this.position.getHeight() / 2.0D + 5.0D), 146, 25);
      double centerLX = this.position.getX() + this.position.getWidth() / 2.0D;
      double centerLY = this.position.getY() + this.position.getHeight() / 2.0D;
      this.buttons.clear();
      this.buttons.add(new AltManagerClickable(0, "Add", this.position.getX() + this.position.getWidth() / 2.0D - 72.5D, this.position.getY() + this.position.getHeight() / 2.0D - 17.5D, 70.0D, 15.0D));
      this.buttons.add(new AltManagerClickable(1, "Login", this.position.getX() + this.position.getWidth() / 2.0D + 2.5D, this.position.getY() + this.position.getHeight() / 2.0D - 17.5D, 70.0D, 15.0D));
      this.buttons.add(new CloseButton(2, this.position.getX() + this.position.getWidth() / 2.0D - 17.0D, this.position.getY() + 2.0D, 15.0D, 15.0D));
   }

   public void draw(int mouseX, int mouseY) {
      this.updatePositions();
      RenderUtils.drawRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), 548055722, 3.0F);
      RenderUtils.drawOutlinedRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), 1350006647, 3.0D, 0.5D);
      String[] s = "Add account".split("");
      double w = 0.0D;
      String[] var9 = s;
      int var8 = s.length;

      for(int var7 = 0; var7 < var8; ++var7) {
         String t = var9[var7];
         Fonts.getFont("rb-b-h").drawStringWithShadow(t, this.position.getX() + this.position.getWidth() / 2.0D - Fonts.getFont("rb-b-h").getWidth("Add account") / 2.0D + w, this.position.getY() + 7.0D, ColorUtils.mix(-10007340, -12751688, Math.sin(Math.toRadians((double)(System.nanoTime() / 1000000L) + w * 10.0D) / 3.0D) + 1.0D, 2.0D), 1157627904);
         w += Fonts.getFont("rb-b-h").getWidth(t);
      }

      this.renderTextField(this.username);
      this.renderTextField(this.password);
      Iterator var11 = this.buttons.iterator();

      while(var11.hasNext()) {
         Button b = (Button)var11.next();
         b.draw(mouseX, mouseY);
      }

   }

   public void key(char typedChar, int keyCode) {
      if (typedChar == '\t') {
         if (!this.username.isFocused()) {
            this.username.setFocused(true);
         }

         if (!this.password.isFocused()) {
            this.password.setFocused(true);
         }
      }

      this.username.textboxKeyTyped(typedChar, keyCode);
      this.password.textboxKeyTyped(typedChar, keyCode);
   }

   public void click(int mouseX, int mouseY, int b) {
      if (this.open && !this.position.isInside(mouseX, mouseY)) {
         this.open = false;
      }

      Iterator var5 = this.getButtons().iterator();

      while(var5.hasNext()) {
         Button butt = (Button)var5.next();
         if (butt.getPosition().isInside(mouseX, mouseY) && b == 0) {
            Session session;
            switch(butt.getId()) {
            case 0:
               if (this.username.getText().equals("")) {
                  return;
               }

               if (this.password.getText().equals("")) {
                  this.altManager.sessions.add(new AltManagerSession(this.altManager, new Session(this.username.getText(), "0", "0", "mojang")));
                  return;
               }

               session = this.loginToAccount(this.username.getText(), this.password.getText());
               if (session == null) {
                  return;
               }

               this.altManager.sessions.add(new AltManagerSession(this.altManager, session));
               break;
            case 1:
               if (this.username.getText().equals("")) {
                  return;
               }

               if (this.password.getText().equals("")) {
                  Minecraft.getMinecraft().session = new Session(this.username.getText(), "0", "0", "mojang");
                  return;
               }

               session = this.loginToAccount(this.username.getText(), this.password.getText());
               if (session == null) {
                  return;
               }

               Minecraft.getMinecraft().session = session;
               break;
            case 2:
               this.open = false;
            }
         }
      }

      this.username.mouseClicked(mouseX, mouseY, b);
      this.password.mouseClicked(mouseX, mouseY, b);
   }

   public Session loginToAccount(String email, String password) {
      try {
         MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
         MicrosoftAuthResult result = authenticator.loginWithCredentials(email, password);
         MinecraftProfile profile = result.getProfile();
         Session session = new Session(profile.getName(), profile.getId(), result.getAccessToken(), "mojang");
         return session;
      } catch (Exception var7) {
         var7.printStackTrace();
         return null;
      }
   }

   public void updatePositions() {
      double centerLX = this.position.getX() + this.position.getWidth() / 2.0D;
      double centerLY = this.position.getY() + this.position.getHeight() / 2.0D;
      ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
      if (!this.open) {
         this.position.setY((this.position.getY() * 17.0D + (double)sr.getScaledHeight()) / 18.0D);
      } else {
         this.position.setY((this.position.getY() * 17.0D + (double)(sr.getScaledHeight() / 2 - 75)) / 18.0D);
      }

      Iterator var7 = this.buttons.iterator();

      while(var7.hasNext()) {
         Button b = (Button)var7.next();
         switch(b.getId()) {
         case 0:
            b.getPosition().setX(this.position.getX() + this.position.getWidth() / 2.0D - 72.5D);
            b.getPosition().setY(this.position.getY() + this.position.getHeight() - 17.5D);
            break;
         case 1:
            b.getPosition().setX(this.position.getX() + this.position.getWidth() / 2.0D + 2.5D);
            b.getPosition().setY(this.position.getY() + this.position.getHeight() - 17.5D);
            break;
         case 2:
            b.getPosition().setX(this.position.getX() + this.position.getWidth() - 17.0D);
            b.getPosition().setY(this.position.getY() + 2.0D);
         }
      }

      this.username.xPosition = (int)(this.position.getX() + 2.0D);
      this.username.yPosition = (int)(this.position.getY() + this.position.getHeight() / 2.0D - 5.0D - 22.0D);
      this.password.xPosition = (int)(this.position.getX() + 2.0D);
      this.password.yPosition = (int)(this.position.getY() + this.position.getHeight() / 2.0D + 5.0D);
   }

   private double spacing(GuiTextField field) {
      String[] text = field.getText().split("");
      double d = 0.0D;
      int i = 0;
      String[] var9 = text;
      int var8 = text.length;

      for(int var7 = 0; var7 < var8; ++var7) {
         String t = var9[var7];
         if (field.getCursorPosition() == i) {
            return d;
         }

         ++i;
         d += field == this.password ? Fonts.getFont("rb-r").getWidth("*") : Fonts.getFont("rb-r").getWidth(t);
      }

      return d;
   }

   public void renderTextField(GuiTextField field) {
      RenderUtils.drawRoundedRect((double)field.xPosition, (double)field.yPosition, (double)(field.xPosition + field.width), (double)(field.yPosition + field.height), 900377258, 3.0F);
      if (field.isFocused()) {
         if (field == this.username) {
            this.fieldAnimation2 = (this.fieldAnimation2 * 10.0D + 10.0D) / 11.0D;
            RenderUtils.drawOutlinedRoundedRect((double)field.xPosition, (double)field.yPosition, (double)(field.xPosition + field.width), (double)(field.yPosition + field.height), ColorUtils.getAlphaColor(ColorUtils.mix(900377258, -1, this.fieldAnimation2, 10.0D), 10), 3.0D, 0.1D);
         } else {
            this.fieldAnimation1 = (this.fieldAnimation1 * 10.0D + 10.0D) / 11.0D;
            RenderUtils.drawOutlinedRoundedRect((double)field.xPosition, (double)field.yPosition, (double)(field.xPosition + field.width), (double)(field.yPosition + field.height), ColorUtils.getAlphaColor(ColorUtils.mix(900377258, -1, this.fieldAnimation1, 10.0D), 10), 3.0D, 0.1D);
         }

         Fonts.getFont("rb-r").drawString("|", (double)(field.xPosition + 4) + this.spacing(field), (double)(field.yPosition + 11), -1);
      } else if (field == this.username) {
         this.fieldAnimation2 = this.fieldAnimation2 * 6.0D / 7.0D;
         RenderUtils.drawOutlinedRoundedRect((double)field.xPosition, (double)field.yPosition, (double)(field.xPosition + field.width), (double)(field.yPosition + field.height), ColorUtils.getAlphaColor(ColorUtils.mix(900377258, -1, this.fieldAnimation2, 10.0D), 10), 3.0D, 0.1D);
      } else {
         this.fieldAnimation1 = this.fieldAnimation1 * 6.0D / 7.0D;
         RenderUtils.drawOutlinedRoundedRect((double)field.xPosition, (double)field.yPosition, (double)(field.xPosition + field.width), (double)(field.yPosition + field.height), ColorUtils.getAlphaColor(ColorUtils.mix(900377258, -1, this.fieldAnimation1, 10.0D), 10), 3.0D, 0.1D);
      }

      if (field.getText().equals("")) {
         if (field == this.username) {
            Fonts.getFont("rb-r").drawString("Name / E-mail", (double)(field.xPosition + 4), (double)(field.yPosition + 11), -5592406);
         } else {
            Fonts.getFont("rb-r").drawString("Password", (double)(field.xPosition + 4), (double)(field.yPosition + 11), -5592406);
         }
      } else {
         int color = -5592406;
         if (field.isFocused()) {
            color = -1;
         }

         if (field == this.password) {
            String ps = "";
            String[] var7;
            int var6 = (var7 = field.getText().split("")).length;

            for(int var5 = 0; var5 < var6; ++var5) {
               String var10000 = var7[var5];
               ps = ps + "*";
            }

            Fonts.getFont("rb-r").drawString(ps, (double)(field.xPosition + 4), (double)field.yPosition + 12.5D, color);
            return;
         }

         Fonts.getFont("rb-r").drawString(field.getText(), (double)(field.xPosition + 4), (double)(field.yPosition + 11), color);
      }

   }

   public List<Button> getButtons() {
      return this.buttons;
   }

   public void setButtons(List<Button> buttons) {
      this.buttons = buttons;
   }
}
