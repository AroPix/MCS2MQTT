package de.aropix.mcs2mqtt.hooks;

import static de.aropix.mcs2mqtt.ConfigHandler.getSettings;
import static de.aropix.mcs2mqtt.ConfigHandler.saveSettings;

import android.graphics.Bitmap;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONException;

import java.io.IOException;

import de.aropix.mcs2mqtt.Settings;
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

                            webView.addJavascriptInterface(new Object() {


                                @JavascriptInterface
                                public void saveSettingsHTML(String host, String port, String username, String password) throws JSONException {
                                    saveSettings(host, port, username, password);
                                }

                                @JavascriptInterface
                                public String getSettingsHTML() throws JSONException, IOException {
                                    Settings settings = getSettings();
                                    return settings.getHost() + "," + settings.getPort() + "," + settings.getUser() + "," + settings.getPass();
                                }




                            }, "AndroidBridge");


                            webView.loadUrl("file:///sdcard/settings.html");

                        }
                    }
                }
        );

    }
}

