package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.Pair;
import java.util.Comparator;

public interface IntObjectPair<V> extends Pair<Integer, V> {
   int leftInt();

   /** @deprecated */
   @Deprecated
   default Integer left() {
      return this.leftInt();
   }

   default IntObjectPair<V> left(int l) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default IntObjectPair<V> left(Integer l) {
      return this.left(l);
   }

   default int firstInt() {
      return this.leftInt();
   }

   /** @deprecated */
   @Deprecated
   default Integer first() {
      return this.firstInt();
   }

   default IntObjectPair<V> first(int l) {
      return this.left(l);
   }

   /** @deprecated */
   @Deprecated
   default IntObjectPair<V> first(Integer l) {
      return this.first(l);
   }

   default int keyInt() {
      return this.firstInt();
   }

   /** @deprecated */
   @Deprecated
   default Integer key() {
      return this.keyInt();
   }

   default IntObjectPair<V> key(int l) {
      return this.left(l);
   }

   /** @deprecated */
   @Deprecated
   default IntObjectPair<V> key(Integer l) {
      return this.key(l);
   }

   static <V> IntObjectPair<V> of(int left, V right) {
      return new IntObjectImmutablePair(left, right);
   }

   static <V> Comparator<IntObjectPair<V>> lexComparator() {
      return (x, y) -> {
         int t = Integer.compare(x.leftInt(), y.leftInt());
         return t != 0 ? t : ((Comparable)x.right()).compareTo(y.right());
      };
   }
}
