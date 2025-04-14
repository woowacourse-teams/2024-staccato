package com.on.staccato.data.timeline

import com.on.staccato.data.dto.mapper.toCategoryCandidates
import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.network.ApiResult
import com.on.staccato.data.network.handle
import com.on.staccato.domain.model.CategoryCandidates
import com.on.staccato.domain.model.Timeline
import com.on.staccato.domain.repository.TimelineRepository
import javax.inject.Inject

class TimelineDefaultRepository
    @Inject
    constructor(
        private val timelineDataSource: TimelineDataSource,
    ) : TimelineRepository {
        override suspend fun getTimeline(
            sort: String?,
            filter: String?,
        ): ApiResult<Timeline> =
            timelineDataSource.getTimeline(sort, filter)
                .handle { it.toDomain() }

        override suspend fun getCategoryCandidates(): ApiResult<CategoryCandidates> =
            timelineDataSource.getTimeline().handle {
                it.toCategoryCandidates()
            }
    }
