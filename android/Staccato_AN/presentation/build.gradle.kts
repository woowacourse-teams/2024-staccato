plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.androidJUnit5)
}

android {
    namespace = "com.on.staccato.presentation"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] =
            "de.mannodermaus.junit5.AndroidJUnit5Builder"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    dataBinding {
        enable = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // JUnit5
    testImplementation(libs.junit5)
    testRuntimeOnly(libs.junit.vintage.engine)

    // AssertJ
    testImplementation(libs.assertj.core)

    // Android LiveData Test
    testImplementation(libs.androidx.arch.core)

    // Android Test Runner
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)

    // Android JUnit5
    androidTestImplementation(libs.junit5.android.test.core)
    androidTestRuntimeOnly(libs.junit5.android.test.runner)

    // Coroutines Test
    testImplementation(libs.kotlinx.coroutines.test)

    // Mockk
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.agent)
    androidTestImplementation(libs.mockk.agent)
    androidTestImplementation(libs.mockk.android)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // Fragment
    implementation(libs.androidx.fragment.ktx)

    // RecyclerView
    implementation(libs.androidx.recyclerview)

    // Splash Screen
    implementation(libs.splashscreen)

    // Coil
    implementation(libs.coil)

    // Firebase
    implementation(libs.firebase.messaging.ktx)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Lottie
    implementation(libs.lottie)

    // CameraX
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
    implementation(libs.lifecycle.viewmodel.compose)

    // Compose ConstraintLayout
    implementation(libs.androidx.constraintlayout.compose)

    implementation(project(":domain"))
}
