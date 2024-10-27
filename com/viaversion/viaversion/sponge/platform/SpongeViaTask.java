package com.viaversion.viaversion.sponge.platform;

import com.viaversion.viaversion.api.platform.PlatformTask;
import org.spongepowered.api.scheduler.ScheduledTask;

public class SpongeViaTask implements PlatformTask<ScheduledTask> {
   private final ScheduledTask task;

   public SpongeViaTask(ScheduledTask task) {
      this.task = task;
   }

   public ScheduledTask getObject() {
      return this.task;
   }

   public void cancel() {
      this.task.cancel();
   }
}
