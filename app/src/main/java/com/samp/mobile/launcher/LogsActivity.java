package com.samp.mobile.launcher;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.samp.mobile.R;
import com.samp.mobile.launcher.util.LogUtil;

/**
 * Shows the in-app client log (/storage/emulated/0/GRAND CITY RP/client.log)
 * opened from the Logs button on the main screen.
 */
public class LogsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        TextView tvLogs = findViewById(R.id.tv_logs);
        tvLogs.setMovementMethod(new ScrollingMovementMethod());
        tvLogs.setText(LogUtil.read());
    }
}
