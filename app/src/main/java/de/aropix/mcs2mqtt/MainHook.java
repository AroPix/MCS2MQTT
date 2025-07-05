package de.aropix.mcs2mqtt;

import static de.aropix.mcs2mqtt.ConfigHandler.getSettings;

import de.aropix.mcs2mqtt.hooks.CustomHTML;
import de.aropix.mcs2mqtt.hooks.GetRecipeHook;
import de.aropix.mcs2mqtt.hooks.PrivacyModal;
import de.aropix.mcs2mqtt.hooks.SerialDataHook;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.tecpal.device.mc30")) return;

        Settings settings = getSettings();

        XposedBridge.log("Host: " + settings.host + " | Port: " + settings.port);


        MqttHandler mqtt = new MqttHandler(settings.host, settings.port, settings.user, settings.pass);

        SerialDataHook.initHook(lpparam, mqtt);
        PrivacyModal.initHook(lpparam.classLoader);
        GetRecipeHook.initHook(lpparam.classLoader, mqtt);
        CustomHTML.initHook(lpparam.classLoader);
        //GetModeHook.initHook(lpparam.classLoader);

    }
}
