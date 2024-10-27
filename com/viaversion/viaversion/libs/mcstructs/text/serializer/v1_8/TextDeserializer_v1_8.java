package com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_8;

import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonDeserializationContext;
import com.viaversion.viaversion.libs.gson.JsonDeserializer;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.libs.mcstructs.text.ATextComponent;
import com.viaversion.viaversion.libs.mcstructs.text.Style;
import com.viaversion.viaversion.libs.mcstructs.text.components.ScoreComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.SelectorComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.StringComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.TranslationComponent;
import com.viaversion.viaversion.libs.mcstructs.text.utils.JsonUtils;
import java.lang.reflect.Type;
import java.util.Iterator;

public class TextDeserializer_v1_8 implements JsonDeserializer<ATextComponent> {
   public ATextComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if (json.isJsonPrimitive()) {
         return new StringComponent(json.getAsString());
      } else if (json.isJsonArray()) {
         JsonArray array = json.getAsJsonArray();
         ATextComponent component = null;
         Iterator var16 = array.iterator();

         while(var16.hasNext()) {
            JsonElement element = (JsonElement)var16.next();
            ATextComponent serializedElement = this.deserialize(element, element.getClass(), context);
            if (component == null) {
               component = serializedElement;
            } else {
               component.append(serializedElement);
            }
         }

         return component;
      } else if (!json.isJsonObject()) {
         throw new JsonParseException("Don't know how to turn " + json + " into a Component");
      } else {
         JsonObject rawComponent = json.getAsJsonObject();
         Object component;
         if (rawComponent.has("text")) {
            component = new StringComponent(rawComponent.get("text").getAsString());
         } else if (rawComponent.has("translate")) {
            String translate = rawComponent.get("translate").getAsString();
            if (rawComponent.has("with")) {
               JsonArray with = rawComponent.getAsJsonArray("with");
               Object[] args = new Object[with.size()];

               for(int i = 0; i < with.size(); ++i) {
                  ATextComponent element = this.deserialize(with.get(i), typeOfT, context);
                  args[i] = element;
                  if (element instanceof StringComponent) {
                     StringComponent stringComponent = (StringComponent)element;
                     if (stringComponent.getStyle().isEmpty() && stringComponent.getSiblings().isEmpty()) {
                        args[i] = stringComponent.getText();
                     }
                  }
               }

               component = new TranslationComponent(translate, args);
            } else {
               component = new TranslationComponent(translate, new Object[0]);
            }
         } else if (rawComponent.has("score")) {
            JsonObject score = rawComponent.getAsJsonObject("score");
            if (!score.has("name") || !score.has("objective")) {
               throw new JsonParseException("A score component needs at least a name and an objective");
            }

            component = new ScoreComponent(JsonUtils.getString(score, "name"), JsonUtils.getString(score, "objective"));
            if (score.has("value")) {
               ((ScoreComponent)component).setValue(JsonUtils.getString(score, "value"));
            }
         } else {
            if (!rawComponent.has("selector")) {
               throw new JsonParseException("Don't know how to turn " + json + " into a Component");
            }

            component = new SelectorComponent(JsonUtils.getString(rawComponent, "selector"), (ATextComponent)null);
         }

         if (rawComponent.has("extra")) {
            JsonArray extra = rawComponent.getAsJsonArray("extra");
            if (extra.isEmpty()) {
               throw new JsonParseException("Unexpected empty array of components");
            }

            Iterator var17 = extra.iterator();

            while(var17.hasNext()) {
               JsonElement element = (JsonElement)var17.next();
               ((ATextComponent)component).append(this.deserialize(element, typeOfT, context));
            }
         }

         Style newStyle = (Style)context.deserialize(rawComponent, Style.class);
         if (newStyle != null) {
            ((ATextComponent)component).setStyle(newStyle);
         }

         return (ATextComponent)component;
      }
   }
}
