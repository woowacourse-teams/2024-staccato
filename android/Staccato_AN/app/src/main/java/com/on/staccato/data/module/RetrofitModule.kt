package com.on.staccato.data.module

import com.on.staccato.data.StaccatoClient
import com.on.staccato.data.comment.CommentApiService
import com.on.staccato.data.image.ImageApiService
import com.on.staccato.data.login.LoginApiService
import com.on.staccato.data.member.MemberApiService
import com.on.staccato.data.memory.MemoryApiService
import com.on.staccato.data.mypage.MyPageApiService
import com.on.staccato.data.staccato.MomentApiService
import com.on.staccato.data.timeline.TimeLineApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RetrofitModule {
    @Singleton
    @Provides
    fun provideLoginApiService(): LoginApiService = StaccatoClient.create(LoginApiService::class.java)

    @Singleton
    @Provides
    fun provideMemoryApiService(): MemoryApiService = StaccatoClient.create(MemoryApiService::class.java)

    @Singleton
    @Provides
    fun provideTimelineApiService(): TimeLineApiService = StaccatoClient.create(TimeLineApiService::class.java)

    @Singleton
    @Provides
    fun provideStaccatoApiService(): MomentApiService = StaccatoClient.create(MomentApiService::class.java)

    @Singleton
    @Provides
    fun provideCommentApiService(): CommentApiService = StaccatoClient.create(CommentApiService::class.java)

    @Singleton
    @Provides
    fun provideImageApiService(): ImageApiService = StaccatoClient.create(ImageApiService::class.java)

    @Singleton
    @Provides
    fun memberApiService(): MemberApiService = StaccatoClient.create(MemberApiService::class.java)

    @Singleton
    @Provides
    fun myPageApiService(): MyPageApiService = StaccatoClient.create(MyPageApiService::class.java)
}
