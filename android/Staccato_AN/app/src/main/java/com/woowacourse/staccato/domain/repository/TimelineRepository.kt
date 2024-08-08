package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.domain.model.Timeline

interface TimelineRepository {
    suspend fun getTimeline(): ResponseResult<Timeline>
}
