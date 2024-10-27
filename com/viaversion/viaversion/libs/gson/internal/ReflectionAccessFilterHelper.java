package com.viaversion.viaversion.libs.gson.internal;

import com.viaversion.viaversion.libs.gson.ReflectionAccessFilter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

public class ReflectionAccessFilterHelper {
   private ReflectionAccessFilterHelper() {
   }

   public static boolean isJavaType(Class<?> c) {
      return isJavaType(c.getName());
   }

   private static boolean isJavaType(String className) {
      return className.startsWith("java.") || className.startsWith("javax.");
   }

   public static boolean isAndroidType(Class<?> c) {
      return isAndroidType(c.getName());
   }

   private static boolean isAndroidType(String className) {
      return className.startsWith("android.") || className.startsWith("androidx.") || isJavaType(className);
   }

   public static boolean isAnyPlatformType(Class<?> c) {
      String className = c.getName();
      return isAndroidType(className) || className.startsWith("kotlin.") || className.startsWith("kotlinx.") || className.startsWith("scala.");
   }

   public static ReflectionAccessFilter.FilterResult getFilterResult(List<ReflectionAccessFilter> reflectionFilters, Class<?> c) {
      Iterator var2 = reflectionFilters.iterator();

      ReflectionAccessFilter.FilterResult result;
      do {
         if (!var2.hasNext()) {
            return ReflectionAccessFilter.FilterResult.ALLOW;
         }

         ReflectionAccessFilter filter = (ReflectionAccessFilter)var2.next();
         result = filter.check(c);
      } while(result == ReflectionAccessFilter.FilterResult.INDECISIVE);

      return result;
   }

   public static boolean canAccess(AccessibleObject accessibleObject, Object object) {
      return ReflectionAccessFilterHelper.AccessChecker.INSTANCE.canAccess(accessibleObject, object);
   }

   private abstract static class AccessChecker {
      public static final ReflectionAccessFilterHelper.AccessChecker INSTANCE;

      private AccessChecker() {
      }

      public abstract boolean canAccess(AccessibleObject var1, Object var2);

      // $FF: synthetic method
      AccessChecker(Object x0) {
         this();
      }

      static {
         ReflectionAccessFilterHelper.AccessChecker accessChecker = null;
         if (JavaVersion.isJava9OrLater()) {
            try {
               final Method canAccessMethod = AccessibleObject.class.getDeclaredMethod("canAccess", Object.class);
               accessChecker = new ReflectionAccessFilterHelper.AccessChecker() {
                  public boolean canAccess(AccessibleObject accessibleObject, Object object) {
                     try {
                        return (Boolean)canAccessMethod.invoke(accessibleObject, object);
                     } catch (Exception var4) {
                        throw new RuntimeException("Failed invoking canAccess", var4);
                     }
                  }
               };
            } catch (NoSuchMethodException var2) {
            }
         }

         if (accessChecker == null) {
            accessChecker = new ReflectionAccessFilterHelper.AccessChecker() {
               public boolean canAccess(AccessibleObject accessibleObject, Object object) {
                  return true;
               }
            };
         }

         INSTANCE = accessChecker;
      }
   }
}
