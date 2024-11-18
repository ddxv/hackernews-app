plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version "1.9.10"
}

android {
    namespace = "com.thirdgate.hackernews"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.thirdgate.hackernews"
        minSdk = 26
        targetSdk = 35
        versionCode = 10
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"

    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

}



dependencies {
//    val composeBom = platform("androidx.compose:compose-bom:2024.05.00")
//    implementation("androidx.compose:compose-bom:2024.05.00")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.compose.ui:ui:1.7.5")
    implementation("androidx.compose.foundation:foundation-layout-android:1.7.5")
    implementation("androidx.activity:activity-compose:1.9.3")
    // Android Studio Preview support
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Material
    implementation("androidx.compose.material:material:1.7.5")
    implementation("androidx.compose.material:material-icons-core:1.7.5")
    //implementation("androidx.compose.material3:material3:1.1.1")
    implementation("com.google.android.material:material:1.12.0")

    // Glance
    implementation("androidx.glance:glance-appwidget:1.1.1")
    implementation("androidx.glance:glance-material:1.1.1")
    implementation("androidx.glance:glance:1.1.1")

    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    implementation("androidx.work:work-runtime-ktx:2.10.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("androidx.datastore:datastore:1.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    implementation("com.google.code.gson:gson:2.11.0")

    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    // define any required OkHttp artifacts without version
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")


}
