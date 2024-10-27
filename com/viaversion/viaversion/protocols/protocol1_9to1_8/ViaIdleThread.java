package com.viaversion.viaversion.protocols.protocol1_9to1_8;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.MovementTracker;
import java.util.Iterator;

public class ViaIdleThread implements Runnable {
   public void run() {
      Iterator var1 = Via.getManager().getConnectionManager().getConnections().iterator();

      while(var1.hasNext()) {
         UserConnection info = (UserConnection)var1.next();
         ProtocolInfo protocolInfo = info.getProtocolInfo();
         if (protocolInfo != null && protocolInfo.getPipeline().contains(Protocol1_9To1_8.class)) {
            MovementTracker movementTracker = (MovementTracker)info.get(MovementTracker.class);
            if (movementTracker != null) {
               long nextIdleUpdate = movementTracker.getNextIdlePacket();
               if (nextIdleUpdate <= System.currentTimeMillis() && info.getChannel().isOpen()) {
                  ((MovementTransmitterProvider)Via.getManager().getProviders().get(MovementTransmitterProvider.class)).sendPlayer(info);
               }
            }
         }
      }

   }
}
