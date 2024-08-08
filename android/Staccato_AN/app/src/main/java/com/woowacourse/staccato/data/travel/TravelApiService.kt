package com.woowacourse.staccato.data.travel

import com.woowacourse.staccato.data.dto.travel.TravelRequest
import com.woowacourse.staccato.data.dto.travel.TravelResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface TravelApiService {
    @GET(TRAVEL_PATH_WITH_ID)
    suspend fun getTravel(
        @Path("travelId") travelId: Long,
    ): Response<TravelResponse>

    @Multipart
    @POST(TRAVELS_PATH)
    suspend fun postTravel(
        @Part("data") data: TravelRequest,
        @Part thumbnailFile: MultipartBody.Part?,
    ): Response<String>

    @PUT(TRAVEL_PATH_WITH_ID)
    suspend fun putTravel(
        @Path("travelId") travelId: Long,
        @Body travelRequest: TravelRequest,
    ): Response<String>

    @DELETE(TRAVEL_PATH_WITH_ID)
    suspend fun deleteTravel(
        @Path("travelId") travelId: Long,
    ): Response<Unit>

    companion object {
        private const val TRAVELS_PATH = "/travels"
        private const val TRAVEL_ID = "/{travelId}"
        private const val TRAVEL_PATH_WITH_ID = "$TRAVELS_PATH$TRAVEL_ID"
    }
}
