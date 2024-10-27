package com.viaversion.viaversion.libs.gson;

import java.lang.reflect.Field;
import java.util.Locale;

public enum FieldNamingPolicy implements FieldNamingStrategy {
   IDENTITY {
      public String translateName(Field f) {
         return f.getName();
      }
   },
   UPPER_CAMEL_CASE {
      public String translateName(Field f) {
         return upperCaseFirstLetter(f.getName());
      }
   },
   UPPER_CAMEL_CASE_WITH_SPACES {
      public String translateName(Field f) {
         return upperCaseFirstLetter(separateCamelCase(f.getName(), ' '));
      }
   },
   UPPER_CASE_WITH_UNDERSCORES {
      public String translateName(Field f) {
         return separateCamelCase(f.getName(), '_').toUpperCase(Locale.ENGLISH);
      }
   },
   LOWER_CASE_WITH_UNDERSCORES {
      public String translateName(Field f) {
         return separateCamelCase(f.getName(), '_').toLowerCase(Locale.ENGLISH);
      }
   },
   LOWER_CASE_WITH_DASHES {
      public String translateName(Field f) {
         return separateCamelCase(f.getName(), '-').toLowerCase(Locale.ENGLISH);
      }
   },
   LOWER_CASE_WITH_DOTS {
      public String translateName(Field f) {
         return separateCamelCase(f.getName(), '.').toLowerCase(Locale.ENGLISH);
      }
   };

   private FieldNamingPolicy() {
   }

   static String separateCamelCase(String name, char separator) {
      StringBuilder translation = new StringBuilder();
      int i = 0;

      for(int length = name.length(); i < length; ++i) {
         char character = name.charAt(i);
         if (Character.isUpperCase(character) && translation.length() != 0) {
            translation.append(separator);
         }

         translation.append(character);
      }

      return translation.toString();
   }

   static String upperCaseFirstLetter(String s) {
      int length = s.length();

      for(int i = 0; i < length; ++i) {
         char c = s.charAt(i);
         if (Character.isLetter(c)) {
            if (Character.isUpperCase(c)) {
               return s;
            }

            char uppercased = Character.toUpperCase(c);
            if (i == 0) {
               return uppercased + s.substring(1);
            }

            return s.substring(0, i) + uppercased + s.substring(i + 1);
         }
      }

      return s;
   }

   // $FF: synthetic method
   FieldNamingPolicy(Object x2) {
      this();
   }
}