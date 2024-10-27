package com.viaversion.viaversion.data.entity;

import com.viaversion.viaversion.api.data.entity.StoredEntityData;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class StoredEntityDataImpl implements StoredEntityData {
   private final Map<Class<?>, Object> storedObjects = new ConcurrentHashMap();
   private final EntityType type;

   public StoredEntityDataImpl(EntityType type) {
      this.type = type;
   }

   public EntityType type() {
      return this.type;
   }

   @Nullable
   public <T> T get(Class<T> objectClass) {
      return this.storedObjects.get(objectClass);
   }

   @Nullable
   public <T> T remove(Class<T> objectClass) {
      return this.storedObjects.remove(objectClass);
   }

   public boolean has(Class<?> objectClass) {
      return this.storedObjects.containsKey(objectClass);
   }

   public void put(Object object) {
      this.storedObjects.put(object.getClass(), object);
   }
}
