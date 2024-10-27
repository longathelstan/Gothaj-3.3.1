package com.viaversion.viaversion.protocols.protocol1_11to1_10.metadata;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.entities.EntityTypes1_11;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_9;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_11to1_10.EntityIdRewriter;
import com.viaversion.viaversion.protocols.protocol1_11to1_10.Protocol1_11To1_10;
import com.viaversion.viaversion.protocols.protocol1_11to1_10.storage.EntityTracker1_11;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class MetadataRewriter1_11To1_10 extends EntityRewriter<ClientboundPackets1_9_3, Protocol1_11To1_10> {
   public MetadataRewriter1_11To1_10(Protocol1_11To1_10 protocol) {
      super(protocol);
   }

   protected void handleMetadata(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) {
      if (metadata.getValue() instanceof DataItem) {
         EntityIdRewriter.toClientItem((Item)metadata.getValue());
      }

      if (type != null) {
         int oldid;
         if (type.is((EntityType)EntityTypes1_11.EntityType.ELDER_GUARDIAN) || type.is((EntityType)EntityTypes1_11.EntityType.GUARDIAN)) {
            oldid = metadata.id();
            if (oldid == 12) {
               boolean val = ((Byte)metadata.getValue() & 2) == 2;
               metadata.setTypeAndValue(MetaType1_9.Boolean, val);
            }
         }

         if (type.isOrHasParent(EntityTypes1_11.EntityType.ABSTRACT_SKELETON)) {
            oldid = metadata.id();
            if (oldid == 12) {
               metadatas.remove(metadata);
            }

            if (oldid == 13) {
               metadata.setId(12);
            }
         }

         if (type.isOrHasParent(EntityTypes1_11.EntityType.ZOMBIE)) {
            if ((type == EntityTypes1_11.EntityType.ZOMBIE || type == EntityTypes1_11.EntityType.HUSK) && metadata.id() == 14) {
               metadatas.remove(metadata);
            } else if (metadata.id() == 15) {
               metadata.setId(14);
            } else if (metadata.id() == 14) {
               metadata.setId(15);
            }
         }

         if (type.isOrHasParent(EntityTypes1_11.EntityType.ABSTRACT_HORSE)) {
            oldid = metadata.id();
            if (oldid == 14) {
               metadatas.remove(metadata);
            }

            if (oldid == 16) {
               metadata.setId(14);
            }

            if (oldid == 17) {
               metadata.setId(16);
            }

            if (!type.is((EntityType)EntityTypes1_11.EntityType.HORSE) && (metadata.id() == 15 || metadata.id() == 16)) {
               metadatas.remove(metadata);
            }

            if ((type == EntityTypes1_11.EntityType.DONKEY || type == EntityTypes1_11.EntityType.MULE) && metadata.id() == 13) {
               if (((Byte)metadata.getValue() & 8) == 8) {
                  metadatas.add(new Metadata(15, MetaType1_9.Boolean, true));
               } else {
                  metadatas.add(new Metadata(15, MetaType1_9.Boolean, false));
               }
            }
         }

         if (type.is((EntityType)EntityTypes1_11.EntityType.ARMOR_STAND) && Via.getConfig().isHologramPatch()) {
            Metadata flags = this.metaByIndex(11, metadatas);
            Metadata customName = this.metaByIndex(2, metadatas);
            Metadata customNameVisible = this.metaByIndex(3, metadatas);
            if (metadata.id() == 0 && flags != null && customName != null && customNameVisible != null) {
               byte data = (Byte)metadata.getValue();
               if ((data & 32) == 32 && ((Byte)flags.getValue() & 1) == 1 && !((String)customName.getValue()).isEmpty() && (Boolean)customNameVisible.getValue()) {
                  EntityTracker1_11 tracker = (EntityTracker1_11)this.tracker(connection);
                  if (tracker.addHologram(entityId)) {
                     try {
                        PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_9_3.ENTITY_POSITION, (ByteBuf)null, connection);
                        wrapper.write(Type.VAR_INT, entityId);
                        wrapper.write(Type.SHORT, Short.valueOf((short)0));
                        wrapper.write(Type.SHORT, (short)((int)(128.0D * -Via.getConfig().getHologramYOffset() * 32.0D)));
                        wrapper.write(Type.SHORT, Short.valueOf((short)0));
                        wrapper.write(Type.BOOLEAN, true);
                        wrapper.send(Protocol1_11To1_10.class);
                     } catch (Exception var12) {
                        var12.printStackTrace();
                     }
                  }
               }
            }
         }

      }
   }

   public EntityType typeFromId(int type) {
      return EntityTypes1_11.getTypeFromId(type, false);
   }

   public EntityType objectTypeFromId(int type) {
      return EntityTypes1_11.getTypeFromId(type, true);
   }

   public static EntityTypes1_11.EntityType rewriteEntityType(int numType, List<Metadata> metadata) {
      Optional<EntityTypes1_11.EntityType> optType = EntityTypes1_11.EntityType.findById(numType);
      if (!optType.isPresent()) {
         Via.getManager().getPlatform().getLogger().severe("Error: could not find Entity type " + numType + " with metadata: " + metadata);
         return null;
      } else {
         EntityTypes1_11.EntityType type = (EntityTypes1_11.EntityType)optType.get();

         try {
            Optional options;
            if (type.is(EntityTypes1_11.EntityType.GUARDIAN)) {
               options = getById(metadata, 12);
               if (options.isPresent() && ((Byte)((Metadata)options.get()).getValue() & 4) == 4) {
                  return EntityTypes1_11.EntityType.ELDER_GUARDIAN;
               }
            }

            if (type.is(EntityTypes1_11.EntityType.SKELETON)) {
               options = getById(metadata, 12);
               if (options.isPresent()) {
                  if ((Integer)((Metadata)options.get()).getValue() == 1) {
                     return EntityTypes1_11.EntityType.WITHER_SKELETON;
                  }

                  if ((Integer)((Metadata)options.get()).getValue() == 2) {
                     return EntityTypes1_11.EntityType.STRAY;
                  }
               }
            }

            if (type.is(EntityTypes1_11.EntityType.ZOMBIE)) {
               options = getById(metadata, 13);
               if (options.isPresent()) {
                  int value = (Integer)((Metadata)options.get()).getValue();
                  if (value > 0 && value < 6) {
                     metadata.add(new Metadata(16, MetaType1_9.VarInt, value - 1));
                     return EntityTypes1_11.EntityType.ZOMBIE_VILLAGER;
                  }

                  if (value == 6) {
                     return EntityTypes1_11.EntityType.HUSK;
                  }
               }
            }

            if (type.is(EntityTypes1_11.EntityType.HORSE)) {
               options = getById(metadata, 14);
               if (options.isPresent()) {
                  if ((Integer)((Metadata)options.get()).getValue() == 0) {
                     return EntityTypes1_11.EntityType.HORSE;
                  }

                  if ((Integer)((Metadata)options.get()).getValue() == 1) {
                     return EntityTypes1_11.EntityType.DONKEY;
                  }

                  if ((Integer)((Metadata)options.get()).getValue() == 2) {
                     return EntityTypes1_11.EntityType.MULE;
                  }

                  if ((Integer)((Metadata)options.get()).getValue() == 3) {
                     return EntityTypes1_11.EntityType.ZOMBIE_HORSE;
                  }

                  if ((Integer)((Metadata)options.get()).getValue() == 4) {
                     return EntityTypes1_11.EntityType.SKELETON_HORSE;
                  }
               }
            }
         } catch (Exception var6) {
            if (!Via.getConfig().isSuppressMetadataErrors() || Via.getManager().isDebug()) {
               Via.getPlatform().getLogger().warning("An error occurred with entity type rewriter");
               Via.getPlatform().getLogger().warning("Metadata: " + metadata);
               Via.getPlatform().getLogger().log(Level.WARNING, "Error: ", var6);
            }
         }

         return type;
      }
   }

   public static Optional<Metadata> getById(List<Metadata> metadatas, int id) {
      Iterator var2 = metadatas.iterator();

      Metadata metadata;
      do {
         if (!var2.hasNext()) {
            return Optional.empty();
         }

         metadata = (Metadata)var2.next();
      } while(metadata.id() != id);

      return Optional.of(metadata);
   }
}
