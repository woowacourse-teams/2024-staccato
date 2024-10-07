package com.on.staccato.data.moment

import com.on.staccato.data.dto.moment.FeelingRequest
import com.on.staccato.data.dto.moment.MomentCreationRequest
import com.on.staccato.data.dto.moment.MomentCreationResponse
import com.on.staccato.data.dto.moment.MomentLocationResponse
import com.on.staccato.data.dto.moment.MomentResponse
import com.on.staccato.data.dto.moment.MomentUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MomentApiService {
    @GET(MOMENTS_PATH)
    suspend fun getMoments(): Response<MomentLocationResponse>

    @GET(MOMENT_PATH_WITH_ID)
    suspend fun getMoment(
        @Path(value = "momentId") momentId: Long,
    ): MomentResponse

    @POST(MOMENTS_PATH)
    suspend fun postMoment(
        @Body momentCreationRequest: MomentCreationRequest,
    ): MomentCreationResponse

    @PUT(MOMENT_PATH_WITH_ID)
    suspend fun putMoment(
        @Path(value = "momentId") momentId: Long,
        @Body momentUpdateRequest: MomentUpdateRequest,
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
