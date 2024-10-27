package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockFace;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.libs.fastutil.ints.IntOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractStempConnectionHandler extends ConnectionHandler {
   private static final BlockFace[] BLOCK_FACES;
   private final IntSet blockId = new IntOpenHashSet();
   private final int baseStateId;
   private final Map<BlockFace, Integer> stemps = new EnumMap(BlockFace.class);

   protected AbstractStempConnectionHandler(String baseStateId) {
      this.baseStateId = ConnectionData.getId(baseStateId);
   }

   public ConnectionData.ConnectorInitAction getInitAction(String blockId, String toKey) {
      return (blockData) -> {
         if (blockData.getSavedBlockStateId() == thisx.baseStateId || blockId.equals(blockData.getMinecraftKey())) {
            if (blockData.getSavedBlockStateId() != thisx.baseStateId) {
               this.blockId.add(blockData.getSavedBlockStateId());
            }

            ConnectionData.connectionHandlerMap.put(blockData.getSavedBlockStateId(), this);
         }

         if (blockData.getMinecraftKey().equals(toKey)) {
            String facing = blockData.getValue("facing").toUpperCase(Locale.ROOT);
            thisx.stemps.put(BlockFace.valueOf(facing), blockData.getSavedBlockStateId());
         }

      };
   }

   public int connect(UserConnection user, Position position, int blockState) {
      if (blockState != this.baseStateId) {
         return blockState;
      } else {
         BlockFace[] var4 = BLOCK_FACES;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            BlockFace blockFace = var4[var6];
            if (this.blockId.contains(this.getBlockData(user, position.getRelative(blockFace)))) {
               return (Integer)this.stemps.get(blockFace);
            }
         }

         return this.baseStateId;
      }
   }

   static {
      BLOCK_FACES = new BlockFace[]{BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST};
   }
}
