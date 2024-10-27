package com.viaversion.viaversion;

import com.google.inject.Inject;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.platform.PlatformTask;
import com.viaversion.viaversion.api.platform.ViaPlatform;
import com.viaversion.viaversion.dump.PluginInfo;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.sponge.commands.SpongeCommandHandler;
import com.viaversion.viaversion.sponge.commands.SpongePlayer;
import com.viaversion.viaversion.sponge.platform.SpongeViaAPI;
import com.viaversion.viaversion.sponge.platform.SpongeViaConfig;
import com.viaversion.viaversion.sponge.platform.SpongeViaInjector;
import com.viaversion.viaversion.sponge.platform.SpongeViaLoader;
import com.viaversion.viaversion.sponge.platform.SpongeViaTask;
import com.viaversion.viaversion.sponge.util.LoggerWrapper;
import com.viaversion.viaversion.util.GsonUtil;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.Platform.Component;
import org.spongepowered.api.command.Command.Raw;
import org.spongepowered.api.command.registrar.CommandRegistrar;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;
import org.spongepowered.plugin.metadata.PluginMetadata;
import org.spongepowered.plugin.metadata.model.PluginContributor;

@Plugin("viaversion")
public class SpongePlugin implements ViaPlatform<Player> {
   public static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder().extractUrls().build();
   private final SpongeViaAPI api = new SpongeViaAPI();
   private final PluginContainer container;
   private final Game game;
   private final Logger logger;
   private SpongeViaConfig conf;
   @Inject
   @ConfigDir(
      sharedRoot = false
   )
   private Path configDir;

   @Inject
   SpongePlugin(PluginContainer container, Game game, org.apache.logging.log4j.Logger logger) {
      this.container = container;
      this.game = game;
      this.logger = new LoggerWrapper(logger);
   }

   @Listener
   public void constructPlugin(ConstructPluginEvent event) {
      this.conf = new SpongeViaConfig(this.configDir.toFile());
      Via.init(ViaManagerImpl.builder().platform(this).commandHandler(new SpongeCommandHandler()).injector(new SpongeViaInjector()).loader(new SpongeViaLoader(this)).build());
   }

   @Listener
   public void onServerStart(StartingEngineEvent<Server> event) {
      ((CommandRegistrar)Sponge.server().commandManager().registrar(Raw.class).get()).register(this.container, (Raw)Via.getManager().getCommandHandler(), "viaversion", new String[]{"viaver", "vvsponge"});
      ViaManagerImpl manager = (ViaManagerImpl)Via.getManager();
      manager.init();
   }

   @Listener
   public void onServerStarted(StartedEngineEvent<Server> event) {
      ViaManagerImpl manager = (ViaManagerImpl)Via.getManager();
      manager.onServerLoaded();
   }

   @Listener
   public void onServerStop(StoppingEngineEvent<Server> event) {
      ((ViaManagerImpl)Via.getManager()).destroy();
   }

   public String getPlatformName() {
      return (String)this.game.platform().container(Component.IMPLEMENTATION).metadata().name().orElse("unknown");
   }

   public String getPlatformVersion() {
      return this.game.platform().container(Component.IMPLEMENTATION).metadata().version().toString();
   }

   public String getPluginVersion() {
      return this.container.metadata().version().toString();
   }

   public PlatformTask runAsync(Runnable runnable) {
      Task task = Task.builder().plugin(this.container).execute(runnable).build();
      return new SpongeViaTask(this.game.asyncScheduler().submit(task));
   }

   public PlatformTask runRepeatingAsync(Runnable runnable, long ticks) {
      Task task = Task.builder().plugin(this.container).execute(runnable).interval(Ticks.of(ticks)).build();
      return new SpongeViaTask(this.game.asyncScheduler().submit(task));
   }

   public PlatformTask runSync(Runnable runnable) {
      Task task = Task.builder().plugin(this.container).execute(runnable).build();
      return new SpongeViaTask(this.game.server().scheduler().submit(task));
   }

   public PlatformTask runSync(Runnable runnable, long delay) {
      Task task = Task.builder().plugin(this.container).execute(runnable).delay(Ticks.of(delay)).build();
      return new SpongeViaTask(this.game.server().scheduler().submit(task));
   }

   public PlatformTask runRepeatingSync(Runnable runnable, long period) {
      Task task = Task.builder().plugin(this.container).execute(runnable).interval(Ticks.of(period)).build();
      return new SpongeViaTask(this.game.server().scheduler().submit(task));
   }

   public ViaCommandSender[] getOnlinePlayers() {
      Collection<ServerPlayer> players = this.game.server().onlinePlayers();
      ViaCommandSender[] array = new ViaCommandSender[players.size()];
      int i = 0;

      ServerPlayer player;
      for(Iterator var4 = players.iterator(); var4.hasNext(); array[i++] = new SpongePlayer(player)) {
         player = (ServerPlayer)var4.next();
      }

      return array;
   }

   public void sendMessage(UUID uuid, String message) {
      this.game.server().player(uuid).ifPresent((player) -> {
         player.sendMessage(LEGACY_SERIALIZER.deserialize(message));
      });
   }

   public boolean kickPlayer(UUID uuid, String message) {
      return (Boolean)this.game.server().player(uuid).map((player) -> {
         player.kick(LegacyComponentSerializer.legacySection().deserialize(message));
         return true;
      }).orElse(false);
   }

   public boolean isPluginEnabled() {
      return true;
   }

   public File getDataFolder() {
      return this.configDir.toFile();
   }

   public void onReload() {
      this.logger.severe("ViaVersion is already loaded, this should work fine. If you get any console errors, try rebooting.");
   }

   public JsonObject getDump() {
      JsonObject platformSpecific = new JsonObject();
      List<PluginInfo> plugins = new ArrayList();
      Iterator var3 = this.game.pluginManager().plugins().iterator();

      while(var3.hasNext()) {
         PluginContainer plugin = (PluginContainer)var3.next();
         PluginMetadata metadata = plugin.metadata();
         plugins.add(new PluginInfo(true, (String)metadata.name().orElse("Unknown"), metadata.version().toString(), plugin.instance().getClass().getCanonicalName(), (List)metadata.contributors().stream().map(PluginContributor::name).collect(Collectors.toList())));
      }

      platformSpecific.add("plugins", GsonUtil.getGson().toJsonTree(plugins));
      return platformSpecific;
   }

   public boolean hasPlugin(String name) {
      return this.game.pluginManager().plugin(name).isPresent();
   }

   public SpongeViaAPI getApi() {
      return this.api;
   }

   public SpongeViaConfig getConf() {
      return this.conf;
   }

   public Logger getLogger() {
      return this.logger;
   }

   public PluginContainer container() {
      return this.container;
   }
}