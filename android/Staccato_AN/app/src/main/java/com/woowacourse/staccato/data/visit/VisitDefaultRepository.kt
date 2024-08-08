package com.woowacourse.staccato.data.visit

import com.woowacourse.staccato.data.dto.mapper.toDomain
import com.woowacourse.staccato.data.dto.visit.VisitCreationRequest
import com.woowacourse.staccato.data.dto.visit.VisitUpdateRequest
import com.woowacourse.staccato.domain.model.Visit
import com.woowacourse.staccato.domain.repository.VisitRepository
import retrofit2.Response

class VisitDefaultRepository(private val remoteDataSource: VisitRemoteDataSource) :
    VisitRepository {
    override suspend fun getVisit(visitId: Long): Result<Visit> {
        return runCatching {
            remoteDataSource.fetchVisit(visitId).toDomain()
        }
    }

    override suspend fun createVisit(
        pinId: Long,
        visitImageUrls: List<String>,
        visitedAt: String,
        travelId: Long,
    ): Result<Response<String>> {
        return runCatching {
            remoteDataSource.createVisit(
                VisitCreationRequest(
                    pinId = pinId,
                    visitImageUrls = visitImageUrls,
                    visitedAt = visitedAt,
                    travelId = travelId,
                ),
            )
        }
    }

    override suspend fun updateVisit(
        visitImageUrls: List<String>,
        visitedAt: String,
    ): Result<Unit> {
        return runCatching {
            remoteDataSource.updateVisit(
                VisitUpdateRequest(
                    visitImageUrls = visitImageUrls,
                    visitedAt = visitedAt,
                ),
            )
        }
    }
}
