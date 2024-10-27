package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Iterator;

public class UserListWhitelist extends UserList<GameProfile, UserListWhitelistEntry> {
   public UserListWhitelist(File p_i1132_1_) {
      super(p_i1132_1_);
   }

   protected UserListEntry<GameProfile> createEntry(JsonObject entryData) {
      return new UserListWhitelistEntry(entryData);
   }

   public String[] getKeys() {
      String[] astring = new String[this.getValues().size()];
      int i = 0;

      UserListWhitelistEntry userlistwhitelistentry;
      for(Iterator var4 = this.getValues().values().iterator(); var4.hasNext(); astring[i++] = ((GameProfile)userlistwhitelistentry.getValue()).getName()) {
         userlistwhitelistentry = (UserListWhitelistEntry)var4.next();
      }

      return astring;
   }

   protected String getObjectKey(GameProfile obj) {
      return obj.getId().toString();
   }

   public GameProfile getBannedProfile(String name) {
      Iterator var3 = this.getValues().values().iterator();

      while(var3.hasNext()) {
         UserListWhitelistEntry userlistwhitelistentry = (UserListWhitelistEntry)var3.next();
         if (name.equalsIgnoreCase(((GameProfile)userlistwhitelistentry.getValue()).getName())) {
            return (GameProfile)userlistwhitelistentry.getValue();
         }
      }

      return null;
   }
}
