package xyz.cucumber.base.commands;

import xyz.cucumber.base.Client;

public class Command {
   private String name = ((CommandInfo)this.getClass().getAnnotation(CommandInfo.class)).name();
   private String[] aliases = ((CommandInfo)this.getClass().getAnnotation(CommandInfo.class)).aliases();
   private String usage = ((CommandInfo)this.getClass().getAnnotation(CommandInfo.class)).usage();

   public void onSendCommand(String[] args) {
   }

   public void sendUsage() {
      Client.INSTANCE.getCommandManager().sendChatMessage("§cUsage: " + this.usage);
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String[] getAliases() {
      return this.aliases;
   }

   public void setAliases(String[] aliases) {
      this.aliases = aliases;
   }

   public String getUsage() {
      return this.usage;
   }

   public void setUsage(String usage) {
      this.usage = usage;
   }
}
