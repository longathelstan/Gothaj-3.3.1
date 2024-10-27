package com.viaversion.viabackwards.api.rewriters;

import com.google.common.base.Preconditions;
import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.entities.storage.EntityData;
import com.viaversion.viabackwards.api.entities.storage.WrappedMetadata;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.Int2IntMapMappings;
import com.viaversion.viaversion.api.data.entity.StoredEntityData;
import com.viaversion.viaversion.api.data.entity.TrackedEntity;
import com.viaversion.viaversion.api.minecraft.ClientWorld;
import com.viaversion.viaversion.api.minecraft.Particle;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;
import java.util.Iterator;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class EntityRewriterBase<C extends ClientboundPacketType, T extends BackwardsProtocol<C, ?, ?, ?>> extends com.viaversion.viaversion.rewriter.EntityRewriter<C, T> {
   private final Int2ObjectMap<EntityData> entityDataMappings = new Int2ObjectOpenHashMap();
   private final MetaType displayNameMetaType;
   private final MetaType displayVisibilityMetaType;
   private final int displayNameIndex;
   private final int displayVisibilityIndex;

   EntityRewriterBase(T protocol, MetaType displayNameMetaType, int displayNameIndex, MetaType displayVisibilityMetaType, int displayVisibilityIndex) {
      super(protocol, false);
      this.displayNameMetaType = displayNameMetaType;
      this.displayNameIndex = displayNameIndex;
      this.displayVisibilityMetaType = displayVisibilityMetaType;
      this.displayVisibilityIndex = displayVisibilityIndex;
   }

   public void handleMetadata(int entityId, List<Metadata> metadataList, UserConnection connection) {
      TrackedEntity entity = this.tracker(connection).entity(entityId);
      boolean initialMetadata = entity == null || !entity.hasSentMetadata();
      super.handleMetadata(entityId, metadataList, connection);
      if (entity != null) {
         EntityData entityData = this.entityDataForType(entity.entityType());
         Object displayNameObject;
         if (entityData != null && (displayNameObject = entityData.entityName()) != null) {
            Metadata displayName = this.getMeta(this.displayNameIndex, metadataList);
            if (initialMetadata) {
               if (displayName == null) {
                  metadataList.add(new Metadata(this.displayNameIndex, this.displayNameMetaType, displayNameObject));
                  this.addDisplayVisibilityMeta(metadataList);
               } else if (displayName.getValue() == null || displayName.getValue().toString().isEmpty()) {
                  displayName.setValue(displayNameObject);
                  this.addDisplayVisibilityMeta(metadataList);
               }
            } else if (displayName != null && (displayName.getValue() == null || displayName.getValue().toString().isEmpty())) {
               displayName.setValue(displayNameObject);
               this.addDisplayVisibilityMeta(metadataList);
            }
         }

         if (entityData != null && entityData.hasBaseMeta() && initialMetadata) {
            entityData.defaultMeta().createMeta(new WrappedMetadata(metadataList));
         }

      }
   }

   private void addDisplayVisibilityMeta(List<Metadata> metadataList) {
      if (ViaBackwards.getConfig().alwaysShowOriginalMobName()) {
         this.removeMeta(this.displayVisibilityIndex, metadataList);
         metadataList.add(new Metadata(this.displayVisibilityIndex, this.displayVisibilityMetaType, true));
      }

   }

   @Nullable
   protected Metadata getMeta(int metaIndex, List<Metadata> metadataList) {
      Iterator var3 = metadataList.iterator();

      Metadata metadata;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         metadata = (Metadata)var3.next();
      } while(metadata.id() != metaIndex);

      return metadata;
   }

   protected void removeMeta(int metaIndex, List<Metadata> metadataList) {
      metadataList.removeIf((meta) -> {
         return meta.id() == metaIndex;
      });
   }

   protected boolean hasData(EntityType type) {
      return this.entityDataMappings.containsKey(type.getId());
   }

   @Nullable
   protected EntityData entityDataForType(EntityType type) {
      return (EntityData)this.entityDataMappings.get(type.getId());
   }

   @Nullable
   protected StoredEntityData storedEntityData(MetaHandlerEvent event) {
      return this.tracker(event.user()).entityData(event.entityId());
   }

   protected EntityData mapEntityTypeWithData(EntityType type, EntityType mappedType) {
      Preconditions.checkArgument(type.getClass() == mappedType.getClass(), "Both entity types need to be of the same class");
      int mappedReplacementId = this.newEntityId(mappedType.getId());
      EntityData data = new EntityData((BackwardsProtocol)this.protocol, type, mappedReplacementId);
      this.mapEntityType(type.getId(), mappedReplacementId);
      this.entityDataMappings.put(type.getId(), data);
      return data;
   }

   public <E extends Enum<E> & EntityType> void mapTypes(EntityType[] oldTypes, Class<E> newTypeClass) {
      if (this.typeMappings == null) {
         this.typeMappings = Int2IntMapMappings.of();
      }

      EntityType[] var3 = oldTypes;
      int var4 = oldTypes.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EntityType oldType = var3[var5];

         try {
            E newType = Enum.valueOf(newTypeClass, oldType.name());
            this.typeMappings.setNewId(oldType.getId(), ((EntityType)newType).getId());
         } catch (IllegalArgumentException var8) {
         }
      }

   }

   public void registerMetaTypeHandler(@Nullable MetaType itemType, @Nullable MetaType blockStateType, @Nullable MetaType optionalBlockStateType, @Nullable MetaType particleType, @Nullable MetaType componentType, @Nullable MetaType optionalComponentType) {
      this.filter().handler((event, meta) -> {
         MetaType type = meta.metaType();
         if (type == itemType) {
            ((BackwardsProtocol)this.protocol).getItemRewriter().handleItemToClient((Item)meta.value());
         } else {
            int data;
            if (type == blockStateType) {
               data = (Integer)meta.value();
               meta.setValue(((BackwardsProtocol)this.protocol).getMappingData().getNewBlockStateId(data));
            } else if (type == optionalBlockStateType) {
               data = (Integer)meta.value();
               if (data != 0) {
                  meta.setValue(((BackwardsProtocol)this.protocol).getMappingData().getNewBlockStateId(data));
               }
            } else if (type == particleType) {
               this.rewriteParticle((Particle)meta.value());
            } else if (type == optionalComponentType || type == componentType) {
               JsonElement text = (JsonElement)meta.value();
               if (text != null) {
                  ((BackwardsProtocol)this.protocol).getTranslatableRewriter().processText(text);
               }
            }
         }

      });
   }

   protected PacketHandler getTrackerHandler(Type<? extends Number> intType, int typeIndex) {
      return (wrapper) -> {
         Number id = (Number)wrapper.get(intType, typeIndex);
         this.tracker(wrapper.user()).addEntity((Integer)wrapper.get(Type.VAR_INT, 0), this.typeFromId(id.intValue()));
      };
   }

   protected PacketHandler getTrackerHandler() {
      return this.getTrackerHandler(Type.VAR_INT, 1);
   }

   protected PacketHandler getTrackerHandler(EntityType entityType, Type<? extends Number> intType) {
      return (wrapper) -> {
         this.tracker(wrapper.user()).addEntity((Integer)((Number)wrapper.get(intType, 0)), entityType);
      };
   }

   protected PacketHandler getDimensionHandler(int index) {
      return (wrapper) -> {
         ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
         int dimensionId = (Integer)wrapper.get(Type.INT, index);
         clientWorld.setEnvironment(dimensionId);
      };
   }
}
