package com.viaversion.viaversion.bukkit.commands;

import com.viaversion.viaversion.api.command.ViaCommandSender;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

public class BukkitCommandSender implements ViaCommandSender {
   private final CommandSender sender;

   public BukkitCommandSender(CommandSender sender) {
      this.sender = sender;
   }

   public boolean hasPermission(String permission) {
      return this.sender.hasPermission(permission);
   }

   public void sendMessage(String msg) {
      this.sender.sendMessage(msg);
   }

   public UUID getUUID() {
      return this.sender instanceof Entity ? ((Entity)this.sender).getUniqueId() : new UUID(0L, 0L);
   }

   public String getName() {
      return this.sender.getName();
   }
}
