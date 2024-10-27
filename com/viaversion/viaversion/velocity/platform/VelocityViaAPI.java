package com.viaversion.viaversion.velocity.platform;

import com.velocitypowered.api.proxy.Player;
import com.viaversion.viaversion.ViaAPIBase;
import io.netty.buffer.ByteBuf;
import java.util.UUID;

public class VelocityViaAPI extends ViaAPIBase<Player> {
   public int getPlayerVersion(Player player) {
      return this.getPlayerVersion((UUID)player.getUniqueId());
   }

   public void sendRawPacket(Player player, ByteBuf packet) throws IllegalArgumentException {
      this.sendRawPacket((UUID)player.getUniqueId(), packet);
   }
}
