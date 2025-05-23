package com.on.staccato.domain.model.invitation

import com.on.staccato.domain.model.Member

data class ReceivedInvitation(
    val categoryId: Long,
    val categoryTitle: String,
    val inviter: Member,
)
