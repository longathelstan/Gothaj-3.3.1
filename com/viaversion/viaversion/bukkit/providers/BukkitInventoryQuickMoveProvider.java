package com.viaversion.viaversion.bukkit.providers;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.bukkit.tasks.protocol1_12to1_11_1.BukkitInventoryUpdateTask;
import com.viaversion.viaversion.bukkit.util.NMSUtil;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.providers.InventoryQuickMoveProvider;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.storage.ItemTransaction;
import com.viaversion.viaversion.util.ReflectionUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class BukkitInventoryQuickMoveProvider extends InventoryQuickMoveProvider {
   private final Map<UUID, BukkitInventoryUpdateTask> updateTasks = new ConcurrentHashMap();
   private final boolean supported = this.isSupported();
   private Class<?> windowClickPacketClass;
   private Object clickTypeEnum;
   private Method nmsItemMethod;
   private Method craftPlayerHandle;
   private Field connection;
   private Method packetMethod;

   public BukkitInventoryQuickMoveProvider() {
      this.setupReflection();
   }

   public boolean registerQuickMoveAction(short windowId, short slotId, short actionId, UserConnection userConnection) {
      if (!this.supported) {
         return false;
      } else if (slotId < 0) {
         return false;
      } else {
         if (windowId == 0 && slotId >= 36 && slotId <= 45) {
            int protocolId = Via.getAPI().getServerVersion().lowestSupportedVersion();
            if (protocolId == ProtocolVersion.v1_8.getVersion()) {
               return false;
            }
         }

         ProtocolInfo info = userConnection.getProtocolInfo();
         UUID uuid = info.getUuid();
         BukkitInventoryUpdateTask updateTask = (BukkitInventoryUpdateTask)this.updateTasks.get(uuid);
         boolean registered = updateTask != null;
         if (!registered) {
            updateTask = new BukkitInventoryUpdateTask(this, uuid);
            this.updateTasks.put(uuid, updateTask);
         }

         updateTask.addItem(windowId, slotId, actionId);
         if (!registered && Via.getPlatform().isPluginEnabled()) {
            Via.getPlatform().runSync(updateTask);
         }

         return true;
      }
   }

   public Object buildWindowClickPacket(Player p, ItemTransaction storage) {
      if (!this.supported) {
         return null;
      } else {
         InventoryView inv = p.getOpenInventory();
         short slotId = storage.getSlotId();
         Inventory tinv = inv.getTopInventory();
         InventoryType tinvtype = tinv == null ? null : tinv.getType();
         if (tinvtype != null) {
            int protocolId = Via.getAPI().getServerVersion().lowestSupportedVersion();
            if (protocolId == ProtocolVersion.v1_8.getVersion() && tinvtype == InventoryType.BREWING && slotId >= 5 && slotId <= 40) {
               --slotId;
            }
         }

         ItemStack itemstack = null;
         if (slotId <= inv.countSlots()) {
            itemstack = inv.getItem(slotId);
         } else {
            String cause = "Too many inventory slots: slotId: " + slotId + " invSlotCount: " + inv.countSlots() + " invType: " + inv.getType() + " topInvType: " + tinvtype;
            Via.getPlatform().getLogger().severe("Failed to get an item to create a window click packet. Please report this issue to the ViaVersion Github: " + cause);
         }

         Object packet = null;

         try {
            packet = this.windowClickPacketClass.getDeclaredConstructor().newInstance();
            Object nmsItem = itemstack == null ? null : this.nmsItemMethod.invoke((Object)null, itemstack);
            ReflectionUtil.set(packet, "a", Integer.valueOf(storage.getWindowId()));
            ReflectionUtil.set(packet, "slot", Integer.valueOf(slotId));
            ReflectionUtil.set(packet, "button", 0);
            ReflectionUtil.set(packet, "d", storage.getActionId());
            ReflectionUtil.set(packet, "item", nmsItem);
            int protocolId = Via.getAPI().getServerVersion().lowestSupportedVersion();
            if (protocolId == ProtocolVersion.v1_8.getVersion()) {
               ReflectionUtil.set(packet, "shift", 1);
            } else if (protocolId >= ProtocolVersion.v1_9.getVersion()) {
               ReflectionUtil.set(packet, "shift", this.clickTypeEnum);
            }
         } catch (Exception var11) {
            Via.getPlatform().getLogger().log(Level.SEVERE, "Failed to create a window click packet. Please report this issue to the ViaVersion Github: " + var11.getMessage(), var11);
         }

         return packet;
      }
   }

   public boolean sendPacketToServer(Player p, Object packet) {
      if (packet == null) {
         return true;
      } else {
         try {
            Object entityPlayer = this.craftPlayerHandle.invoke(p);
            Object playerConnection = this.connection.get(entityPlayer);
            this.packetMethod.invoke(playerConnection, packet);
            return true;
         } catch (InvocationTargetException | IllegalAccessException var5) {
            var5.printStackTrace();
            return false;
         }
      }
   }

   public void onTaskExecuted(UUID uuid) {
      this.updateTasks.remove(uuid);
   }

   private void setupReflection() {
      if (this.supported) {
         try {
            this.windowClickPacketClass = NMSUtil.nms("PacketPlayInWindowClick");
            int protocolId = Via.getAPI().getServerVersion().lowestSupportedVersion();
            Class craftItemStack;
            if (protocolId >= ProtocolVersion.v1_9.getVersion()) {
               craftItemStack = NMSUtil.nms("InventoryClickType");
               Object[] constants = craftItemStack.getEnumConstants();
               this.clickTypeEnum = constants[1];
            }

            craftItemStack = NMSUtil.obc("inventory.CraftItemStack");
            this.nmsItemMethod = craftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class);
         } catch (Exception var7) {
            throw new RuntimeException("Couldn't find required inventory classes", var7);
         }

         try {
            this.craftPlayerHandle = NMSUtil.obc("entity.CraftPlayer").getDeclaredMethod("getHandle");
         } catch (ClassNotFoundException | NoSuchMethodException var6) {
            throw new RuntimeException("Couldn't find CraftPlayer", var6);
         }

         try {
            this.connection = NMSUtil.nms("EntityPlayer").getDeclaredField("playerConnection");
         } catch (ClassNotFoundException | NoSuchFieldException var5) {
            throw new RuntimeException("Couldn't find Player Connection", var5);
         }

         try {
            this.packetMethod = NMSUtil.nms("PlayerConnection").getDeclaredMethod("a", this.windowClickPacketClass);
         } catch (ClassNotFoundException | NoSuchMethodException var4) {
            throw new RuntimeException("Couldn't find CraftPlayer", var4);
         }
      }
   }

   private boolean isSupported() {
      int protocolId = Via.getAPI().getServerVersion().lowestSupportedVersion();
      return protocolId >= ProtocolVersion.v1_8.getVersion() && protocolId <= ProtocolVersion.v1_11_1.getVersion();
   }
}
