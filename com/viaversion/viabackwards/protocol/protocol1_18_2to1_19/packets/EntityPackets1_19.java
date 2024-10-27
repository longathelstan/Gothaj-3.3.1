package com.viaversion.viabackwards.protocol.protocol1_18_2to1_19.packets;

import com.viaversion.viabackwards.api.rewriters.EntityRewriter;
import com.viaversion.viabackwards.protocol.protocol1_18_2to1_19.Protocol1_18_2To1_19;
import com.viaversion.viabackwards.protocol.protocol1_18_2to1_19.storage.DimensionRegistryStorage;
import com.viaversion.viabackwards.protocol.protocol1_18_2to1_19.storage.StoredPainting;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.ParticleMappings;
import com.viaversion.viaversion.api.data.entity.StoredEntityData;
import com.viaversion.viaversion.api.minecraft.Particle;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_19;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_18;
import com.viaversion.viaversion.api.type.types.version.Types1_19;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.ClientboundPackets1_18;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.ClientboundPackets1_19;
import java.util.Iterator;
import java.util.UUID;

public final class EntityPackets1_19 extends EntityRewriter<ClientboundPackets1_19, Protocol1_18_2To1_19> {
   public EntityPackets1_19(Protocol1_18_2To1_19 protocol) {
      super(protocol);
   }

   protected void registerPackets() {
      this.registerTracker(ClientboundPackets1_19.SPAWN_EXPERIENCE_ORB, EntityTypes1_19.EXPERIENCE_ORB);
      this.registerTracker(ClientboundPackets1_19.SPAWN_PLAYER, EntityTypes1_19.PLAYER);
      this.registerMetadataRewriter(ClientboundPackets1_19.ENTITY_METADATA, Types1_19.METADATA_LIST, Types1_18.METADATA_LIST);
      this.registerRemoveEntities(ClientboundPackets1_19.REMOVE_ENTITIES);
      ((Protocol1_18_2To1_19)this.protocol).registerClientbound(ClientboundPackets1_19.SPAWN_ENTITY, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.UUID);
            this.map(Type.VAR_INT);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.handler((wrapper) -> {
               byte headYaw = (Byte)wrapper.read(Type.BYTE);
               int data = (Integer)wrapper.read(Type.VAR_INT);
               EntityType entityType = EntityPackets1_19.this.trackAndMapEntity(wrapper);
               if (entityType.isOrHasParent(EntityTypes1_19.LIVINGENTITY)) {
                  wrapper.write(Type.BYTE, headYaw);
                  byte pitch = (Byte)wrapper.get(Type.BYTE, 0);
                  byte yaw = (Byte)wrapper.get(Type.BYTE, 1);
                  wrapper.set(Type.BYTE, 0, yaw);
                  wrapper.set(Type.BYTE, 1, pitch);
                  wrapper.setPacketType(ClientboundPackets1_18.SPAWN_MOB);
               } else if (entityType == EntityTypes1_19.PAINTING) {
                  wrapper.cancel();
                  int entityId = (Integer)wrapper.get(Type.VAR_INT, 0);
                  StoredEntityData entityData = EntityPackets1_19.this.tracker(wrapper.user()).entityData(entityId);
                  Position position = new Position(((Double)wrapper.get(Type.DOUBLE, 0)).intValue(), ((Double)wrapper.get(Type.DOUBLE, 1)).intValue(), ((Double)wrapper.get(Type.DOUBLE, 2)).intValue());
                  entityData.put(new StoredPainting(entityId, (UUID)wrapper.get(Type.UUID, 0), position, data));
               } else {
                  if (entityType == EntityTypes1_19.FALLING_BLOCK) {
                     data = ((Protocol1_18_2To1_19)EntityPackets1_19.this.protocol).getMappingData().getNewBlockStateId(data);
                  }

                  wrapper.write(Type.INT, data);
               }
            });
         }
      });
      ((Protocol1_18_2To1_19)this.protocol).registerClientbound(ClientboundPackets1_19.ENTITY_EFFECT, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.VAR_INT);
            this.map(Type.BYTE);
            this.map(Type.VAR_INT);
            this.map(Type.BYTE);
            this.handler((wrapper) -> {
               if ((Boolean)wrapper.read(Type.BOOLEAN)) {
                  wrapper.read(Type.NAMED_COMPOUND_TAG);
               }

            });
         }
      });
      ((Protocol1_18_2To1_19)this.protocol).registerClientbound(ClientboundPackets1_19.JOIN_GAME, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.BOOLEAN);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.STRING_ARRAY);
            this.map(Type.NAMED_COMPOUND_TAG);
            this.handler((wrapper) -> {
               DimensionRegistryStorage dimensionRegistryStorage = (DimensionRegistryStorage)wrapper.user().get(DimensionRegistryStorage.class);
               dimensionRegistryStorage.clear();
               String dimensionKey = (String)wrapper.read(Type.STRING);
               CompoundTag registry = (CompoundTag)wrapper.get(Type.NAMED_COMPOUND_TAG, 0);
               ListTag dimensions = (ListTag)((CompoundTag)registry.get("minecraft:dimension_type")).get("value");
               boolean found = false;
               Iterator var7 = dimensions.iterator();

               CompoundTag biomeCompound;
               while(var7.hasNext()) {
                  Tag dimension = (Tag)var7.next();
                  CompoundTag dimensionCompound = (CompoundTag)dimension;
                  StringTag nameTag = (StringTag)dimensionCompound.get("name");
                  biomeCompound = (CompoundTag)dimensionCompound.get("element");
                  dimensionRegistryStorage.addDimension(nameTag.getValue(), biomeCompound.copy());
                  if (!found && nameTag.getValue().equals(dimensionKey)) {
                     wrapper.write(Type.NAMED_COMPOUND_TAG, biomeCompound);
                     found = true;
                  }
               }

               if (!found) {
                  throw new IllegalStateException("Could not find dimension " + dimensionKey + " in dimension registry");
               } else {
                  CompoundTag biomeRegistry = (CompoundTag)registry.get("minecraft:worldgen/biome");
                  ListTag biomes = (ListTag)biomeRegistry.get("value");
                  Iterator var16 = biomes.getValue().iterator();

                  while(var16.hasNext()) {
                     Tag biome = (Tag)var16.next();
                     biomeCompound = (CompoundTag)((CompoundTag)biome).get("element");
                     biomeCompound.put("category", new StringTag("none"));
                  }

                  EntityPackets1_19.this.tracker(wrapper.user()).setBiomesSent(biomes.size());
                  ListTag chatTypes = (ListTag)((CompoundTag)registry.remove("minecraft:chat_type")).get("value");
                  Iterator var19 = chatTypes.iterator();

                  while(var19.hasNext()) {
                     Tag chatType = (Tag)var19.next();
                     CompoundTag chatTypeCompound = (CompoundTag)chatType;
                     NumberTag idTag = (NumberTag)chatTypeCompound.get("id");
                     dimensionRegistryStorage.addChatType(idTag.asInt(), chatTypeCompound);
                  }

               }
            });
            this.map(Type.STRING);
            this.map(Type.LONG);
            this.map(Type.VAR_INT);
            this.map(Type.VAR_INT);
            this.map(Type.VAR_INT);
            this.map(Type.BOOLEAN);
            this.map(Type.BOOLEAN);
            this.map(Type.BOOLEAN);
            this.map(Type.BOOLEAN);
            this.read(Type.OPTIONAL_GLOBAL_POSITION);
            this.handler(EntityPackets1_19.this.worldDataTrackerHandler(1));
            this.handler(EntityPackets1_19.this.playerTrackerHandler());
         }
      });
      ((Protocol1_18_2To1_19)this.protocol).registerClientbound(ClientboundPackets1_19.RESPAWN, new PacketHandlers() {
         public void register() {
            this.handler((wrapper) -> {
               String dimensionKey = (String)wrapper.read(Type.STRING);
               CompoundTag dimension = ((DimensionRegistryStorage)wrapper.user().get(DimensionRegistryStorage.class)).dimension(dimensionKey);
               if (dimension == null) {
                  throw new IllegalArgumentException("Could not find dimension " + dimensionKey + " in dimension registry");
               } else {
                  wrapper.write(Type.NAMED_COMPOUND_TAG, dimension);
               }
            });
            this.map(Type.STRING);
            this.map(Type.LONG);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.BYTE);
            this.map(Type.BOOLEAN);
            this.map(Type.BOOLEAN);
            this.map(Type.BOOLEAN);
            this.read(Type.OPTIONAL_GLOBAL_POSITION);
            this.handler(EntityPackets1_19.this.worldDataTrackerHandler(0));
         }
      });
      ((Protocol1_18_2To1_19)this.protocol).registerClientbound(ClientboundPackets1_19.PLAYER_INFO, (wrapper) -> {
         int action = (Integer)wrapper.passthrough(Type.VAR_INT);
         int entries = (Integer)wrapper.passthrough(Type.VAR_INT);

         for(int i = 0; i < entries; ++i) {
            wrapper.passthrough(Type.UUID);
            if (action != 0) {
               if (action != 1 && action != 2) {
                  if (action == 3) {
                     wrapper.passthrough(Type.OPTIONAL_COMPONENT);
                  }
               } else {
                  wrapper.passthrough(Type.VAR_INT);
               }
            } else {
               wrapper.passthrough(Type.STRING);
               int properties = (Integer)wrapper.passthrough(Type.VAR_INT);

               for(int j = 0; j < properties; ++j) {
                  wrapper.passthrough(Type.STRING);
                  wrapper.passthrough(Type.STRING);
                  wrapper.passthrough(Type.OPTIONAL_STRING);
               }

               wrapper.passthrough(Type.VAR_INT);
               wrapper.passthrough(Type.VAR_INT);
               wrapper.passthrough(Type.OPTIONAL_COMPONENT);
               wrapper.read(Type.OPTIONAL_PROFILE_KEY);
            }
         }

      });
   }

   protected void registerRewrites() {
      this.filter().handler((event, meta) -> {
         if (meta.metaType().typeId() <= Types1_18.META_TYPES.poseType.typeId()) {
            meta.setMetaType(Types1_18.META_TYPES.byId(meta.metaType().typeId()));
         }

         MetaType type = meta.metaType();
         if (type == Types1_18.META_TYPES.particleType) {
            Particle particle = (Particle)meta.getValue();
            ParticleMappings particleMappings = ((Protocol1_18_2To1_19)this.protocol).getMappingData().getParticleMappings();
            if (particle.getId() == particleMappings.id("sculk_charge")) {
               event.cancel();
               return;
            }

            if (particle.getId() == particleMappings.id("shriek")) {
               event.cancel();
               return;
            }

            if (particle.getId() == particleMappings.id("vibration")) {
               event.cancel();
               return;
            }

            this.rewriteParticle(particle);
         } else if (type == Types1_18.META_TYPES.poseType) {
            int pose = (Integer)meta.value();
            if (pose >= 8) {
               meta.setValue(0);
            }
         }

      });
      this.registerMetaTypeHandler(Types1_18.META_TYPES.itemType, Types1_18.META_TYPES.blockStateType, (MetaType)null, (MetaType)null, Types1_18.META_TYPES.componentType, Types1_18.META_TYPES.optionalComponentType);
      this.filter().filterFamily(EntityTypes1_19.MINECART_ABSTRACT).index(11).handler((event, meta) -> {
         int data = (Integer)meta.getValue();
         meta.setValue(((Protocol1_18_2To1_19)this.protocol).getMappingData().getNewBlockStateId(data));
      });
      this.filter().type(EntityTypes1_19.PAINTING).index(8).handler((event, meta) -> {
         event.cancel();
         StoredEntityData entityData = this.tracker(event.user()).entityDataIfPresent(event.entityId());
         StoredPainting storedPainting = (StoredPainting)entityData.remove(StoredPainting.class);
         if (storedPainting != null) {
            PacketWrapper packet = PacketWrapper.create(ClientboundPackets1_18.SPAWN_PAINTING, (UserConnection)event.user());
            packet.write(Type.VAR_INT, storedPainting.entityId());
            packet.write(Type.UUID, storedPainting.uuid());
            packet.write(Type.VAR_INT, (Integer)meta.value());
            packet.write(Type.POSITION1_14, storedPainting.position());
            packet.write(Type.BYTE, storedPainting.direction());

            try {
               packet.send(Protocol1_18_2To1_19.class);
            } catch (Exception var7) {
               throw new RuntimeException(var7);
            }
         }

      });
      this.filter().type(EntityTypes1_19.CAT).index(19).handler((event, meta) -> {
         meta.setMetaType(Types1_18.META_TYPES.varIntType);
      });
      this.filter().type(EntityTypes1_19.FROG).cancel(16);
      this.filter().type(EntityTypes1_19.FROG).cancel(17);
      this.filter().type(EntityTypes1_19.FROG).cancel(18);
      this.filter().type(EntityTypes1_19.WARDEN).cancel(16);
      this.filter().type(EntityTypes1_19.GOAT).cancel(18);
      this.filter().type(EntityTypes1_19.GOAT).cancel(19);
   }

   public void onMappingDataLoaded() {
      this.mapTypes();
      this.mapEntityTypeWithData(EntityTypes1_19.FROG, EntityTypes1_19.RABBIT).jsonName();
      this.mapEntityTypeWithData(EntityTypes1_19.TADPOLE, EntityTypes1_19.PUFFERFISH).jsonName();
      this.mapEntityTypeWithData(EntityTypes1_19.CHEST_BOAT, EntityTypes1_19.BOAT);
      this.mapEntityTypeWithData(EntityTypes1_19.WARDEN, EntityTypes1_19.IRON_GOLEM).jsonName();
      this.mapEntityTypeWithData(EntityTypes1_19.ALLAY, EntityTypes1_19.VEX).jsonName();
   }

   public EntityType typeFromId(int typeId) {
      return EntityTypes1_19.getTypeFromId(typeId);
   }
}
