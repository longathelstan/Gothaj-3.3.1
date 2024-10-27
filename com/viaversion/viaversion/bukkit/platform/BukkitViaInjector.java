package com.viaversion.viaversion.bukkit.platform;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.bukkit.handlers.BukkitChannelInitializer;
import com.viaversion.viaversion.bukkit.util.NMSUtil;
import com.viaversion.viaversion.platform.LegacyViaInjector;
import com.viaversion.viaversion.platform.WrappedChannelInitializer;
import com.viaversion.viaversion.util.ReflectionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BukkitViaInjector extends LegacyViaInjector {
   private static final boolean HAS_WORLD_VERSION_PROTOCOL_VERSION = PaperViaInjector.hasClass("net.minecraft.SharedConstants") && PaperViaInjector.hasClass("net.minecraft.WorldVersion") && !PaperViaInjector.hasClass("com.mojang.bridge.game.GameVersion");

   public void inject() throws ReflectiveOperationException {
      if (PaperViaInjector.PAPER_INJECTION_METHOD) {
         PaperViaInjector.setPaperChannelInitializeListener();
      } else {
         super.inject();
      }
   }

   public void uninject() throws ReflectiveOperationException {
      if (PaperViaInjector.PAPER_INJECTION_METHOD) {
         PaperViaInjector.removePaperChannelInitializeListener();
      } else {
         super.uninject();
      }
   }

   public int getServerProtocolVersion() throws ReflectiveOperationException {
      if (PaperViaInjector.PAPER_PROTOCOL_METHOD) {
         return Bukkit.getUnsafe().getProtocolVersion();
      } else {
         return HAS_WORLD_VERSION_PROTOCOL_VERSION ? this.cursedProtocolDetection() : this.veryCursedProtocolDetection();
      }
   }

   private int cursedProtocolDetection() throws ReflectiveOperationException {
      Class<?> sharedConstantsClass = Class.forName("net.minecraft.SharedConstants");
      Class<?> worldVersionClass = Class.forName("net.minecraft.WorldVersion");
      Method getWorldVersionMethod = null;
      Method[] var4 = sharedConstantsClass.getDeclaredMethods();
      int var5 = var4.length;

      int var6;
      for(var6 = 0; var6 < var5; ++var6) {
         Method method = var4[var6];
         if (method.getReturnType() == worldVersionClass && method.getParameterTypes().length == 0) {
            getWorldVersionMethod = method;
            break;
         }
      }

      Preconditions.checkNotNull(getWorldVersionMethod, "Failed to get world version method");
      Object worldVersion = getWorldVersionMethod.invoke((Object)null);
      Method[] var10 = worldVersionClass.getDeclaredMethods();
      var6 = var10.length;

      for(int var11 = 0; var11 < var6; ++var11) {
         Method method = var10[var11];
         if (method.getReturnType() == Integer.TYPE && method.getParameterTypes().length == 0) {
            return (Integer)method.invoke(worldVersion);
         }
      }

      throw new IllegalAccessException("Failed to find protocol version method in WorldVersion");
   }

   private int veryCursedProtocolDetection() throws ReflectiveOperationException {
      Class<?> serverClazz = NMSUtil.nms("MinecraftServer", "net.minecraft.server.MinecraftServer");
      Object server = ReflectionUtil.invokeStatic(serverClazz, "getServer");
      Preconditions.checkNotNull(server, "Failed to get server instance");
      Class<?> pingClazz = NMSUtil.nms("ServerPing", "net.minecraft.network.protocol.status.ServerPing");
      Object ping = null;
      Field[] var5 = serverClazz.getDeclaredFields();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Field field = var5[var7];
         if (field.getType() == pingClazz) {
            field.setAccessible(true);
            ping = field.get(server);
            break;
         }
      }

      Preconditions.checkNotNull(ping, "Failed to get server ping");
      Class<?> serverDataClass = NMSUtil.nms("ServerPing$ServerData", "net.minecraft.network.protocol.status.ServerPing$ServerData");
      Object serverData = null;
      Field[] var14 = pingClazz.getDeclaredFields();
      int var15 = var14.length;

      int var9;
      Field field;
      for(var9 = 0; var9 < var15; ++var9) {
         field = var14[var9];
         if (field.getType() == serverDataClass) {
            field.setAccessible(true);
            serverData = field.get(ping);
            break;
         }
      }

      Preconditions.checkNotNull(serverData, "Failed to get server data");
      var14 = serverDataClass.getDeclaredFields();
      var15 = var14.length;

      for(var9 = 0; var9 < var15; ++var9) {
         field = var14[var9];
         if (field.getType() == Integer.TYPE) {
            field.setAccessible(true);
            int protocolVersion = (Integer)field.get(serverData);
            if (protocolVersion != -1) {
               return protocolVersion;
            }
         }
      }

      throw new RuntimeException("Failed to get server");
   }

   @Nullable
   protected Object getServerConnection() throws ReflectiveOperationException {
      Class<?> serverClass = NMSUtil.nms("MinecraftServer", "net.minecraft.server.MinecraftServer");
      Class<?> connectionClass = NMSUtil.nms("ServerConnection", "net.minecraft.server.network.ServerConnection");
      Object server = ReflectionUtil.invokeStatic(serverClass, "getServer");
      Method[] var4 = serverClass.getDeclaredMethods();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Method method = var4[var6];
         if (method.getReturnType() == connectionClass && method.getParameterTypes().length == 0) {
            Object connection = method.invoke(server);
            if (connection != null) {
               return connection;
            }
         }
      }

      return null;
   }

   protected WrappedChannelInitializer createChannelInitializer(ChannelInitializer<Channel> oldInitializer) {
      return new BukkitChannelInitializer(oldInitializer);
   }

   protected void blame(ChannelHandler bootstrapAcceptor) throws ReflectiveOperationException {
      ClassLoader classLoader = bootstrapAcceptor.getClass().getClassLoader();
      if (classLoader.getClass().getName().equals("org.bukkit.plugin.java.PluginClassLoader")) {
         PluginDescriptionFile description = (PluginDescriptionFile)ReflectionUtil.get(classLoader, "description", PluginDescriptionFile.class);
         throw new RuntimeException("Unable to inject, due to " + bootstrapAcceptor.getClass().getName() + ", try without the plugin " + description.getName() + "?");
      } else {
         throw new RuntimeException("Unable to find core component 'childHandler', please check your plugins. issue: " + bootstrapAcceptor.getClass().getName());
      }
   }

   public boolean lateProtocolVersionSetting() {
      return !PaperViaInjector.PAPER_PROTOCOL_METHOD && !HAS_WORLD_VERSION_PROTOCOL_VERSION;
   }

   public boolean isBinded() {
      if (PaperViaInjector.PAPER_INJECTION_METHOD) {
         return true;
      } else {
         try {
            Object connection = this.getServerConnection();
            if (connection == null) {
               return false;
            }

            Field[] var2 = connection.getClass().getDeclaredFields();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Field field = var2[var4];
               if (List.class.isAssignableFrom(field.getType())) {
                  field.setAccessible(true);
                  List<?> value = (List)field.get(connection);
                  synchronized(value) {
                     if (!value.isEmpty() && value.get(0) instanceof ChannelFuture) {
                        return true;
                     }
                  }
               }
            }
         } catch (ReflectiveOperationException var10) {
            var10.printStackTrace();
         }

         return false;
      }
   }
}
