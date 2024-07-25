package com.woowacourse.staccato.data.travel

import com.woowacourse.staccato.data.dto.travel.TravelResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface TravelApiService {
    @GET("/travels/{travelId}")
    suspend fun requestTravel(
        @Path("travelId") travelId: Long,
    ): Response<TravelResponse>
}
