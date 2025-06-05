package com.on.staccato

import android.app.Application
import com.google.android.libraries.places.api.net.PlacesClient
import com.on.staccato.data.PlacesClientProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StaccatoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        placesClient = PlacesClientProvider.getClient(this)
    }

    companion object {
        lateinit var placesClient: PlacesClient
    }
}
