package com.viaversion.viaversion;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.legacy.LegacyViaAPI;
import com.viaversion.viaversion.api.protocol.version.BlockedProtocolVersions;
import com.viaversion.viaversion.api.protocol.version.ServerProtocolVersion;
import com.viaversion.viaversion.legacy.LegacyAPI;
import io.netty.buffer.ByteBuf;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class ViaAPIBase<T> implements ViaAPI<T> {
   private final LegacyAPI<T> legacy = new LegacyAPI();

   public ServerProtocolVersion getServerVersion() {
      return Via.getManager().getProtocolManager().getServerProtocolVersion();
   }

   public int getPlayerVersion(UUID uuid) {
      UserConnection connection = Via.getManager().getConnectionManager().getConnectedClient(uuid);
      return connection != null ? connection.getProtocolInfo().getProtocolVersion() : -1;
   }

   public String getVersion() {
      return Via.getPlatform().getPluginVersion();
   }

   public boolean isInjected(UUID uuid) {
      return Via.getManager().getConnectionManager().isClientConnected(uuid);
   }

   @Nullable
   public UserConnection getConnection(UUID uuid) {
      return Via.getManager().getConnectionManager().getConnectedClient(uuid);
   }

   public void sendRawPacket(UUID uuid, ByteBuf packet) throws IllegalArgumentException {
      if (!this.isInjected(uuid)) {
         throw new IllegalArgumentException("This player is not controlled by ViaVersion!");
      } else {
         UserConnection user = Via.getManager().getConnectionManager().getConnectedClient(uuid);
         user.scheduleSendRawPacket(packet);
      }
   }

   public SortedSet<Integer> getSupportedVersions() {
      SortedSet<Integer> outputSet = new TreeSet(Via.getManager().getProtocolManager().getSupportedVersions());
      BlockedProtocolVersions blockedVersions = Via.getPlatform().getConf().blockedProtocolVersions();
      outputSet.removeIf(blockedVersions::contains);
      return outputSet;
   }

   public SortedSet<Integer> getFullSupportedVersions() {
      return Via.getManager().getProtocolManager().getSupportedVersions();
   }

   public LegacyViaAPI<T> legacyAPI() {
      return this.legacy;
   }
}
