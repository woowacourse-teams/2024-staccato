import java.util.Properties

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlinSerialization)
    id("kotlin-kapt")
}

android {
    namespace = "com.on.staccato.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] =
            "de.mannodermaus.junit5.AndroidJUnit5Builder"
        consumerProguardFiles("consumer-rules.pro")

        val secretPropsFile = rootProject.file("secret.properties")
        val localDefaultsFile = rootProject.file("local.defaults.properties")
        val props =
            Properties().apply {
                if (secretPropsFile.exists()) {
                    load(secretPropsFile.inputStream())
                } else if (localDefaultsFile.exists()) {
                    load(localDefaultsFile.inputStream())
                }
            }
        val mapsApiKey = props.getProperty("MAPS_API_KEY") ?: ""
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization.converter)
    implementation(libs.converter.scalars)

    // Kotlinx-Serialization
    implementation(libs.kotlinx.serialization.json)

    // OkHttp
    implementation(libs.okhttp.logging.interceptor)
    testImplementation(libs.okhttp.mockwebserver)

    // Google Place
    implementation(libs.places)
    implementation(platform(libs.kotlin.bom))

    // Firebase
    implementation(libs.firebase.messaging.ktx)

    // JUnit5
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.junit.vintage.engine)

    // Coroutines Test
    testImplementation(libs.kotlinx.coroutines.test)

    // AssertJ
    testImplementation(libs.assertj.core)

    // Mockk
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.agent)

    implementation(project(":domain"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}
