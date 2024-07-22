// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.kotlinKapt) apply false
    alias(libs.plugins.kotlinSerialization) apply false
}

buildscript {
    dependencies {
        classpath(libs.google.maps.secrets.gradle.plugin)
    }
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}
