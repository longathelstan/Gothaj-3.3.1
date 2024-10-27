package xyz.cucumber.base.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import xyz.cucumber.base.utils.render.shaders.Shaders;

public class GlassUtils {
   private Minecraft mc = Minecraft.getMinecraft();
   private Framebuffer framebuffer = new Framebuffer(1, 1, true);
   private ScaledResolution sr;
   private int programId;
   private float[] dir;
   private float offset;

   public GlassUtils() {
      this.sr = new ScaledResolution(this.mc);
      this.programId = Shaders.glassEffect.getProgramID();
      this.dir = new float[]{1.0F, 0.0F};
      this.offset = 3.0F;
   }

   public void pre() {
   }

   public void post() {
   }

   public void reset() {
   }
}
