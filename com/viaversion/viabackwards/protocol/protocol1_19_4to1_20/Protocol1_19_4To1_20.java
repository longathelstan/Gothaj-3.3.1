package com.viaversion.viabackwards.protocol.protocol1_19_4to1_20;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.rewriters.SoundRewriter;
import com.viaversion.viabackwards.api.rewriters.TranslatableRewriter;
import com.viaversion.viabackwards.protocol.protocol1_19_4to1_20.data.BackwardsMappings;
import com.viaversion.viabackwards.protocol.protocol1_19_4to1_20.packets.BlockItemPackets1_20;
import com.viaversion.viabackwards.protocol.protocol1_19_4to1_20.packets.EntityPackets1_20;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_19_4;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.ClientboundPackets1_19_4;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.ServerboundPackets1_19_4;
import com.viaversion.viaversion.rewriter.ComponentRewriter;
import com.viaversion.viaversion.rewriter.StatisticsRewriter;
import com.viaversion.viaversion.rewriter.TagRewriter;
import java.util.Arrays;

public final class Protocol1_19_4To1_20 extends BackwardsProtocol<ClientboundPackets1_19_4, ClientboundPackets1_19_4, ServerboundPackets1_19_4, ServerboundPackets1_19_4> {
   public static final BackwardsMappings MAPPINGS = new BackwardsMappings();
   private final TranslatableRewriter<ClientboundPackets1_19_4> translatableRewriter;
   private final EntityPackets1_20 entityRewriter;
   private final BlockItemPackets1_20 itemRewriter;

   public Protocol1_19_4To1_20() {
      super(ClientboundPackets1_19_4.class, ClientboundPackets1_19_4.class, ServerboundPackets1_19_4.class, ServerboundPackets1_19_4.class);
      this.translatableRewriter = new TranslatableRewriter(this, ComponentRewriter.ReadType.JSON);
      this.entityRewriter = new EntityPackets1_20(this);
      this.itemRewriter = new BlockItemPackets1_20(this);
   }

   protected void registerPackets() {
      super.registerPackets();
      TagRewriter<ClientboundPackets1_19_4> tagRewriter = new TagRewriter(this);
      tagRewriter.addEmptyTag(RegistryType.BLOCK, "minecraft:replaceable_plants");
      tagRewriter.registerGeneric(ClientboundPackets1_19_4.TAGS);
      SoundRewriter<ClientboundPackets1_19_4> soundRewriter = new SoundRewriter(this);
      soundRewriter.registerStopSound(ClientboundPackets1_19_4.STOP_SOUND);
      soundRewriter.register1_19_3Sound(ClientboundPackets1_19_4.SOUND);
      soundRewriter.registerSound(ClientboundPackets1_19_4.ENTITY_SOUND);
      (new StatisticsRewriter(this)).register(ClientboundPackets1_19_4.STATISTICS);
      this.translatableRewriter.registerComponentPacket(ClientboundPackets1_19_4.ACTIONBAR);
      this.translatableRewriter.registerComponentPacket(ClientboundPackets1_19_4.TITLE_TEXT);
      this.translatableRewriter.registerComponentPacket(ClientboundPackets1_19_4.TITLE_SUBTITLE);
      this.translatableRewriter.registerBossBar(ClientboundPackets1_19_4.BOSSBAR);
      this.translatableRewriter.registerComponentPacket(ClientboundPackets1_19_4.DISCONNECT);
      this.translatableRewriter.registerTabList(ClientboundPackets1_19_4.TAB_LIST);
      this.translatableRewriter.registerComponentPacket(ClientboundPackets1_19_4.SYSTEM_CHAT);
      this.translatableRewriter.registerComponentPacket(ClientboundPackets1_19_4.DISGUISED_CHAT);
      this.translatableRewriter.registerPing();
      this.registerClientbound(ClientboundPackets1_19_4.UPDATE_ENABLED_FEATURES, (wrapper) -> {
         String[] enabledFeatures = (String[])wrapper.read(Type.STRING_ARRAY);
         int length = enabledFeatures.length;
         enabledFeatures = (String[])Arrays.copyOf(enabledFeatures, length + 1);
         enabledFeatures[length] = "minecraft:update_1_20";
         wrapper.write(Type.STRING_ARRAY, enabledFeatures);
      });
      this.registerClientbound(ClientboundPackets1_19_4.COMBAT_END, (wrapper) -> {
         wrapper.passthrough(Type.VAR_INT);
         wrapper.write(Type.INT, -1);
      });
      this.registerClientbound(ClientboundPackets1_19_4.COMBAT_KILL, (wrapper) -> {
         wrapper.passthrough(Type.VAR_INT);
         wrapper.write(Type.INT, -1);
         this.translatableRewriter.processText((JsonElement)wrapper.passthrough(Type.COMPONENT));
      });
   }

   public void init(UserConnection user) {
      this.addEntityTracker(user, new EntityTrackerBase(user, EntityTypes1_19_4.PLAYER));
   }

   public BackwardsMappings getMappingData() {
      return MAPPINGS;
   }

   public EntityPackets1_20 getEntityRewriter() {
      return this.entityRewriter;
   }

   public BlockItemPackets1_20 getItemRewriter() {
      return this.itemRewriter;
   }

   public TranslatableRewriter<ClientboundPackets1_19_4> getTranslatableRewriter() {
      return this.translatableRewriter;
   }
}
