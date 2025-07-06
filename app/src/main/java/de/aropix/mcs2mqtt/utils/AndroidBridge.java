package de.aropix.mcs2mqtt.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import de.aropix.mcs2mqtt.MqttHandler;
import de.aropix.mcs2mqtt.Settings;

import static de.aropix.mcs2mqtt.utils.ConfigHandler.*;

public class AndroidBridge {

    private final Context context;
    private String environment = "module";
    private MqttHandler mqtt;

    public AndroidBridge(Context context, MqttHandler mqtt) {
        this.context = context;
        this.mqtt = mqtt;
    }

    public AndroidBridge(Context context, String environment) {
        this.context = context;
        this.environment = environment;
    }

    @JavascriptInterface
    public void saveSettingsHTML(String jsonStr) throws JSONException {
        JSONObject obj = new JSONObject(jsonStr);

        String host = obj.optString("host", "localhost");
        String port = obj.optString("port", "1883");
        String username = obj.optString("user", "");
        String password = obj.optString("pass", "");
        boolean showWelcomePopup = obj.optBoolean("showWelcomePopup", true);
        boolean sendRecipeToMQTT = obj.optBoolean("sendRecipeToMQTT", true);

        saveSettings(host, port, username, password, showWelcomePopup, sendRecipeToMQTT);

        if (Objects.equals(environment, "module") && mqtt != null) {
            mqtt.reconnect(host, port, username, password);
        }
    }


    @JavascriptInterface
    public String getEnvironment() {
        return this.environment;
    }

    @JavascriptInterface
    public String getSettingsHTML() throws JSONException, IOException {
        Settings settings = getSettings();
        return settings.toJsonString();
    }

    @JavascriptInterface
    public void launchApplication(String packageName) {
        launchAppByPackageName(context, packageName);
    }


    @JavascriptInterface
    public static void killAppWithRoot(String packageName) {
        try {
            Process su = Runtime.getRuntime().exec("su");
            OutputStream os = su.getOutputStream();
            String cmd = "am force-stop " + packageName + "\nexit\n";
            os.write(cmd.getBytes());
            os.flush();
            os.close();
            su.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void launchAppByPackageName(Context context, String packageName) {
        if (context == null || packageName == null || packageName.isEmpty()) {
            return;
        }
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                Log.e("LaunchApp", "Cannot find launch intent for package: " + packageName);
            }
        } catch (Exception e) {
            Log.e("LaunchApp", "Failed to launch package: " + packageName, e);
        }
    }
}
