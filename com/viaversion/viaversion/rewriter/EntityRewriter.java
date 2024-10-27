package com.viaversion.viaversion.rewriter;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.Int2IntMapMappings;
import com.viaversion.viaversion.api.data.Mappings;
import com.viaversion.viaversion.api.data.ParticleMappings;
import com.viaversion.viaversion.api.data.entity.DimensionData;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.data.entity.TrackedEntity;
import com.viaversion.viaversion.api.minecraft.Particle;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.data.entity.DimensionDataImpl;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.rewriter.meta.MetaFilter;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEventImpl;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class EntityRewriter<C extends ClientboundPacketType, T extends Protocol<C, ?, ?, ?>> extends RewriterBase<T> implements com.viaversion.viaversion.api.rewriter.EntityRewriter<T> {
   private static final Metadata[] EMPTY_ARRAY = new Metadata[0];
   protected final List<MetaFilter> metadataFilters;
   protected final boolean trackMappedType;
   protected Mappings typeMappings;

   protected EntityRewriter(T protocol) {
      this(protocol, true);
   }

   protected EntityRewriter(T protocol, boolean trackMappedType) {
      super(protocol);
      this.metadataFilters = new ArrayList();
      this.trackMappedType = trackMappedType;
      protocol.put(this);
   }

   public MetaFilter.Builder filter() {
      return new MetaFilter.Builder(this);
   }

   public void registerFilter(MetaFilter filter) {
      Preconditions.checkArgument(!this.metadataFilters.contains(filter));
      this.metadataFilters.add(filter);
   }

   public void handleMetadata(int entityId, List<Metadata> metadataList, UserConnection connection) {
      TrackedEntity entity = this.tracker(connection).entity(entityId);
      EntityType type = entity != null ? entity.entityType() : null;
      Metadata[] var6 = (Metadata[])metadataList.toArray(EMPTY_ARRAY);
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Metadata metadata = var6[var8];
         if (!this.callOldMetaHandler(entityId, type, metadata, metadataList, connection)) {
            metadataList.remove(metadata);
         } else {
            MetaHandlerEvent event = null;
            Iterator var11 = this.metadataFilters.iterator();

            while(var11.hasNext()) {
               MetaFilter filter = (MetaFilter)var11.next();
               if (filter.isFiltered(type, metadata)) {
                  if (event == null) {
                     event = new MetaHandlerEventImpl(connection, entity, entityId, metadata, metadataList);
                  }

                  try {
                     filter.handler().handle(event, metadata);
                  } catch (Exception var14) {
                     this.logException(var14, type, metadataList, metadata);
                     metadataList.remove(metadata);
                     break;
                  }

                  if (event.cancelled()) {
                     metadataList.remove(metadata);
                     break;
                  }
               }
            }

            List<Metadata> extraMeta = event != null ? event.extraMeta() : null;
            if (extraMeta != null) {
               metadataList.addAll(extraMeta);
            }
         }
      }

      if (entity != null) {
         entity.sentMetadata(true);
      }

   }

   /** @deprecated */
   @Deprecated
   private boolean callOldMetaHandler(int entityId, @Nullable EntityType type, Metadata metadata, List<Metadata> metadataList, UserConnection connection) {
      try {
         this.handleMetadata(entityId, type, metadata, metadataList, connection);
         return true;
      } catch (Exception var7) {
         this.logException(var7, type, metadataList, metadata);
         return false;
      }
   }

   /** @deprecated */
   @Deprecated
   protected void handleMetadata(int entityId, @Nullable EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) throws Exception {
   }

   public int newEntityId(int id) {
      return this.typeMappings != null ? this.typeMappings.getNewIdOrDefault(id, id) : id;
   }

   public void mapEntityType(EntityType type, EntityType mappedType) {
      Preconditions.checkArgument(type.getClass() != mappedType.getClass(), "EntityTypes should not be of the same class/enum");
      this.mapEntityType(type.getId(), mappedType.getId());
   }

   protected void mapEntityType(int id, int mappedId) {
      if (this.typeMappings == null) {
         this.typeMappings = Int2IntMapMappings.of();
      }

      this.typeMappings.setNewId(id, mappedId);
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
            if (!this.typeMappings.contains(oldType.getId())) {
               Via.getPlatform().getLogger().warning("Could not find new entity type for " + oldType + "! Old type: " + oldType.getClass().getEnclosingClass().getSimpleName() + ", new type: " + newTypeClass.getEnclosingClass().getSimpleName());
            }
         }
      }

   }

   public void mapTypes() {
      Preconditions.checkArgument(this.typeMappings == null, "Type mappings have already been set - manual type mappings should be set *after* this");
      Preconditions.checkNotNull(this.protocol.getMappingData().getEntityMappings(), "Protocol does not have entity mappings");
      this.typeMappings = this.protocol.getMappingData().getEntityMappings();
   }

   public void registerMetaTypeHandler(@Nullable MetaType itemType, @Nullable MetaType blockStateType, @Nullable MetaType optionalBlockStateType, @Nullable MetaType particleType) {
      this.filter().handler((event, meta) -> {
         MetaType type = meta.metaType();
         if (type == itemType) {
            this.protocol.getItemRewriter().handleItemToClient((Item)meta.value());
         } else {
            int data;
            if (type == blockStateType) {
               data = (Integer)meta.value();
               meta.setValue(this.protocol.getMappingData().getNewBlockStateId(data));
            } else if (type == optionalBlockStateType) {
               data = (Integer)meta.value();
               if (data != 0) {
                  meta.setValue(this.protocol.getMappingData().getNewBlockStateId(data));
               }
            } else if (type == particleType) {
               this.rewriteParticle((Particle)meta.value());
            }
         }

      });
   }

   public void registerTracker(C packetType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.UUID);
            this.map(Type.VAR_INT);
            this.handler(EntityRewriter.this.trackerHandler());
         }
      }));
   }

   public void registerTrackerWithData(C packetType, final EntityType fallingBlockType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.UUID);
            this.map(Type.VAR_INT);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.INT);
            this.handler(EntityRewriter.this.trackerHandler());
            this.handler((wrapper) -> {
               int entityId = (Integer)wrapper.get(Type.VAR_INT, 0);
               EntityType entityType = EntityRewriter.this.tracker(wrapper.user()).entityType(entityId);
               if (entityType == fallingBlockType) {
                  wrapper.set(Type.INT, 0, EntityRewriter.this.protocol.getMappingData().getNewBlockStateId((Integer)wrapper.get(Type.INT, 0)));
               }

            });
         }
      }));
   }

   public void registerTrackerWithData1_19(C packetType, final EntityType fallingBlockType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.UUID);
            this.map(Type.VAR_INT);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.VAR_INT);
            this.handler(EntityRewriter.this.trackerHandler());
            this.handler((wrapper) -> {
               if (EntityRewriter.this.protocol.getMappingData() != null) {
                  int entityId = (Integer)wrapper.get(Type.VAR_INT, 0);
                  EntityType entityType = EntityRewriter.this.tracker(wrapper.user()).entityType(entityId);
                  if (entityType == fallingBlockType) {
                     wrapper.set(Type.VAR_INT, 2, EntityRewriter.this.protocol.getMappingData().getNewBlockStateId((Integer)wrapper.get(Type.VAR_INT, 2)));
                  }

               }
            });
         }
      }));
   }

   public void registerTracker(C packetType, EntityType entityType, Type<Integer> intType) {
      this.protocol.registerClientbound(packetType, (wrapper) -> {
         int entityId = (Integer)wrapper.passthrough(intType);
         this.tracker(wrapper.user()).addEntity(entityId, entityType);
      });
   }

   public void registerTracker(C packetType, EntityType entityType) {
      this.registerTracker(packetType, entityType, Type.VAR_INT);
   }

   public void registerRemoveEntities(C packetType) {
      this.protocol.registerClientbound(packetType, (wrapper) -> {
         int[] entityIds = (int[])wrapper.passthrough(Type.VAR_INT_ARRAY_PRIMITIVE);
         EntityTracker entityTracker = this.tracker(wrapper.user());
         int[] var4 = entityIds;
         int var5 = entityIds.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            int entity = var4[var6];
            entityTracker.removeEntity(entity);
         }

      });
   }

   public void registerRemoveEntity(C packetType) {
      this.protocol.registerClientbound(packetType, (wrapper) -> {
         int entityId = (Integer)wrapper.passthrough(Type.VAR_INT);
         this.tracker(wrapper.user()).removeEntity(entityId);
      });
   }

   public void registerMetadataRewriter(C packetType, @Nullable final Type<List<Metadata>> oldMetaType, final Type<List<Metadata>> newMetaType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            if (oldMetaType != null) {
               this.map(oldMetaType, newMetaType);
            } else {
               this.map(newMetaType);
            }

            this.handler((wrapper) -> {
               int entityId = (Integer)wrapper.get(Type.VAR_INT, 0);
               List<Metadata> metadata = (List)wrapper.get(newMetaType, 0);
               EntityRewriter.this.handleMetadata(entityId, metadata, wrapper.user());
            });
         }
      }));
   }

   public void registerMetadataRewriter(C packetType, Type<List<Metadata>> metaType) {
      this.registerMetadataRewriter(packetType, (Type)null, metaType);
   }

   public PacketHandler trackerHandler() {
      return this.trackerAndRewriterHandler((Type)null);
   }

   public PacketHandler playerTrackerHandler() {
      return (wrapper) -> {
         EntityTracker tracker = this.tracker(wrapper.user());
         int entityId = (Integer)wrapper.get(Type.INT, 0);
         tracker.setClientEntityId(entityId);
         tracker.addEntity(entityId, tracker.playerType());
      };
   }

   public PacketHandler worldDataTrackerHandler(int nbtIndex) {
      return (wrapper) -> {
         EntityTracker tracker = this.tracker(wrapper.user());
         CompoundTag registryData = (CompoundTag)wrapper.get(Type.NAMED_COMPOUND_TAG, nbtIndex);
         Tag height = registryData.get("height");
         if (height instanceof IntTag) {
            int blockHeight = ((IntTag)height).asInt();
            tracker.setCurrentWorldSectionHeight(blockHeight >> 4);
         } else {
            Via.getPlatform().getLogger().warning("Height missing in dimension data: " + registryData);
         }

         Tag minY = registryData.get("min_y");
         if (minY instanceof IntTag) {
            tracker.setCurrentMinY(((IntTag)minY).asInt());
         } else {
            Via.getPlatform().getLogger().warning("Min Y missing in dimension data: " + registryData);
         }

         String world = (String)wrapper.get(Type.STRING, 0);
         if (tracker.currentWorld() != null && !tracker.currentWorld().equals(world)) {
            tracker.clearEntities();
            tracker.trackClientEntity();
         }

         tracker.setCurrentWorld(world);
      };
   }

   public PacketHandler worldDataTrackerHandlerByKey() {
      return (wrapper) -> {
         EntityTracker tracker = this.tracker(wrapper.user());
         String dimensionKey = (String)wrapper.get(Type.STRING, 0);
         DimensionData dimensionData = tracker.dimensionData(dimensionKey);
         if (dimensionData == null) {
            Via.getPlatform().getLogger().severe("Dimension data missing for dimension: " + dimensionKey + ", falling back to overworld");
            dimensionData = tracker.dimensionData("minecraft:overworld");
            Preconditions.checkNotNull(dimensionData, "Overworld data missing");
         }

         tracker.setCurrentWorldSectionHeight(dimensionData.height() >> 4);
         tracker.setCurrentMinY(dimensionData.minY());
         String world = (String)wrapper.get(Type.STRING, 1);
         if (tracker.currentWorld() != null && !tracker.currentWorld().equals(world)) {
            tracker.clearEntities();
            tracker.trackClientEntity();
         }

         tracker.setCurrentWorld(world);
      };
   }

   public PacketHandler biomeSizeTracker() {
      return (wrapper) -> {
         this.trackBiomeSize(wrapper.user(), (CompoundTag)wrapper.get(Type.NAMED_COMPOUND_TAG, 0));
      };
   }

   public PacketHandler configurationBiomeSizeTracker() {
      return (wrapper) -> {
         this.trackBiomeSize(wrapper.user(), (CompoundTag)wrapper.get(Type.COMPOUND_TAG, 0));
      };
   }

   public void trackBiomeSize(UserConnection connection, CompoundTag registry) {
      CompoundTag biomeRegistry = (CompoundTag)registry.get("minecraft:worldgen/biome");
      ListTag biomes = (ListTag)biomeRegistry.get("value");
      this.tracker(connection).setBiomesSent(biomes.size());
   }

   public PacketHandler dimensionDataHandler() {
      return (wrapper) -> {
         this.cacheDimensionData(wrapper.user(), (CompoundTag)wrapper.get(Type.NAMED_COMPOUND_TAG, 0));
      };
   }

   public PacketHandler configurationDimensionDataHandler() {
      return (wrapper) -> {
         this.cacheDimensionData(wrapper.user(), (CompoundTag)wrapper.get(Type.COMPOUND_TAG, 0));
      };
   }

   public void cacheDimensionData(UserConnection connection, CompoundTag registry) {
      ListTag dimensions = (ListTag)((CompoundTag)registry.get("minecraft:dimension_type")).get("value");
      Map<String, DimensionData> dimensionDataMap = new HashMap(dimensions.size());
      Iterator var5 = dimensions.iterator();

      while(var5.hasNext()) {
         Tag dimension = (Tag)var5.next();
         CompoundTag dimensionCompound = (CompoundTag)dimension;
         CompoundTag element = (CompoundTag)dimensionCompound.get("element");
         String name = (String)dimensionCompound.get("name").getValue();
         dimensionDataMap.put(name, new DimensionDataImpl(element));
      }

      this.tracker(connection).setDimensions(dimensionDataMap);
   }

   public PacketHandler trackerAndRewriterHandler(@Nullable Type<List<Metadata>> metaType) {
      return (wrapper) -> {
         int entityId = (Integer)wrapper.get(Type.VAR_INT, 0);
         int type = (Integer)wrapper.get(Type.VAR_INT, 1);
         int newType = this.newEntityId(type);
         if (newType != type) {
            wrapper.set(Type.VAR_INT, 1, newType);
         }

         EntityType entType = this.typeFromId(this.trackMappedType ? newType : type);
         this.tracker(wrapper.user()).addEntity(entityId, entType);
         if (metaType != null) {
            this.handleMetadata(entityId, (List)wrapper.get(metaType, 0), wrapper.user());
         }

      };
   }

   public PacketHandler trackerAndRewriterHandler(@Nullable Type<List<Metadata>> metaType, EntityType entityType) {
      return (wrapper) -> {
         int entityId = (Integer)wrapper.get(Type.VAR_INT, 0);
         this.tracker(wrapper.user()).addEntity(entityId, entityType);
         if (metaType != null) {
            this.handleMetadata(entityId, (List)wrapper.get(metaType, 0), wrapper.user());
         }

      };
   }

   public PacketHandler objectTrackerHandler() {
      return (wrapper) -> {
         int entityId = (Integer)wrapper.get(Type.VAR_INT, 0);
         byte type = (Byte)wrapper.get(Type.BYTE, 0);
         EntityType entType = this.objectTypeFromId(type);
         this.tracker(wrapper.user()).addEntity(entityId, entType);
      };
   }

   /** @deprecated */
   @Deprecated
   @Nullable
   protected Metadata metaByIndex(int index, List<Metadata> metadataList) {
      Iterator var3 = metadataList.iterator();

      Metadata metadata;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         metadata = (Metadata)var3.next();
      } while(metadata.id() != index);

      return metadata;
   }

   protected void rewriteParticle(Particle particle) {
      ParticleMappings mappings = this.protocol.getMappingData().getParticleMappings();
      int id = particle.getId();
      Particle.ParticleData data;
      if (mappings.isBlockParticle(id)) {
         data = particle.getArgument(0);
         data.setValue(this.protocol.getMappingData().getNewBlockStateId((Integer)data.getValue()));
      } else if (mappings.isItemParticle(id) && this.protocol.getItemRewriter() != null) {
         data = particle.getArgument(0);
         Item item = (Item)data.getValue();
         this.protocol.getItemRewriter().handleItemToClient(item);
      }

      particle.setId(this.protocol.getMappingData().getNewParticleId(id));
   }

   private void logException(Exception e, @Nullable EntityType type, List<Metadata> metadataList, Metadata metadata) {
      if (!Via.getConfig().isSuppressMetadataErrors() || Via.getManager().isDebug()) {
         Logger logger = Via.getPlatform().getLogger();
         logger.severe("An error occurred in metadata handler " + this.getClass().getSimpleName() + " for " + (type != null ? type.name() : "untracked") + " entity type: " + metadata);
         logger.severe((String)metadataList.stream().sorted(Comparator.comparingInt(Metadata::id)).map(Metadata::toString).collect(Collectors.joining("\n", "Full metadata: ", "")));
         logger.log(Level.SEVERE, "Error: ", e);
      }

   }
}
