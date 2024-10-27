package com.viaversion.viabackwards.api.rewriters;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.data.MappedItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import java.util.Iterator;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ItemRewriter<C extends ClientboundPacketType, S extends ServerboundPacketType, T extends BackwardsProtocol<C, ?, ?, S>> extends ItemRewriterBase<C, S, T> {
   public ItemRewriter(T protocol) {
      super(protocol, true);
   }

   public ItemRewriter(T protocol, Type<Item> itemType, Type<Item[]> itemArrayType) {
      super(protocol, itemType, itemArrayType, true);
   }

   @Nullable
   public Item handleItemToClient(@Nullable Item item) {
      if (item == null) {
         return null;
      } else {
         CompoundTag display = item.tag() != null ? (CompoundTag)item.tag().get("display") : null;
         if (((BackwardsProtocol)this.protocol).getTranslatableRewriter() != null && display != null) {
            Tag name = display.get("Name");
            if (name instanceof StringTag) {
               StringTag nameStringTag = (StringTag)name;
               String newValue = ((BackwardsProtocol)this.protocol).getTranslatableRewriter().processText(nameStringTag.getValue()).toString();
               if (!newValue.equals(name.getValue())) {
                  this.saveStringTag(display, nameStringTag, "Name");
               }

               nameStringTag.setValue(newValue);
            }

            Tag lore = display.get("Lore");
            if (lore instanceof ListTag) {
               ListTag loreListTag = (ListTag)lore;
               boolean changed = false;
               Iterator var7 = loreListTag.iterator();

               while(var7.hasNext()) {
                  Tag loreEntryTag = (Tag)var7.next();
                  if (loreEntryTag instanceof StringTag) {
                     StringTag loreEntry = (StringTag)loreEntryTag;
                     String newValue = ((BackwardsProtocol)this.protocol).getTranslatableRewriter().processText(loreEntry.getValue()).toString();
                     if (!changed && !newValue.equals(loreEntry.getValue())) {
                        changed = true;
                        this.saveListTag(display, loreListTag, "Lore");
                     }

                     loreEntry.setValue(newValue);
                  }
               }
            }
         }

         MappedItem data = ((BackwardsProtocol)this.protocol).getMappingData() != null ? ((BackwardsProtocol)this.protocol).getMappingData().getMappedItem(item.identifier()) : null;
         if (data == null) {
            return super.handleItemToClient(item);
         } else {
            if (item.tag() == null) {
               item.setTag(new CompoundTag());
            }

            item.tag().put(this.nbtTagName + "|id", new IntTag(item.identifier()));
            item.setIdentifier(data.getId());
            if (data.customModelData() != null && !item.tag().contains("CustomModelData")) {
               item.tag().put("CustomModelData", new IntTag(data.customModelData()));
            }

            if (display == null) {
               item.tag().put("display", display = new CompoundTag());
            }

            if (!display.contains("Name")) {
               display.put("Name", new StringTag(data.getJsonName()));
               display.put(this.nbtTagName + "|customName", new ByteTag());
            }

            return item;
         }
      }
   }

   @Nullable
   public Item handleItemToServer(@Nullable Item item) {
      if (item == null) {
         return null;
      } else {
         super.handleItemToServer(item);
         if (item.tag() != null) {
            IntTag originalId = (IntTag)item.tag().remove(this.nbtTagName + "|id");
            if (originalId != null) {
               item.setIdentifier(originalId.asInt());
            }
         }

         return item;
      }
   }

   public void registerAdvancements(C packetType, final Type<Item> type) {
      ((BackwardsProtocol)this.protocol).registerClientbound(packetType, new PacketHandlers() {
         public void register() {
            this.handler((wrapper) -> {
               wrapper.passthrough(Type.BOOLEAN);
               int size = (Integer)wrapper.passthrough(Type.VAR_INT);

               for(int i = 0; i < size; ++i) {
                  wrapper.passthrough(Type.STRING);
                  if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
                     wrapper.passthrough(Type.STRING);
                  }

                  if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
                     JsonElement title = (JsonElement)wrapper.passthrough(Type.COMPONENT);
                     JsonElement description = (JsonElement)wrapper.passthrough(Type.COMPONENT);
                     TranslatableRewriter<C> translatableRewriter = ((BackwardsProtocol)ItemRewriter.this.protocol).getTranslatableRewriter();
                     if (translatableRewriter != null) {
                        translatableRewriter.processText(title);
                        translatableRewriter.processText(description);
                     }

                     ItemRewriter.this.handleItemToClient((Item)wrapper.passthrough(type));
                     wrapper.passthrough(Type.VAR_INT);
                     int flags = (Integer)wrapper.passthrough(Type.INT);
                     if ((flags & 1) != 0) {
                        wrapper.passthrough(Type.STRING);
                     }

                     wrapper.passthrough(Type.FLOAT);
                     wrapper.passthrough(Type.FLOAT);
                  }

                  wrapper.passthrough(Type.STRING_ARRAY);
                  int arrayLength = (Integer)wrapper.passthrough(Type.VAR_INT);

                  for(int array = 0; array < arrayLength; ++array) {
                     wrapper.passthrough(Type.STRING_ARRAY);
                  }
               }

            });
         }
      });
   }

   public void registerAdvancements1_20_3(C packetType) {
      ((BackwardsProtocol)this.protocol).registerClientbound(packetType, (wrapper) -> {
         wrapper.passthrough(Type.BOOLEAN);
         int size = (Integer)wrapper.passthrough(Type.VAR_INT);

         for(int i = 0; i < size; ++i) {
            wrapper.passthrough(Type.STRING);
            if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
               wrapper.passthrough(Type.STRING);
            }

            if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
               Tag title = (Tag)wrapper.passthrough(Type.TAG);
               Tag description = (Tag)wrapper.passthrough(Type.TAG);
               TranslatableRewriter<C> translatableRewriter = ((BackwardsProtocol)this.protocol).getTranslatableRewriter();
               if (translatableRewriter != null) {
                  translatableRewriter.processTag(title);
                  translatableRewriter.processTag(description);
               }

               this.handleItemToClient((Item)wrapper.passthrough(Type.ITEM1_20_2));
               wrapper.passthrough(Type.VAR_INT);
               int flags = (Integer)wrapper.passthrough(Type.INT);
               if ((flags & 1) != 0) {
                  wrapper.passthrough(Type.STRING);
               }

               wrapper.passthrough(Type.FLOAT);
               wrapper.passthrough(Type.FLOAT);
            }

            int requirements = (Integer)wrapper.passthrough(Type.VAR_INT);

            for(int array = 0; array < requirements; ++array) {
               wrapper.passthrough(Type.STRING_ARRAY);
            }

            wrapper.passthrough(Type.BOOLEAN);
         }

      });
   }
}
