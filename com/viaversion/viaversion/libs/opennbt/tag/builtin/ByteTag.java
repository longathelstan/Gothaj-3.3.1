package com.viaversion.viaversion.libs.opennbt.tag.builtin;

import com.viaversion.viaversion.libs.opennbt.tag.limiter.TagLimiter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteTag extends NumberTag {
   public static final int ID = 1;
   private byte value;

   public ByteTag() {
      this((byte)0);
   }

   public ByteTag(byte value) {
      this.value = value;
   }

   public static ByteTag read(DataInput in, TagLimiter tagLimiter) throws IOException {
      tagLimiter.countByte();
      return new ByteTag(in.readByte());
   }

   /** @deprecated */
   @Deprecated
   public Byte getValue() {
      return this.value;
   }

   public String asRawString() {
      return Byte.toString(this.value);
   }

   public void setValue(byte value) {
      this.value = value;
   }

   public void write(DataOutput out) throws IOException {
      out.writeByte(this.value);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ByteTag byteTag = (ByteTag)o;
         return this.value == byteTag.value;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.value;
   }

   public ByteTag copy() {
      return new ByteTag(this.value);
   }

   public byte asByte() {
      return this.value;
   }

   public short asShort() {
      return (short)this.value;
   }

   public int asInt() {
      return this.value;
   }

   public long asLong() {
      return (long)this.value;
   }

   public float asFloat() {
      return (float)this.value;
   }

   public double asDouble() {
      return (double)this.value;
   }

   public int getTagId() {
      return 1;
   }
}
