# S20-RAW-CAM — Expert RAW Companion & QA Audit Implementation Plan

Convert S20-RAW-CAM into an intentional, zero-permission field companion for Samsung Expert RAW on the Galaxy S20 Ultra, addressing all confirmed static QA findings and pivoting the viewfinder into an interactive, step-by-step manual dial checklist with deep-linking to Expert RAW.

## User Review & Critical Decisions

> [!IMPORTANT]
> The QA audit clarified the core product intent: this app is a **companion field manual** for Samsung Expert RAW, not a standalone camera app or capture replacement. Users choose a recipe here, then enter values manually into Samsung Expert RAW.

- **Confirmed Decision 1 (Camera Feed)**: User confirmed removing the camera feed and `CAMERA` permission completely, pivoting to a clean, zero-permission companion guide with no hardware capture or battery drain.
- **Confirmed Decision 2 (Presentation)**: User confirmed presenting manual recipe entries as an **interactive step-by-step checklist** matching Expert RAW's physical dial layout and workflow.
- **Confirmed Platform Constraint (Keystore & Signing)**: Per AI Studio platform mandates, `debugConfig` and `signingConfigs` in `build.gradle.kts` are preserved as required build infrastructure, while all other audit items (intents, Room persistence, state retention, hardware specs, manifest queries) are fully remediated.

---

## 1. Overview & Core Concept

- **What It Does**: Serves as a dedicated companion field guide for Samsung Galaxy S20 Ultra photographers using Samsung Expert RAW. Photographers browse 28+ specialized scene recipes, view acceptable adjustment ranges and dial locations, check off settings as they enter them, copy parameters with one tap, and launch Samsung Expert RAW (or its Galaxy Store listing) directly.
- **Target Audience**: Galaxy S20 Ultra photographers seeking pro-grade 16-bit Linear DNG RAW captures without guessing manual exposure dials for complex lighting situations (milky way, golden hour, neon streets, macro, waterfalls).
- **Key Value**: Bridges the gap between scene recipes and the actual Samsung Expert RAW camera interface through explicit dial mapping, copyable parameters, adjustment tolerance hints, and an immediate app hand-off.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Recipe Browsing to Companion Hand-Off**:
   - User browses scene recipes in the **Presets** catalog.
   - Tapping "Companion Guide" or "Set in Expert RAW" transitions immediately to the **Companion** screen with that recipe loaded.
   - Selected recipe ID is persisted with `rememberSaveable` so rotating or switching apps never loses context.

2. **Interactive Expert RAW Dial Checklist**:
   - The user sees a step-by-step checklist ordered exactly as controls appear in Expert RAW's manual bottom bar:
     1. **Lens Selection**: S20 Ultra module guidance (Ultra-Wide 13mm $f/2.2$, Wide 26mm $f/1.8$, Telephoto 103mm equiv $f/3.5$ with 4x optical baseline).
     2. **ISO**: Recommended value, acceptable range (e.g., ISO 50–200 for landscapes), and dynamic range guidance.
     3. **Shutter Speed**: Recommended speed, hand-held vs tripod warning, and motion blur notes.
     4. **EV Compensation**: Target bias and highlight clipping warning.
     5. **White Balance (Kelvin)**: Target Kelvin with visual temperature swatch and color cast notes.
     6. **Focus & Metering Mode**: Manual slider / peaking recommendation and Matrix/Spot metering choice.
     7. **Format & Composition Check**: Reminder to verify 16-bit Linear DNG is toggled, and instruction to check Expert RAW's live histogram and grid lines.
   - Each step features:
     - Clear checkbox to track what has been dialed in.
     - 1-tap "Copy" button for quick clipboard reference.
     - Dial location indicator (e.g., "Expert RAW bottom bar → 2nd dial from left").

3. **Launching Expert RAW / Galaxy Store**:
   - Prominent **"Open Expert RAW"** floating action bar.
   - If installed, opens package `com.samsung.android.app.galaxyraw`.
   - If not installed, gracefully opens the Galaxy Store page (`market://details?id=com.samsung.android.app.galaxyraw` with HTTPS fallback).

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Zero-Permission Companion vs. Live Preview**:
  - *Chosen Approach*: Completely remove `CAMERA` permission, `<uses-feature>`, and CameraX preview dependencies from active UI.
  - *Why*: Eliminates unnecessary camera hardware lockups, background battery drain, and misleading "fake shutter" captures. Focuses 100% on high-value recipe translation and dial guidance.
- **Decision 2: Room Destructive Migration Removal**:
  - *Chosen Approach*: Remove `fallbackToDestructiveMigration()` from `AppDatabase.kt`.
  - *Why*: Prevents silent loss of user favorites and custom field notes across app upgrades.
- **Decision 3: S20 Ultra Optical Hardware Accuracy**:
  - *Chosen Approach*: Update specifications to match real S20 Ultra hardware:
    - Main Wide: ~26mm equivalent (108MP ISOCELL HM1, 1/1.33", $f/1.8$).
    - Telephoto: 103mm equivalent (48MP periscope, 4x optical baseline, $f/3.5$, supporting 10x hybrid opt).
    - Ultra-Wide: 13mm equivalent (12MP, 120° FOV, $f/2.2$).
  - *Why*: Corrects confusing zoom simulation scaling and reflects true optical baselines.
- **Decision 4: State Preservation across Recreation**:
  - *Chosen Approach*: Use `rememberSaveable` for `currentTab` and `selectedPresetId`.
  - *Why*: Prevents accidental tab resets to the home catalog during device rotation or app switching while the photographer is dialing values in Expert RAW.

---

## 4. Technical Architecture & Data Strategy

### System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                       MainActivity                          │
│  - rememberSaveable { currentTab, selectedPresetId }        │
│  - Bottom Navigation (Presets | Companion | Guide | EV Tool)│
└──────────────┬──────────────────────────────┬───────────────┘
               │                              │
       ┌───────▼──────────────┐       ┌───────▼────────────────┐
       │   PresetsListScreen  │       │ ExpertRawCompanionView │
       │  - Room Favorites    │       │  - Dial-by-Dial Steps  │
       │  - Scene Categories  │       │  - S20 Ultra Lens Spec │
       │  - Recipe Selection  │       │  - Checked Step State  │
       └──────────────────────┘       │  - Copy to Clipboard   │
                                      └───────┬────────────────┘
                                              │ Intent
                                      ┌───────▼────────────────┐
                                      │ Samsung Expert RAW /   │
                                      │ Galaxy Store Fallback  │
                                      │ (com.samsung.android.  │
                                      │  app.galaxyraw)        │
                                      └────────────────────────┘
```

### Component & State Mapping

1. **`AndroidManifest.xml`**:
   - Add `<queries>` block targeting `com.samsung.android.app.galaxyraw`.
   - Remove `<uses-permission android:name="android.permission.CAMERA" />` and `<uses-feature android:name="android.hardware.camera" />`.

2. **`AppDatabase.kt`**:
   - Construct Room database with `Room.databaseBuilder(...).build()`, explicitly removing `.fallbackToDestructiveMigration()`.

3. **`MainActivity.kt`**:
   - Rename navigation tab from `VIEWFINDER` to `COMPANION` ("Companion", `Icons.Filled.Tune`).
   - Store navigation tab and selected preset ID via `rememberSaveable { mutableIntStateOf(defaultId) }`.
   - Route `onOpenInCamera` callback directly into the Companion screen with the chosen preset ID.

4. **`ExpertRawCompanionScreen.kt`** (replaces camera viewfinder simulation):
   - Comprehensive interactive manual dial checklist for Expert RAW.
   - S20 Ultra lens specifications (26mm $f/1.8$, 13mm $f/2.2$, 103mm $f/3.5$ 4x optical baseline).
   - `openExpertRawOrStore(context)` launcher with Galaxy Store fallback and user notification.
   - Clear educational disclaimers: "Reference settings are starting baselines — always check Expert RAW's live histogram and exposure monitor."

5. **`ExampleRobolectricTest.kt`**:
   - Add tests verifying the Galaxy Store package intent construction, S20 Ultra lens specs, preset ID state restoration, and Room database configuration without destructive fallback.
