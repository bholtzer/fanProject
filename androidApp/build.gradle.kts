plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.fansim.app.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fansim.app.android"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.android.gms:play-services-ads:23.6.0")
    implementation("com.russhwolf:multiplatform-settings:1.2.0")
}
