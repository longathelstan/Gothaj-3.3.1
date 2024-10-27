package org.yaml.snakeyaml.util;

public class EnumUtils {
   public static <T extends Enum<T>> T findEnumInsensitiveCase(Class<T> enumType, String name) {
      Enum[] var2 = (Enum[])enumType.getEnumConstants();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         T constant = var2[var4];
         if (constant.name().compareToIgnoreCase(name) == 0) {
            return constant;
         }
      }

      throw new IllegalArgumentException("No enum constant " + enumType.getCanonicalName() + "." + name);
   }
}
