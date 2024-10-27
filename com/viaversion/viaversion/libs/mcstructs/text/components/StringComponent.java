package com.viaversion.viaversion.libs.mcstructs.text.components;

import com.viaversion.viaversion.libs.mcstructs.text.ATextComponent;
import java.util.Objects;

public class StringComponent extends ATextComponent {
   private final String text;

   public StringComponent() {
      this("");
   }

   public StringComponent(String text) {
      this.text = text;
   }

   public String getText() {
      return this.text;
   }

   public String asSingleString() {
      return this.text;
   }

   public ATextComponent copy() {
      return this.putMetaCopy(new StringComponent(this.text));
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         StringComponent that = (StringComponent)o;
         return Objects.equals(this.getSiblings(), that.getSiblings()) && Objects.equals(this.getStyle(), that.getStyle()) && Objects.equals(this.text, that.text);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.getSiblings(), this.getStyle(), this.text});
   }

   public String toString() {
      return "StringComponent{siblings=" + this.getSiblings() + ", style=" + this.getStyle() + ", text='" + this.text + '\'' + '}';
   }
}
