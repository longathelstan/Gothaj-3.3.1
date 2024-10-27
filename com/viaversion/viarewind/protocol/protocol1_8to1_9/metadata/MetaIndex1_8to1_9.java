package com.viaversion.viarewind.protocol.protocol1_8to1_9.metadata;

import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_10;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.metadata.MetaIndex;
import com.viaversion.viaversion.util.Pair;
import java.util.HashMap;
import java.util.Optional;

public class MetaIndex1_8to1_9 {
   private static final HashMap<Pair<EntityTypes1_10.EntityType, Integer>, MetaIndex> metadataRewrites = new HashMap();

   private static Optional<MetaIndex> getIndex(EntityTypes1_10.EntityType type, int index) {
      Pair<EntityTypes1_10.EntityType, Integer> pair = new Pair(type, index);
      return metadataRewrites.containsKey(pair) ? Optional.of((MetaIndex)metadataRewrites.get(pair)) : Optional.empty();
   }

   public static MetaIndex searchIndex(EntityTypes1_10.EntityType type, int index) {
      EntityTypes1_10.EntityType currentType = type;

      do {
         Optional<MetaIndex> optMeta = getIndex(currentType, index);
         if (optMeta.isPresent()) {
            return (MetaIndex)optMeta.get();
         }

         currentType = currentType.getParent();
      } while(currentType != null);

      return null;
   }

   static {
      MetaIndex[] var0 = MetaIndex.values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         MetaIndex index = var0[var2];
         metadataRewrites.put(new Pair(index.getClazz(), index.getNewIndex()), index);
      }

   }
}
