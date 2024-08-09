package com.woowacourse.staccato.data.visit

import com.woowacourse.staccato.data.dto.visit.VisitCreationRequest
import com.woowacourse.staccato.data.dto.visit.VisitCreationResponse
import com.woowacourse.staccato.data.dto.visit.VisitResponse
import com.woowacourse.staccato.data.dto.visit.VisitUpdateRequest
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface VisitApiService {
    @GET("/visits/{visitId}")
    suspend fun getVisit(
        @Path(value = "visitId") visitId: Long,
    ): VisitResponse

    @Multipart
    @POST("/visits")
    suspend fun postVisit(
        @Part("data") data: VisitCreationRequest,
        @Part visitImageFiles: List<MultipartBody.Part>,
    ): VisitCreationResponse

    @Multipart
    @PUT("/visits/{visitId}")
    suspend fun putVisit(
        @Path(value = "visitId") visitId: Long,
        @Part("data") data: VisitUpdateRequest,
        @Part visitImageFiles: List<MultipartBody.Part>,
    )

    @DELETE("/visits/{visitId}")
    suspend fun deleteVisit(
        @Path(value = "visitId") visitId: Long,
    )
}
