package xyz.cucumber.base.events.ext;

import xyz.cucumber.base.events.Event;

public class EventLook extends Event {
   private float yaw;
   private float pitch;

   public float getYaw() {
      return this.yaw;
   }

   public void setYaw(float yaw) {
      this.yaw = yaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void setPitch(float pitch) {
      this.pitch = pitch;
   }

   public EventLook(float yaw, float pitch) {
      this.yaw = yaw;
      this.pitch = pitch;
   }
}
