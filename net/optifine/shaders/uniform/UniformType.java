package net.optifine.shaders.uniform;

import net.optifine.expr.ExpressionType;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionBool;
import net.optifine.expr.IExpressionFloat;
import net.optifine.expr.IExpressionFloatArray;

public enum UniformType {
   BOOL,
   INT,
   FLOAT,
   VEC2,
   VEC3,
   VEC4;

   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$net$optifine$shaders$uniform$UniformType;

   public ShaderUniformBase makeShaderUniform(String name) {
      switch($SWITCH_TABLE$net$optifine$shaders$uniform$UniformType()[this.ordinal()]) {
      case 1:
         return new ShaderUniform1i(name);
      case 2:
         return new ShaderUniform1i(name);
      case 3:
         return new ShaderUniform1f(name);
      case 4:
         return new ShaderUniform2f(name);
      case 5:
         return new ShaderUniform3f(name);
      case 6:
         return new ShaderUniform4f(name);
      default:
         throw new RuntimeException("Unknown uniform type: " + this);
      }
   }

   public void updateUniform(IExpression expression, ShaderUniformBase uniform) {
      switch($SWITCH_TABLE$net$optifine$shaders$uniform$UniformType()[this.ordinal()]) {
      case 1:
         this.updateUniformBool((IExpressionBool)expression, (ShaderUniform1i)uniform);
         return;
      case 2:
         this.updateUniformInt((IExpressionFloat)expression, (ShaderUniform1i)uniform);
         return;
      case 3:
         this.updateUniformFloat((IExpressionFloat)expression, (ShaderUniform1f)uniform);
         return;
      case 4:
         this.updateUniformFloat2((IExpressionFloatArray)expression, (ShaderUniform2f)uniform);
         return;
      case 5:
         this.updateUniformFloat3((IExpressionFloatArray)expression, (ShaderUniform3f)uniform);
         return;
      case 6:
         this.updateUniformFloat4((IExpressionFloatArray)expression, (ShaderUniform4f)uniform);
         return;
      default:
         throw new RuntimeException("Unknown uniform type: " + this);
      }
   }

   private void updateUniformBool(IExpressionBool expression, ShaderUniform1i uniform) {
      boolean flag = expression.eval();
      int i = flag ? 1 : 0;
      uniform.setValue(i);
   }

   private void updateUniformInt(IExpressionFloat expression, ShaderUniform1i uniform) {
      int i = (int)expression.eval();
      uniform.setValue(i);
   }

   private void updateUniformFloat(IExpressionFloat expression, ShaderUniform1f uniform) {
      float f = expression.eval();
      uniform.setValue(f);
   }

   private void updateUniformFloat2(IExpressionFloatArray expression, ShaderUniform2f uniform) {
      float[] afloat = expression.eval();
      if (afloat.length != 2) {
         throw new RuntimeException("Value length is not 2, length: " + afloat.length);
      } else {
         uniform.setValue(afloat[0], afloat[1]);
      }
   }

   private void updateUniformFloat3(IExpressionFloatArray expression, ShaderUniform3f uniform) {
      float[] afloat = expression.eval();
      if (afloat.length != 3) {
         throw new RuntimeException("Value length is not 3, length: " + afloat.length);
      } else {
         uniform.setValue(afloat[0], afloat[1], afloat[2]);
      }
   }

   private void updateUniformFloat4(IExpressionFloatArray expression, ShaderUniform4f uniform) {
      float[] afloat = expression.eval();
      if (afloat.length != 4) {
         throw new RuntimeException("Value length is not 4, length: " + afloat.length);
      } else {
         uniform.setValue(afloat[0], afloat[1], afloat[2], afloat[3]);
      }
   }

   public boolean matchesExpressionType(ExpressionType expressionType) {
      switch($SWITCH_TABLE$net$optifine$shaders$uniform$UniformType()[this.ordinal()]) {
      case 1:
         if (expressionType == ExpressionType.BOOL) {
            return true;
         }

         return false;
      case 2:
         if (expressionType == ExpressionType.FLOAT) {
            return true;
         }

         return false;
      case 3:
         if (expressionType == ExpressionType.FLOAT) {
            return true;
         }

         return false;
      case 4:
      case 5:
      case 6:
         if (expressionType == ExpressionType.FLOAT_ARRAY) {
            return true;
         }

         return false;
      default:
         throw new RuntimeException("Unknown uniform type: " + this);
      }
   }

   public static UniformType parse(String type) {
      UniformType[] auniformtype = values();

      for(int i = 0; i < auniformtype.length; ++i) {
         UniformType uniformtype = auniformtype[i];
         if (uniformtype.name().toLowerCase().equals(type)) {
            return uniformtype;
         }
      }

      return null;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$optifine$shaders$uniform$UniformType() {
      int[] var10000 = $SWITCH_TABLE$net$optifine$shaders$uniform$UniformType;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[values().length];

         try {
            var0[BOOL.ordinal()] = 1;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[FLOAT.ordinal()] = 3;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[INT.ordinal()] = 2;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[VEC2.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[VEC3.ordinal()] = 5;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[VEC4.ordinal()] = 6;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$net$optifine$shaders$uniform$UniformType = var0;
         return var0;
      }
   }
}
