package xyz.cucumber.base.file.files;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map.Entry;
import xyz.cucumber.base.Client;
import xyz.cucumber.base.module.Mod;
import xyz.cucumber.base.utils.FileUtils;

public class BindFile extends FileUtils {
   public BindFile() {
      super("Gothaj", "binds.json");
   }

   public void save() {
      try {
         JsonObject json = new JsonObject();
         Iterator var3 = Client.INSTANCE.getModuleManager().getModules().iterator();

         while(var3.hasNext()) {
            Mod m = (Mod)var3.next();
            JsonObject jsonMod = new JsonObject();
            jsonMod.addProperty("key", m.getKey());
            json.add(m.getName(), jsonMod);
         }

         PrintWriter save = new PrintWriter(new FileWriter(this.getFile()));
         save.println(prettyGson.toJson(json));
         save.close();
      } catch (Exception var5) {
      }

   }

   public void load() {
      try {
         BufferedReader load = new BufferedReader(new FileReader(this.getFile()));
         JsonObject json = (JsonObject)jsonParser.parse(load);
         load.close();
         Iterator itr = json.entrySet().iterator();

         while(itr.hasNext()) {
            Entry<String, JsonElement> entry = (Entry)itr.next();
            Mod mod = Client.INSTANCE.getModuleManager().getModule((String)entry.getKey());
            if (mod != null) {
               JsonObject module = (JsonObject)entry.getValue();
               mod.setKey(module.get("key").getAsInt());
            }
         }
      } catch (Exception var7) {
      }

   }
}
