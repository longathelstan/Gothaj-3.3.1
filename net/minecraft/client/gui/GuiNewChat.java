package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.cucumber.base.Client;
import xyz.cucumber.base.interf.clientsettings.ext.adds.BlurSetting;
import xyz.cucumber.base.module.feat.player.NameProtectModule;

public class GuiNewChat extends Gui {
   private static final Logger logger = LogManager.getLogger();
   private final Minecraft mc;
   private final List<String> sentMessages = Lists.newArrayList();
   private final List<ChatLine> chatLines = Lists.newArrayList();
   private final List<ChatLine> drawnChatLines = Lists.newArrayList();
   private int scrollPos;
   private boolean isScrolled;

   public GuiNewChat(Minecraft mcIn) {
      this.mc = mcIn;
   }

   public void drawChat(int updateCounter) {
      if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
         int i = this.getLineCount();
         boolean flag = false;
         int j = 0;
         int k = this.drawnChatLines.size();
         float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
         if (k > 0) {
            if (this.getChatOpen()) {
               flag = true;
            }

            float f1 = this.getChatScale();
            int l = MathHelper.ceiling_float_int((float)this.getChatWidth() / f1);
            GlStateManager.pushMatrix();
            GlStateManager.translate(2.0F, 20.0F, 0.0F);
            GlStateManager.scale(f1, f1, 1.0F);

            int i1;
            int j1;
            int l1;
            for(i1 = 0; i1 + this.scrollPos < this.drawnChatLines.size() && i1 < i; ++i1) {
               ChatLine chatline = (ChatLine)this.drawnChatLines.get(i1 + this.scrollPos);
               if (chatline != null) {
                  j1 = updateCounter - chatline.getUpdatedCounter();
                  if (j1 < 200 || flag) {
                     double d0 = (double)j1 / 200.0D;
                     d0 = 1.0D - d0;
                     d0 *= 10.0D;
                     d0 = MathHelper.clamp_double(d0, 0.0D, 1.0D);
                     d0 *= d0;
                     l1 = (int)(255.0D * d0);
                     if (flag) {
                        l1 = 255;
                     }

                     l1 = (int)((float)l1 * f);
                     ++j;
                     if (l1 > 3) {
                        BlurSetting setting = (BlurSetting)Client.INSTANCE.getClientSettings().getSettingByName("Blur");
                        int i2 = 0;
                        int j2 = -i1 * 9;
                        drawRect((double)i2, (double)(j2 - 9), (double)(i2 + l + 4), (double)j2, l1 / 2 << 24);
                        String s = chatline.getChatComponent().getFormattedText();
                        GlStateManager.enableBlend();
                        NameProtectModule mod = (NameProtectModule)Client.INSTANCE.getModuleManager().getModule(NameProtectModule.class);
                        if (mod.isEnabled()) {
                           s = s.replace(this.mc.thePlayer.getName(), mod.getFormatedName());
                        }

                        this.mc.fontRendererObj.drawStringWithShadow(s, (double)((float)i2), (double)((float)(j2 - 8)), 16777215 + (l1 << 24));
                        GlStateManager.disableAlpha();
                        GlStateManager.disableBlend();
                     }
                  }
               }
            }

            if (flag) {
               i1 = this.mc.fontRendererObj.FONT_HEIGHT;
               GlStateManager.translate(-3.0F, 0.0F, 0.0F);
               int l2 = k * i1 + k;
               j1 = j * i1 + j;
               int j3 = this.scrollPos * j1 / k;
               int k1 = j1 * j1 / l2;
               if (l2 != j1) {
                  l1 = j3 > 0 ? 170 : 96;
                  int l3 = this.isScrolled ? 13382451 : 3355562;
                  drawRect(0.0D, (double)(-j3), 2.0D, (double)(-j3 - k1), l3 + (l1 << 24));
                  drawRect(2.0D, (double)(-j3), 1.0D, (double)(-j3 - k1), 13421772 + (l1 << 24));
               }
            }

            GlStateManager.popMatrix();
         }
      }

   }

   public void clearChatMessages() {
      this.drawnChatLines.clear();
      this.chatLines.clear();
      this.sentMessages.clear();
   }

   public void printChatMessage(IChatComponent chatComponent) {
      this.printChatMessageWithOptionalDeletion(chatComponent, 0);
   }

   public void printChatMessageWithOptionalDeletion(IChatComponent chatComponent, int chatLineId) {
      this.setChatLine(chatComponent, chatLineId, this.mc.ingameGUI.getUpdateCounter(), false);
      logger.info("[CHAT] " + chatComponent.getUnformattedText());
   }

   private void setChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
      if (chatLineId != 0) {
         this.deleteChatLine(chatLineId);
      }

      int i = MathHelper.floor_float((float)this.getChatWidth() / this.getChatScale());
      List<IChatComponent> list = GuiUtilRenderComponents.splitText(chatComponent, i, this.mc.fontRendererObj, false, false);
      boolean flag = this.getChatOpen();

      IChatComponent ichatcomponent;
      for(Iterator var9 = list.iterator(); var9.hasNext(); this.drawnChatLines.add(0, new ChatLine(updateCounter, ichatcomponent, chatLineId))) {
         ichatcomponent = (IChatComponent)var9.next();
         if (flag && this.scrollPos > 0) {
            this.isScrolled = true;
            this.scroll(1);
         }
      }

      while(this.drawnChatLines.size() > 100) {
         this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
      }

      if (!displayOnly) {
         this.chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));

         while(this.chatLines.size() > 100) {
            this.chatLines.remove(this.chatLines.size() - 1);
         }
      }

   }

   public void refreshChat() {
      this.drawnChatLines.clear();
      this.resetScroll();

      for(int i = this.chatLines.size() - 1; i >= 0; --i) {
         ChatLine chatline = (ChatLine)this.chatLines.get(i);
         this.setChatLine(chatline.getChatComponent(), chatline.getChatLineID(), chatline.getUpdatedCounter(), true);
      }

   }

   public List<String> getSentMessages() {
      return this.sentMessages;
   }

   public void addToSentMessages(String message) {
      if (this.sentMessages.isEmpty() || !((String)this.sentMessages.get(this.sentMessages.size() - 1)).equals(message)) {
         this.sentMessages.add(message);
      }

   }

   public void resetScroll() {
      this.scrollPos = 0;
      this.isScrolled = false;
   }

   public void scroll(int amount) {
      this.scrollPos += amount;
      int i = this.drawnChatLines.size();
      if (this.scrollPos > i - this.getLineCount()) {
         this.scrollPos = i - this.getLineCount();
      }

      if (this.scrollPos <= 0) {
         this.scrollPos = 0;
         this.isScrolled = false;
      }

   }

   public IChatComponent getChatComponent(int mouseX, int mouseY) {
      if (!this.getChatOpen()) {
         return null;
      } else {
         ScaledResolution scaledresolution = new ScaledResolution(this.mc);
         int i = scaledresolution.getScaleFactor();
         float f = this.getChatScale();
         int j = mouseX / i - 3;
         int k = mouseY / i - 27;
         j = MathHelper.floor_float((float)j / f);
         k = MathHelper.floor_float((float)k / f);
         if (j >= 0 && k >= 0) {
            int l = Math.min(this.getLineCount(), this.drawnChatLines.size());
            if (j <= MathHelper.floor_float((float)this.getChatWidth() / this.getChatScale()) && k < this.mc.fontRendererObj.FONT_HEIGHT * l + l) {
               int i1 = k / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;
               if (i1 >= 0 && i1 < this.drawnChatLines.size()) {
                  ChatLine chatline = (ChatLine)this.drawnChatLines.get(i1);
                  int j1 = 0;
                  Iterator var13 = chatline.getChatComponent().iterator();

                  while(var13.hasNext()) {
                     IChatComponent ichatcomponent = (IChatComponent)var13.next();
                     if (ichatcomponent instanceof ChatComponentText) {
                        j1 += this.mc.fontRendererObj.getStringWidth(GuiUtilRenderComponents.func_178909_a(((ChatComponentText)ichatcomponent).getChatComponentText_TextValue(), false));
                        if (j1 > j) {
                           return ichatcomponent;
                        }
                     }
                  }
               }

               return null;
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }

   public boolean getChatOpen() {
      return this.mc.currentScreen instanceof GuiChat;
   }

   public void deleteChatLine(int id) {
      Iterator iterator = this.drawnChatLines.iterator();

      ChatLine chatline1;
      while(iterator.hasNext()) {
         chatline1 = (ChatLine)iterator.next();
         if (chatline1.getChatLineID() == id) {
            iterator.remove();
         }
      }

      iterator = this.chatLines.iterator();

      while(iterator.hasNext()) {
         chatline1 = (ChatLine)iterator.next();
         if (chatline1.getChatLineID() == id) {
            iterator.remove();
            break;
         }
      }

   }

   public int getChatWidth() {
      return calculateChatboxWidth(this.mc.gameSettings.chatWidth);
   }

   public int getChatHeight() {
      return calculateChatboxHeight(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
   }

   public float getChatScale() {
      return this.mc.gameSettings.chatScale;
   }

   public static int calculateChatboxWidth(float scale) {
      int i = 320;
      int j = 40;
      return MathHelper.floor_float(scale * (float)(i - j) + (float)j);
   }

   public static int calculateChatboxHeight(float scale) {
      int i = 180;
      int j = 20;
      return MathHelper.floor_float(scale * (float)(i - j) + (float)j);
   }

   public int getLineCount() {
      return this.getChatHeight() / 9;
   }
}
