# LOVELY-GCRP

GRAND CITY RP — simplified CRMP / SA-MP Mobile client (built on SA-MP Mobile v2.10).

## Base

Ported from [kuzia15/SAMP-Mobile](https://github.com/kuzia15/SAMP-Mobile.git), branch `GTA-2.10`
(SA-MP 2.10: ARM32 + ARM64, **ShadowHook** integration).

## What this build does

- **Simple client UI only** (no launcher / server-list UI):
  - Username typing box
  - Connect button — saves username to `settings.ini` and connects to the server
    (host/port read from `settings.ini`)
  - Logs button — shows the client log
- **Reads host and port from a config file** you edit:
  `/storage/emulated/0/GRAND CITY RP/SAMP/settings.ini` (`host = ...`, `port = ...`, `name = ...`)
- **Storage path forced to `/storage/emulated/0/GRAND CITY RP/`** — put all your CRMP data
  (models, maps, settings.ini, ...) in that folder.
- **ShadowHook** is kept (native + `com.bytedance.android:shadowhook:1.0.9`).
- `MainActivity` is the app entry; tapping **Connect** launches the `SAMP` game client
  which connects using host/port/name from `settings.ini`.

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
4. Open app → enter username → **Connect**.

If it crashes, send the logcat / client log.

## Build

See `CONFIG.md`. There are two options:

- **Local:** build with Android SDK + NDK `26.2.11394342` + JDK:

  ```bash
  export ANDROID_HOME=/path/to/android-sdk
  export ANDROID_NDK_HOME=$ANDROID_HOME/ndk/26.2.11394342
  chmod +x gradlew
  ./gradlew assembleRelease
  ```

  Output: `app/build/outputs/apk/release/app-release.apk`

- **GitHub Actions:** add `.github/workflows/release.yml` yourself (see `CONFIG.md`
  for the exact content) — the repo does not include it because the push token lacks
  `workflows` permission.

The signing keystore is not part of the repo, so CI yields an unsigned APK unless you
provide your keystore; build locally with your keystore for a signed installable APK.

```bash
export ANDROID_HOME=/path/to/android-sdk
export ANDROID_NDK_HOME=$ANDROID_HOME/ndk/26.2.11394342
chmod +x gradlew
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

## Repository

Source code is stored here; push the release APK (after building/signing it) to the
branch as well.
