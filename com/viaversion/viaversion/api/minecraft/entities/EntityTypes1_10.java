package com.viaversion.viaversion.api.minecraft.entities;

import com.viaversion.viaversion.api.Via;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EntityTypes1_10 {
   public static EntityTypes1_10.EntityType getTypeFromId(int typeID, boolean isObject) {
      Optional type;
      if (isObject) {
         type = EntityTypes1_10.ObjectType.getPCEntity(typeID);
      } else {
         type = EntityTypes1_10.EntityType.findById(typeID);
      }

      if (!type.isPresent()) {
         Via.getPlatform().getLogger().severe("Could not find 1.10 type id " + typeID + " isObject=" + isObject);
         return EntityTypes1_10.EntityType.ENTITY;
      } else {
         return (EntityTypes1_10.EntityType)type.get();
      }
   }

   public static enum ObjectType implements com.viaversion.viaversion.api.minecraft.entities.ObjectType {
      BOAT(1, EntityTypes1_10.EntityType.BOAT),
      ITEM(2, EntityTypes1_10.EntityType.DROPPED_ITEM),
      AREA_EFFECT_CLOUD(3, EntityTypes1_10.EntityType.AREA_EFFECT_CLOUD),
      MINECART(10, EntityTypes1_10.EntityType.MINECART_RIDEABLE),
      TNT_PRIMED(50, EntityTypes1_10.EntityType.PRIMED_TNT),
      ENDER_CRYSTAL(51, EntityTypes1_10.EntityType.ENDER_CRYSTAL),
      TIPPED_ARROW(60, EntityTypes1_10.EntityType.TIPPED_ARROW),
      SNOWBALL(61, EntityTypes1_10.EntityType.SNOWBALL),
      EGG(62, EntityTypes1_10.EntityType.EGG),
      FIREBALL(63, EntityTypes1_10.EntityType.FIREBALL),
      SMALL_FIREBALL(64, EntityTypes1_10.EntityType.SMALL_FIREBALL),
      ENDER_PEARL(65, EntityTypes1_10.EntityType.ENDER_PEARL),
      WITHER_SKULL(66, EntityTypes1_10.EntityType.WITHER_SKULL),
      SHULKER_BULLET(67, EntityTypes1_10.EntityType.SHULKER_BULLET),
      FALLING_BLOCK(70, EntityTypes1_10.EntityType.FALLING_BLOCK),
      ITEM_FRAME(71, EntityTypes1_10.EntityType.ITEM_FRAME),
      ENDER_SIGNAL(72, EntityTypes1_10.EntityType.ENDER_SIGNAL),
      POTION(73, EntityTypes1_10.EntityType.SPLASH_POTION),
      THROWN_EXP_BOTTLE(75, EntityTypes1_10.EntityType.THROWN_EXP_BOTTLE),
      FIREWORK(76, EntityTypes1_10.EntityType.FIREWORK),
      LEASH(77, EntityTypes1_10.EntityType.LEASH_HITCH),
      ARMOR_STAND(78, EntityTypes1_10.EntityType.ARMOR_STAND),
      FISHIHNG_HOOK(90, EntityTypes1_10.EntityType.FISHING_HOOK),
      SPECTRAL_ARROW(91, EntityTypes1_10.EntityType.SPECTRAL_ARROW),
      DRAGON_FIREBALL(93, EntityTypes1_10.EntityType.DRAGON_FIREBALL);

      private static final Map<Integer, EntityTypes1_10.ObjectType> TYPES = new HashMap();
      private final int id;
      private final EntityTypes1_10.EntityType type;

      private ObjectType(int id, EntityTypes1_10.EntityType type) {
         this.id = id;
         this.type = type;
      }

      public int getId() {
         return this.id;
      }

      public EntityTypes1_10.EntityType getType() {
         return this.type;
      }

      public static Optional<EntityTypes1_10.ObjectType> findById(int id) {
         return id == -1 ? Optional.empty() : Optional.ofNullable(TYPES.get(id));
      }

      public static Optional<EntityTypes1_10.EntityType> getPCEntity(int id) {
         Optional<EntityTypes1_10.ObjectType> output = findById(id);
         return !output.isPresent() ? Optional.empty() : Optional.of(((EntityTypes1_10.ObjectType)output.get()).type);
      }

      static {
         EntityTypes1_10.ObjectType[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            EntityTypes1_10.ObjectType type = var0[var2];
            TYPES.put(type.id, type);
         }

      }
   }

   public static enum EntityType implements com.viaversion.viaversion.api.minecraft.entities.EntityType {
      ENTITY(-1),
      DROPPED_ITEM(1, ENTITY),
      EXPERIENCE_ORB(2, ENTITY),
      LEASH_HITCH(8, ENTITY),
      PAINTING(9, ENTITY),
      ARROW(10, ENTITY),
      SNOWBALL(11, ENTITY),
      FIREBALL(12, ENTITY),
      SMALL_FIREBALL(13, ENTITY),
      ENDER_PEARL(14, ENTITY),
      ENDER_SIGNAL(15, ENTITY),
      THROWN_EXP_BOTTLE(17, ENTITY),
      ITEM_FRAME(18, ENTITY),
      WITHER_SKULL(19, ENTITY),
      PRIMED_TNT(20, ENTITY),
      FALLING_BLOCK(21, ENTITY),
      FIREWORK(22, ENTITY),
      TIPPED_ARROW(23, ARROW),
      SPECTRAL_ARROW(24, ARROW),
      SHULKER_BULLET(25, ENTITY),
      DRAGON_FIREBALL(26, FIREBALL),
      ENTITY_LIVING(-1, ENTITY),
      ENTITY_INSENTIENT(-1, ENTITY_LIVING),
      ENTITY_AGEABLE(-1, ENTITY_INSENTIENT),
      ENTITY_TAMEABLE_ANIMAL(-1, ENTITY_AGEABLE),
      ENTITY_HUMAN(-1, ENTITY_LIVING),
      ARMOR_STAND(30, ENTITY_LIVING),
      MINECART_ABSTRACT(-1, ENTITY),
      MINECART_COMMAND(40, MINECART_ABSTRACT),
      BOAT(41, ENTITY),
      MINECART_RIDEABLE(42, MINECART_ABSTRACT),
      MINECART_CHEST(43, MINECART_ABSTRACT),
      MINECART_FURNACE(44, MINECART_ABSTRACT),
      MINECART_TNT(45, MINECART_ABSTRACT),
      MINECART_HOPPER(46, MINECART_ABSTRACT),
      MINECART_MOB_SPAWNER(47, MINECART_ABSTRACT),
      CREEPER(50, ENTITY_INSENTIENT),
      SKELETON(51, ENTITY_INSENTIENT),
      SPIDER(52, ENTITY_INSENTIENT),
      GIANT(53, ENTITY_INSENTIENT),
      ZOMBIE(54, ENTITY_INSENTIENT),
      SLIME(55, ENTITY_INSENTIENT),
      GHAST(56, ENTITY_INSENTIENT),
      PIG_ZOMBIE(57, ZOMBIE),
      ENDERMAN(58, ENTITY_INSENTIENT),
      CAVE_SPIDER(59, SPIDER),
      SILVERFISH(60, ENTITY_INSENTIENT),
      BLAZE(61, ENTITY_INSENTIENT),
      MAGMA_CUBE(62, SLIME),
      ENDER_DRAGON(63, ENTITY_INSENTIENT),
      WITHER(64, ENTITY_INSENTIENT),
      BAT(65, ENTITY_INSENTIENT),
      WITCH(66, ENTITY_INSENTIENT),
      ENDERMITE(67, ENTITY_INSENTIENT),
      GUARDIAN(68, ENTITY_INSENTIENT),
      IRON_GOLEM(99, ENTITY_INSENTIENT),
      SHULKER(69, IRON_GOLEM),
      PIG(90, ENTITY_AGEABLE),
      SHEEP(91, ENTITY_AGEABLE),
      COW(92, ENTITY_AGEABLE),
      CHICKEN(93, ENTITY_AGEABLE),
      SQUID(94, ENTITY_INSENTIENT),
      WOLF(95, ENTITY_TAMEABLE_ANIMAL),
      MUSHROOM_COW(96, COW),
      SNOWMAN(97, IRON_GOLEM),
      OCELOT(98, ENTITY_TAMEABLE_ANIMAL),
      HORSE(100, ENTITY_AGEABLE),
      RABBIT(101, ENTITY_AGEABLE),
      POLAR_BEAR(102, ENTITY_AGEABLE),
      VILLAGER(120, ENTITY_AGEABLE),
      ENDER_CRYSTAL(200, ENTITY),
      SPLASH_POTION(-1, ENTITY),
      LINGERING_POTION(-1, SPLASH_POTION),
      AREA_EFFECT_CLOUD(-1, ENTITY),
      EGG(-1, ENTITY),
      FISHING_HOOK(-1, ENTITY),
      LIGHTNING(-1, ENTITY),
      WEATHER(-1, ENTITY),
      PLAYER(-1, ENTITY_HUMAN),
      COMPLEX_PART(-1, ENTITY);

      private static final Map<Integer, EntityTypes1_10.EntityType> TYPES = new HashMap();
      private final int id;
      private final EntityTypes1_10.EntityType parent;

      private EntityType(int id) {
         this.id = id;
         this.parent = null;
      }

      private EntityType(int id, EntityTypes1_10.EntityType parent) {
         this.id = id;
         this.parent = parent;
      }

      public static Optional<EntityTypes1_10.EntityType> findById(int id) {
         return id == -1 ? Optional.empty() : Optional.ofNullable(TYPES.get(id));
      }

      public int getId() {
         return this.id;
      }

      public EntityTypes1_10.EntityType getParent() {
         return this.parent;
      }

      public String identifier() {
         throw new UnsupportedOperationException();
      }

      public boolean isAbstractType() {
         return this.id != -1;
      }

      static {
         EntityTypes1_10.EntityType[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            EntityTypes1_10.EntityType type = var0[var2];
            TYPES.put(type.id, type);
         }

      }
   }
}
