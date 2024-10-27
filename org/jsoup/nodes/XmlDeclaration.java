package org.jsoup.nodes;

import java.io.IOException;
import java.util.Iterator;
import org.jsoup.SerializationException;
import org.jsoup.helper.Validate;
import org.jsoup.internal.StringUtil;

public class XmlDeclaration extends LeafNode {
   private final boolean isProcessingInstruction;

   public XmlDeclaration(String name, boolean isProcessingInstruction) {
      Validate.notNull(name);
      this.value = name;
      this.isProcessingInstruction = isProcessingInstruction;
   }

   public String nodeName() {
      return "#declaration";
   }

   public String name() {
      return this.coreValue();
   }

   public String getWholeDeclaration() {
      StringBuilder sb = StringUtil.borrowBuilder();

      try {
         this.getWholeDeclaration(sb, new Document.OutputSettings());
      } catch (IOException var3) {
         throw new SerializationException(var3);
      }

      return StringUtil.releaseBuilder(sb).trim();
   }

   private void getWholeDeclaration(Appendable accum, Document.OutputSettings out) throws IOException {
      Iterator var3 = this.attributes().iterator();

      while(var3.hasNext()) {
         Attribute attribute = (Attribute)var3.next();
         String key = attribute.getKey();
         String val = attribute.getValue();
         if (!key.equals(this.nodeName())) {
            accum.append(' ');
            accum.append(key);
            if (!val.isEmpty()) {
               accum.append("=\"");
               Entities.escape(accum, val, out, true, false, false, false);
               accum.append('"');
            }
         }
      }

   }

   void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
      accum.append("<").append(this.isProcessingInstruction ? "!" : "?").append(this.coreValue());
      this.getWholeDeclaration(accum, out);
      accum.append(this.isProcessingInstruction ? "!" : "?").append(">");
   }

   void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) {
   }

   public String toString() {
      return this.outerHtml();
   }

   public XmlDeclaration clone() {
      return (XmlDeclaration)super.clone();
   }
}
