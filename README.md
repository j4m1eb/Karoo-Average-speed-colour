# Average Speed Colour

A Karoo cycling computer extension that displays your current speed with live colour-coded feedback — instantly showing whether you're above, below, or on pace at a glance.

---

## How It Works

The speed field changes colour in real time based on how your current speed compares to a reference:

| Colour | Meaning |
| :--- | :--- |
| 🟢 **Green** | Above target / average |
| 🔴 **Red** | Below target / average |
| ⬜ **No colour** | At target / average (within ±1) |

The header row shows the reference speed (average or target) alongside a small icon, and the large number shows your current speed — so you always know what you're being compared against.

---

## Data Fields

Add one or more of these fields via the Karoo data field editor:

| Field | Description |
| :--- | :--- |
| **Speed vs. Ride Average** | Current speed vs. your overall ride average |
| **Speed vs. Last Lap** | Current speed vs. last lap's average |
| **Current Lap vs. Last Lap** | This lap's average vs. the previous lap's average |
| **Speed vs. Target** | Current speed vs. a custom target speed you set |
| **Lap vs. Target** | Current lap average vs. your custom target speed |

---

## Configuration

Tap the app icon on your Karoo to open settings:

- **Target Speed** — Set your desired pace (shown in mph or kmh based on your Karoo preference). Used by the "vs. Target" fields.
- **Use Teal** — Swap the above-pace green for a teal colour if you prefer.
- **Show Icons** — Toggle direction arrows on/off. When off, a small gauge icon is shown next to the reference speed instead.

A welcome screen with a colour key is shown each time you open the app as a quick reminder.

---

## Requirements

- Karoo 2 or Karoo 3
- Firmware version **1.527 or later**

---

## Installation

### Karoo 3 (recommended)

1. Go to the [Releases page](https://github.com/j4m1eb/Karoo-Average-speed-colour/releases/latest) on your phone.
2. Tap `app-release.apk` to download it.
3. Once downloaded, tap **Open** and choose the **Hammerhead companion app** to open it with.
4. The companion app will push it to your Karoo — reboot when prompted.

### Karoo 2 (ADB sideload)

1. Download `app-release.apk` from the [Releases page](https://github.com/j4m1eb/Karoo-Average-speed-colour/releases).
2. Enable developer mode on your Karoo — follow the [DC Rainmaker sideloading guide](https://www.dcrainmaker.com/2021/02/how-to-sideload-android-apps-on-your-hammerhead-karoo-1-karoo-2.html).
3. Install via ADB: `adb install app-release.apk`
4. Reboot your Karoo.

---

## Adding Fields to a Ride Profile

1. Open **Karoo settings → Profiles → [Your Profile]**
2. Edit a data page and tap **Add Field**
3. Search for "Color Speed" or browse to find the fields
4. Pick the field(s) you want and save

---

## Built With

- [Hammerhead Karoo Extension SDK](https://github.com/hammerheadnav/karoo-ext)
- Jetpack Compose + Glance (RemoteViews)
- Kotlin Coroutines & DataStore
- Koin dependency injection
