package de.aropix.mcs2mqtt.hooks;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class WelcomePopupHook {
    public static void initHook(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("a.g.a.n.h.v", classLoader, "W0", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                param.setThrowable(null);
            }
        });

    }
}
