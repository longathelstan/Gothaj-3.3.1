package com.viaversion.viaversion.sponge.platform;

import com.viaversion.viaversion.platform.LegacyViaInjector;
import com.viaversion.viaversion.platform.WrappedChannelInitializer;
import com.viaversion.viaversion.sponge.handlers.SpongeChannelInitializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.Sponge;

public class SpongeViaInjector extends LegacyViaInjector {
   public int getServerProtocolVersion() throws ReflectiveOperationException {
      MinecraftVersion version = Sponge.platform().minecraftVersion();

      try {
         return (Integer)version.getClass().getDeclaredMethod("getProtocol").invoke(version);
      } catch (NoSuchMethodException var3) {
         return (Integer)version.getClass().getDeclaredMethod("protocolVersion").invoke(version);
      }
   }

   @Nullable
   protected Object getServerConnection() throws ReflectiveOperationException {
      Class<?> serverClazz = Class.forName("net.minecraft.server.MinecraftServer");
      return serverClazz.getDeclaredMethod("getConnection").invoke(Sponge.server());
   }

   protected WrappedChannelInitializer createChannelInitializer(ChannelInitializer<Channel> oldInitializer) {
      return new SpongeChannelInitializer(oldInitializer);
   }

   protected void blame(ChannelHandler bootstrapAcceptor) {
      throw new RuntimeException("Unable to find core component 'childHandler', please check your plugins. Issue: " + bootstrapAcceptor.getClass().getName());
   }

   public String getEncoderName() {
      return "encoder";
   }

   public String getDecoderName() {
      return "decoder";
   }
}
