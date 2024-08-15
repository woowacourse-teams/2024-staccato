package com.woowacourse.staccato.data.moment

import com.woowacourse.staccato.data.dto.mapper.toDomain
import com.woowacourse.staccato.data.dto.moment.MomentCreationRequest
import com.woowacourse.staccato.data.dto.moment.MomentCreationResponse
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
        memoryId: Long,
        placeName: String,
        latitude: String,
        longitude: String,
        address: String,
        visitedAt: LocalDateTime,
        visitImageMultiParts: List<MultipartBody.Part>,
    ): Result<MomentCreationResponse> {
        return runCatching {
            remoteDataSource.createVisit(
                MomentCreationRequest(
                    memoryId = memoryId,
                    placeName = placeName,
                    latitude = latitude,
                    longitude = longitude,
                    address = address,
                    visitedAt = visitedAt.toString(),
                ),
                visitImageMultiParts,
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
