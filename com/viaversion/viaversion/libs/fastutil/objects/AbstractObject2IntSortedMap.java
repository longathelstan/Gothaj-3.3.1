package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.ints.AbstractIntCollection;
import com.viaversion.viaversion.libs.fastutil.ints.IntCollection;
import com.viaversion.viaversion.libs.fastutil.ints.IntIterator;
import java.util.Comparator;

public abstract class AbstractObject2IntSortedMap<K> extends AbstractObject2IntMap<K> implements Object2IntSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractObject2IntSortedMap() {
   }

   public ObjectSortedSet<K> keySet() {
      return new AbstractObject2IntSortedMap.KeySet();
   }

   public IntCollection values() {
      return new AbstractObject2IntSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractObjectSortedSet<K> {
      public boolean contains(Object k) {
         return AbstractObject2IntSortedMap.this.containsKey(k);
      }

      public int size() {
         return AbstractObject2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2IntSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractObject2IntSortedMap.this.comparator();
      }

      public K first() {
         return AbstractObject2IntSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractObject2IntSortedMap.this.lastKey();
      }

      public ObjectSortedSet<K> headSet(K to) {
         return AbstractObject2IntSortedMap.this.headMap(to).keySet();
      }

      public ObjectSortedSet<K> tailSet(K from) {
         return AbstractObject2IntSortedMap.this.tailMap(from).keySet();
      }

      public ObjectSortedSet<K> subSet(K from, K to) {
         return AbstractObject2IntSortedMap.this.subMap(from, to).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K from) {
         return new AbstractObject2IntSortedMap.KeySetIterator(AbstractObject2IntSortedMap.this.object2IntEntrySet().iterator(new AbstractObject2IntMap.BasicEntry(from, 0)));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractObject2IntSortedMap.KeySetIterator(Object2IntSortedMaps.fastIterator(AbstractObject2IntSortedMap.this));
      }
   }

   protected class ValuesCollection extends AbstractIntCollection {
      public IntIterator iterator() {
         return new AbstractObject2IntSortedMap.ValuesIterator(Object2IntSortedMaps.fastIterator(AbstractObject2IntSortedMap.this));
      }

      public boolean contains(int k) {
         return AbstractObject2IntSortedMap.this.containsValue(k);
      }

      public int size() {
         return AbstractObject2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2IntSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator<K> implements IntIterator {
      protected final ObjectBidirectionalIterator<Object2IntMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Object2IntMap.Entry<K>> i) {
         this.i = i;
      }

      public int nextInt() {
         return ((Object2IntMap.Entry)this.i.next()).getIntValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Object2IntMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Object2IntMap.Entry<K>> i) {
         this.i = i;
      }

      public K next() {
         return ((Object2IntMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Object2IntMap.Entry)this.i.previous()).getKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }
}
