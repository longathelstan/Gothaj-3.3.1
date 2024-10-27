package org.jsoup.select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import javax.annotation.Nullable;
import org.jsoup.helper.Validate;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class Elements extends ArrayList<Element> {
   public Elements() {
   }

   public Elements(int initialCapacity) {
      super(initialCapacity);
   }

   public Elements(Collection<Element> elements) {
      super(elements);
   }

   public Elements(List<Element> elements) {
      super(elements);
   }

   public Elements(Element... elements) {
      super(Arrays.asList(elements));
   }

   public Elements clone() {
      Elements clone = new Elements(this.size());
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Element e = (Element)var2.next();
         clone.add(e.clone());
      }

      return clone;
   }

   public String attr(String attributeKey) {
      Iterator var2 = this.iterator();

      Element element;
      do {
         if (!var2.hasNext()) {
            return "";
         }

         element = (Element)var2.next();
      } while(!element.hasAttr(attributeKey));

      return element.attr(attributeKey);
   }

   public boolean hasAttr(String attributeKey) {
      Iterator var2 = this.iterator();

      Element element;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         element = (Element)var2.next();
      } while(!element.hasAttr(attributeKey));

      return true;
   }

   public List<String> eachAttr(String attributeKey) {
      List<String> attrs = new ArrayList(this.size());
      Iterator var3 = this.iterator();

      while(var3.hasNext()) {
         Element element = (Element)var3.next();
         if (element.hasAttr(attributeKey)) {
            attrs.add(element.attr(attributeKey));
         }
      }

      return attrs;
   }

   public Elements attr(String attributeKey, String attributeValue) {
      Iterator var3 = this.iterator();

      while(var3.hasNext()) {
         Element element = (Element)var3.next();
         element.attr(attributeKey, attributeValue);
      }

      return this;
   }

   public Elements removeAttr(String attributeKey) {
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Element element = (Element)var2.next();
         element.removeAttr(attributeKey);
      }

      return this;
   }

   public Elements addClass(String className) {
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Element element = (Element)var2.next();
         element.addClass(className);
      }

      return this;
   }

   public Elements removeClass(String className) {
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Element element = (Element)var2.next();
         element.removeClass(className);
      }

      return this;
   }

   public Elements toggleClass(String className) {
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Element element = (Element)var2.next();
         element.toggleClass(className);
      }

      return this;
   }

   public boolean hasClass(String className) {
      Iterator var2 = this.iterator();

      Element element;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         element = (Element)var2.next();
      } while(!element.hasClass(className));

      return true;
   }

   public String val() {
      return this.size() > 0 ? this.first().val() : "";
   }

   public Elements val(String value) {
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Element element = (Element)var2.next();
         element.val(value);
      }

      return this;
   }

   public String text() {
      StringBuilder sb = StringUtil.borrowBuilder();

      Element element;
      for(Iterator var2 = this.iterator(); var2.hasNext(); sb.append(element.text())) {
         element = (Element)var2.next();
         if (sb.length() != 0) {
            sb.append(" ");
         }
      }

      return StringUtil.releaseBuilder(sb);
   }

   public boolean hasText() {
      Iterator var1 = this.iterator();

      Element element;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         element = (Element)var1.next();
      } while(!element.hasText());

      return true;
   }

   public List<String> eachText() {
      ArrayList<String> texts = new ArrayList(this.size());
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Element el = (Element)var2.next();
         if (el.hasText()) {
            texts.add(el.text());
         }
      }

      return texts;
   }

   public String html() {
      StringBuilder sb = StringUtil.borrowBuilder();

      Element element;
      for(Iterator var2 = this.iterator(); var2.hasNext(); sb.append(element.html())) {
         element = (Element)var2.next();
         if (sb.length() != 0) {
            sb.append("\n");
         }
      }

      return StringUtil.releaseBuilder(sb);
   }

   public String outerHtml() {
      StringBuilder sb = StringUtil.borrowBuilder();

      Element element;
      for(Iterator var2 = this.iterator(); var2.hasNext(); sb.append(element.outerHtml())) {
         element = (Element)var2.next();
         if (sb.length() != 0) {
            sb.append("\n");
         }
      }

      return StringUtil.releaseBuilder(sb);
   }

   public String toString() {
      return this.outerHtml();
   }

   public Elements tagName(String tagName) {
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Element element = (Element)var2.next();
         element.tagName(tagName);
      }

      return this;
   }

   public Elements html(String html) {
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Element element = (Element)var2.next();
         element.html(html);
      }

      return this;
   }

   public Elements prepend(String html) {
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Element element = (Element)var2.next();
         element.prepend(html);
      }

      return this;
   }

   public Elements append(String html) {
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Element element = (Element)var2.next();
         element.append(html);
      }

      return this;
   }

   public Elements before(String html) {
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Element element = (Element)var2.next();
         element.before(html);
      }

      return this;
   }

   public Elements after(String html) {
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Element element = (Element)var2.next();
         element.after(html);
      }

      return this;
   }

   public Elements wrap(String html) {
      Validate.notEmpty(html);
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Element element = (Element)var2.next();
         element.wrap(html);
      }

      return this;
   }

   public Elements unwrap() {
      Iterator var1 = this.iterator();

      while(var1.hasNext()) {
         Element element = (Element)var1.next();
         element.unwrap();
      }

      return this;
   }

   public Elements empty() {
      Iterator var1 = this.iterator();

      while(var1.hasNext()) {
         Element element = (Element)var1.next();
         element.empty();
      }

      return this;
   }

   public Elements remove() {
      Iterator var1 = this.iterator();

      while(var1.hasNext()) {
         Element element = (Element)var1.next();
         element.remove();
      }

      return this;
   }

   public Elements select(String query) {
      return Selector.select((String)query, (Iterable)this);
   }

   public Elements not(String query) {
      Elements out = Selector.select((String)query, (Iterable)this);
      return Selector.filterOut(this, out);
   }

   public Elements eq(int index) {
      return this.size() > index ? new Elements(new Element[]{(Element)this.get(index)}) : new Elements();
   }

   public boolean is(String query) {
      Evaluator eval = QueryParser.parse(query);
      Iterator var3 = this.iterator();

      Element e;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         e = (Element)var3.next();
      } while(!e.is(eval));

      return true;
   }

   public Elements next() {
      return this.siblings((String)null, true, false);
   }

   public Elements next(String query) {
      return this.siblings(query, true, false);
   }

   public Elements nextAll() {
      return this.siblings((String)null, true, true);
   }

   public Elements nextAll(String query) {
      return this.siblings(query, true, true);
   }

   public Elements prev() {
      return this.siblings((String)null, false, false);
   }

   public Elements prev(String query) {
      return this.siblings(query, false, false);
   }

   public Elements prevAll() {
      return this.siblings((String)null, false, true);
   }

   public Elements prevAll(String query) {
      return this.siblings(query, false, true);
   }

   private Elements siblings(@Nullable String query, boolean next, boolean all) {
      Elements els = new Elements();
      Evaluator eval = query != null ? QueryParser.parse(query) : null;
      Iterator var6 = this.iterator();

      while(var6.hasNext()) {
         Element e = (Element)var6.next();

         while(true) {
            Element sib = next ? e.nextElementSibling() : e.previousElementSibling();
            if (sib == null) {
               break;
            }

            if (eval == null) {
               els.add(sib);
            } else if (sib.is(eval)) {
               els.add(sib);
            }

            e = sib;
            if (!all) {
               break;
            }
         }
      }

      return els;
   }

   public Elements parents() {
      HashSet<Element> combo = new LinkedHashSet();
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Element e = (Element)var2.next();
         combo.addAll(e.parents());
      }

      return new Elements(combo);
   }

   @Nullable
   public Element first() {
      return this.isEmpty() ? null : (Element)this.get(0);
   }

   @Nullable
   public Element last() {
      return this.isEmpty() ? null : (Element)this.get(this.size() - 1);
   }

   public Elements traverse(NodeVisitor nodeVisitor) {
      NodeTraversor.traverse(nodeVisitor, this);
      return this;
   }

   public Elements filter(NodeFilter nodeFilter) {
      NodeTraversor.filter(nodeFilter, this);
      return this;
   }

   public List<FormElement> forms() {
      ArrayList<FormElement> forms = new ArrayList();
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Element el = (Element)var2.next();
         if (el instanceof FormElement) {
            forms.add((FormElement)el);
         }
      }

      return forms;
   }

   public List<Comment> comments() {
      return this.childNodesOfType(Comment.class);
   }

   public List<TextNode> textNodes() {
      return this.childNodesOfType(TextNode.class);
   }

   public List<DataNode> dataNodes() {
      return this.childNodesOfType(DataNode.class);
   }

   private <T extends Node> List<T> childNodesOfType(Class<T> tClass) {
      ArrayList<T> nodes = new ArrayList();
      Iterator var3 = this.iterator();

      while(var3.hasNext()) {
         Element el = (Element)var3.next();

         for(int i = 0; i < el.childNodeSize(); ++i) {
            Node node = el.childNode(i);
            if (tClass.isInstance(node)) {
               nodes.add((Node)tClass.cast(node));
            }
         }
      }

      return nodes;
   }
}
