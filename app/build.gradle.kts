plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.soundguard"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.soundguard"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.amazonaws:aws-android-sdk-s3:2.42.+")

// OkHttp for making HTTP requests
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")

// ExoPlayer for audio playback
    implementation ("com.google.android.exoplayer:exoplayer:2.15.0")


}