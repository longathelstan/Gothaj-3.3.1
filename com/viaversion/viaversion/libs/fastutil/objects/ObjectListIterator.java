package com.viaversion.viaversion.libs.fastutil.objects;

import java.util.ListIterator;

public interface ObjectListIterator<K> extends ObjectBidirectionalIterator<K>, ListIterator<K> {
   default void set(K k) {
      throw new UnsupportedOperationException();
   }

   default void add(K k) {
      throw new UnsupportedOperationException();
   }

   default void remove() {
      throw new UnsupportedOperationException();
   }
}
