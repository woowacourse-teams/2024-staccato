package com.on.staccato.data.staccato

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.staccato.FeelingRequest
import com.on.staccato.data.dto.staccato.StaccatoCreationRequest
import com.on.staccato.data.dto.staccato.StaccatoCreationResponse
import com.on.staccato.data.dto.staccato.StaccatoLocationResponse
import com.on.staccato.data.dto.staccato.StaccatoResponse
import com.on.staccato.data.dto.staccato.StaccatoUpdateRequest

interface StaccatoDataSource {
    suspend fun fetchStaccatos(): ResponseResult<StaccatoLocationResponse>

    suspend fun fetchStaccato(staccatoId: Long): StaccatoResponse

    suspend fun createStaccato(staccatoCreationRequest: StaccatoCreationRequest): StaccatoCreationResponse

    suspend fun updateStaccato(
        staccatoId: Long,
        staccatoUpdateRequest: StaccatoUpdateRequest,
    )

    suspend fun deleteStaccato(staccatoId: Long)

    suspend fun updateFeeling(
        staccatoId: Long,
        feelingRequest: FeelingRequest,
    )
}
