package com.viaversion.viaversion.libs.fastutil.ints;

import java.util.function.BinaryOperator;

@FunctionalInterface
public interface IntBinaryOperator extends BinaryOperator<Integer>, java.util.function.IntBinaryOperator {
   int apply(int var1, int var2);

   /** @deprecated */
   @Deprecated
   default int applyAsInt(int x, int y) {
      return this.apply(x, y);
   }

   /** @deprecated */
   @Deprecated
   default Integer apply(Integer x, Integer y) {
      return this.apply(x, y);
   }
}
