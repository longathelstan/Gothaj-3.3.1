package com.viaversion.viaversion.libs.opennbt.tag.builtin;

import com.viaversion.viaversion.libs.opennbt.tag.limiter.TagLimiter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class ByteArrayTag extends NumberArrayTag {
   public static final int ID = 7;
   private static final byte[] EMPTY_ARRAY = new byte[0];
   private byte[] value;

   public ByteArrayTag() {
      this(EMPTY_ARRAY);
   }

   public ByteArrayTag(byte[] value) {
      if (value == null) {
         throw new NullPointerException("value cannot be null");
      } else {
         this.value = value;
      }
   }

   public static ByteArrayTag read(DataInput in, TagLimiter tagLimiter) throws IOException {
      tagLimiter.countInt();
      byte[] value = new byte[in.readInt()];
      tagLimiter.countBytes(value.length);
      in.readFully(value);
      return new ByteArrayTag(value);
   }

   public byte[] getValue() {
      return this.value;
   }

   public String asRawString() {
      return Arrays.toString(this.value);
   }

   public void setValue(byte[] value) {
      if (value != null) {
         this.value = value;
      }
   }

   public byte getValue(int index) {
      return this.value[index];
   }

   public void setValue(int index, byte value) {
      this.value[index] = value;
   }

   public int length() {
      return this.value.length;
   }

   public ListTag toListTag() {
      ListTag list = new ListTag(ByteTag.class);
      byte[] var2 = this.value;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         byte b = var2[var4];
         list.add(new ByteTag(b));
      }

      return list;
   }

   public void write(DataOutput out) throws IOException {
      out.writeInt(this.value.length);
      out.write(this.value);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ByteArrayTag that = (ByteArrayTag)o;
         return Arrays.equals(this.value, that.value);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.value);
   }

   public ByteArrayTag copy() {
      return new ByteArrayTag((byte[])this.value.clone());
   }

   public int getTagId() {
      return 7;
   }
}
