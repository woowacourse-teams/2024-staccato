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
    ): VisitCreationResponse =
        visitApiService.postVisit(
            data = visitCreationRequest,
            visitImageFiles = visitImageFiles,
        )

    suspend fun updateVisit(visitUpdateRequest: VisitUpdateRequest) = visitApiService.putVisit(visitUpdateRequest = visitUpdateRequest)
}
