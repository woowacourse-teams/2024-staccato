package com.woowacourse.staccato

import android.app.Application
import com.google.firebase.FirebaseApp

class StaccatoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
