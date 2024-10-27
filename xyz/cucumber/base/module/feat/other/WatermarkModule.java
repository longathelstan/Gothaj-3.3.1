package xyz.cucumber.base.module.feat.other;

import i.dupx.launcher.CLAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;
import xyz.cucumber.base.Client;
import xyz.cucumber.base.events.EventListener;
import xyz.cucumber.base.events.EventType;
import xyz.cucumber.base.events.ext.EventBloom;
import xyz.cucumber.base.events.ext.EventBlur;
import xyz.cucumber.base.events.ext.EventRenderGui;
import xyz.cucumber.base.module.ArrayPriority;
import xyz.cucumber.base.module.Category;
import xyz.cucumber.base.module.Mod;
import xyz.cucumber.base.module.ModuleInfo;
import xyz.cucumber.base.module.addons.Dragable;
import xyz.cucumber.base.module.settings.BooleanSettings;
import xyz.cucumber.base.module.settings.ColorSettings;
import xyz.cucumber.base.module.settings.ModeSettings;
import xyz.cucumber.base.module.settings.ModuleSettings;
import xyz.cucumber.base.module.settings.NumberSettings;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.math.PositionHandler;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.ColorUtils;
import xyz.cucumber.base.utils.render.Fonts;
import xyz.cucumber.base.utils.render.StencilUtils;

@ModuleInfo(
   category = Category.OTHER,
   description = "Displays watermark on screen",
   name = "Watermark",
   priority = ArrayPriority.LOW
)
public class WatermarkModule extends Mod implements Dragable {
   private PositionUtils accounts = new PositionUtils(0.0D, 0.0D, 150.0D, 15.0D, 1.0F);
   private NumberSettings positionX = new NumberSettings("Position X", 30.0D, 0.0D, 1000.0D, 1.0D);
   private NumberSettings positionY = new NumberSettings("Position Y", 50.0D, 0.0D, 1000.0D, 1.0D);
   private BooleanSettings blur = new BooleanSettings("Blur", true);
   private BooleanSettings bloom = new BooleanSettings("Bloom", true);
   public ColorSettings bloomColor = new ColorSettings("Bloom color", "Static", -16777216, -1, 40);
   private ModeSettings mode = new ModeSettings("Mode", new String[]{"Modern", "Simple"});
   public ColorSettings logoColor = new ColorSettings("Mark color", "Mix", -12272388, -13470224, 100);
   public ColorSettings backgroundColor = new ColorSettings("Background color", "Static", -16777216, -1, 40);

   public WatermarkModule() {
      this.addSettings(new ModuleSettings[]{this.positionX, this.positionY, this.mode, this.blur, this.bloom, this.logoColor, this.backgroundColor, this.bloomColor});
   }

   @EventListener
   public void onBlur(EventBlur e) {
      if (this.blur.isEnabled()) {
         e.setCancelled(true);
         if (e.getType() == EventType.POST) {
            String var2;
            switch((var2 = this.mode.getMode().toLowerCase()).hashCode()) {
            case -1068799201:
               if (var2.equals("modern")) {
                  RenderUtils.drawRoundedRect(this.accounts.getX(), this.accounts.getY(), this.accounts.getX2(), this.accounts.getY2(), ColorUtils.getColor(this.backgroundColor, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D), 1.5D);
               }
               break;
            case -902286926:
               if (!var2.equals("simple")) {
               }
            }
         }

      }
   }

   @EventListener
   public void onBloom(EventBloom e) {
      if (this.bloom.isEnabled()) {
         e.setCancelled(true);
         if (e.getType() == EventType.POST) {
            ColorSettings fixedColor = new ColorSettings(this.bloomColor.getMode(), this.bloomColor.getMode(), this.bloomColor.getMainColor(), this.bloomColor.getSecondaryColor(), 255);
            String var3;
            switch((var3 = this.mode.getMode().toLowerCase()).hashCode()) {
            case -1068799201:
               if (var3.equals("modern")) {
                  RenderUtils.drawRoundedRect(this.accounts.getX(), this.accounts.getY(), this.accounts.getX2(), this.accounts.getY2(), ColorUtils.getColor(fixedColor, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D), 1.5D);
               }
               break;
            case -902286926:
               if (var3.equals("simple")) {
                  String[] s = "Gothaj".split("");
                  double w = 0.0D;
                  String[] var10 = s;
                  int var9 = s.length;

                  for(int var8 = 0; var8 < var9; ++var8) {
                     String t = var10[var8];
                     Fonts.getFont("rb-b-h").drawString(t, this.accounts.getX() + w, this.accounts.getY() + this.accounts.getHeight() / 2.0D - (double)(Fonts.getFont("rb-b-h").getHeight("s") / 2.0F) - 1.0D, ColorUtils.getColor(this.logoColor, (double)(System.nanoTime() / 1000000L), w * 2.0D, 5.0D));
                     w += Fonts.getFont("rb-b-h").getWidth(t);
                  }

                  Fonts.getFont("rb-b-13").drawString(Client.INSTANCE.version, this.accounts.getX() + Fonts.getFont("rb-b-h").getWidth("Gothaj") + 4.0D, this.accounts.getY() + this.accounts.getHeight() / 2.0D - (double)(Fonts.getFont("rb-b-h").getHeight("s") / 2.0F) - 1.0D, -1);
               }
            }
         }

      }
   }

   @EventListener
   public void onRender2D(EventRenderGui e) {
      double[] pos = PositionHandler.getScaledPosition(this.positionX.getValue(), this.positionY.getValue());
      this.accounts.setX(pos[0]);
      this.accounts.setY(pos[1]);
      String var3;
      String[] s;
      double w;
      String t;
      int var8;
      int var9;
      String[] var10;
      switch((var3 = this.mode.getMode().toLowerCase()).hashCode()) {
      case -1068799201:
         if (var3.equals("modern")) {
            this.accounts.setWidth(17.0D + Fonts.getFont("rb-b").getWidth("Gothaj") + 5.0D + Fonts.getFont("rb-r-13").getWidth(Client.INSTANCE.version) + 5.0D + Fonts.getFont("rb-m-13").getWidth(CLAPI.getCLUsername()) + 5.0D + Fonts.getFont("rb-m-13").getWidth("Fps: ") + Fonts.getFont("rb-r-13").getWidth("" + Minecraft.debugFPS) + 2.0D);
            RenderUtils.drawRoundedRect(this.accounts.getX(), this.accounts.getY(), this.accounts.getX2(), this.accounts.getY2(), ColorUtils.getColor(this.backgroundColor, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D), 1.5D);
            StencilUtils.initStencil();
            GL11.glEnable(2960);
            StencilUtils.bindWriteStencilBuffer();
            RenderUtils.drawRoundedRect(this.accounts.getX() + 2.0D, this.accounts.getY() + 2.0D, this.accounts.getX() + 13.0D, this.accounts.getY() + 13.0D, -1, 2.0D);
            StencilUtils.bindReadStencilBuffer(1);
            if (this.mc.getNetHandler() != null && this.mc.thePlayer.getUniqueID() != null && this.mc.getNetHandler().getPlayerInfo(this.mc.thePlayer.getUniqueID()) != null && this.mc.getNetHandler().getPlayerInfo(this.mc.thePlayer.getUniqueID()).getLocationSkin() != null) {
               Minecraft.getMinecraft().getTextureManager().bindTexture(this.mc.getNetHandler().getPlayerInfo(this.mc.thePlayer.getUniqueID()).getLocationSkin());
            }

            Gui.drawScaledCustomSizeModalRect(this.accounts.getX() + 2.0D, this.accounts.getY() + 2.0D, 8.0F, 8.0F, 8.0D, 8.0D, 11.0D, 11.0D, 64.0F, 64.0F);
            StencilUtils.uninitStencilBuffer();
            s = "Gothaj".split("");
            w = 0.0D;
            var10 = s;
            var9 = s.length;

            for(var8 = 0; var8 < var9; ++var8) {
               t = var10[var8];
               Fonts.getFont("rb-b").drawStringWithShadow(t, this.accounts.getX() + 2.0D + 11.0D + 4.0D + w, this.accounts.getY() + this.accounts.getHeight() / 2.0D - 1.0D, ColorUtils.getColor(this.logoColor, (double)(System.nanoTime() / 1000000L), w * 2.0D, 5.0D), -1879048192);
               w += Fonts.getFont("rb-b").getWidth(t);
            }

            Fonts.getFont("rb-r-13").drawStringWithShadow(Client.INSTANCE.version, this.accounts.getX() + 2.0D + 11.0D + 4.0D + Fonts.getFont("rb-b").getWidth("Gothaj") + 5.0D, this.accounts.getY() + this.accounts.getHeight() / 2.0D - 0.75D, -1, -1879048192);
            Fonts.getFont("rb-m-13").drawStringWithShadow(CLAPI.getCLUsername(), this.accounts.getX() + 2.0D + 11.0D + 4.0D + Fonts.getFont("rb-b").getWidth("Gothaj") + 5.0D + Fonts.getFont("rb-r-13").getWidth(Client.INSTANCE.version) + 5.0D, this.accounts.getY() + this.accounts.getHeight() / 2.0D - 0.75D, -1, -1879048192);
            Fonts.getFont("rb-m-13").drawStringWithShadow("FPS:", this.accounts.getX() + 2.0D + 11.0D + 4.0D + Fonts.getFont("rb-b").getWidth("Gothaj") + 5.0D + Fonts.getFont("rb-r-13").getWidth(Client.INSTANCE.version) + 5.0D + Fonts.getFont("rb-m-13").getWidth(CLAPI.getCLUsername()) + 5.0D, this.accounts.getY() + this.accounts.getHeight() / 2.0D - 0.75D, -1, -1879048192);
            Fonts.getFont("rb-r-13").drawStringWithShadow("" + Minecraft.debugFPS, this.accounts.getX() + 2.0D + 11.0D + 4.0D + Fonts.getFont("rb-b").getWidth("Gothaj") + 5.0D + Fonts.getFont("rb-r-13").getWidth(Client.INSTANCE.version) + 5.0D + Fonts.getFont("rb-m-13").getWidth(CLAPI.getCLUsername()) + 5.0D + Fonts.getFont("rb-m-13").getWidth("Fps: "), this.accounts.getY() + this.accounts.getHeight() / 2.0D - 0.75D, -1, -1879048192);
         }
         break;
      case -902286926:
         if (var3.equals("simple")) {
            this.accounts.setWidth(Fonts.getFont("rb-b-h").getWidth("Gothaj") + 4.0D + Fonts.getFont("rb-b-13").getWidth(Client.INSTANCE.version));
            s = "Gothaj".split("");
            w = 0.0D;
            var10 = s;
            var9 = s.length;

            for(var8 = 0; var8 < var9; ++var8) {
               t = var10[var8];
               Fonts.getFont("rb-b-h").drawString(t, this.accounts.getX() + w, this.accounts.getY() + this.accounts.getHeight() / 2.0D - (double)(Fonts.getFont("rb-b-h").getHeight("s") / 2.0F) - 1.0D, ColorUtils.getColor(this.logoColor, (double)(System.nanoTime() / 1000000L), w * 2.0D, 5.0D));
               w += Fonts.getFont("rb-b-h").getWidth(t);
            }

            Fonts.getFont("rb-b-13").drawString(Client.INSTANCE.version, this.accounts.getX() + Fonts.getFont("rb-b-h").getWidth("Gothaj") + 4.0D, this.accounts.getY() + this.accounts.getHeight() / 2.0D - (double)(Fonts.getFont("rb-b-h").getHeight("s") / 2.0F) - 1.0D, -1);
         }
      }

   }

   public PositionUtils getPosition() {
      return this.accounts;
   }

   public void setXYPosition(double x, double y) {
      this.positionX.setValue(x);
      this.positionY.setValue(y);
   }
}
