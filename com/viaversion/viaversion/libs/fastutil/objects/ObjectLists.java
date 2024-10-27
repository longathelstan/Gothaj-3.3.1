package com.viaversion.viaversion.libs.fastutil.objects;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class ObjectLists {
   public static final ObjectLists.EmptyList EMPTY_LIST = new ObjectLists.EmptyList();

   private ObjectLists() {
   }

   public static <K> ObjectList<K> shuffle(ObjectList<K> l, Random random) {
      int i = l.size();

      while(i-- != 0) {
         int p = random.nextInt(i + 1);
         K t = l.get(i);
         l.set(i, l.get(p));
         l.set(p, t);
      }

      return l;
   }

   public static <K> ObjectList<K> emptyList() {
      return EMPTY_LIST;
   }

   public static <K> ObjectList<K> singleton(K element) {
      return new ObjectLists.Singleton(element);
   }

   public static <K> ObjectList<K> synchronize(ObjectList<K> l) {
      return (ObjectList)(l instanceof RandomAccess ? new com.viaversion.viaversion.libs.fastutil.objects.ObjectLists.SynchronizedRandomAccessList(l) : new com.viaversion.viaversion.libs.fastutil.objects.ObjectLists.SynchronizedList(l));
   }

   public static <K> ObjectList<K> synchronize(ObjectList<K> l, Object sync) {
      return (ObjectList)(l instanceof RandomAccess ? new com.viaversion.viaversion.libs.fastutil.objects.ObjectLists.SynchronizedRandomAccessList(l, sync) : new com.viaversion.viaversion.libs.fastutil.objects.ObjectLists.SynchronizedList(l, sync));
   }

   public static <K> ObjectList<K> unmodifiable(ObjectList<? extends K> l) {
      return (ObjectList)(l instanceof RandomAccess ? new com.viaversion.viaversion.libs.fastutil.objects.ObjectLists.UnmodifiableRandomAccessList(l) : new com.viaversion.viaversion.libs.fastutil.objects.ObjectLists.UnmodifiableList(l));
   }

   public static class EmptyList<K> extends ObjectCollections.EmptyCollection<K> implements ObjectList<K>, RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyList() {
      }

      public K get(int i) {
         throw new IndexOutOfBoundsException();
      }

      public boolean remove(Object k) {
         throw new UnsupportedOperationException();
      }

      public K remove(int i) {
         throw new UnsupportedOperationException();
      }

      public void add(int index, K k) {
         throw new UnsupportedOperationException();
      }

      public K set(int index, K k) {
         throw new UnsupportedOperationException();
      }

      public int indexOf(Object k) {
         return -1;
      }

      public int lastIndexOf(Object k) {
         return -1;
      }

      public boolean addAll(int i, Collection<? extends K> c) {
         throw new UnsupportedOperationException();
      }

      public void replaceAll(UnaryOperator<K> operator) {
         throw new UnsupportedOperationException();
      }

      public void sort(Comparator<? super K> comparator) {
      }

      public void unstableSort(Comparator<? super K> comparator) {
      }

      public ObjectListIterator<K> listIterator() {
         return ObjectIterators.EMPTY_ITERATOR;
      }

      public ObjectListIterator<K> iterator() {
         return ObjectIterators.EMPTY_ITERATOR;
      }

      public ObjectListIterator<K> listIterator(int i) {
         if (i == 0) {
            return ObjectIterators.EMPTY_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(i));
         }
      }

      public ObjectList<K> subList(int from, int to) {
         if (from == 0 && to == 0) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void getElements(int from, Object[] a, int offset, int length) {
         if (from != 0 || length != 0 || offset < 0 || offset > a.length) {
            throw new IndexOutOfBoundsException();
         }
      }

      public void removeElements(int from, int to) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int index, K[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int index, K[] a) {
         throw new UnsupportedOperationException();
      }

      public void setElements(K[] a) {
         throw new UnsupportedOperationException();
      }

      public void setElements(int index, K[] a) {
         throw new UnsupportedOperationException();
      }

      public void setElements(int index, K[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      public void size(int s) {
         throw new UnsupportedOperationException();
      }

      public int compareTo(List<? extends K> o) {
         if (o == this) {
            return 0;
         } else {
            return o.isEmpty() ? 0 : -1;
         }
      }

      public Object clone() {
         return ObjectLists.EMPTY_LIST;
      }

      public int hashCode() {
         return 1;
      }

      public boolean equals(Object o) {
         return o instanceof List && ((List)o).isEmpty();
      }

      public String toString() {
         return "[]";
      }

      private Object readResolve() {
         return ObjectLists.EMPTY_LIST;
      }
   }

   public static class Singleton<K> extends AbstractObjectList<K> implements RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final K element;

      protected Singleton(K element) {
         this.element = element;
      }

      public K get(int i) {
         if (i == 0) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public boolean remove(Object k) {
         throw new UnsupportedOperationException();
      }

      public K remove(int i) {
         throw new UnsupportedOperationException();
      }

      public boolean contains(Object k) {
         return Objects.equals(k, this.element);
      }

      public int indexOf(Object k) {
         return Objects.equals(k, this.element) ? 0 : -1;
      }

      public Object[] toArray() {
         return new Object[]{this.element};
      }

      public ObjectListIterator<K> listIterator() {
         return ObjectIterators.singleton(this.element);
      }

      public ObjectListIterator<K> iterator() {
         return this.listIterator();
      }

      public ObjectSpliterator<K> spliterator() {
         return ObjectSpliterators.singleton(this.element);
      }

      public ObjectListIterator<K> listIterator(int i) {
         if (i <= 1 && i >= 0) {
            ObjectListIterator<K> l = this.listIterator();
            if (i == 1) {
               l.next();
            }

            return l;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public ObjectList<K> subList(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return (ObjectList)(from == 0 && to == 1 ? this : ObjectLists.EMPTY_LIST);
         }
      }

      public void forEach(Consumer<? super K> action) {
         action.accept(this.element);
      }

      public boolean addAll(int i, Collection<? extends K> c) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(Collection<? extends K> c) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> c) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> c) {
         throw new UnsupportedOperationException();
      }

      public boolean removeIf(Predicate<? super K> filter) {
         throw new UnsupportedOperationException();
      }

      public void replaceAll(UnaryOperator<K> operator) {
         throw new UnsupportedOperationException();
      }

      public void sort(Comparator<? super K> comparator) {
      }

      public void unstableSort(Comparator<? super K> comparator) {
      }

      public void getElements(int from, Object[] a, int offset, int length) {
         if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative");
         } else if (offset + length > a.length) {
            throw new ArrayIndexOutOfBoundsException("End index (" + (offset + length) + ") is greater than array length (" + a.length + ")");
         } else if (from + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size() + ")");
         } else if (length > 0) {
            a[offset] = this.element;
         }
      }

      public void removeElements(int from, int to) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int index, K[] a) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int index, K[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      public void setElements(K[] a) {
         throw new UnsupportedOperationException();
      }

      public void setElements(int index, K[] a) {
         throw new UnsupportedOperationException();
      }

      public void setElements(int index, K[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return 1;
      }

      public void size(int size) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return this;
      }
   }

   abstract static class ImmutableListBase<K> extends AbstractObjectList<K> implements ObjectList<K> {
      /** @deprecated */
      @Deprecated
      public final void add(int index, K k) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public final boolean add(K k) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public final boolean addAll(Collection<? extends K> c) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public final boolean addAll(int index, Collection<? extends K> c) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public final K remove(int index) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public final boolean remove(Object k) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public final boolean removeAll(Collection<?> c) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public final boolean retainAll(Collection<?> c) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public final boolean removeIf(Predicate<? super K> c) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public final void replaceAll(UnaryOperator<K> operator) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public final K set(int index, K k) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public final void clear() {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public final void size(int size) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public final void removeElements(int from, int to) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public final void addElements(int index, K[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public final void setElements(int index, K[] a, int offset, int length) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public final void sort(Comparator<? super K> comparator) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public final void unstableSort(Comparator<? super K> comparator) {
         throw new UnsupportedOperationException();
      }
   }
}
