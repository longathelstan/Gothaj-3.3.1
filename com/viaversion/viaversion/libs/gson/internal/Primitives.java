package com.viaversion.viaversion.libs.gson.internal;

import java.lang.reflect.Type;

public final class Primitives {
   private Primitives() {
   }

   public static boolean isPrimitive(Type type) {
      return type instanceof Class && ((Class)type).isPrimitive();
   }

   public static boolean isWrapperType(Type type) {
      return type == Integer.class || type == Float.class || type == Byte.class || type == Double.class || type == Long.class || type == Character.class || type == Boolean.class || type == Short.class || type == Void.class;
   }

   public static <T> Class<T> wrap(Class<T> type) {
      if (type == Integer.TYPE) {
         return Integer.class;
      } else if (type == Float.TYPE) {
         return Float.class;
      } else if (type == Byte.TYPE) {
         return Byte.class;
      } else if (type == Double.TYPE) {
         return Double.class;
      } else if (type == Long.TYPE) {
         return Long.class;
      } else if (type == Character.TYPE) {
         return Character.class;
      } else if (type == Boolean.TYPE) {
         return Boolean.class;
      } else if (type == Short.TYPE) {
         return Short.class;
      } else {
         return type == Void.TYPE ? Void.class : type;
      }
   }

   public static <T> Class<T> unwrap(Class<T> type) {
      if (type == Integer.class) {
         return Integer.TYPE;
      } else if (type == Float.class) {
         return Float.TYPE;
      } else if (type == Byte.class) {
         return Byte.TYPE;
      } else if (type == Double.class) {
         return Double.TYPE;
      } else if (type == Long.class) {
         return Long.TYPE;
      } else if (type == Character.class) {
         return Character.TYPE;
      } else if (type == Boolean.class) {
         return Boolean.TYPE;
      } else if (type == Short.class) {
         return Short.TYPE;
      } else {
         return type == Void.class ? Void.TYPE : type;
      }
   }
}
