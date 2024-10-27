package xyz.cucumber.base.file.files;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.util.Session;
import xyz.cucumber.base.interf.altmanager.AltManager;
import xyz.cucumber.base.interf.altmanager.ut.AltManagerSession;
import xyz.cucumber.base.utils.FileUtils;

public class AccountsFile extends FileUtils {
   public AccountsFile() {
      super("Gothaj", "accounts.json");
   }

   public void save(AltManager altManager) {
      try {
         JsonObject json = new JsonObject();
         Iterator var4 = altManager.sessions.iterator();

         while(var4.hasNext()) {
            AltManagerSession m = (AltManagerSession)var4.next();
            JsonObject jsonMod = new JsonObject();
            jsonMod.addProperty("id", m.getSession().getPlayerID());
            jsonMod.addProperty("token", m.getSession().getToken());
            json.add(m.getSession().getUsername(), jsonMod);
         }

         PrintWriter save = new PrintWriter(new FileWriter(this.getFile()));
         save.println(prettyGson.toJson(json));
         save.close();
      } catch (Exception var6) {
      }

   }

   public void load(AltManager altManager) {
      try {
         BufferedReader load = new BufferedReader(new FileReader(this.getFile()));
         JsonObject json = (JsonObject)jsonParser.parse(load);
         load.close();
         Iterator itr = json.entrySet().iterator();

         while(itr.hasNext()) {
            Entry<String, JsonElement> entry = (Entry)itr.next();
            JsonObject module = (JsonObject)entry.getValue();
            altManager.sessions.add(new AltManagerSession(altManager, new Session((String)entry.getKey(), module.get("id").getAsString(), module.get("token").getAsString(), "mojang")));
         }
      } catch (Exception var7) {
      }

   }
}
