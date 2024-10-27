package net.minecraft.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class WeightedRandom {
   public static int getTotalWeight(Collection<? extends WeightedRandom.Item> collection) {
      int i = 0;

      WeightedRandom.Item weightedrandom$item;
      for(Iterator var3 = collection.iterator(); var3.hasNext(); i += weightedrandom$item.itemWeight) {
         weightedrandom$item = (WeightedRandom.Item)var3.next();
      }

      return i;
   }

   public static <T extends WeightedRandom.Item> T getRandomItem(Random random, Collection<T> collection, int totalWeight) {
      if (totalWeight <= 0) {
         throw new IllegalArgumentException();
      } else {
         int i = random.nextInt(totalWeight);
         return getRandomItem(collection, i);
      }
   }

   public static <T extends WeightedRandom.Item> T getRandomItem(Collection<T> collection, int weight) {
      Iterator var3 = collection.iterator();

      while(var3.hasNext()) {
         T t = (WeightedRandom.Item)var3.next();
         weight -= t.itemWeight;
         if (weight < 0) {
            return t;
         }
      }

      return null;
   }

   public static <T extends WeightedRandom.Item> T getRandomItem(Random random, Collection<T> collection) {
      return getRandomItem(random, collection, getTotalWeight(collection));
   }

   public static class Item {
      protected int itemWeight;

      public Item(int itemWeightIn) {
         this.itemWeight = itemWeightIn;
      }
   }
}
