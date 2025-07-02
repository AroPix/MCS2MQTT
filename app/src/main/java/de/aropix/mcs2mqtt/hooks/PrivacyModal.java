package de.aropix.mcs2mqtt.hooks;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;

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
                Context context = (Context) AndroidAppHelper.currentApplication();

                Intent intent = context.getPackageManager().getLaunchIntentForPackage("de.aropix.mcs2mqtt");
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    XposedBridge.log("Could not launch de.aropix.mcs2mqtt");
                }
            }
        });
    }
}
