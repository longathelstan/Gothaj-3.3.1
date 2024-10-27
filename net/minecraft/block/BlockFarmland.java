package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFarmland extends Block {
   public static final PropertyInteger MOISTURE = PropertyInteger.create("moisture", 0, 7);
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$net$minecraft$util$EnumFacing;

   protected BlockFarmland() {
      super(Material.ground);
      this.setDefaultState(this.blockState.getBaseState().withProperty(MOISTURE, 0));
      this.setTickRandomly(true);
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
      this.setLightOpacity(255);
   }

   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return new AxisAlignedBB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1));
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean isFullCube() {
      return false;
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      int i = (Integer)state.getValue(MOISTURE);
      if (!this.hasWater(worldIn, pos) && !worldIn.isRainingAt(pos.up())) {
         if (i > 0) {
            worldIn.setBlockState(pos, state.withProperty(MOISTURE, i - 1), 2);
         } else if (!this.hasCrops(worldIn, pos)) {
            worldIn.setBlockState(pos, Blocks.dirt.getDefaultState());
         }
      } else if (i < 7) {
         worldIn.setBlockState(pos, state.withProperty(MOISTURE, 7), 2);
      }

   }

   public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
      if (entityIn instanceof EntityLivingBase) {
         if (!worldIn.isRemote && worldIn.rand.nextFloat() < fallDistance - 0.5F) {
            if (!(entityIn instanceof EntityPlayer) && !worldIn.getGameRules().getBoolean("mobGriefing")) {
               return;
            }

            worldIn.setBlockState(pos, Blocks.dirt.getDefaultState());
         }

         super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
      }

   }

   private boolean hasCrops(World worldIn, BlockPos pos) {
      Block block = worldIn.getBlockState(pos.up()).getBlock();
      return block instanceof BlockCrops || block instanceof BlockStem;
   }

   private boolean hasWater(World worldIn, BlockPos pos) {
      Iterator var4 = BlockPos.getAllInBoxMutable(pos.add(-4, 0, -4), pos.add(4, 1, 4)).iterator();

      while(var4.hasNext()) {
         BlockPos.MutableBlockPos blockpos$mutableblockpos = (BlockPos.MutableBlockPos)var4.next();
         if (worldIn.getBlockState(blockpos$mutableblockpos).getBlock().getMaterial() == Material.water) {
            return true;
         }
      }

      return false;
   }

   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
      if (worldIn.getBlockState(pos.up()).getBlock().getMaterial().isSolid()) {
         worldIn.setBlockState(pos, Blocks.dirt.getDefaultState());
      }

   }

   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      switch($SWITCH_TABLE$net$minecraft$util$EnumFacing()[side.ordinal()]) {
      case 2:
         return true;
      case 3:
      case 4:
      case 5:
      case 6:
         Block block = worldIn.getBlockState(pos).getBlock();
         if (!block.isOpaqueCube() && block != Blocks.farmland) {
            return true;
         }

         return false;
      default:
         return super.shouldSideBeRendered(worldIn, pos, side);
      }
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Blocks.dirt.getItemDropped(Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), rand, fortune);
   }

   public Item getItem(World worldIn, BlockPos pos) {
      return Item.getItemFromBlock(Blocks.dirt);
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(MOISTURE, meta & 7);
   }

   public int getMetaFromState(IBlockState state) {
      return (Integer)state.getValue(MOISTURE);
   }

   protected BlockState createBlockState() {
      return new BlockState(this, new IProperty[]{MOISTURE});
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$minecraft$util$EnumFacing() {
      int[] var10000 = $SWITCH_TABLE$net$minecraft$util$EnumFacing;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[EnumFacing.values().length];

         try {
            var0[EnumFacing.DOWN.ordinal()] = 1;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[EnumFacing.EAST.ordinal()] = 6;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[EnumFacing.NORTH.ordinal()] = 3;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[EnumFacing.SOUTH.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[EnumFacing.UP.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[EnumFacing.WEST.ordinal()] = 5;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$net$minecraft$util$EnumFacing = var0;
         return var0;
      }
   }
}
