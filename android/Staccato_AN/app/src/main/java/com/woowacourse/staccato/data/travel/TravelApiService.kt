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
        @Header("Authorization") authorization: String = TEMP_AUTHORIZATION,
    ): Response<TravelResponse>

    companion object {
        private const val TEMP_AUTHORIZATION = "1"
    }
}
