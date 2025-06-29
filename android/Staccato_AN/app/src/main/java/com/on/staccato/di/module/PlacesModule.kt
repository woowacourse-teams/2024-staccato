package com.on.staccato.di.module

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.on.staccato.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlacesModule {
    private const val MAPS_API_KEY: String = BuildConfig.MAPS_API_KEY

    @Provides
    @Singleton
    fun providePlacesClient(
        @ApplicationContext context: Context,
    ): PlacesClient {
        Places.initializeWithNewPlacesApiEnabled(context.applicationContext, MAPS_API_KEY)
        return Places.createClient(context.applicationContext)
    }
}
