package com.viaversion.viaversion.api.data;

import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterator;

public class Int2IntMapMappings implements Mappings {
   private final Int2IntMap mappings;
   private final int mappedIds;

   protected Int2IntMapMappings(Int2IntMap mappings, int mappedIds) {
      this.mappings = mappings;
      this.mappedIds = mappedIds;
      mappings.defaultReturnValue(-1);
   }

   public static Int2IntMapMappings of(Int2IntMap mappings, int mappedIds) {
      return new Int2IntMapMappings(mappings, mappedIds);
   }

   public static Int2IntMapMappings of() {
      return new Int2IntMapMappings(new Int2IntOpenHashMap(), -1);
   }

   public int getNewId(int id) {
      return this.mappings.get(id);
   }

   public void setNewId(int id, int mappedId) {
      this.mappings.put(id, mappedId);
   }

   public int size() {
      return this.mappings.size();
   }

   public int mappedSize() {
      return this.mappedIds;
   }

   public Mappings inverse() {
      Int2IntMap inverse = new Int2IntOpenHashMap();
      inverse.defaultReturnValue(-1);
      ObjectIterator var2 = this.mappings.int2IntEntrySet().iterator();

      while(var2.hasNext()) {
         Int2IntMap.Entry entry = (Int2IntMap.Entry)var2.next();
         if (entry.getIntValue() != -1) {
            inverse.putIfAbsent(entry.getIntValue(), entry.getIntKey());
         }
      }

      return of(inverse, this.size());
   }
}
