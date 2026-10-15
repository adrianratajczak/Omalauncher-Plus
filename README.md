# Omalauncher+

A fork of [SimonSchubert/omalauncher](https://github.com/SimonSchubert/omalauncher),
based on upstream **0.7.2** (versionCode 14). Everything upstream does, plus a
few features below. It installs **side by side** with the original: applicationId
`dev.omalauncher.plus`, label **Omalauncher+**.

Upstream PRs, each with only its feature: swipe down →
[#119](https://github.com/SimonSchubert/omalauncher/pull/119),
icon packs + icon size →
[#121](https://github.com/SimonSchubert/omalauncher/pull/121).

An Android home screen with the look of [Omarchy](https://omarchy.org), and an
optional Arch Linux ARM system next to your phone apps. Omalauncher runs the
system under proot, without root, and is its Wayland compositor: every Linux
window opens as its own Android task, with its own card in Recents.

![The home screen in twelve Omarchy themes, each with different widgets](art/collage.png)

<p>
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/01.png" width="24%" alt="Screenshot">
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/02.png" width="24%" alt="Screenshot">
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/03.png" width="24%" alt="Screenshot">
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/04.png" width="24%" alt="Screenshot">
</p>

- **Themes**: all 22 Omarchy themes, with their wallpapers, applied to the
  launcher and to the Linux apps, plus the community themes from
  [omarchy.org/themes](https://omarchy.org/themes/), or your own in
  `~/.config/omarchy/themes`
- **Widgets** written in OWL, a small declarative language you can edit on the
  phone, plus a [widget store](https://github.com/SimonSchubert/omarchy-widgets)
- **Linux, optional**: pacman and the AUR, Linux apps in their own windows,
  terminal widgets, a file manager, and opt-in GPU acceleration on Mali GPUs
- **Coding agents**: the bar at the bottom sends a prompt to Claude Code, Codex,
  Copilot, opencode and others, which open in a terminal
- No account, no ads, no tracking

Needs an arm64 phone with Android 10 or newer. Without Linux, the launcher still
has the themes, wallpapers, Android apps and the widgets that don't run commands.

## What's new in this fork

### Swipe down for notifications

Swiping down anywhere on home pulls down Android's notification shade, like
stock launchers. Both directions are configurable in settings › gestures:
swipe up and swipe down each open the app drawer, the notifications, or do
nothing. The original swipe-up drawer behavior is unchanged by default.

### Custom icon packs and icon size

Settings › icon pack lists third-party icon packs installed on the phone
(Nova, ADW, GO, Apex, Tesla, Fede, Motorola and Sony theme actions). Picking
one redraws every app icon from the pack: component mapping, scale, iconback,
iconmask and iconupon layers from the pack's `appfilter.xml` are all honored,
and the monochrome icon style still works off the pack's icons. Apps the pack
has nothing for keep their own icons.

Packs that draw small stay readable too: settings › icon size scales the icon
slot (small 0.8x, normal 1x, large 1.25x) in the drawer, the recent strip, the
shortcut menu and the pin dialog, and re-renders app icons at the new size.

### A bar that also searches

Typing in the bottom bar used to show one echo row (`ask <agent>: <text>`).
The echo is gone — Enter in the bar asks the agent by default — and two action
rows take its place above the bar, store over web:

- **search web** opens the query in the default browser,
- **search store** opens it in the Play app (its site when Play is absent) or
  on F-Droid.

The bar's button says what Enter does (`⏎ ask`, `⌕ web`, `⌕ store`, `▸ open`),
and Tab/arrows walk apps, store, web and the agent's ask. Settings › search
picks the web engine (Google by default, DuckDuckGo, Bing, or a custom URL with
`{q}` for the query) and the store (Play or F-Droid).

### Coding agent installs that work

npm-based agents (OpenCode, Grok, Kimi) install with plain `npm install -g`
after mise sets up Node, instead of mise's npm backend (current mise passes an
empty `--before` flag that fails the install). Install scripts are re-enabled
so packages whose binary comes from a postinstall (OpenCode) actually get it;
a broken copy left by an earlier attempt is reinstalled from scratch.

### Omalauncher+ settings

Settings › omalauncher+ holds this fork's own extras, away from upstream's
rows: the icon pack and icon size screens, a switch hiding the Linux session
notification (no status bar icon; progress stays in settings › linux), and
corner presets (theme, edge, soft, round, full) rounding every frame in the
launcher over what the theme sets.

### Lockscreen (`lockscreen/`)

A second app and APK in this repo: `dev.omalauncher.plus.lockscreen`, a clock
over the system lock screen in the launcher's monospace voice. A foreground
service watches for screen-off and raises it with a full-screen intent; swipe
up dismisses the keyguard (with a PIN set, the system's PIN pad shows — the
lockscreen never sees credentials). Blocks are added and removed in its setup:
clock, date, battery level, and flashlight/camera shortcuts. No Linux needed.

## Using it

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

## Building

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

## Layout

- `app/`: the Android app (Kotlin, Jetpack Compose, and the JNI bridge in `src/main/cpp`)
- `owl/`: the widget language and the `owl` command line, plain Kotlin
- `art/`: SVG masters for the app, agent and store icons
- `store/`: Play listing sources (screenshot frames, privacy policy)
- `fastlane/`: the Play listing and the upload lanes
- `scripts/`: fetching proot and theme colors, rendering icons, screenshots and widget previews

## License

MIT, see [LICENSE](LICENSE), for Omalauncher's own code and artwork. The
coding agents' logos, the symbol font and Omarchy's theme colors are not
covered; [THIRD_PARTY.md](THIRD_PARTY.md) lists them with their sources and
licenses. The app also ships proot, talloc and libandroid-shmem (GPL-2.0,
LGPL-3.0 and BSD-3-Clause, prebuilt by Termux), listed in the app under
settings › licenses.

Omalauncher is an independent project and is not affiliated with or endorsed
by Omarchy, Arch Linux, or the makers of the coding agents it installs. This
fork is not affiliated with or endorsed by the upstream Omalauncher project.
