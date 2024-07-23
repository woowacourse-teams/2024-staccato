plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
}

android {
    namespace = "com.woowacourse.staccato"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.woowacourse.staccato"
        minSdk = 26
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Glide
    implementation(libs.glide)

    // Coil
    implementation(libs.coil)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization.converter)

    // Kotlinx-Serialization
    implementation(libs.kotlinx.serialization.json)

    // OkHttp
    implementation(libs.okhttp.logging.interceptor)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // Splash Screen
    implementation(libs.splashscreen)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // Room
    implementation(libs.room)

    // RecyclerView
    implementation(libs.androidx.recyclerview)

    // Fragment
    implementation(libs.androidx.fragment.ktx)

    // Mockk
    testImplementation(libs.mockk.android)
    testImplementation(libs.mockk.agent)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
}
