package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data;

import com.google.common.collect.ObjectArrays;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;
import com.viaversion.viaversion.util.GsonUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class BlockIdData {
   public static final String[] PREVIOUS = new String[0];
   public static Map<String, String[]> blockIdMapping;
   public static Map<String, String[]> fallbackReverseMapping;
   public static Int2ObjectMap<String> numberIdToString;

   public static void init() {
      InputStream stream = MappingData.class.getClassLoader().getResourceAsStream("assets/viaversion/data/blockIds1.12to1.13.json");

      try {
         InputStreamReader reader = new InputStreamReader(stream);
         Throwable var2 = null;

         try {
            Map<String, String[]> map = (Map)GsonUtil.getGson().fromJson((Reader)reader, (Type)(new TypeToken<Map<String, String[]>>() {
            }).getType());
            blockIdMapping = new HashMap(map);
            fallbackReverseMapping = new HashMap();
            Iterator var4 = blockIdMapping.entrySet().iterator();

            while(var4.hasNext()) {
               Entry<String, String[]> entry = (Entry)var4.next();
               String[] var6 = (String[])entry.getValue();
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  String val = var6[var8];
                  String[] previous = (String[])fallbackReverseMapping.get(val);
                  if (previous == null) {
                     previous = PREVIOUS;
                  }

                  fallbackReverseMapping.put(val, ObjectArrays.concat(previous, entry.getKey()));
               }
            }
         } catch (Throwable var39) {
            var2 = var39;
            throw var39;
         } finally {
            if (reader != null) {
               if (var2 != null) {
                  try {
                     reader.close();
                  } catch (Throwable var36) {
                     var2.addSuppressed(var36);
                  }
               } else {
                  reader.close();
               }
            }

         }
      } catch (IOException var41) {
         var41.printStackTrace();
      }

      InputStream blockS = MappingData.class.getClassLoader().getResourceAsStream("assets/viaversion/data/blockNumberToString1.12.json");

      try {
         InputStreamReader blockR = new InputStreamReader(blockS);
         Throwable var44 = null;

         try {
            Map<Integer, String> map = (Map)GsonUtil.getGson().fromJson((Reader)blockR, (Type)(new TypeToken<Map<Integer, String>>() {
            }).getType());
            numberIdToString = new Int2ObjectOpenHashMap(map);
         } catch (Throwable var35) {
            var44 = var35;
            throw var35;
         } finally {
            if (blockR != null) {
               if (var44 != null) {
                  try {
                     blockR.close();
                  } catch (Throwable var34) {
                     var44.addSuppressed(var34);
                  }
               } else {
                  blockR.close();
               }
            }

         }
      } catch (IOException var38) {
         var38.printStackTrace();
      }

   }
}
