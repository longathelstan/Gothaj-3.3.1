package com.viaversion.viaversion.protocols.protocol1_13_2to1_13_1;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_13_2to1_13_1.packets.EntityPackets;
import com.viaversion.viaversion.protocols.protocol1_13_2to1_13_1.packets.InventoryPackets;
import com.viaversion.viaversion.protocols.protocol1_13_2to1_13_1.packets.WorldPackets;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;

public class Protocol1_13_2To1_13_1 extends AbstractProtocol<ClientboundPackets1_13, ClientboundPackets1_13, ServerboundPackets1_13, ServerboundPackets1_13> {
   public Protocol1_13_2To1_13_1() {
      super(ClientboundPackets1_13.class, ClientboundPackets1_13.class, ServerboundPackets1_13.class, ServerboundPackets1_13.class);
   }

   protected void registerPackets() {
      InventoryPackets.register(this);
      WorldPackets.register(this);
      EntityPackets.register(this);
      this.registerServerbound(ServerboundPackets1_13.EDIT_BOOK, new PacketHandlers() {
         public void register() {
            this.map(Type.ITEM1_13_2, Type.ITEM1_13);
         }
      });
      this.registerClientbound(ClientboundPackets1_13.ADVANCEMENTS, (wrapper) -> {
         wrapper.passthrough(Type.BOOLEAN);
         int size = (Integer)wrapper.passthrough(Type.VAR_INT);

         for(int i = 0; i < size; ++i) {
            wrapper.passthrough(Type.STRING);
            if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
               wrapper.passthrough(Type.STRING);
            }

            int array;
            if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
               wrapper.passthrough(Type.COMPONENT);
               wrapper.passthrough(Type.COMPONENT);
               Item icon = (Item)wrapper.read(Type.ITEM1_13);
               wrapper.write(Type.ITEM1_13_2, icon);
               wrapper.passthrough(Type.VAR_INT);
               array = (Integer)wrapper.passthrough(Type.INT);
               if ((array & 1) != 0) {
                  wrapper.passthrough(Type.STRING);
               }

               wrapper.passthrough(Type.FLOAT);
               wrapper.passthrough(Type.FLOAT);
            }

            wrapper.passthrough(Type.STRING_ARRAY);
            int arrayLength = (Integer)wrapper.passthrough(Type.VAR_INT);

            for(array = 0; array < arrayLength; ++array) {
               wrapper.passthrough(Type.STRING_ARRAY);
            }
         }

      });
   }
}
