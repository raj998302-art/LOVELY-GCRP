package com.samp.mobile.launcher;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.samp.mobile.R;
import com.samp.mobile.launcher.util.LogUtil;

/**
 * Shows the in-app client log (/storage/emulated/0/GRAND CITY RP/client.log)
 * opened from the Logs button on the main screen.
 */
public class LogsActivity extends AppCompatActivity {

    private static final String TAG = "GCRPLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_logs);
        } catch (Exception e) {
            Log.e(TAG, "setContentView failed", e);
            finish();
            return;
        }

        TextView tvLogs = findViewById(R.id.tv_logs);
        if (tvLogs != null) {
            try {
                tvLogs.setMovementMethod(new ScrollingMovementMethod());
                tvLogs.setText(LogUtil.read());
            } catch (Exception e) {
                Log.e(TAG, "load log text failed", e);
                tvLogs.setText("Error loading log: " + e.getMessage() + "\n");
            }
        }
    }
}
