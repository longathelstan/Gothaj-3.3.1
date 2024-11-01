package net.minecraft.block;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWireHook extends Block {
   public static final PropertyDirection FACING;
   public static final PropertyBool POWERED;
   public static final PropertyBool ATTACHED;
   public static final PropertyBool SUSPENDED;
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$net$minecraft$util$EnumFacing;

   static {
      FACING = PropertyDirection.create("facing", (Predicate)EnumFacing.Plane.HORIZONTAL);
      POWERED = PropertyBool.create("powered");
      ATTACHED = PropertyBool.create("attached");
      SUSPENDED = PropertyBool.create("suspended");
   }

   public BlockTripWireHook() {
      super(Material.circuits);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, false).withProperty(ATTACHED, false).withProperty(SUSPENDED, false));
      this.setCreativeTab(CreativeTabs.tabRedstone);
      this.setTickRandomly(true);
   }

   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return state.withProperty(SUSPENDED, !World.doesBlockHaveSolidTopSurface(worldIn, pos.down()));
   }

   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean isFullCube() {
      return false;
   }

   public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
      return side.getAxis().isHorizontal() && worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock().isNormalCube();
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      Iterator var4 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(var4.hasNext()) {
         EnumFacing enumfacing = (EnumFacing)var4.next();
         if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock().isNormalCube()) {
            return true;
         }
      }

      return false;
   }

   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      IBlockState iblockstate = this.getDefaultState().withProperty(POWERED, false).withProperty(ATTACHED, false).withProperty(SUSPENDED, false);
      if (facing.getAxis().isHorizontal()) {
         iblockstate = iblockstate.withProperty(FACING, facing);
      }

      return iblockstate;
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      this.func_176260_a(worldIn, pos, state, false, false, -1, (IBlockState)null);
   }

   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (neighborBlock != this && this.checkForDrop(worldIn, pos, state)) {
         EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
         if (!worldIn.getBlockState(pos.offset(enumfacing.getOpposite())).getBlock().isNormalCube()) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
         }
      }

   }

   public void func_176260_a(World worldIn, BlockPos pos, IBlockState hookState, boolean p_176260_4_, boolean p_176260_5_, int p_176260_6_, IBlockState p_176260_7_) {
      EnumFacing enumfacing = (EnumFacing)hookState.getValue(FACING);
      boolean flag = (Boolean)hookState.getValue(ATTACHED);
      boolean flag1 = (Boolean)hookState.getValue(POWERED);
      boolean flag2 = !World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
      boolean flag3 = !p_176260_4_;
      boolean flag4 = false;
      int i = 0;
      IBlockState[] aiblockstate = new IBlockState[42];

      BlockPos blockpos1;
      for(int j = 1; j < 42; ++j) {
         blockpos1 = pos.offset(enumfacing, j);
         IBlockState iblockstate = worldIn.getBlockState(blockpos1);
         if (iblockstate.getBlock() == Blocks.tripwire_hook) {
            if (iblockstate.getValue(FACING) == enumfacing.getOpposite()) {
               i = j;
            }
            break;
         }

         if (iblockstate.getBlock() != Blocks.tripwire && j != p_176260_6_) {
            aiblockstate[j] = null;
            flag3 = false;
         } else {
            if (j == p_176260_6_) {
               iblockstate = (IBlockState)Objects.firstNonNull(p_176260_7_, iblockstate);
            }

            boolean flag5 = !(Boolean)iblockstate.getValue(BlockTripWire.DISARMED);
            boolean flag6 = (Boolean)iblockstate.getValue(BlockTripWire.POWERED);
            boolean flag7 = (Boolean)iblockstate.getValue(BlockTripWire.SUSPENDED);
            flag3 &= flag7 == flag2;
            flag4 |= flag5 && flag6;
            aiblockstate[j] = iblockstate;
            if (j == p_176260_6_) {
               worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
               flag3 &= flag5;
            }
         }
      }

      flag3 &= i > 1;
      flag4 &= flag3;
      IBlockState iblockstate1 = this.getDefaultState().withProperty(ATTACHED, flag3).withProperty(POWERED, flag4);
      if (i > 0) {
         blockpos1 = pos.offset(enumfacing, i);
         EnumFacing enumfacing1 = enumfacing.getOpposite();
         worldIn.setBlockState(blockpos1, iblockstate1.withProperty(FACING, enumfacing1), 3);
         this.func_176262_b(worldIn, blockpos1, enumfacing1);
         this.func_180694_a(worldIn, blockpos1, flag3, flag4, flag, flag1);
      }

      this.func_180694_a(worldIn, pos, flag3, flag4, flag, flag1);
      if (!p_176260_4_) {
         worldIn.setBlockState(pos, iblockstate1.withProperty(FACING, enumfacing), 3);
         if (p_176260_5_) {
            this.func_176262_b(worldIn, pos, enumfacing);
         }
      }

      if (flag != flag3) {
         for(int k = 1; k < i; ++k) {
            BlockPos blockpos2 = pos.offset(enumfacing, k);
            IBlockState iblockstate2 = aiblockstate[k];
            if (iblockstate2 != null && worldIn.getBlockState(blockpos2).getBlock() != Blocks.air) {
               worldIn.setBlockState(blockpos2, iblockstate2.withProperty(ATTACHED, flag3), 3);
            }
         }
      }

   }

   public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      this.func_176260_a(worldIn, pos, state, false, true, -1, (IBlockState)null);
   }

   private void func_180694_a(World worldIn, BlockPos pos, boolean p_180694_3_, boolean p_180694_4_, boolean p_180694_5_, boolean p_180694_6_) {
      if (p_180694_4_ && !p_180694_6_) {
         worldIn.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D, "random.click", 0.4F, 0.6F);
      } else if (!p_180694_4_ && p_180694_6_) {
         worldIn.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D, "random.click", 0.4F, 0.5F);
      } else if (p_180694_3_ && !p_180694_5_) {
         worldIn.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D, "random.click", 0.4F, 0.7F);
      } else if (!p_180694_3_ && p_180694_5_) {
         worldIn.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D, "random.bowhit", 0.4F, 1.2F / (worldIn.rand.nextFloat() * 0.2F + 0.9F));
      }

   }

   private void func_176262_b(World worldIn, BlockPos p_176262_2_, EnumFacing p_176262_3_) {
      worldIn.notifyNeighborsOfStateChange(p_176262_2_, this);
      worldIn.notifyNeighborsOfStateChange(p_176262_2_.offset(p_176262_3_.getOpposite()), this);
   }

   private boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.canPlaceBlockAt(worldIn, pos)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
         return false;
      } else {
         return true;
      }
   }

   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      float f = 0.1875F;
      switch($SWITCH_TABLE$net$minecraft$util$EnumFacing()[((EnumFacing)worldIn.getBlockState(pos).getValue(FACING)).ordinal()]) {
      case 3:
         this.setBlockBounds(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
         break;
      case 4:
         this.setBlockBounds(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
         break;
      case 5:
         this.setBlockBounds(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
         break;
      case 6:
         this.setBlockBounds(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
      }

   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      boolean flag = (Boolean)state.getValue(ATTACHED);
      boolean flag1 = (Boolean)state.getValue(POWERED);
      if (flag || flag1) {
         this.func_176260_a(worldIn, pos, state, true, false, -1, (IBlockState)null);
      }

      if (flag1) {
         worldIn.notifyNeighborsOfStateChange(pos, this);
         worldIn.notifyNeighborsOfStateChange(pos.offset(((EnumFacing)state.getValue(FACING)).getOpposite()), this);
      }

      super.breakBlock(worldIn, pos, state);
   }

   public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return (Boolean)state.getValue(POWERED) ? 15 : 0;
   }

   public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return !(Boolean)state.getValue(POWERED) ? 0 : (state.getValue(FACING) == side ? 15 : 0);
   }

   public boolean canProvidePower() {
      return true;
   }

   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT_MIPPED;
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3)).withProperty(POWERED, (meta & 8) > 0).withProperty(ATTACHED, (meta & 4) > 0);
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      int i = i | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
      if ((Boolean)state.getValue(POWERED)) {
         i |= 8;
      }

      if ((Boolean)state.getValue(ATTACHED)) {
         i |= 4;
      }

      return i;
   }

   protected BlockState createBlockState() {
      return new BlockState(this, new IProperty[]{FACING, POWERED, ATTACHED, SUSPENDED});
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
