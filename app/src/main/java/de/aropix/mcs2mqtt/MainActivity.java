package de.aropix.mcs2mqtt;

import static de.aropix.mcs2mqtt.ConfigHandler.getSettings;
import static de.aropix.mcs2mqtt.ConfigHandler.saveSettings;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.robv.android.xposed.XposedBridge;

public class MainActivity extends AppCompatActivity {

    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText mqttHost = findViewById(R.id.mqtt_host);
        EditText mqttPort = findViewById(R.id.mqtt_port);
        EditText mqttUsername = findViewById(R.id.mqtt_username);
        EditText mqttPassword = findViewById(R.id.mqtt_password);

        try {
            settings = getSettings();
            mqttHost.setText(settings.getHost());
            mqttPort.setText(settings.getPort());
            mqttUsername.setText(settings.getUser());
            mqttPassword.setText(settings.getPass());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        Button killMCSButton = findViewById(R.id.kill_mcs_app);
        Button startMCSButton = findViewById(R.id.start_mcs_app);

        Button openFactoryMode = findViewById(R.id.open_factory_mode);
        Button openSettings = findViewById(R.id.open_settings);
        Button openLsposed = findViewById(R.id.open_lsposed);
        Button openMagisk = findViewById(R.id.open_magisk);
        Button saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(v -> {
            try {
                saveSettings(mqttHost.getText().toString(), mqttPort.getText().toString(), mqttUsername.getText().toString(), mqttPassword.getText().toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        openFactoryMode.setOnClickListener(v -> {
            launchAppByPackageName(v.getContext(), "com.discovery.factorymode");
        });

        openSettings.setOnClickListener(v -> {
            launchAppByPackageName(v.getContext(), "com.android.settings");
        });

        openLsposed.setOnClickListener(v -> {
            launchAppByPackageName(v.getContext(), "org.lsposed.manager");
        });

        openMagisk.setOnClickListener(v -> {
            launchAppByPackageName(v.getContext(), "com.topjohnwu.magisk");
        });

        startMCSButton.setOnClickListener(v -> {
            launchAppByPackageName(v.getContext(), "com.tecpal.device.mc30");
        });

        killMCSButton.setOnClickListener(v -> killAppWithRoot("com.tecpal.device.mc30"));
        copySettingsHtmlToSdcard(getApplicationContext());
    }

    private void killAppWithRoot(String packageName) {
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

    public static void copySettingsHtmlToSdcard(Context moduleContext) {
        File outFile = new File(Environment.getExternalStorageDirectory(), "settings.html");

        try (
                InputStream is = moduleContext.getAssets().open("settings.html");
                OutputStream os = new FileOutputStream(outFile, false)
        ) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
            Log.e("CustomHtml", "Copied settings.html to " + outFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e("CustomHtml", "Failed to copy settings.html: " + Log.getStackTraceString(e));
        }
    }

}
