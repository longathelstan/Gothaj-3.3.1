package com.viaversion.viaversion.util;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.protocol.Protocol;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class EntityTypeUtil {
   private static final EntityType[] EMPTY_ARRAY = new EntityType[0];

   public static EntityType[] toOrderedArray(EntityType[] values) {
      List<EntityType> types = new ArrayList();
      EntityType[] var2 = values;
      int var3 = values.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EntityType type = var2[var4];
         if (type.getId() != -1) {
            types.add(type);
         }
      }

      types.sort(Comparator.comparingInt(EntityType::getId));
      return (EntityType[])types.toArray(EMPTY_ARRAY);
   }

   public static <T extends EntityType> void initialize(T[] values, EntityType[] typesToFill, Protocol<?, ?, ?, ?> protocol, EntityTypeUtil.EntityIdSetter<T> idSetter) {
      EntityType[] var4 = values;
      int var5 = values.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         T type = var4[var6];
         if (!type.isAbstractType()) {
            int id = protocol.getMappingData().getEntityMappings().mappedId(type.identifier());
            Preconditions.checkArgument(id != -1, "Entity type %s has no id", new Object[]{type.identifier()});
            idSetter.setId(type, id);
            typesToFill[id] = type;
         }
      }

   }

   public static EntityType[] createSizedArray(EntityType[] values) {
      int count = 0;
      EntityType[] var2 = values;
      int var3 = values.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EntityType type = var2[var4];
         if (!type.isAbstractType()) {
            ++count;
         }
      }

      return new EntityType[count];
   }

   public static EntityType getTypeFromId(EntityType[] values, int typeId, EntityType fallback) {
      EntityType type;
      if (typeId >= 0 && typeId < values.length && (type = values[typeId]) != null) {
         return type;
      } else {
         Via.getPlatform().getLogger().severe("Could not find " + fallback.getClass().getSimpleName() + " type id " + typeId);
         return fallback;
      }
   }

   @FunctionalInterface
   public interface EntityIdSetter<T extends EntityType> {
      void setId(T var1, int var2);
   }
}
