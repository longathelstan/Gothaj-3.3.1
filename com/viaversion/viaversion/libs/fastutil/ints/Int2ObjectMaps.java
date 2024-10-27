package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.objects.ObjectCollection;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterable;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSet;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSets;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSpliterator;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class Int2ObjectMaps {
   public static final Int2ObjectMaps.EmptyMap EMPTY_MAP = new Int2ObjectMaps.EmptyMap();

   private Int2ObjectMaps() {
   }

   public static <V> ObjectIterator<Int2ObjectMap.Entry<V>> fastIterator(Int2ObjectMap<V> map) {
      ObjectSet<Int2ObjectMap.Entry<V>> entries = map.int2ObjectEntrySet();
      return entries instanceof Int2ObjectMap.FastEntrySet ? ((Int2ObjectMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static <V> void fastForEach(Int2ObjectMap<V> map, Consumer<? super Int2ObjectMap.Entry<V>> consumer) {
      ObjectSet<Int2ObjectMap.Entry<V>> entries = map.int2ObjectEntrySet();
      if (entries instanceof Int2ObjectMap.FastEntrySet) {
         ((Int2ObjectMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }

   }

   public static <V> ObjectIterable<Int2ObjectMap.Entry<V>> fastIterable(Int2ObjectMap<V> map) {
      final ObjectSet<Int2ObjectMap.Entry<V>> entries = map.int2ObjectEntrySet();
      return (ObjectIterable)(entries instanceof Int2ObjectMap.FastEntrySet ? new ObjectIterable<Int2ObjectMap.Entry<V>>() {
         public ObjectIterator<Int2ObjectMap.Entry<V>> iterator() {
            return ((Int2ObjectMap.FastEntrySet)entries).fastIterator();
         }

         public ObjectSpliterator<Int2ObjectMap.Entry<V>> spliterator() {
            return entries.spliterator();
         }

         public void forEach(Consumer<? super Int2ObjectMap.Entry<V>> consumer) {
            ((Int2ObjectMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static <V> Int2ObjectMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Int2ObjectMap<V> singleton(int key, V value) {
      return new Int2ObjectMaps.Singleton(key, value);
   }

   public static <V> Int2ObjectMap<V> singleton(Integer key, V value) {
      return new Int2ObjectMaps.Singleton(key, value);
   }

   public static <V> Int2ObjectMap<V> synchronize(Int2ObjectMap<V> m) {
      return new com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMaps.SynchronizedMap(m);
   }

   public static <V> Int2ObjectMap<V> synchronize(Int2ObjectMap<V> m, Object sync) {
      return new com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMaps.SynchronizedMap(m, sync);
   }

   public static <V> Int2ObjectMap<V> unmodifiable(Int2ObjectMap<? extends V> m) {
      return new com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMaps.UnmodifiableMap(m);
   }

   public static class EmptyMap<V> extends Int2ObjectFunctions.EmptyFunction<V> implements Int2ObjectMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
      }

      public boolean containsValue(Object v) {
         return false;
      }

      /** @deprecated */
      @Deprecated
      public V getOrDefault(Object key, V defaultValue) {
         return defaultValue;
      }

      public V getOrDefault(int key, V defaultValue) {
         return defaultValue;
      }

      public void putAll(Map<? extends Integer, ? extends V> m) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public IntSet keySet() {
         return IntSets.EMPTY_SET;
      }

      public ObjectCollection<V> values() {
         return ObjectSets.EMPTY_SET;
      }

      public void forEach(BiConsumer<? super Integer, ? super V> consumer) {
      }

      public Object clone() {
         return Int2ObjectMaps.EMPTY_MAP;
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

   public static class Singleton<V> extends Int2ObjectFunctions.Singleton<V> implements Int2ObjectMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Int2ObjectMap.Entry<V>> entries;
      protected transient IntSet keys;
      protected transient ObjectCollection<V> values;

      protected Singleton(int key, V value) {
         super(key, value);
      }

      public boolean containsValue(Object v) {
         return Objects.equals(this.value, v);
      }

      public void putAll(Map<? extends Integer, ? extends V> m) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractInt2ObjectMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Integer, V>> entrySet() {
         return this.int2ObjectEntrySet();
      }

      public IntSet keySet() {
         if (this.keys == null) {
            this.keys = IntSets.singleton(this.key);
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
         return this.key ^ (this.value == null ? 0 : this.value.hashCode());
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
