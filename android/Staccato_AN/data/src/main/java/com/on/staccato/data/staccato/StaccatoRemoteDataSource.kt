package com.on.staccato.data.staccato

import com.on.staccato.data.dto.staccato.FeelingRequest
import com.on.staccato.data.dto.staccato.StaccatoCreationRequest
import com.on.staccato.data.dto.staccato.StaccatoCreationResponse
import com.on.staccato.data.dto.staccato.StaccatoMarkerResponses
import com.on.staccato.data.dto.staccato.StaccatoResponse
import com.on.staccato.data.dto.staccato.StaccatoShareLinkResponse
import com.on.staccato.data.dto.staccato.StaccatoUpdateRequest
import com.on.staccato.domain.ApiResult
import javax.inject.Inject

class StaccatoRemoteDataSource
    @Inject
    constructor(
        private val staccatoApiService: StaccatoApiService,
    ) : StaccatoDataSource {
        override suspend fun fetchStaccatoMarkers(): ApiResult<StaccatoMarkerResponses> = staccatoApiService.getStaccatoMarkers()

        override suspend fun fetchStaccato(staccatoId: Long): ApiResult<StaccatoResponse> =
            staccatoApiService.getStaccato(
                staccatoId = staccatoId,
            )

        override suspend fun createStaccato(staccatoCreationRequest: StaccatoCreationRequest): ApiResult<StaccatoCreationResponse> =
            staccatoApiService.postStaccato(
                staccatoCreationRequest,
            )

        override suspend fun createStaccatoShareLink(staccatoId: Long): ApiResult<StaccatoShareLinkResponse> =
            staccatoApiService.postStaccatoShareLink(staccatoId)

        override suspend fun updateStaccato(
            staccatoId: Long,
            staccatoUpdateRequest: StaccatoUpdateRequest,
        ): ApiResult<Unit> =
            staccatoApiService.putStaccato(
                staccatoId = staccatoId,
                staccatoUpdateRequest = staccatoUpdateRequest,
            )

        override suspend fun deleteStaccato(staccatoId: Long): ApiResult<Unit> = staccatoApiService.deleteStaccato(staccatoId)

        override suspend fun updateFeeling(
            staccatoId: Long,
            feelingRequest: FeelingRequest,
        ): ApiResult<Unit> =
            staccatoApiService.postFeeling(
                staccatoId = staccatoId,
                feelingRequest = feelingRequest,
            )
    }
