package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.providers;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.BlockConnectionStorage;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PacketBlockConnectionProvider extends BlockConnectionProvider {
   public void storeBlock(UserConnection connection, int x, int y, int z, int blockState) {
      ((BlockConnectionStorage)connection.get(BlockConnectionStorage.class)).store(x, y, z, blockState);
   }

   public void removeBlock(UserConnection connection, int x, int y, int z) {
      ((BlockConnectionStorage)connection.get(BlockConnectionStorage.class)).remove(x, y, z);
   }

   public int getBlockData(UserConnection connection, int x, int y, int z) {
      return ((BlockConnectionStorage)connection.get(BlockConnectionStorage.class)).get(x, y, z);
   }

   public void clearStorage(UserConnection connection) {
      ((BlockConnectionStorage)connection.get(BlockConnectionStorage.class)).clear();
   }

   public void modifiedBlock(UserConnection connection, Position position) {
      ((BlockConnectionStorage)connection.get(BlockConnectionStorage.class)).markModified(position);
   }

   public void unloadChunk(UserConnection connection, int x, int z) {
      ((BlockConnectionStorage)connection.get(BlockConnectionStorage.class)).unloadChunk(x, z);
   }

   public void unloadChunkSection(UserConnection connection, int chunkX, int chunkY, int chunkZ) {
      ((BlockConnectionStorage)connection.get(BlockConnectionStorage.class)).unloadSection(chunkX, chunkY, chunkZ);
   }

   public boolean storesBlocks(UserConnection connection, @Nullable Position pos) {
      if (pos != null && connection != null) {
         return !((BlockConnectionStorage)connection.get(BlockConnectionStorage.class)).recentlyModified(pos);
      } else {
         return true;
      }
   }

   public UserBlockData forUser(UserConnection connection) {
      BlockConnectionStorage storage = (BlockConnectionStorage)connection.get(BlockConnectionStorage.class);
      return (x, y, z) -> {
         return storage.get(x, y, z);
      };
   }
}
