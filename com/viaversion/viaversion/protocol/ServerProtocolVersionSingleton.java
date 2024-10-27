package com.viaversion.viaversion.protocol;

import com.viaversion.viaversion.api.protocol.version.ServerProtocolVersion;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSets;

public class ServerProtocolVersionSingleton implements ServerProtocolVersion {
   private final int protocolVersion;

   public ServerProtocolVersionSingleton(int protocolVersion) {
      this.protocolVersion = protocolVersion;
   }

   public int lowestSupportedVersion() {
      return this.protocolVersion;
   }

   public int highestSupportedVersion() {
      return this.protocolVersion;
   }

   public IntSortedSet supportedVersions() {
      return IntSortedSets.singleton(this.protocolVersion);
   }
}
