package com.viaversion.viaversion.api.type.types;

import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class LongArrayType extends Type<long[]> {
   public LongArrayType() {
      super(long[].class);
   }

   public long[] read(ByteBuf buffer) throws Exception {
      int length = Type.VAR_INT.readPrimitive(buffer);
      long[] array = new long[length];

      for(int i = 0; i < array.length; ++i) {
         array[i] = buffer.readLong();
      }

      return array;
   }

   public void write(ByteBuf buffer, long[] object) throws Exception {
      Type.VAR_INT.writePrimitive(buffer, object.length);
      long[] var3 = object;
      int var4 = object.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         long l = var3[var5];
         buffer.writeLong(l);
      }

   }
}
