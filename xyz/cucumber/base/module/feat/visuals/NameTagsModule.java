package xyz.cucumber.base.module.feat.visuals;

import i.dupx.launcher.CLAPI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import xyz.cucumber.base.events.EventListener;
import xyz.cucumber.base.events.EventType;
import xyz.cucumber.base.events.ext.EventBloom;
import xyz.cucumber.base.events.ext.EventBlur;
import xyz.cucumber.base.events.ext.EventNametagRenderer;
import xyz.cucumber.base.events.ext.EventRender3D;
import xyz.cucumber.base.events.ext.EventRenderGui;
import xyz.cucumber.base.module.Category;
import xyz.cucumber.base.module.Mod;
import xyz.cucumber.base.module.ModuleInfo;
import xyz.cucumber.base.module.settings.BooleanSettings;
import xyz.cucumber.base.module.settings.ModuleSettings;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.math.Convertors;
import xyz.cucumber.base.utils.packet.CLUtils;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.ColorUtils;
import xyz.cucumber.base.utils.render.Fonts;

@ModuleInfo(
   category = Category.VISUALS,
   description = "Displays names above players",
   name = "Name Tags"
)
public class NameTagsModule extends Mod {
   public BooleanSettings health = new BooleanSettings("Health", true);
   public BooleanSettings bloom = new BooleanSettings("Shadow", true);
   public HashMap<EntityPlayer, PositionUtils> entities = new HashMap();

   public NameTagsModule() {
      this.addSettings(new ModuleSettings[]{this.health, this.bloom});
   }

   @EventListener
   public void onRender3D(EventRender3D e) {
      this.entities.clear();
      Iterator var3 = this.mc.theWorld.loadedEntityList.iterator();

      while(true) {
         Entity entity;
         do {
            do {
               do {
                  do {
                     if (!var3.hasNext()) {
                        return;
                     }

                     entity = (Entity)var3.next();
                  } while(this.mc.thePlayer.getDistanceToEntity(entity) < 1.0F && this.mc.gameSettings.thirdPersonView == 0);
               } while(entity == this.mc.thePlayer && this.mc.gameSettings.thirdPersonView == 0);
            } while(!(entity instanceof EntityPlayer));
         } while(!RenderUtils.isInViewFrustrum(entity));

         double x = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)e.getPartialTicks() - this.mc.getRenderManager().viewerPosX;
         double y = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)e.getPartialTicks() - this.mc.getRenderManager().viewerPosY;
         double z = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)e.getPartialTicks() - this.mc.getRenderManager().viewerPosZ;
         double width = (double)entity.width / 2.5D;
         AxisAlignedBB bb = (new AxisAlignedBB(x - width, y, z - width, x + width, y + (double)entity.height, z + width)).expand(0.2D, 0.1D, 0.2D);
         List<double[]> vectors = Arrays.asList(new double[]{bb.minX, bb.minY, bb.minZ}, new double[]{bb.minX, bb.maxY, bb.minZ}, new double[]{bb.minX, bb.maxY, bb.maxZ}, new double[]{bb.minX, bb.minY, bb.maxZ}, new double[]{bb.maxX, bb.minY, bb.minZ}, new double[]{bb.maxX, bb.maxY, bb.minZ}, new double[]{bb.maxX, bb.maxY, bb.maxZ}, new double[]{bb.maxX, bb.minY, bb.maxZ});
         double[] position = new double[]{3.4028234663852886E38D, 3.4028234663852886E38D, -1.0D, -1.0D};
         Iterator var16 = vectors.iterator();

         while(var16.hasNext()) {
            double[] vec = (double[])var16.next();
            float[] points = Convertors.convert2D((float)vec[0], (float)vec[1], (float)vec[2], (new ScaledResolution(this.mc)).getScaleFactor());
            if (points != null && points[2] >= 0.0F && points[2] < 1.0F) {
               float pX = points[0];
               float pY = points[1];
               position[0] = Math.min(position[0], (double)pX);
               position[1] = Math.min(position[1], (double)pY);
               position[2] = Math.max(position[2], (double)pX);
               position[3] = Math.max(position[3], (double)pY);
            }
         }

         this.entities.put((EntityPlayer)entity, new PositionUtils(position[0], position[1], position[2] - position[0], position[3] - position[1], 1.0F));
      }
   }

   @EventListener
   public void onRenderNameTag(EventNametagRenderer e) {
      e.setCancelled(true);
   }

   @EventListener
   public void onBlur(EventBlur e) {
   }

   @EventListener
   public void onBloom(EventBloom e) {
      if (this.bloom.isEnabled()) {
         if (e.getType() == EventType.PRE) {
            e.setCancelled(true);
            return;
         }

         Iterator var3 = this.entities.entrySet().iterator();

         while(var3.hasNext()) {
            Entry<EntityPlayer, PositionUtils> entry = (Entry)var3.next();
            EntityPlayer player = (EntityPlayer)entry.getKey();
            PositionUtils pos = (PositionUtils)entry.getValue();
            if (!player.getName().equals("")) {
               CLAPI.CLUserInfo user = CLAPI.getUserInfo(player.getName());
               double w = Fonts.getFont("rb-r").getWidth(user.runningClient.equals("-") ? player.getName() : "[" + user.runningClient + "]" + " " + user.nickname + " [" + CLUtils.getClientRoleByID(user.role) + "]") + (this.health.isEnabled() ? Fonts.getFont("rb-r").getWidth((int)player.getHealth() + " ") : 0.0D);
               RenderUtils.drawRoundedRectWithCorners(pos.getX() + pos.getWidth() / 2.0D - w / 2.0D - 1.0D, pos.getY() - 10.0D, pos.getX() + pos.getWidth() / 2.0D + w / 2.0D + 1.0D, pos.getY() - 10.0D + 8.0D, -16777216, 2.0D, true, true, true, true);
            }
         }
      }

   }

   @EventListener
   public void onRender2D(EventRenderGui e) {
      Iterator var3 = this.entities.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<EntityPlayer, PositionUtils> entry = (Entry)var3.next();
         EntityPlayer player = (EntityPlayer)entry.getKey();
         PositionUtils pos = (PositionUtils)entry.getValue();
         if (!player.getName().equals("")) {
            CLAPI.CLUserInfo user = CLAPI.getUserInfo(player.getName());
            GlStateManager.pushMatrix();
            GL11.glTranslated(pos.getX() + pos.getWidth() / 2.0D, pos.getY() - 10.0D, 0.0D);
            double w = Fonts.getFont("rb-r").getWidth(user.runningClient.equals("-") ? player.getName() : "[" + user.runningClient + "]" + " " + user.nickname + " [" + CLUtils.getClientRoleByID(user.role) + "]") + (this.health.isEnabled() ? Fonts.getFont("rb-r").getWidth((int)player.getHealth() + " ") : 0.0D);
            RenderUtils.drawRoundedRectWithCorners(-w / 2.0D - 1.0D, 0.0D, w / 2.0D + 1.0D, 8.0D, -1879048192, 2.0D, true, true, true, true);
            if (this.health.isEnabled()) {
               Fonts.getFont("rb-r").drawString(String.valueOf((int)player.getHealth()), w / 2.0D - Fonts.getFont("rb-r").getWidth(String.valueOf((int)player.getHealth())), 3.0D, ColorUtils.mix(-65536, -16711936, (double)(player.getHealth() < 0.0F ? 0.0F : player.getHealth()), (double)player.getMaxHealth()));
            }

            Fonts.getFont("rb-r").drawString(user.runningClient.equals("-") ? player.getName() : "§7[" + user.runningClient + '§' + "7]" + " " + '§' + "f" + user.nickname + " " + '§' + "7[" + CLUtils.getClientRoleByID(user.role) + '§' + "7]", -w / 2.0D, 3.0D, -1);
            GlStateManager.popMatrix();
         }
      }

   }
}
