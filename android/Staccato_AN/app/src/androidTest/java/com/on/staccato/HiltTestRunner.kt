package com.on.staccato

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        classLoader: ClassLoader?,
        name: String?,
        context: Context?,
    ): Application =
        super.newApplication(
            classLoader,
            HiltTestApplication::class.java.name,
            context,
        )
}
