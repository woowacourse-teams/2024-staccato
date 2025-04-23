package com.on.staccato.data.timeline

import com.on.staccato.data.dto.timeline.TimelineResponse
import com.on.staccato.data.network.ApiResult
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
