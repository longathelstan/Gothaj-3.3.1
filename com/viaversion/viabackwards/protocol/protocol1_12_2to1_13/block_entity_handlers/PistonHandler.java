package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.block_entity_handlers;

import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.Protocol1_12_2To1_13;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.providers.BackwardsBlockEntityProvider;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringJoiner;
import java.util.Map.Entry;

public class PistonHandler implements BackwardsBlockEntityProvider.BackwardsBlockEntityHandler {
   private final Map<String, Integer> pistonIds = new HashMap();

   public PistonHandler() {
      if (Via.getConfig().isServersideBlockConnections()) {
         Map<String, Integer> keyToId = ConnectionData.getKeyToId();
         Iterator var2 = keyToId.entrySet().iterator();

         while(var2.hasNext()) {
            Entry<String, Integer> entry = (Entry)var2.next();
            if (((String)entry.getKey()).contains("piston")) {
               this.addEntries((String)entry.getKey(), (Integer)entry.getValue());
            }
         }
      } else {
         ListTag blockStates = (ListTag)MappingDataLoader.loadNBT("blockstates-1.13.nbt").get("blockstates");

         for(int id = 0; id < blockStates.size(); ++id) {
            StringTag state = (StringTag)blockStates.get(id);
            String key = state.getValue();
            if (key.contains("piston")) {
               this.addEntries(key, id);
            }
         }
      }

   }

   private void addEntries(String data, int id) {
      id = Protocol1_12_2To1_13.MAPPINGS.getNewBlockStateId(id);
      this.pistonIds.put(data, id);
      String substring = data.substring(10);
      if (substring.startsWith("piston") || substring.startsWith("sticky_piston")) {
         String[] split = data.substring(0, data.length() - 1).split("\\[");
         String[] properties = split[1].split(",");
         data = split[0] + "[" + properties[1] + "," + properties[0] + "]";
         this.pistonIds.put(data, id);
      }
   }

   public CompoundTag transform(UserConnection user, int blockId, CompoundTag tag) {
      CompoundTag blockState = (CompoundTag)tag.get("blockState");
      if (blockState == null) {
         return tag;
      } else {
         String dataFromTag = this.getDataFromTag(blockState);
         if (dataFromTag == null) {
            return tag;
         } else {
            Integer id = (Integer)this.pistonIds.get(dataFromTag);
            if (id == null) {
               return tag;
            } else {
               tag.put("blockId", new IntTag(id >> 4));
               tag.put("blockData", new IntTag(id & 15));
               return tag;
            }
         }
      }
   }

   private String getDataFromTag(CompoundTag tag) {
      StringTag name = (StringTag)tag.get("Name");
      if (name == null) {
         return null;
      } else {
         CompoundTag properties = (CompoundTag)tag.get("Properties");
         if (properties == null) {
            return name.getValue();
         } else {
            StringJoiner joiner = new StringJoiner(",", name.getValue() + "[", "]");
            Iterator var5 = properties.iterator();

            while(var5.hasNext()) {
               Entry<String, Tag> entry = (Entry)var5.next();
               if (entry.getValue() instanceof StringTag) {
                  joiner.add((String)entry.getKey() + "=" + ((StringTag)entry.getValue()).getValue());
               }
            }

            return joiner.toString();
         }
      }
   }
}
