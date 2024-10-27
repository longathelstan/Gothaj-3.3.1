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

public class DoorConnectionHandler extends ConnectionHandler {
   private static final Int2ObjectMap<DoorConnectionHandler.DoorData> DOOR_DATA_MAP = new Int2ObjectOpenHashMap();
   private static final Map<Short, Integer> CONNECTED_STATES = new HashMap();

   static ConnectionData.ConnectorInitAction init() {
      List<String> baseDoors = new LinkedList();
      baseDoors.add("minecraft:oak_door");
      baseDoors.add("minecraft:birch_door");
      baseDoors.add("minecraft:jungle_door");
      baseDoors.add("minecraft:dark_oak_door");
      baseDoors.add("minecraft:acacia_door");
      baseDoors.add("minecraft:spruce_door");
      baseDoors.add("minecraft:iron_door");
      DoorConnectionHandler connectionHandler = new DoorConnectionHandler();
      return (blockData) -> {
         int type = baseDoors.indexOf(blockData.getMinecraftKey());
         if (type != -1) {
            int id = blockData.getSavedBlockStateId();
            DoorConnectionHandler.DoorData doorData = new DoorConnectionHandler.DoorData(blockData.getValue("half").equals("lower"), blockData.getValue("hinge").equals("right"), blockData.getValue("powered").equals("true"), blockData.getValue("open").equals("true"), BlockFace.valueOf(blockData.getValue("facing").toUpperCase(Locale.ROOT)), type);
            DOOR_DATA_MAP.put(id, doorData);
            CONNECTED_STATES.put(getStates(doorData), id);
            ConnectionData.connectionHandlerMap.put(id, connectionHandler);
         }
      };
   }

   private static short getStates(DoorConnectionHandler.DoorData doorData) {
      short s = 0;
      if (doorData.isLower()) {
         s = (short)(s | 1);
      }

      if (doorData.isOpen()) {
         s = (short)(s | 2);
      }

      if (doorData.isPowered()) {
         s = (short)(s | 4);
      }

      if (doorData.isRightHinge()) {
         s = (short)(s | 8);
      }

      s = (short)(s | doorData.getFacing().ordinal() << 4);
      s = (short)(s | (doorData.getType() & 7) << 6);
      return s;
   }

   public int connect(UserConnection user, Position position, int blockState) {
      DoorConnectionHandler.DoorData doorData = (DoorConnectionHandler.DoorData)DOOR_DATA_MAP.get(blockState);
      if (doorData == null) {
         return blockState;
      } else {
         short s = 0;
         short s = (short)(s | (doorData.getType() & 7) << 6);
         DoorConnectionHandler.DoorData lowerHalf;
         if (doorData.isLower()) {
            lowerHalf = (DoorConnectionHandler.DoorData)DOOR_DATA_MAP.get(this.getBlockData(user, position.getRelative(BlockFace.TOP)));
            if (lowerHalf == null) {
               return blockState;
            }

            s = (short)(s | 1);
            if (doorData.isOpen()) {
               s = (short)(s | 2);
            }

            if (lowerHalf.isPowered()) {
               s = (short)(s | 4);
            }

            if (lowerHalf.isRightHinge()) {
               s = (short)(s | 8);
            }

            s = (short)(s | doorData.getFacing().ordinal() << 4);
         } else {
            lowerHalf = (DoorConnectionHandler.DoorData)DOOR_DATA_MAP.get(this.getBlockData(user, position.getRelative(BlockFace.BOTTOM)));
            if (lowerHalf == null) {
               return blockState;
            }

            if (lowerHalf.isOpen()) {
               s = (short)(s | 2);
            }

            if (doorData.isPowered()) {
               s = (short)(s | 4);
            }

            if (doorData.isRightHinge()) {
               s = (short)(s | 8);
            }

            s = (short)(s | lowerHalf.getFacing().ordinal() << 4);
         }

         Integer newBlockState = (Integer)CONNECTED_STATES.get(s);
         return newBlockState == null ? blockState : newBlockState;
      }
   }

   private static final class DoorData {
      private final boolean lower;
      private final boolean rightHinge;
      private final boolean powered;
      private final boolean open;
      private final BlockFace facing;
      private final int type;

      private DoorData(boolean lower, boolean rightHinge, boolean powered, boolean open, BlockFace facing, int type) {
         this.lower = lower;
         this.rightHinge = rightHinge;
         this.powered = powered;
         this.open = open;
         this.facing = facing;
         this.type = type;
      }

      public boolean isLower() {
         return this.lower;
      }

      public boolean isRightHinge() {
         return this.rightHinge;
      }

      public boolean isPowered() {
         return this.powered;
      }

      public boolean isOpen() {
         return this.open;
      }

      public BlockFace getFacing() {
         return this.facing;
      }

      public int getType() {
         return this.type;
      }

      // $FF: synthetic method
      DoorData(boolean x0, boolean x1, boolean x2, boolean x3, BlockFace x4, int x5, Object x6) {
         this(x0, x1, x2, x3, x4, x5);
      }
   }
}
