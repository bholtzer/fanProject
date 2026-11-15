# Version 1.5 onboarding and fan image verification

- Android versionCode 6, versionName 1.5.
- Clean Android build and all 3 existing unit tests passed.
- Installed on emulator-5554 and connected Samsung phone without clearing saved data.
- Emulator: startup displays Find your fan with the updated product images.
- Emulator: selecting Folding Hand Fan opens customization, then Start my breeze opens the simulator with the same selected product image.
- Emulator: force-stop and relaunch after saving the fan returns to Find your fan, rather than skipping onboarding.
- APK inspected: exactly 21 fan_product PNG images; every SHA-256 hash matches the source image. The three obsolete photo assets are absent after a clean build.
- Phone installation/version verified. Phone UI was locked, so interactive flow verification was performed on the emulator.

Build uses the working JDK at C:/Users/User/.gradle/jdks/jetbrains_s_r_o_-21-amd64-windows.2. The old Android Studio jbr directory is incomplete.

Command: gradle clean :androidApp:assembleDebug :shared:testDebugUnitTest --offline --console=plain
