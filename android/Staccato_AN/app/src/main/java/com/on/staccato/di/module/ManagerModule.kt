package com.on.staccato.di.module

import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import com.on.staccato.presentation.common.share.ShareManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext

@InstallIn(ActivityComponent::class)
@Module
object ManagerModule {
    @Provides
    fun provideShareManager(
        @ActivityContext context: Context,
    ): ShareManager = ShareManager(context)

    @Provides
    fun provideClipboardManager(
        @ApplicationContext context: Context,
    ): ClipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
}
