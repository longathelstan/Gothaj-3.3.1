package com.viaversion.viaversion.api.minecraft.item;

import com.viaversion.viaversion.libs.gson.annotations.SerializedName;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DataItem implements Item {
   @SerializedName(
      value = "identifier",
      alternate = {"id"}
   )
   private int identifier;
   private byte amount;
   private short data;
   private CompoundTag tag;

   public DataItem() {
   }

   public DataItem(int identifier, byte amount, short data, @Nullable CompoundTag tag) {
      this.identifier = identifier;
      this.amount = amount;
      this.data = data;
      this.tag = tag;
   }

   public DataItem(Item toCopy) {
      this(toCopy.identifier(), (byte)toCopy.amount(), toCopy.data(), toCopy.tag());
   }

   public int identifier() {
      return this.identifier;
   }

   public void setIdentifier(int identifier) {
      this.identifier = identifier;
   }

   public int amount() {
      return this.amount;
   }

   public void setAmount(int amount) {
      if (amount <= 127 && amount >= -128) {
         this.amount = (byte)amount;
      } else {
         throw new IllegalArgumentException("Invalid item amount: " + amount);
      }
   }

   public short data() {
      return this.data;
   }

   public void setData(short data) {
      this.data = data;
   }

   @Nullable
   public CompoundTag tag() {
      return this.tag;
   }

   public void setTag(@Nullable CompoundTag tag) {
      this.tag = tag;
   }

   public Item copy() {
      return new DataItem(this.identifier, this.amount, this.data, this.tag);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         DataItem item = (DataItem)o;
         if (this.identifier != item.identifier) {
            return false;
         } else if (this.amount != item.amount) {
            return false;
         } else {
            return this.data != item.data ? false : Objects.equals(this.tag, item.tag);
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.identifier;
      result = 31 * result + this.amount;
      result = 31 * result + this.data;
      result = 31 * result + (this.tag != null ? this.tag.hashCode() : 0);
      return result;
   }

   public String toString() {
      return "Item{identifier=" + this.identifier + ", amount=" + this.amount + ", data=" + this.data + ", tag=" + this.tag + '}';
   }
}
