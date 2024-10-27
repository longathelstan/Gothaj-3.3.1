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
import xyz.cucumber.base.interf.clientsettings.ext.Setting;
import xyz.cucumber.base.interf.clientsettings.ext.adds.BloomSetting;
import xyz.cucumber.base.interf.clientsettings.ext.adds.BlurSetting;
import xyz.cucumber.base.utils.FileUtils;

public class ClientSettingsFile extends FileUtils {
   public ClientSettingsFile() {
      super("Gothaj", "client-settings.json");
   }

   public void save() {
      try {
         JsonObject json = new JsonObject();

         Setting s;
         JsonObject jsonMod;
         for(Iterator var3 = Client.INSTANCE.getClientSettings().getSettings().iterator(); var3.hasNext(); json.add(s.getName(), jsonMod)) {
            s = (Setting)var3.next();
            jsonMod = new JsonObject();
            if (s instanceof BloomSetting) {
               jsonMod.addProperty("compression", ((BloomSetting)s).compression.getValue());
               jsonMod.addProperty("radius", ((BloomSetting)s).radius.getValue());
               jsonMod.addProperty("saturation", ((BloomSetting)s).saturation.getValue());
            }

            if (s instanceof BlurSetting) {
               jsonMod.addProperty("radius", ((BlurSetting)s).radius.getValue());
            }
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

         while(true) {
            JsonObject module;
            Setting settingName;
            do {
               if (!itr.hasNext()) {
                  return;
               }

               Entry<String, JsonElement> entry = (Entry)itr.next();
               module = (JsonObject)entry.getValue();
               settingName = Client.INSTANCE.getClientSettings().getSettingByName((String)entry.getKey());
            } while(settingName == null);

            Iterator var8 = Client.INSTANCE.getClientSettings().getSettings().iterator();

            while(var8.hasNext()) {
               Setting s = (Setting)var8.next();
               if (s instanceof BloomSetting) {
                  BloomSetting setting = (BloomSetting)s;
                  setting.compression.setValue((double)module.get("compression").getAsInt());
                  setting.radius.setValue((double)module.get("radius").getAsInt());
                  setting.saturation.setValue((double)module.get("saturation").getAsInt());
               }

               if (s instanceof BlurSetting) {
                  BlurSetting setting = (BlurSetting)s;
                  setting.radius.setValue((double)module.get("radius").getAsInt());
               }
            }
         }
      } catch (Exception var10) {
      }
   }
}
