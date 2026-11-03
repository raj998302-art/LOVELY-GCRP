# GRAND CITY RP - CRMP / SA-MP Mobile Client (v2.10 base)

This is a simplified SA-MP Mobile 2.10 client (based on kuzia15/SAMP-Mobile branch `GTA-2.10`)
rebuilt as a lightweight client for CRMP (Grand City RP).

## What was changed

1. **Storage path forced to `/storage/emulated/0/GRAND CITY RP/`**
   - Native code (`app/src/main/cpp/samp/game/hooks.cpp`, `NvUtilInit_hook`) now overrides
     `g_pszStorage` to `/storage/emulated/0/GRAND CITY RP/`.
   - All game data (models, maps, `SAMP/settings.ini`, ...) is loaded from this folder.
   - Put all your CRMP data inside `/storage/emulated/0/GRAND CITY RP/`.

2. **Simple client UI (launcher UI removed)**
   - `LoginActivity` is the app entry point (replaces the old launcher/server-list UI).
   - Two UI elements only:
     1. **Username** field (`EditText`)
     2. **Login** button
   - Layout: `app/src/main/res/layout/activity_login.xml`

3. **Host / Port read from config file**
   - The client reads `host`, `port` and `name` from
     `/storage/emulated/0/GRAND CITY RP/SAMP/settings.ini`.
   - A template is bundled in `app/src/main/assets/settings.ini` and is copied to the
     storage folder on first launch if missing.
   - Edit that file and fill in your server data before tapping Login.

4. **ShadowHook kept**
   - ShadowHook integration (`com.bytedance.android:shadowhook:1.0.9`) is preserved in
     `GTASA.java` and the native build (`shadowhook::shadowhook`).

5. **`SAMP` game activity is no longer the launcher** — `LoginActivity` is now
   `MAIN`/`LAUNCHER`; tapping Login starts `SAMP`.

## First-time setup on your device

1. Install the APK (built from `assembleRelease` — see BUILD below).
2. Create the folder `/storage/emulated/0/GRAND CITY RP/` on the device (or let the app
   create the `SAMP` subfolder).
3. Place `/storage/emulated/0/GRAND CITY RP/SAMP/settings.ini` with your server data:

   ```ini
   [client]
   host = YOUR_SERVER_IP
   port = 7777
   name = YourNickName
   ```

   *(The app copies a template there on first run if it's missing.)*
4. Grant storage permissions when prompted. On Android 11+ you may need to grant the
   **"All files access"** permission (Settings → Apps → GRAND CITY RP → Permissions) so the
   client can read/write `/storage/emulated/0/GRAND CITY RP/`.
5. Open the app, enter your username, tap **Login**. The client reads host/port from
   `settings.ini` and connects to your CRMP server.

## Notes

- Version 2.10 = ARM32 + ARM64, ShadowHook integration, cache/data path configurable.
- If the app crashes, send the logcat/crash log and I can adjust the build.
- The release APK must be built with the Android SDK + NDK (see below); this sandbox does
  not have the Android toolchain, so the APK is not included here.

## Build

Requirements: Android SDK, NDK `26.2.11394342`, Java JDK.

```bash
cd /home/user/LOVELY-GCRP
export ANDROID_HOME=/path/to/android-sdk
export ANDROID_NDK_HOME=$ANDROID_HOME/ndk/26.2.11394342
chmod +x gradlew
./gradlew assembleRelease
```

The release APK will be at `app/build/outputs/apk/release/app-release.apk`.

Signing is configured in `app/build.gradle` using `../../keys/new.jks`
(store/key password `123123`, alias `key0`). You need that keystore to build a signed
release APK, or change `signingConfigs` to your own keystore.
