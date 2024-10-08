package com.on.staccato.domain.repository

import com.on.staccato.data.ResponseResult
import com.on.staccato.domain.model.MemoryCandidates
import com.on.staccato.domain.model.Timeline

interface TimelineRepository {
    suspend fun getTimeline(): ResponseResult<Timeline>

    suspend fun getMemoryCandidates(): ResponseResult<MemoryCandidates>
}
