package com.viaversion.viaversion.bungee.handlers;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.ClientEntityIdChangeListener;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.ProtocolPathEntry;
import com.viaversion.viaversion.api.protocol.ProtocolPipeline;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.bungee.storage.BungeeStorage;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.packets.InventoryPackets;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.EntityIdProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import io.netty.buffer.ByteBuf;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.score.Team;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class BungeeServerHandler implements Listener {
   private static final Method getHandshake;
   private static final Method getRegisteredChannels;
   private static final Method getBrandMessage;
   private static final Method setProtocol;
   private static final Method getEntityMap;
   private static final Method setVersion;
   private static final Field entityRewrite;
   private static final Field channelWrapper;

   @EventHandler(
      priority = 120
   )
   public void onServerConnect(ServerConnectEvent event) {
      if (!event.isCancelled()) {
         UserConnection user = Via.getManager().getConnectionManager().getConnectedClient(event.getPlayer().getUniqueId());
         if (user != null) {
            if (!user.has(BungeeStorage.class)) {
               user.put(new BungeeStorage(event.getPlayer()));
            }

            int serverProtocolVersion = Via.proxyPlatform().protocolDetectorService().serverProtocolVersion(event.getTarget().getName());
            int clientProtocolVersion = user.getProtocolInfo().getProtocolVersion();
            List protocols = Via.getManager().getProtocolManager().getProtocolPath(clientProtocolVersion, serverProtocolVersion);

            try {
               Object handshake = getHandshake.invoke(event.getPlayer().getPendingConnection());
               setProtocol.invoke(handshake, protocols == null ? clientProtocolVersion : serverProtocolVersion);
            } catch (IllegalAccessException | InvocationTargetException var7) {
               var7.printStackTrace();
            }

         }
      }
   }

   @EventHandler(
      priority = -120
   )
   public void onServerConnected(ServerConnectedEvent event) {
      try {
         this.checkServerChange(event, Via.getManager().getConnectionManager().getConnectedClient(event.getPlayer().getUniqueId()));
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   @EventHandler(
      priority = -120
   )
   public void onServerSwitch(ServerSwitchEvent event) {
      UserConnection userConnection = Via.getManager().getConnectionManager().getConnectedClient(event.getPlayer().getUniqueId());
      if (userConnection != null) {
         int playerId;
         try {
            playerId = ((EntityIdProvider)Via.getManager().getProviders().get(EntityIdProvider.class)).getEntityId(userConnection);
         } catch (Exception var6) {
            return;
         }

         Iterator var4 = userConnection.getEntityTrackers().iterator();

         while(var4.hasNext()) {
            EntityTracker tracker = (EntityTracker)var4.next();
            tracker.setClientEntityId(playerId);
         }

         var4 = userConnection.getStoredObjects().values().iterator();

         while(var4.hasNext()) {
            StorableObject object = (StorableObject)var4.next();
            if (object instanceof ClientEntityIdChangeListener) {
               ((ClientEntityIdChangeListener)object).setClientEntityId(playerId);
            }
         }

      }
   }

   public void checkServerChange(ServerConnectedEvent event, UserConnection user) throws Exception {
      if (user != null) {
         BungeeStorage storage = (BungeeStorage)user.get(BungeeStorage.class);
         if (storage != null) {
            Server server = event.getServer();
            if (server != null && !server.getInfo().getName().equals(storage.getCurrentServer())) {
               EntityTracker1_9 oldEntityTracker = (EntityTracker1_9)user.getEntityTracker(Protocol1_9To1_8.class);
               if (oldEntityTracker != null && oldEntityTracker.isAutoTeam() && oldEntityTracker.isTeamExists()) {
                  oldEntityTracker.sendTeamPacket(false, true);
               }

               String serverName = server.getInfo().getName();
               storage.setCurrentServer(serverName);
               int serverProtocolVersion = Via.proxyPlatform().protocolDetectorService().serverProtocolVersion(serverName);
               if (serverProtocolVersion <= ProtocolVersion.v1_8.getVersion() && storage.getBossbar() != null) {
                  if (user.getProtocolInfo().getPipeline().contains(Protocol1_9To1_8.class)) {
                     Iterator var8 = storage.getBossbar().iterator();

                     while(var8.hasNext()) {
                        UUID uuid = (UUID)var8.next();
                        PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_9.BOSSBAR, (ByteBuf)null, user);
                        wrapper.write(Type.UUID, uuid);
                        wrapper.write(Type.VAR_INT, 1);
                        wrapper.send(Protocol1_9To1_8.class);
                     }
                  }

                  storage.getBossbar().clear();
               }

               ProtocolInfo info = user.getProtocolInfo();
               int previousServerProtocol = info.getServerProtocolVersion();
               List<ProtocolPathEntry> protocolPath = Via.getManager().getProtocolManager().getProtocolPath(info.getProtocolVersion(), serverProtocolVersion);
               ProtocolPipeline pipeline = user.getProtocolInfo().getPipeline();
               user.clearStoredObjects(true);
               pipeline.cleanPipes();
               if (protocolPath == null) {
                  serverProtocolVersion = info.getProtocolVersion();
               } else {
                  List<Protocol> protocols = new ArrayList(protocolPath.size());
                  Iterator var13 = protocolPath.iterator();

                  while(var13.hasNext()) {
                     ProtocolPathEntry entry = (ProtocolPathEntry)var13.next();
                     protocols.add(entry.protocol());
                  }

                  pipeline.add((Collection)protocols);
               }

               info.setServerProtocolVersion(serverProtocolVersion);
               pipeline.add(Via.getManager().getProtocolManager().getBaseProtocol(serverProtocolVersion));
               int id1_13 = ProtocolVersion.v1_13.getVersion();
               boolean toNewId = previousServerProtocol < id1_13 && serverProtocolVersion >= id1_13;
               boolean toOldId = previousServerProtocol >= id1_13 && serverProtocolVersion < id1_13;
               String channel;
               if (previousServerProtocol != -1 && (toNewId || toOldId)) {
                  Collection<String> registeredChannels = (Collection)getRegisteredChannels.invoke(event.getPlayer().getPendingConnection());
                  if (!registeredChannels.isEmpty()) {
                     Collection<String> newChannels = new HashSet();
                     Iterator iterator = registeredChannels.iterator();

                     while(iterator.hasNext()) {
                        String channel = (String)iterator.next();
                        String oldChannel = channel;
                        if (toNewId) {
                           channel = InventoryPackets.getNewPluginChannelId(channel);
                        } else {
                           channel = InventoryPackets.getOldPluginChannelId(channel);
                        }

                        if (channel == null) {
                           iterator.remove();
                        } else if (!oldChannel.equals(channel)) {
                           iterator.remove();
                           newChannels.add(channel);
                        }
                     }

                     registeredChannels.addAll(newChannels);
                  }

                  PluginMessage brandMessage = (PluginMessage)getBrandMessage.invoke(event.getPlayer().getPendingConnection());
                  if (brandMessage != null) {
                     channel = brandMessage.getTag();
                     if (toNewId) {
                        channel = InventoryPackets.getNewPluginChannelId(channel);
                     } else {
                        channel = InventoryPackets.getOldPluginChannelId(channel);
                     }

                     if (channel != null) {
                        brandMessage.setTag(channel);
                     }
                  }
               }

               user.put(storage);
               user.setActive(protocolPath != null);
               Iterator var26 = pipeline.pipes().iterator();

               while(var26.hasNext()) {
                  Protocol protocol = (Protocol)var26.next();
                  protocol.init(user);
               }

               ProxiedPlayer player = storage.getPlayer();
               EntityTracker1_9 newTracker = (EntityTracker1_9)user.getEntityTracker(Protocol1_9To1_8.class);
               if (newTracker != null && Via.getConfig().isAutoTeam()) {
                  channel = null;
                  Iterator var32 = player.getScoreboard().getTeams().iterator();

                  while(var32.hasNext()) {
                     Team team = (Team)var32.next();
                     if (team.getPlayers().contains(info.getUsername())) {
                        channel = team.getName();
                     }
                  }

                  newTracker.setAutoTeam(true);
                  if (channel == null) {
                     newTracker.sendTeamPacket(true, true);
                     newTracker.setCurrentTeam("viaversion");
                  } else {
                     newTracker.setAutoTeam(Via.getConfig().isAutoTeam());
                     newTracker.setCurrentTeam(channel);
                  }
               }

               Object wrapper = channelWrapper.get(player);
               setVersion.invoke(wrapper, serverProtocolVersion);
               Object entityMap = getEntityMap.invoke((Object)null, serverProtocolVersion);
               entityRewrite.set(player, entityMap);
            }
         }
      }
   }

   static {
      try {
         getHandshake = Class.forName("net.md_5.bungee.connection.InitialHandler").getDeclaredMethod("getHandshake");
         getRegisteredChannels = Class.forName("net.md_5.bungee.connection.InitialHandler").getDeclaredMethod("getRegisteredChannels");
         getBrandMessage = Class.forName("net.md_5.bungee.connection.InitialHandler").getDeclaredMethod("getBrandMessage");
         setProtocol = Class.forName("net.md_5.bungee.protocol.packet.Handshake").getDeclaredMethod("setProtocolVersion", Integer.TYPE);
         getEntityMap = Class.forName("net.md_5.bungee.entitymap.EntityMap").getDeclaredMethod("getEntityMap", Integer.TYPE);
         setVersion = Class.forName("net.md_5.bungee.netty.ChannelWrapper").getDeclaredMethod("setVersion", Integer.TYPE);
         channelWrapper = Class.forName("net.md_5.bungee.UserConnection").getDeclaredField("ch");
         channelWrapper.setAccessible(true);
         entityRewrite = Class.forName("net.md_5.bungee.UserConnection").getDeclaredField("entityRewrite");
         entityRewrite.setAccessible(true);
      } catch (ReflectiveOperationException var1) {
         Via.getPlatform().getLogger().severe("Error initializing BungeeServerHandler, try updating BungeeCord or ViaVersion!");
         throw new RuntimeException(var1);
      }
   }
}
