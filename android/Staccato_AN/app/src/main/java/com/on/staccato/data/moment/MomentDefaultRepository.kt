package com.on.staccato.data.moment

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.dto.moment.FeelingRequest
import com.on.staccato.data.dto.moment.MomentCreationRequest
import com.on.staccato.data.dto.moment.MomentCreationResponse
import com.on.staccato.domain.model.Moment
import com.on.staccato.domain.model.MomentLocation
import com.on.staccato.domain.repository.MomentRepository
import java.time.LocalDateTime

class MomentDefaultRepository(private val remoteDataSource: MomentRemoteDataSource) :
    MomentRepository {
    override suspend fun getMoments(): ResponseResult<List<MomentLocation>> {
        return when (val responseResult = remoteDataSource.fetchMoments()) {
            is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
            is ResponseResult.ServerError -> ResponseResult.ServerError(responseResult.status, responseResult.message)
            is ResponseResult.Success -> ResponseResult.Success(responseResult.data.momentLocationResponses.map { it.toDomain() })
        }
    }

    override suspend fun getMoment(momentId: Long): Result<Moment> {
        return runCatching {
            remoteDataSource.fetchMoment(momentId).toDomain()
        }
    }

    override suspend fun createMoment(
        memoryId: Long,
        placeName: String,
        latitude: Double,
        longitude: Double,
        address: String,
        visitedAt: LocalDateTime,
        momentImageUrls: List<String>,
    ): Result<MomentCreationResponse> {
        return runCatching {
            remoteDataSource.createMoment(
                MomentCreationRequest(
                    memoryId = memoryId,
                    placeName = placeName,
                    latitude = latitude,
                    longitude = longitude,
                    address = address,
                    visitedAt = visitedAt.toString(),
                    momentImageUrls = momentImageUrls,
                ),
            )
        }
    }

    override suspend fun updateMoment(
        momentId: Long,
        placeName: String,
        momentImageUrls: List<String>,
    ): Result<Unit> {
        return runCatching {
            remoteDataSource.updateMoment(
                momentId = momentId,
                placeName = placeName,
                momentImageUrls = momentImageUrls,
            )
        }
    }

    override suspend fun deleteMoment(momentId: Long): Result<Unit> {
        return runCatching {
            remoteDataSource.deleteMoment(momentId)
        }
    }

    override suspend fun updateFeeling(
        momentId: Long,
        feeling: String,
    ): Result<Unit> {
        return runCatching {
            remoteDataSource.updateFeeling(
                momentId = momentId,
                feelingRequest = FeelingRequest(feeling),
            )
        }
    }

    companion object {
        const val ERROR_MESSAGE = "스타카토 목록을 조회할 수 없어요."
    }
}
