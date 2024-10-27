package com.viaversion.viabackwards.protocol.protocol1_19_4to1_20.packets;

import com.viaversion.viabackwards.api.rewriters.ItemRewriter;
import com.viaversion.viabackwards.protocol.protocol1_19_4to1_20.Protocol1_19_4To1_20;
import com.viaversion.viabackwards.protocol.protocol1_19_4to1_20.storage.BackSignEditStorage;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.blockentity.BlockEntity;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_18;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.ClientboundPackets1_19_4;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.ServerboundPackets1_19_4;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.rewriter.RecipeRewriter1_19_4;
import com.viaversion.viaversion.rewriter.BlockRewriter;
import com.viaversion.viaversion.util.Key;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class BlockItemPackets1_20 extends ItemRewriter<ClientboundPackets1_19_4, ServerboundPackets1_19_4, Protocol1_19_4To1_20> {
   private static final Set<String> NEW_TRIM_PATTERNS = new HashSet(Arrays.asList("host", "raiser", "shaper", "silence", "wayfinder"));

   public BlockItemPackets1_20(Protocol1_19_4To1_20 protocol) {
      super(protocol);
   }

   public void registerPackets() {
      final BlockRewriter<ClientboundPackets1_19_4> blockRewriter = BlockRewriter.for1_14(this.protocol);
      blockRewriter.registerBlockAction(ClientboundPackets1_19_4.BLOCK_ACTION);
      blockRewriter.registerBlockChange(ClientboundPackets1_19_4.BLOCK_CHANGE);
      blockRewriter.registerEffect(ClientboundPackets1_19_4.EFFECT, 1010, 2001);
      blockRewriter.registerBlockEntityData(ClientboundPackets1_19_4.BLOCK_ENTITY_DATA, this::handleBlockEntity);
      ((Protocol1_19_4To1_20)this.protocol).registerClientbound(ClientboundPackets1_19_4.CHUNK_DATA, new PacketHandlers() {
         protected void register() {
            this.handler(blockRewriter.chunkDataHandler1_19(ChunkType1_18::new, (x$0) -> {
               BlockItemPackets1_20.this.handleBlockEntity(x$0);
            }));
            this.create(Type.BOOLEAN, true);
         }
      });
      ((Protocol1_19_4To1_20)this.protocol).registerClientbound(ClientboundPackets1_19_4.UPDATE_LIGHT, (wrapper) -> {
         wrapper.passthrough(Type.VAR_INT);
         wrapper.passthrough(Type.VAR_INT);
         wrapper.write(Type.BOOLEAN, true);
      });
      ((Protocol1_19_4To1_20)this.protocol).registerClientbound(ClientboundPackets1_19_4.MULTI_BLOCK_CHANGE, new PacketHandlers() {
         public void register() {
            this.map(Type.LONG);
            this.create(Type.BOOLEAN, false);
            this.handler((wrapper) -> {
               BlockChangeRecord[] var2 = (BlockChangeRecord[])wrapper.passthrough(Type.VAR_LONG_BLOCK_CHANGE_RECORD_ARRAY);
               int var3 = var2.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  BlockChangeRecord record = var2[var4];
                  record.setBlockId(((Protocol1_19_4To1_20)BlockItemPackets1_20.this.protocol).getMappingData().getNewBlockStateId(record.getBlockId()));
               }

            });
         }
      });
      this.registerOpenWindow(ClientboundPackets1_19_4.OPEN_WINDOW);
      this.registerSetCooldown(ClientboundPackets1_19_4.COOLDOWN);
      this.registerWindowItems1_17_1(ClientboundPackets1_19_4.WINDOW_ITEMS);
      this.registerSetSlot1_17_1(ClientboundPackets1_19_4.SET_SLOT);
      this.registerEntityEquipmentArray(ClientboundPackets1_19_4.ENTITY_EQUIPMENT);
      this.registerClickWindow1_17_1(ServerboundPackets1_19_4.CLICK_WINDOW);
      this.registerTradeList1_19(ClientboundPackets1_19_4.TRADE_LIST);
      this.registerCreativeInvAction(ServerboundPackets1_19_4.CREATIVE_INVENTORY_ACTION, Type.ITEM1_13_2);
      this.registerWindowPropertyEnchantmentHandler(ClientboundPackets1_19_4.WINDOW_PROPERTY);
      this.registerSpawnParticle1_19(ClientboundPackets1_19_4.SPAWN_PARTICLE);
      ((Protocol1_19_4To1_20)this.protocol).registerClientbound(ClientboundPackets1_19_4.ADVANCEMENTS, (wrapper) -> {
         wrapper.passthrough(Type.BOOLEAN);
         int size = (Integer)wrapper.passthrough(Type.VAR_INT);

         for(int i = 0; i < size; ++i) {
            wrapper.passthrough(Type.STRING);
            if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
               wrapper.passthrough(Type.STRING);
            }

            int arrayLength;
            if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
               wrapper.passthrough(Type.COMPONENT);
               wrapper.passthrough(Type.COMPONENT);
               this.handleItemToClient((Item)wrapper.passthrough(Type.ITEM1_13_2));
               wrapper.passthrough(Type.VAR_INT);
               arrayLength = (Integer)wrapper.passthrough(Type.INT);
               if ((arrayLength & 1) != 0) {
                  wrapper.passthrough(Type.STRING);
               }

               wrapper.passthrough(Type.FLOAT);
               wrapper.passthrough(Type.FLOAT);
            }

            wrapper.passthrough(Type.STRING_ARRAY);
            arrayLength = (Integer)wrapper.passthrough(Type.VAR_INT);

            for(int array = 0; array < arrayLength; ++array) {
               wrapper.passthrough(Type.STRING_ARRAY);
            }

            wrapper.read(Type.BOOLEAN);
         }

      });
      ((Protocol1_19_4To1_20)this.protocol).registerClientbound(ClientboundPackets1_19_4.OPEN_SIGN_EDITOR, (wrapper) -> {
         Position position = (Position)wrapper.passthrough(Type.POSITION1_14);
         boolean frontSide = (Boolean)wrapper.read(Type.BOOLEAN);
         if (frontSide) {
            wrapper.user().remove(BackSignEditStorage.class);
         } else {
            wrapper.user().put(new BackSignEditStorage(position));
         }

      });
      ((Protocol1_19_4To1_20)this.protocol).registerServerbound(ServerboundPackets1_19_4.UPDATE_SIGN, (wrapper) -> {
         Position position = (Position)wrapper.passthrough(Type.POSITION1_14);
         BackSignEditStorage backSignEditStorage = (BackSignEditStorage)wrapper.user().remove(BackSignEditStorage.class);
         boolean frontSide = backSignEditStorage == null || !backSignEditStorage.position().equals(position);
         wrapper.write(Type.BOOLEAN, frontSide);
      });
      (new RecipeRewriter1_19_4(this.protocol)).register(ClientboundPackets1_19_4.DECLARE_RECIPES);
   }

   @Nullable
   public Item handleItemToClient(@Nullable Item item) {
      if (item == null) {
         return null;
      } else {
         super.handleItemToClient(item);
         Tag trimTag;
         if (item.tag() != null && (trimTag = item.tag().get("Trim")) instanceof CompoundTag) {
            Tag patternTag = ((CompoundTag)trimTag).get("pattern");
            if (patternTag instanceof StringTag) {
               StringTag patternStringTag = (StringTag)patternTag;
               String pattern = Key.stripMinecraftNamespace(patternStringTag.getValue());
               if (NEW_TRIM_PATTERNS.contains(pattern)) {
                  item.tag().remove("Trim");
                  item.tag().put(this.nbtTagName + "|Trim", trimTag);
               }
            }
         }

         return item;
      }
   }

   @Nullable
   public Item handleItemToServer(@Nullable Item item) {
      if (item == null) {
         return null;
      } else {
         super.handleItemToServer(item);
         Tag trimTag;
         if (item.tag() != null && (trimTag = item.tag().remove(this.nbtTagName + "|Trim")) != null) {
            item.tag().put("Trim", trimTag);
         }

         return item;
      }
   }

   private void handleBlockEntity(BlockEntity blockEntity) {
      if (blockEntity.typeId() == 7 || blockEntity.typeId() == 8) {
         CompoundTag tag = blockEntity.tag();
         CompoundTag frontText = (CompoundTag)tag.remove("front_text");
         tag.remove("back_text");
         if (frontText != null) {
            this.writeMessages(frontText, tag, false);
            this.writeMessages(frontText, tag, true);
            Tag color = frontText.remove("color");
            if (color != null) {
               tag.put("Color", color);
            }

            Tag glowing = frontText.remove("has_glowing_text");
            if (glowing != null) {
               tag.put("GlowingText", glowing);
            }
         }

      }
   }

   private void writeMessages(CompoundTag frontText, CompoundTag tag, boolean filtered) {
      ListTag messages = (ListTag)frontText.get(filtered ? "filtered_messages" : "messages");
      if (messages != null) {
         int i = 0;
         Iterator var6 = messages.iterator();

         while(var6.hasNext()) {
            Tag message = (Tag)var6.next();
            StringBuilder var10001 = (new StringBuilder()).append(filtered ? "FilteredText" : "Text");
            ++i;
            tag.put(var10001.append(i).toString(), message);
         }

      }
   }
}
