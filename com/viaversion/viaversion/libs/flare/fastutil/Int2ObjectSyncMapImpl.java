package com.viaversion.viaversion.libs.flare.fastutil;

import com.viaversion.viaversion.libs.fastutil.ints.AbstractInt2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectFunction;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMaps;
import com.viaversion.viaversion.libs.fastutil.objects.AbstractObjectSet;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class Int2ObjectSyncMapImpl<V> extends AbstractInt2ObjectMap<V> implements Int2ObjectSyncMap<V> {
   private static final long serialVersionUID = 1L;
   private final transient Object lock = new Object();
   private transient volatile Int2ObjectMap<Int2ObjectSyncMap.ExpungingEntry<V>> read;
   private transient volatile boolean amended;
   private transient Int2ObjectMap<Int2ObjectSyncMap.ExpungingEntry<V>> dirty;
   private transient int misses;
   private final transient IntFunction<Int2ObjectMap<Int2ObjectSyncMap.ExpungingEntry<V>>> function;
   private transient Int2ObjectSyncMapImpl<V>.EntrySetView entrySet;

   Int2ObjectSyncMapImpl(@NonNull final IntFunction<Int2ObjectMap<Int2ObjectSyncMap.ExpungingEntry<V>>> function, final int initialCapacity) {
      if (initialCapacity < 0) {
         throw new IllegalArgumentException("Initial capacity must be greater than 0");
      } else {
         this.function = function;
         this.read = (Int2ObjectMap)function.apply(initialCapacity);
      }
   }

   public int size() {
      this.promote();
      int size = 0;
      ObjectIterator var2 = this.read.values().iterator();

      while(var2.hasNext()) {
         Int2ObjectSyncMap.ExpungingEntry<V> value = (Int2ObjectSyncMap.ExpungingEntry)var2.next();
         if (value.exists()) {
            ++size;
         }
      }

      return size;
   }

   public boolean isEmpty() {
      this.promote();
      ObjectIterator var1 = this.read.values().iterator();

      Int2ObjectSyncMap.ExpungingEntry value;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         value = (Int2ObjectSyncMap.ExpungingEntry)var1.next();
      } while(!value.exists());

      return false;
   }

   public boolean containsValue(@Nullable final Object value) {
      ObjectIterator var2 = this.int2ObjectEntrySet().iterator();

      Int2ObjectMap.Entry entry;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         entry = (Int2ObjectMap.Entry)var2.next();
      } while(!Objects.equals(entry.getValue(), value));

      return true;
   }

   public boolean containsKey(final int key) {
      Int2ObjectSyncMap.ExpungingEntry<V> entry = this.getEntry(key);
      return entry != null && entry.exists();
   }

   @Nullable
   public V get(final int key) {
      Int2ObjectSyncMap.ExpungingEntry<V> entry = this.getEntry(key);
      return entry != null ? entry.get() : null;
   }

   @NonNull
   public V getOrDefault(final int key, @NonNull final V defaultValue) {
      Objects.requireNonNull(defaultValue, "defaultValue");
      Int2ObjectSyncMap.ExpungingEntry<V> entry = this.getEntry(key);
      return entry != null ? entry.getOr(defaultValue) : defaultValue;
   }

   @Nullable
   public Int2ObjectSyncMap.ExpungingEntry<V> getEntry(final int key) {
      Int2ObjectSyncMap.ExpungingEntry<V> entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
      if (entry == null && this.amended) {
         synchronized(this.lock) {
            if ((entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key)) == null && this.amended && this.dirty != null) {
               entry = (Int2ObjectSyncMap.ExpungingEntry)this.dirty.get(key);
               this.missLocked();
            }
         }
      }

      return entry;
   }

   @Nullable
   public V computeIfAbsent(final int key, @NonNull final IntFunction<? extends V> mappingFunction) {
      Objects.requireNonNull(mappingFunction, "mappingFunction");
      Int2ObjectSyncMap.ExpungingEntry<V> entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
      Int2ObjectSyncMap.InsertionResult<V> result = entry != null ? entry.computeIfAbsent(key, mappingFunction) : null;
      if (result != null && result.operation() == 1) {
         return result.current();
      } else {
         synchronized(this.lock) {
            if ((entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key)) != null) {
               if (entry.tryUnexpungeAndCompute(key, mappingFunction)) {
                  if (entry.exists()) {
                     this.dirty.put(key, entry);
                  }

                  return entry.get();
               }

               result = entry.computeIfAbsent(key, mappingFunction);
            } else {
               if (this.dirty == null || (entry = (Int2ObjectSyncMap.ExpungingEntry)this.dirty.get(key)) == null) {
                  if (!this.amended) {
                     this.dirtyLocked();
                     this.amended = true;
                  }

                  V computed = mappingFunction.apply(key);
                  if (computed != null) {
                     this.dirty.put(key, new Int2ObjectSyncMapImpl.ExpungingEntryImpl(computed));
                  }

                  return computed;
               }

               result = entry.computeIfAbsent(key, mappingFunction);
               if (result.current() == null) {
                  this.dirty.remove(key);
               }

               this.missLocked();
            }
         }

         return result.current();
      }
   }

   @Nullable
   public V computeIfAbsent(final int key, @NonNull final Int2ObjectFunction<? extends V> mappingFunction) {
      Objects.requireNonNull(mappingFunction, "mappingFunction");
      Int2ObjectSyncMap.ExpungingEntry<V> entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
      Int2ObjectSyncMap.InsertionResult<V> result = entry != null ? entry.computeIfAbsentPrimitive(key, mappingFunction) : null;
      if (result != null && result.operation() == 1) {
         return result.current();
      } else {
         synchronized(this.lock) {
            if ((entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key)) != null) {
               if (entry.tryUnexpungeAndComputePrimitive(key, mappingFunction)) {
                  if (entry.exists()) {
                     this.dirty.put(key, entry);
                  }

                  return entry.get();
               }

               result = entry.computeIfAbsentPrimitive(key, mappingFunction);
            } else {
               if (this.dirty == null || (entry = (Int2ObjectSyncMap.ExpungingEntry)this.dirty.get(key)) == null) {
                  if (!this.amended) {
                     this.dirtyLocked();
                     this.amended = true;
                  }

                  V computed = mappingFunction.get(key);
                  if (computed != null) {
                     this.dirty.put(key, new Int2ObjectSyncMapImpl.ExpungingEntryImpl(computed));
                  }

                  return computed;
               }

               result = entry.computeIfAbsentPrimitive(key, mappingFunction);
               if (result.current() == null) {
                  this.dirty.remove(key);
               }

               this.missLocked();
            }
         }

         return result.current();
      }
   }

   @Nullable
   public V computeIfPresent(final int key, @NonNull final BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction, "remappingFunction");
      Int2ObjectSyncMap.ExpungingEntry<V> entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
      Int2ObjectSyncMap.InsertionResult<V> result = entry != null ? entry.computeIfPresent(key, remappingFunction) : null;
      if (result != null && result.operation() == 1) {
         return result.current();
      } else {
         synchronized(this.lock) {
            if ((entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key)) != null) {
               result = entry.computeIfPresent(key, remappingFunction);
            } else if (this.dirty != null && (entry = (Int2ObjectSyncMap.ExpungingEntry)this.dirty.get(key)) != null) {
               result = entry.computeIfPresent(key, remappingFunction);
               if (result.current() == null) {
                  this.dirty.remove(key);
               }

               this.missLocked();
            }
         }

         return result != null ? result.current() : null;
      }
   }

   @Nullable
   public V compute(final int key, @NonNull final BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction, "remappingFunction");
      Int2ObjectSyncMap.ExpungingEntry<V> entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
      Int2ObjectSyncMap.InsertionResult<V> result = entry != null ? entry.compute(key, remappingFunction) : null;
      if (result != null && result.operation() == 1) {
         return result.current();
      } else {
         synchronized(this.lock) {
            if ((entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key)) != null) {
               if (entry.tryUnexpungeAndCompute(key, remappingFunction)) {
                  if (entry.exists()) {
                     this.dirty.put(key, entry);
                  }

                  return entry.get();
               }

               result = entry.compute(key, remappingFunction);
            } else {
               if (this.dirty == null || (entry = (Int2ObjectSyncMap.ExpungingEntry)this.dirty.get(key)) == null) {
                  if (!this.amended) {
                     this.dirtyLocked();
                     this.amended = true;
                  }

                  V computed = remappingFunction.apply(key, (Object)null);
                  if (computed != null) {
                     this.dirty.put(key, new Int2ObjectSyncMapImpl.ExpungingEntryImpl(computed));
                  }

                  return computed;
               }

               result = entry.compute(key, remappingFunction);
               if (result.current() == null) {
                  this.dirty.remove(key);
               }

               this.missLocked();
            }
         }

         return result.current();
      }
   }

   @Nullable
   public V putIfAbsent(final int key, @NonNull final V value) {
      Objects.requireNonNull(value, "value");
      Int2ObjectSyncMap.ExpungingEntry<V> entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
      Int2ObjectSyncMap.InsertionResult<V> result = entry != null ? entry.setIfAbsent(value) : null;
      if (result != null && result.operation() == 1) {
         return result.previous();
      } else {
         synchronized(this.lock) {
            if ((entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key)) != null) {
               if (entry.tryUnexpungeAndSet(value)) {
                  this.dirty.put(key, entry);
                  return null;
               }

               result = entry.setIfAbsent(value);
            } else {
               if (this.dirty == null || (entry = (Int2ObjectSyncMap.ExpungingEntry)this.dirty.get(key)) == null) {
                  if (!this.amended) {
                     this.dirtyLocked();
                     this.amended = true;
                  }

                  this.dirty.put(key, new Int2ObjectSyncMapImpl.ExpungingEntryImpl(value));
                  return null;
               }

               result = entry.setIfAbsent(value);
               this.missLocked();
            }
         }

         return result.previous();
      }
   }

   @Nullable
   public V put(final int key, @NonNull final V value) {
      Objects.requireNonNull(value, "value");
      Int2ObjectSyncMap.ExpungingEntry<V> entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
      V previous = entry != null ? entry.get() : null;
      if (entry != null && entry.trySet(value)) {
         return previous;
      } else {
         synchronized(this.lock) {
            if ((entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key)) != null) {
               previous = entry.get();
               if (entry.tryUnexpungeAndSet(value)) {
                  this.dirty.put(key, entry);
               } else {
                  entry.set(value);
               }
            } else {
               if (this.dirty == null || (entry = (Int2ObjectSyncMap.ExpungingEntry)this.dirty.get(key)) == null) {
                  if (!this.amended) {
                     this.dirtyLocked();
                     this.amended = true;
                  }

                  this.dirty.put(key, new Int2ObjectSyncMapImpl.ExpungingEntryImpl(value));
                  return null;
               }

               previous = entry.get();
               entry.set(value);
            }

            return previous;
         }
      }
   }

   @Nullable
   public V remove(final int key) {
      Int2ObjectSyncMap.ExpungingEntry<V> entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
      if (entry == null && this.amended) {
         synchronized(this.lock) {
            if ((entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key)) == null && this.amended && this.dirty != null) {
               entry = (Int2ObjectSyncMap.ExpungingEntry)this.dirty.remove(key);
               this.missLocked();
            }
         }
      }

      return entry != null ? entry.clear() : null;
   }

   public boolean remove(final int key, @NonNull final Object value) {
      Objects.requireNonNull(value, "value");
      Int2ObjectSyncMap.ExpungingEntry<V> entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key);
      if (entry == null && this.amended) {
         synchronized(this.lock) {
            if ((entry = (Int2ObjectSyncMap.ExpungingEntry)this.read.get(key)) == null && this.amended && this.dirty != null) {
               boolean present = (entry = (Int2ObjectSyncMap.ExpungingEntry)this.dirty.get(key)) != null && entry.replace(value, (Object)null);
               if (present) {
                  this.dirty.remove(key);
               }

               this.missLocked();
               return present;
            }
         }
      }

      return entry != null && entry.replace(value, (Object)null);
   }

   @Nullable
   public V replace(final int key, @NonNull final V value) {
      Objects.requireNonNull(value, "value");
      Int2ObjectSyncMap.ExpungingEntry<V> entry = this.getEntry(key);
      return entry != null ? entry.tryReplace(value) : null;
   }

   public boolean replace(final int key, @NonNull final V oldValue, @NonNull final V newValue) {
      Objects.requireNonNull(oldValue, "oldValue");
      Objects.requireNonNull(newValue, "newValue");
      Int2ObjectSyncMap.ExpungingEntry<V> entry = this.getEntry(key);
      return entry != null && entry.replace(oldValue, newValue);
   }

   public void forEach(@NonNull final BiConsumer<? super Integer, ? super V> action) {
      Objects.requireNonNull(action, "action");
      this.promote();
      ObjectIterator var3 = this.read.int2ObjectEntrySet().iterator();

      while(var3.hasNext()) {
         Int2ObjectMap.Entry<Int2ObjectSyncMap.ExpungingEntry<V>> that = (Int2ObjectMap.Entry)var3.next();
         Object value;
         if ((value = ((Int2ObjectSyncMap.ExpungingEntry)that.getValue()).get()) != null) {
            action.accept(that.getIntKey(), value);
         }
      }

   }

   public void putAll(@NonNull final Map<? extends Integer, ? extends V> map) {
      Objects.requireNonNull(map, "map");
      Iterator var2 = map.entrySet().iterator();

      while(var2.hasNext()) {
         java.util.Map.Entry<? extends Integer, ? extends V> entry = (java.util.Map.Entry)var2.next();
         this.put((Integer)entry.getKey(), entry.getValue());
      }

   }

   public void replaceAll(@NonNull final BiFunction<? super Integer, ? super V, ? extends V> function) {
      Objects.requireNonNull(function, "function");
      this.promote();
      ObjectIterator var4 = this.read.int2ObjectEntrySet().iterator();

      while(var4.hasNext()) {
         Int2ObjectMap.Entry<Int2ObjectSyncMap.ExpungingEntry<V>> that = (Int2ObjectMap.Entry)var4.next();
         Int2ObjectSyncMap.ExpungingEntry entry;
         Object value;
         if ((value = (entry = (Int2ObjectSyncMap.ExpungingEntry)that.getValue()).get()) != null) {
            entry.tryReplace(function.apply(that.getIntKey(), value));
         }
      }

   }

   public void clear() {
      synchronized(this.lock) {
         this.read = (Int2ObjectMap)this.function.apply(this.read.size());
         this.dirty = null;
         this.amended = false;
         this.misses = 0;
      }
   }

   @NonNull
   public ObjectSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet() {
      return this.entrySet != null ? this.entrySet : (this.entrySet = new Int2ObjectSyncMapImpl.EntrySetView());
   }

   private void promote() {
      if (this.amended) {
         synchronized(this.lock) {
            if (this.amended) {
               this.promoteLocked();
            }
         }
      }

   }

   private void missLocked() {
      ++this.misses;
      if (this.misses >= this.dirty.size()) {
         this.promoteLocked();
      }
   }

   private void promoteLocked() {
      this.read = this.dirty;
      this.amended = false;
      this.dirty = null;
      this.misses = 0;
   }

   private void dirtyLocked() {
      if (this.dirty == null) {
         this.dirty = (Int2ObjectMap)this.function.apply(this.read.size());
         Int2ObjectMaps.fastForEach(this.read, (entry) -> {
            if (!((Int2ObjectSyncMap.ExpungingEntry)entry.getValue()).tryExpunge()) {
               this.dirty.put(entry.getIntKey(), (Int2ObjectSyncMap.ExpungingEntry)entry.getValue());
            }

         });
      }
   }

   final class EntryIterator implements ObjectIterator<Int2ObjectMap.Entry<V>> {
      private final Iterator<Int2ObjectMap.Entry<Int2ObjectSyncMap.ExpungingEntry<V>>> backingIterator;
      private Int2ObjectMap.Entry<V> next;
      private Int2ObjectMap.Entry<V> current;

      EntryIterator(@NonNull final Iterator<Int2ObjectMap.Entry<Int2ObjectSyncMap.ExpungingEntry<V>>> backingIterator) {
         this.backingIterator = backingIterator;
         this.advance();
      }

      public boolean hasNext() {
         return this.next != null;
      }

      public Int2ObjectMap.Entry<V> next() {
         Int2ObjectMap.Entry current;
         if ((current = this.next) == null) {
            throw new NoSuchElementException();
         } else {
            this.current = current;
            this.advance();
            return current;
         }
      }

      public void remove() {
         Int2ObjectMap.Entry current;
         if ((current = this.current) == null) {
            throw new IllegalStateException();
         } else {
            this.current = null;
            Int2ObjectSyncMapImpl.this.remove(current.getIntKey());
         }
      }

      private void advance() {
         this.next = null;

         Int2ObjectMap.Entry entry;
         Object value;
         do {
            if (!this.backingIterator.hasNext()) {
               return;
            }
         } while((value = ((Int2ObjectSyncMap.ExpungingEntry)(entry = (Int2ObjectMap.Entry)this.backingIterator.next()).getValue()).get()) == null);

         this.next = Int2ObjectSyncMapImpl.this.new MapEntry(entry.getIntKey(), value);
      }
   }

   final class EntrySetView extends AbstractObjectSet<Int2ObjectMap.Entry<V>> {
      public int size() {
         return Int2ObjectSyncMapImpl.this.size();
      }

      public boolean contains(@Nullable final Object entry) {
         if (!(entry instanceof Int2ObjectMap.Entry)) {
            return false;
         } else {
            Int2ObjectMap.Entry<?> mapEntry = (Int2ObjectMap.Entry)entry;
            V value = Int2ObjectSyncMapImpl.this.get(mapEntry.getIntKey());
            return value != null && Objects.equals(value, mapEntry.getValue());
         }
      }

      public boolean add(@NonNull final Int2ObjectMap.Entry<V> entry) {
         Objects.requireNonNull(entry, "entry");
         return Int2ObjectSyncMapImpl.this.put(entry.getIntKey(), entry.getValue()) == null;
      }

      public boolean remove(@Nullable final Object entry) {
         if (!(entry instanceof Int2ObjectMap.Entry)) {
            return false;
         } else {
            Int2ObjectMap.Entry<?> mapEntry = (Int2ObjectMap.Entry)entry;
            return Int2ObjectSyncMapImpl.this.remove(mapEntry.getIntKey(), mapEntry.getValue());
         }
      }

      public void clear() {
         Int2ObjectSyncMapImpl.this.clear();
      }

      @NonNull
      public ObjectIterator<Int2ObjectMap.Entry<V>> iterator() {
         Int2ObjectSyncMapImpl.this.promote();
         return Int2ObjectSyncMapImpl.this.new EntryIterator(Int2ObjectSyncMapImpl.this.read.int2ObjectEntrySet().iterator());
      }
   }

   final class MapEntry implements Int2ObjectMap.Entry<V> {
      private final int key;
      private V value;

      MapEntry(final int key, @NonNull final V value) {
         this.key = key;
         this.value = value;
      }

      public int getIntKey() {
         return this.key;
      }

      @NonNull
      public V getValue() {
         return this.value;
      }

      @Nullable
      public V setValue(@NonNull final V value) {
         Objects.requireNonNull(value, "value");
         V previous = Int2ObjectSyncMapImpl.this.put(this.key, value);
         this.value = value;
         return previous;
      }

      @NonNull
      public String toString() {
         return "Int2ObjectSyncMapImpl.MapEntry{key=" + this.getIntKey() + ", value=" + this.getValue() + "}";
      }

      public boolean equals(@Nullable final Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof Int2ObjectMap.Entry)) {
            return false;
         } else {
            Int2ObjectMap.Entry<?> that = (Int2ObjectMap.Entry)other;
            return Objects.equals(this.getIntKey(), that.getIntKey()) && Objects.equals(this.getValue(), that.getValue());
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.getIntKey(), this.getValue()});
      }
   }

   static final class InsertionResultImpl<V> implements Int2ObjectSyncMap.InsertionResult<V> {
      private static final byte UNCHANGED = 0;
      private static final byte UPDATED = 1;
      private static final byte EXPUNGED = 2;
      private final byte operation;
      private final V previous;
      private final V current;

      InsertionResultImpl(final byte operation, @Nullable final V previous, @Nullable final V current) {
         this.operation = operation;
         this.previous = previous;
         this.current = current;
      }

      public byte operation() {
         return this.operation;
      }

      @Nullable
      public V previous() {
         return this.previous;
      }

      @Nullable
      public V current() {
         return this.current;
      }
   }

   static final class ExpungingEntryImpl<V> implements Int2ObjectSyncMap.ExpungingEntry<V> {
      private static final AtomicReferenceFieldUpdater<Int2ObjectSyncMapImpl.ExpungingEntryImpl, Object> UPDATER = AtomicReferenceFieldUpdater.newUpdater(Int2ObjectSyncMapImpl.ExpungingEntryImpl.class, Object.class, "value");
      private static final Object EXPUNGED = new Object();
      private volatile Object value;

      ExpungingEntryImpl(@NonNull final V value) {
         this.value = value;
      }

      public boolean exists() {
         return this.value != null && this.value != EXPUNGED;
      }

      @Nullable
      public V get() {
         return this.value == EXPUNGED ? null : this.value;
      }

      @NonNull
      public V getOr(@NonNull final V other) {
         Object value = this.value;
         return value != null && value != EXPUNGED ? this.value : other;
      }

      @NonNull
      public Int2ObjectSyncMap.InsertionResult<V> setIfAbsent(@NonNull final V value) {
         do {
            Object previous = this.value;
            if (previous == EXPUNGED) {
               return new Int2ObjectSyncMapImpl.InsertionResultImpl((byte)2, (Object)null, (Object)null);
            }

            if (previous != null) {
               return new Int2ObjectSyncMapImpl.InsertionResultImpl((byte)0, previous, previous);
            }
         } while(!UPDATER.compareAndSet(this, (Object)null, value));

         return new Int2ObjectSyncMapImpl.InsertionResultImpl((byte)1, (Object)null, value);
      }

      @NonNull
      public Int2ObjectSyncMap.InsertionResult<V> computeIfAbsent(final int key, @NonNull final IntFunction<? extends V> function) {
         Object next = null;

         do {
            Object previous = this.value;
            if (previous == EXPUNGED) {
               return new Int2ObjectSyncMapImpl.InsertionResultImpl((byte)2, (Object)null, (Object)null);
            }

            if (previous != null) {
               return new Int2ObjectSyncMapImpl.InsertionResultImpl((byte)0, previous, previous);
            }
         } while(!UPDATER.compareAndSet(this, (Object)null, next != null ? next : (next = function.apply(key))));

         return new Int2ObjectSyncMapImpl.InsertionResultImpl((byte)1, (Object)null, next);
      }

      @NonNull
      public Int2ObjectSyncMap.InsertionResult<V> computeIfAbsentPrimitive(final int key, @NonNull final Int2ObjectFunction<? extends V> function) {
         Object next = null;

         AtomicReferenceFieldUpdater var10000;
         Object var10003;
         do {
            Object previous = this.value;
            if (previous == EXPUNGED) {
               return new Int2ObjectSyncMapImpl.InsertionResultImpl((byte)2, (Object)null, (Object)null);
            }

            if (previous != null) {
               return new Int2ObjectSyncMapImpl.InsertionResultImpl((byte)0, previous, previous);
            }

            var10000 = UPDATER;
            if (next != null) {
               var10003 = next;
            } else {
               var10003 = function.containsKey(key) ? function.get(key) : null;
               next = var10003;
            }
         } while(!var10000.compareAndSet(this, (Object)null, var10003));

         return new Int2ObjectSyncMapImpl.InsertionResultImpl((byte)1, (Object)null, next);
      }

      @NonNull
      public Int2ObjectSyncMap.InsertionResult<V> computeIfPresent(final int key, @NonNull final BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
         Object next = null;

         Object previous;
         do {
            previous = this.value;
            if (previous == EXPUNGED) {
               return new Int2ObjectSyncMapImpl.InsertionResultImpl((byte)2, (Object)null, (Object)null);
            }

            if (previous == null) {
               return new Int2ObjectSyncMapImpl.InsertionResultImpl((byte)0, (Object)null, (Object)null);
            }
         } while(!UPDATER.compareAndSet(this, previous, next != null ? next : (next = remappingFunction.apply(key, previous))));

         return new Int2ObjectSyncMapImpl.InsertionResultImpl((byte)1, previous, next);
      }

      @NonNull
      public Int2ObjectSyncMap.InsertionResult<V> compute(final int key, @NonNull final BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
         Object next = null;

         Object previous;
         do {
            previous = this.value;
            if (previous == EXPUNGED) {
               return new Int2ObjectSyncMapImpl.InsertionResultImpl((byte)2, (Object)null, (Object)null);
            }
         } while(!UPDATER.compareAndSet(this, previous, next != null ? next : (next = remappingFunction.apply(key, previous))));

         return new Int2ObjectSyncMapImpl.InsertionResultImpl((byte)1, previous, next);
      }

      public void set(@NonNull final V value) {
         UPDATER.set(this, value);
      }

      public boolean replace(@NonNull final Object compare, @Nullable final V value) {
         while(true) {
            Object previous = this.value;
            if (previous != EXPUNGED && Objects.equals(previous, compare)) {
               if (!UPDATER.compareAndSet(this, previous, value)) {
                  continue;
               }

               return true;
            }

            return false;
         }
      }

      @Nullable
      public V clear() {
         while(true) {
            Object previous = this.value;
            if (previous != null && previous != EXPUNGED) {
               if (!UPDATER.compareAndSet(this, previous, (Object)null)) {
                  continue;
               }

               return previous;
            }

            return null;
         }
      }

      public boolean trySet(@NonNull final V value) {
         Object previous;
         do {
            previous = this.value;
            if (previous == EXPUNGED) {
               return false;
            }
         } while(!UPDATER.compareAndSet(this, previous, value));

         return true;
      }

      @Nullable
      public V tryReplace(@NonNull final V value) {
         while(true) {
            Object previous = this.value;
            if (previous != null && previous != EXPUNGED) {
               if (!UPDATER.compareAndSet(this, previous, value)) {
                  continue;
               }

               return previous;
            }

            return null;
         }
      }

      public boolean tryExpunge() {
         while(true) {
            if (this.value == null) {
               if (!UPDATER.compareAndSet(this, (Object)null, EXPUNGED)) {
                  continue;
               }

               return true;
            }

            return this.value == EXPUNGED;
         }
      }

      public boolean tryUnexpungeAndSet(@NonNull final V value) {
         return UPDATER.compareAndSet(this, EXPUNGED, value);
      }

      public boolean tryUnexpungeAndCompute(final int key, @NonNull final IntFunction<? extends V> function) {
         if (this.value == EXPUNGED) {
            Object value = function.apply(key);
            return UPDATER.compareAndSet(this, EXPUNGED, value);
         } else {
            return false;
         }
      }

      public boolean tryUnexpungeAndComputePrimitive(final int key, @NonNull final Int2ObjectFunction<? extends V> function) {
         if (this.value == EXPUNGED) {
            Object value = function.containsKey(key) ? function.get(key) : null;
            return UPDATER.compareAndSet(this, EXPUNGED, value);
         } else {
            return false;
         }
      }

      public boolean tryUnexpungeAndCompute(final int key, @NonNull final BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
         if (this.value == EXPUNGED) {
            Object value = remappingFunction.apply(key, (Object)null);
            return UPDATER.compareAndSet(this, EXPUNGED, value);
         } else {
            return false;
         }
      }
   }
}
