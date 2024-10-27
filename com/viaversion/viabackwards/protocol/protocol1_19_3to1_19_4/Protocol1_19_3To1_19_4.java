package com.viaversion.viabackwards.protocol.protocol1_19_3to1_19_4;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.data.BackwardsMappings;
import com.viaversion.viabackwards.api.rewriters.SoundRewriter;
import com.viaversion.viabackwards.api.rewriters.TranslatableRewriter;
import com.viaversion.viabackwards.protocol.protocol1_19_3to1_19_4.packets.BlockItemPackets1_19_4;
import com.viaversion.viabackwards.protocol.protocol1_19_3to1_19_4.packets.EntityPackets1_19_4;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_19_4;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.ClientboundPackets1_19_3;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.ServerboundPackets1_19_3;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.ClientboundPackets1_19_4;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.Protocol1_19_4To1_19_3;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.ServerboundPackets1_19_4;
import com.viaversion.viaversion.rewriter.CommandRewriter;
import com.viaversion.viaversion.rewriter.ComponentRewriter;
import com.viaversion.viaversion.rewriter.StatisticsRewriter;
import com.viaversion.viaversion.rewriter.TagRewriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class Protocol1_19_3To1_19_4 extends BackwardsProtocol<ClientboundPackets1_19_4, ClientboundPackets1_19_3, ServerboundPackets1_19_4, ServerboundPackets1_19_3> {
   public static final BackwardsMappings MAPPINGS = new BackwardsMappings("1.19.4", "1.19.3", Protocol1_19_4To1_19_3.class);
   private final EntityPackets1_19_4 entityRewriter = new EntityPackets1_19_4(this);
   private final BlockItemPackets1_19_4 itemRewriter = new BlockItemPackets1_19_4(this);
   private final TranslatableRewriter<ClientboundPackets1_19_4> translatableRewriter;

   public Protocol1_19_3To1_19_4() {
      super(ClientboundPackets1_19_4.class, ClientboundPackets1_19_3.class, ServerboundPackets1_19_4.class, ServerboundPackets1_19_3.class);
      this.translatableRewriter = new TranslatableRewriter(this, ComponentRewriter.ReadType.JSON);
   }

   protected void registerPackets() {
      super.registerPackets();
      SoundRewriter<ClientboundPackets1_19_4> soundRewriter = new SoundRewriter(this);
      soundRewriter.registerStopSound(ClientboundPackets1_19_4.STOP_SOUND);
      soundRewriter.register1_19_3Sound(ClientboundPackets1_19_4.SOUND);
      soundRewriter.registerSound(ClientboundPackets1_19_4.ENTITY_SOUND);
      this.translatableRewriter.registerComponentPacket(ClientboundPackets1_19_4.ACTIONBAR);
      this.translatableRewriter.registerComponentPacket(ClientboundPackets1_19_4.TITLE_TEXT);
      this.translatableRewriter.registerComponentPacket(ClientboundPackets1_19_4.TITLE_SUBTITLE);
      this.translatableRewriter.registerBossBar(ClientboundPackets1_19_4.BOSSBAR);
      this.translatableRewriter.registerComponentPacket(ClientboundPackets1_19_4.DISCONNECT);
      this.translatableRewriter.registerTabList(ClientboundPackets1_19_4.TAB_LIST);
      this.translatableRewriter.registerCombatKill(ClientboundPackets1_19_4.COMBAT_KILL);
      this.translatableRewriter.registerComponentPacket(ClientboundPackets1_19_4.SYSTEM_CHAT);
      this.translatableRewriter.registerComponentPacket(ClientboundPackets1_19_4.DISGUISED_CHAT);
      this.translatableRewriter.registerPing();
      (new CommandRewriter<ClientboundPackets1_19_4>(this) {
         public void handleArgument(PacketWrapper wrapper, String argumentType) throws Exception {
            byte var4 = -1;
            switch(argumentType.hashCode()) {
            case -1373584190:
               if (argumentType.equals("minecraft:resource_or_tag")) {
                  var4 = 3;
               }
               break;
            case -1006391174:
               if (argumentType.equals("minecraft:time")) {
                  var4 = 1;
               }
               break;
            case 234902856:
               if (argumentType.equals("minecraft:heightmap")) {
                  var4 = 0;
               }
               break;
            case 688423739:
               if (argumentType.equals("minecraft:resource")) {
                  var4 = 2;
               }
            }

            switch(var4) {
            case 0:
               wrapper.write(Type.VAR_INT, 0);
               break;
            case 1:
               wrapper.read(Type.INT);
               break;
            case 2:
            case 3:
               String resource = (String)wrapper.read(Type.STRING);
               wrapper.write(Type.STRING, resource.equals("minecraft:damage_type") ? "minecraft:mob_effect" : resource);
               break;
            default:
               super.handleArgument(wrapper, argumentType);
            }

         }
      }).registerDeclareCommands1_19(ClientboundPackets1_19_4.DECLARE_COMMANDS);
      TagRewriter<ClientboundPackets1_19_4> tagRewriter = new TagRewriter(this);
      tagRewriter.removeTags("minecraft:damage_type");
      tagRewriter.registerGeneric(ClientboundPackets1_19_4.TAGS);
      (new StatisticsRewriter(this)).register(ClientboundPackets1_19_4.STATISTICS);
      this.registerClientbound(ClientboundPackets1_19_4.SERVER_DATA, (wrapper) -> {
         JsonElement element = (JsonElement)wrapper.read(Type.COMPONENT);
         wrapper.write(Type.OPTIONAL_COMPONENT, element);
         byte[] iconBytes = (byte[])wrapper.read(Type.OPTIONAL_BYTE_ARRAY_PRIMITIVE);
         String iconBase64 = iconBytes != null ? "data:image/png;base64," + new String(Base64.getEncoder().encode(iconBytes), StandardCharsets.UTF_8) : null;
         wrapper.write(Type.OPTIONAL_STRING, iconBase64);
      });
      this.cancelClientbound(ClientboundPackets1_19_4.BUNDLE);
      this.cancelClientbound(ClientboundPackets1_19_4.CHUNK_BIOMES);
   }

   public void init(UserConnection user) {
      this.addEntityTracker(user, new EntityTrackerBase(user, EntityTypes1_19_4.PLAYER));
   }

   public BackwardsMappings getMappingData() {
      return MAPPINGS;
   }

   public BlockItemPackets1_19_4 getItemRewriter() {
      return this.itemRewriter;
   }

   public EntityPackets1_19_4 getEntityRewriter() {
      return this.entityRewriter;
   }

   public TranslatableRewriter<ClientboundPackets1_19_4> getTranslatableRewriter() {
      return this.translatableRewriter;
   }
}
