package com.viaversion.viaversion.api.minecraft;

import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

public enum RegistryType {
   BLOCK("block"),
   ITEM("item"),
   FLUID("fluid"),
   ENTITY("entity_type"),
   GAME_EVENT("game_event");

   private static final Map<String, RegistryType> MAP = new HashMap();
   private static final RegistryType[] VALUES = values();
   private final String resourceLocation;

   public static RegistryType[] getValues() {
      return VALUES;
   }

   @Nullable
   public static RegistryType getByKey(String resourceKey) {
      return (RegistryType)MAP.get(resourceKey);
   }

   private RegistryType(String resourceLocation) {
      this.resourceLocation = resourceLocation;
   }

   /** @deprecated */
   @Deprecated
   public String getResourceLocation() {
      return this.resourceLocation;
   }

   public String resourceLocation() {
      return this.resourceLocation;
   }

   static {
      RegistryType[] var0 = getValues();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         RegistryType type = var0[var2];
         MAP.put(type.resourceLocation, type);
      }

   }
}
