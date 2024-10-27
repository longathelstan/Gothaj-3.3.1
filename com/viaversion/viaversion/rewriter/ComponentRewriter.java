package com.viaversion.viaversion.rewriter;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonParser;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import com.viaversion.viaversion.libs.gson.JsonSyntaxException;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import java.util.Iterator;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ComponentRewriter<C extends ClientboundPacketType> {
   protected final Protocol<C, ?, ?, ?> protocol;
   protected final ComponentRewriter.ReadType type;

   /** @deprecated */
   @Deprecated
   public ComponentRewriter(Protocol<C, ?, ?, ?> protocol) {
      this(protocol, ComponentRewriter.ReadType.JSON);
   }

   public ComponentRewriter(Protocol<C, ?, ?, ?> protocol, ComponentRewriter.ReadType type) {
      this.protocol = protocol;
      this.type = type;
   }

   public void registerComponentPacket(C packetType) {
      this.protocol.registerClientbound(packetType, this::passthroughAndProcess);
   }

   public void registerBossBar(C packetType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.UUID);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               int action = (Integer)wrapper.get(Type.VAR_INT, 0);
               if (action == 0 || action == 3) {
                  ComponentRewriter.this.passthroughAndProcess(wrapper);
               }

            });
         }
      }));
   }

   public void registerCombatEvent(C packetType) {
      this.protocol.registerClientbound(packetType, (wrapper) -> {
         if ((Integer)wrapper.passthrough(Type.VAR_INT) == 2) {
            wrapper.passthrough(Type.VAR_INT);
            wrapper.passthrough(Type.INT);
            this.processText((JsonElement)wrapper.passthrough(Type.COMPONENT));
         }

      });
   }

   public void registerTitle(C packetType) {
      this.protocol.registerClientbound(packetType, (wrapper) -> {
         int action = (Integer)wrapper.passthrough(Type.VAR_INT);
         if (action >= 0 && action <= 2) {
            this.processText((JsonElement)wrapper.passthrough(Type.COMPONENT));
         }

      });
   }

   public void registerPing() {
      this.protocol.registerClientbound((State)State.LOGIN, ClientboundLoginPackets.LOGIN_DISCONNECT, (PacketHandler)((wrapper) -> {
         this.processText((JsonElement)wrapper.passthrough(Type.COMPONENT));
      }));
   }

   public void registerLegacyOpenWindow(C packetType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.STRING);
            this.handler((wrapper) -> {
               ComponentRewriter.this.processText((JsonElement)wrapper.passthrough(Type.COMPONENT));
            });
         }
      }));
   }

   public void registerOpenWindow(C packetType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               ComponentRewriter.this.passthroughAndProcess(wrapper);
            });
         }
      }));
   }

   public void registerTabList(C packetType) {
      this.protocol.registerClientbound(packetType, (wrapper) -> {
         this.passthroughAndProcess(wrapper);
         this.passthroughAndProcess(wrapper);
      });
   }

   public void registerCombatKill(C packetType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.INT);
            this.handler((wrapper) -> {
               ComponentRewriter.this.processText((JsonElement)wrapper.passthrough(Type.COMPONENT));
            });
         }
      }));
   }

   public void registerCombatKill1_20(C packetType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               ComponentRewriter.this.passthroughAndProcess(wrapper);
            });
         }
      }));
   }

   public void passthroughAndProcess(PacketWrapper wrapper) throws Exception {
      switch(this.type) {
      case JSON:
         this.processText((JsonElement)wrapper.passthrough(Type.COMPONENT));
         break;
      case NBT:
         this.processTag((Tag)wrapper.passthrough(Type.TAG));
      }

   }

   public JsonElement processText(String value) {
      try {
         JsonElement root = JsonParser.parseString(value);
         this.processText(root);
         return root;
      } catch (JsonSyntaxException var3) {
         if (Via.getManager().isDebug()) {
            Via.getPlatform().getLogger().severe("Error when trying to parse json: " + value);
            throw var3;
         } else {
            return new JsonPrimitive(value);
         }
      }
   }

   public void processText(JsonElement element) {
      if (element != null && !element.isJsonNull()) {
         if (element.isJsonArray()) {
            this.processJsonArray(element.getAsJsonArray());
         } else if (element.isJsonObject()) {
            this.processJsonObject(element.getAsJsonObject());
         }

      }
   }

   protected void processJsonArray(JsonArray array) {
      Iterator var2 = array.iterator();

      while(var2.hasNext()) {
         JsonElement jsonElement = (JsonElement)var2.next();
         this.processText(jsonElement);
      }

   }

   protected void processJsonObject(JsonObject object) {
      JsonElement translate = object.get("translate");
      JsonElement extra;
      if (translate != null && translate.isJsonPrimitive()) {
         this.handleTranslate(object, translate.getAsString());
         extra = object.get("with");
         if (extra != null && extra.isJsonArray()) {
            this.processJsonArray(extra.getAsJsonArray());
         }
      }

      extra = object.get("extra");
      if (extra != null && extra.isJsonArray()) {
         this.processJsonArray(extra.getAsJsonArray());
      }

      JsonElement hoverEvent = object.get("hoverEvent");
      if (hoverEvent != null && hoverEvent.isJsonObject()) {
         this.handleHoverEvent(hoverEvent.getAsJsonObject());
      }

   }

   protected void handleTranslate(JsonObject object, String translate) {
   }

   protected void handleHoverEvent(JsonObject hoverEvent) {
      JsonPrimitive actionElement = hoverEvent.getAsJsonPrimitive("action");
      if (actionElement.isString()) {
         String action = actionElement.getAsString();
         JsonElement contents;
         if (action.equals("show_text")) {
            contents = hoverEvent.get("value");
            this.processText(contents != null ? contents : hoverEvent.get("contents"));
         } else if (action.equals("show_entity")) {
            contents = hoverEvent.get("contents");
            if (contents != null && contents.isJsonObject()) {
               this.processText(contents.getAsJsonObject().get("name"));
            }
         }

      }
   }

   public void processTag(@Nullable Tag tag) {
      if (tag != null) {
         if (tag instanceof ListTag) {
            this.processListTag((ListTag)tag);
         } else if (tag instanceof CompoundTag) {
            this.processCompoundTag((CompoundTag)tag);
         }

      }
   }

   private void processListTag(ListTag tag) {
      Iterator var2 = tag.iterator();

      while(var2.hasNext()) {
         Tag entry = (Tag)var2.next();
         this.processTag(entry);
      }

   }

   protected void processCompoundTag(CompoundTag tag) {
      Tag translate = tag.get("translate");
      Tag extra;
      if (translate instanceof StringTag) {
         this.handleTranslate(tag, (StringTag)translate);
         extra = tag.get("with");
         if (extra instanceof ListTag) {
            this.processListTag((ListTag)extra);
         }
      }

      extra = tag.get("extra");
      if (extra instanceof ListTag) {
         this.processListTag((ListTag)extra);
      }

      Tag hoverEvent = tag.get("hoverEvent");
      if (hoverEvent instanceof CompoundTag) {
         this.handleHoverEvent((CompoundTag)hoverEvent);
      }

   }

   protected void handleTranslate(CompoundTag parentTag, StringTag translateTag) {
   }

   protected void handleHoverEvent(CompoundTag hoverEventTag) {
      Tag actionTag = hoverEventTag.get("action");
      if (actionTag instanceof StringTag) {
         String action = ((StringTag)actionTag).getValue();
         Tag contents;
         if (action.equals("show_text")) {
            contents = hoverEventTag.get("value");
            this.processTag(contents != null ? contents : hoverEventTag.get("contents"));
         } else if (action.equals("show_entity")) {
            contents = hoverEventTag.get("contents");
            if (contents instanceof CompoundTag) {
               this.processTag(((CompoundTag)contents).get("name"));
            }
         }

      }
   }

   public static enum ReadType {
      JSON,
      NBT;
   }
}
