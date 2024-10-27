package com.viaversion.viabackwards.protocol.protocol1_17to1_17_1;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.protocol.protocol1_16_4to1_17.storage.PlayerLastCursorItem;
import com.viaversion.viabackwards.protocol.protocol1_17to1_17_1.storage.InventoryStateIds;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_17_1to1_17.ClientboundPackets1_17_1;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ClientboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ServerboundPackets1_17;
import java.util.Iterator;

public final class Protocol1_17To1_17_1 extends BackwardsProtocol<ClientboundPackets1_17_1, ClientboundPackets1_17, ServerboundPackets1_17, ServerboundPackets1_17> {
   private static final int MAX_PAGE_LENGTH = 8192;
   private static final int MAX_TITLE_LENGTH = 128;
   private static final int MAX_PAGES = 200;

   public Protocol1_17To1_17_1() {
      super(ClientboundPackets1_17_1.class, ClientboundPackets1_17.class, ServerboundPackets1_17.class, ServerboundPackets1_17.class);
   }

   protected void registerPackets() {
      this.registerClientbound(ClientboundPackets1_17_1.REMOVE_ENTITIES, (ClientboundPacketType)null, (wrapper) -> {
         int[] entityIds = (int[])wrapper.read(Type.VAR_INT_ARRAY_PRIMITIVE);
         wrapper.cancel();
         int[] var2 = entityIds;
         int var3 = entityIds.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            int entityId = var2[var4];
            PacketWrapper newPacket = wrapper.create(ClientboundPackets1_17.REMOVE_ENTITY);
            newPacket.write(Type.VAR_INT, entityId);
            newPacket.send(Protocol1_17To1_17_1.class);
         }

      });
      this.registerClientbound(ClientboundPackets1_17_1.CLOSE_WINDOW, (wrapper) -> {
         short containerId = (Short)wrapper.passthrough(Type.UNSIGNED_BYTE);
         ((InventoryStateIds)wrapper.user().get(InventoryStateIds.class)).removeStateId(containerId);
      });
      this.registerClientbound(ClientboundPackets1_17_1.SET_SLOT, (wrapper) -> {
         short containerId = (Short)wrapper.passthrough(Type.UNSIGNED_BYTE);
         int stateId = (Integer)wrapper.read(Type.VAR_INT);
         ((InventoryStateIds)wrapper.user().get(InventoryStateIds.class)).setStateId(containerId, stateId);
      });
      this.registerClientbound(ClientboundPackets1_17_1.WINDOW_ITEMS, (wrapper) -> {
         short containerId = (Short)wrapper.passthrough(Type.UNSIGNED_BYTE);
         int stateId = (Integer)wrapper.read(Type.VAR_INT);
         ((InventoryStateIds)wrapper.user().get(InventoryStateIds.class)).setStateId(containerId, stateId);
         wrapper.write(Type.ITEM1_13_2_SHORT_ARRAY, (Item[])wrapper.read(Type.ITEM1_13_2_ARRAY));
         Item carried = (Item)wrapper.read(Type.ITEM1_13_2);
         PlayerLastCursorItem lastCursorItem = (PlayerLastCursorItem)wrapper.user().get(PlayerLastCursorItem.class);
         if (lastCursorItem != null) {
            lastCursorItem.setLastCursorItem(carried);
         }

      });
      this.registerServerbound(ServerboundPackets1_17.CLOSE_WINDOW, (wrapper) -> {
         short containerId = (Short)wrapper.passthrough(Type.UNSIGNED_BYTE);
         ((InventoryStateIds)wrapper.user().get(InventoryStateIds.class)).removeStateId(containerId);
      });
      this.registerServerbound(ServerboundPackets1_17.CLICK_WINDOW, (wrapper) -> {
         short containerId = (Short)wrapper.passthrough(Type.UNSIGNED_BYTE);
         int stateId = ((InventoryStateIds)wrapper.user().get(InventoryStateIds.class)).removeStateId(containerId);
         wrapper.write(Type.VAR_INT, stateId == Integer.MAX_VALUE ? 0 : stateId);
      });
      this.registerServerbound(ServerboundPackets1_17.EDIT_BOOK, (wrapper) -> {
         Item item = (Item)wrapper.read(Type.ITEM1_13_2);
         boolean signing = (Boolean)wrapper.read(Type.BOOLEAN);
         wrapper.passthrough(Type.VAR_INT);
         CompoundTag tag = item.tag();
         StringTag titleTag = null;
         ListTag pagesTag;
         if (tag == null || (pagesTag = (ListTag)tag.get("pages")) == null || signing && (titleTag = (StringTag)tag.get("title")) == null) {
            wrapper.write(Type.VAR_INT, 0);
            wrapper.write(Type.BOOLEAN, false);
         } else {
            if (pagesTag.size() > 200) {
               pagesTag = new ListTag(pagesTag.getValue().subList(0, 200));
            }

            wrapper.write(Type.VAR_INT, pagesTag.size());

            String page;
            for(Iterator var6 = pagesTag.iterator(); var6.hasNext(); wrapper.write(Type.STRING, page)) {
               Tag pageTag = (Tag)var6.next();
               page = ((StringTag)pageTag).getValue();
               if (page.length() > 8192) {
                  page = page.substring(0, 8192);
               }
            }

            wrapper.write(Type.BOOLEAN, signing);
            if (signing) {
               if (titleTag == null) {
                  titleTag = (StringTag)tag.get("title");
               }

               String title = titleTag.getValue();
               if (title.length() > 128) {
                  title = title.substring(0, 128);
               }

               wrapper.write(Type.STRING, title);
            }

         }
      });
   }

   public void init(UserConnection connection) {
      connection.put(new InventoryStateIds());
   }
}
