package com.on.staccato.data.timeline

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.timeline.TimelineResponse

interface TimelineDataSource {
    suspend fun getTimeline(
        sort: String? = null,
        filter: String? = null,
    ): ApiResult<TimelineResponse>
}
