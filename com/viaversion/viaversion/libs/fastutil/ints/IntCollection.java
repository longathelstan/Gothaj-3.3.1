package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.Size64;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface IntCollection extends Collection<Integer>, IntIterable {
   IntIterator iterator();

   default IntIterator intIterator() {
      return this.iterator();
   }

   default IntSpliterator spliterator() {
      return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf((Collection)this), 320);
   }

   default IntSpliterator intSpliterator() {
      return this.spliterator();
   }

   boolean add(int var1);

   boolean contains(int var1);

   boolean rem(int var1);

   /** @deprecated */
   @Deprecated
   default boolean add(Integer key) {
      return this.add(key);
   }

   /** @deprecated */
   @Deprecated
   default boolean contains(Object key) {
      return key == null ? false : this.contains((Integer)key);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object key) {
      return key == null ? false : this.rem((Integer)key);
   }

   int[] toIntArray();

   /** @deprecated */
   @Deprecated
   default int[] toIntArray(int[] a) {
      return this.toArray(a);
   }

   int[] toArray(int[] var1);

   boolean addAll(IntCollection var1);

   boolean containsAll(IntCollection var1);

   boolean removeAll(IntCollection var1);

   /** @deprecated */
   @Deprecated
   default boolean removeIf(Predicate<? super Integer> filter) {
      return this.removeIf(filter instanceof java.util.function.IntPredicate ? (java.util.function.IntPredicate)filter : (key) -> {
         return filter.test(key);
      });
   }

   default boolean removeIf(java.util.function.IntPredicate filter) {
      Objects.requireNonNull(filter);
      boolean removed = false;
      IntIterator each = this.iterator();

      while(each.hasNext()) {
         if (filter.test(each.nextInt())) {
            each.remove();
            removed = true;
         }
      }

      return removed;
   }

   default boolean removeIf(IntPredicate filter) {
      return this.removeIf((java.util.function.IntPredicate)filter);
   }

   boolean retainAll(IntCollection var1);

   /** @deprecated */
   @Deprecated
   default Stream<Integer> stream() {
      return super.stream();
   }

   default IntStream intStream() {
      return StreamSupport.intStream(this.intSpliterator(), false);
   }

   /** @deprecated */
   @Deprecated
   default Stream<Integer> parallelStream() {
      return super.parallelStream();
   }

   default IntStream intParallelStream() {
      return StreamSupport.intStream(this.intSpliterator(), true);
   }
}
