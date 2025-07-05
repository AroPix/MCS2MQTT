package de.aropix.mcs2mqtt;

import static de.aropix.mcs2mqtt.utils.ConfigHandler.getSettings;
import static de.aropix.mcs2mqtt.utils.ConfigHandler.saveSettings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.webkit.WebView;
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

import de.aropix.mcs2mqtt.utils.AndroidBridge;

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

        WebView webView = findViewById(R.id.settings_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(this, "app"), "AndroidBridge");

        copySettingsHtmlToSdcard(getApplicationContext());
        webView.loadUrl("file:///android_asset/settings.html");
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
