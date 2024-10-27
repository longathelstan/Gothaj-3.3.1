package com.viaversion.viaversion.api.minecraft.chunks;

import java.util.EnumMap;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ChunkSectionImpl implements ChunkSection {
   private final EnumMap<PaletteType, DataPalette> palettes = new EnumMap(PaletteType.class);
   private ChunkSectionLight light;
   private int nonAirBlocksCount;

   public ChunkSectionImpl() {
   }

   public ChunkSectionImpl(boolean holdsLight) {
      this.addPalette(PaletteType.BLOCKS, new DataPaletteImpl(4096));
      if (holdsLight) {
         this.light = new ChunkSectionLightImpl();
      }

   }

   public ChunkSectionImpl(boolean holdsLight, int expectedPaletteLength) {
      this.addPalette(PaletteType.BLOCKS, new DataPaletteImpl(4096, expectedPaletteLength));
      if (holdsLight) {
         this.light = new ChunkSectionLightImpl();
      }

   }

   public int getNonAirBlocksCount() {
      return this.nonAirBlocksCount;
   }

   public void setNonAirBlocksCount(int nonAirBlocksCount) {
      this.nonAirBlocksCount = nonAirBlocksCount;
   }

   @Nullable
   public ChunkSectionLight getLight() {
      return this.light;
   }

   public void setLight(@Nullable ChunkSectionLight light) {
      this.light = light;
   }

   public DataPalette palette(PaletteType type) {
      return (DataPalette)this.palettes.get(type);
   }

   public void addPalette(PaletteType type, DataPalette palette) {
      this.palettes.put(type, palette);
   }

   public void removePalette(PaletteType type) {
      this.palettes.remove(type);
   }
}
