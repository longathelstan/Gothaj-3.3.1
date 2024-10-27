package net.minecraft.util;

public class ChatAllowedCharacters {
   public static final char[] allowedCharactersArray = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};

   public static boolean isAllowedCharacter(char character) {
      return character != 167 && character >= ' ' && character != 127;
   }

   public static String filterAllowedCharacters(String input) {
      StringBuilder stringbuilder = new StringBuilder();
      char[] var5;
      int var4 = (var5 = input.toCharArray()).length;

      for(int var3 = 0; var3 < var4; ++var3) {
         char c0 = var5[var3];
         if (isAllowedCharacter(c0)) {
            stringbuilder.append(c0);
         }
      }

      return stringbuilder.toString();
   }
}
