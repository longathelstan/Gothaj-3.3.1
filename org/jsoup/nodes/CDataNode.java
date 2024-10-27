package org.jsoup.nodes;

import java.io.IOException;
import org.jsoup.UncheckedIOException;

public class CDataNode extends TextNode {
   public CDataNode(String text) {
      super(text);
   }

   public String nodeName() {
      return "#cdata";
   }

   public String text() {
      return this.getWholeText();
   }

   void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
      accum.append("<![CDATA[").append(this.getWholeText());
   }

   void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) {
      try {
         accum.append("]]>");
      } catch (IOException var5) {
         throw new UncheckedIOException(var5);
      }
   }

   public CDataNode clone() {
      return (CDataNode)super.clone();
   }
}
