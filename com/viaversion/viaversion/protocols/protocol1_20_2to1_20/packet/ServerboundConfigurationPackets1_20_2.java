package com.viaversion.viaversion.protocols.protocol1_20_2to1_20.packet;

import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;

public enum ServerboundConfigurationPackets1_20_2 implements ServerboundPacketType {
   CLIENT_INFORMATION,
   CUSTOM_PAYLOAD,
   FINISH_CONFIGURATION,
   KEEP_ALIVE,
   PONG,
   RESOURCE_PACK;

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
