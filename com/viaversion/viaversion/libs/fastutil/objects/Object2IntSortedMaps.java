package com.viaversion.viaversion.libs.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;

public final class Object2IntSortedMaps {
   public static final Object2IntSortedMaps.EmptySortedMap EMPTY_MAP = new Object2IntSortedMaps.EmptySortedMap();

   private Object2IntSortedMaps() {
   }

   public static <K> Comparator<? super Entry<K, ?>> entryComparator(Comparator<? super K> comparator) {
      return (x, y) -> {
         return comparator.compare(x.getKey(), y.getKey());
      };
   }

   public static <K> ObjectBidirectionalIterator<Object2IntMap.Entry<K>> fastIterator(Object2IntSortedMap<K> map) {
      ObjectSortedSet<Object2IntMap.Entry<K>> entries = map.object2IntEntrySet();
      return entries instanceof Object2IntSortedMap.FastSortedEntrySet ? ((Object2IntSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
   }

   public static <K> ObjectBidirectionalIterable<Object2IntMap.Entry<K>> fastIterable(Object2IntSortedMap<K> map) {
      ObjectSortedSet<Object2IntMap.Entry<K>> entries = map.object2IntEntrySet();
      Object var2;
      if (entries instanceof Object2IntSortedMap.FastSortedEntrySet) {
         Object2IntSortedMap.FastSortedEntrySet var10000 = (Object2IntSortedMap.FastSortedEntrySet)entries;
         Objects.requireNonNull((Object2IntSortedMap.FastSortedEntrySet)entries);
         var2 = var10000::fastIterator;
      } else {
         var2 = entries;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static <K> Object2IntSortedMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Object2IntSortedMap<K> singleton(K key, Integer value) {
      return new Object2IntSortedMaps.Singleton(key, value);
   }

   public static <K> Object2IntSortedMap<K> singleton(K key, Integer value, Comparator<? super K> comparator) {
      return new Object2IntSortedMaps.Singleton(key, value, comparator);
   }

   public static <K> Object2IntSortedMap<K> singleton(K key, int value) {
      return new Object2IntSortedMaps.Singleton(key, value);
   }

   public static <K> Object2IntSortedMap<K> singleton(K key, int value, Comparator<? super K> comparator) {
      return new Object2IntSortedMaps.Singleton(key, value, comparator);
   }

   public static <K> Object2IntSortedMap<K> synchronize(Object2IntSortedMap<K> m) {
      return new com.viaversion.viaversion.libs.fastutil.objects.Object2IntSortedMaps.SynchronizedSortedMap(m);
   }

   public static <K> Object2IntSortedMap<K> synchronize(Object2IntSortedMap<K> m, Object sync) {
      return new com.viaversion.viaversion.libs.fastutil.objects.Object2IntSortedMaps.SynchronizedSortedMap(m, sync);
   }

   public static <K> Object2IntSortedMap<K> unmodifiable(Object2IntSortedMap<K> m) {
      return new com.viaversion.viaversion.libs.fastutil.objects.Object2IntSortedMaps.UnmodifiableSortedMap(m);
   }

   public static class EmptySortedMap<K> extends Object2IntMaps.EmptyMap<K> implements Object2IntSortedMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
      }

      public Comparator<? super K> comparator() {
         return null;
      }

      public ObjectSortedSet<Object2IntMap.Entry<K>> object2IntEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<K, Integer>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ObjectSortedSet<K> keySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public Object2IntSortedMap<K> subMap(K from, K to) {
         return Object2IntSortedMaps.EMPTY_MAP;
      }

      public Object2IntSortedMap<K> headMap(K to) {
         return Object2IntSortedMaps.EMPTY_MAP;
      }

      public Object2IntSortedMap<K> tailMap(K from) {
         return Object2IntSortedMaps.EMPTY_MAP;
      }

      public K firstKey() {
         throw new NoSuchElementException();
      }

      public K lastKey() {
         throw new NoSuchElementException();
      }
   }

   public static class Singleton<K> extends Object2IntMaps.Singleton<K> implements Object2IntSortedMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Comparator<? super K> comparator;

      protected Singleton(K key, int value, Comparator<? super K> comparator) {
         super(key, value);
         this.comparator = comparator;
      }

      protected Singleton(K key, int value) {
         this(key, value, (Comparator)null);
      }

      final int compare(K k1, K k2) {
         return this.comparator == null ? ((Comparable)k1).compareTo(k2) : this.comparator.compare(k1, k2);
      }

      public Comparator<? super K> comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Object2IntMap.Entry<K>> object2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractObject2IntMap.BasicEntry(this.key, this.value), Object2IntSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<K, Integer>> entrySet() {
         return this.object2IntEntrySet();
      }

      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSortedSets.singleton(this.key, this.comparator);
         }

         return (ObjectSortedSet)this.keys;
      }

      public Object2IntSortedMap<K> subMap(K from, K to) {
         return (Object2IntSortedMap)(this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0 ? this : Object2IntSortedMaps.EMPTY_MAP);
      }

      public Object2IntSortedMap<K> headMap(K to) {
         return (Object2IntSortedMap)(this.compare(this.key, to) < 0 ? this : Object2IntSortedMaps.EMPTY_MAP);
      }

      public Object2IntSortedMap<K> tailMap(K from) {
         return (Object2IntSortedMap)(this.compare(from, this.key) <= 0 ? this : Object2IntSortedMaps.EMPTY_MAP);
      }

      public K firstKey() {
         return this.key;
      }

      public K lastKey() {
         return this.key;
      }
   }
}
