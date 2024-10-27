package com.viaversion.viabackwards.api.exceptions;

import java.io.IOException;

public class RemovedValueException extends IOException {
   public static final RemovedValueException EX = new RemovedValueException() {
      public Throwable fillInStackTrace() {
         return this;
      }
   };
}
