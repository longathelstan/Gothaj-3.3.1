package com.viaversion.viaversion.libs.fastutil.ints;

public abstract class AbstractIntSortedSet extends AbstractIntSet implements IntSortedSet {
   protected AbstractIntSortedSet() {
   }

   public abstract IntBidirectionalIterator iterator();
}
