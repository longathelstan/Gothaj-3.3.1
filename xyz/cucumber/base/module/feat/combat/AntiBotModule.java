package xyz.cucumber.base.module.feat.combat;

import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import xyz.cucumber.base.events.EventListener;
import xyz.cucumber.base.events.ext.EventTick;
import xyz.cucumber.base.module.Category;
import xyz.cucumber.base.module.Mod;
import xyz.cucumber.base.module.ModuleInfo;
import xyz.cucumber.base.module.settings.BooleanSettings;
import xyz.cucumber.base.module.settings.ModuleSettings;

@ModuleInfo(
   category = Category.COMBAT,
   description = "Automatically removes anticheat bot",
   name = "Anti Bot",
   key = 0
)
public class AntiBotModule extends Mod {
   public BooleanSettings hypixel = new BooleanSettings("Hypixel", true);
   public BooleanSettings matrix = new BooleanSettings("Matrix", true);

   public AntiBotModule() {
      this.addSettings(new ModuleSettings[]{this.hypixel, this.matrix});
   }

   @EventListener
   public void onTick(EventTick e) {
      Iterator var3 = this.mc.theWorld.loadedEntityList.iterator();

      while(true) {
         Entity ent;
         do {
            do {
               if (!var3.hasNext()) {
                  return;
               }

               ent = (Entity)var3.next();
            } while(ent == this.mc.thePlayer);
         } while(!(ent instanceof EntityPlayer));

         if (ent.getCustomNameTag() == this.mc.thePlayer.getCustomNameTag() || ent.getCustomNameTag() == this.mc.thePlayer.getName()) {
            this.mc.theWorld.removeEntity(ent);
         }

         if (ent.getCustomNameTag() == "" && this.matrix.isEnabled()) {
            this.mc.theWorld.removeEntity(ent);
         }

         if (ent.isInvisible() && this.hypixel.isEnabled()) {
            this.mc.theWorld.removeEntity(ent);
         }
      }
   }
}
