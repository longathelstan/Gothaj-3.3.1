package xyz.cucumber.base.utils.game;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import xyz.cucumber.base.Client;
import xyz.cucumber.base.commands.cmds.FriendsCommand;
import xyz.cucumber.base.module.feat.combat.KillAuraModule;
import xyz.cucumber.base.module.feat.other.FriendsModule;
import xyz.cucumber.base.module.feat.other.ReverseFriendsModule;
import xyz.cucumber.base.utils.Timer;
import xyz.cucumber.base.utils.math.RotationUtils;

public class EntityUtils {
   public static Minecraft mc = Minecraft.getMinecraft();
   public static int size = 0;
   public static Timer timer = new Timer();

   public static EntityLivingBase getTarget(double range, String targetMode, String attackMode, int switchTimer, boolean teams, boolean troughWalls, boolean dead, boolean invisible) {
      EntityLivingBase target = null;
      Stream var10000 = mc.theWorld.loadedEntityList.stream();
      EntityLivingBase.class.getClass();
      List<Entity> targets = (List)var10000.filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
      targets = (List)targets.stream().filter((entity) -> {
         return (double)mc.thePlayer.getDistanceToEntity(entity) < range && entity != mc.thePlayer && !(entity instanceof EntityArmorStand);
      }).collect(Collectors.toList());
      targets.removeIf((entity) -> {
         return !invisible && entity.isInvisible();
      });
      targets.removeIf((entity) -> {
         return !dead && ((EntityLivingBase)entity).getHealth() <= 0.0F;
      });
      KillAuraModule ka = (KillAuraModule)Client.INSTANCE.getModuleManager().getModule(KillAuraModule.class);
      targets.removeIf((entity) -> {
         return (double)(Math.abs(RotationUtils.getRotationFromPosition(entity.posX, entity.posY, entity.posZ)[0] - Minecraft.getMinecraft().thePlayer.rotationYaw) % 360.0F > 180.0F ? 360.0F - Math.abs(RotationUtils.getRotationFromPosition(entity.posX, entity.posY, entity.posZ)[0] - Minecraft.getMinecraft().thePlayer.rotationYaw) % 360.0F : Math.abs(RotationUtils.getRotationFromPosition(entity.posX, entity.posY, entity.posZ)[0] - Minecraft.getMinecraft().thePlayer.rotationYaw) % 360.0F) > ka.fov.getValue();
      });
      String friend;
      if (Client.INSTANCE.getModuleManager().getModule(ReverseFriendsModule.class).isEnabled()) {
         targets.removeIf((entity) -> {
            return !ReverseFriendsModule.allowed.contains(entity.getName());
         });
      } else {
         if (Client.INSTANCE.getModuleManager().getModule(FriendsModule.class).isEnabled()) {
            Iterator var14 = FriendsCommand.friends.iterator();

            while(var14.hasNext()) {
               friend = (String)var14.next();
               targets.removeIf((entity) -> {
                  return entity.getName().equalsIgnoreCase(friend);
               });
            }
         }

         targets.removeIf((entity) -> {
            return teams && mc.thePlayer.isOnSameTeam((EntityLivingBase)entity);
         });

         try {
            targets.removeIf((entity) -> {
               return teams && entity instanceof EntityPlayer && isInSameTeam((EntityPlayer)entity);
            });
         } catch (Exception var17) {
         }
      }

      targets.removeIf((entity) -> {
         return !troughWalls && !mc.thePlayer.canEntityBeSeen(entity);
      });
      int i;
      Entity ent;
      boolean isPlayer;
      switch((friend = ka.sort.getMode().toLowerCase()).hashCode()) {
      case -1314571118:
         if (friend.equals("strongest player")) {
            if (targets.size() > 1) {
               isPlayer = false;

               for(i = 0; i < targets.size(); ++i) {
                  ent = (Entity)targets.get(i);
                  if (ent != null) {
                     if (!(ent instanceof EntityPlayer)) {
                        if (isPlayer) {
                           targets.remove(ent);
                        }
                     } else {
                        isPlayer = true;
                     }
                  }
               }
            }

            targets.sort(Comparator.comparingDouble((entity) -> {
               return getStrongestPlayerSort(entity);
            }));
         }
         break;
      case -1221262756:
         if (friend.equals("health")) {
            targets.sort(Comparator.comparingDouble((entity) -> {
               return (double)(entity instanceof EntityPlayer ? ((EntityPlayer)entity).getHealth() : mc.thePlayer.getDistanceToEntity(entity));
            }));
         }
         break;
      case 109549001:
         if (friend.equals("smart")) {
            if (targets.size() > 1) {
               isPlayer = false;

               for(i = 0; i < targets.size(); ++i) {
                  ent = (Entity)targets.get(i);
                  if (ent != null) {
                     if (!(ent instanceof EntityPlayer)) {
                        if (isPlayer) {
                           targets.remove(ent);
                        }
                     } else {
                        isPlayer = true;
                     }
                  }
               }
            }

            targets.sort(Comparator.comparingDouble((entity) -> {
               return getSmartSort(entity);
            }));
         }
         break;
      case 288459765:
         if (friend.equals("distance")) {
            targets.sort(Comparator.comparingDouble((entity) -> {
               return (double)mc.thePlayer.getDistanceToEntity(entity);
            }));
         }
      }

      String var19;
      switch((var19 = targetMode.toLowerCase()).hashCode()) {
      case -493567566:
         if (var19.equals("players")) {
            var10000 = targets.stream();
            EntityPlayer.class.getClass();
            targets = (List)var10000.filter(EntityPlayer.class::isInstance).collect(Collectors.toList());
         }
      default:
         if (!targets.isEmpty()) {
            String var20;
            switch((var20 = attackMode.toLowerCase()).hashCode()) {
            case 109935:
               if (var20.equals("off")) {
                  target = (EntityLivingBase)targets.get(0);
               }
               break;
            case 110364485:
               if (var20.equals("timer")) {
                  if (timer.hasTimeElapsed((double)switchTimer, true)) {
                     ++size;
                  }

                  if (targets.size() > 0 && size >= targets.size()) {
                     size = 0;
                  }

                  target = (EntityLivingBase)targets.get(size);
               }
               break;
            case 203974718:
               if (var20.equals("hurt time") && targets.size() > 0) {
                  targets.sort(Comparator.comparingDouble((entity) -> {
                     return (double)entity.hurtResistantTime;
                  }));
                  target = (EntityLivingBase)targets.get(0);
               }
            }
         }

         return target;
      }
   }

   public static EntityLivingBase getTargetBox(double range, String targetMode, String attackMode, int switchTimer, boolean teams, boolean troughWalls, boolean dead, boolean invisible) {
      EntityLivingBase target = null;
      Stream var10000 = mc.theWorld.loadedEntityList.stream();
      EntityLivingBase.class.getClass();
      List<Entity> targets = (List)var10000.filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
      targets = (List)targets.stream().filter((entity) -> {
         return getDistanceToEntityBox(entity) < range && entity != mc.thePlayer && !(entity instanceof EntityArmorStand);
      }).collect(Collectors.toList());
      targets.removeIf((entity) -> {
         return !invisible && entity.isInvisible();
      });
      targets.removeIf((entity) -> {
         return !dead && ((EntityLivingBase)entity).getHealth() <= 0.0F;
      });
      KillAuraModule ka = (KillAuraModule)Client.INSTANCE.getModuleManager().getModule(KillAuraModule.class);
      targets.removeIf((entity) -> {
         return (double)(Math.abs(RotationUtils.getRotationFromPosition(entity.posX, entity.posY, entity.posZ)[0] - Minecraft.getMinecraft().thePlayer.rotationYaw) % 360.0F > 180.0F ? 360.0F - Math.abs(RotationUtils.getRotationFromPosition(entity.posX, entity.posY, entity.posZ)[0] - Minecraft.getMinecraft().thePlayer.rotationYaw) % 360.0F : Math.abs(RotationUtils.getRotationFromPosition(entity.posX, entity.posY, entity.posZ)[0] - Minecraft.getMinecraft().thePlayer.rotationYaw) % 360.0F) > ka.fov.getValue();
      });
      String friend;
      if (Client.INSTANCE.getModuleManager().getModule(ReverseFriendsModule.class).isEnabled()) {
         targets.removeIf((entity) -> {
            return !ReverseFriendsModule.allowed.contains(entity.getName());
         });
      } else {
         if (Client.INSTANCE.getModuleManager().getModule(FriendsModule.class).isEnabled()) {
            Iterator var14 = FriendsCommand.friends.iterator();

            while(var14.hasNext()) {
               friend = (String)var14.next();
               targets.removeIf((entity) -> {
                  return entity.getName().equalsIgnoreCase(friend);
               });
            }
         }

         targets.removeIf((entity) -> {
            return teams && mc.thePlayer.isOnSameTeam((EntityLivingBase)entity);
         });

         try {
            targets.removeIf((entity) -> {
               return teams && entity instanceof EntityPlayer && isInSameTeam((EntityPlayer)entity);
            });
         } catch (Exception var17) {
         }
      }

      targets.removeIf((entity) -> {
         return !troughWalls && !mc.thePlayer.canEntityBeSeen(entity);
      });
      int i;
      Entity ent;
      boolean isPlayer;
      switch((friend = ka.sort.getMode().toLowerCase()).hashCode()) {
      case -1314571118:
         if (friend.equals("strongest player")) {
            if (targets.size() > 1) {
               isPlayer = false;

               for(i = 0; i < targets.size(); ++i) {
                  ent = (Entity)targets.get(i);
                  if (ent != null) {
                     if (!(ent instanceof EntityPlayer)) {
                        if (isPlayer) {
                           targets.remove(ent);
                        }
                     } else {
                        isPlayer = true;
                     }
                  }
               }
            }

            targets.sort(Comparator.comparingDouble((entity) -> {
               return getStrongestPlayerSort(entity);
            }));
         }
         break;
      case -1221262756:
         if (friend.equals("health")) {
            targets.sort(Comparator.comparingDouble((entity) -> {
               return (double)(entity instanceof EntityPlayer ? ((EntityPlayer)entity).getHealth() : mc.thePlayer.getDistanceToEntity(entity));
            }));
         }
         break;
      case 109549001:
         if (friend.equals("smart")) {
            if (targets.size() > 1) {
               isPlayer = false;

               for(i = 0; i < targets.size(); ++i) {
                  ent = (Entity)targets.get(i);
                  if (ent != null) {
                     if (!(ent instanceof EntityPlayer)) {
                        if (isPlayer) {
                           targets.remove(ent);
                        }
                     } else {
                        isPlayer = true;
                     }
                  }
               }
            }

            targets.sort(Comparator.comparingDouble((entity) -> {
               return getSmartSort(entity);
            }));
         }
         break;
      case 288459765:
         if (friend.equals("distance")) {
            targets.sort(Comparator.comparingDouble((entity) -> {
               return (double)mc.thePlayer.getDistanceToEntity(entity);
            }));
         }
      }

      String var19;
      switch((var19 = targetMode.toLowerCase()).hashCode()) {
      case -493567566:
         if (var19.equals("players")) {
            var10000 = targets.stream();
            EntityPlayer.class.getClass();
            targets = (List)var10000.filter(EntityPlayer.class::isInstance).collect(Collectors.toList());
         }
      default:
         if (!targets.isEmpty()) {
            String var20;
            switch((var20 = attackMode.toLowerCase()).hashCode()) {
            case 109935:
               if (var20.equals("off")) {
                  target = (EntityLivingBase)targets.get(0);
               }
               break;
            case 110364485:
               if (var20.equals("timer")) {
                  if (timer.hasTimeElapsed((double)switchTimer, true)) {
                     ++size;
                  }

                  if (targets.size() > 0 && size >= targets.size()) {
                     size = 0;
                  }

                  target = (EntityLivingBase)targets.get(size);
               }
               break;
            case 203974718:
               if (var20.equals("hurt time") && targets.size() > 0) {
                  targets.sort(Comparator.comparingDouble((entity) -> {
                     return (double)entity.hurtResistantTime;
                  }));
                  target = (EntityLivingBase)targets.get(0);
               }
            }
         }

         return target;
      }
   }

   public static boolean isInSameTeam(EntityPlayer player) {
      try {
         String[] name = mc.thePlayer.getDisplayName().getUnformattedText().split("");
         String[] parts = player.getDisplayName().getUnformattedText().split("");
         boolean b = Arrays.asList(name).contains("Â§") && Arrays.asList(parts).contains("Â§") && ((String)Arrays.asList(name).get(Arrays.asList(name).indexOf("Â§") + 1)).equals(Arrays.asList(parts).get(Arrays.asList(parts).indexOf("Â§") + 1));
         return b;
      } catch (Exception var4) {
         return false;
      }
   }

   public static double getDistanceToEntityBox(Entity entity) {
      Vec3 eyes = mc.thePlayer.getPositionEyes(1.0F);
      Vec3 pos = RotationUtils.getBestHitVec(entity);
      double xDist = Math.abs(pos.xCoord - eyes.xCoord);
      double yDist = Math.abs(pos.yCoord - eyes.yCoord);
      double zDist = Math.abs(pos.zCoord - eyes.zCoord);
      return Math.sqrt(Math.pow(xDist, 2.0D) + Math.pow(yDist, 2.0D) + Math.pow(zDist, 2.0D));
   }

   public static double getDistanceToEntityBoxFromPosition(double posX, double posY, double posZ, Entity entity) {
      Vec3 eyes = mc.thePlayer.getPositionEyes(1.0F);
      Vec3 pos = RotationUtils.getBestHitVec(entity);
      double xDist = Math.abs(pos.xCoord - posX);
      double yDist = Math.abs(pos.yCoord - posY + (double)mc.thePlayer.getEyeHeight());
      double zDist = Math.abs(pos.zCoord - posZ);
      return Math.sqrt(Math.pow(xDist, 2.0D) + Math.pow(yDist, 2.0D) + Math.pow(zDist, 2.0D));
   }

   public static double getSmartSort(Entity entity) {
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         double playerDamage = 0.0D;
         double targetDamage = 0.0D;
         if (mc.thePlayer.getHeldItem() != null) {
            playerDamage = (double)Math.max(0.0F, InventoryUtils.getItemDamage(mc.thePlayer.getHeldItem()));
         }

         if (player.getHeldItem() != null) {
            targetDamage = (double)Math.max(0.0F, InventoryUtils.getItemDamage(player.getHeldItem()));
         }

         playerDamage = playerDamage * 20.0D / (double)(player.getTotalArmorValue() * 4);
         if (mc.thePlayer.fallDistance > 0.0F) {
            playerDamage *= 1.5D;
         }

         return playerDamage >= (double)player.getHealth() ? -1.0E8D : targetDamage * -1.0D;
      } else {
         return (double)mc.thePlayer.getDistanceToEntity(entity);
      }
   }

   public static double getStrongestPlayerSort(Entity entity) {
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         double targetDamage = 0.0D;
         if (player.getHeldItem() != null) {
            targetDamage = (double)Math.max(0.0F, InventoryUtils.getItemDamage(player.getHeldItem()));
         }

         return targetDamage * -1.0D;
      } else {
         return (double)mc.thePlayer.getDistanceToEntity(entity);
      }
   }
}
