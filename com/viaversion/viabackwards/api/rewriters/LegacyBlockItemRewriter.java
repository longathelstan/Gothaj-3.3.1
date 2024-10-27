package com.viaversion.viabackwards.api.rewriters;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.data.MappedLegacyBlockItem;
import com.viaversion.viabackwards.api.data.VBMappingDataLoader;
import com.viaversion.viabackwards.protocol.protocol1_11_1to1_12.data.BlockColors;
import com.viaversion.viabackwards.utils.Block;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.util.ComponentUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class LegacyBlockItemRewriter<C extends ClientboundPacketType, S extends ServerboundPacketType, T extends BackwardsProtocol<C, ?, ?, S>> extends ItemRewriterBase<C, S, T> {
   private static final Map<String, Int2ObjectMap<MappedLegacyBlockItem>> LEGACY_MAPPINGS = new HashMap();
   protected final Int2ObjectMap<MappedLegacyBlockItem> replacementData;

   private static void addMapping(String key, JsonObject object, Int2ObjectMap<MappedLegacyBlockItem> mappings) {
      int id = object.getAsJsonPrimitive("id").getAsInt();
      JsonPrimitive jsonData = object.getAsJsonPrimitive("data");
      short data = jsonData != null ? jsonData.getAsShort() : 0;
      String name = object.getAsJsonPrimitive("name").getAsString();
      JsonPrimitive blockField = object.getAsJsonPrimitive("block");
      boolean block = blockField != null && blockField.getAsBoolean();
      int from;
      if (key.indexOf(45) == -1) {
         from = key.indexOf(58);
         int unmappedId;
         if (from != -1) {
            short unmappedData = Short.parseShort(key.substring(from + 1));
            unmappedId = Integer.parseInt(key.substring(0, from));
            unmappedId = unmappedId << 4 | unmappedData & 15;
         } else {
            unmappedId = Integer.parseInt(key) << 4;
         }

         mappings.put(unmappedId, new MappedLegacyBlockItem(id, data, name, block));
      } else {
         String[] split = key.split("-", 2);
         from = Integer.parseInt(split[0]);
         int to = Integer.parseInt(split[1]);
         if (name.contains("%color%")) {
            for(int i = from; i <= to; ++i) {
               mappings.put(i << 4, new MappedLegacyBlockItem(id, data, name.replace("%color%", BlockColors.get(i - from)), block));
            }
         } else {
            MappedLegacyBlockItem mappedBlockItem = new MappedLegacyBlockItem(id, data, name, block);

            for(int i = from; i <= to; ++i) {
               mappings.put(i << 4, mappedBlockItem);
            }
         }

      }
   }

   protected LegacyBlockItemRewriter(T protocol) {
      super(protocol, false);
      this.replacementData = (Int2ObjectMap)LEGACY_MAPPINGS.get(protocol.getClass().getSimpleName().split("To")[1].replace("_", "."));
   }

   @Nullable
   public Item handleItemToClient(@Nullable Item item) {
      if (item == null) {
         return null;
      } else {
         MappedLegacyBlockItem data = this.getMappedBlockItem(item.identifier(), item.data());
         if (data == null) {
            return super.handleItemToClient(item);
         } else {
            short originalData = item.data();
            item.setIdentifier(data.getId());
            if (data.getData() != -1) {
               item.setData(data.getData());
            }

            if (data.getName() != null) {
               if (item.tag() == null) {
                  item.setTag(new CompoundTag());
               }

               CompoundTag display = (CompoundTag)item.tag().get("display");
               if (display == null) {
                  item.tag().put("display", display = new CompoundTag());
               }

               StringTag nameTag = (StringTag)display.get("Name");
               if (nameTag == null) {
                  display.put("Name", nameTag = new StringTag(data.getName()));
                  display.put(this.nbtTagName + "|customName", new ByteTag());
               }

               String value = nameTag.getValue();
               if (value.contains("%vb_color%")) {
                  display.put("Name", new StringTag(value.replace("%vb_color%", BlockColors.get(originalData))));
               }
            }

            return item;
         }
      }
   }

   public int handleBlockID(int idx) {
      int type = idx >> 4;
      int meta = idx & 15;
      Block b = this.handleBlock(type, meta);
      return b == null ? idx : b.getId() << 4 | b.getData() & 15;
   }

   @Nullable
   public Block handleBlock(int blockId, int data) {
      MappedLegacyBlockItem settings = this.getMappedBlockItem(blockId, data);
      if (settings != null && settings.isBlock()) {
         Block block = settings.getBlock();
         return block.getData() == -1 ? block.withData(data) : block;
      } else {
         return null;
      }
   }

   @Nullable
   private MappedLegacyBlockItem getMappedBlockItem(int id, int data) {
      MappedLegacyBlockItem mapping = (MappedLegacyBlockItem)this.replacementData.get(id << 4 | data & 15);
      return mapping == null && data != 0 ? (MappedLegacyBlockItem)this.replacementData.get(id << 4) : mapping;
   }

   @Nullable
   private MappedLegacyBlockItem getMappedBlockItem(int rawId) {
      MappedLegacyBlockItem mapping = (MappedLegacyBlockItem)this.replacementData.get(rawId);
      return mapping != null ? mapping : (MappedLegacyBlockItem)this.replacementData.get(rawId & -16);
   }

   protected void handleChunk(Chunk chunk) {
      Map<LegacyBlockItemRewriter.Pos, CompoundTag> tags = new HashMap();
      Iterator var3 = chunk.getBlockEntities().iterator();

      int block;
      MappedLegacyBlockItem settings;
      while(var3.hasNext()) {
         CompoundTag tag = (CompoundTag)var3.next();
         Tag xTag;
         Tag yTag;
         Tag zTag;
         if ((xTag = tag.get("x")) != null && (yTag = tag.get("y")) != null && (zTag = tag.get("z")) != null) {
            LegacyBlockItemRewriter.Pos pos = new LegacyBlockItemRewriter.Pos(((NumberTag)xTag).asInt() & 15, ((NumberTag)yTag).asInt(), ((NumberTag)zTag).asInt() & 15);
            tags.put(pos, tag);
            if (pos.getY() >= 0 && pos.getY() <= 255) {
               ChunkSection section = chunk.getSections()[pos.getY() >> 4];
               if (section != null) {
                  block = section.palette(PaletteType.BLOCKS).idAt(pos.getX(), pos.getY() & 15, pos.getZ());
                  settings = this.getMappedBlockItem(block);
                  if (settings != null && settings.hasBlockEntityHandler()) {
                     settings.getBlockEntityHandler().handleOrNewCompoundTag(block, tag);
                  }
               }
            }
         }
      }

      for(int i = 0; i < chunk.getSections().length; ++i) {
         ChunkSection section = chunk.getSections()[i];
         if (section != null) {
            boolean hasBlockEntityHandler = false;
            DataPalette palette = section.palette(PaletteType.BLOCKS);

            int x;
            int y;
            int z;
            for(x = 0; x < palette.size(); ++x) {
               y = palette.idByIndex(x);
               z = y >> 4;
               block = y & 15;
               Block b = this.handleBlock(z, block);
               if (b != null) {
                  palette.setIdByIndex(x, b.getId() << 4 | b.getData() & 15);
               }

               if (!hasBlockEntityHandler) {
                  MappedLegacyBlockItem settings = this.getMappedBlockItem(y);
                  if (settings != null && settings.hasBlockEntityHandler()) {
                     hasBlockEntityHandler = true;
                  }
               }
            }

            if (hasBlockEntityHandler) {
               for(x = 0; x < 16; ++x) {
                  for(y = 0; y < 16; ++y) {
                     for(z = 0; z < 16; ++z) {
                        block = palette.idAt(x, y, z);
                        settings = this.getMappedBlockItem(block);
                        if (settings != null && settings.hasBlockEntityHandler()) {
                           LegacyBlockItemRewriter.Pos pos = new LegacyBlockItemRewriter.Pos(x, y + (i << 4), z);
                           if (!tags.containsKey(pos)) {
                              CompoundTag tag = new CompoundTag();
                              tag.put("x", new IntTag(x + (chunk.getX() << 4)));
                              tag.put("y", new IntTag(y + (i << 4)));
                              tag.put("z", new IntTag(z + (chunk.getZ() << 4)));
                              settings.getBlockEntityHandler().handleOrNewCompoundTag(block, tag);
                              chunk.getBlockEntities().add(tag);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   }

   protected CompoundTag getNamedTag(String text) {
      CompoundTag tag = new CompoundTag();
      tag.put("display", new CompoundTag());
      text = "§r" + text;
      ((CompoundTag)tag.get("display")).put("Name", new StringTag(this.jsonNameFormat ? ComponentUtil.legacyToJsonString(text) : text));
      return tag;
   }

   static {
      JsonObject jsonObject = VBMappingDataLoader.loadFromDataDir("legacy-mappings.json");
      Iterator var1 = jsonObject.entrySet().iterator();

      while(var1.hasNext()) {
         Entry<String, JsonElement> entry = (Entry)var1.next();
         Int2ObjectMap<MappedLegacyBlockItem> mappings = new Int2ObjectOpenHashMap(8);
         LEGACY_MAPPINGS.put((String)entry.getKey(), mappings);
         Iterator var4 = ((JsonElement)entry.getValue()).getAsJsonObject().entrySet().iterator();

         while(var4.hasNext()) {
            Entry<String, JsonElement> dataEntry = (Entry)var4.next();
            addMapping((String)dataEntry.getKey(), ((JsonElement)dataEntry.getValue()).getAsJsonObject(), mappings);
         }
      }

   }

   private static final class Pos {
      private final int x;
      private final short y;
      private final int z;

      private Pos(int x, int y, int z) {
         this.x = x;
         this.y = (short)y;
         this.z = z;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public int getZ() {
         return this.z;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            LegacyBlockItemRewriter.Pos pos = (LegacyBlockItemRewriter.Pos)o;
            if (this.x != pos.x) {
               return false;
            } else if (this.y != pos.y) {
               return false;
            } else {
               return this.z == pos.z;
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         int result = this.x;
         result = 31 * result + this.y;
         result = 31 * result + this.z;
         return result;
      }

      public String toString() {
         return "Pos{x=" + this.x + ", y=" + this.y + ", z=" + this.z + '}';
      }

      // $FF: synthetic method
      Pos(int x0, int x1, int x2, Object x3) {
         this(x0, x1, x2);
      }
   }
}
