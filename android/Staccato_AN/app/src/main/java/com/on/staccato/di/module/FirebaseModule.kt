package com.on.staccato.di.module

import com.on.staccato.util.logging.AnalyticsLoggingManager
import com.on.staccato.util.logging.LoggingManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object FirebaseModule {
    @Singleton
    @Provides
    fun provideLoggingManager(): LoggingManager = AnalyticsLoggingManager()
}
