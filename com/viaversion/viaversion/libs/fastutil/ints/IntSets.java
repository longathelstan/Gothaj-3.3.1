package com.viaversion.viaversion.libs.fastutil.ints;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class IntSets {
   static final int ARRAY_SET_CUTOFF = 4;
   public static final IntSets.EmptySet EMPTY_SET = new IntSets.EmptySet();
   static final IntSet UNMODIFIABLE_EMPTY_SET;

   private IntSets() {
   }

   public static IntSet emptySet() {
      return EMPTY_SET;
   }

   public static IntSet singleton(int element) {
      return new IntSets.Singleton(element);
   }

   public static IntSet singleton(Integer element) {
      return new IntSets.Singleton(element);
   }

   public static IntSet synchronize(IntSet s) {
      return new com.viaversion.viaversion.libs.fastutil.ints.IntSets.SynchronizedSet(s);
   }

   public static IntSet synchronize(IntSet s, Object sync) {
      return new com.viaversion.viaversion.libs.fastutil.ints.IntSets.SynchronizedSet(s, sync);
   }

   public static IntSet unmodifiable(IntSet s) {
      return new com.viaversion.viaversion.libs.fastutil.ints.IntSets.UnmodifiableSet(s);
   }

   public static IntSet fromTo(final int from, final int to) {
      return new AbstractIntSet() {
         public boolean contains(int x) {
            return x >= from && x < to;
         }

         public IntIterator iterator() {
            return IntIterators.fromTo(from, to);
         }

         public int size() {
            long size = (long)to - (long)from;
            return size >= 0L && size <= 2147483647L ? (int)size : Integer.MAX_VALUE;
         }
      };
   }

   public static IntSet from(final int from) {
      return new AbstractIntSet() {
         public boolean contains(int x) {
            return x >= from;
         }

         public IntIterator iterator() {
            return IntIterators.concat(IntIterators.fromTo(from, Integer.MAX_VALUE), IntSets.singleton(Integer.MAX_VALUE).iterator());
         }

         public int size() {
            long size = 2147483647L - (long)from + 1L;
            return size >= 0L && size <= 2147483647L ? (int)size : Integer.MAX_VALUE;
         }
      };
   }

   public static IntSet to(final int to) {
      return new AbstractIntSet() {
         public boolean contains(int x) {
            return x < to;
         }

         public IntIterator iterator() {
            return IntIterators.fromTo(Integer.MIN_VALUE, to);
         }

         public int size() {
            long size = (long)to - -2147483648L;
            return size >= 0L && size <= 2147483647L ? (int)size : Integer.MAX_VALUE;
         }
      };
   }

   static {
      UNMODIFIABLE_EMPTY_SET = unmodifiable(new IntArraySet(IntArrays.EMPTY_ARRAY));
   }

   public static class EmptySet extends IntCollections.EmptyCollection implements IntSet, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySet() {
      }

      public boolean remove(int ok) {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return IntSets.EMPTY_SET;
      }

      public boolean equals(Object o) {
         return o instanceof Set && ((Set)o).isEmpty();
      }

      /** @deprecated */
      @Deprecated
      public boolean rem(int k) {
         return super.rem(k);
      }

      private Object readResolve() {
         return IntSets.EMPTY_SET;
      }
   }

   public static class Singleton extends AbstractIntSet implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final int element;

      protected Singleton(int element) {
         this.element = element;
      }

      public boolean contains(int k) {
         return k == this.element;
      }

      public boolean remove(int k) {
         throw new UnsupportedOperationException();
      }

      public IntListIterator iterator() {
         return IntIterators.singleton(this.element);
      }

      public IntSpliterator spliterator() {
         return IntSpliterators.singleton(this.element);
      }

      public int size() {
         return 1;
      }

      public int[] toIntArray() {
         return new int[]{this.element};
      }

      /** @deprecated */
      @Deprecated
      public void forEach(Consumer<? super Integer> action) {
         action.accept(this.element);
      }

      public boolean addAll(Collection<? extends Integer> c) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> c) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> c) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean removeIf(Predicate<? super Integer> filter) {
         throw new UnsupportedOperationException();
      }

      public void forEach(java.util.function.IntConsumer action) {
         action.accept(this.element);
      }

      public boolean addAll(IntCollection c) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(IntCollection c) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(IntCollection c) {
         throw new UnsupportedOperationException();
      }

      public boolean removeIf(java.util.function.IntPredicate filter) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Object[] toArray() {
         return new Object[]{this.element};
      }

      public Object clone() {
         return this;
      }
   }
}
