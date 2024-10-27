package com.viaversion.viaversion.api.minecraft.chunks;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface ChunkSection {
   int SIZE = 4096;
   int BIOME_SIZE = 64;

   static int index(int x, int y, int z) {
      return y << 8 | z << 4 | x;
   }

   static int xFromIndex(int idx) {
      return idx & 15;
   }

   static int yFromIndex(int idx) {
      return idx >> 8 & 15;
   }

   static int zFromIndex(int idx) {
      return idx >> 4 & 15;
   }

   /** @deprecated */
   @Deprecated
   default int getFlatBlock(int idx) {
      return this.palette(PaletteType.BLOCKS).idAt(idx);
   }

   /** @deprecated */
   @Deprecated
   default int getFlatBlock(int x, int y, int z) {
      return this.getFlatBlock(index(x, y, z));
   }

   /** @deprecated */
   @Deprecated
   default void setFlatBlock(int idx, int id) {
      this.palette(PaletteType.BLOCKS).setIdAt(idx, id);
   }

   /** @deprecated */
   @Deprecated
   default void setFlatBlock(int x, int y, int z, int id) {
      this.setFlatBlock(index(x, y, z), id);
   }

   /** @deprecated */
   @Deprecated
   default int getBlockWithoutData(int x, int y, int z) {
      return this.getFlatBlock(x, y, z) >> 4;
   }

   /** @deprecated */
   @Deprecated
   default int getBlockData(int x, int y, int z) {
      return this.getFlatBlock(x, y, z) & 15;
   }

   /** @deprecated */
   @Deprecated
   default void setBlockWithData(int x, int y, int z, int type, int data) {
      this.setFlatBlock(index(x, y, z), type << 4 | data & 15);
   }

   /** @deprecated */
   @Deprecated
   default void setBlockWithData(int idx, int type, int data) {
      this.setFlatBlock(idx, type << 4 | data & 15);
   }

   /** @deprecated */
   @Deprecated
   default void setPaletteIndex(int idx, int index) {
      this.palette(PaletteType.BLOCKS).setPaletteIndexAt(idx, index);
   }

   /** @deprecated */
   @Deprecated
   default int getPaletteIndex(int idx) {
      return this.palette(PaletteType.BLOCKS).paletteIndexAt(idx);
   }

   /** @deprecated */
   @Deprecated
   default int getPaletteSize() {
      return this.palette(PaletteType.BLOCKS).size();
   }

   /** @deprecated */
   @Deprecated
   default int getPaletteEntry(int index) {
      return this.palette(PaletteType.BLOCKS).idByIndex(index);
   }

   /** @deprecated */
   @Deprecated
   default void setPaletteEntry(int index, int id) {
      this.palette(PaletteType.BLOCKS).setIdByIndex(index, id);
   }

   /** @deprecated */
   @Deprecated
   default void replacePaletteEntry(int oldId, int newId) {
      this.palette(PaletteType.BLOCKS).replaceId(oldId, newId);
   }

   /** @deprecated */
   @Deprecated
   default void addPaletteEntry(int id) {
      this.palette(PaletteType.BLOCKS).addId(id);
   }

   /** @deprecated */
   @Deprecated
   default void clearPalette() {
      this.palette(PaletteType.BLOCKS).clear();
   }

   int getNonAirBlocksCount();

   void setNonAirBlocksCount(int var1);

   default boolean hasLight() {
      return this.getLight() != null;
   }

   @Nullable
   ChunkSectionLight getLight();

   void setLight(@Nullable ChunkSectionLight var1);

   @Nullable
   DataPalette palette(PaletteType var1);

   void addPalette(PaletteType var1, DataPalette var2);

   void removePalette(PaletteType var1);
}
