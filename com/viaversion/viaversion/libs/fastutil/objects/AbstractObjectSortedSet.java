package com.viaversion.viaversion.libs.fastutil.objects;

public abstract class AbstractObjectSortedSet<K> extends AbstractObjectSet<K> implements ObjectSortedSet<K> {
   protected AbstractObjectSortedSet() {
   }

   public abstract ObjectBidirectionalIterator<K> iterator();
}
