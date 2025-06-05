package com.on.staccato.di.module

import com.on.staccato.data.category.CategoryApiService
import com.on.staccato.data.comment.CommentApiService
import com.on.staccato.data.image.ImageApiService
import com.on.staccato.data.invitation.InvitationApiService
import com.on.staccato.data.login.LoginApiService
import com.on.staccato.data.member.MemberApiService
import com.on.staccato.data.mypage.MyPageApiService
import com.on.staccato.data.notification.NotificationApiService
import com.on.staccato.data.staccato.StaccatoApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiServiceModule {
    @Singleton
    @Provides
    fun provideLoginApiService(retrofit: Retrofit): LoginApiService = retrofit.create(LoginApiService::class.java)

    @Singleton
    @Provides
    fun provideCategoryApiService(retrofit: Retrofit): CategoryApiService = retrofit.create(CategoryApiService::class.java)

    @Singleton
    @Provides
    fun provideStaccatoApiService(retrofit: Retrofit): StaccatoApiService = retrofit.create(StaccatoApiService::class.java)

    @Singleton
    @Provides
    fun provideCommentApiService(retrofit: Retrofit): CommentApiService = retrofit.create(CommentApiService::class.java)

    @Singleton
    @Provides
    fun provideImageApiService(retrofit: Retrofit): ImageApiService = retrofit.create(ImageApiService::class.java)

    @Singleton
    @Provides
    fun memberApiService(retrofit: Retrofit): MemberApiService = retrofit.create(MemberApiService::class.java)

    @Singleton
    @Provides
    fun myPageApiService(retrofit: Retrofit): MyPageApiService = retrofit.create(MyPageApiService::class.java)

    @Singleton
    @Provides
    fun invitationApiService(retrofit: Retrofit): InvitationApiService = retrofit.create(InvitationApiService::class.java)

    @Singleton
    @Provides
    fun provideNotificationService(retrofit: Retrofit): NotificationApiService = retrofit.create(NotificationApiService::class.java)
}
