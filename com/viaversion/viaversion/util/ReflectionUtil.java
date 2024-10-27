package com.viaversion.viaversion.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionUtil {
   public static Object invokeStatic(Class<?> clazz, String method) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      Method m = clazz.getDeclaredMethod(method);
      return m.invoke((Object)null);
   }

   public static Object invoke(Object o, String method) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      Method m = o.getClass().getDeclaredMethod(method);
      return m.invoke(o);
   }

   public static <T> T getStatic(Class<?> clazz, String f, Class<T> type) throws NoSuchFieldException, IllegalAccessException {
      Field field = clazz.getDeclaredField(f);
      field.setAccessible(true);
      return type.cast(field.get((Object)null));
   }

   public static void setStatic(Class<?> clazz, String f, Object value) throws NoSuchFieldException, IllegalAccessException {
      Field field = clazz.getDeclaredField(f);
      field.setAccessible(true);
      field.set((Object)null, value);
   }

   public static <T> T getSuper(Object o, String f, Class<T> type) throws NoSuchFieldException, IllegalAccessException {
      Field field = o.getClass().getSuperclass().getDeclaredField(f);
      field.setAccessible(true);
      return type.cast(field.get(o));
   }

   public static <T> T get(Object instance, Class<?> clazz, String f, Class<T> type) throws NoSuchFieldException, IllegalAccessException {
      Field field = clazz.getDeclaredField(f);
      field.setAccessible(true);
      return type.cast(field.get(instance));
   }

   public static <T> T get(Object o, String f, Class<T> type) throws NoSuchFieldException, IllegalAccessException {
      Field field = o.getClass().getDeclaredField(f);
      field.setAccessible(true);
      return type.cast(field.get(o));
   }

   public static <T> T getPublic(Object o, String f, Class<T> type) throws NoSuchFieldException, IllegalAccessException {
      Field field = o.getClass().getField(f);
      field.setAccessible(true);
      return type.cast(field.get(o));
   }

   public static void set(Object o, String f, Object value) throws NoSuchFieldException, IllegalAccessException {
      Field field = o.getClass().getDeclaredField(f);
      field.setAccessible(true);
      field.set(o, value);
   }

   public static final class ClassReflection {
      private final Class<?> handle;
      private final Map<String, Field> fields;
      private final Map<String, Method> methods;

      public ClassReflection(Class<?> handle) {
         this(handle, true);
      }

      public ClassReflection(Class<?> handle, boolean recursive) {
         this.fields = new ConcurrentHashMap();
         this.methods = new ConcurrentHashMap();
         this.handle = handle;
         this.scanFields(handle, recursive);
         this.scanMethods(handle, recursive);
      }

      private void scanFields(Class<?> host, boolean recursive) {
         if (recursive && host.getSuperclass() != null && host.getSuperclass() != Object.class) {
            this.scanFields(host.getSuperclass(), true);
         }

         Field[] var3 = host.getDeclaredFields();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Field field = var3[var5];
            field.setAccessible(true);
            this.fields.put(field.getName(), field);
         }

      }

      private void scanMethods(Class<?> host, boolean recursive) {
         if (recursive && host.getSuperclass() != null && host.getSuperclass() != Object.class) {
            this.scanMethods(host.getSuperclass(), true);
         }

         Method[] var3 = host.getDeclaredMethods();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Method method = var3[var5];
            method.setAccessible(true);
            this.methods.put(method.getName(), method);
         }

      }

      public Object newInstance() throws ReflectiveOperationException {
         return this.handle.getConstructor().newInstance();
      }

      public Field getField(String name) {
         return (Field)this.fields.get(name);
      }

      public void setFieldValue(String fieldName, Object instance, Object value) throws IllegalAccessException {
         this.getField(fieldName).set(instance, value);
      }

      public <T> T getFieldValue(String fieldName, Object instance, Class<T> type) throws IllegalAccessException {
         return type.cast(this.getField(fieldName).get(instance));
      }

      public <T> T invokeMethod(Class<T> type, String methodName, Object instance, Object... args) throws InvocationTargetException, IllegalAccessException {
         return type.cast(this.getMethod(methodName).invoke(instance, args));
      }

      public Method getMethod(String name) {
         return (Method)this.methods.get(name);
      }

      public Collection<Field> getFields() {
         return Collections.unmodifiableCollection(this.fields.values());
      }

      public Collection<Method> getMethods() {
         return Collections.unmodifiableCollection(this.methods.values());
      }
   }
}
