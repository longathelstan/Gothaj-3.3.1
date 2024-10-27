package com.viaversion.viabackwards.protocol.protocol1_11_1to1_12.packets;

import com.viaversion.viabackwards.api.rewriters.LegacyBlockItemRewriter;
import com.viaversion.viabackwards.protocol.protocol1_11_1to1_12.Protocol1_11_1To1_12;
import com.viaversion.viabackwards.protocol.protocol1_11_1to1_12.data.MapColorMapping;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.ClientWorld;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_9_3;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.ClientboundPackets1_12;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.ServerboundPackets1_12;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import java.util.Iterator;
import java.util.Map.Entry;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BlockItemPackets1_12 extends LegacyBlockItemRewriter<ClientboundPackets1_12, ServerboundPackets1_9_3, Protocol1_11_1To1_12> {
   public BlockItemPackets1_12(Protocol1_11_1To1_12 protocol) {
      super(protocol);
   }

   protected void registerPackets() {
      ((Protocol1_11_1To1_12)this.protocol).registerClientbound(ClientboundPackets1_12.MAP_DATA, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.BYTE);
            this.map(Type.BOOLEAN);
            this.handler((wrapper) -> {
               int count = (Integer)wrapper.passthrough(Type.VAR_INT);

               for(int i = 0; i < count * 3; ++i) {
                  wrapper.passthrough(Type.BYTE);
               }

            });
            this.handler((wrapper) -> {
               short columns = (Short)wrapper.passthrough(Type.UNSIGNED_BYTE);
               if (columns > 0) {
                  wrapper.passthrough(Type.UNSIGNED_BYTE);
                  wrapper.passthrough(Type.UNSIGNED_BYTE);
                  wrapper.passthrough(Type.UNSIGNED_BYTE);
                  byte[] data = (byte[])wrapper.read(Type.BYTE_ARRAY_PRIMITIVE);

                  for(int i = 0; i < data.length; ++i) {
                     short color = (short)(data[i] & 255);
                     if (color > 143) {
                        color = (short)MapColorMapping.getNearestOldColor(color);
                        data[i] = (byte)color;
                     }
                  }

                  wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, data);
               }
            });
         }
      });
      this.registerSetSlot(ClientboundPackets1_12.SET_SLOT, Type.ITEM1_8);
      this.registerWindowItems(ClientboundPackets1_12.WINDOW_ITEMS, Type.ITEM1_8_SHORT_ARRAY);
      this.registerEntityEquipment(ClientboundPackets1_12.ENTITY_EQUIPMENT, Type.ITEM1_8);
      ((Protocol1_11_1To1_12)this.protocol).registerClientbound(ClientboundPackets1_12.PLUGIN_MESSAGE, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.handler((wrapper) -> {
               if (((String)wrapper.get(Type.STRING, 0)).equalsIgnoreCase("MC|TrList")) {
                  wrapper.passthrough(Type.INT);
                  int size = (Short)wrapper.passthrough(Type.UNSIGNED_BYTE);

                  for(int i = 0; i < size; ++i) {
                     wrapper.write(Type.ITEM1_8, BlockItemPackets1_12.this.handleItemToClient((Item)wrapper.read(Type.ITEM1_8)));
                     wrapper.write(Type.ITEM1_8, BlockItemPackets1_12.this.handleItemToClient((Item)wrapper.read(Type.ITEM1_8)));
                     boolean secondItem = (Boolean)wrapper.passthrough(Type.BOOLEAN);
                     if (secondItem) {
                        wrapper.write(Type.ITEM1_8, BlockItemPackets1_12.this.handleItemToClient((Item)wrapper.read(Type.ITEM1_8)));
                     }

                     wrapper.passthrough(Type.BOOLEAN);
                     wrapper.passthrough(Type.INT);
                     wrapper.passthrough(Type.INT);
                  }
               }

            });
         }
      });
      ((Protocol1_11_1To1_12)this.protocol).registerServerbound(ServerboundPackets1_9_3.CLICK_WINDOW, new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.SHORT);
            this.map(Type.BYTE);
            this.map(Type.SHORT);
            this.map(Type.VAR_INT);
            this.map(Type.ITEM1_8);
            this.handler((wrapper) -> {
               if ((Integer)wrapper.get(Type.VAR_INT, 0) == 1) {
                  wrapper.set(Type.ITEM1_8, 0, (Object)null);
                  PacketWrapper confirm = wrapper.create(ServerboundPackets1_12.WINDOW_CONFIRMATION);
                  confirm.write(Type.UNSIGNED_BYTE, (Short)wrapper.get(Type.UNSIGNED_BYTE, 0));
                  confirm.write(Type.SHORT, (Short)wrapper.get(Type.SHORT, 1));
                  confirm.write(Type.BOOLEAN, false);
                  wrapper.sendToServer(Protocol1_11_1To1_12.class);
                  wrapper.cancel();
                  confirm.sendToServer(Protocol1_11_1To1_12.class);
               } else {
                  Item item = (Item)wrapper.get(Type.ITEM1_8, 0);
                  BlockItemPackets1_12.this.handleItemToServer(item);
               }
            });
         }
      });
      this.registerCreativeInvAction(ServerboundPackets1_9_3.CREATIVE_INVENTORY_ACTION, Type.ITEM1_8);
      ((Protocol1_11_1To1_12)this.protocol).registerClientbound(ClientboundPackets1_12.CHUNK_DATA, (wrapper) -> {
         ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
         ChunkType1_9_3 type = ChunkType1_9_3.forEnvironment(clientWorld.getEnvironment());
         Chunk chunk = (Chunk)wrapper.passthrough(type);
         this.handleChunk(chunk);
      });
      ((Protocol1_11_1To1_12)this.protocol).registerClientbound(ClientboundPackets1_12.BLOCK_CHANGE, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_8);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               int idx = (Integer)wrapper.get(Type.VAR_INT, 0);
               wrapper.set(Type.VAR_INT, 0, BlockItemPackets1_12.this.handleBlockID(idx));
            });
         }
      });
      ((Protocol1_11_1To1_12)this.protocol).registerClientbound(ClientboundPackets1_12.MULTI_BLOCK_CHANGE, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.INT);
            this.map(Type.BLOCK_CHANGE_RECORD_ARRAY);
            this.handler((wrapper) -> {
               BlockChangeRecord[] var2 = (BlockChangeRecord[])wrapper.get(Type.BLOCK_CHANGE_RECORD_ARRAY, 0);
               int var3 = var2.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  BlockChangeRecord record = var2[var4];
                  record.setBlockId(BlockItemPackets1_12.this.handleBlockID(record.getBlockId()));
               }

            });
         }
      });
      ((Protocol1_11_1To1_12)this.protocol).registerClientbound(ClientboundPackets1_12.BLOCK_ENTITY_DATA, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_8);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.NAMED_COMPOUND_TAG);
            this.handler((wrapper) -> {
               if ((Short)wrapper.get(Type.UNSIGNED_BYTE, 0) == 11) {
                  wrapper.cancel();
               }

            });
         }
      });
      ((Protocol1_11_1To1_12)this.protocol).getEntityRewriter().filter().handler((event, meta) -> {
         if (meta.metaType().type().equals(Type.ITEM1_8)) {
            meta.setValue(this.handleItemToClient((Item)meta.getValue()));
         }

      });
      ((Protocol1_11_1To1_12)this.protocol).registerServerbound(ServerboundPackets1_9_3.CLIENT_STATUS, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               if ((Integer)wrapper.get(Type.VAR_INT, 0) == 2) {
                  wrapper.cancel();
               }

            });
         }
      });
   }

   @Nullable
   public Item handleItemToClient(Item item) {
      if (item == null) {
         return null;
      } else {
         super.handleItemToClient(item);
         if (item.tag() != null) {
            CompoundTag backupTag = new CompoundTag();
            if (this.handleNbtToClient(item.tag(), backupTag)) {
               item.tag().put("Via|LongArrayTags", backupTag);
            }
         }

         return item;
      }
   }

   private boolean handleNbtToClient(CompoundTag compoundTag, CompoundTag backupTag) {
      Iterator<Entry<String, Tag>> iterator = compoundTag.iterator();
      boolean hasLongArrayTag = false;

      while(iterator.hasNext()) {
         Entry<String, Tag> entry = (Entry)iterator.next();
         if (entry.getValue() instanceof CompoundTag) {
            CompoundTag nestedBackupTag = new CompoundTag();
            backupTag.put((String)entry.getKey(), nestedBackupTag);
            hasLongArrayTag |= this.handleNbtToClient((CompoundTag)entry.getValue(), nestedBackupTag);
         } else if (entry.getValue() instanceof LongArrayTag) {
            backupTag.put((String)entry.getKey(), this.fromLongArrayTag((LongArrayTag)entry.getValue()));
            iterator.remove();
            hasLongArrayTag = true;
         }
      }

      return hasLongArrayTag;
   }

   @Nullable
   public Item handleItemToServer(Item item) {
      if (item == null) {
         return null;
      } else {
         super.handleItemToServer(item);
         if (item.tag() != null) {
            Tag tag = item.tag().remove("Via|LongArrayTags");
            if (tag instanceof CompoundTag) {
               this.handleNbtToServer(item.tag(), (CompoundTag)tag);
            }
         }

         return item;
      }
   }

   private void handleNbtToServer(CompoundTag compoundTag, CompoundTag backupTag) {
      Iterator var3 = backupTag.iterator();

      while(var3.hasNext()) {
         Entry<String, Tag> entry = (Entry)var3.next();
         if (entry.getValue() instanceof CompoundTag) {
            CompoundTag nestedTag = (CompoundTag)compoundTag.get((String)entry.getKey());
            this.handleNbtToServer(nestedTag, (CompoundTag)entry.getValue());
         } else {
            compoundTag.put((String)entry.getKey(), this.fromIntArrayTag((IntArrayTag)entry.getValue()));
         }
      }

   }

   private IntArrayTag fromLongArrayTag(LongArrayTag tag) {
      int[] intArray = new int[tag.length() * 2];
      long[] longArray = tag.getValue();
      int i = 0;
      long[] var5 = longArray;
      int var6 = longArray.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         long l = var5[var7];
         intArray[i++] = (int)(l >> 32);
         intArray[i++] = (int)l;
      }

      return new IntArrayTag(intArray);
   }

   private LongArrayTag fromIntArrayTag(IntArrayTag tag) {
      long[] longArray = new long[tag.length() / 2];
      int[] intArray = tag.getValue();
      int i = 0;

      for(int j = 0; i < intArray.length; ++j) {
         longArray[j] = (long)intArray[i] << 32 | (long)intArray[i + 1] & 4294967295L;
         i += 2;
      }

      return new LongArrayTag(longArray);
   }
}
