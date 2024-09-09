package com.on.staccato

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.on.staccato.data.UserInfoPreferencesManager

class StaccatoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        userInfoPrefsManager = UserInfoPreferencesManager(applicationContext)
    }

    companion object {
        lateinit var userInfoPrefsManager: UserInfoPreferencesManager
    }
}