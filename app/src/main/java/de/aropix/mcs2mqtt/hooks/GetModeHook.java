package de.aropix.mcs2mqtt.hooks;

import android.content.Context;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class GetModeHook {
    public static void initHook(ClassLoader classLoader) {
        Class<?> clazz = XposedHelpers.findClass("a.g.a.g.f.u1", classLoader);
        for (Method method : clazz.getDeclaredMethods()) {
            Class<?>[] params = method.getParameterTypes();
            if (params.length == 6 &&
                    params[0] == Context.class &&
                    params[1] == int.class &&
                    params[2] == String.class &&
                    params[3] == String.class &&
                    params[4] == String.class &&
                    params[5] == boolean.class) {

                XposedBridge.log("Hooking obfuscated method: " + method);
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("Before hooked obfuscated method");
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("After hooked obfuscated method");
                    }
                });
            }
        }

    }
}
