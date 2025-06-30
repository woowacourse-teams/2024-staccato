package com.on.staccato.domain.model.invitation

import com.on.staccato.domain.model.Member

data class ReceivedInvitation(
    val invitationId: Long,
    val inviter: Member,
    val categoryId: Long,
    val categoryTitle: String,
)
