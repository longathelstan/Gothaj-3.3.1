package com.viaversion.viaversion.libs.mcstructs.text.events.click;

import java.util.Objects;

public class ClickEvent {
   private final ClickEventAction action;
   private final String value;

   public ClickEvent(ClickEventAction action, String value) {
      this.action = action;
      this.value = value;
   }

   public ClickEventAction getAction() {
      return this.action;
   }

   public String getValue() {
      return this.value;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ClickEvent that = (ClickEvent)o;
         return this.action == that.action && Objects.equals(this.value, that.value);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.action, this.value});
   }

   public String toString() {
      return "ClickEvent{action=" + this.action + ", value='" + this.value + "'}";
   }
}
