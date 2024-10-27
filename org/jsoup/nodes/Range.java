package org.jsoup.nodes;

import org.jsoup.helper.Validate;

public class Range {
   private final Range.Position start;
   private final Range.Position end;
   private static final String RangeKey = Attributes.internalKey("jsoup.sourceRange");
   private static final String EndRangeKey = Attributes.internalKey("jsoup.endSourceRange");
   private static final Range.Position UntrackedPos = new Range.Position(-1, -1, -1);
   private static final Range Untracked;

   public Range(Range.Position start, Range.Position end) {
      this.start = start;
      this.end = end;
   }

   public Range.Position start() {
      return this.start;
   }

   public Range.Position end() {
      return this.end;
   }

   public boolean isTracked() {
      return this != Untracked;
   }

   static Range of(Node node, boolean start) {
      String key = start ? RangeKey : EndRangeKey;
      return !node.hasAttr(key) ? Untracked : (Range)Validate.ensureNotNull(node.attributes().getUserData(key));
   }

   public void track(Node node, boolean start) {
      node.attributes().putUserData(start ? RangeKey : EndRangeKey, this);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Range range = (Range)o;
         return !this.start.equals(range.start) ? false : this.end.equals(range.end);
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.start.hashCode();
      result = 31 * result + this.end.hashCode();
      return result;
   }

   public String toString() {
      return this.start + "-" + this.end;
   }

   static {
      Untracked = new Range(UntrackedPos, UntrackedPos);
   }

   public static class Position {
      private final int pos;
      private final int lineNumber;
      private final int columnNumber;

      public Position(int pos, int lineNumber, int columnNumber) {
         this.pos = pos;
         this.lineNumber = lineNumber;
         this.columnNumber = columnNumber;
      }

      public int pos() {
         return this.pos;
      }

      public int lineNumber() {
         return this.lineNumber;
      }

      public int columnNumber() {
         return this.columnNumber;
      }

      public boolean isTracked() {
         return this != Range.UntrackedPos;
      }

      public String toString() {
         return this.lineNumber + "," + this.columnNumber + ":" + this.pos;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            Range.Position position = (Range.Position)o;
            if (this.pos != position.pos) {
               return false;
            } else if (this.lineNumber != position.lineNumber) {
               return false;
            } else {
               return this.columnNumber == position.columnNumber;
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         int result = this.pos;
         result = 31 * result + this.lineNumber;
         result = 31 * result + this.columnNumber;
         return result;
      }
   }
}
