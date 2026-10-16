<div align="center">

# Omalauncher+

**An Omarchy-style launcher on steroids.** A fork of
[SimonSchubert/omalauncher](https://github.com/SimonSchubert/omalauncher)
(upstream **0.7.2**) — everything upstream does, plus the whole list below.
It installs **side by side** with the original (`dev.omalauncher.plus`).

[![Download APK](https://img.shields.io/badge/Download-APK-7aa2f7?style=for-the-badge&logo=android)](https://github.com/adrianratajczak/Omalauncher-Plus/releases)
[![Po polsku](https://img.shields.io/badge/Czytaj-po_polsku-9ece6a?style=for-the-badge)](README.plus.pl.md)
[![Project page](https://img.shields.io/badge/Project-page-bb9af7?style=for-the-badge)](omalauncher-plus.html)

![branch](https://img.shields.io/badge/branch-omalauncher--plus-7aa2f7)
![upstream](https://img.shields.io/badge/upstream-0.7.2-565f89)
![android](https://img.shields.io/badge/android-10%2B-9ece6a)
![license](https://img.shields.io/badge/license-MIT-565f89)

</div>

## ⬇️ Install

1. Download the APK from [**Releases**](https://github.com/adrianratajczak/Omalauncher-Plus/releases)
   (`omalauncher-plus-*.apk`, also committed under [`releases/`](releases/)).
2. Install it and set Omalauncher+ as the default launcher.

No root, no account, no ads. Releases are signed with a debug key —
fine for sideloading. Requires arm64 + Android 10+.

## ✨ What's new in this fork

### 🌗 Day & night themes

`omalauncher+ › day & night` — a separate theme for day and night
(`none` keeps whatever shows) plus the switch hours. It flips on every
return home and every minute while home is visible.

### 🕘 Recent apps, your way

The strip above the bar can be **turned off entirely** (its room goes
back to the grid), and any app goes **long-press → hide from recents** —
gone immediately, usage history kept. Hidden ones wait in
`omalauncher+ › drawer`, one tap brings them back.

### 👍 Thumb-friendly drawer

- **Niagara-style letter rail** — alphabet on the right edge, drag filters,
  ★ clears, the letter parks in the corner.
- The bucket **pins to the bottom**, above the bar, in full rows with no gaps.
- **Align apps right** hugs rows to the rail. Search results grow bottom-up,
  best match right above the bar.

### 🎨 Icons without limits

- **Third-party icon packs** (Nova, ADW, GO, Apex, Tesla, Fede, Motorola,
  Sony) — mappings, scale, iconback/mask/upon layers from `appfilter.xml`.
- **Size** 0.8x / 1x / 1.25x, **shape** system/circle/squircle,
  **adaptive background**, **grayscale**.
- **Tint**: off / custom / theme accent; custom is a color or a duotone
  top→bottom gradient, swatches + `#rrggbb`.
- **Labels separately** for home and the drawer.

### 🔍 A bar that also searches

**Search store / search web** rows above the bar (Enter asks the agent);
web engine (Google, DuckDuckGo, Bing, custom `{q}` URL) and store
(Play / F-Droid) are configurable.

### 👇 Swipe down is the shade

Swipe down anywhere on home pulls down Android's notification shade, like
stock launchers (both directions configurable in settings › gestures).

### ⚙️ Everything in one place

`omalauncher+` keeps its extras away from upstream's rows, grouped as
**icons → day & night → home → drawer → labels → search → widgets →
notifications → corners**: icon pack and icon size screens, icon shape
(system, circle, squircle), adaptive background on/off, grayscale, and
tint (off, custom colors with an optional top-to-bottom duotone gradient,
or the theme's accent, reapplied on every theme change); the day/night
switcher; the recent apps strip toggle; drawer row alignment plus the
hidden-recents list; labels on/off separately for home and drawer;
a switch hiding the Linux session notification (gone while idle, still
shows installs and errors); a corner radius slider (theme, or 0 to 60px
over the whole launcher); and a blurred widget background switch
(per-tile frost sampling the wallpaper). Long-pressing home also offers
settings next to the themes.

### 🔒 Lockscreen (`lockscreen/`) — soon

> 🚧 Work in progress: the lockscreen module ships in this repo but is
> still being built; expect rough edges.

A second app and APK in this repo: `dev.omalauncher.plus.lockscreen`, a
clock over the system lock screen in the launcher's monospace voice.
A foreground service watches for screen-off and raises it with a
full-screen intent; swipe up dismisses the keyguard (with a PIN set, the
system's PIN pad shows — the lockscreen never sees credentials). Blocks
are added and removed in its setup: clock, date, battery level, and
flashlight/camera shortcuts. No Linux needed.

> Upstream PRs, each with only its feature: swipe down →
> [#119](https://github.com/SimonSchubert/omalauncher/pull/119),
> icon packs + icon size →
> [#121](https://github.com/SimonSchubert/omalauncher/pull/121).

## 📱 The base: upstream

An [Omarchy](https://omarchy.org)-style home screen plus optional Arch Linux
ARM next to the phone apps: rootless proot, Wayland, Linux windows as their
own Recents tasks. 22 Omarchy themes with wallpapers, OWL widgets + their
store, pacman/AUR, terminals, Mali GPU, coding agents from the bar.

![The home screen in twelve Omarchy themes, each with different widgets](art/collage.png)

<p>
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/01.png" width="24%" alt="Screenshot">
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/02.png" width="24%" alt="Screenshot">
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/03.png" width="24%" alt="Screenshot">
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/04.png" width="24%" alt="Screenshot">
</p>

More: [README.plus.en.md](README.plus.en.md) (same in
[Polish](README.plus.pl.md)), page [omalauncher-plus.html](omalauncher-plus.html),
dev inventory [PLUS.md](PLUS.md).

## ▶️ Using it

The bar: the one input at the bottom of the home screen opens apps and asks
coding agents. Focusing it opens the list of Linux and Android apps; typing
narrows the list. Enter opens the highlighted app, searches the highlighted
row's target, or sends the text to the agent; Tab and the arrow keys move the
highlight. Short queries that name an app open it, sentences go to the agent.
Tap the agent's name to pick another installed one; the choice is saved in
`~/.config/omalauncher/agent`. Hold the mic to talk and release to send, or tap
it to dictate into the field. Speech is recognized by Android (on the device
when the phone supports it), so only text reaches Linux.

Touch input in Linux windows: tap is a left click, long press is a right
click, drag scrolls; apps with touch support (foot, GTK) get real touch.
Hardware keyboard shortcuts: Super+Enter (terminal), Super+Space or
Super+A (the bar), Super+1..9 (home pages) on the home screen; Super+Enter
and Super+W (close) in Linux windows.

## 🔨 Building

```sh
./scripts/fetch-proot.sh          # Termux's proot -> app/src/main/jniLibs (needs bsdtar and NDK 30.0.16248370)
./gradlew :app:installDebug
# optional: skip the 830 MB download on the device
adb push ArchLinuxARM-aarch64-latest.tar.gz /sdcard/Android/data/dev.omalauncher.plus/files/
```

`fetch-proot.sh` is required: without `app/src/main/jniLibs/arm64-v8a`
(`libproot.so` and friends) the APK builds but Linux can't start (`Cannot run
program ... libproot`). The directory is git-ignored; every fresh checkout
needs the script before building.

Tests: `./gradlew :owl:test :app:testDebugUnitTest`. `./gradlew :owl:owlJar`
builds `owl/build/cli/owl.jar`, the checker the widget store's CI runs.

In debug builds you can launch guest commands with
`adb shell am start -n dev.omalauncher.plus/.MainActivity --es exec foot`.

- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md): the compositor, rendering, GPU
  acceleration, and how updates reach existing installs
- [docs/RELEASING.md](docs/RELEASING.md): Play releases and store assets
- [app/src/main/assets/widgets/README.md](app/src/main/assets/widgets/README.md):
  the widget language

## 🗂️ Layout

- `app/`: the Android app (Kotlin, Jetpack Compose, and the JNI bridge in `src/main/cpp`)
- `owl/`: the widget language and the `owl` command line, plain Kotlin
- `art/`: SVG masters for the app, agent and store icons
- `store/`: Play listing sources (screenshot frames, privacy policy)
- `fastlane/`: the Play listing and the upload lanes
- `scripts/`: fetching proot and theme colors, rendering icons, screenshots and widget previews

## 📄 License

MIT, see [LICENSE](LICENSE), for Omalauncher's own code and artwork. The
coding agents' logos, the symbol font and Omarchy's theme colors are not
covered; [THIRD_PARTY.md](THIRD_PARTY.md) lists them with their sources and
licenses. The app also ships proot, talloc and libandroid-shmem (GPL-2.0,
LGPL-3.0 and BSD-3-Clause, prebuilt by Termux), listed in the app under
settings › licenses.

Omalauncher is an independent project and is not affiliated with or endorsed
by Omarchy, Arch Linux, or the makers of the coding agents it installs. This
fork is not affiliated with or endorsed by the upstream Omalauncher project.
