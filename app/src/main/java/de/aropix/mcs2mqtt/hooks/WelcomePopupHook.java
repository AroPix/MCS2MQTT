package de.aropix.mcs2mqtt.hooks;

import static de.aropix.mcs2mqtt.ConfigHandler.getSettings;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class WelcomePopupHook {
    public static void initHook(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("a.g.a.n.h.v", classLoader, "W0", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws JSONException, IOException {
                if (!getSettings().getShowWelcomePopup())
                    param.setThrowable(null);
            }
        });

    }
}
