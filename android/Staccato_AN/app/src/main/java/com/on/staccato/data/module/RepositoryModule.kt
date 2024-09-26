package com.on.staccato.data.module

import com.on.staccato.data.comment.CommentDefaultRepository
import com.on.staccato.data.image.ImageDefaultRepository
import com.on.staccato.data.login.LoginDefaultRepository
import com.on.staccato.data.member.MemberDefaultRepository
import com.on.staccato.data.memory.MemoryDefaultRepository
import com.on.staccato.data.moment.MomentDefaultRepository
import com.on.staccato.data.timeline.TimelineDefaultRepository
import com.on.staccato.domain.repository.CommentRepository
import com.on.staccato.domain.repository.ImageRepository
import com.on.staccato.domain.repository.LoginRepository
import com.on.staccato.domain.repository.MemberRepository
import com.on.staccato.domain.repository.MemoryRepository
import com.on.staccato.domain.repository.MomentRepository
import com.on.staccato.domain.repository.TimelineRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
abstract class RepositoryModule {
    @Binds
    abstract fun bindCommentRepository(commentDefaultRepository: CommentDefaultRepository): CommentRepository

    @Binds
    abstract fun bindImageRepository(imageDefaultRepository: ImageDefaultRepository): ImageRepository

    @Binds
    abstract fun bindLoginRepository(loginDefaultRepository: LoginDefaultRepository): LoginRepository

    @Binds
    abstract fun bindMemoryRepository(memoryDefaultRepository: MemoryDefaultRepository): MemoryRepository

    @Binds
    abstract fun bindMomentRepository(momentDefaultRepository: MomentDefaultRepository): MomentRepository

    @Binds
    abstract fun bindTimelineRepository(timelineDefaultRepository: TimelineDefaultRepository): TimelineRepository

    @Binds
    abstract fun bindMemberRepository(memberRepository: MemberDefaultRepository): MemberRepository
}
