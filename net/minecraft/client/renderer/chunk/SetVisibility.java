package net.minecraft.client.renderer.chunk;

import java.util.Iterator;
import java.util.Set;
import net.minecraft.util.EnumFacing;

public class SetVisibility {
   private static final int COUNT_FACES = EnumFacing.values().length;
   private long bits;

   public void setManyVisible(Set<EnumFacing> p_178620_1_) {
      Iterator var3 = p_178620_1_.iterator();

      while(var3.hasNext()) {
         EnumFacing enumfacing = (EnumFacing)var3.next();
         Iterator var5 = p_178620_1_.iterator();

         while(var5.hasNext()) {
            EnumFacing enumfacing1 = (EnumFacing)var5.next();
            this.setVisible(enumfacing, enumfacing1, true);
         }
      }

   }

   public void setVisible(EnumFacing facing, EnumFacing facing2, boolean p_178619_3_) {
      this.setBit(facing.ordinal() + facing2.ordinal() * COUNT_FACES, p_178619_3_);
      this.setBit(facing2.ordinal() + facing.ordinal() * COUNT_FACES, p_178619_3_);
   }

   public void setAllVisible(boolean visible) {
      if (visible) {
         this.bits = -1L;
      } else {
         this.bits = 0L;
      }

   }

   public boolean isVisible(EnumFacing facing, EnumFacing facing2) {
      return this.getBit(facing.ordinal() + facing2.ordinal() * COUNT_FACES);
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append(' ');
      EnumFacing[] var5;
      int var4 = (var5 = EnumFacing.values()).length;

      EnumFacing enumfacing2;
      int var3;
      for(var3 = 0; var3 < var4; ++var3) {
         enumfacing2 = var5[var3];
         stringbuilder.append(' ').append(enumfacing2.toString().toUpperCase().charAt(0));
      }

      stringbuilder.append('\n');
      var4 = (var5 = EnumFacing.values()).length;

      for(var3 = 0; var3 < var4; ++var3) {
         enumfacing2 = var5[var3];
         stringbuilder.append(enumfacing2.toString().toUpperCase().charAt(0));
         EnumFacing[] var9;
         int var8 = (var9 = EnumFacing.values()).length;

         for(int var7 = 0; var7 < var8; ++var7) {
            EnumFacing enumfacing1 = var9[var7];
            if (enumfacing2 == enumfacing1) {
               stringbuilder.append("  ");
            } else {
               boolean flag = this.isVisible(enumfacing2, enumfacing1);
               stringbuilder.append(' ').append((char)(flag ? 'Y' : 'n'));
            }
         }

         stringbuilder.append('\n');
      }

      return stringbuilder.toString();
   }

   private boolean getBit(int p_getBit_1_) {
      return (this.bits & (long)(1 << p_getBit_1_)) != 0L;
   }

   private void setBit(int p_setBit_1_, boolean p_setBit_2_) {
      if (p_setBit_2_) {
         this.setBit(p_setBit_1_);
      } else {
         this.clearBit(p_setBit_1_);
      }

   }

   private void setBit(int p_setBit_1_) {
      this.bits |= (long)(1 << p_setBit_1_);
   }

   private void clearBit(int p_clearBit_1_) {
      this.bits &= (long)(~(1 << p_clearBit_1_));
   }
}
