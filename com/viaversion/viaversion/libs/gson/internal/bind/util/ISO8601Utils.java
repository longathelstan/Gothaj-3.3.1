package com.viaversion.viaversion.libs.gson.internal.bind.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class ISO8601Utils {
   private static final String UTC_ID = "UTC";
   private static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");

   public static String format(Date date) {
      return format(date, false, TIMEZONE_UTC);
   }

   public static String format(Date date, boolean millis) {
      return format(date, millis, TIMEZONE_UTC);
   }

   public static String format(Date date, boolean millis, TimeZone tz) {
      Calendar calendar = new GregorianCalendar(tz, Locale.US);
      calendar.setTime(date);
      int capacity = "yyyy-MM-ddThh:mm:ss".length();
      capacity += millis ? ".sss".length() : 0;
      capacity += tz.getRawOffset() == 0 ? "Z".length() : "+hh:mm".length();
      StringBuilder formatted = new StringBuilder(capacity);
      padInt(formatted, calendar.get(1), "yyyy".length());
      formatted.append('-');
      padInt(formatted, calendar.get(2) + 1, "MM".length());
      formatted.append('-');
      padInt(formatted, calendar.get(5), "dd".length());
      formatted.append('T');
      padInt(formatted, calendar.get(11), "hh".length());
      formatted.append(':');
      padInt(formatted, calendar.get(12), "mm".length());
      formatted.append(':');
      padInt(formatted, calendar.get(13), "ss".length());
      if (millis) {
         formatted.append('.');
         padInt(formatted, calendar.get(14), "sss".length());
      }

      int offset = tz.getOffset(calendar.getTimeInMillis());
      if (offset != 0) {
         int hours = Math.abs(offset / '\uea60' / 60);
         int minutes = Math.abs(offset / '\uea60' % 60);
         formatted.append((char)(offset < 0 ? '-' : '+'));
         padInt(formatted, hours, "hh".length());
         formatted.append(':');
         padInt(formatted, minutes, "mm".length());
      } else {
         formatted.append('Z');
      }

      return formatted.toString();
   }

   public static Date parse(String date, ParsePosition pos) throws ParseException {
      Object fail = null;

      try {
         int offset = pos.getIndex();
         int var10001 = offset;
         offset += 4;
         int year = parseInt(date, var10001, offset);
         if (checkOffset(date, offset, '-')) {
            ++offset;
         }

         var10001 = offset;
         offset += 2;
         int month = parseInt(date, var10001, offset);
         if (checkOffset(date, offset, '-')) {
            ++offset;
         }

         var10001 = offset;
         offset += 2;
         int day = parseInt(date, var10001, offset);
         int hour = 0;
         int minutes = 0;
         int seconds = 0;
         int milliseconds = 0;
         boolean hasT = checkOffset(date, offset, 'T');
         if (!hasT && date.length() <= offset) {
            Calendar calendar = new GregorianCalendar(year, month - 1, day);
            calendar.setLenient(false);
            pos.setIndex(offset);
            return calendar.getTime();
         }

         if (hasT) {
            ++offset;
            var10001 = offset;
            offset += 2;
            hour = parseInt(date, var10001, offset);
            if (checkOffset(date, offset, ':')) {
               ++offset;
            }

            var10001 = offset;
            offset += 2;
            minutes = parseInt(date, var10001, offset);
            if (checkOffset(date, offset, ':')) {
               ++offset;
            }

            if (date.length() > offset) {
               char c = date.charAt(offset);
               if (c != 'Z' && c != '+' && c != '-') {
                  var10001 = offset;
                  offset += 2;
                  seconds = parseInt(date, var10001, offset);
                  if (seconds > 59 && seconds < 63) {
                     seconds = 59;
                  }

                  if (checkOffset(date, offset, '.')) {
                     ++offset;
                     int endOffset = indexOfNonDigit(date, offset + 1);
                     int parseEndOffset = Math.min(endOffset, offset + 3);
                     int fraction = parseInt(date, offset, parseEndOffset);
                     switch(parseEndOffset - offset) {
                     case 1:
                        milliseconds = fraction * 100;
                        break;
                     case 2:
                        milliseconds = fraction * 10;
                        break;
                     default:
                        milliseconds = fraction;
                     }

                     offset = endOffset;
                  }
               }
            }
         }

         if (date.length() <= offset) {
            throw new IllegalArgumentException("No time zone indicator");
         }

         TimeZone timezone = null;
         char timezoneIndicator = date.charAt(offset);
         if (timezoneIndicator == 'Z') {
            timezone = TIMEZONE_UTC;
            ++offset;
         } else {
            if (timezoneIndicator != '+' && timezoneIndicator != '-') {
               throw new IndexOutOfBoundsException("Invalid time zone indicator '" + timezoneIndicator + "'");
            }

            String timezoneOffset = date.substring(offset);
            timezoneOffset = timezoneOffset.length() >= 5 ? timezoneOffset : timezoneOffset + "00";
            offset += timezoneOffset.length();
            if (!"+0000".equals(timezoneOffset) && !"+00:00".equals(timezoneOffset)) {
               String timezoneId = "GMT" + timezoneOffset;
               timezone = TimeZone.getTimeZone(timezoneId);
               String act = timezone.getID();
               if (!act.equals(timezoneId)) {
                  String cleaned = act.replace(":", "");
                  if (!cleaned.equals(timezoneId)) {
                     throw new IndexOutOfBoundsException("Mismatching time zone indicator: " + timezoneId + " given, resolves to " + timezone.getID());
                  }
               }
            } else {
               timezone = TIMEZONE_UTC;
            }
         }

         Calendar calendar = new GregorianCalendar(timezone);
         calendar.setLenient(false);
         calendar.set(1, year);
         calendar.set(2, month - 1);
         calendar.set(5, day);
         calendar.set(11, hour);
         calendar.set(12, minutes);
         calendar.set(13, seconds);
         calendar.set(14, milliseconds);
         pos.setIndex(offset);
         return calendar.getTime();
      } catch (IndexOutOfBoundsException var18) {
         fail = var18;
      } catch (NumberFormatException var19) {
         fail = var19;
      } catch (IllegalArgumentException var20) {
         fail = var20;
      }

      String input = date == null ? null : '"' + date + '"';
      String msg = ((Exception)fail).getMessage();
      if (msg == null || msg.isEmpty()) {
         msg = "(" + fail.getClass().getName() + ")";
      }

      ParseException ex = new ParseException("Failed to parse date [" + input + "]: " + msg, pos.getIndex());
      ex.initCause((Throwable)fail);
      throw ex;
   }

   private static boolean checkOffset(String value, int offset, char expected) {
      return offset < value.length() && value.charAt(offset) == expected;
   }

   private static int parseInt(String value, int beginIndex, int endIndex) throws NumberFormatException {
      if (beginIndex >= 0 && endIndex <= value.length() && beginIndex <= endIndex) {
         int i = beginIndex;
         int result = 0;
         int digit;
         if (beginIndex < endIndex) {
            i = beginIndex + 1;
            digit = Character.digit(value.charAt(beginIndex), 10);
            if (digit < 0) {
               throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex));
            }

            result = -digit;
         }

         while(i < endIndex) {
            digit = Character.digit(value.charAt(i++), 10);
            if (digit < 0) {
               throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex));
            }

            result *= 10;
            result -= digit;
         }

         return -result;
      } else {
         throw new NumberFormatException(value);
      }
   }

   private static void padInt(StringBuilder buffer, int value, int length) {
      String strValue = Integer.toString(value);

      for(int i = length - strValue.length(); i > 0; --i) {
         buffer.append('0');
      }

      buffer.append(strValue);
   }

   private static int indexOfNonDigit(String string, int offset) {
      for(int i = offset; i < string.length(); ++i) {
         char c = string.charAt(i);
         if (c < '0' || c > '9') {
            return i;
         }
      }

      return string.length();
   }
}
