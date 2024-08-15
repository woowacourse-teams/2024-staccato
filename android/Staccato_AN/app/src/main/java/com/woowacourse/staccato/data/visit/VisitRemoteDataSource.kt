package com.woowacourse.staccato.data.visit

import com.woowacourse.staccato.data.dto.visit.VisitCreationRequest
import com.woowacourse.staccato.data.dto.visit.VisitCreationResponse
import com.woowacourse.staccato.data.dto.visit.VisitResponse
import com.woowacourse.staccato.data.dto.visit.VisitUpdateRequest
import okhttp3.MultipartBody

class VisitRemoteDataSource(
    private val momentApiService: MomentApiService,
) {
    suspend fun fetchVisit(visitId: Long): VisitResponse {
        return momentApiService.getMoment(momentId = visitId)
    }

    suspend fun createVisit(
        visitCreationRequest: VisitCreationRequest,
        visitImageFiles: List<MultipartBody.Part>,
    ): VisitCreationResponse {
        return momentApiService.postMoment(
            data = visitCreationRequest,
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
            VisitUpdateRequest(
                placeName = placeName,
                visitImageUrls = visitImageUrls,
            ),
        momentImageFiles = visitImageFiles,
    )

    suspend fun deleteVisit(visitId: Long) {
        momentApiService.deleteMoment(visitId)
    }
}
