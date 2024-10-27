package com.viaversion.viaversion.libs.mcstructs.text;

import com.viaversion.viaversion.libs.mcstructs.core.ICopyable;
import com.viaversion.viaversion.libs.mcstructs.core.TextFormatting;
import com.viaversion.viaversion.libs.mcstructs.text.components.StringComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public abstract class ATextComponent implements ICopyable<ATextComponent> {
   private final List<ATextComponent> siblings = new ArrayList();
   private Style style = new Style();

   public ATextComponent append(String s) {
      this.append((ATextComponent)(new StringComponent(s)));
      return this;
   }

   public ATextComponent append(ATextComponent component) {
      this.siblings.add(component);
      return this;
   }

   public ATextComponent append(ATextComponent... components) {
      this.siblings.addAll(Arrays.asList(components));
      return this;
   }

   public List<ATextComponent> getSiblings() {
      return this.siblings;
   }

   public ATextComponent forEach(Consumer<ATextComponent> consumer) {
      consumer.accept(this);
      Iterator var2 = this.siblings.iterator();

      while(var2.hasNext()) {
         ATextComponent sibling = (ATextComponent)var2.next();
         sibling.forEach(consumer);
      }

      return this;
   }

   @Nonnull
   public Style getStyle() {
      return this.style;
   }

   public ATextComponent setStyle(@Nonnull Style style) {
      this.style = style;
      return this;
   }

   public ATextComponent setParentStyle(@Nonnull Style style) {
      this.style.setParent(style);
      return this;
   }

   public ATextComponent copyParentStyle() {
      Iterator var1 = this.siblings.iterator();

      while(var1.hasNext()) {
         ATextComponent sibling = (ATextComponent)var1.next();
         sibling.getStyle().setParent(this.style);
         sibling.copyParentStyle();
      }

      return this;
   }

   public <C extends ATextComponent> C putMetaCopy(C component) {
      component.setStyle(this.style.copy());
      Iterator var2 = this.siblings.iterator();

      while(var2.hasNext()) {
         ATextComponent sibling = (ATextComponent)var2.next();
         component.append(sibling.copy());
      }

      return component;
   }

   public String asUnformattedString() {
      StringBuilder out = new StringBuilder(this.asSingleString());
      Iterator var2 = this.siblings.iterator();

      while(var2.hasNext()) {
         ATextComponent sibling = (ATextComponent)var2.next();
         out.append(sibling.asUnformattedString());
      }

      return out.toString();
   }

   public String asLegacyFormatString() {
      StringBuilder out = new StringBuilder();
      if (this.style.getColor() != null && this.style.getColor().isFormattingColor()) {
         out.append('§').append(this.style.getColor().getCode());
      }

      if (this.style.isObfuscated()) {
         out.append('§').append(TextFormatting.OBFUSCATED.getCode());
      }

      if (this.style.isBold()) {
         out.append('§').append(TextFormatting.BOLD.getCode());
      }

      if (this.style.isStrikethrough()) {
         out.append('§').append(TextFormatting.STRIKETHROUGH.getCode());
      }

      if (this.style.isUnderlined()) {
         out.append('§').append(TextFormatting.UNDERLINE.getCode());
      }

      if (this.style.isItalic()) {
         out.append('§').append(TextFormatting.ITALIC.getCode());
      }

      out.append(this.asSingleString());
      Iterator var2 = this.siblings.iterator();

      while(var2.hasNext()) {
         ATextComponent sibling = (ATextComponent)var2.next();
         ATextComponent copy = sibling.copy();
         copy.getStyle().setParent(this.style);
         out.append(copy.asLegacyFormatString());
      }

      return out.toString();
   }

   public abstract String asSingleString();

   public abstract ATextComponent copy();

   public abstract boolean equals(Object var1);

   public abstract int hashCode();

   public abstract String toString();
}
