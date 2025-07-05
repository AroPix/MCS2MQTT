package de.aropix.mcs2mqtt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

        Button killMCSButton = findViewById(R.id.kill_mcs_app);
        Button startMCSButton = findViewById(R.id.start_mcs_app);

        Button openFactoryMode = findViewById(R.id.open_factory_mode);
        Button openSettings = findViewById(R.id.open_settings);
        Button openLsposed = findViewById(R.id.open_lsposed);
        Button openMagisk = findViewById(R.id.open_magisk);

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
}
