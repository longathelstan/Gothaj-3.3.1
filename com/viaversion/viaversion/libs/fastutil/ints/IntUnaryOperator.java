package com.viaversion.viaversion.libs.fastutil.ints;

import java.util.function.UnaryOperator;

@FunctionalInterface
public interface IntUnaryOperator extends UnaryOperator<Integer>, java.util.function.IntUnaryOperator {
   int apply(int var1);

   static IntUnaryOperator identity() {
      return (i) -> {
         return i;
      };
   }

   static IntUnaryOperator negation() {
      return (i) -> {
         return -i;
      };
   }

   /** @deprecated */
   @Deprecated
   default int applyAsInt(int x) {
      return this.apply(x);
   }

   /** @deprecated */
   @Deprecated
   default Integer apply(Integer x) {
      return this.apply(x);
   }
}
