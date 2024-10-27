package com.viaversion.viabackwards;

import com.viaversion.viaversion.util.Config;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ViaBackwardsConfig extends Config implements com.viaversion.viabackwards.api.ViaBackwardsConfig {
   private boolean addCustomEnchantsToLore;
   private boolean addTeamColorToPrefix;
   private boolean fix1_13FacePlayer;
   private boolean alwaysShowOriginalMobName;
   private boolean fix1_13FormattedInventoryTitles;
   private boolean handlePingsAsInvAcknowledgements;

   public ViaBackwardsConfig(File configFile) {
      super(configFile);
   }

   public void reload() {
      super.reload();
      this.loadFields();
   }

   private void loadFields() {
      this.addCustomEnchantsToLore = this.getBoolean("add-custom-enchants-into-lore", true);
      this.addTeamColorToPrefix = this.getBoolean("add-teamcolor-to-prefix", true);
      this.fix1_13FacePlayer = this.getBoolean("fix-1_13-face-player", false);
      this.fix1_13FormattedInventoryTitles = this.getBoolean("fix-formatted-inventory-titles", true);
      this.alwaysShowOriginalMobName = this.getBoolean("always-show-original-mob-name", true);
      this.handlePingsAsInvAcknowledgements = this.getBoolean("handle-pings-as-inv-acknowledgements", false);
   }

   public boolean addCustomEnchantsToLore() {
      return this.addCustomEnchantsToLore;
   }

   public boolean addTeamColorTo1_13Prefix() {
      return this.addTeamColorToPrefix;
   }

   public boolean isFix1_13FacePlayer() {
      return this.fix1_13FacePlayer;
   }

   public boolean fix1_13FormattedInventoryTitle() {
      return this.fix1_13FormattedInventoryTitles;
   }

   public boolean alwaysShowOriginalMobName() {
      return this.alwaysShowOriginalMobName;
   }

   public boolean handlePingsAsInvAcknowledgements() {
      return this.handlePingsAsInvAcknowledgements || Boolean.getBoolean("com.viaversion.handlePingsAsInvAcknowledgements");
   }

   public URL getDefaultConfigURL() {
      return this.getClass().getClassLoader().getResource("assets/viabackwards/config.yml");
   }

   protected void handleConfig(Map<String, Object> map) {
   }

   public List<String> getUnsupportedOptions() {
      return Collections.emptyList();
   }
}
