package com.on.staccato.di.module

import com.on.staccato.data.category.CategoryDataSource
import com.on.staccato.data.category.CategoryRemoteDataSource
import com.on.staccato.data.comment.CommentDataSource
import com.on.staccato.data.comment.CommentRemoteDataSource
import com.on.staccato.data.location.LocationDataSource
import com.on.staccato.data.location.LocationLocalDataSource
import com.on.staccato.data.login.LoginDataSource
import com.on.staccato.data.login.LoginRemoteDataSource
import com.on.staccato.data.member.MemberDataSource
import com.on.staccato.data.member.MemberLocalDataSource
import com.on.staccato.data.mypage.MyPageDataSource
import com.on.staccato.data.mypage.MyPageRemoteDataSource
import com.on.staccato.data.staccato.StaccatoDataSource
import com.on.staccato.data.staccato.StaccatoRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class DataSourceModule {
    @Binds
    abstract fun bindCommentDataSource(commentRemoteDataSource: CommentRemoteDataSource): CommentDataSource

    @Binds
    abstract fun bindLoginDataSource(loginRemoteDataSource: LoginRemoteDataSource): LoginDataSource

    @Binds
    abstract fun bindCategoryDataSource(categoryRemoteDataSource: CategoryRemoteDataSource): CategoryDataSource

    @Binds
    abstract fun bindStaccatoDataSource(staccatoRemoteDataSource: StaccatoRemoteDataSource): StaccatoDataSource

    @Binds
    abstract fun bindMemberLocalDataSource(memberLocalDataSource: MemberLocalDataSource): MemberDataSource

    @Binds
    abstract fun bindMyPageRemoteDataSource(myPageRemoteDataSource: MyPageRemoteDataSource): MyPageDataSource

    @Binds
    abstract fun bindLocationLocalDataSource(locationLocalDataSource: LocationLocalDataSource): LocationDataSource
}
