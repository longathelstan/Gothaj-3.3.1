package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.Pair;
import java.util.Comparator;

public interface IntIntPair extends Pair<Integer, Integer> {
   int leftInt();

   /** @deprecated */
   @Deprecated
   default Integer left() {
      return this.leftInt();
   }

   default IntIntPair left(int l) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default IntIntPair left(Integer l) {
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

   default IntIntPair first(int l) {
      return this.left(l);
   }

   /** @deprecated */
   @Deprecated
   default IntIntPair first(Integer l) {
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

   default IntIntPair key(int l) {
      return this.left(l);
   }

   /** @deprecated */
   @Deprecated
   default IntIntPair key(Integer l) {
      return this.key(l);
   }

   int rightInt();

   /** @deprecated */
   @Deprecated
   default Integer right() {
      return this.rightInt();
   }

   default IntIntPair right(int r) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default IntIntPair right(Integer l) {
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

   default IntIntPair second(int r) {
      return this.right(r);
   }

   /** @deprecated */
   @Deprecated
   default IntIntPair second(Integer l) {
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

   default IntIntPair value(int r) {
      return this.right(r);
   }

   /** @deprecated */
   @Deprecated
   default IntIntPair value(Integer l) {
      return this.value(l);
   }

   static IntIntPair of(int left, int right) {
      return new IntIntImmutablePair(left, right);
   }

   static Comparator<IntIntPair> lexComparator() {
      return (x, y) -> {
         int t = Integer.compare(x.leftInt(), y.leftInt());
         return t != 0 ? t : Integer.compare(x.rightInt(), y.rightInt());
      };
   }
}
