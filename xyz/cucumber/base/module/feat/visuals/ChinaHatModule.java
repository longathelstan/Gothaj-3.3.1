package xyz.cucumber.base.module.feat.visuals;

import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import xyz.cucumber.base.events.EventListener;
import xyz.cucumber.base.events.ext.EventRender3D;
import xyz.cucumber.base.module.ArrayPriority;
import xyz.cucumber.base.module.Category;
import xyz.cucumber.base.module.Mod;
import xyz.cucumber.base.module.ModuleInfo;
import xyz.cucumber.base.module.settings.ColorSettings;
import xyz.cucumber.base.module.settings.ModeSettings;
import xyz.cucumber.base.module.settings.ModuleSettings;
import xyz.cucumber.base.module.settings.NumberSettings;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.math.RotationUtils;
import xyz.cucumber.base.utils.render.ColorUtils;

@ModuleInfo(
   category = Category.VISUALS,
   description = "Displays china hat on your head",
   name = "China Hat",
   priority = ArrayPriority.LOW
)
public class ChinaHatModule extends Mod {
   public ModeSettings mode = new ModeSettings("Mode", new String[]{"Player", "All"});
   public NumberSettings radius = new NumberSettings("Radius", 0.7D, 0.0D, 3.0D, 0.05D);
   public ColorSettings color = new ColorSettings("Color", "Static", -1, -1, 70);

   public ChinaHatModule() {
      this.addSettings(new ModuleSettings[]{this.mode, this.radius, this.color});
   }

   @EventListener
   public void onRender3D(EventRender3D e) {
      if (this.mode.getMode().equalsIgnoreCase("All")) {
         Iterator var3 = this.mc.theWorld.loadedEntityList.iterator();

         while(var3.hasNext()) {
            Entity entity = (Entity)var3.next();
            if (entity != this.mc.thePlayer && entity instanceof EntityPlayer) {
               this.render(entity, e.getPartialTicks(), new float[]{entity.rotationYaw, entity.rotationPitch});
            }
         }
      }

      if (this.mc.gameSettings.thirdPersonView != 0) {
         this.render(this.mc.thePlayer, e.getPartialTicks(), new float[]{RotationUtils.customRots ? RotationUtils.serverYaw : this.mc.thePlayer.rotationYaw, RotationUtils.customRots ? RotationUtils.serverPitch : this.mc.thePlayer.rotationPitch});
      }
   }

   public void render(Entity e, float partialTicks, float[] rots) {
      GL11.glPushMatrix();
      RenderUtils.start3D();
      double x = e.prevPosX + (e.posX - e.prevPosX) * (double)partialTicks - this.mc.getRenderManager().viewerPosX;
      double y = e.prevPosY + (e.posY - e.prevPosY) * (double)partialTicks - this.mc.getRenderManager().viewerPosY;
      double z = e.prevPosZ + (e.posZ - e.prevPosZ) * (double)partialTicks - this.mc.getRenderManager().viewerPosZ;
      float yaw = rots[0];
      float pitch = rots[1];
      GL11.glTranslated(x - Math.sin((double)yaw * 3.141592653589793D / 180.0D) * (double)pitch / 270.0D, y + (double)e.height - (e.isSneaking() ? 0.25D : 0.0D), z + Math.cos((double)yaw * 3.141592653589793D / 180.0D) * (double)pitch / 270.0D);
      GL11.glRotated((double)pitch, Math.cos((double)yaw * 3.141592653589793D / 180.0D), 0.0D, Math.sin((double)yaw * 3.141592653589793D / 180.0D));
      GL11.glRotated((double)(-this.mc.thePlayer.rotationYaw - 270.0F), 0.0D, 1.0D, 0.0D);
      GL11.glBegin(6);
      GL11.glVertex3d(0.0D, 0.3D, 0.0D);

      for(double i = 0.0D; i <= 360.0D; i += 5.0D) {
         RenderUtils.color(ColorUtils.getColor(this.color, (double)(System.nanoTime() / 1000000L), i, 5.0D));
         GL11.glVertex3d(Math.sin(i * 3.141592653589793D / 180.0D) * this.radius.getValue(), 0.0D, -Math.cos(i * 3.141592653589793D / 180.0D) * this.radius.getValue());
      }

      GL11.glEnd();
      GL11.glDisable(2929);
      RenderUtils.stop3D();
      GL11.glPopMatrix();
   }
}
