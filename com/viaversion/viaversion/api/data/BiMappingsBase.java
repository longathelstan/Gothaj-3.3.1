package com.viaversion.viaversion.api.data;

public class BiMappingsBase implements BiMappings {
   protected final Mappings mappings;
   private final BiMappingsBase inverse;

   protected BiMappingsBase(Mappings mappings, Mappings inverse) {
      this.mappings = mappings;
      this.inverse = new BiMappingsBase(inverse, this);
   }

   private BiMappingsBase(Mappings mappings, BiMappingsBase inverse) {
      this.mappings = mappings;
      this.inverse = inverse;
   }

   public int getNewId(int id) {
      return this.mappings.getNewId(id);
   }

   public void setNewId(int id, int mappedId) {
      this.mappings.setNewId(id, mappedId);
      this.inverse.mappings.setNewId(mappedId, id);
   }

   public int size() {
      return this.mappings.size();
   }

   public int mappedSize() {
      return this.mappings.mappedSize();
   }

   public BiMappings inverse() {
      return this.inverse;
   }
}
