package com.viaversion.viaversion.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class SynchronizedListWrapper<E> implements List<E> {
   private final List<E> list;
   private final Consumer<E> addHandler;

   public SynchronizedListWrapper(List<E> inputList, Consumer<E> addHandler) {
      this.list = inputList;
      this.addHandler = addHandler;
   }

   public List<E> originalList() {
      return this.list;
   }

   private void handleAdd(E o) {
      this.addHandler.accept(o);
   }

   public int size() {
      synchronized(this) {
         return this.list.size();
      }
   }

   public boolean isEmpty() {
      synchronized(this) {
         return this.list.isEmpty();
      }
   }

   public boolean contains(Object o) {
      synchronized(this) {
         return this.list.contains(o);
      }
   }

   @NonNull
   public Iterator<E> iterator() {
      return this.listIterator();
   }

   @NonNull
   public Object[] toArray() {
      synchronized(this) {
         return this.list.toArray();
      }
   }

   public boolean add(E o) {
      synchronized(this) {
         this.handleAdd(o);
         return this.list.add(o);
      }
   }

   public boolean remove(Object o) {
      synchronized(this) {
         return this.list.remove(o);
      }
   }

   public boolean addAll(Collection<? extends E> c) {
      synchronized(this) {
         Iterator var3 = c.iterator();

         while(var3.hasNext()) {
            E o = var3.next();
            this.handleAdd(o);
         }

         return this.list.addAll(c);
      }
   }

   public boolean addAll(int index, Collection<? extends E> c) {
      synchronized(this) {
         Iterator var4 = c.iterator();

         while(var4.hasNext()) {
            E o = var4.next();
            this.handleAdd(o);
         }

         return this.list.addAll(index, c);
      }
   }

   public void clear() {
      synchronized(this) {
         this.list.clear();
      }
   }

   public E get(int index) {
      synchronized(this) {
         return this.list.get(index);
      }
   }

   public E set(int index, E element) {
      synchronized(this) {
         return this.list.set(index, element);
      }
   }

   public void add(int index, E element) {
      synchronized(this) {
         this.list.add(index, element);
      }
   }

   public E remove(int index) {
      synchronized(this) {
         return this.list.remove(index);
      }
   }

   public int indexOf(Object o) {
      synchronized(this) {
         return this.list.indexOf(o);
      }
   }

   public int lastIndexOf(Object o) {
      synchronized(this) {
         return this.list.lastIndexOf(o);
      }
   }

   @NonNull
   public ListIterator<E> listIterator() {
      return this.list.listIterator();
   }

   @NonNull
   public ListIterator<E> listIterator(int index) {
      return this.list.listIterator(index);
   }

   @NonNull
   public List<E> subList(int fromIndex, int toIndex) {
      synchronized(this) {
         return this.list.subList(fromIndex, toIndex);
      }
   }

   public boolean retainAll(@NonNull Collection<?> c) {
      synchronized(this) {
         return this.list.retainAll(c);
      }
   }

   public boolean removeAll(@NonNull Collection<?> c) {
      synchronized(this) {
         return this.list.removeAll(c);
      }
   }

   public boolean containsAll(@NonNull Collection<?> c) {
      synchronized(this) {
         return this.list.containsAll(c);
      }
   }

   @NonNull
   public <T> T[] toArray(@NonNull T[] a) {
      synchronized(this) {
         return this.list.toArray(a);
      }
   }

   public void sort(Comparator<? super E> c) {
      synchronized(this) {
         this.list.sort(c);
      }
   }

   public void forEach(Consumer<? super E> consumer) {
      synchronized(this) {
         this.list.forEach(consumer);
      }
   }

   public boolean removeIf(Predicate<? super E> filter) {
      synchronized(this) {
         return this.list.removeIf(filter);
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         synchronized(this) {
            return this.list.equals(o);
         }
      }
   }

   public int hashCode() {
      synchronized(this) {
         return this.list.hashCode();
      }
   }

   public String toString() {
      synchronized(this) {
         return this.list.toString();
      }
   }
}
