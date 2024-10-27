package xyz.cucumber.base.utils.math;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Sha256 {
   private static final int[] K = new int[]{1116352408, 1899447441, -1245643825, -373957723, 961987163, 1508970993, -1841331548, -1424204075, -670586216, 310598401, 607225278, 1426881987, 1925078388, -2132889090, -1680079193, -1046744716, -459576895, -272742522, 264347078, 604807628, 770255983, 1249150122, 1555081692, 1996064986, -1740746414, -1473132947, -1341970488, -1084653625, -958395405, -710438585, 113926993, 338241895, 666307205, 773529912, 1294757372, 1396182291, 1695183700, 1986661051, -2117940946, -1838011259, -1564481375, -1474664885, -1035236496, -949202525, -778901479, -694614492, -200395387, 275423344, 430227734, 506948616, 659060556, 883997877, 958139571, 1322822218, 1537002063, 1747873779, 1955562222, 2024104815, -2067236844, -1933114872, -1866530822, -1538233109, -1090935817, -965641998};
   private static final int[] H0 = new int[]{1779033703, -1150833019, 1013904242, -1521486534, 1359893119, -1694144372, 528734635, 1541459225};
   private static final int BLOCK_BITS = 512;
   private static final int BLOCK_BYTES = 64;
   private static final int[] W = new int[64];
   private static final int[] H = new int[8];
   private static final int[] TEMP = new int[8];

   public static byte[] hash(byte[] message) {
      System.arraycopy(H0, 0, H, 0, H0.length);
      int[] words = pad(message);
      int i = 0;

      for(int n = words.length / 16; i < n; ++i) {
         System.arraycopy(words, i * 16, W, 0, 16);

         int t;
         for(t = 16; t < W.length; ++t) {
            W[t] = smallSig1(W[t - 2]) + W[t - 7] + smallSig0(W[t - 15]) + W[t - 16];
         }

         System.arraycopy(H, 0, TEMP, 0, H.length);

         int[] var10000;
         for(t = 0; t < W.length; ++t) {
            int t1 = TEMP[7] + bigSig1(TEMP[4]) + ch(TEMP[4], TEMP[5], TEMP[6]) + K[t] + W[t];
            int t2 = bigSig0(TEMP[0]) + maj(TEMP[0], TEMP[1], TEMP[2]);
            System.arraycopy(TEMP, 0, TEMP, 1, TEMP.length - 1);
            var10000 = TEMP;
            var10000[4] += t1;
            TEMP[0] = t1 + t2;
         }

         for(t = 0; t < H.length; ++t) {
            var10000 = H;
            var10000[t] += TEMP[t];
         }
      }

      return toByteArray(H);
   }

   public static int[] pad(byte[] message) {
      int finalBlockLength = message.length % 64;
      int blockCount = message.length / 64 + (finalBlockLength + 1 + 8 > 64 ? 2 : 1);
      IntBuffer result = IntBuffer.allocate(blockCount * 16);
      ByteBuffer buf = ByteBuffer.wrap(message);
      int i = 0;

      for(int n = message.length / 4; i < n; ++i) {
         result.put(buf.getInt());
      }

      ByteBuffer remainder = ByteBuffer.allocate(4);
      remainder.put(buf).put((byte)-128).rewind();
      result.put(remainder.getInt());
      result.position(result.capacity() - 2);
      long msgLength = (long)message.length * 8L;
      result.put((int)(msgLength >>> 32));
      result.put((int)msgLength);
      return result.array();
   }

   private static byte[] toByteArray(int[] ints) {
      ByteBuffer buf = ByteBuffer.allocate(ints.length * 4);
      int[] var5 = ints;
      int var4 = ints.length;

      for(int var3 = 0; var3 < var4; ++var3) {
         int i = var5[var3];
         buf.putInt(i);
      }

      return buf.array();
   }

   private static int ch(int x, int y, int z) {
      return x & y | ~x & z;
   }

   private static int maj(int x, int y, int z) {
      return x & y | x & z | y & z;
   }

   private static int bigSig0(int x) {
      return Integer.rotateRight(x, 2) ^ Integer.rotateRight(x, 13) ^ Integer.rotateRight(x, 22);
   }

   private static int bigSig1(int x) {
      return Integer.rotateRight(x, 6) ^ Integer.rotateRight(x, 11) ^ Integer.rotateRight(x, 25);
   }

   private static int smallSig0(int x) {
      return Integer.rotateRight(x, 7) ^ Integer.rotateRight(x, 18) ^ x >>> 3;
   }

   private static int smallSig1(int x) {
      return Integer.rotateRight(x, 17) ^ Integer.rotateRight(x, 19) ^ x >>> 10;
   }
}