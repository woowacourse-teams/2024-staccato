package com.on.staccato.data.timeline

import com.on.staccato.data.dto.timeline.TimelineResponse
import com.on.staccato.data.network.ApiResult

interface TimelineDataSource {
    suspend fun getTimeline(
        sort: String? = null,
        filter: String? = null,
    ): ApiResult<TimelineResponse>
}
