package com.viaversion.viaversion.data.entity;

import com.viaversion.viaversion.api.data.entity.StoredEntityData;
import com.viaversion.viaversion.api.data.entity.TrackedEntity;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;

public final class TrackedEntityImpl implements TrackedEntity {
   private final EntityType entityType;
   private StoredEntityData data;
   private boolean sentMetadata;

   public TrackedEntityImpl(EntityType entityType) {
      this.entityType = entityType;
   }

   public EntityType entityType() {
      return this.entityType;
   }

   public StoredEntityData data() {
      if (this.data == null) {
         this.data = new StoredEntityDataImpl(this.entityType);
      }

      return this.data;
   }

   public boolean hasData() {
      return this.data != null;
   }

   public boolean hasSentMetadata() {
      return this.sentMetadata;
   }

   public void sentMetadata(boolean sentMetadata) {
      this.sentMetadata = sentMetadata;
   }
}
