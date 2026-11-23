# Fan Simulator (KMP + Compose Multiplatform)

This is the maintained project: `C:\Users\User\AndroidStudioProjects\FanSimulator`.
It contains the 21 updated fan product images and builds Android package
`com.bihstudio.fan` (version 1.7). In Android Studio, open this folder and use
the **FanSimulator (new fan images)** run configuration.

The obsolete `FanProject` copy was archived on 2026-11-05 in
`artifacts/FanProject-old-backup-2026-11-05.zip`, including its Git history and
local changes. Keep that ZIP as a backup; do not use it as the active project.

A fan-simulator app for Android and iOS from **one shared codebase**, built with:

- **Kotlin Multiplatform (KMP)** — shared domain, data, and presentation logic
- **Compose Multiplatform** — one Compose UI shared by both Android and iOS (no duplicated screens)
- **MVVM** — plain `StateFlow`-based ViewModels per screen
- **Clean Architecture** — `domain` (models/use cases) → `data` (repositories) → `presentation` (ViewModels/UI), with `platform/` expect-actual for anything OS-specific
- **AdMob** — a preloaded interstitial can appear when changing fans, rewarded video wired up as a monetisation hook
- Latest stable SDKs at time of writing: Kotlin 2.0.20, AGP 8.5.2, compileSdk/targetSdk 35, Compose Multiplatform 1.7.0

## Project layout

```
FanSimulator/
├── shared/                     KMP module — all shared code
│   └── src/
│       ├── commonMain/          domain, data, presentation (screens+viewmodels), App.kt
│       ├── androidMain/         Android actuals: vibration, proximity sensor, sound, AdMob
│       └── iosMain/             iOS actuals + MainViewController() entry point for Swift
├── androidApp/                  Thin Android shell (Activity, Manifest, AdMob App ID)
└── iosApp/                      Thin iOS shell (SwiftUI wrapper around the shared Compose UI)
```

## App flow (matches the requested UX)

1. **Splash** — a spinning pedestal fan illustration matching the Android app icon (`presentation/splash`).
2. **Fan selection** — grid of **21 fan types**, ordered from ancient handheld fans to the latest digital model (`domain/model/FanCatalog`, `presentation/fanselection`). Includes supplied photos for the ROTAKE floor fan, grey and purple desk fan, and Air King wall fan. Photo models retain their original colors and remain static; sound and speed controls still work.
3. **Customization** — pick a color and name the fan (`presentation/customization`).
4. **Main screen** (`presentation/main`):
   - Displays the chosen fan, its name and color.
   - **Turn on/off** and **Slower/Faster** controls set the speed from Off through High. Tap the fan to open the same artwork fullscreen. Tilt the phone or drag for a perspective view. Pinch or use the zoom buttons to resize it, and drag to pan when zoomed in.
   - A **Fan sound** switch saves the user's sound preference across launches. Each fan model has its own pitch, and the playback rate follows the selected speed.
   - In fullscreen, running the fan for four seconds brings in paper, gum, or a fly, depending on the model. Higher speeds pull the object toward the fan faster until it disappears.
   - **Swipe/drag** the fan to spin it manually — every full revolution ("reaches its limit") fires a short vibration tick.
   - The proximity and motion sensors detect when the phone approaches the face or is picked up. This starts the fan at Low if it is off, boosts the sound, and starts vibration. High speed also vibrates.

## Monetization

- `platform/AdManager` (expect/actual) wraps Google Mobile Ads.
- **Android debug** uses Google's demo app and ad unit IDs.
- **Android release** reads the production AdMob app ID and interstitial ad unit for `com.bihstudio.fan` from the project Gradle properties. Override them with `ADMOB_APP_ID` and `ADMOB_INTERSTITIAL_ID` in your user Gradle properties file (`%USERPROFILE%/.gradle/gradle.properties`) or using `-P`. Set `ADMOB_REWARDED_ID` only to configure the rewarded video hook. Release builds require production app and interstitial IDs and reject demo IDs.
- A preloaded interstitial can appear when the user opens fullscreen from the customization or main screen, or chooses **Change Fan**. Navigation continues when the ad closes or immediately if no ad is available.
- **iOS** delegates through `IosAdsBridge` to `iosApp/iosApp/AdMobBridge.swift`. The Swift host installs the bridge before creating Compose UI. It initializes the SDK, preloads and refreshes interstitial/rewarded ads, expires cached ads after an hour, and handles dismissal, presentation failure and earned rewards. Missing ads continue navigation without granting a reward. All bridge calls must run on the main thread.
- A rewarded-video hook (`showRewarded`) is ready to use for extra monetisation, e.g. "watch a video to unlock a bonus fan color."

## Languages

The shared Compose UI follows the device language, with English as the fallback.
Broadly used languages covered include English, Chinese, Hindi, Spanish, French,
Arabic, Portuguese, Bengali, Russian, and Urdu. Existing Italian, Japanese,
Ukrainian, and Australian English translations are also preserved.
The **Language** button in the Android fan catalog opens a picker for all 14
supported locales, plus **Use device language**. The app remembers the selection.
Android 13 and later also expose it in the system app settings. Arabic and Urdu
use right-to-left layouts.

Validate resource keys and formatting arguments with `python tools/verify_localizations.py`.

## Building and running

**Android:**
1. Open the root folder in Android Studio (Koala+) with the KMP plugin.
2. For local development, select the `debug` build variant and run `androidApp` on a device/emulator. A looping fan sound is included.
3. For a signed release, put these values in `%USERPROFILE%/.gradle/gradle.properties` (not this repository), or environment variables with the same names:

```properties
ANDROID_KEYSTORE_FILE=C:/private/your-upload-key.jks
ANDROID_KEYSTORE_PASSWORD=your-store-password
ANDROID_KEY_ALIAS=your-key-alias
ANDROID_KEY_PASSWORD=your-key-password
```

Use forward slashes in the keystore path on Windows. Sync Gradle after setting all four values, then select `release` or build `:androidApp:assembleRelease` / `:androidApp:bundleRelease`. Use the existing upload/signing key for a published app. If this is a new app, create a keystore using Android Studio’s **Build → Generate Signed App Bundle / APK → Create new** and keep a secure backup. Release signing is configured only when all four values are supplied; credentials are never stored in project build scripts.

**iOS:**
1. Add `fan_sound.mp3` to the `iosApp` Xcode project's bundle resources.
2. Build the `shared` framework (`./gradlew :shared:embedAndSignAppleFrameworkForXcode` or via Android Studio's KMP wizard), then open `iosApp` in Xcode and run.
3. This repository contains iOS source files, but no Xcode project. In your macOS Xcode app target, include all files under `iosApp/iosApp`, including `AdMobBridge.swift`, and link the generated `shared` framework.
4. Add the Swift package `https://github.com/googleads/swift-package-manager-google-mobile-ads.git` and link its `GoogleMobileAds` product (Swift API names in the bridge target SDK 12+). Follow https://developers.google.com/admob/ios/quick-start for deployment requirements and SKAdNetworkItems in the target's Info.plist.
5. Use the supplied Info.plist (or merge its keys into the target's plist). It contains Google demo app, interstitial and rewarded IDs. Replace all three with your iOS AdMob IDs before publishing; Android IDs cannot be reused. Initialize ads only after any required consent flow completes.
6. On a simulator/device, verify loaded-ad dismissal, unavailable ads, presentation failure, and rewarded completion versus early dismissal. iOS compilation and runtime verification require macOS/Xcode; an Android build does not validate Swift.

> Note: this project ships as raw Gradle sources without a checked-in Gradle wrapper JAR. Opening it in Android Studio (File → Open) will offer to generate the wrapper automatically; alternatively run `gradle wrapper` once you have Gradle installed locally.

## Where to plug in your own assets

- `shared/src/androidMain/res/raw/fan_sound.wav` and the iOS bundle equivalent — the looping fan sound. Regenerate the Android asset with `python tools/generate_fan_sound.py`.
- Real AdMob App IDs and ad unit IDs (`AndroidManifest.xml`, `AdManager.android.kt`, `Info.plist`, `AdManager.ios.kt`).
- Android app icon: `androidApp/src/main/res/drawable/ic_fan.xml`. The iOS asset catalog still needs its own icon.
