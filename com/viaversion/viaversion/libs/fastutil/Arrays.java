package com.viaversion.viaversion.libs.fastutil;

import com.viaversion.viaversion.libs.fastutil.ints.IntComparator;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class Arrays {
   public static final int MAX_ARRAY_SIZE = 2147483639;
   private static final int MERGESORT_NO_REC = 16;
   private static final int QUICKSORT_NO_REC = 16;
   private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
   private static final int QUICKSORT_MEDIAN_OF_9 = 128;

   private Arrays() {
   }

   public static void ensureFromTo(int arrayLength, int from, int to) {
      assert arrayLength >= 0;

      if (from < 0) {
         throw new ArrayIndexOutOfBoundsException("Start index (" + from + ") is negative");
      } else if (from > to) {
         throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else if (to > arrayLength) {
         throw new ArrayIndexOutOfBoundsException("End index (" + to + ") is greater than array length (" + arrayLength + ")");
      }
   }

   public static void ensureOffsetLength(int arrayLength, int offset, int length) {
      assert arrayLength >= 0;

      if (offset < 0) {
         throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative");
      } else if (length < 0) {
         throw new IllegalArgumentException("Length (" + length + ") is negative");
      } else if (length > arrayLength - offset) {
         throw new ArrayIndexOutOfBoundsException("Last index (" + ((long)offset + (long)length) + ") is greater than array length (" + arrayLength + ")");
      }
   }

   private static void inPlaceMerge(int from, int mid, int to, IntComparator comp, Swapper swapper) {
      if (from < mid && mid < to) {
         if (to - from == 2) {
            if (comp.compare(mid, from) < 0) {
               swapper.swap(from, mid);
            }

         } else {
            int firstCut;
            int secondCut;
            if (mid - from > to - mid) {
               firstCut = from + (mid - from) / 2;
               secondCut = lowerBound(mid, to, firstCut, comp);
            } else {
               secondCut = mid + (to - mid) / 2;
               firstCut = upperBound(from, mid, secondCut, comp);
            }

            if (mid != firstCut && mid != secondCut) {
               int first1 = firstCut;
               int last1 = mid;

               label43:
               while(true) {
                  --last1;
                  if (first1 >= last1) {
                     first1 = mid;
                     last1 = secondCut;

                     while(true) {
                        --last1;
                        if (first1 >= last1) {
                           first1 = firstCut;
                           last1 = secondCut;

                           while(true) {
                              --last1;
                              if (first1 >= last1) {
                                 break label43;
                              }

                              swapper.swap(first1++, last1);
                           }
                        }

                        swapper.swap(first1++, last1);
                     }
                  }

                  swapper.swap(first1++, last1);
               }
            }

            mid = firstCut + (secondCut - mid);
            inPlaceMerge(from, firstCut, mid, comp, swapper);
            inPlaceMerge(mid, secondCut, to, comp, swapper);
         }
      }
   }

   private static int lowerBound(int from, int to, int pos, IntComparator comp) {
      int len = to - from;

      while(len > 0) {
         int half = len / 2;
         int middle = from + half;
         if (comp.compare(middle, pos) < 0) {
            from = middle + 1;
            len -= half + 1;
         } else {
            len = half;
         }
      }

      return from;
   }

   private static int upperBound(int from, int mid, int pos, IntComparator comp) {
      int len = mid - from;

      while(len > 0) {
         int half = len / 2;
         int middle = from + half;
         if (comp.compare(pos, middle) < 0) {
            len = half;
         } else {
            from = middle + 1;
            len -= half + 1;
         }
      }

      return from;
   }

   private static int med3(int a, int b, int c, IntComparator comp) {
      int ab = comp.compare(a, b);
      int ac = comp.compare(a, c);
      int bc = comp.compare(b, c);
      return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
   }

   private static ForkJoinPool getPool() {
      ForkJoinPool current = ForkJoinTask.getPool();
      return current == null ? ForkJoinPool.commonPool() : current;
   }

   public static void mergeSort(int from, int to, IntComparator c, Swapper swapper) {
      int length = to - from;
      int i;
      if (length >= 16) {
         i = from + to >>> 1;
         mergeSort(from, i, c, swapper);
         mergeSort(i, to, c, swapper);
         if (c.compare(i - 1, i) > 0) {
            inPlaceMerge(from, i, to, c, swapper);
         }
      } else {
         for(i = from; i < to; ++i) {
            for(int j = i; j > from && c.compare(j - 1, j) > 0; --j) {
               swapper.swap(j, j - 1);
            }
         }

      }
   }

   protected static void swap(Swapper swapper, int a, int b, int n) {
      for(int i = 0; i < n; ++b) {
         swapper.swap(a, b);
         ++i;
         ++a;
      }

   }

   public static void parallelQuickSort(int from, int to, IntComparator comp, Swapper swapper) {
      ForkJoinPool pool = getPool();
      if (to - from >= 8192 && pool.getParallelism() != 1) {
         pool.invoke(new Arrays.ForkJoinGenericQuickSort(from, to, comp, swapper));
      } else {
         quickSort(from, to, comp, swapper);
      }

   }

   public static void quickSort(int from, int to, IntComparator comp, Swapper swapper) {
      int len = to - from;
      int m;
      int j;
      if (len < 16) {
         for(m = from; m < to; ++m) {
            for(j = m; j > from && comp.compare(j - 1, j) > 0; --j) {
               swapper.swap(j, j - 1);
            }
         }

      } else {
         m = from + len / 2;
         j = from;
         int n = to - 1;
         int a;
         if (len > 128) {
            a = len / 8;
            j = med3(from, from + a, from + 2 * a, comp);
            m = med3(m - a, m, m + a, comp);
            n = med3(n - 2 * a, n - a, n, comp);
         }

         m = med3(j, m, n, comp);
         a = from;
         int b = from;
         int c = to - 1;
         int d = c;

         while(true) {
            int s;
            for(; b > c || (s = comp.compare(b, m)) > 0; swapper.swap(b++, c--)) {
               for(; c >= b && (s = comp.compare(c, m)) >= 0; --c) {
                  if (s == 0) {
                     if (c == m) {
                        m = d;
                     } else if (d == m) {
                        m = c;
                     }

                     swapper.swap(c, d--);
                  }
               }

               if (b > c) {
                  s = Math.min(a - from, b - a);
                  swap(swapper, from, b - s, s);
                  s = Math.min(d - c, to - d - 1);
                  swap(swapper, b, to - s, s);
                  if ((s = b - a) > 1) {
                     quickSort(from, from + s, comp, swapper);
                  }

                  if ((s = d - c) > 1) {
                     quickSort(to - s, to, comp, swapper);
                  }

                  return;
               }

               if (b == m) {
                  m = d;
               } else if (c == m) {
                  m = c;
               }
            }

            if (s == 0) {
               if (a == m) {
                  m = b;
               } else if (b == m) {
                  m = a;
               }

               swapper.swap(a++, b);
            }

            ++b;
         }
      }
   }

   protected static class ForkJoinGenericQuickSort extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final int from;
      private final int to;
      private final IntComparator comp;
      private final Swapper swapper;

      public ForkJoinGenericQuickSort(int from, int to, IntComparator comp, Swapper swapper) {
         this.from = from;
         this.to = to;
         this.comp = comp;
         this.swapper = swapper;
      }

      protected void compute() {
         int len = this.to - this.from;
         if (len < 8192) {
            Arrays.quickSort(this.from, this.to, this.comp, this.swapper);
         } else {
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = Arrays.med3(l, l + s, l + 2 * s, this.comp);
            m = Arrays.med3(m - s, m, m + s, this.comp);
            n = Arrays.med3(n - 2 * s, n - s, n, this.comp);
            m = Arrays.med3(l, m, n, this.comp);
            int a = this.from;
            int b = a;
            int c = this.to - 1;
            int d = c;

            while(true) {
               int t;
               for(; b > c || (t = this.comp.compare(b, m)) > 0; this.swapper.swap(b++, c--)) {
                  for(; c >= b && (t = this.comp.compare(c, m)) >= 0; --c) {
                     if (t == 0) {
                        if (c == m) {
                           m = d;
                        } else if (d == m) {
                           m = c;
                        }

                        this.swapper.swap(c, d--);
                     }
                  }

                  if (b > c) {
                     s = Math.min(a - this.from, b - a);
                     Arrays.swap(this.swapper, this.from, b - s, s);
                     s = Math.min(d - c, this.to - d - 1);
                     Arrays.swap(this.swapper, b, this.to - s, s);
                     s = b - a;
                     t = d - c;
                     if (s > 1 && t > 1) {
                        invokeAll(new Arrays.ForkJoinGenericQuickSort(this.from, this.from + s, this.comp, this.swapper), new Arrays.ForkJoinGenericQuickSort(this.to - t, this.to, this.comp, this.swapper));
                     } else if (s > 1) {
                        invokeAll(new ForkJoinTask[]{new Arrays.ForkJoinGenericQuickSort(this.from, this.from + s, this.comp, this.swapper)});
                     } else {
                        invokeAll(new ForkJoinTask[]{new Arrays.ForkJoinGenericQuickSort(this.to - t, this.to, this.comp, this.swapper)});
                     }

                     return;
                  }

                  if (b == m) {
                     m = d;
                  } else if (c == m) {
                     m = c;
                  }
               }

               if (t == 0) {
                  if (a == m) {
                     m = b;
                  } else if (b == m) {
                     m = a;
                  }

                  this.swapper.swap(a++, b);
               }

               ++b;
            }
         }
      }
   }
}
