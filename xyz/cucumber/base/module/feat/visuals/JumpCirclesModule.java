package xyz.cucumber.base.module.feat.visuals;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import xyz.cucumber.base.events.EventListener;
import xyz.cucumber.base.events.ext.EventJump;
import xyz.cucumber.base.events.ext.EventRender3D;
import xyz.cucumber.base.module.Category;
import xyz.cucumber.base.module.Mod;
import xyz.cucumber.base.module.ModuleInfo;
import xyz.cucumber.base.module.settings.ColorSettings;
import xyz.cucumber.base.module.settings.ModuleSettings;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.render.ColorUtils;

@ModuleInfo(
   category = Category.VISUALS,
   description = "Displays circle where you jump",
   name = "Jump Circles"
)
public class JumpCirclesModule extends Mod {
   private HashMap<Integer, Vec3> circles = new HashMap();
   private ColorSettings color = new ColorSettings("Color", "Static", -1, -1, 90);

   public JumpCirclesModule() {
      this.addSettings(new ModuleSettings[]{this.color});
   }

   @EventListener
   public void onRender3D(EventRender3D e) {
      Iterator itr = this.circles.entrySet().iterator();

      while(true) {
         while(itr.hasNext()) {
            Entry<Integer, Vec3> circle = (Entry)itr.next();
            Vec3 position = (Vec3)circle.getValue();
            int time = (Integer)circle.getKey();
            if ((long)time < System.nanoTime() / 1000000L) {
               itr.remove();
            } else {
               double timeDiff = (double)((long)time - System.nanoTime() / 1000000L);
               double r = 0.800000011920929D;
               float a = (float)this.color.getAlpha() / 100.0F;
               double x;
               if (timeDiff >= 1000.0D) {
                  x = Math.abs(timeDiff - 2000.0D);
                  r = Math.pow(8.00000037997961E-4D * x, 0.9D);
               } else {
                  x = Math.abs(timeDiff - 1000.0D);
                  a = (float)this.color.getAlpha() / 100.0F - (float)((double)((float)this.color.getAlpha() / 100.0F / 1000.0F) * x);
               }

               x = position.xCoord - this.mc.getRenderManager().viewerPosX;
               double y = position.yCoord - this.mc.getRenderManager().viewerPosY;
               double z = position.zCoord - this.mc.getRenderManager().viewerPosZ;
               GL11.glPushMatrix();
               RenderUtils.start3D();
               GL11.glShadeModel(7425);
               GL11.glEnable(2929);
               GL11.glTranslated(x, y + 0.01D, z);
               GL11.glRotatef(((float)this.mc.thePlayer.ticksExisted + e.getPartialTicks()) * 8.0F, 0.0F, 1.0F, 0.0F);
               GL11.glBegin(6);
               RenderUtils.color(16777215);
               GL11.glVertex3d(0.0D, 0.0D, 0.0D);

               for(double i = 0.0D; i <= 360.0D; i += 5.0D) {
                  double posX = Math.sin(Math.toRadians(i)) * r;
                  double posZ = Math.cos(Math.toRadians(i)) * r;
                  int c = ColorUtils.getColor(this.color, 0.0D, i, 10.0D);
                  RenderUtils.color(c, a);
                  GL11.glVertex3d(posX, 0.01D, posZ);
               }

               GL11.glEnd();
               GL11.glShadeModel(7424);
               RenderUtils.stop3D();
               GL11.glPopMatrix();
            }
         }

         return;
      }
   }

   @EventListener
   public void onJump(EventJump e) {
      if (this.mc.thePlayer.onGround) {
         this.circles.put((int)(System.nanoTime() / 1000000L + 2000L), new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ));
      }

   }
}
