package de.aropix.mcs2mqtt;

import de.aropix.mcs2mqtt.hooks.GetRecipeHook;
import de.aropix.mcs2mqtt.hooks.PrivacyModal;
import de.aropix.mcs2mqtt.hooks.SerialDataHook;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.tecpal.device.mc30")) return;

        SerialDataHook.initHook(lpparam);
        PrivacyModal.initHook(lpparam.classLoader);
        GetRecipeHook.initHook(lpparam.classLoader);


    }
}
