package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.Pair;
import java.util.Comparator;

public interface ObjectIntPair<K> extends Pair<K, Integer> {
   int rightInt();

   /** @deprecated */
   @Deprecated
   default Integer right() {
      return this.rightInt();
   }

   default ObjectIntPair<K> right(int r) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default ObjectIntPair<K> right(Integer l) {
      return this.right(l);
   }

   default int secondInt() {
      return this.rightInt();
   }

   /** @deprecated */
   @Deprecated
   default Integer second() {
      return this.secondInt();
   }

   default ObjectIntPair<K> second(int r) {
      return this.right(r);
   }

   /** @deprecated */
   @Deprecated
   default ObjectIntPair<K> second(Integer l) {
      return this.second(l);
   }

   default int valueInt() {
      return this.rightInt();
   }

   /** @deprecated */
   @Deprecated
   default Integer value() {
      return this.valueInt();
   }

   default ObjectIntPair<K> value(int r) {
      return this.right(r);
   }

   /** @deprecated */
   @Deprecated
   default ObjectIntPair<K> value(Integer l) {
      return this.value(l);
   }

   static <K> ObjectIntPair<K> of(K left, int right) {
      return new ObjectIntImmutablePair(left, right);
   }

   static <K> Comparator<ObjectIntPair<K>> lexComparator() {
      return (x, y) -> {
         int t = ((Comparable)x.left()).compareTo(y.left());
         return t != 0 ? t : Integer.compare(x.rightInt(), y.rightInt());
      };
   }
}
