package net.minecraft.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public enum EnumChatFormatting {
   BLACK("BLACK", '0', 0),
   DARK_BLUE("DARK_BLUE", '1', 1),
   DARK_GREEN("DARK_GREEN", '2', 2),
   DARK_AQUA("DARK_AQUA", '3', 3),
   DARK_RED("DARK_RED", '4', 4),
   DARK_PURPLE("DARK_PURPLE", '5', 5),
   GOLD("GOLD", '6', 6),
   GRAY("GRAY", '7', 7),
   DARK_GRAY("DARK_GRAY", '8', 8),
   BLUE("BLUE", '9', 9),
   GREEN("GREEN", 'a', 10),
   AQUA("AQUA", 'b', 11),
   RED("RED", 'c', 12),
   LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13),
   YELLOW("YELLOW", 'e', 14),
   WHITE("WHITE", 'f', 15),
   OBFUSCATED("OBFUSCATED", 'k', true),
   BOLD("BOLD", 'l', true),
   STRIKETHROUGH("STRIKETHROUGH", 'm', true),
   UNDERLINE("UNDERLINE", 'n', true),
   ITALIC("ITALIC", 'o', true),
   RESET("RESET", 'r', -1);

   private static final Map<String, EnumChatFormatting> nameMapping = Maps.newHashMap();
   private static final Pattern formattingCodePattern = Pattern.compile("(?i)" + String.valueOf('§') + "[0-9A-FK-OR]");
   private final String name;
   private final char formattingCode;
   private final boolean fancyStyling;
   private final String controlString;
   private final int colorIndex;

   static {
      EnumChatFormatting[] var3;
      int var2 = (var3 = values()).length;

      for(int var1 = 0; var1 < var2; ++var1) {
         EnumChatFormatting enumchatformatting = var3[var1];
         nameMapping.put(func_175745_c(enumchatformatting.name), enumchatformatting);
      }

   }

   private static String func_175745_c(String p_175745_0_) {
      return p_175745_0_.toLowerCase().replaceAll("[^a-z]", "");
   }

   private EnumChatFormatting(String formattingName, char formattingCodeIn, int colorIndex) {
      this(formattingName, formattingCodeIn, false, colorIndex);
   }

   private EnumChatFormatting(String formattingName, char formattingCodeIn, boolean fancyStylingIn) {
      this(formattingName, formattingCodeIn, fancyStylingIn, -1);
   }

   private EnumChatFormatting(String formattingName, char formattingCodeIn, boolean fancyStylingIn, int colorIndex) {
      this.name = formattingName;
      this.formattingCode = formattingCodeIn;
      this.fancyStyling = fancyStylingIn;
      this.colorIndex = colorIndex;
      this.controlString = "§" + formattingCodeIn;
   }

   public int getColorIndex() {
      return this.colorIndex;
   }

   public boolean isFancyStyling() {
      return this.fancyStyling;
   }

   public boolean isColor() {
      return !this.fancyStyling && this != RESET;
   }

   public String getFriendlyName() {
      return this.name().toLowerCase();
   }

   public String toString() {
      return this.controlString;
   }

   public static String getTextWithoutFormattingCodes(String text) {
      return text == null ? null : formattingCodePattern.matcher(text).replaceAll("");
   }

   public static EnumChatFormatting getValueByName(String friendlyName) {
      return friendlyName == null ? null : (EnumChatFormatting)nameMapping.get(func_175745_c(friendlyName));
   }

   public static EnumChatFormatting func_175744_a(int p_175744_0_) {
      if (p_175744_0_ < 0) {
         return RESET;
      } else {
         EnumChatFormatting[] var4;
         int var3 = (var4 = values()).length;

         for(int var2 = 0; var2 < var3; ++var2) {
            EnumChatFormatting enumchatformatting = var4[var2];
            if (enumchatformatting.getColorIndex() == p_175744_0_) {
               return enumchatformatting;
            }
         }

         return null;
      }
   }

   public static Collection<String> getValidValues(boolean p_96296_0_, boolean p_96296_1_) {
      List<String> list = Lists.newArrayList();
      EnumChatFormatting[] var6;
      int var5 = (var6 = values()).length;

      for(int var4 = 0; var4 < var5; ++var4) {
         EnumChatFormatting enumchatformatting = var6[var4];
         if ((!enumchatformatting.isColor() || p_96296_0_) && (!enumchatformatting.isFancyStyling() || p_96296_1_)) {
            list.add(enumchatformatting.getFriendlyName());
         }
      }

      return list;
   }
}
