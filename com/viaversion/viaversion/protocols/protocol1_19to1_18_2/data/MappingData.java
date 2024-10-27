package com.viaversion.viaversion.protocols.protocol1_19to1_18_2.data;

import com.viaversion.viaversion.api.data.MappingDataBase;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import java.util.Iterator;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class MappingData extends MappingDataBase {
   private final Int2ObjectMap<CompoundTag> defaultChatTypes = new Int2ObjectOpenHashMap();

   public MappingData() {
      super("1.18", "1.19");
   }

   protected void loadExtras(CompoundTag daata) {
      ListTag chatTypes = (ListTag)MappingDataLoader.loadNBTFromFile("chat-types-1.19.nbt").get("values");
      Iterator var3 = chatTypes.iterator();

      while(var3.hasNext()) {
         Tag chatType = (Tag)var3.next();
         CompoundTag chatTypeCompound = (CompoundTag)chatType;
         NumberTag idTag = (NumberTag)chatTypeCompound.get("id");
         this.defaultChatTypes.put(idTag.asInt(), chatTypeCompound);
      }

   }

   @Nullable
   public CompoundTag chatType(int id) {
      return (CompoundTag)this.defaultChatTypes.get(id);
   }
}
