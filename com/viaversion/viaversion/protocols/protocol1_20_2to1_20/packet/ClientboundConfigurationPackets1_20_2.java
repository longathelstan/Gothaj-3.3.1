package com.viaversion.viaversion.protocols.protocol1_20_2to1_20.packet;

import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;

public enum ClientboundConfigurationPackets1_20_2 implements ClientboundPacketType {
   CUSTOM_PAYLOAD,
   DISCONNECT,
   FINISH_CONFIGURATION,
   KEEP_ALIVE,
   PING,
   REGISTRY_DATA,
   RESOURCE_PACK,
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
