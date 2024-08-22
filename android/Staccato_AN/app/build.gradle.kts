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
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
    alias(libs.plugins.mapsplatformSecretsGradlePlugin)
}

android {
    namespace = "com.woowacourse.staccato"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.woowacourse.staccato"
        minSdk = 26
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", "${localProperties["base_url"]}")
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
        release {
            isMinifyEnabled = false
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
    implementation(libs.converter.scalars)

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

    // Google Map
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // View Pager2
    implementation(libs.androidx.viewpager2)
}

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"

    ignoreList.add("keyToIgnore")
    ignoreList.add("sdk.*")
}
