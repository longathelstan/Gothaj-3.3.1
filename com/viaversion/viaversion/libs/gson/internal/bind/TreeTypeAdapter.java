package com.viaversion.viaversion.libs.gson.internal.bind;

import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.JsonDeserializationContext;
import com.viaversion.viaversion.libs.gson.JsonDeserializer;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.libs.gson.JsonSerializationContext;
import com.viaversion.viaversion.libs.gson.JsonSerializer;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.TypeAdapterFactory;
import com.viaversion.viaversion.libs.gson.internal.$Gson$Preconditions;
import com.viaversion.viaversion.libs.gson.internal.Streams;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;

public final class TreeTypeAdapter<T> extends SerializationDelegatingTypeAdapter<T> {
   private final JsonSerializer<T> serializer;
   private final JsonDeserializer<T> deserializer;
   final Gson gson;
   private final TypeToken<T> typeToken;
   private final TypeAdapterFactory skipPast;
   private final TreeTypeAdapter<T>.GsonContextImpl context;
   private final boolean nullSafe;
   private volatile TypeAdapter<T> delegate;

   public TreeTypeAdapter(JsonSerializer<T> serializer, JsonDeserializer<T> deserializer, Gson gson, TypeToken<T> typeToken, TypeAdapterFactory skipPast, boolean nullSafe) {
      this.context = new TreeTypeAdapter.GsonContextImpl();
      this.serializer = serializer;
      this.deserializer = deserializer;
      this.gson = gson;
      this.typeToken = typeToken;
      this.skipPast = skipPast;
      this.nullSafe = nullSafe;
   }

   public TreeTypeAdapter(JsonSerializer<T> serializer, JsonDeserializer<T> deserializer, Gson gson, TypeToken<T> typeToken, TypeAdapterFactory skipPast) {
      this(serializer, deserializer, gson, typeToken, skipPast, true);
   }

   public T read(JsonReader in) throws IOException {
      if (this.deserializer == null) {
         return this.delegate().read(in);
      } else {
         JsonElement value = Streams.parse(in);
         return this.nullSafe && value.isJsonNull() ? null : this.deserializer.deserialize(value, this.typeToken.getType(), this.context);
      }
   }

   public void write(JsonWriter out, T value) throws IOException {
      if (this.serializer == null) {
         this.delegate().write(out, value);
      } else if (this.nullSafe && value == null) {
         out.nullValue();
      } else {
         JsonElement tree = this.serializer.serialize(value, this.typeToken.getType(), this.context);
         Streams.write(tree, out);
      }
   }

   private TypeAdapter<T> delegate() {
      TypeAdapter<T> d = this.delegate;
      return d != null ? d : (this.delegate = this.gson.getDelegateAdapter(this.skipPast, this.typeToken));
   }

   public TypeAdapter<T> getSerializationDelegate() {
      return (TypeAdapter)(this.serializer != null ? this : this.delegate());
   }

   public static TypeAdapterFactory newFactory(TypeToken<?> exactType, Object typeAdapter) {
      return new TreeTypeAdapter.SingleTypeFactory(typeAdapter, exactType, false, (Class)null);
   }

   public static TypeAdapterFactory newFactoryWithMatchRawType(TypeToken<?> exactType, Object typeAdapter) {
      boolean matchRawType = exactType.getType() == exactType.getRawType();
      return new TreeTypeAdapter.SingleTypeFactory(typeAdapter, exactType, matchRawType, (Class)null);
   }

   public static TypeAdapterFactory newTypeHierarchyFactory(Class<?> hierarchyType, Object typeAdapter) {
      return new TreeTypeAdapter.SingleTypeFactory(typeAdapter, (TypeToken)null, false, hierarchyType);
   }

   private final class GsonContextImpl implements JsonSerializationContext, JsonDeserializationContext {
      private GsonContextImpl() {
      }

      public JsonElement serialize(Object src) {
         return TreeTypeAdapter.this.gson.toJsonTree(src);
      }

      public JsonElement serialize(Object src, Type typeOfSrc) {
         return TreeTypeAdapter.this.gson.toJsonTree(src, typeOfSrc);
      }

      public <R> R deserialize(JsonElement json, Type typeOfT) throws JsonParseException {
         return TreeTypeAdapter.this.gson.fromJson(json, typeOfT);
      }

      // $FF: synthetic method
      GsonContextImpl(Object x1) {
         this();
      }
   }

   private static final class SingleTypeFactory implements TypeAdapterFactory {
      private final TypeToken<?> exactType;
      private final boolean matchRawType;
      private final Class<?> hierarchyType;
      private final JsonSerializer<?> serializer;
      private final JsonDeserializer<?> deserializer;

      SingleTypeFactory(Object typeAdapter, TypeToken<?> exactType, boolean matchRawType, Class<?> hierarchyType) {
         this.serializer = typeAdapter instanceof JsonSerializer ? (JsonSerializer)typeAdapter : null;
         this.deserializer = typeAdapter instanceof JsonDeserializer ? (JsonDeserializer)typeAdapter : null;
         $Gson$Preconditions.checkArgument(this.serializer != null || this.deserializer != null);
         this.exactType = exactType;
         this.matchRawType = matchRawType;
         this.hierarchyType = hierarchyType;
      }

      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
         boolean matches = this.exactType != null ? this.exactType.equals(type) || this.matchRawType && this.exactType.getType() == type.getRawType() : this.hierarchyType.isAssignableFrom(type.getRawType());
         return matches ? new TreeTypeAdapter(this.serializer, this.deserializer, gson, type, this) : null;
      }
   }
}
