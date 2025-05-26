package com.on.staccato.domain.repository

import com.on.staccato.data.network.ApiResult
import com.on.staccato.domain.model.invitation.ReceivedInvitation
import com.on.staccato.domain.model.invitation.SentInvitation

interface InvitationRepository {
    suspend fun getReceivedInvitations(): ApiResult<List<ReceivedInvitation>>

    suspend fun acceptInvitation(invitationId: Long): ApiResult<Unit>

    suspend fun rejectInvitation(invitationId: Long): ApiResult<Unit>

    suspend fun getSentInvitations(): ApiResult<List<SentInvitation>>

    suspend fun cancelInvitation(invitationId: Long): ApiResult<Unit>
}
