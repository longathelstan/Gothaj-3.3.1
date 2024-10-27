package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapPopulator {
   public static <K, V> Map<K, V> createMap(Iterable<K> keys, Iterable<V> values) {
      return populateMap(keys, values, Maps.newLinkedHashMap());
   }

   public static <K, V> Map<K, V> populateMap(Iterable<K> keys, Iterable<V> values, Map<K, V> map) {
      Iterator<V> iterator = values.iterator();
      Iterator var5 = keys.iterator();

      while(var5.hasNext()) {
         K k = (Object)var5.next();
         map.put(k, iterator.next());
      }

      if (iterator.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return map;
      }
   }
}
