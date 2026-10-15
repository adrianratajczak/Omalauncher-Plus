plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "dev.omalauncher.plus.lockscreen"
    compileSdk = 37

    defaultConfig {
        applicationId = "dev.omalauncher.plus.lockscreen"
        minSdk = 29
        targetSdk = 37
        versionCode = 1
        versionName = "0.1"
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
}
