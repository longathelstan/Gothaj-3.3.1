package com.viaversion.viaversion.bungee.providers;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.bungee.storage.BungeeStorage;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.BossBarProvider;
import java.util.UUID;

public class BungeeBossBarProvider extends BossBarProvider {
   public void handleAdd(UserConnection user, UUID barUUID) {
      if (user.has(BungeeStorage.class)) {
         BungeeStorage storage = (BungeeStorage)user.get(BungeeStorage.class);
         if (storage.getBossbar() != null) {
            storage.getBossbar().add(barUUID);
         }
      }

   }

   public void handleRemove(UserConnection user, UUID barUUID) {
      if (user.has(BungeeStorage.class)) {
         BungeeStorage storage = (BungeeStorage)user.get(BungeeStorage.class);
         if (storage.getBossbar() != null) {
            storage.getBossbar().remove(barUUID);
         }
      }

   }
}
