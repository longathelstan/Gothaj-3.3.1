package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockFace;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StairConnectionHandler extends ConnectionHandler {
   private static final Int2ObjectMap<StairConnectionHandler.StairData> STAIR_DATA_MAP = new Int2ObjectOpenHashMap();
   private static final Map<Short, Integer> CONNECTED_BLOCKS = new HashMap();

   static ConnectionData.ConnectorInitAction init() {
      List<String> baseStairs = new LinkedList();
      baseStairs.add("minecraft:oak_stairs");
      baseStairs.add("minecraft:cobblestone_stairs");
      baseStairs.add("minecraft:brick_stairs");
      baseStairs.add("minecraft:stone_brick_stairs");
      baseStairs.add("minecraft:nether_brick_stairs");
      baseStairs.add("minecraft:sandstone_stairs");
      baseStairs.add("minecraft:spruce_stairs");
      baseStairs.add("minecraft:birch_stairs");
      baseStairs.add("minecraft:jungle_stairs");
      baseStairs.add("minecraft:quartz_stairs");
      baseStairs.add("minecraft:acacia_stairs");
      baseStairs.add("minecraft:dark_oak_stairs");
      baseStairs.add("minecraft:red_sandstone_stairs");
      baseStairs.add("minecraft:purpur_stairs");
      baseStairs.add("minecraft:prismarine_stairs");
      baseStairs.add("minecraft:prismarine_brick_stairs");
      baseStairs.add("minecraft:dark_prismarine_stairs");
      StairConnectionHandler connectionHandler = new StairConnectionHandler();
      return (blockData) -> {
         int type = baseStairs.indexOf(blockData.getMinecraftKey());
         if (type != -1) {
            if (!blockData.getValue("waterlogged").equals("true")) {
               String var5 = blockData.getValue("shape");
               byte var6 = -1;
               switch(var5.hashCode()) {
               case -1766998696:
                  if (var5.equals("outer_right")) {
                     var6 = 4;
                  }
                  break;
               case -239805709:
                  if (var5.equals("inner_right")) {
                     var6 = 2;
                  }
                  break;
               case 823365712:
                  if (var5.equals("inner_left")) {
                     var6 = 1;
                  }
                  break;
               case 1743932747:
                  if (var5.equals("outer_left")) {
                     var6 = 3;
                  }
                  break;
               case 1787472634:
                  if (var5.equals("straight")) {
                     var6 = 0;
                  }
               }

               byte shape;
               switch(var6) {
               case 0:
                  shape = 0;
                  break;
               case 1:
                  shape = 1;
                  break;
               case 2:
                  shape = 2;
                  break;
               case 3:
                  shape = 3;
                  break;
               case 4:
                  shape = 4;
                  break;
               default:
                  return;
               }

               StairConnectionHandler.StairData stairData = new StairConnectionHandler.StairData(blockData.getValue("half").equals("bottom"), shape, (byte)type, BlockFace.valueOf(blockData.getValue("facing").toUpperCase(Locale.ROOT)));
               STAIR_DATA_MAP.put(blockData.getSavedBlockStateId(), stairData);
               CONNECTED_BLOCKS.put(getStates(stairData), blockData.getSavedBlockStateId());
               ConnectionData.connectionHandlerMap.put(blockData.getSavedBlockStateId(), connectionHandler);
            }
         }
      };
   }

   private static short getStates(StairConnectionHandler.StairData stairData) {
      short s = 0;
      if (stairData.isBottom()) {
         s = (short)(s | 1);
      }

      s = (short)(s | stairData.getShape() << 1);
      s = (short)(s | stairData.getType() << 4);
      s = (short)(s | stairData.getFacing().ordinal() << 9);
      return s;
   }

   public int connect(UserConnection user, Position position, int blockState) {
      StairConnectionHandler.StairData stairData = (StairConnectionHandler.StairData)STAIR_DATA_MAP.get(blockState);
      if (stairData == null) {
         return blockState;
      } else {
         short s = 0;
         if (stairData.isBottom()) {
            s = (short)(s | 1);
         }

         s = (short)(s | this.getShape(user, position, stairData) << 1);
         s = (short)(s | stairData.getType() << 4);
         s = (short)(s | stairData.getFacing().ordinal() << 9);
         Integer newBlockState = (Integer)CONNECTED_BLOCKS.get(s);
         return newBlockState == null ? blockState : newBlockState;
      }
   }

   private int getShape(UserConnection user, Position position, StairConnectionHandler.StairData stair) {
      BlockFace facing = stair.getFacing();
      StairConnectionHandler.StairData relativeStair = (StairConnectionHandler.StairData)STAIR_DATA_MAP.get(this.getBlockData(user, position.getRelative(facing)));
      BlockFace facing2;
      if (relativeStair != null && relativeStair.isBottom() == stair.isBottom()) {
         facing2 = relativeStair.getFacing();
         if (facing.axis() != facing2.axis() && this.checkOpposite(user, stair, position, facing2.opposite())) {
            return facing2 == this.rotateAntiClockwise(facing) ? 3 : 4;
         }
      }

      relativeStair = (StairConnectionHandler.StairData)STAIR_DATA_MAP.get(this.getBlockData(user, position.getRelative(facing.opposite())));
      if (relativeStair != null && relativeStair.isBottom() == stair.isBottom()) {
         facing2 = relativeStair.getFacing();
         if (facing.axis() != facing2.axis() && this.checkOpposite(user, stair, position, facing2)) {
            return facing2 == this.rotateAntiClockwise(facing) ? 1 : 2;
         }
      }

      return 0;
   }

   private boolean checkOpposite(UserConnection user, StairConnectionHandler.StairData stair, Position position, BlockFace face) {
      StairConnectionHandler.StairData relativeStair = (StairConnectionHandler.StairData)STAIR_DATA_MAP.get(this.getBlockData(user, position.getRelative(face)));
      return relativeStair == null || relativeStair.getFacing() != stair.getFacing() || relativeStair.isBottom() != stair.isBottom();
   }

   private BlockFace rotateAntiClockwise(BlockFace face) {
      switch(face) {
      case NORTH:
         return BlockFace.WEST;
      case SOUTH:
         return BlockFace.EAST;
      case EAST:
         return BlockFace.NORTH;
      case WEST:
         return BlockFace.SOUTH;
      default:
         return face;
      }
   }

   private static final class StairData {
      private final boolean bottom;
      private final byte shape;
      private final byte type;
      private final BlockFace facing;

      private StairData(boolean bottom, byte shape, byte type, BlockFace facing) {
         this.bottom = bottom;
         this.shape = shape;
         this.type = type;
         this.facing = facing;
      }

      public boolean isBottom() {
         return this.bottom;
      }

      public byte getShape() {
         return this.shape;
      }

      public byte getType() {
         return this.type;
      }

      public BlockFace getFacing() {
         return this.facing;
      }

      // $FF: synthetic method
      StairData(boolean x0, byte x1, byte x2, BlockFace x3, Object x4) {
         this(x0, x1, x2, x3);
      }
   }
}
