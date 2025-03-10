package com.on.staccato.data.staccato

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

interface StaccatoApiService {
    @GET(STACCATOS_PATH)
    suspend fun getStaccatos(): Response<StaccatoLocationResponse>

    @GET(STACCATO_PATH_WITH_ID)
    suspend fun getStaccato(
        @Path(value = "momentId") momentId: Long,
    ): Response<StaccatoResponse>

    @POST(STACCATOS_PATH)
    suspend fun postStaccato(
        @Body staccatoCreationRequest: StaccatoCreationRequest,
    ): Response<StaccatoCreationResponse>

    @PUT(STACCATO_PATH_WITH_ID)
    suspend fun putStaccato(
        @Path(value = "momentId") momentId: Long,
        @Body staccatoUpdateRequest: StaccatoUpdateRequest,
    ): Response<Unit>

    @DELETE(STACCATO_PATH_WITH_ID)
    suspend fun deleteStaccato(
        @Path(value = "momentId") momentId: Long,
    ): Response<Unit>

    @POST(FEELING_PATH)
    suspend fun postFeeling(
        @Path(value = "momentId") momentId: Long,
        @Body feelingRequest: FeelingRequest,
    ): Response<Unit>

    companion object {
        private const val STACCATOS_PATH = "/moments"
        private const val STACCATO_ID = "/{momentId}"
        private const val STACCATO_PATH_WITH_ID = "$STACCATOS_PATH$STACCATO_ID"
        private const val FEELING_PATH = "$STACCATO_PATH_WITH_ID/feeling"
    }
}
