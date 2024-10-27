package com.viaversion.viaversion.libs.flare;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class SyncMapImpl<K, V> extends AbstractMap<K, V> implements SyncMap<K, V> {
   private final transient Object lock = new Object();
   private transient volatile Map<K, SyncMap.ExpungingEntry<V>> read;
   private transient volatile boolean amended;
   private transient Map<K, SyncMap.ExpungingEntry<V>> dirty;
   private transient int misses;
   private final transient IntFunction<Map<K, SyncMap.ExpungingEntry<V>>> function;
   private transient SyncMapImpl<K, V>.EntrySetView entrySet;

   SyncMapImpl(@NonNull final IntFunction<Map<K, SyncMap.ExpungingEntry<V>>> function, final int initialCapacity) {
      if (initialCapacity < 0) {
         throw new IllegalArgumentException("Initial capacity must be greater than 0");
      } else {
         this.function = function;
         this.read = (Map)function.apply(initialCapacity);
      }
   }

   public int size() {
      this.promote();
      int size = 0;
      Iterator var2 = this.read.values().iterator();

      while(var2.hasNext()) {
         SyncMap.ExpungingEntry<V> value = (SyncMap.ExpungingEntry)var2.next();
         if (value.exists()) {
            ++size;
         }
      }

      return size;
   }

   public boolean isEmpty() {
      this.promote();
      Iterator var1 = this.read.values().iterator();

      SyncMap.ExpungingEntry value;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         value = (SyncMap.ExpungingEntry)var1.next();
      } while(!value.exists());

      return false;
   }

   public boolean containsKey(@Nullable final Object key) {
      SyncMap.ExpungingEntry<V> entry = this.getEntry(key);
      return entry != null && entry.exists();
   }

   @Nullable
   public V get(@Nullable final Object key) {
      SyncMap.ExpungingEntry<V> entry = this.getEntry(key);
      return entry != null ? entry.get() : null;
   }

   @NonNull
   public V getOrDefault(@Nullable final Object key, @NonNull final V defaultValue) {
      Objects.requireNonNull(defaultValue, "defaultValue");
      SyncMap.ExpungingEntry<V> entry = this.getEntry(key);
      return entry != null ? entry.getOr(defaultValue) : defaultValue;
   }

   @Nullable
   private SyncMap.ExpungingEntry<V> getEntry(@Nullable final Object key) {
      SyncMap.ExpungingEntry<V> entry = (SyncMap.ExpungingEntry)this.read.get(key);
      if (entry == null && this.amended) {
         synchronized(this.lock) {
            if ((entry = (SyncMap.ExpungingEntry)this.read.get(key)) == null && this.amended && this.dirty != null) {
               entry = (SyncMap.ExpungingEntry)this.dirty.get(key);
               this.missLocked();
            }
         }
      }

      return entry;
   }

   @Nullable
   public V computeIfAbsent(@Nullable final K key, @NonNull final Function<? super K, ? extends V> mappingFunction) {
      Objects.requireNonNull(mappingFunction, "mappingFunction");
      SyncMap.ExpungingEntry<V> entry = (SyncMap.ExpungingEntry)this.read.get(key);
      SyncMap.InsertionResult<V> result = entry != null ? entry.computeIfAbsent(key, mappingFunction) : null;
      if (result != null && result.operation() == 1) {
         return result.current();
      } else {
         synchronized(this.lock) {
            if ((entry = (SyncMap.ExpungingEntry)this.read.get(key)) != null) {
               if (entry.tryUnexpungeAndCompute(key, mappingFunction)) {
                  if (entry.exists()) {
                     this.dirty.put(key, entry);
                  }

                  return entry.get();
               }

               result = entry.computeIfAbsent(key, mappingFunction);
            } else {
               if (this.dirty == null || (entry = (SyncMap.ExpungingEntry)this.dirty.get(key)) == null) {
                  if (!this.amended) {
                     this.dirtyLocked();
                     this.amended = true;
                  }

                  V computed = mappingFunction.apply(key);
                  if (computed != null) {
                     this.dirty.put(key, new SyncMapImpl.ExpungingEntryImpl(computed));
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
   public V computeIfPresent(@Nullable final K key, @NonNull final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction, "remappingFunction");
      SyncMap.ExpungingEntry<V> entry = (SyncMap.ExpungingEntry)this.read.get(key);
      SyncMap.InsertionResult<V> result = entry != null ? entry.computeIfPresent(key, remappingFunction) : null;
      if (result != null && result.operation() == 1) {
         return result.current();
      } else {
         synchronized(this.lock) {
            if ((entry = (SyncMap.ExpungingEntry)this.read.get(key)) != null) {
               result = entry.computeIfPresent(key, remappingFunction);
            } else if (this.dirty != null && (entry = (SyncMap.ExpungingEntry)this.dirty.get(key)) != null) {
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
   public V compute(@Nullable final K key, @Nullable final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction, "remappingFunction");
      SyncMap.ExpungingEntry<V> entry = (SyncMap.ExpungingEntry)this.read.get(key);
      SyncMap.InsertionResult<V> result = entry != null ? entry.compute(key, remappingFunction) : null;
      if (result != null && result.operation() == 1) {
         return result.current();
      } else {
         synchronized(this.lock) {
            if ((entry = (SyncMap.ExpungingEntry)this.read.get(key)) != null) {
               if (entry.tryUnexpungeAndCompute(key, remappingFunction)) {
                  if (entry.exists()) {
                     this.dirty.put(key, entry);
                  }

                  return entry.get();
               }

               result = entry.compute(key, remappingFunction);
            } else {
               if (this.dirty == null || (entry = (SyncMap.ExpungingEntry)this.dirty.get(key)) == null) {
                  if (!this.amended) {
                     this.dirtyLocked();
                     this.amended = true;
                  }

                  V computed = remappingFunction.apply(key, (Object)null);
                  if (computed != null) {
                     this.dirty.put(key, new SyncMapImpl.ExpungingEntryImpl(computed));
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
   public V putIfAbsent(@Nullable final K key, @NonNull final V value) {
      Objects.requireNonNull(value, "value");
      SyncMap.ExpungingEntry<V> entry = (SyncMap.ExpungingEntry)this.read.get(key);
      SyncMap.InsertionResult<V> result = entry != null ? entry.setIfAbsent(value) : null;
      if (result != null && result.operation() == 1) {
         return result.previous();
      } else {
         synchronized(this.lock) {
            if ((entry = (SyncMap.ExpungingEntry)this.read.get(key)) != null) {
               if (entry.tryUnexpungeAndSet(value)) {
                  this.dirty.put(key, entry);
                  return null;
               }

               result = entry.setIfAbsent(value);
            } else {
               if (this.dirty == null || (entry = (SyncMap.ExpungingEntry)this.dirty.get(key)) == null) {
                  if (!this.amended) {
                     this.dirtyLocked();
                     this.amended = true;
                  }

                  this.dirty.put(key, new SyncMapImpl.ExpungingEntryImpl(value));
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
   public V put(@Nullable final K key, @NonNull final V value) {
      Objects.requireNonNull(value, "value");
      SyncMap.ExpungingEntry<V> entry = (SyncMap.ExpungingEntry)this.read.get(key);
      V previous = entry != null ? entry.get() : null;
      if (entry != null && entry.trySet(value)) {
         return previous;
      } else {
         synchronized(this.lock) {
            if ((entry = (SyncMap.ExpungingEntry)this.read.get(key)) != null) {
               previous = entry.get();
               if (entry.tryUnexpungeAndSet(value)) {
                  this.dirty.put(key, entry);
               } else {
                  entry.set(value);
               }
            } else {
               if (this.dirty == null || (entry = (SyncMap.ExpungingEntry)this.dirty.get(key)) == null) {
                  if (!this.amended) {
                     this.dirtyLocked();
                     this.amended = true;
                  }

                  this.dirty.put(key, new SyncMapImpl.ExpungingEntryImpl(value));
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
   public V remove(@Nullable final Object key) {
      SyncMap.ExpungingEntry<V> entry = (SyncMap.ExpungingEntry)this.read.get(key);
      if (entry == null && this.amended) {
         synchronized(this.lock) {
            if ((entry = (SyncMap.ExpungingEntry)this.read.get(key)) == null && this.amended && this.dirty != null) {
               entry = (SyncMap.ExpungingEntry)this.dirty.remove(key);
               this.missLocked();
            }
         }
      }

      return entry != null ? entry.clear() : null;
   }

   public boolean remove(@Nullable final Object key, @NonNull final Object value) {
      Objects.requireNonNull(value, "value");
      SyncMap.ExpungingEntry<V> entry = (SyncMap.ExpungingEntry)this.read.get(key);
      if (entry == null && this.amended) {
         synchronized(this.lock) {
            if ((entry = (SyncMap.ExpungingEntry)this.read.get(key)) == null && this.amended && this.dirty != null) {
               boolean present = (entry = (SyncMap.ExpungingEntry)this.dirty.get(key)) != null && entry.replace(value, (Object)null);
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
   public V replace(@Nullable final K key, @NonNull final V value) {
      Objects.requireNonNull(value, "value");
      SyncMap.ExpungingEntry<V> entry = this.getEntry(key);
      return entry != null ? entry.tryReplace(value) : null;
   }

   public boolean replace(@Nullable final K key, @NonNull final V oldValue, @NonNull final V newValue) {
      Objects.requireNonNull(oldValue, "oldValue");
      Objects.requireNonNull(newValue, "newValue");
      SyncMap.ExpungingEntry<V> entry = this.getEntry(key);
      return entry != null && entry.replace(oldValue, newValue);
   }

   public void forEach(@NonNull final BiConsumer<? super K, ? super V> action) {
      Objects.requireNonNull(action, "action");
      this.promote();
      Iterator var3 = this.read.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<K, SyncMap.ExpungingEntry<V>> that = (Entry)var3.next();
         Object value;
         if ((value = ((SyncMap.ExpungingEntry)that.getValue()).get()) != null) {
            action.accept(that.getKey(), value);
         }
      }

   }

   public void replaceAll(@NonNull final BiFunction<? super K, ? super V, ? extends V> function) {
      Objects.requireNonNull(function, "function");
      this.promote();
      Iterator var4 = this.read.entrySet().iterator();

      while(var4.hasNext()) {
         Entry<K, SyncMap.ExpungingEntry<V>> that = (Entry)var4.next();
         SyncMap.ExpungingEntry entry;
         Object value;
         if ((value = (entry = (SyncMap.ExpungingEntry)that.getValue()).get()) != null) {
            entry.tryReplace(function.apply(that.getKey(), value));
         }
      }

   }

   public void clear() {
      synchronized(this.lock) {
         this.read = (Map)this.function.apply(this.read.size());
         this.dirty = null;
         this.amended = false;
         this.misses = 0;
      }
   }

   @NonNull
   public Set<Entry<K, V>> entrySet() {
      return this.entrySet != null ? this.entrySet : (this.entrySet = new SyncMapImpl.EntrySetView());
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
         this.dirty = (Map)this.function.apply(this.read.size());
         Iterator var1 = this.read.entrySet().iterator();

         while(var1.hasNext()) {
            Entry<K, SyncMap.ExpungingEntry<V>> entry = (Entry)var1.next();
            if (!((SyncMap.ExpungingEntry)entry.getValue()).tryExpunge()) {
               this.dirty.put(entry.getKey(), (SyncMap.ExpungingEntry)entry.getValue());
            }
         }

      }
   }

   final class EntryIterator implements Iterator<Entry<K, V>> {
      private final Iterator<Entry<K, SyncMap.ExpungingEntry<V>>> backingIterator;
      private Entry<K, V> next;
      private Entry<K, V> current;

      EntryIterator(@NonNull final Iterator<Entry<K, SyncMap.ExpungingEntry<V>>> backingIterator) {
         this.backingIterator = backingIterator;
         this.advance();
      }

      public boolean hasNext() {
         return this.next != null;
      }

      @NonNull
      public Entry<K, V> next() {
         Entry current;
         if ((current = this.next) == null) {
            throw new NoSuchElementException();
         } else {
            this.current = current;
            this.advance();
            return current;
         }
      }

      public void remove() {
         Entry current;
         if ((current = this.current) == null) {
            throw new IllegalStateException();
         } else {
            this.current = null;
            SyncMapImpl.this.remove(current.getKey());
         }
      }

      private void advance() {
         this.next = null;

         Entry entry;
         Object value;
         do {
            if (!this.backingIterator.hasNext()) {
               return;
            }
         } while((value = ((SyncMap.ExpungingEntry)(entry = (Entry)this.backingIterator.next()).getValue()).get()) == null);

         this.next = SyncMapImpl.this.new MapEntry(entry.getKey(), value);
      }
   }

   final class EntrySetView extends AbstractSet<Entry<K, V>> {
      public int size() {
         return SyncMapImpl.this.size();
      }

      public boolean contains(@Nullable final Object entry) {
         if (!(entry instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> mapEntry = (Entry)entry;
            V value = SyncMapImpl.this.get(mapEntry.getKey());
            return value != null && Objects.equals(value, mapEntry.getValue());
         }
      }

      public boolean add(@NonNull final Entry<K, V> entry) {
         Objects.requireNonNull(entry, "entry");
         return SyncMapImpl.this.put(entry.getKey(), entry.getValue()) == null;
      }

      public boolean remove(@Nullable final Object entry) {
         if (!(entry instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> mapEntry = (Entry)entry;
            return SyncMapImpl.this.remove(mapEntry.getKey(), mapEntry.getValue());
         }
      }

      public void clear() {
         SyncMapImpl.this.clear();
      }

      @NonNull
      public Iterator<Entry<K, V>> iterator() {
         SyncMapImpl.this.promote();
         return SyncMapImpl.this.new EntryIterator(SyncMapImpl.this.read.entrySet().iterator());
      }
   }

   final class MapEntry implements Entry<K, V> {
      private final K key;
      private V value;

      MapEntry(@Nullable final K key, @NonNull final V value) {
         this.key = key;
         this.value = value;
      }

      @Nullable
      public K getKey() {
         return this.key;
      }

      @NonNull
      public V getValue() {
         return this.value;
      }

      @Nullable
      public V setValue(@NonNull final V value) {
         Objects.requireNonNull(value, "value");
         V previous = SyncMapImpl.this.put(this.key, value);
         this.value = value;
         return previous;
      }

      @NonNull
      public String toString() {
         return "SyncMapImpl.MapEntry{key=" + this.getKey() + ", value=" + this.getValue() + "}";
      }

      public boolean equals(@Nullable final Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> that = (Entry)other;
            return Objects.equals(this.getKey(), that.getKey()) && Objects.equals(this.getValue(), that.getValue());
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.getKey(), this.getValue()});
      }
   }

   static final class InsertionResultImpl<V> implements SyncMap.InsertionResult<V> {
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

   static final class ExpungingEntryImpl<V> implements SyncMap.ExpungingEntry<V> {
      private static final AtomicReferenceFieldUpdater<SyncMapImpl.ExpungingEntryImpl, Object> UPDATER = AtomicReferenceFieldUpdater.newUpdater(SyncMapImpl.ExpungingEntryImpl.class, Object.class, "value");
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
      public SyncMap.InsertionResult<V> setIfAbsent(@NonNull final V value) {
         do {
            Object previous = this.value;
            if (previous == EXPUNGED) {
               return new SyncMapImpl.InsertionResultImpl((byte)2, (Object)null, (Object)null);
            }

            if (previous != null) {
               return new SyncMapImpl.InsertionResultImpl((byte)0, previous, previous);
            }
         } while(!UPDATER.compareAndSet(this, (Object)null, value));

         return new SyncMapImpl.InsertionResultImpl((byte)1, (Object)null, value);
      }

      @NonNull
      public <K> SyncMap.InsertionResult<V> computeIfAbsent(@Nullable final K key, @NonNull final Function<? super K, ? extends V> function) {
         Object next = null;

         do {
            Object previous = this.value;
            if (previous == EXPUNGED) {
               return new SyncMapImpl.InsertionResultImpl((byte)2, (Object)null, (Object)null);
            }

            if (previous != null) {
               return new SyncMapImpl.InsertionResultImpl((byte)0, previous, previous);
            }
         } while(!UPDATER.compareAndSet(this, (Object)null, next != null ? next : (next = function.apply(key))));

         return new SyncMapImpl.InsertionResultImpl((byte)1, (Object)null, next);
      }

      @NonNull
      public <K> SyncMap.InsertionResult<V> computeIfPresent(@Nullable final K key, @NonNull final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
         Object next = null;

         Object previous;
         do {
            previous = this.value;
            if (previous == EXPUNGED) {
               return new SyncMapImpl.InsertionResultImpl((byte)2, (Object)null, (Object)null);
            }

            if (previous == null) {
               return new SyncMapImpl.InsertionResultImpl((byte)0, (Object)null, (Object)null);
            }
         } while(!UPDATER.compareAndSet(this, previous, next != null ? next : (next = remappingFunction.apply(key, previous))));

         return new SyncMapImpl.InsertionResultImpl((byte)1, previous, next);
      }

      @NonNull
      public <K> SyncMap.InsertionResult<V> compute(@Nullable final K key, @NonNull final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
         Object next = null;

         Object previous;
         do {
            previous = this.value;
            if (previous == EXPUNGED) {
               return new SyncMapImpl.InsertionResultImpl((byte)2, (Object)null, (Object)null);
            }
         } while(!UPDATER.compareAndSet(this, previous, next != null ? next : (next = remappingFunction.apply(key, previous))));

         return new SyncMapImpl.InsertionResultImpl((byte)1, previous, next);
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

      public <K> boolean tryUnexpungeAndCompute(@Nullable final K key, @NonNull final Function<? super K, ? extends V> function) {
         if (this.value == EXPUNGED) {
            Object value = function.apply(key);
            return UPDATER.compareAndSet(this, EXPUNGED, value);
         } else {
            return false;
         }
      }

      public <K> boolean tryUnexpungeAndCompute(@Nullable final K key, @NonNull final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
         if (this.value == EXPUNGED) {
            Object value = remappingFunction.apply(key, (Object)null);
            return UPDATER.compareAndSet(this, EXPUNGED, value);
         } else {
            return false;
         }
      }
   }
}
