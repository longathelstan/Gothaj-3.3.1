package com.viaversion.viaversion.api.minecraft.signature.model.chain.v1_19_1;

import com.viaversion.viaversion.api.minecraft.PlayerMessageSignature;
import com.viaversion.viaversion.api.minecraft.signature.model.DecoratableMessage;
import com.viaversion.viaversion.api.minecraft.signature.util.DataConsumer;
import com.viaversion.viaversion.libs.mcstructs.text.utils.JsonUtils;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Comparator;

public class MessageBody {
   private static final byte HASH_SEPARATOR_BYTE = 70;
   private final DecoratableMessage content;
   private final Instant timestamp;
   private final long salt;
   private final PlayerMessageSignature[] lastSeenMessages;

   public MessageBody(DecoratableMessage content, Instant timestamp, long salt, PlayerMessageSignature[] lastSeenMessages) {
      this.content = content;
      this.timestamp = timestamp;
      this.salt = salt;
      this.lastSeenMessages = lastSeenMessages;
   }

   public void update(DataConsumer dataConsumer) {
      try {
         MessageDigest digest = MessageDigest.getInstance("SHA-256");
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
         dataOutputStream.writeLong(this.salt);
         dataOutputStream.writeLong(this.timestamp.getEpochSecond());
         dataOutputStream.write(this.content.plain().getBytes(StandardCharsets.UTF_8));
         dataOutputStream.write(70);
         if (this.content.isDecorated()) {
            dataOutputStream.write(JsonUtils.toSortedString(this.content.decorated(), (Comparator)null).getBytes(StandardCharsets.UTF_8));
         }

         PlayerMessageSignature[] var5 = this.lastSeenMessages;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            PlayerMessageSignature lastSeenMessage = var5[var7];
            dataOutputStream.writeByte(70);
            dataOutputStream.writeLong(lastSeenMessage.uuid().getMostSignificantBits());
            dataOutputStream.writeLong(lastSeenMessage.uuid().getLeastSignificantBits());
            dataOutputStream.write(lastSeenMessage.signatureBytes());
         }

         digest.update(outputStream.toByteArray());
         dataConsumer.accept(digest.digest());
      } catch (IOException | NoSuchAlgorithmException var9) {
         throw new RuntimeException(var9);
      }
   }
}
