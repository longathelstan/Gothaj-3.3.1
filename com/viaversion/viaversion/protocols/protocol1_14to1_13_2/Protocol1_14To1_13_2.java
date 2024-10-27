package com.viaversion.viaversion.protocols.protocol1_14to1_13_2;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.ClientWorld;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.misc.ParticleType;
import com.viaversion.viaversion.api.type.types.version.Types1_13_2;
import com.viaversion.viaversion.api.type.types.version.Types1_14;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.data.ComponentRewriter1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.data.MappingData;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.metadata.MetadataRewriter1_14To1_13_2;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets.EntityPackets;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets.InventoryPackets;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets.PlayerPackets;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets.WorldPackets;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.storage.EntityTracker1_14;
import com.viaversion.viaversion.rewriter.CommandRewriter;
import com.viaversion.viaversion.rewriter.ComponentRewriter;
import com.viaversion.viaversion.rewriter.SoundRewriter;
import com.viaversion.viaversion.rewriter.StatisticsRewriter;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Protocol1_14To1_13_2 extends AbstractProtocol<ClientboundPackets1_13, ClientboundPackets1_14, ServerboundPackets1_13, ServerboundPackets1_14> {
   public static final MappingData MAPPINGS = new MappingData();
   private final MetadataRewriter1_14To1_13_2 metadataRewriter = new MetadataRewriter1_14To1_13_2(this);
   private final InventoryPackets itemRewriter = new InventoryPackets(this);

   public Protocol1_14To1_13_2() {
      super(ClientboundPackets1_13.class, ClientboundPackets1_14.class, ServerboundPackets1_13.class, ServerboundPackets1_14.class);
   }

   protected void registerPackets() {
      this.metadataRewriter.register();
      this.itemRewriter.register();
      EntityPackets.register(this);
      WorldPackets.register(this);
      PlayerPackets.register(this);
      (new SoundRewriter(this)).registerSound(ClientboundPackets1_13.SOUND);
      (new StatisticsRewriter(this)).register(ClientboundPackets1_13.STATISTICS);
      ComponentRewriter<ClientboundPackets1_13> componentRewriter = new ComponentRewriter1_14(this);
      componentRewriter.registerComponentPacket(ClientboundPackets1_13.CHAT_MESSAGE);
      CommandRewriter<ClientboundPackets1_13> commandRewriter = new CommandRewriter<ClientboundPackets1_13>(this) {
         @Nullable
         public String handleArgumentType(String argumentType) {
            return argumentType.equals("minecraft:nbt") ? "minecraft:nbt_compound_tag" : super.handleArgumentType(argumentType);
         }
      };
      commandRewriter.registerDeclareCommands(ClientboundPackets1_13.DECLARE_COMMANDS);
      this.registerClientbound(ClientboundPackets1_13.TAGS, (wrapper) -> {
         int blockTagsSize = (Integer)wrapper.read(Type.VAR_INT);
         wrapper.write(Type.VAR_INT, blockTagsSize + 6);

         int itemTagsSize;
         int i;
         for(itemTagsSize = 0; itemTagsSize < blockTagsSize; ++itemTagsSize) {
            wrapper.passthrough(Type.STRING);
            int[] blockIds = (int[])wrapper.passthrough(Type.VAR_INT_ARRAY_PRIMITIVE);

            for(i = 0; i < blockIds.length; ++i) {
               blockIds[i] = MAPPINGS.getNewBlockId(blockIds[i]);
            }
         }

         wrapper.write(Type.STRING, "minecraft:signs");
         wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[]{MAPPINGS.getNewBlockId(150), MAPPINGS.getNewBlockId(155)});
         wrapper.write(Type.STRING, "minecraft:wall_signs");
         wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[]{MAPPINGS.getNewBlockId(155)});
         wrapper.write(Type.STRING, "minecraft:standing_signs");
         wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[]{MAPPINGS.getNewBlockId(150)});
         wrapper.write(Type.STRING, "minecraft:fences");
         wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[]{189, 248, 472, 473, 474, 475});
         wrapper.write(Type.STRING, "minecraft:walls");
         wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[]{271, 272});
         wrapper.write(Type.STRING, "minecraft:wooden_fences");
         wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[]{189, 472, 473, 474, 475});
         itemTagsSize = (Integer)wrapper.read(Type.VAR_INT);
         wrapper.write(Type.VAR_INT, itemTagsSize + 2);

         int fluidTagsSize;
         for(fluidTagsSize = 0; fluidTagsSize < itemTagsSize; ++fluidTagsSize) {
            wrapper.passthrough(Type.STRING);
            int[] itemIds = (int[])wrapper.passthrough(Type.VAR_INT_ARRAY_PRIMITIVE);

            for(int j = 0; j < itemIds.length; ++j) {
               itemIds[j] = MAPPINGS.getNewItemId(itemIds[j]);
            }
         }

         wrapper.write(Type.STRING, "minecraft:signs");
         wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[]{MAPPINGS.getNewItemId(541)});
         wrapper.write(Type.STRING, "minecraft:arrows");
         wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[]{526, 825, 826});
         fluidTagsSize = (Integer)wrapper.passthrough(Type.VAR_INT);

         for(i = 0; i < fluidTagsSize; ++i) {
            wrapper.passthrough(Type.STRING);
            wrapper.passthrough(Type.VAR_INT_ARRAY_PRIMITIVE);
         }

         wrapper.write(Type.VAR_INT, 0);
      });
      this.cancelServerbound(ServerboundPackets1_14.SET_DIFFICULTY);
      this.cancelServerbound(ServerboundPackets1_14.LOCK_DIFFICULTY);
      this.cancelServerbound(ServerboundPackets1_14.UPDATE_JIGSAW_BLOCK);
   }

   protected void onMappingDataLoaded() {
      WorldPackets.air = MAPPINGS.getBlockStateMappings().getNewId(0);
      WorldPackets.voidAir = MAPPINGS.getBlockStateMappings().getNewId(8591);
      WorldPackets.caveAir = MAPPINGS.getBlockStateMappings().getNewId(8592);
      Types1_13_2.PARTICLE.filler(this, false).reader("block", ParticleType.Readers.BLOCK).reader("dust", ParticleType.Readers.DUST).reader("falling_dust", ParticleType.Readers.BLOCK).reader("item", ParticleType.Readers.ITEM1_13_2);
      Types1_14.PARTICLE.filler(this).reader("block", ParticleType.Readers.BLOCK).reader("dust", ParticleType.Readers.DUST).reader("falling_dust", ParticleType.Readers.BLOCK).reader("item", ParticleType.Readers.ITEM1_13_2);
   }

   public void init(UserConnection userConnection) {
      userConnection.addEntityTracker(this.getClass(), new EntityTracker1_14(userConnection));
      if (!userConnection.has(ClientWorld.class)) {
         userConnection.put(new ClientWorld());
      }

   }

   public MappingData getMappingData() {
      return MAPPINGS;
   }

   public MetadataRewriter1_14To1_13_2 getEntityRewriter() {
      return this.metadataRewriter;
   }

   public InventoryPackets getItemRewriter() {
      return this.itemRewriter;
   }
}
