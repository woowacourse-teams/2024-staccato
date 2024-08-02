package com.woowacourse.staccato.data.visit

import com.woowacourse.staccato.data.dto.visit.VisitCreationRequest
import com.woowacourse.staccato.data.dto.visit.VisitResponse
import com.woowacourse.staccato.data.dto.visit.VisitUpdateRequest
import retrofit2.Response

class VisitRemoteDataSource(
    private val visitApiService: VisitApiService,
) {
    suspend fun fetchVisit(visitId: Long): VisitResponse {
        return visitApiService.getVisit(visitId = visitId)
    }

    suspend fun createVisit(visitCreationRequest: VisitCreationRequest): Response<String> =
        visitApiService.postVisit(visitCreationRequest = visitCreationRequest)

    suspend fun updateVisit(visitUpdateRequest: VisitUpdateRequest) = visitApiService.putVisit(visitUpdateRequest = visitUpdateRequest)
}
