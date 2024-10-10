package com.on.staccato.data.moment

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.moment.FeelingRequest
import com.on.staccato.data.dto.moment.MomentCreationRequest
import com.on.staccato.data.dto.moment.MomentCreationResponse
import com.on.staccato.data.dto.moment.MomentLocationResponse
import com.on.staccato.data.dto.moment.MomentResponse
import com.on.staccato.data.dto.moment.MomentUpdateRequest

interface MomentDataSource {
    suspend fun fetchMoments(): ResponseResult<MomentLocationResponse>

    suspend fun fetchMoment(momentId: Long): MomentResponse

    suspend fun createMoment(momentCreationRequest: MomentCreationRequest): MomentCreationResponse

    suspend fun updateMoment(
        momentId: Long,
        momentUpdateRequest: MomentUpdateRequest,
    )

    suspend fun deleteMoment(momentId: Long)

    suspend fun updateFeeling(
        momentId: Long,
        feelingRequest: FeelingRequest,
    )
}
