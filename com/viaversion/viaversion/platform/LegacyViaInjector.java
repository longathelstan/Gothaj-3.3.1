package com.viaversion.viaversion.platform;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.util.Pair;
import com.viaversion.viaversion.util.ReflectionUtil;
import com.viaversion.viaversion.util.SynchronizedListWrapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class LegacyViaInjector implements ViaInjector {
   protected final List<ChannelFuture> injectedFutures = new ArrayList();
   protected final List<Pair<Field, Object>> injectedLists = new ArrayList();

   public void inject() throws ReflectiveOperationException {
      Object connection = this.getServerConnection();
      if (connection == null) {
         throw new RuntimeException("Failed to find the core component 'ServerConnection'");
      } else {
         Field[] var2 = connection.getClass().getDeclaredFields();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Field field = var2[var4];
            if (List.class.isAssignableFrom(field.getType()) && field.getGenericType().getTypeName().contains(ChannelFuture.class.getName())) {
               field.setAccessible(true);
               List<ChannelFuture> list = (List)field.get(connection);
               List<ChannelFuture> wrappedList = new SynchronizedListWrapper(list, (o) -> {
                  try {
                     this.injectChannelFuture((ChannelFuture)o);
                  } catch (ReflectiveOperationException var3) {
                     throw new RuntimeException(var3);
                  }
               });
               synchronized(list) {
                  Iterator var9 = list.iterator();

                  while(true) {
                     if (!var9.hasNext()) {
                        field.set(connection, wrappedList);
                        break;
                     }

                     ChannelFuture future = (ChannelFuture)var9.next();
                     this.injectChannelFuture(future);
                  }
               }

               this.injectedLists.add(new Pair(field, connection));
            }
         }

      }
   }

   private void injectChannelFuture(ChannelFuture future) throws ReflectiveOperationException {
      List<String> names = future.channel().pipeline().names();
      ChannelHandler bootstrapAcceptor = null;
      Iterator var4 = names.iterator();

      while(var4.hasNext()) {
         String name = (String)var4.next();
         ChannelHandler handler = future.channel().pipeline().get(name);

         try {
            ReflectionUtil.get(handler, "childHandler", ChannelInitializer.class);
            bootstrapAcceptor = handler;
            break;
         } catch (ReflectiveOperationException var9) {
         }
      }

      if (bootstrapAcceptor == null) {
         bootstrapAcceptor = future.channel().pipeline().first();
      }

      try {
         ChannelInitializer<Channel> oldInitializer = (ChannelInitializer)ReflectionUtil.get(bootstrapAcceptor, "childHandler", ChannelInitializer.class);
         ReflectionUtil.set(bootstrapAcceptor, "childHandler", this.createChannelInitializer(oldInitializer));
         this.injectedFutures.add(future);
      } catch (NoSuchFieldException var8) {
         this.blame(bootstrapAcceptor);
      }

   }

   public void uninject() throws ReflectiveOperationException {
      Iterator var1 = this.injectedFutures.iterator();

      while(true) {
         while(var1.hasNext()) {
            ChannelFuture future = (ChannelFuture)var1.next();
            ChannelPipeline pipeline = future.channel().pipeline();
            ChannelHandler bootstrapAcceptor = pipeline.first();
            if (bootstrapAcceptor == null) {
               Via.getPlatform().getLogger().info("Empty pipeline, nothing to uninject");
            } else {
               Iterator var5 = pipeline.names().iterator();

               while(var5.hasNext()) {
                  String name = (String)var5.next();
                  ChannelHandler handler = pipeline.get(name);
                  if (handler == null) {
                     Via.getPlatform().getLogger().warning("Could not get handler " + name);
                  } else {
                     try {
                        if (ReflectionUtil.get(handler, "childHandler", ChannelInitializer.class) instanceof WrappedChannelInitializer) {
                           bootstrapAcceptor = handler;
                           break;
                        }
                     } catch (ReflectiveOperationException var13) {
                     }
                  }
               }

               try {
                  ChannelInitializer<Channel> initializer = (ChannelInitializer)ReflectionUtil.get(bootstrapAcceptor, "childHandler", ChannelInitializer.class);
                  if (initializer instanceof WrappedChannelInitializer) {
                     ReflectionUtil.set(bootstrapAcceptor, "childHandler", ((WrappedChannelInitializer)initializer).original());
                  }
               } catch (Exception var12) {
                  Via.getPlatform().getLogger().log(Level.SEVERE, "Failed to remove injection handler, reload won't work with connections, please reboot!", var12);
               }
            }
         }

         this.injectedFutures.clear();
         var1 = this.injectedLists.iterator();

         while(var1.hasNext()) {
            Pair pair = (Pair)var1.next();

            try {
               Field field = (Field)pair.key();
               Object o = field.get(pair.value());
               if (o instanceof SynchronizedListWrapper) {
                  List<ChannelFuture> originalList = ((SynchronizedListWrapper)o).originalList();
                  synchronized(originalList) {
                     field.set(pair.value(), originalList);
                  }
               }
            } catch (ReflectiveOperationException var11) {
               Via.getPlatform().getLogger().severe("Failed to remove injection, reload won't work with connections, please reboot!");
            }
         }

         this.injectedLists.clear();
         return;
      }
   }

   public boolean lateProtocolVersionSetting() {
      return true;
   }

   public JsonObject getDump() {
      JsonObject data = new JsonObject();
      JsonArray injectedChannelInitializers = new JsonArray();
      data.add("injectedChannelInitializers", injectedChannelInitializers);
      Iterator var3 = this.injectedFutures.iterator();

      while(var3.hasNext()) {
         ChannelFuture future = (ChannelFuture)var3.next();
         JsonObject futureInfo = new JsonObject();
         injectedChannelInitializers.add((JsonElement)futureInfo);
         futureInfo.addProperty("futureClass", future.getClass().getName());
         futureInfo.addProperty("channelClass", future.channel().getClass().getName());
         JsonArray pipeline = new JsonArray();
         futureInfo.add("pipeline", pipeline);
         Iterator var7 = future.channel().pipeline().names().iterator();

         while(var7.hasNext()) {
            String pipeName = (String)var7.next();
            JsonObject handlerInfo = new JsonObject();
            pipeline.add((JsonElement)handlerInfo);
            handlerInfo.addProperty("name", pipeName);
            ChannelHandler channelHandler = future.channel().pipeline().get(pipeName);
            if (channelHandler == null) {
               handlerInfo.addProperty("status", "INVALID");
            } else {
               handlerInfo.addProperty("class", channelHandler.getClass().getName());

               try {
                  Object child = ReflectionUtil.get(channelHandler, "childHandler", ChannelInitializer.class);
                  handlerInfo.addProperty("childClass", child.getClass().getName());
                  if (child instanceof WrappedChannelInitializer) {
                     handlerInfo.addProperty("oldInit", ((WrappedChannelInitializer)child).original().getClass().getName());
                  }
               } catch (ReflectiveOperationException var12) {
               }
            }
         }
      }

      JsonObject wrappedLists = new JsonObject();
      JsonObject currentLists = new JsonObject();

      try {
         Iterator var16 = this.injectedLists.iterator();

         while(var16.hasNext()) {
            Pair<Field, Object> pair = (Pair)var16.next();
            Field field = (Field)pair.key();
            Object list = field.get(pair.value());
            currentLists.addProperty(field.getName(), list.getClass().getName());
            if (list instanceof SynchronizedListWrapper) {
               wrappedLists.addProperty(field.getName(), ((SynchronizedListWrapper)list).originalList().getClass().getName());
            }
         }

         data.add("wrappedLists", wrappedLists);
         data.add("currentLists", currentLists);
      } catch (ReflectiveOperationException var13) {
      }

      return data;
   }

   @Nullable
   protected abstract Object getServerConnection() throws ReflectiveOperationException;

   protected abstract WrappedChannelInitializer createChannelInitializer(ChannelInitializer<Channel> var1);

   protected abstract void blame(ChannelHandler var1) throws ReflectiveOperationException;
}
