package com.on.staccato.data.timeline

import com.on.staccato.data.category.CategoryDataSource
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
        private val categoryDataSource: CategoryDataSource,
    ) : TimelineRepository {
        override suspend fun getTimeline(
            sort: String?,
            filter: String?,
        ): ApiResult<Timeline> =
            categoryDataSource.getCategories(sort, filter)
                .handle { it.toDomain() }

        override suspend fun getCategoryCandidates(): ApiResult<CategoryCandidates> =
            categoryDataSource.getCategories().handle {
                it.toCategoryCandidates()
            }
    }
