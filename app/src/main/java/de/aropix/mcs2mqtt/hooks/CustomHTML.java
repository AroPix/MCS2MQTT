package de.aropix.mcs2mqtt.hooks;

import static de.aropix.mcs2mqtt.MainHook.getSettings;

import android.graphics.Bitmap;
import android.os.Environment;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import de.aropix.mcs2mqtt.Settings;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
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
                                public void saveSettings(String host, String port, String username, String password) throws JSONException {
                                    XposedBridge.log("Host: " + host + " | Port: " + port);
                                    File file = new File(Environment.getExternalStorageDirectory(), "mcs2mqtt_config.json");
                                    JSONObject json = new JSONObject();
                                    json.put("host", host);
                                    json.put("port", port);
                                    json.put("user", username);
                                    json.put("pass", password);
                                    try (FileWriter writer = new FileWriter(file)) {
                                        writer.write(json.toString());
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }

                                }

                                @JavascriptInterface
                                public String getSettingsHTML() throws JSONException, IOException {
                                    Settings settings = getSettings();
                                    return settings.getHost() + "," + settings.getPort() + "," + settings.getUser() + "," + settings.getPass();
                                }




                            }, "AndroidBridge");


                            webView.loadUrl("file:///sdcard/Download/settings.html");

                        }
                    }
                }
        );

    }
}

