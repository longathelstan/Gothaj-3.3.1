package net.optifine.expr;

import net.optifine.shaders.uniform.Smoother;

public class FunctionFloat implements IExpressionFloat {
   private FunctionType type;
   private IExpression[] arguments;
   private int smoothId = -1;
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$net$optifine$expr$FunctionType;

   public FunctionFloat(FunctionType type, IExpression[] arguments) {
      this.type = type;
      this.arguments = arguments;
   }

   public float eval() {
      IExpression[] aiexpression = this.arguments;
      switch($SWITCH_TABLE$net$optifine$expr$FunctionType()[this.type.ordinal()]) {
      case 46:
         IExpression iexpression = aiexpression[0];
         if (!(iexpression instanceof ConstantFloat)) {
            float f = evalFloat(aiexpression, 0);
            float f1 = aiexpression.length > 1 ? evalFloat(aiexpression, 1) : 1.0F;
            float f2 = aiexpression.length > 2 ? evalFloat(aiexpression, 2) : f1;
            if (this.smoothId < 0) {
               this.smoothId = Smoother.getNextId();
            }

            float f3 = Smoother.getSmoothValue(this.smoothId, f, f1, f2);
            return f3;
         }
      default:
         return this.type.evalFloat(this.arguments);
      }
   }

   private static float evalFloat(IExpression[] exprs, int index) {
      IExpressionFloat iexpressionfloat = (IExpressionFloat)exprs[index];
      float f = iexpressionfloat.eval();
      return f;
   }

   public ExpressionType getExpressionType() {
      return ExpressionType.FLOAT;
   }

   public String toString() {
      return this.type + "()";
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$optifine$expr$FunctionType() {
      int[] var10000 = $SWITCH_TABLE$net$optifine$expr$FunctionType;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[FunctionType.values().length];

         try {
            var0[FunctionType.ABS.ordinal()] = 20;
         } catch (NoSuchFieldError var51) {
         }

         try {
            var0[FunctionType.ACOS.ordinal()] = 11;
         } catch (NoSuchFieldError var50) {
         }

         try {
            var0[FunctionType.AND.ordinal()] = 35;
         } catch (NoSuchFieldError var49) {
         }

         try {
            var0[FunctionType.ASIN.ordinal()] = 10;
         } catch (NoSuchFieldError var48) {
         }

         try {
            var0[FunctionType.ATAN.ordinal()] = 13;
         } catch (NoSuchFieldError var47) {
         }

         try {
            var0[FunctionType.ATAN2.ordinal()] = 14;
         } catch (NoSuchFieldError var46) {
         }

         try {
            var0[FunctionType.BETWEEN.ordinal()] = 43;
         } catch (NoSuchFieldError var45) {
         }

         try {
            var0[FunctionType.CEIL.ordinal()] = 22;
         } catch (NoSuchFieldError var44) {
         }

         try {
            var0[FunctionType.CLAMP.ordinal()] = 19;
         } catch (NoSuchFieldError var43) {
         }

         try {
            var0[FunctionType.COS.ordinal()] = 9;
         } catch (NoSuchFieldError var42) {
         }

         try {
            var0[FunctionType.DIV.ordinal()] = 4;
         } catch (NoSuchFieldError var41) {
         }

         try {
            var0[FunctionType.EQUAL.ordinal()] = 41;
         } catch (NoSuchFieldError var40) {
         }

         try {
            var0[FunctionType.EQUALS.ordinal()] = 44;
         } catch (NoSuchFieldError var39) {
         }

         try {
            var0[FunctionType.EXP.ordinal()] = 23;
         } catch (NoSuchFieldError var38) {
         }

         try {
            var0[FunctionType.FALSE.ordinal()] = 48;
         } catch (NoSuchFieldError var37) {
         }

         try {
            var0[FunctionType.FLOOR.ordinal()] = 21;
         } catch (NoSuchFieldError var36) {
         }

         try {
            var0[FunctionType.FMOD.ordinal()] = 31;
         } catch (NoSuchFieldError var35) {
         }

         try {
            var0[FunctionType.FRAC.ordinal()] = 24;
         } catch (NoSuchFieldError var34) {
         }

         try {
            var0[FunctionType.GREATER.ordinal()] = 37;
         } catch (NoSuchFieldError var33) {
         }

         try {
            var0[FunctionType.GREATER_OR_EQUAL.ordinal()] = 38;
         } catch (NoSuchFieldError var32) {
         }

         try {
            var0[FunctionType.IF.ordinal()] = 33;
         } catch (NoSuchFieldError var31) {
         }

         try {
            var0[FunctionType.IN.ordinal()] = 45;
         } catch (NoSuchFieldError var30) {
         }

         try {
            var0[FunctionType.LOG.ordinal()] = 25;
         } catch (NoSuchFieldError var29) {
         }

         try {
            var0[FunctionType.MAX.ordinal()] = 18;
         } catch (NoSuchFieldError var28) {
         }

         try {
            var0[FunctionType.MIN.ordinal()] = 17;
         } catch (NoSuchFieldError var27) {
         }

         try {
            var0[FunctionType.MINUS.ordinal()] = 2;
         } catch (NoSuchFieldError var26) {
         }

         try {
            var0[FunctionType.MOD.ordinal()] = 5;
         } catch (NoSuchFieldError var25) {
         }

         try {
            var0[FunctionType.MUL.ordinal()] = 3;
         } catch (NoSuchFieldError var24) {
         }

         try {
            var0[FunctionType.NEG.ordinal()] = 6;
         } catch (NoSuchFieldError var23) {
         }

         try {
            var0[FunctionType.NOT.ordinal()] = 34;
         } catch (NoSuchFieldError var22) {
         }

         try {
            var0[FunctionType.NOT_EQUAL.ordinal()] = 42;
         } catch (NoSuchFieldError var21) {
         }

         try {
            var0[FunctionType.OR.ordinal()] = 36;
         } catch (NoSuchFieldError var20) {
         }

         try {
            var0[FunctionType.PI.ordinal()] = 7;
         } catch (NoSuchFieldError var19) {
         }

         try {
            var0[FunctionType.PLUS.ordinal()] = 1;
         } catch (NoSuchFieldError var18) {
         }

         try {
            var0[FunctionType.POW.ordinal()] = 26;
         } catch (NoSuchFieldError var17) {
         }

         try {
            var0[FunctionType.RANDOM.ordinal()] = 27;
         } catch (NoSuchFieldError var16) {
         }

         try {
            var0[FunctionType.ROUND.ordinal()] = 28;
         } catch (NoSuchFieldError var15) {
         }

         try {
            var0[FunctionType.SIGNUM.ordinal()] = 29;
         } catch (NoSuchFieldError var14) {
         }

         try {
            var0[FunctionType.SIN.ordinal()] = 8;
         } catch (NoSuchFieldError var13) {
         }

         try {
            var0[FunctionType.SMALLER.ordinal()] = 39;
         } catch (NoSuchFieldError var12) {
         }

         try {
            var0[FunctionType.SMALLER_OR_EQUAL.ordinal()] = 40;
         } catch (NoSuchFieldError var11) {
         }

         try {
            var0[FunctionType.SMOOTH.ordinal()] = 46;
         } catch (NoSuchFieldError var10) {
         }

         try {
            var0[FunctionType.SQRT.ordinal()] = 30;
         } catch (NoSuchFieldError var9) {
         }

         try {
            var0[FunctionType.TAN.ordinal()] = 12;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var0[FunctionType.TIME.ordinal()] = 32;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[FunctionType.TODEG.ordinal()] = 16;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[FunctionType.TORAD.ordinal()] = 15;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[FunctionType.TRUE.ordinal()] = 47;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[FunctionType.VEC2.ordinal()] = 49;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[FunctionType.VEC3.ordinal()] = 50;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[FunctionType.VEC4.ordinal()] = 51;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$net$optifine$expr$FunctionType = var0;
         return var0;
      }
   }
}
