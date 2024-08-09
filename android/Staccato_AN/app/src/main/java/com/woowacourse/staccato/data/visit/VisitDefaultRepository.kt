package com.woowacourse.staccato.data.visit

import com.woowacourse.staccato.data.dto.mapper.toDomain
import com.woowacourse.staccato.data.dto.visit.VisitCreationRequest
import com.woowacourse.staccato.data.dto.visit.VisitCreationResponse
import com.woowacourse.staccato.domain.model.Visit
import com.woowacourse.staccato.domain.repository.VisitRepository
import okhttp3.MultipartBody
import java.time.LocalDateTime

class VisitDefaultRepository(private val remoteDataSource: VisitRemoteDataSource) :
    VisitRepository {
    override suspend fun getVisit(visitId: Long): Result<Visit> {
        return runCatching {
            remoteDataSource.fetchVisit(visitId).toDomain()
        }
    }

    override suspend fun createVisit(
        travelId: Long,
        placeName: String,
        latitude: String,
        longitude: String,
        address: String,
        visitedAt: LocalDateTime,
        visitImageFiles: List<MultipartBody.Part>,
    ): Result<VisitCreationResponse> {
        return runCatching {
            remoteDataSource.createVisit(
                VisitCreationRequest(
                    travelId = travelId,
                    placeName = placeName,
                    latitude = latitude,
                    longitude = longitude,
                    address = address,
                    visitedAt = visitedAt.toString(),
                ),
                visitImageFiles,
            )
        }
    }

    override suspend fun updateVisit(
        visitId: Long,
        placeName: String,
        visitImageUrls: List<String>,
        visitImageMultiParts: List<MultipartBody.Part>,
    ): Result<Unit> {
        return runCatching {
            remoteDataSource.updateVisit(
                visitId = visitId,
                placeName = placeName,
                visitImageUrls = visitImageUrls,
                visitImageFiles = visitImageMultiParts,
            )
        }
    }

    override suspend fun deleteVisit(visitId: Long): Result<Unit> {
        return runCatching {
            remoteDataSource.deleteVisit(visitId)
        }
    }
}
