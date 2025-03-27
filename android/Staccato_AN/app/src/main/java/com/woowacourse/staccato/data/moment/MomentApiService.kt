package com.woowacourse.staccato.data.moment

import com.woowacourse.staccato.data.dto.moment.FeelingRequest
import com.woowacourse.staccato.data.dto.moment.MomentCreationRequest
import com.woowacourse.staccato.data.dto.moment.MomentCreationResponse
import com.woowacourse.staccato.data.dto.moment.MomentResponse
import com.woowacourse.staccato.data.dto.moment.MomentUpdateRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MomentApiService {
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
