package com.viaversion.viaversion.libs.gson;

public enum LongSerializationPolicy {
   DEFAULT {
      public JsonElement serialize(Long value) {
         return (JsonElement)(value == null ? JsonNull.INSTANCE : new JsonPrimitive(value));
      }
   },
   STRING {
      public JsonElement serialize(Long value) {
         return (JsonElement)(value == null ? JsonNull.INSTANCE : new JsonPrimitive(value.toString()));
      }
   };

   private LongSerializationPolicy() {
   }

   public abstract JsonElement serialize(Long var1);

   // $FF: synthetic method
   LongSerializationPolicy(Object x2) {
      this();
   }
}
