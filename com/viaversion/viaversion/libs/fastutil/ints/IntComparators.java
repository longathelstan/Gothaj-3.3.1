package com.viaversion.viaversion.libs.fastutil.ints;

import java.io.Serializable;
import java.util.Comparator;

public final class IntComparators {
   public static final IntComparator NATURAL_COMPARATOR = new IntComparators.NaturalImplicitComparator();
   public static final IntComparator OPPOSITE_COMPARATOR = new IntComparators.OppositeImplicitComparator();

   private IntComparators() {
   }

   public static IntComparator oppositeComparator(IntComparator c) {
      return (IntComparator)(c instanceof IntComparators.OppositeComparator ? ((IntComparators.OppositeComparator)c).comparator : new IntComparators.OppositeComparator(c));
   }

   public static IntComparator asIntComparator(final Comparator<? super Integer> c) {
      return c != null && !(c instanceof IntComparator) ? new IntComparator() {
         public int compare(int x, int y) {
            return c.compare(x, y);
         }

         public int compare(Integer x, Integer y) {
            return c.compare(x, y);
         }
      } : (IntComparator)c;
   }

   protected static class OppositeComparator implements IntComparator, Serializable {
      private static final long serialVersionUID = 1L;
      final IntComparator comparator;

      protected OppositeComparator(IntComparator c) {
         this.comparator = c;
      }

      public final int compare(int a, int b) {
         return this.comparator.compare(b, a);
      }

      public final IntComparator reversed() {
         return this.comparator;
      }
   }

   protected static class NaturalImplicitComparator implements IntComparator, Serializable {
      private static final long serialVersionUID = 1L;

      public final int compare(int a, int b) {
         return Integer.compare(a, b);
      }

      public IntComparator reversed() {
         return IntComparators.OPPOSITE_COMPARATOR;
      }

      private Object readResolve() {
         return IntComparators.NATURAL_COMPARATOR;
      }
   }

   protected static class OppositeImplicitComparator implements IntComparator, Serializable {
      private static final long serialVersionUID = 1L;

      public final int compare(int a, int b) {
         return -Integer.compare(a, b);
      }

      public IntComparator reversed() {
         return IntComparators.NATURAL_COMPARATOR;
      }

      private Object readResolve() {
         return IntComparators.OPPOSITE_COMPARATOR;
      }
   }
}
