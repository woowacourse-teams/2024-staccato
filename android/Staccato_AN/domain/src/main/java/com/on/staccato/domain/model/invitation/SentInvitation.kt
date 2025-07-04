package com.on.staccato.domain.model.invitation

import com.on.staccato.domain.model.Member

data class SentInvitation(
    val invitationId: Long,
    val invitee: Member,
    val categoryId: Long,
    val categoryTitle: String,
)
