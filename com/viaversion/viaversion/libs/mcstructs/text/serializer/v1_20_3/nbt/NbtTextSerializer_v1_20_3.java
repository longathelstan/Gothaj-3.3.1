package com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_20_3.nbt;

import com.viaversion.viaversion.libs.mcstructs.core.Identifier;
import com.viaversion.viaversion.libs.mcstructs.snbt.SNbtSerializer;
import com.viaversion.viaversion.libs.mcstructs.text.ATextComponent;
import com.viaversion.viaversion.libs.mcstructs.text.Style;
import com.viaversion.viaversion.libs.mcstructs.text.components.KeybindComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.NbtComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.ScoreComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.SelectorComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.StringComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.TranslationComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.nbt.BlockNbtComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.nbt.EntityNbtComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.nbt.StorageNbtComponent;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.ITypedSerializer;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.TextComponentCodec;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.DoubleTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.FloatTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongArrayTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NbtTextSerializer_v1_20_3 implements ITypedSerializer<Tag, ATextComponent> {
   private final ITypedSerializer<Tag, Style> styleSerializer;

   public NbtTextSerializer_v1_20_3(TextComponentCodec codec, SNbtSerializer<CompoundTag> sNbtSerializer) {
      this.styleSerializer = new NbtStyleSerializer_v1_20_3(codec, this, sNbtSerializer);
   }

   public Tag serialize(ATextComponent object) {
      CompoundTag out = new CompoundTag();
      ArrayList siblings;
      if (object instanceof StringComponent) {
         StringComponent component = (StringComponent)object;
         if (component.getSiblings().isEmpty() && component.getStyle().isEmpty()) {
            return new StringTag(component.getText());
         }

         out.putString("text", component.getText());
      } else if (object instanceof TranslationComponent) {
         TranslationComponent component = (TranslationComponent)object;
         out.putString("translate", component.getKey());
         if (component.getFallback() != null) {
            out.putString("fallback", component.getFallback());
         }

         if (component.getArgs().length > 0) {
            siblings = new ArrayList();
            Object[] var5 = component.getArgs();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               Object arg = var5[var7];
               siblings.add(this.convert(arg));
            }

            out.put("with", this.optimizeAndConvert(siblings));
         }
      } else if (object instanceof KeybindComponent) {
         KeybindComponent component = (KeybindComponent)object;
         out.putString("keybind", component.getKeybind());
      } else if (object instanceof ScoreComponent) {
         ScoreComponent component = (ScoreComponent)object;
         CompoundTag score = new CompoundTag();
         score.putString("name", component.getName());
         score.putString("objective", component.getObjective());
         out.put("score", score);
      } else if (object instanceof SelectorComponent) {
         SelectorComponent component = (SelectorComponent)object;
         out.putString("selector", component.getSelector());
         if (component.getSeparator() != null) {
            out.put("separator", this.serialize(component.getSeparator()));
         }
      } else {
         if (!(object instanceof NbtComponent)) {
            throw new IllegalArgumentException("Unknown component type: " + object.getClass().getName());
         }

         NbtComponent component = (NbtComponent)object;
         out.putString("nbt", component.getComponent());
         if (component.isResolve()) {
            out.putByte("interpret", (byte)1);
         }

         if (component instanceof EntityNbtComponent) {
            EntityNbtComponent entityComponent = (EntityNbtComponent)component;
            out.putString("entity", entityComponent.getSelector());
         } else if (component instanceof BlockNbtComponent) {
            BlockNbtComponent blockNbtComponent = (BlockNbtComponent)component;
            out.putString("block", blockNbtComponent.getPos());
         } else {
            if (!(component instanceof StorageNbtComponent)) {
               throw new IllegalArgumentException("Unknown Nbt component type: " + component.getClass().getName());
            }

            StorageNbtComponent storageNbtComponent = (StorageNbtComponent)component;
            out.putString("storage", storageNbtComponent.getId().get());
         }
      }

      CompoundTag style = (CompoundTag)this.styleSerializer.serialize(object.getStyle());
      if (!style.isEmpty()) {
         out.putAll(style);
      }

      if (!object.getSiblings().isEmpty()) {
         siblings = new ArrayList();
         Iterator var18 = object.getSiblings().iterator();

         while(var18.hasNext()) {
            ATextComponent sibling = (ATextComponent)var18.next();
            siblings.add(this.serialize(sibling));
         }

         out.put("extra", this.optimizeAndConvert(siblings));
      }

      return out;
   }

   private Tag convert(Object object) {
      if (object instanceof Boolean) {
         return new ByteTag((byte)((Boolean)object ? 1 : 0));
      } else if (object instanceof Byte) {
         return new ByteTag((Byte)object);
      } else if (object instanceof Short) {
         return new ShortTag((Short)object);
      } else if (object instanceof Integer) {
         return new IntTag((Integer)object);
      } else if (object instanceof Long) {
         return new LongTag((Long)object);
      } else if (object instanceof Float) {
         return new FloatTag((Float)object);
      } else if (object instanceof Double) {
         return new DoubleTag((Double)object);
      } else if (object instanceof String) {
         return new StringTag((String)object);
      } else if (object instanceof ATextComponent) {
         return this.serialize((ATextComponent)object);
      } else {
         throw new IllegalArgumentException("Unknown object type: " + object.getClass().getName());
      }
   }

   private Tag optimizeAndConvert(List<Tag> tags) {
      Tag commonType = this.getCommonType(tags);
      ListTag out;
      Iterator var4;
      Tag tag;
      if (commonType == null) {
         out = new ListTag();
         var4 = tags.iterator();

         while(var4.hasNext()) {
            tag = (Tag)var4.next();
            if (tag instanceof CompoundTag) {
               out.add((CompoundTag)tag);
            } else {
               CompoundTag marker = new CompoundTag();
               marker.put("", tag);
               out.add(marker);
            }
         }

         return out;
      } else {
         int i;
         if (commonType instanceof ByteTag) {
            byte[] bytes = new byte[tags.size()];

            for(i = 0; i < tags.size(); ++i) {
               bytes[i] = ((ByteTag)tags.get(i)).getValue();
            }

            return new ByteArrayTag(bytes);
         } else if (commonType instanceof IntTag) {
            int[] ints = new int[tags.size()];

            for(i = 0; i < tags.size(); ++i) {
               ints[i] = ((IntTag)tags.get(i)).getValue();
            }

            return new IntArrayTag(ints);
         } else if (commonType instanceof LongTag) {
            long[] longs = new long[tags.size()];

            for(i = 0; i < tags.size(); ++i) {
               longs[i] = ((LongTag)tags.get(i)).getValue();
            }

            return new LongArrayTag(longs);
         } else {
            out = new ListTag();
            var4 = tags.iterator();

            while(var4.hasNext()) {
               tag = (Tag)var4.next();
               out.add(tag);
            }

            return out;
         }
      }
   }

   private Tag getCommonType(List<Tag> tags) {
      if (tags.size() == 1) {
         return (Tag)tags.get(0);
      } else {
         Tag type = (Tag)tags.get(0);

         for(int i = 1; i < tags.size(); ++i) {
            if (type.getClass() != ((Tag)tags.get(i)).getClass()) {
               return null;
            }
         }

         return type;
      }
   }

   public ATextComponent deserialize(Tag object) {
      if (object instanceof StringTag) {
         return new StringComponent(((StringTag)object).getValue());
      } else if (object instanceof ListTag) {
         if (((ListTag)object).isEmpty()) {
            throw new IllegalArgumentException("Empty list tag");
         } else {
            ListTag listTag = (ListTag)object;
            ATextComponent[] components = new ATextComponent[listTag.size()];

            for(int i = 0; i < listTag.size(); ++i) {
               components[i] = this.deserialize(listTag.get(i));
            }

            if (components.length == 1) {
               return components[0];
            } else {
               ATextComponent parent = components[0];

               for(int i = 1; i < components.length; ++i) {
                  parent.append(components[i]);
               }

               return parent;
            }
         }
      } else if (!(object instanceof CompoundTag)) {
         throw new IllegalArgumentException("Unknown component type: " + object.getClass());
      } else {
         ATextComponent component = null;
         CompoundTag tag = (CompoundTag)object;
         String type = tag.get("type") instanceof StringTag ? ((StringTag)tag.get("type")).getValue() : null;
         int i;
         if (!(tag.get("text") instanceof StringTag) || type != null && !type.equals("text")) {
            String fallback;
            String nbt;
            if (tag.get("translate") instanceof StringTag && (type == null || type.equals("translatable"))) {
               nbt = tag.get("translate") instanceof StringTag ? ((StringTag)tag.get("translate")).getValue() : "";
               fallback = tag.get("fallback") instanceof StringTag ? ((StringTag)tag.get("fallback")).getValue() : null;
               if (tag.contains("with")) {
                  List<Tag> with = this.unwrapMarkers(this.getArrayOrList(tag, "with"));
                  Object[] args = new Object[with.size()];

                  for(i = 0; i < with.size(); ++i) {
                     Tag arg = (Tag)with.get(i);
                     if (arg instanceof NumberTag) {
                        args[i] = ((NumberTag)arg).getValue();
                     } else if (arg instanceof StringTag) {
                        args[i] = ((StringTag)arg).getValue();
                     } else {
                        args[i] = this.deserialize(arg);
                     }
                  }

                  component = (new TranslationComponent(nbt, args)).setFallback(fallback);
               } else {
                  component = (new TranslationComponent(nbt, new Object[0])).setFallback(fallback);
               }
            } else if (!(tag.get("keybind") instanceof StringTag) || type != null && !type.equals("keybind")) {
               if (!(tag.get("score") instanceof CompoundTag) || !((tag.get("score") instanceof CompoundTag ? (CompoundTag)tag.get("score") : new CompoundTag()).get("name") instanceof StringTag) || !((tag.get("score") instanceof CompoundTag ? (CompoundTag)tag.get("score") : new CompoundTag()).get("objective") instanceof StringTag) || type != null && !type.equals("score")) {
                  if (!(tag.get("selector") instanceof StringTag) || type != null && !type.equals("selector")) {
                     if (!(tag.get("nbt") instanceof StringTag) || type != null && !type.equals("nbt")) {
                        throw new IllegalArgumentException("Unknown component type: " + tag.getClass());
                     }

                     nbt = tag.get("nbt") instanceof StringTag ? ((StringTag)tag.get("nbt")).getValue() : "";
                     boolean interpret = tag.get("interpret") instanceof ByteTag ? ((ByteTag)tag.get("interpret")).asBoolean() : false;
                     ATextComponent separator = null;
                     if (tag.contains("separator")) {
                        try {
                           separator = this.deserialize(tag.get("separator"));
                        } catch (Throwable var12) {
                        }
                     }

                     String source = tag.get("source") instanceof StringTag ? ((StringTag)tag.get("source")).getValue() : null;
                     boolean typeFound = false;
                     if (!(tag.get("entity") instanceof StringTag) || source != null && !source.equals("entity")) {
                        if (tag.get("block") instanceof StringTag && (source == null || source.equals("block"))) {
                           component = new BlockNbtComponent(nbt, interpret, separator, tag.get("block") instanceof StringTag ? ((StringTag)tag.get("block")).getValue() : "");
                           typeFound = true;
                        } else if (tag.get("storage") instanceof StringTag && (source == null || source.equals("storage"))) {
                           try {
                              component = new StorageNbtComponent(nbt, interpret, separator, Identifier.of(tag.get("storage") instanceof StringTag ? ((StringTag)tag.get("storage")).getValue() : ""));
                              typeFound = true;
                           } catch (Throwable var11) {
                           }
                        }
                     } else {
                        component = new EntityNbtComponent(nbt, interpret, separator, tag.get("entity") instanceof StringTag ? ((StringTag)tag.get("entity")).getValue() : "");
                        typeFound = true;
                     }

                     if (!typeFound) {
                        throw new IllegalArgumentException("Unknown Nbt component type: " + tag.getClass());
                     }
                  } else {
                     nbt = tag.get("selector") instanceof StringTag ? ((StringTag)tag.get("selector")).getValue() : "";
                     ATextComponent separator = null;
                     if (tag.contains("separator")) {
                        separator = this.deserialize(tag.get("separator"));
                     }

                     component = new SelectorComponent(nbt, separator);
                  }
               } else {
                  CompoundTag score = tag.get("score") instanceof CompoundTag ? (CompoundTag)tag.get("score") : new CompoundTag();
                  fallback = score.get("name") instanceof StringTag ? ((StringTag)score.get("name")).getValue() : "";
                  String objective = score.get("objective") instanceof StringTag ? ((StringTag)score.get("objective")).getValue() : "";
                  component = new ScoreComponent(fallback, objective);
               }
            } else {
               component = new KeybindComponent(tag.get("keybind") instanceof StringTag ? ((StringTag)tag.get("keybind")).getValue() : "");
            }
         } else {
            component = new StringComponent(tag.get("text") instanceof StringTag ? ((StringTag)tag.get("text")).getValue() : "");
         }

         Style style = (Style)this.styleSerializer.deserialize(tag);
         if (!style.isEmpty()) {
            ((ATextComponent)component).setStyle(style);
         }

         if (tag.contains("extra")) {
            if (!(tag.get("extra") instanceof ListTag)) {
               throw new IllegalArgumentException("Expected list tag for 'extra' tag");
            }

            ListTag extraTag = tag.get("extra") instanceof ListTag ? (ListTag)tag.get("extra") : new ListTag();
            if (extraTag.isEmpty()) {
               throw new IllegalArgumentException("Empty extra list tag");
            }

            List<Tag> unwrapped = this.unwrapMarkers(extraTag);
            ATextComponent[] extra = new ATextComponent[unwrapped.size()];

            for(i = 0; i < unwrapped.size(); ++i) {
               extra[i] = this.deserialize((Tag)unwrapped.get(i));
            }

            ((ATextComponent)component).append(extra);
         }

         return (ATextComponent)component;
      }
   }

   private ListTag getArrayOrList(CompoundTag tag, String key) {
      if (tag.get(key) instanceof ListTag) {
         return tag.get(key) instanceof ListTag ? (ListTag)tag.get(key) : new ListTag();
      } else if (tag.get(key) instanceof ByteArrayTag) {
         return ((ByteArrayTag)tag.get(key)).toListTag();
      } else if (tag.get(key) instanceof IntArrayTag) {
         return ((IntArrayTag)tag.get(key)).toListTag();
      } else if (tag.get(key) instanceof LongArrayTag) {
         return ((LongArrayTag)tag.get(key)).toListTag();
      } else {
         throw new IllegalArgumentException("Expected array or list tag for '" + key + "' tag");
      }
   }

   private List<Tag> unwrapMarkers(ListTag list) {
      List<Tag> out = new ArrayList();
      Iterator var3 = list.iterator();

      while(true) {
         while(true) {
            while(var3.hasNext()) {
               Tag tag = (Tag)var3.next();
               if (tag instanceof CompoundTag) {
                  CompoundTag compound = (CompoundTag)tag;
                  if (compound.size() == 1 && compound.contains("")) {
                     out.add(compound.get(""));
                  } else {
                     out.add(tag);
                  }
               } else {
                  out.add(tag);
               }
            }

            return out;
         }
      }
   }
}
