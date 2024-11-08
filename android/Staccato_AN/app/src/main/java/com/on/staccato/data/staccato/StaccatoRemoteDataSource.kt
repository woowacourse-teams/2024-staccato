package com.on.staccato.data.staccato

import com.on.staccato.data.ApiResponseHandler.handleApiResponse
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.staccato.FeelingRequest
import com.on.staccato.data.dto.staccato.StaccatoCreationRequest
import com.on.staccato.data.dto.staccato.StaccatoCreationResponse
import com.on.staccato.data.dto.staccato.StaccatoLocationResponse
import com.on.staccato.data.dto.staccato.StaccatoResponse
import com.on.staccato.data.dto.staccato.StaccatoUpdateRequest
import javax.inject.Inject

class StaccatoRemoteDataSource
    @Inject
    constructor(
        private val staccatoApiService: StaccatoApiService,
    ) : StaccatoDataSource {
        override suspend fun fetchStaccatos(): ResponseResult<StaccatoLocationResponse> =
            handleApiResponse { staccatoApiService.getStaccatos() }

        override suspend fun fetchStaccato(staccatoId: Long): ResponseResult<StaccatoResponse> =
            handleApiResponse { staccatoApiService.getStaccato(momentId = staccatoId) }

        override suspend fun createStaccato(staccatoCreationRequest: StaccatoCreationRequest): ResponseResult<StaccatoCreationResponse> =
            handleApiResponse { staccatoApiService.postStaccato(staccatoCreationRequest) }

        override suspend fun updateStaccato(
            staccatoId: Long,
            staccatoUpdateRequest: StaccatoUpdateRequest,
        ): ResponseResult<Unit> =
            handleApiResponse {
                staccatoApiService.putStaccato(
                    momentId = staccatoId,
                    staccatoUpdateRequest,
                )
            }

        override suspend fun deleteStaccato(staccatoId: Long): ResponseResult<Unit> =
            handleApiResponse {
                staccatoApiService.deleteStaccato(staccatoId)
            }

        override suspend fun updateFeeling(
            staccatoId: Long,
            feelingRequest: FeelingRequest,
        ): ResponseResult<Unit> =
            handleApiResponse {
                staccatoApiService.postFeeling(
                    momentId = staccatoId,
                    feelingRequest = feelingRequest,
                )
            }
    }
