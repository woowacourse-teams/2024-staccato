package com.woowacourse.staccato.data.timeline

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.timeline.TimelineResponse

interface TimelineDataSource {
    suspend fun getAllTimeline(): ResponseResult<TimelineResponse>

    suspend fun getTimeline(year: Int): ResponseResult<TimelineResponse>
}
