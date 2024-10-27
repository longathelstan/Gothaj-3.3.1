package com.viaversion.viaversion.libs.fastutil.objects;

import java.util.Iterator;

public final class ObjectIterables {
   private ObjectIterables() {
   }

   public static <K> long size(Iterable<K> iterable) {
      long c = 0L;

      for(Iterator var3 = iterable.iterator(); var3.hasNext(); ++c) {
         K dummy = var3.next();
      }

      return c;
   }
}
