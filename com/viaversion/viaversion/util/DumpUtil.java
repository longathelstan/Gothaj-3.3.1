package com.viaversion.viaversion.util;

import com.google.common.io.CharStreams;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.dump.DumpTemplate;
import com.viaversion.viaversion.libs.gson.GsonBuilder;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidObjectException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class DumpUtil {
   public static CompletableFuture<String> postDump(@Nullable UUID playerToSample) {
      com.viaversion.viaversion.dump.VersionInfo version = new com.viaversion.viaversion.dump.VersionInfo(System.getProperty("java.version"), System.getProperty("os.name"), Via.getAPI().getServerVersion().lowestSupportedVersion(), Via.getManager().getProtocolManager().getSupportedVersions(), Via.getPlatform().getPlatformName(), Via.getPlatform().getPlatformVersion(), Via.getPlatform().getPluginVersion(), VersionInfo.getImplementationVersion(), Via.getManager().getSubPlatforms());
      Map<String, Object> configuration = ((Config)Via.getConfig()).getValues();
      DumpTemplate template = new DumpTemplate(version, configuration, Via.getPlatform().getDump(), Via.getManager().getInjector().getDump(), getPlayerSample(playerToSample));
      CompletableFuture<String> result = new CompletableFuture();
      Via.getPlatform().runAsync(() -> {
         HttpURLConnection con;
         try {
            con = (HttpURLConnection)(new URL("https://dump.viaversion.com/documents")).openConnection();
         } catch (IOException var34) {
            Via.getPlatform().getLogger().log(Level.SEVERE, "Error when opening connection to ViaVersion dump service", var34);
            result.completeExceptionally(new DumpUtil.DumpException(DumpUtil.DumpErrorType.CONNECTION, var34));
            return;
         }

         try {
            con.setRequestProperty("Content-Type", "application/json");
            con.addRequestProperty("User-Agent", "ViaVersion-" + Via.getPlatform().getPlatformName() + "/" + version.getPluginVersion());
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            OutputStream out = con.getOutputStream();
            Throwable var5 = null;

            try {
               out.write((new GsonBuilder()).setPrettyPrinting().create().toJson((Object)template).getBytes(StandardCharsets.UTF_8));
            } catch (Throwable var33) {
               var5 = var33;
               throw var33;
            } finally {
               if (out != null) {
                  if (var5 != null) {
                     try {
                        out.close();
                     } catch (Throwable var31) {
                        var5.addSuppressed(var31);
                     }
                  } else {
                     out.close();
                  }
               }

            }

            if (con.getResponseCode() == 429) {
               result.completeExceptionally(new DumpUtil.DumpException(DumpUtil.DumpErrorType.RATE_LIMITED));
               return;
            }

            InputStream inputStream = con.getInputStream();
            Throwable var6 = null;

            String rawOutput;
            try {
               rawOutput = CharStreams.toString(new InputStreamReader(inputStream));
            } catch (Throwable var32) {
               var6 = var32;
               throw var32;
            } finally {
               if (inputStream != null) {
                  if (var6 != null) {
                     try {
                        inputStream.close();
                     } catch (Throwable var30) {
                        var6.addSuppressed(var30);
                     }
                  } else {
                     inputStream.close();
                  }
               }

            }

            JsonObject output = (JsonObject)GsonUtil.getGson().fromJson(rawOutput, JsonObject.class);
            if (!output.has("key")) {
               throw new InvalidObjectException("Key is not given in Hastebin output");
            }

            result.complete(urlForId(output.get("key").getAsString()));
         } catch (Exception var37) {
            Via.getPlatform().getLogger().log(Level.SEVERE, "Error when posting ViaVersion dump", var37);
            result.completeExceptionally(new DumpUtil.DumpException(DumpUtil.DumpErrorType.POST, var37));
            printFailureInfo(con);
         }

      });
      return result;
   }

   private static void printFailureInfo(HttpURLConnection connection) {
      try {
         if (connection.getResponseCode() < 200 || connection.getResponseCode() > 400) {
            InputStream errorStream = connection.getErrorStream();
            Throwable var2 = null;

            try {
               String rawOutput = CharStreams.toString(new InputStreamReader(errorStream));
               Via.getPlatform().getLogger().log(Level.SEVERE, "Page returned: " + rawOutput);
            } catch (Throwable var12) {
               var2 = var12;
               throw var12;
            } finally {
               if (errorStream != null) {
                  if (var2 != null) {
                     try {
                        errorStream.close();
                     } catch (Throwable var11) {
                        var2.addSuppressed(var11);
                     }
                  } else {
                     errorStream.close();
                  }
               }

            }
         }
      } catch (IOException var14) {
         Via.getPlatform().getLogger().log(Level.SEVERE, "Failed to capture further info", var14);
      }

   }

   public static String urlForId(String id) {
      return String.format("https://dump.viaversion.com/%s", id);
   }

   private static JsonObject getPlayerSample(@Nullable UUID uuid) {
      JsonObject playerSample = new JsonObject();
      JsonObject versions = new JsonObject();
      playerSample.add("versions", versions);
      Map<ProtocolVersion, Integer> playerVersions = new TreeMap((o1, o2) -> {
         return ProtocolVersion.getIndex(o2) - ProtocolVersion.getIndex(o1);
      });
      Iterator var4 = Via.getManager().getConnectionManager().getConnections().iterator();

      UserConnection senderConnection;
      while(var4.hasNext()) {
         senderConnection = (UserConnection)var4.next();
         ProtocolVersion protocolVersion = ProtocolVersion.getProtocol(senderConnection.getProtocolInfo().getProtocolVersion());
         playerVersions.compute(protocolVersion, (v, num) -> {
            return num != null ? num + 1 : 1;
         });
      }

      var4 = playerVersions.entrySet().iterator();

      while(var4.hasNext()) {
         Entry<ProtocolVersion, Integer> entry = (Entry)var4.next();
         versions.addProperty(((ProtocolVersion)entry.getKey()).getName(), (Number)entry.getValue());
      }

      Set<List<String>> pipelines = new HashSet();
      if (uuid != null) {
         senderConnection = Via.getAPI().getConnection(uuid);
         if (senderConnection != null && senderConnection.getChannel() != null) {
            pipelines.add(senderConnection.getChannel().pipeline().names());
         }
      }

      Iterator var13 = Via.getManager().getConnectionManager().getConnections().iterator();

      List names;
      while(var13.hasNext()) {
         UserConnection connection = (UserConnection)var13.next();
         if (connection.getChannel() != null) {
            names = connection.getChannel().pipeline().names();
            if (pipelines.add(names) && pipelines.size() == 3) {
               break;
            }
         }
      }

      int i = 0;
      Iterator var16 = pipelines.iterator();

      while(var16.hasNext()) {
         names = (List)var16.next();
         JsonArray senderPipeline = new JsonArray(names.size());
         Iterator var9 = names.iterator();

         while(var9.hasNext()) {
            String name = (String)var9.next();
            senderPipeline.add(name);
         }

         playerSample.add("pipeline-" + i++, senderPipeline);
      }

      return playerSample;
   }

   public static enum DumpErrorType {
      CONNECTION("Failed to dump, please check the console for more information"),
      RATE_LIMITED("Please wait before creating another dump"),
      POST("Failed to dump, please check the console for more information");

      private final String message;

      private DumpErrorType(String message) {
         this.message = message;
      }

      public String message() {
         return this.message;
      }
   }

   public static final class DumpException extends RuntimeException {
      private final DumpUtil.DumpErrorType errorType;

      private DumpException(DumpUtil.DumpErrorType errorType, Throwable cause) {
         super(errorType.message(), cause);
         this.errorType = errorType;
      }

      private DumpException(DumpUtil.DumpErrorType errorType) {
         super(errorType.message());
         this.errorType = errorType;
      }

      public DumpUtil.DumpErrorType errorType() {
         return this.errorType;
      }

      // $FF: synthetic method
      DumpException(DumpUtil.DumpErrorType x0, Throwable x1, Object x2) {
         this(x0, x1);
      }

      // $FF: synthetic method
      DumpException(DumpUtil.DumpErrorType x0, Object x1) {
         this(x0);
      }
   }
}
