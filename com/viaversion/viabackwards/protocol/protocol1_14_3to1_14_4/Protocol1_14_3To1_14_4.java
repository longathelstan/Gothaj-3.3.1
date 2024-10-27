package com.viaversion.viabackwards.protocol.protocol1_14_3to1_14_4;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_14_4to1_14_3.ClientboundPackets1_14_4;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;

public class Protocol1_14_3To1_14_4 extends BackwardsProtocol<ClientboundPackets1_14_4, ClientboundPackets1_14, ServerboundPackets1_14, ServerboundPackets1_14> {
   public Protocol1_14_3To1_14_4() {
      super(ClientboundPackets1_14_4.class, ClientboundPackets1_14.class, ServerboundPackets1_14.class, ServerboundPackets1_14.class);
   }

   protected void registerPackets() {
      this.registerClientbound(ClientboundPackets1_14_4.ACKNOWLEDGE_PLAYER_DIGGING, ClientboundPackets1_14.BLOCK_CHANGE, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_14);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               int status = (Integer)wrapper.read(Type.VAR_INT);
               boolean allGood = (Boolean)wrapper.read(Type.BOOLEAN);
               if (allGood && status == 0) {
                  wrapper.cancel();
               }

            });
         }
      });
      this.registerClientbound(ClientboundPackets1_14_4.TRADE_LIST, (wrapper) -> {
         wrapper.passthrough(Type.VAR_INT);
         int size = (Short)wrapper.passthrough(Type.UNSIGNED_BYTE);

         for(int i = 0; i < size; ++i) {
            wrapper.passthrough(Type.ITEM1_13_2);
            wrapper.passthrough(Type.ITEM1_13_2);
            if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
               wrapper.passthrough(Type.ITEM1_13_2);
            }

            wrapper.passthrough(Type.BOOLEAN);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.FLOAT);
            wrapper.read(Type.INT);
         }

      });
   }
}
