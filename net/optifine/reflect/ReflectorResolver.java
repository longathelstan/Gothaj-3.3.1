package net.optifine.reflect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ReflectorResolver {
   private static final List<IResolvable> RESOLVABLES = Collections.synchronizedList(new ArrayList());
   private static boolean resolved = false;

   protected static void register(IResolvable resolvable) {
      if (!resolved) {
         RESOLVABLES.add(resolvable);
      } else {
         resolvable.resolve();
      }

   }

   public static void resolve() {
      if (!resolved) {
         Iterator var1 = RESOLVABLES.iterator();

         while(var1.hasNext()) {
            IResolvable iresolvable = (IResolvable)var1.next();
            iresolvable.resolve();
         }

         resolved = true;
      }

   }
}
