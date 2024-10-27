package com.viaversion.viabackwards.protocol.protocol1_16_4to1_17;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.data.BackwardsMappings;
import com.viaversion.viabackwards.api.rewriters.SoundRewriter;
import com.viaversion.viabackwards.api.rewriters.TranslatableRewriter;
import com.viaversion.viabackwards.protocol.protocol1_16_4to1_17.packets.BlockItemPackets1_17;
import com.viaversion.viabackwards.protocol.protocol1_16_4to1_17.packets.EntityPackets1_17;
import com.viaversion.viabackwards.protocol.protocol1_16_4to1_17.storage.PingRequests;
import com.viaversion.viabackwards.protocol.protocol1_16_4to1_17.storage.PlayerLastCursorItem;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.api.minecraft.TagData;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_17;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.libs.fastutil.ints.IntArrayList;
import com.viaversion.viaversion.libs.fastutil.ints.IntList;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ClientboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.Protocol1_17To1_16_4;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ServerboundPackets1_17;
import com.viaversion.viaversion.rewriter.ComponentRewriter;
import com.viaversion.viaversion.rewriter.IdRewriteFunction;
import com.viaversion.viaversion.rewriter.StatisticsRewriter;
import com.viaversion.viaversion.rewriter.TagRewriter;
import com.viaversion.viaversion.util.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Protocol1_16_4To1_17 extends BackwardsProtocol<ClientboundPackets1_17, ClientboundPackets1_16_2, ServerboundPackets1_17, ServerboundPackets1_16_2> {
   public static final BackwardsMappings MAPPINGS = new BackwardsMappings("1.17", "1.16.2", Protocol1_17To1_16_4.class);
   private static final RegistryType[] TAG_REGISTRY_TYPES;
   private static final int[] EMPTY_ARRAY;
   private final EntityPackets1_17 entityRewriter = new EntityPackets1_17(this);
   private final BlockItemPackets1_17 blockItemPackets = new BlockItemPackets1_17(this);
   private final TranslatableRewriter<ClientboundPackets1_17> translatableRewriter;

   public Protocol1_16_4To1_17() {
      super(ClientboundPackets1_17.class, ClientboundPackets1_16_2.class, ServerboundPackets1_17.class, ServerboundPackets1_16_2.class);
      this.translatableRewriter = new TranslatableRewriter(this, ComponentRewriter.ReadType.JSON);
   }

   protected void registerPackets() {
      super.registerPackets();
      this.translatableRewriter.registerComponentPacket(ClientboundPackets1_17.CHAT_MESSAGE);
      this.translatableRewriter.registerBossBar(ClientboundPackets1_17.BOSSBAR);
      this.translatableRewriter.registerComponentPacket(ClientboundPackets1_17.DISCONNECT);
      this.translatableRewriter.registerTabList(ClientboundPackets1_17.TAB_LIST);
      this.translatableRewriter.registerOpenWindow(ClientboundPackets1_17.OPEN_WINDOW);
      this.translatableRewriter.registerPing();
      SoundRewriter<ClientboundPackets1_17> soundRewriter = new SoundRewriter(this);
      soundRewriter.registerSound(ClientboundPackets1_17.SOUND);
      soundRewriter.registerSound(ClientboundPackets1_17.ENTITY_SOUND);
      soundRewriter.registerNamedSound(ClientboundPackets1_17.NAMED_SOUND);
      soundRewriter.registerStopSound(ClientboundPackets1_17.STOP_SOUND);
      TagRewriter<ClientboundPackets1_17> tagRewriter = new TagRewriter(this);
      this.registerClientbound(ClientboundPackets1_17.TAGS, (wrapper) -> {
         Map<String, List<TagData>> tags = new HashMap();
         int length = (Integer)wrapper.read(Type.VAR_INT);

         for(int i = 0; i < length; ++i) {
            String resourceKey = Key.stripMinecraftNamespace((String)wrapper.read(Type.STRING));
            List<TagData> tagListx = new ArrayList();
            tags.put(resourceKey, tagListx);
            int tagLength = (Integer)wrapper.read(Type.VAR_INT);

            for(int j = 0; j < tagLength; ++j) {
               String identifier = (String)wrapper.read(Type.STRING);
               int[] entries = (int[])wrapper.read(Type.VAR_INT_ARRAY_PRIMITIVE);
               tagListx.add(new TagData(identifier, entries));
            }
         }

         RegistryType[] var19 = TAG_REGISTRY_TYPES;
         int var20 = var19.length;

         for(int var21 = 0; var21 < var20; ++var21) {
            RegistryType type = var19[var21];
            List<TagData> tagList = (List)tags.get(type.resourceLocation());
            if (tagList == null) {
               wrapper.write(Type.VAR_INT, 0);
            } else {
               IdRewriteFunction rewriter = tagRewriter.getRewriter(type);
               wrapper.write(Type.VAR_INT, tagList.size());
               Iterator var25 = tagList.iterator();

               while(var25.hasNext()) {
                  TagData tagData = (TagData)var25.next();
                  int[] entriesx = tagData.entries();
                  if (rewriter != null) {
                     IntList idList = new IntArrayList(entriesx.length);
                     int[] var14 = entriesx;
                     int var15 = entriesx.length;

                     for(int var16 = 0; var16 < var15; ++var16) {
                        int id = var14[var16];
                        int mappedId = rewriter.rewrite(id);
                        if (mappedId != -1) {
                           idList.add(mappedId);
                        }
                     }

                     entriesx = idList.toArray(EMPTY_ARRAY);
                  }

                  wrapper.write(Type.STRING, tagData.identifier());
                  wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, entriesx);
               }
            }
         }

      });
      (new StatisticsRewriter(this)).register(ClientboundPackets1_17.STATISTICS);
      this.registerClientbound(ClientboundPackets1_17.RESOURCE_PACK, (wrapper) -> {
         wrapper.passthrough(Type.STRING);
         wrapper.passthrough(Type.STRING);
         wrapper.read(Type.BOOLEAN);
         wrapper.read(Type.OPTIONAL_COMPONENT);
      });
      this.registerClientbound(ClientboundPackets1_17.EXPLOSION, new PacketHandlers() {
         public void register() {
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.handler((wrapper) -> {
               wrapper.write(Type.INT, (Integer)wrapper.read(Type.VAR_INT));
            });
         }
      });
      this.registerClientbound(ClientboundPackets1_17.SPAWN_POSITION, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_14);
            this.handler((wrapper) -> {
               wrapper.read(Type.FLOAT);
            });
         }
      });
      this.registerClientbound(ClientboundPackets1_17.PING, (ClientboundPacketType)null, (wrapper) -> {
         wrapper.cancel();
         int id = (Integer)wrapper.read(Type.INT);
         short shortId = (short)id;
         PacketWrapper pongPacket;
         if (id == shortId && ViaBackwards.getConfig().handlePingsAsInvAcknowledgements()) {
            ((PingRequests)wrapper.user().get(PingRequests.class)).addId(shortId);
            pongPacket = wrapper.create(ClientboundPackets1_16_2.WINDOW_CONFIRMATION);
            pongPacket.write(Type.UNSIGNED_BYTE, Short.valueOf((short)0));
            pongPacket.write(Type.SHORT, shortId);
            pongPacket.write(Type.BOOLEAN, false);
            pongPacket.send(Protocol1_16_4To1_17.class);
         } else {
            pongPacket = wrapper.create(ServerboundPackets1_17.PONG);
            pongPacket.write(Type.INT, id);
            pongPacket.sendToServer(Protocol1_16_4To1_17.class);
         }
      });
      this.registerServerbound(ServerboundPackets1_16_2.CLIENT_SETTINGS, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.map(Type.BYTE);
            this.map(Type.VAR_INT);
            this.map(Type.BOOLEAN);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               wrapper.write(Type.BOOLEAN, false);
            });
         }
      });
      this.mergePacket(ClientboundPackets1_17.TITLE_TEXT, ClientboundPackets1_16_2.TITLE, 0);
      this.mergePacket(ClientboundPackets1_17.TITLE_SUBTITLE, ClientboundPackets1_16_2.TITLE, 1);
      this.mergePacket(ClientboundPackets1_17.ACTIONBAR, ClientboundPackets1_16_2.TITLE, 2);
      this.mergePacket(ClientboundPackets1_17.TITLE_TIMES, ClientboundPackets1_16_2.TITLE, 3);
      this.registerClientbound(ClientboundPackets1_17.CLEAR_TITLES, ClientboundPackets1_16_2.TITLE, (wrapper) -> {
         if ((Boolean)wrapper.read(Type.BOOLEAN)) {
            wrapper.write(Type.VAR_INT, 5);
         } else {
            wrapper.write(Type.VAR_INT, 4);
         }

      });
      this.cancelClientbound(ClientboundPackets1_17.ADD_VIBRATION_SIGNAL);
   }

   public void init(UserConnection user) {
      this.addEntityTracker(user, new EntityTrackerBase(user, EntityTypes1_17.PLAYER));
      user.put(new PingRequests());
      user.put(new PlayerLastCursorItem());
   }

   public BackwardsMappings getMappingData() {
      return MAPPINGS;
   }

   public TranslatableRewriter<ClientboundPackets1_17> getTranslatableRewriter() {
      return this.translatableRewriter;
   }

   public void mergePacket(ClientboundPackets1_17 newPacketType, ClientboundPackets1_16_2 oldPacketType, int type) {
      this.registerClientbound(newPacketType, oldPacketType, (wrapper) -> {
         wrapper.write(Type.VAR_INT, type);
      });
   }

   public EntityPackets1_17 getEntityRewriter() {
      return this.entityRewriter;
   }

   public BlockItemPackets1_17 getItemRewriter() {
      return this.blockItemPackets;
   }

   static {
      TAG_REGISTRY_TYPES = new RegistryType[]{RegistryType.BLOCK, RegistryType.ITEM, RegistryType.FLUID, RegistryType.ENTITY};
      EMPTY_ARRAY = new int[0];
   }
}
