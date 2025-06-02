package com.on.staccato.data.dto.invitation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SentInvitationsResponse(
    @SerialName("invitations") val invitations: List<SentInvitationDto>,
)
