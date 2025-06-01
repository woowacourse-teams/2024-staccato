package com.on.staccato.domain.repository

import com.on.staccato.data.network.ApiResult

interface InvitationRepository {
    suspend fun invite(
        categoryId: Long,
        inviteeIds: List<Long>,
    ): ApiResult<List<Long>>
}
