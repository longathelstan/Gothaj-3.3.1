package net.optifine.util;

import net.minecraft.util.MathHelper;

public class MathUtils {
   public static final float PI = 3.1415927F;
   public static final float PI2 = 6.2831855F;
   public static final float PId2 = 1.5707964F;
   private static final float[] ASIN_TABLE = new float[65536];

   static {
      int j;
      for(j = 0; j < 65536; ++j) {
         ASIN_TABLE[j] = (float)Math.asin((double)j / 32767.5D - 1.0D);
      }

      for(j = -1; j < 2; ++j) {
         ASIN_TABLE[(int)(((double)j + 1.0D) * 32767.5D) & '\uffff'] = (float)Math.asin((double)j);
      }

   }

   public static float asin(float value) {
      return ASIN_TABLE[(int)((double)(value + 1.0F) * 32767.5D) & '\uffff'];
   }

   public static float acos(float value) {
      return 1.5707964F - ASIN_TABLE[(int)((double)(value + 1.0F) * 32767.5D) & '\uffff'];
   }

   public static int getAverage(int[] vals) {
      if (vals.length <= 0) {
         return 0;
      } else {
         int i = getSum(vals);
         int j = i / vals.length;
         return j;
      }
   }

   public static int getSum(int[] vals) {
      if (vals.length <= 0) {
         return 0;
      } else {
         int i = 0;

         for(int j = 0; j < vals.length; ++j) {
            int k = vals[j];
            i += k;
         }

         return i;
      }
   }

   public static int roundDownToPowerOfTwo(int val) {
      int i = MathHelper.roundUpToPowerOfTwo(val);
      return val == i ? i : i / 2;
   }

   public static boolean equalsDelta(float f1, float f2, float delta) {
      return Math.abs(f1 - f2) <= delta;
   }

   public static float toDeg(float angle) {
      return angle * 180.0F / MathHelper.PI;
   }

   public static float toRad(float angle) {
      return angle / 180.0F * MathHelper.PI;
   }

   public static float roundToFloat(double d) {
      return (float)((double)Math.round(d * 1.0E8D) / 1.0E8D);
   }
}
