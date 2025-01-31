package com.on.staccato

import android.app.Application
import com.google.android.libraries.places.api.net.PlacesClient
import com.on.staccato.data.PlacesClientProvider
import com.on.staccato.data.StaccatoClient
import com.on.staccato.data.UserInfoPreferencesManager
import dagger.hilt.android.HiltAndroidApp
import retrofit2.Retrofit

@HiltAndroidApp
class StaccatoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        retrofit = StaccatoClient.initialize()
        userInfoPrefsManager = UserInfoPreferencesManager(applicationContext)
        placesClient = PlacesClientProvider.getClient(this)
    }

    companion object {
        lateinit var retrofit: Retrofit
        lateinit var userInfoPrefsManager: UserInfoPreferencesManager
        lateinit var placesClient: PlacesClient
    }
}
