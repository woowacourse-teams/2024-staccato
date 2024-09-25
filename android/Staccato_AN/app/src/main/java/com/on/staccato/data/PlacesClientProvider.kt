package com.on.staccato.data

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.on.staccato.BuildConfig

object PlacesClientProvider {
    @Volatile
    private var placesClient: PlacesClient? = null
    private const val API_KEY = BuildConfig.MAPS_API_KEY

    fun getClient(context: Context): PlacesClient {
        return placesClient ?: synchronized(this) {
            placesClient ?: run {
                Places.initializeWithNewPlacesApiEnabled(context.applicationContext, API_KEY)
                Places.createClient(context.applicationContext).also { placesClient = it }
            }
        }
    }
}
