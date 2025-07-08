package com.on.staccato.domain.repository

import com.on.staccato.domain.ApiResult
import com.on.staccato.domain.model.invitation.ReceivedInvitation
import com.on.staccato.domain.model.invitation.SentInvitation

interface InvitationRepository {
    suspend fun invite(
        categoryId: Long,
        inviteeIds: List<Long>,
    ): ApiResult<List<Long>>

    suspend fun getReceivedInvitations(): ApiResult<List<ReceivedInvitation>>

    suspend fun getSentInvitations(): ApiResult<List<SentInvitation>>

    suspend fun acceptInvitation(invitationId: Long): ApiResult<Unit>

    suspend fun rejectInvitation(invitationId: Long): ApiResult<Unit>

    suspend fun cancelInvitation(invitationId: Long): ApiResult<Unit>
}
