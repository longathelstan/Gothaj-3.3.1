package com.viaversion.viaversion.bungee.providers;

import com.google.common.collect.Lists;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocols.base.BaseVersionProvider;
import com.viaversion.viaversion.util.ReflectionUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.ProtocolConstants;

public class BungeeVersionProvider extends BaseVersionProvider {
   public int getClosestServerProtocol(UserConnection user) throws Exception {
      List<Integer> list = (List)ReflectionUtil.getStatic(ProtocolConstants.class, "SUPPORTED_VERSION_IDS", List.class);
      List<Integer> sorted = new ArrayList(list);
      Collections.sort(sorted);
      ProtocolInfo info = user.getProtocolInfo();
      if (sorted.contains(info.getProtocolVersion())) {
         return info.getProtocolVersion();
      } else if (info.getProtocolVersion() < (Integer)sorted.get(0)) {
         return getLowestSupportedVersion();
      } else {
         Iterator var5 = Lists.reverse(sorted).iterator();

         Integer protocol;
         do {
            if (!var5.hasNext()) {
               Via.getPlatform().getLogger().severe("Panic, no protocol id found for " + info.getProtocolVersion());
               return info.getProtocolVersion();
            }

            protocol = (Integer)var5.next();
         } while(info.getProtocolVersion() <= protocol || !ProtocolVersion.isRegistered(protocol));

         return protocol;
      }
   }

   public static int getLowestSupportedVersion() {
      try {
         List<Integer> list = (List)ReflectionUtil.getStatic(ProtocolConstants.class, "SUPPORTED_VERSION_IDS", List.class);
         return (Integer)list.get(0);
      } catch (IllegalAccessException | NoSuchFieldException var2) {
         var2.printStackTrace();
         return ProxyServer.getInstance().getProtocolVersion();
      }
   }
}
