package com.woowacourse.staccato

import android.app.Application
import com.woowacourse.staccato.data.UserInfoPreferencesManager

class StaccatoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        userInfoPrefsManager = UserInfoPreferencesManager(applicationContext)
    }

    companion object {
        lateinit var userInfoPrefsManager: UserInfoPreferencesManager
    }
}
