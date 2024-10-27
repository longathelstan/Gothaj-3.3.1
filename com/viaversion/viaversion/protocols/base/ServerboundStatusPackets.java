package com.viaversion.viaversion.protocols.base;

import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;

public enum ServerboundStatusPackets implements ServerboundPacketType {
   STATUS_REQUEST,
   PING_REQUEST;

   public final int getId() {
      return this.ordinal();
   }

   public final String getName() {
      return this.name();
   }

   public final State state() {
      return State.STATUS;
   }
}
