package com.viaversion.viaversion.libs.opennbt.tag.limiter;

final class TagLimiterImpl implements TagLimiter {
   private final int maxBytes;
   private final int maxLevels;
   private int bytes;

   TagLimiterImpl(int maxBytes, int maxLevels) {
      this.maxBytes = maxBytes;
      this.maxLevels = maxLevels;
   }

   public void countBytes(int bytes) {
      this.bytes += bytes;
      if (this.bytes >= this.maxBytes) {
         throw new IllegalArgumentException("NBT data larger than expected (capped at " + this.maxBytes + ")");
      }
   }

   public void checkLevel(int nestedLevel) {
      if (nestedLevel >= this.maxLevels) {
         throw new IllegalArgumentException("Nesting level higher than expected (capped at " + this.maxLevels + ")");
      }
   }

   public int maxBytes() {
      return this.maxBytes;
   }

   public int maxLevels() {
      return this.maxLevels;
   }

   public int bytes() {
      return this.bytes;
   }

   public void reset() {
      this.bytes = 0;
   }
}
