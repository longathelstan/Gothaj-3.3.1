package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.Stack;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Consumer;

public abstract class AbstractObjectList<K> extends AbstractObjectCollection<K> implements ObjectList<K>, Stack<K> {
   protected AbstractObjectList() {
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

   public void add(int index, K k) {
      throw new UnsupportedOperationException();
   }

   public boolean add(K k) {
      this.add(this.size(), k);
      return true;
   }

   public K remove(int i) {
      throw new UnsupportedOperationException();
   }

   public K set(int index, K k) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(int index, Collection<? extends K> c) {
      this.ensureIndex(index);
      Iterator<? extends K> i = c.iterator();
      boolean retVal = i.hasNext();

      while(i.hasNext()) {
         this.add(index++, i.next());
      }

      return retVal;
   }

   public boolean addAll(Collection<? extends K> c) {
      return this.addAll(this.size(), c);
   }

   public ObjectListIterator<K> iterator() {
      return this.listIterator();
   }

   public ObjectListIterator<K> listIterator() {
      return this.listIterator(0);
   }

   public ObjectListIterator<K> listIterator(int index) {
      this.ensureIndex(index);
      return new ObjectIterators.AbstractIndexBasedListIterator<K>(0, index) {
         protected final K get(int i) {
            return AbstractObjectList.this.get(i);
         }

         protected final void add(int i, K k) {
            AbstractObjectList.this.add(i, k);
         }

         protected final void set(int i, K k) {
            AbstractObjectList.this.set(i, k);
         }

         protected final void remove(int i) {
            AbstractObjectList.this.remove(i);
         }

         protected final int getMaxPos() {
            return AbstractObjectList.this.size();
         }
      };
   }

   public boolean contains(Object k) {
      return this.indexOf(k) >= 0;
   }

   public int indexOf(Object k) {
      ObjectListIterator i = this.listIterator();

      Object e;
      do {
         if (!i.hasNext()) {
            return -1;
         }

         e = i.next();
      } while(!Objects.equals(k, e));

      return i.previousIndex();
   }

   public int lastIndexOf(Object k) {
      ObjectListIterator i = this.listIterator(this.size());

      Object e;
      do {
         if (!i.hasPrevious()) {
            return -1;
         }

         e = i.previous();
      } while(!Objects.equals(k, e));

      return i.nextIndex();
   }

   public void size(int size) {
      int i = this.size();
      if (size > i) {
         while(i++ < size) {
            this.add((Object)null);
         }
      } else {
         while(i-- != size) {
            this.remove(i);
         }
      }

   }

   public ObjectList<K> subList(int from, int to) {
      this.ensureIndex(from);
      this.ensureIndex(to);
      if (from > to) {
         throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         return (ObjectList)(this instanceof RandomAccess ? new AbstractObjectList.ObjectRandomAccessSubList(this, from, to) : new AbstractObjectList.ObjectSubList(this, from, to));
      }
   }

   public void forEach(Consumer<? super K> action) {
      if (this instanceof RandomAccess) {
         int i = 0;

         for(int max = this.size(); i < max; ++i) {
            action.accept(this.get(i));
         }
      } else {
         ObjectList.super.forEach(action);
      }

   }

   public void removeElements(int from, int to) {
      this.ensureIndex(to);
      ObjectListIterator<K> i = this.listIterator(from);
      int n = to - from;
      if (n < 0) {
         throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         while(n-- != 0) {
            i.next();
            i.remove();
         }

      }
   }

   public void addElements(int index, K[] a, int offset, int length) {
      this.ensureIndex(index);
      ObjectArrays.ensureOffsetLength(a, offset, length);
      if (this instanceof RandomAccess) {
         while(length-- != 0) {
            this.add(index++, a[offset++]);
         }
      } else {
         ObjectListIterator iter = this.listIterator(index);

         while(length-- != 0) {
            iter.add(a[offset++]);
         }
      }

   }

   public void addElements(int index, K[] a) {
      this.addElements(index, a, 0, a.length);
   }

   public void getElements(int from, Object[] a, int offset, int length) {
      this.ensureIndex(from);
      ObjectArrays.ensureOffsetLength(a, offset, length);
      if (from + length > this.size()) {
         throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size() + ")");
      } else {
         if (this instanceof RandomAccess) {
            for(int var5 = from; length-- != 0; a[offset++] = this.get(var5++)) {
            }
         } else {
            for(ObjectListIterator i = this.listIterator(from); length-- != 0; a[offset++] = i.next()) {
            }
         }

      }
   }

   public void setElements(int index, K[] a, int offset, int length) {
      this.ensureIndex(index);
      ObjectArrays.ensureOffsetLength(a, offset, length);
      if (index + length > this.size()) {
         throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
      } else {
         if (this instanceof RandomAccess) {
            for(int i = 0; i < length; ++i) {
               this.set(i + index, a[i + offset]);
            }
         } else {
            ObjectListIterator<K> iter = this.listIterator(index);
            int i = 0;

            while(i < length) {
               iter.next();
               iter.set(a[offset + i++]);
            }
         }

      }
   }

   public void clear() {
      this.removeElements(0, this.size());
   }

   public Object[] toArray() {
      int size = this.size();
      if (size == 0) {
         return ObjectArrays.EMPTY_ARRAY;
      } else {
         Object[] ret = new Object[size];
         this.getElements(0, ret, 0, size);
         return ret;
      }
   }

   public <T> T[] toArray(T[] a) {
      int size = this.size();
      if (a.length < size) {
         a = Arrays.copyOf(a, size);
      }

      this.getElements(0, a, 0, size);
      if (a.length > size) {
         a[size] = null;
      }

      return a;
   }

   public int hashCode() {
      ObjectIterator<K> i = this.iterator();
      int h = 1;

      Object k;
      for(int var3 = this.size(); var3-- != 0; h = 31 * h + (k == null ? 0 : k.hashCode())) {
         k = i.next();
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
            ListIterator<?> i1 = this.listIterator();
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

   public int compareTo(List<? extends K> l) {
      if (l == this) {
         return 0;
      } else {
         ObjectListIterator i1;
         int r;
         if (l instanceof ObjectList) {
            i1 = this.listIterator();
            ObjectListIterator i2 = ((ObjectList)l).listIterator();

            while(i1.hasNext() && i2.hasNext()) {
               K e1 = i1.next();
               K e2 = i2.next();
               if ((r = ((Comparable)e1).compareTo(e2)) != 0) {
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

   public void push(K o) {
      this.add(o);
   }

   public K pop() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.remove(this.size() - 1);
      }
   }

   public K top() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.get(this.size() - 1);
      }
   }

   public K peek(int i) {
      return this.get(this.size() - 1 - i);
   }

   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<K> i = this.iterator();
      int n = this.size();
      boolean first = true;
      s.append("[");

      while(n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         K k = i.next();
         if (this == k) {
            s.append("(this list)");
         } else {
            s.append(String.valueOf(k));
         }
      }

      s.append("]");
      return s.toString();
   }

   public static class ObjectRandomAccessSubList<K> extends AbstractObjectList.ObjectSubList<K> implements RandomAccess {
      private static final long serialVersionUID = -107070782945191929L;

      public ObjectRandomAccessSubList(ObjectList<K> l, int from, int to) {
         super(l, from, to);
      }

      public ObjectList<K> subList(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractObjectList.ObjectRandomAccessSubList(this, from, to);
         }
      }
   }

   public static class ObjectSubList<K> extends AbstractObjectList<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ObjectList<K> l;
      protected final int from;
      protected int to;

      public ObjectSubList(ObjectList<K> l, int from, int to) {
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

      public boolean add(K k) {
         this.l.add(this.to, k);
         ++this.to;

         assert this.assertRange();

         return true;
      }

      public void add(int index, K k) {
         this.ensureIndex(index);
         this.l.add(this.from + index, k);
         ++this.to;

         assert this.assertRange();

      }

      public boolean addAll(int index, Collection<? extends K> c) {
         this.ensureIndex(index);
         this.to += c.size();
         return this.l.addAll(this.from + index, c);
      }

      public K get(int index) {
         this.ensureRestrictedIndex(index);
         return this.l.get(this.from + index);
      }

      public K remove(int index) {
         this.ensureRestrictedIndex(index);
         --this.to;
         return this.l.remove(this.from + index);
      }

      public K set(int index, K k) {
         this.ensureRestrictedIndex(index);
         return this.l.set(this.from + index, k);
      }

      public int size() {
         return this.to - this.from;
      }

      public void getElements(int from, Object[] a, int offset, int length) {
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

      public void addElements(int index, K[] a, int offset, int length) {
         this.ensureIndex(index);
         this.l.addElements(this.from + index, a, offset, length);
         this.to += length;

         assert this.assertRange();

      }

      public void setElements(int index, K[] a, int offset, int length) {
         this.ensureIndex(index);
         this.l.setElements(this.from + index, a, offset, length);

         assert this.assertRange();

      }

      public ObjectListIterator<K> listIterator(int index) {
         this.ensureIndex(index);
         return (ObjectListIterator)(this.l instanceof RandomAccess ? new AbstractObjectList.ObjectSubList.RandomAccessIter(index) : new AbstractObjectList.ObjectSubList.ParentWrappingIter(this.l.listIterator(index + this.from)));
      }

      public ObjectSpliterator<K> spliterator() {
         return (ObjectSpliterator)(this.l instanceof RandomAccess ? new AbstractObjectList.IndexBasedSpliterator(this.l, this.from, this.to) : super.spliterator());
      }

      public ObjectList<K> subList(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractObjectList.ObjectSubList(this, from, to);
         }
      }

      private final class RandomAccessIter extends ObjectIterators.AbstractIndexBasedListIterator<K> {
         RandomAccessIter(int pos) {
            super(0, pos);
         }

         protected final K get(int i) {
            return ObjectSubList.this.l.get(ObjectSubList.this.from + i);
         }

         protected final void add(int i, K k) {
            ObjectSubList.this.add(i, k);
         }

         protected final void set(int i, K k) {
            ObjectSubList.this.set(i, k);
         }

         protected final void remove(int i) {
            ObjectSubList.this.remove(i);
         }

         protected final int getMaxPos() {
            return ObjectSubList.this.to - ObjectSubList.this.from;
         }

         public void add(K k) {
            super.add(k);

            assert ObjectSubList.this.assertRange();

         }

         public void remove() {
            super.remove();

            assert ObjectSubList.this.assertRange();

         }
      }

      private class ParentWrappingIter implements ObjectListIterator<K> {
         private ObjectListIterator<K> parent;

         ParentWrappingIter(ObjectListIterator<K> parent) {
            this.parent = parent;
         }

         public int nextIndex() {
            return this.parent.nextIndex() - ObjectSubList.this.from;
         }

         public int previousIndex() {
            return this.parent.previousIndex() - ObjectSubList.this.from;
         }

         public boolean hasNext() {
            return this.parent.nextIndex() < ObjectSubList.this.to;
         }

         public boolean hasPrevious() {
            return this.parent.previousIndex() >= ObjectSubList.this.from;
         }

         public K next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return this.parent.next();
            }
         }

         public K previous() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return this.parent.previous();
            }
         }

         public void add(K k) {
            this.parent.add(k);
         }

         public void set(K k) {
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
               if (parentNewPos < ObjectSubList.this.from - 1) {
                  parentNewPos = ObjectSubList.this.from - 1;
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
               if (parentNewPos > ObjectSubList.this.to) {
                  parentNewPos = ObjectSubList.this.to;
               }

               int toSkip = parentNewPos - currentPos;
               return this.parent.skip(toSkip);
            }
         }
      }
   }

   static final class IndexBasedSpliterator<K> extends ObjectSpliterators.LateBindingSizeIndexBasedSpliterator<K> {
      final ObjectList<K> l;

      IndexBasedSpliterator(ObjectList<K> l, int pos) {
         super(pos);
         this.l = l;
      }

      IndexBasedSpliterator(ObjectList<K> l, int pos, int maxPos) {
         super(pos, maxPos);
         this.l = l;
      }

      protected final int getMaxPosFromBackingStore() {
         return this.l.size();
      }

      protected final K get(int i) {
         return this.l.get(i);
      }

      protected final AbstractObjectList.IndexBasedSpliterator<K> makeForSplit(int pos, int maxPos) {
         return new AbstractObjectList.IndexBasedSpliterator(this.l, pos, maxPos);
      }
   }
}
