package com.on.staccato.data.timeline

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.timeline.TimelineResponse

interface TimelineDataSource {
    suspend fun getAllTimeline(): ResponseResult<TimelineResponse>

    suspend fun getTimeline(year: Int): ResponseResult<TimelineResponse>
}
