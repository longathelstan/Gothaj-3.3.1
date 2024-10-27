package net.optifine.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.client.settings.KeyBinding;

public class KeyUtils {
   public static void fixKeyConflicts(KeyBinding[] keys, KeyBinding[] keysPrio) {
      Set<Integer> set = new HashSet();

      KeyBinding keybinding1;
      for(int i = 0; i < keysPrio.length; ++i) {
         keybinding1 = keysPrio[i];
         set.add(keybinding1.getKeyCode());
      }

      Set<KeyBinding> set1 = new HashSet(Arrays.asList(keys));
      set1.removeAll(Arrays.asList(keysPrio));
      Iterator var5 = set1.iterator();

      while(var5.hasNext()) {
         keybinding1 = (KeyBinding)var5.next();
         Integer integer = keybinding1.getKeyCode();
         if (set.contains(integer)) {
            keybinding1.setKeyCode(0);
         }
      }

   }
}
