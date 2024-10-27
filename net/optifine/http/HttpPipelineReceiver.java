package net.optifine.http;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import net.minecraft.src.Config;

public class HttpPipelineReceiver extends Thread {
   private HttpPipelineConnection httpPipelineConnection = null;
   private static final Charset ASCII = Charset.forName("ASCII");
   private static final String HEADER_CONTENT_LENGTH = "Content-Length";
   private static final char CR = '\r';
   private static final char LF = '\n';

   public HttpPipelineReceiver(HttpPipelineConnection httpPipelineConnection) {
      super("HttpPipelineReceiver");
      this.httpPipelineConnection = httpPipelineConnection;
   }

   public void run() {
      while(!Thread.interrupted()) {
         HttpPipelineRequest httppipelinerequest = null;

         try {
            httppipelinerequest = this.httpPipelineConnection.getNextRequestReceive();
            InputStream inputstream = this.httpPipelineConnection.getInputStream();
            HttpResponse httpresponse = this.readResponse(inputstream);
            this.httpPipelineConnection.onResponseReceived(httppipelinerequest, httpresponse);
         } catch (InterruptedException var4) {
            return;
         } catch (Exception var5) {
            this.httpPipelineConnection.onExceptionReceive(httppipelinerequest, var5);
         }
      }

   }

   private HttpResponse readResponse(InputStream in) throws IOException {
      String s = this.readLine(in);
      String[] astring = Config.tokenize(s, " ");
      if (astring.length < 3) {
         throw new IOException("Invalid status line: " + s);
      } else {
         String s1 = astring[0];
         int i = Config.parseInt(astring[1], 0);
         String s2 = astring[2];
         LinkedHashMap map = new LinkedHashMap();

         while(true) {
            String s3 = this.readLine(in);
            String s4;
            String s7;
            if (s3.length() <= 0) {
               byte[] abyte = null;
               s4 = (String)map.get("Content-Length");
               if (s4 != null) {
                  int k = Config.parseInt(s4, -1);
                  if (k > 0) {
                     abyte = new byte[k];
                     this.readFull(abyte, in);
                  }
               } else {
                  s7 = (String)map.get("Transfer-Encoding");
                  if (Config.equals(s7, "chunked")) {
                     abyte = this.readContentChunked(in);
                  }
               }

               return new HttpResponse(i, s, map, abyte);
            }

            int j = s3.indexOf(":");
            if (j > 0) {
               s4 = s3.substring(0, j).trim();
               s7 = s3.substring(j + 1).trim();
               map.put(s4, s7);
            }
         }
      }
   }

   private byte[] readContentChunked(InputStream in) throws IOException {
      ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();

      int i;
      do {
         String s = this.readLine(in);
         String[] astring = Config.tokenize(s, "; ");
         i = Integer.parseInt(astring[0], 16);
         byte[] abyte = new byte[i];
         this.readFull(abyte, in);
         bytearrayoutputstream.write(abyte);
         this.readLine(in);
      } while(i != 0);

      return bytearrayoutputstream.toByteArray();
   }

   private void readFull(byte[] buf, InputStream in) throws IOException {
      int j;
      for(int i = 0; i < buf.length; i += j) {
         j = in.read(buf, i, buf.length - i);
         if (j < 0) {
            throw new EOFException();
         }
      }

   }

   private String readLine(InputStream in) throws IOException {
      ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
      int i = -1;
      boolean flag = false;

      while(true) {
         int j = in.read();
         if (j < 0) {
            break;
         }

         bytearrayoutputstream.write(j);
         if (i == 13 && j == 10) {
            flag = true;
            break;
         }

         i = j;
      }

      byte[] abyte = bytearrayoutputstream.toByteArray();
      String s = new String(abyte, ASCII);
      if (flag) {
         s = s.substring(0, s.length() - 2);
      }

      return s;
   }
}
