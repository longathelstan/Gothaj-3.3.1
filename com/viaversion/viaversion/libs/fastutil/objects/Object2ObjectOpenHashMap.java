package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.Hash;
import com.viaversion.viaversion.libs.fastutil.HashCommon;
import com.viaversion.viaversion.libs.fastutil.Pair;
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

public class Object2ObjectOpenHashMap<K, V> extends AbstractObject2ObjectMap<K, V> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient K[] key;
   protected transient V[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Object2ObjectMap.FastEntrySet<K, V> entries;
   protected transient ObjectSet<K> keys;
   protected transient ObjectCollection<V> values;

   public Object2ObjectOpenHashMap(int expected, float f) {
      if (!(f <= 0.0F) && !(f >= 1.0F)) {
         if (expected < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
         } else {
            this.f = f;
            this.minN = this.n = HashCommon.arraySize(expected, f);
            this.mask = this.n - 1;
            this.maxFill = HashCommon.maxFill(this.n, f);
            this.key = new Object[this.n + 1];
            this.value = new Object[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
      }
   }

   public Object2ObjectOpenHashMap(int expected) {
      this(expected, 0.75F);
   }

   public Object2ObjectOpenHashMap() {
      this(16, 0.75F);
   }

   public Object2ObjectOpenHashMap(Map<? extends K, ? extends V> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Object2ObjectOpenHashMap(Map<? extends K, ? extends V> m) {
      this(m, 0.75F);
   }

   public Object2ObjectOpenHashMap(Object2ObjectMap<K, V> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Object2ObjectOpenHashMap(Object2ObjectMap<K, V> m) {
      this(m, 0.75F);
   }

   public Object2ObjectOpenHashMap(K[] k, V[] v, float f) {
      this(k.length, f);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for(int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
         }

      }
   }

   public Object2ObjectOpenHashMap(K[] k, V[] v) {
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

   private V removeEntry(int pos) {
      V oldValue = this.value[pos];
      this.value[pos] = null;
      --this.size;
      this.shiftKeys(pos);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   private V removeNullEntry() {
      this.containsNullKey = false;
      this.key[this.n] = null;
      V oldValue = this.value[this.n];
      this.value[this.n] = null;
      --this.size;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   public void putAll(Map<? extends K, ? extends V> m) {
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

   private void insert(int pos, K k, V v) {
      if (pos == this.n) {
         this.containsNullKey = true;
      }

      this.key[pos] = k;
      this.value[pos] = v;
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

   }

   public V put(K k, V v) {
      int pos = this.find(k);
      if (pos < 0) {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      } else {
         V oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
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
               this.value[last] = null;
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

   public V remove(Object k) {
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

   public V get(Object k) {
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

   public boolean containsValue(Object v) {
      V[] value = this.value;
      K[] key = this.key;
      if (this.containsNullKey && Objects.equals(value[this.n], v)) {
         return true;
      } else {
         int i = this.n;

         do {
            if (i-- == 0) {
               return false;
            }
         } while(key[i] == null || !Objects.equals(value[i], v));

         return true;
      }
   }

   public V getOrDefault(Object k, V defaultValue) {
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

   public V putIfAbsent(K k, V v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   public boolean remove(Object k, Object v) {
      if (k == null) {
         if (this.containsNullKey && Objects.equals(v, this.value[this.n])) {
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
         } else if (k.equals(curr) && Objects.equals(v, this.value[pos])) {
            this.removeEntry(pos);
            return true;
         } else {
            do {
               if ((curr = key[pos = pos + 1 & this.mask]) == null) {
                  return false;
               }
            } while(!k.equals(curr) || !Objects.equals(v, this.value[pos]));

            this.removeEntry(pos);
            return true;
         }
      }
   }

   public boolean replace(K k, V oldValue, V v) {
      int pos = this.find(k);
      if (pos >= 0 && Objects.equals(oldValue, this.value[pos])) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   public V replace(K k, V v) {
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         V oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   public V computeIfAbsent(K key, Object2ObjectFunction<? super K, ? extends V> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(key);
      if (pos >= 0) {
         return this.value[pos];
      } else if (!mappingFunction.containsKey(key)) {
         return this.defRetValue;
      } else {
         V newValue = mappingFunction.get(key);
         this.insert(-pos - 1, key, newValue);
         return newValue;
      }
   }

   public V computeIfPresent(K k, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else if (this.value[pos] == null) {
         return this.defRetValue;
      } else {
         V newValue = remappingFunction.apply(k, this.value[pos]);
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

   public V compute(K k, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      V newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
      if (newValue == null) {
         if (pos >= 0) {
            if (k == null) {
               this.removeNullEntry();
            } else {
               this.removeEntry(pos);
            }
         }

         return this.defRetValue;
      } else if (pos < 0) {
         this.insert(-pos - 1, k, newValue);
         return newValue;
      } else {
         return this.value[pos] = newValue;
      }
   }

   public V merge(K k, V v, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      Objects.requireNonNull(v);
      int pos = this.find(k);
      if (pos >= 0 && this.value[pos] != null) {
         V newValue = remappingFunction.apply(this.value[pos], v);
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
      } else {
         if (pos < 0) {
            this.insert(-pos - 1, k, v);
         } else {
            this.value[pos] = v;
         }

         return v;
      }
   }

   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNullKey = false;
         Arrays.fill(this.key, (Object)null);
         Arrays.fill(this.value, (Object)null);
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public Object2ObjectMap.FastEntrySet<K, V> object2ObjectEntrySet() {
      if (this.entries == null) {
         this.entries = new Object2ObjectOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public ObjectSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Object2ObjectOpenHashMap.KeySet();
      }

      return this.keys;
   }

   public ObjectCollection<V> values() {
      if (this.values == null) {
         this.values = new AbstractObjectCollection<V>() {
            public ObjectIterator<V> iterator() {
               return Object2ObjectOpenHashMap.this.new ValueIterator();
            }

            public ObjectSpliterator<V> spliterator() {
               return Object2ObjectOpenHashMap.this.new ValueSpliterator();
            }

            public void forEach(Consumer<? super V> consumer) {
               if (Object2ObjectOpenHashMap.this.containsNullKey) {
                  consumer.accept(Object2ObjectOpenHashMap.this.value[Object2ObjectOpenHashMap.this.n]);
               }

               int pos = Object2ObjectOpenHashMap.this.n;

               while(pos-- != 0) {
                  if (Object2ObjectOpenHashMap.this.key[pos] != null) {
                     consumer.accept(Object2ObjectOpenHashMap.this.value[pos]);
                  }
               }

            }

            public int size() {
               return Object2ObjectOpenHashMap.this.size;
            }

            public boolean contains(Object v) {
               return Object2ObjectOpenHashMap.this.containsValue(v);
            }

            public void clear() {
               Object2ObjectOpenHashMap.this.clear();
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
      V[] value = this.value;
      int mask = newN - 1;
      K[] newKey = new Object[newN + 1];
      V[] newValue = new Object[newN + 1];
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

   public Object2ObjectOpenHashMap<K, V> clone() {
      Object2ObjectOpenHashMap c;
      try {
         c = (Object2ObjectOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (Object[])this.key.clone();
      c.value = (Object[])this.value.clone();
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

         if (this != this.value[i]) {
            t ^= this.value[i] == null ? 0 : this.value[i].hashCode();
         }

         h += t;
      }

      if (this.containsNullKey) {
         h += this.value[this.n] == null ? 0 : this.value[this.n].hashCode();
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      K[] key = this.key;
      V[] value = this.value;
      Object2ObjectOpenHashMap<K, V>.EntryIterator i = new Object2ObjectOpenHashMap.EntryIterator();
      s.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int e = i.nextEntry();
         s.writeObject(key[e]);
         s.writeObject(value[e]);
      }

   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      K[] key = this.key = new Object[this.n + 1];
      V[] value = this.value = new Object[this.n + 1];

      Object v;
      int pos;
      for(int var6 = this.size; var6-- != 0; value[pos] = v) {
         K k = s.readObject();
         v = s.readObject();
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

   private final class MapEntrySet extends AbstractObjectSet<Object2ObjectMap.Entry<K, V>> implements Object2ObjectMap.FastEntrySet<K, V> {
      private MapEntrySet() {
      }

      public ObjectIterator<Object2ObjectMap.Entry<K, V>> iterator() {
         return Object2ObjectOpenHashMap.this.new EntryIterator();
      }

      public ObjectIterator<Object2ObjectMap.Entry<K, V>> fastIterator() {
         return Object2ObjectOpenHashMap.this.new FastEntryIterator();
      }

      public ObjectSpliterator<Object2ObjectMap.Entry<K, V>> spliterator() {
         return Object2ObjectOpenHashMap.this.new EntrySpliterator();
      }

      public boolean contains(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry<?, ?> e = (java.util.Map.Entry)o;
            K k = e.getKey();
            V v = e.getValue();
            if (k == null) {
               return Object2ObjectOpenHashMap.this.containsNullKey && Objects.equals(Object2ObjectOpenHashMap.this.value[Object2ObjectOpenHashMap.this.n], v);
            } else {
               K[] key = Object2ObjectOpenHashMap.this.key;
               Object curr;
               int pos;
               if ((curr = key[pos = HashCommon.mix(k.hashCode()) & Object2ObjectOpenHashMap.this.mask]) == null) {
                  return false;
               } else if (k.equals(curr)) {
                  return Objects.equals(Object2ObjectOpenHashMap.this.value[pos], v);
               } else {
                  while((curr = key[pos = pos + 1 & Object2ObjectOpenHashMap.this.mask]) != null) {
                     if (k.equals(curr)) {
                        return Objects.equals(Object2ObjectOpenHashMap.this.value[pos], v);
                     }
                  }

                  return false;
               }
            }
         }
      }

      public boolean remove(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry<?, ?> e = (java.util.Map.Entry)o;
            K k = e.getKey();
            V v = e.getValue();
            if (k == null) {
               if (Object2ObjectOpenHashMap.this.containsNullKey && Objects.equals(Object2ObjectOpenHashMap.this.value[Object2ObjectOpenHashMap.this.n], v)) {
                  Object2ObjectOpenHashMap.this.removeNullEntry();
                  return true;
               } else {
                  return false;
               }
            } else {
               K[] key = Object2ObjectOpenHashMap.this.key;
               Object curr;
               int pos;
               if ((curr = key[pos = HashCommon.mix(k.hashCode()) & Object2ObjectOpenHashMap.this.mask]) == null) {
                  return false;
               } else if (curr.equals(k)) {
                  if (Objects.equals(Object2ObjectOpenHashMap.this.value[pos], v)) {
                     Object2ObjectOpenHashMap.this.removeEntry(pos);
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  do {
                     if ((curr = key[pos = pos + 1 & Object2ObjectOpenHashMap.this.mask]) == null) {
                        return false;
                     }
                  } while(!curr.equals(k) || !Objects.equals(Object2ObjectOpenHashMap.this.value[pos], v));

                  Object2ObjectOpenHashMap.this.removeEntry(pos);
                  return true;
               }
            }
         }
      }

      public int size() {
         return Object2ObjectOpenHashMap.this.size;
      }

      public void clear() {
         Object2ObjectOpenHashMap.this.clear();
      }

      public void forEach(Consumer<? super Object2ObjectMap.Entry<K, V>> consumer) {
         if (Object2ObjectOpenHashMap.this.containsNullKey) {
            consumer.accept(Object2ObjectOpenHashMap.this.new MapEntry(Object2ObjectOpenHashMap.this.n));
         }

         int pos = Object2ObjectOpenHashMap.this.n;

         while(pos-- != 0) {
            if (Object2ObjectOpenHashMap.this.key[pos] != null) {
               consumer.accept(Object2ObjectOpenHashMap.this.new MapEntry(pos));
            }
         }

      }

      public void fastForEach(Consumer<? super Object2ObjectMap.Entry<K, V>> consumer) {
         Object2ObjectOpenHashMap<K, V>.MapEntry entry = Object2ObjectOpenHashMap.this.new MapEntry();
         if (Object2ObjectOpenHashMap.this.containsNullKey) {
            entry.index = Object2ObjectOpenHashMap.this.n;
            consumer.accept(entry);
         }

         int pos = Object2ObjectOpenHashMap.this.n;

         while(pos-- != 0) {
            if (Object2ObjectOpenHashMap.this.key[pos] != null) {
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
         return Object2ObjectOpenHashMap.this.new KeyIterator();
      }

      public ObjectSpliterator<K> spliterator() {
         return Object2ObjectOpenHashMap.this.new KeySpliterator();
      }

      public void forEach(Consumer<? super K> consumer) {
         if (Object2ObjectOpenHashMap.this.containsNullKey) {
            consumer.accept(Object2ObjectOpenHashMap.this.key[Object2ObjectOpenHashMap.this.n]);
         }

         int pos = Object2ObjectOpenHashMap.this.n;

         while(pos-- != 0) {
            K k = Object2ObjectOpenHashMap.this.key[pos];
            if (k != null) {
               consumer.accept(k);
            }
         }

      }

      public int size() {
         return Object2ObjectOpenHashMap.this.size;
      }

      public boolean contains(Object k) {
         return Object2ObjectOpenHashMap.this.containsKey(k);
      }

      public boolean remove(Object k) {
         int oldSize = Object2ObjectOpenHashMap.this.size;
         Object2ObjectOpenHashMap.this.remove(k);
         return Object2ObjectOpenHashMap.this.size != oldSize;
      }

      public void clear() {
         Object2ObjectOpenHashMap.this.clear();
      }

      // $FF: synthetic method
      KeySet(Object x1) {
         this();
      }
   }

   private final class EntryIterator extends Object2ObjectOpenHashMap<K, V>.MapIterator<Consumer<? super Object2ObjectMap.Entry<K, V>>> implements ObjectIterator<Object2ObjectMap.Entry<K, V>> {
      private Object2ObjectOpenHashMap<K, V>.MapEntry entry;

      private EntryIterator() {
         super(null);
      }

      public Object2ObjectOpenHashMap<K, V>.MapEntry next() {
         return this.entry = Object2ObjectOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      final void acceptOnIndex(Consumer<? super Object2ObjectMap.Entry<K, V>> action, int index) {
         action.accept(this.entry = Object2ObjectOpenHashMap.this.new MapEntry(index));
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

   private final class ValueSpliterator extends Object2ObjectOpenHashMap<K, V>.MapSpliterator<Consumer<? super V>, Object2ObjectOpenHashMap<K, V>.ValueSpliterator> implements ObjectSpliterator<V> {
      private static final int POST_SPLIT_CHARACTERISTICS = 0;

      ValueSpliterator() {
         super();
      }

      ValueSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         super(pos, max, mustReturnNull, hasSplit);
      }

      public int characteristics() {
         return this.hasSplit ? 0 : 64;
      }

      final void acceptOnIndex(Consumer<? super V> action, int index) {
         action.accept(Object2ObjectOpenHashMap.this.value[index]);
      }

      final Object2ObjectOpenHashMap<K, V>.ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Object2ObjectOpenHashMap.this.new ValueSpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class ValueIterator extends Object2ObjectOpenHashMap<K, V>.MapIterator<Consumer<? super V>> implements ObjectIterator<V> {
      public ValueIterator() {
         super(null);
      }

      final void acceptOnIndex(Consumer<? super V> action, int index) {
         action.accept(Object2ObjectOpenHashMap.this.value[index]);
      }

      public V next() {
         return Object2ObjectOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySpliterator extends Object2ObjectOpenHashMap<K, V>.MapSpliterator<Consumer<? super K>, Object2ObjectOpenHashMap<K, V>.KeySpliterator> implements ObjectSpliterator<K> {
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
         action.accept(Object2ObjectOpenHashMap.this.key[index]);
      }

      final Object2ObjectOpenHashMap<K, V>.KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Object2ObjectOpenHashMap.this.new KeySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class KeyIterator extends Object2ObjectOpenHashMap<K, V>.MapIterator<Consumer<? super K>> implements ObjectIterator<K> {
      public KeyIterator() {
         super(null);
      }

      final void acceptOnIndex(Consumer<? super K> action, int index) {
         action.accept(Object2ObjectOpenHashMap.this.key[index]);
      }

      public K next() {
         return Object2ObjectOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class EntrySpliterator extends Object2ObjectOpenHashMap<K, V>.MapSpliterator<Consumer<? super Object2ObjectMap.Entry<K, V>>, Object2ObjectOpenHashMap<K, V>.EntrySpliterator> implements ObjectSpliterator<Object2ObjectMap.Entry<K, V>> {
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

      final void acceptOnIndex(Consumer<? super Object2ObjectMap.Entry<K, V>> action, int index) {
         action.accept(Object2ObjectOpenHashMap.this.new MapEntry(index));
      }

      final Object2ObjectOpenHashMap<K, V>.EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Object2ObjectOpenHashMap.this.new EntrySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private abstract class MapSpliterator<ConsumerType, SplitType extends Object2ObjectOpenHashMap<K, V>.MapSpliterator<ConsumerType, SplitType>> {
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      MapSpliterator() {
         this.max = Object2ObjectOpenHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Object2ObjectOpenHashMap.this.containsNullKey;
         this.hasSplit = false;
      }

      MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = Object2ObjectOpenHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Object2ObjectOpenHashMap.this.containsNullKey;
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
            this.acceptOnIndex(action, Object2ObjectOpenHashMap.this.n);
            return true;
         } else {
            for(Object[] key = Object2ObjectOpenHashMap.this.key; this.pos < this.max; ++this.pos) {
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
            this.acceptOnIndex(action, Object2ObjectOpenHashMap.this.n);
         }

         for(Object[] key = Object2ObjectOpenHashMap.this.key; this.pos < this.max; ++this.pos) {
            if (key[this.pos] != null) {
               this.acceptOnIndex(action, this.pos);
               ++this.c;
            }
         }

      }

      public long estimateSize() {
         return !this.hasSplit ? (long)(Object2ObjectOpenHashMap.this.size - this.c) : Math.min((long)(Object2ObjectOpenHashMap.this.size - this.c), (long)((double)Object2ObjectOpenHashMap.this.realSize() / (double)Object2ObjectOpenHashMap.this.n * (double)(this.max - this.pos)) + (long)(this.mustReturnNull ? 1 : 0));
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

            Object[] key = Object2ObjectOpenHashMap.this.key;

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

   private final class FastEntryIterator extends Object2ObjectOpenHashMap<K, V>.MapIterator<Consumer<? super Object2ObjectMap.Entry<K, V>>> implements ObjectIterator<Object2ObjectMap.Entry<K, V>> {
      private final Object2ObjectOpenHashMap<K, V>.MapEntry entry;

      private FastEntryIterator() {
         super(null);
         this.entry = Object2ObjectOpenHashMap.this.new MapEntry();
      }

      public Object2ObjectOpenHashMap<K, V>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      final void acceptOnIndex(Consumer<? super Object2ObjectMap.Entry<K, V>> action, int index) {
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
         this.pos = Object2ObjectOpenHashMap.this.n;
         this.last = -1;
         this.c = Object2ObjectOpenHashMap.this.size;
         this.mustReturnNullKey = Object2ObjectOpenHashMap.this.containsNullKey;
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
               return this.last = Object2ObjectOpenHashMap.this.n;
            } else {
               Object[] key = Object2ObjectOpenHashMap.this.key;

               while(--this.pos >= 0) {
                  if (key[this.pos] != null) {
                     return this.last = this.pos;
                  }
               }

               this.last = Integer.MIN_VALUE;
               K k = this.wrapped.get(-this.pos - 1);

               int p;
               for(p = HashCommon.mix(k.hashCode()) & Object2ObjectOpenHashMap.this.mask; !k.equals(key[p]); p = p + 1 & Object2ObjectOpenHashMap.this.mask) {
               }

               return p;
            }
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNullKey) {
            this.mustReturnNullKey = false;
            this.acceptOnIndex(action, this.last = Object2ObjectOpenHashMap.this.n);
            --this.c;
         }

         Object[] key = Object2ObjectOpenHashMap.this.key;

         while(true) {
            while(this.c != 0) {
               if (--this.pos < 0) {
                  this.last = Integer.MIN_VALUE;
                  K k = this.wrapped.get(-this.pos - 1);

                  int p;
                  for(p = HashCommon.mix(k.hashCode()) & Object2ObjectOpenHashMap.this.mask; !k.equals(key[p]); p = p + 1 & Object2ObjectOpenHashMap.this.mask) {
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
         Object[] key = Object2ObjectOpenHashMap.this.key;

         while(true) {
            int last = pos;
            pos = pos + 1 & Object2ObjectOpenHashMap.this.mask;

            Object curr;
            while(true) {
               if ((curr = key[pos]) == null) {
                  key[last] = null;
                  Object2ObjectOpenHashMap.this.value[last] = null;
                  return;
               }

               int slot = HashCommon.mix(curr.hashCode()) & Object2ObjectOpenHashMap.this.mask;
               if (last <= pos) {
                  if (last >= slot || slot > pos) {
                     break;
                  }
               } else if (last >= slot && slot > pos) {
                  break;
               }

               pos = pos + 1 & Object2ObjectOpenHashMap.this.mask;
            }

            if (pos < last) {
               if (this.wrapped == null) {
                  this.wrapped = new ObjectArrayList(2);
               }

               this.wrapped.add(key[pos]);
            }

            key[last] = curr;
            Object2ObjectOpenHashMap.this.value[last] = Object2ObjectOpenHashMap.this.value[pos];
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Object2ObjectOpenHashMap.this.n) {
               Object2ObjectOpenHashMap.this.containsNullKey = false;
               Object2ObjectOpenHashMap.this.key[Object2ObjectOpenHashMap.this.n] = null;
               Object2ObjectOpenHashMap.this.value[Object2ObjectOpenHashMap.this.n] = null;
            } else {
               if (this.pos < 0) {
                  Object2ObjectOpenHashMap.this.remove(this.wrapped.set(-this.pos - 1, (Object)null));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --Object2ObjectOpenHashMap.this.size;
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

   final class MapEntry implements Object2ObjectMap.Entry<K, V>, java.util.Map.Entry<K, V>, Pair<K, V> {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      public K getKey() {
         return Object2ObjectOpenHashMap.this.key[this.index];
      }

      public K left() {
         return Object2ObjectOpenHashMap.this.key[this.index];
      }

      public V getValue() {
         return Object2ObjectOpenHashMap.this.value[this.index];
      }

      public V right() {
         return Object2ObjectOpenHashMap.this.value[this.index];
      }

      public V setValue(V v) {
         V oldValue = Object2ObjectOpenHashMap.this.value[this.index];
         Object2ObjectOpenHashMap.this.value[this.index] = v;
         return oldValue;
      }

      public Pair<K, V> right(V v) {
         Object2ObjectOpenHashMap.this.value[this.index] = v;
         return this;
      }

      public boolean equals(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry<K, V> e = (java.util.Map.Entry)o;
            return Objects.equals(Object2ObjectOpenHashMap.this.key[this.index], e.getKey()) && Objects.equals(Object2ObjectOpenHashMap.this.value[this.index], e.getValue());
         }
      }

      public int hashCode() {
         return (Object2ObjectOpenHashMap.this.key[this.index] == null ? 0 : Object2ObjectOpenHashMap.this.key[this.index].hashCode()) ^ (Object2ObjectOpenHashMap.this.value[this.index] == null ? 0 : Object2ObjectOpenHashMap.this.value[this.index].hashCode());
      }

      public String toString() {
         return Object2ObjectOpenHashMap.this.key[this.index] + "=>" + Object2ObjectOpenHashMap.this.value[this.index];
      }
   }
}
