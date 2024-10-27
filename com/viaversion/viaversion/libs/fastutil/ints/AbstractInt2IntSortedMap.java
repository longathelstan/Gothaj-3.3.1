package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractInt2IntSortedMap extends AbstractInt2IntMap implements Int2IntSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractInt2IntSortedMap() {
   }

   public IntSortedSet keySet() {
      return new AbstractInt2IntSortedMap.KeySet();
   }

   public IntCollection values() {
      return new AbstractInt2IntSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractIntSortedSet {
      public boolean contains(int k) {
         return AbstractInt2IntSortedMap.this.containsKey(k);
      }

      public int size() {
         return AbstractInt2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2IntSortedMap.this.clear();
      }

      public IntComparator comparator() {
         return AbstractInt2IntSortedMap.this.comparator();
      }

      public int firstInt() {
         return AbstractInt2IntSortedMap.this.firstIntKey();
      }

      public int lastInt() {
         return AbstractInt2IntSortedMap.this.lastIntKey();
      }

      public IntSortedSet headSet(int to) {
         return AbstractInt2IntSortedMap.this.headMap(to).keySet();
      }

      public IntSortedSet tailSet(int from) {
         return AbstractInt2IntSortedMap.this.tailMap(from).keySet();
      }

      public IntSortedSet subSet(int from, int to) {
         return AbstractInt2IntSortedMap.this.subMap(from, to).keySet();
      }

      public IntBidirectionalIterator iterator(int from) {
         return new AbstractInt2IntSortedMap.KeySetIterator(AbstractInt2IntSortedMap.this.int2IntEntrySet().iterator(new AbstractInt2IntMap.BasicEntry(from, 0)));
      }

      public IntBidirectionalIterator iterator() {
         return new AbstractInt2IntSortedMap.KeySetIterator(Int2IntSortedMaps.fastIterator(AbstractInt2IntSortedMap.this));
      }
   }

   protected class ValuesCollection extends AbstractIntCollection {
      public IntIterator iterator() {
         return new AbstractInt2IntSortedMap.ValuesIterator(Int2IntSortedMaps.fastIterator(AbstractInt2IntSortedMap.this));
      }

      public boolean contains(int k) {
         return AbstractInt2IntSortedMap.this.containsValue(k);
      }

      public int size() {
         return AbstractInt2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2IntSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements IntIterator {
      protected final ObjectBidirectionalIterator<Int2IntMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Int2IntMap.Entry> i) {
         this.i = i;
      }

      public int nextInt() {
         return ((Int2IntMap.Entry)this.i.next()).getIntValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected static class KeySetIterator implements IntBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Int2IntMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Int2IntMap.Entry> i) {
         this.i = i;
      }

      public int nextInt() {
         return ((Int2IntMap.Entry)this.i.next()).getIntKey();
      }

      public int previousInt() {
         return ((Int2IntMap.Entry)this.i.previous()).getIntKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }
}
