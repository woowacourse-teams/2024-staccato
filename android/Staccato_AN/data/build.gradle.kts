plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlinSerialization)
    id("kotlin-kapt")
}

android {
    namespace = "com.on.staccato.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] =
            "de.mannodermaus.junit5.AndroidJUnit5Builder"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

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
    testImplementation(libs.junit5)
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
