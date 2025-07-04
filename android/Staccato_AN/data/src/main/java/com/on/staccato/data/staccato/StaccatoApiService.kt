package com.on.staccato.data.staccato

import com.on.staccato.data.dto.staccato.FeelingRequest
import com.on.staccato.data.dto.staccato.StaccatoCreationRequest
import com.on.staccato.data.dto.staccato.StaccatoCreationResponse
import com.on.staccato.data.dto.staccato.StaccatoMarkerResponses
import com.on.staccato.data.dto.staccato.StaccatoResponse
import com.on.staccato.data.dto.staccato.StaccatoShareLinkResponse
import com.on.staccato.data.dto.staccato.StaccatoUpdateRequest
import com.on.staccato.domain.ApiResult
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface StaccatoApiService {
    @GET(STACCATOS_PATH_V2)
    suspend fun getStaccatoMarkers(): ApiResult<StaccatoMarkerResponses>

    @GET(STACCATO_PATH_WITH_ID)
    suspend fun getStaccato(
        @Path(value = STACCATO_ID) staccatoId: Long,
    ): ApiResult<StaccatoResponse>

    @POST(STACCATO_SHARE_LINK_PATH)
    suspend fun postStaccatoShareLink(
        @Path(value = STACCATO_ID) staccatoId: Long,
    ): ApiResult<StaccatoShareLinkResponse>

    @POST(STACCATOS_PATH)
    suspend fun postStaccato(
        @Body staccatoCreationRequest: StaccatoCreationRequest,
    ): ApiResult<StaccatoCreationResponse>

    @PUT(STACCATO_PATH_WITH_ID)
    suspend fun putStaccato(
        @Path(value = STACCATO_ID) staccatoId: Long,
        @Body staccatoUpdateRequest: StaccatoUpdateRequest,
    ): ApiResult<Unit>

    @DELETE(STACCATO_PATH_WITH_ID)
    suspend fun deleteStaccato(
        @Path(value = STACCATO_ID) staccatoId: Long,
    ): ApiResult<Unit>

    @POST(FEELING_PATH)
    suspend fun postFeeling(
        @Path(value = STACCATO_ID) staccatoId: Long,
        @Body feelingRequest: FeelingRequest,
    ): ApiResult<Unit>

    companion object {
        private const val STACCATOS_PATH_V2 = "/v2/staccatos"
        private const val STACCATOS_PATH = "/staccatos"
        private const val STACCATO_ID = "staccatoId"
        private const val STACCATO_PATH_WITH_ID = "$STACCATOS_PATH/{$STACCATO_ID}"
        private const val STACCATO_SHARE_LINK_PATH = "$STACCATO_PATH_WITH_ID/share"
        private const val FEELING_PATH = "$STACCATO_PATH_WITH_ID/feeling"
    }
}
