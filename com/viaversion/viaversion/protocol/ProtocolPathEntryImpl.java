package com.viaversion.viaversion.protocol;

import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.ProtocolPathEntry;

public class ProtocolPathEntryImpl implements ProtocolPathEntry {
   private final int outputProtocolVersion;
   private final Protocol<?, ?, ?, ?> protocol;

   public ProtocolPathEntryImpl(int outputProtocolVersion, Protocol<?, ?, ?, ?> protocol) {
      this.outputProtocolVersion = outputProtocolVersion;
      this.protocol = protocol;
   }

   public int outputProtocolVersion() {
      return this.outputProtocolVersion;
   }

   public Protocol<?, ?, ?, ?> protocol() {
      return this.protocol;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ProtocolPathEntryImpl that = (ProtocolPathEntryImpl)o;
         return this.outputProtocolVersion != that.outputProtocolVersion ? false : this.protocol.equals(that.protocol);
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.outputProtocolVersion;
      result = 31 * result + this.protocol.hashCode();
      return result;
   }

   public String toString() {
      return "ProtocolPathEntryImpl{outputProtocolVersion=" + this.outputProtocolVersion + ", protocol=" + this.protocol + '}';
   }
}
