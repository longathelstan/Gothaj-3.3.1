package xyz.cucumber.base.module.feat.visuals;

import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import xyz.cucumber.base.Client;
import xyz.cucumber.base.commands.cmds.FriendsCommand;
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
import xyz.cucumber.base.module.feat.other.FriendsModule;
import xyz.cucumber.base.module.feat.other.TeamsModule;
import xyz.cucumber.base.module.settings.BooleanSettings;
import xyz.cucumber.base.module.settings.ColorSettings;
import xyz.cucumber.base.module.settings.ModeSettings;
import xyz.cucumber.base.module.settings.ModuleSettings;
import xyz.cucumber.base.module.settings.NumberSettings;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.game.EntityUtils;
import xyz.cucumber.base.utils.math.PositionHandler;
import xyz.cucumber.base.utils.math.RotationUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.ColorUtils;
import xyz.cucumber.base.utils.render.StencilUtils;

@ModuleInfo(
   category = Category.VISUALS,
   description = "Display players on map",
   name = "Radar",
   priority = ArrayPriority.LOW
)
public class RadarModule extends Mod implements Dragable {
   private NumberSettings positionX = new NumberSettings("Position X", 30.0D, 0.0D, 1000.0D, 1.0D);
   private NumberSettings positionY = new NumberSettings("Position Y", 50.0D, 0.0D, 1000.0D, 1.0D);
   private ModeSettings mode = new ModeSettings("Mode", new String[]{"Rect", "Rounded", "Circle"});
   private PositionUtils position = new PositionUtils(0.0D, 0.0D, 100.0D, 100.0D, 1.0F);
   private NumberSettings size = new NumberSettings("Size", 100.0D, 40.0D, 200.0D, 1.0D);
   private BooleanSettings blur = new BooleanSettings("Blur", true);
   private BooleanSettings bloom = new BooleanSettings("Bloom", true);
   public ColorSettings bloomColor = new ColorSettings("Bloom color", "Static", -16777216, -1, 40);
   private ColorSettings bg = new ColorSettings("Background Color", "Static", -16777216, -1, 100);
   private ColorSettings normalC = new ColorSettings("Player Color", "Static", -1, -1, 100);
   private ColorSettings teamC = new ColorSettings("Team Color", "Static", -1, -1, 100);
   private ColorSettings friendC = new ColorSettings("Friend Color", "Static", -1, -1, 100);
   private ColorSettings murderC = new ColorSettings("Murder Color", "Static", -1, -1, 100);

   public RadarModule() {
      this.addSettings(new ModuleSettings[]{this.positionX, this.positionY, this.mode, this.size, this.bg, this.normalC, this.teamC, this.friendC, this.murderC, this.blur, this.bloom, this.bloomColor});
   }

   public PositionUtils getPosition() {
      return this.position;
   }

   public void setXYPosition(double x, double y) {
      this.positionX.setValue(x);
      this.positionY.setValue(y);
   }

   @EventListener
   public void onBlur(EventBlur e) {
      if (this.blur.isEnabled()) {
         e.setCancelled(true);
         if (e.getType() == EventType.POST) {
            String var2;
            switch((var2 = this.mode.getMode().toLowerCase()).hashCode()) {
            case -1360216880:
               if (var2.equals("circle")) {
                  RenderUtils.drawRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), -1, (float)(this.size.getValue() / 2.0D));
                  return;
               }
               break;
            case 1385468589:
               if (var2.equals("rounded")) {
                  RenderUtils.drawRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), -1, 3.0F);
                  return;
               }
            }

            RenderUtils.drawRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), -1);
         }

      }
   }

   @EventListener
   public void onBloom(EventBloom e) {
      if (this.bloom.isEnabled()) {
         e.setCancelled(true);
         if (e.getType() == EventType.POST) {
            String var2;
            switch((var2 = this.mode.getMode().toLowerCase()).hashCode()) {
            case -1360216880:
               if (var2.equals("circle")) {
                  RenderUtils.drawRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), ColorUtils.getColor(this.bloomColor, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D), (float)(this.size.getValue() / 2.0D));
                  return;
               }
               break;
            case 1385468589:
               if (var2.equals("rounded")) {
                  RenderUtils.drawRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), ColorUtils.getColor(this.bloomColor, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D), 5.0F);
                  return;
               }
            }

            RenderUtils.drawRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), ColorUtils.getColor(this.bloomColor, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D));
         }

      }
   }

   @EventListener
   public void onRenderGui(EventRenderGui e) {
      label87: {
         double[] pos = PositionHandler.getScaledPosition(this.positionX.getValue(), this.positionY.getValue());
         this.position.setX(pos[0]);
         this.position.setY(pos[1]);
         this.position.setWidth(this.size.getValue());
         this.position.setHeight(this.size.getValue());
         String var3;
         switch((var3 = this.mode.getMode().toLowerCase()).hashCode()) {
         case -1360216880:
            if (var3.equals("circle")) {
               RenderUtils.drawRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), ColorUtils.getColor(this.bg, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D), (float)(this.size.getValue() / 2.0D));
               break label87;
            }
            break;
         case 1385468589:
            if (var3.equals("rounded")) {
               RenderUtils.drawRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), ColorUtils.getColor(this.bg, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D), 5.0F);
               break label87;
            }
         }

         RenderUtils.drawRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), ColorUtils.getColor(this.bg, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D));
      }

      label79: {
         StencilUtils.initStencil();
         GL11.glEnable(2960);
         StencilUtils.bindWriteStencilBuffer();
         String var4;
         switch((var4 = this.mode.getMode().toLowerCase()).hashCode()) {
         case -1360216880:
            if (var4.equals("circle")) {
               RenderUtils.drawRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), ColorUtils.getColor(this.bg, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D), (float)(this.size.getValue() / 2.0D));
               break label79;
            }
            break;
         case 1385468589:
            if (var4.equals("rounded")) {
               RenderUtils.drawRoundedRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), ColorUtils.getColor(this.bg, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D), 5.0F);
               break label79;
            }
         }

         RenderUtils.drawRect(this.position.getX(), this.position.getY(), this.position.getX2(), this.position.getY2(), ColorUtils.getColor(this.bg, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D));
      }

      StencilUtils.bindReadStencilBuffer(1);
      Iterator var6 = this.mc.theWorld.playerEntities.iterator();

      while(true) {
         EntityPlayer entity;
         do {
            if (!var6.hasNext()) {
               StencilUtils.uninitStencilBuffer();
               RenderUtils.drawLine(this.position.getX() + this.position.getWidth() / 2.0D, this.position.getY(), this.position.getX() + this.position.getWidth() / 2.0D, this.position.getY2(), -8947849, 1.0F);
               RenderUtils.drawLine(this.position.getX(), this.position.getY() + this.position.getHeight() / 2.0D, this.position.getX2(), this.position.getY() + this.position.getHeight() / 2.0D, -8947849, 1.0F);
               return;
            }

            entity = (EntityPlayer)var6.next();
         } while(entity == this.mc.thePlayer);

         double playerX = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)this.mc.timer.renderPartialTicks;
         double playerZ = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)this.mc.timer.renderPartialTicks;
         double diffX = playerX - this.mc.thePlayer.posX;
         double diffZ = playerZ - this.mc.thePlayer.posZ;
         double dist = Math.sqrt(Math.pow(diffX, 2.0D) + Math.pow(diffZ, 2.0D)) * 1.2D;
         double x = this.position.getX() + this.position.getWidth() / 2.0D;
         double y = this.position.getY() + this.position.getHeight() / 2.0D;
         x -= Math.sin(Math.toRadians(RotationUtils.fovFromEntity(entity))) * dist;
         y -= Math.cos(Math.toRadians(RotationUtils.fovFromEntity(entity))) * dist;
         int color = ColorUtils.getColor(this.normalC, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D);
         if (Client.INSTANCE.getModuleManager().getModule(TeamsModule.class).isEnabled() && (this.mc.thePlayer.isOnSameTeam(entity) || EntityUtils.isInSameTeam(entity))) {
            color = ColorUtils.getColor(this.teamC, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D);
         }

         if (Client.INSTANCE.getModuleManager().getModule(FriendsModule.class).isEnabled()) {
            Iterator var23 = FriendsCommand.friends.iterator();

            while(var23.hasNext()) {
               String friend = (String)var23.next();
               if (friend.equalsIgnoreCase(entity.getName())) {
                  color = ColorUtils.getColor(this.friendC, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D);
                  break;
               }
            }
         }

         if (Client.INSTANCE.getModuleManager().getModule(MurderFinderModule.class).isEnabled()) {
            MurderFinderModule mod = (MurderFinderModule)Client.INSTANCE.getModuleManager().getModule(MurderFinderModule.class);
            Iterator var24 = mod.murders.entrySet().iterator();

            while(var24.hasNext()) {
               Entry<String, Entity> entry = (Entry)var24.next();
               if (((String)entry.getKey()).equalsIgnoreCase(entity.getName())) {
                  color = ColorUtils.getColor(this.murderC, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D);
                  break;
               }
            }
         }

         RenderUtils.drawCircle(x, y, 1.4D, color, 1.0D);
      }
   }

   public void renderView(double x, double y, double yaw) {
      GL11.glPushMatrix();
      RenderUtils.start2D();
      RenderUtils.color(-1);
      GL11.glShadeModel(7425);
      GL11.glBegin(9);
      GL11.glVertex2d(x, y);
      RenderUtils.color(16777215);
      GL11.glVertex2d(x - Math.sin((yaw - 35.0D) * 3.141592653589793D / 180.0D) * 8.0D, y - Math.cos((yaw - 35.0D) * 3.141592653589793D / 180.0D) * 8.0D);
      GL11.glVertex2d(x - Math.sin((yaw + 35.0D) * 3.141592653589793D / 180.0D) * 8.0D, y - Math.cos((yaw + 35.0D) * 3.141592653589793D / 180.0D) * 8.0D);
      GL11.glEnd();
      RenderUtils.stop2D();
      GL11.glRotatef(0.0F, 0.0F, 0.0F, 0.0F);
      GL11.glPopMatrix();
   }
}
