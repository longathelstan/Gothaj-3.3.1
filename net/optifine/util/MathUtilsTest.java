package net.optifine.util;

import net.minecraft.util.MathHelper;

public class MathUtilsTest {
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$net$optifine$util$MathUtilsTest$OPER;

   public static void main(String[] args) throws Exception {
      MathUtilsTest.OPER[] amathutilstest$oper = MathUtilsTest.OPER.values();

      for(int i = 0; i < amathutilstest$oper.length; ++i) {
         MathUtilsTest.OPER mathutilstest$oper = amathutilstest$oper[i];
         dbg("******** " + mathutilstest$oper + " ***********");
         test(mathutilstest$oper, false);
      }

   }

   private static void test(MathUtilsTest.OPER oper, boolean fast) {
      MathHelper.fastMath = fast;
      double d0;
      double d1;
      switch($SWITCH_TABLE$net$optifine$util$MathUtilsTest$OPER()[oper.ordinal()]) {
      case 1:
      case 2:
         d0 = (double)(-MathHelper.PI);
         d1 = (double)MathHelper.PI;
         break;
      case 3:
      case 4:
         d0 = -1.0D;
         d1 = 1.0D;
         break;
      default:
         return;
      }

      int i = 10;

      for(int j = 0; j <= i; ++j) {
         double d2 = d0 + (double)j * (d1 - d0) / (double)i;
         float f;
         float f1;
         switch($SWITCH_TABLE$net$optifine$util$MathUtilsTest$OPER()[oper.ordinal()]) {
         case 1:
            f = (float)Math.sin(d2);
            f1 = MathHelper.sin((float)d2);
            break;
         case 2:
            f = (float)Math.cos(d2);
            f1 = MathHelper.cos((float)d2);
            break;
         case 3:
            f = (float)Math.asin(d2);
            f1 = MathUtils.asin((float)d2);
            break;
         case 4:
            f = (float)Math.acos(d2);
            f1 = MathUtils.acos((float)d2);
            break;
         default:
            return;
         }

         dbg(String.format("%.2f, Math: %f, Helper: %f, diff: %f", d2, f, f1, Math.abs(f - f1)));
      }

   }

   public static void dbg(String str) {
      System.out.println(str);
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$optifine$util$MathUtilsTest$OPER() {
      int[] var10000 = $SWITCH_TABLE$net$optifine$util$MathUtilsTest$OPER;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[MathUtilsTest.OPER.values().length];

         try {
            var0[MathUtilsTest.OPER.ACOS.ordinal()] = 4;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[MathUtilsTest.OPER.ASIN.ordinal()] = 3;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[MathUtilsTest.OPER.COS.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[MathUtilsTest.OPER.SIN.ordinal()] = 1;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$net$optifine$util$MathUtilsTest$OPER = var0;
         return var0;
      }
   }

   private static enum OPER {
      SIN,
      COS,
      ASIN,
      ACOS;
   }
}
