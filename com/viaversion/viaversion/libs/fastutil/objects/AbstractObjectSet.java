package com.viaversion.viaversion.libs.fastutil.objects;

import java.util.Set;

public abstract class AbstractObjectSet<K> extends AbstractObjectCollection<K> implements Cloneable, ObjectSet<K> {
   protected AbstractObjectSet() {
   }

   public abstract ObjectIterator<K> iterator();

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Set)) {
         return false;
      } else {
         Set<?> s = (Set)o;
         return s.size() != this.size() ? false : this.containsAll(s);
      }
   }

   public int hashCode() {
      int h = 0;
      int n = this.size();

      Object k;
      for(ObjectIterator i = this.iterator(); n-- != 0; h += k == null ? 0 : k.hashCode()) {
         k = i.next();
      }

      return h;
   }
}
