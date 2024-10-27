package com.viaversion.viaversion.commands;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.command.ViaSubCommand;
import com.viaversion.viaversion.api.command.ViaVersionCommand;
import com.viaversion.viaversion.commands.defaultsubs.AutoTeamSubCmd;
import com.viaversion.viaversion.commands.defaultsubs.DebugSubCmd;
import com.viaversion.viaversion.commands.defaultsubs.DisplayLeaksSubCmd;
import com.viaversion.viaversion.commands.defaultsubs.DontBugMeSubCmd;
import com.viaversion.viaversion.commands.defaultsubs.DumpSubCmd;
import com.viaversion.viaversion.commands.defaultsubs.ListSubCmd;
import com.viaversion.viaversion.commands.defaultsubs.PPSSubCmd;
import com.viaversion.viaversion.commands.defaultsubs.ReloadSubCmd;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public abstract class ViaCommandHandler implements ViaVersionCommand {
   private final Map<String, ViaSubCommand> commandMap = new HashMap();

   protected ViaCommandHandler() {
      this.registerDefaults();
   }

   public void registerSubCommand(ViaSubCommand command) {
      Preconditions.checkArgument(command.name().matches("^[a-z0-9_-]{3,15}$"), command.name() + " is not a valid sub-command name.");
      Preconditions.checkArgument(!this.hasSubCommand(command.name()), "ViaSubCommand " + command.name() + " does already exists!");
      this.commandMap.put(command.name().toLowerCase(Locale.ROOT), command);
   }

   public boolean hasSubCommand(String name) {
      return this.commandMap.containsKey(name.toLowerCase(Locale.ROOT));
   }

   public ViaSubCommand getSubCommand(String name) {
      return (ViaSubCommand)this.commandMap.get(name.toLowerCase(Locale.ROOT));
   }

   public boolean onCommand(ViaCommandSender sender, String[] args) {
      boolean hasPermissions = sender.hasPermission("viaversion.admin");
      Iterator var4 = this.commandMap.values().iterator();

      while(var4.hasNext()) {
         ViaSubCommand command = (ViaSubCommand)var4.next();
         if (sender.hasPermission(command.permission())) {
            hasPermissions = true;
            break;
         }
      }

      if (!hasPermissions) {
         sender.sendMessage(ViaSubCommand.color("&cYou are not allowed to use this command!"));
         return false;
      } else if (args.length == 0) {
         this.showHelp(sender);
         return false;
      } else if (!this.hasSubCommand(args[0])) {
         sender.sendMessage(ViaSubCommand.color("&cThis command does not exist."));
         this.showHelp(sender);
         return false;
      } else {
         ViaSubCommand handler = this.getSubCommand(args[0]);
         if (!this.hasPermission(sender, handler.permission())) {
            sender.sendMessage(ViaSubCommand.color("&cYou are not allowed to use this command!"));
            return false;
         } else {
            String[] subArgs = (String[])Arrays.copyOfRange(args, 1, args.length);
            boolean result = handler.execute(sender, subArgs);
            if (!result) {
               sender.sendMessage("Usage: /viaversion " + handler.usage());
            }

            return result;
         }
      }
   }

   public List<String> onTabComplete(ViaCommandSender sender, String[] args) {
      Set<ViaSubCommand> allowed = this.calculateAllowedCommands(sender);
      List<String> output = new ArrayList();
      if (args.length == 1) {
         Iterator var5;
         ViaSubCommand sub;
         if (!args[0].isEmpty()) {
            var5 = allowed.iterator();

            while(var5.hasNext()) {
               sub = (ViaSubCommand)var5.next();
               if (sub.name().toLowerCase().startsWith(args[0].toLowerCase(Locale.ROOT))) {
                  output.add(sub.name());
               }
            }
         } else {
            var5 = allowed.iterator();

            while(var5.hasNext()) {
               sub = (ViaSubCommand)var5.next();
               output.add(sub.name());
            }
         }
      } else if (args.length >= 2 && this.getSubCommand(args[0]) != null) {
         ViaSubCommand sub = this.getSubCommand(args[0]);
         if (!allowed.contains(sub)) {
            return output;
         }

         String[] subArgs = (String[])Arrays.copyOfRange(args, 1, args.length);
         List<String> tab = sub.onTabComplete(sender, subArgs);
         Collections.sort(tab);
         if (!tab.isEmpty()) {
            String currArg = subArgs[subArgs.length - 1];
            Iterator var9 = tab.iterator();

            while(var9.hasNext()) {
               String s = (String)var9.next();
               if (s.toLowerCase(Locale.ROOT).startsWith(currArg.toLowerCase(Locale.ROOT))) {
                  output.add(s);
               }
            }
         }

         return output;
      }

      return output;
   }

   public void showHelp(ViaCommandSender sender) {
      Set<ViaSubCommand> allowed = this.calculateAllowedCommands(sender);
      if (allowed.isEmpty()) {
         sender.sendMessage(ViaSubCommand.color("&cYou are not allowed to use these commands!"));
      } else {
         sender.sendMessage(ViaSubCommand.color("&aViaVersion &c" + Via.getPlatform().getPluginVersion()));
         sender.sendMessage(ViaSubCommand.color("&6Commands:"));
         Iterator var3 = allowed.iterator();

         while(var3.hasNext()) {
            ViaSubCommand cmd = (ViaSubCommand)var3.next();
            sender.sendMessage(ViaSubCommand.color(String.format("&2/viaversion %s &7- &6%s", cmd.usage(), cmd.description())));
         }

         allowed.clear();
      }
   }

   private Set<ViaSubCommand> calculateAllowedCommands(ViaCommandSender sender) {
      Set<ViaSubCommand> cmds = new HashSet();
      Iterator var3 = this.commandMap.values().iterator();

      while(var3.hasNext()) {
         ViaSubCommand sub = (ViaSubCommand)var3.next();
         if (this.hasPermission(sender, sub.permission())) {
            cmds.add(sub);
         }
      }

      return cmds;
   }

   private boolean hasPermission(ViaCommandSender sender, String permission) {
      return permission == null || sender.hasPermission("viaversion.admin") || sender.hasPermission(permission);
   }

   private void registerDefaults() {
      this.registerSubCommand(new ListSubCmd());
      this.registerSubCommand(new PPSSubCmd());
      this.registerSubCommand(new DebugSubCmd());
      this.registerSubCommand(new DumpSubCmd());
      this.registerSubCommand(new DisplayLeaksSubCmd());
      this.registerSubCommand(new DontBugMeSubCmd());
      this.registerSubCommand(new AutoTeamSubCmd());
      this.registerSubCommand(new ReloadSubCmd());
   }
}
