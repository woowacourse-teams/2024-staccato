package com.on.staccato.data.moment

import com.on.staccato.data.ApiResponseHandler.handleApiResponse
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.moment.FeelingRequest
import com.on.staccato.data.dto.moment.MomentCreationRequest
import com.on.staccato.data.dto.moment.MomentCreationResponse
import com.on.staccato.data.dto.moment.MomentLocationResponse
import com.on.staccato.data.dto.moment.MomentResponse
import com.on.staccato.data.dto.moment.MomentUpdateRequest

class MomentRemoteDataSource(
    private val momentApiService: MomentApiService,
) {
    suspend fun fetchMoments(): ResponseResult<MomentLocationResponse> = handleApiResponse { momentApiService.getMoments() }

    suspend fun fetchMoment(momentId: Long): MomentResponse {
        return momentApiService.getMoment(momentId = momentId)
    }

    suspend fun createMoment(momentCreationRequest: MomentCreationRequest): MomentCreationResponse {
        return momentApiService.postMoment(momentCreationRequest)
    }

    suspend fun updateMoment(
        momentId: Long,
        placeName: String,
        momentImageUrls: List<String>,
    ) {
        return momentApiService.putMoment(
            momentId = momentId,
            momentUpdateRequest =
                MomentUpdateRequest(
                    placeName = placeName,
                    momentImageUrls = momentImageUrls,
                ),
        )
    }

    suspend fun deleteMoment(momentId: Long) {
        momentApiService.deleteMoment(momentId)
    }

    suspend fun updateFeeling(
        momentId: Long,
        feelingRequest: FeelingRequest,
    ) {
        momentApiService.postFeeling(
            momentId = momentId,
            feelingRequest = feelingRequest,
        )
    }
}
