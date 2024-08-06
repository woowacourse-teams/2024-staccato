package com.woowacourse.staccato.data.travel

import com.woowacourse.staccato.data.dto.travel.TravelRequest
import com.woowacourse.staccato.data.dto.travel.TravelResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TravelApiService {
    @GET("/travels/{travelId}")
    suspend fun getTravel(
        @Path("travelId") travelId: Long,
    ): Response<TravelResponse>

    @POST("/travels")
    suspend fun postTravel(
        @Body travelRequest: TravelRequest,
    ): Response<String>

    @PUT("/travels/{travelId}")
    suspend fun putTravel(
        @Path("travelId") travelId: Long,
        @Body travelRequest: TravelRequest,
    ): Response<String>
}
