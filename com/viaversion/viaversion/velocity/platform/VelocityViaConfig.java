package com.viaversion.viaversion.velocity.platform;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.configuration.AbstractViaConfig;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class VelocityViaConfig extends AbstractViaConfig {
   private static final List<String> UNSUPPORTED = Arrays.asList("nms-player-ticking", "item-cache", "quick-move-action-fix", "bungee-ping-interval", "bungee-ping-save", "bungee-servers", "blockconnection-method", "change-1_9-hitbox", "change-1_14-hitbox");
   private int velocityPingInterval;
   private boolean velocityPingSave;
   private Map<String, Integer> velocityServerProtocols;

   public VelocityViaConfig(File configFile) {
      super(new File(configFile, "config.yml"));
      this.reload();
   }

   protected void loadFields() {
      super.loadFields();
      this.velocityPingInterval = this.getInt("velocity-ping-interval", 60);
      this.velocityPingSave = this.getBoolean("velocity-ping-save", true);
      this.velocityServerProtocols = (Map)this.get("velocity-servers", Map.class, new HashMap());
   }

   protected void handleConfig(Map<String, Object> config) {
      Object servers;
      if (!(config.get("velocity-servers") instanceof Map)) {
         servers = new HashMap();
      } else {
         servers = (Map)config.get("velocity-servers");
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
         try {
            ((Map)servers).put("default", VelocityViaInjector.getLowestSupportedProtocolVersion());
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      }

      config.put("velocity-servers", servers);
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

   public int getVelocityPingInterval() {
      return this.velocityPingInterval;
   }

   public boolean isVelocityPingSave() {
      return this.velocityPingSave;
   }

   public Map<String, Integer> getVelocityServerProtocols() {
      return this.velocityServerProtocols;
   }
}
