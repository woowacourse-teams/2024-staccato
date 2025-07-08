package com.on.staccato.util.logging

import android.os.Build
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.on.staccato.util.logging.Param.Companion.KEY_SDK_VERSION

class AnalyticsLoggingManager : LoggingManager {
    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    init {
        val sdkVersionParam =
            Bundle().apply {
                putInt(KEY_SDK_VERSION, Build.VERSION.SDK_INT)
            }

        firebaseAnalytics.setDefaultEventParameters(sdkVersionParam)
    }

    override fun <T : Any> logEvent(
        name: String,
        vararg params: Param<T>,
    ) {
        val event = AnalyticsEvent.of(name, *params)
        val param =
            Bundle().apply {
                event.params.forEach {
                    when (it.value) {
                        is String -> putString(it.key, it.value)
                        is Int -> putInt(it.key, it.value)
                        is Long -> putLong(it.key, it.value)
                        is Double -> putDouble(it.key, it.value)
                        is Boolean -> putBoolean(it.key, it.value)
                        is Throwable -> {
                            putString(it.key, "cause: ${it.value.cause}")
                            putString(it.key, "message: ${it.value.message}")
                        }
                    }
                }
            }

        firebaseAnalytics.logEvent(event.name, param)
    }
}
