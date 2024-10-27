package com.viaversion.viaversion.protocols.protocol1_9to1_8;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.ClientWorld;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_8.ServerboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.metadata.MetadataRewriter1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.packets.EntityPackets;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.packets.InventoryPackets;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.packets.PlayerPackets;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.packets.SpawnPackets;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.packets.WorldPackets;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.BossBarProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.CommandBlockProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.CompressionProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.EntityIdProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MainHandProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.ClientChunks;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.CommandBlockStorage;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.InventoryTracker;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.MovementTracker;
import com.viaversion.viaversion.util.GsonUtil;

public class Protocol1_9To1_8 extends AbstractProtocol<ClientboundPackets1_8, ClientboundPackets1_9, ServerboundPackets1_8, ServerboundPackets1_9> {
   public static final ValueTransformer<String, JsonElement> FIX_JSON;
   private final MetadataRewriter1_9To1_8 metadataRewriter = new MetadataRewriter1_9To1_8(this);

   public Protocol1_9To1_8() {
      super(ClientboundPackets1_8.class, ClientboundPackets1_9.class, ServerboundPackets1_8.class, ServerboundPackets1_9.class);
   }

   public static JsonElement fixJson(String line) {
      if (line != null && !line.equalsIgnoreCase("null")) {
         if ((!line.startsWith("\"") || !line.endsWith("\"")) && (!line.startsWith("{") || !line.endsWith("}"))) {
            return constructJson(line);
         }

         if (line.startsWith("\"") && line.endsWith("\"")) {
            line = "{\"text\":" + line + "}";
         }
      } else {
         line = "{\"text\":\"\"}";
      }

      try {
         return (JsonElement)GsonUtil.getGson().fromJson(line, JsonObject.class);
      } catch (Exception var2) {
         if (Via.getConfig().isForceJsonTransform()) {
            return constructJson(line);
         } else {
            Via.getPlatform().getLogger().warning("Invalid JSON String: \"" + line + "\" Please report this issue to the ViaVersion Github: " + var2.getMessage());
            return (JsonElement)GsonUtil.getGson().fromJson("{\"text\":\"\"}", JsonObject.class);
         }
      }
   }

   private static JsonElement constructJson(String text) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("text", text);
      return jsonObject;
   }

   public static Item getHandItem(UserConnection info) {
      return ((HandItemProvider)Via.getManager().getProviders().get(HandItemProvider.class)).getHandItem(info);
   }

   public static boolean isSword(int id) {
      if (id == 267) {
         return true;
      } else if (id == 268) {
         return true;
      } else if (id == 272) {
         return true;
      } else if (id == 276) {
         return true;
      } else {
         return id == 283;
      }
   }

   protected void registerPackets() {
      this.metadataRewriter.register();
      this.registerClientbound(State.LOGIN, 0, 0, (wrapper) -> {
         if (!wrapper.isReadable(Type.COMPONENT, 0)) {
            wrapper.write(Type.COMPONENT, fixJson((String)wrapper.read(Type.STRING)));
         }
      });
      SpawnPackets.register(this);
      InventoryPackets.register(this);
      EntityPackets.register(this);
      PlayerPackets.register(this);
      WorldPackets.register(this);
   }

   public void register(ViaProviders providers) {
      providers.register(HandItemProvider.class, new HandItemProvider());
      providers.register(CommandBlockProvider.class, new CommandBlockProvider());
      providers.register(EntityIdProvider.class, new EntityIdProvider());
      providers.register(BossBarProvider.class, new BossBarProvider());
      providers.register(MainHandProvider.class, new MainHandProvider());
      providers.register(CompressionProvider.class, new CompressionProvider());
      providers.register(MovementTransmitterProvider.class, new MovementTransmitterProvider());
   }

   public void init(UserConnection userConnection) {
      userConnection.addEntityTracker(this.getClass(), new EntityTracker1_9(userConnection));
      userConnection.put(new ClientChunks());
      userConnection.put(new MovementTracker());
      userConnection.put(new InventoryTracker());
      userConnection.put(new CommandBlockStorage());
      if (!userConnection.has(ClientWorld.class)) {
         userConnection.put(new ClientWorld());
      }

   }

   public MetadataRewriter1_9To1_8 getEntityRewriter() {
      return this.metadataRewriter;
   }

   static {
      FIX_JSON = new ValueTransformer<String, JsonElement>(Type.COMPONENT) {
         public JsonElement transform(PacketWrapper wrapper, String line) {
            return Protocol1_9To1_8.fixJson(line);
         }
      };
   }
}
