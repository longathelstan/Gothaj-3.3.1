package com.viaversion.viaversion.api.debug;

import com.google.common.annotations.Beta;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;

@Beta
public interface DebugHandler {
   boolean enabled();

   void setEnabled(boolean var1);

   void addPacketTypeNameToLog(String var1);

   void addPacketTypeToLog(PacketType var1);

   boolean removePacketTypeNameToLog(String var1);

   void clearPacketTypesToLog();

   boolean logPostPacketTransform();

   void setLogPostPacketTransform(boolean var1);

   boolean shouldLog(PacketWrapper var1, Direction var2);

   default void enableAndLogIds(PacketType... packetTypes) {
      this.setEnabled(true);
      PacketType[] var2 = packetTypes;
      int var3 = packetTypes.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         PacketType packetType = var2[var4];
         this.addPacketTypeToLog(packetType);
      }

   }
}
