package org.jsoup.select;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.helper.Validate;
import org.jsoup.internal.Normalizer;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.PseudoTextElement;
import org.jsoup.nodes.TextNode;
import org.jsoup.nodes.XmlDeclaration;

public abstract class Evaluator {
   protected Evaluator() {
   }

   public abstract boolean matches(Element var1, Element var2);

   public static final class MatchText extends Evaluator {
      public boolean matches(Element root, Element element) {
         if (element instanceof PseudoTextElement) {
            return true;
         } else {
            List<TextNode> textNodes = element.textNodes();
            Iterator var4 = textNodes.iterator();

            while(var4.hasNext()) {
               TextNode textNode = (TextNode)var4.next();
               PseudoTextElement pel = new PseudoTextElement(org.jsoup.parser.Tag.valueOf(element.tagName()), element.baseUri(), element.attributes());
               textNode.replaceWith(pel);
               pel.appendChild(textNode);
            }

            return false;
         }
      }

      public String toString() {
         return ":matchText";
      }
   }

   public static final class MatchesWholeOwnText extends Evaluator {
      private final Pattern pattern;

      public MatchesWholeOwnText(Pattern pattern) {
         this.pattern = pattern;
      }

      public boolean matches(Element root, Element element) {
         Matcher m = this.pattern.matcher(element.wholeOwnText());
         return m.find();
      }

      public String toString() {
         return String.format(":matchesWholeOwnText(%s)", this.pattern);
      }
   }

   public static final class MatchesWholeText extends Evaluator {
      private final Pattern pattern;

      public MatchesWholeText(Pattern pattern) {
         this.pattern = pattern;
      }

      public boolean matches(Element root, Element element) {
         Matcher m = this.pattern.matcher(element.wholeText());
         return m.find();
      }

      public String toString() {
         return String.format(":matchesWholeText(%s)", this.pattern);
      }
   }

   public static final class MatchesOwn extends Evaluator {
      private final Pattern pattern;

      public MatchesOwn(Pattern pattern) {
         this.pattern = pattern;
      }

      public boolean matches(Element root, Element element) {
         Matcher m = this.pattern.matcher(element.ownText());
         return m.find();
      }

      public String toString() {
         return String.format(":matchesOwn(%s)", this.pattern);
      }
   }

   public static final class Matches extends Evaluator {
      private final Pattern pattern;

      public Matches(Pattern pattern) {
         this.pattern = pattern;
      }

      public boolean matches(Element root, Element element) {
         Matcher m = this.pattern.matcher(element.text());
         return m.find();
      }

      public String toString() {
         return String.format(":matches(%s)", this.pattern);
      }
   }

   public static final class ContainsOwnText extends Evaluator {
      private final String searchText;

      public ContainsOwnText(String searchText) {
         this.searchText = Normalizer.lowerCase(StringUtil.normaliseWhitespace(searchText));
      }

      public boolean matches(Element root, Element element) {
         return Normalizer.lowerCase(element.ownText()).contains(this.searchText);
      }

      public String toString() {
         return String.format(":containsOwn(%s)", this.searchText);
      }
   }

   public static final class ContainsData extends Evaluator {
      private final String searchText;

      public ContainsData(String searchText) {
         this.searchText = Normalizer.lowerCase(searchText);
      }

      public boolean matches(Element root, Element element) {
         return Normalizer.lowerCase(element.data()).contains(this.searchText);
      }

      public String toString() {
         return String.format(":containsData(%s)", this.searchText);
      }
   }

   public static final class ContainsWholeOwnText extends Evaluator {
      private final String searchText;

      public ContainsWholeOwnText(String searchText) {
         this.searchText = searchText;
      }

      public boolean matches(Element root, Element element) {
         return element.wholeOwnText().contains(this.searchText);
      }

      public String toString() {
         return String.format(":containsWholeOwnText(%s)", this.searchText);
      }
   }

   public static final class ContainsWholeText extends Evaluator {
      private final String searchText;

      public ContainsWholeText(String searchText) {
         this.searchText = searchText;
      }

      public boolean matches(Element root, Element element) {
         return element.wholeText().contains(this.searchText);
      }

      public String toString() {
         return String.format(":containsWholeText(%s)", this.searchText);
      }
   }

   public static final class ContainsText extends Evaluator {
      private final String searchText;

      public ContainsText(String searchText) {
         this.searchText = Normalizer.lowerCase(StringUtil.normaliseWhitespace(searchText));
      }

      public boolean matches(Element root, Element element) {
         return Normalizer.lowerCase(element.text()).contains(this.searchText);
      }

      public String toString() {
         return String.format(":contains(%s)", this.searchText);
      }
   }

   public abstract static class IndexEvaluator extends Evaluator {
      int index;

      public IndexEvaluator(int index) {
         this.index = index;
      }
   }

   public static final class IsEmpty extends Evaluator {
      public boolean matches(Element root, Element element) {
         List<Node> family = element.childNodes();
         Iterator var4 = family.iterator();

         Node n;
         do {
            if (!var4.hasNext()) {
               return true;
            }

            n = (Node)var4.next();
         } while(n instanceof Comment || n instanceof XmlDeclaration || n instanceof DocumentType);

         return false;
      }

      public String toString() {
         return ":empty";
      }
   }

   public static final class IsOnlyOfType extends Evaluator {
      public boolean matches(Element root, Element element) {
         Element p = element.parent();
         if (p != null && !(p instanceof Document)) {
            int pos = 0;
            Elements family = p.children();
            Iterator var6 = family.iterator();

            while(var6.hasNext()) {
               Element el = (Element)var6.next();
               if (el.tag().equals(element.tag())) {
                  ++pos;
               }
            }

            return pos == 1;
         } else {
            return false;
         }
      }

      public String toString() {
         return ":only-of-type";
      }
   }

   public static final class IsOnlyChild extends Evaluator {
      public boolean matches(Element root, Element element) {
         Element p = element.parent();
         return p != null && !(p instanceof Document) && element.siblingElements().isEmpty();
      }

      public String toString() {
         return ":only-child";
      }
   }

   public static final class IsRoot extends Evaluator {
      public boolean matches(Element root, Element element) {
         Element r = root instanceof Document ? root.child(0) : root;
         return element == r;
      }

      public String toString() {
         return ":root";
      }
   }

   public static final class IsFirstChild extends Evaluator {
      public boolean matches(Element root, Element element) {
         Element p = element.parent();
         return p != null && !(p instanceof Document) && element.elementSiblingIndex() == 0;
      }

      public String toString() {
         return ":first-child";
      }
   }

   public static class IsNthLastOfType extends Evaluator.CssNthEvaluator {
      public IsNthLastOfType(int a, int b) {
         super(a, b);
      }

      protected int calculatePosition(Element root, Element element) {
         int pos = 0;
         if (element.parent() == null) {
            return 0;
         } else {
            Elements family = element.parent().children();

            for(int i = element.elementSiblingIndex(); i < family.size(); ++i) {
               if (((Element)family.get(i)).tag().equals(element.tag())) {
                  ++pos;
               }
            }

            return pos;
         }
      }

      protected String getPseudoClass() {
         return "nth-last-of-type";
      }
   }

   public static class IsNthOfType extends Evaluator.CssNthEvaluator {
      public IsNthOfType(int a, int b) {
         super(a, b);
      }

      protected int calculatePosition(Element root, Element element) {
         int pos = 0;
         if (element.parent() == null) {
            return 0;
         } else {
            Elements family = element.parent().children();
            Iterator var5 = family.iterator();

            while(var5.hasNext()) {
               Element el = (Element)var5.next();
               if (el.tag().equals(element.tag())) {
                  ++pos;
               }

               if (el == element) {
                  break;
               }
            }

            return pos;
         }
      }

      protected String getPseudoClass() {
         return "nth-of-type";
      }
   }

   public static final class IsNthLastChild extends Evaluator.CssNthEvaluator {
      public IsNthLastChild(int a, int b) {
         super(a, b);
      }

      protected int calculatePosition(Element root, Element element) {
         return element.parent() == null ? 0 : element.parent().children().size() - element.elementSiblingIndex();
      }

      protected String getPseudoClass() {
         return "nth-last-child";
      }
   }

   public static final class IsNthChild extends Evaluator.CssNthEvaluator {
      public IsNthChild(int a, int b) {
         super(a, b);
      }

      protected int calculatePosition(Element root, Element element) {
         return element.elementSiblingIndex() + 1;
      }

      protected String getPseudoClass() {
         return "nth-child";
      }
   }

   public abstract static class CssNthEvaluator extends Evaluator {
      protected final int a;
      protected final int b;

      public CssNthEvaluator(int a, int b) {
         this.a = a;
         this.b = b;
      }

      public CssNthEvaluator(int b) {
         this(0, b);
      }

      public boolean matches(Element root, Element element) {
         Element p = element.parent();
         if (p != null && !(p instanceof Document)) {
            int pos = this.calculatePosition(root, element);
            if (this.a == 0) {
               return pos == this.b;
            } else {
               return (pos - this.b) * this.a >= 0 && (pos - this.b) % this.a == 0;
            }
         } else {
            return false;
         }
      }

      public String toString() {
         if (this.a == 0) {
            return String.format(":%s(%d)", this.getPseudoClass(), this.b);
         } else {
            return this.b == 0 ? String.format(":%s(%dn)", this.getPseudoClass(), this.a) : String.format(":%s(%dn%+d)", this.getPseudoClass(), this.a, this.b);
         }
      }

      protected abstract String getPseudoClass();

      protected abstract int calculatePosition(Element var1, Element var2);
   }

   public static final class IsLastOfType extends Evaluator.IsNthLastOfType {
      public IsLastOfType() {
         super(0, 1);
      }

      public String toString() {
         return ":last-of-type";
      }
   }

   public static final class IsFirstOfType extends Evaluator.IsNthOfType {
      public IsFirstOfType() {
         super(0, 1);
      }

      public String toString() {
         return ":first-of-type";
      }
   }

   public static final class IsLastChild extends Evaluator {
      public boolean matches(Element root, Element element) {
         Element p = element.parent();
         return p != null && !(p instanceof Document) && element.elementSiblingIndex() == p.children().size() - 1;
      }

      public String toString() {
         return ":last-child";
      }
   }

   public static final class IndexEquals extends Evaluator.IndexEvaluator {
      public IndexEquals(int index) {
         super(index);
      }

      public boolean matches(Element root, Element element) {
         return element.elementSiblingIndex() == this.index;
      }

      public String toString() {
         return String.format(":eq(%d)", this.index);
      }
   }

   public static final class IndexGreaterThan extends Evaluator.IndexEvaluator {
      public IndexGreaterThan(int index) {
         super(index);
      }

      public boolean matches(Element root, Element element) {
         return element.elementSiblingIndex() > this.index;
      }

      public String toString() {
         return String.format(":gt(%d)", this.index);
      }
   }

   public static final class IndexLessThan extends Evaluator.IndexEvaluator {
      public IndexLessThan(int index) {
         super(index);
      }

      public boolean matches(Element root, Element element) {
         return root != element && element.elementSiblingIndex() < this.index;
      }

      public String toString() {
         return String.format(":lt(%d)", this.index);
      }
   }

   public static final class AllElements extends Evaluator {
      public boolean matches(Element root, Element element) {
         return true;
      }

      public String toString() {
         return "*";
      }
   }

   public abstract static class AttributeKeyPair extends Evaluator {
      String key;
      String value;

      public AttributeKeyPair(String key, String value) {
         this(key, value, true);
      }

      public AttributeKeyPair(String key, String value, boolean trimValue) {
         Validate.notEmpty(key);
         Validate.notEmpty(value);
         this.key = Normalizer.normalize(key);
         boolean isStringLiteral = value.startsWith("'") && value.endsWith("'") || value.startsWith("\"") && value.endsWith("\"");
         if (isStringLiteral) {
            value = value.substring(1, value.length() - 1);
         }

         this.value = trimValue ? Normalizer.normalize(value) : Normalizer.normalize(value, isStringLiteral);
      }
   }

   public static final class AttributeWithValueMatching extends Evaluator {
      String key;
      Pattern pattern;

      public AttributeWithValueMatching(String key, Pattern pattern) {
         this.key = Normalizer.normalize(key);
         this.pattern = pattern;
      }

      public boolean matches(Element root, Element element) {
         return element.hasAttr(this.key) && this.pattern.matcher(element.attr(this.key)).find();
      }

      public String toString() {
         return String.format("[%s~=%s]", this.key, this.pattern.toString());
      }
   }

   public static final class AttributeWithValueContaining extends Evaluator.AttributeKeyPair {
      public AttributeWithValueContaining(String key, String value) {
         super(key, value);
      }

      public boolean matches(Element root, Element element) {
         return element.hasAttr(this.key) && Normalizer.lowerCase(element.attr(this.key)).contains(this.value);
      }

      public String toString() {
         return String.format("[%s*=%s]", this.key, this.value);
      }
   }

   public static final class AttributeWithValueEnding extends Evaluator.AttributeKeyPair {
      public AttributeWithValueEnding(String key, String value) {
         super(key, value, false);
      }

      public boolean matches(Element root, Element element) {
         return element.hasAttr(this.key) && Normalizer.lowerCase(element.attr(this.key)).endsWith(this.value);
      }

      public String toString() {
         return String.format("[%s$=%s]", this.key, this.value);
      }
   }

   public static final class AttributeWithValueStarting extends Evaluator.AttributeKeyPair {
      public AttributeWithValueStarting(String key, String value) {
         super(key, value, false);
      }

      public boolean matches(Element root, Element element) {
         return element.hasAttr(this.key) && Normalizer.lowerCase(element.attr(this.key)).startsWith(this.value);
      }

      public String toString() {
         return String.format("[%s^=%s]", this.key, this.value);
      }
   }

   public static final class AttributeWithValueNot extends Evaluator.AttributeKeyPair {
      public AttributeWithValueNot(String key, String value) {
         super(key, value);
      }

      public boolean matches(Element root, Element element) {
         return !this.value.equalsIgnoreCase(element.attr(this.key));
      }

      public String toString() {
         return String.format("[%s!=%s]", this.key, this.value);
      }
   }

   public static final class AttributeWithValue extends Evaluator.AttributeKeyPair {
      public AttributeWithValue(String key, String value) {
         super(key, value);
      }

      public boolean matches(Element root, Element element) {
         return element.hasAttr(this.key) && this.value.equalsIgnoreCase(element.attr(this.key).trim());
      }

      public String toString() {
         return String.format("[%s=%s]", this.key, this.value);
      }
   }

   public static final class AttributeStarting extends Evaluator {
      private final String keyPrefix;

      public AttributeStarting(String keyPrefix) {
         Validate.notEmpty(keyPrefix);
         this.keyPrefix = Normalizer.lowerCase(keyPrefix);
      }

      public boolean matches(Element root, Element element) {
         List<org.jsoup.nodes.Attribute> values = element.attributes().asList();
         Iterator var4 = values.iterator();

         org.jsoup.nodes.Attribute attribute;
         do {
            if (!var4.hasNext()) {
               return false;
            }

            attribute = (org.jsoup.nodes.Attribute)var4.next();
         } while(!Normalizer.lowerCase(attribute.getKey()).startsWith(this.keyPrefix));

         return true;
      }

      public String toString() {
         return String.format("[^%s]", this.keyPrefix);
      }
   }

   public static final class Attribute extends Evaluator {
      private final String key;

      public Attribute(String key) {
         this.key = key;
      }

      public boolean matches(Element root, Element element) {
         return element.hasAttr(this.key);
      }

      public String toString() {
         return String.format("[%s]", this.key);
      }
   }

   public static final class Class extends Evaluator {
      private final String className;

      public Class(String className) {
         this.className = className;
      }

      public boolean matches(Element root, Element element) {
         return element.hasClass(this.className);
      }

      public String toString() {
         return String.format(".%s", this.className);
      }
   }

   public static final class Id extends Evaluator {
      private final String id;

      public Id(String id) {
         this.id = id;
      }

      public boolean matches(Element root, Element element) {
         return this.id.equals(element.id());
      }

      public String toString() {
         return String.format("#%s", this.id);
      }
   }

   public static final class TagEndsWith extends Evaluator {
      private final String tagName;

      public TagEndsWith(String tagName) {
         this.tagName = tagName;
      }

      public boolean matches(Element root, Element element) {
         return element.normalName().endsWith(this.tagName);
      }

      public String toString() {
         return String.format("%s", this.tagName);
      }
   }

   public static final class Tag extends Evaluator {
      private final String tagName;

      public Tag(String tagName) {
         this.tagName = tagName;
      }

      public boolean matches(Element root, Element element) {
         return element.normalName().equals(this.tagName);
      }

      public String toString() {
         return String.format("%s", this.tagName);
      }
   }
}
