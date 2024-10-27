package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.objects.ObjectArrays;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class IntCollections {
   private IntCollections() {
   }

   public static IntCollection synchronize(IntCollection c) {
      return new com.viaversion.viaversion.libs.fastutil.ints.IntCollections.SynchronizedCollection(c);
   }

   public static IntCollection synchronize(IntCollection c, Object sync) {
      return new com.viaversion.viaversion.libs.fastutil.ints.IntCollections.SynchronizedCollection(c, sync);
   }

   public static IntCollection unmodifiable(IntCollection c) {
      return new com.viaversion.viaversion.libs.fastutil.ints.IntCollections.UnmodifiableCollection(c);
   }

   public static IntCollection asCollection(IntIterable iterable) {
      return (IntCollection)(iterable instanceof IntCollection ? (IntCollection)iterable : new IntCollections.IterableCollection(iterable));
   }

   public static class IterableCollection extends AbstractIntCollection implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntIterable iterable;

      protected IterableCollection(IntIterable iterable) {
         this.iterable = (IntIterable)Objects.requireNonNull(iterable);
      }

      public int size() {
         long size = this.iterable.spliterator().getExactSizeIfKnown();
         if (size >= 0L) {
            return (int)Math.min(2147483647L, size);
         } else {
            int c = 0;

            for(IntIterator iterator = this.iterator(); iterator.hasNext(); ++c) {
               iterator.nextInt();
            }

            return c;
         }
      }

      public boolean isEmpty() {
         return !this.iterable.iterator().hasNext();
      }

      public IntIterator iterator() {
         return this.iterable.iterator();
      }

      public IntSpliterator spliterator() {
         return this.iterable.spliterator();
      }

      public IntIterator intIterator() {
         return this.iterable.intIterator();
      }

      public IntSpliterator intSpliterator() {
         return this.iterable.intSpliterator();
      }
   }

   static class SizeDecreasingSupplier<C extends IntCollection> implements Supplier<C> {
      static final int RECOMMENDED_MIN_SIZE = 8;
      final AtomicInteger suppliedCount = new AtomicInteger(0);
      final int expectedFinalSize;
      final IntFunction<C> builder;

      SizeDecreasingSupplier(int expectedFinalSize, IntFunction<C> builder) {
         this.expectedFinalSize = expectedFinalSize;
         this.builder = builder;
      }

      public C get() {
         int expectedNeededNextSize = 1 + (this.expectedFinalSize - 1) / this.suppliedCount.incrementAndGet();
         if (expectedNeededNextSize < 0) {
            expectedNeededNextSize = 8;
         }

         return (IntCollection)this.builder.apply(expectedNeededNextSize);
      }
   }

   public abstract static class EmptyCollection extends AbstractIntCollection {
      protected EmptyCollection() {
      }

      public boolean contains(int k) {
         return false;
      }

      public Object[] toArray() {
         return ObjectArrays.EMPTY_ARRAY;
      }

      public <T> T[] toArray(T[] array) {
         if (array.length > 0) {
            array[0] = null;
         }

         return array;
      }

      public IntBidirectionalIterator iterator() {
         return IntIterators.EMPTY_ITERATOR;
      }

      public IntSpliterator spliterator() {
         return IntSpliterators.EMPTY_SPLITERATOR;
      }

      public int size() {
         return 0;
      }

      public void clear() {
      }

      public int hashCode() {
         return 0;
      }

      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else {
            return !(o instanceof Collection) ? false : ((Collection)o).isEmpty();
         }
      }

      /** @deprecated */
      @Deprecated
      public void forEach(Consumer<? super Integer> action) {
      }

      public boolean containsAll(Collection<?> c) {
         return c.isEmpty();
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
         Objects.requireNonNull(filter);
         return false;
      }

      public int[] toIntArray() {
         return IntArrays.EMPTY_ARRAY;
      }

      /** @deprecated */
      @Deprecated
      public int[] toIntArray(int[] a) {
         return a;
      }

      public void forEach(java.util.function.IntConsumer action) {
      }

      public boolean containsAll(IntCollection c) {
         return c.isEmpty();
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
         Objects.requireNonNull(filter);
         return false;
      }
   }
}
