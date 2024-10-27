package org.yaml.snakeyaml.reader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.scanner.Constant;

public class StreamReader {
   private String name;
   private final Reader stream;
   private int[] dataWindow;
   private int dataLength;
   private int pointer;
   private boolean eof;
   private int index;
   private int documentIndex;
   private int line;
   private int column;
   private final char[] buffer;
   private static final int BUFFER_SIZE = 1025;

   public StreamReader(String stream) {
      this((Reader)(new StringReader(stream)));
      this.name = "'string'";
   }

   public StreamReader(Reader reader) {
      this.pointer = 0;
      this.index = 0;
      this.documentIndex = 0;
      this.line = 0;
      this.column = 0;
      if (reader == null) {
         throw new NullPointerException("Reader must be provided.");
      } else {
         this.name = "'reader'";
         this.dataWindow = new int[0];
         this.dataLength = 0;
         this.stream = reader;
         this.eof = false;
         this.buffer = new char[1025];
      }
   }

   public static boolean isPrintable(String data) {
      int length = data.length();

      int codePoint;
      for(int offset = 0; offset < length; offset += Character.charCount(codePoint)) {
         codePoint = data.codePointAt(offset);
         if (!isPrintable(codePoint)) {
            return false;
         }
      }

      return true;
   }

   public static boolean isPrintable(int c) {
      return c >= 32 && c <= 126 || c == 9 || c == 10 || c == 13 || c == 133 || c >= 160 && c <= 55295 || c >= 57344 && c <= 65533 || c >= 65536 && c <= 1114111;
   }

   public Mark getMark() {
      return new Mark(this.name, this.index, this.line, this.column, this.dataWindow, this.pointer);
   }

   public void forward() {
      this.forward(1);
   }

   public void forward(int length) {
      for(int i = 0; i < length && this.ensureEnoughData(); ++i) {
         int c = this.dataWindow[this.pointer++];
         this.moveIndices(1);
         if (Constant.LINEBR.has(c) || c == 13 && this.ensureEnoughData() && this.dataWindow[this.pointer] != 10) {
            ++this.line;
            this.column = 0;
         } else if (c != 65279) {
            ++this.column;
         }
      }

   }

   public int peek() {
      return this.ensureEnoughData() ? this.dataWindow[this.pointer] : 0;
   }

   public int peek(int index) {
      return this.ensureEnoughData(index) ? this.dataWindow[this.pointer + index] : 0;
   }

   public String prefix(int length) {
      if (length == 0) {
         return "";
      } else {
         return this.ensureEnoughData(length) ? new String(this.dataWindow, this.pointer, length) : new String(this.dataWindow, this.pointer, Math.min(length, this.dataLength - this.pointer));
      }
   }

   public String prefixForward(int length) {
      String prefix = this.prefix(length);
      this.pointer += length;
      this.moveIndices(length);
      this.column += length;
      return prefix;
   }

   private boolean ensureEnoughData() {
      return this.ensureEnoughData(0);
   }

   private boolean ensureEnoughData(int size) {
      if (!this.eof && this.pointer + size >= this.dataLength) {
         this.update();
      }

      return this.pointer + size < this.dataLength;
   }

   private void update() {
      try {
         int read = this.stream.read(this.buffer, 0, 1024);
         if (read > 0) {
            int cpIndex = this.dataLength - this.pointer;
            this.dataWindow = Arrays.copyOfRange(this.dataWindow, this.pointer, this.dataLength + read);
            if (Character.isHighSurrogate(this.buffer[read - 1])) {
               if (this.stream.read(this.buffer, read, 1) == -1) {
                  this.eof = true;
               } else {
                  ++read;
               }
            }

            int nonPrintable = 32;

            for(int i = 0; i < read; ++cpIndex) {
               int codePoint = Character.codePointAt(this.buffer, i);
               this.dataWindow[cpIndex] = codePoint;
               if (isPrintable(codePoint)) {
                  i += Character.charCount(codePoint);
               } else {
                  nonPrintable = codePoint;
                  i = read;
               }
            }

            this.dataLength = cpIndex;
            this.pointer = 0;
            if (nonPrintable != 32) {
               throw new ReaderException(this.name, cpIndex - 1, nonPrintable, "special characters are not allowed");
            }
         } else {
            this.eof = true;
         }

      } catch (IOException var6) {
         throw new YAMLException(var6);
      }
   }

   public int getColumn() {
      return this.column;
   }

   private void moveIndices(int length) {
      this.index += length;
      this.documentIndex += length;
   }

   public int getDocumentIndex() {
      return this.documentIndex;
   }

   public void resetDocumentIndex() {
      this.documentIndex = 0;
   }

   public int getIndex() {
      return this.index;
   }

   public int getLine() {
      return this.line;
   }
}