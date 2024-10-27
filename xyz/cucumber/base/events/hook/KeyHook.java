package xyz.cucumber.base.events.hook;

import java.util.Iterator;
import xyz.cucumber.base.Client;
import xyz.cucumber.base.module.Mod;

public class KeyHook {
   public static void handle(int key) {
      Iterator var2 = Client.INSTANCE.getModuleManager().getModules().iterator();

      while(var2.hasNext()) {
         Mod m = (Mod)var2.next();
         if (m.getKey() == key) {
            m.toggle();
         }
      }

   }
}
