package com.viaversion.viaversion.api.type.types;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class VarIntArrayType extends Type<int[]> {
   public VarIntArrayType() {
      super(int[].class);
   }

   public int[] read(ByteBuf buffer) throws Exception {
      int length = Type.VAR_INT.readPrimitive(buffer);
      Preconditions.checkArgument(buffer.isReadable(length));
      int[] array = new int[length];

      for(int i = 0; i < array.length; ++i) {
         array[i] = Type.VAR_INT.readPrimitive(buffer);
      }

      return array;
   }

   public void write(ByteBuf buffer, int[] object) throws Exception {
      Type.VAR_INT.writePrimitive(buffer, object.length);
      int[] var3 = object;
      int var4 = object.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int i = var3[var5];
         Type.VAR_INT.writePrimitive(buffer, i);
      }

   }
}
