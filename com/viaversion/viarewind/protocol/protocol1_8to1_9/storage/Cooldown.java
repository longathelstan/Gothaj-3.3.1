package com.viaversion.viarewind.protocol.protocol1_8to1_9.storage;

import com.viaversion.viarewind.ViaRewind;
import com.viaversion.viarewind.protocol.protocol1_8to1_9.cooldown.CooldownVisualization;
import com.viaversion.viarewind.utils.Tickable;
import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.util.Pair;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;

public class Cooldown extends StoredObject implements Tickable {
   private double attackSpeed = 4.0D;
   private long lastHit = 0L;
   private CooldownVisualization.Factory visualizationFactory = CooldownVisualization.Factory.fromConfiguration();
   private CooldownVisualization current;

   public Cooldown(UserConnection user) {
      super(user);
   }

   public void tick() {
      if (!this.hasCooldown()) {
         this.endCurrentVisualization();
      } else {
         BlockPlaceDestroyTracker tracker = (BlockPlaceDestroyTracker)this.getUser().get(BlockPlaceDestroyTracker.class);
         if (tracker.isMining()) {
            this.lastHit = 0L;
            this.endCurrentVisualization();
         } else {
            if (this.current == null) {
               this.current = this.visualizationFactory.create(this.getUser());
            }

            try {
               this.current.show(this.getCooldown());
            } catch (Exception var3) {
               ViaRewind.getPlatform().getLogger().log(Level.WARNING, "Unable to show cooldown visualization", var3);
            }

         }
      }
   }

   private void endCurrentVisualization() {
      if (this.current != null) {
         try {
            this.current.hide();
         } catch (Exception var2) {
            ViaRewind.getPlatform().getLogger().log(Level.WARNING, "Unable to hide cooldown visualization", var2);
         }

         this.current = null;
      }

   }

   public boolean hasCooldown() {
      long time = System.currentTimeMillis() - this.lastHit;
      double cooldown = this.restrain((double)time * this.attackSpeed / 1000.0D, 0.0D, 1.5D);
      return cooldown > 0.1D && cooldown < 1.1D;
   }

   public double getCooldown() {
      long time = System.currentTimeMillis() - this.lastHit;
      return this.restrain((double)time * this.attackSpeed / 1000.0D, 0.0D, 1.0D);
   }

   private double restrain(double x, double a, double b) {
      return x < a ? a : Math.min(x, b);
   }

   public double getAttackSpeed() {
      return this.attackSpeed;
   }

   public void setAttackSpeed(double attackSpeed) {
      this.attackSpeed = attackSpeed;
   }

   public void setAttackSpeed(double base, ArrayList<Pair<Byte, Double>> modifiers) {
      this.attackSpeed = base;

      int j;
      for(j = 0; j < modifiers.size(); ++j) {
         if ((Byte)((Pair)modifiers.get(j)).key() == 0) {
            this.attackSpeed += (Double)((Pair)modifiers.get(j)).value();
            modifiers.remove(j--);
         }
      }

      for(j = 0; j < modifiers.size(); ++j) {
         if ((Byte)((Pair)modifiers.get(j)).key() == 1) {
            this.attackSpeed += base * (Double)((Pair)modifiers.get(j)).value();
            modifiers.remove(j--);
         }
      }

      for(j = 0; j < modifiers.size(); ++j) {
         if ((Byte)((Pair)modifiers.get(j)).key() == 2) {
            this.attackSpeed *= 1.0D + (Double)((Pair)modifiers.get(j)).value();
            modifiers.remove(j--);
         }
      }

   }

   public void hit() {
      this.lastHit = System.currentTimeMillis();
   }

   public void setLastHit(long lastHit) {
      this.lastHit = lastHit;
   }

   public void setVisualizationFactory(CooldownVisualization.Factory visualizationFactory) {
      this.visualizationFactory = (CooldownVisualization.Factory)Objects.requireNonNull(visualizationFactory, "visualizationFactory");
   }
}
