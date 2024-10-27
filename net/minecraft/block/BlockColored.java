package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockColored extends Block {
   public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);

   public BlockColored(Material materialIn) {
      super(materialIn);
      this.setDefaultState(this.blockState.getBaseState().withProperty(COLOR, EnumDyeColor.WHITE));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   public int damageDropped(IBlockState state) {
      return ((EnumDyeColor)state.getValue(COLOR)).getMetadata();
   }

   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      EnumDyeColor[] var7;
      int var6 = (var7 = EnumDyeColor.values()).length;

      for(int var5 = 0; var5 < var6; ++var5) {
         EnumDyeColor enumdyecolor = var7[var5];
         list.add(new ItemStack(itemIn, 1, enumdyecolor.getMetadata()));
      }

   }

   public MapColor getMapColor(IBlockState state) {
      return ((EnumDyeColor)state.getValue(COLOR)).getMapColor();
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(COLOR, EnumDyeColor.byMetadata(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumDyeColor)state.getValue(COLOR)).getMetadata();
   }

   protected BlockState createBlockState() {
      return new BlockState(this, new IProperty[]{COLOR});
   }
}
