package com.viaversion.viaversion.libs.fastutil.objects;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class Object2ObjectMaps {
   public static final Object2ObjectMaps.EmptyMap EMPTY_MAP = new Object2ObjectMaps.EmptyMap();

   private Object2ObjectMaps() {
   }

   public static <K, V> ObjectIterator<Object2ObjectMap.Entry<K, V>> fastIterator(Object2ObjectMap<K, V> map) {
      ObjectSet<Object2ObjectMap.Entry<K, V>> entries = map.object2ObjectEntrySet();
      return entries instanceof Object2ObjectMap.FastEntrySet ? ((Object2ObjectMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static <K, V> void fastForEach(Object2ObjectMap<K, V> map, Consumer<? super Object2ObjectMap.Entry<K, V>> consumer) {
      ObjectSet<Object2ObjectMap.Entry<K, V>> entries = map.object2ObjectEntrySet();
      if (entries instanceof Object2ObjectMap.FastEntrySet) {
         ((Object2ObjectMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }

   }

   public static <K, V> ObjectIterable<Object2ObjectMap.Entry<K, V>> fastIterable(Object2ObjectMap<K, V> map) {
      final ObjectSet<Object2ObjectMap.Entry<K, V>> entries = map.object2ObjectEntrySet();
      return (ObjectIterable)(entries instanceof Object2ObjectMap.FastEntrySet ? new ObjectIterable<Object2ObjectMap.Entry<K, V>>() {
         public ObjectIterator<Object2ObjectMap.Entry<K, V>> iterator() {
            return ((Object2ObjectMap.FastEntrySet)entries).fastIterator();
         }

         public ObjectSpliterator<Object2ObjectMap.Entry<K, V>> spliterator() {
            return entries.spliterator();
         }

         public void forEach(Consumer<? super Object2ObjectMap.Entry<K, V>> consumer) {
            ((Object2ObjectMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static <K, V> Object2ObjectMap<K, V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K, V> Object2ObjectMap<K, V> singleton(K key, V value) {
      return new Object2ObjectMaps.Singleton(key, value);
   }

   public static <K, V> Object2ObjectMap<K, V> synchronize(Object2ObjectMap<K, V> m) {
      return new com.viaversion.viaversion.libs.fastutil.objects.Object2ObjectMaps.SynchronizedMap(m);
   }

   public static <K, V> Object2ObjectMap<K, V> synchronize(Object2ObjectMap<K, V> m, Object sync) {
      return new com.viaversion.viaversion.libs.fastutil.objects.Object2ObjectMaps.SynchronizedMap(m, sync);
   }

   public static <K, V> Object2ObjectMap<K, V> unmodifiable(Object2ObjectMap<? extends K, ? extends V> m) {
      return new com.viaversion.viaversion.libs.fastutil.objects.Object2ObjectMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap<K, V> extends Object2ObjectFunctions.EmptyFunction<K, V> implements Object2ObjectMap<K, V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
      }

      public boolean containsValue(Object v) {
         return false;
      }

      public V getOrDefault(Object key, V defaultValue) {
         return defaultValue;
      }

      public void putAll(Map<? extends K, ? extends V> m) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ObjectSet<K> keySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ObjectCollection<V> values() {
         return ObjectSets.EMPTY_SET;
      }

      public void forEach(BiConsumer<? super K, ? super V> consumer) {
      }

      public Object clone() {
         return Object2ObjectMaps.EMPTY_MAP;
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

   public static class Singleton<K, V> extends Object2ObjectFunctions.Singleton<K, V> implements Object2ObjectMap<K, V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Object2ObjectMap.Entry<K, V>> entries;
      protected transient ObjectSet<K> keys;
      protected transient ObjectCollection<V> values;

      protected Singleton(K key, V value) {
         super(key, value);
      }

      public boolean containsValue(Object v) {
         return Objects.equals(this.value, v);
      }

      public void putAll(Map<? extends K, ? extends V> m) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractObject2ObjectMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      public ObjectSet<Entry<K, V>> entrySet() {
         return this.object2ObjectEntrySet();
      }

      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.singleton(this.key);
         }

         return this.keys;
      }

      public ObjectCollection<V> values() {
         if (this.values == null) {
            this.values = ObjectSets.singleton(this.value);
         }

         return this.values;
      }

      public boolean isEmpty() {
         return false;
      }

      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
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
