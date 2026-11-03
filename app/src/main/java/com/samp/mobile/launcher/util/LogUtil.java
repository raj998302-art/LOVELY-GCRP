package com.samp.mobile.launcher.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Tiny in-app logger used by the "Logs" button.
 * Writes to /storage/emulated/0/GRAND CITY RP/client.log so it can be reviewed on device.
 */
public class LogUtil {

    public static final String LOG_FILE = "/storage/emulated/0/GRAND CITY RP/client.log";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    private LogUtil() {}

    public static void log(String msg) {
        String line = SDF.format(new Date()) + " " + msg + "\n";
        try {
            File f = new File(LOG_FILE);
            if (f.getParentFile() != null) {
                f.getParentFile().mkdirs();
            }
            FileWriter fw = new FileWriter(f, true);
            fw.write(line);
            fw.close();
        } catch (IOException ignored) {
        }
    }

    public static String read() {
        File f = new File(LOG_FILE);
        if (!f.exists()) {
            return "(no logs yet)\n";
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            return "Error reading logs: " + e.getMessage() + "\n";
        }
        if (sb.length() == 0) {
            return "(no logs yet)\n";
        }
        return sb.toString();
    }
}
