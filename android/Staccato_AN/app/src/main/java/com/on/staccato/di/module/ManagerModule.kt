package com.on.staccato.di.module

import android.content.Context
import com.on.staccato.presentation.common.ShareManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@InstallIn(ActivityComponent::class)
@Module
object ManagerModule {
    @Provides
    fun provideShareManager(
        @ActivityContext context: Context,
    ): ShareManager = ShareManager(context)
}
