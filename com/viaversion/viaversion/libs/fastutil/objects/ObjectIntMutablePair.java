package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectIntMutablePair<K> implements ObjectIntPair<K>, Serializable {
   private static final long serialVersionUID = 0L;
   protected K left;
   protected int right;

   public ObjectIntMutablePair(K left, int right) {
      this.left = left;
      this.right = right;
   }

   public static <K> ObjectIntMutablePair<K> of(K left, int right) {
      return new ObjectIntMutablePair(left, right);
   }

   public K left() {
      return this.left;
   }

   public ObjectIntMutablePair<K> left(K l) {
      this.left = l;
      return this;
   }

   public int rightInt() {
      return this.right;
   }

   public ObjectIntMutablePair<K> right(int r) {
      this.right = r;
      return this;
   }

   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (other instanceof ObjectIntPair) {
         return Objects.equals(this.left, ((ObjectIntPair)other).left()) && this.right == ((ObjectIntPair)other).rightInt();
      } else if (!(other instanceof Pair)) {
         return false;
      } else {
         return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   public int hashCode() {
      return (this.left == null ? 0 : this.left.hashCode()) * 19 + this.right;
   }

   public String toString() {
      return "<" + this.left() + "," + this.rightInt() + ">";
   }
}
