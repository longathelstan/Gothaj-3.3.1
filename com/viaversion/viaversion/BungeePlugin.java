package com.viaversion.viaversion;

import com.google.common.collect.ImmutableList;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.platform.PlatformTask;
import com.viaversion.viaversion.api.platform.UnsupportedSoftware;
import com.viaversion.viaversion.api.platform.ViaServerProxyPlatform;
import com.viaversion.viaversion.bungee.commands.BungeeCommand;
import com.viaversion.viaversion.bungee.commands.BungeeCommandHandler;
import com.viaversion.viaversion.bungee.commands.BungeeCommandSender;
import com.viaversion.viaversion.bungee.platform.BungeeViaAPI;
import com.viaversion.viaversion.bungee.platform.BungeeViaConfig;
import com.viaversion.viaversion.bungee.platform.BungeeViaInjector;
import com.viaversion.viaversion.bungee.platform.BungeeViaLoader;
import com.viaversion.viaversion.bungee.platform.BungeeViaTask;
import com.viaversion.viaversion.bungee.service.ProtocolDetectorService;
import com.viaversion.viaversion.dump.PluginInfo;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.unsupported.UnsupportedServerSoftware;
import com.viaversion.viaversion.util.GsonUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.protocol.ProtocolConstants;

public class BungeePlugin extends Plugin implements ViaServerProxyPlatform<ProxiedPlayer>, Listener {
   private final ProtocolDetectorService protocolDetectorService = new ProtocolDetectorService();
   private BungeeViaAPI api;
   private BungeeViaConfig config;

   public void onLoad() {
      try {
         ProtocolConstants.class.getField("MINECRAFT_1_19_4");
      } catch (NoSuchFieldException var2) {
         this.getLogger().warning("      / \\");
         this.getLogger().warning("     /   \\");
         this.getLogger().warning("    /  |  \\");
         this.getLogger().warning("   /   |   \\         BUNGEECORD IS OUTDATED");
         this.getLogger().warning("  /         \\   VIAVERSION MAY NOT WORK AS INTENDED");
         this.getLogger().warning(" /     o     \\");
         this.getLogger().warning("/_____________\\");
      }

      this.api = new BungeeViaAPI();
      this.config = new BungeeViaConfig(this.getDataFolder());
      BungeeCommandHandler commandHandler = new BungeeCommandHandler();
      ProxyServer.getInstance().getPluginManager().registerCommand(this, new BungeeCommand(commandHandler));
      Via.init(ViaManagerImpl.builder().platform(this).injector(new BungeeViaInjector()).loader(new BungeeViaLoader(this)).commandHandler(commandHandler).build());
   }

   public void onEnable() {
      ViaManagerImpl manager = (ViaManagerImpl)Via.getManager();
      manager.init();
      manager.onServerLoaded();
   }

   public String getPlatformName() {
      return this.getProxy().getName();
   }

   public String getPlatformVersion() {
      return this.getProxy().getVersion();
   }

   public boolean isProxy() {
      return true;
   }

   public String getPluginVersion() {
      return this.getDescription().getVersion();
   }

   public PlatformTask runAsync(Runnable runnable) {
      return new BungeeViaTask(this.getProxy().getScheduler().runAsync(this, runnable));
   }

   public PlatformTask runRepeatingAsync(Runnable runnable, long ticks) {
      return new BungeeViaTask(this.getProxy().getScheduler().schedule(this, runnable, 0L, ticks * 50L, TimeUnit.MILLISECONDS));
   }

   public PlatformTask runSync(Runnable runnable) {
      return this.runAsync(runnable);
   }

   public PlatformTask runSync(Runnable runnable, long delay) {
      return new BungeeViaTask(this.getProxy().getScheduler().schedule(this, runnable, delay * 50L, TimeUnit.MILLISECONDS));
   }

   public PlatformTask runRepeatingSync(Runnable runnable, long period) {
      return this.runRepeatingAsync(runnable, period);
   }

   public ViaCommandSender[] getOnlinePlayers() {
      Collection<ProxiedPlayer> players = this.getProxy().getPlayers();
      ViaCommandSender[] array = new ViaCommandSender[players.size()];
      int i = 0;

      ProxiedPlayer player;
      for(Iterator var4 = players.iterator(); var4.hasNext(); array[i++] = new BungeeCommandSender(player)) {
         player = (ProxiedPlayer)var4.next();
      }

      return array;
   }

   public void sendMessage(UUID uuid, String message) {
      this.getProxy().getPlayer(uuid).sendMessage(message);
   }

   public boolean kickPlayer(UUID uuid, String message) {
      ProxiedPlayer player = this.getProxy().getPlayer(uuid);
      if (player != null) {
         player.disconnect(message);
         return true;
      } else {
         return false;
      }
   }

   public boolean isPluginEnabled() {
      return true;
   }

   public ViaAPI<ProxiedPlayer> getApi() {
      return this.api;
   }

   public BungeeViaConfig getConf() {
      return this.config;
   }

   public void onReload() {
   }

   public JsonObject getDump() {
      JsonObject platformSpecific = new JsonObject();
      List<PluginInfo> plugins = new ArrayList();
      Iterator var3 = ProxyServer.getInstance().getPluginManager().getPlugins().iterator();

      while(var3.hasNext()) {
         Plugin p = (Plugin)var3.next();
         plugins.add(new PluginInfo(true, p.getDescription().getName(), p.getDescription().getVersion(), p.getDescription().getMain(), Collections.singletonList(p.getDescription().getAuthor())));
      }

      platformSpecific.add("plugins", GsonUtil.getGson().toJsonTree(plugins));
      platformSpecific.add("servers", GsonUtil.getGson().toJsonTree(this.protocolDetectorService.detectedProtocolVersions()));
      return platformSpecific;
   }

   public Collection<UnsupportedSoftware> getUnsupportedSoftwareClasses() {
      Collection<UnsupportedSoftware> list = new ArrayList(ViaServerProxyPlatform.super.getUnsupportedSoftwareClasses());
      list.add((new UnsupportedServerSoftware.Builder()).name("FlameCord").addClassName("dev._2lstudios.flamecord.FlameCord").reason("You are using proxy software that intentionally breaks ViaVersion. Please use another proxy software or move ViaVersion to each backend server instead of the proxy.").build());
      return ImmutableList.copyOf(list);
   }

   public boolean hasPlugin(String name) {
      return this.getProxy().getPluginManager().getPlugin(name) != null;
   }

   public ProtocolDetectorService protocolDetectorService() {
      return this.protocolDetectorService;
   }
}
