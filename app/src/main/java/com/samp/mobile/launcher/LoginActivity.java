package com.samp.mobile.launcher;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.samp.mobile.R;
import com.samp.mobile.game.SAMP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Simple CRMP / SA-MP client entry screen (no launcher UI).
 *
 * The client only shows two UI elements:
 *   1. Username field
 *   2. Login button
 *
 * Host and port are read from the config file that the user edits themselves:
 *   /storage/emulated/0/GRAND CITY RP/SAMP/settings.ini
 *
 * All game data lives in /storage/emulated/0/GRAND CITY RP/ (storage path is forced
 * in native code, see cpp/samp/game/hooks.cpp).
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // GRAND CITY RP data folder (all data goes here).
    public static final String GCRP_FOLDER = "/storage/emulated/0/GRAND CITY RP";
    public static final String SETTINGS_FILE = GCRP_FOLDER + "/SAMP/settings.ini";

    private static final int REQ_ALL_FILES_ACCESS = 1001;
    private static final int REQ_STORAGE_PERMISSION = 1002;

    private EditText etUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // On first launch, copy the bundled settings template into the GRAND CITY RP folder
        // so the user has a settings.ini to fill in with their host/port/name.
        copySettingsTemplateFromAssets();

        etUsername = findViewById(R.id.et_username);
        Button btnLogin = findViewById(R.id.btn_login);

        // Pre-fill username if already saved in settings.ini
        String savedName = readSetting("name");
        if (!TextUtils.isEmpty(savedName)) {
            etUsername.setText(savedName);
            etUsername.setSelection(etUsername.getText().length());
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ensureStorageReady()) {
                    return;
                }

                String username = etUsername.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    username = "Nick_Name";
                }
                // Save the chosen username into settings.ini (read by native CSettings).
                writeSetting("name", username);

                // Host and port must already be set by the user in settings.ini.
                String host = readSetting("host");
                String port = readSetting("port");
                if (TextUtils.isEmpty(host) || TextUtils.isEmpty(port)) {
                    Toast.makeText(LoginActivity.this,
                            "Please set host and port in " + SETTINGS_FILE,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                Log.i(TAG, "Connecting to " + host + ":" + port + " as " + username);

                // Launch the CRMP / SA-MP game client.
                startActivity(new Intent(LoginActivity.this, SAMP.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ALL_FILES_ACCESS) {
            // User came back from the "All files access" settings page.
            // Storage will be re-checked on the next login click.
        }
    }

    /**
     * Make sure we can read/write /storage/emulated/0/GRAND CITY RP/SAMP/settings.ini.
     */
    private boolean ensureStorageReady() {
        File folder = new File(GCRP_FOLDER + "/SAMP");
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (!created) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                        && !Environment.isExternalStorageManager()) {
                    Toast.makeText(this,
                            "Grant 'All files access' to create " + GCRP_FOLDER,
                            Toast.LENGTH_LONG).show();
                    openAllFilesAccessSettings();
                    return false;
                }
                Toast.makeText(this, "Cannot create " + GCRP_FOLDER + "/SAMP",
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                && !Environment.isExternalStorageManager()) {
            Toast.makeText(this,
                    "Grant 'All files access' to write to " + GCRP_FOLDER,
                    Toast.LENGTH_LONG).show();
            openAllFilesAccessSettings();
            return false;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQ_STORAGE_PERMISSION);
            return false;
        }

        return true;
    }

    private void openAllFilesAccessSettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQ_ALL_FILES_ACCESS);
    }

    /**
     * Copy assets/settings.ini to the storage folder if it does not exist yet.
     */
    private void copySettingsTemplateFromAssets() {
        File dest = new File(SETTINGS_FILE);
        if (dest.exists()) {
            return;
        }
        dest.getParentFile().mkdirs();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = getAssets().open("settings.ini");
            out = new FileWriter(dest);
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            Log.i(TAG, "Copied settings template to " + SETTINGS_FILE);
        } catch (IOException e) {
            Log.e(TAG, "Failed to copy settings template", e);
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException ignored) {
            }
            try {
                if (out != null) out.close();
            } catch (IOException ignored) {
            }
        }
    }

    private String readSetting(String key) {
        File file = new File(SETTINGS_FILE);
        if (!file.exists()) {
            return "";
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("[")) {
                    continue;
                }
                int idx = line.indexOf('=');
                if (idx < 0) {
                    continue;
                }
                String k = line.substring(0, idx).trim();
                String v = line.substring(idx + 1).trim();
                if (k.equalsIgnoreCase(key)) {
                    return v;
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to read settings", e);
        }
        return "";
    }

    private void writeSetting(String key, String value) {
        File file = new File(SETTINGS_FILE);
        file.getParentFile().mkdirs();
        StringBuilder content = new StringBuilder();
        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("#") && !trimmed.startsWith("[")) {
                    int idx = line.indexOf('=');
                    if (idx >= 0 && line.substring(0, idx).trim().equalsIgnoreCase(key)) {
                        content.append(key).append(" = ").append(value).append("\n");
                        found = true;
                        continue;
                    }
                }
                content.append(line).append("\n");
            }
        } catch (IOException ignored) {
            // file may not exist yet
        }
        if (!found) {
            content.append(key).append(" = ").append(value).append("\n");
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(content.toString());
        } catch (IOException e) {
            Log.e(TAG, "Failed to write settings", e);
        }
    }
}
