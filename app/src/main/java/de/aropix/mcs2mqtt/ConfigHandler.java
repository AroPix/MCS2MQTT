package de.aropix.mcs2mqtt;

import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class ConfigHandler {
    public static Settings getSettings() throws JSONException, IOException {
        Settings settings = new Settings();
        File file = new File(Environment.getExternalStorageDirectory(), "mcs2mqtt_config.json");
        if (file.exists()) {
            String jsonStr = new String(Files.readAllBytes(file.toPath()));
            JSONObject obj = new JSONObject(jsonStr);
            if (obj.has("host")) settings.setHost(obj.getString("host"));
            if (obj.has("port")) settings.setPort(obj.getString("port"));
            if (obj.has("user")) settings.setUser(obj.getString("user"));
            if (obj.has("pass")) settings.setPass(obj.getString("pass"));
            if (obj.has("showWelcomePopup")) settings.setShowWelcomePopup(obj.getBoolean("showWelcomePopup"));

        }
        return settings;
    }

    public static void saveSettings(String host, String port, String username, String password, Boolean showWelcomePopup) throws JSONException {
        File file = new File(Environment.getExternalStorageDirectory(), "mcs2mqtt_config.json");
        JSONObject json = new JSONObject();
        json.put("host", host);
        json.put("port", port);
        json.put("user", username);
        json.put("pass", password);
        json.put("showWelcomePopup", showWelcomePopup);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
