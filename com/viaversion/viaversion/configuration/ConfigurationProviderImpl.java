package com.viaversion.viaversion.configuration;

import com.viaversion.viaversion.api.configuration.Config;
import com.viaversion.viaversion.api.configuration.ConfigurationProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ConfigurationProviderImpl implements ConfigurationProvider {
   private final List<Config> configs = new ArrayList();

   public void register(Config config) {
      this.configs.add(config);
   }

   public Collection<Config> configs() {
      return Collections.unmodifiableCollection(this.configs);
   }

   public void reloadConfigs() {
      Iterator var1 = this.configs.iterator();

      while(var1.hasNext()) {
         Config config = (Config)var1.next();
         config.reload();
      }

   }
}
