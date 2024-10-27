package com.viaversion.viabackwards.api.data;

import com.google.common.base.Preconditions;
import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.BiMappings;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.MappingDataBase;
import com.viaversion.viaversion.api.data.Mappings;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.util.Key;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BackwardsMappings extends MappingDataBase {
   private final Class<? extends Protocol<?, ?, ?, ?>> vvProtocolClass;
   protected Int2ObjectMap<MappedItem> backwardsItemMappings;
   private Map<String, String> backwardsSoundMappings;
   private Map<String, String> entityNames;

   public BackwardsMappings(String unmappedVersion, String mappedVersion) {
      this(unmappedVersion, mappedVersion, (Class)null);
   }

   public BackwardsMappings(String unmappedVersion, String mappedVersion, @Nullable Class<? extends Protocol<?, ?, ?, ?>> vvProtocolClass) {
      super(unmappedVersion, mappedVersion);
      Preconditions.checkArgument(vvProtocolClass == null || !vvProtocolClass.isAssignableFrom(BackwardsProtocol.class));
      this.vvProtocolClass = vvProtocolClass;
   }

   protected void loadExtras(CompoundTag data) {
      CompoundTag itemNames = (CompoundTag)data.get("itemnames");
      CompoundTag extraItemData;
      Iterator var4;
      Entry entry;
      StringTag name;
      if (itemNames != null) {
         Preconditions.checkNotNull(this.itemMappings);
         this.backwardsItemMappings = new Int2ObjectOpenHashMap(itemNames.size());
         extraItemData = (CompoundTag)data.get("itemdata");

         int id;
         Integer customModelData;
         for(var4 = itemNames.entrySet().iterator(); var4.hasNext(); this.backwardsItemMappings.put(id, new MappedItem(this.getNewItemId(id), name.getValue(), customModelData))) {
            entry = (Entry)var4.next();
            name = (StringTag)entry.getValue();
            id = Integer.parseInt((String)entry.getKey());
            customModelData = null;
            if (extraItemData != null && extraItemData.contains((String)entry.getKey())) {
               CompoundTag entryTag = (CompoundTag)extraItemData.get((String)entry.getKey());
               NumberTag customModelDataTag = (NumberTag)entryTag.get("custom_model_data");
               customModelData = customModelDataTag != null ? customModelDataTag.asInt() : null;
            }
         }
      }

      extraItemData = (CompoundTag)data.get("entitynames");
      if (extraItemData != null) {
         this.entityNames = new HashMap(extraItemData.size());
         var4 = extraItemData.entrySet().iterator();

         while(var4.hasNext()) {
            entry = (Entry)var4.next();
            name = (StringTag)entry.getValue();
            this.entityNames.put((String)entry.getKey(), name.getValue());
         }
      }

      CompoundTag soundNames = (CompoundTag)data.get("soundnames");
      if (soundNames != null) {
         this.backwardsSoundMappings = new HashMap(soundNames.size());
         Iterator var12 = soundNames.entrySet().iterator();

         while(var12.hasNext()) {
            Entry<String, Tag> entry = (Entry)var12.next();
            StringTag mappedTag = (StringTag)entry.getValue();
            this.backwardsSoundMappings.put((String)entry.getKey(), mappedTag.getValue());
         }
      }

   }

   @Nullable
   protected BiMappings loadBiMappings(CompoundTag data, String key) {
      if (key.equals("items") && this.vvProtocolClass != null) {
         Mappings mappings = super.loadMappings(data, key);
         MappingData mappingData = Via.getManager().getProtocolManager().getProtocol(this.vvProtocolClass).getMappingData();
         if (mappingData != null && mappingData.getItemMappings() != null) {
            return ItemMappings.of(mappings, mappingData.getItemMappings());
         }
      }

      return super.loadBiMappings(data, key);
   }

   public int getNewItemId(int id) {
      return this.itemMappings.getNewId(id);
   }

   public int getNewBlockId(int id) {
      return this.blockMappings.getNewId(id);
   }

   public int getOldItemId(int id) {
      return this.checkValidity(id, this.itemMappings.inverse().getNewId(id), "item");
   }

   @Nullable
   public MappedItem getMappedItem(int id) {
      return this.backwardsItemMappings != null ? (MappedItem)this.backwardsItemMappings.get(id) : null;
   }

   @Nullable
   public String getMappedNamedSound(String id) {
      return this.backwardsSoundMappings == null ? null : (String)this.backwardsSoundMappings.get(Key.stripMinecraftNamespace(id));
   }

   @Nullable
   public String mappedEntityName(String entityName) {
      if (this.entityNames == null) {
         ViaBackwards.getPlatform().getLogger().severe("No entity mappings found when requesting them for " + entityName);
         (new Exception()).printStackTrace();
         return null;
      } else {
         return (String)this.entityNames.get(entityName);
      }
   }

   @Nullable
   public Int2ObjectMap<MappedItem> getBackwardsItemMappings() {
      return this.backwardsItemMappings;
   }

   @Nullable
   public Map<String, String> getBackwardsSoundMappings() {
      return this.backwardsSoundMappings;
   }

   @Nullable
   public Class<? extends Protocol<?, ?, ?, ?>> getViaVersionProtocolClass() {
      return this.vvProtocolClass;
   }

   protected Logger getLogger() {
      return ViaBackwards.getPlatform().getLogger();
   }

   @Nullable
   protected CompoundTag readNBTFile(String name) {
      return VBMappingDataLoader.loadNBTFromDir(name);
   }
}
