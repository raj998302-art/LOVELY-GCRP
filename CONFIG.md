# GRAND CITY RP - CRMP / SA-MP Mobile Client (v2.10 base)

Simplified SA-MP Mobile 2.10 client (based on kuzia15/SAMP-Mobile branch `GTA-2.10`)
rebuilt as a lightweight client for CRMP (Grand City RP).

## Client UI (exactly 3 elements)

The app opens on a single screen with only:

1. **Username typing box** (`EditText`) — type your in-game name.
2. **Connect button** — saves the username to
   `/storage/emulated/0/GRAND CITY RP/SAMP/settings.ini` (`[client] name = ...`),
   then connects to the server using `host`/`port` from that same `settings.ini`.
3. **Logs button** — opens the client log view
   (`/storage/emulated/0/GRAND CITY RP/client.log`).

No launcher / server-list UI is included.

## Storage path

All game data is loaded from `/storage/emulated/0/GRAND CITY RP/`
(forced in native code, `cpp/samp/game/hooks.cpp`). Put your CRMP data there.

Host / port / name are read from
`/storage/emulated/0/GRAND CITY RP/SAMP/settings.ini` — edit it with your server
data before tapping Connect:

```ini
[client]
host = YOUR_SERVER_IP
port = 7777
name = YourNickName
```

(The app copies a template there on first launch if missing.)

On Android 11+ grant **"All files access"** so the app can read/write that folder.

## ShadowHook

Preserved (native + `com.bytedance.android:shadowhook:1.0.9`).

## Build the release APK

### Option A — GitHub Actions (add this yourself)

The workflow file is **not** in this repo (the push token lacks `workflows`
permission). Create `.github/workflows/release.yml` yourself with this content,
then push or run it manually from the Actions tab:

```yaml
name: Build Release APK

on:
  push:
    branches: [ arena/8eb20683-lovely-gcrp ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: 17
          distribution: temurin
      - name: Set up Android SDK / NDK
        uses: android-actions/setup-android@v3
        with:
          api-level: 34
          build-tools: 34.0.0
          ndk: 26.2.11394342
          cmake: 3.22.1
      - run: chmod +x gradlew
      - name: Build release APK
        run: ./gradlew assembleRelease
      - name: Upload release APK
        uses: actions/upload-artifact@v4
        with:
          name: app-release-apk
          path: app/build/outputs/apk/release/app-release.apk
          if-no-files-found: error
```

It builds `./gradlew assembleRelease` and uploads the APK as a workflow artifact.

Note: the signing keystore (`../../keys/new.jks`, pass `123123`, alias `key0`) is
**not** part of this repo. Without it, the CI build produces an unsigned APK.
To get a signed, installable APK either:
- add your keystore as a GitHub Actions secret and restore it in the workflow, or
- build locally (Option B) and sign it yourself.

### Option B — local build

Requires Android SDK, NDK `26.2.11394342`, JDK:

```bash
export ANDROID_HOME=/path/to/android-sdk
export ANDROID_NDK_HOME=$ANDROID_HOME/ndk/26.2.11394342
chmod +x gradlew
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

## First-time setup on your device

1. Install the APK.
2. Create `/storage/emulated/0/GRAND CITY RP/` and place `SAMP/settings.ini` with
   your `host`/`port`/`name`.
3. Grant storage permissions ("All files access" on Android 11+).
4. Open app → type username → **Connect**.

If it crashes, send the logcat / client log and I'll adjust the build.
