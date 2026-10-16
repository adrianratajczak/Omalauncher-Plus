<div align="center">

# Omalauncher+

**Launcher w stylu Omarchy, na sterydach.** Fork
[SimonSchubert/omalauncher](https://github.com/SimonSchubert/omalauncher)
(upstream **0.7.2**) — wszystko co upstream, plus cała lista niżej.
Instaluje się **obok** oryginału (`dev.omalauncher.plus`).

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

### 🌗 Motywy dzień / noc

`omalauncher+ › day & night` — osobny motyw na dzień i na noc
(`none` zostawia to co jest) plus godziny przełączenia. Zmiana sama
przy powrocie na home i co minutę, gdy home jest widoczny.

### 🕘 Ostatnie apki po Twojemu

Strip nad barem można **wyłączyć w całości** (miejsce wraca do siatki),
a każdą apkę **long-press → hide from recents** — znika od razu, historia
użyć zostaje. Ukryte czekają w `omalauncher+ › drawer`, tap przywraca.

### 👍 Drawer pod kciuk

- **Letter rail jak w Niagari** — alfabet na prawej krawędzi, drag filtruje,
  ★ czyści, litera parkuje w rogu.
- Bucket **dokleja się do dołu**, nad bar, w pełnych rzędach bez dziur.
- **Align apps right** przykleja wiersze do raila. Wyniki szukania rosną
  od dołu, najlepszy tuż nad barem.

### 🎨 Ikony bez limitów

- **Icon packi** third-party (Nova, ADW, GO, Apex, Tesla, Fede, Motorola,
  Sony) — mapowania, scale, warstwy iconback/mask/upon z `appfilter.xml`.
- **Rozmiar** 0.8x / 1x / 1.25x, **kształt** system/koło/squircle,
  **adaptive background**, **grayscale**.
- **Tint**: off / custom / accent motywu; custom to kolor albo gradient
  duotone top→bottom, swatche + `#rrggbb`.
- **Labelki osobno** dla home i drawera.

### 🔍 Bar, który też szuka

Wiersze **search store / search web** nad barem (Enter pyta agenta);
silnik webu (Google, DuckDuckGo, Bing, własny URL z `{q}`) i sklep
(Play / F-Droid) do wyboru.

### 👇 Swipe down = shade

Swipe w dół rozwija notification shade jak w stock launcherach
(kierunki konfigurowalne w settings › gestures).

### ⚙️ Everything in one place

`omalauncher+` trzyma swoje extras z dala od upstreamu, w grupach
**icons → day & night → home → drawer → labels → search → widgets →
notifications → corners**: packi i rozmiar ikon, tint, day/night, toggle
stripa, align + ukryte apki, labelki, wyciszana notyfikacja Linuxa,
rogi (theme albo 0–60px), frost per-kafelek z tapety.

### 🔒 Lockscreen (`lockscreen/`) — soon

> 🚧 Work in progress: the lockscreen module ships in this repo but is
> still being built; expect rough edges.

Druga apka w repo (`dev.omalauncher.plus.lockscreen`): zegar nad systemowym
lock screenem + dokładane bloki (clock, date, battery, akcje). Bez Linuxa.

> Upstream PRs, każdy tylko ze swoim ficzrem: swipe down →
> [#119](https://github.com/SimonSchubert/omalauncher/pull/119),
> icon packs + icon size →
> [#121](https://github.com/SimonSchubert/omalauncher/pull/121).

## 📱 The base: upstream

Ekran domowy w stylu [Omarchy](https://omarchy.org) + opcjonalny Arch Linux
ARM obok apiek z telefonu: proot bez roota, Wayland, okna Linuxa jako osobne
taski w Recents. 22 motywy Omarchy z tapetami, widgety w OWL + ich sklep,
pacman/AUR, terminale, GPU Mali, agenci kodujący z bara.

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
