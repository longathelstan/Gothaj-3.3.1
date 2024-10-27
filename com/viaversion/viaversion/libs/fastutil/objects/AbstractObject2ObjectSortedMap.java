package com.viaversion.viaversion.libs.fastutil.objects;

import java.util.Comparator;

public abstract class AbstractObject2ObjectSortedMap<K, V> extends AbstractObject2ObjectMap<K, V> implements Object2ObjectSortedMap<K, V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractObject2ObjectSortedMap() {
   }

   public ObjectSortedSet<K> keySet() {
      return new AbstractObject2ObjectSortedMap.KeySet();
   }

   public ObjectCollection<V> values() {
      return new AbstractObject2ObjectSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractObjectSortedSet<K> {
      public boolean contains(Object k) {
         return AbstractObject2ObjectSortedMap.this.containsKey(k);
      }

      public int size() {
         return AbstractObject2ObjectSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2ObjectSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractObject2ObjectSortedMap.this.comparator();
      }

      public K first() {
         return AbstractObject2ObjectSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractObject2ObjectSortedMap.this.lastKey();
      }

      public ObjectSortedSet<K> headSet(K to) {
         return AbstractObject2ObjectSortedMap.this.headMap(to).keySet();
      }

      public ObjectSortedSet<K> tailSet(K from) {
         return AbstractObject2ObjectSortedMap.this.tailMap(from).keySet();
      }

      public ObjectSortedSet<K> subSet(K from, K to) {
         return AbstractObject2ObjectSortedMap.this.subMap(from, to).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K from) {
         return new AbstractObject2ObjectSortedMap.KeySetIterator(AbstractObject2ObjectSortedMap.this.object2ObjectEntrySet().iterator(new AbstractObject2ObjectMap.BasicEntry(from, (Object)null)));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractObject2ObjectSortedMap.KeySetIterator(Object2ObjectSortedMaps.fastIterator(AbstractObject2ObjectSortedMap.this));
      }
   }

   protected class ValuesCollection extends AbstractObjectCollection<V> {
      public ObjectIterator<V> iterator() {
         return new AbstractObject2ObjectSortedMap.ValuesIterator(Object2ObjectSortedMaps.fastIterator(AbstractObject2ObjectSortedMap.this));
      }

      public boolean contains(Object k) {
         return AbstractObject2ObjectSortedMap.this.containsValue(k);
      }

      public int size() {
         return AbstractObject2ObjectSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2ObjectSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator<K, V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Object2ObjectMap.Entry<K, V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Object2ObjectMap.Entry<K, V>> i) {
         this.i = i;
      }

      public V next() {
         return ((Object2ObjectMap.Entry)this.i.next()).getValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected static class KeySetIterator<K, V> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Object2ObjectMap.Entry<K, V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Object2ObjectMap.Entry<K, V>> i) {
         this.i = i;
      }

      public K next() {
         return ((Object2ObjectMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Object2ObjectMap.Entry)this.i.previous()).getKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }
}
