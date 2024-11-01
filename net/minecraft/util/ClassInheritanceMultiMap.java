package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.optifine.util.IteratorCache;

public class ClassInheritanceMultiMap<T> extends AbstractSet<T> {
   private static final Set<Class<?>> field_181158_a = Collections.newSetFromMap(new ConcurrentHashMap());
   private final Map<Class<?>, List<T>> map = Maps.newHashMap();
   private final Set<Class<?>> knownKeys = Sets.newIdentityHashSet();
   private final Class<T> baseClass;
   private final List<T> values = Lists.newArrayList();
   public boolean empty;

   public ClassInheritanceMultiMap(Class<T> baseClassIn) {
      this.baseClass = baseClassIn;
      this.knownKeys.add(baseClassIn);
      this.map.put(baseClassIn, this.values);
      Iterator var3 = field_181158_a.iterator();

      while(var3.hasNext()) {
         Class<?> oclass = (Class)var3.next();
         this.createLookup(oclass);
      }

      this.empty = this.values.size() == 0;
   }

   protected void createLookup(Class<?> clazz) {
      field_181158_a.add(clazz);
      int i = this.values.size();

      for(int j = 0; j < i; ++j) {
         T t = this.values.get(j);
         if (clazz.isAssignableFrom(t.getClass())) {
            this.addForClass(t, clazz);
         }
      }

      this.knownKeys.add(clazz);
   }

   protected Class<?> initializeClassLookup(Class<?> clazz) {
      if (this.baseClass.isAssignableFrom(clazz)) {
         if (!this.knownKeys.contains(clazz)) {
            this.createLookup(clazz);
         }

         return clazz;
      } else {
         throw new IllegalArgumentException("Don't know how to search for " + clazz);
      }
   }

   public boolean add(T p_add_1_) {
      Iterator var3 = this.knownKeys.iterator();

      while(var3.hasNext()) {
         Class<?> oclass = (Class)var3.next();
         if (oclass.isAssignableFrom(p_add_1_.getClass())) {
            this.addForClass(p_add_1_, oclass);
         }
      }

      this.empty = this.values.size() == 0;
      return true;
   }

   private void addForClass(T value, Class<?> parentClass) {
      List<T> list = (List)this.map.get(parentClass);
      if (list == null) {
         this.map.put(parentClass, Lists.newArrayList(new Object[]{value}));
      } else {
         list.add(value);
      }

      this.empty = this.values.size() == 0;
   }

   public boolean remove(Object p_remove_1_) {
      T t = p_remove_1_;
      boolean flag = false;
      Iterator var5 = this.knownKeys.iterator();

      while(var5.hasNext()) {
         Class<?> oclass = (Class)var5.next();
         if (oclass.isAssignableFrom(t.getClass())) {
            List<T> list = (List)this.map.get(oclass);
            if (list != null && list.remove(t)) {
               flag = true;
            }
         }
      }

      this.empty = this.values.size() == 0;
      return flag;
   }

   public boolean contains(Object p_contains_1_) {
      return Iterators.contains(this.getByClass(p_contains_1_.getClass()).iterator(), p_contains_1_);
   }

   public <S> Iterable<S> getByClass(final Class<S> clazz) {
      return new Iterable<S>() {
         public Iterator<S> iterator() {
            List<T> list = (List)ClassInheritanceMultiMap.this.map.get(ClassInheritanceMultiMap.this.initializeClassLookup(clazz));
            if (list == null) {
               return Iterators.emptyIterator();
            } else {
               Iterator<T> iterator = list.iterator();
               return Iterators.filter(iterator, clazz);
            }
         }
      };
   }

   public Iterator<T> iterator() {
      return (Iterator)(this.values.isEmpty() ? Iterators.emptyIterator() : IteratorCache.getReadOnly(this.values));
   }

   public int size() {
      return this.values.size();
   }

   public boolean isEmpty() {
      return this.empty;
   }
}
