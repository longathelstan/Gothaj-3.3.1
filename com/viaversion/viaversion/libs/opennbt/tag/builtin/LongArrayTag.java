package com.viaversion.viaversion.libs.opennbt.tag.builtin;

import com.viaversion.viaversion.libs.opennbt.tag.limiter.TagLimiter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class LongArrayTag extends NumberArrayTag {
   public static final int ID = 12;
   private static final long[] EMPTY_ARRAY = new long[0];
   private long[] value;

   public LongArrayTag() {
      this(EMPTY_ARRAY);
   }

   public LongArrayTag(long[] value) {
      if (value == null) {
         throw new NullPointerException("value cannot be null");
      } else {
         this.value = value;
      }
   }

   public static LongArrayTag read(DataInput in, TagLimiter tagLimiter) throws IOException {
      tagLimiter.countInt();
      long[] value = new long[in.readInt()];
      tagLimiter.countBytes(8 * value.length);

      for(int index = 0; index < value.length; ++index) {
         value[index] = in.readLong();
      }

      return new LongArrayTag(value);
   }

   public long[] getValue() {
      return this.value;
   }

   public String asRawString() {
      return Arrays.toString(this.value);
   }

   public void setValue(long[] value) {
      if (value == null) {
         throw new NullPointerException("value cannot be null");
      } else {
         this.value = value;
      }
   }

   public long getValue(int index) {
      return this.value[index];
   }

   public void setValue(int index, long value) {
      this.value[index] = value;
   }

   public int length() {
      return this.value.length;
   }

   public ListTag toListTag() {
      ListTag list = new ListTag(LongTag.class);
      long[] var2 = this.value;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         long l = var2[var4];
         list.add(new LongTag(l));
      }

      return list;
   }

   public void write(DataOutput out) throws IOException {
      out.writeInt(this.value.length);
      long[] var2 = this.value;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         long l = var2[var4];
         out.writeLong(l);
      }

   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         LongArrayTag that = (LongArrayTag)o;
         return Arrays.equals(this.value, that.value);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.value);
   }

   public LongArrayTag copy() {
      return new LongArrayTag((long[])this.value.clone());
   }

   public int getTagId() {
      return 12;
   }
}
