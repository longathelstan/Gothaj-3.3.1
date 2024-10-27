package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.Size64;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class AbstractObject2ObjectMap<K, V> extends AbstractObject2ObjectFunction<K, V> implements Object2ObjectMap<K, V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractObject2ObjectMap() {
   }

   public boolean containsKey(Object k) {
      ObjectIterator i = this.object2ObjectEntrySet().iterator();

      do {
         if (!i.hasNext()) {
            return false;
         }
      } while(((Object2ObjectMap.Entry)i.next()).getKey() != k);

      return true;
   }

   public boolean containsValue(Object v) {
      ObjectIterator i = this.object2ObjectEntrySet().iterator();

      do {
         if (!i.hasNext()) {
            return false;
         }
      } while(((Object2ObjectMap.Entry)i.next()).getValue() != v);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public ObjectSet<K> keySet() {
      return new AbstractObjectSet<K>() {
         public boolean contains(Object k) {
            return AbstractObject2ObjectMap.this.containsKey(k);
         }

         public int size() {
            return AbstractObject2ObjectMap.this.size();
         }

         public void clear() {
            AbstractObject2ObjectMap.this.clear();
         }

         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Object2ObjectMap.Entry<K, V>> i = Object2ObjectMaps.fastIterator(AbstractObject2ObjectMap.this);

               public K next() {
                  return ((Object2ObjectMap.Entry)this.i.next()).getKey();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }

               public void remove() {
                  this.i.remove();
               }

               public void forEachRemaining(Consumer<? super K> action) {
                  this.i.forEachRemaining((entry) -> {
                     action.accept(entry.getKey());
                  });
               }
            };
         }

         public ObjectSpliterator<K> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf((Map)AbstractObject2ObjectMap.this), 65);
         }
      };
   }

   public ObjectCollection<V> values() {
      return new AbstractObjectCollection<V>() {
         public boolean contains(Object k) {
            return AbstractObject2ObjectMap.this.containsValue(k);
         }

         public int size() {
            return AbstractObject2ObjectMap.this.size();
         }

         public void clear() {
            AbstractObject2ObjectMap.this.clear();
         }

         public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>() {
               private final ObjectIterator<Object2ObjectMap.Entry<K, V>> i = Object2ObjectMaps.fastIterator(AbstractObject2ObjectMap.this);

               public V next() {
                  return ((Object2ObjectMap.Entry)this.i.next()).getValue();
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
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf((Map)AbstractObject2ObjectMap.this), 64);
         }
      };
   }

   public void putAll(Map<? extends K, ? extends V> m) {
      if (m instanceof Object2ObjectMap) {
         ObjectIterator i = Object2ObjectMaps.fastIterator((Object2ObjectMap)m);

         while(i.hasNext()) {
            Object2ObjectMap.Entry<? extends K, ? extends V> e = (Object2ObjectMap.Entry)i.next();
            this.put(e.getKey(), e.getValue());
         }
      } else {
         int n = m.size();
         Iterator i = m.entrySet().iterator();

         while(n-- != 0) {
            java.util.Map.Entry<? extends K, ? extends V> e = (java.util.Map.Entry)i.next();
            this.put(e.getKey(), e.getValue());
         }
      }

   }

   public int hashCode() {
      int h = 0;
      int n = this.size();

      for(ObjectIterator i = Object2ObjectMaps.fastIterator(this); n-- != 0; h += ((Object2ObjectMap.Entry)i.next()).hashCode()) {
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
         return m.size() != this.size() ? false : this.object2ObjectEntrySet().containsAll(m.entrySet());
      }
   }

   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Object2ObjectMap.Entry<K, V>> i = Object2ObjectMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while(n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Object2ObjectMap.Entry<K, V> e = (Object2ObjectMap.Entry)i.next();
         if (this == e.getKey()) {
            s.append("(this map)");
         } else {
            s.append(String.valueOf(e.getKey()));
         }

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

   public abstract static class BasicEntrySet<K, V> extends AbstractObjectSet<Object2ObjectMap.Entry<K, V>> {
      protected final Object2ObjectMap<K, V> map;

      public BasicEntrySet(Object2ObjectMap<K, V> map) {
         this.map = map;
      }

      public boolean contains(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else {
            Object k;
            if (o instanceof Object2ObjectMap.Entry) {
               Object2ObjectMap.Entry<K, V> e = (Object2ObjectMap.Entry)o;
               k = e.getKey();
               return this.map.containsKey(k) && Objects.equals(this.map.get(k), e.getValue());
            } else {
               java.util.Map.Entry<?, ?> e = (java.util.Map.Entry)o;
               k = e.getKey();
               Object value = e.getValue();
               return this.map.containsKey(k) && Objects.equals(this.map.get(k), value);
            }
         }
      }

      public boolean remove(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else if (o instanceof Object2ObjectMap.Entry) {
            Object2ObjectMap.Entry<K, V> e = (Object2ObjectMap.Entry)o;
            return this.map.remove(e.getKey(), e.getValue());
         } else {
            java.util.Map.Entry<?, ?> e = (java.util.Map.Entry)o;
            Object k = e.getKey();
            Object v = e.getValue();
            return this.map.remove(k, v);
         }
      }

      public int size() {
         return this.map.size();
      }

      public ObjectSpliterator<Object2ObjectMap.Entry<K, V>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf((Map)this.map), 65);
      }
   }

   public static class BasicEntry<K, V> implements Object2ObjectMap.Entry<K, V> {
      protected K key;
      protected V value;

      public BasicEntry() {
      }

      public BasicEntry(K key, V value) {
         this.key = key;
         this.value = value;
      }

      public K getKey() {
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
         } else if (o instanceof Object2ObjectMap.Entry) {
            Object2ObjectMap.Entry<K, V> e = (Object2ObjectMap.Entry)o;
            return Objects.equals(this.key, e.getKey()) && Objects.equals(this.value, e.getValue());
         } else {
            java.util.Map.Entry<?, ?> e = (java.util.Map.Entry)o;
            Object key = e.getKey();
            Object value = e.getValue();
            return Objects.equals(this.key, key) && Objects.equals(this.value, value);
         }
      }

      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
