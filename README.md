# LOVELY-GCRP

GRAND CITY RP — simplified CRMP / SA-MP Mobile client (built on SA-MP Mobile v2.10).

## Base

Ported from [kuzia15/SAMP-Mobile](https://github.com/kuzia15/SAMP-Mobile.git), branch `GTA-2.10`
(SA-MP 2.10: ARM32 + ARM64, **ShadowHook** integration).

## What this build does

- **Simple client UI only** (no launcher/server-list UI):
  - Username field
  - Login button
- **Reads host and port from a config file** you edit:
  `/storage/emulated/0/GRAND CITY RP/SAMP/settings.ini` (`host = ...`, `port = ...`, `name = ...`)
- **Storage path forced to `/storage/emulated/0/GRAND CITY RP/`** — put all your CRMP data
  (models, maps, settings.ini, ...) in that folder.
- **ShadowHook** is kept (native + `com.bytedance.android:shadowhook:1.0.9`).
- `LoginActivity` is the app entry; tapping **Login** launches the `SAMP` game client which
  connects using host/port/name from `settings.ini`.

## First-time setup

1. Install the release APK.
2. Create/edit `/storage/emulated/0/GRAND CITY RP/SAMP/settings.ini`:

   ```ini
   [client]
   host = YOUR_SERVER_IP
   port = 7777
   name = YourNickName
   ```

3. Grant storage permissions (Android 11+: "All files access" for the GRAND CITY RP folder).
4. Open app → enter username → **Login**.

If it crashes, send the logcat/crash log.

## Build (release APK)

This sandbox does not have the Android SDK/NDK, so the APK is **not** built here. Build on a
machine with Android SDK + NDK `26.2.11394342` + JDK:

```bash
export ANDROID_HOME=/path/to/android-sdk
export ANDROID_NDK_HOME=$ANDROID_HOME/ndk/26.2.11394342
chmod +x gradlew
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

Signing: `app/build.gradle` is configured to sign with `../../keys/new.jks`
(pass `123123`, alias `key0`). Provide that keystore or point `signingConfigs` at your own.

## Repository

Source code is stored here; push the release APK (after building it) to the repo as well.
