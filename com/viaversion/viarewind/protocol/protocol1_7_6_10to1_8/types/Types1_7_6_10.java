package com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types;

import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.item.ItemArrayType;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.item.ItemType;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.item.NBTType;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.metadata.MetadataListType;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.metadata.MetadataType;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.primitive.ByteIntArrayType;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.primitive.PositionUYType;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import java.util.List;

public class Types1_7_6_10 {
   public static final Type<int[]> BYTE_INT_ARRAY = new ByteIntArrayType();
   public static final Type<Position> SHORT_POSITION;
   public static final Type<Position> INT_POSITION;
   public static final Type<Position> BYTE_POSITION;
   public static final Type<Position> U_BYTE_POSITION;
   public static final Type<CompoundTag> COMPRESSED_NBT;
   public static final Type<Item> COMPRESSED_NBT_ITEM;
   public static final Type<Item[]> COMPRESSED_NBT_ITEM_ARRAY;
   public static final Type<Metadata> METADATA;
   public static final Type<List<Metadata>> METADATA_LIST;

   static {
      SHORT_POSITION = new PositionUYType(Type.SHORT, (value) -> {
         return (short)value;
      });
      INT_POSITION = new PositionUYType(Type.INT, (value) -> {
         return value;
      });
      BYTE_POSITION = new PositionUYType(Type.BYTE, (value) -> {
         return (byte)value;
      });
      U_BYTE_POSITION = new PositionUYType(Type.UNSIGNED_BYTE, (value) -> {
         return (short)value;
      });
      COMPRESSED_NBT = new NBTType();
      COMPRESSED_NBT_ITEM = new ItemType();
      COMPRESSED_NBT_ITEM_ARRAY = new ItemArrayType();
      METADATA = new MetadataType();
      METADATA_LIST = new MetadataListType();
   }
}
