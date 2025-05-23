package com.on.staccato.domain.repository

import com.on.staccato.data.network.ApiResult
import com.on.staccato.domain.model.invitation.ReceivedInvitation
import com.on.staccato.domain.model.invitation.SentInvitation

interface InvitationRepository {
    fun getReceivedInvitations(): ApiResult<List<ReceivedInvitation>>

    fun getSentInvitations(): ApiResult<List<SentInvitation>>
}
