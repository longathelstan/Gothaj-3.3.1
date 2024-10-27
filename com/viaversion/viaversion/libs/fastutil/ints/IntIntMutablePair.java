package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class IntIntMutablePair implements IntIntPair, Serializable {
   private static final long serialVersionUID = 0L;
   protected int left;
   protected int right;

   public IntIntMutablePair(int left, int right) {
      this.left = left;
      this.right = right;
   }

   public static IntIntMutablePair of(int left, int right) {
      return new IntIntMutablePair(left, right);
   }

   public int leftInt() {
      return this.left;
   }

   public IntIntMutablePair left(int l) {
      this.left = l;
      return this;
   }

   public int rightInt() {
      return this.right;
   }

   public IntIntMutablePair right(int r) {
      this.right = r;
      return this;
   }

   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof IntIntPair) {
         return this.left == ((IntIntPair)other).leftInt() && this.right == ((IntIntPair)other).rightInt();
      } else if (!(other instanceof Pair)) {
         return false;
      } else {
         return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   public int hashCode() {
      return this.left * 19 + this.right;
   }

   public String toString() {
      return "<" + this.leftInt() + "," + this.rightInt() + ">";
   }
}
