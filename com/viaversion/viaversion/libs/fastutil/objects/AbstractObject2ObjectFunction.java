package com.viaversion.viaversion.libs.fastutil.objects;

import java.io.Serializable;

public abstract class AbstractObject2ObjectFunction<K, V> implements Object2ObjectFunction<K, V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;
   protected V defRetValue;

   protected AbstractObject2ObjectFunction() {
   }

   public void defaultReturnValue(V rv) {
      this.defRetValue = rv;
   }

   public V defaultReturnValue() {
      return this.defRetValue;
   }
}
