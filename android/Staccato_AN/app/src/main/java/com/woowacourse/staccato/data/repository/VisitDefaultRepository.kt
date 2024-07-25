package com.woowacourse.staccato.data.repository

import com.woowacourse.staccato.data.dto.mapper.toDomain
import com.woowacourse.staccato.data.dto.visit.VisitCreationRequest
import com.woowacourse.staccato.data.dto.visit.VisitUpdateRequest
import com.woowacourse.staccato.data.visit.RemoteVisitDataSource
import com.woowacourse.staccato.domain.model.Visit
import com.woowacourse.staccato.domain.repository.VisitRepository
import retrofit2.Response

class VisitDefaultRepository(private val remoteDataSource: RemoteVisitDataSource) :
    VisitRepository {
    override suspend fun loadVisit(visitId: Long): Result<Visit> {
        return runCatching {
            remoteDataSource.fetchVisit(visitId).toDomain()
        }
    }

    override suspend fun createVisit(
        pinId: Long,
        visitImages: List<String>,
        visitedAt: String,
        travelId: Long,
    ): Result<Response<String>> {
        return runCatching {
            remoteDataSource.createVisit(
                VisitCreationRequest(
                    pinId = pinId,
                    visitImages = visitImages,
                    visitedAt = visitedAt,
                    travelId = travelId,
                ),
            )
        }
    }

    override suspend fun updateVisit(
        visitImages: List<String>,
        visitedAt: String,
    ): Result<Unit> {
        return runCatching {
            remoteDataSource.updateVisit(
                VisitUpdateRequest(
                    visitImages = visitImages,
                    visitedAt = visitedAt,
                ),
            )
        }
    }
}
