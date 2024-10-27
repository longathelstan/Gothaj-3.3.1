package com.viaversion.viaversion.api.protocol.packet.provider;

import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import java.util.Collection;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

final class PacketTypeMapMap<P extends PacketType> implements PacketTypeMap<P> {
   private final Map<String, P> packetsByName;
   private final Int2ObjectMap<P> packetsById;

   PacketTypeMapMap(Map<String, P> packetsByName, Int2ObjectMap<P> packetsById) {
      this.packetsByName = packetsByName;
      this.packetsById = packetsById;
   }

   @Nullable
   public P typeByName(String packetTypeName) {
      return (PacketType)this.packetsByName.get(packetTypeName);
   }

   @Nullable
   public P typeById(int packetTypeId) {
      return (PacketType)this.packetsById.get(packetTypeId);
   }

   public Collection<P> types() {
      return this.packetsById.values();
   }
}
