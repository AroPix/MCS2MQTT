package de.aropix.mcs2mqtt;

import de.aropix.mcs2mqtt.hooks.GetRecipeHook;
import de.aropix.mcs2mqtt.hooks.PrivacyModal;
import de.aropix.mcs2mqtt.hooks.SerialDataHook;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.tecpal.device.mc30")) return;

        XSharedPreferences prefs = new XSharedPreferences("de.aropix.mcs2mqtt", "mqtt_settings");
        prefs.reload();

        // @todo implement retrieval of preferences
        // Override these strings or change the default value to your liking
        String hostname = prefs.getString("mqtt_host", "");
        String port = prefs.getString("mqtt_port", "1883");
        String username = prefs.getString("mqtt_username", "");
        String password = prefs.getString("mqtt_password", "");
        XposedBridge.log("Hostname: " + hostname + " | Username: " + username);


        MqttHandler mqtt = new MqttHandler(hostname, port, username, password);

        SerialDataHook.initHook(lpparam, mqtt);
        PrivacyModal.initHook(lpparam.classLoader);
        GetRecipeHook.initHook(lpparam.classLoader, mqtt);
        //GetModeHook.initHook(lpparam.classLoader);

    }
}
