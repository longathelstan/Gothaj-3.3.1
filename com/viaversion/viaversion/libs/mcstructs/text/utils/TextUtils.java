package com.viaversion.viaversion.libs.mcstructs.text.utils;

import com.viaversion.viaversion.libs.mcstructs.core.TextFormatting;
import com.viaversion.viaversion.libs.mcstructs.text.ATextComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.StringComponent;
import com.viaversion.viaversion.libs.mcstructs.text.events.click.ClickEvent;
import com.viaversion.viaversion.libs.mcstructs.text.events.click.ClickEventAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
   private static final String URL_PATTERN = "(?:https?://)?[\\w._-]+\\.\\w{2,}(?:/\\S*)?";

   public static ATextComponent makeURLsClickable(ATextComponent component) {
      return replace(component, "(?:https?://)?[\\w._-]+\\.\\w{2,}(?:/\\S*)?", (comp) -> {
         comp.getStyle().setClickEvent(new ClickEvent(ClickEventAction.OPEN_URL, comp.asSingleString()));
         return comp;
      });
   }

   public static ATextComponent replace(ATextComponent component, String searchRegex, Function<ATextComponent, ATextComponent> replaceFunction) {
      Pattern pattern = Pattern.compile(searchRegex);
      Object out;
      if (component instanceof StringComponent) {
         String text = component.asSingleString();
         Matcher matcher = pattern.matcher(text);
         List<ATextComponent> parts = new ArrayList();

         int last;
         for(last = 0; matcher.find(); last = matcher.end()) {
            int start = matcher.start();
            String match = matcher.group();
            if (start > last) {
               parts.add((new StringComponent(text.substring(last, start))).setStyle(component.getStyle().copy()));
            }

            ATextComponent replace = (ATextComponent)replaceFunction.apply((new StringComponent(match)).setStyle(component.getStyle().copy()));
            if (replace != null) {
               parts.add(replace);
            }
         }

         if (last < text.length()) {
            parts.add((new StringComponent(text.substring(last))).setStyle(component.getStyle().copy()));
         }

         if (parts.size() > 1) {
            out = new StringComponent("");
            Iterator var15 = parts.iterator();

            while(var15.hasNext()) {
               ATextComponent part = (ATextComponent)var15.next();
               ((ATextComponent)out).append(part);
            }
         } else {
            if (parts.size() == 1) {
               out = ((ATextComponent)parts.get(0)).copy();
            } else {
               out = component.copy();
            }

            ((ATextComponent)out).getSiblings().clear();
         }
      } else {
         out = component.copy();
         ((ATextComponent)out).getSiblings().clear();
      }

      Iterator var12 = component.getSiblings().iterator();

      while(var12.hasNext()) {
         ATextComponent sibling = (ATextComponent)var12.next();
         ATextComponent replace = replace(sibling, searchRegex, replaceFunction);
         ((ATextComponent)out).append(replace);
      }

      return (ATextComponent)out;
   }

   public static ATextComponent replaceRGBColors(ATextComponent component) {
      ATextComponent out = component.copy();
      out.forEach((comp) -> {
         if (comp.getStyle().getColor() != null && comp.getStyle().getColor().isRGBColor()) {
            comp.getStyle().setFormatting(TextFormatting.getClosestFormattingColor(comp.getStyle().getColor().getRgbValue()));
         }

      });
      return out;
   }

   public static ATextComponent join(ATextComponent separator, ATextComponent... components) {
      if (components.length == 0) {
         return new StringComponent("");
      } else if (components.length == 1) {
         return components[0].copy();
      } else {
         ATextComponent out = null;
         ATextComponent[] var3 = components;
         int var4 = components.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            ATextComponent component = var3[var5];
            if (out == null) {
               out = (new StringComponent("")).append(component.copy());
            } else {
               out.append(separator.copy()).append(component.copy());
            }
         }

         return out;
      }
   }
}
