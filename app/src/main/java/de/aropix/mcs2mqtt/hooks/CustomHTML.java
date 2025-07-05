package de.aropix.mcs2mqtt.hooks;

import static de.aropix.mcs2mqtt.utils.ConfigHandler.getSettings;
import static de.aropix.mcs2mqtt.utils.ConfigHandler.saveSettings;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.aropix.mcs2mqtt.Settings;
import de.aropix.mcs2mqtt.utils.AndroidBridge;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class CustomHTML {
    public static void initHook(ClassLoader classLoader) {

        XposedHelpers.findAndHookMethod(
                WebView.class,
                "loadUrl",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String url = (String) param.args[0];
                        XposedBridge.log("WebView is loading URL: " + url);
                    }
                }
        );

        XposedHelpers.findAndHookMethod(
                "android.webkit.WebViewClient",
                classLoader,
                "onPageStarted",
                WebView.class,
                String.class,
                Bitmap.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String url = (String) param.args[1];
                        if (url == null || url.isEmpty() || url.startsWith("about:") || url.startsWith("javascript:")) return;

                        XposedBridge.log("onPageStarted: " + url);

                        // url = https://mylidlaccount.lidl.com/?country_code=DE&track=False&language=de-DE&client_id=monsieurcuisineclient
                        if (url.contains("mylidlaccount.lidl.com")) {
                            WebView webView = (WebView) param.args[0];
                            webView.addJavascriptInterface(new AndroidBridge(AndroidAppHelper.currentApplication()), "AndroidBridge");
                            webView.loadUrl("file:///sdcard/settings.html");
                        }
                    }
                }
        );

    }
}

