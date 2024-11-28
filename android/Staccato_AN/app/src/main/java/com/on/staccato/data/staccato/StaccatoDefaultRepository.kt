package com.on.staccato.data.staccato

import com.on.staccato.data.Exception
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.ServerError
import com.on.staccato.data.Success
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
                is Exception ->
                    Exception(
                        responseResult.e,
                        EXCEPTION_NETWORK_ERROR_MESSAGE,
                    )

                is ServerError ->
                    ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is Success -> Success(responseResult.data.staccatoLocationResponses.map { it.toDomain() })
            }
        }

        override suspend fun getStaccato(staccatoId: Long): ResponseResult<Staccato> {
            return when (val responseResult = remoteDataSource.fetchStaccato(staccatoId)) {
                is Exception ->
                    Exception(
                        responseResult.e,
                        EXCEPTION_NETWORK_ERROR_MESSAGE,
                    )

                is ServerError ->
                    ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is Success -> Success(responseResult.data.toDomain())
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
        ): ResponseResult<StaccatoCreationResponse> {
            return when (
                val responseResult =
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
            ) {
                is Exception ->
                    Exception(
                        responseResult.e,
                        EXCEPTION_NETWORK_ERROR_MESSAGE,
                    )

                is ServerError ->
                    ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is Success -> Success(responseResult.data)
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
        ): ResponseResult<Unit> {
            return when (
                val responseResult =
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
            ) {
                is Exception ->
                    Exception(
                        responseResult.e,
                        EXCEPTION_NETWORK_ERROR_MESSAGE,
                    )

                is ServerError ->
                    ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is Success -> Success(responseResult.data)
            }
        }

        override suspend fun deleteStaccato(staccatoId: Long): ResponseResult<Unit> {
            return when (val responseResult = remoteDataSource.deleteStaccato(staccatoId)) {
                is Exception ->
                    Exception(
                        responseResult.e,
                        EXCEPTION_NETWORK_ERROR_MESSAGE,
                    )

                is ServerError ->
                    ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is Success -> Success(responseResult.data)
            }
        }

        override suspend fun updateFeeling(
            staccatoId: Long,
            feeling: String,
        ): ResponseResult<Unit> {
            return when (
                val responseResult =
                    remoteDataSource.updateFeeling(
                        staccatoId = staccatoId,
                        feelingRequest = FeelingRequest(feeling),
                    )
            ) {
                is Exception ->
                    Exception(
                        responseResult.e,
                        EXCEPTION_NETWORK_ERROR_MESSAGE,
                    )

                is ServerError ->
                    ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is Success -> Success(responseResult.data)
            }
        }

        companion object {
            private const val EXCEPTION_NETWORK_ERROR_MESSAGE = "네트워크 연결이 불안정합니다.\n연결을 재설정한 후 다시 시도해 주세요."
        }
    }
