package com.viaversion.viaversion.libs.fastutil.ints;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.Comparator;

@FunctionalInterface
public interface IntComparator extends Comparator<Integer> {
   int compare(int var1, int var2);

   default IntComparator reversed() {
      return IntComparators.oppositeComparator(this);
   }

   /** @deprecated */
   @Deprecated
   default int compare(Integer ok1, Integer ok2) {
      return this.compare(ok1, ok2);
   }

   default IntComparator thenComparing(IntComparator second) {
      return (IntComparator)((Serializable)((k1, k2) -> {
         int comp = this.compare(k1, k2);
         return comp == 0 ? second.compare(k1, k2) : comp;
      }));
   }

   default Comparator<Integer> thenComparing(Comparator<? super Integer> second) {
      return (Comparator)(second instanceof IntComparator ? this.thenComparing((IntComparator)second) : super.thenComparing(second));
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      String var1 = lambda.getImplMethodName();
      byte var2 = -1;
      switch(var1.hashCode()) {
      case -1554871547:
         if (var1.equals("lambda$thenComparing$931d6fed$1")) {
            var2 = 0;
         }
      default:
         switch(var2) {
         case 0:
            if (lambda.getImplMethodKind() == 7 && lambda.getFunctionalInterfaceClass().equals("com/viaversion/viaversion/libs/fastutil/ints/IntComparator") && lambda.getFunctionalInterfaceMethodName().equals("compare") && lambda.getFunctionalInterfaceMethodSignature().equals("(II)I") && lambda.getImplClass().equals("com/viaversion/viaversion/libs/fastutil/ints/IntComparator") && lambda.getImplMethodSignature().equals("(Lit/unimi/dsi/fastutil/ints/IntComparator;II)I")) {
               IntComparator var10000 = (IntComparator)lambda.getCapturedArg(0);
               return (k1, k2) -> {
                  int comp = this.compare(k1, k2);
                  return comp == 0 ? second.compare(k1, k2) : comp;
               };
            }
         default:
            throw new IllegalArgumentException("Invalid lambda deserialization");
         }
      }
   }
}
