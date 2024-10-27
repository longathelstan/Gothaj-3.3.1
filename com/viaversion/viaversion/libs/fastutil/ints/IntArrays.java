package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.Arrays;
import com.viaversion.viaversion.libs.fastutil.Hash;
import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

public final class IntArrays {
   public static final int[] EMPTY_ARRAY = new int[0];
   public static final int[] DEFAULT_EMPTY_ARRAY = new int[0];
   private static final int QUICKSORT_NO_REC = 16;
   private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
   private static final int QUICKSORT_MEDIAN_OF_9 = 128;
   private static final int MERGESORT_NO_REC = 16;
   private static final int DIGIT_BITS = 8;
   private static final int DIGIT_MASK = 255;
   private static final int DIGITS_PER_ELEMENT = 4;
   private static final int RADIXSORT_NO_REC = 1024;
   private static final int RADIXSORT_NO_REC_SMALL = 64;
   private static final int PARALLEL_RADIXSORT_NO_FORK = 1024;
   static final int RADIX_SORT_MIN_THRESHOLD = 2000;
   protected static final IntArrays.Segment POISON_PILL = new IntArrays.Segment(-1, -1, -1);
   public static final Hash.Strategy<int[]> HASH_STRATEGY = new IntArrays.ArrayHashStrategy();

   private IntArrays() {
   }

   public static int[] forceCapacity(int[] array, int length, int preserve) {
      int[] t = new int[length];
      System.arraycopy(array, 0, t, 0, preserve);
      return t;
   }

   public static int[] ensureCapacity(int[] array, int length) {
      return ensureCapacity(array, length, array.length);
   }

   public static int[] ensureCapacity(int[] array, int length, int preserve) {
      return length > array.length ? forceCapacity(array, length, preserve) : array;
   }

   public static int[] grow(int[] array, int length) {
      return grow(array, length, array.length);
   }

   public static int[] grow(int[] array, int length, int preserve) {
      if (length > array.length) {
         int newLength = (int)Math.max(Math.min((long)array.length + (long)(array.length >> 1), 2147483639L), (long)length);
         int[] t = new int[newLength];
         System.arraycopy(array, 0, t, 0, preserve);
         return t;
      } else {
         return array;
      }
   }

   public static int[] trim(int[] array, int length) {
      if (length >= array.length) {
         return array;
      } else {
         int[] t = length == 0 ? EMPTY_ARRAY : new int[length];
         System.arraycopy(array, 0, t, 0, length);
         return t;
      }
   }

   public static int[] setLength(int[] array, int length) {
      if (length == array.length) {
         return array;
      } else {
         return length < array.length ? trim(array, length) : ensureCapacity(array, length);
      }
   }

   public static int[] copy(int[] array, int offset, int length) {
      ensureOffsetLength(array, offset, length);
      int[] a = length == 0 ? EMPTY_ARRAY : new int[length];
      System.arraycopy(array, offset, a, 0, length);
      return a;
   }

   public static int[] copy(int[] array) {
      return (int[])array.clone();
   }

   /** @deprecated */
   @Deprecated
   public static void fill(int[] array, int value) {
      for(int i = array.length; i-- != 0; array[i] = value) {
      }

   }

   /** @deprecated */
   @Deprecated
   public static void fill(int[] array, int from, int to, int value) {
      ensureFromTo(array, from, to);
      if (from == 0) {
         while(to-- != 0) {
            array[to] = value;
         }
      } else {
         for(int i = from; i < to; ++i) {
            array[i] = value;
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public static boolean equals(int[] a1, int[] a2) {
      int i = a1.length;
      if (i != a2.length) {
         return false;
      } else {
         do {
            if (i-- == 0) {
               return true;
            }
         } while(a1[i] == a2[i]);

         return false;
      }
   }

   public static void ensureFromTo(int[] a, int from, int to) {
      Arrays.ensureFromTo(a.length, from, to);
   }

   public static void ensureOffsetLength(int[] a, int offset, int length) {
      Arrays.ensureOffsetLength(a.length, offset, length);
   }

   public static void ensureSameLength(int[] a, int[] b) {
      if (a.length != b.length) {
         throw new IllegalArgumentException("Array size mismatch: " + a.length + " != " + b.length);
      }
   }

   private static ForkJoinPool getPool() {
      ForkJoinPool current = ForkJoinTask.getPool();
      return current == null ? ForkJoinPool.commonPool() : current;
   }

   public static void swap(int[] x, int a, int b) {
      int t = x[a];
      x[a] = x[b];
      x[b] = t;
   }

   public static void swap(int[] x, int a, int b, int n) {
      for(int i = 0; i < n; ++b) {
         swap(x, a, b);
         ++i;
         ++a;
      }

   }

   private static int med3(int[] x, int a, int b, int c, IntComparator comp) {
      int ab = comp.compare(x[a], x[b]);
      int ac = comp.compare(x[a], x[c]);
      int bc = comp.compare(x[b], x[c]);
      return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
   }

   private static void selectionSort(int[] a, int from, int to, IntComparator comp) {
      for(int i = from; i < to - 1; ++i) {
         int m = i;

         int u;
         for(u = i + 1; u < to; ++u) {
            if (comp.compare(a[u], a[m]) < 0) {
               m = u;
            }
         }

         if (m != i) {
            u = a[i];
            a[i] = a[m];
            a[m] = u;
         }
      }

   }

   private static void insertionSort(int[] a, int from, int to, IntComparator comp) {
      int i = from;

      while(true) {
         ++i;
         if (i >= to) {
            return;
         }

         int t = a[i];
         int j = i;

         for(int u = a[i - 1]; comp.compare(t, u) < 0; u = a[j - 1]) {
            a[j] = u;
            if (from == j - 1) {
               --j;
               break;
            }

            --j;
         }

         a[j] = t;
      }
   }

   public static void quickSort(int[] x, int from, int to, IntComparator comp) {
      int len = to - from;
      if (len < 16) {
         selectionSort(x, from, to, comp);
      } else {
         int m = from + len / 2;
         int l = from;
         int n = to - 1;
         int v;
         if (len > 128) {
            v = len / 8;
            l = med3(x, from, from + v, from + 2 * v, comp);
            m = med3(x, m - v, m, m + v, comp);
            n = med3(x, n - 2 * v, n - v, n, comp);
         }

         m = med3(x, l, m, n, comp);
         v = x[m];
         int a = from;
         int b = from;
         int c = to - 1;
         int d = c;

         while(true) {
            int s;
            while(b > c || (s = comp.compare(x[b], v)) > 0) {
               for(; c >= b && (s = comp.compare(x[c], v)) >= 0; --c) {
                  if (s == 0) {
                     swap(x, c, d--);
                  }
               }

               if (b > c) {
                  s = Math.min(a - from, b - a);
                  swap(x, from, b - s, s);
                  s = Math.min(d - c, to - d - 1);
                  swap(x, b, to - s, s);
                  if ((s = b - a) > 1) {
                     quickSort(x, from, from + s, comp);
                  }

                  if ((s = d - c) > 1) {
                     quickSort(x, to - s, to, comp);
                  }

                  return;
               }

               swap(x, b++, c--);
            }

            if (s == 0) {
               swap(x, a++, b);
            }

            ++b;
         }
      }
   }

   public static void quickSort(int[] x, IntComparator comp) {
      quickSort(x, 0, x.length, comp);
   }

   public static void parallelQuickSort(int[] x, int from, int to, IntComparator comp) {
      ForkJoinPool pool = getPool();
      if (to - from >= 8192 && pool.getParallelism() != 1) {
         pool.invoke(new IntArrays.ForkJoinQuickSortComp(x, from, to, comp));
      } else {
         quickSort(x, from, to, comp);
      }

   }

   public static void parallelQuickSort(int[] x, IntComparator comp) {
      parallelQuickSort(x, 0, x.length, comp);
   }

   private static int med3(int[] x, int a, int b, int c) {
      int ab = Integer.compare(x[a], x[b]);
      int ac = Integer.compare(x[a], x[c]);
      int bc = Integer.compare(x[b], x[c]);
      return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
   }

   private static void selectionSort(int[] a, int from, int to) {
      for(int i = from; i < to - 1; ++i) {
         int m = i;

         int u;
         for(u = i + 1; u < to; ++u) {
            if (a[u] < a[m]) {
               m = u;
            }
         }

         if (m != i) {
            u = a[i];
            a[i] = a[m];
            a[m] = u;
         }
      }

   }

   private static void insertionSort(int[] a, int from, int to) {
      int i = from;

      while(true) {
         ++i;
         if (i >= to) {
            return;
         }

         int t = a[i];
         int j = i;

         for(int u = a[i - 1]; t < u; u = a[j - 1]) {
            a[j] = u;
            if (from == j - 1) {
               --j;
               break;
            }

            --j;
         }

         a[j] = t;
      }
   }

   public static void quickSort(int[] x, int from, int to) {
      int len = to - from;
      if (len < 16) {
         selectionSort(x, from, to);
      } else {
         int m = from + len / 2;
         int l = from;
         int n = to - 1;
         int v;
         if (len > 128) {
            v = len / 8;
            l = med3(x, from, from + v, from + 2 * v);
            m = med3(x, m - v, m, m + v);
            n = med3(x, n - 2 * v, n - v, n);
         }

         m = med3(x, l, m, n);
         v = x[m];
         int a = from;
         int b = from;
         int c = to - 1;
         int d = c;

         while(true) {
            int s;
            while(b > c || (s = Integer.compare(x[b], v)) > 0) {
               for(; c >= b && (s = Integer.compare(x[c], v)) >= 0; --c) {
                  if (s == 0) {
                     swap(x, c, d--);
                  }
               }

               if (b > c) {
                  s = Math.min(a - from, b - a);
                  swap(x, from, b - s, s);
                  s = Math.min(d - c, to - d - 1);
                  swap(x, b, to - s, s);
                  if ((s = b - a) > 1) {
                     quickSort(x, from, from + s);
                  }

                  if ((s = d - c) > 1) {
                     quickSort(x, to - s, to);
                  }

                  return;
               }

               swap(x, b++, c--);
            }

            if (s == 0) {
               swap(x, a++, b);
            }

            ++b;
         }
      }
   }

   public static void quickSort(int[] x) {
      quickSort(x, 0, x.length);
   }

   public static void parallelQuickSort(int[] x, int from, int to) {
      ForkJoinPool pool = getPool();
      if (to - from >= 8192 && pool.getParallelism() != 1) {
         pool.invoke(new IntArrays.ForkJoinQuickSort(x, from, to));
      } else {
         quickSort(x, from, to);
      }

   }

   public static void parallelQuickSort(int[] x) {
      parallelQuickSort(x, 0, x.length);
   }

   private static int med3Indirect(int[] perm, int[] x, int a, int b, int c) {
      int aa = x[perm[a]];
      int bb = x[perm[b]];
      int cc = x[perm[c]];
      int ab = Integer.compare(aa, bb);
      int ac = Integer.compare(aa, cc);
      int bc = Integer.compare(bb, cc);
      return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
   }

   private static void insertionSortIndirect(int[] perm, int[] a, int from, int to) {
      int i = from;

      while(true) {
         ++i;
         if (i >= to) {
            return;
         }

         int t = perm[i];
         int j = i;

         for(int u = perm[i - 1]; a[t] < a[u]; u = perm[j - 1]) {
            perm[j] = u;
            if (from == j - 1) {
               --j;
               break;
            }

            --j;
         }

         perm[j] = t;
      }
   }

   public static void quickSortIndirect(int[] perm, int[] x, int from, int to) {
      int len = to - from;
      if (len < 16) {
         insertionSortIndirect(perm, x, from, to);
      } else {
         int m = from + len / 2;
         int l = from;
         int n = to - 1;
         int v;
         if (len > 128) {
            v = len / 8;
            l = med3Indirect(perm, x, from, from + v, from + 2 * v);
            m = med3Indirect(perm, x, m - v, m, m + v);
            n = med3Indirect(perm, x, n - 2 * v, n - v, n);
         }

         m = med3Indirect(perm, x, l, m, n);
         v = x[perm[m]];
         int a = from;
         int b = from;
         int c = to - 1;
         int d = c;

         while(true) {
            int s;
            while(b > c || (s = Integer.compare(x[perm[b]], v)) > 0) {
               for(; c >= b && (s = Integer.compare(x[perm[c]], v)) >= 0; --c) {
                  if (s == 0) {
                     swap(perm, c, d--);
                  }
               }

               if (b > c) {
                  s = Math.min(a - from, b - a);
                  swap(perm, from, b - s, s);
                  s = Math.min(d - c, to - d - 1);
                  swap(perm, b, to - s, s);
                  if ((s = b - a) > 1) {
                     quickSortIndirect(perm, x, from, from + s);
                  }

                  if ((s = d - c) > 1) {
                     quickSortIndirect(perm, x, to - s, to);
                  }

                  return;
               }

               swap(perm, b++, c--);
            }

            if (s == 0) {
               swap(perm, a++, b);
            }

            ++b;
         }
      }
   }

   public static void quickSortIndirect(int[] perm, int[] x) {
      quickSortIndirect(perm, x, 0, x.length);
   }

   public static void parallelQuickSortIndirect(int[] perm, int[] x, int from, int to) {
      ForkJoinPool pool = getPool();
      if (to - from >= 8192 && pool.getParallelism() != 1) {
         pool.invoke(new IntArrays.ForkJoinQuickSortIndirect(perm, x, from, to));
      } else {
         quickSortIndirect(perm, x, from, to);
      }

   }

   public static void parallelQuickSortIndirect(int[] perm, int[] x) {
      parallelQuickSortIndirect(perm, x, 0, x.length);
   }

   public static void stabilize(int[] perm, int[] x, int from, int to) {
      int curr = from;

      for(int i = from + 1; i < to; ++i) {
         if (x[perm[i]] != x[perm[curr]]) {
            if (i - curr > 1) {
               parallelQuickSort(perm, curr, i);
            }

            curr = i;
         }
      }

      if (to - curr > 1) {
         parallelQuickSort(perm, curr, to);
      }

   }

   public static void stabilize(int[] perm, int[] x) {
      stabilize(perm, x, 0, perm.length);
   }

   private static int med3(int[] x, int[] y, int a, int b, int c) {
      int t;
      int ab = (t = Integer.compare(x[a], x[b])) == 0 ? Integer.compare(y[a], y[b]) : t;
      int ac = (t = Integer.compare(x[a], x[c])) == 0 ? Integer.compare(y[a], y[c]) : t;
      int bc = (t = Integer.compare(x[b], x[c])) == 0 ? Integer.compare(y[b], y[c]) : t;
      return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
   }

   private static void swap(int[] x, int[] y, int a, int b) {
      int t = x[a];
      int u = y[a];
      x[a] = x[b];
      y[a] = y[b];
      x[b] = t;
      y[b] = u;
   }

   private static void swap(int[] x, int[] y, int a, int b, int n) {
      for(int i = 0; i < n; ++b) {
         swap(x, y, a, b);
         ++i;
         ++a;
      }

   }

   private static void selectionSort(int[] a, int[] b, int from, int to) {
      for(int i = from; i < to - 1; ++i) {
         int m = i;

         int t;
         for(t = i + 1; t < to; ++t) {
            int u;
            if ((u = Integer.compare(a[t], a[m])) < 0 || u == 0 && b[t] < b[m]) {
               m = t;
            }
         }

         if (m != i) {
            t = a[i];
            a[i] = a[m];
            a[m] = t;
            t = b[i];
            b[i] = b[m];
            b[m] = t;
         }
      }

   }

   public static void quickSort(int[] x, int[] y, int from, int to) {
      int len = to - from;
      if (len < 16) {
         selectionSort(x, y, from, to);
      } else {
         int m = from + len / 2;
         int l = from;
         int n = to - 1;
         int v;
         if (len > 128) {
            v = len / 8;
            l = med3(x, y, from, from + v, from + 2 * v);
            m = med3(x, y, m - v, m, m + v);
            n = med3(x, y, n - 2 * v, n - v, n);
         }

         m = med3(x, y, l, m, n);
         v = x[m];
         int w = y[m];
         int a = from;
         int b = from;
         int c = to - 1;
         int d = c;

         while(true) {
            int s;
            int t;
            while(b > c || (s = (t = Integer.compare(x[b], v)) == 0 ? Integer.compare(y[b], w) : t) > 0) {
               for(; c >= b && (s = (t = Integer.compare(x[c], v)) == 0 ? Integer.compare(y[c], w) : t) >= 0; --c) {
                  if (s == 0) {
                     swap(x, y, c, d--);
                  }
               }

               if (b > c) {
                  s = Math.min(a - from, b - a);
                  swap(x, y, from, b - s, s);
                  s = Math.min(d - c, to - d - 1);
                  swap(x, y, b, to - s, s);
                  if ((s = b - a) > 1) {
                     quickSort(x, y, from, from + s);
                  }

                  if ((s = d - c) > 1) {
                     quickSort(x, y, to - s, to);
                  }

                  return;
               }

               swap(x, y, b++, c--);
            }

            if (s == 0) {
               swap(x, y, a++, b);
            }

            ++b;
         }
      }
   }

   public static void quickSort(int[] x, int[] y) {
      ensureSameLength(x, y);
      quickSort(x, y, 0, x.length);
   }

   public static void parallelQuickSort(int[] x, int[] y, int from, int to) {
      ForkJoinPool pool = getPool();
      if (to - from >= 8192 && pool.getParallelism() != 1) {
         pool.invoke(new IntArrays.ForkJoinQuickSort2(x, y, from, to));
      } else {
         quickSort(x, y, from, to);
      }

   }

   public static void parallelQuickSort(int[] x, int[] y) {
      ensureSameLength(x, y);
      parallelQuickSort(x, y, 0, x.length);
   }

   public static void unstableSort(int[] a, int from, int to) {
      if (to - from >= 2000) {
         radixSort(a, from, to);
      } else {
         quickSort(a, from, to);
      }

   }

   public static void unstableSort(int[] a) {
      unstableSort(a, 0, a.length);
   }

   public static void unstableSort(int[] a, int from, int to, IntComparator comp) {
      quickSort(a, from, to, comp);
   }

   public static void unstableSort(int[] a, IntComparator comp) {
      unstableSort(a, 0, a.length, comp);
   }

   public static void mergeSort(int[] a, int from, int to, int[] supp) {
      int len = to - from;
      if (len < 16) {
         insertionSort(a, from, to);
      } else {
         if (supp == null) {
            supp = java.util.Arrays.copyOf(a, to);
         }

         int mid = from + to >>> 1;
         mergeSort(supp, from, mid, a);
         mergeSort(supp, mid, to, a);
         if (supp[mid - 1] <= supp[mid]) {
            System.arraycopy(supp, from, a, from, len);
         } else {
            int i = from;
            int p = from;

            for(int q = mid; i < to; ++i) {
               if (q < to && (p >= mid || supp[p] > supp[q])) {
                  a[i] = supp[q++];
               } else {
                  a[i] = supp[p++];
               }
            }

         }
      }
   }

   public static void mergeSort(int[] a, int from, int to) {
      mergeSort(a, from, to, (int[])null);
   }

   public static void mergeSort(int[] a) {
      mergeSort(a, 0, a.length);
   }

   public static void mergeSort(int[] a, int from, int to, IntComparator comp, int[] supp) {
      int len = to - from;
      if (len < 16) {
         insertionSort(a, from, to, comp);
      } else {
         if (supp == null) {
            supp = java.util.Arrays.copyOf(a, to);
         }

         int mid = from + to >>> 1;
         mergeSort(supp, from, mid, comp, a);
         mergeSort(supp, mid, to, comp, a);
         if (comp.compare(supp[mid - 1], supp[mid]) <= 0) {
            System.arraycopy(supp, from, a, from, len);
         } else {
            int i = from;
            int p = from;

            for(int q = mid; i < to; ++i) {
               if (q < to && (p >= mid || comp.compare(supp[p], supp[q]) > 0)) {
                  a[i] = supp[q++];
               } else {
                  a[i] = supp[p++];
               }
            }

         }
      }
   }

   public static void mergeSort(int[] a, int from, int to, IntComparator comp) {
      mergeSort(a, from, to, comp, (int[])null);
   }

   public static void mergeSort(int[] a, IntComparator comp) {
      mergeSort(a, 0, a.length, (IntComparator)comp);
   }

   public static void stableSort(int[] a, int from, int to) {
      unstableSort(a, from, to);
   }

   public static void stableSort(int[] a) {
      stableSort(a, 0, a.length);
   }

   public static void stableSort(int[] a, int from, int to, IntComparator comp) {
      mergeSort(a, from, to, comp);
   }

   public static void stableSort(int[] a, IntComparator comp) {
      stableSort(a, 0, a.length, comp);
   }

   public static int binarySearch(int[] a, int from, int to, int key) {
      --to;

      while(from <= to) {
         int mid = from + to >>> 1;
         int midVal = a[mid];
         if (midVal < key) {
            from = mid + 1;
         } else {
            if (midVal <= key) {
               return mid;
            }

            to = mid - 1;
         }
      }

      return -(from + 1);
   }

   public static int binarySearch(int[] a, int key) {
      return binarySearch(a, 0, a.length, key);
   }

   public static int binarySearch(int[] a, int from, int to, int key, IntComparator c) {
      --to;

      while(from <= to) {
         int mid = from + to >>> 1;
         int midVal = a[mid];
         int cmp = c.compare(midVal, key);
         if (cmp < 0) {
            from = mid + 1;
         } else {
            if (cmp <= 0) {
               return mid;
            }

            to = mid - 1;
         }
      }

      return -(from + 1);
   }

   public static int binarySearch(int[] a, int key, IntComparator c) {
      return binarySearch(a, 0, a.length, key, c);
   }

   public static void radixSort(int[] a) {
      radixSort((int[])a, 0, a.length);
   }

   public static void radixSort(int[] a, int from, int to) {
      if (to - from < 1024) {
         quickSort(a, from, to);
      } else {
         int maxLevel = true;
         int stackSize = true;
         int stackPos = 0;
         int[] offsetStack = new int[766];
         int[] lengthStack = new int[766];
         int[] levelStack = new int[766];
         offsetStack[stackPos] = from;
         lengthStack[stackPos] = to - from;
         int stackPos = stackPos + 1;
         levelStack[stackPos] = 0;
         int[] count = new int[256];
         int[] pos = new int[256];

         while(stackPos > 0) {
            --stackPos;
            int first = offsetStack[stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 4 == 0 ? 128 : 0;
            int shift = (3 - level % 4) * 8;

            int lastUsed;
            for(lastUsed = first + length; lastUsed-- != first; ++count[a[lastUsed] >>> shift & 255 ^ signMask]) {
            }

            lastUsed = -1;
            int end = 0;

            int i;
            for(i = first; end < 256; ++end) {
               if (count[end] != 0) {
                  lastUsed = end;
               }

               pos[end] = i += count[end];
            }

            end = first + length - count[lastUsed];
            i = first;

            int c;
            for(boolean var19 = true; i <= end; count[c] = 0) {
               int t = a[i];
               c = t >>> shift & 255 ^ signMask;
               if (i < end) {
                  int d;
                  while((d = --pos[c]) > i) {
                     int z = t;
                     t = a[d];
                     a[d] = z;
                     c = t >>> shift & 255 ^ signMask;
                  }

                  a[i] = t;
               }

               if (level < 3 && count[c] > 1) {
                  if (count[c] < 1024) {
                     quickSort(a, i, i + count[c]);
                  } else {
                     offsetStack[stackPos] = i;
                     lengthStack[stackPos] = count[c];
                     levelStack[stackPos++] = level + 1;
                  }
               }

               i += count[c];
            }
         }

      }
   }

   public static void parallelRadixSort(int[] a, int from, int to) {
      ForkJoinPool pool = getPool();
      if (to - from >= 1024 && pool.getParallelism() != 1) {
         int maxLevel = true;
         LinkedBlockingQueue<IntArrays.Segment> queue = new LinkedBlockingQueue();
         queue.add(new IntArrays.Segment(from, to - from, 0));
         AtomicInteger queueSize = new AtomicInteger(1);
         int numberOfThreads = pool.getParallelism();
         ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService(pool);
         int var9 = numberOfThreads;

         while(var9-- != 0) {
            executorCompletionService.submit(() -> {
               int[] count = new int[256];
               int[] pos = new int[256];

               while(true) {
                  if (queueSize.get() == 0) {
                     int var6 = numberOfThreads;

                     while(var6-- != 0) {
                        queue.add(POISON_PILL);
                     }
                  }

                  IntArrays.Segment segment = (IntArrays.Segment)queue.take();
                  if (segment == POISON_PILL) {
                     return null;
                  }

                  int first = segment.offset;
                  int length = segment.length;
                  int level = segment.level;
                  int signMask = level % 4 == 0 ? 128 : 0;
                  int shift = (3 - level % 4) * 8;

                  int lastUsed;
                  for(lastUsed = first + length; lastUsed-- != first; ++count[a[lastUsed] >>> shift & 255 ^ signMask]) {
                  }

                  lastUsed = -1;
                  int end = 0;

                  int i;
                  for(i = first; end < 256; ++end) {
                     if (count[end] != 0) {
                        lastUsed = end;
                     }

                     pos[end] = i += count[end];
                  }

                  end = first + length - count[lastUsed];
                  i = first;

                  int c;
                  for(boolean var15 = true; i <= end; count[c] = 0) {
                     int t = a[i];
                     c = t >>> shift & 255 ^ signMask;
                     if (i < end) {
                        int d;
                        while((d = --pos[c]) > i) {
                           int z = t;
                           t = a[d];
                           a[d] = z;
                           c = t >>> shift & 255 ^ signMask;
                        }

                        a[i] = t;
                     }

                     if (level < 3 && count[c] > 1) {
                        if (count[c] < 1024) {
                           quickSort(a, i, i + count[c]);
                        } else {
                           queueSize.incrementAndGet();
                           queue.add(new IntArrays.Segment(i, count[c], level + 1));
                        }
                     }

                     i += count[c];
                  }

                  queueSize.decrementAndGet();
               }
            });
         }

         Throwable problem = null;
         int var10 = numberOfThreads;

         while(var10-- != 0) {
            try {
               executorCompletionService.take().get();
            } catch (Exception var12) {
               problem = var12.getCause();
            }
         }

         if (problem != null) {
            throw problem instanceof RuntimeException ? (RuntimeException)problem : new RuntimeException(problem);
         }
      } else {
         quickSort(a, from, to);
      }
   }

   public static void parallelRadixSort(int[] a) {
      parallelRadixSort(a, 0, a.length);
   }

   public static void radixSortIndirect(int[] perm, int[] a, boolean stable) {
      radixSortIndirect(perm, a, 0, perm.length, stable);
   }

   public static void radixSortIndirect(int[] perm, int[] a, int from, int to, boolean stable) {
      if (to - from < 1024) {
         quickSortIndirect(perm, a, from, to);
         if (stable) {
            stabilize(perm, a, from, to);
         }

      } else {
         int maxLevel = true;
         int stackSize = true;
         int stackPos = 0;
         int[] offsetStack = new int[766];
         int[] lengthStack = new int[766];
         int[] levelStack = new int[766];
         offsetStack[stackPos] = from;
         lengthStack[stackPos] = to - from;
         int stackPos = stackPos + 1;
         levelStack[stackPos] = 0;
         int[] count = new int[256];
         int[] pos = new int[256];
         int[] support = stable ? new int[perm.length] : null;

         while(true) {
            while(stackPos > 0) {
               --stackPos;
               int first = offsetStack[stackPos];
               int length = lengthStack[stackPos];
               int level = levelStack[stackPos];
               int signMask = level % 4 == 0 ? 128 : 0;
               int shift = (3 - level % 4) * 8;

               int lastUsed;
               for(lastUsed = first + length; lastUsed-- != first; ++count[a[perm[lastUsed]] >>> shift & 255 ^ signMask]) {
               }

               lastUsed = -1;
               int i = 0;

               int i;
               for(i = stable ? 0 : first; i < 256; ++i) {
                  if (count[i] != 0) {
                     lastUsed = i;
                  }

                  pos[i] = i += count[i];
               }

               if (stable) {
                  for(i = first + length; i-- != first; support[--pos[a[perm[i]] >>> shift & 255 ^ signMask]] = perm[i]) {
                  }

                  System.arraycopy(support, 0, perm, first, length);
                  i = 0;

                  for(i = first; i <= lastUsed; ++i) {
                     if (level < 3 && count[i] > 1) {
                        if (count[i] < 1024) {
                           quickSortIndirect(perm, a, i, i + count[i]);
                           if (stable) {
                              stabilize(perm, a, i, i + count[i]);
                           }
                        } else {
                           offsetStack[stackPos] = i;
                           lengthStack[stackPos] = count[i];
                           levelStack[stackPos++] = level + 1;
                        }
                     }

                     i += count[i];
                  }

                  java.util.Arrays.fill(count, 0);
               } else {
                  i = first + length - count[lastUsed];
                  i = first;

                  int c;
                  for(boolean var22 = true; i <= i; count[c] = 0) {
                     int t = perm[i];
                     c = a[t] >>> shift & 255 ^ signMask;
                     if (i < i) {
                        int d;
                        while((d = --pos[c]) > i) {
                           int z = t;
                           t = perm[d];
                           perm[d] = z;
                           c = a[t] >>> shift & 255 ^ signMask;
                        }

                        perm[i] = t;
                     }

                     if (level < 3 && count[c] > 1) {
                        if (count[c] < 1024) {
                           quickSortIndirect(perm, a, i, i + count[c]);
                           if (stable) {
                              stabilize(perm, a, i, i + count[c]);
                           }
                        } else {
                           offsetStack[stackPos] = i;
                           lengthStack[stackPos] = count[c];
                           levelStack[stackPos++] = level + 1;
                        }
                     }

                     i += count[c];
                  }
               }
            }

            return;
         }
      }
   }

   public static void parallelRadixSortIndirect(int[] perm, int[] a, int from, int to, boolean stable) {
      ForkJoinPool pool = getPool();
      if (to - from >= 1024 && pool.getParallelism() != 1) {
         int maxLevel = true;
         LinkedBlockingQueue<IntArrays.Segment> queue = new LinkedBlockingQueue();
         queue.add(new IntArrays.Segment(from, to - from, 0));
         AtomicInteger queueSize = new AtomicInteger(1);
         int numberOfThreads = pool.getParallelism();
         ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService(pool);
         int[] support = stable ? new int[perm.length] : null;
         int var12 = numberOfThreads;

         while(var12-- != 0) {
            executorCompletionService.submit(() -> {
               int[] count = new int[256];
               int[] pos = new int[256];

               while(true) {
                  if (queueSize.get() == 0) {
                     int var9 = numberOfThreads;

                     while(var9-- != 0) {
                        queue.add(POISON_PILL);
                     }
                  }

                  IntArrays.Segment segment = (IntArrays.Segment)queue.take();
                  if (segment == POISON_PILL) {
                     return null;
                  }

                  int first = segment.offset;
                  int length = segment.length;
                  int level = segment.level;
                  int signMask = level % 4 == 0 ? 128 : 0;
                  int shift = (3 - level % 4) * 8;

                  int lastUsed;
                  for(lastUsed = first + length; lastUsed-- != first; ++count[a[perm[lastUsed]] >>> shift & 255 ^ signMask]) {
                  }

                  lastUsed = -1;
                  int i = 0;

                  int ix;
                  for(ix = first; i < 256; ++i) {
                     if (count[i] != 0) {
                        lastUsed = i;
                     }

                     pos[i] = ix += count[i];
                  }

                  if (stable) {
                     for(i = first + length; i-- != first; support[--pos[a[perm[i]] >>> shift & 255 ^ signMask]] = perm[i]) {
                     }

                     System.arraycopy(support, first, perm, first, length);
                     i = 0;

                     for(ix = first; i <= lastUsed; ++i) {
                        if (level < 3 && count[i] > 1) {
                           if (count[i] < 1024) {
                              radixSortIndirect(perm, a, ix, ix + count[i], stable);
                           } else {
                              queueSize.incrementAndGet();
                              queue.add(new IntArrays.Segment(ix, count[i], level + 1));
                           }
                        }

                        ix += count[i];
                     }

                     java.util.Arrays.fill(count, 0);
                  } else {
                     i = first + length - count[lastUsed];
                     ix = first;

                     int c;
                     for(boolean var18 = true; ix <= i; count[c] = 0) {
                        int t = perm[ix];
                        c = a[t] >>> shift & 255 ^ signMask;
                        if (ix < i) {
                           int d;
                           while((d = --pos[c]) > ix) {
                              int z = t;
                              t = perm[d];
                              perm[d] = z;
                              c = a[t] >>> shift & 255 ^ signMask;
                           }

                           perm[ix] = t;
                        }

                        if (level < 3 && count[c] > 1) {
                           if (count[c] < 1024) {
                              radixSortIndirect(perm, a, ix, ix + count[c], stable);
                           } else {
                              queueSize.incrementAndGet();
                              queue.add(new IntArrays.Segment(ix, count[c], level + 1));
                           }
                        }

                        ix += count[c];
                     }
                  }

                  queueSize.decrementAndGet();
               }
            });
         }

         Throwable problem = null;
         int var13 = numberOfThreads;

         while(var13-- != 0) {
            try {
               executorCompletionService.take().get();
            } catch (Exception var15) {
               problem = var15.getCause();
            }
         }

         if (problem != null) {
            throw problem instanceof RuntimeException ? (RuntimeException)problem : new RuntimeException(problem);
         }
      } else {
         radixSortIndirect(perm, a, from, to, stable);
      }
   }

   public static void parallelRadixSortIndirect(int[] perm, int[] a, boolean stable) {
      parallelRadixSortIndirect(perm, a, 0, a.length, stable);
   }

   public static void radixSort(int[] a, int[] b) {
      ensureSameLength(a, b);
      radixSort(a, b, 0, a.length);
   }

   public static void radixSort(int[] a, int[] b, int from, int to) {
      if (to - from < 1024) {
         quickSort(a, b, from, to);
      } else {
         int layers = true;
         int maxLevel = true;
         int stackSize = true;
         int stackPos = 0;
         int[] offsetStack = new int[1786];
         int[] lengthStack = new int[1786];
         int[] levelStack = new int[1786];
         offsetStack[stackPos] = from;
         lengthStack[stackPos] = to - from;
         int stackPos = stackPos + 1;
         levelStack[stackPos] = 0;
         int[] count = new int[256];
         int[] pos = new int[256];

         while(stackPos > 0) {
            --stackPos;
            int first = offsetStack[stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 4 == 0 ? 128 : 0;
            int[] k = level < 4 ? a : b;
            int shift = (3 - level % 4) * 8;

            int lastUsed;
            for(lastUsed = first + length; lastUsed-- != first; ++count[k[lastUsed] >>> shift & 255 ^ signMask]) {
            }

            lastUsed = -1;
            int end = 0;

            int i;
            for(i = first; end < 256; ++end) {
               if (count[end] != 0) {
                  lastUsed = end;
               }

               pos[end] = i += count[end];
            }

            end = first + length - count[lastUsed];
            i = first;

            int c;
            for(boolean var22 = true; i <= end; count[c] = 0) {
               int t = a[i];
               int u = b[i];
               c = k[i] >>> shift & 255 ^ signMask;
               if (i < end) {
                  int d;
                  while((d = --pos[c]) > i) {
                     c = k[d] >>> shift & 255 ^ signMask;
                     int z = t;
                     t = a[d];
                     a[d] = z;
                     z = u;
                     u = b[d];
                     b[d] = z;
                  }

                  a[i] = t;
                  b[i] = u;
               }

               if (level < 7 && count[c] > 1) {
                  if (count[c] < 1024) {
                     quickSort(a, b, i, i + count[c]);
                  } else {
                     offsetStack[stackPos] = i;
                     lengthStack[stackPos] = count[c];
                     levelStack[stackPos++] = level + 1;
                  }
               }

               i += count[c];
            }
         }

      }
   }

   public static void parallelRadixSort(int[] a, int[] b, int from, int to) {
      ForkJoinPool pool = getPool();
      if (to - from >= 1024 && pool.getParallelism() != 1) {
         int layers = true;
         if (a.length != b.length) {
            throw new IllegalArgumentException("Array size mismatch.");
         } else {
            int maxLevel = true;
            LinkedBlockingQueue<IntArrays.Segment> queue = new LinkedBlockingQueue();
            queue.add(new IntArrays.Segment(from, to - from, 0));
            AtomicInteger queueSize = new AtomicInteger(1);
            int numberOfThreads = pool.getParallelism();
            ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService(pool);
            int var11 = numberOfThreads;

            while(var11-- != 0) {
               executorCompletionService.submit(() -> {
                  int[] count = new int[256];
                  int[] pos = new int[256];

                  while(true) {
                     if (queueSize.get() == 0) {
                        int var7 = numberOfThreads;

                        while(var7-- != 0) {
                           queue.add(POISON_PILL);
                        }
                     }

                     IntArrays.Segment segment = (IntArrays.Segment)queue.take();
                     if (segment == POISON_PILL) {
                        return null;
                     }

                     int first = segment.offset;
                     int length = segment.length;
                     int level = segment.level;
                     int signMask = level % 4 == 0 ? 128 : 0;
                     int[] k = level < 4 ? a : b;
                     int shift = (3 - level % 4) * 8;

                     int lastUsed;
                     for(lastUsed = first + length; lastUsed-- != first; ++count[k[lastUsed] >>> shift & 255 ^ signMask]) {
                     }

                     lastUsed = -1;
                     int end = 0;

                     int i;
                     for(i = first; end < 256; ++end) {
                        if (count[end] != 0) {
                           lastUsed = end;
                        }

                        pos[end] = i += count[end];
                     }

                     end = first + length - count[lastUsed];
                     i = first;

                     int c;
                     for(boolean var17 = true; i <= end; count[c] = 0) {
                        int t = a[i];
                        int u = b[i];
                        c = k[i] >>> shift & 255 ^ signMask;
                        if (i < end) {
                           int d;
                           while((d = --pos[c]) > i) {
                              c = k[d] >>> shift & 255 ^ signMask;
                              int z = t;
                              int w = u;
                              t = a[d];
                              u = b[d];
                              a[d] = z;
                              b[d] = w;
                           }

                           a[i] = t;
                           b[i] = u;
                        }

                        if (level < 7 && count[c] > 1) {
                           if (count[c] < 1024) {
                              quickSort(a, b, i, i + count[c]);
                           } else {
                              queueSize.incrementAndGet();
                              queue.add(new IntArrays.Segment(i, count[c], level + 1));
                           }
                        }

                        i += count[c];
                     }

                     queueSize.decrementAndGet();
                  }
               });
            }

            Throwable problem = null;
            int var12 = numberOfThreads;

            while(var12-- != 0) {
               try {
                  executorCompletionService.take().get();
               } catch (Exception var14) {
                  problem = var14.getCause();
               }
            }

            if (problem != null) {
               throw problem instanceof RuntimeException ? (RuntimeException)problem : new RuntimeException(problem);
            }
         }
      } else {
         quickSort(a, b, from, to);
      }
   }

   public static void parallelRadixSort(int[] a, int[] b) {
      ensureSameLength(a, b);
      parallelRadixSort(a, b, 0, a.length);
   }

   private static void insertionSortIndirect(int[] perm, int[] a, int[] b, int from, int to) {
      int i = from;

      while(true) {
         ++i;
         if (i >= to) {
            return;
         }

         int t = perm[i];
         int j = i;

         for(int u = perm[i - 1]; a[t] < a[u] || a[t] == a[u] && b[t] < b[u]; u = perm[j - 1]) {
            perm[j] = u;
            if (from == j - 1) {
               --j;
               break;
            }

            --j;
         }

         perm[j] = t;
      }
   }

   public static void radixSortIndirect(int[] perm, int[] a, int[] b, boolean stable) {
      ensureSameLength(a, b);
      radixSortIndirect(perm, a, b, 0, a.length, stable);
   }

   public static void radixSortIndirect(int[] perm, int[] a, int[] b, int from, int to, boolean stable) {
      if (to - from < 64) {
         insertionSortIndirect(perm, a, b, from, to);
      } else {
         int layers = true;
         int maxLevel = true;
         int stackSize = true;
         int stackPos = 0;
         int[] offsetStack = new int[1786];
         int[] lengthStack = new int[1786];
         int[] levelStack = new int[1786];
         offsetStack[stackPos] = from;
         lengthStack[stackPos] = to - from;
         int stackPos = stackPos + 1;
         levelStack[stackPos] = 0;
         int[] count = new int[256];
         int[] pos = new int[256];
         int[] support = stable ? new int[perm.length] : null;

         while(true) {
            while(stackPos > 0) {
               --stackPos;
               int first = offsetStack[stackPos];
               int length = lengthStack[stackPos];
               int level = levelStack[stackPos];
               int signMask = level % 4 == 0 ? 128 : 0;
               int[] k = level < 4 ? a : b;
               int shift = (3 - level % 4) * 8;

               int lastUsed;
               for(lastUsed = first + length; lastUsed-- != first; ++count[k[perm[lastUsed]] >>> shift & 255 ^ signMask]) {
               }

               lastUsed = -1;
               int i = 0;

               int i;
               for(i = stable ? 0 : first; i < 256; ++i) {
                  if (count[i] != 0) {
                     lastUsed = i;
                  }

                  pos[i] = i += count[i];
               }

               if (stable) {
                  for(i = first + length; i-- != first; support[--pos[k[perm[i]] >>> shift & 255 ^ signMask]] = perm[i]) {
                  }

                  System.arraycopy(support, 0, perm, first, length);
                  i = 0;

                  for(i = first; i < 256; ++i) {
                     if (level < 7 && count[i] > 1) {
                        if (count[i] < 64) {
                           insertionSortIndirect(perm, a, b, i, i + count[i]);
                        } else {
                           offsetStack[stackPos] = i;
                           lengthStack[stackPos] = count[i];
                           levelStack[stackPos++] = level + 1;
                        }
                     }

                     i += count[i];
                  }

                  java.util.Arrays.fill(count, 0);
               } else {
                  i = first + length - count[lastUsed];
                  i = first;

                  int c;
                  for(boolean var25 = true; i <= i; count[c] = 0) {
                     int t = perm[i];
                     c = k[t] >>> shift & 255 ^ signMask;
                     if (i < i) {
                        int d;
                        while((d = --pos[c]) > i) {
                           int z = t;
                           t = perm[d];
                           perm[d] = z;
                           c = k[t] >>> shift & 255 ^ signMask;
                        }

                        perm[i] = t;
                     }

                     if (level < 7 && count[c] > 1) {
                        if (count[c] < 64) {
                           insertionSortIndirect(perm, a, b, i, i + count[c]);
                        } else {
                           offsetStack[stackPos] = i;
                           lengthStack[stackPos] = count[c];
                           levelStack[stackPos++] = level + 1;
                        }
                     }

                     i += count[c];
                  }
               }
            }

            return;
         }
      }
   }

   private static void selectionSort(int[][] a, int from, int to, int level) {
      int layers = a.length;
      int firstLayer = level / 4;

      for(int i = from; i < to - 1; ++i) {
         int m = i;

         int p;
         int u;
         for(p = i + 1; p < to; ++p) {
            for(u = firstLayer; u < layers; ++u) {
               if (a[u][p] < a[u][m]) {
                  m = p;
                  break;
               }

               if (a[u][p] > a[u][m]) {
                  break;
               }
            }
         }

         if (m != i) {
            for(p = layers; p-- != 0; a[p][m] = u) {
               u = a[p][i];
               a[p][i] = a[p][m];
            }
         }
      }

   }

   public static void radixSort(int[][] a) {
      radixSort((int[][])a, 0, a[0].length);
   }

   public static void radixSort(int[][] a, int from, int to) {
      if (to - from < 64) {
         selectionSort(a, from, to, 0);
      } else {
         int layers = a.length;
         int maxLevel = 4 * layers - 1;
         int p = layers;
         int stackPos = a[0].length;

         while(p-- != 0) {
            if (a[p].length != stackPos) {
               throw new IllegalArgumentException("The array of index " + p + " has not the same length of the array of index 0.");
            }
         }

         p = 255 * (layers * 4 - 1) + 1;
         int stackPos = 0;
         int[] offsetStack = new int[p];
         int[] lengthStack = new int[p];
         int[] levelStack = new int[p];
         offsetStack[stackPos] = from;
         lengthStack[stackPos] = to - from;
         stackPos = stackPos + 1;
         levelStack[stackPos] = 0;
         int[] count = new int[256];
         int[] pos = new int[256];
         int[] t = new int[layers];

         while(stackPos > 0) {
            --stackPos;
            int first = offsetStack[stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 4 == 0 ? 128 : 0;
            int[] k = a[level / 4];
            int shift = (3 - level % 4) * 8;

            int lastUsed;
            for(lastUsed = first + length; lastUsed-- != first; ++count[k[lastUsed] >>> shift & 255 ^ signMask]) {
            }

            lastUsed = -1;
            int end = 0;

            int i;
            for(i = first; end < 256; ++end) {
               if (count[end] != 0) {
                  lastUsed = end;
               }

               pos[end] = i += count[end];
            }

            end = first + length - count[lastUsed];
            i = first;

            int c;
            for(boolean var22 = true; i <= end; count[c] = 0) {
               int p;
               for(p = layers; p-- != 0; t[p] = a[p][i]) {
               }

               c = k[i] >>> shift & 255 ^ signMask;
               if (i < end) {
                  label92:
                  while(true) {
                     int d;
                     if ((d = --pos[c]) <= i) {
                        p = layers;

                        while(true) {
                           if (p-- == 0) {
                              break label92;
                           }

                           a[p][i] = t[p];
                        }
                     }

                     c = k[d] >>> shift & 255 ^ signMask;

                     int u;
                     for(p = layers; p-- != 0; a[p][d] = u) {
                        u = t[p];
                        t[p] = a[p][d];
                     }
                  }
               }

               if (level < maxLevel && count[c] > 1) {
                  if (count[c] < 64) {
                     selectionSort(a, i, i + count[c], level + 1);
                  } else {
                     offsetStack[stackPos] = i;
                     lengthStack[stackPos] = count[c];
                     levelStack[stackPos++] = level + 1;
                  }
               }

               i += count[c];
            }
         }

      }
   }

   public static int[] shuffle(int[] a, int from, int to, Random random) {
      int p;
      int t;
      for(int i = to - from; i-- != 0; a[from + p] = t) {
         p = random.nextInt(i + 1);
         t = a[from + i];
         a[from + i] = a[from + p];
      }

      return a;
   }

   public static int[] shuffle(int[] a, Random random) {
      int p;
      int t;
      for(int i = a.length; i-- != 0; a[p] = t) {
         p = random.nextInt(i + 1);
         t = a[i];
         a[i] = a[p];
      }

      return a;
   }

   public static int[] reverse(int[] a) {
      int length = a.length;

      int t;
      for(int i = length / 2; i-- != 0; a[i] = t) {
         t = a[length - i - 1];
         a[length - i - 1] = a[i];
      }

      return a;
   }

   public static int[] reverse(int[] a, int from, int to) {
      int length = to - from;

      int t;
      for(int i = length / 2; i-- != 0; a[from + i] = t) {
         t = a[from + length - i - 1];
         a[from + length - i - 1] = a[from + i];
      }

      return a;
   }

   protected static class ForkJoinQuickSortComp extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final int from;
      private final int to;
      private final int[] x;
      private final IntComparator comp;

      public ForkJoinQuickSortComp(int[] x, int from, int to, IntComparator comp) {
         this.from = from;
         this.to = to;
         this.x = x;
         this.comp = comp;
      }

      protected void compute() {
         int[] x = this.x;
         int len = this.to - this.from;
         if (len < 8192) {
            IntArrays.quickSort(x, this.from, this.to, this.comp);
         } else {
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = IntArrays.med3(x, l, l + s, l + 2 * s, this.comp);
            m = IntArrays.med3(x, m - s, m, m + s, this.comp);
            n = IntArrays.med3(x, n - 2 * s, n - s, n, this.comp);
            m = IntArrays.med3(x, l, m, n, this.comp);
            int v = x[m];
            int a = this.from;
            int b = a;
            int c = this.to - 1;
            int d = c;

            while(true) {
               int t;
               while(b > c || (t = this.comp.compare(x[b], v)) > 0) {
                  for(; c >= b && (t = this.comp.compare(x[c], v)) >= 0; --c) {
                     if (t == 0) {
                        IntArrays.swap(x, c, d--);
                     }
                  }

                  if (b > c) {
                     s = Math.min(a - this.from, b - a);
                     IntArrays.swap(x, this.from, b - s, s);
                     s = Math.min(d - c, this.to - d - 1);
                     IntArrays.swap(x, b, this.to - s, s);
                     s = b - a;
                     t = d - c;
                     if (s > 1 && t > 1) {
                        invokeAll(new IntArrays.ForkJoinQuickSortComp(x, this.from, this.from + s, this.comp), new IntArrays.ForkJoinQuickSortComp(x, this.to - t, this.to, this.comp));
                     } else if (s > 1) {
                        invokeAll(new ForkJoinTask[]{new IntArrays.ForkJoinQuickSortComp(x, this.from, this.from + s, this.comp)});
                     } else {
                        invokeAll(new ForkJoinTask[]{new IntArrays.ForkJoinQuickSortComp(x, this.to - t, this.to, this.comp)});
                     }

                     return;
                  }

                  IntArrays.swap(x, b++, c--);
               }

               if (t == 0) {
                  IntArrays.swap(x, a++, b);
               }

               ++b;
            }
         }
      }
   }

   protected static class ForkJoinQuickSort extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final int from;
      private final int to;
      private final int[] x;

      public ForkJoinQuickSort(int[] x, int from, int to) {
         this.from = from;
         this.to = to;
         this.x = x;
      }

      protected void compute() {
         int[] x = this.x;
         int len = this.to - this.from;
         if (len < 8192) {
            IntArrays.quickSort(x, this.from, this.to);
         } else {
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = IntArrays.med3(x, l, l + s, l + 2 * s);
            m = IntArrays.med3(x, m - s, m, m + s);
            n = IntArrays.med3(x, n - 2 * s, n - s, n);
            m = IntArrays.med3(x, l, m, n);
            int v = x[m];
            int a = this.from;
            int b = a;
            int c = this.to - 1;
            int d = c;

            while(true) {
               int t;
               while(b > c || (t = Integer.compare(x[b], v)) > 0) {
                  for(; c >= b && (t = Integer.compare(x[c], v)) >= 0; --c) {
                     if (t == 0) {
                        IntArrays.swap(x, c, d--);
                     }
                  }

                  if (b > c) {
                     s = Math.min(a - this.from, b - a);
                     IntArrays.swap(x, this.from, b - s, s);
                     s = Math.min(d - c, this.to - d - 1);
                     IntArrays.swap(x, b, this.to - s, s);
                     s = b - a;
                     t = d - c;
                     if (s > 1 && t > 1) {
                        invokeAll(new IntArrays.ForkJoinQuickSort(x, this.from, this.from + s), new IntArrays.ForkJoinQuickSort(x, this.to - t, this.to));
                     } else if (s > 1) {
                        invokeAll(new ForkJoinTask[]{new IntArrays.ForkJoinQuickSort(x, this.from, this.from + s)});
                     } else {
                        invokeAll(new ForkJoinTask[]{new IntArrays.ForkJoinQuickSort(x, this.to - t, this.to)});
                     }

                     return;
                  }

                  IntArrays.swap(x, b++, c--);
               }

               if (t == 0) {
                  IntArrays.swap(x, a++, b);
               }

               ++b;
            }
         }
      }
   }

   protected static class ForkJoinQuickSortIndirect extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final int from;
      private final int to;
      private final int[] perm;
      private final int[] x;

      public ForkJoinQuickSortIndirect(int[] perm, int[] x, int from, int to) {
         this.from = from;
         this.to = to;
         this.x = x;
         this.perm = perm;
      }

      protected void compute() {
         int[] x = this.x;
         int len = this.to - this.from;
         if (len < 8192) {
            IntArrays.quickSortIndirect(this.perm, x, this.from, this.to);
         } else {
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = IntArrays.med3Indirect(this.perm, x, l, l + s, l + 2 * s);
            m = IntArrays.med3Indirect(this.perm, x, m - s, m, m + s);
            n = IntArrays.med3Indirect(this.perm, x, n - 2 * s, n - s, n);
            m = IntArrays.med3Indirect(this.perm, x, l, m, n);
            int v = x[this.perm[m]];
            int a = this.from;
            int b = a;
            int c = this.to - 1;
            int d = c;

            while(true) {
               int t;
               while(b > c || (t = Integer.compare(x[this.perm[b]], v)) > 0) {
                  for(; c >= b && (t = Integer.compare(x[this.perm[c]], v)) >= 0; --c) {
                     if (t == 0) {
                        IntArrays.swap(this.perm, c, d--);
                     }
                  }

                  if (b > c) {
                     s = Math.min(a - this.from, b - a);
                     IntArrays.swap(this.perm, this.from, b - s, s);
                     s = Math.min(d - c, this.to - d - 1);
                     IntArrays.swap(this.perm, b, this.to - s, s);
                     s = b - a;
                     t = d - c;
                     if (s > 1 && t > 1) {
                        invokeAll(new IntArrays.ForkJoinQuickSortIndirect(this.perm, x, this.from, this.from + s), new IntArrays.ForkJoinQuickSortIndirect(this.perm, x, this.to - t, this.to));
                     } else if (s > 1) {
                        invokeAll(new ForkJoinTask[]{new IntArrays.ForkJoinQuickSortIndirect(this.perm, x, this.from, this.from + s)});
                     } else {
                        invokeAll(new ForkJoinTask[]{new IntArrays.ForkJoinQuickSortIndirect(this.perm, x, this.to - t, this.to)});
                     }

                     return;
                  }

                  IntArrays.swap(this.perm, b++, c--);
               }

               if (t == 0) {
                  IntArrays.swap(this.perm, a++, b);
               }

               ++b;
            }
         }
      }
   }

   protected static class ForkJoinQuickSort2 extends RecursiveAction {
      private static final long serialVersionUID = 1L;
      private final int from;
      private final int to;
      private final int[] x;
      private final int[] y;

      public ForkJoinQuickSort2(int[] x, int[] y, int from, int to) {
         this.from = from;
         this.to = to;
         this.x = x;
         this.y = y;
      }

      protected void compute() {
         int[] x = this.x;
         int[] y = this.y;
         int len = this.to - this.from;
         if (len < 8192) {
            IntArrays.quickSort(x, y, this.from, this.to);
         } else {
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = IntArrays.med3(x, y, l, l + s, l + 2 * s);
            m = IntArrays.med3(x, y, m - s, m, m + s);
            n = IntArrays.med3(x, y, n - 2 * s, n - s, n);
            m = IntArrays.med3(x, y, l, m, n);
            int v = x[m];
            int w = y[m];
            int a = this.from;
            int b = a;
            int c = this.to - 1;
            int d = c;

            while(true) {
               int t;
               int t;
               while(b > c || (t = (t = Integer.compare(x[b], v)) == 0 ? Integer.compare(y[b], w) : t) > 0) {
                  for(; c >= b && (t = (t = Integer.compare(x[c], v)) == 0 ? Integer.compare(y[c], w) : t) >= 0; --c) {
                     if (t == 0) {
                        IntArrays.swap(x, y, c, d--);
                     }
                  }

                  if (b > c) {
                     s = Math.min(a - this.from, b - a);
                     IntArrays.swap(x, y, this.from, b - s, s);
                     s = Math.min(d - c, this.to - d - 1);
                     IntArrays.swap(x, y, b, this.to - s, s);
                     s = b - a;
                     t = d - c;
                     if (s > 1 && t > 1) {
                        invokeAll(new IntArrays.ForkJoinQuickSort2(x, y, this.from, this.from + s), new IntArrays.ForkJoinQuickSort2(x, y, this.to - t, this.to));
                     } else if (s > 1) {
                        invokeAll(new ForkJoinTask[]{new IntArrays.ForkJoinQuickSort2(x, y, this.from, this.from + s)});
                     } else {
                        invokeAll(new ForkJoinTask[]{new IntArrays.ForkJoinQuickSort2(x, y, this.to - t, this.to)});
                     }

                     return;
                  }

                  IntArrays.swap(x, y, b++, c--);
               }

               if (t == 0) {
                  IntArrays.swap(x, y, a++, b);
               }

               ++b;
            }
         }
      }
   }

   protected static final class Segment {
      protected final int offset;
      protected final int length;
      protected final int level;

      protected Segment(int offset, int length, int level) {
         this.offset = offset;
         this.length = length;
         this.level = level;
      }

      public String toString() {
         return "Segment [offset=" + this.offset + ", length=" + this.length + ", level=" + this.level + "]";
      }
   }

   private static final class ArrayHashStrategy implements Hash.Strategy<int[]>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;

      private ArrayHashStrategy() {
      }

      public int hashCode(int[] o) {
         return java.util.Arrays.hashCode(o);
      }

      public boolean equals(int[] a, int[] b) {
         return java.util.Arrays.equals(a, b);
      }

      // $FF: synthetic method
      ArrayHashStrategy(Object x0) {
         this();
      }
   }
}
