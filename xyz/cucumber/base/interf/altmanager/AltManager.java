package xyz.cucumber.base.interf.altmanager;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.apache.commons.lang3.RandomStringUtils;
import org.lwjgl.input.Mouse;
import xyz.cucumber.base.Client;
import xyz.cucumber.base.file.files.AccountsFile;
import xyz.cucumber.base.interf.altmanager.impl.AltManagerClickable;
import xyz.cucumber.base.interf.altmanager.ut.AltManagerPanel;
import xyz.cucumber.base.interf.altmanager.ut.AltManagerSession;
import xyz.cucumber.base.interf.mainmenu.Menu;
import xyz.cucumber.base.microsoft.MicrosoftLogin;
import xyz.cucumber.base.utils.BroswerUtil;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.StringUtils;
import xyz.cucumber.base.utils.button.Button;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.BlurUtils;
import xyz.cucumber.base.utils.render.ColorUtils;
import xyz.cucumber.base.utils.render.Fonts;

public class AltManager extends GuiScreen {
   private GuiScreen menu;
   private long startTime;
   public ArrayList<Button> buttons = new ArrayList();
   public static String[] firstNames = new String[]{"Aban", "Ammar", "Fakhir", "Hassan", "Jabbar", "Kareem", "Muwaffaq", "Omar", "Rashid", "Suhail", "Ahmed", "Muhamad", "Amir", "Allah"};
   public static String[] lastNames = new String[]{"Abadi", "Asghar", "Khouri", "Boulos", "Nasser", "Safar", "Wasem", "Handal", "Fakhoury", "Awad", "Kasem", "Akbar"};
   private ResourceLocation head;
   private AltManagerPanel panel;
   public AltManagerSession active;
   public ArrayList<AltManagerSession> sessions = new ArrayList();
   private PositionUtils position;
   private double scrollY;
   private double temp;

   public AltManager(GuiScreen menu) {
      this.menu = menu;
   }

   public void initGui() {
      this.buttons.clear();
      this.sessions.clear();
      this.position = new PositionUtils(130.0D, 2.0D, (double)(this.width - 6 - 130), (double)(this.height - 4), 1.0F);
      ((AccountsFile)Client.INSTANCE.getFileManager().getFile(AccountsFile.class)).load(this);
      this.startTime = System.nanoTime();
      this.panel = new AltManagerPanel(this);
      this.buttons.add(new AltManagerClickable(1, "Add", 15.0D, (double)(this.height - 70), 100.0D, 15.0D));
      this.buttons.add(new AltManagerClickable(2, "Remove", 15.0D, (double)(this.height - 53), 100.0D, 15.0D));
      this.buttons.add(new AltManagerClickable(3, "Random cracked", 15.0D, (double)(this.height - 87), 100.0D, 15.0D));
      this.buttons.add(new AltManagerClickable(5, "Legit cracked", 15.0D, (double)(this.height - 104), 100.0D, 15.0D));
      this.buttons.add(new AltManagerClickable(4, "Cookie from Clipboard", 15.0D, (double)(this.height - 121), 100.0D, 15.0D));
      this.buttons.add(new AltManagerClickable(6, "Cookie Login (Best Version)", 15.0D, (double)(this.height - 138), 100.0D, 15.0D));
   }

   public double getBigger() {
      double x = 0.0D;
      double y = 0.0D;
      Iterator var6 = this.sessions.iterator();

      while(var6.hasNext()) {
         AltManagerSession session = (AltManagerSession)var6.next();
         ++x;
         if (x == 3.0D) {
            x = 0.0D;
            y += 17.0D;
         }
      }

      return y;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      RenderUtils.drawOtherBackground(0.0D, 0.0D, (double)this.width, (double)this.height, (float)(System.nanoTime() - this.startTime) / 1.0E9F);
      BlurUtils.renderBlur(8.0F);
      RenderUtils.drawRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), 805306368, 3.0D);
      int x = 0;
      double y = 0.0D;

      AltManagerSession session;
      for(Iterator var8 = this.sessions.iterator(); var8.hasNext(); session.draw(mouseX, mouseY)) {
         session = (AltManagerSession)var8.next();
         session.getPosition().setX(this.position.getX() + 2.0D + (double)x * (this.position.getWidth() - 8.0D + 5.0D) / 3.0D);
         session.getPosition().setY(this.position.getY() + 2.0D + y - this.scrollY);
         session.getPosition().setWidth((this.position.getWidth() - 8.0D) / 3.0D);
         ++x;
         if (x == 3) {
            x = 0;
            y += 17.0D;
         }
      }

      RenderUtils.drawImage(2.0D, 2.0D, 18.0D, 18.0D, new ResourceLocation("client/images/gothaj.png"), -1);
      Fonts.getFont("rb-b").drawString("Alt Manager", 65.0D - Fonts.getFont("rb-b").getWidth("Alt Manager") / 2.0D, 25.0D - Fonts.getFont("rb-b").getWidth("GOTHAJ") / 2.0D, -3355444);
      RenderUtils.drawRoundedRect(2.0D, 25.0D, 128.0D, 63.0D, 805306368, 2.0F);
      String[] s = "Active Session".split("");
      double w = 0.0D;
      String[] var13 = s;
      int var12 = s.length;

      for(int var11 = 0; var11 < var12; ++var11) {
         String t = var13[var11];
         Fonts.getFont("rb-b").drawStringWithShadow(t, 65.0D - Fonts.getFont("rb-b").getWidth("Active Session") / 2.0D + w, 31.0D, ColorUtils.mix(-10007340, -12751688, Math.sin(Math.toRadians((double)(System.nanoTime() / 1000000L) + w * 10.0D) / 3.0D) + 1.0D, 2.0D), 1157627904);
         w += Fonts.getFont("rb-b").getWidth(t);
      }

      Fonts.getFont("rb-r").drawString("Name: §7" + Minecraft.getMinecraft().session.getUsername(), 5.0D, 43.0D, -1);
      Fonts.getFont("rb-r").drawString("Online: " + (this.mc.session.getToken().equals("0") ? "§cNo" : "§aYes"), 5.0D, 53.0D, -1);
      Iterator var23 = this.buttons.iterator();

      while(var23.hasNext()) {
         Button b = (Button)var23.next();
         b.draw(mouseX, mouseY);
      }

      this.panel.draw(mouseX, mouseY);
      double save = this.position.getHeight();
      if (save < this.getBigger()) {
         float g = (float)Mouse.getEventDWheel();
         double maxScrollY = this.getBigger() - save;
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

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      this.panel.key(typedChar, keyCode);
      super.keyTyped(typedChar, keyCode);
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      if (this.panel.open) {
         this.panel.click(mouseX, mouseY, mouseButton);
      } else {
         Iterator var5 = this.sessions.iterator();

         while(var5.hasNext()) {
            AltManagerSession session = (AltManagerSession)var5.next();
            if (session.getPosition().isInside(mouseX, mouseY) && mouseButton == 0) {
               session.onClick(mouseX, mouseY, mouseButton);
            }
         }

         var5 = this.buttons.iterator();

         while(var5.hasNext()) {
            Button b = (Button)var5.next();
            if (b.getPosition().isInside(mouseX, mouseY) && mouseButton == 0) {
               switch(b.getId()) {
               case 0:
                  this.mc.displayGuiScreen(new Menu());
                  break;
               case 1:
                  this.panel.open = true;
                  break;
               case 2:
                  this.sessions.remove(this.active);
                  break;
               case 3:
                  Minecraft.getMinecraft().session = new Session(StringUtils.generateNamesForMinecraft(firstNames, lastNames), "0", "0", "mojang");
                  break;
               case 4:
                  String text = this.getClipBoard();
                  if (text.equals("")) {
                     return;
                  }

                  if (text.contains(":")) {
                     String[] args = text.split(":");
                     args[1] = args[1].replaceAll("-", "");
                     if (args.length == 3) {
                        Minecraft.getMinecraft().session = new Session(args[0], args[1], args[2], "mojang");
                        return;
                     }
                  }
                  break;
               case 5:
                  Minecraft.getMinecraft().session = new Session(RandomStringUtils.randomAlphanumeric(16), "0", "0", "mojang");
                  break;
               case 6:
                  MicrosoftLogin.getRefreshToken((refreshToken) -> {
                     if (refreshToken != null) {
                        (new Thread(() -> {
                           MicrosoftLogin.LoginData var1 = BroswerUtil.loginWithRefreshToken(refreshToken);
                        })).start();
                     }

                  }, true);
               }
            }
         }

         super.mouseClicked(mouseX, mouseY, mouseButton);
      }
   }

   public String getClipBoard() {
      try {
         return (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
      } catch (HeadlessException var2) {
         var2.printStackTrace();
      } catch (UnsupportedFlavorException var3) {
         var3.printStackTrace();
      } catch (IOException var4) {
         var4.printStackTrace();
      }

      return "";
   }

   public void onGuiClosed() {
      ((AccountsFile)Client.INSTANCE.getFileManager().getFile(AccountsFile.class)).save(this);
   }

   private void openFirefox(String url) {
      try {
         Runtime.getRuntime().exec("cmd /c start firefox \"" + url + "\"");
      } catch (IOException var3) {
         System.out.println("Could not open firefox");
      }

   }
}