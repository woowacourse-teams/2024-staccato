package com.on.staccato.data.invitation

import com.on.staccato.data.dto.invitation.InvitationRequest
import com.on.staccato.data.network.ApiResult
import com.on.staccato.data.network.handle
import com.on.staccato.domain.repository.InvitationRepository
import javax.inject.Inject

class InvitationDefaultRepository
    @Inject
    constructor(
        private val invitationApiService: InvitationApiService,
    ) : InvitationRepository {
        override suspend fun invite(
            categoryId: Long,
            inviteeIds: List<Long>,
        ): ApiResult<List<Long>> =
            invitationApiService.postInvitation(InvitationRequest(categoryId, inviteeIds))
                .handle { it.invitationIds }
    }
