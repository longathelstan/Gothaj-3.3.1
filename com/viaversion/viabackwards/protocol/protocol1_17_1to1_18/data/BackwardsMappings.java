package com.viaversion.viabackwards.protocol.protocol1_17_1to1_18.data;

import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterator;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.Protocol1_18To1_17_1;

public final class BackwardsMappings extends com.viaversion.viabackwards.api.data.BackwardsMappings {
   private final Int2ObjectMap<String> blockEntities = new Int2ObjectOpenHashMap();

   public BackwardsMappings() {
      super("1.18", "1.17", Protocol1_18To1_17_1.class);
   }

   protected void loadExtras(CompoundTag data) {
      ObjectIterator var2 = Protocol1_18To1_17_1.MAPPINGS.blockEntityIds().object2IntEntrySet().iterator();

      while(var2.hasNext()) {
         Object2IntMap.Entry<String> entry = (Object2IntMap.Entry)var2.next();
         this.blockEntities.put(entry.getIntValue(), (String)entry.getKey());
      }

   }

   public Int2ObjectMap<String> blockEntities() {
      return this.blockEntities;
   }
}
