package com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.ClientWorld;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_9_1;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_9_3;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.chunks.FakeTileEntity;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import java.util.List;

public class Protocol1_9_3To1_9_1_2 extends AbstractProtocol<ClientboundPackets1_9, ClientboundPackets1_9_3, ServerboundPackets1_9, ServerboundPackets1_9_3> {
   public static final ValueTransformer<Short, Short> ADJUST_PITCH;

   public Protocol1_9_3To1_9_1_2() {
      super(ClientboundPackets1_9.class, ClientboundPackets1_9_3.class, ServerboundPackets1_9.class, ServerboundPackets1_9_3.class);
   }

   protected void registerPackets() {
      this.registerClientbound(ClientboundPackets1_9.UPDATE_SIGN, (ClientboundPacketType)null, (wrapper) -> {
         Position position = (Position)wrapper.read(Type.POSITION1_8);
         JsonElement[] lines = new JsonElement[4];

         for(int i = 0; i < 4; ++i) {
            lines[i] = (JsonElement)wrapper.read(Type.COMPONENT);
         }

         wrapper.clearInputBuffer();
         wrapper.setPacketType(ClientboundPackets1_9_3.BLOCK_ENTITY_DATA);
         wrapper.write(Type.POSITION1_8, position);
         wrapper.write(Type.UNSIGNED_BYTE, Short.valueOf((short)9));
         CompoundTag tag = new CompoundTag();
         tag.put("id", new StringTag("Sign"));
         tag.put("x", new IntTag(position.x()));
         tag.put("y", new IntTag(position.y()));
         tag.put("z", new IntTag(position.z()));

         for(int ix = 0; ix < lines.length; ++ix) {
            tag.put("Text" + (ix + 1), new StringTag(lines[ix].toString()));
         }

         wrapper.write(Type.NAMED_COMPOUND_TAG, tag);
      });
      this.registerClientbound(ClientboundPackets1_9.CHUNK_DATA, (wrapper) -> {
         ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
         Chunk chunk = (Chunk)wrapper.read(ChunkType1_9_1.forEnvironment(clientWorld.getEnvironment()));
         wrapper.write(ChunkType1_9_3.forEnvironment(clientWorld.getEnvironment()), chunk);
         List<CompoundTag> tags = chunk.getBlockEntities();

         for(int s = 0; s < chunk.getSections().length; ++s) {
            ChunkSection section = chunk.getSections()[s];
            if (section != null) {
               DataPalette blocks = section.palette(PaletteType.BLOCKS);

               for(int idx = 0; idx < 4096; ++idx) {
                  int id = blocks.idAt(idx) >> 4;
                  if (FakeTileEntity.isTileEntity(id)) {
                     tags.add(FakeTileEntity.createTileEntity(ChunkSection.xFromIndex(idx) + (chunk.getX() << 4), ChunkSection.yFromIndex(idx) + (s << 4), ChunkSection.zFromIndex(idx) + (chunk.getZ() << 4), id));
                  }
               }
            }
         }

      });
      this.registerClientbound(ClientboundPackets1_9.JOIN_GAME, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.map(Type.UNSIGNED_BYTE);
            this.map(Type.INT);
            this.handler((wrapper) -> {
               ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
               int dimensionId = (Integer)wrapper.get(Type.INT, 1);
               clientWorld.setEnvironment(dimensionId);
            });
         }
      });
      this.registerClientbound(ClientboundPackets1_9.RESPAWN, new PacketHandlers() {
         public void register() {
            this.map(Type.INT);
            this.handler((wrapper) -> {
               ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
               int dimensionId = (Integer)wrapper.get(Type.INT, 0);
               clientWorld.setEnvironment(dimensionId);
            });
         }
      });
      this.registerClientbound(ClientboundPackets1_9.SOUND, new PacketHandlers() {
         public void register() {
            this.map(Type.VAR_INT);
            this.map(Type.VAR_INT);
            this.map(Type.INT);
            this.map(Type.INT);
            this.map(Type.INT);
            this.map(Type.FLOAT);
            this.map(Protocol1_9_3To1_9_1_2.ADJUST_PITCH);
         }
      });
   }

   public void init(UserConnection user) {
      if (!user.has(ClientWorld.class)) {
         user.put(new ClientWorld(user));
      }

   }

   static {
      ADJUST_PITCH = new ValueTransformer<Short, Short>(Type.UNSIGNED_BYTE, Type.UNSIGNED_BYTE) {
         public Short transform(PacketWrapper wrapper, Short inputValue) throws Exception {
            return (short)Math.round((float)inputValue / 63.5F * 63.0F);
         }
      };
   }
}
