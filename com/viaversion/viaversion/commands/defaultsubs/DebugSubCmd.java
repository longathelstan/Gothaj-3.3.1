package com.viaversion.viaversion.commands.defaultsubs;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.command.ViaSubCommand;
import com.viaversion.viaversion.api.debug.DebugHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DebugSubCmd extends ViaSubCommand {
   public String name() {
      return "debug";
   }

   public String description() {
      return "Toggle various debug modes.";
   }

   public boolean execute(ViaCommandSender sender, String[] args) {
      DebugHandler debug = Via.getManager().debugHandler();
      if (args.length == 0) {
         Via.getManager().debugHandler().setEnabled(!Via.getManager().debugHandler().enabled());
         sendMessage(sender, "&6Debug mode is now %s", new Object[]{Via.getManager().debugHandler().enabled() ? "&aenabled" : "&cdisabled"});
         return true;
      } else {
         if (args.length == 1) {
            if (args[0].equalsIgnoreCase("clear")) {
               debug.clearPacketTypesToLog();
               sendMessage(sender, "&6Cleared packet types to log", new Object[0]);
               return true;
            }

            if (args[0].equalsIgnoreCase("logposttransform")) {
               debug.setLogPostPacketTransform(!debug.logPostPacketTransform());
               sendMessage(sender, "&6Post transform packet logging is now %s", new Object[]{debug.logPostPacketTransform() ? "&aenabled" : "&cdisabled"});
               return true;
            }
         } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
               debug.addPacketTypeNameToLog(args[1].toUpperCase(Locale.ROOT));
               sendMessage(sender, "&6Added packet type %s to debug logging", new Object[]{args[1]});
               return true;
            }

            if (args[0].equalsIgnoreCase("remove")) {
               debug.removePacketTypeNameToLog(args[1].toUpperCase(Locale.ROOT));
               sendMessage(sender, "&6Removed packet type %s from debug logging", new Object[]{args[1]});
               return true;
            }
         }

         return false;
      }
   }

   public List<String> onTabComplete(ViaCommandSender sender, String[] args) {
      return args.length == 1 ? Arrays.asList("clear", "logposttransform", "add", "remove") : Collections.emptyList();
   }
}
