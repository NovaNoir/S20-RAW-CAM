# S20-RAW-CAM — Galaxy S20 Ultra Expert RAW Field Companion

[![Platform](https://img.shields.io/badge/Platform-Android%2014%2B%20%28SDK%2036%29-3DDC84.svg?style=flat&logo=android)](https://developer.android.com)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose%20%28M3%29-4285F4.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Permissions](https://img.shields.io/badge/Permissions-Zero%20Permission-success.svg?style=flat)](#zero-permission-design)
[![Target App](https://img.shields.io/badge/Companion%20For-Samsung%20Expert%20RAW-0389FF.svg?style=flat)](https://galaxystore.samsung.com/detail/com.samsung.android.app.galaxyraw)

> **CRITICAL PRODUCT NOTICE**: **S20-RAW-CAM is a companion field manual and exposure recipe guide** for Samsung's official **Expert RAW** camera app on the Samsung Galaxy S20 Ultra. It is **NOT** a camera application and does **NOT** capture photographs or replace Expert RAW. Photographers browse specialized scene recipes and exposure calculations here, then dial those settings manually into Samsung Expert RAW.

---

## Table of Contents

- [Overview & Workflow](#overview--workflow)
- [Key Features](#key-features)
- [S20 Ultra Camera System Specifications](#s20-ultra-camera-system-specifications)
- [Project Architecture](#project-architecture)
- [Setup & Build Instructions](#setup--build-instructions)
- [Running Tests](#running-tests)
- [How to Use the Companion](#how-to-use-the-companion)
- [Zero-Permission Design](#zero-permission-design)
- [Contributing](#contributing)
- [Disclaimers & Trademarks](#disclaimers--trademarks)

---

## Overview & Workflow

Samsung Expert RAW enables 16-bit Linear DNG multi-frame captures on compatible Galaxy smartphones. However, dialing manual exposure (ISO, Shutter, EV Bias, Kelvin WB, Focus, and Metering) across diverse conditions—such as astrophotography, high-contrast cityscapes, or macro textures—requires domain expertise.

**S20-RAW-CAM** acts as your pocket digital photography reference:

```
┌──────────────────────────┐           ┌─────────────────────────────┐
│       S20-RAW-CAM        │           │     Samsung Expert RAW      │
│  (Companion Field Guide) │           │ (Galaxy Store: galaxyraw)   │
├──────────────────────────┤           ├─────────────────────────────┤
│ 1. Choose Scene Recipe   │           │                             │
│ 2. Check Dial Locations  │──Intent──>│ 1. Switch to Target Lens    │
│ 3. Copy Values / Range   │  Launch   │ 2. Dial in ISO, Shutter, WB │
│ 4. Tap "Open Expert RAW" │           │ 3. Check Live Histogram     │
└──────────────────────────┘           │ 4. Capture 16-bit DNG Frame │
                                       └─────────────────────────────┘
```

---

## Key Features

- **Interactive Expert RAW Dial Checklist**:
  - Step-by-step breakdown matching Expert RAW's physical dial strip (Lens switcher, ISO dial, Shutter speed dial, EV compensation, Kelvin slider, Manual focus peaking, and Metering mode).
  - Tolerance ranges and "why" rationale for every parameter.
  - One-tap clipboard copy for numeric values.
  - Check-off tracking so you never miss a dial.
- **Direct Expert RAW Hand-Off**:
  - Integrated launcher intent targeting `com.samsung.android.app.galaxyraw`.
  - Automatic fallback to the Galaxy Store details page if Expert RAW is not yet installed.
- **1,008 Scene Recipes across 56 Scene Families**:
  - Structured into 7 photography domains: Astrophotography, Golden Hour & Landscapes, Urban & Night Street, Macro & Detail, Portraits, Motion & Sports, and Architecture.
  - Search, filter by scene family, and tag favorites.
- **Local Persistence with Room**:
  - Bookmark favorite scene recipes and record field notes directly on your device.
  - Safe migrations without destructive data recreation.
- **Exposure & Reciprocity Tools**:
  - Comprehensive EV calculators.
  - Schwarzschild long-exposure reciprocity failure compensation for extreme night exposures and analog film cross-referencing.
  - Interactive Kelvin white balance cheat sheet with accurate color swatches.

---

## S20 Ultra Camera System Specifications

S20-RAW-CAM is calibrated specifically for the rear camera hardware array of the **Samsung Galaxy S20 Ultra (SM-G988B/N/U)**:

| Module | 35mm Equiv. | Aperture | Sensor Specifications | Optical Baseline & Role |
| :--- | :--- | :--- | :--- | :--- |
| **Ultra-Wide (UW)** | 13mm equiv. | $f/2.2$ | 12MP, 1.4µm pixels, 120° FOV | 0.5× Ultra-Wide (Vistas, architecture, Milky Way foregrounds) |
| **Wide Primary (W)** | 26mm equiv. | $f/1.8$ | 108MP ISOCELL Bright HM1, 1/1.33", OIS, 0.8µm (3×3 Nonacell 2.4µm) | 1.0× Native baseline (Maximum dynamic range, low-light RAW) |
| **Telephoto (T)** | 103mm equiv. | $f/3.5$ | 48MP, 1/2.0", OIS, 0.8µm (Tetracell 1.6µm) | 4.0× Optical periscope baseline (10× Hybrid Opt, up to 100× Space Zoom) |

> **Note**: While Samsung marketing refers to the telephoto zoom button as "5x" in some One UI camera modes, the physical optical magnification baseline of the S20 Ultra periscope lens is **4.0× (103mm equivalent)**, with 10× hybrid optics. S20-RAW-CAM highlights this physical baseline for optical accuracy.

---

## Project Architecture

The codebase follows modern Android development standards using Kotlin and Jetpack Compose:

```
app/src/main/java/com/example/
├── MainActivity.kt               # Main entry point with rememberSaveable tab state
├── data/
│   ├── CameraPreset.kt           # 1,008 Scene recipes, categories, and decision rules
│   ├── PresetStorage.kt          # SharedPreferences fallback utility
│   └── db/
│       ├── AppDatabase.kt        # Room database definition with protected migrations
│       ├── PresetDao.kt          # Reactive Flow queries for favorites and user notes
│       ├── PresetEntity.kt       # Room data entity
│       └── RoomPresetRepository.kt # Repository implementation
├── ui/
│   ├── PresetViewModel.kt        # StateFlow presentation layer
│   ├── components/               # Custom UI cards, reciprocity tool, and dialogs
│   ├── screens/
│   │   ├── ExpertRawCompanionScreen.kt # Interactive manual dial checklist & launcher
│   │   ├── PresetsListScreen.kt        # Recipe catalog with search and filters
│   │   ├── FieldGuideScreen.kt         # Photography guide & Kelvin cheat sheet
│   │   └── ExposureCalculatorScreen.kt # EV and reciprocity failure calculator
│   └── theme/                    # High-contrast M3 theme, Kelvin colors, typography
```

---

## Setup & Build Instructions

### Prerequisites

- **Android Studio**: Ladybug (2024.2+) or Meerkat
- **JDK**: Java 17 or Java 21
- **Android SDK**: Compile SDK 36 (Android 16), Min SDK 24 (Android 7.0+)
- **Build System**: Gradle 9.x with Kotlin DSL (`.gradle.kts`)

### Building the Project

1. Open a terminal in the project root directory.
2. Build the debug APK using Gradle:
   ```bash
   gradle assembleDebug
   ```
3. The compiled APK will be located at:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

### Installing on Device

Connect your Samsung Galaxy S20 Ultra via USB with USB Debugging enabled:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## Running Tests

The project includes unit and local JVM tests powered by **JUnit 4** and **Robolectric**:

```bash
# Run unit and Robolectric tests
gradle :app:testDebugUnitTest
```

Key test suites:
- `ExampleRobolectricTest`: Validates Room database seeding, note storage, S20 Ultra physical lens specifications, reciprocity math, and package intent strings.
- `GreetingScreenshotTest`: Roborazzi screenshot test for UI components.

---

## How to Use the Companion

1. **Browse Recipes**: Open the **Presets** tab. Filter by category (e.g., *Night & Astrophotography*, *Golden Hour*, or *Urban Street*) or search for specific scene keywords.
2. **Select & Review**: Tap a recipe to inspect its parameters, or tap **Companion Guide** to enter the step-by-step checklist.
3. **Open Samsung Expert RAW**: Tap **Open Samsung Expert RAW** at the top or bottom of the companion screen.
4. **Dial in the Values**:
   - Follow the numbered steps (Lens → ISO → Shutter → EV → WB → Focus/Metering).
   - Tap the **Copy** button if you want to retain an exact Kelvin temperature or shutter speed in your clipboard.
   - Check off each dial as you set it in Expert RAW.
5. **Verify Exposure**: Check Expert RAW's live histogram and composition grid before triggering the shutter.

---

## Zero-Permission Design

To ensure user privacy, minimal battery consumption, and maximum reliability:
- S20-RAW-CAM declares **zero runtime permissions**.
- It does **not** request `android.permission.CAMERA`, location, or storage permissions.
- All personal shooting notes and favorites remain 100% on-device in local SQLite storage.

---

## Contributing

Contributions, preset suggestions, and bug reports are welcome!

1. Fork the repository.
2. Create a feature branch:
   ```bash
   git checkout -b feature/new-scene-family
   ```
3. Commit your changes following standard Android/Kotlin style conventions.
4. Verify your changes build and all unit tests pass:
   ```bash
   gradle :app:testDebugUnitTest
   ```
5. Open a Pull Request with a clear description of your additions.

---

## Disclaimers & Trademarks

- **Samsung**, **Galaxy**, **Galaxy Store**, and **Expert RAW** are registered trademarks of Samsung Electronics Co., Ltd.
- This open-source repository is an independent companion guide developed for mobile photographers and is **not** affiliated with, endorsed by, or sponsored by Samsung Electronics.
- Recipe settings are recommended starting baselines. Because real-world lighting conditions, cloud densities, and firmware versions vary, always evaluate exposure using Expert RAW's live histogram.
