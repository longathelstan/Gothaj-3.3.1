package com.viaversion.viaversion.protocols.protocol1_12to1_11_1;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import java.util.Iterator;
import java.util.regex.Pattern;

public class ChatItemRewriter {
   private static final Pattern indexRemoval = Pattern.compile("(?<![\\w-.+])\\d+:(?=([^\"\\\\]*(\\\\.|\"([^\"\\\\]*\\\\.)*[^\"\\\\]*\"))*[^\"]*$)");

   public static void toClient(JsonElement element, UserConnection user) {
      if (element instanceof JsonObject) {
         JsonObject obj = (JsonObject)element;
         if (obj.has("hoverEvent")) {
            if (obj.get("hoverEvent") instanceof JsonObject) {
               JsonObject hoverEvent = (JsonObject)obj.get("hoverEvent");
               if (hoverEvent.has("action") && hoverEvent.has("value")) {
                  String type = hoverEvent.get("action").getAsString();
                  if (type.equals("show_item") || type.equals("show_entity")) {
                     JsonElement value = hoverEvent.get("value");
                     if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
                        String newValue = indexRemoval.matcher(value.getAsString()).replaceAll("");
                        hoverEvent.addProperty("value", newValue);
                     } else if (value.isJsonArray()) {
                        JsonArray newArray = new JsonArray();
                        Iterator var7 = value.getAsJsonArray().iterator();

                        while(var7.hasNext()) {
                           JsonElement valueElement = (JsonElement)var7.next();
                           if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()) {
                              String newValue = indexRemoval.matcher(valueElement.getAsString()).replaceAll("");
                              newArray.add((JsonElement)(new JsonPrimitive(newValue)));
                           }
                        }

                        hoverEvent.add("value", newArray);
                     }
                  }
               }
            }
         } else if (obj.has("extra")) {
            toClient(obj.get("extra"), user);
         }
      } else if (element instanceof JsonArray) {
         JsonArray array = (JsonArray)element;
         Iterator var11 = array.iterator();

         while(var11.hasNext()) {
            JsonElement value = (JsonElement)var11.next();
            toClient(value, user);
         }
      }

   }
}
