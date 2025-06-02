package com.on.staccato.data.invitation

import com.on.staccato.data.dto.invitation.ReceivedInvitationsResponse
import com.on.staccato.data.dto.invitation.SentInvitationsResponse
import com.on.staccato.data.network.ApiResult
import javax.inject.Inject

class InvitationRemoteDataSource
    @Inject
    constructor(
        private val invitationApiService: InvitationApiService,
    ) : InvitationDataSource {
        override suspend fun getReceivedInvitations(): ApiResult<ReceivedInvitationsResponse> =
            invitationApiService.getReceivedInvitations()

        override suspend fun acceptInvitation(invitationId: Long): ApiResult<Unit> =
            invitationApiService.postInvitationAccept(invitationId = invitationId)

        override suspend fun rejectInvitation(invitationId: Long): ApiResult<Unit> =
            invitationApiService.postInvitationReject(invitationId = invitationId)

        override suspend fun getSentInvitations(): ApiResult<SentInvitationsResponse> = invitationApiService.getSentInvitations()

        override suspend fun cancelInvitation(invitationId: Long): ApiResult<Unit> =
            invitationApiService.postInvitationCancel(invitationId = invitationId)
    }
