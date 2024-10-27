package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectObjectMutablePair<K, V> implements Pair<K, V>, Serializable {
   private static final long serialVersionUID = 0L;
   protected K left;
   protected V right;

   public ObjectObjectMutablePair(K left, V right) {
      this.left = left;
      this.right = right;
   }

   public static <K, V> ObjectObjectMutablePair<K, V> of(K left, V right) {
      return new ObjectObjectMutablePair(left, right);
   }

   public K left() {
      return this.left;
   }

   public ObjectObjectMutablePair<K, V> left(K l) {
      this.left = l;
      return this;
   }

   public V right() {
      return this.right;
   }

   public ObjectObjectMutablePair<K, V> right(V r) {
      this.right = r;
      return this;
   }

   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else if (!(other instanceof Pair)) {
         return false;
      } else {
         return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
      }
   }

   public int hashCode() {
      return (this.left == null ? 0 : this.left.hashCode()) * 19 + (this.right == null ? 0 : this.right.hashCode());
   }

   public String toString() {
      return "<" + this.left() + "," + this.right() + ">";
   }
}
