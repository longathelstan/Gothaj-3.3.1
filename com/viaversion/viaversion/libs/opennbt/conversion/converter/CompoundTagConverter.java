package com.viaversion.viaversion.libs.opennbt.conversion.converter;

import com.viaversion.viaversion.libs.opennbt.conversion.ConverterRegistry;
import com.viaversion.viaversion.libs.opennbt.conversion.TagConverter;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class CompoundTagConverter implements TagConverter<CompoundTag, Map> {
   public Map convert(CompoundTag tag) {
      Map<String, Object> ret = new HashMap();
      Map<String, Tag> tags = tag.getValue();
      Iterator var4 = tags.entrySet().iterator();

      while(var4.hasNext()) {
         Entry<String, Tag> entry = (Entry)var4.next();
         ret.put((String)entry.getKey(), ConverterRegistry.convertToValue((Tag)entry.getValue()));
      }

      return ret;
   }

   public CompoundTag convert(Map value) {
      Map<String, Tag> tags = new HashMap();
      Iterator var3 = value.keySet().iterator();

      while(var3.hasNext()) {
         Object na = var3.next();
         String n = (String)na;
         tags.put(n, ConverterRegistry.convertToTag(value.get(n)));
      }

      return new CompoundTag(tags);
   }
}
