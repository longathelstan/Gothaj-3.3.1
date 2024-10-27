package com.viaversion.viabackwards.api.rewriters;

import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LegacyEnchantmentRewriter {
   private final Map<Short, String> enchantmentMappings = new HashMap();
   private final String nbtTagName;
   private Set<Short> hideLevelForEnchants;

   public LegacyEnchantmentRewriter(String nbtTagName) {
      this.nbtTagName = nbtTagName;
   }

   public void registerEnchantment(int id, String replacementLore) {
      this.enchantmentMappings.put((short)id, replacementLore);
   }

   public void rewriteEnchantmentsToClient(CompoundTag tag, boolean storedEnchant) {
      String key = storedEnchant ? "StoredEnchantments" : "ench";
      ListTag enchantments = (ListTag)tag.get(key);
      ListTag remappedEnchantments = new ListTag(CompoundTag.class);
      List<Tag> lore = new ArrayList();
      Iterator var7 = enchantments.copy().iterator();

      while(true) {
         Tag enchantmentEntry;
         short newId;
         String enchantmentName;
         do {
            Tag idTag;
            do {
               if (!var7.hasNext()) {
                  if (!lore.isEmpty()) {
                     CompoundTag display;
                     if (!storedEnchant && enchantments.size() == 0) {
                        display = new CompoundTag();
                        display.put("id", new ShortTag((short)0));
                        display.put("lvl", new ShortTag((short)0));
                        enchantments.add(display);
                        tag.put(this.nbtTagName + "|dummyEnchant", new ByteTag());
                        IntTag hideFlags = (IntTag)tag.get("HideFlags");
                        if (hideFlags == null) {
                           hideFlags = new IntTag();
                        } else {
                           tag.put(this.nbtTagName + "|oldHideFlags", new IntTag(hideFlags.asByte()));
                        }

                        int flags = hideFlags.asByte() | 1;
                        hideFlags.setValue(flags);
                        tag.put("HideFlags", hideFlags);
                     }

                     tag.put(this.nbtTagName + "|" + key, remappedEnchantments);
                     display = (CompoundTag)tag.get("display");
                     if (display == null) {
                        tag.put("display", display = new CompoundTag());
                     }

                     ListTag loreTag = (ListTag)display.get("Lore");
                     if (loreTag == null) {
                        display.put("Lore", loreTag = new ListTag(StringTag.class));
                     }

                     lore.addAll(loreTag.getValue());
                     loreTag.setValue(lore);
                  }

                  return;
               }

               enchantmentEntry = (Tag)var7.next();
               idTag = ((CompoundTag)enchantmentEntry).get("id");
            } while(idTag == null);

            newId = ((NumberTag)idTag).asShort();
            enchantmentName = (String)this.enchantmentMappings.get(newId);
         } while(enchantmentName == null);

         enchantments.remove(enchantmentEntry);
         short level = ((NumberTag)((CompoundTag)enchantmentEntry).get("lvl")).asShort();
         if (this.hideLevelForEnchants != null && this.hideLevelForEnchants.contains(newId)) {
            lore.add(new StringTag(enchantmentName));
         } else {
            lore.add(new StringTag(enchantmentName + " " + EnchantmentRewriter.getRomanNumber(level)));
         }

         remappedEnchantments.add(enchantmentEntry);
      }
   }

   public void rewriteEnchantmentsToServer(CompoundTag tag, boolean storedEnchant) {
      String key = storedEnchant ? "StoredEnchantments" : "ench";
      ListTag remappedEnchantments = (ListTag)tag.remove(this.nbtTagName + "|" + key);
      ListTag enchantments = (ListTag)tag.get(key);
      if (enchantments == null) {
         enchantments = new ListTag(CompoundTag.class);
      }

      if (!storedEnchant && tag.remove(this.nbtTagName + "|dummyEnchant") != null) {
         Iterator var6 = enchantments.copy().iterator();

         while(var6.hasNext()) {
            Tag enchantment = (Tag)var6.next();
            short id = ((NumberTag)((CompoundTag)enchantment).get("id")).asShort();
            short level = ((NumberTag)((CompoundTag)enchantment).get("lvl")).asShort();
            if (id == 0 && level == 0) {
               enchantments.remove(enchantment);
            }
         }

         IntTag hideFlags = (IntTag)tag.remove(this.nbtTagName + "|oldHideFlags");
         if (hideFlags != null) {
            tag.put("HideFlags", new IntTag(hideFlags.asByte()));
         } else {
            tag.remove("HideFlags");
         }
      }

      CompoundTag display = (CompoundTag)tag.get("display");
      ListTag lore = display != null ? (ListTag)display.get("Lore") : null;
      Iterator var13 = remappedEnchantments.copy().iterator();

      while(var13.hasNext()) {
         Tag enchantment = (Tag)var13.next();
         enchantments.add(enchantment);
         if (lore != null && lore.size() != 0) {
            lore.remove(lore.get(0));
         }
      }

      if (lore != null && lore.size() == 0) {
         display.remove("Lore");
         if (display.isEmpty()) {
            tag.remove("display");
         }
      }

      tag.put(key, enchantments);
   }

   public void setHideLevelForEnchants(int... enchants) {
      this.hideLevelForEnchants = new HashSet();
      int[] var2 = enchants;
      int var3 = enchants.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int enchant = var2[var4];
         this.hideLevelForEnchants.add((short)enchant);
      }

   }
}
