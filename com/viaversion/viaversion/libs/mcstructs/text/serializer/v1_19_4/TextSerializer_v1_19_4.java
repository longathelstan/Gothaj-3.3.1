package com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_19_4;

import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import com.viaversion.viaversion.libs.gson.JsonSerializationContext;
import com.viaversion.viaversion.libs.gson.JsonSerializer;
import com.viaversion.viaversion.libs.mcstructs.text.ATextComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.KeybindComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.NbtComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.ScoreComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.SelectorComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.StringComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.TranslationComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.nbt.BlockNbtComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.nbt.EntityNbtComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.nbt.StorageNbtComponent;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map.Entry;

public class TextSerializer_v1_19_4 implements JsonSerializer<ATextComponent> {
   public JsonElement serialize(ATextComponent src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject serializedComponent = new JsonObject();
      JsonObject serializedScore;
      if (!src.getStyle().isEmpty()) {
         JsonElement serializedStyle = context.serialize(src.getStyle());
         if (serializedStyle.isJsonObject()) {
            serializedScore = serializedStyle.getAsJsonObject();
            Iterator var7 = serializedScore.entrySet().iterator();

            while(var7.hasNext()) {
               Entry<String, JsonElement> entry = (Entry)var7.next();
               serializedComponent.add((String)entry.getKey(), (JsonElement)entry.getValue());
            }
         }
      }

      if (!src.getSiblings().isEmpty()) {
         JsonArray siblings = new JsonArray();
         Iterator var15 = src.getSiblings().iterator();

         while(var15.hasNext()) {
            ATextComponent sibling = (ATextComponent)var15.next();
            siblings.add(this.serialize((ATextComponent)sibling, sibling.getClass(), context));
         }

         serializedComponent.add("extra", siblings);
      }

      if (src instanceof StringComponent) {
         serializedComponent.addProperty("text", ((StringComponent)src).getText());
      } else if (src instanceof TranslationComponent) {
         TranslationComponent translationComponent = (TranslationComponent)src;
         serializedComponent.addProperty("translate", translationComponent.getKey());
         if (translationComponent.getFallback() != null) {
            serializedComponent.addProperty("fallback", translationComponent.getFallback());
         }

         if (translationComponent.getArgs().length > 0) {
            JsonArray with = new JsonArray();
            Object[] args = translationComponent.getArgs();
            Object[] var21 = args;
            int var9 = args.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               Object arg = var21[var10];
               if (arg instanceof ATextComponent) {
                  with.add(this.serialize((ATextComponent)((ATextComponent)arg), arg.getClass(), context));
               } else {
                  with.add((JsonElement)(new JsonPrimitive(String.valueOf(arg))));
               }
            }

            serializedComponent.add("with", with);
         }
      } else if (src instanceof ScoreComponent) {
         ScoreComponent scoreComponent = (ScoreComponent)src;
         serializedScore = new JsonObject();
         serializedScore.addProperty("name", scoreComponent.getName());
         serializedScore.addProperty("objective", scoreComponent.getObjective());
         serializedComponent.add("score", serializedScore);
      } else if (src instanceof SelectorComponent) {
         SelectorComponent selectorComponent = (SelectorComponent)src;
         serializedComponent.addProperty("selector", selectorComponent.getSelector());
         if (selectorComponent.getSeparator() != null) {
            serializedComponent.add("separator", this.serialize(selectorComponent.getSeparator(), typeOfSrc, context));
         }
      } else if (src instanceof KeybindComponent) {
         serializedComponent.addProperty("keybind", ((KeybindComponent)src).getKeybind());
      } else {
         if (!(src instanceof NbtComponent)) {
            throw new JsonParseException("Don't know how to serialize " + src + " as a Component");
         }

         NbtComponent nbtComponent = (NbtComponent)src;
         serializedComponent.addProperty("nbt", nbtComponent.getComponent());
         serializedComponent.addProperty("interpret", nbtComponent.isResolve());
         if (nbtComponent.getSeparator() != null) {
            serializedComponent.add("separator", this.serialize(nbtComponent.getSeparator(), typeOfSrc, context));
         }

         if (nbtComponent instanceof BlockNbtComponent) {
            serializedComponent.addProperty("block", ((BlockNbtComponent)nbtComponent).getPos());
         } else if (nbtComponent instanceof EntityNbtComponent) {
            serializedComponent.addProperty("entity", ((EntityNbtComponent)nbtComponent).getSelector());
         } else {
            if (!(nbtComponent instanceof StorageNbtComponent)) {
               throw new JsonParseException("Don't know how to serialize " + src + " as a Component");
            }

            serializedComponent.addProperty("storage", ((StorageNbtComponent)nbtComponent).getId().get());
         }
      }

      return serializedComponent;
   }
}
