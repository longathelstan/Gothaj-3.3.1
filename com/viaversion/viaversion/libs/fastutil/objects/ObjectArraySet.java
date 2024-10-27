package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class ObjectArraySet<K> extends AbstractObjectSet<K> implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient Object[] a;
   protected int size;

   public ObjectArraySet(Object[] a) {
      this.a = a;
      this.size = a.length;
   }

   public ObjectArraySet() {
      this.a = ObjectArrays.EMPTY_ARRAY;
   }

   public ObjectArraySet(int capacity) {
      this.a = new Object[capacity];
   }

   public ObjectArraySet(ObjectCollection<K> c) {
      this(c.size());
      this.addAll(c);
   }

   public ObjectArraySet(Collection<? extends K> c) {
      this(c.size());
      this.addAll(c);
   }

   public ObjectArraySet(ObjectSet<K> c) {
      this(c.size());
      int i = 0;

      for(ObjectIterator var3 = c.iterator(); var3.hasNext(); ++i) {
         Object x = var3.next();
         this.a[i] = x;
      }

      this.size = i;
   }

   public ObjectArraySet(Set<? extends K> c) {
      this(c.size());
      int i = 0;

      for(Iterator var3 = c.iterator(); var3.hasNext(); ++i) {
         K x = var3.next();
         this.a[i] = x;
      }

      this.size = i;
   }

   public ObjectArraySet(Object[] a, int size) {
      this.a = a;
      this.size = size;
      if (size > a.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a.length + ")");
      }
   }

   public static <K> ObjectArraySet<K> of() {
      return ofUnchecked();
   }

   public static <K> ObjectArraySet<K> of(K e) {
      return ofUnchecked(e);
   }

   @SafeVarargs
   public static <K> ObjectArraySet<K> of(K... a) {
      if (a.length == 2) {
         if (Objects.equals(a[0], a[1])) {
            throw new IllegalArgumentException("Duplicate element: " + a[1]);
         }
      } else if (a.length > 2) {
         ObjectOpenHashSet.of(a);
      }

      return ofUnchecked(a);
   }

   public static <K> ObjectArraySet<K> ofUnchecked() {
      return new ObjectArraySet();
   }

   @SafeVarargs
   public static <K> ObjectArraySet<K> ofUnchecked(K... a) {
      return new ObjectArraySet(a);
   }

   private int findKey(Object o) {
      int i = this.size;

      do {
         if (i-- == 0) {
            return -1;
         }
      } while(!Objects.equals(this.a[i], o));

      return i;
   }

   public ObjectIterator<K> iterator() {
      return new ObjectIterator<K>() {
         int next = 0;

         public boolean hasNext() {
            return this.next < ObjectArraySet.this.size;
         }

         public K next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return ObjectArraySet.this.a[this.next++];
            }
         }

         public void remove() {
            int tail = ObjectArraySet.this.size-- - this.next--;
            System.arraycopy(ObjectArraySet.this.a, this.next + 1, ObjectArraySet.this.a, this.next, tail);
            ObjectArraySet.this.a[ObjectArraySet.this.size] = null;
         }

         public int skip(int n) {
            if (n < 0) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               int remaining = ObjectArraySet.this.size - this.next;
               if (n < remaining) {
                  this.next += n;
                  return n;
               } else {
                  this.next = ObjectArraySet.this.size;
                  return remaining;
               }
            }
         }
      };
   }

   public ObjectSpliterator<K> spliterator() {
      return new ObjectArraySet.Spliterator();
   }

   public boolean contains(Object k) {
      return this.findKey(k) != -1;
   }

   public int size() {
      return this.size;
   }

   public boolean remove(Object k) {
      int pos = this.findKey(k);
      if (pos == -1) {
         return false;
      } else {
         int tail = this.size - pos - 1;

         for(int i = 0; i < tail; ++i) {
            this.a[pos + i] = this.a[pos + i + 1];
         }

         --this.size;
         this.a[this.size] = null;
         return true;
      }
   }

   public boolean add(K k) {
      int pos = this.findKey(k);
      if (pos != -1) {
         return false;
      } else {
         if (this.size == this.a.length) {
            Object[] b = new Object[this.size == 0 ? 2 : this.size * 2];

            for(int i = this.size; i-- != 0; b[i] = this.a[i]) {
            }

            this.a = b;
         }

         this.a[this.size++] = k;
         return true;
      }
   }

   public void clear() {
      Arrays.fill(this.a, 0, this.size, (Object)null);
      this.size = 0;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public Object[] toArray() {
      int size = this.size();
      return size == 0 ? ObjectArrays.EMPTY_ARRAY : Arrays.copyOf(this.a, size, Object[].class);
   }

   public <T> T[] toArray(T[] a) {
      if (a == null) {
         a = new Object[this.size];
      } else if (a.length < this.size) {
         a = (Object[])Array.newInstance(a.getClass().getComponentType(), this.size);
      }

      System.arraycopy(this.a, 0, a, 0, this.size);
      if (a.length > this.size) {
         a[this.size] = null;
      }

      return a;
   }

   public ObjectArraySet<K> clone() {
      ObjectArraySet c;
      try {
         c = (ObjectArraySet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.a = (Object[])this.a.clone();
      return c;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();

      for(int i = 0; i < this.size; ++i) {
         s.writeObject(this.a[i]);
      }

   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.a = new Object[this.size];

      for(int i = 0; i < this.size; ++i) {
         this.a[i] = s.readObject();
      }

   }

   private final class Spliterator implements ObjectSpliterator<K> {
      boolean hasSplit;
      int pos;
      int max;

      public Spliterator() {
         this(0, ObjectArraySet.this.size, false);
      }

      private Spliterator(int pos, int max, boolean hasSplit) {
         this.hasSplit = false;

         assert pos <= max : "pos " + pos + " must be <= max " + max;

         this.pos = pos;
         this.max = max;
         this.hasSplit = hasSplit;
      }

      private int getWorkingMax() {
         return this.hasSplit ? this.max : ObjectArraySet.this.size;
      }

      public int characteristics() {
         return 16465;
      }

      public long estimateSize() {
         return (long)(this.getWorkingMax() - this.pos);
      }

      public boolean tryAdvance(Consumer<? super K> action) {
         if (this.pos >= this.getWorkingMax()) {
            return false;
         } else {
            action.accept(ObjectArraySet.this.a[this.pos++]);
            return true;
         }
      }

      public void forEachRemaining(Consumer<? super K> action) {
         for(int max = this.getWorkingMax(); this.pos < max; ++this.pos) {
            action.accept(ObjectArraySet.this.a[this.pos]);
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

      public ObjectSpliterator<K> trySplit() {
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
            return ObjectArraySet.this.new Spliterator(oldPos, myNewPos, true);
         }
      }
   }
}
