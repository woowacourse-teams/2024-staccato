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

class MomentRemoteDataSource
    @Inject
    constructor(
        private val staccatoApiService: StaccatoApiService,
    ) : MomentDataSource {
        override suspend fun fetchMoments(): ResponseResult<StaccatoLocationResponse> =
            handleApiResponse { staccatoApiService.getStaccatos() }

        override suspend fun fetchMoment(momentId: Long): StaccatoResponse {
            return staccatoApiService.getStaccato(momentId = momentId)
        }

        override suspend fun createMoment(staccatoCreationRequest: StaccatoCreationRequest): StaccatoCreationResponse {
            return staccatoApiService.postStaccato(staccatoCreationRequest)
        }

        override suspend fun updateMoment(
            momentId: Long,
            staccatoUpdateRequest: StaccatoUpdateRequest,
        ) {
            return staccatoApiService.putStaccato(
                momentId = momentId,
                staccatoUpdateRequest,
            )
        }

        override suspend fun deleteMoment(momentId: Long) {
            staccatoApiService.deleteStaccato(momentId)
        }

        override suspend fun updateFeeling(
            momentId: Long,
            feelingRequest: FeelingRequest,
        ) {
            staccatoApiService.postFeeling(
                momentId = momentId,
                feelingRequest = feelingRequest,
            )
        }
    }
