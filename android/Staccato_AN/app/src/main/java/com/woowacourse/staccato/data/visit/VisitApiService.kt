package com.woowacourse.staccato.data.visit

import com.woowacourse.staccato.data.dto.visit.VisitCreationRequest
import com.woowacourse.staccato.data.dto.visit.VisitResponse
import com.woowacourse.staccato.data.dto.visit.VisitUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface VisitApiService {
    @GET("/visits/{visitId}")
    suspend fun getVisit(
        @Path(value = "visitId") visitId: Long,
    ): VisitResponse

    @POST("/visits")
    suspend fun postVisit(
        @Body visitCreationRequest: VisitCreationRequest,
    ): Response<String>

    @PUT("/visits")
    suspend fun putVisit(
        @Body visitUpdateRequest: VisitUpdateRequest,
    )
}
