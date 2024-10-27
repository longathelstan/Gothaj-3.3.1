package com.viaversion.viaversion.protocols.protocol1_20_2to1_20.rewriter;

import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_19_4;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_20;
import com.viaversion.viaversion.api.type.types.version.Types1_20_2;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.ClientboundPackets1_19_4;
import com.viaversion.viaversion.protocols.protocol1_20_2to1_20.Protocol1_20_2To1_20;
import com.viaversion.viaversion.protocols.protocol1_20_2to1_20.packet.ClientboundPackets1_20_2;
import com.viaversion.viaversion.protocols.protocol1_20_2to1_20.storage.ConfigurationState;
import com.viaversion.viaversion.protocols.protocol1_20_2to1_20.storage.LastResourcePack;
import com.viaversion.viaversion.rewriter.EntityRewriter;

public final class EntityPacketRewriter1_20_2 extends EntityRewriter<ClientboundPackets1_19_4, Protocol1_20_2To1_20> {
   public EntityPacketRewriter1_20_2(Protocol1_20_2To1_20 protocol) {
      super(protocol);
   }

   public void registerPackets() {
      this.registerTrackerWithData1_19(ClientboundPackets1_19_4.SPAWN_ENTITY, EntityTypes1_19_4.FALLING_BLOCK);
      this.registerMetadataRewriter(ClientboundPackets1_19_4.ENTITY_METADATA, Types1_20.METADATA_LIST, Types1_20_2.METADATA_LIST);
      this.registerRemoveEntities(ClientboundPackets1_19_4.REMOVE_ENTITIES);
      ((Protocol1_20_2To1_20)this.protocol).registerClientbound(ClientboundPackets1_19_4.SPAWN_PLAYER, ClientboundPackets1_20_2.SPAWN_ENTITY, (wrapper) -> {
         wrapper.passthrough(Type.VAR_INT);
         wrapper.passthrough(Type.UUID);
         wrapper.write(Type.VAR_INT, EntityTypes1_19_4.PLAYER.getId());
         wrapper.passthrough(Type.DOUBLE);
         wrapper.passthrough(Type.DOUBLE);
         wrapper.passthrough(Type.DOUBLE);
         byte yaw = (Byte)wrapper.read(Type.BYTE);
         wrapper.passthrough(Type.BYTE);
         wrapper.write(Type.BYTE, yaw);
         wrapper.write(Type.BYTE, yaw);
         wrapper.write(Type.VAR_INT, 0);
         wrapper.write(Type.SHORT, Short.valueOf((short)0));
         wrapper.write(Type.SHORT, Short.valueOf((short)0));
         wrapper.write(Type.SHORT, Short.valueOf((short)0));
      });
      ((Protocol1_20_2To1_20)this.protocol).registerClientbound(ClientboundPackets1_19_4.JOIN_GAME, new PacketHandlers() {
         public void register() {
            this.handler((wrapper) -> {
               wrapper.passthrough(Type.INT);
               wrapper.passthrough(Type.BOOLEAN);
               byte gamemode = (Byte)wrapper.read(Type.BYTE);
               byte previousGamemode = (Byte)wrapper.read(Type.BYTE);
               wrapper.passthrough(Type.STRING_ARRAY);
               CompoundTag dimensionRegistry = (CompoundTag)wrapper.read(Type.NAMED_COMPOUND_TAG);
               String dimensionType = (String)wrapper.read(Type.STRING);
               String world = (String)wrapper.read(Type.STRING);
               long seed = (Long)wrapper.read(Type.LONG);
               EntityPacketRewriter1_20_2.this.trackBiomeSize(wrapper.user(), dimensionRegistry);
               EntityPacketRewriter1_20_2.this.cacheDimensionData(wrapper.user(), dimensionRegistry);
               wrapper.passthrough(Type.VAR_INT);
               wrapper.passthrough(Type.VAR_INT);
               wrapper.passthrough(Type.VAR_INT);
               wrapper.passthrough(Type.BOOLEAN);
               wrapper.passthrough(Type.BOOLEAN);
               wrapper.write(Type.BOOLEAN, false);
               wrapper.write(Type.STRING, dimensionType);
               wrapper.write(Type.STRING, world);
               wrapper.write(Type.LONG, seed);
               wrapper.write(Type.BYTE, gamemode);
               wrapper.write(Type.BYTE, previousGamemode);
               ConfigurationState configurationBridge = (ConfigurationState)wrapper.user().get(ConfigurationState.class);
               PacketWrapper configurationPacket;
               if (!configurationBridge.setLastDimensionRegistry(dimensionRegistry)) {
                  configurationPacket = configurationBridge.clientInformationPacket(wrapper.user());
                  if (configurationPacket != null) {
                     configurationPacket.scheduleSendToServer(Protocol1_20_2To1_20.class);
                  }

               } else if (configurationBridge.bridgePhase() == ConfigurationState.BridgePhase.NONE) {
                  configurationPacket = wrapper.create(ClientboundPackets1_20_2.START_CONFIGURATION);
                  configurationPacket.send(Protocol1_20_2To1_20.class);
                  configurationBridge.setBridgePhase(ConfigurationState.BridgePhase.REENTERING_CONFIGURATION);
                  configurationBridge.setJoinGamePacket(wrapper);
                  wrapper.cancel();
               } else {
                  configurationBridge.setJoinGamePacket(wrapper);
                  wrapper.cancel();
                  Protocol1_20_2To1_20.sendConfigurationPackets(wrapper.user(), dimensionRegistry, (LastResourcePack)null);
               }
            });
            this.handler(EntityPacketRewriter1_20_2.this.worldDataTrackerHandlerByKey());
         }
      });
      ((Protocol1_20_2To1_20)this.protocol).registerClientbound(ClientboundPackets1_19_4.RESPAWN, new PacketHandlers() {
         public void register() {
            this.handler((wrapper) -> {
               wrapper.passthrough(Type.STRING);
               wrapper.passthrough(Type.STRING);
               wrapper.passthrough(Type.LONG);
               wrapper.write(Type.BYTE, ((Short)wrapper.read(Type.UNSIGNED_BYTE)).byteValue());
               wrapper.passthrough(Type.BYTE);
               wrapper.passthrough(Type.BOOLEAN);
               wrapper.passthrough(Type.BOOLEAN);
               byte dataToKeep = (Byte)wrapper.read(Type.BYTE);
               wrapper.passthrough(Type.OPTIONAL_GLOBAL_POSITION);
               wrapper.passthrough(Type.VAR_INT);
               wrapper.write(Type.BYTE, dataToKeep);
            });
            this.handler(EntityPacketRewriter1_20_2.this.worldDataTrackerHandlerByKey());
         }
      });
      ((Protocol1_20_2To1_20)this.protocol).registerClientbound(ClientboundPackets1_19_4.ENTITY_EFFECT, (wrapper) -> {
         wrapper.passthrough(Type.VAR_INT);
         wrapper.write(Type.VAR_INT, (Integer)wrapper.read(Type.VAR_INT) - 1);
         wrapper.passthrough(Type.BYTE);
         wrapper.passthrough(Type.VAR_INT);
         wrapper.passthrough(Type.BYTE);
         if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
            wrapper.write(Type.COMPOUND_TAG, wrapper.read(Type.NAMED_COMPOUND_TAG));
         }

      });
      ((Protocol1_20_2To1_20)this.protocol).registerClientbound(ClientboundPackets1_19_4.REMOVE_ENTITY_EFFECT, (wrapper) -> {
         wrapper.passthrough(Type.VAR_INT);
         wrapper.write(Type.VAR_INT, (Integer)wrapper.read(Type.VAR_INT) - 1);
      });
   }

   protected void registerRewrites() {
      this.filter().handler((event, meta) -> {
         meta.setMetaType(Types1_20_2.META_TYPES.byId(meta.metaType().typeId()));
      });
      this.registerMetaTypeHandler(Types1_20_2.META_TYPES.itemType, Types1_20_2.META_TYPES.blockStateType, Types1_20_2.META_TYPES.optionalBlockStateType, Types1_20_2.META_TYPES.particleType);
      this.filter().filterFamily(EntityTypes1_19_4.DISPLAY).addIndex(10);
      this.filter().filterFamily(EntityTypes1_19_4.MINECART_ABSTRACT).index(11).handler((event, meta) -> {
         int blockState = (Integer)meta.value();
         meta.setValue(((Protocol1_20_2To1_20)this.protocol).getMappingData().getNewBlockStateId(blockState));
      });
   }

   public EntityType typeFromId(int type) {
      return EntityTypes1_19_4.getTypeFromId(type);
   }
}