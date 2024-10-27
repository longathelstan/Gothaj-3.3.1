package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.ints.IntCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.function.ToIntFunction;

public interface Object2IntMap<K> extends Object2IntFunction<K>, Map<K, Integer> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(int var1);

   int defaultReturnValue();

   ObjectSet<Object2IntMap.Entry<K>> object2IntEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<K, Integer>> entrySet() {
      return this.object2IntEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Integer put(K key, Integer value) {
      return Object2IntFunction.super.put(key, value);
   }

   /** @deprecated */
   @Deprecated
   default Integer get(Object key) {
      return Object2IntFunction.super.get(key);
   }

   /** @deprecated */
   @Deprecated
   default Integer remove(Object key) {
      return Object2IntFunction.super.remove(key);
   }

   ObjectSet<K> keySet();

   IntCollection values();

   boolean containsKey(Object var1);

   boolean containsValue(int var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object value) {
      return value == null ? false : this.containsValue((Integer)value);
   }

   default void forEach(BiConsumer<? super K, ? super Integer> consumer) {
      ObjectSet<Object2IntMap.Entry<K>> entrySet = this.object2IntEntrySet();
      Consumer<Object2IntMap.Entry<K>> wrappingConsumer = (entry) -> {
         consumer.accept(entry.getKey(), entry.getIntValue());
      };
      if (entrySet instanceof Object2IntMap.FastEntrySet) {
         ((Object2IntMap.FastEntrySet)entrySet).fastForEach(wrappingConsumer);
      } else {
         entrySet.forEach(wrappingConsumer);
      }

   }

   default int getOrDefault(Object key, int defaultValue) {
      int v;
      return (v = this.getInt(key)) == this.defaultReturnValue() && !this.containsKey(key) ? defaultValue : v;
   }

   /** @deprecated */
   @Deprecated
   default Integer getOrDefault(Object key, Integer defaultValue) {
      return (Integer)super.getOrDefault(key, defaultValue);
   }

   default int putIfAbsent(K key, int value) {
      int v = this.getInt(key);
      int drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         this.put(key, value);
         return drv;
      } else {
         return v;
      }
   }

   default boolean remove(Object key, int value) {
      int curValue = this.getInt(key);
      if (curValue == value && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.removeInt(key);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(K key, int oldValue, int newValue) {
      int curValue = this.getInt(key);
      if (curValue == oldValue && (curValue != this.defaultReturnValue() || this.containsKey(key))) {
         this.put(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   default int replace(K key, int value) {
      return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
   }

   default int computeIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int v = this.getInt(key);
      if (v == this.defaultReturnValue() && !this.containsKey(key)) {
         int newValue = mappingFunction.applyAsInt(key);
         this.put(key, newValue);
         return newValue;
      } else {
         return v;
      }
   }

   /** @deprecated */
   @Deprecated
   default int computeIntIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default int computeIfAbsent(K key, Object2IntFunction<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int v = this.getInt(key);
      int drv = this.defaultReturnValue();
      if (v == drv && !this.containsKey(key)) {
         if (!mappingFunction.containsKey(key)) {
            return drv;
         } else {
            int newValue = mappingFunction.getInt(key);
            this.put(key, newValue);
            return newValue;
         }
      } else {
         return v;
      }
   }

   /** @deprecated */
   @Deprecated
   default int computeIntIfAbsentPartial(K key, Object2IntFunction<? super K> mappingFunction) {
      return this.computeIfAbsent(key, mappingFunction);
   }

   default int computeIntIfPresent(K key, BiFunction<? super K, ? super Integer, ? extends Integer> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int oldValue = this.getInt(key);
      int drv = this.defaultReturnValue();
      if (oldValue == drv && !this.containsKey(key)) {
         return drv;
      } else {
         Integer newValue = (Integer)remappingFunction.apply(key, oldValue);
         if (newValue == null) {
            this.removeInt(key);
            return drv;
         } else {
            int newVal = newValue;
            this.put(key, newVal);
            return newVal;
         }
      }
   }

   default int computeInt(K key, BiFunction<? super K, ? super Integer, ? extends Integer> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int oldValue = this.getInt(key);
      int drv = this.defaultReturnValue();
      boolean contained = oldValue != drv || this.containsKey(key);
      Integer newValue = (Integer)remappingFunction.apply(key, contained ? oldValue : null);
      if (newValue == null) {
         if (contained) {
            this.removeInt(key);
         }

         return drv;
      } else {
         int newVal = newValue;
         this.put(key, newVal);
         return newVal;
      }
   }

   default int merge(K key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int oldValue = this.getInt(key);
      int drv = this.defaultReturnValue();
      int newValue;
      if (oldValue == drv && !this.containsKey(key)) {
         newValue = value;
      } else {
         Integer mergedValue = (Integer)remappingFunction.apply(oldValue, value);
         if (mergedValue == null) {
            this.removeInt(key);
            return drv;
         }

         newValue = mergedValue;
      }

      this.put(key, newValue);
      return newValue;
   }

   default int mergeInt(K key, int value, IntBinaryOperator remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int oldValue = this.getInt(key);
      int drv = this.defaultReturnValue();
      int newValue = oldValue == drv && !this.containsKey(key) ? value : remappingFunction.applyAsInt(oldValue, value);
      this.put(key, newValue);
      return newValue;
   }

   default int mergeInt(K key, int value, com.viaversion.viaversion.libs.fastutil.ints.IntBinaryOperator remappingFunction) {
      return this.mergeInt(key, value, (IntBinaryOperator)remappingFunction);
   }

   /** @deprecated */
   @Deprecated
   default int mergeInt(K key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
      return this.merge(key, value, remappingFunction);
   }

   /** @deprecated */
   @Deprecated
   default Integer putIfAbsent(K key, Integer value) {
      return (Integer)super.putIfAbsent(key, value);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object key, Object value) {
      return super.remove(key, value);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(K key, Integer oldValue, Integer newValue) {
      return super.replace(key, oldValue, newValue);
   }

   /** @deprecated */
   @Deprecated
   default Integer replace(K key, Integer value) {
      return (Integer)super.replace(key, value);
   }

   /** @deprecated */
   @Deprecated
   default Integer merge(K key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
      return (Integer)super.merge(key, value, remappingFunction);
   }

   public interface FastEntrySet<K> extends ObjectSet<Object2IntMap.Entry<K>> {
      ObjectIterator<Object2IntMap.Entry<K>> fastIterator();

      default void fastForEach(Consumer<? super Object2IntMap.Entry<K>> consumer) {
         this.forEach(consumer);
      }
   }

   public interface Entry<K> extends java.util.Map.Entry<K, Integer> {
      int getIntValue();

      int setValue(int var1);

      /** @deprecated */
      @Deprecated
      default Integer getValue() {
         return this.getIntValue();
      }

      /** @deprecated */
      @Deprecated
      default Integer setValue(Integer value) {
         return this.setValue(value);
      }
   }
}
