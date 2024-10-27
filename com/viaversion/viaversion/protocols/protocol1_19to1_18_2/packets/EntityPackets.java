package com.viaversion.viaversion.protocols.protocol1_19to1_18_2.packets;

import com.google.common.collect.Maps;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.ParticleMappings;
import com.viaversion.viaversion.api.data.entity.DimensionData;
import com.viaversion.viaversion.api.minecraft.Particle;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_19;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_18;
import com.viaversion.viaversion.api.type.types.version.Types1_19;
import com.viaversion.viaversion.data.entity.DimensionDataImpl;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.opennbt.stringified.SNBT;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.ClientboundPackets1_18;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.ClientboundPackets1_19;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.Protocol1_19To1_18_2;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.storage.DimensionRegistryStorage;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import com.viaversion.viaversion.util.Key;
import com.viaversion.viaversion.util.Pair;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class EntityPackets extends EntityRewriter<ClientboundPackets1_18, Protocol1_19To1_18_2> {
   private static final String CHAT_REGISTRY_SNBT = "{\n  \"minecraft:chat_type\": {\n    \"type\": \"minecraft:chat_type\",\n    \"value\": [\n      {\n        \"name\": \"minecraft:system\",\n        \"id\": 1,\n        \"element\": {\n          \"chat\": {},\n          \"narration\": {\n            \"priority\": \"system\"\n          }\n        }\n      },\n      {\n        \"name\": \"minecraft:game_info\",\n        \"id\": 2,\n        \"element\": {\n          \"overlay\": {}\n        }\n      }\n    ]\n  }\n}";
   public static final CompoundTag CHAT_REGISTRY = (CompoundTag)SNBT.deserializeCompoundTag("{\n  \"minecraft:chat_type\": {\n    \"type\": \"minecraft:chat_type\",\n    \"value\": [\n      {\n        \"name\": \"minecraft:system\",\n        \"id\": 1,\n        \"element\": {\n          \"chat\": {},\n          \"narration\": {\n            \"priority\": \"system\"\n          }\n        }\n      },\n      {\n        \"name\": \"minecraft:game_info\",\n        \"id\": 2,\n        \"element\": {\n          \"overlay\": {}\n        }\n      }\n    ]\n  }\n}").get("minecraft:chat_type");

   public EntityPackets(Protocol1_19To1_18_2 protocol) {
      super(protocol);
   }

   public void registerPackets() {
      this.registerTracker(ClientboundPackets1_18.SPAWN_PLAYER, EntityTypes1_19.PLAYER);
      this.registerMetadataRewriter(ClientboundPackets1_18.ENTITY_METADATA, Types1_18.METADATA_LIST, Types1_19.METADATA_LIST);
      this.registerRemoveEntities(ClientboundPackets1_18.REMOVE_ENTITIES);
      ((Protocol1_19To1_18_2)this.protocol).registerClientbound(ClientboundPackets1_18.SPAWN_ENTITY, new PacketHandlers() {
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
               byte yaw = (Byte)wrapper.get(Type.BYTE, 1);
               wrapper.write(Type.BYTE, yaw);
            });
            this.map(Type.INT, Type.VAR_INT);
            this.handler(EntityPackets.this.trackerHandler());
            this.handler((wrapper) -> {
               int entityId = (Integer)wrapper.get(Type.VAR_INT, 0);
               EntityType entityType = EntityPackets.this.tracker(wrapper.user()).entityType(entityId);
               if (entityType == EntityTypes1_19.FALLING_BLOCK) {
                  wrapper.set(Type.VAR_INT, 2, ((Protocol1_19To1_18_2)EntityPackets.this.protocol).getMappingData().getNewBlockStateId((Integer)wrapper.get(Type.VAR_INT, 2)));
               }

            });
         }
      });
      ((Protocol1_19To1_18_2)this.protocol).registerClientbound(ClientboundPackets1_18.SPAWN_PAINTING, ClientboundPackets1_19.SPAWN_ENTITY, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.UUID);
            this.handler((wrapper) -> {
               wrapper.write(Type.VAR_INT, EntityTypes1_19.PAINTING.getId());
               int motive = (Integer)wrapper.read(Type.VAR_INT);
               Position blockPosition = (Position)wrapper.read(Type.POSITION1_14);
               byte direction = (Byte)wrapper.read(Type.BYTE);
               wrapper.write(Type.DOUBLE, (double)blockPosition.x() + 0.5D);
               wrapper.write(Type.DOUBLE, (double)blockPosition.y() + 0.5D);
               wrapper.write(Type.DOUBLE, (double)blockPosition.z() + 0.5D);
               wrapper.write(Type.BYTE, (byte)0);
               wrapper.write(Type.BYTE, (byte)0);
               wrapper.write(Type.BYTE, (byte)0);
               wrapper.write(Type.VAR_INT, EntityPackets.to3dId(direction));
               wrapper.write(Type.SHORT, Short.valueOf((short)0));
               wrapper.write(Type.SHORT, Short.valueOf((short)0));
               wrapper.write(Type.SHORT, Short.valueOf((short)0));
               wrapper.send(Protocol1_19To1_18_2.class);
               wrapper.cancel();
               PacketWrapper metaPacket = wrapper.create(ClientboundPackets1_19.ENTITY_METADATA);
               metaPacket.write(Type.VAR_INT, wrapper.get(Type.VAR_INT, 0));
               List<Metadata> metadata = new ArrayList();
               metadata.add(new Metadata(8, Types1_19.META_TYPES.paintingVariantType, ((Protocol1_19To1_18_2)EntityPackets.this.protocol).getMappingData().getPaintingMappings().getNewIdOrDefault(motive, 0)));
               metaPacket.write(Types1_19.METADATA_LIST, metadata);
               metaPacket.send(Protocol1_19To1_18_2.class);
            });
         }
      });
      ((Protocol1_19To1_18_2)this.protocol).registerClientbound(ClientboundPackets1_18.SPAWN_MOB, ClientboundPackets1_19.SPAWN_ENTITY, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.UUID);
            this.map(Type.VAR_INT);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.handler((wrapper) -> {
               byte yaw = (Byte)wrapper.read(Type.BYTE);
               byte pitch = (Byte)wrapper.read(Type.BYTE);
               wrapper.write(Type.BYTE, pitch);
               wrapper.write(Type.BYTE, yaw);
            });
            this.map(Type.BYTE);
            this.create(Type.VAR_INT, 0);
            this.map(Type.SHORT);
            this.map(Type.SHORT);
            this.map(Type.SHORT);
            this.handler(EntityPackets.this.trackerHandler());
         }
      });
      ((Protocol1_19To1_18_2)this.protocol).registerClientbound(ClientboundPackets1_18.ENTITY_EFFECT, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.VAR_INT);
            this.map(Type.BYTE);
            this.map(Type.VAR_INT);
            this.map(Type.BYTE);
            this.create(Type.BOOLEAN, false);
         }
      });
      ((Protocol1_19To1_18_2)this.protocol).registerClientbound(ClientboundPackets1_18.JOIN_GAME, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.BOOLEAN);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.STRING_ARRAY);
            this.map(Type.NAMED_COMPOUND_TAG);
            this.handler((wrapper) -> {
               CompoundTag tag = (CompoundTag)wrapper.get(Type.NAMED_COMPOUND_TAG, 0);
               tag.put("minecraft:chat_type", EntityPackets.CHAT_REGISTRY.copy());
               ListTag dimensions = (ListTag)((CompoundTag)tag.get("minecraft:dimension_type")).get("value");
               Map<String, DimensionData> dimensionDataMap = new HashMap(dimensions.size());
               Map<CompoundTag, String> dimensionsMap = new HashMap(dimensions.size());
               Iterator var6 = dimensions.iterator();

               while(var6.hasNext()) {
                  Tag dimension = (Tag)var6.next();
                  CompoundTag dimensionCompound = (CompoundTag)dimension;
                  CompoundTag element = (CompoundTag)dimensionCompound.get("element");
                  String name = (String)dimensionCompound.get("name").getValue();
                  EntityPackets.addMonsterSpawnData(element);
                  dimensionDataMap.put(name, new DimensionDataImpl(element));
                  dimensionsMap.put(element.copy(), name);
               }

               EntityPackets.this.tracker(wrapper.user()).setDimensions(dimensionDataMap);
               DimensionRegistryStorage registryStorage = (DimensionRegistryStorage)wrapper.user().get(DimensionRegistryStorage.class);
               registryStorage.setDimensions(dimensionsMap);
               EntityPackets.writeDimensionKey(wrapper, registryStorage);
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
            this.create(Type.OPTIONAL_GLOBAL_POSITION, (Object)null);
            this.handler(EntityPackets.this.playerTrackerHandler());
            this.handler(EntityPackets.this.worldDataTrackerHandlerByKey());
            this.handler(EntityPackets.this.biomeSizeTracker());
            this.handler((wrapper) -> {
               PacketWrapper displayPreviewPacket = wrapper.create(ClientboundPackets1_19.SET_DISPLAY_CHAT_PREVIEW);
               displayPreviewPacket.write(Type.BOOLEAN, false);
               displayPreviewPacket.scheduleSend(Protocol1_19To1_18_2.class);
            });
         }
      });
      ((Protocol1_19To1_18_2)this.protocol).registerClientbound(ClientboundPackets1_18.RESPAWN, new PacketHandlers() {
         public void register() {
            this.handler((wrapper) -> {
               EntityPackets.writeDimensionKey(wrapper, (DimensionRegistryStorage)wrapper.user().get(DimensionRegistryStorage.class));
            });
            this.map(Type.STRING);
            this.map(Type.LONG);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.BYTE);
            this.map(Type.BOOLEAN);
            this.map(Type.BOOLEAN);
            this.map(Type.BOOLEAN);
            this.create(Type.OPTIONAL_GLOBAL_POSITION, (Object)null);
            this.handler(EntityPackets.this.worldDataTrackerHandlerByKey());
         }
      });
      ((Protocol1_19To1_18_2)this.protocol).registerClientbound(ClientboundPackets1_18.PLAYER_INFO, (wrapper) -> {
         int action = (Integer)wrapper.passthrough(Type.VAR_INT);
         int entries = (Integer)wrapper.passthrough(Type.VAR_INT);

         for(int i = 0; i < entries; ++i) {
            wrapper.passthrough(Type.UUID);
            if (action != 0) {
               if (action != 1 && action != 2) {
                  if (action == 3) {
                     JsonElement displayName = (JsonElement)wrapper.read(Type.OPTIONAL_COMPONENT);
                     if (!Protocol1_19To1_18_2.isTextComponentNull(displayName)) {
                        wrapper.write(Type.OPTIONAL_COMPONENT, displayName);
                     } else {
                        wrapper.write(Type.OPTIONAL_COMPONENT, (Object)null);
                     }
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
               JsonElement displayNamex = (JsonElement)wrapper.read(Type.OPTIONAL_COMPONENT);
               if (!Protocol1_19To1_18_2.isTextComponentNull(displayNamex)) {
                  wrapper.write(Type.OPTIONAL_COMPONENT, displayNamex);
               } else {
                  wrapper.write(Type.OPTIONAL_COMPONENT, (Object)null);
               }

               wrapper.write(Type.OPTIONAL_PROFILE_KEY, (Object)null);
            }
         }

      });
   }

   private static void writeDimensionKey(PacketWrapper wrapper, DimensionRegistryStorage registryStorage) throws Exception {
      CompoundTag currentDimension = (CompoundTag)wrapper.read(Type.NAMED_COMPOUND_TAG);
      addMonsterSpawnData(currentDimension);
      String dimensionKey = registryStorage.dimensionKey(currentDimension);
      if (dimensionKey == null) {
         if (!Via.getConfig().isSuppressConversionWarnings()) {
            Via.getPlatform().getLogger().warning("The server tried to send dimension data from a dimension the client wasn't told about on join. Plugins and mods have to make sure they are not creating new dimension types while players are online, and proxies need to make sure they don't scramble dimension data. Received dimension: " + currentDimension + ". Known dimensions: " + registryStorage.dimensions());
         }

         dimensionKey = (String)((Entry)((Pair)registryStorage.dimensions().entrySet().stream().map((it) -> {
            return new Pair(it, Maps.difference(currentDimension.getValue(), ((CompoundTag)it.getKey()).getValue()).entriesInCommon());
         }).filter((it) -> {
            return ((Map)it.value()).containsKey("min_y") && ((Map)it.value()).containsKey("height");
         }).max(Comparator.comparingInt((it) -> {
            return ((Map)it.value()).size();
         })).orElseThrow(() -> {
            return new IllegalArgumentException("Dimension not found in registry data from join packet: " + currentDimension);
         })).key()).getValue();
      }

      wrapper.write(Type.STRING, dimensionKey);
   }

   private static int to3dId(int id) {
      switch(id) {
      case -1:
         return 1;
      case 0:
         return 3;
      case 1:
         return 4;
      case 2:
         return 2;
      case 3:
         return 5;
      default:
         throw new IllegalArgumentException("Unknown 2d id: " + id);
      }
   }

   private static void addMonsterSpawnData(CompoundTag dimension) {
      dimension.put("monster_spawn_block_light_limit", new IntTag(0));
      dimension.put("monster_spawn_light_level", new IntTag(11));
   }

   protected void registerRewrites() {
      this.filter().handler((event, meta) -> {
         meta.setMetaType(Types1_19.META_TYPES.byId(meta.metaType().typeId()));
         MetaType type = meta.metaType();
         if (type == Types1_19.META_TYPES.particleType) {
            Particle particle = (Particle)meta.getValue();
            ParticleMappings particleMappings = ((Protocol1_19To1_18_2)this.protocol).getMappingData().getParticleMappings();
            if (particle.getId() == particleMappings.id("vibration")) {
               particle.getArguments().remove(0);
               String resourceLocation = Key.stripMinecraftNamespace((String)particle.getArgument(0).getValue());
               if (resourceLocation.equals("entity")) {
                  particle.getArguments().add(2, new Particle.ParticleData(Type.FLOAT, 0.0F));
               }
            }

            this.rewriteParticle(particle);
         }

      });
      this.registerMetaTypeHandler(Types1_19.META_TYPES.itemType, Types1_19.META_TYPES.blockStateType, (MetaType)null, (MetaType)null);
      this.filter().filterFamily(EntityTypes1_19.MINECART_ABSTRACT).index(11).handler((event, meta) -> {
         int data = (Integer)meta.getValue();
         meta.setValue(((Protocol1_19To1_18_2)this.protocol).getMappingData().getNewBlockStateId(data));
      });
      this.filter().type(EntityTypes1_19.CAT).index(19).handler((event, meta) -> {
         meta.setMetaType(Types1_19.META_TYPES.catVariantType);
      });
   }

   public void onMappingDataLoaded() {
      this.mapTypes();
   }

   public EntityType typeFromId(int type) {
      return EntityTypes1_19.getTypeFromId(type);
   }
}
