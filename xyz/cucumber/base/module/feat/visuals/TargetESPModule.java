package xyz.cucumber.base.module.feat.visuals;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import xyz.cucumber.base.Client;
import xyz.cucumber.base.events.EventListener;
import xyz.cucumber.base.events.ext.EventRender3D;
import xyz.cucumber.base.module.Category;
import xyz.cucumber.base.module.Mod;
import xyz.cucumber.base.module.ModuleInfo;
import xyz.cucumber.base.module.feat.combat.KillAuraModule;
import xyz.cucumber.base.module.settings.ColorSettings;
import xyz.cucumber.base.module.settings.ModeSettings;
import xyz.cucumber.base.module.settings.ModuleSettings;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.render.ColorUtils;

@ModuleInfo(
   category = Category.VISUALS,
   description = "",
   name = "Target ESP"
)
public class TargetESPModule extends Mod {
   public ModeSettings mode = new ModeSettings("Mode", new String[]{"Circle", "Dotted", "Sims", "Cock", "Rect", "Hitbox"});
   public ColorSettings color = new ColorSettings("Color", "Static", -1, -1, 100);
   public ColorSettings hitColor = new ColorSettings("Hurttime Color", "Static", -65536, -1, 100);
   KillAuraModule ka;
   private double animation;
   private boolean direction;

   public TargetESPModule() {
      this.addSettings(new ModuleSettings[]{this.mode, this.color, this.hitColor});
   }

   public void onEnable() {
      this.ka = (KillAuraModule)Client.INSTANCE.getModuleManager().getModule(KillAuraModule.class);
   }

   @EventListener
   public void onRender3D(EventRender3D e) {
      this.ka = (KillAuraModule)Client.INSTANCE.getModuleManager().getModule(KillAuraModule.class);
      if (this.ka.isEnabled()) {
         if (this.ka.target != null) {
            EntityLivingBase entity = this.ka.target;
            GL11.glPushMatrix();
            RenderUtils.start3D();
            GL11.glShadeModel(7425);
            double x = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)e.getPartialTicks() - this.mc.getRenderManager().viewerPosX;
            double y = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)e.getPartialTicks() - this.mc.getRenderManager().viewerPosY;
            double z = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)e.getPartialTicks() - this.mc.getRenderManager().viewerPosZ;
            double size = (double)(entity.width / 2.0F);
            AxisAlignedBB bb = new AxisAlignedBB(x - size, y + (double)entity.height, z - size, x + size, y, z + size);
            String var12;
            double rY;
            double i;
            double rX;
            double rZ;
            switch((var12 = this.mode.getMode().toLowerCase()).hashCode()) {
            case -1360216880:
               if (var12.equals("circle")) {
                  if (this.direction) {
                     this.animation -= 0.02D;
                     if ((double)(-entity.height / 2.0F) > this.animation) {
                        this.direction = !this.direction;
                     }
                  } else {
                     this.animation += 0.02D;
                     if ((double)(entity.height / 2.0F) < this.animation) {
                        this.direction = !this.direction;
                     }
                  }

                  GL11.glBegin(8);

                  for(rY = 0.0D; rY <= 360.0D; rY += 10.0D) {
                     i = x + Math.sin(rY * 3.141592653589793D / 180.0D) * (double)entity.width;
                     rX = y + this.animation;
                     rZ = z + Math.cos(rY * 3.141592653589793D / 180.0D) * (double)entity.width;
                     RenderUtils.color(ColorUtils.mix(ColorUtils.getColor(this.color, (double)(System.nanoTime() / 1000000L), rY, 5.0D), ColorUtils.getColor(this.hitColor, (double)(System.nanoTime() / 1000000L), rY, 5.0D), (double)entity.hurtTime, 10.0D));
                     GL11.glVertex3d(i, rX + (double)(entity.height / 2.0F), rZ);
                     RenderUtils.color(ColorUtils.mix(ColorUtils.getColor(this.color, (double)(System.nanoTime() / 1000000L), rY, 5.0D), ColorUtils.getColor(this.hitColor, (double)(System.nanoTime() / 1000000L), rY, 5.0D), (double)entity.hurtTime, 10.0D), 0.0F);
                     GL11.glVertex3d(i, rX + (double)(entity.height / 2.0F) - this.animation / (double)entity.height, rZ);
                  }

                  GL11.glEnd();
               }
               break;
            case -1325970902:
               if (var12.equals("dotted")) {
                  if (this.direction) {
                     this.animation -= 0.02D;
                     if ((double)(-entity.height / 2.0F) > this.animation) {
                        this.direction = !this.direction;
                     }
                  } else {
                     this.animation += 0.02D;
                     if ((double)(entity.height / 2.0F) < this.animation) {
                        this.direction = !this.direction;
                     }
                  }

                  GL11.glPointSize(10.0F);
                  GL11.glTranslated(x, y, z);
                  GL11.glRotatef(((float)this.mc.thePlayer.ticksExisted + e.getPartialTicks()) * 8.0F, 0.0F, 1.0F, 0.0F);
                  GL11.glTranslated(-x, -y, -z);
                  GL11.glBegin(0);

                  for(rY = 0.0D; rY <= 360.0D; rY += 40.0D) {
                     i = x + Math.sin(rY * 3.141592653589793D / 180.0D) * (double)entity.width;
                     rX = y + this.animation;
                     rZ = z + Math.cos(rY * 3.141592653589793D / 180.0D) * (double)entity.width;
                     RenderUtils.color(ColorUtils.mix(ColorUtils.getColor(this.color, (double)(System.nanoTime() / 1000000L), rY, 5.0D), ColorUtils.getColor(this.hitColor, (double)(System.nanoTime() / 1000000L), rY, 5.0D), (double)entity.hurtTime, 10.0D));
                     GL11.glVertex3d(i, rX + (double)(entity.height / 2.0F), rZ);
                  }

                  GL11.glEnd();
                  GL11.glPointSize(18.0F);
                  GL11.glBegin(0);

                  for(rY = 0.0D; rY <= 360.0D; rY += 40.0D) {
                     i = x + Math.sin(rY * 3.141592653589793D / 180.0D) * (double)entity.width;
                     rX = y + this.animation;
                     rZ = z + Math.cos(rY * 3.141592653589793D / 180.0D) * (double)entity.width;
                     RenderUtils.color(ColorUtils.getAlphaColor(ColorUtils.mix(ColorUtils.getColor(this.color, (double)(System.nanoTime() / 1000000L), rY, 5.0D), ColorUtils.getColor(this.hitColor, (double)(System.nanoTime() / 1000000L), rY, 5.0D), (double)entity.hurtTime, 10.0D), 20));
                     GL11.glVertex3d(i, rX + (double)(entity.height / 2.0F), rZ);
                  }

                  GL11.glEnd();
               }
               break;
            case -1217012392:
               if (var12.equals("hitbox")) {
                  RenderUtils.color(ColorUtils.getAlphaColor(ColorUtils.mix(ColorUtils.getColor(this.color, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D), ColorUtils.getColor(this.hitColor, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D), (double)entity.hurtTime, 10.0D), this.color.getAlpha()));
                  RenderUtils.renderHitbox(bb, 7);
               }
               break;
            case 3059156:
               if (var12.equals("cock")) {
                  GL11.glPushMatrix();
                  GL11.glPopMatrix();
               }
               break;
            case 3496420:
               if (var12.equals("rect")) {
                  RenderUtils.color(ColorUtils.getAlphaColor(ColorUtils.mix(ColorUtils.getColor(this.color, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D), ColorUtils.getColor(this.hitColor, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D), (double)entity.hurtTime, 10.0D), this.color.getAlpha()));
                  RenderUtils.renderHitbox(new AxisAlignedBB(x - size, y + (double)entity.height + 0.3D, z - size, x + size, y + (double)entity.height + 0.1D, z + size), 7);
               }
               break;
            case 3530364:
               if (var12.equals("sims")) {
                  GL11.glEnable(2929);
                  GL11.glRotated(z, x, y, size);
                  GL11.glBegin(6);
                  rY = y + (double)entity.height + 0.5D;
                  RenderUtils.color(-1, (float)this.color.getAlpha());
                  GL11.glVertex3d(x, rY + 0.8D, z);

                  for(i = 0.0D; i <= 360.0D; i += 60.0D) {
                     rX = x + Math.sin(i * 3.141592653589793D / 180.0D) * (double)entity.width / 3.0D;
                     rZ = z + Math.cos(i * 3.141592653589793D / 180.0D) * (double)entity.width / 3.0D;
                     RenderUtils.color(ColorUtils.mix(ColorUtils.getColor(this.color, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D), ColorUtils.getColor(this.hitColor, (double)(System.nanoTime() / 1000000L), 0.0D, 5.0D), (double)entity.hurtTime, 10.0D));
                     GL11.glVertex3d(rX, rY + 0.4D, rZ);
                  }

                  GL11.glEnd();
                  GL11.glBegin(6);
                  GL11.glVertex3d(x, rY, z);

                  for(i = 0.0D; i <= 360.0D; i += 60.0D) {
                     rX = x + Math.sin(i * 3.141592653589793D / 180.0D) * (double)entity.width / 3.0D;
                     rZ = z + Math.cos(i * 3.141592653589793D / 180.0D) * (double)entity.width / 3.0D;
                     GL11.glVertex3d(rX, rY + 0.4D, rZ);
                  }

                  GL11.glEnd();
                  GL11.glDisable(2929);
                  RenderUtils.color(620756992);
                  GL11.glBegin(2);

                  for(i = 0.0D; i <= 360.0D; i += 60.0D) {
                     rX = x + Math.sin(i * 3.141592653589793D / 180.0D) * (double)entity.width / 3.0D;
                     rZ = z + Math.cos(i * 3.141592653589793D / 180.0D) * (double)entity.width / 3.0D;
                     GL11.glVertex3d(x, rY, z);
                     GL11.glVertex3d(rX, rY + 0.4D, rZ);
                  }

                  GL11.glEnd();
                  GL11.glBegin(2);

                  for(i = 0.0D; i <= 360.0D; i += 60.0D) {
                     rX = x + Math.sin(i * 3.141592653589793D / 180.0D) * (double)entity.width / 3.0D;
                     rZ = z + Math.cos(i * 3.141592653589793D / 180.0D) * (double)entity.width / 3.0D;
                     GL11.glVertex3d(x, rY + 0.8D, z);
                     GL11.glVertex3d(rX, rY + 0.4D, rZ);
                  }

                  GL11.glEnd();
                  GL11.glBegin(2);

                  for(i = 0.0D; i <= 360.0D; i += 60.0D) {
                     rX = x + Math.sin(i * 3.141592653589793D / 180.0D) * (double)entity.width / 3.0D;
                     rZ = z + Math.cos(i * 3.141592653589793D / 180.0D) * (double)entity.width / 3.0D;
                     GL11.glVertex3d(rX, rY + 0.4D, rZ);
                  }

                  GL11.glEnd();
               }
            }

            RenderUtils.stop3D();
            GL11.glPopMatrix();
         }
      }
   }
}
