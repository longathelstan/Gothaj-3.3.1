package i.dupx.launcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

public class CLAPI {
   private static final long USER_CACHE_EXPIRE_TIME = 60000L;
   private static final Map<String, CLAPI.CLUserInfo> userInfoCache = new HashMap();
   private static final CLAPI.CLUserInfo loadingUserInfo = new CLAPI.CLUserInfo((CLAPI.CLUserInfo)null);
   private static String lastServer = null;
   private static boolean privacyMode = System.getProperty("clprivacy", "false").equals("true");
   private static CLAPI.IClient client;
   private static List<byte[]> packetQueue = new LinkedList();
   private static Set<String> friends = new HashSet();
   private static BiConsumer<Integer, DataInputStream> internalDataProcessor;

   static {
      (new Thread(CLAPI::cleanupCache)).start();
      (new Thread(CLAPI::clapi_reader)).start();
   }

   public static void setClassAcceptor(BiConsumer<Integer, DataInputStream> internalDataProcessor) {
      CLAPI.internalDataProcessor = internalDataProcessor;
   }

   public static void setClient(CLAPI.IClient client) {
      CLAPI.client = client;
   }

   public static Map<String, CLAPI.CLUserInfo> getUserInfoCache() {
      return Collections.unmodifiableMap(userInfoCache);
   }

   public static boolean getPrivacyMode() {
      return privacyMode;
   }

   public static void setPrivacyMode(boolean privacyMode) {
      CLAPI.privacyMode = privacyMode;

      try {
         Throwable var1 = null;
         Object var2 = null;

         try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            try {
               DataOutputStream dos = new DataOutputStream(bos);

               try {
                  dos.writeByte(5);
                  dos.writeBoolean(privacyMode);
                  sendPacket(bos.toByteArray());
               } finally {
                  if (dos != null) {
                     dos.close();
                  }

               }
            } catch (Throwable var18) {
               if (var1 == null) {
                  var1 = var18;
               } else if (var1 != var18) {
                  var1.addSuppressed(var18);
               }

               if (bos != null) {
                  bos.close();
               }

               throw var1;
            }

            if (bos != null) {
               bos.close();
            }
         } catch (Throwable var19) {
            if (var1 == null) {
               var1 = var19;
            } else if (var1 != var19) {
               var1.addSuppressed(var19);
            }

            throw var1;
         }
      } catch (Throwable var20) {
         System.err.println("Failed to send setPrivacyMode(" + privacyMode + ")");
         var20.printStackTrace();
      }

   }

   public static boolean isFriend(String username) {
      synchronized(friends) {
         return friends.contains(username);
      }
   }

   public static String getCLUsername() {
      return System.getProperty("clname", "");
   }

   public static String getCLToken() {
      return System.getProperty("cltoken", "");
   }

   public static int getClientRole() {
      return Integer.parseInt(System.getProperty("clrole", ""));
   }

   public static long getFirstBuyStamp() {
      return Long.parseLong(System.getProperty("clfirstBuy", "0"));
   }

   public static String getBranch() {
      return System.getProperty("clbranch", "");
   }

   public static void reportUsername(String username, String server) {
      if (!Objects.equals(server, lastServer)) {
         synchronized(userInfoCache) {
            userInfoCache.clear();
         }

         lastServer = server;
      }

      try {
         Throwable var2 = null;
         Object var3 = null;

         try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            try {
               DataOutputStream dos = new DataOutputStream(bos);

               try {
                  dos.writeByte(2);
                  dos.writeBoolean(username != null);
                  if (username != null) {
                     writeString(dos, username);
                  }

                  dos.writeBoolean(server != null);
                  if (server != null) {
                     writeString(dos, server);
                  }

                  sendPacket(bos.toByteArray());
               } finally {
                  if (dos != null) {
                     dos.close();
                  }

               }
            } catch (Throwable var23) {
               if (var2 == null) {
                  var2 = var23;
               } else if (var2 != var23) {
                  var2.addSuppressed(var23);
               }

               if (bos != null) {
                  bos.close();
               }

               throw var2;
            }

            if (bos != null) {
               bos.close();
            }
         } catch (Throwable var24) {
            if (var2 == null) {
               var2 = var24;
            } else if (var2 != var24) {
               var2.addSuppressed(var24);
            }

            throw var2;
         }
      } catch (Throwable var25) {
         System.err.println("Failed to send reportUsername('" + username + "', '" + server + "')");
         var25.printStackTrace();
      }

   }

   public static String getCapeUrl(String username) {
      return "https://api.haze.yt:8443/getCape?token=" + getCLToken() + "&username=" + URLEncoder.encode(username);
   }

   public static CLAPI.CLUserInfo getUserInfo(String username) {
      synchronized(userInfoCache) {
         CLAPI.CLUserInfo userData = (CLAPI.CLUserInfo)userInfoCache.get(username);
         if (userData == null) {
            userInfoCache.put(username, userData = loadingUserInfo);

            try {
               Throwable var3 = null;
               Object var4 = null;

               try {
                  ByteArrayOutputStream bos = new ByteArrayOutputStream();

                  try {
                     DataOutputStream dos = new DataOutputStream(bos);

                     try {
                        dos.writeByte(3);
                        writeString(dos, username);
                        sendPacket(bos.toByteArray());
                     } finally {
                        if (dos != null) {
                           dos.close();
                        }

                     }
                  } catch (Throwable var23) {
                     if (var3 == null) {
                        var3 = var23;
                     } else if (var3 != var23) {
                        var3.addSuppressed(var23);
                     }

                     if (bos != null) {
                        bos.close();
                     }

                     throw var3;
                  }

                  if (bos != null) {
                     bos.close();
                  }
               } catch (Throwable var24) {
                  if (var3 == null) {
                     var3 = var24;
                  } else if (var3 != var24) {
                     var3.addSuppressed(var24);
                  }

                  throw var3;
               }
            } catch (Throwable var25) {
               System.err.println("Failed to send setPrivacyMode(" + privacyMode + ")");
            }
         }

         return userData;
      }
   }

   private static void cleanupCache() {
      Thread.currentThread().setName("CLAPI Cleanup Cache");

      while(true) {
         while(true) {
            try {
               long time = System.currentTimeMillis();
               synchronized(userInfoCache) {
                  userInfoCache.values().removeIf((userInfo) -> {
                     return time > userInfo.expire;
                  });
               }
            } catch (Throwable var13) {
               var13.printStackTrace();
            } finally {
               try {
                  Thread.sleep(30000L);
               } catch (InterruptedException var11) {
               }

            }
         }
      }
   }

   private static void clapi_reader() {
      Thread.currentThread().setName("CLAPI CL Connection");

      try {
         Throwable var0 = null;
         Object var1 = null;

         try {
            Socket socket = new Socket("127.0.0.1", Integer.parseInt(System.getProperty("clport", "0")));

            try {
               DataInputStream dataInput = new DataInputStream(socket.getInputStream());
               DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
               (new Thread(() -> {
                  clapi_writer(dataOutput);
               })).start();

               while(socket.isConnected() && !socket.isClosed()) {
                  byte[] data = readByteArray(dataInput);

                  try {
                     Throwable var6 = null;
                     Object var7 = null;

                     try {
                        DataInputStream packetStream = new DataInputStream(new ByteArrayInputStream(data));

                        try {
                           readPacket(packetStream.readUnsignedByte(), packetStream);
                        } finally {
                           if (packetStream != null) {
                              packetStream.close();
                           }

                        }
                     } catch (Throwable var38) {
                        if (var6 == null) {
                           var6 = var38;
                        } else if (var6 != var38) {
                           var6.addSuppressed(var38);
                        }

                        throw var6;
                     }
                  } catch (SocketException var39) {
                     break;
                  } catch (Throwable var40) {
                     System.err.println("Failed to read packet data");
                     var40.printStackTrace();
                  }
               }
            } finally {
               if (socket != null) {
                  socket.close();
               }

            }
         } catch (Throwable var42) {
            if (var0 == null) {
               var0 = var42;
            } else if (var0 != var42) {
               var0.addSuppressed(var42);
            }

            throw var0;
         }
      } catch (Throwable var43) {
      }

   }

   private static void clapi_writer(DataOutputStream output) {
      Thread.currentThread().setName("CLAPI CL Connection");

      while(true) {
         while(true) {
            try {
               byte[] packet;
               synchronized(packetQueue) {
                  packet = packetQueue.isEmpty() ? null : (byte[])packetQueue.remove(0);
               }

               if (packet == null) {
                  Thread.sleep(1L);
               } else {
                  output.writeInt(packet.length);
                  output.write(packet);
                  output.flush();
               }
            } catch (Throwable var4) {
               System.err.println("Failed to write packet data");
               var4.printStackTrace();
            }
         }
      }
   }

   public static void sendPacket(byte[] data) {
      synchronized(packetQueue) {
         packetQueue.add(data);
      }
   }

   private static void sendError(String error) throws IOException {
      Throwable var1 = null;
      Object var2 = null;

      try {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();

         try {
            DataOutputStream dos = new DataOutputStream(bos);

            try {
               dos.writeByte(7);
               writeString(dos, error);
               sendPacket(bos.toByteArray());
            } finally {
               if (dos != null) {
                  dos.close();
               }

            }
         } catch (Throwable var15) {
            if (var1 == null) {
               var1 = var15;
            } else if (var1 != var15) {
               var1.addSuppressed(var15);
            }

            if (bos != null) {
               bos.close();
            }

            throw var1;
         }

         if (bos != null) {
            bos.close();
         }

      } catch (Throwable var16) {
         if (var1 == null) {
            var1 = var16;
         } else if (var1 != var16) {
            var1.addSuppressed(var16);
         }

         throw var1;
      }
   }

   protected static void readPacket(int packetType, DataInputStream input) throws IOException {
      String added;
      switch(packetType) {
      case 0:
         if (client != null) {
            client.loadCurrentConfig(readString(input));
         } else {
            sendError("Client = null");
         }
         break;
      case 1:
         if (client != null) {
            Throwable var2 = null;
            added = null;

            try {
               ByteArrayOutputStream bos = new ByteArrayOutputStream();

               try {
                  DataOutputStream dos = new DataOutputStream(bos);

                  try {
                     dos.writeByte(1);
                     dos.writeInt(input.readInt());
                     writeString(dos, client.writeCurrentConfig());
                     sendPacket(bos.toByteArray());
                  } finally {
                     if (dos != null) {
                        dos.close();
                     }

                  }
               } catch (Throwable var24) {
                  if (var2 == null) {
                     var2 = var24;
                  } else if (var2 != var24) {
                     var2.addSuppressed(var24);
                  }

                  if (bos != null) {
                     bos.close();
                  }

                  throw var2;
               }

               if (bos != null) {
                  bos.close();
               }
            } catch (Throwable var25) {
               if (var2 == null) {
                  var2 = var25;
               } else if (var2 != var25) {
                  var2.addSuppressed(var25);
               }

               throw var2;
            }
         } else {
            sendError("Client = null");
         }
         break;
      case 2:
      default:
         internalDataProcessor.accept(packetType, input);
         break;
      case 3:
         synchronized(userInfoCache) {
            userInfoCache.put(readString(input), new CLAPI.CLUserInfo(input, (CLAPI.CLUserInfo)null));
            break;
         }
      case 4:
         synchronized(friends) {
            friends.clear();
            String[] var6;
            int var5 = (var6 = readStringArray(input)).length;

            for(int var4 = 0; var4 < var5; ++var4) {
               added = var6[var4];
               friends.add(added);
            }

            return;
         }
      case 5:
         privacyMode = input.readBoolean();
         break;
      case 6:
         if (client != null) {
            client.joinServer(readString(input));
         } else {
            sendError("Client = null");
         }
      }

   }

   protected static String[] readStringArray(DataInputStream input) throws IOException {
      String[] strings = new String[input.readUnsignedShort()];

      for(int i = 0; i < strings.length; ++i) {
         strings[i] = readString(input);
      }

      return strings;
   }

   public static byte[] readByteArray(DataInputStream input) throws IOException {
      byte[] bytes = new byte[input.readInt()];
      input.readFully(bytes);
      return bytes;
   }

   protected static void writeString(DataOutputStream output, String str) throws IOException {
      writeByteArray(output, str.getBytes("UTF-8"));
   }

   protected static void writeByteArray(DataOutputStream output, byte[] bytes) throws IOException {
      output.writeInt(bytes.length);
      output.write(bytes);
   }

   protected static String readString(DataInputStream input) throws IOException {
      return new String(readByteArray(input), "UTF-8");
   }

   public static class CLUserInfo {
      public final String nickname;
      public final int role;
      public final String runningClient;
      public final String runningBranch;
      public final String mc_name;
      private long expire;

      private CLUserInfo(DataInputStream dis) throws IOException {
         if (dis.readBoolean()) {
            this.nickname = CLAPI.readString(dis);
            this.role = dis.readUnsignedByte();
            this.runningClient = CLAPI.readString(dis);
            this.runningBranch = CLAPI.readString(dis);
            this.mc_name = CLAPI.readString(dis);
            this.expire = System.currentTimeMillis() + 60000L;
         } else {
            this.nickname = "None";
            this.role = Integer.MAX_VALUE;
            this.runningClient = "-";
            this.runningBranch = "-";
            this.mc_name = "-";
         }

      }

      private CLUserInfo() {
         this.nickname = "Loading...";
         this.role = Integer.MAX_VALUE;
         this.runningClient = "None";
         this.runningBranch = "None";
         this.expire = Long.MAX_VALUE;
         this.mc_name = "Unknown";
      }

      public String toString() {
         return "nickname=" + this.nickname + "," + "role=" + this.role + "," + "runningClient=" + this.runningClient + "," + "runningBranch=" + this.runningBranch;
      }

      // $FF: synthetic method
      CLUserInfo(CLAPI.CLUserInfo var1) {
         this();
      }

      // $FF: synthetic method
      CLUserInfo(DataInputStream var1, CLAPI.CLUserInfo var2) throws IOException {
         this(var1);
      }
   }

   public interface IClient {
      String writeCurrentConfig();

      void loadCurrentConfig(String var1);

      void joinServer(String var1);
   }
}
