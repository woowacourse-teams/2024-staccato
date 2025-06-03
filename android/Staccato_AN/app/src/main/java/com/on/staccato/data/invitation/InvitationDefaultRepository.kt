package com.on.staccato.data.invitation

import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.network.ApiResult
import com.on.staccato.data.network.handle
import com.on.staccato.domain.model.invitation.ReceivedInvitation
import com.on.staccato.domain.model.invitation.SentInvitation
import com.on.staccato.domain.repository.InvitationRepository
import javax.inject.Inject

class InvitationDefaultRepository
    @Inject
    constructor(
        private val invitationApiService: InvitationApiService,
    ) : InvitationRepository {
        override suspend fun getReceivedInvitations(): ApiResult<List<ReceivedInvitation>> =
            invitationApiService.getReceivedInvitations().handle { it.toDomain() }

        override suspend fun acceptInvitation(invitationId: Long): ApiResult<Unit> =
            invitationApiService.postInvitationAccept(invitationId = invitationId).handle()

        override suspend fun rejectInvitation(invitationId: Long): ApiResult<Unit> =
            invitationApiService.postInvitationReject(invitationId = invitationId).handle()

        override suspend fun getSentInvitations(): ApiResult<List<SentInvitation>> =
            invitationApiService.getSentInvitations().handle { it.toDomain() }

        override suspend fun cancelInvitation(invitationId: Long): ApiResult<Unit> =
            invitationApiService.postInvitationCancel(invitationId = invitationId).handle()
    }
