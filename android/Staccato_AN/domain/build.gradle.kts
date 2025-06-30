import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java-library")
    alias(libs.plugins.jetbrainsKotlinJvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    // JUnit4
    testImplementation(libs.junit)
    testImplementation(libs.junitparams)

    // JUnit5
    testImplementation(libs.junit5)
    testRuntimeOnly(libs.junit.vintage.engine)

    // AssertJ
    testImplementation(libs.assertj.core)
}
