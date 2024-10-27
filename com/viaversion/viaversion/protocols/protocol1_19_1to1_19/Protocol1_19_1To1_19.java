package com.viaversion.viaversion.protocols.protocol1_19_1to1_19;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.ProfileKey;
import com.viaversion.viaversion.api.minecraft.signature.SignableCommandArgumentsProvider;
import com.viaversion.viaversion.api.minecraft.signature.model.DecoratableMessage;
import com.viaversion.viaversion.api.minecraft.signature.model.MessageMetadata;
import com.viaversion.viaversion.api.minecraft.signature.storage.ChatSession1_19_0;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.mcstructs.core.TextFormatting;
import com.viaversion.viaversion.libs.mcstructs.text.ATextComponent;
import com.viaversion.viaversion.libs.mcstructs.text.Style;
import com.viaversion.viaversion.libs.mcstructs.text.components.TranslationComponent;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.TextComponentSerializer;
import com.viaversion.viaversion.libs.opennbt.stringified.SNBT;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.storage.ChatTypeStorage;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.storage.NonceStorage;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.ClientboundPackets1_19;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.ServerboundPackets1_19;
import com.viaversion.viaversion.util.CipherUtil;
import com.viaversion.viaversion.util.Pair;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Protocol1_19_1To1_19 extends AbstractProtocol<ClientboundPackets1_19, ClientboundPackets1_19_1, ServerboundPackets1_19, ServerboundPackets1_19_1> {
   private static final String CHAT_REGISTRY_SNBT = "{\n  \"minecraft:chat_type\": {\n    \"type\": \"minecraft:chat_type\",\n    \"value\": [\n         {\n            \"name\":\"minecraft:chat\",\n            \"id\":1,\n            \"element\":{\n               \"chat\":{\n                  \"translation_key\":\"chat.type.text\",\n                  \"parameters\":[\n                     \"sender\",\n                     \"content\"\n                  ]\n               },\n               \"narration\":{\n                  \"translation_key\":\"chat.type.text.narrate\",\n                  \"parameters\":[\n                     \"sender\",\n                     \"content\"\n                  ]\n               }\n            }\n         }    ]\n  }\n}";
   private static final CompoundTag CHAT_REGISTRY = (CompoundTag)SNBT.deserializeCompoundTag("{\n  \"minecraft:chat_type\": {\n    \"type\": \"minecraft:chat_type\",\n    \"value\": [\n         {\n            \"name\":\"minecraft:chat\",\n            \"id\":1,\n            \"element\":{\n               \"chat\":{\n                  \"translation_key\":\"chat.type.text\",\n                  \"parameters\":[\n                     \"sender\",\n                     \"content\"\n                  ]\n               },\n               \"narration\":{\n                  \"translation_key\":\"chat.type.text.narrate\",\n                  \"parameters\":[\n                     \"sender\",\n                     \"content\"\n                  ]\n               }\n            }\n         }    ]\n  }\n}").get("minecraft:chat_type");

   public Protocol1_19_1To1_19() {
      super(ClientboundPackets1_19.class, ClientboundPackets1_19_1.class, ServerboundPackets1_19.class, ServerboundPackets1_19_1.class);
   }

   protected void registerPackets() {
      this.registerClientbound(ClientboundPackets1_19.SYSTEM_CHAT, new PacketHandlers() {
         public void register() {
            this.map(Type.COMPONENT);
            this.handler((wrapper) -> {
               int type = (Integer)wrapper.read(Type.VAR_INT);
               boolean overlay = type == 2;
               wrapper.write(Type.BOOLEAN, overlay);
            });
         }
      });
      this.registerClientbound(ClientboundPackets1_19.PLAYER_CHAT, ClientboundPackets1_19_1.SYSTEM_CHAT, new PacketHandlers() {
         public void register() {
            this.handler((wrapper) -> {
               JsonElement signedContent = (JsonElement)wrapper.read(Type.COMPONENT);
               JsonElement unsignedContent = (JsonElement)wrapper.read(Type.OPTIONAL_COMPONENT);
               int chatTypeId = (Integer)wrapper.read(Type.VAR_INT);
               wrapper.read(Type.UUID);
               JsonElement senderName = (JsonElement)wrapper.read(Type.COMPONENT);
               JsonElement teamName = (JsonElement)wrapper.read(Type.OPTIONAL_COMPONENT);
               CompoundTag chatType = ((ChatTypeStorage)wrapper.user().get(ChatTypeStorage.class)).chatType(chatTypeId);
               ChatDecorationResult decorationResult = Protocol1_19_1To1_19.decorateChatMessage(chatType, chatTypeId, senderName, teamName, unsignedContent != null ? unsignedContent : signedContent);
               if (decorationResult == null) {
                  wrapper.cancel();
               } else {
                  wrapper.write(Type.COMPONENT, decorationResult.content());
                  wrapper.write(Type.BOOLEAN, decorationResult.overlay());
               }
            });
            this.read(Type.LONG);
            this.read(Type.LONG);
            this.read(Type.BYTE_ARRAY_PRIMITIVE);
         }
      });
      this.registerServerbound(ServerboundPackets1_19_1.CHAT_MESSAGE, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.map(Type.LONG);
            this.map(Type.LONG);
            this.map(Type.BYTE_ARRAY_PRIMITIVE);
            this.map(Type.BOOLEAN);
            this.handler((wrapper) -> {
               ChatSession1_19_0 chatSession = (ChatSession1_19_0)wrapper.user().get(ChatSession1_19_0.class);
               if (chatSession != null) {
                  UUID sender = wrapper.user().getProtocolInfo().getUuid();
                  String message = (String)wrapper.get(Type.STRING, 0);
                  long timestamp = (Long)wrapper.get(Type.LONG, 0);
                  long salt = (Long)wrapper.get(Type.LONG, 1);
                  MessageMetadata metadata = new MessageMetadata(sender, timestamp, salt);
                  DecoratableMessage decoratableMessage = new DecoratableMessage(message);
                  byte[] signature = chatSession.signChatMessage(metadata, decoratableMessage);
                  wrapper.set(Type.BYTE_ARRAY_PRIMITIVE, 0, signature);
                  wrapper.set(Type.BOOLEAN, 0, decoratableMessage.isDecorated());
               }

            });
            this.read(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY);
            this.read(Type.OPTIONAL_PLAYER_MESSAGE_SIGNATURE);
         }
      });
      this.registerServerbound(ServerboundPackets1_19_1.CHAT_COMMAND, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.map(Type.LONG);
            this.map(Type.LONG);
            this.handler((wrapper) -> {
               ChatSession1_19_0 chatSession = (ChatSession1_19_0)wrapper.user().get(ChatSession1_19_0.class);
               SignableCommandArgumentsProvider argumentsProvider = (SignableCommandArgumentsProvider)Via.getManager().getProviders().get(SignableCommandArgumentsProvider.class);
               int signatures = (Integer)wrapper.read(Type.VAR_INT);

               for(int i = 0; i < signatures; ++i) {
                  wrapper.read(Type.STRING);
                  wrapper.read(Type.BYTE_ARRAY_PRIMITIVE);
               }

               if (chatSession != null && argumentsProvider != null) {
                  UUID sender = wrapper.user().getProtocolInfo().getUuid();
                  String message = (String)wrapper.get(Type.STRING, 0);
                  long timestamp = (Long)wrapper.get(Type.LONG, 0);
                  long salt = (Long)wrapper.get(Type.LONG, 1);
                  List<Pair<String, String>> arguments = argumentsProvider.getSignableArguments(message);
                  wrapper.write(Type.VAR_INT, arguments.size());
                  Iterator var11 = arguments.iterator();

                  while(var11.hasNext()) {
                     Pair<String, String> argument = (Pair)var11.next();
                     MessageMetadata metadata = new MessageMetadata(sender, timestamp, salt);
                     DecoratableMessage decoratableMessage = new DecoratableMessage((String)argument.value());
                     byte[] signature = chatSession.signChatMessage(metadata, decoratableMessage);
                     wrapper.write(Type.STRING, argument.key());
                     wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, signature);
                  }
               } else {
                  wrapper.write(Type.VAR_INT, 0);
               }

            });
            this.map(Type.BOOLEAN);
            this.read(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY);
            this.read(Type.OPTIONAL_PLAYER_MESSAGE_SIGNATURE);
         }
      });
      this.cancelServerbound(ServerboundPackets1_19_1.CHAT_ACK);
      this.registerClientbound(ClientboundPackets1_19.JOIN_GAME, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.BOOLEAN);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.STRING_ARRAY);
            this.handler((wrapper) -> {
               ChatTypeStorage chatTypeStorage = (ChatTypeStorage)wrapper.user().get(ChatTypeStorage.class);
               chatTypeStorage.clear();
               CompoundTag registry = (CompoundTag)wrapper.passthrough(Type.NAMED_COMPOUND_TAG);
               ListTag chatTypes = (ListTag)((CompoundTag)registry.get("minecraft:chat_type")).get("value");
               Iterator var4 = chatTypes.iterator();

               while(var4.hasNext()) {
                  Tag chatType = (Tag)var4.next();
                  CompoundTag chatTypeCompound = (CompoundTag)chatType;
                  NumberTag idTag = (NumberTag)chatTypeCompound.get("id");
                  chatTypeStorage.addChatType(idTag.asInt(), chatTypeCompound);
               }

               registry.put("minecraft:chat_type", Protocol1_19_1To1_19.CHAT_REGISTRY.copy());
            });
         }
      });
      this.registerClientbound(ClientboundPackets1_19.SERVER_DATA, new PacketHandlers() {
         public void register() {
            this.map(Type.OPTIONAL_COMPONENT);
            this.map(Type.OPTIONAL_STRING);
            this.map(Type.BOOLEAN);
            this.create(Type.BOOLEAN, false);
         }
      });
      this.registerServerbound(State.LOGIN, ServerboundLoginPackets.HELLO.getId(), ServerboundLoginPackets.HELLO.getId(), new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.handler((wrapper) -> {
               ProfileKey profileKey = (ProfileKey)wrapper.read(Type.OPTIONAL_PROFILE_KEY);
               ChatSession1_19_0 chatSession = (ChatSession1_19_0)wrapper.user().get(ChatSession1_19_0.class);
               wrapper.write(Type.OPTIONAL_PROFILE_KEY, chatSession == null ? null : chatSession.getProfileKey());
               if (profileKey == null || chatSession != null) {
                  wrapper.user().put(new NonceStorage((byte[])null));
               }

            });
            this.read(Type.OPTIONAL_UUID);
         }
      });
      this.registerClientbound(State.LOGIN, ClientboundLoginPackets.HELLO.getId(), ClientboundLoginPackets.HELLO.getId(), new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.handler((wrapper) -> {
               if (!wrapper.user().has(NonceStorage.class)) {
                  byte[] publicKey = (byte[])wrapper.passthrough(Type.BYTE_ARRAY_PRIMITIVE);
                  byte[] nonce = (byte[])wrapper.passthrough(Type.BYTE_ARRAY_PRIMITIVE);
                  wrapper.user().put(new NonceStorage(CipherUtil.encryptNonce(publicKey, nonce)));
               }
            });
         }
      });
      this.registerServerbound(State.LOGIN, ServerboundLoginPackets.ENCRYPTION_KEY.getId(), ServerboundLoginPackets.ENCRYPTION_KEY.getId(), new PacketHandlers() {
         public void register() {
            this.map(Type.BYTE_ARRAY_PRIMITIVE);
            this.handler((wrapper) -> {
               NonceStorage nonceStorage = (NonceStorage)wrapper.user().remove(NonceStorage.class);
               if (nonceStorage.nonce() != null) {
                  boolean isNonce = (Boolean)wrapper.read(Type.BOOLEAN);
                  wrapper.write(Type.BOOLEAN, true);
                  if (!isNonce) {
                     wrapper.read(Type.LONG);
                     wrapper.read(Type.BYTE_ARRAY_PRIMITIVE);
                     wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, nonceStorage.nonce());
                  }

               }
            });
         }
      });
      this.registerClientbound(State.LOGIN, ClientboundLoginPackets.CUSTOM_QUERY.getId(), ClientboundLoginPackets.CUSTOM_QUERY.getId(), new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.STRING);
            this.handler((wrapper) -> {
               String identifier = (String)wrapper.get(Type.STRING, 0);
               if (identifier.equals("velocity:player_info")) {
                  byte[] data = (byte[])wrapper.passthrough(Type.REMAINING_BYTES);
                  if (data.length == 1 && data[0] > 1) {
                     data[0] = 1;
                  } else if (data.length == 0) {
                     data = new byte[]{1};
                     wrapper.set(Type.REMAINING_BYTES, 0, data);
                  } else {
                     Via.getPlatform().getLogger().warning("Received unexpected data in velocity:player_info (length=" + data.length + ")");
                  }
               }

            });
         }
      });
   }

   public void init(UserConnection connection) {
      connection.put(new ChatTypeStorage());
   }

   @Nullable
   public static ChatDecorationResult decorateChatMessage(CompoundTag chatType, int chatTypeId, JsonElement senderName, @Nullable JsonElement teamName, JsonElement message) {
      if (chatType == null) {
         Via.getPlatform().getLogger().warning("Chat message has unknown chat type id " + chatTypeId + ". Message: " + message);
         return null;
      } else {
         CompoundTag chatData = (CompoundTag)((CompoundTag)chatType.get("element")).get("chat");
         boolean overlay = false;
         if (chatData == null) {
            chatData = (CompoundTag)((CompoundTag)chatType.get("element")).get("overlay");
            if (chatData == null) {
               return null;
            }

            overlay = true;
         }

         CompoundTag decoration = (CompoundTag)chatData.get("decoration");
         return decoration == null ? new ChatDecorationResult(message, overlay) : new ChatDecorationResult(translatabaleComponentFromTag(decoration, senderName, teamName, message), overlay);
      }
   }

   public static JsonElement translatabaleComponentFromTag(CompoundTag tag, JsonElement senderName, @Nullable JsonElement targetName, JsonElement message) {
      String translationKey = (String)tag.get("translation_key").getValue();
      Style style = new Style();
      CompoundTag styleTag = (CompoundTag)tag.get("style");
      Tag element;
      if (styleTag != null) {
         StringTag color = (StringTag)styleTag.get("color");
         if (color != null) {
            TextFormatting textColor = TextFormatting.getByName(color.getValue());
            if (textColor != null) {
               style.setFormatting(textColor);
            }
         }

         Iterator var15 = TextFormatting.FORMATTINGS.entrySet().iterator();

         while(var15.hasNext()) {
            Entry<String, TextFormatting> entry = (Entry)var15.next();
            element = styleTag.get((String)entry.getKey());
            if (element instanceof ByteTag) {
               boolean value = ((NumberTag)element).asBoolean();
               TextFormatting formatting = (TextFormatting)entry.getValue();
               if (formatting == TextFormatting.OBFUSCATED) {
                  style.setObfuscated(value);
               } else if (formatting == TextFormatting.BOLD) {
                  style.setBold(value);
               } else if (formatting == TextFormatting.STRIKETHROUGH) {
                  style.setStrikethrough(value);
               } else if (formatting == TextFormatting.UNDERLINE) {
                  style.setUnderlined(value);
               } else if (formatting == TextFormatting.ITALIC) {
                  style.setItalic(value);
               }
            }
         }
      }

      ListTag parameters = (ListTag)tag.get("parameters");
      List<ATextComponent> arguments = new ArrayList();
      if (parameters != null) {
         Iterator var17 = parameters.iterator();

         while(var17.hasNext()) {
            element = (Tag)var17.next();
            JsonElement argument = null;
            String var19 = (String)element.getValue();
            byte var13 = -1;
            switch(var19.hashCode()) {
            case -905962955:
               if (var19.equals("sender")) {
                  var13 = 0;
               }
               break;
            case -880905839:
               if (var19.equals("target")) {
                  var13 = 3;
               }
               break;
            case -175906003:
               if (var19.equals("team_name")) {
                  var13 = 2;
               }
               break;
            case 951530617:
               if (var19.equals("content")) {
                  var13 = 1;
               }
            }

            switch(var13) {
            case 0:
               argument = senderName;
               break;
            case 1:
               argument = message;
               break;
            case 2:
            case 3:
               Preconditions.checkNotNull(targetName, "Team name is null");
               argument = targetName;
               break;
            default:
               Via.getPlatform().getLogger().warning("Unknown parameter for chat decoration: " + element.getValue());
            }

            if (argument != null) {
               arguments.add(TextComponentSerializer.V1_18.deserialize(argument));
            }
         }
      }

      return TextComponentSerializer.V1_18.serializeJson(new TranslationComponent(translationKey, arguments));
   }
}
