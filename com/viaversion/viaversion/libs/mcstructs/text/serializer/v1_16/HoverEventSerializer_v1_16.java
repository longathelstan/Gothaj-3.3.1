package com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_16;

import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonSerializationContext;
import com.viaversion.viaversion.libs.gson.JsonSerializer;
import com.viaversion.viaversion.libs.mcstructs.snbt.SNbtSerializer;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.AHoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.impl.EntityHoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.impl.ItemHoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.impl.TextHoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.TextComponentSerializer;
import java.lang.reflect.Type;

public class HoverEventSerializer_v1_16 implements JsonSerializer<AHoverEvent> {
   private final TextComponentSerializer textComponentSerializer;
   private final SNbtSerializer<?> sNbtSerializer;

   public HoverEventSerializer_v1_16(TextComponentSerializer textComponentSerializer, SNbtSerializer<?> sNbtSerializer) {
      this.textComponentSerializer = textComponentSerializer;
      this.sNbtSerializer = sNbtSerializer;
   }

   public JsonElement serialize(AHoverEvent src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject serializedHoverEvent = new JsonObject();
      serializedHoverEvent.addProperty("action", src.getAction().getName());
      if (src instanceof TextHoverEvent) {
         TextHoverEvent textHoverEvent = (TextHoverEvent)src;
         serializedHoverEvent.add("contents", this.textComponentSerializer.serializeJson(textHoverEvent.getText()));
      } else {
         JsonObject serializedItem;
         if (src instanceof ItemHoverEvent) {
            ItemHoverEvent itemHoverEvent = (ItemHoverEvent)src;
            serializedItem = new JsonObject();
            serializedItem.addProperty("id", itemHoverEvent.getItem().get());
            if (itemHoverEvent.getCount() != 1) {
               serializedItem.addProperty("count", (Number)itemHoverEvent.getCount());
            }

            if (itemHoverEvent.getNbt() != null) {
               serializedItem.addProperty("tag", this.sNbtSerializer.trySerialize(itemHoverEvent.getNbt()));
            }

            serializedHoverEvent.add("contents", serializedItem);
         } else if (src instanceof EntityHoverEvent) {
            EntityHoverEvent entityHoverEvent = (EntityHoverEvent)src;
            serializedItem = new JsonObject();
            serializedItem.addProperty("type", entityHoverEvent.getEntityType().get());
            serializedItem.addProperty("id", entityHoverEvent.getUuid().toString());
            if (entityHoverEvent.getName() != null) {
               serializedItem.add("name", this.textComponentSerializer.serializeJson(entityHoverEvent.getName()));
            }

            serializedHoverEvent.add("contents", serializedItem);
         }
      }

      return serializedHoverEvent;
   }
}
