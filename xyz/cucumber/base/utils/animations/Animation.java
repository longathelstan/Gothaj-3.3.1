package xyz.cucumber.base.utils.animations;

public class Animation {
   private AnimationDirection direction;
   private double animation;
   private int delay;
   private int finishTime;

   public Animation(AnimationDirection direction, int delay, int states) {
      this.direction = direction;
      this.delay = delay;
      this.finishTime = (int)(System.nanoTime() / 1000000L + (long)delay);
   }

   public void reset() {
      this.finishTime = (int)(System.nanoTime() / 1000000L + (long)this.delay);
      this.animation = 0.0D;
      this.direction = AnimationDirection.PRESCALING;
   }

   public float getAnimation(float finalValue, int speed) {
      float finals;
      if (this.direction == AnimationDirection.PRESCALING) {
         if (this.animation < 1.005D) {
            finals = (float)((this.animation * 7.0D + (double)finalValue * 1.02D) / 8.0D);
            this.animation = (double)finals;
            return finals;
         }

         this.direction = AnimationDirection.CORRECTING;
      } else {
         if (this.direction != AnimationDirection.CORRECTING) {
            return finalValue;
         }

         if (this.animation > 1.005D) {
            finals = (float)((this.animation * 4.0D + (double)finalValue) / 5.0D);
            this.animation = (double)finals;
            return finals;
         }

         this.direction = AnimationDirection.FINAL;
         this.direction = AnimationDirection.FINAL;
      }

      return finalValue;
   }

   public void changeState() {
      if (this.direction == AnimationDirection.PRESCALING) {
         this.direction = AnimationDirection.CORRECTING;
      } else if (this.direction == AnimationDirection.CORRECTING) {
         this.direction = AnimationDirection.FINAL;
      } else {
         this.direction = AnimationDirection.PRESCALING;
      }

   }

   public AnimationDirection getDirection() {
      return this.direction;
   }

   public void setDirection(AnimationDirection direction) {
      this.direction = direction;
   }

   public long getDelay() {
      return (long)this.delay;
   }
}
