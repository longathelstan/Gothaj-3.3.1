package com.viaversion.viaversion.configuration;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.configuration.ViaVersionConfig;
import com.viaversion.viaversion.api.minecraft.WorldIdentifiers;
import com.viaversion.viaversion.api.protocol.version.BlockedProtocolVersions;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.fastutil.ints.IntOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.protocol.BlockedProtocolVersionsImpl;
import com.viaversion.viaversion.util.Config;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractViaConfig extends Config implements ViaVersionConfig {
   private boolean checkForUpdates;
   private boolean preventCollision;
   private boolean useNewEffectIndicator;
   private boolean useNewDeathmessages;
   private boolean suppressMetadataErrors;
   private boolean shieldBlocking;
   private boolean noDelayShieldBlocking;
   private boolean showShieldWhenSwordInHand;
   private boolean hologramPatch;
   private boolean pistonAnimationPatch;
   private boolean bossbarPatch;
   private boolean bossbarAntiFlicker;
   private double hologramOffset;
   private int maxPPS;
   private String maxPPSKickMessage;
   private int trackingPeriod;
   private int warningPPS;
   private int maxPPSWarnings;
   private String maxPPSWarningsKickMessage;
   private boolean sendSupportedVersions;
   private boolean simulatePlayerTick;
   private boolean itemCache;
   private boolean nmsPlayerTicking;
   private boolean replacePistons;
   private int pistonReplacementId;
   private boolean chunkBorderFix;
   private boolean autoTeam;
   private boolean forceJsonTransform;
   private boolean nbtArrayFix;
   private BlockedProtocolVersions blockedProtocolVersions;
   private String blockedDisconnectMessage;
   private String reloadDisconnectMessage;
   private boolean suppressConversionWarnings;
   private boolean disable1_13TabComplete;
   private boolean minimizeCooldown;
   private boolean teamColourFix;
   private boolean serversideBlockConnections;
   private boolean reduceBlockStorageMemory;
   private boolean flowerStemWhenBlockAbove;
   private boolean vineClimbFix;
   private boolean snowCollisionFix;
   private boolean infestedBlocksFix;
   private int tabCompleteDelay;
   private boolean truncate1_14Books;
   private boolean leftHandedHandling;
   private boolean fullBlockLightFix;
   private boolean healthNaNFix;
   private boolean instantRespawn;
   private boolean ignoreLongChannelNames;
   private boolean forcedUse1_17ResourcePack;
   private JsonElement resourcePack1_17PromptMessage;
   private WorldIdentifiers map1_16WorldNames;
   private boolean cache1_17Light;

   protected AbstractViaConfig(File configFile) {
      super(configFile);
   }

   public void reload() {
      super.reload();
      this.loadFields();
   }

   protected void loadFields() {
      this.checkForUpdates = this.getBoolean("checkforupdates", true);
      this.preventCollision = this.getBoolean("prevent-collision", true);
      this.useNewEffectIndicator = this.getBoolean("use-new-effect-indicator", true);
      this.useNewDeathmessages = this.getBoolean("use-new-deathmessages", true);
      this.suppressMetadataErrors = this.getBoolean("suppress-metadata-errors", false);
      this.shieldBlocking = this.getBoolean("shield-blocking", true);
      this.noDelayShieldBlocking = this.getBoolean("no-delay-shield-blocking", false);
      this.showShieldWhenSwordInHand = this.getBoolean("show-shield-when-sword-in-hand", false);
      this.hologramPatch = this.getBoolean("hologram-patch", false);
      this.pistonAnimationPatch = this.getBoolean("piston-animation-patch", false);
      this.bossbarPatch = this.getBoolean("bossbar-patch", true);
      this.bossbarAntiFlicker = this.getBoolean("bossbar-anti-flicker", false);
      this.hologramOffset = this.getDouble("hologram-y", -0.96D);
      this.maxPPS = this.getInt("max-pps", 800);
      this.maxPPSKickMessage = this.getString("max-pps-kick-msg", "Sending packets too fast? lag?");
      this.trackingPeriod = this.getInt("tracking-period", 6);
      this.warningPPS = this.getInt("tracking-warning-pps", 120);
      this.maxPPSWarnings = this.getInt("tracking-max-warnings", 3);
      this.maxPPSWarningsKickMessage = this.getString("tracking-max-kick-msg", "You are sending too many packets, :(");
      this.sendSupportedVersions = this.getBoolean("send-supported-versions", false);
      this.simulatePlayerTick = this.getBoolean("simulate-pt", true);
      this.itemCache = this.getBoolean("item-cache", true);
      this.nmsPlayerTicking = this.getBoolean("nms-player-ticking", true);
      this.replacePistons = this.getBoolean("replace-pistons", false);
      this.pistonReplacementId = this.getInt("replacement-piston-id", 0);
      this.chunkBorderFix = this.getBoolean("chunk-border-fix", false);
      this.autoTeam = this.getBoolean("auto-team", true);
      this.forceJsonTransform = this.getBoolean("force-json-transform", false);
      this.nbtArrayFix = this.getBoolean("chat-nbt-fix", true);
      this.blockedProtocolVersions = this.loadBlockedProtocolVersions();
      this.blockedDisconnectMessage = this.getString("block-disconnect-msg", "You are using an unsupported Minecraft version!");
      this.reloadDisconnectMessage = this.getString("reload-disconnect-msg", "Server reload, please rejoin!");
      this.minimizeCooldown = this.getBoolean("minimize-cooldown", true);
      this.teamColourFix = this.getBoolean("team-colour-fix", true);
      this.suppressConversionWarnings = this.getBoolean("suppress-conversion-warnings", false);
      this.disable1_13TabComplete = this.getBoolean("disable-1_13-auto-complete", false);
      this.serversideBlockConnections = this.getBoolean("serverside-blockconnections", true);
      this.reduceBlockStorageMemory = this.getBoolean("reduce-blockstorage-memory", false);
      this.flowerStemWhenBlockAbove = this.getBoolean("flowerstem-when-block-above", false);
      this.vineClimbFix = this.getBoolean("vine-climb-fix", false);
      this.snowCollisionFix = this.getBoolean("fix-low-snow-collision", false);
      this.infestedBlocksFix = this.getBoolean("fix-infested-block-breaking", true);
      this.tabCompleteDelay = this.getInt("1_13-tab-complete-delay", 0);
      this.truncate1_14Books = this.getBoolean("truncate-1_14-books", false);
      this.leftHandedHandling = this.getBoolean("left-handed-handling", true);
      this.fullBlockLightFix = this.getBoolean("fix-non-full-blocklight", false);
      this.healthNaNFix = this.getBoolean("fix-1_14-health-nan", true);
      this.instantRespawn = this.getBoolean("use-1_15-instant-respawn", false);
      this.ignoreLongChannelNames = this.getBoolean("ignore-long-1_16-channel-names", true);
      this.forcedUse1_17ResourcePack = this.getBoolean("forced-use-1_17-resource-pack", false);
      this.resourcePack1_17PromptMessage = this.getSerializedComponent("resource-pack-1_17-prompt");
      Map<String, String> worlds = (Map)this.get("map-1_16-world-names", Map.class, new HashMap());
      this.map1_16WorldNames = new WorldIdentifiers((String)worlds.getOrDefault("overworld", "minecraft:overworld"), (String)worlds.getOrDefault("nether", "minecraft:the_nether"), (String)worlds.getOrDefault("end", "minecraft:the_end"));
      this.cache1_17Light = this.getBoolean("cache-1_17-light", true);
   }

   private BlockedProtocolVersions loadBlockedProtocolVersions() {
      List<Integer> blockProtocols = this.getListSafe("block-protocols", Integer.class, "Invalid blocked version protocol found in config: '%s'");
      List<String> blockVersions = this.getListSafe("block-versions", String.class, "Invalid blocked version found in config: '%s'");
      IntSet blockedProtocols = new IntOpenHashSet(blockProtocols);
      int lowerBound = -1;
      int upperBound = -1;
      Iterator var6 = blockVersions.iterator();

      while(true) {
         while(true) {
            String s;
            do {
               if (!var6.hasNext()) {
                  if (lowerBound != -1 || upperBound != -1) {
                     blockedProtocols.removeIf((version) -> {
                        if ((lowerBound == -1 || version >= lowerBound) && (upperBound == -1 || version <= upperBound)) {
                           return false;
                        } else {
                           ProtocolVersion protocolVersion = ProtocolVersion.getProtocol(version);
                           Via.getPlatform().getLogger().warning("Blocked protocol version " + protocolVersion.getName() + "/" + protocolVersion.getVersion() + " already covered by upper or lower bound");
                           return true;
                        }
                     });
                  }

                  return new BlockedProtocolVersionsImpl(blockedProtocols, lowerBound, upperBound);
               }

               s = (String)var6.next();
            } while(s.isEmpty());

            char c = s.charAt(0);
            ProtocolVersion protocolVersion;
            if (c != '<' && c != '>') {
               protocolVersion = this.protocolVersion(s);
               if (protocolVersion != null && !blockedProtocols.add(protocolVersion.getVersion())) {
                  Via.getPlatform().getLogger().warning("Duplicated blocked protocol version " + protocolVersion.getName() + "/" + protocolVersion.getVersion());
               }
            } else {
               protocolVersion = this.protocolVersion(s.substring(1));
               if (protocolVersion != null) {
                  if (c == '<') {
                     if (lowerBound != -1) {
                        Via.getPlatform().getLogger().warning("Already set lower bound " + lowerBound + " overridden by " + protocolVersion.getName());
                     }

                     lowerBound = protocolVersion.getVersion();
                  } else {
                     if (upperBound != -1) {
                        Via.getPlatform().getLogger().warning("Already set upper bound " + upperBound + " overridden by " + protocolVersion.getName());
                     }

                     upperBound = protocolVersion.getVersion();
                  }
               }
            }
         }
      }
   }

   @Nullable
   private ProtocolVersion protocolVersion(String s) {
      ProtocolVersion protocolVersion = ProtocolVersion.getClosest(s);
      if (protocolVersion == null) {
         Via.getPlatform().getLogger().warning("Unknown protocol version in block-versions: " + s);
         return null;
      } else {
         return protocolVersion;
      }
   }

   public boolean isCheckForUpdates() {
      return this.checkForUpdates;
   }

   public void setCheckForUpdates(boolean checkForUpdates) {
      this.checkForUpdates = checkForUpdates;
      this.set("checkforupdates", checkForUpdates);
   }

   public boolean isPreventCollision() {
      return this.preventCollision;
   }

   public boolean isNewEffectIndicator() {
      return this.useNewEffectIndicator;
   }

   public boolean isShowNewDeathMessages() {
      return this.useNewDeathmessages;
   }

   public boolean isSuppressMetadataErrors() {
      return this.suppressMetadataErrors;
   }

   public boolean isShieldBlocking() {
      return this.shieldBlocking;
   }

   public boolean isNoDelayShieldBlocking() {
      return this.noDelayShieldBlocking;
   }

   public boolean isShowShieldWhenSwordInHand() {
      return this.showShieldWhenSwordInHand;
   }

   public boolean isHologramPatch() {
      return this.hologramPatch;
   }

   public boolean isPistonAnimationPatch() {
      return this.pistonAnimationPatch;
   }

   public boolean isBossbarPatch() {
      return this.bossbarPatch;
   }

   public boolean isBossbarAntiflicker() {
      return this.bossbarAntiFlicker;
   }

   public double getHologramYOffset() {
      return this.hologramOffset;
   }

   public int getMaxPPS() {
      return this.maxPPS;
   }

   public String getMaxPPSKickMessage() {
      return this.maxPPSKickMessage;
   }

   public int getTrackingPeriod() {
      return this.trackingPeriod;
   }

   public int getWarningPPS() {
      return this.warningPPS;
   }

   public int getMaxWarnings() {
      return this.maxPPSWarnings;
   }

   public String getMaxWarningsKickMessage() {
      return this.maxPPSWarningsKickMessage;
   }

   public boolean isSendSupportedVersions() {
      return this.sendSupportedVersions;
   }

   public boolean isSimulatePlayerTick() {
      return this.simulatePlayerTick;
   }

   public boolean isItemCache() {
      return this.itemCache;
   }

   public boolean isNMSPlayerTicking() {
      return this.nmsPlayerTicking;
   }

   public boolean isReplacePistons() {
      return this.replacePistons;
   }

   public int getPistonReplacementId() {
      return this.pistonReplacementId;
   }

   public boolean isChunkBorderFix() {
      return this.chunkBorderFix;
   }

   public boolean isAutoTeam() {
      return this.preventCollision && this.autoTeam;
   }

   public boolean isForceJsonTransform() {
      return this.forceJsonTransform;
   }

   public boolean is1_12NBTArrayFix() {
      return this.nbtArrayFix;
   }

   public boolean shouldRegisterUserConnectionOnJoin() {
      return false;
   }

   public boolean is1_12QuickMoveActionFix() {
      return false;
   }

   public BlockedProtocolVersions blockedProtocolVersions() {
      return this.blockedProtocolVersions;
   }

   public String getBlockedDisconnectMsg() {
      return this.blockedDisconnectMessage;
   }

   public String getReloadDisconnectMsg() {
      return this.reloadDisconnectMessage;
   }

   public boolean isMinimizeCooldown() {
      return this.minimizeCooldown;
   }

   public boolean is1_13TeamColourFix() {
      return this.teamColourFix;
   }

   public boolean isSuppressConversionWarnings() {
      return this.suppressConversionWarnings;
   }

   public boolean isDisable1_13AutoComplete() {
      return this.disable1_13TabComplete;
   }

   public boolean isServersideBlockConnections() {
      return this.serversideBlockConnections;
   }

   public String getBlockConnectionMethod() {
      return "packet";
   }

   public boolean isReduceBlockStorageMemory() {
      return this.reduceBlockStorageMemory;
   }

   public boolean isStemWhenBlockAbove() {
      return this.flowerStemWhenBlockAbove;
   }

   public boolean isVineClimbFix() {
      return this.vineClimbFix;
   }

   public boolean isSnowCollisionFix() {
      return this.snowCollisionFix;
   }

   public boolean isInfestedBlocksFix() {
      return this.infestedBlocksFix;
   }

   public int get1_13TabCompleteDelay() {
      return this.tabCompleteDelay;
   }

   public boolean isTruncate1_14Books() {
      return this.truncate1_14Books;
   }

   public boolean isLeftHandedHandling() {
      return this.leftHandedHandling;
   }

   public boolean is1_9HitboxFix() {
      return false;
   }

   public boolean is1_14HitboxFix() {
      return false;
   }

   public boolean isNonFullBlockLightFix() {
      return this.fullBlockLightFix;
   }

   public boolean is1_14HealthNaNFix() {
      return this.healthNaNFix;
   }

   public boolean is1_15InstantRespawn() {
      return this.instantRespawn;
   }

   public boolean isIgnoreLong1_16ChannelNames() {
      return this.ignoreLongChannelNames;
   }

   public boolean isForcedUse1_17ResourcePack() {
      return this.forcedUse1_17ResourcePack;
   }

   public JsonElement get1_17ResourcePackPrompt() {
      return this.resourcePack1_17PromptMessage;
   }

   public WorldIdentifiers get1_16WorldNamesMap() {
      return this.map1_16WorldNames;
   }

   public boolean cache1_17Light() {
      return this.cache1_17Light;
   }

   public boolean isArmorToggleFix() {
      return false;
   }
}
