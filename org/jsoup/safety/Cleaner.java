package org.jsoup.safety;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.ParseErrorList;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Tag;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

public class Cleaner {
   private final Safelist safelist;

   public Cleaner(Safelist safelist) {
      Validate.notNull(safelist);
      this.safelist = safelist;
   }

   public Document clean(Document dirtyDocument) {
      Validate.notNull(dirtyDocument);
      Document clean = Document.createShell(dirtyDocument.baseUri());
      this.copySafeNodes(dirtyDocument.body(), clean.body());
      clean.outputSettings(dirtyDocument.outputSettings().clone());
      return clean;
   }

   public boolean isValid(Document dirtyDocument) {
      Validate.notNull(dirtyDocument);
      Document clean = Document.createShell(dirtyDocument.baseUri());
      int numDiscarded = this.copySafeNodes(dirtyDocument.body(), clean.body());
      return numDiscarded == 0 && dirtyDocument.head().childNodes().isEmpty();
   }

   public boolean isValidBodyHtml(String bodyHtml) {
      Document clean = Document.createShell("");
      Document dirty = Document.createShell("");
      ParseErrorList errorList = ParseErrorList.tracking(1);
      List<Node> nodes = Parser.parseFragment(bodyHtml, dirty.body(), "", errorList);
      dirty.body().insertChildren(0, (Collection)nodes);
      int numDiscarded = this.copySafeNodes(dirty.body(), clean.body());
      return numDiscarded == 0 && errorList.isEmpty();
   }

   private int copySafeNodes(Element source, Element dest) {
      Cleaner.CleaningVisitor cleaningVisitor = new Cleaner.CleaningVisitor(source, dest);
      NodeTraversor.traverse(cleaningVisitor, (Node)source);
      return cleaningVisitor.numDiscarded;
   }

   private Cleaner.ElementMeta createSafeElement(Element sourceEl) {
      String sourceTag = sourceEl.tagName();
      Attributes destAttrs = new Attributes();
      Element dest = new Element(Tag.valueOf(sourceTag), sourceEl.baseUri(), destAttrs);
      int numDiscarded = 0;
      Attributes sourceAttrs = sourceEl.attributes();
      Iterator var7 = sourceAttrs.iterator();

      while(var7.hasNext()) {
         Attribute sourceAttr = (Attribute)var7.next();
         if (this.safelist.isSafeAttribute(sourceTag, sourceEl, sourceAttr)) {
            destAttrs.put(sourceAttr);
         } else {
            ++numDiscarded;
         }
      }

      Attributes enforcedAttrs = this.safelist.getEnforcedAttributes(sourceTag);
      destAttrs.addAll(enforcedAttrs);
      if (sourceEl.sourceRange().isTracked()) {
         sourceEl.sourceRange().track(dest, true);
      }

      if (sourceEl.endSourceRange().isTracked()) {
         sourceEl.endSourceRange().track(dest, false);
      }

      return new Cleaner.ElementMeta(dest, numDiscarded);
   }

   private static class ElementMeta {
      Element el;
      int numAttribsDiscarded;

      ElementMeta(Element el, int numAttribsDiscarded) {
         this.el = el;
         this.numAttribsDiscarded = numAttribsDiscarded;
      }
   }

   private final class CleaningVisitor implements NodeVisitor {
      private int numDiscarded;
      private final Element root;
      private Element destination;

      private CleaningVisitor(Element root, Element destination) {
         this.numDiscarded = 0;
         this.root = root;
         this.destination = destination;
      }

      public void head(Node source, int depth) {
         if (source instanceof Element) {
            Element sourceEl = (Element)source;
            if (Cleaner.this.safelist.isSafeTag(sourceEl.normalName())) {
               Cleaner.ElementMeta meta = Cleaner.this.createSafeElement(sourceEl);
               Element destChild = meta.el;
               this.destination.appendChild(destChild);
               this.numDiscarded += meta.numAttribsDiscarded;
               this.destination = destChild;
            } else if (source != this.root) {
               ++this.numDiscarded;
            }
         } else if (source instanceof TextNode) {
            TextNode sourceText = (TextNode)source;
            TextNode destText = new TextNode(sourceText.getWholeText());
            this.destination.appendChild(destText);
         } else if (source instanceof DataNode && Cleaner.this.safelist.isSafeTag(source.parent().nodeName())) {
            DataNode sourceData = (DataNode)source;
            DataNode destData = new DataNode(sourceData.getWholeData());
            this.destination.appendChild(destData);
         } else {
            ++this.numDiscarded;
         }

      }

      public void tail(Node source, int depth) {
         if (source instanceof Element && Cleaner.this.safelist.isSafeTag(source.nodeName())) {
            this.destination = this.destination.parent();
         }

      }

      // $FF: synthetic method
      CleaningVisitor(Element x1, Element x2, Object x3) {
         this(x1, x2);
      }
   }
}
