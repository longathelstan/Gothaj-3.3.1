package com.viaversion.viaversion.unsupported;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.platform.UnsupportedSoftware;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class UnsupportedServerSoftware implements UnsupportedSoftware {
   private final String name;
   private final List<String> classNames;
   private final List<UnsupportedMethods> methods;
   private final String reason;

   public UnsupportedServerSoftware(String name, List<String> classNames, List<UnsupportedMethods> methods, String reason) {
      Preconditions.checkNotNull(name);
      Preconditions.checkNotNull(reason);
      Preconditions.checkArgument(!classNames.isEmpty() || !methods.isEmpty());
      this.name = name;
      this.classNames = Collections.unmodifiableList(classNames);
      this.methods = Collections.unmodifiableList(methods);
      this.reason = reason;
   }

   public String getName() {
      return this.name;
   }

   public String getReason() {
      return this.reason;
   }

   @Nullable
   public final String match() {
      Iterator var1 = this.classNames.iterator();

      while(var1.hasNext()) {
         String className = (String)var1.next();

         try {
            Class.forName(className);
            return this.name;
         } catch (ClassNotFoundException var4) {
         }
      }

      var1 = this.methods.iterator();

      UnsupportedMethods method;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         method = (UnsupportedMethods)var1.next();
      } while(!method.findMatch());

      return this.name;
   }

   public static final class Reason {
      public static final String DANGEROUS_SERVER_SOFTWARE = "You are using server software that - outside of possibly breaking ViaVersion - can also cause severe damage to your server's integrity as a whole.";
      public static final String BREAKING_PROXY_SOFTWARE = "You are using proxy software that intentionally breaks ViaVersion. Please use another proxy software or move ViaVersion to each backend server instead of the proxy.";
   }

   public static final class Builder {
      private final List<String> classNames = new ArrayList();
      private final List<UnsupportedMethods> methods = new ArrayList();
      private String name;
      private String reason;

      public UnsupportedServerSoftware.Builder name(String name) {
         this.name = name;
         return this;
      }

      public UnsupportedServerSoftware.Builder reason(String reason) {
         this.reason = reason;
         return this;
      }

      public UnsupportedServerSoftware.Builder addMethod(String className, String methodName) {
         this.methods.add(new UnsupportedMethods(className, Collections.singleton(methodName)));
         return this;
      }

      public UnsupportedServerSoftware.Builder addMethods(String className, String... methodNames) {
         this.methods.add(new UnsupportedMethods(className, new HashSet(Arrays.asList(methodNames))));
         return this;
      }

      public UnsupportedServerSoftware.Builder addClassName(String className) {
         this.classNames.add(className);
         return this;
      }

      public UnsupportedSoftware build() {
         return new UnsupportedServerSoftware(this.name, this.classNames, this.methods, this.reason);
      }
   }
}
