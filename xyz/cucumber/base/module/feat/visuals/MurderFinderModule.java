package xyz.cucumber.base.module.feat.visuals;

import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import xyz.cucumber.base.Client;
import xyz.cucumber.base.events.EventListener;
import xyz.cucumber.base.events.ext.EventLook;
import xyz.cucumber.base.events.ext.EventRender3D;
import xyz.cucumber.base.events.ext.EventWorldChange;
import xyz.cucumber.base.module.Category;
import xyz.cucumber.base.module.Mod;
import xyz.cucumber.base.module.ModuleInfo;

@ModuleInfo(
   category = Category.VISUALS,
   description = "Shows murder in minigame",
   name = "Murder Finder"
)
public class MurderFinderModule extends Mod {
   public HashMap<String, Entity> murders = new HashMap();

   public void onEnable() {
      this.murders.clear();
   }

   @EventListener
   public void onTick(EventLook e) {
      Iterator var3 = this.mc.theWorld.loadedEntityList.iterator();

      while(var3.hasNext()) {
         Entity entity = (Entity)var3.next();
         if (entity instanceof EntityPlayer && entity != this.mc.thePlayer && !this.murders.containsKey(entity.getName())) {
            EntityPlayer player = (EntityPlayer)entity;
            if (player.getHeldItem() != null && player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemSword) {
               this.murders.put(player.getName(), player);
               Client.INSTANCE.getCommandManager().sendChatMessage("Found murder: " + player.getName());
            }
         }
      }

   }

   @EventListener
   public void onRender3D(EventRender3D e) {
   }

   @EventListener
   public void onWorldChange(EventWorldChange e) {
      this.murders.clear();
   }
}
