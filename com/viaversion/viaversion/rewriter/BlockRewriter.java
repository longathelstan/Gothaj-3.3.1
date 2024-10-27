package com.viaversion.viaversion.rewriter;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.data.Mappings;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.blockentity.BlockEntity;
import com.viaversion.viaversion.api.minecraft.blockentity.BlockEntityImpl;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.util.MathUtil;
import java.util.List;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BlockRewriter<C extends ClientboundPacketType> {
   private final Protocol<C, ?, ?, ?> protocol;
   private final Type<Position> positionType;
   private final Type<CompoundTag> compoundTagType;

   /** @deprecated */
   @Deprecated
   public BlockRewriter(Protocol<C, ?, ?, ?> protocol, Type<Position> positionType) {
      this(protocol, positionType, Type.NAMED_COMPOUND_TAG);
   }

   public BlockRewriter(Protocol<C, ?, ?, ?> protocol, Type<Position> positionType, Type<CompoundTag> compoundTagType) {
      this.protocol = protocol;
      this.positionType = positionType;
      this.compoundTagType = compoundTagType;
   }

   public static <C extends ClientboundPacketType> BlockRewriter<C> legacy(Protocol<C, ?, ?, ?> protocol) {
      return new BlockRewriter(protocol, Type.POSITION1_8, Type.NAMED_COMPOUND_TAG);
   }

   public static <C extends ClientboundPacketType> BlockRewriter<C> for1_14(Protocol<C, ?, ?, ?> protocol) {
      return new BlockRewriter(protocol, Type.POSITION1_14, Type.NAMED_COMPOUND_TAG);
   }

   public static <C extends ClientboundPacketType> BlockRewriter<C> for1_20_2(Protocol<C, ?, ?, ?> protocol) {
      return new BlockRewriter(protocol, Type.POSITION1_14, Type.COMPOUND_TAG);
   }

   public void registerBlockAction(C packetType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(BlockRewriter.this.positionType);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               if (BlockRewriter.this.protocol.getMappingData().getBlockMappings() != null) {
                  int id = (Integer)wrapper.get(Type.VAR_INT, 0);
                  int mappedId = BlockRewriter.this.protocol.getMappingData().getNewBlockId(id);
                  if (mappedId == -1) {
                     wrapper.cancel();
                  } else {
                     wrapper.set(Type.VAR_INT, 0, mappedId);
                  }
               }
            });
         }
      }));
   }

   public void registerBlockChange(C packetType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(BlockRewriter.this.positionType);
            this.map(Type.VAR_INT);
            this.handler((wrapper) -> {
               wrapper.set(Type.VAR_INT, 0, BlockRewriter.this.protocol.getMappingData().getNewBlockStateId((Integer)wrapper.get(Type.VAR_INT, 0)));
            });
         }
      }));
   }

   public void registerMultiBlockChange(C packetType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.INT);
            this.handler((wrapper) -> {
               BlockChangeRecord[] var2 = (BlockChangeRecord[])wrapper.passthrough(Type.BLOCK_CHANGE_RECORD_ARRAY);
               int var3 = var2.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  BlockChangeRecord record = var2[var4];
                  record.setBlockId(BlockRewriter.this.protocol.getMappingData().getNewBlockStateId(record.getBlockId()));
               }

            });
         }
      }));
   }

   public void registerVarLongMultiBlockChange(C packetType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.LONG);
            this.map(Type.BOOLEAN);
            this.handler((wrapper) -> {
               BlockChangeRecord[] var2 = (BlockChangeRecord[])wrapper.passthrough(Type.VAR_LONG_BLOCK_CHANGE_RECORD_ARRAY);
               int var3 = var2.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  BlockChangeRecord record = var2[var4];
                  record.setBlockId(BlockRewriter.this.protocol.getMappingData().getNewBlockStateId(record.getBlockId()));
               }

            });
         }
      }));
   }

   public void registerVarLongMultiBlockChange1_20(C packetType) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.LONG);
            this.handler((wrapper) -> {
               BlockChangeRecord[] var2 = (BlockChangeRecord[])wrapper.passthrough(Type.VAR_LONG_BLOCK_CHANGE_RECORD_ARRAY);
               int var3 = var2.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  BlockChangeRecord record = var2[var4];
                  record.setBlockId(BlockRewriter.this.protocol.getMappingData().getNewBlockStateId(record.getBlockId()));
               }

            });
         }
      }));
   }

   public void registerAcknowledgePlayerDigging(C packetType) {
      this.registerBlockChange(packetType);
   }

   public void registerEffect(C packetType, final int playRecordId, final int blockBreakId) {
      this.protocol.registerClientbound(packetType, (PacketHandler)(new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(BlockRewriter.this.positionType);
            this.map(Type.INT);
            this.handler((wrapper) -> {
               int id = (Integer)wrapper.get(Type.INT, 0);
               int data = (Integer)wrapper.get(Type.INT, 1);
               if (id == playRecordId && BlockRewriter.this.protocol.getMappingData().getItemMappings() != null) {
                  wrapper.set(Type.INT, 1, BlockRewriter.this.protocol.getMappingData().getNewItemId(data));
               } else if (id == blockBreakId && BlockRewriter.this.protocol.getMappingData().getBlockStateMappings() != null) {
                  wrapper.set(Type.INT, 1, BlockRewriter.this.protocol.getMappingData().getNewBlockStateId(data));
               }

            });
         }
      }));
   }

   public void registerChunkData1_19(C packetType, BlockRewriter.ChunkTypeSupplier chunkTypeSupplier) {
      this.registerChunkData1_19(packetType, chunkTypeSupplier, (Consumer)null);
   }

   public void registerChunkData1_19(C packetType, BlockRewriter.ChunkTypeSupplier chunkTypeSupplier, @Nullable Consumer<BlockEntity> blockEntityHandler) {
      this.protocol.registerClientbound(packetType, this.chunkDataHandler1_19(chunkTypeSupplier, blockEntityHandler));
   }

   public PacketHandler chunkDataHandler1_19(BlockRewriter.ChunkTypeSupplier chunkTypeSupplier, @Nullable Consumer<BlockEntity> blockEntityHandler) {
      return (wrapper) -> {
         EntityTracker tracker = this.protocol.getEntityRewriter().tracker(wrapper.user());
         Preconditions.checkArgument(tracker.biomesSent() != -1, "Biome count not set");
         Preconditions.checkArgument(tracker.currentWorldSectionHeight() != -1, "Section height not set");
         Type<Chunk> chunkType = chunkTypeSupplier.supply(tracker.currentWorldSectionHeight(), MathUtil.ceilLog2(this.protocol.getMappingData().getBlockStateMappings().mappedSize()), MathUtil.ceilLog2(tracker.biomesSent()));
         Chunk chunk = (Chunk)wrapper.passthrough(chunkType);
         ChunkSection[] var7 = chunk.getSections();
         int var8 = var7.length;

         int ix;
         for(ix = 0; ix < var8; ++ix) {
            ChunkSection section = var7[ix];
            DataPalette blockPalette = section.palette(PaletteType.BLOCKS);

            for(int i = 0; i < blockPalette.size(); ++i) {
               int id = blockPalette.idByIndex(i);
               blockPalette.setIdByIndex(i, this.protocol.getMappingData().getNewBlockStateId(id));
            }
         }

         Mappings blockEntityMappings = this.protocol.getMappingData().getBlockEntityMappings();
         if (blockEntityMappings != null || blockEntityHandler != null) {
            List<BlockEntity> blockEntities = chunk.blockEntities();

            for(ix = 0; ix < blockEntities.size(); ++ix) {
               BlockEntity blockEntity = (BlockEntity)blockEntities.get(ix);
               if (blockEntityMappings != null) {
                  blockEntities.set(ix, blockEntity.withTypeId(blockEntityMappings.getNewIdOrDefault(blockEntity.typeId(), blockEntity.typeId())));
               }

               if (blockEntityHandler != null && blockEntity.tag() != null) {
                  blockEntityHandler.accept(blockEntity);
               }
            }
         }

      };
   }

   public void registerBlockEntityData(C packetType) {
      this.registerBlockEntityData(packetType, (Consumer)null);
   }

   public void registerBlockEntityData(C packetType, @Nullable Consumer<BlockEntity> blockEntityHandler) {
      this.protocol.registerClientbound(packetType, (wrapper) -> {
         Position position = (Position)wrapper.passthrough(this.positionType);
         int blockEntityId = (Integer)wrapper.read(Type.VAR_INT);
         Mappings mappings = this.protocol.getMappingData().getBlockEntityMappings();
         if (mappings != null) {
            wrapper.write(Type.VAR_INT, mappings.getNewIdOrDefault(blockEntityId, blockEntityId));
         } else {
            wrapper.write(Type.VAR_INT, blockEntityId);
         }

         CompoundTag tag;
         if (blockEntityHandler != null && (tag = (CompoundTag)wrapper.passthrough(this.compoundTagType)) != null) {
            BlockEntity blockEntity = new BlockEntityImpl(BlockEntity.pack(position.x(), position.z()), (short)position.y(), blockEntityId, tag);
            blockEntityHandler.accept(blockEntity);
         }

      });
   }

   @FunctionalInterface
   public interface ChunkTypeSupplier {
      Type<Chunk> supply(int var1, int var2, int var3);
   }
}
