package com.viaversion.viaversion.api.data;

import com.google.common.annotations.Beta;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntOpenHashMap;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonIOException;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonSyntaxException;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.io.NBTIO;
import com.viaversion.viaversion.libs.opennbt.tag.io.TagReader;
import com.viaversion.viaversion.util.GsonUtil;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class MappingDataLoader {
   private static final Map<String, CompoundTag> MAPPINGS_CACHE = new HashMap();
   private static final TagReader<CompoundTag> MAPPINGS_READER = NBTIO.reader(CompoundTag.class).named();
   private static final byte DIRECT_ID = 0;
   private static final byte SHIFTS_ID = 1;
   private static final byte CHANGES_ID = 2;
   private static final byte IDENTITY_ID = 3;
   private static boolean cacheValid = true;

   /** @deprecated */
   @Deprecated
   public static void enableMappingsCache() {
   }

   public static void clearCache() {
      MAPPINGS_CACHE.clear();
      cacheValid = false;
   }

   @Nullable
   public static JsonObject loadFromDataDir(String name) {
      File file = new File(Via.getPlatform().getDataFolder(), name);
      if (!file.exists()) {
         return loadData(name);
      } else {
         try {
            FileReader reader = new FileReader(file);
            Throwable var3 = null;

            JsonObject var4;
            try {
               var4 = (JsonObject)GsonUtil.getGson().fromJson((Reader)reader, (Class)JsonObject.class);
            } catch (Throwable var15) {
               var3 = var15;
               throw var15;
            } finally {
               if (reader != null) {
                  if (var3 != null) {
                     try {
                        reader.close();
                     } catch (Throwable var14) {
                        var3.addSuppressed(var14);
                     }
                  } else {
                     reader.close();
                  }
               }

            }

            return var4;
         } catch (JsonSyntaxException var17) {
            Via.getPlatform().getLogger().warning(name + " is badly formatted!");
            throw new RuntimeException(var17);
         } catch (JsonIOException | IOException var18) {
            throw new RuntimeException(var18);
         }
      }
   }

   @Nullable
   public static JsonObject loadData(String name) {
      InputStream stream = getResource(name);
      if (stream == null) {
         return null;
      } else {
         try {
            InputStreamReader reader = new InputStreamReader(stream);
            Throwable var3 = null;

            JsonObject var4;
            try {
               var4 = (JsonObject)GsonUtil.getGson().fromJson((Reader)reader, (Class)JsonObject.class);
            } catch (Throwable var14) {
               var3 = var14;
               throw var14;
            } finally {
               if (reader != null) {
                  if (var3 != null) {
                     try {
                        reader.close();
                     } catch (Throwable var13) {
                        var3.addSuppressed(var13);
                     }
                  } else {
                     reader.close();
                  }
               }

            }

            return var4;
         } catch (IOException var16) {
            throw new RuntimeException(var16);
         }
      }
   }

   @Nullable
   public static CompoundTag loadNBT(String name, boolean cache) {
      if (!cacheValid) {
         return loadNBTFromFile(name);
      } else {
         CompoundTag data = (CompoundTag)MAPPINGS_CACHE.get(name);
         if (data != null) {
            return data;
         } else {
            data = loadNBTFromFile(name);
            if (cache && data != null) {
               MAPPINGS_CACHE.put(name, data);
            }

            return data;
         }
      }
   }

   @Nullable
   public static CompoundTag loadNBT(String name) {
      return loadNBT(name, false);
   }

   @Nullable
   public static CompoundTag loadNBTFromFile(String name) {
      InputStream resource = getResource(name);
      if (resource == null) {
         return null;
      } else {
         try {
            InputStream stream = resource;
            Throwable var3 = null;

            CompoundTag var4;
            try {
               var4 = (CompoundTag)MAPPINGS_READER.read(stream);
            } catch (Throwable var14) {
               var3 = var14;
               throw var14;
            } finally {
               if (resource != null) {
                  if (var3 != null) {
                     try {
                        stream.close();
                     } catch (Throwable var13) {
                        var3.addSuppressed(var13);
                     }
                  } else {
                     resource.close();
                  }
               }

            }

            return var4;
         } catch (IOException var16) {
            throw new RuntimeException(var16);
         }
      }
   }

   @Nullable
   public static Mappings loadMappings(CompoundTag mappingsTag, String key) {
      return loadMappings(mappingsTag, key, (size) -> {
         int[] array = new int[size];
         Arrays.fill(array, -1);
         return array;
      }, (array, id, mappedId) -> {
         array[id] = mappedId;
      }, IntArrayMappings::of);
   }

   @Beta
   @Nullable
   public static <M extends Mappings, V> Mappings loadMappings(CompoundTag mappingsTag, String key, MappingDataLoader.MappingHolderSupplier<V> holderSupplier, MappingDataLoader.AddConsumer<V> addConsumer, MappingDataLoader.MappingsSupplier<M, V> mappingsSupplier) {
      CompoundTag tag = (CompoundTag)mappingsTag.get(key);
      if (tag == null) {
         return null;
      } else {
         ByteTag serializationStragetyTag = (ByteTag)tag.get("id");
         IntTag mappedSizeTag = (IntTag)tag.get("mappedSize");
         byte strategy = serializationStragetyTag.asByte();
         IntArrayTag changesAtTag;
         if (strategy == 0) {
            changesAtTag = (IntArrayTag)tag.get("val");
            return IntArrayMappings.of(changesAtTag.getValue(), mappedSizeTag.asInt());
         } else {
            Object mappings;
            IntArrayTag valuesTag;
            IntTag sizeTag;
            int[] changesAt;
            int i;
            int id;
            int previousId;
            int identity;
            if (strategy == 1) {
               changesAtTag = (IntArrayTag)tag.get("at");
               valuesTag = (IntArrayTag)tag.get("to");
               sizeTag = (IntTag)tag.get("size");
               int[] shiftsAt = changesAtTag.getValue();
               changesAt = valuesTag.getValue();
               int size = sizeTag.asInt();
               mappings = holderSupplier.get(size);
               if (shiftsAt[0] != 0) {
                  i = shiftsAt[0];

                  for(id = 0; id < i; ++id) {
                     addConsumer.addTo(mappings, id, id);
                  }
               }

               for(i = 0; i < shiftsAt.length; ++i) {
                  id = shiftsAt[i];
                  previousId = i == shiftsAt.length - 1 ? size : shiftsAt[i + 1];
                  identity = changesAt[i];

                  for(int id = id; id < previousId; ++id) {
                     addConsumer.addTo(mappings, id, identity++);
                  }
               }
            } else {
               if (strategy != 2) {
                  if (strategy == 3) {
                     IntTag sizeTag = (IntTag)tag.get("size");
                     return new IdentityMappings(sizeTag.asInt(), mappedSizeTag.asInt());
                  }

                  throw new IllegalArgumentException("Unknown serialization strategy: " + strategy);
               }

               changesAtTag = (IntArrayTag)tag.get("at");
               valuesTag = (IntArrayTag)tag.get("val");
               sizeTag = (IntTag)tag.get("size");
               boolean fillBetween = tag.get("nofill") == null;
               changesAt = changesAtTag.getValue();
               int[] values = valuesTag.getValue();
               mappings = holderSupplier.get(sizeTag.asInt());

               for(i = 0; i < changesAt.length; ++i) {
                  id = changesAt[i];
                  if (fillBetween) {
                     previousId = i != 0 ? changesAt[i - 1] + 1 : 0;

                     for(identity = previousId; identity < id; ++identity) {
                        addConsumer.addTo(mappings, identity, identity);
                     }
                  }

                  addConsumer.addTo(mappings, id, values[i]);
               }
            }

            return mappingsSupplier.create(mappings, mappedSizeTag.asInt());
         }
      }
   }

   public static FullMappings loadFullMappings(CompoundTag mappingsTag, CompoundTag unmappedIdentifiers, CompoundTag mappedIdentifiers, String key) {
      ListTag unmappedElements = (ListTag)unmappedIdentifiers.get(key);
      ListTag mappedElements = (ListTag)mappedIdentifiers.get(key);
      if (unmappedElements != null && mappedElements != null) {
         Mappings mappings = loadMappings(mappingsTag, key);
         if (mappings == null) {
            mappings = new IdentityMappings(unmappedElements.size(), mappedElements.size());
         }

         return new FullMappingsBase((List)unmappedElements.getValue().stream().map((t) -> {
            return (String)t.getValue();
         }).collect(Collectors.toList()), (List)mappedElements.getValue().stream().map((t) -> {
            return (String)t.getValue();
         }).collect(Collectors.toList()), (Mappings)mappings);
      } else {
         return null;
      }
   }

   /** @deprecated */
   @Deprecated
   public static void mapIdentifiers(int[] output, JsonObject unmappedIdentifiers, JsonObject mappedIdentifiers, @Nullable JsonObject diffIdentifiers, boolean warnOnMissing) {
      Object2IntMap<String> newIdentifierMap = indexedObjectToMap(mappedIdentifiers);
      Iterator var6 = unmappedIdentifiers.entrySet().iterator();

      while(var6.hasNext()) {
         Entry<String, JsonElement> entry = (Entry)var6.next();
         int id = Integer.parseInt((String)entry.getKey());
         int mappedId = mapIdentifierEntry(id, ((JsonElement)entry.getValue()).getAsString(), newIdentifierMap, diffIdentifiers, warnOnMissing);
         if (mappedId != -1) {
            output[id] = mappedId;
         }
      }

   }

   private static int mapIdentifierEntry(int id, String val, Object2IntMap<String> mappedIdentifiers, @Nullable JsonObject diffIdentifiers, boolean warnOnMissing) {
      int mappedId = mappedIdentifiers.getInt(val);
      if (mappedId == -1) {
         if (diffIdentifiers != null) {
            JsonElement diffElement = diffIdentifiers.get(val);
            if (diffElement != null || (diffElement = diffIdentifiers.get(Integer.toString(id))) != null) {
               String mappedName = diffElement.getAsString();
               if (mappedName.isEmpty()) {
                  return -1;
               }

               mappedId = mappedIdentifiers.getInt(mappedName);
            }
         }

         if (mappedId == -1) {
            if (warnOnMissing && !Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
               Via.getPlatform().getLogger().warning("No key for " + val + " :( ");
            }

            return -1;
         }
      }

      return mappedId;
   }

   /** @deprecated */
   @Deprecated
   public static void mapIdentifiers(int[] output, JsonArray unmappedIdentifiers, JsonArray mappedIdentifiers, @Nullable JsonObject diffIdentifiers, boolean warnOnMissing) {
      Object2IntMap<String> newIdentifierMap = arrayToMap(mappedIdentifiers);

      for(int id = 0; id < unmappedIdentifiers.size(); ++id) {
         JsonElement unmappedIdentifier = unmappedIdentifiers.get(id);
         int mappedId = mapIdentifierEntry(id, unmappedIdentifier.getAsString(), newIdentifierMap, diffIdentifiers, warnOnMissing);
         if (mappedId != -1) {
            output[id] = mappedId;
         }
      }

   }

   public static Object2IntMap<String> indexedObjectToMap(JsonObject object) {
      Object2IntMap<String> map = new Object2IntOpenHashMap(object.size(), 0.99F);
      map.defaultReturnValue(-1);
      Iterator var2 = object.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<String, JsonElement> entry = (Entry)var2.next();
         map.put(((JsonElement)entry.getValue()).getAsString(), Integer.parseInt((String)entry.getKey()));
      }

      return map;
   }

   public static Object2IntMap<String> arrayToMap(JsonArray array) {
      Object2IntMap<String> map = new Object2IntOpenHashMap(array.size(), 0.99F);
      map.defaultReturnValue(-1);

      for(int i = 0; i < array.size(); ++i) {
         map.put(array.get(i).getAsString(), i);
      }

      return map;
   }

   @Nullable
   public static InputStream getResource(String name) {
      return MappingDataLoader.class.getClassLoader().getResourceAsStream("assets/viaversion/data/" + name);
   }

   @FunctionalInterface
   public interface MappingsSupplier<T extends Mappings, V> {
      T create(V var1, int var2);
   }

   @FunctionalInterface
   public interface MappingHolderSupplier<T> {
      T get(int var1);
   }

   @FunctionalInterface
   public interface AddConsumer<T> {
      void addTo(T var1, int var2, int var3);
   }
}
