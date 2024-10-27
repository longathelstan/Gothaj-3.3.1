package com.viaversion.viaversion.api.protocol;

public interface ProtocolPathKey {
   int clientProtocolVersion();

   int serverProtocolVersion();

   /** @deprecated */
   @Deprecated
   default int getClientProtocolVersion() {
      return this.clientProtocolVersion();
   }

   /** @deprecated */
   @Deprecated
   default int getServerProtocolVersion() {
      return this.serverProtocolVersion();
   }
}
