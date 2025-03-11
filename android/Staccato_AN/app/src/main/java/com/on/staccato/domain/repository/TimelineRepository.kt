package com.on.staccato.domain.repository

import com.on.staccato.data.ApiResult
import com.on.staccato.domain.model.CategoryCandidates
import com.on.staccato.domain.model.Timeline

interface TimelineRepository {
    suspend fun getTimeline(
        sort: String?,
        term: String?,
    ): ApiResult<Timeline>

    suspend fun getCategoryCandidates(): ApiResult<CategoryCandidates>
}
