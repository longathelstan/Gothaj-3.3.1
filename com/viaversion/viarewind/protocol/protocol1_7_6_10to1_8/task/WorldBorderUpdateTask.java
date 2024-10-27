package com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.task;

import com.viaversion.viarewind.ViaRewind;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.Protocol1_7_6_10To1_8;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.storage.PlayerSessionStorage;
import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.storage.WorldBorderEmulator;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import java.util.Iterator;
import java.util.logging.Level;

public class WorldBorderUpdateTask implements Runnable {
   public static final int VIEW_DISTANCE = 16;

   public void run() {
      Iterator var1 = Via.getManager().getConnectionManager().getConnections().iterator();

      while(true) {
         UserConnection connection;
         WorldBorderEmulator worldBorderEmulatorTracker;
         do {
            if (!var1.hasNext()) {
               return;
            }

            connection = (UserConnection)var1.next();
            worldBorderEmulatorTracker = (WorldBorderEmulator)connection.get(WorldBorderEmulator.class);
         } while(!worldBorderEmulatorTracker.isInit());

         PlayerSessionStorage playerSession = (PlayerSessionStorage)connection.get(PlayerSessionStorage.class);
         double radius = worldBorderEmulatorTracker.getSize() / 2.0D;
         WorldBorderEmulator.Side[] var7 = WorldBorderEmulator.Side.values();
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            WorldBorderEmulator.Side side = var7[var9];
            double d;
            double pos;
            double center;
            if (side.modX != 0) {
               pos = playerSession.getPosZ();
               center = worldBorderEmulatorTracker.getZ();
               d = Math.abs(worldBorderEmulatorTracker.getX() + radius * (double)side.modX - playerSession.getPosX());
            } else {
               center = worldBorderEmulatorTracker.getX();
               pos = playerSession.getPosX();
               d = Math.abs(worldBorderEmulatorTracker.getZ() + radius * (double)side.modZ - playerSession.getPosZ());
            }

            if (!(d >= 16.0D)) {
               double r = Math.sqrt(256.0D - d * d);
               double minH = Math.ceil(pos - r);
               double maxH = Math.floor(pos + r);
               double minV = Math.ceil(playerSession.getPosY() - r);
               double maxV = Math.floor(playerSession.getPosY() + r);
               if (minH < center - radius) {
                  minH = Math.ceil(center - radius);
               }

               if (maxH > center + radius) {
                  maxH = Math.floor(center + radius);
               }

               if (minV < 0.0D) {
                  minV = 0.0D;
               }

               double centerH = (minH + maxH) / 2.0D;
               double centerV = (minV + maxV) / 2.0D;
               double particleOffset = 2.5D;
               PacketWrapper spawnParticle = PacketWrapper.create(ClientboundPackets1_8.SPAWN_PARTICLE, (UserConnection)connection);
               spawnParticle.write(Type.STRING, ViaRewind.getConfig().getWorldBorderParticle());
               spawnParticle.write(Type.FLOAT, (float)(side.modX != 0 ? worldBorderEmulatorTracker.getX() + radius * (double)side.modX : centerH));
               spawnParticle.write(Type.FLOAT, (float)centerV);
               spawnParticle.write(Type.FLOAT, (float)(side.modX == 0 ? worldBorderEmulatorTracker.getZ() + radius * (double)side.modZ : centerH));
               spawnParticle.write(Type.FLOAT, (float)(side.modX != 0 ? 0.0D : (maxH - minH) / particleOffset));
               spawnParticle.write(Type.FLOAT, (float)((maxV - minV) / particleOffset));
               spawnParticle.write(Type.FLOAT, (float)(side.modX == 0 ? 0.0D : (maxH - minH) / particleOffset));
               spawnParticle.write(Type.FLOAT, 0.0F);
               spawnParticle.write(Type.INT, (int)Math.floor((maxH - minH) * (maxV - minV) * 0.5D));

               try {
                  spawnParticle.send(Protocol1_7_6_10To1_8.class, true);
               } catch (Exception var35) {
                  ViaRewind.getPlatform().getLogger().log(Level.SEVERE, "Failed to send world border particle", var35);
               }
            }
         }
      }
   }
}
