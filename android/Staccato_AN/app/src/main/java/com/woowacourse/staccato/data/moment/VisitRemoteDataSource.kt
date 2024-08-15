package com.woowacourse.staccato.data.moment

import com.woowacourse.staccato.data.dto.moment.MomentCreationRequest
import com.woowacourse.staccato.data.dto.moment.MomentCreationResponse
import com.woowacourse.staccato.data.dto.moment.MomentResponse
import com.woowacourse.staccato.data.dto.moment.MomentUpdateRequest
import okhttp3.MultipartBody

class VisitRemoteDataSource(
    private val momentApiService: MomentApiService,
) {
    suspend fun fetchVisit(visitId: Long): MomentResponse {
        return momentApiService.getMoment(momentId = visitId)
    }

    suspend fun createVisit(
        momentCreationRequest: MomentCreationRequest,
        visitImageFiles: List<MultipartBody.Part>,
    ): MomentCreationResponse {
        return momentApiService.postMoment(
            data = momentCreationRequest,
            momentImageFiles = visitImageFiles,
        )
    }

    suspend fun updateVisit(
        visitId: Long,
        placeName: String,
        visitImageUrls: List<String>,
        visitImageFiles: List<MultipartBody.Part>,
    ) = momentApiService.putMoment(
        momentId = visitId,
        data =
            MomentUpdateRequest(
                placeName = placeName,
                momentImageUrls = visitImageUrls,
            ),
        momentImageFiles = visitImageFiles,
    )

    suspend fun deleteVisit(visitId: Long) {
        momentApiService.deleteMoment(visitId)
    }
}
