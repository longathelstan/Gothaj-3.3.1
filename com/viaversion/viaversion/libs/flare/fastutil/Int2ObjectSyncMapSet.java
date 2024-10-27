package com.viaversion.viaversion.libs.flare.fastutil;

import com.viaversion.viaversion.libs.fastutil.ints.AbstractIntSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntCollection;
import com.viaversion.viaversion.libs.fastutil.ints.IntIterator;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSpliterator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class Int2ObjectSyncMapSet extends AbstractIntSet implements IntSet {
   private static final long serialVersionUID = 1L;
   private final Int2ObjectSyncMap<Boolean> map;
   private final IntSet set;

   Int2ObjectSyncMapSet(@NonNull final Int2ObjectSyncMap<Boolean> map) {
      this.map = map;
      this.set = map.keySet();
   }

   public void clear() {
      this.map.clear();
   }

   public int size() {
      return this.map.size();
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public boolean contains(final int key) {
      return this.map.containsKey(key);
   }

   public boolean remove(final int key) {
      return this.map.remove(key) != null;
   }

   public boolean add(final int key) {
      return this.map.put(key, Boolean.TRUE) == null;
   }

   public boolean containsAll(@NonNull final IntCollection collection) {
      return this.set.containsAll(collection);
   }

   public boolean removeAll(@NonNull final IntCollection collection) {
      return this.set.removeAll(collection);
   }

   public boolean retainAll(@NonNull final IntCollection collection) {
      return this.set.retainAll(collection);
   }

   @NonNull
   public IntIterator iterator() {
      return this.set.iterator();
   }

   @NonNull
   public IntSpliterator spliterator() {
      return this.set.spliterator();
   }

   public int[] toArray(int[] original) {
      return this.set.toArray(original);
   }

   public int[] toIntArray() {
      return this.set.toIntArray();
   }

   public boolean equals(@Nullable final Object other) {
      return other == this || this.set.equals(other);
   }

   @NonNull
   public String toString() {
      return this.set.toString();
   }

   public int hashCode() {
      return this.set.hashCode();
   }
}
