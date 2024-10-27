package com.viaversion.viaversion.rewriter.meta;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MetaFilter {
   private final MetaHandler handler;
   private final EntityType type;
   private final int index;
   private final boolean filterFamily;

   public MetaFilter(@Nullable EntityType type, boolean filterFamily, int index, MetaHandler handler) {
      Preconditions.checkNotNull(handler, "MetaHandler cannot be null");
      this.type = type;
      this.filterFamily = filterFamily;
      this.index = index;
      this.handler = handler;
   }

   public int index() {
      return this.index;
   }

   @Nullable
   public EntityType type() {
      return this.type;
   }

   public MetaHandler handler() {
      return this.handler;
   }

   public boolean filterFamily() {
      return this.filterFamily;
   }

   public boolean isFiltered(@Nullable EntityType type, Metadata metadata) {
      return (this.index == -1 || metadata.id() == this.index) && (this.type == null || this.matchesType(type));
   }

   private boolean matchesType(@Nullable EntityType type) {
      boolean var10000;
      label25: {
         if (type != null) {
            if (this.filterFamily) {
               if (type.isOrHasParent(this.type)) {
                  break label25;
               }
            } else if (this.type == type) {
               break label25;
            }
         }

         var10000 = false;
         return var10000;
      }

      var10000 = true;
      return var10000;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MetaFilter that = (MetaFilter)o;
         if (this.index != that.index) {
            return false;
         } else if (this.filterFamily != that.filterFamily) {
            return false;
         } else {
            return !this.handler.equals(that.handler) ? false : Objects.equals(this.type, that.type);
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.handler.hashCode();
      result = 31 * result + (this.type != null ? this.type.hashCode() : 0);
      result = 31 * result + this.index;
      result = 31 * result + (this.filterFamily ? 1 : 0);
      return result;
   }

   public String toString() {
      return "MetaFilter{type=" + this.type + ", filterFamily=" + this.filterFamily + ", index=" + this.index + ", handler=" + this.handler + '}';
   }

   public static final class Builder {
      private final EntityRewriter<?, ?> rewriter;
      private EntityType type;
      private int index = -1;
      private boolean filterFamily;
      private MetaHandler handler;

      public Builder(EntityRewriter<?, ?> rewriter) {
         this.rewriter = rewriter;
      }

      public MetaFilter.Builder type(EntityType type) {
         Preconditions.checkArgument(this.type == null);
         this.type = type;
         return this;
      }

      public MetaFilter.Builder index(int index) {
         Preconditions.checkArgument(this.index == -1);
         this.index = index;
         return this;
      }

      public MetaFilter.Builder filterFamily(EntityType type) {
         Preconditions.checkArgument(this.type == null);
         this.type = type;
         this.filterFamily = true;
         return this;
      }

      public MetaFilter.Builder handlerNoRegister(MetaHandler handler) {
         Preconditions.checkArgument(this.handler == null);
         this.handler = handler;
         return this;
      }

      public void handler(MetaHandler handler) {
         Preconditions.checkArgument(this.handler == null);
         this.handler = handler;
         this.register();
      }

      public void cancel(int index) {
         this.index = index;
         this.handler((event, meta) -> {
            event.cancel();
         });
      }

      public void toIndex(int newIndex) {
         Preconditions.checkArgument(this.index != -1);
         this.handler((event, meta) -> {
            event.setIndex(newIndex);
         });
      }

      public void addIndex(int index) {
         Preconditions.checkArgument(this.index == -1);
         this.handler((event, meta) -> {
            if (event.index() >= index) {
               event.setIndex(event.index() + 1);
            }

         });
      }

      public void removeIndex(int index) {
         Preconditions.checkArgument(this.index == -1);
         this.handler((event, meta) -> {
            int metaIndex = event.index();
            if (metaIndex == index) {
               event.cancel();
            } else if (metaIndex > index) {
               event.setIndex(metaIndex - 1);
            }

         });
      }

      public void register() {
         this.rewriter.registerFilter(this.build());
      }

      public MetaFilter build() {
         return new MetaFilter(this.type, this.filterFamily, this.index, this.handler);
      }
   }
}
