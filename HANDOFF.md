# Developer Handoff — Average Speed Colour

**Current version:** 2.0  
**GitHub repo:** https://github.com/j4m1eb/Karoo-Average-speed-colour  
**Owner:** Jamie Bishop (@j4m1eb)  
**Last updated:** May 2026

---

## What This App Does

A Karoo cycling computer extension that shows current speed colour-coded against a reference speed (ride average, lap average, or a user-defined target). At a glance the rider can see if they're above, below or on pace.

- 🟢 **Green** = above target/average
- 🔴 **Red** = below target/average
- ⬜ **No colour** = within ±1 of target/average (at pace)

The app provides 5 data fields the rider adds to their Karoo data pages.

---

## Repository Structure

```
karoo-colorspeed/
├── app/
│   ├── build.gradle.kts              # Version, dependencies, manifest generation task
│   ├── manifest.json                 # Auto-generated on build — do not edit manually
│   └── src/main/
│       ├── AndroidManifest.xml       # MANIFEST_URL meta-data is critical — must match repo name
│       ├── kotlin/com/j4m1eb/averagespeedcolour/
│       │   ├── ColorApplication.kt   # App entry point, Koin DI init
│       │   ├── MainActivity.kt       # Shows WelcomeScreen then MainScreen
│       │   ├── data/
│       │   │   ├── ColorSpeedView.kt         # MAIN WIDGET COMPOSABLE — all rendering logic
│       │   │   ├── ConfigData.kt             # Settings data class
│       │   │   ├── ArrowProvider.kt          # Direction arrow icons (6 levels)
│       │   │   ├── CurrentVsRideAverageSpeed.kt   # Data type: speed vs ride avg
│       │   │   ├── CurrentVsLapAverageSpeed.kt    # Data type: speed vs last lap
│       │   │   ├── CurrentLapVsLLAverageSpeed.kt  # Data type: current lap vs last lap
│       │   │   ├── SpeedVsTargetColorSpeed.kt     # Data type: speed vs target
│       │   │   └── LapVsTargetColorSpeed.kt       # Data type: lap vs target
│       │   ├── extension/
│       │   │   └── ColorSpeedExtension.kt    # Karoo extension entry point, registers data types
│       │   ├── managers/
│       │   │   └── ConfigurationManager.kt   # DataStore persistence for settings
│       │   └── screens/
│       │       ├── MainScreen.kt             # Settings UI (Compose)
│       │       └── WelcomeScreen.kt          # Colour key splash shown on every launch
│       └── res/
│           ├── drawable/
│           │   ├── icon_gauge.xml            # Gauge vector icon (used in fields)
│           │   ├── icon_avg_pace.xml         # Avg pace icon (used when showIcons=false)
│           │   ├── ic_launcher_foreground.xml # Green gauge for app launcher icon
│           │   ├── ic_launcher_background.xml # Dark navy background for launcher icon
│           │   └── equals_24px.xml           # Two bars for at-average state
│           ├── mipmap-anydpi-v26/
│           │   ├── ic_launcher.xml           # Adaptive launcher icon
│           │   └── ic_launcher_round.xml     # Round adaptive launcher icon
│           ├── values/colors.xml             # dark_green (#1EFF00), teal, dark_red etc
│           ├── values/strings.xml            # All UI strings
│           └── xml/extension_info.xml        # Karoo extension metadata + data type IDs
├── .github/workflows/
│   ├── android.yml                   # Release build — triggered by *[0-9]-release tags
│   └── pre-release.yml               # Pre-release build — triggered on every push to main
└── gradle.properties                 # NOTE: org.gradle.java.home was removed — do NOT re-add it
```

---

## Key Files to Understand

### `ColorSpeedView.kt`
The heart of the app. A Glance (RemoteViews) composable that renders the speed field on the Karoo.

**Layout logic:**
- Detects double-width by checking `config.viewSize.first > 400`
- **Single width:** Column with small header row (icon + reference speed) and large current speed below
- **Double width:** Column with small header (gauge icon + "AVG SPEED COLOUR" label) and two large numbers side by side
- `colorConfig.swapRows` flips which value is on top/left vs bottom/right

**Colour logic:**
- Stopped (≤ 2 km/h): transparent background
- `speedDiff > 1.0`: green (`#1EFF00`) or teal if `useTeal=true`, black text
- `speedDiff < -1.0`: dark red, white text
- Otherwise: transparent, default text

**Icon alignment note:** The Material Symbol icons sit high within their bounding box due to the flipped Y-axis viewBox (`viewBox="0 -960 960 960"`). They require a `<group android:translateY="960">` wrapper in the XML and `padding(top = 4.dp)` in the composable for optical alignment.

### `ConfigData.kt`
```kotlin
data class ConfigData(
    val targetSpeed: Double,       // in m/s internally, converted to mph/kmh in UI
    val useTeal: Boolean = false,  // swap green for teal when above pace
    val showIcons: Boolean = true, // show direction arrows vs gauge icon
    val hasSeenWelcome: Boolean = false,
    val swapRows: Boolean = false, // flip position of current/average values
)
```

### `AndroidManifest.xml` — IMPORTANT
Contains this critical meta-data:
```xml
<meta-data
    android:name="io.hammerhead.karooext.MANIFEST_URL"
    android:value="https://github.com/j4m1eb/Karoo-Average-speed-colour/releases/latest/download/manifest.json" />
```
This URL is **baked into the APK** and is how the Karoo device finds the download. If the repo is ever renamed, this must be updated too, and a new release built.

### `extension_info.xml` — IMPORTANT
The `id` and `typeId` values are **permanent identifiers** used by the Karoo to track installed fields. Do NOT change them — doing so would break existing user setups, appearing as a new/unknown extension.
```xml
id="karoocolorspeed"
typeId="currentcolorspeed"
typeId="lapscolorspeed"
typeId="avgcolorspeed"
typeId="speedvstarget"
typeId="lapvstarget"
```

### `build.gradle.kts`
- `projectName = "Karoo-Average-speed-colour"` — must match the GitHub repo name exactly (used in manifest.json APK URL)
- `projectDeveloper = "j4m1eb"` — GitHub username
- `generateManifest` task auto-generates `app/manifest.json` on every build
- Java toolchain set to 21

---

## CI / CD

### GitHub Actions Workflows

**`android.yml` — Release Build**
- Triggers on tags matching `*[0-9]-release` (e.g. `1.2-release`, `2.0-release`)
- Decodes keystore from `KEYSTORE_BASE64` secret, builds signed APK
- Creates a GitHub Release with `app-release.apk` and `manifest.json` attached
- Also triggers on pushes to `master` branch (currently unused — main branch is `main`)

**`pre-release.yml` — Pre-Release Build**
- Triggers on every push to `main` (and any non-master branch)
- Builds APK but does NOT create a release — useful for verifying builds pass

### Required GitHub Secrets
All stored at: https://github.com/j4m1eb/Karoo-Average-speed-colour/settings/secrets/actions

| Secret | Purpose |
|---|---|
| `GPR_KEY` | Personal Access Token with `read:packages` scope — downloads Karoo SDK from GitHub Packages (hammerheadnav). **Set to no expiry.** Needs renewing if regenerated. |
| `KEYSTORE_BASE64` | Base64-encoded release keystore — ensures consistent APK signing across builds |
| `KEYSTORE_PASSWORD` | `karoo2024release` |
| `KEY_ALIAS` | `karoo-colorspeed` |
| `KEY_PASSWORD` | `karoo2024release` |

**Critical:** The keystore file itself (`release.keystore`) is stored locally on Jamie's Mac at `/Users/jamiebishop/Documents/karoo-colorspeed/release.keystore` and is gitignored. If it is lost and a new keystore is generated, all existing installs will have a signature conflict and users will need to uninstall before reinstalling. **Keep the keystore backed up.**

---

## How to Release a New Version

1. Make code changes on `main` branch
2. Bump version in `app/build.gradle.kts`:
   ```kotlin
   versionCode = 10200   // increment by 1 or 100
   versionName = "1.2"
   ```
3. Commit and push to `main`
4. Tag the release:
   ```bash
   git tag 1.2-release
   git push origin 1.2-release
   ```
5. Watch the **Release Build** workflow at https://github.com/j4m1eb/Karoo-Average-speed-colour/actions
6. Once green (~7 minutes), the APK is live at https://github.com/j4m1eb/Karoo-Average-speed-colour/releases/latest

---

## Local Development Setup

**Requirements:**
- Android Studio (latest stable)
- JDK 21 (configured via Android Studio's bundled JDR)
- Git

**First time setup:**
```bash
git clone https://github.com/j4m1eb/Karoo-Average-speed-colour.git
cd Karoo-Average-speed-colour
```

Create `local.properties` in the project root with:
```
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_GITHUB_PAT_WITH_READ_PACKAGES
```

This is needed to download the Karoo SDK from GitHub Packages (hammerheadnav). The same token used for `GPR_KEY` in CI works here.

**Do NOT add `org.gradle.java.home` to `gradle.properties`** — this was a hardcoded Mac path that broke CI. Android Studio sets the JDK automatically.

---

## Installing on Karoo for Testing

**Karoo 3 (recommended):**
1. On your phone, go to the [latest release](https://github.com/j4m1eb/Karoo-Average-speed-colour/releases/latest)
2. Tap `app-release.apk`
3. Use the iOS/Android **share sheet** to share the file to the **Hammerhead companion app**
4. The companion app pushes it to the Karoo — reboot when prompted

**Karoo 2 (ADB):**
```bash
adb install app-release.apk
```

---

## Technology Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| Settings UI | Jetpack Compose (Material3) |
| Widget rendering | Jetpack Glance (RemoteViews) |
| Settings persistence | Android DataStore Preferences |
| Dependency injection | Koin |
| Logging | Timber |
| Karoo integration | Hammerhead Karoo Extension SDK |
| Build | Gradle (Kotlin DSL) |
| Async | Kotlinx Coroutines |

---

## Known Quirks & Notes

- **Unit conversion:** The Karoo SDK delivers all speed values in **m/s**. The app converts to mph (`× 2.23694`) or kmh (`× 3.6`) based on the user's Karoo preference. The `targetSpeed` in `ConfigData` is always stored in m/s internally.
- **±1 threshold:** The "at pace" zone is ±1 in the user's chosen units (mph or kmh). This is not configurable.
- **Double-width detection:** Uses `config.viewSize.first > 400` pixels. This has not been tested on all Karoo screen sizes — if the threshold needs adjusting, this is the value to change in `ColorSpeedView.kt`.
- **Icon Y-axis:** Material Symbol icons use `viewBox="0 -960 960 960"` which flips the coordinate system. All custom icons require a `<group android:translateY="960">` wrapper in the XML drawable, and `padding(top = 4.dp)` in the composable to optically align with adjacent text.
- **Extension ID is permanent:** `karoocolorspeed` cannot be changed without breaking existing user installations.

---

## Planned Next Work

- **W' Balance Extension** — A separate new Karoo extension showing W' (W-prime) balance with a target pacing curve for criterium racing and time trials. Planned as a new repo. See project notes in `~/.claude/projects/memory/project_wprime_extension.md`.
