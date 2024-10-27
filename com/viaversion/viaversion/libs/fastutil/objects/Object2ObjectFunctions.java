package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.Function;
import java.io.Serializable;
import java.util.Objects;

public final class Object2ObjectFunctions {
   public static final Object2ObjectFunctions.EmptyFunction EMPTY_FUNCTION = new Object2ObjectFunctions.EmptyFunction();

   private Object2ObjectFunctions() {
   }

   public static <K, V> Object2ObjectFunction<K, V> singleton(K key, V value) {
      return new Object2ObjectFunctions.Singleton(key, value);
   }

   public static <K, V> Object2ObjectFunction<K, V> synchronize(Object2ObjectFunction<K, V> f) {
      return new com.viaversion.viaversion.libs.fastutil.objects.Object2ObjectFunctions.SynchronizedFunction(f);
   }

   public static <K, V> Object2ObjectFunction<K, V> synchronize(Object2ObjectFunction<K, V> f, Object sync) {
      return new com.viaversion.viaversion.libs.fastutil.objects.Object2ObjectFunctions.SynchronizedFunction(f, sync);
   }

   public static <K, V> Object2ObjectFunction<K, V> unmodifiable(Object2ObjectFunction<? extends K, ? extends V> f) {
      return new com.viaversion.viaversion.libs.fastutil.objects.Object2ObjectFunctions.UnmodifiableFunction(f);
   }

   public static class Singleton<K, V> extends AbstractObject2ObjectFunction<K, V> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final K key;
      protected final V value;

      protected Singleton(K key, V value) {
         this.key = key;
         this.value = value;
      }

      public boolean containsKey(Object k) {
         return Objects.equals(this.key, k);
      }

      public V get(Object k) {
         return Objects.equals(this.key, k) ? this.value : this.defRetValue;
      }

      public V getOrDefault(Object k, V defaultValue) {
         return Objects.equals(this.key, k) ? this.value : defaultValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyFunction<K, V> extends AbstractObject2ObjectFunction<K, V> implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      public V get(Object k) {
         return null;
      }

      public V getOrDefault(Object k, V defaultValue) {
         return defaultValue;
      }

      public boolean containsKey(Object k) {
         return false;
      }

      public V defaultReturnValue() {
         return null;
      }

      public void defaultReturnValue(V defRetValue) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return 0;
      }

      public void clear() {
      }

      public Object clone() {
         return Object2ObjectFunctions.EMPTY_FUNCTION;
      }

      public int hashCode() {
         return 0;
      }

      public boolean equals(Object o) {
         if (!(o instanceof Function)) {
            return false;
         } else {
            return ((Function)o).size() == 0;
         }
      }

      public String toString() {
         return "{}";
      }

      private Object readResolve() {
         return Object2ObjectFunctions.EMPTY_FUNCTION;
      }
   }
}
