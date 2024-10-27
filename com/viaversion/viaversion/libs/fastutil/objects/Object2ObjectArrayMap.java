package com.viaversion.viaversion.libs.fastutil.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

public class Object2ObjectArrayMap<K, V> extends AbstractObject2ObjectMap<K, V> implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   protected transient Object[] key;
   protected transient Object[] value;
   protected int size;
   protected transient Object2ObjectMap.FastEntrySet<K, V> entries;
   protected transient ObjectSet<K> keys;
   protected transient ObjectCollection<V> values;

   public Object2ObjectArrayMap(Object[] key, Object[] value) {
      this.key = key;
      this.value = value;
      this.size = key.length;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      }
   }

   public Object2ObjectArrayMap() {
      this.key = ObjectArrays.EMPTY_ARRAY;
      this.value = ObjectArrays.EMPTY_ARRAY;
   }

   public Object2ObjectArrayMap(int capacity) {
      this.key = new Object[capacity];
      this.value = new Object[capacity];
   }

   public Object2ObjectArrayMap(Object2ObjectMap<K, V> m) {
      this(m.size());
      int i = 0;

      for(ObjectIterator var3 = m.object2ObjectEntrySet().iterator(); var3.hasNext(); ++i) {
         Object2ObjectMap.Entry<K, V> e = (Object2ObjectMap.Entry)var3.next();
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
      }

      this.size = i;
   }

   public Object2ObjectArrayMap(Map<? extends K, ? extends V> m) {
      this(m.size());
      int i = 0;

      for(Iterator var3 = m.entrySet().iterator(); var3.hasNext(); ++i) {
         java.util.Map.Entry<? extends K, ? extends V> e = (java.util.Map.Entry)var3.next();
         this.key[i] = e.getKey();
         this.value[i] = e.getValue();
      }

      this.size = i;
   }

   public Object2ObjectArrayMap(Object[] key, Object[] value, int size) {
      this.key = key;
      this.value = value;
      this.size = size;
      if (key.length != value.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
      } else if (size > key.length) {
         throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
      }
   }

   public Object2ObjectMap.FastEntrySet<K, V> object2ObjectEntrySet() {
      if (this.entries == null) {
         this.entries = new Object2ObjectArrayMap.EntrySet();
      }

      return this.entries;
   }

   private int findKey(Object k) {
      Object[] key = this.key;
      int i = this.size;

      do {
         if (i-- == 0) {
            return -1;
         }
      } while(!Objects.equals(key[i], k));

      return i;
   }

   public V get(Object k) {
      Object[] key = this.key;
      int i = this.size;

      do {
         if (i-- == 0) {
            return this.defRetValue;
         }
      } while(!Objects.equals(key[i], k));

      return this.value[i];
   }

   public int size() {
      return this.size;
   }

   public void clear() {
      for(int i = this.size; i-- != 0; this.value[i] = null) {
         this.key[i] = null;
      }

      this.size = 0;
   }

   public boolean containsKey(Object k) {
      return this.findKey(k) != -1;
   }

   public boolean containsValue(Object v) {
      int i = this.size;

      do {
         if (i-- == 0) {
            return false;
         }
      } while(!Objects.equals(this.value[i], v));

      return true;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public V put(K k, V v) {
      int oldKey = this.findKey(k);
      if (oldKey != -1) {
         V oldValue = this.value[oldKey];
         this.value[oldKey] = v;
         return oldValue;
      } else {
         if (this.size == this.key.length) {
            Object[] newKey = new Object[this.size == 0 ? 2 : this.size * 2];
            Object[] newValue = new Object[this.size == 0 ? 2 : this.size * 2];

            for(int i = this.size; i-- != 0; newValue[i] = this.value[i]) {
               newKey[i] = this.key[i];
            }

            this.key = newKey;
            this.value = newValue;
         }

         this.key[this.size] = k;
         this.value[this.size] = v;
         ++this.size;
         return this.defRetValue;
      }
   }

   public V remove(Object k) {
      int oldPos = this.findKey(k);
      if (oldPos == -1) {
         return this.defRetValue;
      } else {
         V oldValue = this.value[oldPos];
         int tail = this.size - oldPos - 1;
         System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
         System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
         --this.size;
         this.key[this.size] = null;
         this.value[this.size] = null;
         return oldValue;
      }
   }

   public ObjectSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Object2ObjectArrayMap.KeySet();
      }

      return this.keys;
   }

   public ObjectCollection<V> values() {
      if (this.values == null) {
         this.values = new Object2ObjectArrayMap.ValuesCollection();
      }

      return this.values;
   }

   public Object2ObjectArrayMap<K, V> clone() {
      Object2ObjectArrayMap c;
      try {
         c = (Object2ObjectArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (Object[])this.key.clone();
      c.value = (Object[])this.value.clone();
      c.entries = null;
      c.keys = null;
      c.values = null;
      return c;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();
      int i = 0;

      for(int max = this.size; i < max; ++i) {
         s.writeObject(this.key[i]);
         s.writeObject(this.value[i]);
      }

   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.key = new Object[this.size];
      this.value = new Object[this.size];

      for(int i = 0; i < this.size; ++i) {
         this.key[i] = s.readObject();
         this.value[i] = s.readObject();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Object2ObjectMap.Entry<K, V>> implements Object2ObjectMap.FastEntrySet<K, V> {
      private EntrySet() {
      }

      public ObjectIterator<Object2ObjectMap.Entry<K, V>> iterator() {
         return new ObjectIterator<Object2ObjectMap.Entry<K, V>>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Object2ObjectArrayMap.this.size;
            }

            public Object2ObjectMap.Entry<K, V> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractObject2ObjectMap.BasicEntry(Object2ObjectArrayMap.this.key[this.curr = this.next], Object2ObjectArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Object2ObjectArrayMap.this.size-- - this.next--;
                  System.arraycopy(Object2ObjectArrayMap.this.key, this.next + 1, Object2ObjectArrayMap.this.key, this.next, tail);
                  System.arraycopy(Object2ObjectArrayMap.this.value, this.next + 1, Object2ObjectArrayMap.this.value, this.next, tail);
                  Object2ObjectArrayMap.this.key[Object2ObjectArrayMap.this.size] = null;
                  Object2ObjectArrayMap.this.value[Object2ObjectArrayMap.this.size] = null;
               }
            }

            public void forEachRemaining(Consumer<? super Object2ObjectMap.Entry<K, V>> action) {
               int max = Object2ObjectArrayMap.this.size;

               while(this.next < max) {
                  action.accept(new AbstractObject2ObjectMap.BasicEntry(Object2ObjectArrayMap.this.key[this.curr = this.next], Object2ObjectArrayMap.this.value[this.next++]));
               }

            }
         };
      }

      public ObjectIterator<Object2ObjectMap.Entry<K, V>> fastIterator() {
         return new ObjectIterator<Object2ObjectMap.Entry<K, V>>() {
            int next = 0;
            int curr = -1;
            final AbstractObject2ObjectMap.BasicEntry<K, V> entry = new AbstractObject2ObjectMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Object2ObjectArrayMap.this.size;
            }

            public Object2ObjectMap.Entry<K, V> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Object2ObjectArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Object2ObjectArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int tail = Object2ObjectArrayMap.this.size-- - this.next--;
                  System.arraycopy(Object2ObjectArrayMap.this.key, this.next + 1, Object2ObjectArrayMap.this.key, this.next, tail);
                  System.arraycopy(Object2ObjectArrayMap.this.value, this.next + 1, Object2ObjectArrayMap.this.value, this.next, tail);
                  Object2ObjectArrayMap.this.key[Object2ObjectArrayMap.this.size] = null;
                  Object2ObjectArrayMap.this.value[Object2ObjectArrayMap.this.size] = null;
               }
            }

            public void forEachRemaining(Consumer<? super Object2ObjectMap.Entry<K, V>> action) {
               int max = Object2ObjectArrayMap.this.size;

               while(this.next < max) {
                  this.entry.key = Object2ObjectArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Object2ObjectArrayMap.this.value[this.next++];
                  action.accept(this.entry);
               }

            }
         };
      }

      public ObjectSpliterator<Object2ObjectMap.Entry<K, V>> spliterator() {
         return new Object2ObjectArrayMap.EntrySet.EntrySetSpliterator(0, Object2ObjectArrayMap.this.size);
      }

      public void forEach(Consumer<? super Object2ObjectMap.Entry<K, V>> action) {
         int i = 0;

         for(int max = Object2ObjectArrayMap.this.size; i < max; ++i) {
            action.accept(new AbstractObject2ObjectMap.BasicEntry(Object2ObjectArrayMap.this.key[i], Object2ObjectArrayMap.this.value[i]));
         }

      }

      public void fastForEach(Consumer<? super Object2ObjectMap.Entry<K, V>> action) {
         AbstractObject2ObjectMap.BasicEntry<K, V> entry = new AbstractObject2ObjectMap.BasicEntry();
         int i = 0;

         for(int max = Object2ObjectArrayMap.this.size; i < max; ++i) {
            entry.key = Object2ObjectArrayMap.this.key[i];
            entry.value = Object2ObjectArrayMap.this.value[i];
            action.accept(entry);
         }

      }

      public int size() {
         return Object2ObjectArrayMap.this.size;
      }

      public boolean contains(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry<?, ?> e = (java.util.Map.Entry)o;
            K k = e.getKey();
            return Object2ObjectArrayMap.this.containsKey(k) && Objects.equals(Object2ObjectArrayMap.this.get(k), e.getValue());
         }
      }

      public boolean remove(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry<?, ?> e = (java.util.Map.Entry)o;
            K k = e.getKey();
            V v = e.getValue();
            int oldPos = Object2ObjectArrayMap.this.findKey(k);
            if (oldPos != -1 && Objects.equals(v, Object2ObjectArrayMap.this.value[oldPos])) {
               int tail = Object2ObjectArrayMap.this.size - oldPos - 1;
               System.arraycopy(Object2ObjectArrayMap.this.key, oldPos + 1, Object2ObjectArrayMap.this.key, oldPos, tail);
               System.arraycopy(Object2ObjectArrayMap.this.value, oldPos + 1, Object2ObjectArrayMap.this.value, oldPos, tail);
               --Object2ObjectArrayMap.this.size;
               Object2ObjectArrayMap.this.key[Object2ObjectArrayMap.this.size] = null;
               Object2ObjectArrayMap.this.value[Object2ObjectArrayMap.this.size] = null;
               return true;
            } else {
               return false;
            }
         }
      }

      // $FF: synthetic method
      EntrySet(Object x1) {
         this();
      }

      final class EntrySetSpliterator extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Object2ObjectMap.Entry<K, V>> implements ObjectSpliterator<Object2ObjectMap.Entry<K, V>> {
         EntrySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         public int characteristics() {
            return 16465;
         }

         protected final Object2ObjectMap.Entry<K, V> get(int location) {
            return new AbstractObject2ObjectMap.BasicEntry(Object2ObjectArrayMap.this.key[location], Object2ObjectArrayMap.this.value[location]);
         }

         protected final Object2ObjectArrayMap<K, V>.EntrySet.EntrySetSpliterator makeForSplit(int pos, int maxPos) {
            return EntrySet.this.new EntrySetSpliterator(pos, maxPos);
         }
      }
   }

   private final class KeySet extends AbstractObjectSet<K> {
      private KeySet() {
      }

      public boolean contains(Object k) {
         return Object2ObjectArrayMap.this.findKey(k) != -1;
      }

      public boolean remove(Object k) {
         int oldPos = Object2ObjectArrayMap.this.findKey(k);
         if (oldPos == -1) {
            return false;
         } else {
            int tail = Object2ObjectArrayMap.this.size - oldPos - 1;
            System.arraycopy(Object2ObjectArrayMap.this.key, oldPos + 1, Object2ObjectArrayMap.this.key, oldPos, tail);
            System.arraycopy(Object2ObjectArrayMap.this.value, oldPos + 1, Object2ObjectArrayMap.this.value, oldPos, tail);
            --Object2ObjectArrayMap.this.size;
            Object2ObjectArrayMap.this.key[Object2ObjectArrayMap.this.size] = null;
            Object2ObjectArrayMap.this.value[Object2ObjectArrayMap.this.size] = null;
            return true;
         }
      }

      public ObjectIterator<K> iterator() {
         return new ObjectIterator<K>() {
            int pos = 0;

            public boolean hasNext() {
               return this.pos < Object2ObjectArrayMap.this.size;
            }

            public K next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Object2ObjectArrayMap.this.key[this.pos++];
               }
            }

            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Object2ObjectArrayMap.this.size - this.pos;
                  System.arraycopy(Object2ObjectArrayMap.this.key, this.pos, Object2ObjectArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Object2ObjectArrayMap.this.value, this.pos, Object2ObjectArrayMap.this.value, this.pos - 1, tail);
                  --Object2ObjectArrayMap.this.size;
                  --this.pos;
                  Object2ObjectArrayMap.this.key[Object2ObjectArrayMap.this.size] = null;
                  Object2ObjectArrayMap.this.value[Object2ObjectArrayMap.this.size] = null;
               }
            }

            public void forEachRemaining(Consumer<? super K> action) {
               int max = Object2ObjectArrayMap.this.size;

               while(this.pos < max) {
                  action.accept(Object2ObjectArrayMap.this.key[this.pos++]);
               }

            }
         };
      }

      public ObjectSpliterator<K> spliterator() {
         return new Object2ObjectArrayMap.KeySet.KeySetSpliterator(0, Object2ObjectArrayMap.this.size);
      }

      public void forEach(Consumer<? super K> action) {
         int i = 0;

         for(int max = Object2ObjectArrayMap.this.size; i < max; ++i) {
            action.accept(Object2ObjectArrayMap.this.key[i]);
         }

      }

      public int size() {
         return Object2ObjectArrayMap.this.size;
      }

      public void clear() {
         Object2ObjectArrayMap.this.clear();
      }

      // $FF: synthetic method
      KeySet(Object x1) {
         this();
      }

      final class KeySetSpliterator extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<K> implements ObjectSpliterator<K> {
         KeySetSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         public int characteristics() {
            return 16465;
         }

         protected final K get(int location) {
            return Object2ObjectArrayMap.this.key[location];
         }

         protected final Object2ObjectArrayMap<K, V>.KeySet.KeySetSpliterator makeForSplit(int pos, int maxPos) {
            return KeySet.this.new KeySetSpliterator(pos, maxPos);
         }

         public void forEachRemaining(Consumer<? super K> action) {
            int max = Object2ObjectArrayMap.this.size;

            while(this.pos < max) {
               action.accept(Object2ObjectArrayMap.this.key[this.pos++]);
            }

         }
      }
   }

   private final class ValuesCollection extends AbstractObjectCollection<V> {
      private ValuesCollection() {
      }

      public boolean contains(Object v) {
         return Object2ObjectArrayMap.this.containsValue(v);
      }

      public ObjectIterator<V> iterator() {
         return new ObjectIterator<V>() {
            int pos = 0;

            public boolean hasNext() {
               return this.pos < Object2ObjectArrayMap.this.size;
            }

            public V next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return Object2ObjectArrayMap.this.value[this.pos++];
               }
            }

            public void remove() {
               if (this.pos == 0) {
                  throw new IllegalStateException();
               } else {
                  int tail = Object2ObjectArrayMap.this.size - this.pos;
                  System.arraycopy(Object2ObjectArrayMap.this.key, this.pos, Object2ObjectArrayMap.this.key, this.pos - 1, tail);
                  System.arraycopy(Object2ObjectArrayMap.this.value, this.pos, Object2ObjectArrayMap.this.value, this.pos - 1, tail);
                  --Object2ObjectArrayMap.this.size;
                  --this.pos;
                  Object2ObjectArrayMap.this.key[Object2ObjectArrayMap.this.size] = null;
                  Object2ObjectArrayMap.this.value[Object2ObjectArrayMap.this.size] = null;
               }
            }

            public void forEachRemaining(Consumer<? super V> action) {
               int max = Object2ObjectArrayMap.this.size;

               while(this.pos < max) {
                  action.accept(Object2ObjectArrayMap.this.value[this.pos++]);
               }

            }
         };
      }

      public ObjectSpliterator<V> spliterator() {
         return new Object2ObjectArrayMap.ValuesCollection.ValuesSpliterator(0, Object2ObjectArrayMap.this.size);
      }

      public void forEach(Consumer<? super V> action) {
         int i = 0;

         for(int max = Object2ObjectArrayMap.this.size; i < max; ++i) {
            action.accept(Object2ObjectArrayMap.this.value[i]);
         }

      }

      public int size() {
         return Object2ObjectArrayMap.this.size;
      }

      public void clear() {
         Object2ObjectArrayMap.this.clear();
      }

      // $FF: synthetic method
      ValuesCollection(Object x1) {
         this();
      }

      final class ValuesSpliterator extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<V> implements ObjectSpliterator<V> {
         ValuesSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         public int characteristics() {
            return 16464;
         }

         protected final V get(int location) {
            return Object2ObjectArrayMap.this.value[location];
         }

         protected final Object2ObjectArrayMap<K, V>.ValuesCollection.ValuesSpliterator makeForSplit(int pos, int maxPos) {
            return ValuesCollection.this.new ValuesSpliterator(pos, maxPos);
         }

         public void forEachRemaining(Consumer<? super V> action) {
            int max = Object2ObjectArrayMap.this.size;

            while(this.pos < max) {
               action.accept(Object2ObjectArrayMap.this.value[this.pos++]);
            }

         }
      }
   }
}
