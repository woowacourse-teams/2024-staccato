package com.woowacourse.staccato.data.visit

import com.woowacourse.staccato.data.StaccatoClient
import com.woowacourse.staccato.data.dto.visit.VisitCreationRequest
import com.woowacourse.staccato.data.dto.visit.VisitResponse
import com.woowacourse.staccato.data.dto.visit.VisitUpdateRequest

class RemoteVisitDataSource {
    private val visitApiService: VisitApiService =
        StaccatoClient.create(VisitApiService::class.java)

    suspend fun fetchVisit(visitId: Long): VisitResponse = visitApiService.requestVisit(visitId = visitId)

    suspend fun createVisit(visitCreationRequest: VisitCreationRequest): Unit =
        visitApiService.requestCreateVisit(visitCreationRequest = visitCreationRequest)

    suspend fun updateVisit(visitUpdateRequest: VisitUpdateRequest): Unit =
        visitApiService.requestUpdateVisit(visitUpdateRequest = visitUpdateRequest)
}
