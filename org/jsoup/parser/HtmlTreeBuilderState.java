package org.jsoup.parser;

import java.util.ArrayList;
import java.util.Iterator;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;

enum HtmlTreeBuilderState {
   Initial {
      boolean process(Token t, HtmlTreeBuilder tb) {
         if (HtmlTreeBuilderState.isWhitespace(t)) {
            return true;
         } else {
            if (t.isComment()) {
               tb.insert(t.asComment());
            } else {
               if (!t.isDoctype()) {
                  tb.transition(BeforeHtml);
                  return tb.process(t);
               }

               Token.Doctype d = t.asDoctype();
               DocumentType doctype = new DocumentType(tb.settings.normalizeTag(d.getName()), d.getPublicIdentifier(), d.getSystemIdentifier());
               doctype.setPubSysKey(d.getPubSysKey());
               tb.getDocument().appendChild(doctype);
               tb.onNodeInserted(doctype, t);
               if (d.isForceQuirks()) {
                  tb.getDocument().quirksMode(Document.QuirksMode.quirks);
               }

               tb.transition(BeforeHtml);
            }

            return true;
         }
      }
   },
   BeforeHtml {
      boolean process(Token t, HtmlTreeBuilder tb) {
         if (t.isDoctype()) {
            tb.error(this);
            return false;
         } else {
            if (t.isComment()) {
               tb.insert(t.asComment());
            } else if (HtmlTreeBuilderState.isWhitespace(t)) {
               tb.insert(t.asCharacter());
            } else {
               if (!t.isStartTag() || !t.asStartTag().normalName().equals("html")) {
                  if (t.isEndTag() && StringUtil.inSorted(t.asEndTag().normalName(), HtmlTreeBuilderState.Constants.BeforeHtmlToHead)) {
                     return this.anythingElse(t, tb);
                  }

                  if (t.isEndTag()) {
                     tb.error(this);
                     return false;
                  }

                  return this.anythingElse(t, tb);
               }

               tb.insert(t.asStartTag());
               tb.transition(BeforeHead);
            }

            return true;
         }
      }

      private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
         tb.insertStartTag("html");
         tb.transition(BeforeHead);
         return tb.process(t);
      }
   },
   BeforeHead {
      boolean process(Token t, HtmlTreeBuilder tb) {
         if (HtmlTreeBuilderState.isWhitespace(t)) {
            tb.insert(t.asCharacter());
         } else if (t.isComment()) {
            tb.insert(t.asComment());
         } else {
            if (t.isDoctype()) {
               tb.error(this);
               return false;
            }

            if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
               return InBody.process(t, tb);
            }

            if (!t.isStartTag() || !t.asStartTag().normalName().equals("head")) {
               if (t.isEndTag() && StringUtil.inSorted(t.asEndTag().normalName(), HtmlTreeBuilderState.Constants.BeforeHtmlToHead)) {
                  tb.processStartTag("head");
                  return tb.process(t);
               }

               if (t.isEndTag()) {
                  tb.error(this);
                  return false;
               }

               tb.processStartTag("head");
               return tb.process(t);
            }

            Element head = tb.insert(t.asStartTag());
            tb.setHeadElement(head);
            tb.transition(InHead);
         }

         return true;
      }
   },
   InHead {
      boolean process(Token t, HtmlTreeBuilder tb) {
         if (HtmlTreeBuilderState.isWhitespace(t)) {
            tb.insert(t.asCharacter());
            return true;
         } else {
            String name;
            switch(t.type) {
            case Comment:
               tb.insert(t.asComment());
               break;
            case Doctype:
               tb.error(this);
               return false;
            case StartTag:
               Token.StartTag start = t.asStartTag();
               name = start.normalName();
               if (name.equals("html")) {
                  return InBody.process(t, tb);
               }

               if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InHeadEmpty)) {
                  Element el = tb.insertEmpty(start);
                  if (name.equals("base") && el.hasAttr("href")) {
                     tb.maybeSetBaseUri(el);
                  }
               } else if (name.equals("meta")) {
                  tb.insertEmpty(start);
               } else if (name.equals("title")) {
                  HtmlTreeBuilderState.handleRcData(start, tb);
               } else if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InHeadRaw)) {
                  HtmlTreeBuilderState.handleRawtext(start, tb);
               } else if (name.equals("noscript")) {
                  tb.insert(start);
                  tb.transition(InHeadNoscript);
               } else if (name.equals("script")) {
                  tb.tokeniser.transition(TokeniserState.ScriptData);
                  tb.markInsertionMode();
                  tb.transition(Text);
                  tb.insert(start);
               } else {
                  if (name.equals("head")) {
                     tb.error(this);
                     return false;
                  }

                  if (!name.equals("template")) {
                     return this.anythingElse(t, tb);
                  }

                  tb.insert(start);
                  tb.insertMarkerToFormattingElements();
                  tb.framesetOk(false);
                  tb.transition(InTemplate);
                  tb.pushTemplateMode(InTemplate);
               }
               break;
            case EndTag:
               Token.EndTag end = t.asEndTag();
               name = end.normalName();
               if (name.equals("head")) {
                  tb.pop();
                  tb.transition(AfterHead);
               } else {
                  if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InHeadEnd)) {
                     return this.anythingElse(t, tb);
                  }

                  if (!name.equals("template")) {
                     tb.error(this);
                     return false;
                  }

                  if (!tb.onStack(name)) {
                     tb.error(this);
                  } else {
                     tb.generateImpliedEndTags(true);
                     if (!name.equals(tb.currentElement().normalName())) {
                        tb.error(this);
                     }

                     tb.popStackToClose(name);
                     tb.clearFormattingElementsToLastMarker();
                     tb.popTemplateMode();
                     tb.resetInsertionMode();
                  }
               }
               break;
            default:
               return this.anythingElse(t, tb);
            }

            return true;
         }
      }

      private boolean anythingElse(Token t, TreeBuilder tb) {
         tb.processEndTag("head");
         return tb.process(t);
      }
   },
   InHeadNoscript {
      boolean process(Token t, HtmlTreeBuilder tb) {
         if (t.isDoctype()) {
            tb.error(this);
         } else {
            if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
               return tb.process(t, InBody);
            }

            if (!t.isEndTag() || !t.asEndTag().normalName().equals("noscript")) {
               if (HtmlTreeBuilderState.isWhitespace(t) || t.isComment() || t.isStartTag() && StringUtil.inSorted(t.asStartTag().normalName(), HtmlTreeBuilderState.Constants.InHeadNoScriptHead)) {
                  return tb.process(t, InHead);
               }

               if (t.isEndTag() && t.asEndTag().normalName().equals("br")) {
                  return this.anythingElse(t, tb);
               }

               if ((!t.isStartTag() || !StringUtil.inSorted(t.asStartTag().normalName(), HtmlTreeBuilderState.Constants.InHeadNoscriptIgnore)) && !t.isEndTag()) {
                  return this.anythingElse(t, tb);
               }

               tb.error(this);
               return false;
            }

            tb.pop();
            tb.transition(InHead);
         }

         return true;
      }

      private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
         tb.error(this);
         tb.insert((new Token.Character()).data(t.toString()));
         return true;
      }
   },
   AfterHead {
      boolean process(Token t, HtmlTreeBuilder tb) {
         if (HtmlTreeBuilderState.isWhitespace(t)) {
            tb.insert(t.asCharacter());
         } else if (t.isComment()) {
            tb.insert(t.asComment());
         } else if (t.isDoctype()) {
            tb.error(this);
         } else if (t.isStartTag()) {
            Token.StartTag startTag = t.asStartTag();
            String namex = startTag.normalName();
            if (namex.equals("html")) {
               return tb.process(t, InBody);
            }

            if (namex.equals("body")) {
               tb.insert(startTag);
               tb.framesetOk(false);
               tb.transition(InBody);
            } else if (namex.equals("frameset")) {
               tb.insert(startTag);
               tb.transition(InFrameset);
            } else if (StringUtil.inSorted(namex, HtmlTreeBuilderState.Constants.InBodyStartToHead)) {
               tb.error(this);
               Element head = tb.getHeadElement();
               tb.push(head);
               tb.process(t, InHead);
               tb.removeFromStack(head);
            } else {
               if (namex.equals("head")) {
                  tb.error(this);
                  return false;
               }

               this.anythingElse(t, tb);
            }
         } else if (t.isEndTag()) {
            String name = t.asEndTag().normalName();
            if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.AfterHeadBody)) {
               this.anythingElse(t, tb);
            } else {
               if (!name.equals("template")) {
                  tb.error(this);
                  return false;
               }

               tb.process(t, InHead);
            }
         } else {
            this.anythingElse(t, tb);
         }

         return true;
      }

      private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
         tb.processStartTag("body");
         tb.framesetOk(true);
         return tb.process(t);
      }
   },
   InBody {
      private static final int MaxStackScan = 24;

      boolean process(Token t, HtmlTreeBuilder tb) {
         switch(t.type) {
         case Comment:
            tb.insert(t.asComment());
            break;
         case Doctype:
            tb.error(this);
            return false;
         case StartTag:
            return this.inBodyStartTag(t, tb);
         case EndTag:
            return this.inBodyEndTag(t, tb);
         case Character:
            Token.Character c = t.asCharacter();
            if (c.getData().equals(HtmlTreeBuilderState.nullString)) {
               tb.error(this);
               return false;
            }

            if (tb.framesetOk() && HtmlTreeBuilderState.isWhitespace((Token)c)) {
               tb.reconstructFormattingElements();
               tb.insert(c);
            } else {
               tb.reconstructFormattingElements();
               tb.insert(c);
               tb.framesetOk(false);
            }
            break;
         case EOF:
            if (tb.templateModeSize() > 0) {
               return tb.process(t, InTemplate);
            }
         }

         return true;
      }

      private boolean inBodyStartTag(Token t, HtmlTreeBuilder tb) {
         Token.StartTag startTag = t.asStartTag();
         String name = startTag.normalName();
         byte var8 = -1;
         switch(name.hashCode()) {
         case -1644953643:
            if (name.equals("frameset")) {
               var8 = 5;
            }
            break;
         case -1377687758:
            if (name.equals("button")) {
               var8 = 8;
            }
            break;
         case -1191214428:
            if (name.equals("iframe")) {
               var8 = 17;
            }
            break;
         case -1134665583:
            if (name.equals("keygen")) {
               var8 = 40;
            }
            break;
         case -1010136971:
            if (name.equals("option")) {
               var8 = 33;
            }
            break;
         case -1003243718:
            if (name.equals("textarea")) {
               var8 = 15;
            }
            break;
         case -906021636:
            if (name.equals("select")) {
               var8 = 19;
            }
            break;
         case -891985998:
            if (name.equals("strike")) {
               var8 = 50;
            }
            break;
         case -891980137:
            if (name.equals("strong")) {
               var8 = 51;
            }
            break;
         case -80773204:
            if (name.equals("optgroup")) {
               var8 = 32;
            }
            break;
         case 97:
            if (name.equals("a")) {
               var8 = 0;
            }
            break;
         case 98:
            if (name.equals("b")) {
               var8 = 42;
            }
            break;
         case 105:
            if (name.equals("i")) {
               var8 = 47;
            }
            break;
         case 115:
            if (name.equals("s")) {
               var8 = 48;
            }
            break;
         case 117:
            if (name.equals("u")) {
               var8 = 53;
            }
            break;
         case 3152:
            if (name.equals("br")) {
               var8 = 37;
            }
            break;
         case 3200:
            if (name.equals("dd")) {
               var8 = 30;
            }
            break;
         case 3216:
            if (name.equals("dt")) {
               var8 = 31;
            }
            break;
         case 3240:
            if (name.equals("em")) {
               var8 = 45;
            }
            break;
         case 3273:
            if (name.equals("h1")) {
               var8 = 22;
            }
            break;
         case 3274:
            if (name.equals("h2")) {
               var8 = 23;
            }
            break;
         case 3275:
            if (name.equals("h3")) {
               var8 = 24;
            }
            break;
         case 3276:
            if (name.equals("h4")) {
               var8 = 25;
            }
            break;
         case 3277:
            if (name.equals("h5")) {
               var8 = 26;
            }
            break;
         case 3278:
            if (name.equals("h6")) {
               var8 = 27;
            }
            break;
         case 3338:
            if (name.equals("hr")) {
               var8 = 12;
            }
            break;
         case 3453:
            if (name.equals("li")) {
               var8 = 2;
            }
            break;
         case 3646:
            if (name.equals("rp")) {
               var8 = 34;
            }
            break;
         case 3650:
            if (name.equals("rt")) {
               var8 = 35;
            }
            break;
         case 3712:
            if (name.equals("tt")) {
               var8 = 52;
            }
            break;
         case 97536:
            if (name.equals("big")) {
               var8 = 43;
            }
            break;
         case 104387:
            if (name.equals("img")) {
               var8 = 39;
            }
            break;
         case 111267:
            if (name.equals("pre")) {
               var8 = 28;
            }
            break;
         case 114276:
            if (name.equals("svg")) {
               var8 = 21;
            }
            break;
         case 117511:
            if (name.equals("wbr")) {
               var8 = 41;
            }
            break;
         case 118811:
            if (name.equals("xmp")) {
               var8 = 16;
            }
            break;
         case 3002509:
            if (name.equals("area")) {
               var8 = 36;
            }
            break;
         case 3029410:
            if (name.equals("body")) {
               var8 = 4;
            }
            break;
         case 3059181:
            if (name.equals("code")) {
               var8 = 44;
            }
            break;
         case 3148879:
            if (name.equals("font")) {
               var8 = 46;
            }
            break;
         case 3148996:
            if (name.equals("form")) {
               var8 = 6;
            }
            break;
         case 3213227:
            if (name.equals("html")) {
               var8 = 3;
            }
            break;
         case 3344136:
            if (name.equals("math")) {
               var8 = 20;
            }
            break;
         case 3386833:
            if (name.equals("nobr")) {
               var8 = 9;
            }
            break;
         case 3536714:
            if (name.equals("span")) {
               var8 = 1;
            }
            break;
         case 96620249:
            if (name.equals("embed")) {
               var8 = 38;
            }
            break;
         case 100313435:
            if (name.equals("image")) {
               var8 = 13;
            }
            break;
         case 100358090:
            if (name.equals("input")) {
               var8 = 11;
            }
            break;
         case 109548807:
            if (name.equals("small")) {
               var8 = 49;
            }
            break;
         case 110115790:
            if (name.equals("table")) {
               var8 = 10;
            }
            break;
         case 181975684:
            if (name.equals("listing")) {
               var8 = 29;
            }
            break;
         case 1973234167:
            if (name.equals("plaintext")) {
               var8 = 7;
            }
            break;
         case 2091304424:
            if (name.equals("isindex")) {
               var8 = 14;
            }
            break;
         case 2115613112:
            if (name.equals("noembed")) {
               var8 = 18;
            }
         }

         ArrayList stack;
         Element el;
         Element html;
         Iterator var19;
         Attribute attribute;
         switch(var8) {
         case 0:
            if (tb.getActiveFormattingElement("a") != null) {
               tb.error(this);
               tb.processEndTag("a");
               html = tb.getFromStack("a");
               if (html != null) {
                  tb.removeFromActiveFormattingElements(html);
                  tb.removeFromStack(html);
               }
            }

            tb.reconstructFormattingElements();
            el = tb.insert(startTag);
            tb.pushActiveFormattingElements(el);
            break;
         case 1:
            tb.reconstructFormattingElements();
            tb.insert(startTag);
            break;
         case 2:
            tb.framesetOk(false);
            stack = tb.getStack();

            for(int ix = stack.size() - 1; ix > 0; --ix) {
               el = (Element)stack.get(ix);
               if (el.normalName().equals("li")) {
                  tb.processEndTag("li");
                  break;
               }

               if (tb.isSpecial(el) && !StringUtil.inSorted(el.normalName(), HtmlTreeBuilderState.Constants.InBodyStartLiBreakers)) {
                  break;
               }
            }

            if (tb.inButtonScope("p")) {
               tb.processEndTag("p");
            }

            tb.insert(startTag);
            break;
         case 3:
            tb.error(this);
            if (tb.onStack("template")) {
               return false;
            }

            stack = tb.getStack();
            if (stack.size() > 0) {
               html = (Element)tb.getStack().get(0);
               if (startTag.hasAttributes()) {
                  var19 = startTag.attributes.iterator();

                  while(var19.hasNext()) {
                     attribute = (Attribute)var19.next();
                     if (!html.hasAttr(attribute.getKey())) {
                        html.attributes().put(attribute);
                     }
                  }
               }
            }
            break;
         case 4:
            tb.error(this);
            stack = tb.getStack();
            if (stack.size() == 1 || stack.size() > 2 && !((Element)stack.get(1)).normalName().equals("body") || tb.onStack("template")) {
               return false;
            }

            tb.framesetOk(false);
            if (startTag.hasAttributes() && (html = tb.getFromStack("body")) != null) {
               var19 = startTag.attributes.iterator();

               while(var19.hasNext()) {
                  attribute = (Attribute)var19.next();
                  if (!html.hasAttr(attribute.getKey())) {
                     html.attributes().put(attribute);
                  }
               }
            }
            break;
         case 5:
            tb.error(this);
            stack = tb.getStack();
            if (stack.size() == 1 || stack.size() > 2 && !((Element)stack.get(1)).normalName().equals("body")) {
               return false;
            }

            if (!tb.framesetOk()) {
               return false;
            }

            html = (Element)stack.get(1);
            if (html.parent() != null) {
               html.remove();
            }

            while(stack.size() > 1) {
               stack.remove(stack.size() - 1);
            }

            tb.insert(startTag);
            tb.transition(InFrameset);
            break;
         case 6:
            if (tb.getFormElement() != null && !tb.onStack("template")) {
               tb.error(this);
               return false;
            }

            if (tb.inButtonScope("p")) {
               tb.closeElement("p");
            }

            tb.insertForm(startTag, true, true);
            break;
         case 7:
            if (tb.inButtonScope("p")) {
               tb.processEndTag("p");
            }

            tb.insert(startTag);
            tb.tokeniser.transition(TokeniserState.PLAINTEXT);
            break;
         case 8:
            if (tb.inButtonScope("button")) {
               tb.error(this);
               tb.processEndTag("button");
               tb.process(startTag);
            } else {
               tb.reconstructFormattingElements();
               tb.insert(startTag);
               tb.framesetOk(false);
            }
            break;
         case 9:
            tb.reconstructFormattingElements();
            if (tb.inScope("nobr")) {
               tb.error(this);
               tb.processEndTag("nobr");
               tb.reconstructFormattingElements();
            }

            el = tb.insert(startTag);
            tb.pushActiveFormattingElements(el);
            break;
         case 10:
            if (tb.getDocument().quirksMode() != Document.QuirksMode.quirks && tb.inButtonScope("p")) {
               tb.processEndTag("p");
            }

            tb.insert(startTag);
            tb.framesetOk(false);
            tb.transition(InTable);
            break;
         case 11:
            tb.reconstructFormattingElements();
            el = tb.insertEmpty(startTag);
            if (!el.attr("type").equalsIgnoreCase("hidden")) {
               tb.framesetOk(false);
            }
            break;
         case 12:
            if (tb.inButtonScope("p")) {
               tb.processEndTag("p");
            }

            tb.insertEmpty(startTag);
            tb.framesetOk(false);
            break;
         case 13:
            if (tb.getFromStack("svg") == null) {
               return tb.process(startTag.name("img"));
            }

            tb.insert(startTag);
            break;
         case 14:
            tb.error(this);
            if (tb.getFormElement() != null) {
               return false;
            }

            tb.processStartTag("form");
            if (startTag.hasAttribute("action")) {
               Element form = tb.getFormElement();
               if (form != null && startTag.hasAttribute("action")) {
                  String action = startTag.attributes.get("action");
                  form.attributes().put("action", action);
               }
            }

            tb.processStartTag("hr");
            tb.processStartTag("label");
            String prompt = startTag.hasAttribute("prompt") ? startTag.attributes.get("prompt") : "This is a searchable index. Enter search keywords: ";
            tb.process((new Token.Character()).data(prompt));
            Attributes inputAttribs = new Attributes();
            if (startTag.hasAttributes()) {
               Iterator var20 = startTag.attributes.iterator();

               while(var20.hasNext()) {
                  Attribute attr = (Attribute)var20.next();
                  if (!StringUtil.inSorted(attr.getKey(), HtmlTreeBuilderState.Constants.InBodyStartInputAttribs)) {
                     inputAttribs.put(attr);
                  }
               }
            }

            inputAttribs.put("name", "isindex");
            tb.processStartTag("input", inputAttribs);
            tb.processEndTag("label");
            tb.processStartTag("hr");
            tb.processEndTag("form");
            break;
         case 15:
            tb.insert(startTag);
            if (!startTag.isSelfClosing()) {
               tb.tokeniser.transition(TokeniserState.Rcdata);
               tb.markInsertionMode();
               tb.framesetOk(false);
               tb.transition(Text);
            }
            break;
         case 16:
            if (tb.inButtonScope("p")) {
               tb.processEndTag("p");
            }

            tb.reconstructFormattingElements();
            tb.framesetOk(false);
            HtmlTreeBuilderState.handleRawtext(startTag, tb);
            break;
         case 17:
            tb.framesetOk(false);
            HtmlTreeBuilderState.handleRawtext(startTag, tb);
            break;
         case 18:
            HtmlTreeBuilderState.handleRawtext(startTag, tb);
            break;
         case 19:
            tb.reconstructFormattingElements();
            tb.insert(startTag);
            tb.framesetOk(false);
            if (!startTag.selfClosing) {
               HtmlTreeBuilderState state = tb.state();
               if (!state.equals(InTable) && !state.equals(InCaption) && !state.equals(InTableBody) && !state.equals(InRow) && !state.equals(InCell)) {
                  tb.transition(InSelect);
               } else {
                  tb.transition(InSelectInTable);
               }
            }
            break;
         case 20:
            tb.reconstructFormattingElements();
            tb.insert(startTag);
            break;
         case 21:
            tb.reconstructFormattingElements();
            tb.insert(startTag);
            break;
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
            if (tb.inButtonScope("p")) {
               tb.processEndTag("p");
            }

            if (StringUtil.inSorted(tb.currentElement().normalName(), HtmlTreeBuilderState.Constants.Headings)) {
               tb.error(this);
               tb.pop();
            }

            tb.insert(startTag);
            break;
         case 28:
         case 29:
            if (tb.inButtonScope("p")) {
               tb.processEndTag("p");
            }

            tb.insert(startTag);
            tb.reader.matchConsume("\n");
            tb.framesetOk(false);
            break;
         case 30:
         case 31:
            tb.framesetOk(false);
            stack = tb.getStack();
            int bottom = stack.size() - 1;
            int upper = bottom >= 24 ? bottom - 24 : 0;

            for(int i = bottom; i >= upper; --i) {
               el = (Element)stack.get(i);
               if (StringUtil.inSorted(el.normalName(), HtmlTreeBuilderState.Constants.DdDt)) {
                  tb.processEndTag(el.normalName());
                  break;
               }

               if (tb.isSpecial(el) && !StringUtil.inSorted(el.normalName(), HtmlTreeBuilderState.Constants.InBodyStartLiBreakers)) {
                  break;
               }
            }

            if (tb.inButtonScope("p")) {
               tb.processEndTag("p");
            }

            tb.insert(startTag);
            break;
         case 32:
         case 33:
            if (tb.currentElementIs("option")) {
               tb.processEndTag("option");
            }

            tb.reconstructFormattingElements();
            tb.insert(startTag);
            break;
         case 34:
         case 35:
            if (tb.inScope("ruby")) {
               tb.generateImpliedEndTags();
               if (!tb.currentElementIs("ruby")) {
                  tb.error(this);
                  tb.popStackToBefore("ruby");
               }

               tb.insert(startTag);
            }
            break;
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
            tb.reconstructFormattingElements();
            tb.insertEmpty(startTag);
            tb.framesetOk(false);
            break;
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
            tb.reconstructFormattingElements();
            el = tb.insert(startTag);
            tb.pushActiveFormattingElements(el);
            break;
         default:
            if (!Tag.isKnownTag(name)) {
               tb.insert(startTag);
            } else if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InBodyStartPClosers)) {
               if (tb.inButtonScope("p")) {
                  tb.processEndTag("p");
               }

               tb.insert(startTag);
            } else {
               if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InBodyStartToHead)) {
                  return tb.process(t, InHead);
               }

               if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InBodyStartApplets)) {
                  tb.reconstructFormattingElements();
                  tb.insert(startTag);
                  tb.insertMarkerToFormattingElements();
                  tb.framesetOk(false);
               } else if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InBodyStartMedia)) {
                  tb.insertEmpty(startTag);
               } else {
                  if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InBodyStartDrop)) {
                     tb.error(this);
                     return false;
                  }

                  tb.reconstructFormattingElements();
                  tb.insert(startTag);
               }
            }
         }

         return true;
      }

      private boolean inBodyEndTag(Token t, HtmlTreeBuilder tb) {
         Token.EndTag endTag = t.asEndTag();
         String name = endTag.normalName();
         byte var6 = -1;
         switch(name.hashCode()) {
         case -1321546630:
            if (name.equals("template")) {
               var6 = 0;
            }
            break;
         case 112:
            if (name.equals("p")) {
               var6 = 7;
            }
            break;
         case 3152:
            if (name.equals("br")) {
               var6 = 16;
            }
            break;
         case 3200:
            if (name.equals("dd")) {
               var6 = 8;
            }
            break;
         case 3216:
            if (name.equals("dt")) {
               var6 = 9;
            }
            break;
         case 3273:
            if (name.equals("h1")) {
               var6 = 10;
            }
            break;
         case 3274:
            if (name.equals("h2")) {
               var6 = 11;
            }
            break;
         case 3275:
            if (name.equals("h3")) {
               var6 = 12;
            }
            break;
         case 3276:
            if (name.equals("h4")) {
               var6 = 13;
            }
            break;
         case 3277:
            if (name.equals("h5")) {
               var6 = 14;
            }
            break;
         case 3278:
            if (name.equals("h6")) {
               var6 = 15;
            }
            break;
         case 3453:
            if (name.equals("li")) {
               var6 = 3;
            }
            break;
         case 3029410:
            if (name.equals("body")) {
               var6 = 4;
            }
            break;
         case 3148996:
            if (name.equals("form")) {
               var6 = 6;
            }
            break;
         case 3213227:
            if (name.equals("html")) {
               var6 = 5;
            }
            break;
         case 3536714:
            if (name.equals("span")) {
               var6 = 2;
            }
            break;
         case 1869063452:
            if (name.equals("sarcasm")) {
               var6 = 1;
            }
         }

         switch(var6) {
         case 0:
            tb.process(t, InHead);
            break;
         case 1:
         case 2:
            return this.anyOtherEndTag(t, tb);
         case 3:
            if (!tb.inListItemScope(name)) {
               tb.error(this);
               return false;
            }

            tb.generateImpliedEndTags(name);
            if (!tb.currentElementIs(name)) {
               tb.error(this);
            }

            tb.popStackToClose(name);
            break;
         case 4:
            if (!tb.inScope("body")) {
               tb.error(this);
               return false;
            }

            this.anyOtherEndTag(t, tb);
            tb.transition(AfterBody);
            break;
         case 5:
            boolean notIgnored = tb.processEndTag("body");
            if (notIgnored) {
               return tb.process(endTag);
            }
            break;
         case 6:
            if (!tb.onStack("template")) {
               Element currentForm = tb.getFormElement();
               tb.setFormElement((FormElement)null);
               if (currentForm == null || !tb.inScope(name)) {
                  tb.error(this);
                  return false;
               }

               tb.generateImpliedEndTags();
               if (!tb.currentElementIs(name)) {
                  tb.error(this);
               }

               tb.removeFromStack(currentForm);
            } else {
               if (!tb.inScope(name)) {
                  tb.error(this);
                  return false;
               }

               tb.generateImpliedEndTags();
               if (!tb.currentElementIs(name)) {
                  tb.error(this);
               }

               tb.popStackToClose(name);
            }
            break;
         case 7:
            if (!tb.inButtonScope(name)) {
               tb.error(this);
               tb.processStartTag(name);
               return tb.process(endTag);
            }

            tb.generateImpliedEndTags(name);
            if (!tb.currentElementIs(name)) {
               tb.error(this);
            }

            tb.popStackToClose(name);
            break;
         case 8:
         case 9:
            if (!tb.inScope(name)) {
               tb.error(this);
               return false;
            }

            tb.generateImpliedEndTags(name);
            if (!tb.currentElementIs(name)) {
               tb.error(this);
            }

            tb.popStackToClose(name);
            break;
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
            if (!tb.inScope(HtmlTreeBuilderState.Constants.Headings)) {
               tb.error(this);
               return false;
            }

            tb.generateImpliedEndTags(name);
            if (!tb.currentElementIs(name)) {
               tb.error(this);
            }

            tb.popStackToClose(HtmlTreeBuilderState.Constants.Headings);
            break;
         case 16:
            tb.error(this);
            tb.processStartTag("br");
            return false;
         default:
            if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InBodyEndAdoptionFormatters)) {
               return this.inBodyEndTagAdoption(t, tb);
            }

            if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InBodyEndClosers)) {
               if (!tb.inScope(name)) {
                  tb.error(this);
                  return false;
               }

               tb.generateImpliedEndTags();
               if (!tb.currentElementIs(name)) {
                  tb.error(this);
               }

               tb.popStackToClose(name);
            } else {
               if (!StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InBodyStartApplets)) {
                  return this.anyOtherEndTag(t, tb);
               }

               if (!tb.inScope("name")) {
                  if (!tb.inScope(name)) {
                     tb.error(this);
                     return false;
                  }

                  tb.generateImpliedEndTags();
                  if (!tb.currentElementIs(name)) {
                     tb.error(this);
                  }

                  tb.popStackToClose(name);
                  tb.clearFormattingElementsToLastMarker();
               }
            }
         }

         return true;
      }

      boolean anyOtherEndTag(Token t, HtmlTreeBuilder tb) {
         String name = t.asEndTag().normalName;
         ArrayList<Element> stack = tb.getStack();
         Element elFromStack = tb.getFromStack(name);
         if (elFromStack == null) {
            tb.error(this);
            return false;
         } else {
            for(int pos = stack.size() - 1; pos >= 0; --pos) {
               Element node = (Element)stack.get(pos);
               if (node.normalName().equals(name)) {
                  tb.generateImpliedEndTags(name);
                  if (!tb.currentElementIs(name)) {
                     tb.error(this);
                  }

                  tb.popStackToClose(name);
                  break;
               }

               if (tb.isSpecial(node)) {
                  tb.error(this);
                  return false;
               }
            }

            return true;
         }
      }

      private boolean inBodyEndTagAdoption(Token t, HtmlTreeBuilder tb) {
         Token.EndTag endTag = t.asEndTag();
         String name = endTag.normalName();
         ArrayList<Element> stack = tb.getStack();

         for(int i = 0; i < 8; ++i) {
            Element formatEl = tb.getActiveFormattingElement(name);
            if (formatEl == null) {
               return this.anyOtherEndTag(t, tb);
            }

            if (!tb.onStack(formatEl)) {
               tb.error(this);
               tb.removeFromActiveFormattingElements(formatEl);
               return true;
            }

            if (!tb.inScope(formatEl.normalName())) {
               tb.error(this);
               return false;
            }

            if (tb.currentElement() != formatEl) {
               tb.error(this);
            }

            Element furthestBlock = null;
            Element commonAncestor = null;
            boolean seenFormattingElement = false;
            int stackSize = stack.size();
            int bookmark = -1;

            for(int si = 1; si < stackSize && si < 64; ++si) {
               Element el = (Element)stack.get(si);
               if (el == formatEl) {
                  commonAncestor = (Element)stack.get(si - 1);
                  seenFormattingElement = true;
                  bookmark = tb.positionOfElement(el);
               } else if (seenFormattingElement && tb.isSpecial(el)) {
                  furthestBlock = el;
                  break;
               }
            }

            if (furthestBlock == null) {
               tb.popStackToClose(formatEl.normalName());
               tb.removeFromActiveFormattingElements(formatEl);
               return true;
            }

            Element node = furthestBlock;
            Element lastNode = furthestBlock;

            for(int j = 0; j < 3; ++j) {
               if (tb.onStack(node)) {
                  node = tb.aboveOnStack(node);
               }

               if (!tb.isInActiveFormattingElements(node)) {
                  tb.removeFromStack(node);
               } else {
                  if (node == formatEl) {
                     break;
                  }

                  Element replacement = new Element(tb.tagFor(node.nodeName(), ParseSettings.preserveCase), tb.getBaseUri());
                  tb.replaceActiveFormattingElement(node, replacement);
                  tb.replaceOnStack(node, replacement);
                  node = replacement;
                  if (lastNode == furthestBlock) {
                     bookmark = tb.positionOfElement(replacement) + 1;
                  }

                  if (lastNode.parent() != null) {
                     lastNode.remove();
                  }

                  replacement.appendChild(lastNode);
                  lastNode = replacement;
               }
            }

            if (commonAncestor != null) {
               if (StringUtil.inSorted(commonAncestor.normalName(), HtmlTreeBuilderState.Constants.InBodyEndTableFosters)) {
                  if (lastNode.parent() != null) {
                     lastNode.remove();
                  }

                  tb.insertInFosterParent(lastNode);
               } else {
                  if (lastNode.parent() != null) {
                     lastNode.remove();
                  }

                  commonAncestor.appendChild(lastNode);
               }
            }

            Element adopter = new Element(formatEl.tag(), tb.getBaseUri());
            adopter.attributes().addAll(formatEl.attributes());
            adopter.appendChildren(furthestBlock.childNodes());
            furthestBlock.appendChild(adopter);
            tb.removeFromActiveFormattingElements(formatEl);
            tb.pushWithBookmark(adopter, bookmark);
            tb.removeFromStack(formatEl);
            tb.insertOnStackAfter(furthestBlock, adopter);
         }

         return true;
      }
   },
   Text {
      boolean process(Token t, HtmlTreeBuilder tb) {
         if (t.isCharacter()) {
            tb.insert(t.asCharacter());
         } else {
            if (t.isEOF()) {
               tb.error(this);
               tb.pop();
               tb.transition(tb.originalState());
               return tb.process(t);
            }

            if (t.isEndTag()) {
               tb.pop();
               tb.transition(tb.originalState());
            }
         }

         return true;
      }
   },
   InTable {
      boolean process(Token t, HtmlTreeBuilder tb) {
         if (t.isCharacter() && StringUtil.inSorted(tb.currentElement().normalName(), HtmlTreeBuilderState.Constants.InTableFoster)) {
            tb.newPendingTableCharacters();
            tb.markInsertionMode();
            tb.transition(InTableText);
            return tb.process(t);
         } else if (t.isComment()) {
            tb.insert(t.asComment());
            return true;
         } else if (t.isDoctype()) {
            tb.error(this);
            return false;
         } else {
            String name;
            if (!t.isStartTag()) {
               if (t.isEndTag()) {
                  Token.EndTag endTag = t.asEndTag();
                  name = endTag.normalName();
                  if (name.equals("table")) {
                     if (!tb.inTableScope(name)) {
                        tb.error(this);
                        return false;
                     }

                     tb.popStackToClose("table");
                     tb.resetInsertionMode();
                  } else {
                     if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InTableEndErr)) {
                        tb.error(this);
                        return false;
                     }

                     if (!name.equals("template")) {
                        return this.anythingElse(t, tb);
                     }

                     tb.process(t, InHead);
                  }

                  return true;
               } else if (t.isEOF()) {
                  if (tb.currentElementIs("html")) {
                     tb.error(this);
                  }

                  return true;
               } else {
                  return this.anythingElse(t, tb);
               }
            } else {
               Token.StartTag startTag = t.asStartTag();
               name = startTag.normalName();
               if (name.equals("caption")) {
                  tb.clearStackToTableContext();
                  tb.insertMarkerToFormattingElements();
                  tb.insert(startTag);
                  tb.transition(InCaption);
               } else if (name.equals("colgroup")) {
                  tb.clearStackToTableContext();
                  tb.insert(startTag);
                  tb.transition(InColumnGroup);
               } else {
                  if (name.equals("col")) {
                     tb.clearStackToTableContext();
                     tb.processStartTag("colgroup");
                     return tb.process(t);
                  }

                  if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InTableToBody)) {
                     tb.clearStackToTableContext();
                     tb.insert(startTag);
                     tb.transition(InTableBody);
                  } else {
                     if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InTableAddBody)) {
                        tb.clearStackToTableContext();
                        tb.processStartTag("tbody");
                        return tb.process(t);
                     }

                     if (name.equals("table")) {
                        tb.error(this);
                        if (!tb.inTableScope(name)) {
                           return false;
                        }

                        tb.popStackToClose(name);
                        if (!tb.resetInsertionMode()) {
                           tb.insert(startTag);
                           return true;
                        }

                        return tb.process(t);
                     }

                     if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InTableToHead)) {
                        return tb.process(t, InHead);
                     }

                     if (name.equals("input")) {
                        if (!startTag.hasAttributes() || !startTag.attributes.get("type").equalsIgnoreCase("hidden")) {
                           return this.anythingElse(t, tb);
                        }

                        tb.insertEmpty(startTag);
                     } else {
                        if (!name.equals("form")) {
                           return this.anythingElse(t, tb);
                        }

                        tb.error(this);
                        if (tb.getFormElement() != null || tb.onStack("template")) {
                           return false;
                        }

                        tb.insertForm(startTag, false, false);
                     }
                  }
               }

               return true;
            }
         }
      }

      boolean anythingElse(Token t, HtmlTreeBuilder tb) {
         tb.error(this);
         tb.setFosterInserts(true);
         tb.process(t, InBody);
         tb.setFosterInserts(false);
         return true;
      }
   },
   InTableText {
      boolean process(Token t, HtmlTreeBuilder tb) {
         if (t.type == Token.TokenType.Character) {
            Token.Character c = t.asCharacter();
            if (c.getData().equals(HtmlTreeBuilderState.nullString)) {
               tb.error(this);
               return false;
            } else {
               tb.getPendingTableCharacters().add(c.getData());
               return true;
            }
         } else {
            if (tb.getPendingTableCharacters().size() > 0) {
               Iterator var3 = tb.getPendingTableCharacters().iterator();

               while(var3.hasNext()) {
                  String character = (String)var3.next();
                  if (!HtmlTreeBuilderState.isWhitespace(character)) {
                     tb.error(this);
                     if (StringUtil.inSorted(tb.currentElement().normalName(), HtmlTreeBuilderState.Constants.InTableFoster)) {
                        tb.setFosterInserts(true);
                        tb.process((new Token.Character()).data(character), InBody);
                        tb.setFosterInserts(false);
                     } else {
                        tb.process((new Token.Character()).data(character), InBody);
                     }
                  } else {
                     tb.insert((new Token.Character()).data(character));
                  }
               }

               tb.newPendingTableCharacters();
            }

            tb.transition(tb.originalState());
            return tb.process(t);
         }
      }
   },
   InCaption {
      boolean process(Token t, HtmlTreeBuilder tb) {
         if (t.isEndTag() && t.asEndTag().normalName().equals("caption")) {
            Token.EndTag endTag = t.asEndTag();
            String name = endTag.normalName();
            if (!tb.inTableScope(name)) {
               tb.error(this);
               return false;
            }

            tb.generateImpliedEndTags();
            if (!tb.currentElementIs("caption")) {
               tb.error(this);
            }

            tb.popStackToClose("caption");
            tb.clearFormattingElementsToLastMarker();
            tb.transition(InTable);
         } else {
            if ((!t.isStartTag() || !StringUtil.inSorted(t.asStartTag().normalName(), HtmlTreeBuilderState.Constants.InCellCol)) && (!t.isEndTag() || !t.asEndTag().normalName().equals("table"))) {
               if (t.isEndTag() && StringUtil.inSorted(t.asEndTag().normalName(), HtmlTreeBuilderState.Constants.InCaptionIgnore)) {
                  tb.error(this);
                  return false;
               }

               return tb.process(t, InBody);
            }

            tb.error(this);
            boolean processed = tb.processEndTag("caption");
            if (processed) {
               return tb.process(t);
            }
         }

         return true;
      }
   },
   InColumnGroup {
      boolean process(Token t, HtmlTreeBuilder tb) {
         if (HtmlTreeBuilderState.isWhitespace(t)) {
            tb.insert(t.asCharacter());
            return true;
         } else {
            switch(t.type) {
            case Comment:
               tb.insert(t.asComment());
               break;
            case Doctype:
               tb.error(this);
               break;
            case StartTag:
               Token.StartTag startTag = t.asStartTag();
               String var8 = startTag.normalName();
               byte var9 = -1;
               switch(var8.hashCode()) {
               case -1321546630:
                  if (var8.equals("template")) {
                     var9 = 2;
                  }
                  break;
               case 98688:
                  if (var8.equals("col")) {
                     var9 = 1;
                  }
                  break;
               case 3213227:
                  if (var8.equals("html")) {
                     var9 = 0;
                  }
               }

               switch(var9) {
               case 0:
                  return tb.process(t, InBody);
               case 1:
                  tb.insertEmpty(startTag);
                  return true;
               case 2:
                  tb.process(t, InHead);
                  return true;
               default:
                  return this.anythingElse(t, tb);
               }
            case EndTag:
               Token.EndTag endTag = t.asEndTag();
               String name = endTag.normalName();
               byte var7 = -1;
               switch(name.hashCode()) {
               case -1321546630:
                  if (name.equals("template")) {
                     var7 = 1;
                  }
                  break;
               case -636197633:
                  if (name.equals("colgroup")) {
                     var7 = 0;
                  }
               }

               switch(var7) {
               case 0:
                  if (!tb.currentElementIs(name)) {
                     tb.error(this);
                     return false;
                  }

                  tb.pop();
                  tb.transition(InTable);
                  return true;
               case 1:
                  tb.process(t, InHead);
                  return true;
               default:
                  return this.anythingElse(t, tb);
               }
            case Character:
            default:
               return this.anythingElse(t, tb);
            case EOF:
               if (tb.currentElementIs("html")) {
                  return true;
               }

               return this.anythingElse(t, tb);
            }

            return true;
         }
      }

      private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
         if (!tb.currentElementIs("colgroup")) {
            tb.error(this);
            return false;
         } else {
            tb.pop();
            tb.transition(InTable);
            tb.process(t);
            return true;
         }
      }
   },
   InTableBody {
      boolean process(Token t, HtmlTreeBuilder tb) {
         String name;
         switch(t.type) {
         case StartTag:
            Token.StartTag startTag = t.asStartTag();
            name = startTag.normalName();
            if (!name.equals("tr")) {
               if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InCellNames)) {
                  tb.error(this);
                  tb.processStartTag("tr");
                  return tb.process(startTag);
               }

               if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InTableBodyExit)) {
                  return this.exitTableBody(t, tb);
               }

               return this.anythingElse(t, tb);
            }

            tb.clearStackToTableBodyContext();
            tb.insert(startTag);
            tb.transition(InRow);
            break;
         case EndTag:
            Token.EndTag endTag = t.asEndTag();
            name = endTag.normalName();
            if (!StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InTableEndIgnore)) {
               if (name.equals("table")) {
                  return this.exitTableBody(t, tb);
               }

               if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InTableBodyEndIgnore)) {
                  tb.error(this);
                  return false;
               }

               return this.anythingElse(t, tb);
            }

            if (!tb.inTableScope(name)) {
               tb.error(this);
               return false;
            }

            tb.clearStackToTableBodyContext();
            tb.pop();
            tb.transition(InTable);
            break;
         default:
            return this.anythingElse(t, tb);
         }

         return true;
      }

      private boolean exitTableBody(Token t, HtmlTreeBuilder tb) {
         if (!tb.inTableScope("tbody") && !tb.inTableScope("thead") && !tb.inScope("tfoot")) {
            tb.error(this);
            return false;
         } else {
            tb.clearStackToTableBodyContext();
            tb.processEndTag(tb.currentElement().normalName());
            return tb.process(t);
         }
      }

      private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
         return tb.process(t, InTable);
      }
   },
   InRow {
      boolean process(Token t, HtmlTreeBuilder tb) {
         String name;
         if (t.isStartTag()) {
            Token.StartTag startTag = t.asStartTag();
            name = startTag.normalName();
            if (!StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InCellNames)) {
               if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InRowMissing)) {
                  return this.handleMissingTr(t, tb);
               }

               return this.anythingElse(t, tb);
            }

            tb.clearStackToTableRowContext();
            tb.insert(startTag);
            tb.transition(InCell);
            tb.insertMarkerToFormattingElements();
         } else {
            if (!t.isEndTag()) {
               return this.anythingElse(t, tb);
            }

            Token.EndTag endTag = t.asEndTag();
            name = endTag.normalName();
            if (name.equals("tr")) {
               if (!tb.inTableScope(name)) {
                  tb.error(this);
                  return false;
               }

               tb.clearStackToTableRowContext();
               tb.pop();
               tb.transition(InTableBody);
            } else {
               if (name.equals("table")) {
                  return this.handleMissingTr(t, tb);
               }

               if (!StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InTableToBody)) {
                  if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InRowIgnore)) {
                     tb.error(this);
                     return false;
                  }

                  return this.anythingElse(t, tb);
               }

               if (!tb.inTableScope(name) || !tb.inTableScope("tr")) {
                  tb.error(this);
                  return false;
               }

               tb.clearStackToTableRowContext();
               tb.pop();
               tb.transition(InTableBody);
            }
         }

         return true;
      }

      private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
         return tb.process(t, InTable);
      }

      private boolean handleMissingTr(Token t, TreeBuilder tb) {
         boolean processed = tb.processEndTag("tr");
         return processed ? tb.process(t) : false;
      }
   },
   InCell {
      boolean process(Token t, HtmlTreeBuilder tb) {
         if (t.isEndTag()) {
            Token.EndTag endTag = t.asEndTag();
            String name = endTag.normalName();
            if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InCellNames)) {
               if (!tb.inTableScope(name)) {
                  tb.error(this);
                  tb.transition(InRow);
                  return false;
               } else {
                  tb.generateImpliedEndTags();
                  if (!tb.currentElementIs(name)) {
                     tb.error(this);
                  }

                  tb.popStackToClose(name);
                  tb.clearFormattingElementsToLastMarker();
                  tb.transition(InRow);
                  return true;
               }
            } else if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InCellBody)) {
               tb.error(this);
               return false;
            } else if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InCellTable)) {
               if (!tb.inTableScope(name)) {
                  tb.error(this);
                  return false;
               } else {
                  this.closeCell(tb);
                  return tb.process(t);
               }
            } else {
               return this.anythingElse(t, tb);
            }
         } else if (t.isStartTag() && StringUtil.inSorted(t.asStartTag().normalName(), HtmlTreeBuilderState.Constants.InCellCol)) {
            if (!tb.inTableScope("td") && !tb.inTableScope("th")) {
               tb.error(this);
               return false;
            } else {
               this.closeCell(tb);
               return tb.process(t);
            }
         } else {
            return this.anythingElse(t, tb);
         }
      }

      private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
         return tb.process(t, InBody);
      }

      private void closeCell(HtmlTreeBuilder tb) {
         if (tb.inTableScope("td")) {
            tb.processEndTag("td");
         } else {
            tb.processEndTag("th");
         }

      }
   },
   InSelect {
      boolean process(Token t, HtmlTreeBuilder tb) {
         String name;
         switch(t.type) {
         case Comment:
            tb.insert(t.asComment());
            break;
         case Doctype:
            tb.error(this);
            return false;
         case StartTag:
            Token.StartTag start = t.asStartTag();
            name = start.normalName();
            if (name.equals("html")) {
               return tb.process(start, InBody);
            }

            if (name.equals("option")) {
               if (tb.currentElementIs("option")) {
                  tb.processEndTag("option");
               }

               tb.insert(start);
            } else {
               if (!name.equals("optgroup")) {
                  if (name.equals("select")) {
                     tb.error(this);
                     return tb.processEndTag("select");
                  }

                  if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InSelectEnd)) {
                     tb.error(this);
                     if (!tb.inSelectScope("select")) {
                        return false;
                     }

                     tb.processEndTag("select");
                     return tb.process(start);
                  }

                  if (!name.equals("script") && !name.equals("template")) {
                     return this.anythingElse(t, tb);
                  }

                  return tb.process(t, InHead);
               }

               if (tb.currentElementIs("option")) {
                  tb.processEndTag("option");
               }

               if (tb.currentElementIs("optgroup")) {
                  tb.processEndTag("optgroup");
               }

               tb.insert(start);
            }
            break;
         case EndTag:
            Token.EndTag end = t.asEndTag();
            name = end.normalName();
            byte var8 = -1;
            switch(name.hashCode()) {
            case -1321546630:
               if (name.equals("template")) {
                  var8 = 3;
               }
               break;
            case -1010136971:
               if (name.equals("option")) {
                  var8 = 1;
               }
               break;
            case -906021636:
               if (name.equals("select")) {
                  var8 = 2;
               }
               break;
            case -80773204:
               if (name.equals("optgroup")) {
                  var8 = 0;
               }
            }

            switch(var8) {
            case 0:
               if (tb.currentElementIs("option") && tb.aboveOnStack(tb.currentElement()) != null && tb.aboveOnStack(tb.currentElement()).normalName().equals("optgroup")) {
                  tb.processEndTag("option");
               }

               if (tb.currentElementIs("optgroup")) {
                  tb.pop();
               } else {
                  tb.error(this);
               }

               return true;
            case 1:
               if (tb.currentElementIs("option")) {
                  tb.pop();
               } else {
                  tb.error(this);
               }

               return true;
            case 2:
               if (!tb.inSelectScope(name)) {
                  tb.error(this);
                  return false;
               }

               tb.popStackToClose(name);
               tb.resetInsertionMode();
               return true;
            case 3:
               return tb.process(t, InHead);
            default:
               return this.anythingElse(t, tb);
            }
         case Character:
            Token.Character c = t.asCharacter();
            if (c.getData().equals(HtmlTreeBuilderState.nullString)) {
               tb.error(this);
               return false;
            }

            tb.insert(c);
            break;
         case EOF:
            if (!tb.currentElementIs("html")) {
               tb.error(this);
            }
            break;
         default:
            return this.anythingElse(t, tb);
         }

         return true;
      }

      private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
         tb.error(this);
         return false;
      }
   },
   InSelectInTable {
      boolean process(Token t, HtmlTreeBuilder tb) {
         if (t.isStartTag() && StringUtil.inSorted(t.asStartTag().normalName(), HtmlTreeBuilderState.Constants.InSelectTableEnd)) {
            tb.error(this);
            tb.popStackToClose("select");
            tb.resetInsertionMode();
            return tb.process(t);
         } else if (t.isEndTag() && StringUtil.inSorted(t.asEndTag().normalName(), HtmlTreeBuilderState.Constants.InSelectTableEnd)) {
            tb.error(this);
            if (tb.inTableScope(t.asEndTag().normalName())) {
               tb.popStackToClose("select");
               tb.resetInsertionMode();
               return tb.process(t);
            } else {
               return false;
            }
         } else {
            return tb.process(t, InSelect);
         }
      }
   },
   InTemplate {
      boolean process(Token t, HtmlTreeBuilder tb) {
         String name;
         switch(t.type) {
         case Comment:
         case Doctype:
         case Character:
            tb.process(t, InBody);
            break;
         case StartTag:
            name = t.asStartTag().normalName();
            if (!StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InTemplateToHead)) {
               if (StringUtil.inSorted(name, HtmlTreeBuilderState.Constants.InTemplateToTable)) {
                  tb.popTemplateMode();
                  tb.pushTemplateMode(InTable);
                  tb.transition(InTable);
                  return tb.process(t);
               }

               if (name.equals("col")) {
                  tb.popTemplateMode();
                  tb.pushTemplateMode(InColumnGroup);
                  tb.transition(InColumnGroup);
                  return tb.process(t);
               }

               if (name.equals("tr")) {
                  tb.popTemplateMode();
                  tb.pushTemplateMode(InTableBody);
                  tb.transition(InTableBody);
                  return tb.process(t);
               }

               if (!name.equals("td") && !name.equals("th")) {
                  tb.popTemplateMode();
                  tb.pushTemplateMode(InBody);
                  tb.transition(InBody);
                  return tb.process(t);
               }

               tb.popTemplateMode();
               tb.pushTemplateMode(InRow);
               tb.transition(InRow);
               return tb.process(t);
            }

            tb.process(t, InHead);
            break;
         case EndTag:
            name = t.asEndTag().normalName();
            if (!name.equals("template")) {
               tb.error(this);
               return false;
            }

            tb.process(t, InHead);
            break;
         case EOF:
            if (!tb.onStack("template")) {
               return true;
            }

            tb.error(this);
            tb.popStackToClose("template");
            tb.clearFormattingElementsToLastMarker();
            tb.popTemplateMode();
            tb.resetInsertionMode();
            if (tb.state() != InTemplate && tb.templateModeSize() < 12) {
               return tb.process(t);
            }

            return true;
         }

         return true;
      }
   },
   AfterBody {
      boolean process(Token t, HtmlTreeBuilder tb) {
         if (HtmlTreeBuilderState.isWhitespace(t)) {
            tb.insert(t.asCharacter());
         } else if (t.isComment()) {
            tb.insert(t.asComment());
         } else {
            if (t.isDoctype()) {
               tb.error(this);
               return false;
            }

            if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
               return tb.process(t, InBody);
            }

            if (t.isEndTag() && t.asEndTag().normalName().equals("html")) {
               if (tb.isFragmentParsing()) {
                  tb.error(this);
                  return false;
               }

               if (tb.onStack("html")) {
                  tb.popStackToClose("html");
               }

               tb.transition(AfterAfterBody);
            } else if (!t.isEOF()) {
               tb.error(this);
               tb.resetBody();
               return tb.process(t);
            }
         }

         return true;
      }
   },
   InFrameset {
      boolean process(Token t, HtmlTreeBuilder tb) {
         if (HtmlTreeBuilderState.isWhitespace(t)) {
            tb.insert(t.asCharacter());
         } else if (t.isComment()) {
            tb.insert(t.asComment());
         } else {
            if (t.isDoctype()) {
               tb.error(this);
               return false;
            }

            if (t.isStartTag()) {
               Token.StartTag start = t.asStartTag();
               String var4 = start.normalName();
               byte var5 = -1;
               switch(var4.hashCode()) {
               case -1644953643:
                  if (var4.equals("frameset")) {
                     var5 = 1;
                  }
                  break;
               case 3213227:
                  if (var4.equals("html")) {
                     var5 = 0;
                  }
                  break;
               case 97692013:
                  if (var4.equals("frame")) {
                     var5 = 2;
                  }
                  break;
               case 1192721831:
                  if (var4.equals("noframes")) {
                     var5 = 3;
                  }
               }

               switch(var5) {
               case 0:
                  return tb.process(start, InBody);
               case 1:
                  tb.insert(start);
                  break;
               case 2:
                  tb.insertEmpty(start);
                  break;
               case 3:
                  return tb.process(start, InHead);
               default:
                  tb.error(this);
                  return false;
               }
            } else if (t.isEndTag() && t.asEndTag().normalName().equals("frameset")) {
               if (tb.currentElementIs("html")) {
                  tb.error(this);
                  return false;
               }

               tb.pop();
               if (!tb.isFragmentParsing() && !tb.currentElementIs("frameset")) {
                  tb.transition(AfterFrameset);
               }
            } else {
               if (!t.isEOF()) {
                  tb.error(this);
                  return false;
               }

               if (!tb.currentElementIs("html")) {
                  tb.error(this);
                  return true;
               }
            }
         }

         return true;
      }
   },
   AfterFrameset {
      boolean process(Token t, HtmlTreeBuilder tb) {
         if (HtmlTreeBuilderState.isWhitespace(t)) {
            tb.insert(t.asCharacter());
         } else if (t.isComment()) {
            tb.insert(t.asComment());
         } else {
            if (t.isDoctype()) {
               tb.error(this);
               return false;
            }

            if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
               return tb.process(t, InBody);
            }

            if (t.isEndTag() && t.asEndTag().normalName().equals("html")) {
               tb.transition(AfterAfterFrameset);
            } else {
               if (t.isStartTag() && t.asStartTag().normalName().equals("noframes")) {
                  return tb.process(t, InHead);
               }

               if (!t.isEOF()) {
                  tb.error(this);
                  return false;
               }
            }
         }

         return true;
      }
   },
   AfterAfterBody {
      boolean process(Token t, HtmlTreeBuilder tb) {
         if (t.isComment()) {
            tb.insert(t.asComment());
         } else {
            if (t.isDoctype() || t.isStartTag() && t.asStartTag().normalName().equals("html")) {
               return tb.process(t, InBody);
            }

            if (HtmlTreeBuilderState.isWhitespace(t)) {
               tb.insert(t.asCharacter());
            } else if (!t.isEOF()) {
               tb.error(this);
               tb.resetBody();
               return tb.process(t);
            }
         }

         return true;
      }
   },
   AfterAfterFrameset {
      boolean process(Token t, HtmlTreeBuilder tb) {
         if (t.isComment()) {
            tb.insert(t.asComment());
         } else {
            if (t.isDoctype() || HtmlTreeBuilderState.isWhitespace(t) || t.isStartTag() && t.asStartTag().normalName().equals("html")) {
               return tb.process(t, InBody);
            }

            if (!t.isEOF()) {
               if (t.isStartTag() && t.asStartTag().normalName().equals("noframes")) {
                  return tb.process(t, InHead);
               }

               tb.error(this);
               return false;
            }
         }

         return true;
      }
   },
   ForeignContent {
      boolean process(Token t, HtmlTreeBuilder tb) {
         return true;
      }
   };

   private static final String nullString = String.valueOf('\u0000');

   private HtmlTreeBuilderState() {
   }

   abstract boolean process(Token var1, HtmlTreeBuilder var2);

   private static boolean isWhitespace(Token t) {
      if (t.isCharacter()) {
         String data = t.asCharacter().getData();
         return StringUtil.isBlank(data);
      } else {
         return false;
      }
   }

   private static boolean isWhitespace(String data) {
      return StringUtil.isBlank(data);
   }

   private static void handleRcData(Token.StartTag startTag, HtmlTreeBuilder tb) {
      tb.tokeniser.transition(TokeniserState.Rcdata);
      tb.markInsertionMode();
      tb.transition(Text);
      tb.insert(startTag);
   }

   private static void handleRawtext(Token.StartTag startTag, HtmlTreeBuilder tb) {
      tb.tokeniser.transition(TokeniserState.Rawtext);
      tb.markInsertionMode();
      tb.transition(Text);
      tb.insert(startTag);
   }

   // $FF: synthetic method
   private static HtmlTreeBuilderState[] $values() {
      return new HtmlTreeBuilderState[]{Initial, BeforeHtml, BeforeHead, InHead, InHeadNoscript, AfterHead, InBody, Text, InTable, InTableText, InCaption, InColumnGroup, InTableBody, InRow, InCell, InSelect, InSelectInTable, InTemplate, AfterBody, InFrameset, AfterFrameset, AfterAfterBody, AfterAfterFrameset, ForeignContent};
   }

   // $FF: synthetic method
   HtmlTreeBuilderState(Object x2) {
      this();
   }

   static final class Constants {
      static final String[] InHeadEmpty = new String[]{"base", "basefont", "bgsound", "command", "link"};
      static final String[] InHeadRaw = new String[]{"noframes", "style"};
      static final String[] InHeadEnd = new String[]{"body", "br", "html"};
      static final String[] AfterHeadBody = new String[]{"body", "br", "html"};
      static final String[] BeforeHtmlToHead = new String[]{"body", "br", "head", "html"};
      static final String[] InHeadNoScriptHead = new String[]{"basefont", "bgsound", "link", "meta", "noframes", "style"};
      static final String[] InBodyStartToHead = new String[]{"base", "basefont", "bgsound", "command", "link", "meta", "noframes", "script", "style", "template", "title"};
      static final String[] InBodyStartPClosers = new String[]{"address", "article", "aside", "blockquote", "center", "details", "dir", "div", "dl", "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "menu", "nav", "ol", "p", "section", "summary", "ul"};
      static final String[] Headings = new String[]{"h1", "h2", "h3", "h4", "h5", "h6"};
      static final String[] InBodyStartLiBreakers = new String[]{"address", "div", "p"};
      static final String[] DdDt = new String[]{"dd", "dt"};
      static final String[] InBodyStartApplets = new String[]{"applet", "marquee", "object"};
      static final String[] InBodyStartMedia = new String[]{"param", "source", "track"};
      static final String[] InBodyStartInputAttribs = new String[]{"action", "name", "prompt"};
      static final String[] InBodyStartDrop = new String[]{"caption", "col", "colgroup", "frame", "head", "tbody", "td", "tfoot", "th", "thead", "tr"};
      static final String[] InBodyEndClosers = new String[]{"address", "article", "aside", "blockquote", "button", "center", "details", "dir", "div", "dl", "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "listing", "menu", "nav", "ol", "pre", "section", "summary", "ul"};
      static final String[] InBodyEndAdoptionFormatters = new String[]{"a", "b", "big", "code", "em", "font", "i", "nobr", "s", "small", "strike", "strong", "tt", "u"};
      static final String[] InBodyEndTableFosters = new String[]{"table", "tbody", "tfoot", "thead", "tr"};
      static final String[] InTableToBody = new String[]{"tbody", "tfoot", "thead"};
      static final String[] InTableAddBody = new String[]{"td", "th", "tr"};
      static final String[] InTableToHead = new String[]{"script", "style", "template"};
      static final String[] InCellNames = new String[]{"td", "th"};
      static final String[] InCellBody = new String[]{"body", "caption", "col", "colgroup", "html"};
      static final String[] InCellTable = new String[]{"table", "tbody", "tfoot", "thead", "tr"};
      static final String[] InCellCol = new String[]{"caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr"};
      static final String[] InTableEndErr = new String[]{"body", "caption", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", "tr"};
      static final String[] InTableFoster = new String[]{"table", "tbody", "tfoot", "thead", "tr"};
      static final String[] InTableBodyExit = new String[]{"caption", "col", "colgroup", "tbody", "tfoot", "thead"};
      static final String[] InTableBodyEndIgnore = new String[]{"body", "caption", "col", "colgroup", "html", "td", "th", "tr"};
      static final String[] InRowMissing = new String[]{"caption", "col", "colgroup", "tbody", "tfoot", "thead", "tr"};
      static final String[] InRowIgnore = new String[]{"body", "caption", "col", "colgroup", "html", "td", "th"};
      static final String[] InSelectEnd = new String[]{"input", "keygen", "textarea"};
      static final String[] InSelectTableEnd = new String[]{"caption", "table", "tbody", "td", "tfoot", "th", "thead", "tr"};
      static final String[] InTableEndIgnore = new String[]{"tbody", "tfoot", "thead"};
      static final String[] InHeadNoscriptIgnore = new String[]{"head", "noscript"};
      static final String[] InCaptionIgnore = new String[]{"body", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", "tr"};
      static final String[] InTemplateToHead = new String[]{"base", "basefont", "bgsound", "link", "meta", "noframes", "script", "style", "template", "title"};
      static final String[] InTemplateToTable = new String[]{"caption", "colgroup", "tbody", "tfoot", "thead"};
   }
}
