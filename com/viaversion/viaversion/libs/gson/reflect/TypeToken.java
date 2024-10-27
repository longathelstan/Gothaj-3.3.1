package com.viaversion.viaversion.libs.gson.reflect;

import com.viaversion.viaversion.libs.gson.internal.$Gson$Types;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TypeToken<T> {
   private final Class<? super T> rawType;
   private final Type type;
   private final int hashCode;

   protected TypeToken() {
      this.type = this.getTypeTokenTypeArgument();
      this.rawType = $Gson$Types.getRawType(this.type);
      this.hashCode = this.type.hashCode();
   }

   private TypeToken(Type type) {
      this.type = $Gson$Types.canonicalize((Type)Objects.requireNonNull(type));
      this.rawType = $Gson$Types.getRawType(this.type);
      this.hashCode = this.type.hashCode();
   }

   private Type getTypeTokenTypeArgument() {
      Type superclass = this.getClass().getGenericSuperclass();
      if (superclass instanceof ParameterizedType) {
         ParameterizedType parameterized = (ParameterizedType)superclass;
         if (parameterized.getRawType() == TypeToken.class) {
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
         }
      } else if (superclass == TypeToken.class) {
         throw new IllegalStateException("TypeToken must be created with a type argument: new TypeToken<...>() {}; When using code shrinkers (ProGuard, R8, ...) make sure that generic signatures are preserved.");
      }

      throw new IllegalStateException("Must only create direct subclasses of TypeToken");
   }

   public final Class<? super T> getRawType() {
      return this.rawType;
   }

   public final Type getType() {
      return this.type;
   }

   /** @deprecated */
   @Deprecated
   public boolean isAssignableFrom(Class<?> cls) {
      return this.isAssignableFrom((Type)cls);
   }

   /** @deprecated */
   @Deprecated
   public boolean isAssignableFrom(Type from) {
      if (from == null) {
         return false;
      } else if (this.type.equals(from)) {
         return true;
      } else if (this.type instanceof Class) {
         return this.rawType.isAssignableFrom($Gson$Types.getRawType(from));
      } else if (this.type instanceof ParameterizedType) {
         return isAssignableFrom(from, (ParameterizedType)this.type, new HashMap());
      } else if (!(this.type instanceof GenericArrayType)) {
         throw buildUnexpectedTypeError(this.type, Class.class, ParameterizedType.class, GenericArrayType.class);
      } else {
         return this.rawType.isAssignableFrom($Gson$Types.getRawType(from)) && isAssignableFrom(from, (GenericArrayType)this.type);
      }
   }

   /** @deprecated */
   @Deprecated
   public boolean isAssignableFrom(TypeToken<?> token) {
      return this.isAssignableFrom(token.getType());
   }

   private static boolean isAssignableFrom(Type from, GenericArrayType to) {
      Type toGenericComponentType = to.getGenericComponentType();
      if (!(toGenericComponentType instanceof ParameterizedType)) {
         return true;
      } else {
         Type t = from;
         if (from instanceof GenericArrayType) {
            t = ((GenericArrayType)from).getGenericComponentType();
         } else if (from instanceof Class) {
            Class classType;
            for(classType = (Class)from; classType.isArray(); classType = classType.getComponentType()) {
            }

            t = classType;
         }

         return isAssignableFrom((Type)t, (ParameterizedType)toGenericComponentType, new HashMap());
      }
   }

   private static boolean isAssignableFrom(Type from, ParameterizedType to, Map<String, Type> typeVarMap) {
      if (from == null) {
         return false;
      } else if (to.equals(from)) {
         return true;
      } else {
         Class<?> clazz = $Gson$Types.getRawType(from);
         ParameterizedType ptype = null;
         if (from instanceof ParameterizedType) {
            ptype = (ParameterizedType)from;
         }

         Type[] tArgs;
         int i;
         Type arg;
         if (ptype != null) {
            tArgs = ptype.getActualTypeArguments();
            TypeVariable<?>[] tParams = clazz.getTypeParameters();

            for(i = 0; i < tArgs.length; ++i) {
               arg = tArgs[i];

               TypeVariable var;
               TypeVariable v;
               for(var = tParams[i]; arg instanceof TypeVariable; arg = (Type)typeVarMap.get(v.getName())) {
                  v = (TypeVariable)arg;
               }

               typeVarMap.put(var.getName(), arg);
            }

            if (typeEquals(ptype, to, typeVarMap)) {
               return true;
            }
         }

         tArgs = clazz.getGenericInterfaces();
         int var12 = tArgs.length;

         for(i = 0; i < var12; ++i) {
            arg = tArgs[i];
            if (isAssignableFrom(arg, to, new HashMap(typeVarMap))) {
               return true;
            }
         }

         Type sType = clazz.getGenericSuperclass();
         return isAssignableFrom(sType, to, new HashMap(typeVarMap));
      }
   }

   private static boolean typeEquals(ParameterizedType from, ParameterizedType to, Map<String, Type> typeVarMap) {
      if (from.getRawType().equals(to.getRawType())) {
         Type[] fromArgs = from.getActualTypeArguments();
         Type[] toArgs = to.getActualTypeArguments();

         for(int i = 0; i < fromArgs.length; ++i) {
            if (!matches(fromArgs[i], toArgs[i], typeVarMap)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static AssertionError buildUnexpectedTypeError(Type token, Class<?>... expected) {
      StringBuilder exceptionMessage = new StringBuilder("Unexpected type. Expected one of: ");
      Class[] var3 = expected;
      int var4 = expected.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Class<?> clazz = var3[var5];
         exceptionMessage.append(clazz.getName()).append(", ");
      }

      exceptionMessage.append("but got: ").append(token.getClass().getName()).append(", for type token: ").append(token.toString()).append('.');
      return new AssertionError(exceptionMessage.toString());
   }

   private static boolean matches(Type from, Type to, Map<String, Type> typeMap) {
      return to.equals(from) || from instanceof TypeVariable && to.equals(typeMap.get(((TypeVariable)from).getName()));
   }

   public final int hashCode() {
      return this.hashCode;
   }

   public final boolean equals(Object o) {
      return o instanceof TypeToken && $Gson$Types.equals(this.type, ((TypeToken)o).type);
   }

   public final String toString() {
      return $Gson$Types.typeToString(this.type);
   }

   public static TypeToken<?> get(Type type) {
      return new TypeToken(type);
   }

   public static <T> TypeToken<T> get(Class<T> type) {
      return new TypeToken(type);
   }

   public static TypeToken<?> getParameterized(Type rawType, Type... typeArguments) {
      Objects.requireNonNull(rawType);
      Objects.requireNonNull(typeArguments);
      if (!(rawType instanceof Class)) {
         throw new IllegalArgumentException("rawType must be of type Class, but was " + rawType);
      } else {
         Class<?> rawClass = (Class)rawType;
         TypeVariable<?>[] typeVariables = rawClass.getTypeParameters();
         int expectedArgsCount = typeVariables.length;
         int actualArgsCount = typeArguments.length;
         if (actualArgsCount != expectedArgsCount) {
            throw new IllegalArgumentException(rawClass.getName() + " requires " + expectedArgsCount + " type arguments, but got " + actualArgsCount);
         } else {
            for(int i = 0; i < expectedArgsCount; ++i) {
               Type typeArgument = typeArguments[i];
               Class<?> rawTypeArgument = $Gson$Types.getRawType(typeArgument);
               TypeVariable<?> typeVariable = typeVariables[i];
               Type[] var10 = typeVariable.getBounds();
               int var11 = var10.length;

               for(int var12 = 0; var12 < var11; ++var12) {
                  Type bound = var10[var12];
                  Class<?> rawBound = $Gson$Types.getRawType(bound);
                  if (!rawBound.isAssignableFrom(rawTypeArgument)) {
                     throw new IllegalArgumentException("Type argument " + typeArgument + " does not satisfy bounds for type variable " + typeVariable + " declared by " + rawType);
                  }
               }
            }

            return new TypeToken($Gson$Types.newParameterizedTypeWithOwner((Type)null, rawType, typeArguments));
         }
      }
   }

   public static TypeToken<?> getArray(Type componentType) {
      return new TypeToken($Gson$Types.arrayOf(componentType));
   }
}
