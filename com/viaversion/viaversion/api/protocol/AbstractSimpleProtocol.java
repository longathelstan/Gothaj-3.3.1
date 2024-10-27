package com.viaversion.viaversion.api.protocol;

public abstract class AbstractSimpleProtocol extends AbstractProtocol<SimpleProtocol.DummyPacketTypes, SimpleProtocol.DummyPacketTypes, SimpleProtocol.DummyPacketTypes, SimpleProtocol.DummyPacketTypes> implements SimpleProtocol {
   protected AbstractSimpleProtocol() {
      super((Class)null, (Class)null, (Class)null, (Class)null);
   }
}
