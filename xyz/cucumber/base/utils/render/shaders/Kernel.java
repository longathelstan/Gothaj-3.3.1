package xyz.cucumber.base.utils.render.shaders;

public class Kernel {
   public static float calculateGaussianValue(float x, float sigma) {
      double output = 1.0D / Math.sqrt(6.283185307179586D * (double)(sigma * sigma));
      return (float)(output * Math.exp((double)(-(x * x)) / (2.0D * (double)(sigma * sigma))));
   }
}
