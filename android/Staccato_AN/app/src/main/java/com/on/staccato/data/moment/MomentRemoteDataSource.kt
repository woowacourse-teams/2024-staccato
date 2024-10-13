package com.on.staccato.data.moment

import com.on.staccato.data.ApiResponseHandler.handleApiResponse
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.staccato.FeelingRequest
import com.on.staccato.data.dto.staccato.MomentLocationResponse
import com.on.staccato.data.dto.staccato.MomentResponse
import com.on.staccato.data.dto.staccato.StaccatoCreationRequest
import com.on.staccato.data.dto.staccato.StaccatoCreationResponse
import com.on.staccato.data.dto.staccato.StaccatoUpdateRequest
import javax.inject.Inject

class MomentRemoteDataSource
    @Inject
    constructor(
        private val momentApiService: MomentApiService,
    ) : MomentDataSource {
        override suspend fun fetchMoments(): ResponseResult<MomentLocationResponse> = handleApiResponse { momentApiService.getMoments() }

        override suspend fun fetchMoment(momentId: Long): MomentResponse {
            return momentApiService.getMoment(momentId = momentId)
        }

        override suspend fun createMoment(staccatoCreationRequest: StaccatoCreationRequest): StaccatoCreationResponse {
            return momentApiService.postMoment(staccatoCreationRequest)
        }

        override suspend fun updateMoment(
            momentId: Long,
            staccatoUpdateRequest: StaccatoUpdateRequest,
        ) {
            return momentApiService.putMoment(
                momentId = momentId,
                staccatoUpdateRequest,
            )
        }

        override suspend fun deleteMoment(momentId: Long) {
            momentApiService.deleteMoment(momentId)
        }

        override suspend fun updateFeeling(
            momentId: Long,
            feelingRequest: FeelingRequest,
        ) {
            momentApiService.postFeeling(
                momentId = momentId,
                feelingRequest = feelingRequest,
            )
        }
    }
