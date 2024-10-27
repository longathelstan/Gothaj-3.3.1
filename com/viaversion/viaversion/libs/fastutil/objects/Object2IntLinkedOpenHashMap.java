package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.Hash;
import com.viaversion.viaversion.libs.fastutil.HashCommon;
import com.viaversion.viaversion.libs.fastutil.Size64;
import com.viaversion.viaversion.libs.fastutil.ints.AbstractIntCollection;
import com.viaversion.viaversion.libs.fastutil.ints.IntCollection;
import com.viaversion.viaversion.libs.fastutil.ints.IntIterator;
import com.viaversion.viaversion.libs.fastutil.ints.IntListIterator;
import com.viaversion.viaversion.libs.fastutil.ints.IntSpliterator;
import com.viaversion.viaversion.libs.fastutil.ints.IntSpliterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.ToIntFunction;

public class Object2IntLinkedOpenHashMap<K> extends AbstractObject2IntSortedMap<K> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient K[] key;
   protected transient int[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int first;
   protected transient int last;
   protected transient long[] link;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Object2IntSortedMap.FastSortedEntrySet<K> entries;
   protected transient ObjectSortedSet<K> keys;
   protected transient IntCollection values;

   public Object2IntLinkedOpenHashMap(int expected, float f) {
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
            this.value = new int[this.n + 1];
            this.link = new long[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
      }
   }

   public Object2IntLinkedOpenHashMap(int expected) {
      this(expected, 0.75F);
   }

   public Object2IntLinkedOpenHashMap() {
      this(16, 0.75F);
   }

   public Object2IntLinkedOpenHashMap(Map<? extends K, ? extends Integer> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Object2IntLinkedOpenHashMap(Map<? extends K, ? extends Integer> m) {
      this(m, 0.75F);
   }

   public Object2IntLinkedOpenHashMap(Object2IntMap<K> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Object2IntLinkedOpenHashMap(Object2IntMap<K> m) {
      this(m, 0.75F);
   }

   public Object2IntLinkedOpenHashMap(K[] k, int[] v, float f) {
      this(k.length, f);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for(int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
         }

      }
   }

   public Object2IntLinkedOpenHashMap(K[] k, int[] v) {
      this(k, v, 0.75F);
   }

   private int realSize() {
      return this.containsNullKey ? this.size - 1 : this.size;
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

   private int removeEntry(int pos) {
      int oldValue = this.value[pos];
      --this.size;
      this.fixPointers(pos);
      this.shiftKeys(pos);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   private int removeNullEntry() {
      this.containsNullKey = false;
      this.key[this.n] = null;
      int oldValue = this.value[this.n];
      --this.size;
      this.fixPointers(this.n);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   public void putAll(Map<? extends K, ? extends Integer> m) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(m.size());
      } else {
         this.tryCapacity((long)(this.size() + m.size()));
      }

      super.putAll(m);
   }

   private int find(K k) {
      if (k == null) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         K[] key = this.key;
         Object curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null) {
            return -(pos + 1);
         } else if (k.equals(curr)) {
            return pos;
         } else {
            while((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (k.equals(curr)) {
                  return pos;
               }
            }

            return -(pos + 1);
         }
      }
   }

   private void insert(int pos, K k, int v) {
      if (pos == this.n) {
         this.containsNullKey = true;
      }

      this.key[pos] = k;
      this.value[pos] = v;
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

   }

   public int put(K k, int v) {
      int pos = this.find(k);
      if (pos < 0) {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      } else {
         int oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   private int addToValue(int pos, int incr) {
      int oldValue = this.value[pos];
      this.value[pos] = oldValue + incr;
      return oldValue;
   }

   public int addTo(K k, int incr) {
      int pos;
      if (k == null) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, incr);
         }

         pos = this.n;
         this.containsNullKey = true;
      } else {
         K[] key = this.key;
         Object curr;
         if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) != null) {
            if (curr.equals(k)) {
               return this.addToValue(pos, incr);
            }

            while((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (curr.equals(k)) {
                  return this.addToValue(pos, incr);
               }
            }
         }
      }

      this.key[pos] = k;
      this.value[pos] = this.defRetValue + incr;
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

      return this.defRetValue;
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
         this.value[last] = this.value[pos];
         this.fixPointers(pos, last);
      }
   }

   public int removeInt(Object k) {
      if (k == null) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         K[] key = this.key;
         Object curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null) {
            return this.defRetValue;
         } else if (k.equals(curr)) {
            return this.removeEntry(pos);
         } else {
            while((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (k.equals(curr)) {
                  return this.removeEntry(pos);
               }
            }

            return this.defRetValue;
         }
      }
   }

   private int setValue(int pos, int v) {
      int oldValue = this.value[pos];
      this.value[pos] = v;
      return oldValue;
   }

   public int removeFirstInt() {
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

         --this.size;
         int v = this.value[pos];
         if (pos == this.n) {
            this.containsNullKey = false;
            this.key[this.n] = null;
         } else {
            this.shiftKeys(pos);
         }

         if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
         }

         return v;
      }
   }

   public int removeLastInt() {
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

         --this.size;
         int v = this.value[pos];
         if (pos == this.n) {
            this.containsNullKey = false;
            this.key[this.n] = null;
         } else {
            this.shiftKeys(pos);
         }

         if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
         }

         return v;
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

   public int getAndMoveToFirst(K k) {
      if (k == null) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         K[] key = this.key;
         Object curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null) {
            return this.defRetValue;
         } else if (k.equals(curr)) {
            this.moveIndexToFirst(pos);
            return this.value[pos];
         } else {
            while((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (k.equals(curr)) {
                  this.moveIndexToFirst(pos);
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public int getAndMoveToLast(K k) {
      if (k == null) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         K[] key = this.key;
         Object curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null) {
            return this.defRetValue;
         } else if (k.equals(curr)) {
            this.moveIndexToLast(pos);
            return this.value[pos];
         } else {
            while((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (k.equals(curr)) {
                  this.moveIndexToLast(pos);
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public int putAndMoveToFirst(K k, int v) {
      int pos;
      if (k == null) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.setValue(this.n, v);
         }

         this.containsNullKey = true;
         pos = this.n;
      } else {
         K[] key = this.key;
         Object curr;
         if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) != null) {
            if (curr.equals(k)) {
               this.moveIndexToFirst(pos);
               return this.setValue(pos, v);
            }

            while((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (curr.equals(k)) {
                  this.moveIndexToFirst(pos);
                  return this.setValue(pos, v);
               }
            }
         }
      }

      this.key[pos] = k;
      this.value[pos] = v;
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

      return this.defRetValue;
   }

   public int putAndMoveToLast(K k, int v) {
      int pos;
      if (k == null) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.setValue(this.n, v);
         }

         this.containsNullKey = true;
         pos = this.n;
      } else {
         K[] key = this.key;
         Object curr;
         if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) != null) {
            if (curr.equals(k)) {
               this.moveIndexToLast(pos);
               return this.setValue(pos, v);
            }

            while((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (curr.equals(k)) {
                  this.moveIndexToLast(pos);
                  return this.setValue(pos, v);
               }
            }
         }
      }

      this.key[pos] = k;
      this.value[pos] = v;
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

      return this.defRetValue;
   }

   public int getInt(Object k) {
      if (k == null) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         K[] key = this.key;
         Object curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null) {
            return this.defRetValue;
         } else if (k.equals(curr)) {
            return this.value[pos];
         } else {
            while((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (k.equals(curr)) {
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public boolean containsKey(Object k) {
      if (k == null) {
         return this.containsNullKey;
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

   public boolean containsValue(int v) {
      int[] value = this.value;
      K[] key = this.key;
      if (this.containsNullKey && value[this.n] == v) {
         return true;
      } else {
         int i = this.n;

         do {
            if (i-- == 0) {
               return false;
            }
         } while(key[i] == null || value[i] != v);

         return true;
      }
   }

   public int getOrDefault(Object k, int defaultValue) {
      if (k == null) {
         return this.containsNullKey ? this.value[this.n] : defaultValue;
      } else {
         K[] key = this.key;
         Object curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null) {
            return defaultValue;
         } else if (k.equals(curr)) {
            return this.value[pos];
         } else {
            while((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (k.equals(curr)) {
                  return this.value[pos];
               }
            }

            return defaultValue;
         }
      }
   }

   public int putIfAbsent(K k, int v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   public boolean remove(Object k, int v) {
      if (k == null) {
         if (this.containsNullKey && v == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         K[] key = this.key;
         Object curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) == null) {
            return false;
         } else if (k.equals(curr) && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
         } else {
            do {
               if ((curr = key[pos = pos + 1 & this.mask]) == null) {
                  return false;
               }
            } while(!k.equals(curr) || v != this.value[pos]);

            this.removeEntry(pos);
            return true;
         }
      }
   }

   public boolean replace(K k, int oldValue, int v) {
      int pos = this.find(k);
      if (pos >= 0 && oldValue == this.value[pos]) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   public int replace(K k, int v) {
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         int oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   public int computeIfAbsent(K k, ToIntFunction<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         int newValue = mappingFunction.applyAsInt(k);
         this.insert(-pos - 1, k, newValue);
         return newValue;
      }
   }

   public int computeIfAbsent(K key, Object2IntFunction<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(key);
      if (pos >= 0) {
         return this.value[pos];
      } else if (!mappingFunction.containsKey(key)) {
         return this.defRetValue;
      } else {
         int newValue = mappingFunction.getInt(key);
         this.insert(-pos - 1, key, newValue);
         return newValue;
      }
   }

   public int computeIntIfPresent(K k, BiFunction<? super K, ? super Integer, ? extends Integer> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Integer newValue = (Integer)remappingFunction.apply(k, this.value[pos]);
         if (newValue == null) {
            if (k == null) {
               this.removeNullEntry();
            } else {
               this.removeEntry(pos);
            }

            return this.defRetValue;
         } else {
            return this.value[pos] = newValue;
         }
      }
   }

   public int computeInt(K k, BiFunction<? super K, ? super Integer, ? extends Integer> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Integer newValue = (Integer)remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
      if (newValue == null) {
         if (pos >= 0) {
            if (k == null) {
               this.removeNullEntry();
            } else {
               this.removeEntry(pos);
            }
         }

         return this.defRetValue;
      } else {
         int newVal = newValue;
         if (pos < 0) {
            this.insert(-pos - 1, k, newVal);
            return newVal;
         } else {
            return this.value[pos] = newVal;
         }
      }
   }

   public int merge(K k, int v, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         if (pos < 0) {
            this.insert(-pos - 1, k, v);
         } else {
            this.value[pos] = v;
         }

         return v;
      } else {
         Integer newValue = (Integer)remappingFunction.apply(this.value[pos], v);
         if (newValue == null) {
            if (k == null) {
               this.removeNullEntry();
            } else {
               this.removeEntry(pos);
            }

            return this.defRetValue;
         } else {
            return this.value[pos] = newValue;
         }
      }
   }

   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNullKey = false;
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

   public K firstKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   public K lastKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   public Object2IntSortedMap<K> tailMap(K from) {
      throw new UnsupportedOperationException();
   }

   public Object2IntSortedMap<K> headMap(K to) {
      throw new UnsupportedOperationException();
   }

   public Object2IntSortedMap<K> subMap(K from, K to) {
      throw new UnsupportedOperationException();
   }

   public Comparator<? super K> comparator() {
      return null;
   }

   public Object2IntSortedMap.FastSortedEntrySet<K> object2IntEntrySet() {
      if (this.entries == null) {
         this.entries = new Object2IntLinkedOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public ObjectSortedSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Object2IntLinkedOpenHashMap.KeySet();
      }

      return this.keys;
   }

   public IntCollection values() {
      if (this.values == null) {
         this.values = new AbstractIntCollection() {
            private static final int SPLITERATOR_CHARACTERISTICS = 336;

            public IntIterator iterator() {
               return Object2IntLinkedOpenHashMap.this.new ValueIterator();
            }

            public IntSpliterator spliterator() {
               return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf((Map)Object2IntLinkedOpenHashMap.this), 336);
            }

            public void forEach(IntConsumer consumer) {
               int i = Object2IntLinkedOpenHashMap.this.size;
               int next = Object2IntLinkedOpenHashMap.this.first;

               while(i-- != 0) {
                  int curr = next;
                  next = (int)Object2IntLinkedOpenHashMap.this.link[next];
                  consumer.accept(Object2IntLinkedOpenHashMap.this.value[curr]);
               }

            }

            public int size() {
               return Object2IntLinkedOpenHashMap.this.size;
            }

            public boolean contains(int v) {
               return Object2IntLinkedOpenHashMap.this.containsValue(v);
            }

            public void clear() {
               Object2IntLinkedOpenHashMap.this.clear();
            }
         };
      }

      return this.values;
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
      int[] value = this.value;
      int mask = newN - 1;
      K[] newKey = new Object[newN + 1];
      int[] newValue = new int[newN + 1];
      int i = this.first;
      int prev = -1;
      int newPrev = -1;
      long[] link = this.link;
      long[] newLink = new long[newN + 1];
      this.first = -1;

      int t;
      for(int var14 = this.size; var14-- != 0; prev = t) {
         int pos;
         if (key[i] == null) {
            pos = newN;
         } else {
            for(pos = HashCommon.mix(key[i].hashCode()) & mask; newKey[pos] != null; pos = pos + 1 & mask) {
            }
         }

         newKey[pos] = key[i];
         newValue[pos] = value[i];
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
      this.value = newValue;
   }

   public Object2IntLinkedOpenHashMap<K> clone() {
      Object2IntLinkedOpenHashMap c;
      try {
         c = (Object2IntLinkedOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (Object[])this.key.clone();
      c.value = (int[])this.value.clone();
      c.link = (long[])this.link.clone();
      return c;
   }

   public int hashCode() {
      int h = 0;
      int j = this.realSize();
      int i = 0;

      for(int t = 0; j-- != 0; ++i) {
         while(this.key[i] == null) {
            ++i;
         }

         if (this != this.key[i]) {
            t = this.key[i].hashCode();
         }

         t ^= this.value[i];
         h += t;
      }

      if (this.containsNullKey) {
         h += this.value[this.n];
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      K[] key = this.key;
      int[] value = this.value;
      Object2IntLinkedOpenHashMap<K>.EntryIterator i = new Object2IntLinkedOpenHashMap.EntryIterator();
      s.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int e = i.nextEntry();
         s.writeObject(key[e]);
         s.writeInt(value[e]);
      }

   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      K[] key = this.key = new Object[this.n + 1];
      int[] value = this.value = new int[this.n + 1];
      long[] link = this.link = new long[this.n + 1];
      int prev = -1;
      this.first = this.last = -1;
      int var8 = this.size;

      while(var8-- != 0) {
         K k = s.readObject();
         int v = s.readInt();
         int pos;
         if (k == null) {
            pos = this.n;
            this.containsNullKey = true;
         } else {
            for(pos = HashCommon.mix(k.hashCode()) & this.mask; key[pos] != null; pos = pos + 1 & this.mask) {
            }
         }

         key[pos] = k;
         value[pos] = v;
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

   private final class MapEntrySet extends AbstractObjectSortedSet<Object2IntMap.Entry<K>> implements Object2IntSortedMap.FastSortedEntrySet<K> {
      private static final int SPLITERATOR_CHARACTERISTICS = 81;

      private MapEntrySet() {
      }

      public ObjectBidirectionalIterator<Object2IntMap.Entry<K>> iterator() {
         return Object2IntLinkedOpenHashMap.this.new EntryIterator();
      }

      public ObjectSpliterator<Object2IntMap.Entry<K>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf((Map)Object2IntLinkedOpenHashMap.this), 81);
      }

      public Comparator<? super Object2IntMap.Entry<K>> comparator() {
         return null;
      }

      public ObjectSortedSet<Object2IntMap.Entry<K>> subSet(Object2IntMap.Entry<K> fromElement, Object2IntMap.Entry<K> toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Object2IntMap.Entry<K>> headSet(Object2IntMap.Entry<K> toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Object2IntMap.Entry<K>> tailSet(Object2IntMap.Entry<K> fromElement) {
         throw new UnsupportedOperationException();
      }

      public Object2IntMap.Entry<K> first() {
         if (Object2IntLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Object2IntLinkedOpenHashMap.this.new MapEntry(Object2IntLinkedOpenHashMap.this.first);
         }
      }

      public Object2IntMap.Entry<K> last() {
         if (Object2IntLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Object2IntLinkedOpenHashMap.this.new MapEntry(Object2IntLinkedOpenHashMap.this.last);
         }
      }

      public boolean contains(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry<?, ?> e = (java.util.Map.Entry)o;
            if (e.getValue() != null && e.getValue() instanceof Integer) {
               K k = e.getKey();
               int v = (Integer)e.getValue();
               if (k == null) {
                  return Object2IntLinkedOpenHashMap.this.containsNullKey && Object2IntLinkedOpenHashMap.this.value[Object2IntLinkedOpenHashMap.this.n] == v;
               } else {
                  K[] key = Object2IntLinkedOpenHashMap.this.key;
                  Object curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix(k.hashCode()) & Object2IntLinkedOpenHashMap.this.mask]) == null) {
                     return false;
                  } else if (k.equals(curr)) {
                     return Object2IntLinkedOpenHashMap.this.value[pos] == v;
                  } else {
                     while((curr = key[pos = pos + 1 & Object2IntLinkedOpenHashMap.this.mask]) != null) {
                        if (k.equals(curr)) {
                           return Object2IntLinkedOpenHashMap.this.value[pos] == v;
                        }
                     }

                     return false;
                  }
               }
            } else {
               return false;
            }
         }
      }

      public boolean remove(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry<?, ?> e = (java.util.Map.Entry)o;
            if (e.getValue() != null && e.getValue() instanceof Integer) {
               K k = e.getKey();
               int v = (Integer)e.getValue();
               if (k == null) {
                  if (Object2IntLinkedOpenHashMap.this.containsNullKey && Object2IntLinkedOpenHashMap.this.value[Object2IntLinkedOpenHashMap.this.n] == v) {
                     Object2IntLinkedOpenHashMap.this.removeNullEntry();
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  K[] key = Object2IntLinkedOpenHashMap.this.key;
                  Object curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix(k.hashCode()) & Object2IntLinkedOpenHashMap.this.mask]) == null) {
                     return false;
                  } else if (curr.equals(k)) {
                     if (Object2IntLinkedOpenHashMap.this.value[pos] == v) {
                        Object2IntLinkedOpenHashMap.this.removeEntry(pos);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     do {
                        if ((curr = key[pos = pos + 1 & Object2IntLinkedOpenHashMap.this.mask]) == null) {
                           return false;
                        }
                     } while(!curr.equals(k) || Object2IntLinkedOpenHashMap.this.value[pos] != v);

                     Object2IntLinkedOpenHashMap.this.removeEntry(pos);
                     return true;
                  }
               }
            } else {
               return false;
            }
         }
      }

      public int size() {
         return Object2IntLinkedOpenHashMap.this.size;
      }

      public void clear() {
         Object2IntLinkedOpenHashMap.this.clear();
      }

      public ObjectListIterator<Object2IntMap.Entry<K>> iterator(Object2IntMap.Entry<K> from) {
         return Object2IntLinkedOpenHashMap.this.new EntryIterator(from.getKey());
      }

      public ObjectListIterator<Object2IntMap.Entry<K>> fastIterator() {
         return Object2IntLinkedOpenHashMap.this.new FastEntryIterator();
      }

      public ObjectListIterator<Object2IntMap.Entry<K>> fastIterator(Object2IntMap.Entry<K> from) {
         return Object2IntLinkedOpenHashMap.this.new FastEntryIterator(from.getKey());
      }

      public void forEach(Consumer<? super Object2IntMap.Entry<K>> consumer) {
         int i = Object2IntLinkedOpenHashMap.this.size;
         int next = Object2IntLinkedOpenHashMap.this.first;

         while(i-- != 0) {
            int curr = next;
            next = (int)Object2IntLinkedOpenHashMap.this.link[next];
            consumer.accept(Object2IntLinkedOpenHashMap.this.new MapEntry(curr));
         }

      }

      public void fastForEach(Consumer<? super Object2IntMap.Entry<K>> consumer) {
         Object2IntLinkedOpenHashMap<K>.MapEntry entry = Object2IntLinkedOpenHashMap.this.new MapEntry();
         int i = Object2IntLinkedOpenHashMap.this.size;
         int next = Object2IntLinkedOpenHashMap.this.first;

         while(i-- != 0) {
            entry.index = next;
            next = (int)Object2IntLinkedOpenHashMap.this.link[next];
            consumer.accept(entry);
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object x1) {
         this();
      }
   }

   private final class KeySet extends AbstractObjectSortedSet<K> {
      private static final int SPLITERATOR_CHARACTERISTICS = 81;

      private KeySet() {
      }

      public ObjectListIterator<K> iterator(K from) {
         return Object2IntLinkedOpenHashMap.this.new KeyIterator(from);
      }

      public ObjectListIterator<K> iterator() {
         return Object2IntLinkedOpenHashMap.this.new KeyIterator();
      }

      public ObjectSpliterator<K> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf((Map)Object2IntLinkedOpenHashMap.this), 81);
      }

      public void forEach(Consumer<? super K> consumer) {
         int i = Object2IntLinkedOpenHashMap.this.size;
         int next = Object2IntLinkedOpenHashMap.this.first;

         while(i-- != 0) {
            int curr = next;
            next = (int)Object2IntLinkedOpenHashMap.this.link[next];
            consumer.accept(Object2IntLinkedOpenHashMap.this.key[curr]);
         }

      }

      public int size() {
         return Object2IntLinkedOpenHashMap.this.size;
      }

      public boolean contains(Object k) {
         return Object2IntLinkedOpenHashMap.this.containsKey(k);
      }

      public boolean remove(Object k) {
         int oldSize = Object2IntLinkedOpenHashMap.this.size;
         Object2IntLinkedOpenHashMap.this.removeInt(k);
         return Object2IntLinkedOpenHashMap.this.size != oldSize;
      }

      public void clear() {
         Object2IntLinkedOpenHashMap.this.clear();
      }

      public K first() {
         if (Object2IntLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Object2IntLinkedOpenHashMap.this.key[Object2IntLinkedOpenHashMap.this.first];
         }
      }

      public K last() {
         if (Object2IntLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Object2IntLinkedOpenHashMap.this.key[Object2IntLinkedOpenHashMap.this.last];
         }
      }

      public Comparator<? super K> comparator() {
         return null;
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

      // $FF: synthetic method
      KeySet(Object x1) {
         this();
      }
   }

   private final class EntryIterator extends Object2IntLinkedOpenHashMap<K>.MapIterator<Consumer<? super Object2IntMap.Entry<K>>> implements ObjectListIterator<Object2IntMap.Entry<K>> {
      private Object2IntLinkedOpenHashMap<K>.MapEntry entry;

      public EntryIterator() {
         super();
      }

      public EntryIterator(K from) {
         super(from, null);
      }

      final void acceptOnIndex(Consumer<? super Object2IntMap.Entry<K>> action, int index) {
         action.accept(Object2IntLinkedOpenHashMap.this.new MapEntry(index));
      }

      public Object2IntLinkedOpenHashMap<K>.MapEntry next() {
         return this.entry = Object2IntLinkedOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      public Object2IntLinkedOpenHashMap<K>.MapEntry previous() {
         return this.entry = Object2IntLinkedOpenHashMap.this.new MapEntry(this.previousEntry());
      }

      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class ValueIterator extends Object2IntLinkedOpenHashMap<K>.MapIterator<IntConsumer> implements IntListIterator {
      public int previousInt() {
         return Object2IntLinkedOpenHashMap.this.value[this.previousEntry()];
      }

      public ValueIterator() {
         super();
      }

      final void acceptOnIndex(IntConsumer action, int index) {
         action.accept(Object2IntLinkedOpenHashMap.this.value[index]);
      }

      public int nextInt() {
         return Object2IntLinkedOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeyIterator extends Object2IntLinkedOpenHashMap<K>.MapIterator<Consumer<? super K>> implements ObjectListIterator<K> {
      public KeyIterator(K k) {
         super(k, null);
      }

      public K previous() {
         return Object2IntLinkedOpenHashMap.this.key[this.previousEntry()];
      }

      public KeyIterator() {
         super();
      }

      final void acceptOnIndex(Consumer<? super K> action, int index) {
         action.accept(Object2IntLinkedOpenHashMap.this.key[index]);
      }

      public K next() {
         return Object2IntLinkedOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class FastEntryIterator extends Object2IntLinkedOpenHashMap<K>.MapIterator<Consumer<? super Object2IntMap.Entry<K>>> implements ObjectListIterator<Object2IntMap.Entry<K>> {
      final Object2IntLinkedOpenHashMap<K>.MapEntry entry;

      public FastEntryIterator() {
         super();
         this.entry = Object2IntLinkedOpenHashMap.this.new MapEntry();
      }

      public FastEntryIterator(K from) {
         super(from, null);
         this.entry = Object2IntLinkedOpenHashMap.this.new MapEntry();
      }

      final void acceptOnIndex(Consumer<? super Object2IntMap.Entry<K>> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }

      public Object2IntLinkedOpenHashMap<K>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      public Object2IntLinkedOpenHashMap<K>.MapEntry previous() {
         this.entry.index = this.previousEntry();
         return this.entry;
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int prev;
      int next;
      int curr;
      int index;

      abstract void acceptOnIndex(ConsumerType var1, int var2);

      protected MapIterator() {
         this.prev = -1;
         this.next = -1;
         this.curr = -1;
         this.index = -1;
         this.next = Object2IntLinkedOpenHashMap.this.first;
         this.index = 0;
      }

      private MapIterator(K from) {
         this.prev = -1;
         this.next = -1;
         this.curr = -1;
         this.index = -1;
         if (from == null) {
            if (Object2IntLinkedOpenHashMap.this.containsNullKey) {
               this.next = (int)Object2IntLinkedOpenHashMap.this.link[Object2IntLinkedOpenHashMap.this.n];
               this.prev = Object2IntLinkedOpenHashMap.this.n;
            } else {
               throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
         } else if (Objects.equals(Object2IntLinkedOpenHashMap.this.key[Object2IntLinkedOpenHashMap.this.last], from)) {
            this.prev = Object2IntLinkedOpenHashMap.this.last;
            this.index = Object2IntLinkedOpenHashMap.this.size;
         } else {
            for(int pos = HashCommon.mix(from.hashCode()) & Object2IntLinkedOpenHashMap.this.mask; Object2IntLinkedOpenHashMap.this.key[pos] != null; pos = pos + 1 & Object2IntLinkedOpenHashMap.this.mask) {
               if (Object2IntLinkedOpenHashMap.this.key[pos].equals(from)) {
                  this.next = (int)Object2IntLinkedOpenHashMap.this.link[pos];
                  this.prev = pos;
                  return;
               }
            }

            throw new NoSuchElementException("The key " + from + " does not belong to this map.");
         }
      }

      public boolean hasNext() {
         return this.next != -1;
      }

      public boolean hasPrevious() {
         return this.prev != -1;
      }

      private final void ensureIndexKnown() {
         if (this.index < 0) {
            if (this.prev == -1) {
               this.index = 0;
            } else if (this.next == -1) {
               this.index = Object2IntLinkedOpenHashMap.this.size;
            } else {
               int pos = Object2IntLinkedOpenHashMap.this.first;

               for(this.index = 1; pos != this.prev; ++this.index) {
                  pos = (int)Object2IntLinkedOpenHashMap.this.link[pos];
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

      public int nextEntry() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.next;
            this.next = (int)Object2IntLinkedOpenHashMap.this.link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
               ++this.index;
            }

            return this.curr;
         }
      }

      public int previousEntry() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.prev;
            this.prev = (int)(Object2IntLinkedOpenHashMap.this.link[this.curr] >>> 32);
            this.next = this.curr;
            if (this.index >= 0) {
               --this.index;
            }

            return this.curr;
         }
      }

      public void forEachRemaining(ConsumerType action) {
         for(; this.hasNext(); this.acceptOnIndex(action, this.curr)) {
            this.curr = this.next;
            this.next = (int)Object2IntLinkedOpenHashMap.this.link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
               ++this.index;
            }
         }

      }

      public void remove() {
         this.ensureIndexKnown();
         if (this.curr == -1) {
            throw new IllegalStateException();
         } else {
            if (this.curr == this.prev) {
               --this.index;
               this.prev = (int)(Object2IntLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)Object2IntLinkedOpenHashMap.this.link[this.curr];
            }

            --Object2IntLinkedOpenHashMap.this.size;
            int var10001;
            long[] var6;
            if (this.prev == -1) {
               Object2IntLinkedOpenHashMap.this.first = this.next;
            } else {
               var6 = Object2IntLinkedOpenHashMap.this.link;
               var10001 = this.prev;
               var6[var10001] ^= (Object2IntLinkedOpenHashMap.this.link[this.prev] ^ (long)this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               Object2IntLinkedOpenHashMap.this.last = this.prev;
            } else {
               var6 = Object2IntLinkedOpenHashMap.this.link;
               var10001 = this.next;
               var6[var10001] ^= (Object2IntLinkedOpenHashMap.this.link[this.next] ^ ((long)this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int pos = this.curr;
            this.curr = -1;
            if (pos == Object2IntLinkedOpenHashMap.this.n) {
               Object2IntLinkedOpenHashMap.this.containsNullKey = false;
               Object2IntLinkedOpenHashMap.this.key[Object2IntLinkedOpenHashMap.this.n] = null;
            } else {
               Object[] key = Object2IntLinkedOpenHashMap.this.key;

               while(true) {
                  int last = pos;
                  pos = pos + 1 & Object2IntLinkedOpenHashMap.this.mask;

                  Object curr;
                  while(true) {
                     if ((curr = key[pos]) == null) {
                        key[last] = null;
                        return;
                     }

                     int slot = HashCommon.mix(curr.hashCode()) & Object2IntLinkedOpenHashMap.this.mask;
                     if (last <= pos) {
                        if (last >= slot || slot > pos) {
                           break;
                        }
                     } else if (last >= slot && slot > pos) {
                        break;
                     }

                     pos = pos + 1 & Object2IntLinkedOpenHashMap.this.mask;
                  }

                  key[last] = curr;
                  Object2IntLinkedOpenHashMap.this.value[last] = Object2IntLinkedOpenHashMap.this.value[pos];
                  if (this.next == pos) {
                     this.next = last;
                  }

                  if (this.prev == pos) {
                     this.prev = last;
                  }

                  Object2IntLinkedOpenHashMap.this.fixPointers(pos, last);
               }
            }
         }
      }

      public int skip(int n) {
         int i = n;

         while(i-- != 0 && this.hasNext()) {
            this.nextEntry();
         }

         return n - i - 1;
      }

      public int back(int n) {
         int i = n;

         while(i-- != 0 && this.hasPrevious()) {
            this.previousEntry();
         }

         return n - i - 1;
      }

      public void set(Object2IntMap.Entry<K> ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Object2IntMap.Entry<K> ok) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      MapIterator(Object x1, Object x2) {
         this(x1);
      }
   }

   final class MapEntry implements Object2IntMap.Entry<K>, java.util.Map.Entry<K, Integer>, ObjectIntPair<K> {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      public K getKey() {
         return Object2IntLinkedOpenHashMap.this.key[this.index];
      }

      public K left() {
         return Object2IntLinkedOpenHashMap.this.key[this.index];
      }

      public int getIntValue() {
         return Object2IntLinkedOpenHashMap.this.value[this.index];
      }

      public int rightInt() {
         return Object2IntLinkedOpenHashMap.this.value[this.index];
      }

      public int setValue(int v) {
         int oldValue = Object2IntLinkedOpenHashMap.this.value[this.index];
         Object2IntLinkedOpenHashMap.this.value[this.index] = v;
         return oldValue;
      }

      public ObjectIntPair<K> right(int v) {
         Object2IntLinkedOpenHashMap.this.value[this.index] = v;
         return this;
      }

      /** @deprecated */
      @Deprecated
      public Integer getValue() {
         return Object2IntLinkedOpenHashMap.this.value[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Integer setValue(Integer v) {
         return this.setValue(v);
      }

      public boolean equals(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry<K, Integer> e = (java.util.Map.Entry)o;
            return Objects.equals(Object2IntLinkedOpenHashMap.this.key[this.index], e.getKey()) && Object2IntLinkedOpenHashMap.this.value[this.index] == (Integer)e.getValue();
         }
      }

      public int hashCode() {
         return (Object2IntLinkedOpenHashMap.this.key[this.index] == null ? 0 : Object2IntLinkedOpenHashMap.this.key[this.index].hashCode()) ^ Object2IntLinkedOpenHashMap.this.value[this.index];
      }

      public String toString() {
         return Object2IntLinkedOpenHashMap.this.key[this.index] + "=>" + Object2IntLinkedOpenHashMap.this.value[this.index];
      }
   }
}
