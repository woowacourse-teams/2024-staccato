package com.on.staccato.data.moment

import com.on.staccato.data.dto.staccato.FeelingRequest
import com.on.staccato.data.dto.staccato.StaccatoCreationRequest
import com.on.staccato.data.dto.staccato.StaccatoCreationResponse
import com.on.staccato.data.dto.staccato.StaccatoLocationResponse
import com.on.staccato.data.dto.staccato.StaccatoResponse
import com.on.staccato.data.dto.staccato.StaccatoUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MomentApiService {
    @GET(MOMENTS_PATH)
    suspend fun getMoments(): Response<StaccatoLocationResponse>

    @GET(MOMENT_PATH_WITH_ID)
    suspend fun getMoment(
        @Path(value = "momentId") momentId: Long,
    ): StaccatoResponse

    @POST(MOMENTS_PATH)
    suspend fun postMoment(
        @Body staccatoCreationRequest: StaccatoCreationRequest,
    ): StaccatoCreationResponse

    @PUT(MOMENT_PATH_WITH_ID)
    suspend fun putMoment(
        @Path(value = "momentId") momentId: Long,
        @Body staccatoUpdateRequest: StaccatoUpdateRequest,
    )

    @DELETE(MOMENT_PATH_WITH_ID)
    suspend fun deleteMoment(
        @Path(value = "momentId") momentId: Long,
    )

    @POST(FEELING_PATH)
    suspend fun postFeeling(
        @Path(value = "momentId") momentId: Long,
        @Body feelingRequest: FeelingRequest,
    )

    companion object {
        private const val MOMENTS_PATH = "/moments"
        private const val MOMENT_ID = "/{momentId}"
        private const val MOMENT_PATH_WITH_ID = "$MOMENTS_PATH$MOMENT_ID"
        private const val FEELING_PATH = "$MOMENT_PATH_WITH_ID/feeling"
    }
}
