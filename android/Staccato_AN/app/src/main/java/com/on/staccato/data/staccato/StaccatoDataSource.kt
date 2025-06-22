package com.on.staccato.data.staccato

import com.on.staccato.data.dto.staccato.FeelingRequest
import com.on.staccato.data.dto.staccato.StaccatoCreationRequest
import com.on.staccato.data.dto.staccato.StaccatoCreationResponse
import com.on.staccato.data.dto.staccato.StaccatoMarkerResponses
import com.on.staccato.data.dto.staccato.StaccatoResponse
import com.on.staccato.data.dto.staccato.StaccatoShareLinkResponse
import com.on.staccato.data.dto.staccato.StaccatoUpdateRequest
import com.on.staccato.data.network.ApiResult

interface StaccatoDataSource {
    suspend fun fetchStaccatoMarkers(): ApiResult<StaccatoMarkerResponses>

    suspend fun fetchStaccato(staccatoId: Long): ApiResult<StaccatoResponse>

    suspend fun createStaccato(staccatoCreationRequest: StaccatoCreationRequest): ApiResult<StaccatoCreationResponse>

    suspend fun createStaccatoShareLink(staccatoId: Long): ApiResult<StaccatoShareLinkResponse>

    suspend fun updateStaccato(
        staccatoId: Long,
        staccatoUpdateRequest: StaccatoUpdateRequest,
    ): ApiResult<Unit>

    suspend fun deleteStaccato(staccatoId: Long): ApiResult<Unit>

    suspend fun updateFeeling(
        staccatoId: Long,
        feelingRequest: FeelingRequest,
    ): ApiResult<Unit>
}
