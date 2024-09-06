package com.on.staccato.data.timeline

import com.on.staccato.data.ApiResponseHandler.handleApiResponse
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.StaccatoClient
import com.on.staccato.data.dto.timeline.TimelineResponse

class TimelineRemoteDataSource(
    private val timelineApiService: TimeLineApiService = StaccatoClient.timelineService,
) : TimelineDataSource {
    override suspend fun getAllTimeline(): ResponseResult<TimelineResponse> {
        return fetchTimeline()
    }

    override suspend fun getTimeline(year: Int): ResponseResult<TimelineResponse> {
        return fetchTimeline(year)
    }

    private suspend fun fetchTimeline(year: Int? = null): ResponseResult<TimelineResponse> {
        return handleApiResponse { timelineApiService.getTimeline(year) }
    }
}
