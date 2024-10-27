package com.viaversion.viaversion.libs.fastutil.ints;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

public final class Int2IntFunctions {
   public static final Int2IntFunctions.EmptyFunction EMPTY_FUNCTION = new Int2IntFunctions.EmptyFunction();

   private Int2IntFunctions() {
   }

   public static Int2IntFunction singleton(int key, int value) {
      return new Int2IntFunctions.Singleton(key, value);
   }

   public static Int2IntFunction singleton(Integer key, Integer value) {
      return new Int2IntFunctions.Singleton(key, value);
   }

   public static Int2IntFunction synchronize(Int2IntFunction f) {
      return new com.viaversion.viaversion.libs.fastutil.ints.Int2IntFunctions.SynchronizedFunction(f);
   }

   public static Int2IntFunction synchronize(Int2IntFunction f, Object sync) {
      return new com.viaversion.viaversion.libs.fastutil.ints.Int2IntFunctions.SynchronizedFunction(f, sync);
   }

   public static Int2IntFunction unmodifiable(Int2IntFunction f) {
      return new com.viaversion.viaversion.libs.fastutil.ints.Int2IntFunctions.UnmodifiableFunction(f);
   }

   public static Int2IntFunction primitive(Function<? super Integer, ? extends Integer> f) {
      Objects.requireNonNull(f);
      if (f instanceof Int2IntFunction) {
         return (Int2IntFunction)f;
      } else if (f instanceof java.util.function.IntUnaryOperator) {
         java.util.function.IntUnaryOperator var10000 = (java.util.function.IntUnaryOperator)f;
         Objects.requireNonNull((java.util.function.IntUnaryOperator)f);
         return var10000::applyAsInt;
      } else {
         return new Int2IntFunctions.PrimitiveFunction(f);
      }
   }

   public static class Singleton extends AbstractInt2IntFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final int key;
      protected final int value;

      protected Singleton(int key, int value) {
         this.key = key;
         this.value = value;
      }

      public boolean containsKey(int k) {
         return this.key == k;
      }

      public int get(int k) {
         return this.key == k ? this.value : this.defRetValue;
      }

      public int getOrDefault(int k, int defaultValue) {
         return this.key == k ? this.value : defaultValue;
      }

      public int size() {
         return 1;
      }

      public Object clone() {
         return this;
      }
   }

   public static class PrimitiveFunction implements Int2IntFunction {
      protected final Function<? super Integer, ? extends Integer> function;

      protected PrimitiveFunction(Function<? super Integer, ? extends Integer> function) {
         this.function = function;
      }

      public boolean containsKey(int key) {
         return this.function.apply(key) != null;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsKey(Object key) {
         if (key == null) {
            return false;
         } else {
            return this.function.apply((Integer)key) != null;
         }
      }

      public int get(int key) {
         Integer v = (Integer)this.function.apply(key);
         return v == null ? this.defaultReturnValue() : v;
      }

      public int getOrDefault(int key, int defaultValue) {
         Integer v = (Integer)this.function.apply(key);
         return v == null ? defaultValue : v;
      }

      /** @deprecated */
      @Deprecated
      public Integer get(Object key) {
         return key == null ? null : (Integer)this.function.apply((Integer)key);
      }

      /** @deprecated */
      @Deprecated
      public Integer getOrDefault(Object key, Integer defaultValue) {
         if (key == null) {
            return defaultValue;
         } else {
            Integer v;
            return (v = (Integer)this.function.apply((Integer)key)) == null ? defaultValue : v;
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer put(Integer key, Integer value) {
         throw new UnsupportedOperationException();
      }
   }

   public static class EmptyFunction extends AbstractInt2IntFunction implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyFunction() {
      }

      public int get(int k) {
         return 0;
      }

      public int getOrDefault(int k, int defaultValue) {
         return defaultValue;
      }

      public boolean containsKey(int k) {
         return false;
      }

      public int defaultReturnValue() {
         return 0;
      }

      public void defaultReturnValue(int defRetValue) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return 0;
      }

      public void clear() {
      }

      public Object clone() {
         return Int2IntFunctions.EMPTY_FUNCTION;
      }

      public int hashCode() {
         return 0;
      }

      public boolean equals(Object o) {
         if (!(o instanceof com.viaversion.viaversion.libs.fastutil.Function)) {
            return false;
         } else {
            return ((com.viaversion.viaversion.libs.fastutil.Function)o).size() == 0;
         }
      }

      public String toString() {
         return "{}";
      }

      private Object readResolve() {
         return Int2IntFunctions.EMPTY_FUNCTION;
      }
   }
}
