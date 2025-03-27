package com.on.staccato.data.timeline

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.timeline.TimelineResponse
import javax.inject.Inject

class TimelineRemoteDataSource
    @Inject
    constructor(
        private val timelineApiService: TimeLineApiService,
    ) : TimelineDataSource {
        override suspend fun getTimeline(
            sort: String?,
            filter: String?,
        ): ApiResult<TimelineResponse> = timelineApiService.getTimeline(sort, filter)
    }
