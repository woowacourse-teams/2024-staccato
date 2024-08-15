package com.woowacourse.staccato.data.moment

import com.woowacourse.staccato.data.dto.moment.VisitCreationRequest
import com.woowacourse.staccato.data.dto.moment.VisitCreationResponse
import com.woowacourse.staccato.data.dto.moment.VisitResponse
import com.woowacourse.staccato.data.dto.moment.VisitUpdateRequest
import okhttp3.MultipartBody
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
    ): VisitResponse

    @Multipart
    @POST(MOMENTS_PATH)
    suspend fun postMoment(
        @Part("data") data: VisitCreationRequest,
        @Part momentImageFiles: List<MultipartBody.Part>,
    ): VisitCreationResponse

    @Multipart
    @PUT(MOMENT_PATH_WITH_ID)
    suspend fun putMoment(
        @Path(value = "momentId") momentId: Long,
        @Part("data") data: VisitUpdateRequest,
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
