package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TeleportToTeam implements ISpectatorMenuView, ISpectatorMenuObject {
   private final List<ISpectatorMenuObject> field_178672_a = Lists.newArrayList();

   public TeleportToTeam() {
      Minecraft minecraft = Minecraft.getMinecraft();
      Iterator var3 = minecraft.theWorld.getScoreboard().getTeams().iterator();

      while(var3.hasNext()) {
         ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam)var3.next();
         this.field_178672_a.add(new TeleportToTeam.TeamSelectionObject(scoreplayerteam));
      }

   }

   public List<ISpectatorMenuObject> func_178669_a() {
      return this.field_178672_a;
   }

   public IChatComponent func_178670_b() {
      return new ChatComponentText("Select a team to teleport to");
   }

   public void func_178661_a(SpectatorMenu menu) {
      menu.func_178647_a(this);
   }

   public IChatComponent getSpectatorName() {
      return new ChatComponentText("Teleport to team member");
   }

   public void func_178663_a(float p_178663_1_, int alpha) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.field_175269_a);
      Gui.drawModalRectWithCustomSizedTexture(0.0F, 0.0F, 16.0F, 0.0F, 16.0D, 16.0D, 256.0D, 256.0D);
   }

   public boolean func_178662_A_() {
      Iterator var2 = this.field_178672_a.iterator();

      while(var2.hasNext()) {
         ISpectatorMenuObject ispectatormenuobject = (ISpectatorMenuObject)var2.next();
         if (ispectatormenuobject.func_178662_A_()) {
            return true;
         }
      }

      return false;
   }

   class TeamSelectionObject implements ISpectatorMenuObject {
      private final ScorePlayerTeam field_178676_b;
      private final ResourceLocation field_178677_c;
      private final List<NetworkPlayerInfo> field_178675_d;

      public TeamSelectionObject(ScorePlayerTeam p_i45492_2_) {
         this.field_178676_b = p_i45492_2_;
         this.field_178675_d = Lists.newArrayList();
         Iterator var4 = p_i45492_2_.getMembershipCollection().iterator();

         String s1;
         while(var4.hasNext()) {
            s1 = (String)var4.next();
            NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(s1);
            if (networkplayerinfo != null) {
               this.field_178675_d.add(networkplayerinfo);
            }
         }

         if (!this.field_178675_d.isEmpty()) {
            s1 = ((NetworkPlayerInfo)this.field_178675_d.get((new Random()).nextInt(this.field_178675_d.size()))).getGameProfile().getName();
            this.field_178677_c = AbstractClientPlayer.getLocationSkin(s1);
            AbstractClientPlayer.getDownloadImageSkin(this.field_178677_c, s1);
         } else {
            this.field_178677_c = DefaultPlayerSkin.getDefaultSkinLegacy();
         }

      }

      public void func_178661_a(SpectatorMenu menu) {
         menu.func_178647_a(new TeleportToPlayer(this.field_178675_d));
      }

      public IChatComponent getSpectatorName() {
         return new ChatComponentText(this.field_178676_b.getTeamName());
      }

      public void func_178663_a(float p_178663_1_, int alpha) {
         int i = -1;
         String s = FontRenderer.getFormatFromString(this.field_178676_b.getColorPrefix());
         if (s.length() >= 2) {
            i = Minecraft.getMinecraft().fontRendererObj.getColorCode(s.charAt(1));
         }

         if (i >= 0) {
            float f = (float)(i >> 16 & 255) / 255.0F;
            float f1 = (float)(i >> 8 & 255) / 255.0F;
            float f2 = (float)(i & 255) / 255.0F;
            Gui.drawRect(1.0D, 1.0D, 15.0D, 15.0D, MathHelper.func_180183_b(f * p_178663_1_, f1 * p_178663_1_, f2 * p_178663_1_) | alpha << 24);
         }

         Minecraft.getMinecraft().getTextureManager().bindTexture(this.field_178677_c);
         GlStateManager.color(p_178663_1_, p_178663_1_, p_178663_1_, (float)alpha / 255.0F);
         Gui.drawScaledCustomSizeModalRect(2.0D, 2.0D, 8.0F, 8.0F, 8.0D, 8.0D, 12.0D, 12.0D, 64.0F, 64.0F);
         Gui.drawScaledCustomSizeModalRect(2.0D, 2.0D, 40.0F, 8.0F, 8.0D, 8.0D, 12.0D, 12.0D, 64.0F, 64.0F);
      }

      public boolean func_178662_A_() {
         return !this.field_178675_d.isEmpty();
      }
   }
}
