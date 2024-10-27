package com.viaversion.viaversion.libs.fastutil.ints;

import java.util.ListIterator;

public interface IntListIterator extends IntBidirectionalIterator, ListIterator<Integer> {
   default void set(int k) {
      throw new UnsupportedOperationException();
   }

   default void add(int k) {
      throw new UnsupportedOperationException();
   }

   default void remove() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default void set(Integer k) {
      this.set(k);
   }

   /** @deprecated */
   @Deprecated
   default void add(Integer k) {
      this.add(k);
   }

   /** @deprecated */
   @Deprecated
   default Integer next() {
      return IntBidirectionalIterator.super.next();
   }

   /** @deprecated */
   @Deprecated
   default Integer previous() {
      return IntBidirectionalIterator.super.previous();
   }
}
