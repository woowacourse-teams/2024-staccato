package com.on.staccato.data.invitation

import com.on.staccato.data.dto.invitation.ReceivedInvitationsResponse
import com.on.staccato.data.dto.invitation.SentInvitationsResponse
import com.on.staccato.data.network.ApiResult

interface InvitationDataSource {
    suspend fun getReceivedInvitations(): ApiResult<ReceivedInvitationsResponse>

    suspend fun acceptInvitation(invitationId: Long): ApiResult<Unit>

    suspend fun rejectInvitation(invitationId: Long): ApiResult<Unit>

    suspend fun getSentInvitations(): ApiResult<SentInvitationsResponse>

    suspend fun cancelInvitation(invitationId: Long): ApiResult<Unit>
}
