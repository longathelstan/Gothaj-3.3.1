package com.viaversion.viaversion.libs.gson.internal.bind;

import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonIOException;
import com.viaversion.viaversion.libs.gson.JsonNull;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import com.viaversion.viaversion.libs.gson.JsonSyntaxException;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.TypeAdapterFactory;
import com.viaversion.viaversion.libs.gson.annotations.SerializedName;
import com.viaversion.viaversion.libs.gson.internal.LazilyParsedNumber;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonToken;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public final class TypeAdapters {
   public static final TypeAdapter<Class> CLASS = (new TypeAdapter<Class>() {
      public void write(JsonWriter out, Class value) throws IOException {
         throw new UnsupportedOperationException("Attempted to serialize java.lang.Class: " + value.getName() + ". Forgot to register a type adapter?");
      }

      public Class read(JsonReader in) throws IOException {
         throw new UnsupportedOperationException("Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?");
      }
   }).nullSafe();
   public static final TypeAdapterFactory CLASS_FACTORY;
   public static final TypeAdapter<BitSet> BIT_SET;
   public static final TypeAdapterFactory BIT_SET_FACTORY;
   public static final TypeAdapter<Boolean> BOOLEAN;
   public static final TypeAdapter<Boolean> BOOLEAN_AS_STRING;
   public static final TypeAdapterFactory BOOLEAN_FACTORY;
   public static final TypeAdapter<Number> BYTE;
   public static final TypeAdapterFactory BYTE_FACTORY;
   public static final TypeAdapter<Number> SHORT;
   public static final TypeAdapterFactory SHORT_FACTORY;
   public static final TypeAdapter<Number> INTEGER;
   public static final TypeAdapterFactory INTEGER_FACTORY;
   public static final TypeAdapter<AtomicInteger> ATOMIC_INTEGER;
   public static final TypeAdapterFactory ATOMIC_INTEGER_FACTORY;
   public static final TypeAdapter<AtomicBoolean> ATOMIC_BOOLEAN;
   public static final TypeAdapterFactory ATOMIC_BOOLEAN_FACTORY;
   public static final TypeAdapter<AtomicIntegerArray> ATOMIC_INTEGER_ARRAY;
   public static final TypeAdapterFactory ATOMIC_INTEGER_ARRAY_FACTORY;
   public static final TypeAdapter<Number> LONG;
   public static final TypeAdapter<Number> FLOAT;
   public static final TypeAdapter<Number> DOUBLE;
   public static final TypeAdapter<Character> CHARACTER;
   public static final TypeAdapterFactory CHARACTER_FACTORY;
   public static final TypeAdapter<String> STRING;
   public static final TypeAdapter<BigDecimal> BIG_DECIMAL;
   public static final TypeAdapter<BigInteger> BIG_INTEGER;
   public static final TypeAdapter<LazilyParsedNumber> LAZILY_PARSED_NUMBER;
   public static final TypeAdapterFactory STRING_FACTORY;
   public static final TypeAdapter<StringBuilder> STRING_BUILDER;
   public static final TypeAdapterFactory STRING_BUILDER_FACTORY;
   public static final TypeAdapter<StringBuffer> STRING_BUFFER;
   public static final TypeAdapterFactory STRING_BUFFER_FACTORY;
   public static final TypeAdapter<URL> URL;
   public static final TypeAdapterFactory URL_FACTORY;
   public static final TypeAdapter<URI> URI;
   public static final TypeAdapterFactory URI_FACTORY;
   public static final TypeAdapter<InetAddress> INET_ADDRESS;
   public static final TypeAdapterFactory INET_ADDRESS_FACTORY;
   public static final TypeAdapter<UUID> UUID;
   public static final TypeAdapterFactory UUID_FACTORY;
   public static final TypeAdapter<Currency> CURRENCY;
   public static final TypeAdapterFactory CURRENCY_FACTORY;
   public static final TypeAdapter<Calendar> CALENDAR;
   public static final TypeAdapterFactory CALENDAR_FACTORY;
   public static final TypeAdapter<Locale> LOCALE;
   public static final TypeAdapterFactory LOCALE_FACTORY;
   public static final TypeAdapter<JsonElement> JSON_ELEMENT;
   public static final TypeAdapterFactory JSON_ELEMENT_FACTORY;
   public static final TypeAdapterFactory ENUM_FACTORY;

   private TypeAdapters() {
      throw new UnsupportedOperationException();
   }

   public static <TT> TypeAdapterFactory newFactory(final TypeToken<TT> type, final TypeAdapter<TT> typeAdapter) {
      return new TypeAdapterFactory() {
         public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            return typeToken.equals(type) ? typeAdapter : null;
         }
      };
   }

   public static <TT> TypeAdapterFactory newFactory(final Class<TT> type, final TypeAdapter<TT> typeAdapter) {
      return new TypeAdapterFactory() {
         public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            return typeToken.getRawType() == type ? typeAdapter : null;
         }

         public String toString() {
            return "Factory[type=" + type.getName() + ",adapter=" + typeAdapter + "]";
         }
      };
   }

   public static <TT> TypeAdapterFactory newFactory(final Class<TT> unboxed, final Class<TT> boxed, final TypeAdapter<? super TT> typeAdapter) {
      return new TypeAdapterFactory() {
         public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            Class<? super T> rawType = typeToken.getRawType();
            return rawType != unboxed && rawType != boxed ? null : typeAdapter;
         }

         public String toString() {
            return "Factory[type=" + boxed.getName() + "+" + unboxed.getName() + ",adapter=" + typeAdapter + "]";
         }
      };
   }

   public static <TT> TypeAdapterFactory newFactoryForMultipleTypes(final Class<TT> base, final Class<? extends TT> sub, final TypeAdapter<? super TT> typeAdapter) {
      return new TypeAdapterFactory() {
         public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            Class<? super T> rawType = typeToken.getRawType();
            return rawType != base && rawType != sub ? null : typeAdapter;
         }

         public String toString() {
            return "Factory[type=" + base.getName() + "+" + sub.getName() + ",adapter=" + typeAdapter + "]";
         }
      };
   }

   public static <T1> TypeAdapterFactory newTypeHierarchyFactory(final Class<T1> clazz, final TypeAdapter<T1> typeAdapter) {
      return new TypeAdapterFactory() {
         public <T2> TypeAdapter<T2> create(Gson gson, TypeToken<T2> typeToken) {
            final Class<? super T2> requestedType = typeToken.getRawType();
            return !clazz.isAssignableFrom(requestedType) ? null : new TypeAdapter<T1>() {
               public void write(JsonWriter out, T1 value) throws IOException {
                  typeAdapter.write(out, value);
               }

               public T1 read(JsonReader in) throws IOException {
                  T1 result = typeAdapter.read(in);
                  if (result != null && !requestedType.isInstance(result)) {
                     throw new JsonSyntaxException("Expected a " + requestedType.getName() + " but was " + result.getClass().getName() + "; at path " + in.getPreviousPath());
                  } else {
                     return result;
                  }
               }
            };
         }

         public String toString() {
            return "Factory[typeHierarchy=" + clazz.getName() + ",adapter=" + typeAdapter + "]";
         }
      };
   }

   static {
      CLASS_FACTORY = newFactory(Class.class, CLASS);
      BIT_SET = (new TypeAdapter<BitSet>() {
         public BitSet read(JsonReader in) throws IOException {
            BitSet bitset = new BitSet();
            in.beginArray();
            int i = 0;

            for(JsonToken tokenType = in.peek(); tokenType != JsonToken.END_ARRAY; tokenType = in.peek()) {
               boolean set;
               switch(tokenType) {
               case NUMBER:
               case STRING:
                  int intValue = in.nextInt();
                  if (intValue == 0) {
                     set = false;
                  } else {
                     if (intValue != 1) {
                        throw new JsonSyntaxException("Invalid bitset value " + intValue + ", expected 0 or 1; at path " + in.getPreviousPath());
                     }

                     set = true;
                  }
                  break;
               case BOOLEAN:
                  set = in.nextBoolean();
                  break;
               default:
                  throw new JsonSyntaxException("Invalid bitset value type: " + tokenType + "; at path " + in.getPath());
               }

               if (set) {
                  bitset.set(i);
               }

               ++i;
            }

            in.endArray();
            return bitset;
         }

         public void write(JsonWriter out, BitSet src) throws IOException {
            out.beginArray();
            int i = 0;

            for(int length = src.length(); i < length; ++i) {
               int value = src.get(i) ? 1 : 0;
               out.value((long)value);
            }

            out.endArray();
         }
      }).nullSafe();
      BIT_SET_FACTORY = newFactory(BitSet.class, BIT_SET);
      BOOLEAN = new TypeAdapter<Boolean>() {
         public Boolean read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               return peek == JsonToken.STRING ? Boolean.parseBoolean(in.nextString()) : in.nextBoolean();
            }
         }

         public void write(JsonWriter out, Boolean value) throws IOException {
            out.value(value);
         }
      };
      BOOLEAN_AS_STRING = new TypeAdapter<Boolean>() {
         public Boolean read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               return Boolean.valueOf(in.nextString());
            }
         }

         public void write(JsonWriter out, Boolean value) throws IOException {
            out.value(value == null ? "null" : value.toString());
         }
      };
      BOOLEAN_FACTORY = newFactory(Boolean.TYPE, Boolean.class, BOOLEAN);
      BYTE = new TypeAdapter<Number>() {
         public Number read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               int intValue;
               try {
                  intValue = in.nextInt();
               } catch (NumberFormatException var4) {
                  throw new JsonSyntaxException(var4);
               }

               if (intValue <= 255 && intValue >= -128) {
                  return (byte)intValue;
               } else {
                  throw new JsonSyntaxException("Lossy conversion from " + intValue + " to byte; at path " + in.getPreviousPath());
               }
            }
         }

         public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
               out.nullValue();
            } else {
               out.value((long)value.byteValue());
            }

         }
      };
      BYTE_FACTORY = newFactory(Byte.TYPE, Byte.class, BYTE);
      SHORT = new TypeAdapter<Number>() {
         public Number read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               int intValue;
               try {
                  intValue = in.nextInt();
               } catch (NumberFormatException var4) {
                  throw new JsonSyntaxException(var4);
               }

               if (intValue <= 65535 && intValue >= -32768) {
                  return (short)intValue;
               } else {
                  throw new JsonSyntaxException("Lossy conversion from " + intValue + " to short; at path " + in.getPreviousPath());
               }
            }
         }

         public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
               out.nullValue();
            } else {
               out.value((long)value.shortValue());
            }

         }
      };
      SHORT_FACTORY = newFactory(Short.TYPE, Short.class, SHORT);
      INTEGER = new TypeAdapter<Number>() {
         public Number read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               try {
                  return in.nextInt();
               } catch (NumberFormatException var3) {
                  throw new JsonSyntaxException(var3);
               }
            }
         }

         public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
               out.nullValue();
            } else {
               out.value((long)value.intValue());
            }

         }
      };
      INTEGER_FACTORY = newFactory(Integer.TYPE, Integer.class, INTEGER);
      ATOMIC_INTEGER = (new TypeAdapter<AtomicInteger>() {
         public AtomicInteger read(JsonReader in) throws IOException {
            try {
               return new AtomicInteger(in.nextInt());
            } catch (NumberFormatException var3) {
               throw new JsonSyntaxException(var3);
            }
         }

         public void write(JsonWriter out, AtomicInteger value) throws IOException {
            out.value((long)value.get());
         }
      }).nullSafe();
      ATOMIC_INTEGER_FACTORY = newFactory(AtomicInteger.class, ATOMIC_INTEGER);
      ATOMIC_BOOLEAN = (new TypeAdapter<AtomicBoolean>() {
         public AtomicBoolean read(JsonReader in) throws IOException {
            return new AtomicBoolean(in.nextBoolean());
         }

         public void write(JsonWriter out, AtomicBoolean value) throws IOException {
            out.value(value.get());
         }
      }).nullSafe();
      ATOMIC_BOOLEAN_FACTORY = newFactory(AtomicBoolean.class, ATOMIC_BOOLEAN);
      ATOMIC_INTEGER_ARRAY = (new TypeAdapter<AtomicIntegerArray>() {
         public AtomicIntegerArray read(JsonReader in) throws IOException {
            List<Integer> list = new ArrayList();
            in.beginArray();

            int length;
            while(in.hasNext()) {
               try {
                  length = in.nextInt();
                  list.add(length);
               } catch (NumberFormatException var6) {
                  throw new JsonSyntaxException(var6);
               }
            }

            in.endArray();
            length = list.size();
            AtomicIntegerArray array = new AtomicIntegerArray(length);

            for(int i = 0; i < length; ++i) {
               array.set(i, (Integer)list.get(i));
            }

            return array;
         }

         public void write(JsonWriter out, AtomicIntegerArray value) throws IOException {
            out.beginArray();
            int i = 0;

            for(int length = value.length(); i < length; ++i) {
               out.value((long)value.get(i));
            }

            out.endArray();
         }
      }).nullSafe();
      ATOMIC_INTEGER_ARRAY_FACTORY = newFactory(AtomicIntegerArray.class, ATOMIC_INTEGER_ARRAY);
      LONG = new TypeAdapter<Number>() {
         public Number read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               try {
                  return in.nextLong();
               } catch (NumberFormatException var3) {
                  throw new JsonSyntaxException(var3);
               }
            }
         }

         public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
               out.nullValue();
            } else {
               out.value(value.longValue());
            }

         }
      };
      FLOAT = new TypeAdapter<Number>() {
         public Number read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               return (float)in.nextDouble();
            }
         }

         public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
               out.nullValue();
            } else {
               Number floatNumber = value instanceof Float ? value : value.floatValue();
               out.value((Number)floatNumber);
            }

         }
      };
      DOUBLE = new TypeAdapter<Number>() {
         public Number read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               return in.nextDouble();
            }
         }

         public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
               out.nullValue();
            } else {
               out.value(value.doubleValue());
            }

         }
      };
      CHARACTER = new TypeAdapter<Character>() {
         public Character read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               String str = in.nextString();
               if (str.length() != 1) {
                  throw new JsonSyntaxException("Expecting character, got: " + str + "; at " + in.getPreviousPath());
               } else {
                  return str.charAt(0);
               }
            }
         }

         public void write(JsonWriter out, Character value) throws IOException {
            out.value(value == null ? null : String.valueOf(value));
         }
      };
      CHARACTER_FACTORY = newFactory(Character.TYPE, Character.class, CHARACTER);
      STRING = new TypeAdapter<String>() {
         public String read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               return peek == JsonToken.BOOLEAN ? Boolean.toString(in.nextBoolean()) : in.nextString();
            }
         }

         public void write(JsonWriter out, String value) throws IOException {
            out.value(value);
         }
      };
      BIG_DECIMAL = new TypeAdapter<BigDecimal>() {
         public BigDecimal read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               String s = in.nextString();

               try {
                  return new BigDecimal(s);
               } catch (NumberFormatException var4) {
                  throw new JsonSyntaxException("Failed parsing '" + s + "' as BigDecimal; at path " + in.getPreviousPath(), var4);
               }
            }
         }

         public void write(JsonWriter out, BigDecimal value) throws IOException {
            out.value((Number)value);
         }
      };
      BIG_INTEGER = new TypeAdapter<BigInteger>() {
         public BigInteger read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               String s = in.nextString();

               try {
                  return new BigInteger(s);
               } catch (NumberFormatException var4) {
                  throw new JsonSyntaxException("Failed parsing '" + s + "' as BigInteger; at path " + in.getPreviousPath(), var4);
               }
            }
         }

         public void write(JsonWriter out, BigInteger value) throws IOException {
            out.value((Number)value);
         }
      };
      LAZILY_PARSED_NUMBER = new TypeAdapter<LazilyParsedNumber>() {
         public LazilyParsedNumber read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               return new LazilyParsedNumber(in.nextString());
            }
         }

         public void write(JsonWriter out, LazilyParsedNumber value) throws IOException {
            out.value((Number)value);
         }
      };
      STRING_FACTORY = newFactory(String.class, STRING);
      STRING_BUILDER = new TypeAdapter<StringBuilder>() {
         public StringBuilder read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               return new StringBuilder(in.nextString());
            }
         }

         public void write(JsonWriter out, StringBuilder value) throws IOException {
            out.value(value == null ? null : value.toString());
         }
      };
      STRING_BUILDER_FACTORY = newFactory(StringBuilder.class, STRING_BUILDER);
      STRING_BUFFER = new TypeAdapter<StringBuffer>() {
         public StringBuffer read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               return new StringBuffer(in.nextString());
            }
         }

         public void write(JsonWriter out, StringBuffer value) throws IOException {
            out.value(value == null ? null : value.toString());
         }
      };
      STRING_BUFFER_FACTORY = newFactory(StringBuffer.class, STRING_BUFFER);
      URL = new TypeAdapter<URL>() {
         public URL read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               String nextString = in.nextString();
               return "null".equals(nextString) ? null : new URL(nextString);
            }
         }

         public void write(JsonWriter out, URL value) throws IOException {
            out.value(value == null ? null : value.toExternalForm());
         }
      };
      URL_FACTORY = newFactory(URL.class, URL);
      URI = new TypeAdapter<URI>() {
         public URI read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               try {
                  String nextString = in.nextString();
                  return "null".equals(nextString) ? null : new URI(nextString);
               } catch (URISyntaxException var3) {
                  throw new JsonIOException(var3);
               }
            }
         }

         public void write(JsonWriter out, URI value) throws IOException {
            out.value(value == null ? null : value.toASCIIString());
         }
      };
      URI_FACTORY = newFactory(URI.class, URI);
      INET_ADDRESS = new TypeAdapter<InetAddress>() {
         public InetAddress read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               return InetAddress.getByName(in.nextString());
            }
         }

         public void write(JsonWriter out, InetAddress value) throws IOException {
            out.value(value == null ? null : value.getHostAddress());
         }
      };
      INET_ADDRESS_FACTORY = newTypeHierarchyFactory(InetAddress.class, INET_ADDRESS);
      UUID = new TypeAdapter<UUID>() {
         public UUID read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               String s = in.nextString();

               try {
                  return java.util.UUID.fromString(s);
               } catch (IllegalArgumentException var4) {
                  throw new JsonSyntaxException("Failed parsing '" + s + "' as UUID; at path " + in.getPreviousPath(), var4);
               }
            }
         }

         public void write(JsonWriter out, UUID value) throws IOException {
            out.value(value == null ? null : value.toString());
         }
      };
      UUID_FACTORY = newFactory(UUID.class, UUID);
      CURRENCY = (new TypeAdapter<Currency>() {
         public Currency read(JsonReader in) throws IOException {
            String s = in.nextString();

            try {
               return Currency.getInstance(s);
            } catch (IllegalArgumentException var4) {
               throw new JsonSyntaxException("Failed parsing '" + s + "' as Currency; at path " + in.getPreviousPath(), var4);
            }
         }

         public void write(JsonWriter out, Currency value) throws IOException {
            out.value(value.getCurrencyCode());
         }
      }).nullSafe();
      CURRENCY_FACTORY = newFactory(Currency.class, CURRENCY);
      CALENDAR = new TypeAdapter<Calendar>() {
         private static final String YEAR = "year";
         private static final String MONTH = "month";
         private static final String DAY_OF_MONTH = "dayOfMonth";
         private static final String HOUR_OF_DAY = "hourOfDay";
         private static final String MINUTE = "minute";
         private static final String SECOND = "second";

         public Calendar read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               in.beginObject();
               int year = 0;
               int month = 0;
               int dayOfMonth = 0;
               int hourOfDay = 0;
               int minute = 0;
               int second = 0;

               while(in.peek() != JsonToken.END_OBJECT) {
                  String name = in.nextName();
                  int value = in.nextInt();
                  if ("year".equals(name)) {
                     year = value;
                  } else if ("month".equals(name)) {
                     month = value;
                  } else if ("dayOfMonth".equals(name)) {
                     dayOfMonth = value;
                  } else if ("hourOfDay".equals(name)) {
                     hourOfDay = value;
                  } else if ("minute".equals(name)) {
                     minute = value;
                  } else if ("second".equals(name)) {
                     second = value;
                  }
               }

               in.endObject();
               return new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second);
            }
         }

         public void write(JsonWriter out, Calendar value) throws IOException {
            if (value == null) {
               out.nullValue();
            } else {
               out.beginObject();
               out.name("year");
               out.value((long)value.get(1));
               out.name("month");
               out.value((long)value.get(2));
               out.name("dayOfMonth");
               out.value((long)value.get(5));
               out.name("hourOfDay");
               out.value((long)value.get(11));
               out.name("minute");
               out.value((long)value.get(12));
               out.name("second");
               out.value((long)value.get(13));
               out.endObject();
            }
         }
      };
      CALENDAR_FACTORY = newFactoryForMultipleTypes(Calendar.class, GregorianCalendar.class, CALENDAR);
      LOCALE = new TypeAdapter<Locale>() {
         public Locale read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
               in.nextNull();
               return null;
            } else {
               String locale = in.nextString();
               StringTokenizer tokenizer = new StringTokenizer(locale, "_");
               String language = null;
               String country = null;
               String variant = null;
               if (tokenizer.hasMoreElements()) {
                  language = tokenizer.nextToken();
               }

               if (tokenizer.hasMoreElements()) {
                  country = tokenizer.nextToken();
               }

               if (tokenizer.hasMoreElements()) {
                  variant = tokenizer.nextToken();
               }

               if (country == null && variant == null) {
                  return new Locale(language);
               } else {
                  return variant == null ? new Locale(language, country) : new Locale(language, country, variant);
               }
            }
         }

         public void write(JsonWriter out, Locale value) throws IOException {
            out.value(value == null ? null : value.toString());
         }
      };
      LOCALE_FACTORY = newFactory(Locale.class, LOCALE);
      JSON_ELEMENT = new TypeAdapter<JsonElement>() {
         private JsonElement tryBeginNesting(JsonReader in, JsonToken peeked) throws IOException {
            switch(peeked) {
            case BEGIN_ARRAY:
               in.beginArray();
               return new JsonArray();
            case BEGIN_OBJECT:
               in.beginObject();
               return new JsonObject();
            default:
               return null;
            }
         }

         private JsonElement readTerminal(JsonReader in, JsonToken peeked) throws IOException {
            switch(peeked) {
            case NUMBER:
               String number = in.nextString();
               return new JsonPrimitive(new LazilyParsedNumber(number));
            case STRING:
               return new JsonPrimitive(in.nextString());
            case BOOLEAN:
               return new JsonPrimitive(in.nextBoolean());
            case BEGIN_ARRAY:
            case BEGIN_OBJECT:
            default:
               throw new IllegalStateException("Unexpected token: " + peeked);
            case NULL:
               in.nextNull();
               return JsonNull.INSTANCE;
            }
         }

         public JsonElement read(JsonReader in) throws IOException {
            if (in instanceof JsonTreeReader) {
               return ((JsonTreeReader)in).nextJsonElement();
            } else {
               JsonToken peeked = in.peek();
               JsonElement current = this.tryBeginNesting(in, peeked);
               if (current == null) {
                  return this.readTerminal(in, peeked);
               } else {
                  ArrayDeque stack = new ArrayDeque();

                  while(true) {
                     while(!in.hasNext()) {
                        if (current instanceof JsonArray) {
                           in.endArray();
                        } else {
                           in.endObject();
                        }

                        if (stack.isEmpty()) {
                           return current;
                        }

                        current = (JsonElement)stack.removeLast();
                     }

                     String name = null;
                     if (current instanceof JsonObject) {
                        name = in.nextName();
                     }

                     peeked = in.peek();
                     JsonElement value = this.tryBeginNesting(in, peeked);
                     boolean isNesting = value != null;
                     if (value == null) {
                        value = this.readTerminal(in, peeked);
                     }

                     if (current instanceof JsonArray) {
                        ((JsonArray)current).add(value);
                     } else {
                        ((JsonObject)current).add(name, value);
                     }

                     if (isNesting) {
                        stack.addLast(current);
                        current = value;
                     }
                  }
               }
            }
         }

         public void write(JsonWriter out, JsonElement value) throws IOException {
            if (value != null && !value.isJsonNull()) {
               if (value.isJsonPrimitive()) {
                  JsonPrimitive primitive = value.getAsJsonPrimitive();
                  if (primitive.isNumber()) {
                     out.value(primitive.getAsNumber());
                  } else if (primitive.isBoolean()) {
                     out.value(primitive.getAsBoolean());
                  } else {
                     out.value(primitive.getAsString());
                  }
               } else {
                  Iterator var5;
                  if (value.isJsonArray()) {
                     out.beginArray();
                     var5 = value.getAsJsonArray().iterator();

                     while(var5.hasNext()) {
                        JsonElement ex = (JsonElement)var5.next();
                        this.write(out, ex);
                     }

                     out.endArray();
                  } else {
                     if (!value.isJsonObject()) {
                        throw new IllegalArgumentException("Couldn't write " + value.getClass());
                     }

                     out.beginObject();
                     var5 = value.getAsJsonObject().entrySet().iterator();

                     while(var5.hasNext()) {
                        Entry<String, JsonElement> e = (Entry)var5.next();
                        out.name((String)e.getKey());
                        this.write(out, (JsonElement)e.getValue());
                     }

                     out.endObject();
                  }
               }
            } else {
               out.nullValue();
            }

         }
      };
      JSON_ELEMENT_FACTORY = newTypeHierarchyFactory(JsonElement.class, JSON_ELEMENT);
      ENUM_FACTORY = new TypeAdapterFactory() {
         public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            Class<? super T> rawType = typeToken.getRawType();
            if (Enum.class.isAssignableFrom(rawType) && rawType != Enum.class) {
               if (!rawType.isEnum()) {
                  rawType = rawType.getSuperclass();
               }

               TypeAdapter<T> adapter = new TypeAdapters.EnumTypeAdapter(rawType);
               return adapter;
            } else {
               return null;
            }
         }
      };
   }

   private static final class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {
      private final Map<String, T> nameToConstant = new HashMap();
      private final Map<String, T> stringToConstant = new HashMap();
      private final Map<T, String> constantToName = new HashMap();

      public EnumTypeAdapter(final Class<T> classOfT) {
         try {
            Field[] constantFields = (Field[])AccessController.doPrivileged(new PrivilegedAction<Field[]>() {
               public Field[] run() {
                  Field[] fields = classOfT.getDeclaredFields();
                  ArrayList<Field> constantFieldsList = new ArrayList(fields.length);
                  Field[] constantFields = fields;
                  int var4 = fields.length;

                  for(int var5 = 0; var5 < var4; ++var5) {
                     Field f = constantFields[var5];
                     if (f.isEnumConstant()) {
                        constantFieldsList.add(f);
                     }
                  }

                  constantFields = (Field[])constantFieldsList.toArray(new Field[0]);
                  AccessibleObject.setAccessible(constantFields, true);
                  return constantFields;
               }
            });
            Field[] var3 = constantFields;
            int var4 = constantFields.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Field constantField = var3[var5];
               T constant = (Enum)constantField.get((Object)null);
               String name = constant.name();
               String toStringVal = constant.toString();
               SerializedName annotation = (SerializedName)constantField.getAnnotation(SerializedName.class);
               if (annotation != null) {
                  name = annotation.value();
                  String[] var11 = annotation.alternate();
                  int var12 = var11.length;

                  for(int var13 = 0; var13 < var12; ++var13) {
                     String alternate = var11[var13];
                     this.nameToConstant.put(alternate, constant);
                  }
               }

               this.nameToConstant.put(name, constant);
               this.stringToConstant.put(toStringVal, constant);
               this.constantToName.put(constant, name);
            }

         } catch (IllegalAccessException var15) {
            throw new AssertionError(var15);
         }
      }

      public T read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         } else {
            String key = in.nextString();
            T constant = (Enum)this.nameToConstant.get(key);
            return constant == null ? (Enum)this.stringToConstant.get(key) : constant;
         }
      }

      public void write(JsonWriter out, T value) throws IOException {
         out.value(value == null ? null : (String)this.constantToName.get(value));
      }
   }
}
