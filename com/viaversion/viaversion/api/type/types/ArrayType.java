package com.viaversion.viaversion.api.type.types;

import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;
import java.lang.reflect.Array;

public class ArrayType<T> extends Type<T[]> {
   private final Type<T> elementType;

   public ArrayType(Type<T> type) {
      super(type.getTypeName() + " Array", getArrayClass(type.getOutputClass()));
      this.elementType = type;
   }

   public static Class<?> getArrayClass(Class<?> componentType) {
      return Array.newInstance(componentType, 0).getClass();
   }

   public T[] read(ByteBuf buffer) throws Exception {
      int amount = Type.VAR_INT.readPrimitive(buffer);
      T[] array = (Object[])((Object[])Array.newInstance(this.elementType.getOutputClass(), amount));

      for(int i = 0; i < amount; ++i) {
         array[i] = this.elementType.read(buffer);
      }

      return array;
   }

   public void write(ByteBuf buffer, T[] object) throws Exception {
      Type.VAR_INT.writePrimitive(buffer, object.length);
      Object[] var3 = object;
      int var4 = object.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         T o = var3[var5];
         this.elementType.write(buffer, o);
      }

   }
}
