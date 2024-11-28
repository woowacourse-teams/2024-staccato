package com.on.staccato.data.timeline

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.timeline.TimelineResponse
import javax.inject.Inject

class TimelineRemoteDataSource
    @Inject
    constructor(
        private val timelineApiService: TimeLineApiService,
    ) : TimelineDataSource {
        override suspend fun getAllTimeline(): ResponseResult<TimelineResponse> {
            return fetchTimeline()
        }

        override suspend fun getTimeline(year: Int): ResponseResult<TimelineResponse> {
            return fetchTimeline(year)
        }

        private suspend fun fetchTimeline(year: Int? = null): ResponseResult<TimelineResponse> = timelineApiService.getTimeline(year)
    }
