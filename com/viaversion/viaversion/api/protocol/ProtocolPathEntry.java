package com.viaversion.viaversion.api.protocol;

public interface ProtocolPathEntry {
   int outputProtocolVersion();

   Protocol<?, ?, ?, ?> protocol();

   /** @deprecated */
   @Deprecated
   default int getOutputProtocolVersion() {
      return this.outputProtocolVersion();
   }

   /** @deprecated */
   @Deprecated
   default Protocol getProtocol() {
      return this.protocol();
   }
}
