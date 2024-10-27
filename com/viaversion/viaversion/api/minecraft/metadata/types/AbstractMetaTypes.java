package com.viaversion.viaversion.api.minecraft.metadata.types;

import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.type.Type;

public abstract class AbstractMetaTypes implements MetaTypes {
   private final MetaType[] values;

   protected AbstractMetaTypes(int values) {
      this.values = new MetaType[values];
   }

   public MetaType byId(int id) {
      return this.values[id];
   }

   public MetaType[] values() {
      return this.values;
   }

   protected MetaType add(int typeId, Type<?> type) {
      MetaType metaType = MetaType.create(typeId, type);
      this.values[typeId] = metaType;
      return metaType;
   }
}
