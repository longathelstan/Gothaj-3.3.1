package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.Size64;
import java.util.Collection;

public interface ObjectCollection<K> extends Collection<K>, ObjectIterable<K> {
   ObjectIterator<K> iterator();

   default ObjectSpliterator<K> spliterator() {
      return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf((Collection)this), 64);
   }
}
