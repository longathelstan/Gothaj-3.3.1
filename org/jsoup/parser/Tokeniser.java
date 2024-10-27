package org.jsoup.parser;

import java.util.Arrays;
import javax.annotation.Nullable;
import org.jsoup.helper.Validate;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Entities;

final class Tokeniser {
   static final char replacementChar = '�';
   private static final char[] notCharRefCharsSorted = new char[]{'\t', '\n', '\r', '\f', ' ', '<', '&'};
   static final int win1252ExtensionsStart = 128;
   static final int[] win1252Extensions = new int[]{8364, 129, 8218, 402, 8222, 8230, 8224, 8225, 710, 8240, 352, 8249, 338, 141, 381, 143, 144, 8216, 8217, 8220, 8221, 8226, 8211, 8212, 732, 8482, 353, 8250, 339, 157, 382, 376};
   private final CharacterReader reader;
   private final ParseErrorList errors;
   private TokeniserState state;
   @Nullable
   private Token emitPending;
   private boolean isEmitPending;
   @Nullable
   private String charsString;
   private final StringBuilder charsBuilder;
   StringBuilder dataBuffer;
   Token.StartTag startPending;
   Token.EndTag endPending;
   Token.Tag tagPending;
   Token.Character charPending;
   Token.Doctype doctypePending;
   Token.Comment commentPending;
   @Nullable
   private String lastStartTag;
   @Nullable
   private String lastStartCloseSeq;
   private static final int Unset = -1;
   private int markupStartPos;
   private int charStartPos;
   private final int[] codepointHolder;
   private final int[] multipointHolder;

   Tokeniser(CharacterReader reader, ParseErrorList errors) {
      this.state = TokeniserState.Data;
      this.emitPending = null;
      this.isEmitPending = false;
      this.charsString = null;
      this.charsBuilder = new StringBuilder(1024);
      this.dataBuffer = new StringBuilder(1024);
      this.startPending = new Token.StartTag();
      this.endPending = new Token.EndTag();
      this.tagPending = this.startPending;
      this.charPending = new Token.Character();
      this.doctypePending = new Token.Doctype();
      this.commentPending = new Token.Comment();
      this.charStartPos = -1;
      this.codepointHolder = new int[1];
      this.multipointHolder = new int[2];
      this.reader = reader;
      this.errors = errors;
   }

   Token read() {
      while(!this.isEmitPending) {
         this.state.read(this, this.reader);
      }

      StringBuilder cb = this.charsBuilder;
      if (cb.length() != 0) {
         String str = cb.toString();
         cb.delete(0, cb.length());
         Token token = this.charPending.data(str);
         this.charsString = null;
         return token;
      } else if (this.charsString != null) {
         Token token = this.charPending.data(this.charsString);
         this.charsString = null;
         return token;
      } else {
         this.isEmitPending = false;

         assert this.emitPending != null;

         return this.emitPending;
      }
   }

   void emit(Token token) {
      Validate.isFalse(this.isEmitPending);
      this.emitPending = token;
      this.isEmitPending = true;
      token.startPos(this.markupStartPos);
      token.endPos(this.reader.pos());
      this.charStartPos = -1;
      if (token.type == Token.TokenType.StartTag) {
         Token.StartTag startTag = (Token.StartTag)token;
         this.lastStartTag = startTag.tagName;
         this.lastStartCloseSeq = null;
      } else if (token.type == Token.TokenType.EndTag) {
         Token.EndTag endTag = (Token.EndTag)token;
         if (endTag.hasAttributes()) {
            this.error("Attributes incorrectly present on end tag [/%s]", endTag.normalName());
         }
      }

   }

   void emit(String str) {
      if (this.charsString == null) {
         this.charsString = str;
      } else {
         if (this.charsBuilder.length() == 0) {
            this.charsBuilder.append(this.charsString);
         }

         this.charsBuilder.append(str);
      }

      this.charPending.startPos(this.charStartPos);
      this.charPending.endPos(this.reader.pos());
   }

   void emit(StringBuilder str) {
      if (this.charsString == null) {
         this.charsString = str.toString();
      } else {
         if (this.charsBuilder.length() == 0) {
            this.charsBuilder.append(this.charsString);
         }

         this.charsBuilder.append(str);
      }

      this.charPending.startPos(this.charStartPos);
      this.charPending.endPos(this.reader.pos());
   }

   void emit(char c) {
      if (this.charsString == null) {
         this.charsString = String.valueOf(c);
      } else {
         if (this.charsBuilder.length() == 0) {
            this.charsBuilder.append(this.charsString);
         }

         this.charsBuilder.append(c);
      }

      this.charPending.startPos(this.charStartPos);
      this.charPending.endPos(this.reader.pos());
   }

   void emit(char[] chars) {
      this.emit(String.valueOf(chars));
   }

   void emit(int[] codepoints) {
      this.emit(new String(codepoints, 0, codepoints.length));
   }

   TokeniserState getState() {
      return this.state;
   }

   void transition(TokeniserState newState) {
      switch(newState) {
      case TagOpen:
         this.markupStartPos = this.reader.pos();
         break;
      case Data:
         if (this.charStartPos == -1) {
            this.charStartPos = this.reader.pos();
         }
      }

      this.state = newState;
   }

   void advanceTransition(TokeniserState newState) {
      this.transition(newState);
      this.reader.advance();
   }

   @Nullable
   int[] consumeCharacterReference(@Nullable Character additionalAllowedCharacter, boolean inAttribute) {
      if (this.reader.isEmpty()) {
         return null;
      } else if (additionalAllowedCharacter != null && additionalAllowedCharacter == this.reader.current()) {
         return null;
      } else if (this.reader.matchesAnySorted(notCharRefCharsSorted)) {
         return null;
      } else {
         int[] codeRef = this.codepointHolder;
         this.reader.mark();
         int numChars;
         if (this.reader.matchConsume("#")) {
            boolean isHexMode = this.reader.matchConsumeIgnoreCase("X");
            String numRef = isHexMode ? this.reader.consumeHexSequence() : this.reader.consumeDigitSequence();
            if (numRef.length() == 0) {
               this.characterReferenceError("numeric reference with no numerals");
               this.reader.rewindToMark();
               return null;
            } else {
               this.reader.unmark();
               if (!this.reader.matchConsume(";")) {
                  this.characterReferenceError("missing semicolon on [&#%s]", numRef);
               }

               int charval = -1;

               try {
                  numChars = isHexMode ? 16 : 10;
                  charval = Integer.valueOf(numRef, numChars);
               } catch (NumberFormatException var8) {
               }

               if (charval != -1 && (charval < 55296 || charval > 57343) && charval <= 1114111) {
                  if (charval >= 128 && charval < 128 + win1252Extensions.length) {
                     this.characterReferenceError("character [%s] is not a valid unicode code point", charval);
                     charval = win1252Extensions[charval - 128];
                  }

                  codeRef[0] = charval;
               } else {
                  this.characterReferenceError("character [%s] outside of valid range", charval);
                  codeRef[0] = 65533;
               }

               return codeRef;
            }
         } else {
            String nameRef = this.reader.consumeLetterThenDigitSequence();
            boolean looksLegit = this.reader.matches(';');
            boolean found = Entities.isBaseNamedEntity(nameRef) || Entities.isNamedEntity(nameRef) && looksLegit;
            if (!found) {
               this.reader.rewindToMark();
               if (looksLegit) {
                  this.characterReferenceError("invalid named reference [%s]", nameRef);
               }

               return null;
            } else if (inAttribute && (this.reader.matchesLetter() || this.reader.matchesDigit() || this.reader.matchesAny('=', '-', '_'))) {
               this.reader.rewindToMark();
               return null;
            } else {
               this.reader.unmark();
               if (!this.reader.matchConsume(";")) {
                  this.characterReferenceError("missing semicolon on [&%s]", nameRef);
               }

               numChars = Entities.codepointsForName(nameRef, this.multipointHolder);
               if (numChars == 1) {
                  codeRef[0] = this.multipointHolder[0];
                  return codeRef;
               } else if (numChars == 2) {
                  return this.multipointHolder;
               } else {
                  Validate.fail("Unexpected characters returned for " + nameRef);
                  return this.multipointHolder;
               }
            }
         }
      }
   }

   Token.Tag createTagPending(boolean start) {
      this.tagPending = start ? this.startPending.reset() : this.endPending.reset();
      return this.tagPending;
   }

   void emitTagPending() {
      this.tagPending.finaliseTag();
      this.emit((Token)this.tagPending);
   }

   void createCommentPending() {
      this.commentPending.reset();
   }

   void emitCommentPending() {
      this.emit((Token)this.commentPending);
   }

   void createBogusCommentPending() {
      this.commentPending.reset();
      this.commentPending.bogus = true;
   }

   void createDoctypePending() {
      this.doctypePending.reset();
   }

   void emitDoctypePending() {
      this.emit((Token)this.doctypePending);
   }

   void createTempBuffer() {
      Token.reset(this.dataBuffer);
   }

   boolean isAppropriateEndTagToken() {
      return this.lastStartTag != null && this.tagPending.name().equalsIgnoreCase(this.lastStartTag);
   }

   @Nullable
   String appropriateEndTagName() {
      return this.lastStartTag;
   }

   String appropriateEndTagSeq() {
      if (this.lastStartCloseSeq == null) {
         this.lastStartCloseSeq = "</" + this.lastStartTag;
      }

      return this.lastStartCloseSeq;
   }

   void error(TokeniserState state) {
      if (this.errors.canAddError()) {
         this.errors.add(new ParseError(this.reader, "Unexpected character '%s' in input state [%s]", new Object[]{this.reader.current(), state}));
      }

   }

   void eofError(TokeniserState state) {
      if (this.errors.canAddError()) {
         this.errors.add(new ParseError(this.reader, "Unexpectedly reached end of file (EOF) in input state [%s]", new Object[]{state}));
      }

   }

   private void characterReferenceError(String message, Object... args) {
      if (this.errors.canAddError()) {
         this.errors.add(new ParseError(this.reader, String.format("Invalid character reference: " + message, args)));
      }

   }

   void error(String errorMsg) {
      if (this.errors.canAddError()) {
         this.errors.add(new ParseError(this.reader, errorMsg));
      }

   }

   void error(String errorMsg, Object... args) {
      if (this.errors.canAddError()) {
         this.errors.add(new ParseError(this.reader, errorMsg, args));
      }

   }

   boolean currentNodeInHtmlNS() {
      return true;
   }

   String unescapeEntities(boolean inAttribute) {
      StringBuilder builder = StringUtil.borrowBuilder();

      while(true) {
         while(true) {
            do {
               if (this.reader.isEmpty()) {
                  return StringUtil.releaseBuilder(builder);
               }

               builder.append(this.reader.consumeTo('&'));
            } while(!this.reader.matches('&'));

            this.reader.consume();
            int[] c = this.consumeCharacterReference((Character)null, inAttribute);
            if (c != null && c.length != 0) {
               builder.appendCodePoint(c[0]);
               if (c.length == 2) {
                  builder.appendCodePoint(c[1]);
               }
            } else {
               builder.append('&');
            }
         }
      }
   }

   static {
      Arrays.sort(notCharRefCharsSorted);
   }
}
