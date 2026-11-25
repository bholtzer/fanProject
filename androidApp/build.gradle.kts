import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

val localSigning = Properties().apply {
    val propertiesFile = rootProject.file(".signing/signing.properties")
    if (propertiesFile.isFile) propertiesFile.inputStream().use { load(it) }
}
val signingValues = listOf(
    "ANDROID_KEYSTORE_FILE", "ANDROID_KEYSTORE_PASSWORD",
    "ANDROID_KEY_ALIAS", "ANDROID_KEY_PASSWORD"
).map { name ->
    providers.gradleProperty(name).orElse(providers.environmentVariable(name)).orNull
        ?: localSigning.getProperty(name)
}

val productionAds = mapOf(
    "admobAppId" to providers.gradleProperty("ADMOB_APP_ID"),
    "admobInterstitialId" to providers.gradleProperty("ADMOB_INTERSTITIAL_ID"),
    "admobRewardedId" to providers.gradleProperty("ADMOB_REWARDED_ID")
)
val validateReleaseAds by tasks.registering {
    doLast {
        val invalid = productionAds.filter { (key, provider) ->
            val separator = if (key == "admobAppId") "~" else "/"
            val id = provider.orNull?.trim().orEmpty()
            val optionalAndUnset = key == "admobRewardedId" && id.isEmpty()
            !optionalAndUnset && (
                !id.matches(Regex("ca-app-pub-[0-9]{16}${separator}[0-9]{10}")) ||
                    id.startsWith("ca-app-pub-3940256099942544")
                )
        }
        check(invalid.isEmpty()) {
            "Set valid production AdMob IDs: " + invalid.keys.joinToString { key ->
                when (key) {
                    "admobAppId" -> "ADMOB_APP_ID"
                    "admobInterstitialId" -> "ADMOB_INTERSTITIAL_ID"
                    else -> "ADMOB_REWARDED_ID"
                }
            }
        }
    }
}
tasks.matching { it.name == "preReleaseBuild" }.configureEach {
    dependsOn(validateReleaseAds)
}

android {
    namespace = "com.bihstudio.fan"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.bihstudio.fan"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        getByName("debug") {
            manifestPlaceholders.putAll(mapOf(
                "admobAppId" to "ca-app-pub-3940256099942544~3347511713",
                "admobInterstitialId" to "ca-app-pub-3940256099942544/1033173712",
                "admobRewardedId" to "ca-app-pub-3940256099942544/5224354917"
            ))
        }
        getByName("release") {
            if (signingValues.all { !it.isNullOrBlank() }) {
                signingConfig = signingConfigs.create("release") {
                    storeFile = rootProject.file(requireNotNull(signingValues[0]))
                    storePassword = signingValues[1]
                    keyAlias = signingValues[2]
                    keyPassword = signingValues[3]
                }
            }
            isMinifyEnabled = false
            manifestPlaceholders.putAll(productionAds.mapValues { (_, id) -> id.orNull?.trim().orEmpty() })
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("com.google.android.gms:play-services-ads:23.3.0")
}
