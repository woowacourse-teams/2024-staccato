package com.woowacourse.staccato.data.visit

import com.woowacourse.staccato.data.dto.visit.VisitCreationRequest
import com.woowacourse.staccato.data.dto.visit.VisitCreationResponse
import com.woowacourse.staccato.data.dto.visit.VisitResponse
import com.woowacourse.staccato.data.dto.visit.VisitUpdateRequest
import okhttp3.MultipartBody

class VisitRemoteDataSource(
    private val visitApiService: VisitApiService,
) {
    suspend fun fetchVisit(visitId: Long): VisitResponse {
        return visitApiService.getVisit(visitId = visitId)
    }

    suspend fun createVisit(
        visitCreationRequest: VisitCreationRequest,
        visitImageFiles: List<MultipartBody.Part>,
    ): VisitCreationResponse {
        return visitApiService.postVisit(
            data = visitCreationRequest,
            visitImageFiles = visitImageFiles,
        )
    }

    suspend fun updateVisit(
        visitId: Long,
        placeName: String,
        visitImageUrls: List<String>,
        visitImageFiles: List<MultipartBody.Part>,
    ) = visitApiService.putVisit(
        visitId = visitId,
        data =
            VisitUpdateRequest(
                placeName = placeName,
                visitImageUrls = visitImageUrls,
            ),
        visitImageFiles = visitImageFiles,
    )

    suspend fun deleteVisit(visitId: Long) {
        visitApiService.deleteVisit(visitId)
    }
}
