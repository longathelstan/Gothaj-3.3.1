package com.viaversion.viaversion.unsupported;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

public final class UnsupportedMethods {
   private final String className;
   private final Set<String> methodNames;

   public UnsupportedMethods(String className, Set<String> methodNames) {
      this.className = className;
      this.methodNames = Collections.unmodifiableSet(methodNames);
   }

   public String getClassName() {
      return this.className;
   }

   public final boolean findMatch() {
      try {
         Method[] var1 = Class.forName(this.className).getDeclaredMethods();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Method method = var1[var3];
            if (this.methodNames.contains(method.getName())) {
               return true;
            }
         }
      } catch (ClassNotFoundException var5) {
      }

      return false;
   }
}
