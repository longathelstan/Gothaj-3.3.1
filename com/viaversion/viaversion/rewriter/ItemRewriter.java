package com.viaversion.viaversion.rewriter;

import com.viaversion.viaversion.api.data.Mappings;
import com.viaversion.viaversion.api.data.ParticleMappings;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import com.viaversion.viaversion.api.type.Type;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ItemRewriter<C extends ClientboundPacketType, S extends ServerboundPacketType, T extends Protocol<C, ?, ?, S>> extends RewriterBase<T> implements com.viaversion.viaversion.api.rewriter.ItemRewriter<T> {
   private final Type<Item> itemType;
   private final Type<Item[]> itemArrayType;

   /** @deprecated */
   @Deprecated
   protected ItemRewriter(T protocol) {
      this(protocol, Type.ITEM1_13_2, Type.ITEM1_13_2_ARRAY);
   }

   public ItemRewriter(T protocol, Type<Item> itemType, Type<Item[]> itemArrayType) {
      super(protocol);
      this.itemType = itemType;
      this.itemArrayType = itemArrayType;
   }

   @Nullable
   public Item handleItemToClient(@Nullable Item item) {
      if (item == null) {
         return null;
      } else {
         if (this.protocol.getMappingData() != null && this.protocol.getMappingData().getItemMappings() != null) {
            item.setIdentifier(this.protocol.getMappingData().getNewItemId(item.identifier()));
         }

         return item;
      }
   }

   @Nullable
   public Item handleItemToServer(@Nullable Item item) {
      if (item == null) {
         return null;
      } else {
         if (this.protocol.getMappingData() != null && this.protocol.getMappingData().getItemMappings() != null) {
            item.setIdentifier(this.protocol.getMappingData().getOldItemId(item.identifier()));
         }

         return item;
      }
   }

   public void registerWindowItems(C packetType, final Type<Item[]> type) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(type);
            this.handler(ItemRewriter.this.itemArrayToClientHandler(type));
         }
      }));
   }

   public void registerWindowItems1_17_1(C packetType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               Item[] items = (Item[])wrapper.passthrough(ItemRewriter.this.itemArrayType);
               Item[] var3 = items;
               int var4 = items.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  Item item = var3[var5];
                  ItemRewriter.this.handleItemToClient(item);
               }

               ItemRewriter.this.handleItemToClient((Item)wrapper.passthrough(ItemRewriter.this.itemType));
            });
         }
      }));
   }

   public void registerOpenWindow(C packetType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               int windowType = (Integer)wrapper.read(Type.VAR_INT);
               int mappedId = ItemRewriter.this.protocol.getMappingData().getMenuMappings().getNewId(windowType);
               if (mappedId == -1) {
                  wrapper.cancel();
               } else {
                  wrapper.write(Type.VAR_INT, mappedId);
               }
            });
         }
      }));
   }

   public void registerSetSlot(C packetType, final Type<Item> type) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.SHORT);
            this.map(type);
            this.handler(ItemRewriter.this.itemToClientHandler(type));
         }
      }));
   }

   public void registerSetSlot1_17_1(C packetType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.VAR_INT);
            this.map(Type.SHORT);
            this.map(ItemRewriter.this.itemType);
            this.handler(ItemRewriter.this.itemToClientHandler(ItemRewriter.this.itemType));
         }
      }));
   }

   public void registerEntityEquipment(C packetType, final Type<Item> type) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.VAR_INT);
            this.map(type);
            this.handler(ItemRewriter.this.itemToClientHandler(type));
         }
      }));
   }

   public void registerEntityEquipmentArray(C packetType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               byte slot;
               do {
                  slot = (Byte)wrapper.passthrough(Type.BYTE);
                  ItemRewriter.this.handleItemToClient((Item)wrapper.passthrough(ItemRewriter.this.itemType));
               } while((slot & -128) != 0);

            });
         }
      }));
   }

   public void registerCreativeInvAction(S packetType) {
      this.registerCreativeInvAction(packetType, this.itemType);
   }

   public void registerCreativeInvAction(S packetType, final Type<Item> type) {
      this.protocol.registerServerbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.SHORT);
            this.map(type);
            this.handler(ItemRewriter.this.itemToServerHandler(type));
         }
      }));
   }

   public void registerClickWindow(S packetType, final Type<Item> type) {
      this.protocol.registerServerbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.SHORT);
            this.map(Type.BYTE);
            this.map(Type.SHORT);
            this.map(Type.VAR_INT);
            this.map(type);
            this.handler(ItemRewriter.this.itemToServerHandler(type));
         }
      }));
   }

   public void registerClickWindow1_17_1(S packetType) {
      this.protocol.registerServerbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.VAR_INT);
            this.map(Type.SHORT);
            this.map(Type.BYTE);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               int length = (Integer)wrapper.passthrough(Type.VAR_INT);

               for(int i = 0; i < length; ++i) {
                  wrapper.passthrough(Type.SHORT);
                  ItemRewriter.this.handleItemToServer((Item)wrapper.passthrough(ItemRewriter.this.itemType));
               }

               ItemRewriter.this.handleItemToServer((Item)wrapper.passthrough(ItemRewriter.this.itemType));
            });
         }
      }));
   }

   public void registerSetCooldown(C packetType) {
      this.protocol.registerClientbound(packetType, (wrapper) -> {
         int itemId = (Integer)wrapper.read(Type.VAR_INT);
         wrapper.write(Type.VAR_INT, this.protocol.getMappingData().getNewItemId(itemId));
      });
   }

   public void registerTradeList(C packetType) {
      this.protocol.registerClientbound(packetType, (wrapper) -> {
         wrapper.passthrough(Type.VAR_INT);
         int size = (Short)wrapper.passthrough(Type.UNSIGNED_BYTE);

         for(int i = 0; i < size; ++i) {
            this.handleItemToClient((Item)wrapper.passthrough(this.itemType));
            this.handleItemToClient((Item)wrapper.passthrough(this.itemType));
            if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
               this.handleItemToClient((Item)wrapper.passthrough(this.itemType));
            }

            wrapper.passthrough(Type.BOOLEAN);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.FLOAT);
            wrapper.passthrough(Type.INT);
         }

      });
   }

   public void registerTradeList1_19(C packetType) {
      this.protocol.registerClientbound(packetType, (wrapper) -> {
         wrapper.passthrough(Type.VAR_INT);
         int size = (Integer)wrapper.passthrough(Type.VAR_INT);

         for(int i = 0; i < size; ++i) {
            this.handleItemToClient((Item)wrapper.passthrough(this.itemType));
            this.handleItemToClient((Item)wrapper.passthrough(this.itemType));
            this.handleItemToClient((Item)wrapper.passthrough(this.itemType));
            wrapper.passthrough(Type.BOOLEAN);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.FLOAT);
            wrapper.passthrough(Type.INT);
         }

      });
   }

   public void registerAdvancements(C packetType, Type<Item> type) {
      this.protocol.registerClientbound(packetType, (wrapper) -> {
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
               this.handleItemToClient((Item)wrapper.passthrough(type));
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
         }

      });
   }

   public void registerAdvancements1_20_2(C packetType) {
      this.registerAdvancements1_20_2(packetType, Type.COMPONENT);
   }

   public void registerAdvancements1_20_3(C packetType) {
      this.registerAdvancements1_20_2(packetType, Type.TAG);
   }

   private void registerAdvancements1_20_2(C packetType, Type<?> componentType) {
      this.protocol.registerClientbound(packetType, (wrapper) -> {
         wrapper.passthrough(Type.BOOLEAN);
         int size = (Integer)wrapper.passthrough(Type.VAR_INT);

         for(int i = 0; i < size; ++i) {
            wrapper.passthrough(Type.STRING);
            if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
               wrapper.passthrough(Type.STRING);
            }

            int requirements;
            if ((Boolean)wrapper.passthrough(Type.BOOLEAN)) {
               wrapper.passthrough(componentType);
               wrapper.passthrough(componentType);
               this.handleItemToClient((Item)wrapper.passthrough(this.itemType));
               wrapper.passthrough(Type.VAR_INT);
               requirements = (Integer)wrapper.passthrough(Type.INT);
               if ((requirements & 1) != 0) {
                  wrapper.passthrough(Type.STRING);
               }

               wrapper.passthrough(Type.FLOAT);
               wrapper.passthrough(Type.FLOAT);
            }

            requirements = (Integer)wrapper.passthrough(Type.VAR_INT);

            for(int array = 0; array < requirements; ++array) {
               wrapper.passthrough(Type.STRING_ARRAY);
            }

            wrapper.passthrough(Type.BOOLEAN);
         }

      });
   }

   public void registerWindowPropertyEnchantmentHandler(C packetType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.handler((wrapper) -> {
               Mappings mappings = ItemRewriter.this.protocol.getMappingData().getEnchantmentMappings();
               if (mappings != null) {
                  short property = (Short)wrapper.passthrough(Type.SHORT);
                  if (property >= 4 && property <= 6) {
                     short enchantmentId = (short)mappings.getNewId((Short)wrapper.read(Type.SHORT));
                     wrapper.write(Type.SHORT, enchantmentId);
                  }

               }
            });
         }
      }));
   }

   public void registerSpawnParticle(C packetType, Type<Item> itemType, final Type<?> coordType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.BOOLEAN);
            this.map(coordType);
            this.map(coordType);
            this.map(coordType);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.INT);
            this.handler(ItemRewriter.this.getSpawnParticleHandler());
         }
      }));
   }

   public void registerSpawnParticle1_19(C packetType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.BOOLEAN);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.DOUBLE);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.FLOAT);
            this.map(Type.INT);
            this.handler(ItemRewriter.this.getSpawnParticleHandler(Type.VAR_INT));
         }
      }));
   }

   public PacketHandler getSpawnParticleHandler() {
      return this.getSpawnParticleHandler(Type.INT);
   }

   public PacketHandler getSpawnParticleHandler(Type<Integer> idType) {
      return (wrapper) -> {
         int id = (Integer)wrapper.get(idType, 0);
         if (id != -1) {
            ParticleMappings mappings = this.protocol.getMappingData().getParticleMappings();
            int mappedId;
            if (mappings.isBlockParticle(id)) {
               mappedId = (Integer)wrapper.read(Type.VAR_INT);
               wrapper.write(Type.VAR_INT, this.protocol.getMappingData().getNewBlockStateId(mappedId));
            } else if (mappings.isItemParticle(id)) {
               this.handleItemToClient((Item)wrapper.passthrough(this.itemType));
            }

            mappedId = this.protocol.getMappingData().getNewParticleId(id);
            if (mappedId != id) {
               wrapper.set(idType, 0, mappedId);
            }

         }
      };
   }

   public PacketHandler itemArrayToClientHandler(Type<Item[]> type) {
      return (wrapper) -> {
         Item[] items = (Item[])wrapper.get(type, 0);
         Item[] var4 = items;
         int var5 = items.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Item item = var4[var6];
            this.handleItemToClient(item);
         }

      };
   }

   public PacketHandler itemToClientHandler(Type<Item> type) {
      return (wrapper) -> {
         this.handleItemToClient((Item)wrapper.get(type, 0));
      };
   }

   public PacketHandler itemToServerHandler(Type<Item> type) {
      return (wrapper) -> {
         this.handleItemToServer((Item)wrapper.get(type, 0));
      };
   }
}
