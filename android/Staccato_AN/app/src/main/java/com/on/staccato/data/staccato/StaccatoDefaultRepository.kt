package com.on.staccato.data.staccato

import com.on.staccato.data.ApiResult
import com.on.staccato.data.Exception
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
        override suspend fun getStaccatos(): ApiResult<List<StaccatoLocation>> {
            return when (val responseResult = remoteDataSource.fetchStaccatos()) {
                is Exception ->
                    Exception(
                        responseResult.e,
                    )

                is ServerError ->
                    ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is Success -> Success(responseResult.data.staccatoLocationResponses.map { it.toDomain() })
            }
        }

        override suspend fun getStaccato(staccatoId: Long): ApiResult<Staccato> {
            return when (val responseResult = remoteDataSource.fetchStaccato(staccatoId)) {
                is Exception ->
                    Exception(
                        responseResult.e,
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
        ): ApiResult<StaccatoCreationResponse> {
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
        ): ApiResult<Unit> {
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
                    )

                is ServerError ->
                    ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is Success -> Success(responseResult.data)
            }
        }

        override suspend fun deleteStaccato(staccatoId: Long): ApiResult<Unit> {
            return when (val responseResult = remoteDataSource.deleteStaccato(staccatoId)) {
                is Exception ->
                    Exception(
                        responseResult.e,
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
        ): ApiResult<Unit> {
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
                    )

                is ServerError ->
                    ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is Success -> Success(responseResult.data)
            }
        }
    }
