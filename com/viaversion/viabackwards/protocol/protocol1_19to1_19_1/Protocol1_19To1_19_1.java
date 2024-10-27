package com.viaversion.viabackwards.protocol.protocol1_19to1_19_1;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.rewriters.TranslatableRewriter;
import com.viaversion.viabackwards.protocol.protocol1_19to1_19_1.packets.EntityPackets1_19_1;
import com.viaversion.viabackwards.protocol.protocol1_19to1_19_1.storage.ChatRegistryStorage;
import com.viaversion.viabackwards.protocol.protocol1_19to1_19_1.storage.ChatRegistryStorage1_19_1;
import com.viaversion.viabackwards.protocol.protocol1_19to1_19_1.storage.NonceStorage;
import com.viaversion.viabackwards.protocol.protocol1_19to1_19_1.storage.ReceivedMessagesStorage;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.PlayerMessageSignature;
import com.viaversion.viaversion.api.minecraft.ProfileKey;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_19;
import com.viaversion.viaversion.api.minecraft.signature.SignableCommandArgumentsProvider;
import com.viaversion.viaversion.api.minecraft.signature.model.DecoratableMessage;
import com.viaversion.viaversion.api.minecraft.signature.model.MessageMetadata;
import com.viaversion.viaversion.api.minecraft.signature.storage.ChatSession1_19_1;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.ClientboundPackets1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.Protocol1_19_1To1_19;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.ServerboundPackets1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.ClientboundPackets1_19;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.ServerboundPackets1_19;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.packets.EntityPackets;
import com.viaversion.viaversion.rewriter.ComponentRewriter;
import com.viaversion.viaversion.util.CipherUtil;
import com.viaversion.viaversion.util.ComponentUtil;
import com.viaversion.viaversion.util.Pair;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Protocol1_19To1_19_1 extends BackwardsProtocol<ClientboundPackets1_19_1, ClientboundPackets1_19, ServerboundPackets1_19_1, ServerboundPackets1_19> {
   public static final int SYSTEM_CHAT_ID = 1;
   public static final int GAME_INFO_ID = 2;
   private static final UUID ZERO_UUID = new UUID(0L, 0L);
   private static final byte[] EMPTY_BYTES = new byte[0];
   private final EntityPackets1_19_1 entityRewriter = new EntityPackets1_19_1(this);
   private final TranslatableRewriter<ClientboundPackets1_19_1> translatableRewriter;

   public Protocol1_19To1_19_1() {
      super(ClientboundPackets1_19_1.class, ClientboundPackets1_19.class, ServerboundPackets1_19_1.class, ServerboundPackets1_19.class);
      this.translatableRewriter = new TranslatableRewriter(this, ComponentRewriter.ReadType.JSON);
   }

   protected void registerPackets() {
      this.translatableRewriter.registerComponentPacket(ClientboundPackets1_19_1.ACTIONBAR);
      this.translatableRewriter.registerComponentPacket(ClientboundPackets1_19_1.TITLE_TEXT);
      this.translatableRewriter.registerComponentPacket(ClientboundPackets1_19_1.TITLE_SUBTITLE);
      this.translatableRewriter.registerBossBar(ClientboundPackets1_19_1.BOSSBAR);
      this.translatableRewriter.registerComponentPacket(ClientboundPackets1_19_1.DISCONNECT);
      this.translatableRewriter.registerTabList(ClientboundPackets1_19_1.TAB_LIST);
      this.translatableRewriter.registerOpenWindow(ClientboundPackets1_19_1.OPEN_WINDOW);
      this.translatableRewriter.registerCombatKill(ClientboundPackets1_19_1.COMBAT_KILL);
      this.translatableRewriter.registerPing();
      this.entityRewriter.register();
      this.registerClientbound(ClientboundPackets1_19_1.JOIN_GAME, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.BOOLEAN);
            this.map(Type.BYTE);
            this.map(Type.BYTE);
            this.map(Type.STRING_ARRAY);
            this.map(Type.NAMED_COMPOUND_TAG);
            this.map(Type.STRING);
            this.map(Type.STRING);
            this.handler((wrapper) -> {
               ChatRegistryStorage chatTypeStorage = (ChatRegistryStorage)wrapper.user().get(ChatRegistryStorage1_19_1.class);
               chatTypeStorage.clear();
               CompoundTag registry = (CompoundTag)wrapper.get(Type.NAMED_COMPOUND_TAG, 0);
               ListTag chatTypes = (ListTag)((CompoundTag)registry.get("minecraft:chat_type")).get("value");
               Iterator var4 = chatTypes.iterator();

               while(var4.hasNext()) {
                  Tag chatType = (Tag)var4.next();
                  CompoundTag chatTypeCompound = (CompoundTag)chatType;
                  NumberTag idTag = (NumberTag)chatTypeCompound.get("id");
                  chatTypeStorage.addChatType(idTag.asInt(), chatTypeCompound);
               }

               registry.put("minecraft:chat_type", EntityPackets.CHAT_REGISTRY.copy());
            });
            this.handler(Protocol1_19To1_19_1.this.entityRewriter.worldTrackerHandlerByKey());
         }
      });
      this.registerClientbound(ClientboundPackets1_19_1.PLAYER_CHAT, ClientboundPackets1_19.SYSTEM_CHAT, (wrapper) -> {
         wrapper.read(Type.OPTIONAL_BYTE_ARRAY_PRIMITIVE);
         PlayerMessageSignature signature = (PlayerMessageSignature)wrapper.read(Type.PLAYER_MESSAGE_SIGNATURE);
         if (!signature.uuid().equals(ZERO_UUID) && signature.signatureBytes().length != 0) {
            ReceivedMessagesStorage messagesStorage = (ReceivedMessagesStorage)wrapper.user().get(ReceivedMessagesStorage.class);
            messagesStorage.add(signature);
            if (messagesStorage.tickUnacknowledged() > 64) {
               messagesStorage.resetUnacknowledgedCount();
               PacketWrapper chatAckPacket = wrapper.create(ServerboundPackets1_19_1.CHAT_ACK);
               chatAckPacket.write(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY, messagesStorage.lastSignatures());
               chatAckPacket.write(Type.OPTIONAL_PLAYER_MESSAGE_SIGNATURE, (Object)null);
               chatAckPacket.sendToServer(Protocol1_19To1_19_1.class);
            }
         }

         String plainMessage = (String)wrapper.read(Type.STRING);
         JsonElement message = null;
         JsonElement decoratedMessage = (JsonElement)wrapper.read(Type.OPTIONAL_COMPONENT);
         if (decoratedMessage != null) {
            message = decoratedMessage;
         }

         wrapper.read(Type.LONG);
         wrapper.read(Type.LONG);
         wrapper.read(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY);
         JsonElement unsignedMessage = (JsonElement)wrapper.read(Type.OPTIONAL_COMPONENT);
         if (unsignedMessage != null) {
            message = unsignedMessage;
         }

         if (message == null) {
            message = ComponentUtil.plainToJson(plainMessage);
         }

         int filterMaskType = (Integer)wrapper.read(Type.VAR_INT);
         if (filterMaskType == 2) {
            wrapper.read(Type.LONG_ARRAY_PRIMITIVE);
         }

         int chatTypeId = (Integer)wrapper.read(Type.VAR_INT);
         JsonElement senderName = (JsonElement)wrapper.read(Type.COMPONENT);
         JsonElement targetName = (JsonElement)wrapper.read(Type.OPTIONAL_COMPONENT);
         decoratedMessage = decorateChatMessage((ChatRegistryStorage)wrapper.user().get(ChatRegistryStorage1_19_1.class), chatTypeId, senderName, targetName, (JsonElement)message);
         if (decoratedMessage == null) {
            wrapper.cancel();
         } else {
            this.translatableRewriter.processText(decoratedMessage);
            wrapper.write(Type.COMPONENT, decoratedMessage);
            wrapper.write(Type.VAR_INT, 1);
         }
      });
      this.registerClientbound(ClientboundPackets1_19_1.SYSTEM_CHAT, (wrapper) -> {
         JsonElement content = (JsonElement)wrapper.passthrough(Type.COMPONENT);
         this.translatableRewriter.processText(content);
         boolean overlay = (Boolean)wrapper.read(Type.BOOLEAN);
         wrapper.write(Type.VAR_INT, overlay ? 2 : 1);
      });
      this.registerServerbound(ServerboundPackets1_19.CHAT_MESSAGE, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.map(Type.LONG);
            this.map(Type.LONG);
            this.read(Type.BYTE_ARRAY_PRIMITIVE);
            this.read(Type.BOOLEAN);
            this.handler((wrapper) -> {
               ChatSession1_19_1 chatSession = (ChatSession1_19_1)wrapper.user().get(ChatSession1_19_1.class);
               ReceivedMessagesStorage messagesStorage = (ReceivedMessagesStorage)wrapper.user().get(ReceivedMessagesStorage.class);
               if (chatSession != null) {
                  UUID sender = wrapper.user().getProtocolInfo().getUuid();
                  String message = (String)wrapper.get(Type.STRING, 0);
                  long timestamp = (Long)wrapper.get(Type.LONG, 0);
                  long salt = (Long)wrapper.get(Type.LONG, 1);
                  MessageMetadata metadata = new MessageMetadata(sender, timestamp, salt);
                  DecoratableMessage decoratableMessage = new DecoratableMessage(message);
                  byte[] signature = chatSession.signChatMessage(metadata, decoratableMessage, messagesStorage.lastSignatures());
                  wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, signature);
                  wrapper.write(Type.BOOLEAN, decoratableMessage.isDecorated());
               } else {
                  wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, Protocol1_19To1_19_1.EMPTY_BYTES);
                  wrapper.write(Type.BOOLEAN, false);
               }

               messagesStorage.resetUnacknowledgedCount();
               wrapper.write(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY, messagesStorage.lastSignatures());
               wrapper.write(Type.OPTIONAL_PLAYER_MESSAGE_SIGNATURE, (Object)null);
            });
         }
      });
      this.registerServerbound(ServerboundPackets1_19.CHAT_COMMAND, new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.map(Type.LONG);
            this.map(Type.LONG);
            this.handler((wrapper) -> {
               ReceivedMessagesStorage messagesStorage = (ReceivedMessagesStorage)wrapper.user().get(ReceivedMessagesStorage.class);
               ChatSession1_19_1 chatSession = (ChatSession1_19_1)wrapper.user().get(ChatSession1_19_1.class);
               SignableCommandArgumentsProvider argumentsProvider = (SignableCommandArgumentsProvider)Via.getManager().getProviders().get(SignableCommandArgumentsProvider.class);
               int signatures;
               int i;
               if (chatSession != null && argumentsProvider != null) {
                  signatures = (Integer)wrapper.read(Type.VAR_INT);

                  for(i = 0; i < signatures; ++i) {
                     wrapper.read(Type.STRING);
                     wrapper.read(Type.BYTE_ARRAY_PRIMITIVE);
                  }

                  UUID sender = wrapper.user().getProtocolInfo().getUuid();
                  String command = (String)wrapper.get(Type.STRING, 0);
                  long timestamp = (Long)wrapper.get(Type.LONG, 0);
                  long salt = (Long)wrapper.get(Type.LONG, 1);
                  MessageMetadata metadata = new MessageMetadata(sender, timestamp, salt);
                  List<Pair<String, String>> arguments = argumentsProvider.getSignableArguments(command);
                  wrapper.write(Type.VAR_INT, arguments.size());
                  Iterator var13 = arguments.iterator();

                  while(var13.hasNext()) {
                     Pair<String, String> argument = (Pair)var13.next();
                     byte[] signature = chatSession.signChatMessage(metadata, new DecoratableMessage((String)argument.value()), messagesStorage.lastSignatures());
                     wrapper.write(Type.STRING, (String)argument.key());
                     wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, signature);
                  }
               } else {
                  signatures = (Integer)wrapper.passthrough(Type.VAR_INT);

                  for(i = 0; i < signatures; ++i) {
                     wrapper.passthrough(Type.STRING);
                     wrapper.read(Type.BYTE_ARRAY_PRIMITIVE);
                     wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, Protocol1_19To1_19_1.EMPTY_BYTES);
                  }
               }

               wrapper.passthrough(Type.BOOLEAN);
               messagesStorage.resetUnacknowledgedCount();
               wrapper.write(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY, messagesStorage.lastSignatures());
               wrapper.write(Type.OPTIONAL_PLAYER_MESSAGE_SIGNATURE, (Object)null);
            });
         }
      });
      this.registerClientbound(ClientboundPackets1_19_1.SERVER_DATA, new PacketHandlers() {
         public void register() {
            this.map(Type.OPTIONAL_COMPONENT);
            this.map(Type.OPTIONAL_STRING);
            this.map(Type.BOOLEAN);
            this.read(Type.BOOLEAN);
         }
      });
      this.registerServerbound(State.LOGIN, ServerboundLoginPackets.HELLO.getId(), ServerboundLoginPackets.HELLO.getId(), new PacketHandlers() {
         public void register() {
            this.map(Type.STRING);
            this.handler((wrapper) -> {
               ProfileKey profileKey = (ProfileKey)wrapper.read(Type.OPTIONAL_PROFILE_KEY);
               ChatSession1_19_1 chatSession = (ChatSession1_19_1)wrapper.user().get(ChatSession1_19_1.class);
               wrapper.write(Type.OPTIONAL_PROFILE_KEY, chatSession == null ? null : chatSession.getProfileKey());
               wrapper.write(Type.OPTIONAL_UUID, chatSession == null ? null : chatSession.getUuid());
               if (profileKey == null || chatSession != null) {
                  wrapper.user().put(new NonceStorage((byte[])null));
               }

            });
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
                     ViaBackwards.getPlatform().getLogger().warning("Received unexpected data in velocity:player_info (length=" + data.length + ")");
                  }
               }

            });
         }
      });
      this.cancelClientbound(ClientboundPackets1_19_1.CUSTOM_CHAT_COMPLETIONS);
      this.cancelClientbound(ClientboundPackets1_19_1.DELETE_CHAT_MESSAGE);
      this.cancelClientbound(ClientboundPackets1_19_1.PLAYER_CHAT_HEADER);
   }

   public void init(UserConnection user) {
      user.put(new ChatRegistryStorage1_19_1());
      user.put(new ReceivedMessagesStorage());
      this.addEntityTracker(user, new EntityTrackerBase(user, EntityTypes1_19.PLAYER));
   }

   public TranslatableRewriter<ClientboundPackets1_19_1> getTranslatableRewriter() {
      return this.translatableRewriter;
   }

   public EntityPackets1_19_1 getEntityRewriter() {
      return this.entityRewriter;
   }

   @Nullable
   public static JsonElement decorateChatMessage(ChatRegistryStorage chatRegistryStorage, int chatTypeId, JsonElement senderName, @Nullable JsonElement targetName, JsonElement message) {
      CompoundTag chatType = chatRegistryStorage.chatType(chatTypeId);
      if (chatType == null) {
         ViaBackwards.getPlatform().getLogger().warning("Chat message has unknown chat type id " + chatTypeId + ". Message: " + message);
         return null;
      } else {
         chatType = (CompoundTag)((CompoundTag)chatType.get("element")).get("chat");
         return chatType == null ? null : Protocol1_19_1To1_19.translatabaleComponentFromTag(chatType, senderName, targetName, message);
      }
   }
}
