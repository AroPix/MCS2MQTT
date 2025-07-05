package de.aropix.mcs2mqtt;

import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import de.aropix.mcs2mqtt.hooks.CustomHTML;
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

        Settings settings = getSettings();

        XposedBridge.log("Host: " + settings.host + " | Port: " + settings.port);


        MqttHandler mqtt = new MqttHandler(settings.host, settings.port, settings.user, settings.pass);

        SerialDataHook.initHook(lpparam, mqtt);
        PrivacyModal.initHook(lpparam.classLoader);
        GetRecipeHook.initHook(lpparam.classLoader, mqtt);
        CustomHTML.initHook(lpparam.classLoader);
        //GetModeHook.initHook(lpparam.classLoader);

    }

    public static Settings getSettings() throws JSONException, IOException {
        Settings settings = new Settings();
        File file = new File(Environment.getExternalStorageDirectory(), "mcs2mqtt_config.json");
        if (file.exists()) {
            String jsonStr = new String(Files.readAllBytes(file.toPath()));
            JSONObject obj = new JSONObject(jsonStr);
            settings.setHost(obj.getString("host"));
            settings.setPort(obj.getString("port"));
            settings.setUser(obj.getString("user"));
            settings.setPass(obj.getString("pass"));
        }
        return settings;
    }

}
