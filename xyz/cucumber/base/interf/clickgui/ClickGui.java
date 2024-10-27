package xyz.cucumber.base.interf.clickgui;

import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import xyz.cucumber.base.interf.clickgui.content.Content;
import xyz.cucumber.base.interf.clickgui.navbar.Navbar;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.BlurUtils;

public class ClickGui extends GuiScreen {
   private PositionUtils guiPosition = new PositionUtils(100.0D, 100.0D, 350.0D, 300.0D, 1.0F);
   private Navbar navbar = new Navbar(new PositionUtils(102.5D, 102.5D, 100.0D, 295.0D, 1.0F));
   private static Content content;
   private float startTime;

   public ClickGui() {
      content = new Content(new PositionUtils(205.0D, 102.5D, 242.5D, 265.0D, 1.0F), this.navbar);
   }

   public void initGui() {
      this.startTime = (float)System.nanoTime();
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      BlurUtils.renderBlur(20.0F);
      RenderUtils.drawRoundedRect(this.guiPosition.getX(), this.guiPosition.getY(), this.guiPosition.getX2(), this.guiPosition.getY2(), -299555035, 1.0F);
      this.navbar.getPosition().setX(this.guiPosition.getX() + 2.5D);
      this.navbar.getPosition().setY(this.guiPosition.getY() + 2.5D);
      content.getPosition().setX(this.guiPosition.getX() + 5.0D + 100.0D);
      content.getPosition().setY(this.guiPosition.getY() + 2.5D + 30.0D);
      this.navbar.draw(mouseX, mouseY);
      content.draw(mouseX, mouseY);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      this.navbar.clicked(mouseX, mouseY, mouseButton);
      content.clicked(mouseX, mouseY, mouseButton);
      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      content.released(mouseX, mouseY, state);
      super.mouseReleased(mouseX, mouseY, state);
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      content.key(typedChar, keyCode);
      super.keyTyped(typedChar, keyCode);
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public static Content getContent() {
      return content;
   }

   public static void setContent(Content content) {
      ClickGui.content = content;
   }
}
