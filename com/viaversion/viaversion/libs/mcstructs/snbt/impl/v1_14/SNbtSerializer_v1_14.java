package com.viaversion.viaversion.libs.mcstructs.snbt.impl.v1_14;

import com.viaversion.viaversion.libs.mcstructs.snbt.impl.v1_12.SNbtSerializer_v1_12;

public class SNbtSerializer_v1_14 extends SNbtSerializer_v1_12 {
   protected String escape(String s) {
      StringBuilder out = new StringBuilder(" ");
      char openQuotation = 0;
      char[] chars = s.toCharArray();
      char[] var5 = chars;
      int var6 = chars.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         char c = var5[var7];
         if (c == '\\') {
            out.append("\\");
         } else if (c == '"' || c == '\'') {
            if (openQuotation == 0) {
               if (c == '"') {
                  openQuotation = '\'';
               } else {
                  openQuotation = '"';
               }
            }

            if (openQuotation == c) {
               out.append("\\");
            }
         }

         out.append(c);
      }

      if (openQuotation == 0) {
         openQuotation = '"';
      }

      out.setCharAt(0, openQuotation);
      out.append(openQuotation);
      return out.toString();
   }
}
