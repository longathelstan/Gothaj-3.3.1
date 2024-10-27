package com.viaversion.viaversion.libs.mcstructs.text.utils;

import com.viaversion.viaversion.libs.mcstructs.core.TextFormatting;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LegacyStringUtils {
   public static LegacyStringUtils.LegacyStyle getStyleAt(String s, int position, boolean unknownWhite) {
      return getStyleAt(s, position, (c) -> {
         TextFormatting formatting = TextFormatting.getByCode(c);
         if (formatting == null) {
            return unknownWhite ? TextFormatting.WHITE : null;
         } else {
            return formatting;
         }
      });
   }

   public static LegacyStringUtils.LegacyStyle getStyleAt(String s, int position, Function<Character, TextFormatting> formattingResolver) {
      char[] chars = s.toCharArray();
      LegacyStringUtils.LegacyStyle legacyStyle = new LegacyStringUtils.LegacyStyle();

      for(int i = 0; i < Math.min(chars.length, position); ++i) {
         char c = chars[i];
         if (c == 167 && i + 1 < chars.length) {
            ++i;
            char code = chars[i];
            TextFormatting formatting = (TextFormatting)formattingResolver.apply(code);
            if (formatting != null) {
               if (TextFormatting.RESET.equals(formatting)) {
                  legacyStyle.setColor((TextFormatting)null);
                  legacyStyle.getStyles().clear();
               } else if (formatting.isColor()) {
                  legacyStyle.setColor(formatting);
                  legacyStyle.getStyles().clear();
               } else {
                  legacyStyle.getStyles().add(formatting);
               }
            }
         }
      }

      return legacyStyle;
   }

   public static String[] split(String s, String split, boolean unknownWhite) {
      return split(s, split, (c) -> {
         TextFormatting formatting = TextFormatting.getByCode(c);
         if (formatting == null) {
            return unknownWhite ? TextFormatting.WHITE : null;
         } else {
            return formatting;
         }
      });
   }

   public static String[] split(String s, String split, Function<Character, TextFormatting> formattingResolver) {
      String[] parts = s.split(Pattern.quote(split));

      for(int i = 1; i < parts.length; ++i) {
         String prev = parts[i - 1];
         LegacyStringUtils.LegacyStyle style = getStyleAt(prev, prev.length(), formattingResolver);
         parts[i] = style.toLegacy() + parts[i];
      }

      return parts;
   }

   public static class LegacyStyle {
      private TextFormatting color;
      private final Set<TextFormatting> styles;

      private LegacyStyle() {
         this.color = null;
         this.styles = new HashSet();
      }

      public void setColor(@Nullable TextFormatting color) {
         this.color = color;
      }

      @Nullable
      public TextFormatting getColor() {
         return this.color;
      }

      @Nonnull
      public Set<TextFormatting> getStyles() {
         return this.styles;
      }

      public String toLegacy() {
         StringBuilder out = new StringBuilder();
         if (this.color != null) {
            out.append(this.color.toLegacy());
         }

         Iterator var2 = this.styles.iterator();

         while(var2.hasNext()) {
            TextFormatting style = (TextFormatting)var2.next();
            out.append(style.toLegacy());
         }

         return out.toString();
      }

      public String toString() {
         return "LegacyStyle{color=" + this.color + ", styles=" + this.styles + '}';
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            LegacyStringUtils.LegacyStyle that = (LegacyStringUtils.LegacyStyle)o;
            return Objects.equals(this.color, that.color) && Objects.equals(this.styles, that.styles);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.color, this.styles});
      }

      // $FF: synthetic method
      LegacyStyle(Object x0) {
         this();
      }
   }
}
