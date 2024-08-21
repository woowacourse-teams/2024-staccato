package com.woowacourse.staccato.data.moment

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.mapper.toDomain
import com.woowacourse.staccato.data.dto.moment.MomentCreationRequest
import com.woowacourse.staccato.data.dto.moment.MomentCreationResponse
import com.woowacourse.staccato.domain.model.Moment
import com.woowacourse.staccato.domain.model.MomentLocation
import com.woowacourse.staccato.domain.repository.MomentRepository
import okhttp3.MultipartBody
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
        latitude: String,
        longitude: String,
        address: String,
        visitedAt: LocalDateTime,
        momentImageMultiParts: List<MultipartBody.Part>,
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
                ),
                momentImageMultiParts,
            )
        }
    }

    override suspend fun updateMoment(
        momentId: Long,
        placeName: String,
        momentImageUrls: List<String>,
        momentImageMultiParts: List<MultipartBody.Part>,
    ): Result<Unit> {
        return runCatching {
            remoteDataSource.updateMoment(
                momentId = momentId,
                placeName = placeName,
                momentImageUrls = momentImageUrls,
                momentImageFiles = momentImageMultiParts,
            )
        }
    }

    override suspend fun deleteMoment(momentId: Long): Result<Unit> {
        return runCatching {
            remoteDataSource.deleteMoment(momentId)
        }
    }

    companion object {
        const val ERROR_MESSAGE = "예기치 않은 오류가 발생했습니다"
    }
}
