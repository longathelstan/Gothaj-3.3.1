package com.viaversion.viaversion.bungee.platform;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.bungee.handlers.BungeeChannelInitializer;
import com.viaversion.viaversion.libs.fastutil.ints.IntLinkedOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSet;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.util.ReflectionUtil;
import com.viaversion.viaversion.util.SetWrapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.md_5.bungee.api.ProxyServer;

public class BungeeViaInjector implements ViaInjector {
   private static final Field LISTENERS_FIELD;
   private final List<Channel> injectedChannels = new ArrayList();

   public void inject() throws ReflectiveOperationException {
      Set<Channel> listeners = (Set)LISTENERS_FIELD.get(ProxyServer.getInstance());
      Set<Channel> wrapper = new SetWrapper(listeners, (channelx) -> {
         try {
            this.injectChannel(channelx);
         } catch (Exception var3) {
            throw new RuntimeException(var3);
         }
      });
      LISTENERS_FIELD.set(ProxyServer.getInstance(), wrapper);
      Iterator var3 = listeners.iterator();

      while(var3.hasNext()) {
         Channel channel = (Channel)var3.next();
         this.injectChannel(channel);
      }

   }

   public void uninject() {
      Via.getPlatform().getLogger().severe("ViaVersion cannot remove itself from Bungee without a reboot!");
   }

   private void injectChannel(Channel channel) throws ReflectiveOperationException {
      List<String> names = channel.pipeline().names();
      ChannelHandler bootstrapAcceptor = null;
      Iterator var4 = names.iterator();

      while(var4.hasNext()) {
         String name = (String)var4.next();
         ChannelHandler handler = channel.pipeline().get(name);

         try {
            ReflectionUtil.get(handler, "childHandler", ChannelInitializer.class);
            bootstrapAcceptor = handler;
         } catch (Exception var9) {
         }
      }

      if (bootstrapAcceptor == null) {
         bootstrapAcceptor = channel.pipeline().first();
      }

      if (!bootstrapAcceptor.getClass().getName().equals("net.md_5.bungee.query.QueryHandler")) {
         try {
            ChannelInitializer<Channel> oldInit = (ChannelInitializer)ReflectionUtil.get(bootstrapAcceptor, "childHandler", ChannelInitializer.class);
            ChannelInitializer<Channel> newInit = new BungeeChannelInitializer(oldInit);
            ReflectionUtil.set(bootstrapAcceptor, "childHandler", newInit);
            this.injectedChannels.add(channel);
         } catch (NoSuchFieldException var8) {
            throw new RuntimeException("Unable to find core component 'childHandler', please check your plugins. issue: " + bootstrapAcceptor.getClass().getName());
         }
      }
   }

   public int getServerProtocolVersion() throws Exception {
      return (Integer)this.getBungeeSupportedVersions().get(0);
   }

   public IntSortedSet getServerProtocolVersions() throws Exception {
      return new IntLinkedOpenHashSet(this.getBungeeSupportedVersions());
   }

   private List<Integer> getBungeeSupportedVersions() throws Exception {
      return (List)ReflectionUtil.getStatic(Class.forName("net.md_5.bungee.protocol.ProtocolConstants"), "SUPPORTED_VERSION_IDS", List.class);
   }

   public JsonObject getDump() {
      JsonObject data = new JsonObject();
      JsonArray injectedChannelInitializers = new JsonArray();
      Iterator var3 = this.injectedChannels.iterator();

      while(var3.hasNext()) {
         Channel channel = (Channel)var3.next();
         JsonObject channelInfo = new JsonObject();
         channelInfo.addProperty("channelClass", channel.getClass().getName());
         JsonArray pipeline = new JsonArray();
         Iterator var7 = channel.pipeline().names().iterator();

         while(var7.hasNext()) {
            String pipeName = (String)var7.next();
            JsonObject handlerInfo = new JsonObject();
            handlerInfo.addProperty("name", pipeName);
            ChannelHandler channelHandler = channel.pipeline().get(pipeName);
            if (channelHandler == null) {
               handlerInfo.addProperty("status", "INVALID");
            } else {
               handlerInfo.addProperty("class", channelHandler.getClass().getName());

               try {
                  Object child = ReflectionUtil.get(channelHandler, "childHandler", ChannelInitializer.class);
                  handlerInfo.addProperty("childClass", child.getClass().getName());
                  if (child instanceof BungeeChannelInitializer) {
                     handlerInfo.addProperty("oldInit", ((BungeeChannelInitializer)child).getOriginal().getClass().getName());
                  }
               } catch (ReflectiveOperationException var13) {
               }

               pipeline.add((JsonElement)handlerInfo);
            }
         }

         channelInfo.add("pipeline", pipeline);
         injectedChannelInitializers.add((JsonElement)channelInfo);
      }

      data.add("injectedChannelInitializers", injectedChannelInitializers);

      try {
         Object list = LISTENERS_FIELD.get(ProxyServer.getInstance());
         data.addProperty("currentList", list.getClass().getName());
         if (list instanceof SetWrapper) {
            data.addProperty("wrappedList", ((SetWrapper)list).originalSet().getClass().getName());
         }
      } catch (ReflectiveOperationException var12) {
      }

      return data;
   }

   static {
      try {
         LISTENERS_FIELD = ProxyServer.getInstance().getClass().getDeclaredField("listeners");
         LISTENERS_FIELD.setAccessible(true);
      } catch (ReflectiveOperationException var1) {
         throw new RuntimeException("Unable to access listeners field.", var1);
      }
   }
}
