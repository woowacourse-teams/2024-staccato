package com.on.staccato

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.libraries.places.api.net.PlacesClient
import com.on.staccato.data.PlacesClientProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StaccatoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        placesClient = PlacesClientProvider.getClient(this)
    }

    companion object {
        lateinit var placesClient: PlacesClient
    }
}
