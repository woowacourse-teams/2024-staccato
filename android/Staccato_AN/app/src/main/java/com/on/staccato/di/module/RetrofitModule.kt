package com.on.staccato.di.module

import com.on.staccato.StaccatoApplication.Companion.retrofit
import com.on.staccato.data.category.CategoryApiService
import com.on.staccato.data.comment.CommentApiService
import com.on.staccato.data.image.ImageApiService
import com.on.staccato.data.invitation.InvitationApiService
import com.on.staccato.data.login.LoginApiService
import com.on.staccato.data.member.MemberApiService
import com.on.staccato.data.mypage.MyPageApiService
import com.on.staccato.data.staccato.StaccatoApiService
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
    fun provideLoginApiService(): LoginApiService = retrofit.create(LoginApiService::class.java)

    @Singleton
    @Provides
    fun provideCategoryApiService(): CategoryApiService = retrofit.create(CategoryApiService::class.java)

    @Singleton
    @Provides
    fun provideStaccatoApiService(): StaccatoApiService = retrofit.create(StaccatoApiService::class.java)

    @Singleton
    @Provides
    fun provideCommentApiService(): CommentApiService = retrofit.create(CommentApiService::class.java)

    @Singleton
    @Provides
    fun provideImageApiService(): ImageApiService = retrofit.create(ImageApiService::class.java)

    @Singleton
    @Provides
    fun memberApiService(): MemberApiService = retrofit.create(MemberApiService::class.java)

    @Singleton
    @Provides
    fun myPageApiService(): MyPageApiService = retrofit.create(MyPageApiService::class.java)

    @Singleton
    @Provides
    fun invitationApiService(): InvitationApiService = retrofit.create(InvitationApiService::class.java)
}
