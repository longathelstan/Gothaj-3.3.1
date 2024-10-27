package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Iterator;

public class UserListOps extends UserList<GameProfile, UserListOpsEntry> {
   public UserListOps(File saveFile) {
      super(saveFile);
   }

   protected UserListEntry<GameProfile> createEntry(JsonObject entryData) {
      return new UserListOpsEntry(entryData);
   }

   public String[] getKeys() {
      String[] astring = new String[this.getValues().size()];
      int i = 0;

      UserListOpsEntry userlistopsentry;
      for(Iterator var4 = this.getValues().values().iterator(); var4.hasNext(); astring[i++] = ((GameProfile)userlistopsentry.getValue()).getName()) {
         userlistopsentry = (UserListOpsEntry)var4.next();
      }

      return astring;
   }

   public boolean bypassesPlayerLimit(GameProfile profile) {
      UserListOpsEntry userlistopsentry = (UserListOpsEntry)this.getEntry(profile);
      return userlistopsentry != null ? userlistopsentry.bypassesPlayerLimit() : false;
   }

   protected String getObjectKey(GameProfile obj) {
      return obj.getId().toString();
   }

   public GameProfile getGameProfileFromName(String username) {
      Iterator var3 = this.getValues().values().iterator();

      while(var3.hasNext()) {
         UserListOpsEntry userlistopsentry = (UserListOpsEntry)var3.next();
         if (username.equalsIgnoreCase(((GameProfile)userlistopsentry.getValue()).getName())) {
            return (GameProfile)userlistopsentry.getValue();
         }
      }

      return null;
   }
}
