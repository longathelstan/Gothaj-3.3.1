package com.viaversion.viaversion.protocols.protocol1_13_1to1_13.metadata;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Particle;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_13;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.types.version.Types1_13;
import com.viaversion.viaversion.protocols.protocol1_13_1to1_13.Protocol1_13_1To1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import java.util.List;

public class MetadataRewriter1_13_1To1_13 extends EntityRewriter<ClientboundPackets1_13, Protocol1_13_1To1_13> {
   public MetadataRewriter1_13_1To1_13(Protocol1_13_1To1_13 protocol) {
      super(protocol);
   }

   protected void handleMetadata(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) {
      int data;
      if (metadata.metaType() == Types1_13.META_TYPES.itemType) {
         ((Protocol1_13_1To1_13)this.protocol).getItemRewriter().handleItemToClient((Item)metadata.getValue());
      } else if (metadata.metaType() == Types1_13.META_TYPES.blockStateType) {
         data = (Integer)metadata.getValue();
         metadata.setValue(((Protocol1_13_1To1_13)this.protocol).getMappingData().getNewBlockStateId(data));
      } else if (metadata.metaType() == Types1_13.META_TYPES.particleType) {
         this.rewriteParticle((Particle)metadata.getValue());
      }

      if (type != null) {
         if (type.isOrHasParent(EntityTypes1_13.EntityType.MINECART_ABSTRACT) && metadata.id() == 9) {
            data = (Integer)metadata.getValue();
            metadata.setValue(((Protocol1_13_1To1_13)this.protocol).getMappingData().getNewBlockStateId(data));
         } else if (type.isOrHasParent(EntityTypes1_13.EntityType.ABSTRACT_ARROW) && metadata.id() >= 7) {
            metadata.setId(metadata.id() + 1);
         }

      }
   }

   public EntityType typeFromId(int type) {
      return EntityTypes1_13.getTypeFromId(type, false);
   }

   public EntityType objectTypeFromId(int type) {
      return EntityTypes1_13.getTypeFromId(type, true);
   }
}
