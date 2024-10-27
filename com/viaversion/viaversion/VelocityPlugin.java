package com.viaversion.viaversion;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.platform.PlatformTask;
import com.viaversion.viaversion.api.platform.ViaServerProxyPlatform;
import com.viaversion.viaversion.dump.PluginInfo;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.util.GsonUtil;
import com.viaversion.viaversion.velocity.command.VelocityCommandHandler;
import com.viaversion.viaversion.velocity.command.VelocityCommandSender;
import com.viaversion.viaversion.velocity.platform.VelocityViaAPI;
import com.viaversion.viaversion.velocity.platform.VelocityViaConfig;
import com.viaversion.viaversion.velocity.platform.VelocityViaInjector;
import com.viaversion.viaversion.velocity.platform.VelocityViaLoader;
import com.viaversion.viaversion.velocity.platform.VelocityViaTask;
import com.viaversion.viaversion.velocity.service.ProtocolDetectorService;
import com.viaversion.viaversion.velocity.util.LoggerWrapper;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;

@Plugin(
   id = "viaversion",
   name = "ViaVersion",
   version = "4.9.3-SNAPSHOT",
   authors = {"_MylesC", "creeper123123321", "Gerrygames", "kennytv", "Matsv"},
   description = "Allow newer Minecraft versions to connect to an older server version.",
   url = "https://viaversion.com"
)
public class VelocityPlugin implements ViaServerProxyPlatform<Player> {
   public static final LegacyComponentSerializer COMPONENT_SERIALIZER = LegacyComponentSerializer.builder().character('§').extractUrls().build();
   public static ProxyServer PROXY;
   @Inject
   private ProxyServer proxy;
   @Inject
   private Logger loggerslf4j;
   @Inject
   @DataDirectory
   private Path configDir;
   private final ProtocolDetectorService protocolDetectorService = new ProtocolDetectorService();
   private VelocityViaAPI api;
   private java.util.logging.Logger logger;
   private VelocityViaConfig conf;

   @Subscribe
   public void onProxyInit(ProxyInitializeEvent e) {
      if (!this.hasConnectionEvent()) {
         Logger logger = this.loggerslf4j;
         logger.error("      / \\");
         logger.error("     /   \\");
         logger.error("    /  |  \\");
         logger.error("   /   |   \\        VELOCITY 3.0.0 IS REQUIRED");
         logger.error("  /         \\   VIAVERSION WILL NOT WORK AS INTENDED");
         logger.error(" /     o     \\");
         logger.error("/_____________\\");
      }

      PROXY = this.proxy;
      VelocityCommandHandler commandHandler = new VelocityCommandHandler();
      PROXY.getCommandManager().register("viaver", commandHandler, new String[]{"vvvelocity", "viaversion"});
      this.api = new VelocityViaAPI();
      this.conf = new VelocityViaConfig(this.configDir.toFile());
      this.logger = new LoggerWrapper(this.loggerslf4j);
      Via.init(ViaManagerImpl.builder().platform(this).commandHandler(commandHandler).loader(new VelocityViaLoader()).injector(new VelocityViaInjector()).build());
   }

   @Subscribe(
      order = PostOrder.LAST
   )
   public void onProxyLateInit(ProxyInitializeEvent e) {
      ViaManagerImpl manager = (ViaManagerImpl)Via.getManager();
      manager.init();
      manager.onServerLoaded();
   }

   public String getPlatformName() {
      String proxyImpl = ProxyServer.class.getPackage().getImplementationTitle();
      return proxyImpl != null ? proxyImpl : "Velocity";
   }

   public String getPlatformVersion() {
      String version = ProxyServer.class.getPackage().getImplementationVersion();
      return version != null ? version : "Unknown";
   }

   public boolean isProxy() {
      return true;
   }

   public String getPluginVersion() {
      return "4.9.3-SNAPSHOT";
   }

   public PlatformTask runAsync(Runnable runnable) {
      return this.runSync(runnable);
   }

   public PlatformTask runRepeatingAsync(Runnable runnable, long ticks) {
      return new VelocityViaTask(PROXY.getScheduler().buildTask(this, runnable).repeat(ticks * 50L, TimeUnit.MILLISECONDS).schedule());
   }

   public PlatformTask runSync(Runnable runnable) {
      return this.runSync(runnable, 0L);
   }

   public PlatformTask runSync(Runnable runnable, long delay) {
      return new VelocityViaTask(PROXY.getScheduler().buildTask(this, runnable).delay(delay * 50L, TimeUnit.MILLISECONDS).schedule());
   }

   public PlatformTask runRepeatingSync(Runnable runnable, long period) {
      return this.runRepeatingAsync(runnable, period);
   }

   public ViaCommandSender[] getOnlinePlayers() {
      return (ViaCommandSender[])PROXY.getAllPlayers().stream().map(VelocityCommandSender::new).toArray((x$0) -> {
         return new ViaCommandSender[x$0];
      });
   }

   public void sendMessage(UUID uuid, String message) {
      PROXY.getPlayer(uuid).ifPresent((player) -> {
         player.sendMessage(COMPONENT_SERIALIZER.deserialize(message));
      });
   }

   public boolean kickPlayer(UUID uuid, String message) {
      return (Boolean)PROXY.getPlayer(uuid).map((it) -> {
         it.disconnect(LegacyComponentSerializer.legacySection().deserialize(message));
         return true;
      }).orElse(false);
   }

   public boolean isPluginEnabled() {
      return true;
   }

   public File getDataFolder() {
      return this.configDir.toFile();
   }

   public VelocityViaAPI getApi() {
      return this.api;
   }

   public VelocityViaConfig getConf() {
      return this.conf;
   }

   public void onReload() {
   }

   public JsonObject getDump() {
      JsonObject extra = new JsonObject();
      List<PluginInfo> plugins = new ArrayList();
      Iterator var3 = PROXY.getPluginManager().getPlugins().iterator();

      while(var3.hasNext()) {
         PluginContainer p = (PluginContainer)var3.next();
         plugins.add(new PluginInfo(true, (String)p.getDescription().getName().orElse(p.getDescription().getId()), (String)p.getDescription().getVersion().orElse("Unknown Version"), (String)p.getInstance().map((instance) -> {
            return instance.getClass().getCanonicalName();
         }).orElse("Unknown"), p.getDescription().getAuthors()));
      }

      extra.add("plugins", GsonUtil.getGson().toJsonTree(plugins));
      extra.add("servers", GsonUtil.getGson().toJsonTree(this.protocolDetectorService.detectedProtocolVersions()));
      return extra;
   }

   public boolean hasPlugin(String name) {
      return this.proxy.getPluginManager().getPlugin(name).isPresent();
   }

   public java.util.logging.Logger getLogger() {
      return this.logger;
   }

   public ProtocolDetectorService protocolDetectorService() {
      return this.protocolDetectorService;
   }

   private boolean hasConnectionEvent() {
      try {
         Class.forName("com.velocitypowered.proxy.protocol.VelocityConnectionEvent");
         return true;
      } catch (ClassNotFoundException var2) {
         return false;
      }
   }
}
