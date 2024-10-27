package com.viaversion.viaversion.protocols.protocol1_13to1_12_2;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.TabCompleteTracker;
import java.util.Iterator;

public class TabCompleteThread implements Runnable {
   public void run() {
      Iterator var1 = Via.getManager().getConnectionManager().getConnections().iterator();

      while(var1.hasNext()) {
         UserConnection info = (UserConnection)var1.next();
         if (info.getProtocolInfo() != null && info.getProtocolInfo().getPipeline().contains(Protocol1_13To1_12_2.class) && info.getChannel().isOpen()) {
            ((TabCompleteTracker)info.get(TabCompleteTracker.class)).sendPacketToServer(info);
         }
      }

   }
}
