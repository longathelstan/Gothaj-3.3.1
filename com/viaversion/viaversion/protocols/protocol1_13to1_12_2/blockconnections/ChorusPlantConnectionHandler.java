package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockFace;
import com.viaversion.viaversion.api.minecraft.Position;
import java.util.ArrayList;
import java.util.List;

public class ChorusPlantConnectionHandler extends AbstractFenceConnectionHandler {
   private final int endstone = ConnectionData.getId("minecraft:end_stone");

   static List<ConnectionData.ConnectorInitAction> init() {
      List<ConnectionData.ConnectorInitAction> actions = new ArrayList(2);
      ChorusPlantConnectionHandler handler = new ChorusPlantConnectionHandler();
      actions.add(handler.getInitAction("minecraft:chorus_plant"));
      actions.add(handler.getExtraAction());
      return actions;
   }

   public ChorusPlantConnectionHandler() {
      super((String)null);
   }

   public ConnectionData.ConnectorInitAction getExtraAction() {
      return (blockData) -> {
         if (blockData.getMinecraftKey().equals("minecraft:chorus_flower")) {
            this.getBlockStates().add(blockData.getSavedBlockStateId());
         }

      };
   }

   protected byte getStates(WrappedBlockData blockData) {
      byte states = super.getStates(blockData);
      if (blockData.getValue("up").equals("true")) {
         states = (byte)(states | 16);
      }

      if (blockData.getValue("down").equals("true")) {
         states = (byte)(states | 32);
      }

      return states;
   }

   protected byte statesSize() {
      return 64;
   }

   protected byte getStates(UserConnection user, Position position, int blockState) {
      byte states = super.getStates(user, position, blockState);
      if (this.connects(BlockFace.TOP, this.getBlockData(user, position.getRelative(BlockFace.TOP)), false)) {
         states = (byte)(states | 16);
      }

      if (this.connects(BlockFace.BOTTOM, this.getBlockData(user, position.getRelative(BlockFace.BOTTOM)), false)) {
         states = (byte)(states | 32);
      }

      return states;
   }

   protected boolean connects(BlockFace side, int blockState, boolean pre1_12) {
      return this.getBlockStates().contains(blockState) || side == BlockFace.BOTTOM && blockState == this.endstone;
   }
}
