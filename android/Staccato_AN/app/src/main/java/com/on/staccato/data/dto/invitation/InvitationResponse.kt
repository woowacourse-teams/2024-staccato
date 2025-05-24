package com.on.staccato.data.dto.invitation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InvitationResponse(
    @SerialName("invitationIds") val invitationIds: List<Long>,
)
