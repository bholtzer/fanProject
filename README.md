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
- **AdMob** — interstitial ad attempted at every app opening, rewarded video wired up as a monetisation hook
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
4. **Ad moment** — an interstitial is requested at every app opening after the splash. If the ad cannot load, the app continues.
5. **Main screen** (`presentation/main`):
   - Displays the chosen fan, its name and color.
   - **Turn on/off** and **Slower/Faster** controls set the speed from Off through High. Tapping the fan also toggles power.
   - A **Fan sound** switch saves the user's sound preference across launches.
   - **Swipe/drag** the fan to spin it manually — every full revolution ("reaches its limit") fires a short vibration tick.
   - The proximity and motion sensors detect when the phone approaches the face or is picked up. This starts the fan at Low if it is off, boosts the sound, and starts vibration. High speed also vibrates.

## Monetization

- `platform/AdManager` (expect/actual) wraps Google Mobile Ads.
- **Android** is fully implemented with `play-services-ads` (interstitial + rewarded video), using Google's official **test ad unit IDs** — swap in your real AdMob IDs in `AdManager.android.kt` and the Manifest before release.
- **iOS** has the same interface stubbed in `AdManager.ios.kt` with setup notes, since the Google Mobile Ads iOS SDK is an Objective-C CocoaPod. Two ways to finish it:
  1. Add the pod via the Kotlin CocoaPods Gradle plugin and call the generated cinterop bindings directly, or
  2. Write a small native Swift wrapper in `iosApp` and call it from Kotlin via the shared framework's Objective-C interop.
- A rewarded-video hook (`showRewarded`) is ready to use for extra monetisation, e.g. "watch a video to unlock a bonus fan color."

## Languages

The shared Compose UI follows the device language, with English as the fallback.
Broadly used languages covered include English, Chinese, Hindi, Spanish, French,
Arabic, Portuguese, Bengali, Russian, and Urdu. Existing Italian, Japanese,
Ukrainian, and Australian English translations are also preserved.
Hindi, Bengali, and Urdu include all interface labels, accessibility descriptions,
fan names, and finish names. Android supports right-to-left layouts for Arabic
and Urdu. On Android 13 and later, choose a language under the system app settings
for Fantastic → Language; on older versions, change the device language.

Validate resource keys and formatting arguments with `python tools/verify_localizations.py`.

## Building and running

**Android:**
1. Open the root folder in Android Studio (Koala+) with the KMP plugin.
2. Run the `androidApp` configuration on a device/emulator. A looping fan sound is included.

**iOS:**
1. Add `fan_sound.mp3` to the `iosApp` Xcode project's bundle resources.
2. Build the `shared` framework (`./gradlew :shared:embedAndSignAppleFrameworkForXcode` or via Android Studio's KMP wizard), then open `iosApp` in Xcode and run.
3. Finish `AdManager.ios.kt` per the notes inside it once you're ready to ship ads on iOS.

> Note: this project ships as raw Gradle sources without a checked-in Gradle wrapper JAR. Opening it in Android Studio (File → Open) will offer to generate the wrapper automatically; alternatively run `gradle wrapper` once you have Gradle installed locally.

## Where to plug in your own assets

- `shared/src/androidMain/res/raw/fan_sound.wav` and the iOS bundle equivalent — the looping fan sound. Regenerate the Android asset with `python tools/generate_fan_sound.py`.
- Real AdMob App IDs and ad unit IDs (`AndroidManifest.xml`, `AdManager.android.kt`, `Info.plist`, `AdManager.ios.kt`).
- Android app icon: `androidApp/src/main/res/drawable/ic_fan.xml`. The iOS asset catalog still needs its own icon.
