package com.on.staccato.data.timeline

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.mapper.toCategoryCandidates
import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.handle
import com.on.staccato.domain.model.CategoryCandidates
import com.on.staccato.domain.model.Timeline
import com.on.staccato.domain.repository.TimelineRepository
import javax.inject.Inject

class TimelineDefaultRepository
    @Inject
    constructor(
        private val timelineDataSource: TimelineDataSource,
    ) : TimelineRepository {
        override suspend fun getTimeline(): ApiResult<Timeline> = timelineDataSource.getAllTimeline().handle { it.toDomain() }

        override suspend fun getCategoryCandidates(): ApiResult<CategoryCandidates> =
            timelineDataSource.getAllTimeline().handle {
                it.toCategoryCandidates()
            }
    }
