package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.domain.model.Visit
import retrofit2.Response

interface VisitRepository {
    suspend fun loadVisit(visitId: Long): Result<Visit>

    suspend fun createVisit(
        pinId: Long,
        visitImages: List<String>,
        visitedAt: String,
        travelId: Long,
    ): Result<Response<String>>

    suspend fun updateVisit(
        visitImages: List<String>,
        visitedAt: String,
    ): Result<Unit>
}
