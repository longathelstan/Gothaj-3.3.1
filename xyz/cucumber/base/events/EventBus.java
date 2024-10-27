package xyz.cucumber.base.events;

import god.buddy.aot.BCompiler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import xyz.cucumber.base.Client;
import xyz.cucumber.base.events.ext.EventBloom;
import xyz.cucumber.base.events.ext.EventBlur;
import xyz.cucumber.base.events.ext.EventRenderGui;
import xyz.cucumber.base.events.ext.EventTick;
import xyz.cucumber.base.interf.clientsettings.ext.Setting;
import xyz.cucumber.base.interf.clientsettings.ext.adds.BloomSetting;
import xyz.cucumber.base.interf.clientsettings.ext.adds.BlurSetting;
import xyz.cucumber.base.module.Mod;
import xyz.cucumber.base.utils.Timer;
import xyz.cucumber.base.utils.cfgs.ConfigFileUtils;
import xyz.cucumber.base.utils.render.BloomUtils;
import xyz.cucumber.base.utils.render.BlurUtils;
import xyz.cucumber.base.utils.render.GlassUtils;
import xyz.cucumber.base.utils.render.StencilUtils;

public class EventBus {
   public Timer cfgSaveTimer = new Timer();
   private CopyOnWriteArrayList<EventBus.Callable> handlers = new CopyOnWriteArrayList();
   public BloomUtils bloom = new BloomUtils();
   public GlassUtils glass = new GlassUtils();
   private Timer t = new Timer();

   @BCompiler(
      aot = BCompiler.AOT.AGGRESSIVE
   )
   public void register(Object obj) {
      Method[] var5;
      int var4 = (var5 = obj.getClass().getDeclaredMethods()).length;

      for(int var3 = 0; var3 < var4; ++var3) {
         Method method = var5[var3];
         if (method.isAnnotationPresent(EventListener.class)) {
            Class[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length <= 1) {
               this.handlers.add(new EventBus.Callable(obj, method));
            }
         }
      }

      this.handlers.sort(new EventBus.sort());
   }

   @BCompiler(
      aot = BCompiler.AOT.AGGRESSIVE
   )
   public void unregister(Object obj) {
      this.handlers.removeIf((o) -> {
         return o.getObject() == obj;
      });
   }

   public void call(Event e) {
      this.externalHandler(e);

      try {
         Iterator var3 = this.handlers.iterator();

         while(var3.hasNext()) {
            EventBus.Callable o = (EventBus.Callable)var3.next();
            if (o.getMethod().getParameterTypes()[0].isAssignableFrom(e.getClass()) && Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null) {
               o.getMethod().invoke(o.getObject(), e);
            }
         }
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var4) {
         var4.printStackTrace();
      }

   }

   public void externalHandler(Event e) {
      if (e instanceof EventTick && e.getType() == EventType.PRE && Minecraft.getMinecraft().currentScreen != null) {
         Iterator var3 = Client.INSTANCE.getModuleManager().getModules().iterator();

         while(var3.hasNext()) {
            Mod m = (Mod)var3.next();
            m.onSettingChange();
         }
      }

      if (e instanceof EventRenderGui && e.getType() == EventType.PRE) {
         EventRenderGui event = (EventRenderGui)e;
         if (this.cfgSaveTimer.hasTimeElapsed(15000.0D, true)) {
            Client.INSTANCE.getFileManager().save();
            if (ConfigFileUtils.file != null) {
               ConfigFileUtils.save(ConfigFileUtils.file, false);
            }
         }

         int bloomRadius = 1;
         int blurRadius = 1;
         Iterator var6 = Client.INSTANCE.getClientSettings().getSettings().iterator();

         while(var6.hasNext()) {
            Setting s = (Setting)var6.next();
            if (s instanceof BloomSetting) {
               BloomSetting setting = (BloomSetting)s;
               bloomRadius = (int)setting.radius.getValue();
            }

            if (s instanceof BlurSetting) {
               BlurSetting setting = (BlurSetting)s;
               blurRadius = (int)setting.radius.getValue();
            }
         }

         EventBlur blur = new EventBlur();
         Client.INSTANCE.getEventBus().call(blur);
         if (blur.isCancelled() && blurRadius > 1) {
            BlurSetting setting = (BlurSetting)Client.INSTANCE.getClientSettings().getSettingByName("Blur");
            blur.setType(EventType.POST);
            StencilUtils.initStencil();
            GL11.glEnable(2960);
            StencilUtils.bindWriteStencilBuffer();
            Client.INSTANCE.getEventBus().call(blur);
            StencilUtils.bindReadStencilBuffer(1);
            BlurUtils.renderBlur((float)setting.radius.getValue());
            StencilUtils.uninitStencilBuffer();
         }

         EventBloom bloom = new EventBloom();
         Client.INSTANCE.getEventBus().call(bloom);
         if (bloom.isCancelled() && bloomRadius > 1) {
            bloom.setType(EventType.POST);
            this.bloom.reset();
            this.bloom.pre();
            Client.INSTANCE.getEventBus().call(bloom);
            this.bloom.post();
         }

      }
   }

   public class Callable {
      private Object object;
      private Method method;

      public Callable(Object object, Method method) {
         this.object = object;
         this.method = method;
      }

      public Object getObject() {
         return this.object;
      }

      public Method getMethod() {
         return this.method;
      }
   }

   public class sort implements Comparator<EventBus.Callable> {
      public int compare(EventBus.Callable o1, EventBus.Callable o2) {
         if (((EventListener)o1.getMethod().getAnnotation(EventListener.class)).value().getValue() < ((EventListener)o2.getMethod().getAnnotation(EventListener.class)).value().getValue()) {
            return -1;
         } else {
            return ((EventListener)o1.getMethod().getAnnotation(EventListener.class)).value().getValue() == ((EventListener)o2.getMethod().getAnnotation(EventListener.class)).value().getValue() ? 0 : 1;
         }
      }
   }
}
