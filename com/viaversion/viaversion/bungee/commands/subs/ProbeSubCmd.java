package com.viaversion.viaversion.bungee.commands.subs;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.command.ViaSubCommand;
import com.viaversion.viaversion.bungee.platform.BungeeViaConfig;

public class ProbeSubCmd extends ViaSubCommand {
   public String name() {
      return "probe";
   }

   public String description() {
      return "Forces ViaVersion to scan server protocol versions " + (((BungeeViaConfig)Via.getConfig()).getBungeePingInterval() == -1 ? "" : "(Also happens at an interval)");
   }

   public boolean execute(ViaCommandSender sender, String[] args) {
      Via.proxyPlatform().protocolDetectorService().probeAllServers();
      sendMessage(sender, "&6Started searching for protocol versions", new Object[0]);
      return true;
   }
}
