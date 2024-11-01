package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.StatCollector;

public class ItemFireworkCharge extends Item {
   public int getColorFromItemStack(ItemStack stack, int renderPass) {
      if (renderPass != 1) {
         return super.getColorFromItemStack(stack, renderPass);
      } else {
         NBTBase nbtbase = getExplosionTag(stack, "Colors");
         if (!(nbtbase instanceof NBTTagIntArray)) {
            return 9079434;
         } else {
            NBTTagIntArray nbttagintarray = (NBTTagIntArray)nbtbase;
            int[] aint = nbttagintarray.getIntArray();
            if (aint.length == 1) {
               return aint[0];
            } else {
               int i = 0;
               int j = 0;
               int k = 0;
               int[] var12 = aint;
               int var11 = aint.length;

               for(int var10 = 0; var10 < var11; ++var10) {
                  int l = var12[var10];
                  i += (l & 16711680) >> 16;
                  j += (l & '\uff00') >> 8;
                  k += (l & 255) >> 0;
               }

               i /= aint.length;
               j /= aint.length;
               k /= aint.length;
               return i << 16 | j << 8 | k;
            }
         }
      }
   }

   public static NBTBase getExplosionTag(ItemStack stack, String key) {
      if (stack.hasTagCompound()) {
         NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("Explosion");
         if (nbttagcompound != null) {
            return nbttagcompound.getTag(key);
         }
      }

      return null;
   }

   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      if (stack.hasTagCompound()) {
         NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("Explosion");
         if (nbttagcompound != null) {
            addExplosionInfo(nbttagcompound, tooltip);
         }
      }

   }

   public static void addExplosionInfo(NBTTagCompound nbt, List<String> tooltip) {
      byte b0 = nbt.getByte("Type");
      if (b0 >= 0 && b0 <= 4) {
         tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.type." + b0).trim());
      } else {
         tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.type").trim());
      }

      int[] aint = nbt.getIntArray("Colors");
      int l;
      int var8;
      if (aint.length > 0) {
         boolean flag = true;
         String s = "";
         int[] var9 = aint;
         var8 = aint.length;

         for(l = 0; l < var8; ++l) {
            int i = var9[l];
            if (!flag) {
               s = s + ", ";
            }

            flag = false;
            boolean flag1 = false;

            for(int j = 0; j < ItemDye.dyeColors.length; ++j) {
               if (i == ItemDye.dyeColors[j]) {
                  flag1 = true;
                  s = s + StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(j).getUnlocalizedName());
                  break;
               }
            }

            if (!flag1) {
               s = s + StatCollector.translateToLocal("item.fireworksCharge.customColor");
            }
         }

         tooltip.add(s);
      }

      int[] aint1 = nbt.getIntArray("FadeColors");
      boolean flag2;
      if (aint1.length > 0) {
         flag2 = true;
         String s1 = StatCollector.translateToLocal("item.fireworksCharge.fadeTo") + " ";
         int[] var18 = aint1;
         int var17 = aint1.length;

         for(var8 = 0; var8 < var17; ++var8) {
            l = var18[var8];
            if (!flag2) {
               s1 = s1 + ", ";
            }

            flag2 = false;
            boolean flag5 = false;

            for(int k = 0; k < 16; ++k) {
               if (l == ItemDye.dyeColors[k]) {
                  flag5 = true;
                  s1 = s1 + StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(k).getUnlocalizedName());
                  break;
               }
            }

            if (!flag5) {
               s1 = s1 + StatCollector.translateToLocal("item.fireworksCharge.customColor");
            }
         }

         tooltip.add(s1);
      }

      flag2 = nbt.getBoolean("Trail");
      if (flag2) {
         tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.trail"));
      }

      boolean flag4 = nbt.getBoolean("Flicker");
      if (flag4) {
         tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.flicker"));
      }

   }
}
