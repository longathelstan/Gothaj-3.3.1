package com.viaversion.viaversion.libs.mcstructs.snbt.impl.v1_12;

import com.viaversion.viaversion.libs.mcstructs.snbt.ISNbtSerializer;
import com.viaversion.viaversion.libs.mcstructs.snbt.exceptions.SNbtSerializeException;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.DoubleTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.FloatTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class SNbtSerializer_v1_12 implements ISNbtSerializer {
   private static final Pattern ESCAPE_PATTERN = Pattern.compile("[A-Za-z0-9._+-]+");

   public String serialize(Tag tag) throws SNbtSerializeException {
      if (tag instanceof ByteTag) {
         ByteTag byteTag = (ByteTag)tag;
         return byteTag.getValue() + "b";
      } else if (tag instanceof ShortTag) {
         ShortTag shortTag = (ShortTag)tag;
         return shortTag.getValue() + "s";
      } else if (tag instanceof IntTag) {
         IntTag intTag = (IntTag)tag;
         return String.valueOf(intTag.getValue());
      } else if (tag instanceof LongTag) {
         LongTag longTag = (LongTag)tag;
         return longTag.getValue() + "L";
      } else if (tag instanceof FloatTag) {
         FloatTag floatTag = (FloatTag)tag;
         return floatTag.getValue() + "f";
      } else if (tag instanceof DoubleTag) {
         DoubleTag doubleTag = (DoubleTag)tag;
         return doubleTag.getValue() + "d";
      } else {
         StringBuilder out;
         int i;
         if (tag instanceof ByteArrayTag) {
            ByteArrayTag byteArrayTag = (ByteArrayTag)tag;
            out = new StringBuilder("[B;");

            for(i = 0; i < byteArrayTag.length(); ++i) {
               if (i != 0) {
                  out.append(",");
               }

               out.append(byteArrayTag.getValue(i)).append("B");
            }

            return out.append("]").toString();
         } else if (tag instanceof StringTag) {
            StringTag stringTag = (StringTag)tag;
            return this.escape(stringTag.getValue());
         } else if (tag instanceof ListTag) {
            ListTag listTag = (ListTag)tag;
            out = new StringBuilder("[");

            for(i = 0; i < listTag.size(); ++i) {
               if (i != 0) {
                  out.append(",");
               }

               out.append(this.serialize(listTag.get(i)));
            }

            return out.append("]").toString();
         } else if (tag instanceof CompoundTag) {
            CompoundTag compoundTag = (CompoundTag)tag;
            out = new StringBuilder("{");

            Entry entry;
            for(Iterator var16 = compoundTag.getValue().entrySet().iterator(); var16.hasNext(); out.append(this.checkEscape((String)entry.getKey())).append(":").append(this.serialize((Tag)entry.getValue()))) {
               entry = (Entry)var16.next();
               if (out.length() != 1) {
                  out.append(",");
               }
            }

            return out.append("}").toString();
         } else if (tag instanceof IntArrayTag) {
            IntArrayTag intArrayTag = (IntArrayTag)tag;
            out = new StringBuilder("[I;");

            for(i = 0; i < intArrayTag.length(); ++i) {
               if (i != 0) {
                  out.append(",");
               }

               out.append(intArrayTag.getValue(i));
            }

            return out.append("]").toString();
         } else if (tag instanceof LongArrayTag) {
            LongArrayTag longArrayTag = (LongArrayTag)tag;
            out = new StringBuilder("[L;");

            for(i = 0; i < longArrayTag.length(); ++i) {
               if (i != 0) {
                  out.append(",");
               }

               out.append(longArrayTag.getValue(i)).append("L");
            }

            return out.append("]").toString();
         } else {
            throw new SNbtSerializeException(tag);
         }
      }
   }

   protected String checkEscape(String s) {
      return ESCAPE_PATTERN.matcher(s).matches() ? s : this.escape(s);
   }

   protected String escape(String s) {
      StringBuilder out = new StringBuilder("\"");
      char[] chars = s.toCharArray();
      char[] var4 = chars;
      int var5 = chars.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         char c = var4[var6];
         if (c == '\\' || c == '"') {
            out.append("\\");
         }

         out.append(c);
      }

      return out.append("\"").toString();
   }
}
