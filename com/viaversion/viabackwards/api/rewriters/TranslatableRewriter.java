package com.viaversion.viabackwards.api.rewriters;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.data.VBMappingDataLoader;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.rewriter.ComponentRewriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.checkerframework.checker.nullness.qual.Nullable;

public class TranslatableRewriter<C extends ClientboundPacketType> extends ComponentRewriter<C> {
   private static final Map<String, Map<String, String>> TRANSLATABLES = new HashMap();
   private final Map<String, String> translatables;

   public static void loadTranslatables() {
      JsonObject jsonObject = VBMappingDataLoader.loadFromDataDir("translation-mappings.json");
      Iterator var1 = jsonObject.entrySet().iterator();

      while(var1.hasNext()) {
         Entry<String, JsonElement> entry = (Entry)var1.next();
         Map<String, String> versionMappings = new HashMap();
         TRANSLATABLES.put((String)entry.getKey(), versionMappings);
         Iterator var4 = ((JsonElement)entry.getValue()).getAsJsonObject().entrySet().iterator();

         while(var4.hasNext()) {
            Entry<String, JsonElement> translationEntry = (Entry)var4.next();
            versionMappings.put((String)translationEntry.getKey(), ((JsonElement)translationEntry.getValue()).getAsString());
         }
      }

   }

   public TranslatableRewriter(BackwardsProtocol<C, ?, ?, ?> protocol, ComponentRewriter.ReadType type) {
      this(protocol, type, protocol.getClass().getSimpleName().split("To")[1].replace("_", "."));
   }

   public TranslatableRewriter(BackwardsProtocol<C, ?, ?, ?> protocol, ComponentRewriter.ReadType type, String sectionIdentifier) {
      super(protocol, type);
      Map<String, String> translatableMappings = (Map)TRANSLATABLES.get(sectionIdentifier);
      if (translatableMappings == null) {
         ViaBackwards.getPlatform().getLogger().warning("Missing " + sectionIdentifier + " translatables!");
         this.translatables = new HashMap();
      } else {
         this.translatables = translatableMappings;
      }

   }

   protected void handleTranslate(JsonObject root, String translate) {
      String newTranslate = this.mappedTranslationKey(translate);
      if (newTranslate != null) {
         root.addProperty("translate", newTranslate);
      }

   }

   @Nullable
   public String mappedTranslationKey(String translationKey) {
      return (String)this.translatables.get(translationKey);
   }
}
