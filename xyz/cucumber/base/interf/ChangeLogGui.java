package xyz.cucumber.base.interf;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import xyz.cucumber.base.utils.RenderUtils;
import xyz.cucumber.base.utils.render.Fonts;

public class ChangeLogGui extends GuiScreen {
   private static String file = "https://raw.githubusercontent.com/gothajstorage/gothaj-changelogs/main/Changelogs.json";
   private static List<ChangeLogGui.ChangeLog> changes = new ArrayList();

   public void initGui() {
      this.reloadChangelogs();
      System.out.println(changes);
   }

   public void reloadChangelogs() {
      if (changes.isEmpty()) {
         changes.clear();

         try {
            URLConnection connection = (new URL(file)).openConnection();
            Throwable var2 = null;
            Object var3 = null;

            try {
               BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

               try {
                  StringBuilder stringBuilder = new StringBuilder();

                  String line;
                  while((line = reader.readLine()) != null) {
                     stringBuilder.append(line);
                  }

                  JsonParser parser = new JsonParser();
                  JsonObject json = (JsonObject)parser.parse(stringBuilder.toString());
                  this.loadChanges(json);
               } finally {
                  if (reader != null) {
                     reader.close();
                  }

               }
            } catch (Throwable var16) {
               if (var2 == null) {
                  var2 = var16;
               } else if (var2 != var16) {
                  var2.addSuppressed(var16);
               }

               throw var2;
            }
         } catch (IOException var17) {
            var17.printStackTrace();
         }

      }
   }

   private void loadChanges(JsonObject json) {
      ChangeLogGui.ChangeLog change;
      for(Iterator var3 = json.entrySet().iterator(); var3.hasNext(); changes.add(change)) {
         Entry<String, JsonElement> entry = (Entry)var3.next();
         JsonObject element = (JsonObject)entry.getValue();
         change = new ChangeLogGui.ChangeLog((String)entry.getKey());
         JsonArray adds = element.get("add").getAsJsonArray();
         if (adds != null) {
            for(int i = 0; i < adds.size(); ++i) {
               change.adds.add(adds.get(i).getAsString());
            }
         }

         JsonArray fixes = element.get("fixes").getAsJsonArray();
         if (fixes != null) {
            for(int i = 0; i < fixes.size(); ++i) {
               change.fixes.add(fixes.get(i).getAsString());
            }
         }

         JsonArray removes = element.get("removes").getAsJsonArray();
         if (fixes != null) {
            for(int i = 0; i < removes.size(); ++i) {
               change.removes.add(removes.get(i).getAsString());
            }
         }
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      double h = 0.0D;

      ChangeLogGui.ChangeLog change;
      for(Iterator var7 = changes.iterator(); var7.hasNext(); h += (double)(30 + change.adds.size() * 10 + 24 + change.fixes.size() * 10 + change.removes.size() * 10 + 8)) {
         change = (ChangeLogGui.ChangeLog)var7.next();
         RenderUtils.drawRoundedRect((double)(this.width / 2 - 150), 30.0D + h, (double)(this.width / 2 + 150), (double)(30 + 30 + change.adds.size() * 10 + 24 + change.fixes.size() * 10 + change.removes.size() * 10) + h, -1879048192, 5.0F);
         GlStateManager.pushMatrix();
         GlStateManager.translate((double)(this.width / 2) - Fonts.getFont("mitr").getWidth("Change log: " + change.version) / 2.0D * 1.2999999523162842D, 34.0D + h, 0.0D);
         GlStateManager.scale(1.2999999523162842D, 1.2999999523162842D, 1.0D);
         Fonts.getFont("mitr").drawString("Change log: " + change.version, 0.0D, 0.0D, -1);
         GlStateManager.popMatrix();
         double i = 0.0D;
         String fixs;
         Iterator var11;
         if (!change.adds.isEmpty()) {
            i += 30.0D;

            for(var11 = change.adds.iterator(); var11.hasNext(); i += 10.0D) {
               fixs = (String)var11.next();
               Fonts.getFont("rb-m").drawString("+ " + fixs, (double)(this.width / 2 - 140), 34.0D + h + i, -3355444);
            }
         }

         if (!change.fixes.isEmpty()) {
            i += 12.0D;

            for(var11 = change.fixes.iterator(); var11.hasNext(); i += 10.0D) {
               fixs = (String)var11.next();
               Fonts.getFont("rb-m").drawString("# " + fixs, (double)(this.width / 2 - 140), 34.0D + h + i, -3355444);
            }
         }

         if (!change.removes.isEmpty()) {
            i += 12.0D;

            for(var11 = change.removes.iterator(); var11.hasNext(); i += 10.0D) {
               fixs = (String)var11.next();
               Fonts.getFont("rb-m").drawString("- " + fixs, (double)(this.width / 2 - 140), 34.0D + h + i, -3355444);
            }
         }
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public class ChangeLog {
      private String version;
      private List<String> adds = new ArrayList();
      private List<String> fixes = new ArrayList();
      private List<String> removes = new ArrayList();

      public ChangeLog(String version) {
         this.version = version;
      }

      public void updateList(JsonObject json) {
      }

      public String getVersion() {
         return this.version;
      }

      public List<String> getAdds() {
         return this.adds;
      }

      public List<String> getFixes() {
         return this.fixes;
      }

      public List<String> getRemoves() {
         return this.removes;
      }
   }
}
