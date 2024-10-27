package com.viaversion.viaversion.api.minecraft.signature.model.chain.v1_19_3;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.viaversion.viaversion.api.minecraft.PlayerMessageSignature;
import com.viaversion.viaversion.api.minecraft.signature.util.DataConsumer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class MessageBody {
   private final String content;
   private final Instant timestamp;
   private final long salt;
   private final PlayerMessageSignature[] lastSeenMessages;

   public MessageBody(String content, Instant timestamp, long salt, PlayerMessageSignature[] lastSeenMessages) {
      this.content = content;
      this.timestamp = timestamp;
      this.salt = salt;
      this.lastSeenMessages = lastSeenMessages;
   }

   public void update(DataConsumer dataConsumer) {
      dataConsumer.accept(Longs.toByteArray(this.salt));
      dataConsumer.accept(Longs.toByteArray(this.timestamp.getEpochSecond()));
      byte[] contentData = this.content.getBytes(StandardCharsets.UTF_8);
      dataConsumer.accept(Ints.toByteArray(contentData.length));
      dataConsumer.accept(contentData);
      dataConsumer.accept(Ints.toByteArray(this.lastSeenMessages.length));
      PlayerMessageSignature[] var3 = this.lastSeenMessages;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         PlayerMessageSignature messageSignatureData = var3[var5];
         dataConsumer.accept(messageSignatureData.signatureBytes());
      }

   }
}
