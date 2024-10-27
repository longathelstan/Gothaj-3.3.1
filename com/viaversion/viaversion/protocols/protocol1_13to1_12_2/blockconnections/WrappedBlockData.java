package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import com.viaversion.viaversion.util.Key;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public final class WrappedBlockData {
   private final LinkedHashMap<String, String> blockData = new LinkedHashMap();
   private final String minecraftKey;
   private final int savedBlockStateId;

   public static WrappedBlockData fromString(String s) {
      String[] array = s.split("\\[");
      String key = array[0];
      WrappedBlockData wrappedBlockdata = new WrappedBlockData(key, ConnectionData.getId(s));
      if (array.length > 1) {
         String blockData = array[1];
         blockData = blockData.replace("]", "");
         String[] data = blockData.split(",");
         String[] var6 = data;
         int var7 = data.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            String d = var6[var8];
            String[] a = d.split("=");
            wrappedBlockdata.blockData.put(a[0], a[1]);
         }
      }

      return wrappedBlockdata;
   }

   private WrappedBlockData(String minecraftKey, int savedBlockStateId) {
      this.minecraftKey = Key.namespaced(minecraftKey);
      this.savedBlockStateId = savedBlockStateId;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder(this.minecraftKey + "[");
      Iterator var2 = this.blockData.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<String, String> entry = (Entry)var2.next();
         sb.append((String)entry.getKey()).append('=').append((String)entry.getValue()).append(',');
      }

      return sb.substring(0, sb.length() - 1) + "]";
   }

   public String getMinecraftKey() {
      return this.minecraftKey;
   }

   public int getSavedBlockStateId() {
      return this.savedBlockStateId;
   }

   public int getBlockStateId() {
      return ConnectionData.getId(this.toString());
   }

   public WrappedBlockData set(String data, Object value) {
      if (!this.hasData(data)) {
         throw new UnsupportedOperationException("No blockdata found for " + data + " at " + this.minecraftKey);
      } else {
         this.blockData.put(data, value.toString());
         return this;
      }
   }

   public String getValue(String data) {
      return (String)this.blockData.get(data);
   }

   public boolean hasData(String key) {
      return this.blockData.containsKey(key);
   }
}
