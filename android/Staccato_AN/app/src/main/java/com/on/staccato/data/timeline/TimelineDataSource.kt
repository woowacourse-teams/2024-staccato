package com.on.staccato.data.timeline

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.timeline.TimelineResponse

interface TimelineDataSource {
    suspend fun getAllTimeline(): ApiResult<TimelineResponse>

    suspend fun getTimeline(year: Int): ApiResult<TimelineResponse>
}
