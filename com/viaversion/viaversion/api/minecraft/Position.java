package com.viaversion.viaversion.api.minecraft;

public class Position {
   protected final int x;
   protected final int y;
   protected final int z;

   public Position(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Position(int x, short y, int z) {
      this(x, (int)y, z);
   }

   /** @deprecated */
   @Deprecated
   public Position(Position toCopy) {
      this(toCopy.x(), toCopy.y(), toCopy.z());
   }

   public Position getRelative(BlockFace face) {
      return new Position(this.x + face.modX(), (short)(this.y + face.modY()), this.z + face.modZ());
   }

   public int x() {
      return this.x;
   }

   public int y() {
      return this.y;
   }

   public int z() {
      return this.z;
   }

   public GlobalPosition withDimension(String dimension) {
      return new GlobalPosition(dimension, this.x, this.y, this.z);
   }

   /** @deprecated */
   @Deprecated
   public int getX() {
      return this.x;
   }

   /** @deprecated */
   @Deprecated
   public int getY() {
      return this.y;
   }

   /** @deprecated */
   @Deprecated
   public int getZ() {
      return this.z;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Position position = (Position)o;
         if (this.x != position.x) {
            return false;
         } else if (this.y != position.y) {
            return false;
         } else {
            return this.z == position.z;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.x;
      result = 31 * result + this.y;
      result = 31 * result + this.z;
      return result;
   }

   public String toString() {
      return "Position{x=" + this.x + ", y=" + this.y + ", z=" + this.z + '}';
   }
}
