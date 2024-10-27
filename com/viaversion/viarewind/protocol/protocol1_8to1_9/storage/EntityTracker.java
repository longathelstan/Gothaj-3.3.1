package com.viaversion.viarewind.protocol.protocol1_8to1_9.storage;

import com.viaversion.viarewind.api.minecraft.EntityModel;
import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.ClientEntityIdChangeListener;
import com.viaversion.viaversion.api.minecraft.Vector;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_10;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class EntityTracker extends StoredObject implements ClientEntityIdChangeListener {
   protected final Protocol1_8To1_9 protocol;
   private final Map<Integer, List<Integer>> vehicleMap = new ConcurrentHashMap();
   private final Map<Integer, EntityTypes1_10.EntityType> clientEntityTypes = new ConcurrentHashMap();
   private final Map<Integer, List<Metadata>> metadataBuffer = new ConcurrentHashMap();
   private final Map<Integer, EntityModel> entityReplacements = new ConcurrentHashMap();
   private final Map<Integer, Vector> entityOffsets = new ConcurrentHashMap();
   private final Map<Integer, Byte> statusInformation = new ConcurrentHashMap();
   private int playerId;
   private int playerGamemode = 0;

   public EntityTracker(UserConnection user, Protocol1_8To1_9 protocol) {
      super(user);
      this.protocol = protocol;
   }

   public void setPlayerId(int entityId) {
      this.playerId = entityId;
   }

   public int getPlayerId() {
      return this.playerId;
   }

   public int getPlayerGamemode() {
      return this.playerGamemode;
   }

   public void setPlayerGamemode(int playerGamemode) {
      this.playerGamemode = playerGamemode;
   }

   public void removeEntity(int entityId) {
      this.vehicleMap.remove(entityId);
      this.vehicleMap.forEach((vehicle, passengers) -> {
         passengers.remove(entityId);
      });
      this.vehicleMap.entrySet().removeIf((entry) -> {
         return ((List)entry.getValue()).isEmpty();
      });
      this.clientEntityTypes.remove(entityId);
      this.entityOffsets.remove(entityId);
      if (this.entityReplacements.containsKey(entityId)) {
         ((EntityModel)this.entityReplacements.remove(entityId)).deleteEntity();
      }

   }

   public void resetEntityOffset(int entityId) {
      this.entityOffsets.remove(entityId);
   }

   public Vector getEntityOffset(int entityId) {
      return (Vector)this.entityOffsets.get(entityId);
   }

   public void addToEntityOffset(int entityId, short relX, short relY, short relZ) {
      this.entityOffsets.compute(entityId, (key, offset) -> {
         return offset == null ? new Vector(relX, relY, relZ) : new Vector(offset.blockX() + relX, offset.blockY() + relY, offset.blockZ() + relZ);
      });
   }

   public void setEntityOffset(int entityId, short relX, short relY, short relZ) {
      this.entityOffsets.compute(entityId, (key, offset) -> {
         return new Vector(relX, relY, relZ);
      });
   }

   public void setEntityOffset(int entityId, Vector offset) {
      this.entityOffsets.put(entityId, offset);
   }

   public List<Integer> getPassengers(int entityId) {
      return (List)this.vehicleMap.getOrDefault(entityId, new ArrayList());
   }

   public void setPassengers(int entityId, List<Integer> passengers) {
      this.vehicleMap.put(entityId, passengers);
   }

   public void addEntityReplacement(EntityModel entityModel) {
      this.entityReplacements.put(entityModel.getEntityId(), entityModel);
   }

   public EntityModel getEntityReplacement(int entityId) {
      return (EntityModel)this.entityReplacements.get(entityId);
   }

   public Map<Integer, EntityTypes1_10.EntityType> getClientEntityTypes() {
      return this.clientEntityTypes;
   }

   public void addMetadataToBuffer(int entityID, List<Metadata> metadataList) {
      if (this.metadataBuffer.containsKey(entityID)) {
         ((List)this.metadataBuffer.get(entityID)).addAll(metadataList);
      } else if (!metadataList.isEmpty()) {
         this.metadataBuffer.put(entityID, metadataList);
      }

   }

   public List<Metadata> getBufferedMetadata(int entityId) {
      return (List)this.metadataBuffer.get(entityId);
   }

   public boolean isInsideVehicle(int entityId) {
      Iterator var2 = this.vehicleMap.values().iterator();

      List vehicle;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         vehicle = (List)var2.next();
      } while(!vehicle.contains(entityId));

      return true;
   }

   public int getVehicle(int passenger) {
      Iterator var2 = this.vehicleMap.entrySet().iterator();

      Entry vehicle;
      do {
         if (!var2.hasNext()) {
            return -1;
         }

         vehicle = (Entry)var2.next();
      } while(!((List)vehicle.getValue()).contains(passenger));

      return (Integer)vehicle.getKey();
   }

   public boolean isPassenger(int vehicle, int passenger) {
      return this.vehicleMap.containsKey(vehicle) && ((List)this.vehicleMap.get(vehicle)).contains(passenger);
   }

   public void sendMetadataBuffer(int entityId) {
      if (this.metadataBuffer.containsKey(entityId)) {
         if (this.entityReplacements.containsKey(entityId)) {
            ((EntityModel)this.entityReplacements.get(entityId)).updateMetadata((List)this.metadataBuffer.remove(entityId));
         } else {
            PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_8.ENTITY_METADATA, (UserConnection)this.getUser());
            wrapper.write(Type.VAR_INT, entityId);
            wrapper.write(Types1_8.METADATA_LIST, (List)this.metadataBuffer.get(entityId));
            this.protocol.getMetadataRewriter().transform(this, entityId, (List)this.metadataBuffer.get(entityId));
            if (!((List)this.metadataBuffer.get(entityId)).isEmpty()) {
               try {
                  wrapper.send(Protocol1_8To1_9.class);
               } catch (Exception var4) {
                  var4.printStackTrace();
               }
            }

            this.metadataBuffer.remove(entityId);
         }

      }
   }

   public void setClientEntityId(int playerEntityId) {
      this.clientEntityTypes.remove(this.playerId);
      this.playerId = playerEntityId;
      this.clientEntityTypes.put(this.playerId, EntityTypes1_10.EntityType.ENTITY_HUMAN);
   }

   public Map<Integer, Byte> getStatusInformation() {
      return this.statusInformation;
   }
}
