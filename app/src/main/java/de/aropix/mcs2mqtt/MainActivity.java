package de.aropix.mcs2mqtt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private EditText mqttHost, mqttPort, mqttUsername, mqttPassword;
    private Button saveButton, killMCSButton;

    private static final String PREFS_NAME = "mqtt_settings";

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

        mqttHost = findViewById(R.id.mqtt_host);
        mqttPort = findViewById(R.id.mqtt_port);
        mqttUsername = findViewById(R.id.mqtt_username);
        mqttPassword = findViewById(R.id.mqtt_password);
        saveButton = findViewById(R.id.save_button);
        killMCSButton = findViewById(R.id.kill_mcs_app);


        loadPreferences();

        saveButton.setOnClickListener(v -> {
            savePreferences();
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Restart Required")
                    .setMessage("The Monsieur Cuisine app needs to be restarted for the new settings to take effect, do you want to kill the app?")
                    .setPositiveButton("Accept", (dialog, which) -> {
                        savePreferences();
                        killAppWithRoot("com.tecpal.device.mc30");
                        finishAffinity();
                        System.exit(0);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        killMCSButton.setOnClickListener(v -> killAppWithRoot("com.tecpal.device.mc30"));
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_WORLD_READABLE);
        mqttHost.setText(prefs.getString("mqtt_host", "localhost"));
        mqttPort.setText(prefs.getString("mqtt_port", "1883"));
        mqttUsername.setText(prefs.getString("mqtt_username", ""));
        mqttPassword.setText(prefs.getString("mqtt_password", ""));
    }

    private void savePreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("mqtt_host", mqttHost.getText().toString());
        editor.putString("mqtt_port", mqttPort.getText().toString());
        editor.putString("mqtt_username", mqttUsername.getText().toString());
        editor.putString("mqtt_password", mqttPassword.getText().toString());
        editor.apply();

        File prefsFile = new File(getApplicationInfo().dataDir + "/shared_prefs/settings.xml");
        prefsFile.setReadable(true, false);
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
            // Log only, don't rethrow
            e.printStackTrace();
        }
    }
}
