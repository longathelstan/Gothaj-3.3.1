package org.jsoup.parser;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.jsoup.helper.Validate;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.CDataNode;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class HtmlTreeBuilder extends TreeBuilder {
   static final String[] TagsSearchInScope = new String[]{"applet", "caption", "html", "marquee", "object", "table", "td", "th"};
   static final String[] TagSearchList = new String[]{"ol", "ul"};
   static final String[] TagSearchButton = new String[]{"button"};
   static final String[] TagSearchTableScope = new String[]{"html", "table"};
   static final String[] TagSearchSelectScope = new String[]{"optgroup", "option"};
   static final String[] TagSearchEndTags = new String[]{"dd", "dt", "li", "optgroup", "option", "p", "rb", "rp", "rt", "rtc"};
   static final String[] TagThoroughSearchEndTags = new String[]{"caption", "colgroup", "dd", "dt", "li", "optgroup", "option", "p", "rb", "rp", "rt", "rtc", "tbody", "td", "tfoot", "th", "thead", "tr"};
   static final String[] TagSearchSpecial = new String[]{"address", "applet", "area", "article", "aside", "base", "basefont", "bgsound", "blockquote", "body", "br", "button", "caption", "center", "col", "colgroup", "command", "dd", "details", "dir", "div", "dl", "dt", "embed", "fieldset", "figcaption", "figure", "footer", "form", "frame", "frameset", "h1", "h2", "h3", "h4", "h5", "h6", "head", "header", "hgroup", "hr", "html", "iframe", "img", "input", "isindex", "li", "link", "listing", "marquee", "menu", "meta", "nav", "noembed", "noframes", "noscript", "object", "ol", "p", "param", "plaintext", "pre", "script", "section", "select", "style", "summary", "table", "tbody", "td", "textarea", "tfoot", "th", "thead", "title", "tr", "ul", "wbr", "xmp"};
   public static final int MaxScopeSearchDepth = 100;
   private HtmlTreeBuilderState state;
   private HtmlTreeBuilderState originalState;
   private boolean baseUriSetFromDoc;
   @Nullable
   private Element headElement;
   @Nullable
   private FormElement formElement;
   @Nullable
   private Element contextElement;
   private ArrayList<Element> formattingElements;
   private ArrayList<HtmlTreeBuilderState> tmplInsertMode;
   private List<String> pendingTableCharacters;
   private Token.EndTag emptyEnd;
   private boolean framesetOk;
   private boolean fosterInserts;
   private boolean fragmentParsing;
   private static final int maxQueueDepth = 256;
   private String[] specificScopeTarget = new String[]{null};
   private static final int maxUsedFormattingElements = 12;

   ParseSettings defaultSettings() {
      return ParseSettings.htmlDefault;
   }

   HtmlTreeBuilder newInstance() {
      return new HtmlTreeBuilder();
   }

   @ParametersAreNonnullByDefault
   protected void initialiseParse(Reader input, String baseUri, Parser parser) {
      super.initialiseParse(input, baseUri, parser);
      this.state = HtmlTreeBuilderState.Initial;
      this.originalState = null;
      this.baseUriSetFromDoc = false;
      this.headElement = null;
      this.formElement = null;
      this.contextElement = null;
      this.formattingElements = new ArrayList();
      this.tmplInsertMode = new ArrayList();
      this.pendingTableCharacters = new ArrayList();
      this.emptyEnd = new Token.EndTag();
      this.framesetOk = true;
      this.fosterInserts = false;
      this.fragmentParsing = false;
   }

   List<Node> parseFragment(String inputFragment, @Nullable Element context, String baseUri, Parser parser) {
      this.state = HtmlTreeBuilderState.Initial;
      this.initialiseParse(new StringReader(inputFragment), baseUri, parser);
      this.contextElement = context;
      this.fragmentParsing = true;
      Element root = null;
      if (context != null) {
         if (context.ownerDocument() != null) {
            this.doc.quirksMode(context.ownerDocument().quirksMode());
         }

         String contextTag = context.normalName();
         byte var8 = -1;
         switch(contextTag.hashCode()) {
         case -1321546630:
            if (contextTag.equals("template")) {
               var8 = 10;
            }
            break;
         case -1191214428:
            if (contextTag.equals("iframe")) {
               var8 = 2;
            }
            break;
         case -1003243718:
            if (contextTag.equals("textarea")) {
               var8 = 1;
            }
            break;
         case -907685685:
            if (contextTag.equals("script")) {
               var8 = 7;
            }
            break;
         case 118807:
            if (contextTag.equals("xml")) {
               var8 = 6;
            }
            break;
         case 109780401:
            if (contextTag.equals("style")) {
               var8 = 5;
            }
            break;
         case 110371416:
            if (contextTag.equals("title")) {
               var8 = 0;
            }
            break;
         case 1192721831:
            if (contextTag.equals("noframes")) {
               var8 = 4;
            }
            break;
         case 1551550924:
            if (contextTag.equals("noscript")) {
               var8 = 8;
            }
            break;
         case 1973234167:
            if (contextTag.equals("plaintext")) {
               var8 = 9;
            }
            break;
         case 2115613112:
            if (contextTag.equals("noembed")) {
               var8 = 3;
            }
         }

         switch(var8) {
         case 0:
         case 1:
            this.tokeniser.transition(TokeniserState.Rcdata);
            break;
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
            this.tokeniser.transition(TokeniserState.Rawtext);
            break;
         case 7:
            this.tokeniser.transition(TokeniserState.ScriptData);
            break;
         case 8:
            this.tokeniser.transition(TokeniserState.Data);
            break;
         case 9:
            this.tokeniser.transition(TokeniserState.PLAINTEXT);
            break;
         case 10:
            this.tokeniser.transition(TokeniserState.Data);
            this.pushTemplateMode(HtmlTreeBuilderState.InTemplate);
            break;
         default:
            this.tokeniser.transition(TokeniserState.Data);
         }

         root = new Element(this.tagFor(contextTag, this.settings), baseUri);
         this.doc.appendChild(root);
         this.stack.add(root);
         this.resetInsertionMode();

         for(Element formSearch = context; formSearch != null; formSearch = formSearch.parent()) {
            if (formSearch instanceof FormElement) {
               this.formElement = (FormElement)formSearch;
               break;
            }
         }
      }

      this.runParser();
      if (context != null) {
         List<Node> nodes = root.siblingNodes();
         if (!nodes.isEmpty()) {
            root.insertChildren(-1, (Collection)nodes);
         }

         return root.childNodes();
      } else {
         return this.doc.childNodes();
      }
   }

   protected boolean process(Token token) {
      this.currentToken = token;
      return this.state.process(token, this);
   }

   boolean process(Token token, HtmlTreeBuilderState state) {
      this.currentToken = token;
      return state.process(token, this);
   }

   void transition(HtmlTreeBuilderState state) {
      this.state = state;
   }

   HtmlTreeBuilderState state() {
      return this.state;
   }

   void markInsertionMode() {
      this.originalState = this.state;
   }

   HtmlTreeBuilderState originalState() {
      return this.originalState;
   }

   void framesetOk(boolean framesetOk) {
      this.framesetOk = framesetOk;
   }

   boolean framesetOk() {
      return this.framesetOk;
   }

   Document getDocument() {
      return this.doc;
   }

   String getBaseUri() {
      return this.baseUri;
   }

   void maybeSetBaseUri(Element base) {
      if (!this.baseUriSetFromDoc) {
         String href = base.absUrl("href");
         if (href.length() != 0) {
            this.baseUri = href;
            this.baseUriSetFromDoc = true;
            this.doc.setBaseUri(href);
         }

      }
   }

   boolean isFragmentParsing() {
      return this.fragmentParsing;
   }

   void error(HtmlTreeBuilderState state) {
      if (this.parser.getErrors().canAddError()) {
         this.parser.getErrors().add(new ParseError(this.reader, "Unexpected %s token [%s] when in state [%s]", new Object[]{this.currentToken.tokenType(), this.currentToken, state}));
      }

   }

   Element insert(Token.StartTag startTag) {
      if (startTag.hasAttributes() && !startTag.attributes.isEmpty()) {
         int dupes = startTag.attributes.deduplicate(this.settings);
         if (dupes > 0) {
            this.error("Dropped duplicate attribute(s) in tag [%s]", new Object[]{startTag.normalName});
         }
      }

      Element el;
      if (startTag.isSelfClosing()) {
         el = this.insertEmpty(startTag);
         this.stack.add(el);
         this.tokeniser.transition(TokeniserState.Data);
         this.tokeniser.emit((Token)this.emptyEnd.reset().name(el.tagName()));
         return el;
      } else {
         el = new Element(this.tagFor(startTag.name(), this.settings), (String)null, this.settings.normalizeAttributes(startTag.attributes));
         this.insert(el, startTag);
         return el;
      }
   }

   Element insertStartTag(String startTagName) {
      Element el = new Element(this.tagFor(startTagName, this.settings), (String)null);
      this.insert(el);
      return el;
   }

   void insert(Element el) {
      this.insertNode(el, (Token)null);
      this.stack.add(el);
   }

   private void insert(Element el, @Nullable Token token) {
      this.insertNode(el, token);
      this.stack.add(el);
   }

   Element insertEmpty(Token.StartTag startTag) {
      Tag tag = this.tagFor(startTag.name(), this.settings);
      Element el = new Element(tag, (String)null, this.settings.normalizeAttributes(startTag.attributes));
      this.insertNode(el, startTag);
      if (startTag.isSelfClosing()) {
         if (tag.isKnownTag()) {
            if (!tag.isEmpty()) {
               this.tokeniser.error("Tag [%s] cannot be self closing; not a void tag", tag.normalName());
            }
         } else {
            tag.setSelfClosing();
         }
      }

      return el;
   }

   FormElement insertForm(Token.StartTag startTag, boolean onStack, boolean checkTemplateStack) {
      Tag tag = this.tagFor(startTag.name(), this.settings);
      FormElement el = new FormElement(tag, (String)null, this.settings.normalizeAttributes(startTag.attributes));
      if (checkTemplateStack) {
         if (!this.onStack("template")) {
            this.setFormElement(el);
         }
      } else {
         this.setFormElement(el);
      }

      this.insertNode(el, startTag);
      if (onStack) {
         this.stack.add(el);
      }

      return el;
   }

   void insert(Token.Comment commentToken) {
      Comment comment = new Comment(commentToken.getData());
      this.insertNode(comment, commentToken);
   }

   void insert(Token.Character characterToken) {
      Element el = this.currentElement();
      String tagName = el.normalName();
      String data = characterToken.getData();
      Object node;
      if (characterToken.isCData()) {
         node = new CDataNode(data);
      } else if (this.isContentForTagData(tagName)) {
         node = new DataNode(data);
      } else {
         node = new TextNode(data);
      }

      el.appendChild((Node)node);
      this.onNodeInserted((Node)node, characterToken);
   }

   private void insertNode(Node node, @Nullable Token token) {
      if (this.stack.isEmpty()) {
         this.doc.appendChild(node);
      } else if (this.isFosterInserts() && StringUtil.inSorted(this.currentElement().normalName(), HtmlTreeBuilderState.Constants.InTableFoster)) {
         this.insertInFosterParent(node);
      } else {
         this.currentElement().appendChild(node);
      }

      if (node instanceof Element && ((Element)node).tag().isFormListed() && this.formElement != null) {
         this.formElement.addElement((Element)node);
      }

      this.onNodeInserted(node, token);
   }

   Element pop() {
      int size = this.stack.size();
      return (Element)this.stack.remove(size - 1);
   }

   void push(Element element) {
      this.stack.add(element);
   }

   ArrayList<Element> getStack() {
      return this.stack;
   }

   boolean onStack(Element el) {
      return onStack(this.stack, el);
   }

   boolean onStack(String elName) {
      return this.getFromStack(elName) != null;
   }

   private static boolean onStack(ArrayList<Element> queue, Element element) {
      int bottom = queue.size() - 1;
      int upper = bottom >= 256 ? bottom - 256 : 0;

      for(int pos = bottom; pos >= upper; --pos) {
         Element next = (Element)queue.get(pos);
         if (next == element) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   Element getFromStack(String elName) {
      int bottom = this.stack.size() - 1;
      int upper = bottom >= 256 ? bottom - 256 : 0;

      for(int pos = bottom; pos >= upper; --pos) {
         Element next = (Element)this.stack.get(pos);
         if (next.normalName().equals(elName)) {
            return next;
         }
      }

      return null;
   }

   boolean removeFromStack(Element el) {
      for(int pos = this.stack.size() - 1; pos >= 0; --pos) {
         Element next = (Element)this.stack.get(pos);
         if (next == el) {
            this.stack.remove(pos);
            return true;
         }
      }

      return false;
   }

   @Nullable
   Element popStackToClose(String elName) {
      for(int pos = this.stack.size() - 1; pos >= 0; --pos) {
         Element el = (Element)this.stack.get(pos);
         this.stack.remove(pos);
         if (el.normalName().equals(elName)) {
            if (this.currentToken instanceof Token.EndTag) {
               this.onNodeClosed(el, this.currentToken);
            }

            return el;
         }
      }

      return null;
   }

   void popStackToClose(String... elNames) {
      for(int pos = this.stack.size() - 1; pos >= 0; --pos) {
         Element next = (Element)this.stack.get(pos);
         this.stack.remove(pos);
         if (StringUtil.inSorted(next.normalName(), elNames)) {
            break;
         }
      }

   }

   void popStackToBefore(String elName) {
      for(int pos = this.stack.size() - 1; pos >= 0; --pos) {
         Element next = (Element)this.stack.get(pos);
         if (next.normalName().equals(elName)) {
            break;
         }

         this.stack.remove(pos);
      }

   }

   void clearStackToTableContext() {
      this.clearStackToContext("table", "template");
   }

   void clearStackToTableBodyContext() {
      this.clearStackToContext("tbody", "tfoot", "thead", "template");
   }

   void clearStackToTableRowContext() {
      this.clearStackToContext("tr", "template");
   }

   private void clearStackToContext(String... nodeNames) {
      for(int pos = this.stack.size() - 1; pos >= 0; --pos) {
         Element next = (Element)this.stack.get(pos);
         if (StringUtil.in(next.normalName(), nodeNames) || next.normalName().equals("html")) {
            break;
         }

         this.stack.remove(pos);
      }

   }

   @Nullable
   Element aboveOnStack(Element el) {
      assert this.onStack(el);

      for(int pos = this.stack.size() - 1; pos >= 0; --pos) {
         Element next = (Element)this.stack.get(pos);
         if (next == el) {
            return (Element)this.stack.get(pos - 1);
         }
      }

      return null;
   }

   void insertOnStackAfter(Element after, Element in) {
      int i = this.stack.lastIndexOf(after);
      Validate.isTrue(i != -1);
      this.stack.add(i + 1, in);
   }

   void replaceOnStack(Element out, Element in) {
      this.replaceInQueue(this.stack, out, in);
   }

   private void replaceInQueue(ArrayList<Element> queue, Element out, Element in) {
      int i = queue.lastIndexOf(out);
      Validate.isTrue(i != -1);
      queue.set(i, in);
   }

   boolean resetInsertionMode() {
      boolean last = false;
      int bottom = this.stack.size() - 1;
      int upper = bottom >= 256 ? bottom - 256 : 0;
      HtmlTreeBuilderState origState = this.state;
      if (this.stack.size() == 0) {
         this.transition(HtmlTreeBuilderState.InBody);
      }

      for(int pos = bottom; pos >= upper; --pos) {
         Element node = (Element)this.stack.get(pos);
         if (pos == upper) {
            last = true;
            if (this.fragmentParsing) {
               node = this.contextElement;
            }
         }

         String name = node != null ? node.normalName() : "";
         byte var9 = -1;
         switch(name.hashCode()) {
         case -1644953643:
            if (name.equals("frameset")) {
               var9 = 13;
            }
            break;
         case -1321546630:
            if (name.equals("template")) {
               var9 = 10;
            }
            break;
         case -906021636:
            if (name.equals("select")) {
               var9 = 0;
            }
            break;
         case -636197633:
            if (name.equals("colgroup")) {
               var9 = 8;
            }
            break;
         case 3696:
            if (name.equals("td")) {
               var9 = 1;
            }
            break;
         case 3700:
            if (name.equals("th")) {
               var9 = 2;
            }
            break;
         case 3710:
            if (name.equals("tr")) {
               var9 = 3;
            }
            break;
         case 3029410:
            if (name.equals("body")) {
               var9 = 12;
            }
            break;
         case 3198432:
            if (name.equals("head")) {
               var9 = 11;
            }
            break;
         case 3213227:
            if (name.equals("html")) {
               var9 = 14;
            }
            break;
         case 110115790:
            if (name.equals("table")) {
               var9 = 9;
            }
            break;
         case 110157846:
            if (name.equals("tbody")) {
               var9 = 4;
            }
            break;
         case 110277346:
            if (name.equals("tfoot")) {
               var9 = 6;
            }
            break;
         case 110326868:
            if (name.equals("thead")) {
               var9 = 5;
            }
            break;
         case 552573414:
            if (name.equals("caption")) {
               var9 = 7;
            }
         }

         switch(var9) {
         case 0:
            this.transition(HtmlTreeBuilderState.InSelect);
            return this.state != origState;
         case 1:
         case 2:
            if (!last) {
               this.transition(HtmlTreeBuilderState.InCell);
               return this.state != origState;
            }
            break;
         case 3:
            this.transition(HtmlTreeBuilderState.InRow);
            return this.state != origState;
         case 4:
         case 5:
         case 6:
            this.transition(HtmlTreeBuilderState.InTableBody);
            return this.state != origState;
         case 7:
            this.transition(HtmlTreeBuilderState.InCaption);
            return this.state != origState;
         case 8:
            this.transition(HtmlTreeBuilderState.InColumnGroup);
            return this.state != origState;
         case 9:
            this.transition(HtmlTreeBuilderState.InTable);
            return this.state != origState;
         case 10:
            HtmlTreeBuilderState tmplState = this.currentTemplateMode();
            Validate.notNull(tmplState, "Bug: no template insertion mode on stack!");
            this.transition(tmplState);
            return this.state != origState;
         case 11:
            if (!last) {
               this.transition(HtmlTreeBuilderState.InHead);
               return this.state != origState;
            }
            break;
         case 12:
            this.transition(HtmlTreeBuilderState.InBody);
            return this.state != origState;
         case 13:
            this.transition(HtmlTreeBuilderState.InFrameset);
            return this.state != origState;
         case 14:
            this.transition(this.headElement == null ? HtmlTreeBuilderState.BeforeHead : HtmlTreeBuilderState.AfterHead);
            return this.state != origState;
         }

         if (last) {
            this.transition(HtmlTreeBuilderState.InBody);
            break;
         }
      }

      return this.state != origState;
   }

   void resetBody() {
      if (!this.onStack("body")) {
         this.stack.add(this.doc.body());
      }

      this.transition(HtmlTreeBuilderState.InBody);
   }

   private boolean inSpecificScope(String targetName, String[] baseTypes, String[] extraTypes) {
      this.specificScopeTarget[0] = targetName;
      return this.inSpecificScope(this.specificScopeTarget, baseTypes, extraTypes);
   }

   private boolean inSpecificScope(String[] targetNames, String[] baseTypes, String[] extraTypes) {
      int bottom = this.stack.size() - 1;
      int top = bottom > 100 ? bottom - 100 : 0;

      for(int pos = bottom; pos >= top; --pos) {
         String elName = ((Element)this.stack.get(pos)).normalName();
         if (StringUtil.inSorted(elName, targetNames)) {
            return true;
         }

         if (StringUtil.inSorted(elName, baseTypes)) {
            return false;
         }

         if (extraTypes != null && StringUtil.inSorted(elName, extraTypes)) {
            return false;
         }
      }

      return false;
   }

   boolean inScope(String[] targetNames) {
      return this.inSpecificScope((String[])targetNames, TagsSearchInScope, (String[])null);
   }

   boolean inScope(String targetName) {
      return this.inScope(targetName, (String[])null);
   }

   boolean inScope(String targetName, String[] extras) {
      return this.inSpecificScope(targetName, TagsSearchInScope, extras);
   }

   boolean inListItemScope(String targetName) {
      return this.inScope(targetName, TagSearchList);
   }

   boolean inButtonScope(String targetName) {
      return this.inScope(targetName, TagSearchButton);
   }

   boolean inTableScope(String targetName) {
      return this.inSpecificScope((String)targetName, TagSearchTableScope, (String[])null);
   }

   boolean inSelectScope(String targetName) {
      for(int pos = this.stack.size() - 1; pos >= 0; --pos) {
         Element el = (Element)this.stack.get(pos);
         String elName = el.normalName();
         if (elName.equals(targetName)) {
            return true;
         }

         if (!StringUtil.inSorted(elName, TagSearchSelectScope)) {
            return false;
         }
      }

      Validate.fail("Should not be reachable");
      return false;
   }

   void setHeadElement(Element headElement) {
      this.headElement = headElement;
   }

   Element getHeadElement() {
      return this.headElement;
   }

   boolean isFosterInserts() {
      return this.fosterInserts;
   }

   void setFosterInserts(boolean fosterInserts) {
      this.fosterInserts = fosterInserts;
   }

   @Nullable
   FormElement getFormElement() {
      return this.formElement;
   }

   void setFormElement(FormElement formElement) {
      this.formElement = formElement;
   }

   void newPendingTableCharacters() {
      this.pendingTableCharacters = new ArrayList();
   }

   List<String> getPendingTableCharacters() {
      return this.pendingTableCharacters;
   }

   void generateImpliedEndTags(String excludeTag) {
      while(StringUtil.inSorted(this.currentElement().normalName(), TagSearchEndTags) && (excludeTag == null || !this.currentElementIs(excludeTag))) {
         this.pop();
      }

   }

   void generateImpliedEndTags() {
      this.generateImpliedEndTags(false);
   }

   void generateImpliedEndTags(boolean thorough) {
      String[] search = thorough ? TagThoroughSearchEndTags : TagSearchEndTags;

      while(StringUtil.inSorted(this.currentElement().normalName(), search)) {
         this.pop();
      }

   }

   void closeElement(String name) {
      this.generateImpliedEndTags(name);
      if (!name.equals(this.currentElement().normalName())) {
         this.error(this.state());
      }

      this.popStackToClose(name);
   }

   boolean isSpecial(Element el) {
      String name = el.normalName();
      return StringUtil.inSorted(name, TagSearchSpecial);
   }

   Element lastFormattingElement() {
      return this.formattingElements.size() > 0 ? (Element)this.formattingElements.get(this.formattingElements.size() - 1) : null;
   }

   int positionOfElement(Element el) {
      for(int i = 0; i < this.formattingElements.size(); ++i) {
         if (el == this.formattingElements.get(i)) {
            return i;
         }
      }

      return -1;
   }

   Element removeLastFormattingElement() {
      int size = this.formattingElements.size();
      return size > 0 ? (Element)this.formattingElements.remove(size - 1) : null;
   }

   void pushActiveFormattingElements(Element in) {
      this.checkActiveFormattingElements(in);
      this.formattingElements.add(in);
   }

   void pushWithBookmark(Element in, int bookmark) {
      this.checkActiveFormattingElements(in);

      try {
         this.formattingElements.add(bookmark, in);
      } catch (IndexOutOfBoundsException var4) {
         this.formattingElements.add(in);
      }

   }

   void checkActiveFormattingElements(Element in) {
      int numSeen = 0;
      int size = this.formattingElements.size() - 1;
      int ceil = size - 12;
      if (ceil < 0) {
         ceil = 0;
      }

      for(int pos = size; pos >= ceil; --pos) {
         Element el = (Element)this.formattingElements.get(pos);
         if (el == null) {
            break;
         }

         if (this.isSameFormattingElement(in, el)) {
            ++numSeen;
         }

         if (numSeen == 3) {
            this.formattingElements.remove(pos);
            break;
         }
      }

   }

   private boolean isSameFormattingElement(Element a, Element b) {
      return a.normalName().equals(b.normalName()) && a.attributes().equals(b.attributes());
   }

   void reconstructFormattingElements() {
      if (this.stack.size() <= 256) {
         Element last = this.lastFormattingElement();
         if (last != null && !this.onStack(last)) {
            Element entry = last;
            int size = this.formattingElements.size();
            int ceil = size - 12;
            if (ceil < 0) {
               ceil = 0;
            }

            int pos = size - 1;
            boolean skip = false;

            do {
               if (pos == ceil) {
                  skip = true;
                  break;
               }

               --pos;
               entry = (Element)this.formattingElements.get(pos);
            } while(entry != null && !this.onStack(entry));

            do {
               if (!skip) {
                  ++pos;
                  entry = (Element)this.formattingElements.get(pos);
               }

               Validate.notNull(entry);
               skip = false;
               Element newEl = new Element(this.tagFor(entry.normalName(), this.settings), (String)null, entry.attributes().clone());
               this.insert(newEl);
               this.formattingElements.set(pos, newEl);
            } while(pos != size - 1);

         }
      }
   }

   void clearFormattingElementsToLastMarker() {
      while(true) {
         if (!this.formattingElements.isEmpty()) {
            Element el = this.removeLastFormattingElement();
            if (el != null) {
               continue;
            }
         }

         return;
      }
   }

   void removeFromActiveFormattingElements(Element el) {
      for(int pos = this.formattingElements.size() - 1; pos >= 0; --pos) {
         Element next = (Element)this.formattingElements.get(pos);
         if (next == el) {
            this.formattingElements.remove(pos);
            break;
         }
      }

   }

   boolean isInActiveFormattingElements(Element el) {
      return onStack(this.formattingElements, el);
   }

   Element getActiveFormattingElement(String nodeName) {
      for(int pos = this.formattingElements.size() - 1; pos >= 0; --pos) {
         Element next = (Element)this.formattingElements.get(pos);
         if (next == null) {
            break;
         }

         if (next.normalName().equals(nodeName)) {
            return next;
         }
      }

      return null;
   }

   void replaceActiveFormattingElement(Element out, Element in) {
      this.replaceInQueue(this.formattingElements, out, in);
   }

   void insertMarkerToFormattingElements() {
      this.formattingElements.add((Object)null);
   }

   void insertInFosterParent(Node in) {
      Element lastTable = this.getFromStack("table");
      boolean isLastTableParent = false;
      Element fosterParent;
      if (lastTable != null) {
         if (lastTable.parent() != null) {
            fosterParent = lastTable.parent();
            isLastTableParent = true;
         } else {
            fosterParent = this.aboveOnStack(lastTable);
         }
      } else {
         fosterParent = (Element)this.stack.get(0);
      }

      if (isLastTableParent) {
         Validate.notNull(lastTable);
         lastTable.before(in);
      } else {
         fosterParent.appendChild(in);
      }

   }

   void pushTemplateMode(HtmlTreeBuilderState state) {
      this.tmplInsertMode.add(state);
   }

   @Nullable
   HtmlTreeBuilderState popTemplateMode() {
      return this.tmplInsertMode.size() > 0 ? (HtmlTreeBuilderState)this.tmplInsertMode.remove(this.tmplInsertMode.size() - 1) : null;
   }

   int templateModeSize() {
      return this.tmplInsertMode.size();
   }

   @Nullable
   HtmlTreeBuilderState currentTemplateMode() {
      return this.tmplInsertMode.size() > 0 ? (HtmlTreeBuilderState)this.tmplInsertMode.get(this.tmplInsertMode.size() - 1) : null;
   }

   public String toString() {
      return "TreeBuilder{currentToken=" + this.currentToken + ", state=" + this.state + ", currentElement=" + this.currentElement() + '}';
   }

   protected boolean isContentForTagData(String normalName) {
      return normalName.equals("script") || normalName.equals("style");
   }
}
