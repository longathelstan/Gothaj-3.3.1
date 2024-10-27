package com.viaversion.viaversion.util;

import com.viaversion.viaversion.compatibility.YamlCompat;
import com.viaversion.viaversion.compatibility.unsafe.Yaml1Compat;
import com.viaversion.viaversion.compatibility.unsafe.Yaml2Compat;
import com.viaversion.viaversion.libs.gson.JsonElement;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public abstract class Config {
   private static final Logger LOGGER = Logger.getLogger("ViaVersion Config");
   private static final YamlCompat YAMP_COMPAT = YamlCompat.isVersion1() ? new Yaml1Compat() : new Yaml2Compat();
   private static final ThreadLocal<Yaml> YAML = ThreadLocal.withInitial(() -> {
      DumperOptions options = new DumperOptions();
      options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
      options.setPrettyFlow(false);
      options.setIndent(2);
      return new Yaml(YAMP_COMPAT.createSafeConstructor(), YAMP_COMPAT.createRepresenter(options), options);
   });
   private final CommentStore commentStore = new CommentStore('.', 2);
   private final File configFile;
   private Map<String, Object> config;

   protected Config(File configFile) {
      this.configFile = configFile;
   }

   public URL getDefaultConfigURL() {
      return this.getClass().getClassLoader().getResource("assets/viaversion/config.yml");
   }

   public Map<String, Object> loadConfig(File location) {
      return this.loadConfig(location, this.getDefaultConfigURL());
   }

   public synchronized Map<String, Object> loadConfig(File location, URL jarConfigFile) {
      List unsupported = this.getUnsupportedOptions();

      try {
         this.commentStore.storeComments(jarConfigFile.openStream());
         Iterator var4 = unsupported.iterator();

         while(var4.hasNext()) {
            String option = (String)var4.next();
            List<String> comments = this.commentStore.header(option);
            if (comments != null) {
               comments.clear();
            }
         }
      } catch (IOException var41) {
         var41.printStackTrace();
      }

      Map<String, Object> config = null;
      if (location.exists()) {
         try {
            FileInputStream input = new FileInputStream(location);
            Throwable var45 = null;

            try {
               config = (Map)((Yaml)YAML.get()).load((InputStream)input);
            } catch (Throwable var35) {
               var45 = var35;
               throw var35;
            } finally {
               if (input != null) {
                  if (var45 != null) {
                     try {
                        input.close();
                     } catch (Throwable var34) {
                        var45.addSuppressed(var34);
                     }
                  } else {
                     input.close();
                  }
               }

            }
         } catch (IOException var40) {
            var40.printStackTrace();
         }
      }

      if (config == null) {
         config = new HashMap();
      }

      Object defaults = config;

      try {
         InputStream stream = jarConfigFile.openStream();
         Throwable var7 = null;

         try {
            defaults = (Map)((Yaml)YAML.get()).load(stream);
            Iterator var8 = unsupported.iterator();

            while(var8.hasNext()) {
               String option = (String)var8.next();
               ((Map)defaults).remove(option);
            }

            var8 = ((Map)config).entrySet().iterator();

            while(var8.hasNext()) {
               Entry<String, Object> entry = (Entry)var8.next();
               if (((Map)defaults).containsKey(entry.getKey()) && !unsupported.contains(entry.getKey())) {
                  ((Map)defaults).put(entry.getKey(), entry.getValue());
               }
            }
         } catch (Throwable var36) {
            var7 = var36;
            throw var36;
         } finally {
            if (stream != null) {
               if (var7 != null) {
                  try {
                     stream.close();
                  } catch (Throwable var33) {
                     var7.addSuppressed(var33);
                  }
               } else {
                  stream.close();
               }
            }

         }
      } catch (IOException var38) {
         var38.printStackTrace();
      }

      this.handleConfig((Map)defaults);
      this.save(location, (Map)defaults);
      return (Map)defaults;
   }

   protected abstract void handleConfig(Map<String, Object> var1);

   public synchronized void save(File location, Map<String, Object> config) {
      try {
         this.commentStore.writeComments(((Yaml)YAML.get()).dump(config), location);
      } catch (IOException var4) {
         var4.printStackTrace();
      }

   }

   public abstract List<String> getUnsupportedOptions();

   public void set(String path, Object value) {
      this.config.put(path, value);
   }

   public void save() {
      this.configFile.getParentFile().mkdirs();
      this.save(this.configFile, this.config);
   }

   public void save(File file) {
      this.save(file, this.config);
   }

   public void reload() {
      this.configFile.getParentFile().mkdirs();
      this.config = new ConcurrentSkipListMap(this.loadConfig(this.configFile));
   }

   public Map<String, Object> getValues() {
      return this.config;
   }

   @Nullable
   public <T> T get(String key, Class<T> clazz, T def) {
      Object o = this.config.get(key);
      return o != null ? o : def;
   }

   public boolean getBoolean(String key, boolean def) {
      Object o = this.config.get(key);
      return o != null ? (Boolean)o : def;
   }

   @Nullable
   public String getString(String key, @Nullable String def) {
      Object o = this.config.get(key);
      return o != null ? (String)o : def;
   }

   public int getInt(String key, int def) {
      Object o = this.config.get(key);
      if (o != null) {
         return o instanceof Number ? ((Number)o).intValue() : def;
      } else {
         return def;
      }
   }

   public double getDouble(String key, double def) {
      Object o = this.config.get(key);
      if (o != null) {
         return o instanceof Number ? ((Number)o).doubleValue() : def;
      } else {
         return def;
      }
   }

   public List<Integer> getIntegerList(String key) {
      Object o = this.config.get(key);
      return (List)(o != null ? (List)o : new ArrayList());
   }

   public List<String> getStringList(String key) {
      Object o = this.config.get(key);
      return (List)(o != null ? (List)o : new ArrayList());
   }

   public <T> List<T> getListSafe(String key, Class<T> type, String invalidValueMessage) {
      Object o = this.config.get(key);
      if (o instanceof List) {
         List<?> list = (List)o;
         List<T> filteredValues = new ArrayList();
         Iterator var7 = list.iterator();

         while(var7.hasNext()) {
            Object o1 = var7.next();
            if (type.isInstance(o1)) {
               filteredValues.add(type.cast(o1));
            } else if (invalidValueMessage != null) {
               LOGGER.warning(String.format(invalidValueMessage, o1));
            }
         }

         return filteredValues;
      } else {
         return new ArrayList();
      }
   }

   @Nullable
   public JsonElement getSerializedComponent(String key) {
      Object o = this.config.get(key);
      return o != null && !((String)o).isEmpty() ? ComponentUtil.legacyToJson((String)o) : null;
   }
}
