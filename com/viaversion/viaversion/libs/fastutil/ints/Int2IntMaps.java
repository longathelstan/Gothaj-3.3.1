package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterable;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSet;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSets;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSpliterator;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class Int2IntMaps {
   public static final Int2IntMaps.EmptyMap EMPTY_MAP = new Int2IntMaps.EmptyMap();

   private Int2IntMaps() {
   }

   public static ObjectIterator<Int2IntMap.Entry> fastIterator(Int2IntMap map) {
      ObjectSet<Int2IntMap.Entry> entries = map.int2IntEntrySet();
      return entries instanceof Int2IntMap.FastEntrySet ? ((Int2IntMap.FastEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static void fastForEach(Int2IntMap map, Consumer<? super Int2IntMap.Entry> consumer) {
      ObjectSet<Int2IntMap.Entry> entries = map.int2IntEntrySet();
      if (entries instanceof Int2IntMap.FastEntrySet) {
         ((Int2IntMap.FastEntrySet)entries).fastForEach(consumer);
      } else {
         entries.forEach(consumer);
      }

   }

   public static ObjectIterable<Int2IntMap.Entry> fastIterable(Int2IntMap map) {
      final ObjectSet<Int2IntMap.Entry> entries = map.int2IntEntrySet();
      return (ObjectIterable)(entries instanceof Int2IntMap.FastEntrySet ? new ObjectIterable<Int2IntMap.Entry>() {
         public ObjectIterator<Int2IntMap.Entry> iterator() {
            return ((Int2IntMap.FastEntrySet)entries).fastIterator();
         }

         public ObjectSpliterator<Int2IntMap.Entry> spliterator() {
            return entries.spliterator();
         }

         public void forEach(Consumer<? super Int2IntMap.Entry> consumer) {
            ((Int2IntMap.FastEntrySet)entries).fastForEach(consumer);
         }
      } : entries);
   }

   public static Int2IntMap singleton(int key, int value) {
      return new Int2IntMaps.Singleton(key, value);
   }

   public static Int2IntMap singleton(Integer key, Integer value) {
      return new Int2IntMaps.Singleton(key, value);
   }

   public static Int2IntMap synchronize(Int2IntMap m) {
      return new com.viaversion.viaversion.libs.fastutil.ints.Int2IntMaps.SynchronizedMap(m);
   }

   public static Int2IntMap synchronize(Int2IntMap m, Object sync) {
      return new com.viaversion.viaversion.libs.fastutil.ints.Int2IntMaps.SynchronizedMap(m, sync);
   }

   public static Int2IntMap unmodifiable(Int2IntMap m) {
      return new com.viaversion.viaversion.libs.fastutil.ints.Int2IntMaps.UnmodifiableMap(m);
   }

   public static class Singleton extends Int2IntFunctions.Singleton implements Int2IntMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Int2IntMap.Entry> entries;
      protected transient IntSet keys;
      protected transient IntCollection values;

      protected Singleton(int key, int value) {
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

      public void putAll(Map<? extends Integer, ? extends Integer> m) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractInt2IntMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Integer, Integer>> entrySet() {
         return this.int2IntEntrySet();
      }

      public IntSet keySet() {
         if (this.keys == null) {
            this.keys = IntSets.singleton(this.key);
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
         return this.key ^ this.value;
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

   public static class EmptyMap extends Int2IntFunctions.EmptyFunction implements Int2IntMap, Serializable, Cloneable {
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

      public int getOrDefault(int key, int defaultValue) {
         return defaultValue;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object ov) {
         return false;
      }

      public void putAll(Map<? extends Integer, ? extends Integer> m) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public IntSet keySet() {
         return IntSets.EMPTY_SET;
      }

      public IntCollection values() {
         return IntSets.EMPTY_SET;
      }

      public void forEach(BiConsumer<? super Integer, ? super Integer> consumer) {
      }

      public Object clone() {
         return Int2IntMaps.EMPTY_MAP;
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
}
