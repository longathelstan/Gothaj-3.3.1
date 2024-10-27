package com.viaversion.viaversion.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.update.UpdateUtil;

public class UpdateListener {
   @Subscribe
   public void onJoin(PostLoginEvent e) {
      if (e.getPlayer().hasPermission("viaversion.update") && Via.getConfig().isCheckForUpdates()) {
         UpdateUtil.sendUpdateMessage(e.getPlayer().getUniqueId());
      }

   }
}
