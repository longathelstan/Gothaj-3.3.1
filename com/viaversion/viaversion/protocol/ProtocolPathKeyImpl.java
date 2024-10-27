package com.viaversion.viaversion.protocol;

import com.viaversion.viaversion.api.protocol.ProtocolPathKey;

public class ProtocolPathKeyImpl implements ProtocolPathKey {
   private final int clientProtocolVersion;
   private final int serverProtocolVersion;

   public ProtocolPathKeyImpl(int clientProtocolVersion, int serverProtocolVersion) {
      this.clientProtocolVersion = clientProtocolVersion;
      this.serverProtocolVersion = serverProtocolVersion;
   }

   public int clientProtocolVersion() {
      return this.clientProtocolVersion;
   }

   public int serverProtocolVersion() {
      return this.serverProtocolVersion;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ProtocolPathKeyImpl that = (ProtocolPathKeyImpl)o;
         if (this.clientProtocolVersion != that.clientProtocolVersion) {
            return false;
         } else {
            return this.serverProtocolVersion == that.serverProtocolVersion;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.clientProtocolVersion;
      result = 31 * result + this.serverProtocolVersion;
      return result;
   }
}
