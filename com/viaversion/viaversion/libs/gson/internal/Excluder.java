package com.viaversion.viaversion.libs.gson.internal;

import com.viaversion.viaversion.libs.gson.ExclusionStrategy;
import com.viaversion.viaversion.libs.gson.FieldAttributes;
import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.TypeAdapterFactory;
import com.viaversion.viaversion.libs.gson.annotations.Expose;
import com.viaversion.viaversion.libs.gson.annotations.Since;
import com.viaversion.viaversion.libs.gson.annotations.Until;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class Excluder implements TypeAdapterFactory, Cloneable {
   private static final double IGNORE_VERSIONS = -1.0D;
   public static final Excluder DEFAULT = new Excluder();
   private double version = -1.0D;
   private int modifiers = 136;
   private boolean serializeInnerClasses = true;
   private boolean requireExpose;
   private List<ExclusionStrategy> serializationStrategies = Collections.emptyList();
   private List<ExclusionStrategy> deserializationStrategies = Collections.emptyList();

   protected Excluder clone() {
      try {
         return (Excluder)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new AssertionError(var2);
      }
   }

   public Excluder withVersion(double ignoreVersionsAfter) {
      Excluder result = this.clone();
      result.version = ignoreVersionsAfter;
      return result;
   }

   public Excluder withModifiers(int... modifiers) {
      Excluder result = this.clone();
      result.modifiers = 0;
      int[] var3 = modifiers;
      int var4 = modifiers.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int modifier = var3[var5];
         result.modifiers |= modifier;
      }

      return result;
   }

   public Excluder disableInnerClassSerialization() {
      Excluder result = this.clone();
      result.serializeInnerClasses = false;
      return result;
   }

   public Excluder excludeFieldsWithoutExposeAnnotation() {
      Excluder result = this.clone();
      result.requireExpose = true;
      return result;
   }

   public Excluder withExclusionStrategy(ExclusionStrategy exclusionStrategy, boolean serialization, boolean deserialization) {
      Excluder result = this.clone();
      if (serialization) {
         result.serializationStrategies = new ArrayList(this.serializationStrategies);
         result.serializationStrategies.add(exclusionStrategy);
      }

      if (deserialization) {
         result.deserializationStrategies = new ArrayList(this.deserializationStrategies);
         result.deserializationStrategies.add(exclusionStrategy);
      }

      return result;
   }

   public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
      Class<?> rawType = type.getRawType();
      boolean excludeClass = this.excludeClassChecks(rawType);
      final boolean skipSerialize = excludeClass || this.excludeClassInStrategy(rawType, true);
      final boolean skipDeserialize = excludeClass || this.excludeClassInStrategy(rawType, false);
      return !skipSerialize && !skipDeserialize ? null : new TypeAdapter<T>() {
         private TypeAdapter<T> delegate;

         public T read(JsonReader in) throws IOException {
            if (skipDeserialize) {
               in.skipValue();
               return null;
            } else {
               return this.delegate().read(in);
            }
         }

         public void write(JsonWriter out, T value) throws IOException {
            if (skipSerialize) {
               out.nullValue();
            } else {
               this.delegate().write(out, value);
            }
         }

         private TypeAdapter<T> delegate() {
            TypeAdapter<T> d = this.delegate;
            return d != null ? d : (this.delegate = gson.getDelegateAdapter(Excluder.this, type));
         }
      };
   }

   public boolean excludeField(Field field, boolean serialize) {
      if ((this.modifiers & field.getModifiers()) != 0) {
         return true;
      } else if (this.version != -1.0D && !this.isValidVersion((Since)field.getAnnotation(Since.class), (Until)field.getAnnotation(Until.class))) {
         return true;
      } else if (field.isSynthetic()) {
         return true;
      } else {
         if (this.requireExpose) {
            label69: {
               Expose annotation = (Expose)field.getAnnotation(Expose.class);
               if (annotation != null) {
                  if (serialize) {
                     if (annotation.serialize()) {
                        break label69;
                     }
                  } else if (annotation.deserialize()) {
                     break label69;
                  }
               }

               return true;
            }
         }

         if (!this.serializeInnerClasses && this.isInnerClass(field.getType())) {
            return true;
         } else if (this.isAnonymousOrNonStaticLocal(field.getType())) {
            return true;
         } else {
            List<ExclusionStrategy> list = serialize ? this.serializationStrategies : this.deserializationStrategies;
            if (!list.isEmpty()) {
               FieldAttributes fieldAttributes = new FieldAttributes(field);
               Iterator var5 = list.iterator();

               while(var5.hasNext()) {
                  ExclusionStrategy exclusionStrategy = (ExclusionStrategy)var5.next();
                  if (exclusionStrategy.shouldSkipField(fieldAttributes)) {
                     return true;
                  }
               }
            }

            return false;
         }
      }
   }

   private boolean excludeClassChecks(Class<?> clazz) {
      if (this.version != -1.0D && !this.isValidVersion((Since)clazz.getAnnotation(Since.class), (Until)clazz.getAnnotation(Until.class))) {
         return true;
      } else {
         return !this.serializeInnerClasses && this.isInnerClass(clazz) ? true : this.isAnonymousOrNonStaticLocal(clazz);
      }
   }

   public boolean excludeClass(Class<?> clazz, boolean serialize) {
      return this.excludeClassChecks(clazz) || this.excludeClassInStrategy(clazz, serialize);
   }

   private boolean excludeClassInStrategy(Class<?> clazz, boolean serialize) {
      List<ExclusionStrategy> list = serialize ? this.serializationStrategies : this.deserializationStrategies;
      Iterator var4 = list.iterator();

      ExclusionStrategy exclusionStrategy;
      do {
         if (!var4.hasNext()) {
            return false;
         }

         exclusionStrategy = (ExclusionStrategy)var4.next();
      } while(!exclusionStrategy.shouldSkipClass(clazz));

      return true;
   }

   private boolean isAnonymousOrNonStaticLocal(Class<?> clazz) {
      return !Enum.class.isAssignableFrom(clazz) && !this.isStatic(clazz) && (clazz.isAnonymousClass() || clazz.isLocalClass());
   }

   private boolean isInnerClass(Class<?> clazz) {
      return clazz.isMemberClass() && !this.isStatic(clazz);
   }

   private boolean isStatic(Class<?> clazz) {
      return (clazz.getModifiers() & 8) != 0;
   }

   private boolean isValidVersion(Since since, Until until) {
      return this.isValidSince(since) && this.isValidUntil(until);
   }

   private boolean isValidSince(Since annotation) {
      if (annotation != null) {
         double annotationVersion = annotation.value();
         return this.version >= annotationVersion;
      } else {
         return true;
      }
   }

   private boolean isValidUntil(Until annotation) {
      if (annotation != null) {
         double annotationVersion = annotation.value();
         return this.version < annotationVersion;
      } else {
         return true;
      }
   }
}
