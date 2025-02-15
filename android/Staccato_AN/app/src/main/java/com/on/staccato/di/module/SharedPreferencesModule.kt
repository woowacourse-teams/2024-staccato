package com.on.staccato.di.module

import android.content.Context
import com.on.staccato.data.UserInfoPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedPreferencesModule {
    @Singleton
    @Provides
    fun provideMemberProfileManager(
        @ApplicationContext context: Context,
    ): UserInfoPreferencesManager = UserInfoPreferencesManager(context)
}
