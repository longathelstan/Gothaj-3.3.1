package com.viaversion.viaversion.sponge.platform;

import com.viaversion.viaversion.SpongePlugin;
import com.viaversion.viaversion.api.platform.PlatformTask;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.sponge.listeners.UpdateListener;
import java.util.HashSet;
import java.util.Set;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.EventManager;

public class SpongeViaLoader implements ViaPlatformLoader {
   private final SpongePlugin plugin;
   private final Set<Object> listeners = new HashSet();
   private final Set<PlatformTask> tasks = new HashSet();

   public SpongeViaLoader(SpongePlugin plugin) {
      this.plugin = plugin;
   }

   private void registerListener(Object listener) {
      Sponge.eventManager().registerListeners(this.plugin.container(), this.storeListener(listener));
   }

   private <T> T storeListener(T listener) {
      this.listeners.add(listener);
      return listener;
   }

   public void load() {
      this.registerListener(new UpdateListener());
   }

   public void unload() {
      Set var10000 = this.listeners;
      EventManager var10001 = Sponge.eventManager();
      var10000.forEach(var10001::unregisterListeners);
      this.listeners.clear();
      this.tasks.forEach(PlatformTask::cancel);
      this.tasks.clear();
   }
}
