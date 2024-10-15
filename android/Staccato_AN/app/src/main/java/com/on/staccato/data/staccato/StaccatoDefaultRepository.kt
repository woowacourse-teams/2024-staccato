package com.on.staccato.data.staccato

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.dto.staccato.FeelingRequest
import com.on.staccato.data.dto.staccato.StaccatoCreationRequest
import com.on.staccato.data.dto.staccato.StaccatoCreationResponse
import com.on.staccato.data.dto.staccato.StaccatoUpdateRequest
import com.on.staccato.domain.model.Staccato
import com.on.staccato.domain.model.StaccatoLocation
import com.on.staccato.domain.repository.StaccatoRepository
import java.time.LocalDateTime
import javax.inject.Inject

class StaccatoDefaultRepository
    @Inject
    constructor(
        private val remoteDataSource: StaccatoRemoteDataSource,
    ) :
    StaccatoRepository {
        override suspend fun getStaccatos(): ResponseResult<List<StaccatoLocation>> {
            return when (val responseResult = remoteDataSource.fetchStaccatos()) {
                is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
                is ResponseResult.ServerError -> ResponseResult.ServerError(responseResult.status, responseResult.message)
                is ResponseResult.Success -> ResponseResult.Success(responseResult.data.momentLocationResponses.map { it.toDomain() })
            }
        }

        override suspend fun getStaccato(staccatoId: Long): Result<Staccato> {
            return runCatching {
                remoteDataSource.fetchStaccato(staccatoId).toDomain()
            }
        }

        override suspend fun createStaccato(
            memoryId: Long,
            staccatoTitle: String,
            placeName: String,
            latitude: Double,
            longitude: Double,
            address: String,
            visitedAt: LocalDateTime,
            staccatoImageUrls: List<String>,
        ): Result<StaccatoCreationResponse> {
            return runCatching {
                remoteDataSource.createStaccato(
                    StaccatoCreationRequest(
                        memoryId = memoryId,
                        staccatoTitle = staccatoTitle,
                        placeName = placeName,
                        latitude = latitude,
                        longitude = longitude,
                        address = address,
                        visitedAt = visitedAt.toString(),
                        staccatoImageUrls = staccatoImageUrls,
                    ),
                )
            }
        }

        override suspend fun updateStaccato(
            staccatoId: Long,
            staccatoTitle: String,
            placeName: String,
            address: String,
            latitude: Double,
            longitude: Double,
            visitedAt: LocalDateTime,
            memoryId: Long,
            staccatoImageUrls: List<String>,
        ): Result<Unit> {
            return runCatching {
                remoteDataSource.updateStaccato(
                    staccatoId = staccatoId,
                    staccatoUpdateRequest =
                        StaccatoUpdateRequest(
                            staccatoTitle = staccatoTitle,
                            placeName = placeName,
                            address = address,
                            latitude = latitude,
                            longitude = longitude,
                            visitedAt = visitedAt.toString(),
                            memoryId = memoryId,
                            momentImageUrls = staccatoImageUrls,
                        ),
                )
            }
        }

        override suspend fun deleteStaccato(staccatoId: Long): Result<Unit> {
            return runCatching {
                remoteDataSource.deleteStaccato(staccatoId)
            }
        }

        override suspend fun updateFeeling(
            staccatoId: Long,
            feeling: String,
        ): Result<Unit> {
            return runCatching {
                remoteDataSource.updateFeeling(
                    staccatoId = staccatoId,
                    feelingRequest = FeelingRequest(feeling),
                )
            }
        }

        companion object {
            const val ERROR_MESSAGE = "스타카토 목록을 조회할 수 없어요."
        }
    }
