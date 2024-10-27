package net.optifine.expr;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.optifine.shaders.uniform.Smoother;
import net.optifine.util.MathUtils;

public enum FunctionType {
   PLUS(10, ExpressionType.FLOAT, "+", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
   MINUS(10, ExpressionType.FLOAT, "-", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
   MUL(11, ExpressionType.FLOAT, "*", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
   DIV(11, ExpressionType.FLOAT, "/", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
   MOD(11, ExpressionType.FLOAT, "%", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
   NEG(12, ExpressionType.FLOAT, "neg", new ExpressionType[]{ExpressionType.FLOAT}),
   PI(ExpressionType.FLOAT, "pi", new ExpressionType[0]),
   SIN(ExpressionType.FLOAT, "sin", new ExpressionType[]{ExpressionType.FLOAT}),
   COS(ExpressionType.FLOAT, "cos", new ExpressionType[]{ExpressionType.FLOAT}),
   ASIN(ExpressionType.FLOAT, "asin", new ExpressionType[]{ExpressionType.FLOAT}),
   ACOS(ExpressionType.FLOAT, "acos", new ExpressionType[]{ExpressionType.FLOAT}),
   TAN(ExpressionType.FLOAT, "tan", new ExpressionType[]{ExpressionType.FLOAT}),
   ATAN(ExpressionType.FLOAT, "atan", new ExpressionType[]{ExpressionType.FLOAT}),
   ATAN2(ExpressionType.FLOAT, "atan2", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
   TORAD(ExpressionType.FLOAT, "torad", new ExpressionType[]{ExpressionType.FLOAT}),
   TODEG(ExpressionType.FLOAT, "todeg", new ExpressionType[]{ExpressionType.FLOAT}),
   MIN(ExpressionType.FLOAT, "min", (new ParametersVariable()).first(ExpressionType.FLOAT).repeat(ExpressionType.FLOAT)),
   MAX(ExpressionType.FLOAT, "max", (new ParametersVariable()).first(ExpressionType.FLOAT).repeat(ExpressionType.FLOAT)),
   CLAMP(ExpressionType.FLOAT, "clamp", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT}),
   ABS(ExpressionType.FLOAT, "abs", new ExpressionType[]{ExpressionType.FLOAT}),
   FLOOR(ExpressionType.FLOAT, "floor", new ExpressionType[]{ExpressionType.FLOAT}),
   CEIL(ExpressionType.FLOAT, "ceil", new ExpressionType[]{ExpressionType.FLOAT}),
   EXP(ExpressionType.FLOAT, "exp", new ExpressionType[]{ExpressionType.FLOAT}),
   FRAC(ExpressionType.FLOAT, "frac", new ExpressionType[]{ExpressionType.FLOAT}),
   LOG(ExpressionType.FLOAT, "log", new ExpressionType[]{ExpressionType.FLOAT}),
   POW(ExpressionType.FLOAT, "pow", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
   RANDOM(ExpressionType.FLOAT, "random", new ExpressionType[0]),
   ROUND(ExpressionType.FLOAT, "round", new ExpressionType[]{ExpressionType.FLOAT}),
   SIGNUM(ExpressionType.FLOAT, "signum", new ExpressionType[]{ExpressionType.FLOAT}),
   SQRT(ExpressionType.FLOAT, "sqrt", new ExpressionType[]{ExpressionType.FLOAT}),
   FMOD(ExpressionType.FLOAT, "fmod", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
   TIME(ExpressionType.FLOAT, "time", new ExpressionType[0]),
   IF(ExpressionType.FLOAT, "if", (new ParametersVariable()).first(ExpressionType.BOOL, ExpressionType.FLOAT).repeat(ExpressionType.BOOL, ExpressionType.FLOAT).last(ExpressionType.FLOAT)),
   NOT(12, ExpressionType.BOOL, "!", new ExpressionType[]{ExpressionType.BOOL}),
   AND(3, ExpressionType.BOOL, "&&", new ExpressionType[]{ExpressionType.BOOL, ExpressionType.BOOL}),
   OR(2, ExpressionType.BOOL, "||", new ExpressionType[]{ExpressionType.BOOL, ExpressionType.BOOL}),
   GREATER(8, ExpressionType.BOOL, ">", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
   GREATER_OR_EQUAL(8, ExpressionType.BOOL, ">=", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
   SMALLER(8, ExpressionType.BOOL, "<", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
   SMALLER_OR_EQUAL(8, ExpressionType.BOOL, "<=", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
   EQUAL(7, ExpressionType.BOOL, "==", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
   NOT_EQUAL(7, ExpressionType.BOOL, "!=", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
   BETWEEN(7, ExpressionType.BOOL, "between", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT}),
   EQUALS(7, ExpressionType.BOOL, "equals", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT}),
   IN(ExpressionType.BOOL, "in", (new ParametersVariable()).first(ExpressionType.FLOAT).repeat(ExpressionType.FLOAT).last(ExpressionType.FLOAT)),
   SMOOTH(ExpressionType.FLOAT, "smooth", (new ParametersVariable()).first(ExpressionType.FLOAT).repeat(ExpressionType.FLOAT).maxCount(4)),
   TRUE(ExpressionType.BOOL, "true", new ExpressionType[0]),
   FALSE(ExpressionType.BOOL, "false", new ExpressionType[0]),
   VEC2(ExpressionType.FLOAT_ARRAY, "vec2", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT}),
   VEC3(ExpressionType.FLOAT_ARRAY, "vec3", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT}),
   VEC4(ExpressionType.FLOAT_ARRAY, "vec4", new ExpressionType[]{ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT});

   private int precedence;
   private ExpressionType expressionType;
   private String name;
   private IParameters parameters;
   public static FunctionType[] VALUES = values();
   private static final Map<Integer, Float> mapSmooth = new HashMap();
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$net$optifine$expr$FunctionType;

   private FunctionType(ExpressionType expressionType, String name, ExpressionType[] parameterTypes) {
      this(0, expressionType, name, (ExpressionType[])parameterTypes);
   }

   private FunctionType(int precedence, ExpressionType expressionType, String name, ExpressionType[] parameterTypes) {
      this(precedence, expressionType, name, (IParameters)(new Parameters(parameterTypes)));
   }

   private FunctionType(ExpressionType expressionType, String name, IParameters parameters) {
      this(0, expressionType, name, (IParameters)parameters);
   }

   private FunctionType(int precedence, ExpressionType expressionType, String name, IParameters parameters) {
      this.precedence = precedence;
      this.expressionType = expressionType;
      this.name = name;
      this.parameters = parameters;
   }

   public String getName() {
      return this.name;
   }

   public int getPrecedence() {
      return this.precedence;
   }

   public ExpressionType getExpressionType() {
      return this.expressionType;
   }

   public IParameters getParameters() {
      return this.parameters;
   }

   public int getParameterCount(IExpression[] arguments) {
      return this.parameters.getParameterTypes(arguments).length;
   }

   public ExpressionType[] getParameterTypes(IExpression[] arguments) {
      return this.parameters.getParameterTypes(arguments);
   }

   public float evalFloat(IExpression[] args) {
      int k;
      switch($SWITCH_TABLE$net$optifine$expr$FunctionType()[this.ordinal()]) {
      case 1:
         return evalFloat(args, 0) + evalFloat(args, 1);
      case 2:
         return evalFloat(args, 0) - evalFloat(args, 1);
      case 3:
         return evalFloat(args, 0) * evalFloat(args, 1);
      case 4:
         return evalFloat(args, 0) / evalFloat(args, 1);
      case 5:
         float f = evalFloat(args, 0);
         float f1 = evalFloat(args, 1);
         return f - f1 * (float)((int)(f / f1));
      case 6:
         return -evalFloat(args, 0);
      case 7:
         return MathHelper.PI;
      case 8:
         return MathHelper.sin(evalFloat(args, 0));
      case 9:
         return MathHelper.cos(evalFloat(args, 0));
      case 10:
         return MathUtils.asin(evalFloat(args, 0));
      case 11:
         return MathUtils.acos(evalFloat(args, 0));
      case 12:
         return (float)Math.tan((double)evalFloat(args, 0));
      case 13:
         return (float)Math.atan((double)evalFloat(args, 0));
      case 14:
         return (float)MathHelper.atan2((double)evalFloat(args, 0), (double)evalFloat(args, 1));
      case 15:
         return MathUtils.toRad(evalFloat(args, 0));
      case 16:
         return MathUtils.toDeg(evalFloat(args, 0));
      case 17:
         return this.getMin(args);
      case 18:
         return this.getMax(args);
      case 19:
         return MathHelper.clamp_float(evalFloat(args, 0), evalFloat(args, 1), evalFloat(args, 2));
      case 20:
         return MathHelper.abs(evalFloat(args, 0));
      case 21:
         return (float)MathHelper.floor_float(evalFloat(args, 0));
      case 22:
         return (float)MathHelper.ceiling_float_int(evalFloat(args, 0));
      case 23:
         return (float)Math.exp((double)evalFloat(args, 0));
      case 24:
         return (float)MathHelper.func_181162_h((double)evalFloat(args, 0));
      case 25:
         return (float)Math.log((double)evalFloat(args, 0));
      case 26:
         return (float)Math.pow((double)evalFloat(args, 0), (double)evalFloat(args, 1));
      case 27:
         return (float)Math.random();
      case 28:
         return (float)Math.round(evalFloat(args, 0));
      case 29:
         return Math.signum(evalFloat(args, 0));
      case 30:
         return MathHelper.sqrt_float(evalFloat(args, 0));
      case 31:
         float f2 = evalFloat(args, 0);
         float f3 = evalFloat(args, 1);
         return f2 - f3 * (float)MathHelper.floor_float(f2 / f3);
      case 32:
         Minecraft minecraft = Minecraft.getMinecraft();
         World world = minecraft.theWorld;
         if (world == null) {
            return 0.0F;
         }

         return (float)(world.getTotalWorldTime() % 24000L) + Config.renderPartialTicks;
      case 33:
         int i = (args.length - 1) / 2;

         for(k = 0; k < i; ++k) {
            int l = k * 2;
            if (evalBool(args, l)) {
               return evalFloat(args, l + 1);
            }
         }

         return evalFloat(args, i * 2);
      case 34:
      case 35:
      case 36:
      case 37:
      case 38:
      case 39:
      case 40:
      case 41:
      case 42:
      case 43:
      case 44:
      case 45:
      default:
         Config.warn("Unknown function type: " + this);
         return 0.0F;
      case 46:
         k = (int)evalFloat(args, 0);
         float f4 = evalFloat(args, 1);
         float f5 = args.length > 2 ? evalFloat(args, 2) : 1.0F;
         float f6 = args.length > 3 ? evalFloat(args, 3) : f5;
         float f7 = Smoother.getSmoothValue(k, f4, f5, f6);
         return f7;
      }
   }

   private float getMin(IExpression[] exprs) {
      if (exprs.length == 2) {
         return Math.min(evalFloat(exprs, 0), evalFloat(exprs, 1));
      } else {
         float f = evalFloat(exprs, 0);

         for(int i = 1; i < exprs.length; ++i) {
            float f1 = evalFloat(exprs, i);
            if (f1 < f) {
               f = f1;
            }
         }

         return f;
      }
   }

   private float getMax(IExpression[] exprs) {
      if (exprs.length == 2) {
         return Math.max(evalFloat(exprs, 0), evalFloat(exprs, 1));
      } else {
         float f = evalFloat(exprs, 0);

         for(int i = 1; i < exprs.length; ++i) {
            float f1 = evalFloat(exprs, i);
            if (f1 > f) {
               f = f1;
            }
         }

         return f;
      }
   }

   private static float evalFloat(IExpression[] exprs, int index) {
      IExpressionFloat iexpressionfloat = (IExpressionFloat)exprs[index];
      float f = iexpressionfloat.eval();
      return f;
   }

   public boolean evalBool(IExpression[] args) {
      switch($SWITCH_TABLE$net$optifine$expr$FunctionType()[this.ordinal()]) {
      case 34:
         return !evalBool(args, 0);
      case 35:
         if (evalBool(args, 0) && evalBool(args, 1)) {
            return true;
         }

         return false;
      case 36:
         if (!evalBool(args, 0) && !evalBool(args, 1)) {
            return false;
         }

         return true;
      case 37:
         if (evalFloat(args, 0) > evalFloat(args, 1)) {
            return true;
         }

         return false;
      case 38:
         if (evalFloat(args, 0) >= evalFloat(args, 1)) {
            return true;
         }

         return false;
      case 39:
         if (evalFloat(args, 0) < evalFloat(args, 1)) {
            return true;
         }

         return false;
      case 40:
         if (evalFloat(args, 0) <= evalFloat(args, 1)) {
            return true;
         }

         return false;
      case 41:
         if (evalFloat(args, 0) == evalFloat(args, 1)) {
            return true;
         }

         return false;
      case 42:
         if (evalFloat(args, 0) != evalFloat(args, 1)) {
            return true;
         }

         return false;
      case 43:
         float f = evalFloat(args, 0);
         if (f >= evalFloat(args, 1) && f <= evalFloat(args, 2)) {
            return true;
         }

         return false;
      case 44:
         float f1 = evalFloat(args, 0) - evalFloat(args, 1);
         float f2 = evalFloat(args, 2);
         if (Math.abs(f1) <= f2) {
            return true;
         }

         return false;
      case 45:
         float f3 = evalFloat(args, 0);

         for(int i = 1; i < args.length; ++i) {
            float f4 = evalFloat(args, i);
            if (f3 == f4) {
               return true;
            }
         }

         return false;
      case 46:
      default:
         Config.warn("Unknown function type: " + this);
         return false;
      case 47:
         return true;
      case 48:
         return false;
      }
   }

   private static boolean evalBool(IExpression[] exprs, int index) {
      IExpressionBool iexpressionbool = (IExpressionBool)exprs[index];
      boolean flag = iexpressionbool.eval();
      return flag;
   }

   public float[] evalFloatArray(IExpression[] args) {
      switch($SWITCH_TABLE$net$optifine$expr$FunctionType()[this.ordinal()]) {
      case 49:
         return new float[]{evalFloat(args, 0), evalFloat(args, 1)};
      case 50:
         return new float[]{evalFloat(args, 0), evalFloat(args, 1), evalFloat(args, 2)};
      case 51:
         return new float[]{evalFloat(args, 0), evalFloat(args, 1), evalFloat(args, 2), evalFloat(args, 3)};
      default:
         Config.warn("Unknown function type: " + this);
         return null;
      }
   }

   public static FunctionType parse(String str) {
      for(int i = 0; i < VALUES.length; ++i) {
         FunctionType functiontype = VALUES[i];
         if (functiontype.getName().equals(str)) {
            return functiontype;
         }
      }

      return null;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$optifine$expr$FunctionType() {
      int[] var10000 = $SWITCH_TABLE$net$optifine$expr$FunctionType;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[values().length];

         try {
            var0[ABS.ordinal()] = 20;
         } catch (NoSuchFieldError var51) {
         }

         try {
            var0[ACOS.ordinal()] = 11;
         } catch (NoSuchFieldError var50) {
         }

         try {
            var0[AND.ordinal()] = 35;
         } catch (NoSuchFieldError var49) {
         }

         try {
            var0[ASIN.ordinal()] = 10;
         } catch (NoSuchFieldError var48) {
         }

         try {
            var0[ATAN.ordinal()] = 13;
         } catch (NoSuchFieldError var47) {
         }

         try {
            var0[ATAN2.ordinal()] = 14;
         } catch (NoSuchFieldError var46) {
         }

         try {
            var0[BETWEEN.ordinal()] = 43;
         } catch (NoSuchFieldError var45) {
         }

         try {
            var0[CEIL.ordinal()] = 22;
         } catch (NoSuchFieldError var44) {
         }

         try {
            var0[CLAMP.ordinal()] = 19;
         } catch (NoSuchFieldError var43) {
         }

         try {
            var0[COS.ordinal()] = 9;
         } catch (NoSuchFieldError var42) {
         }

         try {
            var0[DIV.ordinal()] = 4;
         } catch (NoSuchFieldError var41) {
         }

         try {
            var0[EQUAL.ordinal()] = 41;
         } catch (NoSuchFieldError var40) {
         }

         try {
            var0[EQUALS.ordinal()] = 44;
         } catch (NoSuchFieldError var39) {
         }

         try {
            var0[EXP.ordinal()] = 23;
         } catch (NoSuchFieldError var38) {
         }

         try {
            var0[FALSE.ordinal()] = 48;
         } catch (NoSuchFieldError var37) {
         }

         try {
            var0[FLOOR.ordinal()] = 21;
         } catch (NoSuchFieldError var36) {
         }

         try {
            var0[FMOD.ordinal()] = 31;
         } catch (NoSuchFieldError var35) {
         }

         try {
            var0[FRAC.ordinal()] = 24;
         } catch (NoSuchFieldError var34) {
         }

         try {
            var0[GREATER.ordinal()] = 37;
         } catch (NoSuchFieldError var33) {
         }

         try {
            var0[GREATER_OR_EQUAL.ordinal()] = 38;
         } catch (NoSuchFieldError var32) {
         }

         try {
            var0[IF.ordinal()] = 33;
         } catch (NoSuchFieldError var31) {
         }

         try {
            var0[IN.ordinal()] = 45;
         } catch (NoSuchFieldError var30) {
         }

         try {
            var0[LOG.ordinal()] = 25;
         } catch (NoSuchFieldError var29) {
         }

         try {
            var0[MAX.ordinal()] = 18;
         } catch (NoSuchFieldError var28) {
         }

         try {
            var0[MIN.ordinal()] = 17;
         } catch (NoSuchFieldError var27) {
         }

         try {
            var0[MINUS.ordinal()] = 2;
         } catch (NoSuchFieldError var26) {
         }

         try {
            var0[MOD.ordinal()] = 5;
         } catch (NoSuchFieldError var25) {
         }

         try {
            var0[MUL.ordinal()] = 3;
         } catch (NoSuchFieldError var24) {
         }

         try {
            var0[NEG.ordinal()] = 6;
         } catch (NoSuchFieldError var23) {
         }

         try {
            var0[NOT.ordinal()] = 34;
         } catch (NoSuchFieldError var22) {
         }

         try {
            var0[NOT_EQUAL.ordinal()] = 42;
         } catch (NoSuchFieldError var21) {
         }

         try {
            var0[OR.ordinal()] = 36;
         } catch (NoSuchFieldError var20) {
         }

         try {
            var0[PI.ordinal()] = 7;
         } catch (NoSuchFieldError var19) {
         }

         try {
            var0[PLUS.ordinal()] = 1;
         } catch (NoSuchFieldError var18) {
         }

         try {
            var0[POW.ordinal()] = 26;
         } catch (NoSuchFieldError var17) {
         }

         try {
            var0[RANDOM.ordinal()] = 27;
         } catch (NoSuchFieldError var16) {
         }

         try {
            var0[ROUND.ordinal()] = 28;
         } catch (NoSuchFieldError var15) {
         }

         try {
            var0[SIGNUM.ordinal()] = 29;
         } catch (NoSuchFieldError var14) {
         }

         try {
            var0[SIN.ordinal()] = 8;
         } catch (NoSuchFieldError var13) {
         }

         try {
            var0[SMALLER.ordinal()] = 39;
         } catch (NoSuchFieldError var12) {
         }

         try {
            var0[SMALLER_OR_EQUAL.ordinal()] = 40;
         } catch (NoSuchFieldError var11) {
         }

         try {
            var0[SMOOTH.ordinal()] = 46;
         } catch (NoSuchFieldError var10) {
         }

         try {
            var0[SQRT.ordinal()] = 30;
         } catch (NoSuchFieldError var9) {
         }

         try {
            var0[TAN.ordinal()] = 12;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var0[TIME.ordinal()] = 32;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[TODEG.ordinal()] = 16;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[TORAD.ordinal()] = 15;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[TRUE.ordinal()] = 47;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[VEC2.ordinal()] = 49;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[VEC3.ordinal()] = 50;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[VEC4.ordinal()] = 51;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$net$optifine$expr$FunctionType = var0;
         return var0;
      }
   }
}
