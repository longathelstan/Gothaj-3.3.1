package com.viaversion.viaversion.api.data;

public class IdentityMappings implements Mappings {
   private final int size;
   private final int mappedSize;

   public IdentityMappings(int size, int mappedSize) {
      this.size = size;
      this.mappedSize = mappedSize;
   }

   public int getNewId(int id) {
      return id >= 0 && id < this.size ? id : -1;
   }

   public void setNewId(int id, int mappedId) {
      throw new UnsupportedOperationException();
   }

   public int size() {
      return this.size;
   }

   public int mappedSize() {
      return this.mappedSize;
   }

   public Mappings inverse() {
      return new IdentityMappings(this.mappedSize, this.size);
   }
}
