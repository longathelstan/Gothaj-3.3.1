package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.Size64;
import com.viaversion.viaversion.libs.fastutil.ints.AbstractIntCollection;
import com.viaversion.viaversion.libs.fastutil.ints.IntBinaryOperator;
import com.viaversion.viaversion.libs.fastutil.ints.IntCollection;
import com.viaversion.viaversion.libs.fastutil.ints.IntIterator;
import com.viaversion.viaversion.libs.fastutil.ints.IntSpliterator;
import com.viaversion.viaversion.libs.fastutil.ints.IntSpliterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public abstract class AbstractObject2IntMap<K> extends AbstractObject2IntFunction<K> implements Object2IntMap<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractObject2IntMap() {
   }

   public boolean containsKey(Object k) {
      ObjectIterator i = this.object2IntEntrySet().iterator();

      do {
         if (!i.hasNext()) {
            return false;
         }
      } while(((Object2IntMap.Entry)i.next()).getKey() != k);

      return true;
   }

   public boolean containsValue(int v) {
      ObjectIterator i = this.object2IntEntrySet().iterator();

      do {
         if (!i.hasNext()) {
            return false;
         }
      } while(((Object2IntMap.Entry)i.next()).getIntValue() != v);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public final int mergeInt(K key, int value, IntBinaryOperator remappingFunction) {
      return this.mergeInt(key, value, remappingFunction);
   }

   public ObjectSet<K> keySet() {
      return new AbstractObjectSet<K>() {
         public boolean contains(Object k) {
            return AbstractObject2IntMap.this.containsKey(k);
         }

         public int size() {
            return AbstractObject2IntMap.this.size();
         }

         public void clear() {
            AbstractObject2IntMap.this.clear();
         }

         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Object2IntMap.Entry<K>> i = Object2IntMaps.fastIterator(AbstractObject2IntMap.this);

               public K next() {
                  return ((Object2IntMap.Entry)this.i.next()).getKey();
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
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf((Map)AbstractObject2IntMap.this), 65);
         }
      };
   }

   public IntCollection values() {
      return new AbstractIntCollection() {
         public boolean contains(int k) {
            return AbstractObject2IntMap.this.containsValue(k);
         }

         public int size() {
            return AbstractObject2IntMap.this.size();
         }

         public void clear() {
            AbstractObject2IntMap.this.clear();
         }

         public IntIterator iterator() {
            return new IntIterator() {
               private final ObjectIterator<Object2IntMap.Entry<K>> i = Object2IntMaps.fastIterator(AbstractObject2IntMap.this);

               public int nextInt() {
                  return ((Object2IntMap.Entry)this.i.next()).getIntValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }

               public void remove() {
                  this.i.remove();
               }

               public void forEachRemaining(IntConsumer action) {
                  this.i.forEachRemaining((entry) -> {
                     action.accept(entry.getIntValue());
                  });
               }
            };
         }

         public IntSpliterator spliterator() {
            return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf((Map)AbstractObject2IntMap.this), 320);
         }
      };
   }

   public void putAll(Map<? extends K, ? extends Integer> m) {
      if (m instanceof Object2IntMap) {
         ObjectIterator i = Object2IntMaps.fastIterator((Object2IntMap)m);

         while(i.hasNext()) {
            Object2IntMap.Entry<? extends K> e = (Object2IntMap.Entry)i.next();
            this.put(e.getKey(), e.getIntValue());
         }
      } else {
         int n = m.size();
         Iterator i = m.entrySet().iterator();

         while(n-- != 0) {
            java.util.Map.Entry<? extends K, ? extends Integer> e = (java.util.Map.Entry)i.next();
            this.put(e.getKey(), (Integer)e.getValue());
         }
      }

   }

   public int hashCode() {
      int h = 0;
      int n = this.size();

      for(ObjectIterator i = Object2IntMaps.fastIterator(this); n-- != 0; h += ((Object2IntMap.Entry)i.next()).hashCode()) {
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
         return m.size() != this.size() ? false : this.object2IntEntrySet().containsAll(m.entrySet());
      }
   }

   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<Object2IntMap.Entry<K>> i = Object2IntMaps.fastIterator(this);
      int n = this.size();
      boolean first = true;
      s.append("{");

      while(n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         Object2IntMap.Entry<K> e = (Object2IntMap.Entry)i.next();
         if (this == e.getKey()) {
            s.append("(this map)");
         } else {
            s.append(String.valueOf(e.getKey()));
         }

         s.append("=>");
         s.append(String.valueOf(e.getIntValue()));
      }

      s.append("}");
      return s.toString();
   }

   public abstract static class BasicEntrySet<K> extends AbstractObjectSet<Object2IntMap.Entry<K>> {
      protected final Object2IntMap<K> map;

      public BasicEntrySet(Object2IntMap<K> map) {
         this.map = map;
      }

      public boolean contains(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else {
            Object k;
            if (o instanceof Object2IntMap.Entry) {
               Object2IntMap.Entry<K> e = (Object2IntMap.Entry)o;
               k = e.getKey();
               return this.map.containsKey(k) && this.map.getInt(k) == e.getIntValue();
            } else {
               java.util.Map.Entry<?, ?> e = (java.util.Map.Entry)o;
               k = e.getKey();
               Object value = e.getValue();
               if (value != null && value instanceof Integer) {
                  return this.map.containsKey(k) && this.map.getInt(k) == (Integer)value;
               } else {
                  return false;
               }
            }
         }
      }

      public boolean remove(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else if (o instanceof Object2IntMap.Entry) {
            Object2IntMap.Entry<K> e = (Object2IntMap.Entry)o;
            return this.map.remove(e.getKey(), e.getIntValue());
         } else {
            java.util.Map.Entry<?, ?> e = (java.util.Map.Entry)o;
            Object k = e.getKey();
            Object value = e.getValue();
            if (value != null && value instanceof Integer) {
               int v = (Integer)value;
               return this.map.remove(k, v);
            } else {
               return false;
            }
         }
      }

      public int size() {
         return this.map.size();
      }

      public ObjectSpliterator<Object2IntMap.Entry<K>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf((Map)this.map), 65);
      }
   }

   public static class BasicEntry<K> implements Object2IntMap.Entry<K> {
      protected K key;
      protected int value;

      public BasicEntry() {
      }

      public BasicEntry(K key, Integer value) {
         this.key = key;
         this.value = value;
      }

      public BasicEntry(K key, int value) {
         this.key = key;
         this.value = value;
      }

      public K getKey() {
         return this.key;
      }

      public int getIntValue() {
         return this.value;
      }

      public int setValue(int value) {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else if (o instanceof Object2IntMap.Entry) {
            Object2IntMap.Entry<K> e = (Object2IntMap.Entry)o;
            return Objects.equals(this.key, e.getKey()) && this.value == e.getIntValue();
         } else {
            java.util.Map.Entry<?, ?> e = (java.util.Map.Entry)o;
            Object key = e.getKey();
            Object value = e.getValue();
            if (value != null && value instanceof Integer) {
               return Objects.equals(this.key, key) && this.value == (Integer)value;
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ this.value;
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
