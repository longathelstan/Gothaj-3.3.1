package com.viaversion.viaversion.libs.fastutil.ints;

import java.io.Serializable;
import java.util.NoSuchElementException;

public final class IntSortedSets {
   public static final IntSortedSets.EmptySet EMPTY_SET = new IntSortedSets.EmptySet();

   private IntSortedSets() {
   }

   public static IntSortedSet singleton(int element) {
      return new IntSortedSets.Singleton(element);
   }

   public static IntSortedSet singleton(int element, IntComparator comparator) {
      return new IntSortedSets.Singleton(element, comparator);
   }

   public static IntSortedSet singleton(Object element) {
      return new IntSortedSets.Singleton((Integer)element);
   }

   public static IntSortedSet singleton(Object element, IntComparator comparator) {
      return new IntSortedSets.Singleton((Integer)element, comparator);
   }

   public static IntSortedSet synchronize(IntSortedSet s) {
      return new com.viaversion.viaversion.libs.fastutil.ints.IntSortedSets.SynchronizedSortedSet(s);
   }

   public static IntSortedSet synchronize(IntSortedSet s, Object sync) {
      return new com.viaversion.viaversion.libs.fastutil.ints.IntSortedSets.SynchronizedSortedSet(s, sync);
   }

   public static IntSortedSet unmodifiable(IntSortedSet s) {
      return new com.viaversion.viaversion.libs.fastutil.ints.IntSortedSets.UnmodifiableSortedSet(s);
   }

   public static class Singleton extends IntSets.Singleton implements IntSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      final IntComparator comparator;

      protected Singleton(int element, IntComparator comparator) {
         super(element);
         this.comparator = comparator;
      }

      Singleton(int element) {
         this(element, (IntComparator)null);
      }

      final int compare(int k1, int k2) {
         return this.comparator == null ? Integer.compare(k1, k2) : this.comparator.compare(k1, k2);
      }

      public IntBidirectionalIterator iterator(int from) {
         IntBidirectionalIterator i = this.iterator();
         if (this.compare(this.element, from) <= 0) {
            i.nextInt();
         }

         return i;
      }

      public IntComparator comparator() {
         return this.comparator;
      }

      public IntSpliterator spliterator() {
         return IntSpliterators.singleton(this.element, this.comparator);
      }

      public IntSortedSet subSet(int from, int to) {
         return (IntSortedSet)(this.compare(from, this.element) <= 0 && this.compare(this.element, to) < 0 ? this : IntSortedSets.EMPTY_SET);
      }

      public IntSortedSet headSet(int to) {
         return (IntSortedSet)(this.compare(this.element, to) < 0 ? this : IntSortedSets.EMPTY_SET);
      }

      public IntSortedSet tailSet(int from) {
         return (IntSortedSet)(this.compare(from, this.element) <= 0 ? this : IntSortedSets.EMPTY_SET);
      }

      public int firstInt() {
         return this.element;
      }

      public int lastInt() {
         return this.element;
      }

      /** @deprecated */
      @Deprecated
      public IntSortedSet subSet(Integer from, Integer to) {
         return this.subSet(from, to);
      }

      /** @deprecated */
      @Deprecated
      public IntSortedSet headSet(Integer to) {
         return this.headSet(to);
      }

      /** @deprecated */
      @Deprecated
      public IntSortedSet tailSet(Integer from) {
         return this.tailSet(from);
      }

      /** @deprecated */
      @Deprecated
      public Integer first() {
         return this.element;
      }

      /** @deprecated */
      @Deprecated
      public Integer last() {
         return this.element;
      }
   }

   public static class EmptySet extends IntSets.EmptySet implements IntSortedSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
      }

      public IntBidirectionalIterator iterator(int from) {
         return IntIterators.EMPTY_ITERATOR;
      }

      public IntSortedSet subSet(int from, int to) {
         return IntSortedSets.EMPTY_SET;
      }

      public IntSortedSet headSet(int from) {
         return IntSortedSets.EMPTY_SET;
      }

      public IntSortedSet tailSet(int to) {
         return IntSortedSets.EMPTY_SET;
      }

      public int firstInt() {
         throw new NoSuchElementException();
      }

      public int lastInt() {
         throw new NoSuchElementException();
      }

      public IntComparator comparator() {
         return null;
      }

      /** @deprecated */
      @Deprecated
      public IntSortedSet subSet(Integer from, Integer to) {
         return IntSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public IntSortedSet headSet(Integer from) {
         return IntSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public IntSortedSet tailSet(Integer to) {
         return IntSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public Integer first() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Integer last() {
         throw new NoSuchElementException();
      }

      public Object clone() {
         return IntSortedSets.EMPTY_SET;
      }

      private Object readResolve() {
         return IntSortedSets.EMPTY_SET;
      }
   }
}
