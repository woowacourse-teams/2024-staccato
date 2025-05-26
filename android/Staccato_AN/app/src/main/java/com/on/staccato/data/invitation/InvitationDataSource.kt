package com.on.staccato.data.invitation

import com.on.staccato.data.dto.invitation.ReceivedInvitationsResponse
import com.on.staccato.data.dto.invitation.SentInvitationsResponse
import com.on.staccato.data.network.ApiResult

interface InvitationDataSource {
    fun getReceivedInvitations(): ApiResult<ReceivedInvitationsResponse>

    fun acceptInvitation(invitationId: Long): ApiResult<Unit>

    fun rejectInvitation(invitationId: Long): ApiResult<Unit>

    fun getSentInvitations(): ApiResult<SentInvitationsResponse>

    fun cancelInvitation(invitationId: Long): ApiResult<Unit>
}
