package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.ClientWorld;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_13;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_12;
import com.viaversion.viaversion.api.type.types.version.Types1_13;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.metadata.MetadataRewriter1_13To1_12_2;

public class EntityPackets {
   public static void register(Protocol1_13To1_12_2 protocol) {
      final MetadataRewriter1_13To1_12_2 metadataRewriter = (MetadataRewriter1_13To1_12_2)protocol.get(MetadataRewriter1_13To1_12_2.class);
      protocol.registerClientbound(ClientboundPackets1_12_1.SPAWN_ENTITY, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.UUID);
            this.map(Type.BYTE);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.INT);
            this.handler((wrapper) -> {
               int entityId = (Integer)wrapper.get(Type.VAR_INT, 0);
               byte type = (Byte)wrapper.get(Type.BYTE, 0);
               EntityTypes1_13.EntityType entType = EntityTypes1_13.getTypeFromId(type, true);
               if (entType != null) {
                  wrapper.user().getEntityTracker(Protocol1_13To1_12_2.class).addEntity(entityId, entType);
                  int data;
                  if (entType.is(EntityTypes1_13.EntityType.FALLING_BLOCK)) {
                     data = (Integer)wrapper.get(Type.INT, 0);
                     int combined = (data & 4095) << 4 | data >> 12 & 15;
                     wrapper.set(Type.INT, 0, WorldPackets.toNewId(combined));
                  }

                  if (entType.is(EntityTypes1_13.EntityType.ITEM_FRAME)) {
                     data = (Integer)wrapper.get(Type.INT, 0);
                     switch(data) {
                     case 0:
                        data = 3;
                        break;
                     case 1:
                        data = 4;
                     case 2:
                     default:
                        break;
                     case 3:
                        data = 5;
                     }

                     wrapper.set(Type.INT, 0, data);
                  }

               }
            });
         }
      });
      protocol.registerClientbound(ClientboundPackets1_12_1.SPAWN_MOB, new PacketHandlers() {
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
            this.map(Type.SHORT);
            this.map(Type.SHORT);
            this.map(Type.SHORT);
            this.map(Types1_12.METADATA_LIST, Types1_13.METADATA_LIST);
            this.handler(metadataRewriter.trackerAndRewriterHandler(Types1_13.METADATA_LIST));
         }
      });
      protocol.registerClientbound(ClientboundPackets1_12_1.SPAWN_PLAYER, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.UUID);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Types1_12.METADATA_LIST, Types1_13.METADATA_LIST);
            this.handler(metadataRewriter.trackerAndRewriterHandler(Types1_13.METADATA_LIST, EntityTypes1_13.EntityType.PLAYER));
         }
      });
      protocol.registerClientbound(ClientboundPackets1_12_1.JOIN_GAME, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.INT);
            this.handler((wrapper) -> {
               ClientWorld clientChunks = (ClientWorld)wrapper.user().get(ClientWorld.class);
               int dimensionId = (Integer)wrapper.get(Type.INT, 1);
               clientChunks.setEnvironment(dimensionId);
            });
            this.handler(metadataRewriter.playerTrackerHandler());
            this.handler(Protocol1_13To1_12_2.SEND_DECLARE_COMMANDS_AND_TAGS);
         }
      });
      protocol.registerClientbound(ClientboundPackets1_12_1.ENTITY_EFFECT, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.VAR_INT);
            this.handler((packetWrapper) -> {
               byte flags = (Byte)packetWrapper.read(Type.BYTE);
               if (Via.getConfig().isNewEffectIndicator()) {
                  flags = (byte)(flags | 4);
               }

               packetWrapper.write(Type.BYTE, flags);
            });
         }
      });
      metadataRewriter.registerRemoveEntities(ClientboundPackets1_12_1.DESTROY_ENTITIES);
      metadataRewriter.registerMetadataRewriter(ClientboundPackets1_12_1.ENTITY_METADATA, Types1_12.METADATA_LIST, Types1_13.METADATA_LIST);
   }
}
