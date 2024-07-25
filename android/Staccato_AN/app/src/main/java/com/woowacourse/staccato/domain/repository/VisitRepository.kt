package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.domain.model.Visit

interface VisitRepository {
    suspend fun loadVisit(visitId: Long): Result<Visit>

    suspend fun createVisit(
        pinId: Long,
        visitImages: List<String>,
        visitedAt: String,
        travelId: Long,
    ): Result<Unit>

    suspend fun updateVisit(
        visitImages: List<String>,
        visitedAt: String,
    ): Result<Unit>
}
