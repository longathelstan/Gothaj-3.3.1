package com.viaversion.viaversion.util;

import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Pair<X, Y> {
   private final X key;
   private Y value;

   public Pair(@Nullable X key, @Nullable Y value) {
      this.key = key;
      this.value = value;
   }

   @Nullable
   public X key() {
      return this.key;
   }

   @Nullable
   public Y value() {
      return this.value;
   }

   /** @deprecated */
   @Deprecated
   @Nullable
   public X getKey() {
      return this.key;
   }

   /** @deprecated */
   @Deprecated
   @Nullable
   public Y getValue() {
      return this.value;
   }

   /** @deprecated */
   @Deprecated
   public void setValue(@Nullable Y value) {
      this.value = value;
   }

   public String toString() {
      return "Pair{" + this.key + ", " + this.value + '}';
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Pair<?, ?> pair = (Pair)o;
         return !Objects.equals(this.key, pair.key) ? false : Objects.equals(this.value, pair.value);
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.key != null ? this.key.hashCode() : 0;
      result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
      return result;
   }
}
