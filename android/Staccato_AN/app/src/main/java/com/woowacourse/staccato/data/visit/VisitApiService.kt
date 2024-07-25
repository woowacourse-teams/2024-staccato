package com.woowacourse.staccato.data.visit

import com.woowacourse.staccato.data.dto.visit.VisitCreationRequest
import com.woowacourse.staccato.data.dto.visit.VisitResponse
import com.woowacourse.staccato.data.dto.visit.VisitUpdateRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface VisitApiService {
    @GET("/visits/{visitId}")
    suspend fun requestVisit(
        @Path(value = "visitId") visitId: Long,
    ): VisitResponse

    @POST("/visits")
    suspend fun requestCreateVisit(
        @Body visitCreationRequest: VisitCreationRequest,
    )

    @POST("/visits")
    suspend fun requestUpdateVisit(
        @Body visitUpdateRequest: VisitUpdateRequest,
    )
}
