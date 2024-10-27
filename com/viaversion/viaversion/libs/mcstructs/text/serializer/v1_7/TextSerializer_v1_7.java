package com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_7;

import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import com.viaversion.viaversion.libs.gson.JsonSerializationContext;
import com.viaversion.viaversion.libs.gson.JsonSerializer;
import com.viaversion.viaversion.libs.mcstructs.text.ATextComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.StringComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.TranslationComponent;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map.Entry;

public class TextSerializer_v1_7 implements JsonSerializer<ATextComponent> {
   public JsonElement serialize(ATextComponent src, Type typeOfSrc, JsonSerializationContext context) {
      if (src instanceof StringComponent && src.getStyle().isEmpty() && src.getSiblings().isEmpty()) {
         return new JsonPrimitive(((StringComponent)src).getText());
      } else {
         JsonObject serializedComponent = new JsonObject();
         if (!src.getStyle().isEmpty()) {
            JsonElement serializedStyle = context.serialize(src.getStyle());
            if (serializedStyle.isJsonObject()) {
               JsonObject serializedStyleObject = serializedStyle.getAsJsonObject();
               Iterator var7 = serializedStyleObject.entrySet().iterator();

               while(var7.hasNext()) {
                  Entry<String, JsonElement> entry = (Entry)var7.next();
                  serializedComponent.add((String)entry.getKey(), (JsonElement)entry.getValue());
               }
            }
         }

         if (!src.getSiblings().isEmpty()) {
            JsonArray siblings = new JsonArray();
            Iterator var14 = src.getSiblings().iterator();

            while(var14.hasNext()) {
               ATextComponent sibling = (ATextComponent)var14.next();
               siblings.add(this.serialize((ATextComponent)sibling, sibling.getClass(), context));
            }

            serializedComponent.add("extra", siblings);
         }

         if (src instanceof StringComponent) {
            serializedComponent.addProperty("text", ((StringComponent)src).getText());
         } else {
            if (!(src instanceof TranslationComponent)) {
               throw new JsonParseException("Don't know how to serialize " + src + " as a Component");
            }

            TranslationComponent translationComponent = (TranslationComponent)src;
            serializedComponent.addProperty("translate", translationComponent.getKey());
            if (translationComponent.getArgs().length > 0) {
               JsonArray with = new JsonArray();
               Object[] args = translationComponent.getArgs();
               Object[] var18 = args;
               int var9 = args.length;

               for(int var10 = 0; var10 < var9; ++var10) {
                  Object arg = var18[var10];
                  if (arg instanceof ATextComponent) {
                     with.add(this.serialize((ATextComponent)((ATextComponent)arg), arg.getClass(), context));
                  } else {
                     with.add((JsonElement)(new JsonPrimitive(String.valueOf(arg))));
                  }
               }

               serializedComponent.add("with", with);
            }
         }

         return serializedComponent;
      }
   }
}
