plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.amazonaws:aws-android-sdk-s3:2.42.+")
    implementation ("com.airbnb.android:lottie:+")

// OkHttp for making HTTP requests
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")

// ExoPlayer for audio playback
    implementation ("com.google.android.exoplayer:exoplayer:2.15.0")
    // Retrofit for HTTP requests
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

// Converter for JSON responses
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

// OkHttp for logging
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    implementation ("com.google.android.gms:play-services-location:21.0.1")

    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.android.libraries.places:places:3.4.0") // Google Places API

    implementation ("com.google.android.gms:play-services-maps:17.0.1")  // For Google Maps
    implementation ("com.google.android.gms:play-services-location:17.0.0")  // For location









}