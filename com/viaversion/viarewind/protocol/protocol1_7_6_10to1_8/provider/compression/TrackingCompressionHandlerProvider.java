package com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.provider.compression;

import com.viaversion.viarewind.api.netty.EmptyChannelHandler;
import com.viaversion.viarewind.api.netty.ForwardMessageToByteEncoder;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.provider.CompressionHandlerProvider;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;

public class TrackingCompressionHandlerProvider extends CompressionHandlerProvider {
   public static final String COMPRESS_HANDLER_NAME = "compress";
   public static final String DECOMPRESS_HANDLER_NAME = "decompress";

   public void onHandleLoginCompressionPacket(UserConnection user, int threshold) {
      ChannelPipeline pipeline = user.getChannel().pipeline();
      if (user.isClientSide()) {
         pipeline.addBefore(Via.getManager().getInjector().getEncoderName(), "compress", this.getEncoder(threshold));
         pipeline.addBefore(Via.getManager().getInjector().getDecoderName(), "decompress", this.getDecoder(threshold));
      } else {
         this.setCompressionEnabled(user, true);
      }

   }

   public void onTransformPacket(UserConnection user) {
      if (this.isCompressionEnabled(user)) {
         ChannelPipeline pipeline = user.getChannel().pipeline();
         String compressor = null;
         String decompressor = null;
         if (pipeline.get("compress") != null) {
            compressor = "compress";
            decompressor = "decompress";
         } else if (pipeline.get("compression-encoder") != null) {
            compressor = "compression-encoder";
            decompressor = "compression-decoder";
         }

         if (compressor == null) {
            throw new IllegalStateException("Couldn't remove compression for 1.7!");
         }

         pipeline.replace(decompressor, decompressor, new EmptyChannelHandler());
         pipeline.replace(compressor, compressor, new ForwardMessageToByteEncoder());
         this.setCompressionEnabled(user, false);
      }

   }

   public ChannelHandler getEncoder(int threshold) {
      return new CompressionEncoder(threshold);
   }

   public ChannelHandler getDecoder(int threshold) {
      return new CompressionDecoder(threshold);
   }
}
