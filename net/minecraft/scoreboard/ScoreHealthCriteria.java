package net.minecraft.scoreboard;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class ScoreHealthCriteria extends ScoreDummyCriteria {
   public ScoreHealthCriteria(String name) {
      super(name);
   }

   public int setScore(List<EntityPlayer> p_96635_1_) {
      float f = 0.0F;

      EntityPlayer entityplayer;
      for(Iterator var4 = p_96635_1_.iterator(); var4.hasNext(); f += entityplayer.getHealth() + entityplayer.getAbsorptionAmount()) {
         entityplayer = (EntityPlayer)var4.next();
      }

      if (p_96635_1_.size() > 0) {
         f /= (float)p_96635_1_.size();
      }

      return MathHelper.ceiling_float_int(f);
   }

   public boolean isReadOnly() {
      return true;
   }

   public IScoreObjectiveCriteria.EnumRenderType getRenderType() {
      return IScoreObjectiveCriteria.EnumRenderType.HEARTS;
   }
}
