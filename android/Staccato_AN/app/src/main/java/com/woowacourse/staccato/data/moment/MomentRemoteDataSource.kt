package com.woowacourse.staccato.data.moment

import com.woowacourse.staccato.data.dto.moment.FeelingRequest
import com.woowacourse.staccato.data.dto.moment.MomentCreationRequest
import com.woowacourse.staccato.data.dto.moment.MomentCreationResponse
import com.woowacourse.staccato.data.dto.moment.MomentResponse
import com.woowacourse.staccato.data.dto.moment.MomentUpdateRequest
import okhttp3.MultipartBody

class MomentRemoteDataSource(
    private val momentApiService: MomentApiService,
) {
    suspend fun fetchMoment(momentId: Long): MomentResponse {
        return momentApiService.getMoment(momentId = momentId)
    }

    suspend fun createMoment(
        momentCreationRequest: MomentCreationRequest,
        momentImageFiles: List<MultipartBody.Part>,
    ): MomentCreationResponse {
        return momentApiService.postMoment(
            data = momentCreationRequest,
            momentImageFiles = momentImageFiles,
        )
    }

    suspend fun updateMoment(
        momentId: Long,
        placeName: String,
        momentImageUrls: List<String>,
        momentImageFiles: List<MultipartBody.Part>,
    ) = momentApiService.putMoment(
        momentId = momentId,
        data =
            MomentUpdateRequest(
                placeName = placeName,
                momentImageUrls = momentImageUrls,
            ),
        momentImageFiles = momentImageFiles,
    )

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
