package com.viaversion.viaversion.libs.opennbt.tag.limiter;

final class NoopTagLimiter implements TagLimiter {
   static final TagLimiter INSTANCE = new NoopTagLimiter();

   public void countBytes(int bytes) {
   }

   public void checkLevel(int nestedLevel) {
   }

   public int maxBytes() {
      return Integer.MAX_VALUE;
   }

   public int maxLevels() {
      return Integer.MAX_VALUE;
   }

   public int bytes() {
      return 0;
   }

   public void reset() {
   }
}
