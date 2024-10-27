package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.Size64;
import com.viaversion.viaversion.libs.fastutil.objects.AbstractObjectCollection;
import com.viaversion.viaversion.libs.fastutil.objects.AbstractObjectSet;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectCollection;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSpliterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class AbstractInt2ObjectMap<V> extends AbstractInt2ObjectFunction<V> implements Int2ObjectMap<V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractInt2ObjectMap() {
   }

   public boolean containsKey(int k) {
      ObjectIterator i = this.int2ObjectEntrySet().iterator();

      do {
         if (!i.hasNext()) {
            return false;
         }
      } while(((Int2ObjectMap.Entry)i.next()).getIntKey() != k);

      return true;
   }

   public boolean containsValue(Object v) {
      ObjectIterator i = this.int2ObjectEntrySet().iterator();

      do {
         if (!i.hasNext()) {
            return false;
         }
      } while(((Int2ObjectMap.Entry)i.next()).getValue() != v);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public IntSet keySet() {
      return new AbstractIntSet() {
         public boolean contains(int k) {
            return AbstractInt2ObjectMap.this.containsKey(k);
         }

         public int size() {
            return AbstractInt2ObjectMap.this.size();
         }

         public void clear() {
            AbstractInt2ObjectMap.this.clear();
         }

         public IntIterator iterator() {
            return new IntIterator() {
               private final ObjectIterator<Int2ObjectMap.Entry<V>> i = Int2ObjectMaps.fastIterator(AbstractInt2ObjectMap.this);

               public int nextInt() {
                  return ((Int2ObjectMap.Entry)this.i.next()).getIntKey();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }

               public void remove() {
                  this.i.remove();
               }

               public void forEachRemaining(java.util.function.IntConsumer action) {
                  this.i.forEachRemaining((entry) -> {
                     action.accept(entry.getIntKey());
                  });
               }
            };
         }

         public IntSpliterator spliterator() {
            return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf((Map)AbstractInt2ObjectMap.this), 321);
         }
      };
   }

   public ObjectCollection<V> values() {
      return new AbstractObjectCollection<V>() {
         public boolean contains(Object k) {
            return AbstractInt2ObjectMap.this.containsValue(k);
         }

         public int size() {
            return AbstractInt2ObjectMap.this.size();
         }

         public void clear() {
            AbstractInt2ObjectMap.this.clear();
         }

         public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>() {
               private final ObjectIterator<Int2ObjectMap.Entry<V>> i = Int2ObjectMaps.fastIterator(AbstractInt2ObjectMap.this);

               public V next() {
                  return ((Int2ObjectMap.Entry)this.i.next()).getValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }

               public void remove() {
                  this.i.remove();
               }

               public void forEachRemaining(Consumer<? super V> action) {
                  this.i.forEachRemaining((entry) -> {
                     action.accept(entry.getValue());
                  });
               }
            };
         }

         public ObjectSpliterator<V> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf((Map)AbstractInt2ObjectMap.this), 64);
         }
      };
   }

   public void putAll(Map<? extends Integer, ? extends V> m) {
      if (m instanceof Int2ObjectMap) {
         ObjectIterator i = Int2ObjectMaps.fastIterator((Int2ObjectMap)m);

         while(i.hasNext()) {
            Int2ObjectMap.Entry<? extends V> e = (Int2ObjectMap.Entry)i.next();
            this.put(e.getIntKey(), e.getValue());
         }
      } else {
         int n = m.size();
         Iterator i = m.entrySet().iterator();

         while(n-- != 0) {
            java.util.Map.Entry<? extends Integer, ? extends V> e = (java.util.Map.Entry)i.next();
            this.put((Integer)e.getKey(), e.getValue());
         }
      }

   }

   public int hashCode() {
      int h = 0;
      int n = this.size();

      for(ObjectIterator i = Int2ObjectMaps.fastIterator(this); n-- != 0; h += ((Int2ObjectMap.Entry)i.next()).hashCode()) {
      }

      return h;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Map)) {
         return false;
      } else {
         Map<?, ?> m = (Map)o;
         return m.size() != this.size() ? false : this.int2ObjectEntrySet().containsAll(m.entrySet());
      }
   }

   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Int2ObjectMap.Entry<V>> i = Int2ObjectMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while(n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Int2ObjectMap.Entry<V> e = (Int2ObjectMap.Entry)i.next();
         s.append(String.valueOf(e.getIntKey()));
         s.append("=>");
         if (this == e.getValue()) {
            s.append("(this map)");
         } else {
            s.append(String.valueOf(e.getValue()));
         }
      }

      s.append("}");
      return s.toString();
   }

   public abstract static class BasicEntrySet<V> extends AbstractObjectSet<Int2ObjectMap.Entry<V>> {
      protected final Int2ObjectMap<V> map;

      public BasicEntrySet(Int2ObjectMap<V> map) {
         this.map = map;
      }

      public boolean contains(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else if (o instanceof Int2ObjectMap.Entry) {
            Int2ObjectMap.Entry<V> e = (Int2ObjectMap.Entry)o;
            int k = e.getIntKey();
            return this.map.containsKey(k) && Objects.equals(this.map.get(k), e.getValue());
         } else {
            java.util.Map.Entry<?, ?> e = (java.util.Map.Entry)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               int k = (Integer)key;
               Object value = e.getValue();
               return this.map.containsKey(k) && Objects.equals(this.map.get(k), value);
            } else {
               return false;
            }
         }
      }

      public boolean remove(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else if (o instanceof Int2ObjectMap.Entry) {
            Int2ObjectMap.Entry<V> e = (Int2ObjectMap.Entry)o;
            return this.map.remove(e.getIntKey(), e.getValue());
         } else {
            java.util.Map.Entry<?, ?> e = (java.util.Map.Entry)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               int k = (Integer)key;
               Object v = e.getValue();
               return this.map.remove(k, v);
            } else {
               return false;
            }
         }
      }

      public int size() {
         return this.map.size();
      }

      public ObjectSpliterator<Int2ObjectMap.Entry<V>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf((Map)this.map), 65);
      }
   }

   public static class BasicEntry<V> implements Int2ObjectMap.Entry<V> {
      protected int key;
      protected V value;

      public BasicEntry() {
      }

      public BasicEntry(Integer key, V value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(int key, V value) {
         this.key = key;
         this.value = value;
      }

      public int getIntKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      public V setValue(V value) {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else if (o instanceof Int2ObjectMap.Entry) {
            Int2ObjectMap.Entry<V> e = (Int2ObjectMap.Entry)o;
            return this.key == e.getIntKey() && Objects.equals(this.value, e.getValue());
         } else {
            java.util.Map.Entry<?, ?> e = (java.util.Map.Entry)o;
            Object key = e.getKey();
            if (key != null && key instanceof Integer) {
               Object value = e.getValue();
               return this.key == (Integer)key && Objects.equals(this.value, value);
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return this.key ^ (this.value == null ? 0 : this.value.hashCode());
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
