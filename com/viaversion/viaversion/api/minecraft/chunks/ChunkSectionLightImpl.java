package com.viaversion.viaversion.api.minecraft.chunks;

import io.netty.buffer.ByteBuf;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ChunkSectionLightImpl implements ChunkSectionLight {
   private NibbleArray blockLight = new NibbleArray(4096);
   private NibbleArray skyLight;

   public void setBlockLight(byte[] data) {
      if (data.length != 2048) {
         throw new IllegalArgumentException("Data length != 2048");
      } else {
         if (this.blockLight == null) {
            this.blockLight = new NibbleArray(data);
         } else {
            this.blockLight.setHandle(data);
         }

      }
   }

   public void setSkyLight(byte[] data) {
      if (data == null) {
         this.skyLight = null;
      } else if (data.length != 2048) {
         throw new IllegalArgumentException("Data length != 2048");
      } else {
         if (this.skyLight == null) {
            this.skyLight = new NibbleArray(data);
         } else {
            this.skyLight.setHandle(data);
         }

      }
   }

   @Nullable
   public byte[] getBlockLight() {
      return this.blockLight == null ? null : this.blockLight.getHandle();
   }

   @Nullable
   public NibbleArray getBlockLightNibbleArray() {
      return this.blockLight;
   }

   @Nullable
   public byte[] getSkyLight() {
      return this.skyLight == null ? null : this.skyLight.getHandle();
   }

   @Nullable
   public NibbleArray getSkyLightNibbleArray() {
      return this.skyLight;
   }

   public void readBlockLight(ByteBuf input) {
      if (this.blockLight == null) {
         this.blockLight = new NibbleArray(4096);
      }

      input.readBytes(this.blockLight.getHandle());
   }

   public void readSkyLight(ByteBuf input) {
      if (this.skyLight == null) {
         this.skyLight = new NibbleArray(4096);
      }

      input.readBytes(this.skyLight.getHandle());
   }

   public void writeBlockLight(ByteBuf output) {
      output.writeBytes(this.blockLight.getHandle());
   }

   public void writeSkyLight(ByteBuf output) {
      output.writeBytes(this.skyLight.getHandle());
   }

   public boolean hasSkyLight() {
      return this.skyLight != null;
   }

   public boolean hasBlockLight() {
      return this.blockLight != null;
   }
}
