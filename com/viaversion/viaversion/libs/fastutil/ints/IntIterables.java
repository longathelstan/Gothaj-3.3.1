package com.viaversion.viaversion.libs.fastutil.ints;

public final class IntIterables {
   private IntIterables() {
   }

   public static long size(IntIterable iterable) {
      long c = 0L;

      for(IntIterator var3 = iterable.iterator(); var3.hasNext(); ++c) {
         int dummy = (Integer)var3.next();
      }

      return c;
   }
}
