package com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.packets;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_18;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.ClientboundPackets1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.Protocol1_19_3To1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.ServerboundPackets1_19_3;
import com.viaversion.viaversion.rewriter.BlockRewriter;
import com.viaversion.viaversion.rewriter.ItemRewriter;
import com.viaversion.viaversion.rewriter.RecipeRewriter;
import com.viaversion.viaversion.util.Key;

public final class InventoryPackets extends ItemRewriter<ClientboundPackets1_19_1, ServerboundPackets1_19_3, Protocol1_19_3To1_19_1> {
   private static final int MISC_CRAFTING_BOOK_CATEGORY = 0;

   public InventoryPackets(Protocol1_19_3To1_19_1 protocol) {
      super(protocol, Type.ITEM1_13_2, Type.ITEM1_13_2_ARRAY);
   }

   public void registerPackets() {
      BlockRewriter<ClientboundPackets1_19_1> blockRewriter = BlockRewriter.for1_14(this.protocol);
      blockRewriter.registerBlockAction(ClientboundPackets1_19_1.BLOCK_ACTION);
      blockRewriter.registerBlockChange(ClientboundPackets1_19_1.BLOCK_CHANGE);
      blockRewriter.registerVarLongMultiBlockChange(ClientboundPackets1_19_1.MULTI_BLOCK_CHANGE);
      blockRewriter.registerEffect(ClientboundPackets1_19_1.EFFECT, 1010, 2001);
      blockRewriter.registerChunkData1_19(ClientboundPackets1_19_1.CHUNK_DATA, ChunkType1_18::new);
      blockRewriter.registerBlockEntityData(ClientboundPackets1_19_1.BLOCK_ENTITY_DATA);
      this.registerSetCooldown(ClientboundPackets1_19_1.COOLDOWN);
      this.registerWindowItems1_17_1(ClientboundPackets1_19_1.WINDOW_ITEMS);
      this.registerSetSlot1_17_1(ClientboundPackets1_19_1.SET_SLOT);
      this.registerAdvancements(ClientboundPackets1_19_1.ADVANCEMENTS, Type.ITEM1_13_2);
      this.registerEntityEquipmentArray(ClientboundPackets1_19_1.ENTITY_EQUIPMENT);
      this.registerClickWindow1_17_1(ServerboundPackets1_19_3.CLICK_WINDOW);
      this.registerTradeList1_19(ClientboundPackets1_19_1.TRADE_LIST);
      this.registerCreativeInvAction(ServerboundPackets1_19_3.CREATIVE_INVENTORY_ACTION, Type.ITEM1_13_2);
      this.registerWindowPropertyEnchantmentHandler(ClientboundPackets1_19_1.WINDOW_PROPERTY);
      this.registerSpawnParticle1_19(ClientboundPackets1_19_1.SPAWN_PARTICLE);
      RecipeRewriter<ClientboundPackets1_19_1> recipeRewriter = new RecipeRewriter(this.protocol);
      ((Protocol1_19_3To1_19_1)this.protocol).registerClientbound(ClientboundPackets1_19_1.DECLARE_RECIPES, (wrapper) -> {
         int size = (Integer)wrapper.passthrough(Type.VAR_INT);

         for(int i = 0; i < size; ++i) {
            String type = Key.stripMinecraftNamespace((String)wrapper.passthrough(Type.STRING));
            wrapper.passthrough(Type.STRING);
            byte var7 = -1;
            switch(type.hashCode()) {
            case -2084878740:
               if (type.equals("smoking")) {
                  var7 = 5;
               }
               break;
            case -1908982525:
               if (type.equals("crafting_special_repairitem")) {
                  var7 = 18;
               }
               break;
            case -1624693892:
               if (type.equals("crafting_special_shielddecoration")) {
                  var7 = 15;
               }
               break;
            case -1512004703:
               if (type.equals("crafting_special_shulkerboxcoloring")) {
                  var7 = 16;
               }
               break;
            case -1468375218:
               if (type.equals("crafting_special_bookcloning")) {
                  var7 = 7;
               }
               break;
            case -1277885348:
               if (type.equals("crafting_special_bannerduplicate")) {
                  var7 = 14;
               }
               break;
            case -1050336534:
               if (type.equals("blasting")) {
                  var7 = 4;
               }
               break;
            case -831477559:
               if (type.equals("crafting_special_firework_rocket")) {
                  var7 = 10;
               }
               break;
            case -571676035:
               if (type.equals("crafting_shapeless")) {
                  var7 = 0;
               }
               break;
            case -491776273:
               if (type.equals("smelting")) {
                  var7 = 2;
               }
               break;
            case -440399308:
               if (type.equals("crafting_special_firework_star_fade")) {
                  var7 = 12;
               }
               break;
            case -428268831:
               if (type.equals("crafting_special_mapcloning")) {
                  var7 = 8;
               }
               break;
            case -195357933:
               if (type.equals("crafting_special_suspiciousstew")) {
                  var7 = 17;
               }
               break;
            case -68678766:
               if (type.equals("campfire_cooking")) {
                  var7 = 3;
               }
               break;
            case 758673615:
               if (type.equals("crafting_special_mapextending")) {
                  var7 = 9;
               }
               break;
            case 1533084160:
               if (type.equals("crafting_shaped")) {
                  var7 = 1;
               }
               break;
            case 1575155058:
               if (type.equals("crafting_special_tippedarrow")) {
                  var7 = 13;
               }
               break;
            case 1782407559:
               if (type.equals("crafting_special_firework_star")) {
                  var7 = 11;
               }
               break;
            case 2027239028:
               if (type.equals("crafting_special_armordye")) {
                  var7 = 6;
               }
            }

            int var13;
            Item itemx;
            int ingredients;
            int j;
            Item[] items;
            Item[] var18;
            int var19;
            switch(var7) {
            case 0:
               wrapper.passthrough(Type.STRING);
               wrapper.write(Type.VAR_INT, 0);
               ingredients = (Integer)wrapper.passthrough(Type.VAR_INT);

               for(j = 0; j < ingredients; ++j) {
                  items = (Item[])wrapper.passthrough(Type.ITEM1_13_2_ARRAY);
                  var18 = items;
                  var19 = items.length;

                  for(var13 = 0; var13 < var19; ++var13) {
                     itemx = var18[var13];
                     this.handleItemToClient(itemx);
                  }
               }

               this.handleItemToClient((Item)wrapper.passthrough(Type.ITEM1_13_2));
               break;
            case 1:
               ingredients = (Integer)wrapper.passthrough(Type.VAR_INT) * (Integer)wrapper.passthrough(Type.VAR_INT);
               wrapper.passthrough(Type.STRING);
               wrapper.write(Type.VAR_INT, 0);

               for(j = 0; j < ingredients; ++j) {
                  items = (Item[])wrapper.passthrough(Type.ITEM1_13_2_ARRAY);
                  var18 = items;
                  var19 = items.length;

                  for(var13 = 0; var13 < var19; ++var13) {
                     itemx = var18[var13];
                     this.handleItemToClient(itemx);
                  }
               }

               this.handleItemToClient((Item)wrapper.passthrough(Type.ITEM1_13_2));
               break;
            case 2:
            case 3:
            case 4:
            case 5:
               wrapper.passthrough(Type.STRING);
               wrapper.write(Type.VAR_INT, 0);
               Item[] itemsx = (Item[])wrapper.passthrough(Type.ITEM1_13_2_ARRAY);
               Item[] var9 = itemsx;
               int var10 = itemsx.length;

               for(int var11 = 0; var11 < var10; ++var11) {
                  Item item = var9[var11];
                  this.handleItemToClient(item);
               }

               this.handleItemToClient((Item)wrapper.passthrough(Type.ITEM1_13_2));
               wrapper.passthrough(Type.FLOAT);
               wrapper.passthrough(Type.VAR_INT);
               break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
               wrapper.write(Type.VAR_INT, 0);
               break;
            default:
               recipeRewriter.handleRecipeType(wrapper, type);
            }
         }

      });
      ((Protocol1_19_3To1_19_1)this.protocol).registerClientbound(ClientboundPackets1_19_1.EXPLOSION, new PacketHandlers() {
         public void register() {
            this.map(Type.FLOAT, Type.DOUBLE);
            this.map(Type.FLOAT, Type.DOUBLE);
            this.map(Type.FLOAT, Type.DOUBLE);
         }
      });
   }
}
