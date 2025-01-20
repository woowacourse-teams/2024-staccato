package com.on.staccato.data.staccato

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.dto.staccato.FeelingRequest
import com.on.staccato.data.dto.staccato.StaccatoCreationRequest
import com.on.staccato.data.dto.staccato.StaccatoCreationResponse
import com.on.staccato.data.dto.staccato.StaccatoUpdateRequest
import com.on.staccato.data.handle
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
        override suspend fun getStaccatos(): ApiResult<List<StaccatoLocation>> =
            remoteDataSource.fetchStaccatos().handle {
                it.staccatoLocationResponses.map { dto -> dto.toDomain() }
            }

        override suspend fun getStaccato(staccatoId: Long): ApiResult<Staccato> =
            remoteDataSource.fetchStaccato(staccatoId).handle { staccatoResponse -> staccatoResponse.toDomain() }

        override suspend fun createStaccato(
            memoryId: Long,
            staccatoTitle: String,
            placeName: String,
            latitude: Double,
            longitude: Double,
            address: String,
            visitedAt: LocalDateTime,
            staccatoImageUrls: List<String>,
        ): ApiResult<StaccatoCreationResponse> =
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
            ).handle { it }

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
        ): ApiResult<Unit> =
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
            ).handle()

        override suspend fun deleteStaccato(staccatoId: Long): ApiResult<Unit> = remoteDataSource.deleteStaccato(staccatoId).handle()

        override suspend fun updateFeeling(
            staccatoId: Long,
            feeling: String,
        ): ApiResult<Unit> =
            remoteDataSource.updateFeeling(
                staccatoId = staccatoId,
                feelingRequest = FeelingRequest(feeling),
            ).handle()
    }
