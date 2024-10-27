package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.ints.IntCollection;
import com.viaversion.viaversion.libs.fastutil.ints.IntSets;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class Object2IntMaps {
   public static final Object2IntMaps.EmptyMap EMPTY_MAP = new Object2IntMaps.EmptyMap();

   private Object2IntMaps() {
   }

   public static <K> ObjectIterator<Object2IntMap.Entry<K>> fastIterator(Object2IntMap<K> map) {
      ObjectSet<Object2IntMap.Entry<K>> entries = map.object2IntEntrySet();
      return entries instanceof Object2IntMap.FastEntrySet ? ((Object2IntMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static <K> void fastForEach(Object2IntMap<K> map, Consumer<? super Object2IntMap.Entry<K>> consumer) {
      ObjectSet<Object2IntMap.Entry<K>> entries = map.object2IntEntrySet();
      if (entries instanceof Object2IntMap.FastEntrySet) {
         ((Object2IntMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }

   }

   public static <K> ObjectIterable<Object2IntMap.Entry<K>> fastIterable(Object2IntMap<K> map) {
      final ObjectSet<Object2IntMap.Entry<K>> entries = map.object2IntEntrySet();
      return (ObjectIterable)(entries instanceof Object2IntMap.FastEntrySet ? new ObjectIterable<Object2IntMap.Entry<K>>() {
         public ObjectIterator<Object2IntMap.Entry<K>> iterator() {
            return ((Object2IntMap.FastEntrySet)entries).fastIterator();
         }

         public ObjectSpliterator<Object2IntMap.Entry<K>> spliterator() {
            return entries.spliterator();
         }

         public void forEach(Consumer<? super Object2IntMap.Entry<K>> consumer) {
            ((Object2IntMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static <K> Object2IntMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Object2IntMap<K> singleton(K key, int value) {
      return new Object2IntMaps.Singleton(key, value);
   }

   public static <K> Object2IntMap<K> singleton(K key, Integer value) {
      return new Object2IntMaps.Singleton(key, value);
   }

   public static <K> Object2IntMap<K> synchronize(Object2IntMap<K> m) {
      return new com.viaversion.viaversion.libs.fastutil.objects.Object2IntMaps.SynchronizedMap(m);
   }

   public static <K> Object2IntMap<K> synchronize(Object2IntMap<K> m, Object sync) {
      return new com.viaversion.viaversion.libs.fastutil.objects.Object2IntMaps.SynchronizedMap(m, sync);
   }

   public static <K> Object2IntMap<K> unmodifiable(Object2IntMap<? extends K> m) {
      return new com.viaversion.viaversion.libs.fastutil.objects.Object2IntMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap<K> extends Object2IntFunctions.EmptyFunction<K> implements Object2IntMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
      }

      public boolean containsValue(int v) {
         return false;
      }

      /** @deprecated */
      @Deprecated
      public Integer getOrDefault(Object key, Integer defaultValue) {
         return defaultValue;
      }

      public int getOrDefault(Object key, int defaultValue) {
         return defaultValue;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object ov) {
         return false;
      }

      public void putAll(Map<? extends K, ? extends Integer> m) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2IntMap.Entry<K>> object2IntEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ObjectSet<K> keySet() {
         return ObjectSets.EMPTY_SET;
      }

      public IntCollection values() {
         return IntSets.EMPTY_SET;
      }

      public void forEach(BiConsumer<? super K, ? super Integer> consumer) {
      }

      public Object clone() {
         return Object2IntMaps.EMPTY_MAP;
      }

      public boolean isEmpty() {
         return true;
      }

      public int hashCode() {
         return 0;
      }

      public boolean equals(Object o) {
         return !(o instanceof Map) ? false : ((Map)o).isEmpty();
      }

      public String toString() {
         return "{}";
      }
   }

   public static class Singleton<K> extends Object2IntFunctions.Singleton<K> implements Object2IntMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Object2IntMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient IntCollection values;

      protected Singleton(K key, int value) {
         super(key, value);
      }

      public boolean containsValue(int v) {
         return this.value == v;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object ov) {
         return (Integer)ov == this.value;
      }

      public void putAll(Map<? extends K, ? extends Integer> m) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2IntMap.Entry<K>> object2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractObject2IntMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Integer>> entrySet() {
         return this.object2IntEntrySet();
      }

      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.singleton(this.key);
         }

         return this.keys;
      }

      public IntCollection values() {
         if (this.values == null) {
            this.values = IntSets.singleton(this.value);
         }

         return this.values;
      }

      public boolean isEmpty() {
         return false;
      }

      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ this.value;
      }

      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof Map)) {
            return false;
         } else {
            Map<?, ?> m = (Map)o;
            return m.size() != 1 ? false : ((Entry)m.entrySet().iterator().next()).equals(this.entrySet().iterator().next());
         }
      }

      public String toString() {
         return "{" + this.key + "=>" + this.value + "}";
      }
   }
}
