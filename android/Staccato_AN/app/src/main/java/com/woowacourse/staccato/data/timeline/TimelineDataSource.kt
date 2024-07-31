package com.woowacourse.staccato.data.timeline

import com.woowacourse.staccato.data.dto.timeline.TimelineResponse

interface TimelineDataSource {
    suspend fun getAllTimeline(): Result<TimelineResponse>

    suspend fun getTimeline(year: Int): Result<TimelineResponse>
}
