package com.viaversion.viabackwards.protocol.protocol1_10to1_11.packets;

import com.viaversion.viabackwards.api.data.MappedLegacyBlockItem;
import com.viaversion.viabackwards.api.rewriters.LegacyBlockItemRewriter;
import com.viaversion.viabackwards.api.rewriters.LegacyEnchantmentRewriter;
import com.viaversion.viabackwards.protocol.protocol1_10to1_11.Protocol1_10To1_11;
import com.viaversion.viabackwards.protocol.protocol1_10to1_11.storage.ChestedHorseStorage;
import com.viaversion.viabackwards.protocol.protocol1_10to1_11.storage.WindowTracker;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.data.entity.StoredEntityData;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.ClientWorld;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_11;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_9_3;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectFunction;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_11to1_10.EntityIdRewriter;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

public class BlockItemPackets1_11 extends LegacyBlockItemRewriter<ClientboundPackets1_9_3, ServerboundPackets1_9_3, Protocol1_10To1_11> {
   private LegacyEnchantmentRewriter enchantmentRewriter;

   public BlockItemPackets1_11(Protocol1_10To1_11 protocol) {
      super(protocol);
   }

   protected void registerPackets() {
      ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.SET_SLOT, new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.SHORT);
            this.map(Type.ITEM1_8);
            this.handler(BlockItemPackets1_11.this.itemToClientHandler(Type.ITEM1_8));
            this.handler(new PacketHandler() {
               public void handle(PacketWrapper wrapper) throws Exception {
                  if (BlockItemPackets1_11.this.isLlama(wrapper.user())) {
                     Optional<ChestedHorseStorage> horse = BlockItemPackets1_11.this.getChestedHorse(wrapper.user());
                     if (!horse.isPresent()) {
                        return;
                     }

                     ChestedHorseStorage storage = (ChestedHorseStorage)horse.get();
                     int currentSlot = (Short)wrapper.get(Type.SHORT, 0);
                     int currentSlotx;
                     wrapper.set(Type.SHORT, 0, Integer.valueOf(currentSlotx = BlockItemPackets1_11.this.getNewSlotId(storage, currentSlot)).shortValue());
                     wrapper.set(Type.ITEM1_8, 0, BlockItemPackets1_11.this.getNewItem(storage, currentSlotx, (Item)wrapper.get(Type.ITEM1_8, 0)));
                  }

               }
            });
         }
      });
      ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.WINDOW_ITEMS, new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.ITEM1_8_SHORT_ARRAY);
            this.handler((wrapper) -> {
               Item[] stacks = (Item[])wrapper.get(Type.ITEM1_8_SHORT_ARRAY, 0);

               for(int i = 0; i < stacks.length; ++i) {
                  stacks[i] = BlockItemPackets1_11.this.handleItemToClient(stacks[i]);
               }

               if (BlockItemPackets1_11.this.isLlama(wrapper.user())) {
                  Optional<ChestedHorseStorage> horse = BlockItemPackets1_11.this.getChestedHorse(wrapper.user());
                  if (!horse.isPresent()) {
                     return;
                  }

                  ChestedHorseStorage storage = (ChestedHorseStorage)horse.get();
                  stacks = (Item[])Arrays.copyOf(stacks, !storage.isChested() ? 38 : 53);

                  for(int ix = stacks.length - 1; ix >= 0; --ix) {
                     stacks[BlockItemPackets1_11.this.getNewSlotId(storage, ix)] = stacks[ix];
                     stacks[ix] = BlockItemPackets1_11.this.getNewItem(storage, ix, stacks[ix]);
                  }

                  wrapper.set(Type.ITEM1_8_SHORT_ARRAY, 0, stacks);
               }

            });
         }
      });
      this.registerEntityEquipment(ClientboundPackets1_9_3.ENTITY_EQUIPMENT, Type.ITEM1_8);
      ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.PLUGIN_MESSAGE, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.handler((wrapper) -> {
               if (((String)wrapper.get(Type.STRING, 0)).equalsIgnoreCase("MC|TrList")) {
                  wrapper.passthrough(Type.INT);
                  int size = (Short)wrapper.passthrough(Type.UNSIGNED_BYTE);

                  for(int i = 0; i < size; ++i) {
                     wrapper.write(Type.ITEM1_8, BlockItemPackets1_11.this.handleItemToClient((Item)wrapper.read(Type.ITEM1_8)));
                     wrapper.write(Type.ITEM1_8, BlockItemPackets1_11.this.handleItemToClient((Item)wrapper.read(Type.ITEM1_8)));
                     boolean secondItem = (Boolean)wrapper.passthrough(Type.BOOLEAN);
                     if (secondItem) {
                        wrapper.write(Type.ITEM1_8, BlockItemPackets1_11.this.handleItemToClient((Item)wrapper.read(Type.ITEM1_8)));
                     }

                     wrapper.passthrough(Type.BOOLEAN);
                     wrapper.passthrough(Type.INT);
                     wrapper.passthrough(Type.INT);
                  }
               }

            });
         }
      });
      ((Protocol1_10To1_11)this.protocol).registerServerbound(ServerboundPackets1_9_3.CLICK_WINDOW, new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.SHORT);
            this.map(Type.BYTE);
            this.map(Type.SHORT);
            this.map(Type.VAR_INT);
            this.map(Type.ITEM1_8);
            this.handler(BlockItemPackets1_11.this.itemToServerHandler(Type.ITEM1_8));
            this.handler((wrapper) -> {
               if (BlockItemPackets1_11.this.isLlama(wrapper.user())) {
                  Optional<ChestedHorseStorage> horse = BlockItemPackets1_11.this.getChestedHorse(wrapper.user());
                  if (!horse.isPresent()) {
                     return;
                  }

                  ChestedHorseStorage storage = (ChestedHorseStorage)horse.get();
                  int clickSlot = (Short)wrapper.get(Type.SHORT, 0);
                  int correctSlot = BlockItemPackets1_11.this.getOldSlotId(storage, clickSlot);
                  wrapper.set(Type.SHORT, 0, Integer.valueOf(correctSlot).shortValue());
               }

            });
         }
      });
      this.registerCreativeInvAction(ServerboundPackets1_9_3.CREATIVE_INVENTORY_ACTION, Type.ITEM1_8);
      ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.CHUNK_DATA, (wrapper) -> {
         ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
         ChunkType1_9_3 type = ChunkType1_9_3.forEnvironment(clientWorld.getEnvironment());
         Chunk chunk = (Chunk)wrapper.passthrough(type);
         this.handleChunk(chunk);
         Iterator var5 = chunk.getBlockEntities().iterator();

         while(var5.hasNext()) {
            CompoundTag tag = (CompoundTag)var5.next();
            Tag idTag = tag.get("id");
            if (idTag instanceof StringTag) {
               String id = (String)idTag.getValue();
               if (id.equals("minecraft:sign")) {
                  ((StringTag)idTag).setValue("Sign");
               }
            }
         }

      });
      ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.BLOCK_CHANGE, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_8);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               int idx = (Integer)wrapper.get(Type.VAR_INT, 0);
               wrapper.set(Type.VAR_INT, 0, BlockItemPackets1_11.this.handleBlockID(idx));
            });
         }
      });
      ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.MULTI_BLOCK_CHANGE, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.INT);
            this.map(Type.BLOCK_CHANGE_RECORD_ARRAY);
            this.handler((wrapper) -> {
               BlockChangeRecord[] var2 = (BlockChangeRecord[])wrapper.get(Type.BLOCK_CHANGE_RECORD_ARRAY, 0);
               int var3 = var2.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  BlockChangeRecord record = var2[var4];
                  record.setBlockId(BlockItemPackets1_11.this.handleBlockID(record.getBlockId()));
               }

            });
         }
      });
      ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.BLOCK_ENTITY_DATA, new PacketHandlers() {
         public void register() {
            this.map(Type.POSITION1_8);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.NAMED_COMPOUND_TAG);
            this.handler((wrapper) -> {
               if ((Short)wrapper.get(Type.UNSIGNED_BYTE, 0) == 10) {
                  wrapper.cancel();
               }

               if ((Short)wrapper.get(Type.UNSIGNED_BYTE, 0) == 1) {
                  CompoundTag tag = (CompoundTag)wrapper.get(Type.NAMED_COMPOUND_TAG, 0);
                  EntityIdRewriter.toClientSpawner(tag, true);
               }

            });
         }
      });
      ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.OPEN_WINDOW, new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.STRING);
            this.map(Type.COMPONENT);
            this.map(Type.UNSIGNED_BYTE);
            this.handler((wrapper) -> {
               int entityId = -1;
               if (((String)wrapper.get(Type.STRING, 0)).equals("EntityHorse")) {
                  entityId = (Integer)wrapper.passthrough(Type.INT);
               }

               String inventory = (String)wrapper.get(Type.STRING, 0);
               WindowTracker windowTracker = (WindowTracker)wrapper.user().get(WindowTracker.class);
               windowTracker.setInventory(inventory);
               windowTracker.setEntityId(entityId);
               if (BlockItemPackets1_11.this.isLlama(wrapper.user())) {
                  wrapper.set(Type.UNSIGNED_BYTE, 1, Short.valueOf((short)17));
               }

            });
         }
      });
      ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.CLOSE_WINDOW, new PacketHandlers() {
         public void register() {
            this.handler((wrapper) -> {
               WindowTracker windowTracker = (WindowTracker)wrapper.user().get(WindowTracker.class);
               windowTracker.setInventory((String)null);
               windowTracker.setEntityId(-1);
            });
         }
      });
      ((Protocol1_10To1_11)this.protocol).registerServerbound(ServerboundPackets1_9_3.CLOSE_WINDOW, new PacketHandlers() {
         public void register() {
            this.handler((wrapper) -> {
               WindowTracker windowTracker = (WindowTracker)wrapper.user().get(WindowTracker.class);
               windowTracker.setInventory((String)null);
               windowTracker.setEntityId(-1);
            });
         }
      });
      ((Protocol1_10To1_11)this.protocol).getEntityRewriter().filter().handler((event, meta) -> {
         if (meta.metaType().type().equals(Type.ITEM1_8)) {
            meta.setValue(this.handleItemToClient((Item)meta.getValue()));
         }

      });
   }

   protected void registerRewrites() {
      MappedLegacyBlockItem data = (MappedLegacyBlockItem)this.replacementData.computeIfAbsent(832, (Int2ObjectFunction)((s) -> {
         return new MappedLegacyBlockItem(52, (short)-1, (String)null, false);
      }));
      data.setBlockEntityHandler((b, tag) -> {
         EntityIdRewriter.toClientSpawner(tag, true);
         return tag;
      });
      this.enchantmentRewriter = new LegacyEnchantmentRewriter(this.nbtTagName);
      this.enchantmentRewriter.registerEnchantment(71, "§cCurse of Vanishing");
      this.enchantmentRewriter.registerEnchantment(10, "§cCurse of Binding");
      this.enchantmentRewriter.setHideLevelForEnchants(71, 10);
   }

   public Item handleItemToClient(Item item) {
      if (item == null) {
         return null;
      } else {
         super.handleItemToClient(item);
         CompoundTag tag = item.tag();
         if (tag == null) {
            return item;
         } else {
            EntityIdRewriter.toClientItem(item, true);
            if (tag.get("ench") instanceof ListTag) {
               this.enchantmentRewriter.rewriteEnchantmentsToClient(tag, false);
            }

            if (tag.get("StoredEnchantments") instanceof ListTag) {
               this.enchantmentRewriter.rewriteEnchantmentsToClient(tag, true);
            }

            return item;
         }
      }
   }

   public Item handleItemToServer(Item item) {
      if (item == null) {
         return null;
      } else {
         super.handleItemToServer(item);
         CompoundTag tag = item.tag();
         if (tag == null) {
            return item;
         } else {
            EntityIdRewriter.toServerItem(item, true);
            if (tag.contains(this.nbtTagName + "|ench")) {
               this.enchantmentRewriter.rewriteEnchantmentsToServer(tag, false);
            }

            if (tag.contains(this.nbtTagName + "|StoredEnchantments")) {
               this.enchantmentRewriter.rewriteEnchantmentsToServer(tag, true);
            }

            return item;
         }
      }
   }

   private boolean isLlama(UserConnection user) {
      WindowTracker tracker = (WindowTracker)user.get(WindowTracker.class);
      if (tracker.getInventory() != null && tracker.getInventory().equals("EntityHorse")) {
         EntityTracker entTracker = user.getEntityTracker(Protocol1_10To1_11.class);
         StoredEntityData entityData = entTracker.entityData(tracker.getEntityId());
         return entityData != null && entityData.type().is((EntityType)EntityTypes1_11.EntityType.LIAMA);
      } else {
         return false;
      }
   }

   private Optional<ChestedHorseStorage> getChestedHorse(UserConnection user) {
      WindowTracker tracker = (WindowTracker)user.get(WindowTracker.class);
      if (tracker.getInventory() != null && tracker.getInventory().equals("EntityHorse")) {
         EntityTracker entTracker = user.getEntityTracker(Protocol1_10To1_11.class);
         StoredEntityData entityData = entTracker.entityData(tracker.getEntityId());
         if (entityData != null) {
            return Optional.of((ChestedHorseStorage)entityData.get(ChestedHorseStorage.class));
         }
      }

      return Optional.empty();
   }

   private int getNewSlotId(ChestedHorseStorage storage, int slotId) {
      int totalSlots = !storage.isChested() ? 38 : 53;
      int strength = storage.isChested() ? storage.getLiamaStrength() : 0;
      int startNonExistingFormula = 2 + 3 * strength;
      int offsetForm = 15 - 3 * strength;
      if (slotId >= startNonExistingFormula && totalSlots > slotId + offsetForm) {
         return offsetForm + slotId;
      } else {
         return slotId == 1 ? 0 : slotId;
      }
   }

   private int getOldSlotId(ChestedHorseStorage storage, int slotId) {
      int strength = storage.isChested() ? storage.getLiamaStrength() : 0;
      int startNonExistingFormula = 2 + 3 * strength;
      int endNonExistingFormula = 2 + 3 * (storage.isChested() ? 5 : 0);
      int offsetForm = endNonExistingFormula - startNonExistingFormula;
      if (slotId == 1 || slotId >= startNonExistingFormula && slotId < endNonExistingFormula) {
         return 0;
      } else if (slotId >= endNonExistingFormula) {
         return slotId - offsetForm;
      } else {
         return slotId == 0 ? 1 : slotId;
      }
   }

   private Item getNewItem(ChestedHorseStorage storage, int slotId, Item current) {
      int strength = storage.isChested() ? storage.getLiamaStrength() : 0;
      int startNonExistingFormula = 2 + 3 * strength;
      int endNonExistingFormula = 2 + 3 * (storage.isChested() ? 5 : 0);
      if (slotId >= startNonExistingFormula && slotId < endNonExistingFormula) {
         return new DataItem(166, (byte)1, (short)0, this.getNamedTag("§4SLOT DISABLED"));
      } else {
         return slotId == 1 ? null : current;
      }
   }
}
