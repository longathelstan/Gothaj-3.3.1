package com.viaversion.viaversion.libs.fastutil.ints;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;

public abstract class AbstractIntList extends AbstractIntCollection implements IntList, IntStack {
   protected AbstractIntList() {
   }

   protected void ensureIndex(int index) {
      if (index < 0) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
      } else if (index > this.size()) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size() + ")");
      }
   }

   protected void ensureRestrictedIndex(int index) {
      if (index < 0) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
      } else if (index >= this.size()) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size() + ")");
      }
   }

   public void add(int index, int k) {
      throw new UnsupportedOperationException();
   }

   public boolean add(int k) {
      this.add(this.size(), k);
      return true;
   }

   public int removeInt(int i) {
      throw new UnsupportedOperationException();
   }

   public int set(int index, int k) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(int index, Collection<? extends Integer> c) {
      if (c instanceof IntCollection) {
         return this.addAll(index, (IntCollection)c);
      } else {
         this.ensureIndex(index);
         Iterator<? extends Integer> i = c.iterator();
         boolean retVal = i.hasNext();

         while(i.hasNext()) {
            this.add(index++, (Integer)i.next());
         }

         return retVal;
      }
   }

   public boolean addAll(Collection<? extends Integer> c) {
      return this.addAll(this.size(), c);
   }

   public IntListIterator iterator() {
      return this.listIterator();
   }

   public IntListIterator listIterator() {
      return this.listIterator(0);
   }

   public IntListIterator listIterator(int index) {
      this.ensureIndex(index);
      return new IntIterators.AbstractIndexBasedListIterator(0, index) {
         protected final int get(int i) {
            return AbstractIntList.this.getInt(i);
         }

         protected final void add(int i, int k) {
            AbstractIntList.this.add(i, k);
         }

         protected final void set(int i, int k) {
            AbstractIntList.this.set(i, k);
         }

         protected final void remove(int i) {
            AbstractIntList.this.removeInt(i);
         }

         protected final int getMaxPos() {
            return AbstractIntList.this.size();
         }
      };
   }

   public boolean contains(int k) {
      return this.indexOf(k) >= 0;
   }

   public int indexOf(int k) {
      IntListIterator i = this.listIterator();

      int e;
      do {
         if (!i.hasNext()) {
            return -1;
         }

         e = i.nextInt();
      } while(k != e);

      return i.previousIndex();
   }

   public int lastIndexOf(int k) {
      IntListIterator i = this.listIterator(this.size());

      int e;
      do {
         if (!i.hasPrevious()) {
            return -1;
         }

         e = i.previousInt();
      } while(k != e);

      return i.nextIndex();
   }

   public void size(int size) {
      int i = this.size();
      if (size > i) {
         while(i++ < size) {
            this.add(0);
         }
      } else {
         while(i-- != size) {
            this.removeInt(i);
         }
      }

   }

   public IntList subList(int from, int to) {
      this.ensureIndex(from);
      this.ensureIndex(to);
      if (from > to) {
         throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         return (IntList)(this instanceof RandomAccess ? new AbstractIntList.IntRandomAccessSubList(this, from, to) : new AbstractIntList.IntSubList(this, from, to));
      }
   }

   public void forEach(java.util.function.IntConsumer action) {
      if (this instanceof RandomAccess) {
         int i = 0;

         for(int max = this.size(); i < max; ++i) {
            action.accept(this.getInt(i));
         }
      } else {
         IntList.super.forEach(action);
      }

   }

   public void removeElements(int from, int to) {
      this.ensureIndex(to);
      IntListIterator i = this.listIterator(from);
      int n = to - from;
      if (n < 0) {
         throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         while(n-- != 0) {
            i.nextInt();
            i.remove();
         }

      }
   }

   public void addElements(int index, int[] a, int offset, int length) {
      this.ensureIndex(index);
      IntArrays.ensureOffsetLength(a, offset, length);
      if (this instanceof RandomAccess) {
         while(length-- != 0) {
            this.add(index++, a[offset++]);
         }
      } else {
         IntListIterator iter = this.listIterator(index);

         while(length-- != 0) {
            iter.add(a[offset++]);
         }
      }

   }

   public void addElements(int index, int[] a) {
      this.addElements(index, a, 0, a.length);
   }

   public void getElements(int from, int[] a, int offset, int length) {
      this.ensureIndex(from);
      IntArrays.ensureOffsetLength(a, offset, length);
      if (from + length > this.size()) {
         throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size() + ")");
      } else {
         if (this instanceof RandomAccess) {
            for(int var5 = from; length-- != 0; a[offset++] = this.getInt(var5++)) {
            }
         } else {
            for(IntListIterator i = this.listIterator(from); length-- != 0; a[offset++] = i.nextInt()) {
            }
         }

      }
   }

   public void setElements(int index, int[] a, int offset, int length) {
      this.ensureIndex(index);
      IntArrays.ensureOffsetLength(a, offset, length);
      if (index + length > this.size()) {
         throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
      } else {
         if (this instanceof RandomAccess) {
            for(int i = 0; i < length; ++i) {
               this.set(i + index, a[i + offset]);
            }
         } else {
            IntListIterator iter = this.listIterator(index);
            int i = 0;

            while(i < length) {
               iter.nextInt();
               iter.set(a[offset + i++]);
            }
         }

      }
   }

   public void clear() {
      this.removeElements(0, this.size());
   }

   public int hashCode() {
      IntIterator i = this.iterator();
      int h = 1;

      int k;
      for(int var3 = this.size(); var3-- != 0; h = 31 * h + k) {
         k = i.nextInt();
      }

      return h;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof List)) {
         return false;
      } else {
         List<?> l = (List)o;
         int s = this.size();
         if (s != l.size()) {
            return false;
         } else {
            IntListIterator i1;
            if (l instanceof IntList) {
               i1 = this.listIterator();
               IntListIterator i2 = ((IntList)l).listIterator();

               do {
                  if (s-- == 0) {
                     return true;
                  }
               } while(i1.nextInt() == i2.nextInt());

               return false;
            } else {
               i1 = this.listIterator();
               ListIterator i2 = l.listIterator();

               do {
                  if (s-- == 0) {
                     return true;
                  }
               } while(Objects.equals(i1.next(), i2.next()));

               return false;
            }
         }
      }
   }

   public int compareTo(List<? extends Integer> l) {
      if (l == this) {
         return 0;
      } else {
         IntListIterator i1;
         int r;
         if (l instanceof IntList) {
            i1 = this.listIterator();
            IntListIterator i2 = ((IntList)l).listIterator();

            while(i1.hasNext() && i2.hasNext()) {
               int e1 = i1.nextInt();
               int e2 = i2.nextInt();
               if ((r = Integer.compare(e1, e2)) != 0) {
                  return r;
               }
            }

            return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
         } else {
            i1 = this.listIterator();
            ListIterator i2 = l.listIterator();

            while(i1.hasNext() && i2.hasNext()) {
               if ((r = ((Comparable)i1.next()).compareTo(i2.next())) != 0) {
                  return r;
               }
            }

            return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
         }
      }
   }

   public void push(int o) {
      this.add(o);
   }

   public int popInt() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.removeInt(this.size() - 1);
      }
   }

   public int topInt() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getInt(this.size() - 1);
      }
   }

   public int peekInt(int i) {
      return this.getInt(this.size() - 1 - i);
   }

   public boolean rem(int k) {
      int index = this.indexOf(k);
      if (index == -1) {
         return false;
      } else {
         this.removeInt(index);
         return true;
      }
   }

   public int[] toIntArray() {
      int size = this.size();
      if (size == 0) {
         return IntArrays.EMPTY_ARRAY;
      } else {
         int[] ret = new int[size];
         this.getElements(0, ret, 0, size);
         return ret;
      }
   }

   public int[] toArray(int[] a) {
      int size = this.size();
      if (a.length < size) {
         a = Arrays.copyOf(a, size);
      }

      this.getElements(0, a, 0, size);
      return a;
   }

   public boolean addAll(int index, IntCollection c) {
      this.ensureIndex(index);
      IntIterator i = c.iterator();
      boolean retVal = i.hasNext();

      while(i.hasNext()) {
         this.add(index++, i.nextInt());
      }

      return retVal;
   }

   public boolean addAll(IntCollection c) {
      return this.addAll(this.size(), c);
   }

   public final void replaceAll(IntUnaryOperator operator) {
      this.replaceAll(operator);
   }

   public String toString() {
      StringBuilder s = new StringBuilder();
      IntIterator i = this.iterator();
      int n = this.size();
      boolean first = true;
      s.append("[");

      while(n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         int k = i.nextInt();
         s.append(String.valueOf(k));
      }

      s.append("]");
      return s.toString();
   }

   public static class IntRandomAccessSubList extends AbstractIntList.IntSubList implements RandomAccess {
      private static final long serialVersionUID = -107070782945191929L;

      public IntRandomAccessSubList(IntList l, int from, int to) {
         super(l, from, to);
      }

      public IntList subList(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractIntList.IntRandomAccessSubList(this, from, to);
         }
      }
   }

   public static class IntSubList extends AbstractIntList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntList l;
      protected final int from;
      protected int to;

      public IntSubList(IntList l, int from, int to) {
         this.l = l;
         this.from = from;
         this.to = to;
      }

      private boolean assertRange() {
         assert this.from <= this.l.size();

         assert this.to <= this.l.size();

         assert this.to >= this.from;

         return true;
      }

      public boolean add(int k) {
         this.l.add(this.to, k);
         ++this.to;

         assert this.assertRange();

         return true;
      }

      public void add(int index, int k) {
         this.ensureIndex(index);
         this.l.add(this.from + index, k);
         ++this.to;

         assert this.assertRange();

      }

      public boolean addAll(int index, Collection<? extends Integer> c) {
         this.ensureIndex(index);
         this.to += c.size();
         return this.l.addAll(this.from + index, (Collection)c);
      }

      public int getInt(int index) {
         this.ensureRestrictedIndex(index);
         return this.l.getInt(this.from + index);
      }

      public int removeInt(int index) {
         this.ensureRestrictedIndex(index);
         --this.to;
         return this.l.removeInt(this.from + index);
      }

      public int set(int index, int k) {
         this.ensureRestrictedIndex(index);
         return this.l.set(this.from + index, k);
      }

      public int size() {
         return this.to - this.from;
      }

      public void getElements(int from, int[] a, int offset, int length) {
         this.ensureIndex(from);
         if (from + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + from + length + ") is greater than list size (" + this.size() + ")");
         } else {
            this.l.getElements(this.from + from, a, offset, length);
         }
      }

      public void removeElements(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         this.l.removeElements(this.from + from, this.from + to);
         this.to -= to - from;

         assert this.assertRange();

      }

      public void addElements(int index, int[] a, int offset, int length) {
         this.ensureIndex(index);
         this.l.addElements(this.from + index, a, offset, length);
         this.to += length;

         assert this.assertRange();

      }

      public void setElements(int index, int[] a, int offset, int length) {
         this.ensureIndex(index);
         this.l.setElements(this.from + index, a, offset, length);

         assert this.assertRange();

      }

      public IntListIterator listIterator(int index) {
         this.ensureIndex(index);
         return (IntListIterator)(this.l instanceof RandomAccess ? new AbstractIntList.IntSubList.RandomAccessIter(index) : new AbstractIntList.IntSubList.ParentWrappingIter(this.l.listIterator(index + this.from)));
      }

      public IntSpliterator spliterator() {
         return (IntSpliterator)(this.l instanceof RandomAccess ? new AbstractIntList.IndexBasedSpliterator(this.l, this.from, this.to) : super.spliterator());
      }

      public IntList subList(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractIntList.IntSubList(this, from, to);
         }
      }

      public boolean rem(int k) {
         int index = this.indexOf(k);
         if (index == -1) {
            return false;
         } else {
            --this.to;
            this.l.removeInt(this.from + index);

            assert this.assertRange();

            return true;
         }
      }

      public boolean addAll(int index, IntCollection c) {
         this.ensureIndex(index);
         return super.addAll(index, c);
      }

      public boolean addAll(int index, IntList l) {
         this.ensureIndex(index);
         return super.addAll(index, (IntList)l);
      }

      private final class RandomAccessIter extends IntIterators.AbstractIndexBasedListIterator {
         RandomAccessIter(int pos) {
            super(0, pos);
         }

         protected final int get(int i) {
            return IntSubList.this.l.getInt(IntSubList.this.from + i);
         }

         protected final void add(int i, int k) {
            IntSubList.this.add(i, k);
         }

         protected final void set(int i, int k) {
            IntSubList.this.set(i, k);
         }

         protected final void remove(int i) {
            IntSubList.this.removeInt(i);
         }

         protected final int getMaxPos() {
            return IntSubList.this.to - IntSubList.this.from;
         }

         public void add(int k) {
            super.add(k);

            assert IntSubList.this.assertRange();

         }

         public void remove() {
            super.remove();

            assert IntSubList.this.assertRange();

         }
      }

      private class ParentWrappingIter implements IntListIterator {
         private IntListIterator parent;

         ParentWrappingIter(IntListIterator parent) {
            this.parent = parent;
         }

         public int nextIndex() {
            return this.parent.nextIndex() - IntSubList.this.from;
         }

         public int previousIndex() {
            return this.parent.previousIndex() - IntSubList.this.from;
         }

         public boolean hasNext() {
            return this.parent.nextIndex() < IntSubList.this.to;
         }

         public boolean hasPrevious() {
            return this.parent.previousIndex() >= IntSubList.this.from;
         }

         public int nextInt() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return this.parent.nextInt();
            }
         }

         public int previousInt() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return this.parent.previousInt();
            }
         }

         public void add(int k) {
            this.parent.add(k);
         }

         public void set(int k) {
            this.parent.set(k);
         }

         public void remove() {
            this.parent.remove();
         }

         public int back(int n) {
            if (n < 0) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               int currentPos = this.parent.previousIndex();
               int parentNewPos = currentPos - n;
               if (parentNewPos < IntSubList.this.from - 1) {
                  parentNewPos = IntSubList.this.from - 1;
               }

               int toSkip = parentNewPos - currentPos;
               return this.parent.back(toSkip);
            }
         }

         public int skip(int n) {
            if (n < 0) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               int currentPos = this.parent.nextIndex();
               int parentNewPos = currentPos + n;
               if (parentNewPos > IntSubList.this.to) {
                  parentNewPos = IntSubList.this.to;
               }

               int toSkip = parentNewPos - currentPos;
               return this.parent.skip(toSkip);
            }
         }
      }
   }

   static final class IndexBasedSpliterator extends IntSpliterators.LateBindingSizeIndexBasedSpliterator {
      final IntList l;

      IndexBasedSpliterator(IntList l, int pos) {
         super(pos);
         this.l = l;
      }

      IndexBasedSpliterator(IntList l, int pos, int maxPos) {
         super(pos, maxPos);
         this.l = l;
      }

      protected final int getMaxPosFromBackingStore() {
         return this.l.size();
      }

      protected final int get(int i) {
         return this.l.getInt(i);
      }

      protected final AbstractIntList.IndexBasedSpliterator makeForSplit(int pos, int maxPos) {
         return new AbstractIntList.IndexBasedSpliterator(this.l, pos, maxPos);
      }
   }
}
