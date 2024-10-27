package com.viaversion.viaversion.api.protocol.packet.provider;

import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface PacketTypeMap<P extends PacketType> {
   @Nullable
   P typeByName(String var1);

   @Nullable
   P typeById(int var1);

   Collection<P> types();

   static <S extends PacketType, T extends S> PacketTypeMap<S> of(Class<T> enumClass) {
      if (!enumClass.isEnum()) {
         throw new IllegalArgumentException("Given class is not an enum");
      } else {
         S[] types = (PacketType[])enumClass.getEnumConstants();
         Map<String, S> byName = new HashMap(types.length);
         PacketType[] var3 = types;
         int var4 = types.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            S type = var3[var5];
            byName.put(type.getName(), type);
         }

         return of(byName, (PacketType[])types);
      }
   }

   static <T extends PacketType> PacketTypeMap<T> of(Map<String, T> packetsByName, Int2ObjectMap<T> packetsById) {
      return new PacketTypeMapMap(packetsByName, packetsById);
   }

   static <T extends PacketType> PacketTypeMap<T> of(Map<String, T> packetsByName, T[] packets) {
      return new PacketTypeArrayMap(packetsByName, packets);
   }
}
