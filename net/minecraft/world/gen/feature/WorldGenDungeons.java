package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenDungeons extends WorldGenerator {
   private static final Logger field_175918_a = LogManager.getLogger();
   private static final String[] SPAWNERTYPES = new String[]{"Skeleton", "Zombie", "Zombie", "Spider"};
   private static final List<WeightedRandomChestContent> CHESTCONTENT;

   static {
      CHESTCONTENT = Lists.newArrayList(new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 10), new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 4, 10), new WeightedRandomChestContent(Items.bread, 0, 1, 1, 10), new WeightedRandomChestContent(Items.wheat, 0, 1, 4, 10), new WeightedRandomChestContent(Items.gunpowder, 0, 1, 4, 10), new WeightedRandomChestContent(Items.string, 0, 1, 4, 10), new WeightedRandomChestContent(Items.bucket, 0, 1, 1, 10), new WeightedRandomChestContent(Items.golden_apple, 0, 1, 1, 1), new WeightedRandomChestContent(Items.redstone, 0, 1, 4, 10), new WeightedRandomChestContent(Items.record_13, 0, 1, 1, 4), new WeightedRandomChestContent(Items.record_cat, 0, 1, 1, 4), new WeightedRandomChestContent(Items.name_tag, 0, 1, 1, 10), new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 2), new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 5), new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)});
   }

   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = true;
      int j = rand.nextInt(2) + 2;
      int k = -j - 1;
      int l = j + 1;
      int i1 = true;
      int j1 = true;
      int k1 = rand.nextInt(2) + 2;
      int l1 = -k1 - 1;
      int i2 = k1 + 1;
      int j2 = 0;

      int k3;
      int i4;
      int k4;
      BlockPos blockpos1;
      for(k3 = k; k3 <= l; ++k3) {
         for(i4 = -1; i4 <= 4; ++i4) {
            for(k4 = l1; k4 <= i2; ++k4) {
               blockpos1 = position.add(k3, i4, k4);
               Material material = worldIn.getBlockState(blockpos1).getBlock().getMaterial();
               boolean flag = material.isSolid();
               if (i4 == -1 && !flag) {
                  return false;
               }

               if (i4 == 4 && !flag) {
                  return false;
               }

               if ((k3 == k || k3 == l || k4 == l1 || k4 == i2) && i4 == 0 && worldIn.isAirBlock(blockpos1) && worldIn.isAirBlock(blockpos1.up())) {
                  ++j2;
               }
            }
         }
      }

      if (j2 >= 1 && j2 <= 5) {
         for(k3 = k; k3 <= l; ++k3) {
            for(i4 = 3; i4 >= -1; --i4) {
               for(k4 = l1; k4 <= i2; ++k4) {
                  blockpos1 = position.add(k3, i4, k4);
                  if (k3 != k && i4 != -1 && k4 != l1 && k3 != l && i4 != 4 && k4 != i2) {
                     if (worldIn.getBlockState(blockpos1).getBlock() != Blocks.chest) {
                        worldIn.setBlockToAir(blockpos1);
                     }
                  } else if (blockpos1.getY() >= 0 && !worldIn.getBlockState(blockpos1.down()).getBlock().getMaterial().isSolid()) {
                     worldIn.setBlockToAir(blockpos1);
                  } else if (worldIn.getBlockState(blockpos1).getBlock().getMaterial().isSolid() && worldIn.getBlockState(blockpos1).getBlock() != Blocks.chest) {
                     if (i4 == -1 && rand.nextInt(4) != 0) {
                        worldIn.setBlockState(blockpos1, Blocks.mossy_cobblestone.getDefaultState(), 2);
                     } else {
                        worldIn.setBlockState(blockpos1, Blocks.cobblestone.getDefaultState(), 2);
                     }
                  }
               }
            }
         }

         for(k3 = 0; k3 < 2; ++k3) {
            for(i4 = 0; i4 < 3; ++i4) {
               k4 = position.getX() + rand.nextInt(j * 2 + 1) - j;
               int i5 = position.getY();
               int j5 = position.getZ() + rand.nextInt(k1 * 2 + 1) - k1;
               BlockPos blockpos2 = new BlockPos(k4, i5, j5);
               if (worldIn.isAirBlock(blockpos2)) {
                  int j3 = 0;
                  Iterator var22 = EnumFacing.Plane.HORIZONTAL.iterator();

                  while(var22.hasNext()) {
                     EnumFacing enumfacing = (EnumFacing)var22.next();
                     if (worldIn.getBlockState(blockpos2.offset(enumfacing)).getBlock().getMaterial().isSolid()) {
                        ++j3;
                     }
                  }

                  if (j3 == 1) {
                     worldIn.setBlockState(blockpos2, Blocks.chest.correctFacing(worldIn, blockpos2, Blocks.chest.getDefaultState()), 2);
                     List<WeightedRandomChestContent> list = WeightedRandomChestContent.func_177629_a(CHESTCONTENT, Items.enchanted_book.getRandom(rand));
                     TileEntity tileentity1 = worldIn.getTileEntity(blockpos2);
                     if (tileentity1 instanceof TileEntityChest) {
                        WeightedRandomChestContent.generateChestContents(rand, list, (TileEntityChest)tileentity1, 8);
                     }
                     break;
                  }
               }
            }
         }

         worldIn.setBlockState(position, Blocks.mob_spawner.getDefaultState(), 2);
         TileEntity tileentity = worldIn.getTileEntity(position);
         if (tileentity instanceof TileEntityMobSpawner) {
            ((TileEntityMobSpawner)tileentity).getSpawnerBaseLogic().setEntityName(this.pickMobSpawner(rand));
         } else {
            field_175918_a.error("Failed to fetch mob spawner entity at (" + position.getX() + ", " + position.getY() + ", " + position.getZ() + ")");
         }

         return true;
      } else {
         return false;
      }
   }

   private String pickMobSpawner(Random p_76543_1_) {
      return SPAWNERTYPES[p_76543_1_.nextInt(SPAWNERTYPES.length)];
   }
}
