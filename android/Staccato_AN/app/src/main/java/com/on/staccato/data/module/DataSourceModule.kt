package com.on.staccato.data.module

import com.on.staccato.data.UserInfoPreferencesManager
import com.on.staccato.data.comment.CommentDataSource
import com.on.staccato.data.comment.CommentRemoteDataSource
import com.on.staccato.data.login.LoginDataSource
import com.on.staccato.data.login.LoginRemoteDataSource
import com.on.staccato.data.memory.MemoryDataSource
import com.on.staccato.data.memory.MemoryRemoteDataSource
import com.on.staccato.data.mypage.MyPageLocalDataSource
import com.on.staccato.data.mypage.MyPageRemoteDataSource
import com.on.staccato.data.mypage.MyPageRemoteDataSourceImpl
import com.on.staccato.data.staccato.StaccatoDataSource
import com.on.staccato.data.staccato.StaccatoRemoteDataSource
import com.on.staccato.data.timeline.TimelineDataSource
import com.on.staccato.data.timeline.TimelineRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
abstract class DataSourceModule {
    @Binds
    abstract fun bindCommentDataSource(commentRemoteDataSource: CommentRemoteDataSource): CommentDataSource

    @Binds
    abstract fun bindLoginDataSource(loginRemoteDataSource: LoginRemoteDataSource): LoginDataSource

    @Binds
    abstract fun bindMemoryDataSource(memoryRemoteDataSource: MemoryRemoteDataSource): MemoryDataSource

    @Binds
    abstract fun bindStaccatoDataSource(staccatoRemoteDataSource: StaccatoRemoteDataSource): StaccatoDataSource

    @Binds
    abstract fun bindTimelineDataSource(timelineRemoteDataSource: TimelineRemoteDataSource): TimelineDataSource

    @Binds
    abstract fun bindMyPageLocalDataSource(myPageLocalDataSource: UserInfoPreferencesManager): MyPageLocalDataSource

    @Binds
    abstract fun bindMyPageRemoteDataSource(myPageRemoteDataSource: MyPageRemoteDataSourceImpl): MyPageRemoteDataSource
}
