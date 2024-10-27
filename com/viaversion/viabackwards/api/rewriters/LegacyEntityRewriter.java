package com.viaversion.viabackwards.api.rewriters;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.entities.storage.EntityData;
import com.viaversion.viabackwards.api.entities.storage.EntityObjectData;
import com.viaversion.viabackwards.api.entities.storage.WrappedMetadata;
import com.viaversion.viaversion.api.minecraft.ClientWorld;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.entities.ObjectType;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_9;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class LegacyEntityRewriter<C extends ClientboundPacketType, T extends BackwardsProtocol<C, ?, ?, ?>> extends EntityRewriterBase<C, T> {
   private final Map<ObjectType, EntityData> objectTypes;

   protected LegacyEntityRewriter(T protocol) {
      this(protocol, MetaType1_9.String, MetaType1_9.Boolean);
   }

   protected LegacyEntityRewriter(T protocol, MetaType displayType, MetaType displayVisibilityType) {
      super(protocol, displayType, 2, displayVisibilityType, 3);
      this.objectTypes = new HashMap();
   }

   protected EntityObjectData mapObjectType(ObjectType oldObjectType, ObjectType replacement, int data) {
      EntityObjectData entData = new EntityObjectData((BackwardsProtocol)this.protocol, oldObjectType.getType().name(), oldObjectType.getId(), replacement.getId(), data);
      this.objectTypes.put(oldObjectType, entData);
      return entData;
   }

   @Nullable
   protected EntityData getObjectData(ObjectType type) {
      return (EntityData)this.objectTypes.get(type);
   }

   protected void registerRespawn(C packetType) {
      ((BackwardsProtocol)this.protocol).registerClientbound(packetType, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.handler((wrapper) -> {
               ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
               clientWorld.setEnvironment((Integer)wrapper.get(Type.INT, 0));
            });
         }
      });
   }

   protected void registerJoinGame(C packetType, final EntityType playerType) {
      ((BackwardsProtocol)this.protocol).registerClientbound(packetType, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.INT);
            this.handler((wrapper) -> {
               ClientWorld clientChunks = (ClientWorld)wrapper.user().get(ClientWorld.class);
               clientChunks.setEnvironment((Integer)wrapper.get(Type.INT, 1));
               LegacyEntityRewriter.this.addTrackedEntity(wrapper, (Integer)wrapper.get(Type.INT, 0), playerType);
            });
         }
      });
   }

   public void registerMetadataRewriter(C packetType, final Type<List<Metadata>> oldMetaType, final Type<List<Metadata>> newMetaType) {
      ((BackwardsProtocol)this.protocol).registerClientbound(packetType, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            if (oldMetaType != null) {
               this.map(oldMetaType, newMetaType);
            } else {
               this.map(newMetaType);
            }

            this.handler((wrapper) -> {
               List<Metadata> metadata = (List)wrapper.get(newMetaType, 0);
               LegacyEntityRewriter.this.handleMetadata((Integer)wrapper.get(Type.VAR_INT, 0), metadata, wrapper.user());
            });
         }
      });
   }

   public void registerMetadataRewriter(C packetType, Type<List<Metadata>> metaType) {
      this.registerMetadataRewriter(packetType, (Type)null, metaType);
   }

   protected PacketHandler getMobSpawnRewriter(Type<List<Metadata>> metaType) {
      return (wrapper) -> {
         int entityId = (Integer)wrapper.get(Type.VAR_INT, 0);
         EntityType type = this.tracker(wrapper.user()).entityType(entityId);
         List<Metadata> metadata = (List)wrapper.get(metaType, 0);
         this.handleMetadata(entityId, metadata, wrapper.user());
         EntityData entityData = this.entityDataForType(type);
         if (entityData != null) {
            wrapper.set(Type.VAR_INT, 1, entityData.replacementId());
            if (entityData.hasBaseMeta()) {
               entityData.defaultMeta().createMeta(new WrappedMetadata(metadata));
            }
         }

      };
   }

   protected PacketHandler getObjectTrackerHandler() {
      return (wrapper) -> {
         this.addTrackedEntity(wrapper, (Integer)wrapper.get(Type.VAR_INT, 0), this.getObjectTypeFromId((Byte)wrapper.get(Type.BYTE, 0)));
      };
   }

   protected PacketHandler getTrackerAndMetaHandler(Type<List<Metadata>> metaType, EntityType entityType) {
      return (wrapper) -> {
         this.addTrackedEntity(wrapper, (Integer)wrapper.get(Type.VAR_INT, 0), entityType);
         List<Metadata> metadata = (List)wrapper.get(metaType, 0);
         this.handleMetadata((Integer)wrapper.get(Type.VAR_INT, 0), metadata, wrapper.user());
      };
   }

   protected PacketHandler getObjectRewriter(Function<Byte, ObjectType> objectGetter) {
      return (wrapper) -> {
         ObjectType type = (ObjectType)objectGetter.apply((Byte)wrapper.get(Type.BYTE, 0));
         if (type == null) {
            ViaBackwards.getPlatform().getLogger().warning("Could not find Entity Type" + wrapper.get(Type.BYTE, 0));
         } else {
            EntityData data = this.getObjectData(type);
            if (data != null) {
               wrapper.set(Type.BYTE, 0, (byte)data.replacementId());
               if (data.objectData() != -1) {
                  wrapper.set(Type.INT, 0, data.objectData());
               }
            }

         }
      };
   }

   protected EntityType getObjectTypeFromId(int typeId) {
      return this.typeFromId(typeId);
   }

   /** @deprecated */
   @Deprecated
   protected void addTrackedEntity(PacketWrapper wrapper, int entityId, EntityType type) throws Exception {
      this.tracker(wrapper.user()).addEntity(entityId, type);
   }
}
