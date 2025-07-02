package de.aropix.mcs2mqtt.hooks;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class PrivacyModal {
    public static void initHook(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("a.g.a.n.a.v", classLoader, "D0", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("Blocked privacy policy modal (D0)");
                param.setResult(null);
            }
        });
    }
}
