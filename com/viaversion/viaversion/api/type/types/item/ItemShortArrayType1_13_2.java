package com.viaversion.viaversion.api.type.types.item;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class ItemShortArrayType1_13_2 extends BaseItemArrayType {
   public Item[] read(ByteBuf buffer) throws Exception {
      int amount = Type.SHORT.readPrimitive(buffer);
      Item[] array = new Item[amount];

      for(int i = 0; i < amount; ++i) {
         array[i] = (Item)Type.ITEM1_13_2.read(buffer);
      }

      return array;
   }

   public void write(ByteBuf buffer, Item[] object) throws Exception {
      Type.SHORT.writePrimitive(buffer, (short)object.length);
      Item[] var3 = object;
      int var4 = object.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Item o = var3[var5];
         Type.ITEM1_13_2.write(buffer, o);
      }

   }
}
