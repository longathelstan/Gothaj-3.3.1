package com.viaversion.viaversion.rewriter;

import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.api.minecraft.TagData;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.fastutil.ints.IntArrayList;
import com.viaversion.viaversion.libs.fastutil.ints.IntList;
import com.viaversion.viaversion.util.Key;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.Nullable;

public class TagRewriter<C extends ClientboundPacketType> {
   private static final int[] EMPTY_ARRAY = new int[0];
   private final Protocol<C, ?, ?, ?> protocol;
   private final Map<RegistryType, List<TagData>> newTags = new EnumMap(RegistryType.class);
   private final Map<RegistryType, Map<String, String>> toRename = new EnumMap(RegistryType.class);
   private final Set<String> toRemove = new HashSet();

   public TagRewriter(Protocol<C, ?, ?, ?> protocol) {
      this.protocol = protocol;
   }

   public void loadFromMappingData() {
      RegistryType[] var1 = RegistryType.getValues();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         RegistryType type = var1[var3];
         List<TagData> tags = this.protocol.getMappingData().getTags(type);
         if (tags != null) {
            this.getOrComputeNewTags(type).addAll(tags);
         }
      }

   }

   public void removeTags(String registryKey) {
      this.toRemove.add(registryKey);
   }

   public void renameTag(RegistryType type, String registryKey, String renameTo) {
      ((Map)this.toRename.computeIfAbsent(type, (t) -> {
         return new HashMap();
      })).put(registryKey, renameTo);
   }

   public void addEmptyTag(RegistryType tagType, String tagId) {
      this.getOrComputeNewTags(tagType).add(new TagData(tagId, EMPTY_ARRAY));
   }

   public void addEmptyTags(RegistryType tagType, String... tagIds) {
      List<TagData> tagList = this.getOrComputeNewTags(tagType);
      String[] var4 = tagIds;
      int var5 = tagIds.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String id = var4[var6];
         tagList.add(new TagData(id, EMPTY_ARRAY));
      }

   }

   public void addEntityTag(String tagId, EntityType... entities) {
      int[] ids = new int[entities.length];

      for(int i = 0; i < entities.length; ++i) {
         ids[i] = entities[i].getId();
      }

      this.addTagRaw(RegistryType.ENTITY, tagId, ids);
   }

   public void addTag(RegistryType tagType, String tagId, int... unmappedIds) {
      List<TagData> newTags = this.getOrComputeNewTags(tagType);
      IdRewriteFunction rewriteFunction = this.getRewriter(tagType);
      if (rewriteFunction != null) {
         for(int i = 0; i < unmappedIds.length; ++i) {
            int unmappedId = unmappedIds[i];
            unmappedIds[i] = rewriteFunction.rewrite(unmappedId);
         }
      }

      newTags.add(new TagData(tagId, unmappedIds));
   }

   public void addTagRaw(RegistryType tagType, String tagId, int... ids) {
      this.getOrComputeNewTags(tagType).add(new TagData(tagId, ids));
   }

   public void register(C packetType, @Nullable RegistryType readUntilType) {
      this.protocol.registerClientbound(packetType, this.getHandler(readUntilType));
   }

   public void registerGeneric(C packetType) {
      this.protocol.registerClientbound(packetType, this.getGenericHandler());
   }

   public void registerGeneric(State state, ClientboundPacketType packetType) {
      this.protocol.registerClientbound(state, packetType, this.getGenericHandler());
   }

   public PacketHandler getHandler(@Nullable RegistryType readUntilType) {
      return (wrapper) -> {
         RegistryType[] var3 = RegistryType.getValues();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            RegistryType type = var3[var5];
            this.handle(wrapper, this.getRewriter(type), this.getNewTags(type), (Map)this.toRename.get(type));
            if (type == readUntilType) {
               break;
            }
         }

      };
   }

   public PacketHandler getGenericHandler() {
      return (wrapper) -> {
         int length = (Integer)wrapper.passthrough(Type.VAR_INT);
         int editedLength = length;

         for(int i = 0; i < length; ++i) {
            String registryKey = (String)wrapper.read(Type.STRING);
            if (this.toRemove.contains(registryKey)) {
               --editedLength;
               wrapper.set(Type.VAR_INT, 0, editedLength);
               int tagsSize = (Integer)wrapper.read(Type.VAR_INT);

               for(int j = 0; j < tagsSize; ++j) {
                  wrapper.read(Type.STRING);
                  wrapper.read(Type.VAR_INT_ARRAY_PRIMITIVE);
               }
            } else {
               wrapper.write(Type.STRING, registryKey);
               registryKey = Key.stripMinecraftNamespace(registryKey);
               RegistryType type = RegistryType.getByKey(registryKey);
               if (type != null) {
                  this.handle(wrapper, this.getRewriter(type), this.getNewTags(type), (Map)this.toRename.get(type));
               } else {
                  this.handle(wrapper, (IdRewriteFunction)null, (List)null, (Map)null);
               }
            }
         }

      };
   }

   public void handle(PacketWrapper wrapper, @Nullable IdRewriteFunction rewriteFunction, @Nullable List<TagData> newTags) throws Exception {
      this.handle(wrapper, rewriteFunction, newTags, (Map)null);
   }

   public void handle(PacketWrapper wrapper, @Nullable IdRewriteFunction rewriteFunction, @Nullable List<TagData> newTags, @Nullable Map<String, String> tagsToRename) throws Exception {
      int tagsSize = (Integer)wrapper.read(Type.VAR_INT);
      wrapper.write(Type.VAR_INT, newTags != null ? tagsSize + newTags.size() : tagsSize);

      for(int i = 0; i < tagsSize; ++i) {
         String key = (String)wrapper.read(Type.STRING);
         if (tagsToRename != null) {
            String renamedKey = (String)tagsToRename.get(key);
            if (renamedKey != null) {
               key = renamedKey;
            }
         }

         wrapper.write(Type.STRING, key);
         int[] ids = (int[])wrapper.read(Type.VAR_INT_ARRAY_PRIMITIVE);
         if (rewriteFunction != null) {
            IntList idList = new IntArrayList(ids.length);
            int[] var10 = ids;
            int var11 = ids.length;

            for(int var12 = 0; var12 < var11; ++var12) {
               int id = var10[var12];
               int mappedId = rewriteFunction.rewrite(id);
               if (mappedId != -1) {
                  idList.add(mappedId);
               }
            }

            wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, idList.toArray(EMPTY_ARRAY));
         } else {
            wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, ids);
         }
      }

      if (newTags != null) {
         Iterator var15 = newTags.iterator();

         while(var15.hasNext()) {
            TagData tag = (TagData)var15.next();
            wrapper.write(Type.STRING, tag.identifier());
            wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, tag.entries());
         }
      }

   }

   @Nullable
   public List<TagData> getNewTags(RegistryType tagType) {
      return (List)this.newTags.get(tagType);
   }

   public List<TagData> getOrComputeNewTags(RegistryType tagType) {
      return (List)this.newTags.computeIfAbsent(tagType, (type) -> {
         return new ArrayList();
      });
   }

   @Nullable
   public IdRewriteFunction getRewriter(RegistryType tagType) {
      MappingData mappingData = this.protocol.getMappingData();
      switch(tagType) {
      case BLOCK:
         return mappingData != null && mappingData.getBlockMappings() != null ? mappingData::getNewBlockId : null;
      case ITEM:
         return mappingData != null && mappingData.getItemMappings() != null ? mappingData::getNewItemId : null;
      case ENTITY:
         return this.protocol.getEntityRewriter() != null ? (id) -> {
            return this.protocol.getEntityRewriter().newEntityId(id);
         } : null;
      case FLUID:
      case GAME_EVENT:
      default:
         return null;
      }
   }
}
