package com.viaversion.viaversion.api.type.types.metadata;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class MetaListType extends MetaListTypeTemplate {
   private final Type<Metadata> type;

   public MetaListType(Type<Metadata> type) {
      Preconditions.checkNotNull(type);
      this.type = type;
   }

   public List<Metadata> read(ByteBuf buffer) throws Exception {
      ArrayList list = new ArrayList();

      Metadata meta;
      do {
         meta = (Metadata)this.type.read(buffer);
         if (meta != null) {
            list.add(meta);
         }
      } while(meta != null);

      return list;
   }

   public void write(ByteBuf buffer, List<Metadata> object) throws Exception {
      Iterator var3 = object.iterator();

      while(var3.hasNext()) {
         Metadata metadata = (Metadata)var3.next();
         this.type.write(buffer, metadata);
      }

      this.type.write(buffer, (Object)null);
   }
}
