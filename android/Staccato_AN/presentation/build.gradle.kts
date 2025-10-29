plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlinCompose)
    id("kotlin-kapt")
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.androidJUnit5)
}

android {
    namespace = "com.on.staccato.presentation"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        defaultConfig {
            buildConfig = true
        }
        compose = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    dataBinding {
        enable = true
    }
}

dependencies {
    // Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // JUnit5
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.junit.vintage.engine)

    // AssertJ
    testImplementation(libs.assertj.core)

    // Android LiveData Test
    testImplementation(libs.androidx.arch.core)

    // Android Test Runner
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)

    // Coroutines Test
    testImplementation(libs.kotlinx.coroutines.test)

    // MockK
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.agent)
    androidTestImplementation(libs.mockk.agent)
    androidTestImplementation(libs.mockk.android)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.livedata)

    // Fragment
    implementation(libs.androidx.fragment.ktx)

    // RecyclerView
    implementation(libs.androidx.recyclerview)

    // Splash Screen
    implementation(libs.androidx.splashscreen)

    // Coil
    implementation(libs.coil)

    // Firebase
    implementation(libs.firebase.messaging.ktx)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Lottie
    implementation(libs.lottie)

    // Camerax
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extension)

    // Google Maps SDK
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.maps.ktx)

    // Google Maps SDK for Android utility library
    implementation(libs.maps.utils.ktx)

    // Google Place
    implementation(libs.places)
    implementation(platform(libs.kotlin.bom))

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // Hilt & Navigation
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)

    // Material Design 3
    implementation(libs.androidx.material3)

    // Compose 기본 설정
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))

    // Compose core UI
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)

    // Compose Coil
    implementation(libs.coil.compose)

    // Compose ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Compose ConstraintLayout
    implementation(libs.androidx.constraintlayout.compose)

    implementation(project(":domain"))
}
