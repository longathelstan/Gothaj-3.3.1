package xyz.cucumber.base.interf.clientsettings.ext.adds;

import xyz.cucumber.base.interf.clientsettings.ext.Setting;
import xyz.cucumber.base.interf.clientsettings.ext.impl.ClientSetting;
import xyz.cucumber.base.interf.clientsettings.ext.impl.NumberClientSetting;
import xyz.cucumber.base.module.settings.NumberSettings;

public class BloomSetting extends Setting {
   public NumberSettings radius = new NumberSettings("Radius", 7.0D, 1.0D, 15.0D, 0.5D);
   public NumberSettings compression = new NumberSettings("Compression", 1.0D, 1.0D, 10.0D, 0.5D);
   public NumberSettings saturation = new NumberSettings("Saturation", 2.0D, 1.0D, 15.0D, 0.5D);

   public BloomSetting() {
      super("Bloom");
      this.addSettings(new ClientSetting[]{new NumberClientSetting(this.radius), new NumberClientSetting(this.saturation), new NumberClientSetting(this.compression)});
   }
}
