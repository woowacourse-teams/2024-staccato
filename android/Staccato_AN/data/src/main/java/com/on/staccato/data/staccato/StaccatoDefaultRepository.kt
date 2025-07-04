package com.on.staccato.data.staccato

import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.dto.mapper.toFeelingRequest
import com.on.staccato.data.dto.staccato.StaccatoCreationRequest
import com.on.staccato.data.dto.staccato.StaccatoUpdateRequest
import com.on.staccato.data.network.handle
import com.on.staccato.domain.ApiResult
import com.on.staccato.domain.model.Feeling
import com.on.staccato.domain.model.Staccato
import com.on.staccato.domain.model.StaccatoMarker
import com.on.staccato.domain.model.StaccatoShareLink
import com.on.staccato.domain.repository.StaccatoRepository
import java.time.LocalDateTime
import javax.inject.Inject

class StaccatoDefaultRepository
    @Inject
    constructor(
        private val remoteDataSource: StaccatoRemoteDataSource,
    ) :
    StaccatoRepository {
        override suspend fun getStaccatoMarkers(): ApiResult<List<StaccatoMarker>> =
            remoteDataSource.fetchStaccatoMarkers().handle {
                it.staccatoMarkerResponses.map { dto -> dto.toDomain() }
            }

        override suspend fun getStaccato(staccatoId: Long): ApiResult<Staccato> =
            remoteDataSource.fetchStaccato(staccatoId).handle { staccatoResponse -> staccatoResponse.toDomain() }

        override suspend fun createStaccato(
            categoryId: Long,
            staccatoTitle: String,
            placeName: String,
            latitude: Double,
            longitude: Double,
            address: String,
            visitedAt: LocalDateTime,
            staccatoImageUrls: List<String>,
        ): ApiResult<Long> =
            remoteDataSource.createStaccato(
                StaccatoCreationRequest(
                    categoryId = categoryId,
                    staccatoTitle = staccatoTitle,
                    placeName = placeName,
                    latitude = latitude,
                    longitude = longitude,
                    address = address,
                    visitedAt = visitedAt.toString(),
                    staccatoImageUrls = staccatoImageUrls,
                ),
            ).handle { it.staccatoId }

        override suspend fun createStaccatoShareLink(staccatoId: Long): ApiResult<StaccatoShareLink> =
            remoteDataSource.createStaccatoShareLink(staccatoId).handle { it.toDomain() }

        override suspend fun updateStaccato(
            staccatoId: Long,
            staccatoTitle: String,
            placeName: String,
            address: String,
            latitude: Double,
            longitude: Double,
            visitedAt: LocalDateTime,
            categoryId: Long,
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
                        categoryId = categoryId,
                        staccatoImageUrls = staccatoImageUrls,
                    ),
            ).handle()

        override suspend fun deleteStaccato(staccatoId: Long): ApiResult<Unit> = remoteDataSource.deleteStaccato(staccatoId).handle()

        override suspend fun updateFeeling(
            staccatoId: Long,
            feeling: Feeling,
        ): ApiResult<Unit> =
            remoteDataSource.updateFeeling(
                staccatoId = staccatoId,
                feelingRequest = feeling.toFeelingRequest(),
            ).handle()
    }
