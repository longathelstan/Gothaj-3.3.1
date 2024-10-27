package com.viaversion.viaversion.sponge.platform;

import com.viaversion.viaversion.ViaAPIBase;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import org.spongepowered.api.entity.living.player.Player;

public class SpongeViaAPI extends ViaAPIBase<Player> {
   public int getPlayerVersion(Player player) {
      return this.getPlayerVersion((UUID)player.uniqueId());
   }

   public void sendRawPacket(Player player, ByteBuf packet) throws IllegalArgumentException {
      this.sendRawPacket((UUID)player.uniqueId(), packet);
   }
}
