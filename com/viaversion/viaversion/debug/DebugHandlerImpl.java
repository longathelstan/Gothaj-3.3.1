package com.viaversion.viaversion.debug;

import com.viaversion.viaversion.api.debug.DebugHandler;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.libs.fastutil.ints.IntOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;
import java.util.HashSet;
import java.util.Set;

public final class DebugHandlerImpl implements DebugHandler {
   private final Set<String> packetTypesToLog = new HashSet();
   private final IntSet clientboundPacketIdsToLog = new IntOpenHashSet();
   private final IntSet serverboundPacketIdsToLog = new IntOpenHashSet();
   private boolean logPostPacketTransform;
   private boolean enabled;

   public boolean enabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public void addPacketTypeNameToLog(String packetTypeName) {
      this.packetTypesToLog.add(packetTypeName);
   }

   public void addPacketTypeToLog(PacketType packetType) {
      (packetType.direction() == Direction.SERVERBOUND ? this.serverboundPacketIdsToLog : this.clientboundPacketIdsToLog).add(packetType.getId());
   }

   public boolean removePacketTypeNameToLog(String packetTypeName) {
      return this.packetTypesToLog.remove(packetTypeName);
   }

   public void clearPacketTypesToLog() {
      this.packetTypesToLog.clear();
   }

   public boolean logPostPacketTransform() {
      return this.logPostPacketTransform;
   }

   public void setLogPostPacketTransform(boolean logPostPacketTransform) {
      this.logPostPacketTransform = logPostPacketTransform;
   }

   public boolean shouldLog(PacketWrapper wrapper, Direction direction) {
      return this.packetTypesToLog.isEmpty() && this.serverboundPacketIdsToLog.isEmpty() && this.clientboundPacketIdsToLog.isEmpty() || wrapper.getPacketType() != null && this.packetTypesToLog.contains(wrapper.getPacketType().getName()) || (direction == Direction.SERVERBOUND ? this.serverboundPacketIdsToLog : this.clientboundPacketIdsToLog).contains(wrapper.getId());
   }
}
