package com.on.staccato

import android.app.Application
import android.app.NotificationManager
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.libraries.places.api.Places
import com.on.staccato.presentation.notification.model.ChannelType
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StaccatoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        registerNotificationChannel()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Places.initializeWithNewPlacesApiEnabled(this, BuildConfig.MAPS_API_KEY)
    }

    private fun registerNotificationChannel() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannels(ChannelType.getAllChannels(applicationContext))
    }
}
