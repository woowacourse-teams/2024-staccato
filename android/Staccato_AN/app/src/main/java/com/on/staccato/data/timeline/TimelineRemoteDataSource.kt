package com.on.staccato.data.timeline

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.timeline.TimelineResponse
import javax.inject.Inject

class TimelineRemoteDataSource
    @Inject
    constructor(
        private val timelineApiService: TimeLineApiService,
    ) : TimelineDataSource {
        override suspend fun getAllTimeline(): ApiResult<TimelineResponse> {
            return fetchTimeline()
        }

        override suspend fun getTimeline(year: Int): ApiResult<TimelineResponse> {
            return fetchTimeline(year)
        }

        private suspend fun fetchTimeline(year: Int? = null): ApiResult<TimelineResponse> = timelineApiService.getTimeline(year)
    }
