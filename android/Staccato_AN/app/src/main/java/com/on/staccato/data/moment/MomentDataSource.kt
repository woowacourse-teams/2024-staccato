package com.on.staccato.data.moment

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.staccato.FeelingRequest
import com.on.staccato.data.dto.staccato.MomentLocationResponse
import com.on.staccato.data.dto.staccato.MomentResponse
import com.on.staccato.data.dto.staccato.StaccatoCreationRequest
import com.on.staccato.data.dto.staccato.StaccatoCreationResponse
import com.on.staccato.data.dto.staccato.StaccatoUpdateRequest

interface MomentDataSource {
    suspend fun fetchMoments(): ResponseResult<MomentLocationResponse>

    suspend fun fetchMoment(momentId: Long): MomentResponse

    suspend fun createMoment(staccatoCreationRequest: StaccatoCreationRequest): StaccatoCreationResponse

    suspend fun updateMoment(
        momentId: Long,
        staccatoUpdateRequest: StaccatoUpdateRequest,
    )

    suspend fun deleteMoment(momentId: Long)

    suspend fun updateFeeling(
        momentId: Long,
        feelingRequest: FeelingRequest,
    )
}
