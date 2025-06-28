package com.on.staccato

import android.app.Application
import android.app.NotificationManager
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.libraries.places.api.net.PlacesClient
import com.on.staccato.data.PlacesClientProvider
import com.on.staccato.presentation.common.notification.ChannelType
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StaccatoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        registerNotificationChannel()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        placesClient = PlacesClientProvider.getClient(this)
    }

    private fun registerNotificationChannel() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannels(ChannelType.getAllChannels(applicationContext))
    }

    companion object {
        lateinit var placesClient: PlacesClient
    }
}
