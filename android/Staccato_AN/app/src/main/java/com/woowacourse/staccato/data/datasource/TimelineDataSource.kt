package com.woowacourse.staccato.data.datasource

import com.woowacourse.staccato.data.dto.timeline.TimelineResponse

interface TimelineDataSource {
    suspend fun loadAll(): Result<TimelineResponse>

    suspend fun loadByYear(year: Int): Result<TimelineResponse>
}
