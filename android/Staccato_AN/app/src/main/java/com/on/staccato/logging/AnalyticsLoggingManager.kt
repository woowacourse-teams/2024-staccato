package com.on.staccato.logging

import android.os.Build
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.on.staccato.util.logging.AnalyticsEvent
import com.on.staccato.util.logging.LoggingManager
import com.on.staccato.util.logging.Param
import com.on.staccato.util.logging.Param.Companion.KEY_SDK_VERSION
import com.on.staccato.util.logging.putInto

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
                event.params.forEach { param ->
                    param.putInto(this)
                }
            }

        firebaseAnalytics.logEvent(event.name, param)
    }
}
