package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.Hash;
import com.viaversion.viaversion.libs.fastutil.HashCommon;
import com.viaversion.viaversion.libs.fastutil.ints.AbstractIntCollection;
import com.viaversion.viaversion.libs.fastutil.ints.IntCollection;
import com.viaversion.viaversion.libs.fastutil.ints.IntIterator;
import com.viaversion.viaversion.libs.fastutil.ints.IntSpliterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.ToIntFunction;

public class Object2IntOpenHashMap<K> extends AbstractObject2IntMap<K> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient K[] key;
   protected transient int[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Object2IntMap.FastEntrySet<K> entries;
   protected transient ObjectSet<K> keys;
   protected transient IntCollection values;

   public Object2IntOpenHashMap(int expected, float f) {
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
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
      }
   }

   public Object2IntOpenHashMap(int expected) {
      this(expected, 0.75F);
   }

   public Object2IntOpenHashMap() {
      this(16, 0.75F);
   }

   public Object2IntOpenHashMap(Map<? extends K, ? extends Integer> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Object2IntOpenHashMap(Map<? extends K, ? extends Integer> m) {
      this(m, 0.75F);
   }

   public Object2IntOpenHashMap(Object2IntMap<K> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Object2IntOpenHashMap(Object2IntMap<K> m) {
      this(m, 0.75F);
   }

   public Object2IntOpenHashMap(K[] k, int[] v, float f) {
      this(k.length, f);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for(int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
         }

      }
   }

   public Object2IntOpenHashMap(K[] k, int[] v) {
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
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public Object2IntMap.FastEntrySet<K> object2IntEntrySet() {
      if (this.entries == null) {
         this.entries = new Object2IntOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public ObjectSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Object2IntOpenHashMap.KeySet();
      }

      return this.keys;
   }

   public IntCollection values() {
      if (this.values == null) {
         this.values = new AbstractIntCollection() {
            public IntIterator iterator() {
               return Object2IntOpenHashMap.this.new ValueIterator();
            }

            public IntSpliterator spliterator() {
               return Object2IntOpenHashMap.this.new ValueSpliterator();
            }

            public void forEach(IntConsumer consumer) {
               if (Object2IntOpenHashMap.this.containsNullKey) {
                  consumer.accept(Object2IntOpenHashMap.this.value[Object2IntOpenHashMap.this.n]);
               }

               int pos = Object2IntOpenHashMap.this.n;

               while(pos-- != 0) {
                  if (Object2IntOpenHashMap.this.key[pos] != null) {
                     consumer.accept(Object2IntOpenHashMap.this.value[pos]);
                  }
               }

            }

            public int size() {
               return Object2IntOpenHashMap.this.size;
            }

            public boolean contains(int v) {
               return Object2IntOpenHashMap.this.containsValue(v);
            }

            public void clear() {
               Object2IntOpenHashMap.this.clear();
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
      int i = this.n;

      int pos;
      for(int var9 = this.realSize(); var9-- != 0; newValue[pos] = value[i]) {
         do {
            --i;
         } while(key[i] == null);

         if (newKey[pos = HashCommon.mix(key[i].hashCode()) & mask] != null) {
            while(newKey[pos = pos + 1 & mask] != null) {
            }
         }

         newKey[pos] = key[i];
      }

      newValue[newN] = value[this.n];
      this.n = newN;
      this.mask = mask;
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.key = newKey;
      this.value = newValue;
   }

   public Object2IntOpenHashMap<K> clone() {
      Object2IntOpenHashMap c;
      try {
         c = (Object2IntOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (Object[])this.key.clone();
      c.value = (int[])this.value.clone();
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
      Object2IntOpenHashMap<K>.EntryIterator i = new Object2IntOpenHashMap.EntryIterator();
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

      int v;
      int pos;
      for(int var6 = this.size; var6-- != 0; value[pos] = v) {
         K k = s.readObject();
         v = s.readInt();
         if (k == null) {
            pos = this.n;
            this.containsNullKey = true;
         } else {
            for(pos = HashCommon.mix(k.hashCode()) & this.mask; key[pos] != null; pos = pos + 1 & this.mask) {
            }
         }

         key[pos] = k;
      }

   }

   private void checkTable() {
   }

   private final class MapEntrySet extends AbstractObjectSet<Object2IntMap.Entry<K>> implements Object2IntMap.FastEntrySet<K> {
      private MapEntrySet() {
      }

      public ObjectIterator<Object2IntMap.Entry<K>> iterator() {
         return Object2IntOpenHashMap.this.new EntryIterator();
      }

      public ObjectIterator<Object2IntMap.Entry<K>> fastIterator() {
         return Object2IntOpenHashMap.this.new FastEntryIterator();
      }

      public ObjectSpliterator<Object2IntMap.Entry<K>> spliterator() {
         return Object2IntOpenHashMap.this.new EntrySpliterator();
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
                  return Object2IntOpenHashMap.this.containsNullKey && Object2IntOpenHashMap.this.value[Object2IntOpenHashMap.this.n] == v;
               } else {
                  K[] key = Object2IntOpenHashMap.this.key;
                  Object curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix(k.hashCode()) & Object2IntOpenHashMap.this.mask]) == null) {
                     return false;
                  } else if (k.equals(curr)) {
                     return Object2IntOpenHashMap.this.value[pos] == v;
                  } else {
                     while((curr = key[pos = pos + 1 & Object2IntOpenHashMap.this.mask]) != null) {
                        if (k.equals(curr)) {
                           return Object2IntOpenHashMap.this.value[pos] == v;
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
                  if (Object2IntOpenHashMap.this.containsNullKey && Object2IntOpenHashMap.this.value[Object2IntOpenHashMap.this.n] == v) {
                     Object2IntOpenHashMap.this.removeNullEntry();
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  K[] key = Object2IntOpenHashMap.this.key;
                  Object curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix(k.hashCode()) & Object2IntOpenHashMap.this.mask]) == null) {
                     return false;
                  } else if (curr.equals(k)) {
                     if (Object2IntOpenHashMap.this.value[pos] == v) {
                        Object2IntOpenHashMap.this.removeEntry(pos);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     do {
                        if ((curr = key[pos = pos + 1 & Object2IntOpenHashMap.this.mask]) == null) {
                           return false;
                        }
                     } while(!curr.equals(k) || Object2IntOpenHashMap.this.value[pos] != v);

                     Object2IntOpenHashMap.this.removeEntry(pos);
                     return true;
                  }
               }
            } else {
               return false;
            }
         }
      }

      public int size() {
         return Object2IntOpenHashMap.this.size;
      }

      public void clear() {
         Object2IntOpenHashMap.this.clear();
      }

      public void forEach(Consumer<? super Object2IntMap.Entry<K>> consumer) {
         if (Object2IntOpenHashMap.this.containsNullKey) {
            consumer.accept(Object2IntOpenHashMap.this.new MapEntry(Object2IntOpenHashMap.this.n));
         }

         int pos = Object2IntOpenHashMap.this.n;

         while(pos-- != 0) {
            if (Object2IntOpenHashMap.this.key[pos] != null) {
               consumer.accept(Object2IntOpenHashMap.this.new MapEntry(pos));
            }
         }

      }

      public void fastForEach(Consumer<? super Object2IntMap.Entry<K>> consumer) {
         Object2IntOpenHashMap<K>.MapEntry entry = Object2IntOpenHashMap.this.new MapEntry();
         if (Object2IntOpenHashMap.this.containsNullKey) {
            entry.index = Object2IntOpenHashMap.this.n;
            consumer.accept(entry);
         }

         int pos = Object2IntOpenHashMap.this.n;

         while(pos-- != 0) {
            if (Object2IntOpenHashMap.this.key[pos] != null) {
               entry.index = pos;
               consumer.accept(entry);
            }
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object x1) {
         this();
      }
   }

   private final class KeySet extends AbstractObjectSet<K> {
      private KeySet() {
      }

      public ObjectIterator<K> iterator() {
         return Object2IntOpenHashMap.this.new KeyIterator();
      }

      public ObjectSpliterator<K> spliterator() {
         return Object2IntOpenHashMap.this.new KeySpliterator();
      }

      public void forEach(Consumer<? super K> consumer) {
         if (Object2IntOpenHashMap.this.containsNullKey) {
            consumer.accept(Object2IntOpenHashMap.this.key[Object2IntOpenHashMap.this.n]);
         }

         int pos = Object2IntOpenHashMap.this.n;

         while(pos-- != 0) {
            K k = Object2IntOpenHashMap.this.key[pos];
            if (k != null) {
               consumer.accept(k);
            }
         }

      }

      public int size() {
         return Object2IntOpenHashMap.this.size;
      }

      public boolean contains(Object k) {
         return Object2IntOpenHashMap.this.containsKey(k);
      }

      public boolean remove(Object k) {
         int oldSize = Object2IntOpenHashMap.this.size;
         Object2IntOpenHashMap.this.removeInt(k);
         return Object2IntOpenHashMap.this.size != oldSize;
      }

      public void clear() {
         Object2IntOpenHashMap.this.clear();
      }

      // $FF: synthetic method
      KeySet(Object x1) {
         this();
      }
   }

   private final class EntryIterator extends Object2IntOpenHashMap<K>.MapIterator<Consumer<? super Object2IntMap.Entry<K>>> implements ObjectIterator<Object2IntMap.Entry<K>> {
      private Object2IntOpenHashMap<K>.MapEntry entry;

      private EntryIterator() {
         super(null);
      }

      public Object2IntOpenHashMap<K>.MapEntry next() {
         return this.entry = Object2IntOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      final void acceptOnIndex(Consumer<? super Object2IntMap.Entry<K>> action, int index) {
         action.accept(this.entry = Object2IntOpenHashMap.this.new MapEntry(index));
      }

      public void remove() {
         super.remove();
         this.entry.index = -1;
      }

      // $FF: synthetic method
      EntryIterator(Object x1) {
         this();
      }
   }

   private final class ValueSpliterator extends Object2IntOpenHashMap<K>.MapSpliterator<IntConsumer, Object2IntOpenHashMap<K>.ValueSpliterator> implements IntSpliterator {
      private static final int POST_SPLIT_CHARACTERISTICS = 256;

      ValueSpliterator() {
         super();
      }

      ValueSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         super(pos, max, mustReturnNull, hasSplit);
      }

      public int characteristics() {
         return this.hasSplit ? 256 : 320;
      }

      final void acceptOnIndex(IntConsumer action, int index) {
         action.accept(Object2IntOpenHashMap.this.value[index]);
      }

      final Object2IntOpenHashMap<K>.ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Object2IntOpenHashMap.this.new ValueSpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class ValueIterator extends Object2IntOpenHashMap<K>.MapIterator<IntConsumer> implements IntIterator {
      public ValueIterator() {
         super(null);
      }

      final void acceptOnIndex(IntConsumer action, int index) {
         action.accept(Object2IntOpenHashMap.this.value[index]);
      }

      public int nextInt() {
         return Object2IntOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySpliterator extends Object2IntOpenHashMap<K>.MapSpliterator<Consumer<? super K>, Object2IntOpenHashMap<K>.KeySpliterator> implements ObjectSpliterator<K> {
      private static final int POST_SPLIT_CHARACTERISTICS = 1;

      KeySpliterator() {
         super();
      }

      KeySpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         super(pos, max, mustReturnNull, hasSplit);
      }

      public int characteristics() {
         return this.hasSplit ? 1 : 65;
      }

      final void acceptOnIndex(Consumer<? super K> action, int index) {
         action.accept(Object2IntOpenHashMap.this.key[index]);
      }

      final Object2IntOpenHashMap<K>.KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Object2IntOpenHashMap.this.new KeySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class KeyIterator extends Object2IntOpenHashMap<K>.MapIterator<Consumer<? super K>> implements ObjectIterator<K> {
      public KeyIterator() {
         super(null);
      }

      final void acceptOnIndex(Consumer<? super K> action, int index) {
         action.accept(Object2IntOpenHashMap.this.key[index]);
      }

      public K next() {
         return Object2IntOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class EntrySpliterator extends Object2IntOpenHashMap<K>.MapSpliterator<Consumer<? super Object2IntMap.Entry<K>>, Object2IntOpenHashMap<K>.EntrySpliterator> implements ObjectSpliterator<Object2IntMap.Entry<K>> {
      private static final int POST_SPLIT_CHARACTERISTICS = 1;

      EntrySpliterator() {
         super();
      }

      EntrySpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         super(pos, max, mustReturnNull, hasSplit);
      }

      public int characteristics() {
         return this.hasSplit ? 1 : 65;
      }

      final void acceptOnIndex(Consumer<? super Object2IntMap.Entry<K>> action, int index) {
         action.accept(Object2IntOpenHashMap.this.new MapEntry(index));
      }

      final Object2IntOpenHashMap<K>.EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Object2IntOpenHashMap.this.new EntrySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private abstract class MapSpliterator<ConsumerType, SplitType extends Object2IntOpenHashMap<K>.MapSpliterator<ConsumerType, SplitType>> {
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      MapSpliterator() {
         this.max = Object2IntOpenHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Object2IntOpenHashMap.this.containsNullKey;
         this.hasSplit = false;
      }

      MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = Object2IntOpenHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Object2IntOpenHashMap.this.containsNullKey;
         this.hasSplit = false;
         this.pos = pos;
         this.max = max;
         this.mustReturnNull = mustReturnNull;
         this.hasSplit = hasSplit;
      }

      abstract void acceptOnIndex(ConsumerType var1, int var2);

      abstract SplitType makeForSplit(int var1, int var2, boolean var3);

      public boolean tryAdvance(ConsumerType action) {
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            ++this.c;
            this.acceptOnIndex(action, Object2IntOpenHashMap.this.n);
            return true;
         } else {
            for(Object[] key = Object2IntOpenHashMap.this.key; this.pos < this.max; ++this.pos) {
               if (key[this.pos] != null) {
                  ++this.c;
                  this.acceptOnIndex(action, this.pos++);
                  return true;
               }
            }

            return false;
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            ++this.c;
            this.acceptOnIndex(action, Object2IntOpenHashMap.this.n);
         }

         for(Object[] key = Object2IntOpenHashMap.this.key; this.pos < this.max; ++this.pos) {
            if (key[this.pos] != null) {
               this.acceptOnIndex(action, this.pos);
               ++this.c;
            }
         }

      }

      public long estimateSize() {
         return !this.hasSplit ? (long)(Object2IntOpenHashMap.this.size - this.c) : Math.min((long)(Object2IntOpenHashMap.this.size - this.c), (long)((double)Object2IntOpenHashMap.this.realSize() / (double)Object2IntOpenHashMap.this.n * (double)(this.max - this.pos)) + (long)(this.mustReturnNull ? 1 : 0));
      }

      public SplitType trySplit() {
         if (this.pos >= this.max - 1) {
            return null;
         } else {
            int retLen = this.max - this.pos >> 1;
            if (retLen <= 1) {
               return null;
            } else {
               int myNewPos = this.pos + retLen;
               int retPos = this.pos;
               SplitType split = this.makeForSplit(retPos, myNewPos, this.mustReturnNull);
               this.pos = myNewPos;
               this.mustReturnNull = false;
               this.hasSplit = true;
               return split;
            }
         }
      }

      public long skip(long n) {
         if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (n == 0L) {
            return 0L;
         } else {
            long skipped = 0L;
            if (this.mustReturnNull) {
               this.mustReturnNull = false;
               ++skipped;
               --n;
            }

            Object[] key = Object2IntOpenHashMap.this.key;

            while(this.pos < this.max && n > 0L) {
               if (key[this.pos++] != null) {
                  ++skipped;
                  --n;
               }
            }

            return skipped;
         }
      }
   }

   private final class FastEntryIterator extends Object2IntOpenHashMap<K>.MapIterator<Consumer<? super Object2IntMap.Entry<K>>> implements ObjectIterator<Object2IntMap.Entry<K>> {
      private final Object2IntOpenHashMap<K>.MapEntry entry;

      private FastEntryIterator() {
         super(null);
         this.entry = Object2IntOpenHashMap.this.new MapEntry();
      }

      public Object2IntOpenHashMap<K>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      final void acceptOnIndex(Consumer<? super Object2IntMap.Entry<K>> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }

      // $FF: synthetic method
      FastEntryIterator(Object x1) {
         this();
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int pos;
      int last;
      int c;
      boolean mustReturnNullKey;
      ObjectArrayList<K> wrapped;

      private MapIterator() {
         this.pos = Object2IntOpenHashMap.this.n;
         this.last = -1;
         this.c = Object2IntOpenHashMap.this.size;
         this.mustReturnNullKey = Object2IntOpenHashMap.this.containsNullKey;
      }

      abstract void acceptOnIndex(ConsumerType var1, int var2);

      public boolean hasNext() {
         return this.c != 0;
      }

      public int nextEntry() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            --this.c;
            if (this.mustReturnNullKey) {
               this.mustReturnNullKey = false;
               return this.last = Object2IntOpenHashMap.this.n;
            } else {
               Object[] key = Object2IntOpenHashMap.this.key;

               while(--this.pos >= 0) {
                  if (key[this.pos] != null) {
                     return this.last = this.pos;
                  }
               }

               this.last = Integer.MIN_VALUE;
               K k = this.wrapped.get(-this.pos - 1);

               int p;
               for(p = HashCommon.mix(k.hashCode()) & Object2IntOpenHashMap.this.mask; !k.equals(key[p]); p = p + 1 & Object2IntOpenHashMap.this.mask) {
               }

               return p;
            }
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNullKey) {
            this.mustReturnNullKey = false;
            this.acceptOnIndex(action, this.last = Object2IntOpenHashMap.this.n);
            --this.c;
         }

         Object[] key = Object2IntOpenHashMap.this.key;

         while(true) {
            while(this.c != 0) {
               if (--this.pos < 0) {
                  this.last = Integer.MIN_VALUE;
                  K k = this.wrapped.get(-this.pos - 1);

                  int p;
                  for(p = HashCommon.mix(k.hashCode()) & Object2IntOpenHashMap.this.mask; !k.equals(key[p]); p = p + 1 & Object2IntOpenHashMap.this.mask) {
                  }

                  this.acceptOnIndex(action, p);
                  --this.c;
               } else if (key[this.pos] != null) {
                  this.acceptOnIndex(action, this.last = this.pos);
                  --this.c;
               }
            }

            return;
         }
      }

      private void shiftKeys(int pos) {
         Object[] key = Object2IntOpenHashMap.this.key;

         while(true) {
            int last = pos;
            pos = pos + 1 & Object2IntOpenHashMap.this.mask;

            Object curr;
            while(true) {
               if ((curr = key[pos]) == null) {
                  key[last] = null;
                  return;
               }

               int slot = HashCommon.mix(curr.hashCode()) & Object2IntOpenHashMap.this.mask;
               if (last <= pos) {
                  if (last >= slot || slot > pos) {
                     break;
                  }
               } else if (last >= slot && slot > pos) {
                  break;
               }

               pos = pos + 1 & Object2IntOpenHashMap.this.mask;
            }

            if (pos < last) {
               if (this.wrapped == null) {
                  this.wrapped = new ObjectArrayList(2);
               }

               this.wrapped.add(key[pos]);
            }

            key[last] = curr;
            Object2IntOpenHashMap.this.value[last] = Object2IntOpenHashMap.this.value[pos];
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Object2IntOpenHashMap.this.n) {
               Object2IntOpenHashMap.this.containsNullKey = false;
               Object2IntOpenHashMap.this.key[Object2IntOpenHashMap.this.n] = null;
            } else {
               if (this.pos < 0) {
                  Object2IntOpenHashMap.this.removeInt(this.wrapped.set(-this.pos - 1, (Object)null));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --Object2IntOpenHashMap.this.size;
            this.last = -1;
         }
      }

      public int skip(int n) {
         int i = n;

         while(i-- != 0 && this.hasNext()) {
            this.nextEntry();
         }

         return n - i - 1;
      }

      // $FF: synthetic method
      MapIterator(Object x1) {
         this();
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
         return Object2IntOpenHashMap.this.key[this.index];
      }

      public K left() {
         return Object2IntOpenHashMap.this.key[this.index];
      }

      public int getIntValue() {
         return Object2IntOpenHashMap.this.value[this.index];
      }

      public int rightInt() {
         return Object2IntOpenHashMap.this.value[this.index];
      }

      public int setValue(int v) {
         int oldValue = Object2IntOpenHashMap.this.value[this.index];
         Object2IntOpenHashMap.this.value[this.index] = v;
         return oldValue;
      }

      public ObjectIntPair<K> right(int v) {
         Object2IntOpenHashMap.this.value[this.index] = v;
         return this;
      }

      /** @deprecated */
      @Deprecated
      public Integer getValue() {
         return Object2IntOpenHashMap.this.value[this.index];
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
            return Objects.equals(Object2IntOpenHashMap.this.key[this.index], e.getKey()) && Object2IntOpenHashMap.this.value[this.index] == (Integer)e.getValue();
         }
      }

      public int hashCode() {
         return (Object2IntOpenHashMap.this.key[this.index] == null ? 0 : Object2IntOpenHashMap.this.key[this.index].hashCode()) ^ Object2IntOpenHashMap.this.value[this.index];
      }

      public String toString() {
         return Object2IntOpenHashMap.this.key[this.index] + "=>" + Object2IntOpenHashMap.this.value[this.index];
      }
   }
}
