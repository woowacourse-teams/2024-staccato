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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(platform(libs.kotlin.bom))

    // Android
    implementation(libs.androidx.core.ktx)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization.converter)
    implementation(libs.retrofit.converter.scalars)

    // Kotlinx-Serialization
    implementation(libs.kotlinx.serialization.json)

    // OkHttp
    implementation(libs.okhttp.logging.interceptor)
    testImplementation(libs.okhttp.mockwebserver)

    // Google Play Service Location
    implementation(libs.play.services.location)

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

    // MockK
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.agent)

    implementation(project(":domain"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}
