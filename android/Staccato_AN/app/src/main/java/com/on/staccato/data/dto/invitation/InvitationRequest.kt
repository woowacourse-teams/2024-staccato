package com.on.staccato.data.dto.invitation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InvitationRequest(
    @SerialName("categoryId") val categoryId: Long,
    @SerialName("inviteeIds") val inviteeIds: List<Long>,
)
