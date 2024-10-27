package com.viaversion.viaversion.protocols.protocol1_20_3to1_20_2.packet;

import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;

public enum ClientboundConfigurationPackets1_20_3 implements ClientboundPacketType {
   CUSTOM_PAYLOAD,
   DISCONNECT,
   FINISH_CONFIGURATION,
   KEEP_ALIVE,
   PING,
   REGISTRY_DATA,
   RESOURCE_PACK_POP,
   RESOURCE_PACK_PUSH,
   UPDATE_ENABLED_FEATURES,
   UPDATE_TAGS;

   public int getId() {
      return this.ordinal();
   }

   public String getName() {
      return this.name();
   }

   public State state() {
      return State.CONFIGURATION;
   }
}
