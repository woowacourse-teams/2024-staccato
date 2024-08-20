package com.woowacourse.staccato.data.moment

import com.woowacourse.staccato.data.dto.moment.MomentCreationRequest
import com.woowacourse.staccato.data.dto.moment.MomentCreationResponse
import com.woowacourse.staccato.data.dto.moment.MomentResponse
import com.woowacourse.staccato.data.dto.moment.MomentUpdateRequest
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    @Multipart
    @PUT(MOMENT_PATH_WITH_ID)
    suspend fun putMoment(
        @Path(value = "momentId") momentId: Long,
        @Part("data") data: MomentUpdateRequest,
        @Part momentImageFiles: List<MultipartBody.Part>,
    )

    @DELETE(MOMENT_PATH_WITH_ID)
    suspend fun deleteMoment(
        @Path(value = "momentId") momentId: Long,
    )

    companion object {
        private const val MOMENTS_PATH = "/moments"
        private const val MOMENT_ID = "/{momentId}"
        private const val MOMENT_PATH_WITH_ID = "$MOMENTS_PATH$MOMENT_ID"
    }
}
