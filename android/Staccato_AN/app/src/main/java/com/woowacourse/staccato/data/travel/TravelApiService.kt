package com.woowacourse.staccato.data.travel

import com.woowacourse.staccato.data.dto.travel.TravelResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface TravelApiService {
    @GET("/travels/{travelId}")
    suspend fun requestTravel(
        @Header("Authorization") authorization: String = TEMP_AUTHORIZATION,
        @Path("travelId") travelId: Long = TEMP_TRAVEL_ID,
    ): Response<TravelResponse>

    companion object {
        private const val TEMP_AUTHORIZATION = "1"
        private const val TEMP_TRAVEL_ID = 12L
    }
}
