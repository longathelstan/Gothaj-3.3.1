package org.jsoup.helper;

import java.util.ArrayList;
import java.util.Collection;

public abstract class ChangeNotifyingArrayList<E> extends ArrayList<E> {
   public ChangeNotifyingArrayList(int initialCapacity) {
      super(initialCapacity);
   }

   public abstract void onContentsChanged();

   public E set(int index, E element) {
      this.onContentsChanged();
      return super.set(index, element);
   }

   public boolean add(E e) {
      this.onContentsChanged();
      return super.add(e);
   }

   public void add(int index, E element) {
      this.onContentsChanged();
      super.add(index, element);
   }

   public E remove(int index) {
      this.onContentsChanged();
      return super.remove(index);
   }

   public boolean remove(Object o) {
      this.onContentsChanged();
      return super.remove(o);
   }

   public void clear() {
      this.onContentsChanged();
      super.clear();
   }

   public boolean addAll(Collection<? extends E> c) {
      this.onContentsChanged();
      return super.addAll(c);
   }

   public boolean addAll(int index, Collection<? extends E> c) {
      this.onContentsChanged();
      return super.addAll(index, c);
   }

   protected void removeRange(int fromIndex, int toIndex) {
      this.onContentsChanged();
      super.removeRange(fromIndex, toIndex);
   }

   public boolean removeAll(Collection<?> c) {
      this.onContentsChanged();
      return super.removeAll(c);
   }

   public boolean retainAll(Collection<?> c) {
      this.onContentsChanged();
      return super.retainAll(c);
   }
}
