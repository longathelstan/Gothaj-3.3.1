package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.BigArrays;
import com.viaversion.viaversion.libs.fastutil.bytes.ByteIterator;
import com.viaversion.viaversion.libs.fastutil.chars.CharIterator;
import com.viaversion.viaversion.libs.fastutil.shorts.ShortIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PrimitiveIterator.OfInt;
import java.util.function.Consumer;

public final class IntIterators {
   public static final IntIterators.EmptyIterator EMPTY_ITERATOR = new IntIterators.EmptyIterator();

   private IntIterators() {
   }

   public static IntListIterator singleton(int element) {
      return new IntIterators.SingletonIterator(element);
   }

   public static IntListIterator wrap(int[] array, int offset, int length) {
      IntArrays.ensureOffsetLength(array, offset, length);
      return new IntIterators.ArrayIterator(array, offset, length);
   }

   public static IntListIterator wrap(int[] array) {
      return new IntIterators.ArrayIterator(array, 0, array.length);
   }

   public static int unwrap(IntIterator i, int[] array, int offset, int max) {
      if (max < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else if (offset >= 0 && offset + max <= array.length) {
         int j;
         for(j = max; j-- != 0 && i.hasNext(); array[offset++] = i.nextInt()) {
         }

         return max - j - 1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static int unwrap(IntIterator i, int[] array) {
      return unwrap(i, array, 0, array.length);
   }

   public static int[] unwrap(IntIterator i, int max) {
      if (max < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else {
         int[] array = new int[16];

         int j;
         for(j = 0; max-- != 0 && i.hasNext(); array[j++] = i.nextInt()) {
            if (j == array.length) {
               array = IntArrays.grow(array, j + 1);
            }
         }

         return IntArrays.trim(array, j);
      }
   }

   public static int[] unwrap(IntIterator i) {
      return unwrap(i, Integer.MAX_VALUE);
   }

   public static long unwrap(IntIterator i, int[][] array, long offset, long max) {
      if (max < 0L) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else if (offset >= 0L && offset + max <= BigArrays.length(array)) {
         long j = max;

         while(j-- != 0L && i.hasNext()) {
            BigArrays.set(array, offset++, i.nextInt());
         }

         return max - j - 1L;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static long unwrap(IntIterator i, int[][] array) {
      return unwrap(i, array, 0L, BigArrays.length(array));
   }

   public static int unwrap(IntIterator i, IntCollection c, int max) {
      if (max < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else {
         int j = max;

         while(j-- != 0 && i.hasNext()) {
            c.add(i.nextInt());
         }

         return max - j - 1;
      }
   }

   public static int[][] unwrapBig(IntIterator i, long max) {
      if (max < 0L) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else {
         int[][] array = IntBigArrays.newBigArray(16L);

         long j;
         for(j = 0L; max-- != 0L && i.hasNext(); BigArrays.set(array, j++, i.nextInt())) {
            if (j == BigArrays.length(array)) {
               array = BigArrays.grow(array, j + 1L);
            }
         }

         return BigArrays.trim(array, j);
      }
   }

   public static int[][] unwrapBig(IntIterator i) {
      return unwrapBig(i, Long.MAX_VALUE);
   }

   public static long unwrap(IntIterator i, IntCollection c) {
      long n;
      for(n = 0L; i.hasNext(); ++n) {
         c.add(i.nextInt());
      }

      return n;
   }

   public static int pour(IntIterator i, IntCollection s, int max) {
      if (max < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else {
         int j = max;

         while(j-- != 0 && i.hasNext()) {
            s.add(i.nextInt());
         }

         return max - j - 1;
      }
   }

   public static int pour(IntIterator i, IntCollection s) {
      return pour(i, s, Integer.MAX_VALUE);
   }

   public static IntList pour(IntIterator i, int max) {
      IntArrayList l = new IntArrayList();
      pour(i, l, max);
      l.trim();
      return l;
   }

   public static IntList pour(IntIterator i) {
      return pour(i, Integer.MAX_VALUE);
   }

   public static IntIterator asIntIterator(Iterator i) {
      if (i instanceof IntIterator) {
         return (IntIterator)i;
      } else {
         return (IntIterator)(i instanceof OfInt ? new IntIterators.PrimitiveIteratorWrapper((OfInt)i) : new IntIterators.IteratorWrapper(i));
      }
   }

   public static IntListIterator asIntIterator(ListIterator i) {
      return (IntListIterator)(i instanceof IntListIterator ? (IntListIterator)i : new IntIterators.ListIteratorWrapper(i));
   }

   public static boolean any(IntIterator iterator, java.util.function.IntPredicate predicate) {
      return indexOf(iterator, predicate) != -1;
   }

   public static boolean all(IntIterator iterator, java.util.function.IntPredicate predicate) {
      Objects.requireNonNull(predicate);

      while(iterator.hasNext()) {
         if (!predicate.test(iterator.nextInt())) {
            return false;
         }
      }

      return true;
   }

   public static int indexOf(IntIterator iterator, java.util.function.IntPredicate predicate) {
      Objects.requireNonNull(predicate);

      for(int i = 0; iterator.hasNext(); ++i) {
         if (predicate.test(iterator.nextInt())) {
            return i;
         }
      }

      return -1;
   }

   public static IntListIterator fromTo(int from, int to) {
      return new IntIterators.IntervalIterator(from, to);
   }

   public static IntIterator concat(IntIterator... a) {
      return concat(a, 0, a.length);
   }

   public static IntIterator concat(IntIterator[] a, int offset, int length) {
      return new IntIterators.IteratorConcatenator(a, offset, length);
   }

   public static IntIterator unmodifiable(IntIterator i) {
      return new com.viaversion.viaversion.libs.fastutil.ints.IntIterators.UnmodifiableIterator(i);
   }

   public static IntBidirectionalIterator unmodifiable(IntBidirectionalIterator i) {
      return new com.viaversion.viaversion.libs.fastutil.ints.IntIterators.UnmodifiableBidirectionalIterator(i);
   }

   public static IntListIterator unmodifiable(IntListIterator i) {
      return new com.viaversion.viaversion.libs.fastutil.ints.IntIterators.UnmodifiableListIterator(i);
   }

   public static IntIterator wrap(ByteIterator iterator) {
      return new com.viaversion.viaversion.libs.fastutil.ints.IntIterators.ByteIteratorWrapper(iterator);
   }

   public static IntIterator wrap(ShortIterator iterator) {
      return new com.viaversion.viaversion.libs.fastutil.ints.IntIterators.ShortIteratorWrapper(iterator);
   }

   public static IntIterator wrap(CharIterator iterator) {
      return new com.viaversion.viaversion.libs.fastutil.ints.IntIterators.CharIteratorWrapper(iterator);
   }

   private static class SingletonIterator implements IntListIterator {
      private final int element;
      private byte curr;

      public SingletonIterator(int element) {
         this.element = element;
      }

      public boolean hasNext() {
         return this.curr == 0;
      }

      public boolean hasPrevious() {
         return this.curr == 1;
      }

      public int nextInt() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 1;
            return this.element;
         }
      }

      public int previousInt() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 0;
            return this.element;
         }
      }

      public void forEachRemaining(java.util.function.IntConsumer action) {
         Objects.requireNonNull(action);
         if (this.curr == 0) {
            action.accept(this.element);
            this.curr = 1;
         }

      }

      public int nextIndex() {
         return this.curr;
      }

      public int previousIndex() {
         return this.curr - 1;
      }

      public int back(int n) {
         if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (n != 0 && this.curr >= 1) {
            this.curr = 1;
            return 1;
         } else {
            return 0;
         }
      }

      public int skip(int n) {
         if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (n != 0 && this.curr <= 0) {
            this.curr = 0;
            return 1;
         } else {
            return 0;
         }
      }
   }

   private static class ArrayIterator implements IntListIterator {
      private final int[] array;
      private final int offset;
      private final int length;
      private int curr;

      public ArrayIterator(int[] array, int offset, int length) {
         this.array = array;
         this.offset = offset;
         this.length = length;
      }

      public boolean hasNext() {
         return this.curr < this.length;
      }

      public boolean hasPrevious() {
         return this.curr > 0;
      }

      public int nextInt() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + this.curr++];
         }
      }

      public int previousInt() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + --this.curr];
         }
      }

      public void forEachRemaining(java.util.function.IntConsumer action) {
         Objects.requireNonNull(action);

         while(this.curr < this.length) {
            action.accept(this.array[this.offset + this.curr]);
            ++this.curr;
         }

      }

      public int skip(int n) {
         if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (n <= this.length - this.curr) {
            this.curr += n;
            return n;
         } else {
            n = this.length - this.curr;
            this.curr = this.length;
            return n;
         }
      }

      public int back(int n) {
         if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (n <= this.curr) {
            this.curr -= n;
            return n;
         } else {
            n = this.curr;
            this.curr = 0;
            return n;
         }
      }

      public int nextIndex() {
         return this.curr;
      }

      public int previousIndex() {
         return this.curr - 1;
      }
   }

   private static class PrimitiveIteratorWrapper implements IntIterator {
      final OfInt i;

      public PrimitiveIteratorWrapper(OfInt i) {
         this.i = i;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public void remove() {
         this.i.remove();
      }

      public int nextInt() {
         return this.i.nextInt();
      }

      public void forEachRemaining(java.util.function.IntConsumer action) {
         this.i.forEachRemaining(action);
      }
   }

   private static class IteratorWrapper implements IntIterator {
      final Iterator<Integer> i;

      public IteratorWrapper(Iterator<Integer> i) {
         this.i = i;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public void remove() {
         this.i.remove();
      }

      public int nextInt() {
         return (Integer)this.i.next();
      }

      public void forEachRemaining(IntConsumer action) {
         this.i.forEachRemaining(action);
      }

      public void forEachRemaining(java.util.function.IntConsumer action) {
         Objects.requireNonNull(action);
         Iterator var10000 = this.i;
         Consumer var10001;
         if (action instanceof Consumer) {
            var10001 = (Consumer)action;
         } else {
            Objects.requireNonNull(action);
            var10001 = action::accept;
         }

         var10000.forEachRemaining(var10001);
      }

      /** @deprecated */
      @Deprecated
      public void forEachRemaining(Consumer<? super Integer> action) {
         this.i.forEachRemaining(action);
      }
   }

   private static class ListIteratorWrapper implements IntListIterator {
      final ListIterator<Integer> i;

      public ListIteratorWrapper(ListIterator<Integer> i) {
         this.i = i;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }

      public int nextIndex() {
         return this.i.nextIndex();
      }

      public int previousIndex() {
         return this.i.previousIndex();
      }

      public void set(int k) {
         this.i.set(k);
      }

      public void add(int k) {
         this.i.add(k);
      }

      public void remove() {
         this.i.remove();
      }

      public int nextInt() {
         return (Integer)this.i.next();
      }

      public int previousInt() {
         return (Integer)this.i.previous();
      }

      public void forEachRemaining(IntConsumer action) {
         this.i.forEachRemaining(action);
      }

      public void forEachRemaining(java.util.function.IntConsumer action) {
         Objects.requireNonNull(action);
         ListIterator var10000 = this.i;
         Consumer var10001;
         if (action instanceof Consumer) {
            var10001 = (Consumer)action;
         } else {
            Objects.requireNonNull(action);
            var10001 = action::accept;
         }

         var10000.forEachRemaining(var10001);
      }

      /** @deprecated */
      @Deprecated
      public void forEachRemaining(Consumer<? super Integer> action) {
         this.i.forEachRemaining(action);
      }
   }

   private static class IntervalIterator implements IntListIterator {
      private final int from;
      private final int to;
      int curr;

      public IntervalIterator(int from, int to) {
         this.from = this.curr = from;
         this.to = to;
      }

      public boolean hasNext() {
         return this.curr < this.to;
      }

      public boolean hasPrevious() {
         return this.curr > this.from;
      }

      public int nextInt() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.curr++;
         }
      }

      public int previousInt() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            return --this.curr;
         }
      }

      public void forEachRemaining(java.util.function.IntConsumer action) {
         Objects.requireNonNull(action);

         while(this.curr < this.to) {
            action.accept(this.curr);
            ++this.curr;
         }

      }

      public int nextIndex() {
         return this.curr - this.from;
      }

      public int previousIndex() {
         return this.curr - this.from - 1;
      }

      public int skip(int n) {
         if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (this.curr + n <= this.to) {
            this.curr += n;
            return n;
         } else {
            n = this.to - this.curr;
            this.curr = this.to;
            return n;
         }
      }

      public int back(int n) {
         if (this.curr - n >= this.from) {
            this.curr -= n;
            return n;
         } else {
            n = this.curr - this.from;
            this.curr = this.from;
            return n;
         }
      }
   }

   private static class IteratorConcatenator implements IntIterator {
      final IntIterator[] a;
      int offset;
      int length;
      int lastOffset = -1;

      public IteratorConcatenator(IntIterator[] a, int offset, int length) {
         this.a = a;
         this.offset = offset;
         this.length = length;
         this.advance();
      }

      private void advance() {
         while(this.length != 0 && !this.a[this.offset].hasNext()) {
            --this.length;
            ++this.offset;
         }

      }

      public boolean hasNext() {
         return this.length > 0;
      }

      public int nextInt() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            int next = this.a[this.lastOffset = this.offset].nextInt();
            this.advance();
            return next;
         }
      }

      public void forEachRemaining(java.util.function.IntConsumer action) {
         while(this.length > 0) {
            this.a[this.lastOffset = this.offset].forEachRemaining((java.util.function.IntConsumer)action);
            this.advance();
         }

      }

      /** @deprecated */
      @Deprecated
      public void forEachRemaining(Consumer<? super Integer> action) {
         while(this.length > 0) {
            this.a[this.lastOffset = this.offset].forEachRemaining(action);
            this.advance();
         }

      }

      public void remove() {
         if (this.lastOffset == -1) {
            throw new IllegalStateException();
         } else {
            this.a[this.lastOffset].remove();
         }
      }

      public int skip(int n) {
         if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else {
            this.lastOffset = -1;

            int skipped;
            for(skipped = 0; skipped < n && this.length != 0; ++this.offset) {
               skipped += this.a[this.offset].skip(n - skipped);
               if (this.a[this.offset].hasNext()) {
                  break;
               }

               --this.length;
            }

            return skipped;
         }
      }
   }

   public static class EmptyIterator implements IntListIterator, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyIterator() {
      }

      public boolean hasNext() {
         return false;
      }

      public boolean hasPrevious() {
         return false;
      }

      public int nextInt() {
         throw new NoSuchElementException();
      }

      public int previousInt() {
         throw new NoSuchElementException();
      }

      public int nextIndex() {
         return 0;
      }

      public int previousIndex() {
         return -1;
      }

      public int skip(int n) {
         return 0;
      }

      public int back(int n) {
         return 0;
      }

      public void forEachRemaining(java.util.function.IntConsumer action) {
      }

      /** @deprecated */
      @Deprecated
      public void forEachRemaining(Consumer<? super Integer> action) {
      }

      public Object clone() {
         return IntIterators.EMPTY_ITERATOR;
      }

      private Object readResolve() {
         return IntIterators.EMPTY_ITERATOR;
      }
   }

   public abstract static class AbstractIndexBasedListIterator extends IntIterators.AbstractIndexBasedIterator implements IntListIterator {
      protected AbstractIndexBasedListIterator(int minPos, int initialPos) {
         super(minPos, initialPos);
      }

      protected abstract void add(int var1, int var2);

      protected abstract void set(int var1, int var2);

      public boolean hasPrevious() {
         return this.pos > this.minPos;
      }

      public int previousInt() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            return this.get(this.lastReturned = --this.pos);
         }
      }

      public int nextIndex() {
         return this.pos;
      }

      public int previousIndex() {
         return this.pos - 1;
      }

      public void add(int k) {
         this.add(this.pos++, k);
         this.lastReturned = -1;
      }

      public void set(int k) {
         if (this.lastReturned == -1) {
            throw new IllegalStateException();
         } else {
            this.set(this.lastReturned, k);
         }
      }

      public int back(int n) {
         if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else {
            int remaining = this.pos - this.minPos;
            if (n < remaining) {
               this.pos -= n;
            } else {
               n = remaining;
               this.pos = this.minPos;
            }

            this.lastReturned = this.pos;
            return n;
         }
      }
   }

   public abstract static class AbstractIndexBasedIterator extends AbstractIntIterator {
      protected final int minPos;
      protected int pos;
      protected int lastReturned;

      protected AbstractIndexBasedIterator(int minPos, int initialPos) {
         this.minPos = minPos;
         this.pos = initialPos;
      }

      protected abstract int get(int var1);

      protected abstract void remove(int var1);

      protected abstract int getMaxPos();

      public boolean hasNext() {
         return this.pos < this.getMaxPos();
      }

      public int nextInt() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.get(this.lastReturned = this.pos++);
         }
      }

      public void remove() {
         if (this.lastReturned == -1) {
            throw new IllegalStateException();
         } else {
            this.remove(this.lastReturned);
            if (this.lastReturned < this.pos) {
               --this.pos;
            }

            this.lastReturned = -1;
         }
      }

      public void forEachRemaining(java.util.function.IntConsumer action) {
         while(this.pos < this.getMaxPos()) {
            action.accept(this.get(this.lastReturned = this.pos++));
         }

      }

      public int skip(int n) {
         if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else {
            int max = this.getMaxPos();
            int remaining = max - this.pos;
            if (n < remaining) {
               this.pos += n;
            } else {
               n = remaining;
               this.pos = max;
            }

            this.lastReturned = this.pos - 1;
            return n;
         }
      }
   }
}
