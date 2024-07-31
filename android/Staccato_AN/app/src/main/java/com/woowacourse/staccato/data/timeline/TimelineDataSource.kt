package com.woowacourse.staccato.data.timeline

import com.woowacourse.staccato.data.dto.timeline.TimelineResponse

interface TimelineDataSource {
    suspend fun fetchAll(): Result<TimelineResponse>

    suspend fun fetchByYear(year: Int): Result<TimelineResponse>
}
