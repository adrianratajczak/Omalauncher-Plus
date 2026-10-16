# Omalauncher+ — nasze customowe rzeczy

Fork upstreamu `simonschubert/omalauncher`, branch `omalauncher-plus`.
Wszystkie nasze ustawienia żyją w jednym miejscu, żeby nie kolidować
z upstreamem: stan w `app/.../ui/Plus.kt` (`PlusPrefs`), ekran
w `app/.../ui/PlusScreen.kt`, zapis w SharedPreferences `"launcher"`
pod kluczami `plus_*`.

Aktualne grupy ekranu omalauncher+: **icons → day & night → home →
drawer → labels → search → widgets → notifications → corners**.

## Ikony

- **Icon packi** (`ui/IconPacks.kt`, `ui/IconPackScreen.kt`) — wsparcie
  third-party packów (akcje Nova, ADW, GO, Apex, Tesla, Fede, Motorola,
  Sony). Parsowany `appfilter.xml`: mapowania komponent→drawable, scale,
  warstwy iconback/iconmask/iconupon. Wybór packa przerysowuje wszystkie
  ikony.
- **Rozmiar ikon** (`ui/IconSize.kt`, `ui/IconSizeScreen.kt`) — slot
  small 0.8x / normal 1x / large 1.25x. Skaluje drawer, strip, menu
  skrótów i dialog przypinania; ikony renderują się od nowa w nowym
  rozmiarze.
- **Kształt** — system / koło / squircle (`IconShape`).
- **Adaptive background** on/off — off rysuje same logo bez backgroundu.
- **Grayscale** — ikony odbarwione.
- **Tint** — off / custom / theme accent. Custom to jeden kolor (mnoży)
  albo gradient top→bottom (duotone, per-pixel, z zachowaniem światła);
  swatche + pole `#rrggbb`. Theme bierze accent i przeaplikowuje przy
  każdej zmianie motywu (`Runtime.themes.onApplied`).
- **Labelki osobno** — `hide home labels` (kafelki, `ShortcutTile`)
  i `hide drawer labels` (`AppCell`) zamiast jednego wspólnego.

## Day & night

Sekcja **day & night** (`Runtime.syncDayNight`, `PlusPrefs.wantedTheme`):
wybór theme na dzień i na noc (`none` = zostawia to co jest) plus godziny
przełączenia (0–23, zakres dnia może przechodzić przez północ).
Przełączenie następuje przy powrocie na home (`MainActivity.onResume`)
i co minutę, gdy home jest widoczny (ticker w `Runtime.startDayNight`),
więc godzina przełączenia łapie też w trakcie używania. Motyw spoza
dysku aplikuje bundled kolory i dociąga resztę jak ręczny wybór.
Przy włączonym auto ręczna zmiana w style.toml przegra z harmonogramem.

## Home / ostatnie aplikacje

- **recent apps** on/off — chowa cały strip (portret i boczny rail
  w landscape); wyłączony zwalnia też jego miejsce, siatka nie zostawia
  pustego paska (`showRecents()` = theme AND Plus).
- **hide from recents** — long-press na apce w stripie dokleja pozycję
  do menu; apka znika ze stripa od razu (filtr na `usage.recent`,
  historia użyć nietknięta — po odkryciu wraca).
- Lista ukrytych w sekcji **drawer**: tap przywraca, pusta pokazuje
  podpowiedź o long-pressie, odinstalowane widać po kluczu.

## Drawer

- **Letter rail jak w Niagari** — alfabet na prawej krawędzi, tap/drag
  filtruje apki do litery, ★ czyści (`drawerRail`, `LetterRail`,
  `filterRail`).
- **Corner letter** — wybrana litera siedzi w lewym górnym rogu, tap
  czyści; duża litera na środku, gdy palec jedzie po railu.
- **Bottom pinning** — przefiltrowany bucket dokleja się do dołu, nad
  bar, pod kciuk; treść przepakowywana w pełne rzędy, żeby nie było
  dziur po odfiltrowanych apkach.
- **align apps right** — przełącznik w sekcji drawer; puste odstępy idą
  przed ikonami, apki tulą się do raila.
- Wyniki szukania rosną od dołu (reverseLayout), najlepszy tuż nad barem.

## Bar i search

- Nad barem akcje **search store / search web** zamiast wiersza ask;
  Enter pyta agenta domyślnie, strzałki/Tab chodzą po apps/store/web/ask.
- Ekran search: silnik webu (Google, DuckDuckGo, Bing albo własny URL
  z `{q}`) i sklep (Play lub F-Droid).

## Gesty

- Swipe up otwiera drawer, **swipe down rozwija notification shade**
  jak w stock launcherach (`ui/Gestures.kt`, `ui/StatusBar.kt`).

## Lockscreen (osobny moduł `lockscreen/`)

Drugi APK (`dev.omalauncher.plus.lockscreen`): zegar nad systemowym
lock screenem, foreground service na screen-off, dokładane bloki —
clock, date, battery, actions (`Blocks.kt`, `LockActivity.kt`,
`LockService.kt`, `SetupActivity.kt`).

## Powiadomienia

- **hide linux notification** — cicha notyfikacja sesji Linux (pusty
  `ic_notification_empty`), instalacje i błędy dalej widać.

## Wygląd

- **Corners** — theme albo custom 0–60px sliderem.
- **Blurred background** — frost per-kafelek z tapety (Android 12+).
- **Frame glow** — potrójny gasnący ring z jasnym rdzeniem zamiast
  płaskiego (`Theme.kt` `frame()`/`tile()`).

## Usunięte decyzją

- **Kaskady enter** (home + drawer) — działały nierówno na różnych
  kaflach, wywalone w całości.

## Klucze `plus_*` w SharedPreferences `"launcher"`

`plus_hide_linux_notification`, `plus_corners`, `plus_hide_labels`
(legacy, migruje do dwóch nowych), `plus_hide_home_labels`,
`plus_hide_drawer_labels`, `plus_icon_shape`, `plus_grayscale_icons`,
`plus_icon_tint`, `plus_icon_tint_bottom`, `plus_tint_theme`,
`plus_adaptive_background`, `plus_widget_blur`,
`plus_drawer_align_right`, `plus_show_recents`, `plus_recent_hidden`,
`plus_day_night_auto`, `plus_day_theme`, `plus_night_theme`,
`plus_day_hour`, `plus_night_hour`.
