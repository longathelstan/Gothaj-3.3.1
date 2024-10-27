package com.viaversion.viaversion.protocol;

import com.viaversion.viaversion.api.protocol.version.BlockedProtocolVersions;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;

public class BlockedProtocolVersionsImpl implements BlockedProtocolVersions {
   private final IntSet singleBlockedVersions;
   private final int blocksBelow;
   private final int blocksAbove;

   public BlockedProtocolVersionsImpl(IntSet singleBlockedVersions, int blocksBelow, int blocksAbove) {
      this.singleBlockedVersions = singleBlockedVersions;
      this.blocksBelow = blocksBelow;
      this.blocksAbove = blocksAbove;
   }

   public boolean contains(int protocolVersion) {
      return this.blocksBelow != -1 && protocolVersion < this.blocksBelow || this.blocksAbove != -1 && protocolVersion > this.blocksAbove || this.singleBlockedVersions.contains(protocolVersion);
   }

   public int blocksBelow() {
      return this.blocksBelow;
   }

   public int blocksAbove() {
      return this.blocksAbove;
   }

   public IntSet singleBlockedVersions() {
      return this.singleBlockedVersions;
   }
}
