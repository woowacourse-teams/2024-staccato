// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.firebaseCrashlytics) apply false
    alias(libs.plugins.mapsplatformSecretsGradlePlugin) apply false
    alias(libs.plugins.hiltAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
    alias(libs.plugins.androidTest) apply false
}

buildscript {
    dependencies {
        classpath(libs.google.maps.secrets.gradle.plugin)
    }
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}
