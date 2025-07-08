package com.on.staccato.data.dto.invitation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SentInvitationDto(
    @SerialName("invitationId") val invitationId: Long,
    @SerialName("inviteeId") val inviteeId: Long,
    @SerialName("inviteeNickname") val inviteeNickname: String,
    @SerialName("inviteeProfileImageUrl") val inviteeProfileImageUrl: String? = null,
    @SerialName("categoryId") val categoryId: Long,
    @SerialName("categoryTitle") val categoryTitle: String,
)
