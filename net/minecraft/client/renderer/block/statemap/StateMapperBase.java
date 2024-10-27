package net.minecraft.client.renderer.block.statemap;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

public abstract class StateMapperBase implements IStateMapper {
   protected Map<IBlockState, ModelResourceLocation> mapStateModelLocations = Maps.newLinkedHashMap();

   public String getPropertyString(Map<IProperty, Comparable> p_178131_1_) {
      StringBuilder stringbuilder = new StringBuilder();
      Iterator var4 = p_178131_1_.entrySet().iterator();

      while(var4.hasNext()) {
         Entry<IProperty, Comparable> entry = (Entry)var4.next();
         if (stringbuilder.length() != 0) {
            stringbuilder.append(",");
         }

         IProperty iproperty = (IProperty)entry.getKey();
         Comparable comparable = (Comparable)entry.getValue();
         stringbuilder.append(iproperty.getName());
         stringbuilder.append("=");
         stringbuilder.append(iproperty.getName(comparable));
      }

      if (stringbuilder.length() == 0) {
         stringbuilder.append("normal");
      }

      return stringbuilder.toString();
   }

   public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn) {
      Iterator var3 = blockIn.getBlockState().getValidStates().iterator();

      while(var3.hasNext()) {
         IBlockState iblockstate = (IBlockState)var3.next();
         this.mapStateModelLocations.put(iblockstate, this.getModelResourceLocation(iblockstate));
      }

      return this.mapStateModelLocations;
   }

   protected abstract ModelResourceLocation getModelResourceLocation(IBlockState var1);
}
