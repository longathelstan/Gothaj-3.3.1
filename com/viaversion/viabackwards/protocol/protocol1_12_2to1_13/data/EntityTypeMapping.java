package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data;

import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterator;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.EntityTypeRewriter;
import java.lang.reflect.Field;

public class EntityTypeMapping {
   private static final Int2IntMap TYPES = new Int2IntOpenHashMap();

   public static int getOldId(int type1_13) {
      return TYPES.get(type1_13);
   }

   static {
      TYPES.defaultReturnValue(-1);

      try {
         Field field = EntityTypeRewriter.class.getDeclaredField("ENTITY_TYPES");
         field.setAccessible(true);
         Int2IntMap entityTypes = (Int2IntMap)field.get((Object)null);
         ObjectIterator var2 = entityTypes.int2IntEntrySet().iterator();

         while(var2.hasNext()) {
            Int2IntMap.Entry entry = (Int2IntMap.Entry)var2.next();
            TYPES.put(entry.getIntValue(), entry.getIntKey());
         }
      } catch (IllegalAccessException | NoSuchFieldException var4) {
         var4.printStackTrace();
      }

   }
}
