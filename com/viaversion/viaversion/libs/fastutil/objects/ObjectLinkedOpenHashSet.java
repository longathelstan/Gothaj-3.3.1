package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.Hash;
import com.viaversion.viaversion.libs.fastutil.HashCommon;
import com.viaversion.viaversion.libs.fastutil.Size64;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collector;

public class ObjectLinkedOpenHashSet<K> extends AbstractObjectSortedSet<K> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient K[] key;
   protected transient int mask;
   protected transient boolean containsNull;
   protected transient int first;
   protected transient int last;
   protected transient long[] link;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   private static final Collector<Object, ?, ObjectLinkedOpenHashSet<Object>> TO_SET_COLLECTOR = Collector.of(ObjectLinkedOpenHashSet::new, ObjectLinkedOpenHashSet::add, ObjectLinkedOpenHashSet::combine);
   private static final int SPLITERATOR_CHARACTERISTICS = 81;

   public ObjectLinkedOpenHashSet(int expected, float f) {
      this.first = -1;
      this.last = -1;
      if (!(f <= 0.0F) && !(f >= 1.0F)) {
         if (expected < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
         } else {
            this.f = f;
            this.minN = this.n = HashCommon.arraySize(expected, f);
            this.mask = this.n - 1;
            this.maxFill = HashCommon.maxFill(this.n, f);
            this.key = new Object[this.n + 1];
            this.link = new long[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
      }
   }

   public ObjectLinkedOpenHashSet(int expected) {
      this(expected, 0.75F);
   }

   public ObjectLinkedOpenHashSet() {
      this(16, 0.75F);
   }

   public ObjectLinkedOpenHashSet(Collection<? extends K> c, float f) {
      this(c.size(), f);
      this.addAll(c);
   }

   public ObjectLinkedOpenHashSet(Collection<? extends K> c) {
      this(c, 0.75F);
   }

   public ObjectLinkedOpenHashSet(ObjectCollection<? extends K> c, float f) {
      this(c.size(), f);
      this.addAll(c);
   }

   public ObjectLinkedOpenHashSet(ObjectCollection<? extends K> c) {
      this(c, 0.75F);
   }

   public ObjectLinkedOpenHashSet(Iterator<? extends K> i, float f) {
      this(16, f);

      while(i.hasNext()) {
         this.add(i.next());
      }

   }

   public ObjectLinkedOpenHashSet(Iterator<? extends K> i) {
      this(i, 0.75F);
   }

   public ObjectLinkedOpenHashSet(K[] a, int offset, int length, float f) {
      this(length < 0 ? 0 : length, f);
      ObjectArrays.ensureOffsetLength(a, offset, length);

      for(int i = 0; i < length; ++i) {
         this.add(a[offset + i]);
      }

   }

   public ObjectLinkedOpenHashSet(K[] a, int offset, int length) {
      this(a, offset, length, 0.75F);
   }

   public ObjectLinkedOpenHashSet(K[] a, float f) {
      this(a, 0, a.length, f);
   }

   public ObjectLinkedOpenHashSet(K[] a) {
      this(a, 0.75F);
   }

   public static <K> ObjectLinkedOpenHashSet<K> of() {
      return new ObjectLinkedOpenHashSet();
   }

   public static <K> ObjectLinkedOpenHashSet<K> of(K e) {
      ObjectLinkedOpenHashSet<K> result = new ObjectLinkedOpenHashSet(1, 0.75F);
      result.add(e);
      return result;
   }

   public static <K> ObjectLinkedOpenHashSet<K> of(K e0, K e1) {
      ObjectLinkedOpenHashSet<K> result = new ObjectLinkedOpenHashSet(2, 0.75F);
      result.add(e0);
      if (!result.add(e1)) {
         throw new IllegalArgumentException("Duplicate element: " + e1);
      } else {
         return result;
      }
   }

   public static <K> ObjectLinkedOpenHashSet<K> of(K e0, K e1, K e2) {
      ObjectLinkedOpenHashSet<K> result = new ObjectLinkedOpenHashSet(3, 0.75F);
      result.add(e0);
      if (!result.add(e1)) {
         throw new IllegalArgumentException("Duplicate element: " + e1);
      } else if (!result.add(e2)) {
         throw new IllegalArgumentException("Duplicate element: " + e2);
      } else {
         return result;
      }
   }

   @SafeVarargs
   public static <K> ObjectLinkedOpenHashSet<K> of(K... a) {
      ObjectLinkedOpenHashSet<K> result = new ObjectLinkedOpenHashSet(a.length, 0.75F);
      Object[] var2 = a;
      int var3 = a.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         K element = var2[var4];
         if (!result.add(element)) {
            throw new IllegalArgumentException("Duplicate element " + element);
         }
      }

      return result;
   }

   private ObjectLinkedOpenHashSet<K> combine(ObjectLinkedOpenHashSet<? extends K> toAddFrom) {
      this.addAll(toAddFrom);
      return this;
   }

   public static <K> Collector<K, ?, ObjectLinkedOpenHashSet<K>> toSet() {
      return TO_SET_COLLECTOR;
   }

   public static <K> Collector<K, ?, ObjectLinkedOpenHashSet<K>> toSetWithExpectedSize(int expectedSize) {
      return expectedSize <= 16 ? toSet() : Collector.of(new ObjectCollections.SizeDecreasingSupplier(expectedSize, (size) -> {
         return size <= 16 ? new ObjectLinkedOpenHashSet() : new ObjectLinkedOpenHashSet(size);
      }), ObjectLinkedOpenHashSet::add, ObjectLinkedOpenHashSet::combine);
   }

   private int realSize() {
      return this.containsNull ? this.size - 1 : this.size;
   }

   public void ensureCapacity(int capacity) {
      int needed = HashCommon.arraySize(capacity, this.f);
      if (needed > this.n) {
         this.rehash(needed);
      }

   }

   private void tryCapacity(long capacity) {
      int needed = (int)Math.min(1073741824L, Math.max(2L, HashCommon.nextPowerOfTwo((long)Math.ceil((double)((float)capacity / this.f)))));
      if (needed > this.n) {
         this.rehash(needed);
      }

   }

   public boolean addAll(Collection<? extends K> c) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(c.size());
      } else {
         this.tryCapacity((long)(this.size() + c.size()));
      }

      return super.addAll(c);
   }

   public boolean add(K k) {
      int pos;
      if (k == null) {
         if (this.containsNull) {
            return false;
         }

         pos = this.n;
         this.containsNull = true;
      } else {
         K[] key = this.key;
         Object curr;
         if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) != null) {
            if (curr.equals(k)) {
               return false;
            }

            while((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (curr.equals(k)) {
                  return false;
               }
            }
         }

         key[pos] = k;
      }

      if (this.size == 0) {
         this.first = this.last = pos;
         this.link[pos] = -1L;
      } else {
         long[] var10000 = this.link;
         int var10001 = this.last;
         var10000[var10001] ^= (this.link[this.last] ^ (long)pos & 4294967295L) & 4294967295L;
         this.link[pos] = ((long)this.last & 4294967295L) << 32 | 4294967295L;
         this.last = pos;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

      return true;
   }

   public K addOrGet(K k) {
      int pos;
      if (k == null) {
         if (this.containsNull) {
            return this.key[this.n];
         }

         pos = this.n;
         this.containsNull = true;
      } else {
         K[] key = this.key;
         Object curr;
         if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) != null) {
            if (curr.equals(k)) {
               return curr;
            }

            while((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (curr.equals(k)) {
                  return curr;
               }
            }
         }

         key[pos] = k;
      }

      if (this.size == 0) {
         this.first = this.last = pos;
         this.link[pos] = -1L;
      } else {
         long[] var10000 = this.link;
         int var10001 = this.last;
         var10000[var10001] ^= (this.link[this.last] ^ (long)pos & 4294967295L) & 4294967295L;
         this.link[pos] = ((long)this.last & 4294967295L) << 32 | 4294967295L;
         this.last = pos;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

      return k;
   }

   protected final void shiftKeys(int pos) {
      Object[] key = this.key;

      while(true) {
         int last = pos;
         pos = pos + 1 & this.mask;

         Object curr;
         while(true) {
            if ((curr = key[pos]) == null) {
               key[last] = null;
               return;
            }

            int slot = HashCommon.mix(curr.hashCode()) & this.mask;
            if (last <= pos) {
               if (last >= slot || slot > pos) {
                  break;
               }
            } else if (last >= slot && slot > pos) {
               break;
            }

            pos = pos + 1 & this.mask;
         }

         key[last] = curr;
         this.fixPointers(pos, last);
      }
   }

   private boolean removeEntry(int pos) {
      --this.size;
      this.fixPointers(pos);
      this.shiftKeys(pos);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return true;
   }

   private boolean removeNullEntry() {
      this.containsNull = false;
      this.key[this.n] = null;
      --this.size;
      this.fixPointers(this.n);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return true;
   }

   public boolean remove(Object k) {
      if (k == null) {
         return this.containsNull ? this.removeNullEntry() : false;
      } else {
         K[] key = this.key;
         Object curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null) {
            return false;
         } else if (k.equals(curr)) {
            return this.removeEntry(pos);
         } else {
            while((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (k.equals(curr)) {
                  return this.removeEntry(pos);
               }
            }

            return false;
         }
      }
   }

   public boolean contains(Object k) {
      if (k == null) {
         return this.containsNull;
      } else {
         K[] key = this.key;
         Object curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null) {
            return false;
         } else if (k.equals(curr)) {
            return true;
         } else {
            while((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (k.equals(curr)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public K get(Object k) {
      if (k == null) {
         return this.key[this.n];
      } else {
         K[] key = this.key;
         Object curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null) {
            return null;
         } else if (k.equals(curr)) {
            return curr;
         } else {
            while((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (k.equals(curr)) {
                  return curr;
               }
            }

            return null;
         }
      }
   }

   public K removeFirst() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         int pos = this.first;
         if (this.size == 1) {
            this.first = this.last = -1;
         } else {
            this.first = (int)this.link[pos];
            if (0 <= this.first) {
               long[] var10000 = this.link;
               int var10001 = this.first;
               var10000[var10001] |= -4294967296L;
            }
         }

         K k = this.key[pos];
         --this.size;
         if (k == null) {
            this.containsNull = false;
            this.key[this.n] = null;
         } else {
            this.shiftKeys(pos);
         }

         if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
         }

         return k;
      }
   }

   public K removeLast() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         int pos = this.last;
         if (this.size == 1) {
            this.first = this.last = -1;
         } else {
            this.last = (int)(this.link[pos] >>> 32);
            if (0 <= this.last) {
               long[] var10000 = this.link;
               int var10001 = this.last;
               var10000[var10001] |= 4294967295L;
            }
         }

         K k = this.key[pos];
         --this.size;
         if (k == null) {
            this.containsNull = false;
            this.key[this.n] = null;
         } else {
            this.shiftKeys(pos);
         }

         if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
         }

         return k;
      }
   }

   private void moveIndexToFirst(int i) {
      if (this.size != 1 && this.first != i) {
         long[] var10000;
         int var10001;
         if (this.last == i) {
            this.last = (int)(this.link[i] >>> 32);
            var10000 = this.link;
            var10001 = this.last;
            var10000[var10001] |= 4294967295L;
         } else {
            long linki = this.link[i];
            int prev = (int)(linki >>> 32);
            int next = (int)linki;
            var10000 = this.link;
            var10000[prev] ^= (this.link[prev] ^ linki & 4294967295L) & 4294967295L;
            var10000 = this.link;
            var10000[next] ^= (this.link[next] ^ linki & -4294967296L) & -4294967296L;
         }

         var10000 = this.link;
         var10001 = this.first;
         var10000[var10001] ^= (this.link[this.first] ^ ((long)i & 4294967295L) << 32) & -4294967296L;
         this.link[i] = -4294967296L | (long)this.first & 4294967295L;
         this.first = i;
      }
   }

   private void moveIndexToLast(int i) {
      if (this.size != 1 && this.last != i) {
         long[] var10000;
         int var10001;
         if (this.first == i) {
            this.first = (int)this.link[i];
            var10000 = this.link;
            var10001 = this.first;
            var10000[var10001] |= -4294967296L;
         } else {
            long linki = this.link[i];
            int prev = (int)(linki >>> 32);
            int next = (int)linki;
            var10000 = this.link;
            var10000[prev] ^= (this.link[prev] ^ linki & 4294967295L) & 4294967295L;
            var10000 = this.link;
            var10000[next] ^= (this.link[next] ^ linki & -4294967296L) & -4294967296L;
         }

         var10000 = this.link;
         var10001 = this.last;
         var10000[var10001] ^= (this.link[this.last] ^ (long)i & 4294967295L) & 4294967295L;
         this.link[i] = ((long)this.last & 4294967295L) << 32 | 4294967295L;
         this.last = i;
      }
   }

   public boolean addAndMoveToFirst(K k) {
      int pos;
      if (k == null) {
         if (this.containsNull) {
            this.moveIndexToFirst(this.n);
            return false;
         }

         this.containsNull = true;
         pos = this.n;
      } else {
         K[] key = this.key;

         for(pos = HashCommon.mix(k.hashCode()) & this.mask; key[pos] != null; pos = pos + 1 & this.mask) {
            if (k.equals(key[pos])) {
               this.moveIndexToFirst(pos);
               return false;
            }
         }
      }

      this.key[pos] = k;
      if (this.size == 0) {
         this.first = this.last = pos;
         this.link[pos] = -1L;
      } else {
         long[] var10000 = this.link;
         int var10001 = this.first;
         var10000[var10001] ^= (this.link[this.first] ^ ((long)pos & 4294967295L) << 32) & -4294967296L;
         this.link[pos] = -4294967296L | (long)this.first & 4294967295L;
         this.first = pos;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size, this.f));
      }

      return true;
   }

   public boolean addAndMoveToLast(K k) {
      int pos;
      if (k == null) {
         if (this.containsNull) {
            this.moveIndexToLast(this.n);
            return false;
         }

         this.containsNull = true;
         pos = this.n;
      } else {
         K[] key = this.key;

         for(pos = HashCommon.mix(k.hashCode()) & this.mask; key[pos] != null; pos = pos + 1 & this.mask) {
            if (k.equals(key[pos])) {
               this.moveIndexToLast(pos);
               return false;
            }
         }
      }

      this.key[pos] = k;
      if (this.size == 0) {
         this.first = this.last = pos;
         this.link[pos] = -1L;
      } else {
         long[] var10000 = this.link;
         int var10001 = this.last;
         var10000[var10001] ^= (this.link[this.last] ^ (long)pos & 4294967295L) & 4294967295L;
         this.link[pos] = ((long)this.last & 4294967295L) << 32 | 4294967295L;
         this.last = pos;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size, this.f));
      }

      return true;
   }

   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNull = false;
         Arrays.fill(this.key, (Object)null);
         this.first = this.last = -1;
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   protected void fixPointers(int i) {
      if (this.size == 0) {
         this.first = this.last = -1;
      } else {
         long[] var10000;
         int var10001;
         if (this.first == i) {
            this.first = (int)this.link[i];
            if (0 <= this.first) {
               var10000 = this.link;
               var10001 = this.first;
               var10000[var10001] |= -4294967296L;
            }

         } else if (this.last == i) {
            this.last = (int)(this.link[i] >>> 32);
            if (0 <= this.last) {
               var10000 = this.link;
               var10001 = this.last;
               var10000[var10001] |= 4294967295L;
            }

         } else {
            long linki = this.link[i];
            int prev = (int)(linki >>> 32);
            int next = (int)linki;
            var10000 = this.link;
            var10000[prev] ^= (this.link[prev] ^ linki & 4294967295L) & 4294967295L;
            var10000 = this.link;
            var10000[next] ^= (this.link[next] ^ linki & -4294967296L) & -4294967296L;
         }
      }
   }

   protected void fixPointers(int s, int d) {
      if (this.size == 1) {
         this.first = this.last = d;
         this.link[d] = -1L;
      } else {
         long[] var10000;
         int var10001;
         if (this.first == s) {
            this.first = d;
            var10000 = this.link;
            var10001 = (int)this.link[s];
            var10000[var10001] ^= (this.link[(int)this.link[s]] ^ ((long)d & 4294967295L) << 32) & -4294967296L;
            this.link[d] = this.link[s];
         } else if (this.last == s) {
            this.last = d;
            var10000 = this.link;
            var10001 = (int)(this.link[s] >>> 32);
            var10000[var10001] ^= (this.link[(int)(this.link[s] >>> 32)] ^ (long)d & 4294967295L) & 4294967295L;
            this.link[d] = this.link[s];
         } else {
            long links = this.link[s];
            int prev = (int)(links >>> 32);
            int next = (int)links;
            var10000 = this.link;
            var10000[prev] ^= (this.link[prev] ^ (long)d & 4294967295L) & 4294967295L;
            var10000 = this.link;
            var10000[next] ^= (this.link[next] ^ ((long)d & 4294967295L) << 32) & -4294967296L;
            this.link[d] = links;
         }
      }
   }

   public K first() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   public K last() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   public ObjectSortedSet<K> tailSet(K from) {
      throw new UnsupportedOperationException();
   }

   public ObjectSortedSet<K> headSet(K to) {
      throw new UnsupportedOperationException();
   }

   public ObjectSortedSet<K> subSet(K from, K to) {
      throw new UnsupportedOperationException();
   }

   public Comparator<? super K> comparator() {
      return null;
   }

   public ObjectListIterator<K> iterator(K from) {
      return new ObjectLinkedOpenHashSet.SetIterator(from);
   }

   public ObjectListIterator<K> iterator() {
      return new ObjectLinkedOpenHashSet.SetIterator();
   }

   public ObjectSpliterator<K> spliterator() {
      return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf((Collection)this), 81);
   }

   public void forEach(Consumer<? super K> action) {
      int next = this.first;

      while(next != -1) {
         int curr = next;
         next = (int)this.link[next];
         action.accept(this.key[curr]);
      }

   }

   public boolean trim() {
      return this.trim(this.size);
   }

   public boolean trim(int n) {
      int l = HashCommon.nextPowerOfTwo((int)Math.ceil((double)((float)n / this.f)));
      if (l < this.n && this.size <= HashCommon.maxFill(l, this.f)) {
         try {
            this.rehash(l);
            return true;
         } catch (OutOfMemoryError var4) {
            return false;
         }
      } else {
         return true;
      }
   }

   protected void rehash(int newN) {
      K[] key = this.key;
      int mask = newN - 1;
      K[] newKey = new Object[newN + 1];
      int i = this.first;
      int prev = -1;
      int newPrev = -1;
      long[] link = this.link;
      long[] newLink = new long[newN + 1];
      this.first = -1;

      int t;
      for(int var12 = this.size; var12-- != 0; prev = t) {
         int pos;
         if (key[i] == null) {
            pos = newN;
         } else {
            for(pos = HashCommon.mix(key[i].hashCode()) & mask; newKey[pos] != null; pos = pos + 1 & mask) {
            }
         }

         newKey[pos] = key[i];
         if (prev != -1) {
            newLink[newPrev] ^= (newLink[newPrev] ^ (long)pos & 4294967295L) & 4294967295L;
            newLink[pos] ^= (newLink[pos] ^ ((long)newPrev & 4294967295L) << 32) & -4294967296L;
            newPrev = pos;
         } else {
            newPrev = this.first = pos;
            newLink[pos] = -1L;
         }

         t = i;
         i = (int)link[i];
      }

      this.link = newLink;
      this.last = newPrev;
      if (newPrev != -1) {
         newLink[newPrev] |= 4294967295L;
      }

      this.n = newN;
      this.mask = mask;
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.key = newKey;
   }

   public ObjectLinkedOpenHashSet<K> clone() {
      ObjectLinkedOpenHashSet c;
      try {
         c = (ObjectLinkedOpenHashSet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (Object[])this.key.clone();
      c.containsNull = this.containsNull;
      c.link = (long[])this.link.clone();
      return c;
   }

   public int hashCode() {
      int h = 0;
      int j = this.realSize();

      for(int i = 0; j-- != 0; ++i) {
         while(this.key[i] == null) {
            ++i;
         }

         if (this != this.key[i]) {
            h += this.key[i].hashCode();
         }
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      ObjectIterator<K> i = this.iterator();
      s.defaultWriteObject();
      int var3 = this.size;

      while(var3-- != 0) {
         s.writeObject(i.next());
      }

   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      K[] key = this.key = new Object[this.n + 1];
      long[] link = this.link = new long[this.n + 1];
      int prev = -1;
      this.first = this.last = -1;
      int var6 = this.size;

      while(var6-- != 0) {
         K k = s.readObject();
         int pos;
         if (k == null) {
            pos = this.n;
            this.containsNull = true;
         } else if (key[pos = HashCommon.mix(k.hashCode()) & this.mask] != null) {
            while(key[pos = pos + 1 & this.mask] != null) {
            }
         }

         key[pos] = k;
         if (this.first != -1) {
            link[prev] ^= (link[prev] ^ (long)pos & 4294967295L) & 4294967295L;
            link[pos] ^= (link[pos] ^ ((long)prev & 4294967295L) << 32) & -4294967296L;
            prev = pos;
         } else {
            prev = this.first = pos;
            link[pos] |= -4294967296L;
         }
      }

      this.last = prev;
      if (prev != -1) {
         link[prev] |= 4294967295L;
      }

   }

   private void checkTable() {
   }

   private final class SetIterator implements ObjectListIterator<K> {
      int prev = -1;
      int next = -1;
      int curr = -1;
      int index = -1;

      SetIterator() {
         this.next = ObjectLinkedOpenHashSet.this.first;
         this.index = 0;
      }

      SetIterator(K from) {
         if (from == null) {
            if (ObjectLinkedOpenHashSet.this.containsNull) {
               this.next = (int)ObjectLinkedOpenHashSet.this.link[ObjectLinkedOpenHashSet.this.n];
               this.prev = ObjectLinkedOpenHashSet.this.n;
            } else {
               throw new NoSuchElementException("The key " + from + " does not belong to this set.");
            }
         } else if (Objects.equals(ObjectLinkedOpenHashSet.this.key[ObjectLinkedOpenHashSet.this.last], from)) {
            this.prev = ObjectLinkedOpenHashSet.this.last;
            this.index = ObjectLinkedOpenHashSet.this.size;
         } else {
            K[] key = ObjectLinkedOpenHashSet.this.key;

            for(int pos = HashCommon.mix(from.hashCode()) & ObjectLinkedOpenHashSet.this.mask; key[pos] != null; pos = pos + 1 & ObjectLinkedOpenHashSet.this.mask) {
               if (key[pos].equals(from)) {
                  this.next = (int)ObjectLinkedOpenHashSet.this.link[pos];
                  this.prev = pos;
                  return;
               }
            }

            throw new NoSuchElementException("The key " + from + " does not belong to this set.");
         }
      }

      public boolean hasNext() {
         return this.next != -1;
      }

      public boolean hasPrevious() {
         return this.prev != -1;
      }

      public K next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.next;
            this.next = (int)ObjectLinkedOpenHashSet.this.link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
               ++this.index;
            }

            return ObjectLinkedOpenHashSet.this.key[this.curr];
         }
      }

      public K previous() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.prev;
            this.prev = (int)(ObjectLinkedOpenHashSet.this.link[this.curr] >>> 32);
            this.next = this.curr;
            if (this.index >= 0) {
               --this.index;
            }

            return ObjectLinkedOpenHashSet.this.key[this.curr];
         }
      }

      public void forEachRemaining(Consumer<? super K> action) {
         K[] key = ObjectLinkedOpenHashSet.this.key;

         for(long[] link = ObjectLinkedOpenHashSet.this.link; this.next != -1; action.accept(key[this.curr])) {
            this.curr = this.next;
            this.next = (int)link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
               ++this.index;
            }
         }

      }

      private final void ensureIndexKnown() {
         if (this.index < 0) {
            if (this.prev == -1) {
               this.index = 0;
            } else if (this.next == -1) {
               this.index = ObjectLinkedOpenHashSet.this.size;
            } else {
               int pos = ObjectLinkedOpenHashSet.this.first;

               for(this.index = 1; pos != this.prev; ++this.index) {
                  pos = (int)ObjectLinkedOpenHashSet.this.link[pos];
               }

            }
         }
      }

      public int nextIndex() {
         this.ensureIndexKnown();
         return this.index;
      }

      public int previousIndex() {
         this.ensureIndexKnown();
         return this.index - 1;
      }

      public void remove() {
         this.ensureIndexKnown();
         if (this.curr == -1) {
            throw new IllegalStateException();
         } else {
            if (this.curr == this.prev) {
               --this.index;
               this.prev = (int)(ObjectLinkedOpenHashSet.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)ObjectLinkedOpenHashSet.this.link[this.curr];
            }

            --ObjectLinkedOpenHashSet.this.size;
            int var10001;
            long[] var6;
            if (this.prev == -1) {
               ObjectLinkedOpenHashSet.this.first = this.next;
            } else {
               var6 = ObjectLinkedOpenHashSet.this.link;
               var10001 = this.prev;
               var6[var10001] ^= (ObjectLinkedOpenHashSet.this.link[this.prev] ^ (long)this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               ObjectLinkedOpenHashSet.this.last = this.prev;
            } else {
               var6 = ObjectLinkedOpenHashSet.this.link;
               var10001 = this.next;
               var6[var10001] ^= (ObjectLinkedOpenHashSet.this.link[this.next] ^ ((long)this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int pos = this.curr;
            this.curr = -1;
            if (pos == ObjectLinkedOpenHashSet.this.n) {
               ObjectLinkedOpenHashSet.this.containsNull = false;
               ObjectLinkedOpenHashSet.this.key[ObjectLinkedOpenHashSet.this.n] = null;
            } else {
               Object[] key = ObjectLinkedOpenHashSet.this.key;

               while(true) {
                  int last = pos;
                  pos = pos + 1 & ObjectLinkedOpenHashSet.this.mask;

                  Object curr;
                  while(true) {
                     if ((curr = key[pos]) == null) {
                        key[last] = null;
                        return;
                     }

                     int slot = HashCommon.mix(curr.hashCode()) & ObjectLinkedOpenHashSet.this.mask;
                     if (last <= pos) {
                        if (last >= slot || slot > pos) {
                           break;
                        }
                     } else if (last >= slot && slot > pos) {
                        break;
                     }

                     pos = pos + 1 & ObjectLinkedOpenHashSet.this.mask;
                  }

                  key[last] = curr;
                  if (this.next == pos) {
                     this.next = last;
                  }

                  if (this.prev == pos) {
                     this.prev = last;
                  }

                  ObjectLinkedOpenHashSet.this.fixPointers(pos, last);
               }
            }
         }
      }
   }
}
