package com.on.staccato

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.libraries.places.api.net.PlacesClient
import com.on.staccato.data.PlacesClientProvider
import com.on.staccato.data.UserInfoPreferencesManager

class StaccatoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        userInfoPrefsManager = UserInfoPreferencesManager(applicationContext)
        placesClient = PlacesClientProvider.getClient(this)
    }

    companion object {
        lateinit var userInfoPrefsManager: UserInfoPreferencesManager
        lateinit var placesClient: PlacesClient
    }
}
