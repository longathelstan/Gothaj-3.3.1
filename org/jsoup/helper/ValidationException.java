package org.jsoup.helper;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends IllegalArgumentException {
   public static final String Validator = Validate.class.getName();

   public ValidationException(String msg) {
      super(msg);
   }

   public synchronized Throwable fillInStackTrace() {
      super.fillInStackTrace();
      StackTraceElement[] stackTrace = this.getStackTrace();
      List<StackTraceElement> filteredTrace = new ArrayList();
      StackTraceElement[] var3 = stackTrace;
      int var4 = stackTrace.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         StackTraceElement trace = var3[var5];
         if (!trace.getClassName().equals(Validator)) {
            filteredTrace.add(trace);
         }
      }

      this.setStackTrace((StackTraceElement[])filteredTrace.toArray(new StackTraceElement[0]));
      return this;
   }
}
