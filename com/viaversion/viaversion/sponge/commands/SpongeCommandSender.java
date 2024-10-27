package com.viaversion.viaversion.sponge.commands;

import com.viaversion.viaversion.SpongePlugin;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import java.util.UUID;
import net.kyori.adventure.identity.Identity;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.util.Identifiable;

public class SpongeCommandSender implements ViaCommandSender {
   private final CommandCause source;

   public SpongeCommandSender(CommandCause source) {
      this.source = source;
   }

   public boolean hasPermission(String permission) {
      return this.source.hasPermission(permission);
   }

   public void sendMessage(String msg) {
      this.source.sendMessage(Identity.nil(), SpongePlugin.LEGACY_SERIALIZER.deserialize(msg));
   }

   public UUID getUUID() {
      return this.source instanceof Identifiable ? ((Identifiable)this.source).uniqueId() : new UUID(0L, 0L);
   }

   public String getName() {
      return (String)this.source.friendlyIdentifier().orElse(this.source.identifier());
   }
}
