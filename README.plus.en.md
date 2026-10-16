# omalauncher+

A customized fork of [omalauncher](https://github.com/SimonSchubert/omalauncher)
(branch `omalauncher-plus`) — an Omarchy-style Android launcher with icon
packs, day/night themes, a fast Niagara-like drawer and one settings screen
for everything custom.

## Install

Grab the APK from **Releases** (`omalauncher-plus-*.apk`), install it and
set it as the default launcher. No root, no extra apps required.

## What's custom

All settings live on one **omalauncher+** screen, grouped as
icons → day & night → home → drawer → labels → search → widgets →
notifications → corners.

### Icons

- **Icon packs** — third-party packs (Nova, ADW, GO, Apex, Tesla, Fede,
  Motorola, Sony actions); `appfilter.xml` mappings, scale and
  iconback/mask/upon layers.
- **Icon size** — small 0.8x / normal 1x / large 1.25x.
- **Shape** — system / circle / squircle; **adaptive background** on/off;
  **grayscale** on/off.
- **Tint** — off / custom / theme accent; custom is a single color or a
  top-to-bottom duotone gradient, with swatches and a `#rrggbb` field.
- **Labels** — hidden separately for home tiles and the drawer.

### Day & night

Pick a **day theme** and a **night theme** (`none` keeps whatever shows)
plus the switch hours (0–23, may wrap past midnight). The theme applies
when you come back home and re-checks every minute while home is visible.

### Home / recents

- **Recent apps** toggle — hides the whole strip (portrait and the
  landscape side rail) and frees its room.
- **Hide from recents** — long-press an app in the strip; it disappears
  immediately without losing usage history. Hidden apps are listed in
  omalauncher+ → drawer, tap to bring them back.

### Drawer

- **Letter rail** like Niagara's — tap or drag the alphabet on the right
  edge to filter by letter, ★ clears.
- The picked letter parks top-left (tap clears); the filtered bucket
  **pins to the bottom**, repacked into full rows, right above the bar.
- **Align apps right** pins rows to the rail; search results grow
  bottom-up with the best match above the bar.

### Bar & search

**Search store / search web** rows above the bar (Enter asks the agent);
web engine (Google, DuckDuckGo, Bing, custom `{q}` URL) and store
(Play / F-Droid) are configurable.

### Gestures, lockscreen, looks

- **Swipe up** opens the drawer, **swipe down** expands the notification
  shade.
- Separate **lockscreen** app (`dev.omalauncher.plus.lockscreen`) with
  addable clock / date / battery / action blocks.
- Hideable Linux notification, corner radius (theme or 0–60px),
  per-tile wallpaper frost (Android 12+), triple-ring frame glow.

## Notes

- Settings are stored under `plus_*` keys and never collide with upstream.
- Release APKs are signed with a debug key (no upload key in this repo),
  good for sideloading from Releases.
- Full dev-level inventory: [PLUS.md](PLUS.md).
