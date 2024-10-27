package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class IntArraySet extends AbstractIntSet implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient int[] a;
   protected int size;

   public IntArraySet(int[] a) {
      this.a = a;
      this.size = a.length;
   }

   public IntArraySet() {
      this.a = IntArrays.EMPTY_ARRAY;
   }

   public IntArraySet(int capacity) {
      this.a = new int[capacity];
   }

   public IntArraySet(IntCollection c) {
      this(c.size());
      this.addAll(c);
   }

   public IntArraySet(Collection<? extends Integer> c) {
      this(c.size());
      this.addAll(c);
   }

   public IntArraySet(IntSet c) {
      this(c.size());
      int i = 0;

      for(IntIterator var3 = c.iterator(); var3.hasNext(); ++i) {
         int x = (Integer)var3.next();
         this.a[i] = x;
      }

      this.size = i;
   }

   public IntArraySet(Set<? extends Integer> c) {
      this(c.size());
      int i = 0;

      for(Iterator var3 = c.iterator(); var3.hasNext(); ++i) {
         Integer x = (Integer)var3.next();
         this.a[i] = x;
      }

      this.size = i;
   }

   public IntArraySet(int[] a, int size) {
      this.a = a;
      this.size = size;
      if (size > a.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a.length + ")");
      }
   }

   public static IntArraySet of() {
      return ofUnchecked();
   }

   public static IntArraySet of(int e) {
      return ofUnchecked(e);
   }

   public static IntArraySet of(int... a) {
      if (a.length == 2) {
         if (a[0] == a[1]) {
            throw new IllegalArgumentException("Duplicate element: " + a[1]);
         }
      } else if (a.length > 2) {
         IntOpenHashSet.of(a);
      }

      return ofUnchecked(a);
   }

   public static IntArraySet ofUnchecked() {
      return new IntArraySet();
   }

   public static IntArraySet ofUnchecked(int... a) {
      return new IntArraySet(a);
   }

   private int findKey(int o) {
      int i = this.size;

      do {
         if (i-- == 0) {
            return -1;
         }
      } while(this.a[i] != o);

      return i;
   }

   public IntIterator iterator() {
      return new IntIterator() {
         int next = 0;

         public boolean hasNext() {
            return this.next < IntArraySet.this.size;
         }

         public int nextInt() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return IntArraySet.this.a[this.next++];
            }
         }

         public void remove() {
            int tail = IntArraySet.this.size-- - this.next--;
            System.arraycopy(IntArraySet.this.a, this.next + 1, IntArraySet.this.a, this.next, tail);
         }

         public int skip(int n) {
            if (n < 0) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               int remaining = IntArraySet.this.size - this.next;
               if (n < remaining) {
                  this.next += n;
                  return n;
               } else {
                  this.next = IntArraySet.this.size;
                  return remaining;
               }
            }
         }
      };
   }

   public IntSpliterator spliterator() {
      return new IntArraySet.Spliterator();
   }

   public boolean contains(int k) {
      return this.findKey(k) != -1;
   }

   public int size() {
      return this.size;
   }

   public boolean remove(int k) {
      int pos = this.findKey(k);
      if (pos == -1) {
         return false;
      } else {
         int tail = this.size - pos - 1;

         for(int i = 0; i < tail; ++i) {
            this.a[pos + i] = this.a[pos + i + 1];
         }

         --this.size;
         return true;
      }
   }

   public boolean add(int k) {
      int pos = this.findKey(k);
      if (pos != -1) {
         return false;
      } else {
         if (this.size == this.a.length) {
            int[] b = new int[this.size == 0 ? 2 : this.size * 2];

            for(int i = this.size; i-- != 0; b[i] = this.a[i]) {
            }

            this.a = b;
         }

         this.a[this.size++] = k;
         return true;
      }
   }

   public void clear() {
      this.size = 0;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public int[] toIntArray() {
      return this.size == 0 ? IntArrays.EMPTY_ARRAY : Arrays.copyOf(this.a, this.size);
   }

   public int[] toArray(int[] a) {
      if (a == null || a.length < this.size) {
         a = new int[this.size];
      }

      System.arraycopy(this.a, 0, a, 0, this.size);
      return a;
   }

   public IntArraySet clone() {
      IntArraySet c;
      try {
         c = (IntArraySet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.a = (int[])this.a.clone();
      return c;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();

      for(int i = 0; i < this.size; ++i) {
         s.writeInt(this.a[i]);
      }

   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.a = new int[this.size];

      for(int i = 0; i < this.size; ++i) {
         this.a[i] = s.readInt();
      }

   }

   private final class Spliterator implements IntSpliterator {
      boolean hasSplit;
      int pos;
      int max;

      public Spliterator() {
         this(0, IntArraySet.this.size, false);
      }

      private Spliterator(int pos, int max, boolean hasSplit) {
         this.hasSplit = false;

         assert pos <= max : "pos " + pos + " must be <= max " + max;

         this.pos = pos;
         this.max = max;
         this.hasSplit = hasSplit;
      }

      private int getWorkingMax() {
         return this.hasSplit ? this.max : IntArraySet.this.size;
      }

      public int characteristics() {
         return 16721;
      }

      public long estimateSize() {
         return (long)(this.getWorkingMax() - this.pos);
      }

      public boolean tryAdvance(java.util.function.IntConsumer action) {
         if (this.pos >= this.getWorkingMax()) {
            return false;
         } else {
            action.accept(IntArraySet.this.a[this.pos++]);
            return true;
         }
      }

      public void forEachRemaining(java.util.function.IntConsumer action) {
         for(int max = this.getWorkingMax(); this.pos < max; ++this.pos) {
            action.accept(IntArraySet.this.a[this.pos]);
         }

      }

      public long skip(long n) {
         if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else {
            int max = this.getWorkingMax();
            if (this.pos >= max) {
               return 0L;
            } else {
               int remaining = max - this.pos;
               if (n < (long)remaining) {
                  this.pos = SafeMath.safeLongToInt((long)this.pos + n);
                  return n;
               } else {
                  n = (long)remaining;
                  this.pos = max;
                  return n;
               }
            }
         }
      }

      public IntSpliterator trySplit() {
         int max = this.getWorkingMax();
         int retLen = max - this.pos >> 1;
         if (retLen <= 1) {
            return null;
         } else {
            this.max = max;
            int myNewPos = this.pos + retLen;
            int oldPos = this.pos;
            this.pos = myNewPos;
            this.hasSplit = true;
            return IntArraySet.this.new Spliterator(oldPos, myNewPos, true);
         }
      }
   }
}
