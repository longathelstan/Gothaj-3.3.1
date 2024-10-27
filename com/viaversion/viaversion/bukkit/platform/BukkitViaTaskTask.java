package com.viaversion.viaversion.bukkit.platform;

import com.viaversion.viaversion.api.platform.PlatformTask;
import com.viaversion.viaversion.api.scheduler.Task;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BukkitViaTaskTask implements PlatformTask<Task> {
   private final Task task;

   public BukkitViaTaskTask(Task task) {
      this.task = task;
   }

   @Nullable
   public Task getObject() {
      return this.task;
   }

   public void cancel() {
      this.task.cancel();
   }
}
