package com.on.staccato.data.timeline

import com.on.staccato.data.category.CategoryApiService
import com.on.staccato.data.dto.timeline.TimelineResponse
import com.on.staccato.data.network.ApiResult
import javax.inject.Inject

class TimelineRemoteDataSource
    @Inject
    constructor(
        private val categoryApiService: CategoryApiService,
    ) : TimelineDataSource {
        override suspend fun getTimeline(
            sort: String?,
            filter: String?,
        ): ApiResult<TimelineResponse> = categoryApiService.getCategories(sort, filter)
    }
