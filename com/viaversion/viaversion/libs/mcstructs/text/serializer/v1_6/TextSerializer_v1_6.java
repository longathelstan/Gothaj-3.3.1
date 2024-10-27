package com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_6;

import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonSerializationContext;
import com.viaversion.viaversion.libs.gson.JsonSerializer;
import com.viaversion.viaversion.libs.mcstructs.text.ATextComponent;
import com.viaversion.viaversion.libs.mcstructs.text.Style;
import com.viaversion.viaversion.libs.mcstructs.text.components.StringComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.TranslationComponent;
import java.lang.reflect.Type;
import java.util.Iterator;

public class TextSerializer_v1_6 implements JsonSerializer<ATextComponent> {
   public JsonElement serialize(ATextComponent src, Type typeOfSrc, JsonSerializationContext context) {
      Style style = src.getStyle();
      JsonObject component = new JsonObject();
      if (style.getColor() != null && !style.getColor().isRGBColor()) {
         component.addProperty("color", style.getColor().serialize());
      }

      if (style.getBold() != null) {
         component.addProperty("bold", style.isBold());
      }

      if (style.getItalic() != null) {
         component.addProperty("italic", style.isItalic());
      }

      if (style.getUnderlined() != null) {
         component.addProperty("underlined", style.isUnderlined());
      }

      if (style.getObfuscated() != null) {
         component.addProperty("obfuscated", style.isObfuscated());
      }

      if (src instanceof StringComponent) {
         StringComponent stringComponent = (StringComponent)src;
         if (stringComponent.getSiblings().isEmpty()) {
            component.addProperty("text", stringComponent.getText());
         } else {
            JsonArray text = new JsonArray();
            text.add(stringComponent.getText());
            Iterator var8 = stringComponent.getSiblings().iterator();

            while(true) {
               while(var8.hasNext()) {
                  ATextComponent sibling = (ATextComponent)var8.next();
                  if (sibling instanceof StringComponent && sibling.getStyle().isEmpty() && sibling.getSiblings().isEmpty()) {
                     text.add(((StringComponent)sibling).getText());
                  } else {
                     text.add(this.serialize(sibling, typeOfSrc, context));
                  }
               }

               component.add("text", text);
               break;
            }
         }
      } else if (src instanceof TranslationComponent) {
         TranslationComponent translationComponent = (TranslationComponent)src;
         component.addProperty("translate", translationComponent.getKey());
         Object[] args = translationComponent.getArgs();
         if (args != null && args.length > 0) {
            if (args.length == 1 && args[0] instanceof String) {
               component.addProperty("using", (String)args[0]);
            } else {
               JsonArray using = new JsonArray();
               Object[] var17 = args;
               int var10 = args.length;

               for(int var11 = 0; var11 < var10; ++var11) {
                  Object arg = var17[var11];
                  if (arg instanceof String) {
                     using.add((String)arg);
                  } else if (arg instanceof Boolean) {
                     using.add((Boolean)arg);
                  } else if (arg instanceof Character) {
                     using.add((Character)arg);
                  } else if (arg instanceof Number) {
                     using.add((Number)arg);
                  } else if (!(arg instanceof StringComponent)) {
                     if (!(arg instanceof TranslationComponent)) {
                        throw new IllegalArgumentException("Minecraft 1.9 did not support translation arguments of type " + arg.getClass().getName());
                     }

                     using.add(this.serialize((ATextComponent)((TranslationComponent)arg), typeOfSrc, context));
                  } else {
                     StringComponent stringComponent = (StringComponent)arg;
                     if ((stringComponent.getStyle() == null || stringComponent.getStyle().isEmpty()) && stringComponent.getSiblings().isEmpty()) {
                        using.add(stringComponent.getText());
                     } else {
                        using.add(this.serialize((ATextComponent)stringComponent, typeOfSrc, context));
                     }
                  }
               }

               component.add("using", using);
            }
         }
      }

      return component;
   }
}
