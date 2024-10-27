package xyz.cucumber.base.module.feat.other;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import org.lwjgl.opengl.GL11;
import xyz.cucumber.base.Client;
import xyz.cucumber.base.events.EventListener;
import xyz.cucumber.base.events.EventType;
import xyz.cucumber.base.events.ext.EventBloom;
import xyz.cucumber.base.events.ext.EventBlur;
import xyz.cucumber.base.events.ext.EventRenderGui;
import xyz.cucumber.base.module.ArrayPriority;
import xyz.cucumber.base.module.Category;
import xyz.cucumber.base.module.Mod;
import xyz.cucumber.base.module.ModuleInfo;
import xyz.cucumber.base.module.addons.Dragable;
import xyz.cucumber.base.module.settings.BooleanSettings;
import xyz.cucumber.base.module.settings.ColorSettings;
import xyz.cucumber.base.module.settings.ModeSettings;
import xyz.cucumber.base.module.settings.ModuleSettings;
import xyz.cucumber.base.module.settings.NumberSettings;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.cfgs.SettingsUtils;
import xyz.cucumber.base.utils.math.PositionHandler;
import xyz.cucumber.base.utils.position.PositionUtils;
import xyz.cucumber.base.utils.render.ColorUtils;
import xyz.cucumber.base.utils.render.Fonts;

@ModuleInfo(
   category = Category.OTHER,
   description = "Displays enabled modules",
   name = "Array List",
   priority = ArrayPriority.LOW
)
public class ArrayListModule extends Mod implements Dragable {
   public ArrayList<ArrayListModule.ArrayModule> mods = new ArrayList();
   private NumberSettings positionX = new NumberSettings("Position X", 30.0D, 0.0D, 1000.0D, 1.0D);
   private NumberSettings positionY = new NumberSettings("Position Y", 50.0D, 0.0D, 1000.0D, 1.0D);
   private BooleanSettings splitNames = new BooleanSettings("Split Names", true);
   private ModeSettings textStyle = new ModeSettings("Text Style", new String[]{"Normal", "Uppercase", "Lowercase"});
   private NumberSettings scale = new NumberSettings("Scale", 1.0D, 0.3D, 2.0D, 0.02D);
   private ModeSettings fonts = new ModeSettings("Fonts", SettingsUtils.getFonts());
   private ModeSettings priority = new ModeSettings("Priority", new String[]{"Low", "Medium", "High"});
   private ModeSettings direction = new ModeSettings("Direction", new String[]{"Left Up", "Right Up", "Left Down", "Right Down"});
   private NumberSettings spacing = new NumberSettings("Spacing", 10.0D, 8.0D, 20.0D, 0.2D);
   private NumberSettings correction = new NumberSettings("Center corection", 0.0D, -5.0D, 5.0D, 0.02D);
   private BooleanSettings rounded = new BooleanSettings("Rounded", true);
   private ColorSettings modColor = new ColorSettings("Text Color", "Static", -1, -1, 100);
   private BooleanSettings shadow = new BooleanSettings("Shadow", true);
   private ColorSettings shadowColor = new ColorSettings("Shadow color", "Static", -1, -1, 100);
   private BooleanSettings suffix = new BooleanSettings("Suffix", true);
   private ModeSettings suffixMode = new ModeSettings("Suffix Mode", new String[]{"None", "[]", "<>", "-"});
   private ColorSettings suffixColor = new ColorSettings("Suffix Color", "Static", -8947849, -1, 100);
   private BooleanSettings blur = new BooleanSettings("Blur", true);
   private BooleanSettings bloom = new BooleanSettings("Bloom", true);
   private ColorSettings bloomColor = new ColorSettings("Bloom Color", "Static", -16777216, -1, 100);
   private ColorSettings backgroundColor = new ColorSettings("Background Color", "Static", -16777216, -1, 100);
   private BooleanSettings outline = new BooleanSettings("Outline", true);
   private NumberSettings size = new NumberSettings("Outline size", 100.0D, 0.0D, 100.0D, 1.0D);
   private ColorSettings outlineColor = new ColorSettings("Outline Color", "Static", -16777216, -1, 100);
   private BooleanSettings outlineL = new BooleanSettings("Outline Left", true);
   private BooleanSettings outlineR = new BooleanSettings("Outline Right", true);
   private BooleanSettings outlineTop = new BooleanSettings("Outline Top", true);
   private BooleanSettings outlineBot = new BooleanSettings("Outline Bottom", true);
   private BooleanSettings outlineBetweenBot = new BooleanSettings("Outline Between Bottom", true);

   public ArrayListModule() {
      this.addSettings(new ModuleSettings[]{this.positionX, this.positionY, this.splitNames, this.textStyle, this.direction, this.spacing, this.correction, this.fonts, this.priority, this.scale, this.rounded, this.modColor, this.shadow, this.shadowColor, this.suffix, this.suffixMode, this.suffixColor, this.blur, this.bloom, this.bloomColor, this.backgroundColor, this.outline, this.size, this.outlineColor, this.outlineL, this.outlineR, this.outlineTop, this.outlineBot, this.outlineBetweenBot});
   }

   public void onEnable() {
      double[] pos = PositionHandler.getScaledPosition(this.positionX.getValue(), this.positionY.getValue());
      this.mods.clear();
      Iterator var3 = Client.INSTANCE.getModuleManager().getModules().iterator();

      while(var3.hasNext()) {
         Mod m = (Mod)var3.next();
         this.mods.add(new ArrayListModule.ArrayModule(m, new PositionUtils(pos[0], pos[1], 0.0D, 0.0D, 1.0F)));
      }

   }

   @EventListener
   public void onBloom(EventBloom e) {
      double[] pos = PositionHandler.getScaledPosition(this.positionX.getValue(), this.positionY.getValue());
      if (this.bloom.isEnabled()) {
         if (e.getType() == EventType.PRE) {
            e.setCancelled(true);
         } else {
            GL11.glPushMatrix();
            GL11.glTranslated(pos[0] - pos[0] * this.scale.getValue(), pos[1] - pos[1] * this.scale.getValue(), 1.0D);
            GL11.glScaled(this.scale.getValue(), this.scale.getValue(), 1.0D);
            int i = 0;
            double spacing = this.spacing.getValue();
            Iterator var7 = this.mods.iterator();

            while(true) {
               ArrayListModule.ArrayModule mod;
               label46:
               do {
                  while(var7.hasNext()) {
                     mod = (ArrayListModule.ArrayModule)var7.next();
                     String var8;
                     switch((var8 = this.priority.getMode().toLowerCase()).hashCode()) {
                     case -1078030475:
                        if (var8.equals("medium") && mod.module.getPriority() == ArrayPriority.LOW) {
                           break;
                        }
                        continue label46;
                     case 3202466:
                        if (var8.equals("high") && mod.module.getPriority() != ArrayPriority.HIGH) {
                           break;
                        }
                     default:
                        continue label46;
                     }
                  }

                  GL11.glPopMatrix();
                  return;
               } while(!mod.module.isEnabled() && mod.animation < 1.0D);

               int color = ColorUtils.getColor(this.bloomColor, (double)(System.nanoTime() / 1000000L), (double)(i * 10), 5.0D);
               this.renderBackGround(mod, color);
               ++i;
            }
         }
      }
   }

   @EventListener
   public void onBlur(EventBlur e) {
      double[] pos = PositionHandler.getScaledPosition(this.positionX.getValue(), this.positionY.getValue());
      if (this.blur.isEnabled()) {
         if (e.getType() == EventType.PRE) {
            e.setCancelled(true);
         } else {
            GL11.glPushMatrix();
            GL11.glTranslated(pos[0] - pos[0] * this.scale.getValue(), pos[1] - pos[1] * this.scale.getValue(), 1.0D);
            GL11.glScaled(this.scale.getValue(), this.scale.getValue(), 1.0D);
            Iterator var4 = this.mods.iterator();

            while(true) {
               ArrayListModule.ArrayModule mod;
               label46:
               do {
                  while(var4.hasNext()) {
                     mod = (ArrayListModule.ArrayModule)var4.next();
                     String var5;
                     switch((var5 = this.priority.getMode().toLowerCase()).hashCode()) {
                     case -1078030475:
                        if (var5.equals("medium") && mod.module.getPriority() == ArrayPriority.LOW) {
                           break;
                        }
                        continue label46;
                     case 3202466:
                        if (var5.equals("high") && mod.module.getPriority() != ArrayPriority.HIGH) {
                           break;
                        }
                     default:
                        continue label46;
                     }
                  }

                  GL11.glPopMatrix();
                  return;
               } while(!mod.module.isEnabled() && mod.animation < 1.0D);

               this.renderBackGround(mod, -1);
            }
         }
      }
   }

   @EventListener
   public void onRenderGui(EventRenderGui e) {
      double[] pos = PositionHandler.getScaledPosition(this.positionX.getValue(), this.positionY.getValue());
      this.info = this.direction.getMode();
      Collections.sort(this.mods, new ArrayListModule.sortBySize());
      GL11.glPushMatrix();
      GL11.glTranslated(pos[0] - pos[0] * this.scale.getValue(), pos[1] - pos[1] * this.scale.getValue(), 1.0D);
      GL11.glScaled(this.scale.getValue(), this.scale.getValue(), 1.0D);
      int i = 0;
      Iterator var5 = this.mods.iterator();

      while(true) {
         ArrayListModule.ArrayModule mod;
         String hor;
         String ver;
         String font;
         String suffix;
         String fullString;
         double spacing;
         double width;
         do {
            label77:
            while(true) {
               if (!var5.hasNext()) {
                  GL11.glPopMatrix();
                  return;
               }

               mod = (ArrayListModule.ArrayModule)var5.next();
               String var6;
               switch((var6 = this.priority.getMode().toLowerCase()).hashCode()) {
               case -1078030475:
                  if (var6.equals("medium") && mod.module.getPriority() == ArrayPriority.LOW) {
                     break;
                  }
                  break label77;
               case 3202466:
                  if (var6.equals("high") && mod.module.getPriority() != ArrayPriority.HIGH) {
                     break;
                  }
               default:
                  break label77;
               }
            }

            hor = this.direction.getMode().toLowerCase().split(" ")[0];
            ver = this.direction.getMode().toLowerCase().split(" ")[1];
            font = this.fonts.getMode().toLowerCase();
            suffix = this.getSuffix(mod);
            fullString = mod.module.getName(this.splitNames.isEnabled()) + suffix;
            if (this.textStyle.getMode().toLowerCase().equals("lowercase")) {
               fullString = fullString.toLowerCase();
            } else if (this.textStyle.getMode().toLowerCase().equals("uppercase")) {
               fullString = fullString.toUpperCase();
            }

            spacing = this.spacing.getValue();
            width = Fonts.getFont(font).getWidth(fullString) + 6.0D;
            mod.position.setWidth(width);
            mod.position.setHeight(spacing);
            if (mod.module.isEnabled()) {
               mod.animation = (mod.animation * 10.0D + width) / 11.0D;
               break;
            }

            mod.animation = mod.animation * 10.0D / 11.0D;
         } while(mod.animation < 1.0D);

         if (ver.equals("up")) {
            mod.position.setY((mod.position.getY() * 10.0D + pos[1] + spacing * (double)i) / 11.0D);
         } else {
            mod.position.setY((mod.position.getY() * 10.0D + (pos[1] - spacing * (double)i)) / 11.0D);
         }

         if (hor.equals("right")) {
            mod.position.setX(pos[0] - mod.animation);
         } else {
            mod.position.setX(pos[0] + mod.animation - width);
         }

         int color = ColorUtils.getColor(this.backgroundColor, (double)(System.nanoTime() / 1000000L), (double)(i * 10), 5.0D);
         this.renderBackGround(mod, color);
         this.renderOutlineForMod(mod);
         String modname = mod.module.getName(this.splitNames.isEnabled());
         String suffixname = suffix;
         if (this.textStyle.getMode().toLowerCase().equals("lowercase")) {
            suffixname = suffix.toLowerCase();
            modname = modname.toLowerCase();
         } else if (this.textStyle.getMode().toLowerCase().equals("uppercase")) {
            suffixname = suffix.toUpperCase();
            modname = modname.toUpperCase();
         }

         if (!mod.module.info.equals("") && mod.module.info != null) {
            if (hor.equals("right")) {
               if (this.shadow.isEnabled()) {
                  Fonts.getFont(font).drawString(modname, mod.position.getX() + 3.0D + 0.5D + 0.5D, mod.position.getY() + mod.position.getHeight() / 2.0D - (double)(Fonts.getFont(font).getHeight(fullString) / 2.0F) + this.correction.getValue() + 0.5D, ColorUtils.getColor(this.shadowColor, (double)(System.nanoTime() / 1000000L), (double)(i * 10), 5.0D));
                  if (!mod.module.info.equals("") || mod.module.info != null) {
                     Fonts.getFont(font).drawString(suffixname, mod.position.getX2() - Fonts.getFont(font).getWidth(suffixname) - 3.0D + 0.5D, mod.position.getY() + mod.position.getHeight() / 2.0D - (double)(Fonts.getFont(font).getHeight(fullString) / 2.0F) + this.correction.getValue() + 0.5D, ColorUtils.getColor(this.shadowColor, (double)(System.nanoTime() / 1000000L), (double)(i * 10), 5.0D));
                  }
               }

               Fonts.getFont(font).drawString(modname, mod.position.getX() + 3.0D + 0.5D, mod.position.getY() + mod.position.getHeight() / 2.0D - (double)(Fonts.getFont(font).getHeight(fullString) / 2.0F) + this.correction.getValue(), ColorUtils.getAlphaColor(ColorUtils.getColor(this.modColor, (double)(System.nanoTime() / 1000000L), (double)(i * 10), 5.0D), 100));
               if (!mod.module.info.equals("") || mod.module.info != null) {
                  Fonts.getFont(font).drawString(suffixname, mod.position.getX2() - Fonts.getFont(font).getWidth(suffixname) - 3.0D, mod.position.getY() + mod.position.getHeight() / 2.0D - (double)(Fonts.getFont(font).getHeight(fullString) / 2.0F) + this.correction.getValue(), ColorUtils.getAlphaColor(ColorUtils.getColor(this.suffixColor, (double)(System.nanoTime() / 1000000L), (double)(i * 10), 5.0D), 100));
               }
            } else {
               if (this.shadow.isEnabled()) {
                  Fonts.getFont(font).drawString(modname, mod.position.getX2() - Fonts.getFont(font).getWidth(modname) - 3.5D + 0.5D, mod.position.getY() + mod.position.getHeight() / 2.0D - (double)(Fonts.getFont(font).getHeight(fullString) / 2.0F) + this.correction.getValue() + 0.5D, ColorUtils.getColor(this.shadowColor, (double)(System.nanoTime() / 1000000L), (double)(i * 10), 5.0D));
                  if (!mod.module.info.equals("") || mod.module.info != null) {
                     Fonts.getFont(font).drawString(suffixname, mod.position.getX() + 3.0D + 0.5D + 0.5D, mod.position.getY() + mod.position.getHeight() / 2.0D - (double)(Fonts.getFont(font).getHeight(fullString) / 2.0F) + this.correction.getValue() + 0.5D, ColorUtils.getColor(this.shadowColor, (double)(System.nanoTime() / 1000000L), (double)(i * 10), 5.0D));
                  }
               }

               Fonts.getFont(font).drawString(modname, mod.position.getX2() - Fonts.getFont(font).getWidth(modname) - 3.5D, mod.position.getY() + mod.position.getHeight() / 2.0D - (double)(Fonts.getFont(font).getHeight(fullString) / 2.0F) + this.correction.getValue(), ColorUtils.getAlphaColor(ColorUtils.getColor(this.modColor, (double)(System.nanoTime() / 1000000L), (double)(i * 10), 5.0D), 100));
               if (!mod.module.info.equals("") || mod.module.info != null) {
                  Fonts.getFont(font).drawString(suffixname, mod.position.getX() + 3.0D + 0.5D, mod.position.getY() + mod.position.getHeight() / 2.0D - (double)(Fonts.getFont(font).getHeight(fullString) / 2.0F) + this.correction.getValue(), ColorUtils.getColor(this.suffixColor, (double)(System.nanoTime() / 1000000L), (double)(i * 10), 5.0D));
               }
            }
         } else {
            if (this.shadow.isEnabled()) {
               Fonts.getFont(font).drawString(modname, mod.position.getX() + mod.position.getWidth() / 2.0D - Fonts.getFont(font).getWidth(modname) / 2.0D + 0.5D + 0.5D, mod.position.getY() + mod.position.getHeight() / 2.0D - (double)(Fonts.getFont(font).getHeight(fullString) / 2.0F) + this.correction.getValue() + 0.5D, ColorUtils.getColor(this.shadowColor, (double)(System.nanoTime() / 1000000L), (double)(i * 10), 5.0D));
            }

            Fonts.getFont(font).drawString(modname, mod.position.getX() + mod.position.getWidth() / 2.0D - Fonts.getFont(font).getWidth(modname) / 2.0D + 0.5D, mod.position.getY() + mod.position.getHeight() / 2.0D - (double)(Fonts.getFont(font).getHeight(fullString) / 2.0F) + this.correction.getValue(), ColorUtils.getAlphaColor(ColorUtils.getColor(this.modColor, (double)(System.nanoTime() / 1000000L), (double)(i * 10), 5.0D), 100));
         }

         ++i;
      }
   }

   public void renderOutlineForMod(ArrayListModule.ArrayModule mod) {
      if (this.outline.isEnabled()) {
         String hor = this.direction.getMode().toLowerCase().split(" ")[0];
         String ver = this.direction.getMode().toLowerCase().split(" ")[1];
         int index = this.getAvalibeModules().indexOf(mod);
         int color = ColorUtils.getColor(this.outlineColor, (double)(System.nanoTime() / 1000000L), (double)(index * 10), 5.0D);
         double size;
         double var1;
         if (this.outlineL.isEnabled()) {
            size = mod.position.getHeight() * this.size.getValue() / 100.0D;
            var1 = (mod.position.getHeight() - size) / 2.0D;
            RenderUtils.drawLine(mod.position.getX(), mod.position.getY() + var1, mod.position.getX(), mod.position.getY2() - var1, color, (float)(2.0D * this.scale.getValue()));
         }

         if (this.outlineR.isEnabled()) {
            size = mod.position.getHeight() * this.size.getValue() / 100.0D;
            var1 = (mod.position.getHeight() - size) / 2.0D;
            RenderUtils.drawLine(mod.position.getX2(), mod.position.getY() + var1, mod.position.getX2(), mod.position.getY2() - var1, color, (float)(2.0D * this.scale.getValue()));
         }

         if (ver.equals("up")) {
            size = mod.position.getWidth() * this.size.getValue() / 100.0D;
            var1 = (mod.position.getWidth() - size) / 2.0D;
            if (index == 0 && this.outlineTop.isEnabled()) {
               RenderUtils.drawLine(mod.position.getX() + var1, mod.position.getY(), mod.position.getX2() - var1, mod.position.getY(), color, (float)(2.0D * this.scale.getValue()));
            }

            if (this.getAvalibeModules().size() - 1 == index && this.outlineBot.isEnabled()) {
               RenderUtils.drawLine(mod.position.getX() + var1, mod.position.getY2(), mod.position.getX2() - var1, mod.position.getY2(), color, (float)(2.0D * this.scale.getValue()));
            }
         } else {
            size = mod.position.getWidth() * this.size.getValue() / 100.0D;
            var1 = (mod.position.getWidth() - size) / 2.0D;
            if (index == 0 && this.outlineBot.isEnabled()) {
               RenderUtils.drawLine(mod.position.getX() + var1, mod.position.getY2(), mod.position.getX2() - var1, mod.position.getY2(), color, (float)(2.0D * this.scale.getValue()));
            }

            if (this.getAvalibeModules().size() - 1 == index && this.outlineTop.isEnabled()) {
               RenderUtils.drawLine(mod.position.getX() + var1, mod.position.getY(), mod.position.getX2() - var1, mod.position.getY(), color, (float)(2.0D * this.scale.getValue()));
            }
         }

         if (index != 0 && this.outlineBetweenBot.isEnabled()) {
            if (hor.equals("right")) {
               size = ((ArrayListModule.ArrayModule)this.getAvalibeModules().get(index - 1)).position.getWidth() - mod.position.getWidth();
               if (ver.equals("down")) {
                  RenderUtils.drawLine(mod.position.getX() - size, mod.position.getY2(), mod.position.getX(), mod.position.getY2(), color, (float)(2.0D * this.scale.getValue()));
               } else {
                  RenderUtils.drawLine(mod.position.getX() - size, mod.position.getY(), mod.position.getX(), mod.position.getY(), color, (float)(2.0D * this.scale.getValue()));
               }
            } else {
               size = ((ArrayListModule.ArrayModule)this.getAvalibeModules().get(index - 1)).position.getWidth() - mod.position.getWidth();
               if (ver.equals("down")) {
                  RenderUtils.drawLine(mod.position.getX2(), mod.position.getY2(), mod.position.getX2() + size, mod.position.getY2(), color, (float)(2.0D * this.scale.getValue()));
               } else {
                  RenderUtils.drawLine(mod.position.getX2(), mod.position.getY(), mod.position.getX2() + size, mod.position.getY(), color, (float)(2.0D * this.scale.getValue()));
               }
            }
         }

      }
   }

   public void renderBackGround(ArrayListModule.ArrayModule mod, int color) {
      int index = this.getAvalibeModules().indexOf(mod);
      String hor = this.direction.getMode().toLowerCase().split(" ")[0];
      String ver = this.direction.getMode().toLowerCase().split(" ")[1];
      if (!this.rounded.isEnabled()) {
         RenderUtils.drawRect(mod.position.getX(), mod.position.getY(), mod.position.getX2(), mod.position.getY2(), color);
      } else {
         double size;
         boolean leftT;
         boolean rightT;
         boolean leftB;
         boolean rightB;
         int futureindex;
         if (index == 0) {
            size = 2.0D;
            leftT = false;
            rightT = false;
            leftB = false;
            rightB = false;
            if (this.getAvalibeModules().size() == 1) {
               leftT = true;
               rightT = true;
               leftB = true;
               rightB = true;
            } else {
               futureindex = this.getAvalibeModules().indexOf(mod) + 1;
               if (ver.equals("up")) {
                  leftT = true;
                  rightT = true;
                  if (hor.equals("right")) {
                     leftB = true;
                  } else {
                     rightB = true;
                  }
               } else {
                  leftB = true;
                  rightB = true;
                  if (hor.equals("right")) {
                     leftT = true;
                  } else {
                     rightT = true;
                  }
               }

               if (size > Math.abs(((ArrayListModule.ArrayModule)this.getAvalibeModules().get(futureindex)).position.getWidth() - mod.position.getWidth())) {
                  size = Math.abs(((ArrayListModule.ArrayModule)this.getAvalibeModules().get(futureindex)).position.getWidth() - mod.position.getWidth());
               }
            }

            RenderUtils.drawRoundedRectWithCorners(mod.position.getX(), mod.position.getY(), mod.position.getX2(), mod.position.getY2(), color, size, leftT, rightT, leftB, rightB);
         } else if (index == this.getAvalibeModules().indexOf(this.getAvalibeModules().get(this.getAvalibeModules().size() - 1))) {
            boolean leftT = false;
            boolean rightT = false;
            leftT = false;
            rightT = false;
            double size = 2.0D;
            if (ver.equals("up")) {
               leftT = true;
               rightT = true;
            } else {
               leftT = true;
               rightT = true;
            }

            RenderUtils.drawRoundedRectWithCorners(mod.position.getX(), mod.position.getY(), mod.position.getX2(), mod.position.getY2(), color, size, leftT, rightT, leftT, rightT);
         } else {
            size = 2.0D;
            leftT = false;
            rightT = false;
            leftB = false;
            rightB = false;
            if (hor.equals("right")) {
               if (ver.equals("up")) {
                  leftB = true;
               } else {
                  leftT = true;
               }
            } else if (ver.equals("up")) {
               rightB = true;
            } else {
               rightT = true;
            }

            futureindex = this.getAvalibeModules().indexOf(mod) + 1;
            if (size > Math.abs(((ArrayListModule.ArrayModule)this.getAvalibeModules().get(futureindex)).position.getWidth() - mod.position.getWidth())) {
               size = Math.abs(((ArrayListModule.ArrayModule)this.getAvalibeModules().get(futureindex)).position.getWidth() - mod.position.getWidth());
            }

            RenderUtils.drawRoundedRectWithCorners(mod.position.getX(), mod.position.getY(), mod.position.getX2(), mod.position.getY2(), color, size, leftT, rightT, leftB, rightB);
         }
      }

   }

   public ArrayList<ArrayListModule.ArrayModule> getAvalibeModules() {
      ArrayList<ArrayListModule.ArrayModule> md = new ArrayList();
      Iterator var3 = this.mods.iterator();

      while(true) {
         ArrayListModule.ArrayModule mod;
         label38:
         do {
            while(var3.hasNext()) {
               mod = (ArrayListModule.ArrayModule)var3.next();
               String var4;
               switch((var4 = this.priority.getMode().toLowerCase()).hashCode()) {
               case -1078030475:
                  if (var4.equals("medium") && mod.module.getPriority() == ArrayPriority.LOW) {
                     break;
                  }
                  continue label38;
               case 3202466:
                  if (var4.equals("high") && mod.module.getPriority() != ArrayPriority.HIGH) {
                     break;
                  }
               default:
                  continue label38;
               }
            }

            return md;
         } while(!mod.module.isEnabled() && mod.animation < 1.0D);

         md.add(mod);
      }
   }

   public String getSuffix(ArrayListModule.ArrayModule mod) {
      String suffix = "";
      String hor = this.direction.getMode().toLowerCase().split(" ")[0];
      if (this.suffix.isEnabled() && mod.module.info != null && !mod.module.info.equals("")) {
         String var4;
         switch((var4 = this.suffixMode.getMode().toLowerCase()).hashCode()) {
         case 45:
            if (var4.equals("-")) {
               suffix = (hor.equals("right") ? " - " : "") + mod.module.info + (hor.equals("left") ? " - " : "");
            }
            break;
         case 1922:
            if (var4.equals("<>")) {
               suffix = (hor.equals("right") ? " " : "") + "<" + mod.module.info + ">" + (hor.equals("left") ? " " : "");
            }
            break;
         case 2914:
            if (var4.equals("[]")) {
               suffix = (hor.equals("right") ? " " : "") + "[" + mod.module.info + "]" + (hor.equals("left") ? " " : "");
            }
            break;
         case 3387192:
            if (var4.equals("none")) {
               suffix = (hor.equals("right") ? " " : "") + mod.module.info + (hor.equals("left") ? " " : "");
            }
         }
      }

      return suffix;
   }

   public PositionUtils getPosition() {
      double[] pos = PositionHandler.getScaledPosition(this.positionX.getValue(), this.positionY.getValue());
      String hor = this.direction.getMode().toLowerCase().split(" ")[0];
      String ver = this.direction.getMode().toLowerCase().split(" ")[1];
      double vertical = 0.0D;
      if (!ver.equals("up")) {
         vertical = this.getAvalibeModules().size() == 0 ? this.spacing.getValue() * this.scale.getValue() : (double)this.getAvalibeModules().size() * this.spacing.getValue() * this.scale.getValue();
      }

      double horizontal = 0.0D;
      if (hor.equals("right")) {
         horizontal = this.getAvalibeModules().size() == 0 ? 50.0D : ((ArrayListModule.ArrayModule)this.getAvalibeModules().get(0)).position.getWidth();
      }

      return new PositionUtils(pos[0] - horizontal, pos[1] + vertical, this.getAvalibeModules().size() == 0 ? 50.0D : ((ArrayListModule.ArrayModule)this.getAvalibeModules().get(0)).position.getWidth(), this.getAvalibeModules().size() == 0 ? this.spacing.getValue() * this.scale.getValue() : (double)this.getAvalibeModules().size() * this.spacing.getValue() * this.scale.getValue(), 1.0F);
   }

   public void setXYPosition(double x, double y) {
      String hor = this.direction.getMode().toLowerCase().split(" ")[0];
      String ver = this.direction.getMode().toLowerCase().split(" ")[1];
      double vertical = 0.0D;
      if (!ver.equals("up")) {
         vertical = this.getAvalibeModules().size() == 0 ? this.spacing.getValue() * this.scale.getValue() : (double)this.getAvalibeModules().size() * this.spacing.getValue() * this.scale.getValue();
      }

      double horizontal = 0.0D;
      if (hor.equals("right")) {
         horizontal = this.getAvalibeModules().size() == 0 ? 50.0D : ((ArrayListModule.ArrayModule)this.getAvalibeModules().get(0)).position.getWidth();
      }

      this.positionX.setValue(x + horizontal);
      this.positionY.setValue(y - vertical);
   }

   public class ArrayModule {
      public Mod module;
      public PositionUtils position;
      public double animation;

      public ArrayModule(Mod module, PositionUtils position) {
         this.module = module;
         this.position = position;
      }
   }

   public class sortBySize implements Comparator<ArrayListModule.ArrayModule> {
      public int compare(ArrayListModule.ArrayModule o1, ArrayListModule.ArrayModule o2) {
         String font = ArrayListModule.this.fonts.getMode().toLowerCase();
         String n1 = o1.module.getName(ArrayListModule.this.splitNames.isEnabled()) + ArrayListModule.this.getSuffix(o1);
         String n2 = o2.module.getName(ArrayListModule.this.splitNames.isEnabled()) + ArrayListModule.this.getSuffix(o2);
         if (ArrayListModule.this.textStyle.getMode().toLowerCase().equals("lowercase")) {
            n1 = n1.toLowerCase();
            n2 = n2.toLowerCase();
         } else if (ArrayListModule.this.textStyle.getMode().toLowerCase().equals("uppercase")) {
            n1 = n1.toUpperCase();
            n2 = n2.toUpperCase();
         }

         return Fonts.getFont(font).getWidth(n1) > Fonts.getFont(font).getWidth(n2) ? -1 : 1;
      }
   }
}
