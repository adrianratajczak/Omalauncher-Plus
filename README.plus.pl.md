# omalauncher+

Customowy fork launchera [omalauncher](https://github.com/SimonSchubert/omalauncher)
(branch `omalauncher-plus`) — launcher w stylu Omarchy na Androida, z packami
ikon, motywami dzień/noc, szybkim drawerem jak w Niagarze i jednym ekranem
ustawień do wszystkiego.

## Instalacja

Pobierz APK z zakładki **Releases** (`omalauncher-plus-*.apk`), zainstaluj
i ustaw jako domyślny launcher. Bez roota, bez dodatkowych aplikacji.

## Co jest nasze

Wszystko siedzi na jednym ekranie **omalauncher+**, w grupach:
icons → day & night → home → drawer → labels → search → widgets →
notifications → corners.

### Ikony

- **Icon packi** — packi third-party (akcje Nova, ADW, GO, Apex, Tesla,
  Fede, Motorola, Sony); mapowania z `appfilter.xml`, scale i warstwy
  iconback/mask/upon.
- **Rozmiar** — small 0.8x / normal 1x / large 1.25x.
- **Kształt** — system / koło / squircle; **adaptive background** on/off;
  **grayscale** on/off.
- **Tint** — off / custom / accent motywu; custom to jeden kolor albo
  gradient top→bottom (duotone), swatche plus pole `#rrggbb`.
- **Labelki osobno** — ukrywanie podpisów niezależnie dla home i drawera.

### Dzień i noc

Wybór **motywu dziennego i nocnego** (`none` zostawia to co jest) plus
godziny przełączenia (0–23, zakres może przechodzić przez północ).
Motyw aplikuje się przy powrocie na home i jest sprawdzany co minutę,
gdy home jest widoczny.

### Home / ostatnie

- Przełącznik **recent apps** — chowa cały strip (portret i boczny rail
  w landscape) i zwalnia jego miejsce.
- **Hide from recents** — long-press na apce w stripie; znika od razu,
  historia użyć zostaje. Ukryte apki widać w omalauncher+ → drawer,
  tap przywraca.

### Drawer

- **Letter rail jak w Niagari** — alfabet na prawej krawędzi, tap/drag
  filtruje po literze, ★ czyści.
- Wybrana litera parkuje w lewym górnym rogu (tap czyści), a bucket
  **dokleja się do dołu**, przepakowany w pełne rzędy, tuż nad barem.
- **Align apps right** przykleja wiersze do raila; wyniki szukania rosną
  od dołu, najlepszy tuż nad barem.

### Bar i search

Wiersze **search store / search web** nad barem (Enter pyta agenta);
silnik webu (Google, DuckDuckGo, Bing, własny URL z `{q}`) i sklep
(Play / F-Droid) do wyboru.

### Gesty, lockscreen, wygląd

- **Swipe up** otwiera drawer, **swipe down** rozwija notification shade.
- Osobna apka **lockscreen** (`dev.omalauncher.plus.lockscreen`)
  z dokładanymi blokami zegar / data / bateria / akcje.
- Wyciszana notyfikacja Linuxa, rogi (theme albo 0–60px), frost
  per-kafelek z tapety (Android 12+), potrójny ring glow na ramkach.

## Uwagi

- Ustawienia trzymane są pod kluczami `plus_*`, bez kolizji z upstreamem.
- Release APK podpisane jest kluczem debug (brak upload key w repo) —
  do instalacji z Releases w zupełności wystarcza.
- Pełna inwentaryzacja deweloperska: [PLUS.md](PLUS.md).
