package com.on.staccato.data.module

import com.on.staccato.data.comment.CommentDataSource
import com.on.staccato.data.comment.CommentRemoteDataSource
import com.on.staccato.data.login.LoginDataSource
import com.on.staccato.data.login.LoginRemoteDataSource
import com.on.staccato.data.memory.MemoryDataSource
import com.on.staccato.data.memory.MemoryRemoteDataSource
import com.on.staccato.data.staccato.MomentDataSource
import com.on.staccato.data.staccato.MomentRemoteDataSource
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
    abstract fun bindMomentDataSource(momentRemoteDataSource: MomentRemoteDataSource): MomentDataSource

    @Binds
    abstract fun bindTimelineDataSource(timelineRemoteDataSource: TimelineRemoteDataSource): TimelineDataSource
}
