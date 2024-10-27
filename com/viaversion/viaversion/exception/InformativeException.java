package com.viaversion.viaversion.exception;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class InformativeException extends Exception {
   private final Map<String, Object> info = new HashMap();
   private boolean shouldBePrinted = true;
   private int sources;

   public InformativeException(Throwable cause) {
      super(cause);
   }

   public InformativeException set(String key, Object value) {
      this.info.put(key, value);
      return this;
   }

   public InformativeException addSource(Class<?> sourceClazz) {
      return this.set("Source " + this.sources++, this.getSource(sourceClazz));
   }

   private String getSource(Class<?> sourceClazz) {
      return sourceClazz.isAnonymousClass() ? sourceClazz.getName() + " (Anonymous)" : sourceClazz.getName();
   }

   public boolean shouldBePrinted() {
      return this.shouldBePrinted;
   }

   public void setShouldBePrinted(boolean shouldBePrinted) {
      this.shouldBePrinted = shouldBePrinted;
   }

   public String getMessage() {
      StringBuilder builder = new StringBuilder("Please report this on the Via support Discord or open an issue on the relevant GitHub repository\n");
      boolean first = true;

      for(Iterator var3 = this.info.entrySet().iterator(); var3.hasNext(); first = false) {
         Entry<String, Object> entry = (Entry)var3.next();
         if (!first) {
            builder.append(", ");
         }

         builder.append((String)entry.getKey()).append(": ").append(entry.getValue());
      }

      return builder.toString();
   }

   public Throwable fillInStackTrace() {
      return this;
   }
}
