plugins {
    id("com.android.application")
}

android {
    namespace = "com.akahwaj.streamversesetup"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.akahwaj.streamversesetup"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
