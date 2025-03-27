import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import java.io.FileInputStream
import java.util.Properties

val localProperties =
    Properties().apply {
        load(FileInputStream(rootProject.file("local.properties")))
    }

val keystoreProperties =
    Properties().apply {
        load(FileInputStream(rootProject.file("app/signing/keystore.properties")))
    }

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
    alias(libs.plugins.mapsplatformSecretsGradlePlugin)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.androidJUnit5)
}

android {
    namespace = "com.on.staccato"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.on.staccato"
        minSdk = 26
        targetSdk = 34
        versionCode = 10
        versionName = "1.3.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] =
            "de.mannodermaus.junit5.AndroidJUnit5Builder"

        buildConfigField("String", "TOKEN", "${localProperties["token"]}")
    }

    signingConfigs {
        create("release") {
            storeFile = file("${keystoreProperties["store_file"]}")
            keyAlias = "${keystoreProperties["key_alias"]}"
            keyPassword = "${keystoreProperties["key_password"]}"
            storePassword = "${keystoreProperties["keystore_password"]}"
        }
    }

    buildFeatures {
        defaultConfig {
            buildConfig = true
        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "${localProperties["dev_base_url"]}")
            manifestPlaceholders["appName"] = "@string/app_name_dev"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-DEV"
        }
        release {
            buildConfigField("String", "BASE_URL", "${localProperties["base_url"]}")
            manifestPlaceholders["appName"] = "@string/app_name"
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release")
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

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    // Android Setup
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // JUnit4
    testImplementation(libs.junit)
    testImplementation(libs.junitparams)

    // JUnit5
    testImplementation(libs.junit5)
    testRuntimeOnly(libs.junit.vintage.engine)

    // AssertJ
    testImplementation(libs.assertj.core)

    // Android Espresso
    androidTestImplementation(libs.androidx.espresso.core)

    // Android LiveData Test
    testImplementation(libs.androidx.arch.core)

    // Android Test Runner
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)

    // Android JUnit4
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.junitparams)

    // Android JUnit5
    androidTestImplementation(libs.junit5.android.test.core)
    androidTestRuntimeOnly(libs.junit5.android.test.runner)

    // Glide
    implementation(libs.glide)

    // Coil
    implementation(libs.coil)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization.converter)
    implementation(libs.converter.scalars)

    // Kotlinx-Serialization
    implementation(libs.kotlinx.serialization.json)

    // OkHttp
    implementation(libs.okhttp.logging.interceptor)
    testImplementation(libs.okhttp.mockwebserver)

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

    // Coroutines Test
    testImplementation(libs.kotlinx.coroutines.test)

    // Mockk
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.agent)
    androidTestImplementation(libs.mockk.agent)
    androidTestImplementation(libs.mockk.android)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Google Map
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // Google Place
    implementation(libs.places)
    implementation(platform(libs.kotlin.bom))

    // View Pager2
    implementation(libs.androidx.viewpager2)
    implementation(libs.dotsindicator)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // Lottie
    implementation(libs.lottie)

    // CameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extension)
}

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"

    ignoreList.add("keyToIgnore")
    ignoreList.add("sdk.*")
}

tasks.withType<Test> {
    testLogging {
        events("started", "passed", "skipped", "failed", "standardError", "standardOut")
        exceptionFormat = TestExceptionFormat.FULL
    }
}
