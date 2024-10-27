package com.viaversion.viaversion.bungee.platform;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.bungee.providers.BungeeVersionProvider;
import com.viaversion.viaversion.configuration.AbstractViaConfig;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BungeeViaConfig extends AbstractViaConfig {
   private static final List<String> UNSUPPORTED = Arrays.asList("nms-player-ticking", "item-cache", "quick-move-action-fix", "velocity-ping-interval", "velocity-ping-save", "velocity-servers", "blockconnection-method", "change-1_9-hitbox", "change-1_14-hitbox");
   private int bungeePingInterval;
   private boolean bungeePingSave;
   private Map<String, Integer> bungeeServerProtocols;

   public BungeeViaConfig(File configFile) {
      super(new File(configFile, "config.yml"));
      this.reload();
   }

   protected void loadFields() {
      super.loadFields();
      this.bungeePingInterval = this.getInt("bungee-ping-interval", 60);
      this.bungeePingSave = this.getBoolean("bungee-ping-save", true);
      this.bungeeServerProtocols = (Map)this.get("bungee-servers", Map.class, new HashMap());
   }

   protected void handleConfig(Map<String, Object> config) {
      Object servers;
      if (!(config.get("bungee-servers") instanceof Map)) {
         servers = new HashMap();
      } else {
         servers = (Map)config.get("bungee-servers");
      }

      Iterator var3 = (new HashSet(((Map)servers).entrySet())).iterator();

      while(var3.hasNext()) {
         Entry<String, Object> entry = (Entry)var3.next();
         if (!(entry.getValue() instanceof Integer)) {
            if (entry.getValue() instanceof String) {
               ProtocolVersion found = ProtocolVersion.getClosest((String)entry.getValue());
               if (found != null) {
                  ((Map)servers).put(entry.getKey(), found.getVersion());
               } else {
                  ((Map)servers).remove(entry.getKey());
               }
            } else {
               ((Map)servers).remove(entry.getKey());
            }
         }
      }

      if (!((Map)servers).containsKey("default")) {
         ((Map)servers).put("default", BungeeVersionProvider.getLowestSupportedVersion());
      }

      config.put("bungee-servers", servers);
   }

   public List<String> getUnsupportedOptions() {
      return UNSUPPORTED;
   }

   public boolean isItemCache() {
      return false;
   }

   public boolean isNMSPlayerTicking() {
      return false;
   }

   public int getBungeePingInterval() {
      return this.bungeePingInterval;
   }

   public boolean isBungeePingSave() {
      return this.bungeePingSave;
   }

   public Map<String, Integer> getBungeeServerProtocols() {
      return this.bungeeServerProtocols;
   }
}
