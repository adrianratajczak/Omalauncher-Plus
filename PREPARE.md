# Prepare (build & release) checklist for Omalauncher+

Agent: always do this before declaring a release ready.

## 1. Sync upstream
- `git fetch origin`
- Check new commits: `git log --oneline HEAD..origin/main`
- If any, merge: `git merge --no-ff origin/main`
  - Resolve conflicts, especially:
    - `app/build.gradle.kts` (versionCode/versionName, NDK)
    - `Runtime.kt` (linuxApps / LinuxAppCatalog)
    - `AppDrawer.kt`, `Agents.kt`, `Gestures.kt`, `PinShortcutDialog.kt`
  - Keep fork features: icon packs, day/night, drawer rail, tint, calculator.
  - Update `scripts/fetch-proot.sh` NDK_VERSION if needed (installed NDK is 28.2.13676358).

## 2. Build
- Debug: `ANDROID_HOME=... ./gradlew :app:assembleDebug --offline`
- Release: `ANDROID_HOME=... ./gradlew :app:assembleRelease --offline`

## 3. Package
- Copy APK: `cp app/build/outputs/apk/release/app-release.apk releases/omalauncher-plus-<ver>.apk`
- Bump version in `app/build.gradle.kts` and README badges.

## 4. Verify on device (if connected)
- `adb devices`
- `adb install -r releases/omalauncher-plus-<ver>.apk`
- `adb shell am start -n dev.omalauncher.plus/dev.omalauncher.MainActivity`

## 5. Commit & push
- `git add -A && git commit -m "Release omalauncher-plus <ver>"`
- `git push fork omalauncher-plus`
- If rejected due to workflow scope, use a token with `workflow` scope or push via GitHub CLI.

## 6. Docs
- Update README (upstream badge, What's new) and CHANGELOG.md.
