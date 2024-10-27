package net.minecraft.block;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import xyz.cucumber.base.Client;
import xyz.cucumber.base.events.ext.EventAirCollide;

public class BlockAir extends Block {
   private static Map mapOriginalOpacity = new IdentityHashMap();

   protected BlockAir() {
      super(Material.air);
   }

   public int getRenderType() {
      return -1;
   }

   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      EventAirCollide event = new EventAirCollide(worldIn, pos, state, this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
      Client.INSTANCE.getEventBus().call(event);
      return event.isCancelled() ? null : event.returnValue;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
      return false;
   }

   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
   }

   public boolean isReplaceable(World worldIn, BlockPos pos) {
      return true;
   }

   public static void setLightOpacity(Block p_setLightOpacity_0_, int p_setLightOpacity_1_) {
      if (!mapOriginalOpacity.containsKey(p_setLightOpacity_0_)) {
         mapOriginalOpacity.put(p_setLightOpacity_0_, p_setLightOpacity_0_.lightOpacity);
      }

      p_setLightOpacity_0_.lightOpacity = p_setLightOpacity_1_;
   }

   public static void restoreLightOpacity(Block p_restoreLightOpacity_0_) {
      if (mapOriginalOpacity.containsKey(p_restoreLightOpacity_0_)) {
         int i = (Integer)mapOriginalOpacity.get(p_restoreLightOpacity_0_);
         setLightOpacity(p_restoreLightOpacity_0_, i);
      }

   }
}
