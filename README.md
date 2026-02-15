# Nepali Date Bullet Screen

An Android Live Wallpaper that displays a dot-grid visualization of **Bikram Sambat (BS) calendar** year progress on your lock screen.

Each dot represents a day of the Nepali calendar year — bright dots for days past, a red dot for today, and dim dots for days ahead.

## Features

- **Live Wallpaper** — renders directly on your lock screen (Android 8.0+)
- **Months Grid** — 12 Nepali month blocks (Baisakh → Chaitra) in a 3×4 layout
- **Hand-built BS calendar engine** — no external library, uses a 121-year lookup table (BS 1970–2090)
- **Battery friendly** — redraws once per minute, essentially a static image
- **Current date display** — shows BS date (e.g. "Falgun 03, 2082") and progress ("57d left / 84%")
- **Progress bar** — visual indicator of year completion

## Screenshots
<img width="450" alt="image" src="https://github.com/user-attachments/assets/4e8bdbfa-d735-4b5f-abff-580f8f8a40f8" />




## Project Structure

```
com.prashant.datebulletscreen/
├── core/
│   ├── calendar/
│   │   ├── NepaliMonthData.kt      # Lookup table: days per month for BS 1970–2090
│   │   ├── NepaliDate.kt           # Data class: year, month, day + formatting
│   │   ├── NepaliDateConverter.kt  # AD ↔ BS date conversion (java.time based)
│   │   └── NepaliDateUtils.kt      # dayOfYear(), daysRemaining(), yearProgress()
│   └── renderer/
│       ├── DotGridRenderer.kt      # Canvas drawing for months grid
│       ├── GridColors.kt           # Color constants (dark theme)
│       └── GridDimensions.kt       # Responsive layout calculations
├── wallpaper/
│   └── DateBulletWallpaperService.kt  # WallpaperService + Engine
└── ui/
    ├── MainActivity.kt             # Entry point, launches wallpaper picker
    ├── SettingsScreen.kt           # Shows current BS date + stats
    ├── PreviewScreen.kt            # Full-screen grid preview
    └── theme/
        └── Theme.kt                # Material3 dark theme
```

## How It Works

### Nepali Calendar (Bikram Sambat)

Unlike the Gregorian calendar, BS month lengths **vary every year** based on astronomical observation. There's no formula — you need a lookup table.

`NepaliMonthData.kt` contains the days-per-month data for 121 BS years sourced from [keyrunHORNET/date_picker_converter](https://github.com/keyrunHORNET/date_picker_converter).

### AD → BS Conversion

The converter uses a known reference point:

| BS Date        | AD Date         |
|----------------|-----------------|
| 1970/01/01     | 1913/04/13      |

**Algorithm** (`NepaliDateConverter.fromGregorian`):
1. Calculate total days between the input AD date and the epoch using `java.time.ChronoUnit.DAYS`
2. Walk forward through BS years (subtracting each year's total days) to find the year
3. Walk forward through BS months to find the month and day

### Dot Grid Rendering

`DotGridRenderer` draws directly on a `Canvas` using pre-allocated `Paint` objects (no allocations in the draw loop):

- **Months Grid**: 3×4 layout of month blocks, each containing dots for that month's days
- Current month label highlighted brighter
- Progress bar + date text at the bottom

### Live Wallpaper

`DateBulletWallpaperService` extends Android's `WallpaperService`:
- Redraws on visibility change + once per minute via `Handler`
- Cancels callbacks when not visible (battery optimization)

## Getting Started

### Prerequisites

- Android Studio (latest stable)
- Android SDK 35 (installed via Android Studio)
- JDK 17+

### Build & Run

1. Clone the repository:
   ```bash
   git clone git@github.com:prashant676a/nepali-date-bullet-screen-android.git
   cd nepali-date-bullet-screen-android
   ```

2. Open in Android Studio → let Gradle sync

3. Connect your Android phone:
   - Enable **Developer Options** (tap Build Number 7 times in Settings → About)
   - Enable **USB Debugging** in Developer Options
   - Enable **Install via USB** in Developer Options
   - Connect phone via USB

4. Click **Run** (▶) to install the app

5. Open the app → tap **"Set as Live Wallpaper"** → apply

### Run Unit Tests

```bash
./gradlew test
```

Tests verify:
- Known AD/BS date conversions (e.g. BS 2080/01/01 = AD 2023/04/14)
- Round-trip conversion (AD → BS → AD) for multiple dates
- Days-in-year calculations for multiple BS years
- All 121 years have 364–367 days
- Day-of-year arithmetic and progress calculations

## Tech Stack

| Component         | Technology                          |
|-------------------|-------------------------------------|
| Language          | Kotlin                              |
| Min SDK           | 26 (Android 8.0)                    |
| Target SDK        | 35                                  |
| UI Framework      | Jetpack Compose + Material3         |
| Wallpaper         | Android WallpaperService (Canvas)   |
| Build System      | Gradle 8.9 + AGP 8.7.3             |
| Calendar Data     | Hand-built lookup table (no library)|

## Key Design Decisions

- **No external Nepali calendar library** — the lookup table approach is simpler, has zero dependencies, and covers all practical years
- **Canvas rendering over Compose** — Live Wallpapers require `WallpaperService` which provides a `Canvas`, not a Compose surface. Canvas is also more efficient for drawing hundreds of circles
- **`java.time.LocalDate` for day arithmetic** — avoids DST rounding bugs that occur with `GregorianCalendar` millisecond math
- **350px top margin** — avoids overlapping the lock screen clock on most devices
- **Pre-allocated Paint objects** — avoids GC pressure during the draw loop

## Contributing

1. Fork the repo
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Commit your changes
4. Push and open a Pull Request

### Adding Calendar Data for New Years

If you need to extend coverage beyond BS 2090, add entries to the `DATA` array in `NepaliMonthData.kt`:

```kotlin
// 2091
intArrayOf(0, d1, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12),
```

Update `MAX_YEAR` accordingly. The 13-element array uses index 0 as padding, indices 1–12 for months Baisakh through Chaitra.

## License

MIT
