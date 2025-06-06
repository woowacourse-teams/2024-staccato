import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import java.io.FileInputStream
import java.util.Properties

val localProperties =
    Properties().apply {
        load(FileInputStream(rootProject.file("local.properties")))
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
        versionCode = 12
        versionName = "2.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] =
            "de.mannodermaus.junit5.AndroidJUnit5Builder"

        buildConfigField("String", "TOKEN", "${localProperties["token"]}")
    }

    val signingFile = rootProject.file("app/.signing/keystore.properties")
    val releaseSigningConfig =
        if (signingFile.exists()) {
            val keystoreProperties =
                Properties().apply {
                    load(FileInputStream(signingFile))
                }

            signingConfigs.create("release") {
                storeFile = file("${keystoreProperties["store_file"]}")
                keyAlias = "${keystoreProperties["key_alias"]}"
                keyPassword = "${keystoreProperties["key_password"]}"
                storePassword = "${keystoreProperties["keystore_password"]}"
            }
        } else {
            null
        }

    buildFeatures {
        defaultConfig {
            buildConfig = true
        }
        compose = true
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
            if (releaseSigningConfig != null) {
                signingConfig = releaseSigningConfig
            }
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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
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

    // Compose 기본 설정
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))

    // Material Design 3
    implementation(libs.androidx.material3)

    // Compose core UI
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)

    // Hilt & Navigation
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)

    // Compose Coil
    implementation(libs.coil.compose)

    // Compose UI Test
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Compose ViewModel
    implementation(libs.lifecycle.viewmodel.compose)

    // Compose ConstraintLayout
    implementation(libs.androidx.constraintlayout.compose)
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
