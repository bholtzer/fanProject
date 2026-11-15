# Fan animation repair — 2026-11-04

The simulator rendered a static FanProductImage and never consumed the ViewModel's rotationDegreesPerSecond. Drag callbacks only triggered haptics.

- A Compose frame clock now advances a shared phase using elapsed frame time and the ViewModel's speed.
- Inline and full-screen views share phase and manual drag input. Power off retains the last position; changing speed does not reset it.
- Rotary designs rotate a circular region of the existing PNG while retaining the surrounding product image. Hand fans sway; bladeless designs show airflow while powered on.
- The PNGs are flattened: the rotating region includes internal grille details. Truly independent blades and guards require separate image layers.

Validation: Gradle 8.7, bundled JetBrains JDK 21, offline :androidApp:assembleDebug and :shared:testDebugUnitTest both succeeded. All three existing FanAppearanceTest tests passed; these cover catalog appearance, not animation rendering.

The APK was installed successfully with adb install -r on emulator-5554 and the connected Samsung SM-S911B, retaining app data. Emulator startup was unusually slow and UI hierarchy capture was intermittent, so live animation was not visually verified. No AndroidRuntime errors appeared in the bounded log capture.
