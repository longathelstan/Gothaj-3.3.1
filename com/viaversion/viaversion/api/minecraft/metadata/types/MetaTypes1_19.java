package com.viaversion.viaversion.api.minecraft.metadata.types;

import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.misc.ParticleType;

public final class MetaTypes1_19 extends AbstractMetaTypes {
   public final MetaType byteType;
   public final MetaType varIntType;
   public final MetaType floatType;
   public final MetaType stringType;
   public final MetaType componentType;
   public final MetaType optionalComponentType;
   public final MetaType itemType;
   public final MetaType booleanType;
   public final MetaType rotationType;
   public final MetaType positionType;
   public final MetaType optionalPositionType;
   public final MetaType directionType;
   public final MetaType optionalUUIDType;
   public final MetaType blockStateType;
   public final MetaType nbtType;
   public final MetaType particleType;
   public final MetaType villagerDatatType;
   public final MetaType optionalVarIntType;
   public final MetaType poseType;
   public final MetaType catVariantType;
   public final MetaType frogVariantType;
   public final MetaType optionalGlobalPosition;
   public final MetaType paintingVariantType;

   public MetaTypes1_19(ParticleType particleType) {
      super(23);
      this.byteType = this.add(0, Type.BYTE);
      this.varIntType = this.add(1, Type.VAR_INT);
      this.floatType = this.add(2, Type.FLOAT);
      this.stringType = this.add(3, Type.STRING);
      this.componentType = this.add(4, Type.COMPONENT);
      this.optionalComponentType = this.add(5, Type.OPTIONAL_COMPONENT);
      this.itemType = this.add(6, Type.ITEM1_13_2);
      this.booleanType = this.add(7, Type.BOOLEAN);
      this.rotationType = this.add(8, Type.ROTATION);
      this.positionType = this.add(9, Type.POSITION1_14);
      this.optionalPositionType = this.add(10, Type.OPTIONAL_POSITION_1_14);
      this.directionType = this.add(11, Type.VAR_INT);
      this.optionalUUIDType = this.add(12, Type.OPTIONAL_UUID);
      this.blockStateType = this.add(13, Type.VAR_INT);
      this.nbtType = this.add(14, Type.NAMED_COMPOUND_TAG);
      this.villagerDatatType = this.add(16, Type.VILLAGER_DATA);
      this.optionalVarIntType = this.add(17, Type.OPTIONAL_VAR_INT);
      this.poseType = this.add(18, Type.VAR_INT);
      this.catVariantType = this.add(19, Type.VAR_INT);
      this.frogVariantType = this.add(20, Type.VAR_INT);
      this.optionalGlobalPosition = this.add(21, Type.OPTIONAL_GLOBAL_POSITION);
      this.paintingVariantType = this.add(22, Type.VAR_INT);
      this.particleType = this.add(15, particleType);
   }
}
