package com.on.staccato.domain.model.invitation

import com.on.staccato.domain.model.Member

data class SentInvitation(
    val categoryId: Long,
    val categoryTitle: String,
    val invitee: Member,
)
