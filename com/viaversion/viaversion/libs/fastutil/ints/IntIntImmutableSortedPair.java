package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.SortedPair;
import java.io.Serializable;
import java.util.Objects;

public class IntIntImmutableSortedPair extends IntIntImmutablePair implements IntIntSortedPair, Serializable {
   private static final long serialVersionUID = 0L;

   private IntIntImmutableSortedPair(int left, int right) {
      super(left, right);
   }

   public static IntIntImmutableSortedPair of(int left, int right) {
      return left <= right ? new IntIntImmutableSortedPair(left, right) : new IntIntImmutableSortedPair(right, left);
   }

   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof IntIntSortedPair) {
         return this.left == ((IntIntSortedPair)other).leftInt() && this.right == ((IntIntSortedPair)other).rightInt();
      } else if (!(other instanceof SortedPair)) {
         return false;
      } else {
         return Objects.equals(this.left, ((SortedPair)other).left()) && Objects.equals(this.right, ((SortedPair)other).right());
      }
   }

   public String toString() {
      return "{" + this.leftInt() + "," + this.rightInt() + "}";
   }
}
