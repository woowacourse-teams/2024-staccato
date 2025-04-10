package com.on.staccato.di.module

import com.on.staccato.presentation.common.ShareManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
object ManagerModule {
    @Provides
    fun provideShareManager(): ShareManager = ShareManager()
}
