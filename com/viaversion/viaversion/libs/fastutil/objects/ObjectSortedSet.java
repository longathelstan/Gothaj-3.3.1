package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.Size64;
import java.util.Collection;
import java.util.SortedSet;

public interface ObjectSortedSet<K> extends ObjectSet<K>, SortedSet<K>, ObjectBidirectionalIterable<K> {
   ObjectBidirectionalIterator<K> iterator(K var1);

   ObjectBidirectionalIterator<K> iterator();

   default ObjectSpliterator<K> spliterator() {
      return ObjectSpliterators.asSpliteratorFromSorted(this.iterator(), Size64.sizeOf((Collection)this), 85, this.comparator());
   }

   ObjectSortedSet<K> subSet(K var1, K var2);

   ObjectSortedSet<K> headSet(K var1);

   ObjectSortedSet<K> tailSet(K var1);
}
