package com.viaversion.viaversion.commands.defaultsubs;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.command.ViaSubCommand;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

public class ListSubCmd extends ViaSubCommand {
   public String name() {
      return "list";
   }

   public String description() {
      return "Shows lists of the versions from logged in players.";
   }

   public String usage() {
      return "list";
   }

   public boolean execute(ViaCommandSender sender, String[] args) {
      Map<ProtocolVersion, Set<String>> playerVersions = new TreeMap((o1, o2) -> {
         return ProtocolVersion.getIndex(o2) - ProtocolVersion.getIndex(o1);
      });
      ViaCommandSender[] var4 = Via.getPlatform().getOnlinePlayers();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         ViaCommandSender p = var4[var6];
         int playerVersion = Via.getAPI().getPlayerVersion(p.getUUID());
         ProtocolVersion key = ProtocolVersion.getProtocol(playerVersion);
         ((Set)playerVersions.computeIfAbsent(key, (s) -> {
            return new HashSet();
         })).add(p.getName());
      }

      Iterator var10 = playerVersions.entrySet().iterator();

      while(var10.hasNext()) {
         Entry<ProtocolVersion, Set<String>> entry = (Entry)var10.next();
         sendMessage(sender, "&8[&6%s&8] (&7%d&8): &b%s", new Object[]{((ProtocolVersion)entry.getKey()).getName(), ((Set)entry.getValue()).size(), entry.getValue()});
      }

      playerVersions.clear();
      return true;
   }
}
